package io.github.rkeeves.waitingfor;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.google.common.base.Functions.identity;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class WaitingForVisualIndicatorTest {

    /**
     * What's the goal?
     * Demonstrate the usage of assertThat's polling behavior.
     *
     * What does it do?
     * We will select a country.
     * Selecting the country triggers xhr (AJAX, JSF etc.).
     * The client will update the selectable cities based on the country.
     * A small loading icon will appear.
     * We'll wait for this icon to disappear.
     * This technique has many problems on its own, but it is the most basic of all possible strategies.
     */
    @RepeatedTest(4)
    void example() {
        final String country = "Brazil";
        final String city = "Salvador";

        final var driver = await.until(identity());
        driver.navigate().to("https://www.primefaces.org/showcase/ui/ajax/dropdown.xhtml");

        final var countrySelect = By.cssSelector("div[id$=':country']");
        final var countryLabel = new ByChained(countrySelect, By.tagName("label"));
        final var countryPanel = By.cssSelector("div[id$=':country_panel']");
        final var countryOption = By.cssSelector("li[data-label='" + country + "']");
        final var citySelect = By.cssSelector("div[id$=':city']");
        final var cityLabel = new ByChained(citySelect, By.tagName("label"));
        final var cityPanel = By.cssSelector("div[id$=':city_panel']");
        final var cityOption = By.cssSelector("li[data-label='" + city + "']");
        final var spinner = By.cssSelector(".status-indicator .pi-spinner");

        await.until(elementToBeClickable(countryLabel)).click();
        await.until(visibilityOfElementLocated(countryPanel));
        await.until(elementToBeClickable(countryOption)).click();
        await.until(invisibilityOfElementLocated(countryPanel));
        await.until(textToBe(countryLabel, country));

        await.until(invisibilityOfElementLocated(spinner));

        await.until(elementToBeClickable(cityLabel)).click();
        await.until(visibilityOfElementLocated(cityPanel));
        await.until(elementToBeClickable(cityOption)).click();
        await.until(invisibilityOfElementLocated(cityPanel));
        await.until(textToBe(cityLabel, city));
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
