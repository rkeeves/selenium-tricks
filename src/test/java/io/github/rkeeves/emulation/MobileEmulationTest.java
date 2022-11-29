package io.github.rkeeves.emulation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Functions.identity;

public class MobileEmulationTest {

    /**
     *
     * What's the goal?
     * Demonstrate the usage of emulation.
     *
     * What does it do?
     * We initialize the browser context to mimic a Nexus5 device (almost).
     */
    @Test
    void test() {
        final var driver = await.until(identity());
        driver.navigate().to("https://vimeo.com/watch");
        await.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".topnav_mobile_header_search"))).click();
        await.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content .js-search_form input[name='q']"))).sendKeys("cats");
        await.until(ExpectedConditions.elementToBeClickable(By.cssSelector("*[data-action='search.selectsuggestion']"))).click();
        await.until(ExpectedConditions.urlToBe("https://vimeo.com/search?q=cats"));
        final var onDemand = By.cssSelector("*[data-tab-id='ondemand']");
        final var onDemandElement = await.until(ExpectedConditions.elementToBeClickable(onDemand));
        new Actions(driver)
                .setActivePointer(PointerInput.Kind.TOUCH, "default touch")
                .moveToElement(onDemandElement, -10, 10)
                .doubleClick()
                .perform();
        await.until(ExpectedConditions.attributeContains(By.cssSelector(".tab.active"), "data-tab-id", "ondemand"));
    }

    WebDriverWait await;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void beforeEach() {
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", "Nexus 5");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        await = new WebDriverWait(new ChromeDriver(chromeOptions), Duration.ofSeconds(8L));
    }

    @AfterEach
    void afterEach() {
        if (await != null) await.until(identity()).quit();
    }

    @AfterAll
    static void afterAll() {

    }
}
