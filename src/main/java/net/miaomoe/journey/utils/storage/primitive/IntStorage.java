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

package net.miaomoe.journey.utils.storage.primitive;

import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class IntStorage extends PrimitiveStorage<IntStorage, Integer, OptionalInt> {
    private static final @NotNull Function<IntStorage, Integer> BOXED_GETTER = IntStorage::getPrimitive;
    private static final @NotNull BiConsumer<IntStorage, Integer> BOXED_SETTER = IntStorage::setPrimitive;

    private int value;

    public IntStorage() {
        super(BOXED_GETTER, BOXED_SETTER);
        exist = false;
    }

    public IntStorage(final int value) {
        this();
        setPrimitive(value);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public IntStorage(final @NotNull OptionalInt optional) {
        this();
        optional.ifPresent(this::setPrimitive);
    }

    public int getPrimitive() {
        return value;
    }

    public void setPrimitive(final int obj) {
        exist = true;
        this.value = obj;
    }

    @Override
    void reset() {
        value = 0;
    }

    @Override
    public @NotNull OptionalInt getOptional() {
        return exist ? OptionalInt.of(value) : OptionalInt.empty();
    }
}
