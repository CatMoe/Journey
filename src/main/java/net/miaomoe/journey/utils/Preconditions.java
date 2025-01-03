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

package net.miaomoe.journey.utils;


import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class Preconditions {

    public static <T> @NotNull T checkNotNull(final @Nullable T reference, final @Nullable String fieldName) {
        return checkNotNull(reference, fieldName, NullPointerException::new);
    }

    @SneakyThrows
    public static <T> @NotNull T checkNotNull(
            final @Nullable T reference,
            final @Nullable String fieldName,
            final @NotNull Function<@Nullable String, @NotNull Exception> exceptionSupplier
    ) {
        if (reference == null) throw exceptionSupplier.apply(fieldName == null ? null : fieldName + " cannot be null!");
        return reference;
    }

    public static void checkArgument(final boolean expression, final @Nullable String message) {
        if (!expression) throw new IllegalArgumentException(message);
    }

    public static void checkInvoker(
            final @NotNull Class<?> self,
            final @NotNull Class<?> exceptedInvokerClass,
            final @Nullable String exceptedMethodName
    ) throws IllegalAccessException {
        try { throw new Exception(); } catch (final Exception exception) {
            boolean verified = false;
            final String selfName = checkNotNull(self, "self").getName();
            for (final StackTraceElement element : exception.getStackTrace()) {
                if (element.getClassName().equals(selfName) || element.isNativeMethod()) continue;
                if (element.getClassName().equals(exceptedInvokerClass.getName())) {
                    if (
                            exceptedMethodName != null
                                    && !exceptedMethodName.isEmpty()
                                    && !element.getMethodName().equals(exceptedMethodName)
                    ) continue;
                    verified = true;
                    break;
                }
            }
            if (!verified) throw new IllegalAccessException(
                    "Cannot access without "
                            + exceptedInvokerClass.getSimpleName()
                            + (exceptedMethodName == null ? " class!" : "#" + exceptedMethodName + " method!")
            );
        }
    }

}
