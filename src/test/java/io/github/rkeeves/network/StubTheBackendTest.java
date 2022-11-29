package io.github.rkeeves.network;

import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.remote.http.HttpResponse;
import org.openqa.selenium.remote.http.Route;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static com.google.common.base.Functions.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.remote.http.Contents.utf8String;

public class StubTheBackendTest {

    /**
     * What's the goal?
     * Demonstrate usage of API stubbing via Routes.
     *
     * What does it do?
     * We visit a book store app.
     * The client (app) tries to fetch the books from the backend.
     * In this simple example we intercept this "get all books" API call, and we instead provide the response.
     * We always return a "canned" answer to the client app, this is called stubbing.
     * Although this is a pretty dumb example, you get the point.
     */
    @Test
    void example() {
        final var expectedBook = Book.builder().build();
        final var stubbedResponse = new Gson().toJson(
                GetAllBooksApiResponse.builder()
                        .books(List.of(expectedBook))
                        .build()
        );
        final var driver = (ChromeDriver) await.until(identity());
        final var interceptor = new NetworkInterceptor(
                driver,
                Route.matching(req -> HttpMethod.GET.equals(req.getMethod()) && req.getUri().matches(TOTALLY_LEGIT_REGEX))
                        .to(() -> req -> new HttpResponse()
                                .setStatus(200)
                                .setHeader("Content-Type", MediaType.JSON_UTF_8.toString())
                                .setContent(utf8String(stubbedResponse))));
        driver.navigate().to("https://demoqa.com/books");
        await.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".action-buttons a"), 1));
        final var row = driver.findElement(By.cssSelector(".rt-tbody .rt-tr"));
        final var title = row.findElement(By.tagName("a")).getText();
        final var cells = row.findElements(By.cssSelector(".rt-td"));
        final var author = cells.get(2).getText();
        final var publisher = cells.get(3).getText();
        assertEquals(expectedBook.getTitle(), title);
        assertEquals(expectedBook.getAuthor(), author);
        assertEquals(expectedBook.getPublisher(), publisher);
    }

    @Data
    @Builder
    static class GetAllBooksApiResponse {
        @SerializedName("books")
        List<Book> books;
    }

    @Data
    @Builder
    static class Book {
        @Builder.Default
        @SerializedName("isbn")
        String isbn = "9696969696969";
        @Builder.Default
        String title = "GitGud: Kernel Panic Boogalo";
        @Builder.Default
        @SerializedName("subTitle")
        String subTitle = "How the University of Minnesota has been banned from contributing to the Linux kernel";
        @Builder.Default
        @SerializedName("author")
        String author = "Prof. R. U. Serious";
        @Builder.Default
        @SerializedName("publish_date")
        String publishDate = "2020-06-04T08:48:39.000Z";
        @Builder.Default
        @SerializedName("publisher")
        String publisher = "This really happened";
        @Builder.Default
        @SerializedName("pages")
        Integer pages = 69;
        @Builder.Default
        @SerializedName("description")
        String description = "The frat boyz did it again!";
        @Builder.Default
        @SerializedName("website")
        String website = "https://youtu.be/81szj1vpEu8?t=99";
    }

    static final String TOTALLY_LEGIT_REGEX = ".*/BookStore/v1/Books";

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
