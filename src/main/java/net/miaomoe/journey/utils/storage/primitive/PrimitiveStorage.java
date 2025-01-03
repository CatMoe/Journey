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

package net.miaomoe.journey.utils.storage.primitive;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.miaomoe.journey.annotation.Description;
import net.miaomoe.journey.utils.storage.AbstractStorage;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PrimitiveStorage<I extends PrimitiveStorage<?, B, O>, B, O> extends AbstractStorage<B, O> {
    protected boolean exist;
    private final @NotNull Function<I, B> boxedGetter;
    private final @NotNull BiConsumer<I, B> boxedSetter;

    @Deprecated
    @Description(description = "Not recommend get boxed with known PrimitiveStorage.")
    @SuppressWarnings("unchecked")
    @Override public final B get() {
        return exist ? boxedGetter.apply((I) this) : null;
    }

    @Deprecated
    @Description(description = "Not recommend to set boxed with known PrimitiveStorage")
    @SuppressWarnings("unchecked")
    @Override public final void set(B obj) {
        if (obj == null) {
            setNull();
        } else {
            boxedSetter.accept((I) this, obj);
        }
    }

    public void setNull() {
        if (exist) {
            exist = false;
            reset();
        }
    }

    @Override
    public final boolean isPreset() {
        return exist;
    }

    abstract void reset();

    @Override
    @SuppressWarnings("unchecked")
    public final String toString() {
        return this.getClass().getSimpleName() + "[preset=" + exist + ", value=" + boxedGetter.apply((I) this) + "]";
    }
}
