package io.github.rkeeves.qualityoflife.seqwithstate.auxiliary;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SeqWithState<S> {

    // these interfaces are only fluff
    @FunctionalInterface
    public interface DriverEffect<S> extends BiConsumer<WebDriver, S> {

    }

    @FunctionalInterface
    public interface DriverFunction<S, T> extends BiFunction<WebDriver, S, T> {

    }

    @FunctionalInterface
    public interface MergeState<S, T, U> extends BiFunction<S, T, U> {

    }

    private final WebDriver webDriver;

    private final S state;

    public static SeqWithState<Void> withVoidState(WebDriver webDriver) {
        return new SeqWithState<>(webDriver, null);
    }

    public static <S> SeqWithState<S> withState(WebDriver webDriver, @NonNull S state) {
        return new SeqWithState<>(webDriver, state);
    }

    public SeqWithState<S> just(DriverEffect<S> effect) {
        effect.accept(webDriver, state);
        return this;
    }

    public <T, X> SeqWithState<X> merge(DriverFunction<S, T> function, MergeState<S, T, X> merge) {
        final var newVariable = function.apply(webDriver, state);
        final var newData = merge.apply(state, newVariable);
        return new SeqWithState<>(webDriver, newData);
    }

    public SeqWithState<S> peek(Consumer<S> effect) {
        effect.accept(state);
        return this;
    }

    public S finish() {
        return state;
    }
}
