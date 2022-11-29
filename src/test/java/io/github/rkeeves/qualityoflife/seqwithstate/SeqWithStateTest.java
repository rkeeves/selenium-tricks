package io.github.rkeeves.qualityoflife.seqwithstate;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.rkeeves.qualityoflife.seqwithstate.auxiliary.SeqWithState;
import lombok.Value;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import static io.github.rkeeves.qualityoflife.seqwithstate.auxiliary.ChainingCustomCommands.attrGet;
import static io.github.rkeeves.qualityoflife.seqwithstate.auxiliary.ChainingCustomCommands.enterThat;
import static io.github.rkeeves.qualityoflife.seqwithstate.auxiliary.ChainingCustomCommands.openPage;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeqWithStateTest {

    /**
     * What's the goal?
     * Demonstrate the usage of a simple dumb wrapper classes to hide variables.
     * Yes...99% of the time using any kind of dynamic data in tests is avoidable
     * (and should be avoided, especially reading out data from the page and using it later).
     * But what about the 1%?
     *
     * What does it do?
     * This is a dumb example, but it shows the gist of it.
     *
     * If you need more than one state variable,
     * you can simply create an appropriate type to represent the state.
     * If you are not really into the whole 'type safety' thing, then you can use hashmaps.
     *
     * Why is this good at all?
     * This is not good...but...at least this way (immutable objects etc.),
     * you can keep track of things instead of mutating them randomly.
     * Also, because you are just passing in the side effects, you can run arbitrary code before and after
     * the execution of one 'step'.
     * Also, because you keep track of the state you can dump it too if errors happen, you also have a ref to the WebDriver too.
     * Lastly, because it is typed, and you are passing in essentially a binary function (a, b) -> c,
     * you can make your behaviors themselves stateless (and more modular).
     *
     * I'm 100% that you don't like the example below, and you're totally right...it is dumb.
     * Also, I'm 100% sure that you can create much better solutions.
     * This is just a small example of making some things stateless.
     */

    static final String URL = "https://www.saucedemo.com/";

    static final By USERNAME = By.cssSelector("*[data-test='username']");

    static final By PASSWORD = By.cssSelector("*[data-test='password']");

    @Test
    void test() {
        SeqWithState.withVoidState(theDriver)
                // Void -> Void
                .just(openPage(URL))
                // (Void, String) -> String
                .merge(attrGet(USERNAME, "id"), (__, unameId) -> unameId)
                // (String, String) -> StateWithTwoVariables
                .merge(attrGet(PASSWORD, "id"), StateWithTwoVariables::new)
                // just to show the state at this point
                .peek(state -> {
                    assertEquals("user-name", state.getUnameId());
                    assertEquals("password", state.getPasswordId());
                })
                // the 'enterThat' is as stateless as possible
                .just(enterThat(USERNAME, StateWithTwoVariables::getUnameId))
                .just(enterThat(PASSWORD, StateWithTwoVariables::getPasswordId));
    }

    @Value
    static class StateWithTwoVariables {
        String unameId;
        String passwordId;
    }

    ChromeDriver theDriver;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void beforeEach() {
        theDriver = new ChromeDriver();
    }

    @AfterEach
    void afterEach() {
        if (theDriver != null) theDriver.quit();
    }

    @AfterAll
    static void afterAll() {

    }
}
