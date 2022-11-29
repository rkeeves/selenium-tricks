package io.github.rkeeves.browserstate;


import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import lombok.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class AuthOnceViaPageBeforeAllAndReuseStateTest {

    /**
     * What's the goal?
     * Demonstrate the usage of WebStorage and Cookies.
     *
     * What does it do?
     * "Before All" we do a quick manual login.
     * We extract localstorage items and cookies, then kill this WebDriver.
     * Before each test we plop those cookies and localstorage items into the new WebDriver.
     */
    @RepeatedTest(2)
    void example() {
        await.until(identity()).navigate().to("https://demoqa.com/books");
        await.until(textToBePresentInElementLocated(By.id("userName-value"), UNAME));
        await.until(elementToBeClickable(By.cssSelector(".books-wrapper #submit"))).click();
        await.until(urlToBe("https://demoqa.com/login"));
    }

    static final String UNAME = "someuser";

    static final String PASS = "someuser1A@";

    static Set<Cookie> cookieSet;

    static Map<String, String> storageMap;

    WebDriverWait await;

    @BeforeAll
    static void beforeAll() throws Exception {
        WebDriverManager.chromedriver().setup();
        try(final var closeable = DriverQuitter.of(
                new WebDriverWait(new ChromeDriver(), Duration.ofSeconds(4L))
        )) {
            final var await = closeable.await;
            await.until(identity()).navigate().to("https://demoqa.com/login");
            await.until(visibilityOfElementLocated(By.id("userName")))
                    .sendKeys(UNAME);
            await.until(visibilityOfElementLocated(By.id("password")))
                    .sendKeys(PASS);
            final var login = await.until(visibilityOfElementLocated(By.id("login")));
            new Actions(await.until(identity()))
                    .scrollToElement(login)
                    .click(login)
                    .perform();
            await.until(urlToBe("https://demoqa.com/profile"));
            final var driver = await.until(identity());
            final Predicate<Cookie> cookieHasDomain = c -> "demoqa.com".equals(c.getDomain());
            cookieSet = driver.manage()
                    .getCookies()
                    .stream().filter(cookieHasDomain)
                    .collect(Collectors.toSet());
            final var localStorage = ((WebStorage) driver).getLocalStorage();
            storageMap = new HashMap<>();
            for (var key : localStorage.keySet()) {
                storageMap.put(key, localStorage.getItem(key));
            }
        }
    }

    @BeforeEach
    void beforeEach() {
        Assumptions.assumeFalse(cookieSet == null);
        Assumptions.assumeFalse(storageMap == null);
        await = new WebDriverWait(new ChromeDriver(), Duration.ofSeconds(4L));
        await.until(identity()).navigate().to("https://demoqa.com/");
        await.until(urlToBe("https://demoqa.com/"));
        final var driver = await.until(identity());
        for (var cookie : cookieSet) {
            driver.manage().addCookie(cookie);
        }
        final var localStorage = ((WebStorage) driver).getLocalStorage();
        for (var item : storageMap.entrySet()) {
            localStorage.setItem(item.getKey(), item.getValue());
        }
    }

    @AfterEach
    void afterEach() {
        if (await != null) await.until(identity()).quit();
    }

    @Value(staticConstructor = "of")
    static class DriverQuitter implements AutoCloseable {

        @Getter
        WebDriverWait await;

        @Override
        public void close() throws Exception {
            if (await != null) await.until(identity()).quit();
        }
    }
}
