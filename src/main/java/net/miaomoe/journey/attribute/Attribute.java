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

package net.miaomoe.journey.attribute;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.miaomoe.journey.utils.Preconditions;
import net.miaomoe.journey.utils.storage.primitive.BooleanStorage;
import net.miaomoe.journey.utils.storage.primitive.IntStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
@SuppressWarnings("unused")
public interface Attribute<T, O extends Attribute<T, O>> {

    @Deprecated
    default O $null() {
        return null;
    }

    T tryCastValue(final @NotNull Object o) throws ClassCastException;

    interface Result<T, A extends Attribute<T, ?>> extends Supplier<T> {
        Class<? extends A> getAttributeClass();
        T getAttributeValue();

        @Override
        default T get() {
            return getAttributeValue();
        }
    }

    interface AttributeMap {
        <T> void setAttribute(final @NotNull Attribute<T, ?> attribute, final @Nullable T value);

        <T, A extends Attribute<T, A>> @NotNull Result<@Nullable T, A> getAttribute(final @NotNull A attribute);
        default <T, A extends Attribute<T, A>> @NotNull Optional<T> getOptionalAttribute(final @NotNull A attribute) {
            return Optional.ofNullable(getAttribute(attribute).getAttributeValue());
        }

        default <A extends BooleanAttribute<A>> boolean getBooleanAttribute(final @NotNull A attribute, final boolean defaultValue) {
            return getOptionalAttribute(attribute).orElseGet(() -> new BooleanStorage(defaultValue)).getPrimitive();
        }

        default void setBooleanAttribute(final @NotNull BooleanAttribute<?> attribute, final boolean value) {
            setAttribute(attribute, new BooleanStorage(value));
        }

        default <A extends IntAttribute<A>> int getIntAttribute(final @NotNull A attribute, final int defaultValue) {
            return getOptionalAttribute(attribute).orElseGet(() -> new IntStorage(defaultValue)).getPrimitive();
        }

        default void setIntAttribute(final @NotNull IntAttribute<?> attribute, final int value) {
            setAttribute(attribute, new IntStorage(value));
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class DefaultAttributeImpl implements AttributeMap {
        private static final @NotNull AttributeMap EMPTY = new DefaultAttributeImpl(Collections.emptyMap(), true, false);

        private final @NotNull Map<Attribute<?, ?>, Object> map;
        private final boolean unmodifiable, synchronize;

        private <T> T accessMap(final @NotNull Function<Map<Attribute<?, ?>, Object>, T> function) {
            if (synchronize) {
                synchronized (map) {
                    return function.apply(map);
                }
            } else {
                return function.apply(map);
            }
        }

        @Override
        public <T> void setAttribute(final @NotNull Attribute<T, ?> attribute, final @Nullable T value) {
            if (unmodifiable) {
                throw new UnsupportedOperationException("Unmodifiable");
            } else if (value == null) {
                accessMap(map -> map.remove(attribute));
            } else {
                accessMap(map -> map.put(attribute, value));
            }
        }

        @Override
        public <T, A extends Attribute<T, A>> @NotNull Result<T, A> getAttribute(@NotNull A attribute) {
            Object v = accessMap(map -> map.get(attribute));
            return createResult(attribute, () -> v == null ? null : attribute.tryCastValue(v));
        }

        private static  <T, O extends Attribute<T, O>> Result<T, O> createResult(final @NotNull O attribute, final @NotNull Supplier<T> supplier) {
            return new Result<T, O>() {
                @Override
                @SuppressWarnings("unchecked")
                public Class<? extends O> getAttributeClass() {
                    return (Class<? extends O>) attribute.getClass();
                }

                @Override
                public T getAttributeValue() {
                    return supplier.get();
                }
            };
        }
    }

    interface ProxiedAttributeMap extends AttributeMap {
        @NotNull AttributeMap getAttributeMap();

        @Override
        default @NotNull <T, A extends Attribute<T, A>> Result<@Nullable T, A> getAttribute(@NotNull final A attribute) {
            return Preconditions.checkNotNull(getAttributeMap(), "attributeMap").getAttribute(attribute);
        }

        @Override
        default <T> void setAttribute(final @NotNull Attribute<T, ?> attribute, @Nullable final T value) {
            Preconditions.checkNotNull(getAttributeMap(), "attributeMap").setAttribute(attribute, value);
        }
    }

    static AttributeMap emptyMap() {
        return DefaultAttributeImpl.EMPTY;
    }

    static AttributeMap createSynchronizeMap() {
        return new DefaultAttributeImpl(new HashMap<>(), true, true);
    }

}
