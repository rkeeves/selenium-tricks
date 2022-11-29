package io.github.rkeeves.interoperability;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.hamcrest.Matchers;
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
import static org.hamcrest.MatcherAssert.assertThat;

public class PinScriptTest {

    /**
     * What's the goal?
     * Demonstrate how pin script works.
     * Put debug breakpoint on last line.
     *
     * What does it do?
     * Imagine you want to run the same js code 3 times.
     * Each time you execute it, you have to send basically the text on wire.
     * To combat this you can 'pin' scripts, so you would only send the text when it is not found.
     * Like imagine you add the code to the page, then you navigate elsewhere.
     * In that case - in the new page - window, document etc. don't hold the old state, so your magic code needs to be resent.
     * This pin script mechanism is just eye-candy, and you are probably not running 1000 lines of js code,
     * so you probably wont use it. If you happen to use you have to be aware though that basically you are putting
     * items into a synchronized HashMap.
     */
    @Test
    void test() {
        final var url = "https://www.primefaces.org/showcase-v8/ui/input/oneMenu.xhtml";
        final var driver = await.until(identity());
        driver.navigate().to(url);
        final var someLabel = driver.findElement(By.id("j_idt719:j_idt722"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        final var makeRedBorder = js.pin("arguments[0].style.border = '4px solid red';");
        js.executeScript(makeRedBorder, someLabel);
        final var style = someLabel.getAttribute("style");
        // Debug breakpoint the line below
        assertThat(style, Matchers.containsString("border: 4px solid red"));
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
