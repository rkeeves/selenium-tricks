package io.github.rkeeves.interoperability;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.google.common.base.Functions.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CallJsFromJavaAsyncWithoutArgsTest {
    /**
     * What's the goal?
     * Demonstrate how we can call JS functions from Java ASYNCHRONOUSLY.
     * There are some EXTREME CAVEATS though:
     * - the Java side will WAIT UNTIL THE CALLBACK IS CALLED ON CLIENT-SIDE
     * - if the client does not call the callback at all,
     *   then you will get script time out exception
     * - if the client does not call the callback until the preconfigured timeframe /driver.manage().timeouts().getScriptTimeout()/
     *   then you will get script time out exception
     * - if for some reason the client calls the callback multiple times
     *   only the first callback will 'put your Java code back in action', aka other calls will be ignored.
     *
     * What does it do?
     * We open a page.
     * Then we essentially do 3 steps:
     * 1. add an attribute 'a' with value 'true' to an arbitrary DOM element
     * 2. add an attribute 'b' with value 'true' to an arbitrary DOM element
     * 3. pass back to Selenium the string 'everything went fine'
     *
     * We don't just run these scripts in sequence, but we use 'setTimeout' instead.
     * So we:
     * - create all the steps as arrow functions to have them in lexical scope
     * - 'aStep' will schedule 'bStep'
     * - 'bStep' will schedule 'cStep'
     * 'cStep' is actually the callback to selenium.
     * This object is always the item at the last index of the 'arguments' array, which is in scope
     * of our script (selenium provides this, and also wraps our code within an anonymous function).
     *
     * After all of this nonsense is done and we return to Java, we:
     * - ensure that the callback was called with the string 'everything went fine'
     * - ensure that the attributes on the arbitrary DOM element were set
     *
     * Unlike executing synchronous JavaScript,
     * scripts executed with this method must explicitly signal they are finished by invoking the provided callback.
     * This callback is always injected into the executed function as the last argument.
     *
     */
    @Test
    void test() {
        final var url = "https://www.primefaces.org/showcase-v8/ui/input/oneMenu.xhtml";
        final var driver = await.until(identity());
        driver.navigate().to(url);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        final var expectedValueReturnedByTheAsyncCall = "everything went fine";
        final var script = "" +
                "const cStep = arguments[arguments.length - 1];" +
                "const bStep = () => {" +
                "   document.getElementById('j_idt783').setAttribute('b', 'true');" +
                "   window.setTimeout(cStep('" + expectedValueReturnedByTheAsyncCall + "'), 500);" +
                "};" +
                "const aStep = () => {" +
                "  document.getElementById('j_idt783').setAttribute('a', 'true');" +
                "  window.setTimeout(bStep, 500);" +
                "};" +
                "window.setTimeout(aStep, 500);";
        final var actualValueReturnedByTheAsyncCall = js.executeAsyncScript(script);
        assertEquals(expectedValueReturnedByTheAsyncCall, actualValueReturnedByTheAsyncCall);
        final var element = driver.findElement(By.id("j_idt783"));
        assertEquals("true", element.getAttribute("a"));
        assertEquals("true", element.getAttribute("b"));
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

    }
}
