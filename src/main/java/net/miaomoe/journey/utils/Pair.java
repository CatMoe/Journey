/*
 * Copyright (C) 2024-2024. CatMoe / Journey Contributors
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

import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Value
public class Pair<A, B> {
    A a;
    B b;

    public Optional<A> getOptionalA() { return Optional.ofNullable(a); }
    public Optional<B> getOptionalB() { return Optional.ofNullable(b); }

    public void invoke(final @NotNull BiConsumer<A, B> consumer) {
        Preconditions.checkNotNull(consumer, "consumer").accept(a, b);
    }

    public <R> R invoke(final @NotNull BiFunction<A, B, R> function) {
        return Preconditions.checkNotNull(function, "function").apply(a, b);
    }

    public static <A, B> Pair<A, B> of(final A a, final B b) {
        return new Pair<>(a, b);
    }
}
