package io.github.rkeeves.interoperability;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.google.common.base.Functions.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CallJsFromJavaSyncWithoutArgsTest {

    /**
     * What's the goal?
     * Demonstrate how we can call JS functions from Java SYNCHRONOUSLY.
     *
     * What does it do?
     * We open a page which has a ton of arbitrary javascript functions and objects.
     * We evaluate js and get back the value (Selenium wraps it into a function).
     *
     * The script fragment provided by you will be executed as the BODY OF AN ANONYMOUS FUNCTION.
     * The arguments will be made available to the JavaScript via the "arguments" MAGIC VARIABLE.
     * I didn't make up the "MAGIC VARIABLE" part. The sentence is straight from the docs:
     * https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/JavascriptExecutor.html#executeScript(java.lang.String,java.lang.Object...)
     */
    @Test
    void test() {
        final var url = "https://www.primefaces.org/showcase-v8/ui/input/oneMenu.xhtml";
        final var driver = await.until(identity());
        driver.navigate().to(url);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        final var res = js.executeScript("return PrimeFaces.utils.isModalActive()");
        assertEquals(Boolean.FALSE, res);
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
