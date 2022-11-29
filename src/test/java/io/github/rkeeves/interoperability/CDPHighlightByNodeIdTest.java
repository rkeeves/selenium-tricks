package io.github.rkeeves.interoperability;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v105.dom.DOM;
import org.openqa.selenium.devtools.v105.dom.model.RGBA;
import org.openqa.selenium.devtools.v105.overlay.Overlay;
import org.openqa.selenium.devtools.v105.overlay.model.HighlightConfig;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;

import static com.google.common.base.Functions.identity;
import static org.openqa.selenium.devtools.v105.dom.DOM.querySelector;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;

public class CDPHighlightByNodeIdTest {

    /**
     * What's the goal?
     * Demonstrate how can we paint an input GREEN.
     *
     * What does it do?
     * The tldr is that it paints the node GREEN with experimental CDP Overlay domain's appropriate command.
     * To do this we also need to acquire NodeIds via DOM domain,
     * and struggle with ctors and jump through other hoops and a lot more.
     * But it can be done...
     */
    @Test
    void test() {
        final var url = "https://www.saucedemo.com/";
        final var driver = (ChromeDriver) await.until(identity());
        driver.navigate().to(url);
        await.until(urlToBe("https://www.saucedemo.com/"));
        final var devTools = driver.getDevTools();
        devTools.createSession();
        devTools.send(DOM.enable(Optional.empty()));
        final var documentNode = devTools.send(DOM.getDocument(Optional.empty(), Optional.empty()));
        final var targetNodeId = devTools.send(querySelector(documentNode.getNodeId(), "*[data-test='username']"));
        devTools.send(Overlay.enable());
        final var green = new RGBA(34, 177, 76, Optional.empty());
        final var highlightConfig = highlightOfColor(green);
        devTools.send(Overlay.highlightNode(highlightConfig, Optional.of(targetNodeId), Optional.empty(), Optional.empty(), Optional.empty()));
        // Debug breakpoint the line below to see green
        System.out.println("It must be green");
    }

    static HighlightConfig highlightOfColor(RGBA green) {
        return new HighlightConfig(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(green),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
                // https://youtu.be/OrOYvVf6tIM?t=328
        );
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
