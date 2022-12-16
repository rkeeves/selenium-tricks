package io.github.rkeeves.pageobject;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PageObjectTest {

    /**
     * What's the goal?
     * Demonstrate how we can utilize the famous Page Object Model with...khmm....'field initialization'.
     *
     * What does it do?
     *
     * We create a LoginPage, and we use Selenium to inject WebElements into it.
     * Be aware that the whole PageFactory thing is just a hack.
     * Use it with caution.
     */
    @Test
    void test() {
        final List<By> findBys = new ArrayList<>();
        driver = new EventFiringDecorator<>(new FindElementAccumulator(findBys)).decorate(new ChromeDriver());
        driver.navigate().to("https://www.saucedemo.com/");
        final var loginPage = new LoginPage();
        assertNull(loginPage.username);
        assertNull(loginPage.password);
        assertNull(loginPage.submit);
        assertThat(findBys, hasSize(0));

        PageFactory.initElements(driver, loginPage);
        assertNotNull(loginPage.username);
        assertNotNull(loginPage.password);
        assertNotNull(loginPage.submit);
        assertThat(findBys, hasSize(0));

        // https://alexanderontesting.com/2018/05/21/c-and-the-disappearing-pagefactory-my-next-steps-in-selenium-testing/
        final var runtimeGeneratedProxyClass = loginPage.username.getClass().toString();
        assertThat(runtimeGeneratedProxyClass, matchesPattern("class com\\.sun\\.proxy\\.\\$Proxy\\d+"));

        loginPage.getUsername();
        assertThat(findBys, hasSize(0));

        loginPage.getUsername().sendKeys("a");
        assertThat(findBys, hasSize(1));
        assertThat(findBys, hasItem(By.cssSelector("*[data-test='username']")));
        loginPage.getPassword().sendKeys("a");
        assertThat(findBys, hasSize(2));
        assertThat(findBys, hasItems(
                By.cssSelector("*[data-test='username']"),
                By.cssSelector("*[data-test='password']")
        ));
    }

    @RequiredArgsConstructor
    public static class FindElementAccumulator implements WebDriverListener {

        private final List<By> bysUsedForFind;

        @Override
        public void beforeFindElement(WebDriver driver, By locator) {
            bysUsedForFind.add(locator);
        }
    }

    @Getter
    @Setter
    static class LoginPage {

        @FindBy(css = "*[data-test='username']")
        private WebElement username;

        @FindBy(css = "*[data-test='password']")
        private WebElement password;

        @FindBy(css = "*[data-test='login-button']")
        private WebElement submit;
    }

    WebDriver driver;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void beforeEach() {

    }

    @AfterEach
    void afterEach() {
        if (driver != null) driver.quit();
    }

    @AfterAll
    static void afterAll() {

    }
}
