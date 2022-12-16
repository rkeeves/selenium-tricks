package io.github.rkeeves.qualityoflife.composite.auxiliary.core;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@Value(staticConstructor = "of")
public class LeafAct extends Act {

    String nickname;

    Consumer<WebDriverWait> effect;
}
