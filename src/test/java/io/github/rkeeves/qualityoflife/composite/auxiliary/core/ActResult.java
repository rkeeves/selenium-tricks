package io.github.rkeeves.qualityoflife.composite.auxiliary.core;

import lombok.Value;

@Value(staticConstructor = "of")
public class ActResult {

    Act act;

    boolean passed;

    Throwable t;
}
