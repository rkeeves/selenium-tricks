package io.github.rkeeves.interoperability;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v107.runtime.Runtime;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Functions.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CallCDPBindingByClientTest {

    /**
     * What's the goal?
     * Demonstrate how we can add CDP bindings and call them from client REALLY ASYNCHRONOUSLY.
     *
     * What does it do?
     * We add a binding called 'foo'.
     * Then we set up some Java side callback (with countdown latch etc).
     * Then we schedule 4 calls of the binding via 'setTimeout'.
     * We then wait for 3 (not a typo, it is 3) calls to the 'foo'.
     * After that we verify that the last call passed the right string to us.
     *
     * So basically
     * Client             | Java code (test)
     * foo('Han')         | wait...the latch is at 2...
     * foo('shot')        | wait...the latch is at 1...
     * foo('first')       | ok...the latch is at 0...now I continue...
     * foo('is a lie')    | /does not care anymore but the listener still gets called, because it wasn't cleared/
     *
     */
    @Test
    void test() throws InterruptedException {
        final var url = "https://www.primefaces.org/showcase-v8/ui/input/oneMenu.xhtml";
        final var driver = (ChromeDriver) await.until(identity());
        driver.navigate().to(url);
        final var devTools = driver.getDevTools();

        final AtomicReference<String> returnedString = new AtomicReference<>();
        final CountDownLatch countDownLatch = new CountDownLatch(3);
        devTools.createSession();
        devTools.send(Runtime.addBinding("foo", Optional.empty(), Optional.empty()));
        devTools.addListener(Runtime.bindingCalled(), call -> {
            if ("foo".equals(call.getName())) {
                returnedString.set(call.getPayload());
                countDownLatch.countDown();
            }
        });
        driver.executeScript("window.setTimeout(() => { foo('Han'); }, 500);");
        driver.executeScript("window.setTimeout(() => { foo('shot'); }, 1000);");
        driver.executeScript("window.setTimeout(() => { foo('first'); }, 1500);");
        driver.executeScript("window.setTimeout(() => { foo('is a lie'); }, 2000);");
        assertTrue(countDownLatch.await(4L, TimeUnit.SECONDS));
        assertEquals("first", returnedString.get());
        devTools.close();
    }

    WebDriverWait await;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void beforeEach() {
        await = new WebDriverWait(new ChromeDriver(), Duration.ofSeconds(4L));
    }

    @AfterEach
    void afterEach() {
        if (await != null) await.until(identity()).quit();
    }

    @AfterAll
    static void afterAll() {
        new Random().nextInt(2);
    }
}
