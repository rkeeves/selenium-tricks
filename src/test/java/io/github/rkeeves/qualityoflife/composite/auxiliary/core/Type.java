package io.github.rkeeves.qualityoflife.composite.auxiliary.core;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

@UtilityClass
public class Type {


    public static Act text(By locator, String text) {
        return LeafAct.of(
                "type text '" + text + "' into " + locator,
                await -> await.until(ExpectedConditions.presenceOfElementLocated(locator)).sendKeys(text)
        );
    }
}
