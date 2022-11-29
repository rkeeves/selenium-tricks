package io.github.rkeeves.waitingfor;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.google.common.base.Functions.identity;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class WaitingForJSFAjaxQueueEmptinessTest {

    /**
     * What's the goal?
     * Demonstrate the usage of JSF client side AJAX queue.
     *
     * What does it do?
     * We will select a country.
     * Selecting the country triggers xhr (AJAX, JSF etc.).
     * The client will update the selectable cities based on the country.
     * We must wait for the ajax to end, and for the client to rerender the modified DOM.
     * Otherwise we'd get all kinds of glitches.
     * This example's main focus is to give a simple example about AjaxQueue.
     * (see JSR 372 for the official specification of client-side queue).
     */
    @RepeatedTest(4)
    void example() {
        final String country = "Brazil";
        final String city = "Salvador";

        final var driver = await.until(identity());
        driver.navigate().to("https://www.primefaces.org/showcase-v8/ui/ajax/dropdown.xhtml");

        final var countrySelect = By.cssSelector("div[id$=':country']");
        final var countryPanelTrigger = new ByChained(countrySelect, By.tagName("label"));
        final var countryPanel = By.cssSelector("div[id$=':country_panel']");
        final var countryOption = By.cssSelector("li[data-label='" + country + "']");
        final var citySelect = By.cssSelector("div[id$=':city']");
        final var cityPanelTrigger = new ByChained(citySelect, By.tagName("label"));
        final var cityPanel = By.cssSelector("div[id$=':city_panel']");
        final var cityOption = By.cssSelector("li[data-label='" + city + "']");

        await.until(elementToBeClickable(countryPanelTrigger)).click();
        await.until(visibilityOfElementLocated(countryPanel));
        var option = driver.findElement(countryOption);
        new Actions(driver)
                .moveToElement(option)
                .clickAndHold(option)
                .release(option)
                .perform();
        await.until(invisibilityOfElementLocated(countryPanel));
        await.until(textToBe(countryPanelTrigger, country));

        await.until(WaitingForJSFAjaxQueueEmptinessTest::ajaxAndAnimationsDone);

        await.until(elementToBeClickable(cityPanelTrigger)).click();
        await.until(visibilityOfElementLocated(cityPanel));
        option = driver.findElement(cityOption);
        new Actions(driver)
                .moveToElement(option)
                .clickAndHold(option)
                .release(option)
                .perform();
        await.until(invisibilityOfElementLocated(cityPanel));
        await.until(textToBe(cityPanelTrigger, city));
    }

    WebDriverWait await;

    private static Boolean ajaxAndAnimationsDone(WebDriver webDriver) {
        return (Boolean) ((JavascriptExecutor) webDriver)
                .executeScript("return (jQuery.active == 0 && jQuery(':animated').length == 0) && (window.PrimeFaces.ajax.Queue.isEmpty())");
    }

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
