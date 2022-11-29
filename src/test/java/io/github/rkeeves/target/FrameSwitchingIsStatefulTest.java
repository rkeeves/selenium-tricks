package io.github.rkeeves.target;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Consumer;

import static com.google.common.base.Functions.identity;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class FrameSwitchingIsStatefulTest {


    /**
     * What's the goal?
     * Demonstrate the usage of Frames.
     *
     * What does it do?
     * We open nested dialogs.
     * To be able to interact with elements in them, we must switch frames.
     * This 'switching of frames' is stateful though. See: https://www.w3.org/TR/webdriver1/#dfn-switch-to-frame
     */
    @Test
    void example() {
        final var expectedValue = "some value";

        final var driver = await.until(identity());
        await.until(identity()).navigate().to("https://www.primefaces.org/showcase/ui/df/nested.xhtml");
        driver.findElement(By.cssSelector("*[id='rootform:btn']")).click();

        final var dialog1 = await.until(visibilityOfElementLocated(By.cssSelector("*[id='rootform:btn_dlg']")));
        final var dialogIframe1 = dialog1.findElement(By.tagName("iframe"));
        driver.switchTo().frame(dialogIframe1);
        driver.findElement(By.cssSelector("*[id='level1form:level1btn']")).click();
        driver.switchTo().defaultContent();

        final var dialog2 =  await.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("*[id='level1form:level1btn_dlg']")));
        final var dialogIframe2 = dialog2.findElement(By.tagName("iframe"));
        driver.switchTo().frame(dialogIframe2);
        driver.findElement(By.cssSelector("*[id='level2form:level2button']")).click();
        driver.switchTo().defaultContent();

        final var dialog3 =  await.until(presenceOfElementLocated(By.cssSelector("*[id='level2form:level2button_dlg']")));
        final var dialogIframe3 = dialog3.findElement(By.tagName("iframe"));
        final Consumer<WebDriver> fillInputEffect = webDriver -> webDriver.findElement(By.cssSelector("*[id='level3form:val']")).sendKeys(expectedValue);
        final Consumer<WebDriver> submitEffect = webDriver -> driver.findElement(By.cssSelector("*[id='level3form:j_idt10']")).click();
        assertThrows(NoSuchElementException.class, () -> fillInputEffect.accept(driver));
        assertThrows(NoSuchElementException.class, () -> submitEffect.accept(driver));
        driver.switchTo().frame(dialogIframe3);
        fillInputEffect.accept(driver);
        submitEffect.accept(driver);

        driver.switchTo().defaultContent();
        final var growl = By.cssSelector(".ui-growl-item p");
        await.until(textToBePresentInElementLocated(growl, expectedValue));
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
