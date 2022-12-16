package io.github.rkeeves.qualityoflife.composite.auxiliary.sauce;

import io.github.rkeeves.qualityoflife.composite.auxiliary.core.Act;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.Click;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.CompositeAct;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.Type;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.Url;
import org.openqa.selenium.By;

public class Login {

    private static final String LOGIN_URL = "https://www.saucedemo.com/";

    private static final By USERNAME = By.cssSelector("*[data-test='username']");

    private static final By PASSWORD = By.cssSelector("*[data-test='password']");

    private static final By SUBMIT = By.cssSelector("*[data-test='login-button']");

    public static Act gotoAndLoginWith(String username, String password) {
        return CompositeAct.of(
                "Goto Login Page, submit credentials, then land on Inventory Page",
                CompositeAct.of(
                        "Goto Login Page",
                        Url.open(LOGIN_URL)
                ),
                CompositeAct.of(
                        "Fill Login Page fields",
                        Type.text(USERNAME, username),
                        Type.text(PASSWORD, password)
                ),
                CompositeAct.of(
                        "Submit the Login Page form",
                        Click.on(SUBMIT)
                ),
                CompositeAct.of(
                        "Must be at Inventory Page",
                        Url.mustBe(Inventory.INVENTORY_URL)
                )
        );
    }
}
