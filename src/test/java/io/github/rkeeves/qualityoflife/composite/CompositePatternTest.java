package io.github.rkeeves.qualityoflife.composite;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.CompositeAct;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.Episode;
import io.github.rkeeves.qualityoflife.composite.auxiliary.sauce.Cart;
import io.github.rkeeves.qualityoflife.composite.auxiliary.sauce.Inventory;
import io.github.rkeeves.qualityoflife.composite.auxiliary.sauce.Login;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CompositePatternTest {

    /**
     * What's the goal?
     * Demonstrate - through a simplified and heavy-handed implementation example -
     * that we can represent .
     *
     * What does it do?
     *
     * TLDR: We create a tree of functions.
     * Then we traverse this tree of functions, execute them,
     * and map the execution results into a totally differently coded tree.
     * This new tree stores te execution results.
     * At the end we traverse the result tree and print eyecandy to sout, stacktrace whatever.
     * The main point is that, by encoding the semantical relationship,
     * we can produce custom messages which have the whole history of things, like this:
     *
     * [ ] Add a backpack, then verify that backpack is in the cart
     *   [X] Goto Login Page, submit credentials, then land on Inventory Page
     *     [X] Goto Login Page
     *       [X] open url 'https://www.saucedemo.com/'
     *     [X] Fill Login Page fields
     *       [X] type text 'standard_user' into By.cssSelector: *[data-test='username']
     *       [X] type text 'secret_sauce' into By.cssSelector: *[data-test='password']
     *     ...
     *
     * This way we can get the whole tree of "steps and substeps" besides a stacktrace and Selenium error message on failure.
     * Also, the message formatting is not 'baked into the tree' you can use create your own, write to xml, etc.
     * Traversing the result tree is just plain old 'Visitor' pattern.
     *
     * The point of this example is NOT THE IMPLEMENTATION DETAILS!
     * The point of this example is that:
     * - datastructures are a thing even if they are only brought up in coding interview rounds,
     * - https://refactoring.guru/design-patterns/command
     * - https://refactoring.guru/design-patterns/composite
     * - https://refactoring.guru/design-patterns/visitor
     *
     * I didn't care about implementation details, as that wasn't really the point.
     * For example: The implementation short circuits on the first failing node, which I know is a 'questionable' choice.
     * Instead, we could map all nodes to 'skipped' or something, after a failing node if we wanted.
     * You can also spice things up by providing safety, aka representing passed|failed|skipped with their own types.
     * You can also wrap that totally unsafe throwable field into a maybe.
     * This example is not a guide about implementation.
     * This example is simply an illustration of data structures and abstractions.
     *
     * I didn't playaround much with custom Selenium ExpectedConditions and custom messages, as this example
     * is not really about Selenium itself, but about abstractions and data structures.
     * Aka this example is more about representing the history and state of your test,
     * not about babysitting Selenium.
     *
     * You can do whatever you want.
     * I just became bored with the 994th selenium example, always doing a simple sequence of library calls:
     * log("gonna crash?")
     * driver.findElement().click()
     * log("didnt")
     * log("well, lets see if it crashes if I foo it into oblivion")
     * driver.foo()
     * log("goodbye")
     */
    @Test
    void test() {
        final var await = new WebDriverWait(theDriver, Duration.ofSeconds(4L));
        Episode.play(await, JUST_A_REGULAR_N_ARY_TEE)
                .forEach(0, (level, someResult) -> {
                    System.out.printf("%s[%s] %s %n",
                            "  ".repeat(level),
                            someResult.isPassed() ? "X" : " ",
                            someResult.getAct().getNickname());
                    if (someResult.getT() != null) someResult.getT().printStackTrace();
                });
    }

    static final CompositeAct JUST_A_REGULAR_N_ARY_TEE = CompositeAct.of(
            "Add a backpack, then verify that backpack is in the cart",
            Login.gotoAndLoginWith("standard_user", "secret_sauce"),
            Inventory.addBackpackToCart(),
            Inventory.gotoCart(),
            Cart.backpackMustBeInTheCart(),
            // non-sense item count, intentionally
            Cart.mustSeeItemCountEq(69)
    );

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
