package io.github.rkeeves.listener;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BeWarnedTheWebDriverEventListenerExceptionsAreNotIgnoredTest {

    /**
     * What's the goal?
     * Demonstrate that throwables thrown by your WebDriverEventListener
     * WONT BE IGNORED (aka consumed by the adapter implementation).
     *
     * What does it do?
     * We create a listener which will throw a Runtime Exception whenever you click.
     * We then open a page.
     * Click and expect a Runtime Exception.
     *
     * WebDriverEventListener and WebDriverListener DIFFER IN THIS REGARD!
     *
     * Well...more precisely:
     * The listeners themselves have nothing to do with this.
     * The Adapter/Observable - however you call it - is the class where this behavior is found.
     * Basically when it iterates over its listeners it can either wrap the call to the listener in a try catch or not.
     *
     * Q: 'Omg, what's the point?!4!444'
     * A: If you are doing something naughty in the listener, like logging, IO, some magic waiting mechanism,
     * be aware that any throwable thrown in your listener will 'bubble up' to top.
     * TLDR: If you are working with a "company framework" and it utilizes crazy listeners,
     * then don't port the code to Selenium 4's new WebDriverListener because your tests might get f...ed.
     * Just ignore the @Deprecated warning or come up with a better solution than 'listeners with throwables'.
     */
    @Test
    void test() {
        driver.navigate().to("https://www.saucedemo.com/");
        final var exception = assertThrows(RuntimeException.class, () -> {
            driver.findElement(By.cssSelector("*[data-test='login-button']")).click();
        });
        assertEquals("They're thoroughly all-right and decent fellas with their hit single: In Bloom",
                exception.getMessage());
    }

    WebDriver driver;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void beforeEach() {
        final var eventFiringWebDriver = new EventFiringWebDriver(new ChromeDriver());
        eventFiringWebDriver.register(new ListenerWhichThrowsOnClick());
        driver = eventFiringWebDriver;
    }

    @AfterEach
    void afterEach() {
        if (driver != null) driver.quit();
    }

    @AfterAll
    static void afterAll() {

    }

    static class ListenerWhichThrowsOnClick implements WebDriverEventListener {

        @Override
        public void beforeAlertAccept(WebDriver driver) {

        }

        @Override
        public void afterAlertAccept(WebDriver driver) {

        }

        @Override
        public void afterAlertDismiss(WebDriver driver) {

        }

        @Override
        public void beforeAlertDismiss(WebDriver driver) {

        }

        @Override
        public void beforeNavigateTo(String url, WebDriver driver) {

        }

        @Override
        public void afterNavigateTo(String url, WebDriver driver) {

        }

        @Override
        public void beforeNavigateBack(WebDriver driver) {

        }

        @Override
        public void afterNavigateBack(WebDriver driver) {

        }

        @Override
        public void beforeNavigateForward(WebDriver driver) {

        }

        @Override
        public void afterNavigateForward(WebDriver driver) {

        }

        @Override
        public void beforeNavigateRefresh(WebDriver driver) {

        }

        @Override
        public void afterNavigateRefresh(WebDriver driver) {

        }

        @Override
        public void beforeFindBy(By by, WebElement element, WebDriver driver) {

        }

        @Override
        public void afterFindBy(By by, WebElement element, WebDriver driver) {

        }

        @Override
        public void beforeClickOn(WebElement element, WebDriver driver) {
            throw new RuntimeException("They're thoroughly all-right and decent fellas with their hit single: In Bloom");
        }

        @Override
        public void afterClickOn(WebElement element, WebDriver driver) {

        }

        @Override
        public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {

        }

        @Override
        public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {

        }

        @Override
        public void beforeScript(String script, WebDriver driver) {

        }

        @Override
        public void afterScript(String script, WebDriver driver) {

        }

        @Override
        public void beforeSwitchToWindow(String windowName, WebDriver driver) {

        }

        @Override
        public void afterSwitchToWindow(String windowName, WebDriver driver) {

        }

        @Override
        public void onException(Throwable throwable, WebDriver driver) {

        }

        @Override
        public <X> void beforeGetScreenshotAs(OutputType<X> target) {

        }

        @Override
        public <X> void afterGetScreenshotAs(OutputType<X> target, X screenshot) {

        }

        @Override
        public void beforeGetText(WebElement element, WebDriver driver) {

        }

        @Override
        public void afterGetText(WebElement element, WebDriver driver, String text) {

        }
    }
}
