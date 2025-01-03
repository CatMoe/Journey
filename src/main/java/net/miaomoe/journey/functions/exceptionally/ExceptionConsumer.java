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

package net.miaomoe.journey.functions.exceptionally;

import lombok.SneakyThrows;
import net.miaomoe.journey.utils.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@FunctionalInterface
@SuppressWarnings("unused")
public interface ExceptionConsumer<T> {
    void accept(final T value) throws Throwable;
    default @NotNull Consumer<T> asConsumer() {
        return it -> ExceptionConsumer.accept(this, it);
    }

    @SneakyThrows
    static <T> void accept(final ExceptionConsumer<T> consumer, final T value) {
        Preconditions.checkNotNull(consumer, "consumer").accept(value);
    }
}
