package io.github.rkeeves.qualityoflife.composite.auxiliary.core;

import lombok.Value;

import java.util.List;
import java.util.function.BiConsumer;

@Value(staticConstructor = "of")
public class Tree<T> {

    T val;

    List<Tree<T>> children;

    public void forEach(int level, BiConsumer<Integer, T> visitor) {
        visitor.accept(level, this.val);
        children.forEach(ch -> ch.forEach(level + 1, visitor));
    }
}
