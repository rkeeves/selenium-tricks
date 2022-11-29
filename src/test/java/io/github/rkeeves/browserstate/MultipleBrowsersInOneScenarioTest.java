package io.github.rkeeves.browserstate;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipleBrowsersInOneScenarioTest {

    /**
     * What's the goal?
     * Demonstrate that multiple WebDriver instances do NOT share state.
     *
     * What does it do?
     * Two different WebDrivers are instantiated: Alpha and Beta.
     * Both sign in to the same app with different users: standard_user and problem_user.
     * After signing in, we check whether the two WebDrivers have different cookies (username is stored as a cookie).
     */
    @Test
    void example() {
        final var expectedUserAlpha = "standard_user";
        final var actualUserAlpha = AppSteps.using(alpha)
                .visit()
                .fillUname(expectedUserAlpha)
                .fillPass("secret_sauce")
                .submit()
                .shouldBeInInventory()
                .acquireCurrentUser()
                .orElse("cookie did not exist");

        final var expectedUserBeta = "problem_user";
        final var actualUserBeta = AppSteps.using(beta)
                .visit()
                .fillUname(expectedUserBeta)
                .fillPass("secret_sauce")
                .submit()
                .shouldBeInInventory()
                .acquireCurrentUser()
                .orElse("cookie did not exist");

        assertEquals(expectedUserAlpha, actualUserAlpha);
        assertEquals(expectedUserBeta, actualUserBeta);
    }

    WebDriverWait alpha;

    WebDriverWait beta;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void beforeEach() {
        alpha = new WebDriverWait(new ChromeDriver(), Duration.ofSeconds(4L));
        beta = new WebDriverWait(new ChromeDriver(), Duration.ofSeconds(4L));
    }

    @AfterEach
    void afterEach() {
        if (alpha != null) alpha.until(Function.identity()).quit();
        if (beta != null) beta.until(Function.identity()).quit();
    }

    @Value(staticConstructor = "using")
    static class AppSteps {

        WebDriverWait await;

        public AppSteps visit() {
            await.until(Function.identity()).navigate().to("https://www.saucedemo.com/");
            return this;
        }

        public AppSteps fillUname(String uname) {
            final var by = By.cssSelector("*[data-test='username']");
            await.until(ExpectedConditions.visibilityOfElementLocated(by))
                    .sendKeys(uname);
            return this;
        }

        public AppSteps fillPass(String pass) {
            final var by = By.cssSelector("*[data-test='password']");
            await.until(ExpectedConditions.visibilityOfElementLocated(by))
                    .sendKeys(pass);
            return this;
        }

        public AppSteps submit() {
            final var by = By.cssSelector("*[data-test='login-button']");
            await.until(ExpectedConditions.elementToBeClickable(by))
                    .click();
            return this;
        }

        public AppSteps shouldBeInInventory() {
            await.until(ExpectedConditions.urlToBe("https://www.saucedemo.com/inventory.html"));
            return this;
        }

        public Optional<String> acquireCurrentUser() {
            final var options = await.until(Function.identity()).manage();
            return Optional.ofNullable(options.getCookieNamed("session-username"))
                    .map(Cookie::getValue);
        }
    }
}
