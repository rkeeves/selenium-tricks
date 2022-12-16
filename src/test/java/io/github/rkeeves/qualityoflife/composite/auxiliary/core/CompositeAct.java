package io.github.rkeeves.qualityoflife.composite.auxiliary.core;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value(staticConstructor = "of")
public class CompositeAct extends Act {

    String nickname;

    List<Act> childActs;

    public static CompositeAct of(String nickname, Act... acts) {
        return of(nickname, Arrays.asList(acts));
    }
}
