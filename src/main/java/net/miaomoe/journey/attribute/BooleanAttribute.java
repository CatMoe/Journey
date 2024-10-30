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
import net.miaomoe.journey.utils.storage.primitive.BooleanStorage;
import org.jetbrains.annotations.NotNull;

public interface BooleanAttribute<T extends BooleanAttribute<T>> extends Attribute<BooleanStorage, T> {
    @Override
    default BooleanStorage tryCastValue(final @NotNull Object o) throws ClassCastException {
        if (o instanceof Boolean)
            return new BooleanStorage((Boolean) o);
        else if (o instanceof BooleanStorage)
            return (BooleanStorage) o;
        throw new ClassCastException("Cannot cast " + o + " to BooleanStorage");
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class BooleanAttributeImpl implements BooleanAttribute<BooleanAttributeImpl> {}

    static BooleanAttributeImpl create() {
        return new BooleanAttributeImpl();
    }
}
