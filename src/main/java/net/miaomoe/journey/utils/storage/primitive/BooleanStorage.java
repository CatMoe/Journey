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

import net.miaomoe.journey.utils.storage.optional.OptionalBoolean;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class BooleanStorage extends PrimitiveStorage<BooleanStorage, Boolean, OptionalBoolean> {
    private static final @NotNull Function<BooleanStorage, Boolean> BOXED_GETTER = BooleanStorage::getPrimitive;
    private static final @NotNull BiConsumer<BooleanStorage, Boolean> BOXED_SETTER = BooleanStorage::setPrimitive;

    private boolean value;

    public BooleanStorage() {
        super(BOXED_GETTER, BOXED_SETTER);
        exist = false;
    }

    public BooleanStorage(final boolean value) {
        this();
        setPrimitive(value);
    }

    public BooleanStorage(final @NotNull OptionalBoolean optional) {
        this();
        //noinspection AssignmentUsedAsCondition
        if (exist = optional.isPresent()) {
            value = optional.getAsBoolean();
        }
    }

    public boolean getPrimitive() {
        return value;
    }

    public void setPrimitive(final boolean value) {
        exist = true;
        this.value = value;
    }

    @Override
    void reset() {
        value = false;
    }

    @Override
    public @NotNull OptionalBoolean getOptional() {
        return exist ? OptionalBoolean.of(value) : OptionalBoolean.empty();
    }
}
