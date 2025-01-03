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

package net.miaomoe.journey.utils.storage.primitive;

import org.jetbrains.annotations.NotNull;

import java.util.OptionalDouble;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class DoubleStorage extends PrimitiveStorage<DoubleStorage, Double, OptionalDouble> {
    private static final @NotNull Function<DoubleStorage, Double> BOXED_GETTER = DoubleStorage::getPrimitive;
    private static final @NotNull BiConsumer<DoubleStorage, Double> BOXED_SETTER = DoubleStorage::setPrimitive;

    private double value;

    public DoubleStorage() {
        super(BOXED_GETTER, BOXED_SETTER);
        exist = false;
    }

    public DoubleStorage(final double value) {
        this();
        setPrimitive(value);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public DoubleStorage(final @NotNull OptionalDouble optional) {
        this();
        optional.ifPresent(this::setPrimitive);
    }

    public double getPrimitive() {
        return value;
    }

    public void setPrimitive(final double obj) {
        exist = true;
        this.value = obj;
    }

    @Override
    public @NotNull OptionalDouble getOptional() {
        return exist ? OptionalDouble.of(value) : OptionalDouble.empty();
    }

    @Override
    void reset() {
        this.value = 0D;
    }
}
