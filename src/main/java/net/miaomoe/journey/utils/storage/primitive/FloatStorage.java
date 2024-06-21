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

import net.miaomoe.journey.utils.storage.optional.OptionalFloat;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class FloatStorage extends PrimitiveStorage<FloatStorage, Float, OptionalFloat> {
    private static final @NotNull Function<FloatStorage, Float> BOXED_GETTER = FloatStorage::getPrimitive;
    private static final @NotNull BiConsumer<FloatStorage, Float> BOXED_SETTER = FloatStorage::setPrimitive;

    public FloatStorage() {
        super(BOXED_GETTER, BOXED_SETTER);
        exist = false;
    }

    public FloatStorage(float value) {
        this();
        setPrimitive(value);
    }

    public FloatStorage(final @NotNull OptionalFloat optional) {
        this();
        optional.ifPresent(this::setPrimitive);
    }

    private float value;

    public float getPrimitive() {
        return value;
    }

    public void setPrimitive(final float value) {
        this.value = value;
    }

    @Override
    void reset() {
        this.value = 0f;
    }

    @Override
    public @NotNull OptionalFloat getOptional() {
        return exist ? OptionalFloat.of(value) : OptionalFloat.empty();
    }
}
