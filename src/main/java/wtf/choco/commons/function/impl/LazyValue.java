package wtf.choco.commons.function.impl;

import com.google.common.base.Preconditions;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Supplier} evaluated when called.
 *
 * @author Parker Hawke - Choco
 *
 * @param <T> the type of value
 */
public final class LazyValue<T> implements Supplier<T> {

    private T value;
    private boolean evaluated = false;

    private final Supplier<@Nullable T> evaluator;

    /**
     * Construct a new {@link LazyValue} to be evaluated by the given supplier
     *
     * @param evaluator the value evaluator
     */
    public LazyValue(@NotNull Supplier<@Nullable T> evaluator) {
        Preconditions.checkArgument(evaluator != null, "evaluator must not be null");
        this.evaluator = evaluator;
    }

    @Override
    public T get() {
        if (!evaluated) {
            this.value = evaluator.get();
            this.evaluated = true;
        }

        return value;
    }

    /**
     * If this value has been evaluated, run the given consumer with the evaluated value.
     *
     * @param consumer the expression to run
     */
    public void ifEvaluated(@NotNull Consumer<@Nullable T> consumer) {
        Preconditions.checkArgument(consumer != null, "consumer must not be null");

        if (!evaluated) {
            return;
        }

        consumer.accept(value);
    }

    /**
     * Invalidate this lazy value and force it to be reevaluated on its next call to
     * {@link #get()}.
     */
    public void invalidate() {
        this.value = null;
        this.evaluated = false;
    }

}
