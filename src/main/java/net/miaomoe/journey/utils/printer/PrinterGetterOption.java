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

package net.miaomoe.journey.utils.printer;

import lombok.SneakyThrows;
import net.miaomoe.journey.utils.Pair;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@FunctionalInterface
public interface PrinterGetterOption {
    Predicate<Field> FIELD_FILTER = field -> {
        final int modifier = field.getModifiers();
        return !Modifier.isStatic(modifier) && !Modifier.isTransient(modifier);
    };
    Function<Field, Pair<String, MethodHandle>> FIELD_PAIR_FUNCTION = field -> Pair.of(field.getName(), toMethodHandle(field));

    @NotNull Map<String, MethodHandle> get(final @NotNull Class<?> targetClass) throws Exception;

    PrinterGetterOption FIELD = targetClass -> Pair.toMap(
            Arrays.stream(targetClass.getFields()).filter(FIELD_FILTER).map(FIELD_PAIR_FUNCTION).collect(Collectors.toSet())
    );

    PrinterGetterOption DECLARED_FIELD = targetClass -> {
        final Field[] fields = Arrays.stream(targetClass.getDeclaredFields()).filter(FIELD_FILTER).toArray(Field[]::new);
        for (final Field field : fields) {
            field.setAccessible(true);
        }
        return Pair.toMap(Arrays.stream(fields).map(FIELD_PAIR_FUNCTION).collect(Collectors.toSet()));
    };

    PrinterGetterOption METHOD_GETTER = targetClass -> {
        final Map<String, MethodHandle> map = new HashMap<>();
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (final Method method : targetClass.getDeclaredMethods()) {
            if (method.getParameterCount() != 0 || method.getReturnType() == void.class) continue;
            boolean isGetter = method.getName().startsWith("get");
            String name = isGetter ? method.getName().substring(3) : method.getName();
            if (!isGetter && method.getReturnType() == boolean.class && method.getName().startsWith("is")) {
                isGetter = true;
                name = name.substring(2);
            }
            if (!isGetter) continue;
            if (name.length() >= 2 && Character.isUpperCase(name.charAt(0)) && Character.isLowerCase(name.charAt(1))) {
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            }
            map.put(name, lookup.unreflect(method));
        }
        return map;
    };

    @SneakyThrows
    static @NotNull MethodHandle toMethodHandle(final @NotNull Field field) {
        return MethodHandles.lookup().unreflectGetter(field);
    }
}
