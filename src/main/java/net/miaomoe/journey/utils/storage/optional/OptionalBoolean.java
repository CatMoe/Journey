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

package net.miaomoe.journey.utils.storage.optional;

import net.miaomoe.journey.functions.consumer.BooleanConsumer;
import net.miaomoe.journey.utils.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class OptionalBoolean {
    private static final OptionalBoolean EMPTY = new OptionalBoolean();

    private final boolean isPresent, value;

    private OptionalBoolean() {
        this.isPresent = false;
        this.value = false;
    }

    private OptionalBoolean(final boolean value) {
        this.isPresent = true;
        this.value = value;
    }

    public static OptionalBoolean empty() { return EMPTY; }

    public static OptionalBoolean of(boolean value) { return new OptionalBoolean(value); }

    public static OptionalBoolean ofBoxed(final Boolean value) {
        return value == null ? empty() : of(value);
    }

    public boolean getAsBoolean() {
        if (!isPresent) throw new NoSuchElementException("No value present");
        return value;
    }

    public boolean isPresent() { return isPresent; }

    public void ifPresent(final @NotNull BooleanConsumer consumer) {
        if (isPresent) Preconditions.checkNotNull(consumer, "consumer").accept(value);
    }

    public boolean orElse(boolean other) { return isPresent ? value : other; }

    public boolean orElseGet(BooleanSupplier other) {
        return isPresent ? value : other.getAsBoolean();
    }

    public <X extends Throwable> boolean orElseThrow(@NotNull Supplier<X> exceptionSupplier) throws X {
        if (isPresent) return value; else throw Preconditions.checkNotNull(exceptionSupplier, "exceptionSupplier").get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OptionalBoolean)) return false;
        OptionalBoolean other = (OptionalBoolean) obj;
        return (isPresent && other.isPresent) ? value == other.value : isPresent == other.isPresent;
    }

    @Override
    public int hashCode() {
        return isPresent ? Boolean.hashCode(value) : 0;
    }

    @Override
    public String toString() {
        return isPresent ? String.format("OptionalBoolean[%s]", value) : "OptionalBoolean.empty";
    }
}
