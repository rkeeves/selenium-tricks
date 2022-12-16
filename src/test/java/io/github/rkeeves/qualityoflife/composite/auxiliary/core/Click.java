package io.github.rkeeves.qualityoflife.composite.auxiliary.core;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

@UtilityClass
public class Click {

    public static Act on(By locator) {
        return LeafAct.of(
                "click on " + locator,
                await -> await.until(ExpectedConditions.elementToBeClickable(locator)).click()
        );
    }
}
