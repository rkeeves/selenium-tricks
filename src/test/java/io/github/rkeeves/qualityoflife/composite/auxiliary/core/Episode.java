package io.github.rkeeves.qualityoflife.composite.auxiliary.core;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class Episode {

    // or you could do it iteratively...
    public static Tree<ActResult> play(WebDriverWait await, Act act) {
        if (act instanceof CompositeAct) {
            final var compositeAct = (CompositeAct) act;
            boolean passed = true;
            List<Tree<ActResult>> childResults = new ArrayList<>();
            for (var childAct : compositeAct.getChildActs()) {
                final var childResult = play(await, childAct);
                childResults.add(childResult);
                if (!childResult.getVal().isPassed()) {
                    passed = false;
                    break;
                }
            };
            return Tree.of(ActResult.of(act, passed, null), childResults);
        } else if (act instanceof LeafAct) {
            final var leafAct = (LeafAct) act;
            try {
                leafAct.getEffect().accept(await);
                return Tree.of(ActResult.of(act, true, null), Collections.emptyList());
            } catch (Throwable t) {
                return Tree.of(ActResult.of(act, false, t), Collections.emptyList());
            }
        } else {
            return Tree.of(ActResult.of(act, false, null), Collections.emptyList());
        }
    }
}
