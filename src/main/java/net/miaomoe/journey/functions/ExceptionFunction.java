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

package net.miaomoe.journey.functions;

import lombok.SneakyThrows;
import net.miaomoe.journey.utils.Preconditions;

import java.util.function.Function;

@FunctionalInterface
public interface ExceptionFunction<T, R> {
    R apply(T t) throws Throwable;
    default Function<T, R> asFunction() {
        return arg -> get(this, arg);
    }

    @SneakyThrows
    static <T, R> R get(final ExceptionFunction<T, R> supplier, final T arg) {
        return Preconditions.checkNotNull(supplier, "supplier").apply(arg);
    }
}
