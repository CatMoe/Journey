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

package net.miaomoe.journey.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.miaomoe.journey.utils.storage.primitive.IntStorage;
import org.jetbrains.annotations.NotNull;

public interface IntAttribute<T extends IntAttribute<T>> extends Attribute<IntStorage, T> {
    @Override
    default IntStorage tryCastValue(final @NotNull Object o) throws ClassCastException {
        if (o instanceof Integer)
            return new IntStorage((Integer)o);
        else if (o instanceof IntStorage)
            return (IntStorage)o;
        throw new ClassCastException("Cannot cast " + o + " to IntStorage");
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class IntAttributeImpl implements IntAttribute<IntAttributeImpl> {}

    static IntAttributeImpl create() {
        return new IntAttributeImpl();
    }
}
