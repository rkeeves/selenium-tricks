package io.github.rkeeves.qualityoflife.seqwithstate.auxiliary;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.By;

import java.util.function.Function;

@UtilityClass
public class ChainingCustomCommands {

    public static <S> SeqWithState.DriverEffect<S> openPage(final String url) {
        return (d, __) -> d.navigate().to(url);
    }

    public static <S> SeqWithState.DriverFunction<S, String> attrGet(final By locator, final String attributeName) {
        return (d, __) -> d.findElement(locator).getAttribute(attributeName);
    }

    public static <S> SeqWithState.DriverEffect<S> enterThat(final By locator, final Function<S, String> selectStateVariable) {
        return (d, s) -> d.findElement(locator).sendKeys(selectStateVariable.apply(s));
    }
}
