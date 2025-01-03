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

import net.miaomoe.journey.utils.storage.optional.OptionalByte;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class ByteStorage extends PrimitiveStorage<ByteStorage, Byte, OptionalByte> {
    private static final @NotNull Function<ByteStorage, Byte> BOXED_GETTER = ByteStorage::getPrimitive;
    private static final @NotNull BiConsumer<ByteStorage, Byte> BOXED_SETTER = ByteStorage::setPrimitive;

    public ByteStorage() {
        super(BOXED_GETTER, BOXED_SETTER);
        exist = false;
    }

    public ByteStorage(final byte value) {
        this();
        setPrimitive(value);
    }

    public ByteStorage(final @NotNull OptionalByte optional) {
        this();
        optional.ifPresent(this::setPrimitive);
    }

    private byte value;

    public byte getPrimitive() {
        return value;
    }

    public void setPrimitive(byte value) {
        exist = true;
        this.value = value;
    }

    @Override
    void reset() {
        value = 0;
    }

    @Override
    public @NotNull OptionalByte getOptional() {
        return exist ? OptionalByte.of(value) : OptionalByte.empty();
    }
}
