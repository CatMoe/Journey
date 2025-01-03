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

package net.miaomoe.journey.utils.storage.optional;

import net.miaomoe.journey.functions.consumer.FloatConsumer;
import net.miaomoe.journey.functions.supplier.FloatSupplier;
import net.miaomoe.journey.utils.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class OptionalFloat {
    private static final OptionalFloat EMPTY = new OptionalFloat();

    private final boolean isPresent;
    private final float value;

    private OptionalFloat() {
        this.isPresent = false;
        this.value = 0f;
    }

    private OptionalFloat(final float value) {
        this.isPresent = true;
        this.value = value;
    }

    public static OptionalFloat empty() { return EMPTY; }

    public static OptionalFloat of(float value) { return new OptionalFloat(value); }

    public static OptionalFloat ofBoxed(final Float value) {
        return value == null ? empty() : of(value);
    }

    public float getAsFloat() {
        if (!isPresent) throw new NoSuchElementException("No value present");
        return value;
    }

    public boolean isPresent() { return isPresent; }

    public void ifPresent(final @NotNull FloatConsumer consumer) {
        if (isPresent) Preconditions.checkNotNull(consumer, "consumer").accept(value);
    }

    public float orElse(float other) { return isPresent ? value : other; }

    public float orElseGet(FloatSupplier other) {
        return isPresent ? value : other.getAsFloat();
    }

    public <X extends Throwable> float orElseThrow(@NotNull Supplier<X> exceptionSupplier) throws X {
        if (isPresent) return value; else throw Preconditions.checkNotNull(exceptionSupplier, "exceptionSupplier").get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OptionalFloat)) return false;
        OptionalFloat other = (OptionalFloat) obj;
        return (isPresent && other.isPresent) ? value == other.value : isPresent == other.isPresent;
    }

    @Override
    public int hashCode() {
        return isPresent ? Float.hashCode(value) : 0;
    }

    @Override
    public String toString() {
        return isPresent ? String.format("OptionalFloat[%s]", value) : "OptionalFloat.empty";
    }
}
