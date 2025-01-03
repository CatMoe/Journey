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

package net.miaomoe.journey.utils.storage;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

@ToString
@AllArgsConstructor
@NoArgsConstructor
public final class ObjectStorage<T> extends AbstractStorage<T, Optional<T>> {
    private T obj;

    @Override
    public void set(T obj) {
        this.obj = obj;
    }

    @Override
    public T get() {
        return obj;
    }

    @Override
    public boolean isEmpty() {
        if (isPreset()) {
            if (obj instanceof String) {
                return ((String) obj).isEmpty();
            } else if (obj instanceof Collection<?>) {
                return ((Collection<?>) obj).isEmpty();
            } else if (obj instanceof Object[]) {
                return ((Object[]) obj).length == 0;
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isPreset() {
        return obj != null;
    }

    @Override
    public @NotNull Optional<T> getOptional() {
        return Optional.ofNullable(obj);
    }
}
