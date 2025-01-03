/*
 * Copyright (C) 2024-2025. CatMoe / Journey Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.miaomoe.journey.utils;

import lombok.*;
import net.miaomoe.journey.functions.exceptionally.ExceptionFunction;
import net.miaomoe.journey.functions.exceptionally.ExceptionSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
@ToString
@SuppressWarnings("unused")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThrowablePair<T extends Throwable, R> {
    private final T throwable;
    private final R result;

    public @NotNull Pair<T, R> asPair() { return new Pair<>(throwable, result); }
    public @NotNull Pair<R, T> asPairReverse() { return new Pair<>(result, throwable); }

    public @NotNull Optional<T> getOptionalThrowable() { return Optional.ofNullable(throwable); }
    public @NotNull Optional<R> getOptionalResult() { return Optional.ofNullable(result); }

    public boolean hasThrowable() { return this.throwable != null; }
    public boolean hasResult() { return this.result != null; }

    @SneakyThrows
    public R getResultOrThrow() {
        if (throwable != null) throw throwable;
        return result;
    }

    public static <R> @NotNull ThrowablePair<Throwable, R> ofGeneric(final R value) {
        return of(value);
    }

    public static <T extends Throwable, R> @NotNull ThrowablePair<T, R> of(final R value) {
        return new ThrowablePair<>(null, value);
    }

    @Deprecated
    public static <T extends Throwable> @NotNull ThrowablePair<T, Void> ofGeneric(final T throwable) {
        return of(throwable);
    }

    public static <T extends Throwable, R> @NotNull ThrowablePair<T, R> of(final T throwable) {
        return new ThrowablePair<>(throwable, null);
    }

    public static <R> @NotNull ThrowablePair<Throwable, R> apply(final @NotNull ExceptionSupplier<R> supplier) {
        try {
            return of(Preconditions.checkNotNull(supplier, "supplier").get());
        } catch (final Throwable throwable) {
            return of(throwable);
        }
    }

    public static <T, R> @NotNull ThrowablePair<Throwable, R> apply(final @NotNull ExceptionFunction<T, R> function, final T arg) {
        try {
            return of(Preconditions.checkNotNull(function, "function").apply(arg));
        } catch (final Throwable throwable) {
            return of(throwable);
        }
    }
}
