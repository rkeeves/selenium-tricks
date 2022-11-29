package io.github.rkeeves.browserstate;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;

public class AuthOnceViaCookiesAndRestBeforeAllAndReuseStateTest {

    /**
     * What's the goal?
     * Demonstrate the usage of cookie manipulation and directly calling the backend API.
     *
     * What does it do?
     * "Before All" we call the endpoints to acquire the token.
     * We store it in a static field.
     * Later on, before each test, we add cookies to the webdriver.
     * Dirty, ugly, imperative, stateful, but gets the job done.
     */
    @RepeatedTest(2)
    void example() {
        await.until(identity()).navigate().to("https://demoqa.com/books");
        await.until(elementToBeClickable(By.id("userName-value"))).click();
        await.until(elementToBeClickable(By.cssSelector(".books-wrapper #submit")))
                .click();
        await.until(urlToBe("https://demoqa.com/login"));
    }

    static final String UNAME = "someuser";

    static final String PASS = "someuser1A@";

    static LoginResponseDTO loginResponseDTO;

    WebDriverWait await;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
        RestAssured.baseURI = "https://demoqa.com";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.ofEntries(
                        Map.entry("userName", UNAME),
                        Map.entry("password", PASS)
                ))
                .post("/Account/v1/GenerateToken")
                .then()
                .statusCode(HttpStatus.SC_OK);

        loginResponseDTO = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.ofEntries(
                        Map.entry("userName", UNAME),
                        Map.entry("password", PASS)
                ))
                .post("/Account/v1/Login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().body().as(LoginResponseDTO.class);
    }

    @UtilityClass
    static class DemoQaCookies {

        public static List<Cookie> cookiesFor(LoginResponseDTO loginResponseDTO) {
            return List.of(
                    cookieOf("userID", loginResponseDTO.getUserId()),
                    cookieOf("userName", loginResponseDTO.getUsername()),
                    cookieOf("token", loginResponseDTO.getToken()),
                    cookieOf("expires", loginResponseDTO.getExpires())
            );
        }

        private static Cookie cookieOf(String key, String val) {
            return new Cookie.Builder(key, val)
                    .path("/")
                    .domain("demoqa.com")
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginResponseDTO {
        @JsonProperty("userId")
        String userId;
        @JsonProperty("username")
        String username;
        @JsonProperty("password")
        String password;
        @JsonProperty("token")
        String token;
        @JsonProperty("expires")
        String expires;
        @JsonProperty("created_date")
        String createdDate;
        @JsonProperty("isActive")
        Boolean isActive;
    }

    @BeforeEach
    void beforeEach() {
        Assumptions.assumeFalse(loginResponseDTO == null);
        final var driver = new ChromeDriver();
        await = new WebDriverWait(driver, Duration.ofSeconds(4L));
        driver.navigate().to("https://demoqa.com/books");
        await.until(urlToBe("https://demoqa.com/books"));
        for (var cookie : DemoQaCookies.cookiesFor(loginResponseDTO)) {
            driver.manage().addCookie(cookie);
        }
    }

    @AfterEach
    void afterEach() {
        if (await != null) await.until(identity()).quit();
    }
}
