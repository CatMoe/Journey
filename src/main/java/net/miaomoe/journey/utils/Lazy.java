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

package net.miaomoe.journey.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import net.miaomoe.journey.functions.ExceptionSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ToString(exclude = {"init"})
public final class Lazy<T> {
    private final ExceptionSupplier<T> init;
    private T value = null;
    @Getter private boolean alreadyLoaded = false;
    @Getter private @Nullable Throwable caused = null;

    public Lazy(@NotNull final ExceptionSupplier<T> init) {
        this.init=Preconditions.checkNotNull(init, "init supplier");
    }

    @SneakyThrows
    public T getValue() {
        if (caused != null) throw caused;
        final T value;
        try {
            value = alreadyLoaded ? this.value : init.get();
            this.value=value;
        } catch (final Throwable e) {
            caused=e;
            throw e;
        }
        alreadyLoaded =true;
        return value;
    }

    public @Nullable T getValueDirectly() {
        return value;
    }
}
