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

import java.util.OptionalLong;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class LongStorage extends PrimitiveStorage<LongStorage, Long, OptionalLong> {
    private static final @NotNull Function<LongStorage, Long> BOXED_GETTER = LongStorage::getPrimitive;
    private static final @NotNull BiConsumer<LongStorage, Long> BOXED_SETTER = LongStorage::setPrimitive;

    private boolean exist;
    private long value = 0L;

    public LongStorage() {
        super(BOXED_GETTER, BOXED_SETTER);
        exist = false;
    }

    public LongStorage(final long value) {
        this();
        setPrimitive(value);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public LongStorage(final @NotNull OptionalLong optional) {
        this();
        optional.ifPresent(this::setPrimitive);
    }

    public long getPrimitive() {
        return value;
    }

    public void setPrimitive(final long obj) {
        exist = true;
        this.value = obj;
    }

    @Override
    public @NotNull OptionalLong getOptional() {
        return exist ? OptionalLong.of(value) : OptionalLong.empty();
    }

    @Override
    void reset() {
        value = 0L;
    }
}
