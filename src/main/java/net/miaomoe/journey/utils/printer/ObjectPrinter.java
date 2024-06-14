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

package net.miaomoe.journey.utils.printer;

import lombok.*;
import lombok.experimental.Accessors;
import net.miaomoe.journey.utils.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@SuppressWarnings("unused")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectPrinter<T> {
    private final @NotNull Map<String, MethodHandle> handles;
    private final @NotNull String equal, spilt;
    private final @NotNull Function<String, String> formatter;
    private final @NotNull Set<DeeperPrinter<?>> deeperPrinters;
    private final @NotNull Set<String> ignore;

    public @NotNull String print(@NotNull final T object) {
        checkNotNull(object, "object");
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        for (final Map.Entry<String, MethodHandle> entry : handles.entrySet()) {
            i++;
            if (ignore.contains(entry.getKey())) continue;
            sb.append(entry.getKey()).append(equal);
            Object obj = null;
            try {
                obj = entry.getValue().invoke(object);
            } catch (final Throwable ignored) {
            }
            sb.append(printDeeper(obj).orElse(obj == null ? "null" : obj.toString()));
            if (i < handles.size()) sb.append(spilt);
        }
        return formatter.apply(sb.toString());
    }

    private @NotNull Optional<String> printDeeper(final @Nullable Object object) {
        if (object == null || deeperPrinters.isEmpty()) return Optional.empty();
        for (final DeeperPrinter<?> deeperPrinter : deeperPrinters) {
            final Optional<String> optional = deeperPrinter.toString(object);
            if (optional.isPresent()) return optional;
        }
        if (object instanceof Collection<?>) {
            final Collection<?> collections = (Collection<?>) object;
            final StringBuilder sb = new StringBuilder("[");
            int i = 0;
            for (final Object obj : collections) {
                i++;
                sb.append(printDeeper(obj).orElse(obj == null ? "null" : obj.toString()));
                if (i < collections.size()) sb.append(", ");
            }
            sb.append("]");
            return Optional.of(sb.toString());
        } else if (object.getClass().isArray()) {
            return printDeeper(Arrays.asList((Object[]) object));
        }
        return Optional.empty();
    }

    public static <T> @NotNull Builder<T> builder(final @NotNull Class<T> printClass) {
        return new Builder<>(printClass);
    }

    public static <T> @NotNull ObjectPrinter<T> createSimple(final @NotNull Class<T> printClass, final @NotNull Function<String, String> formatter, final @NotNull String @NotNull... ignore) {
        return ObjectPrinter.builder(printClass).formatter(formatter).addIgnore(ignore).build();
    }

    public static <T> @NotNull ObjectPrinter<T> createSimple(final @NotNull Class<T> printClass, final @NotNull String @NotNull... ignore) {
        return createSimple(printClass, Function.identity(), ignore);
    }

    @Setter
    @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder<T> {
        private final @NotNull Class<T> printClass;
        private @NotNull String spiltString = ", ", equalString = "=";
        private @NotNull PrinterGetterOption getterOption = PrinterGetterOption.METHOD_GETTER;
        private @NotNull Function<String, String> formatter = Function.identity();
        private final @NotNull Set<DeeperPrinter<?>> deeperPrinters = new HashSet<>();
        private final @NotNull Set<String> ignore = new HashSet<>();

        public @NotNull Builder<T> registerPrinters(@NotNull final DeeperPrinter<?>... deeperPrinters) {
            for (final DeeperPrinter<?> deeperPrinter : deeperPrinters) this.deeperPrinters.add(Preconditions.checkNotNull(deeperPrinter, "deeperPrinter"));
            return this;
        }

        public @NotNull Builder<T> registerPrinters(@NotNull final Collection<DeeperPrinter<?>> deeperPrinters) {
            for (final DeeperPrinter<?> deeperPrinter : deeperPrinters) this.deeperPrinters.add(Preconditions.checkNotNull(deeperPrinter, "deeperPrinter"));
            return this;
        }

        public <O> @NotNull Builder<T> registerPrinter(final @NotNull Class<O> printClass) {
            return registerPrinter(printClass, InstanceOption.INSTANCE, ObjectPrinter.createSimple(printClass, it -> printClass.getSimpleName() + "[" + it + "]"));
        }

        public <O> @NotNull Builder<T> registerPrinter(
                final @NotNull Class<O> printClass,
                final @NotNull Function<Class<?>, InstanceOption> instanceOption,
                final @NotNull ObjectPrinter<O> objectPrinter
        ) {
            return registerPrinters(DeeperPrinter.of(printClass, instanceOption, objectPrinter));
        }

        public @NotNull Builder<T> addIgnore(final @NotNull String @NotNull... ignore) {
            return addIgnore(Arrays.asList(ignore));
        }

        public @NotNull Builder<T> addIgnore(final @NotNull Collection<String> ignore) {
            this.ignore.addAll(ignore);
            return this;
        }

        @SneakyThrows
        public @NotNull ObjectPrinter<T> build() {
            return new ObjectPrinter<>(getterOption.get(printClass), equalString, spiltString, formatter, deeperPrinters, ignore);
        }
    }

    @FunctionalInterface
    public interface InstanceOption {
        boolean isInstance(final @NotNull Object object);

        Function<Class<?>, InstanceOption> CLASS_EQUAL = targetClass -> (InstanceOption) object -> targetClass == object.getClass();
        Function<Class<?>, InstanceOption> INSTANCE = targetClass -> (InstanceOption) targetClass::isInstance;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class DeeperPrinter<T> {
        private final @NotNull InstanceOption instanceOption;
        private final @NotNull ObjectPrinter<T> objectPrinter;
        private final @NotNull Map<Class<?>, Boolean> cachedInstances = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        public @NotNull Optional<String> toString(@NotNull final Object object) {
            checkNotNull(object, "object");
            if (!cachedInstances.computeIfAbsent(object.getClass(), i -> instanceOption.isInstance(object))) return Optional.empty();
            return Optional.of(objectPrinter.print((T) object));
        }

        public static <T> @NotNull DeeperPrinter<T> of(
                final @NotNull Class<T> printClass,
                final @NotNull Function<Class<?>, InstanceOption> instanceOption,
                final @NotNull ObjectPrinter<T> objectPrinter
        ) {
            return new DeeperPrinter<>(
                    checkNotNull(instanceOption, "instanceOption").apply(checkNotNull(printClass, "printClass")),
                    checkNotNull(objectPrinter, "objectPrinter")
            );
        }
    }
}
