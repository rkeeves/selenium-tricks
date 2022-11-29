package io.github.rkeeves.waitingfor;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.http.Filter;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Functions.identity;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class WaitingForRequestToFinishTest {

    /**
     * What's the goal?
     * Demonstrate the usage of Request/Response inspection.
     *
     * What does it do?
     * We will select a country.
     * Selecting the country triggers xhr (AJAX, JSF etc.).
     * The client will update the selectable cities based on the country.
     * We must wait for the ajax to end.
     * This is a contrived example...also just waiting for the POST to finish might not be sufficient if:
     * - the client does other long tasks (like heavy DOM manipulation)
     * - the client - when the POST finishes - starts another request
     */
    @RepeatedTest(4)
    void example() throws InterruptedException {

        final String country = "Brazil";
        final String city = "Salvador";

        final var driver = (ChromeDriver) await.until(identity());

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final Filter filter = next -> {
            return req -> {
                if (req.getMethod().equals(HttpMethod.POST) && req.getUri().matches(TOTALLY_LEGIT_REGEX)) {
                    countDownLatch.countDown();
                }
                return next.execute(req);
            };
        };
        final var interceptor = new NetworkInterceptor(driver, filter);

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

        assertTrue(countDownLatch.await(4L, TimeUnit.SECONDS));

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

    static final String TOTALLY_LEGIT_REGEX = ".*/dropdown\\.xhtml";

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
