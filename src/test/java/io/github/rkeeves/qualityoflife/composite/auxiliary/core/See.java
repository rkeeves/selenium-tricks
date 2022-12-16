package io.github.rkeeves.qualityoflife.composite.auxiliary.core;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

@UtilityClass
public class See {

    public static Act element(By locator) {
        return LeafAct.of(
                "must be able to see element " + locator,
                await -> await.until(ExpectedConditions.visibilityOfElementLocated(locator))
        );
    }
}
