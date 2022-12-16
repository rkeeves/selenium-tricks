package io.github.rkeeves.qualityoflife.composite.auxiliary.sauce;

import io.github.rkeeves.qualityoflife.composite.auxiliary.core.Act;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.Click;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.CompositeAct;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.Url;
import org.openqa.selenium.By;

public class Inventory {

    public static final String INVENTORY_URL = "https://www.saucedemo.com/inventory.html";

    private static final By ADD_BACKPACK = By.cssSelector("*[data-test='add-to-cart-sauce-labs-backpack']");

    private static final By SHOPPING_CART_LINK = By.cssSelector("a.shopping_cart_link");

    public static Act addBackpackToCart() {
        return CompositeAct.of(
                "Order a backpack on the Inventory Page",
                CompositeAct.of(
                        "Click on Backpack -> Add to cart",
                        Click.on(ADD_BACKPACK)
                )
        );
    }

    public static Act gotoCart() {
        return CompositeAct.of(
                "Go to Cart page from the Inventory Page",
                CompositeAct.of(
                        "Click on the shopping cart link",
                        Click.on(SHOPPING_CART_LINK)
                ),
                CompositeAct.of(
                        "Must be at Cart Page",
                        Url.mustBe(Cart.CART_URL)
                )
        );
    }
}
