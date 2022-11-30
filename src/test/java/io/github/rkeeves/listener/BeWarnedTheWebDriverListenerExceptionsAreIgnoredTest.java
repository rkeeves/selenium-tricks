package io.github.rkeeves.listener;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

public class BeWarnedTheWebDriverListenerExceptionsAreIgnoredTest {

    /**
     * What's the goal?
     * Demonstrate that throwables thrown by your WebDriverListener
     * WILL BE IGNORED (aka consumed by the adapter implementation).
     *
     * What does it do?
     * We create a listener which will throw a Runtime Exception whenever you click.
     * We then open a page.
     * Click and we DON'T expect a Runtime Exception.
     *
     * WebDriverEventListener and WebDriverListener DIFFER IN THIS REGARD!
     *
     * Well...more precisely:
     * The listeners themselves have nothing to do with this.
     * The Adapter/Observable - however you call it - is the class where this behavior is found.
     * Basically when it iterates over its listeners it can either wrap the call to the listener in a try catch or not.
     *
     * Q: 'Omg, what's the point?!4!444'
     * A: Described in BeWarnedTheWebDriverEventListenerExceptionsAreNotIgnoredTest.java
     */
    @Test
    void test() {
        driver.navigate().to("https://www.saucedemo.com/");
        driver.findElement(By.cssSelector("*[data-test='login-button']")).click();
    }

    WebDriver driver;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void beforeEach() {
        driver = new EventFiringDecorator<>(new ListenerWhichThrowsOnClick())
                .decorate(new ChromeDriver());
    }

    @AfterEach
    void afterEach() {
        if (driver != null) driver.quit();
    }

    @AfterAll
    static void afterAll() {

    }

    static class ListenerWhichThrowsOnClick implements WebDriverListener {

        @Override
        public void beforeClick(WebElement element) {
            throw new RuntimeException("They're thoroughly allright and decent fellas with their hit single: In Bloom");
        }
    }
}
