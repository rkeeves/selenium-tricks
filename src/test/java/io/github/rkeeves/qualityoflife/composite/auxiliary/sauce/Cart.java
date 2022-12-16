package io.github.rkeeves.qualityoflife.composite.auxiliary.sauce;

import io.github.rkeeves.qualityoflife.composite.auxiliary.core.Act;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.CompositeAct;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.Count;
import io.github.rkeeves.qualityoflife.composite.auxiliary.core.See;
import org.openqa.selenium.By;

public class Cart {

    public static final String CART_URL = "https://www.saucedemo.com/cart.html";

    private static final By BACKPACK_LINK = By.id("item_4_title_link");

    private static final By CART_ITEM = By.cssSelector(".cart_item");

    public static Act backpackMustBeInTheCart() {
        return CompositeAct.of(
                "Cart Page must show Backpack",
                See.element(BACKPACK_LINK)
        );
    }

    public static Act mustSeeItemCountEq(int expected) {
        return CompositeAct.of(
                "Cart Page must show " + expected + " items in the cart",
                Count.mustBe(CART_ITEM, expected)
        );
    }
}
