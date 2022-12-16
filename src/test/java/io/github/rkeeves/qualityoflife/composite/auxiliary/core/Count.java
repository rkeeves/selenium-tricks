package io.github.rkeeves.qualityoflife.composite.auxiliary.core;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

@UtilityClass
public class Count {

    public static Act mustBe(By locator, int expectedCountOfElement) {
        return LeafAct.of(
                "there must be " + expectedCountOfElement + " elements, by " + locator,
                await -> await.until(ExpectedConditions.numberOfElementsToBe(locator, expectedCountOfElement))
        );
    }
}
