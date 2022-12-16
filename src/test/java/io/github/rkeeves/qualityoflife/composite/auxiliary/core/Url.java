package io.github.rkeeves.qualityoflife.composite.auxiliary.core;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.function.Function;

@UtilityClass
public class Url {

    public static Act open(String url) {
        return LeafAct.of(
                "open url '" + url + "'",
                await -> await.until(Function.identity()).navigate().to(url)
                );
    }

    public static Act mustBe(String url) {
        return LeafAct.of(
                "url must be " + url,
                await -> await.until(ExpectedConditions.urlToBe(url))
        );
    }
}
