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

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@Value
@SuppressWarnings("unused")
public class Pair<A, B> {
    A a;
    B b;

    public Optional<A> getOptionalA() { return Optional.ofNullable(a); }
    public Optional<B> getOptionalB() { return Optional.ofNullable(b); }

    public void apply(final @NotNull BiConsumer<A, B> consumer) {
        checkNotNull(consumer, "consumer").accept(a, b);
    }

    public <R> R let(final @NotNull BiFunction<A, B, R> function) {
        return checkNotNull(function, "function").apply(a, b);
    }

    public @NotNull Pair<A, B> copy() {
        return new Pair<>(a, b);
    }

    public @NotNull Supplier<A> getSupplierA() { return () -> a; }
    public @NotNull Supplier<B> getSupplierB() { return () -> b; }

    public static <A, B> Pair<A, B> of(final A a, final B b) {
        return new Pair<>(a, b);
    }

    public static <A, B, T> @NotNull Set<Pair<A, B>> toPairSet(
            final @NotNull Collection<T> list,
            final @NotNull Function<T, A> aProvider,
            final @NotNull Function<T, B> bProvider
    ) {
        final HashSet<Pair<A, B>> pairs = new HashSet<>();
        for (final T t : list) { pairs.add(Pair.of(aProvider.apply(t), bProvider.apply(t))); }
        return pairs;
    }

    public static <K, V> @NotNull Map<K, V> toMap(final Collection<Pair<K, V>> pairs) {
        return toMap(pairs, HashMap::new);
    }

    public static <K, V, M extends Map<K, V>> @NotNull M toMap(final Collection<Pair<K, V>> pairs, final @NotNull Supplier<M> supplier) {
        final M map = checkNotNull(checkNotNull(supplier, "supplier").get(), "map");
        for (final Pair<K, V> pair : checkNotNull(pairs, "pairs")) map.put(pair.a, pair.b);
        return map;
    }

    public static <K, V> @NotNull Map<K, V> toMapReversed(final @NotNull Collection<Pair<V, K>> pairs) {
        return toMapReversed(pairs, HashMap::new);
    }

    public static <K, V, M extends Map<K, V>> @NotNull Map<K, V> toMapReversed(final @NotNull Collection<Pair<V, K>> pairs, final @NotNull Supplier<M> supplier) {
        final M map = checkNotNull(checkNotNull(supplier, "supplier").get(), "map");
        for (final Pair<V, K> pair : checkNotNull(pairs, "pairs")) map.put(pair.b, pair.a);
        return map;
    }

    public static <K, V> @NotNull Set<Pair<K, V>> fromMap(final @NotNull Map<K, V> map) {
        return toPairSet(map.entrySet(), Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <K, V> @NotNull Set<Pair<V, K>> fromMapReversed(final @NotNull Map<K, V> map) {
        return toPairSet(map.entrySet(), Map.Entry::getValue, Map.Entry::getKey);
    }
}
