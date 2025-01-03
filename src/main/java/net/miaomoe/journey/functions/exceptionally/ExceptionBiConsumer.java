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

import java.util.function.BiConsumer;

@FunctionalInterface
@SuppressWarnings("unused")
public interface ExceptionBiConsumer<T, U> {
    void accept(final T arg1, final U arg2) throws Throwable;
    default BiConsumer<T, U> asBiConsumer() {
        return (arg1, arg2) -> accept(arg1, arg2, this);
    }

    @SneakyThrows
    static <T, U> void accept(final T arg1, final U arg2, final ExceptionBiConsumer<T, U> consumer) {
        consumer.accept(arg1, arg2);
    }
}
