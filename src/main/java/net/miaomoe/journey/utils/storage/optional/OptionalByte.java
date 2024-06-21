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

import net.miaomoe.journey.functions.consumer.ByteConsumer;
import net.miaomoe.journey.functions.supplier.FloatSupplier;
import net.miaomoe.journey.utils.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class OptionalByte {
    private static final OptionalByte EMPTY = new OptionalByte();

    private final boolean isPresent;
    private final byte value;

    private OptionalByte() {
        this.isPresent = false;
        this.value = 0;
    }

    private OptionalByte(final byte value) {
        this.isPresent = true;
        this.value = value;
    }

    public static OptionalByte empty() { return EMPTY; }

    public static OptionalByte of(byte value) { return new OptionalByte(value); }

    public static OptionalByte ofBoxed(final Byte value) {
        return value == null ? empty() : of(value);
    }

    public byte getAsByte() {
        if (!isPresent) throw new NoSuchElementException("No value present");
        return value;
    }

    public boolean isPresent() { return isPresent; }

    public void ifPresent(final @NotNull ByteConsumer consumer) {
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
        if (!(obj instanceof OptionalByte)) return false;
        OptionalByte other = (OptionalByte) obj;
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
