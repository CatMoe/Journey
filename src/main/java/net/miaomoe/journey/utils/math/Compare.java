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

package net.miaomoe.journey.utils.math;

import net.miaomoe.journey.annotation.Description;

@SuppressWarnings("unused")
@Description(description = {
        "Interface to help compare number.",
        "Example: Compare.GREATER_THAN_EQUAL is A >= B",
        "A is a number to compare. B is number to compare than A."
})
public interface Compare {
    boolean compare(final int a, final int b);
    boolean compare(final long a, final long b);
    boolean compare(final double a, final double b);
    boolean compare(final float a, final float b);
    boolean compare(final byte a, final byte b);

    Compare EQUAL = new Compare() {
        @Override public boolean compare(final int a, final int b) { return a == b; }
        @Override public boolean compare(final long a, final long b) { return a == b; }
        @Override public boolean compare(final double a, final double b) { return a == b; }
        @Override public boolean compare(final float a, final float b) { return a == b; }
        @Override public boolean compare(final byte a, final byte b) { return a == b; }
    };
    Compare NOT_EQUAL = new Compare() {
        @Override public boolean compare(final int a, final int b) { return a != b; }
        @Override public boolean compare(final long a, final long b) { return a != b; }
        @Override public boolean compare(final double a, final double b) { return a != b; }
        @Override public boolean compare(final float a, final float b) { return a != b; }
        @Override public boolean compare(final byte a, final byte b) { return a != b; }
    };
    Compare LESS_THAN = new Compare() {
        @Override public boolean compare(final int a, final int b) { return a < b; }
        @Override public boolean compare(final long a, final long b) { return a < b; }
        @Override public boolean compare(final double a, final double b) { return a < b; }
        @Override public boolean compare(final float a, final float b) { return a < b; }
        @Override public boolean compare(final byte a, final byte b) { return a < b; }
    };
    Compare LESS_THAN_EQUAL = new Compare() {
        @Override public boolean compare(final int a, final int b) { return a <= b; }
        @Override public boolean compare(final long a, final long b) { return a <= b; }
        @Override public boolean compare(final double a, final double b) { return a <= b; }
        @Override public boolean compare(final float a, final float b) { return a <= b; }
        @Override public boolean compare(final byte a, final byte b) { return a <= b; }
    };
    Compare GREATER_THAN = new Compare() {
        @Override public boolean compare(final int a, final int b) { return a > b; }
        @Override public boolean compare(final long a, final long b) { return a > b; }
        @Override public boolean compare(final double a, final double b) { return a > b; }
        @Override public boolean compare(final float a, final float b) { return a > b; }
        @Override public boolean compare(final byte a, final byte b) { return a > b; }
    };
    Compare GREATER_THAN_EQUAL = new Compare() {
        @Override public boolean compare(final int a, final int b) { return a >= b; }
        @Override public boolean compare(final long a, final long b) { return a >= b; }
        @Override public boolean compare(final double a, final double b) { return a >= b; }
        @Override public boolean compare(final float a, final float b) { return a >= b; }
        @Override public boolean compare(final byte a, final byte b) { return a >= b; }
    };

    // Proxy interfaces as enum
    enum Enum implements Compare {
        EQUAL(Compare.EQUAL),
        NOT_EQUAL(Compare.NOT_EQUAL),
        LESS_THAN(Compare.LESS_THAN),
        LESS_THAN_EQUAL(Compare.LESS_THAN_EQUAL),
        GREATER_THAN(Compare.GREATER_THAN),
        GREATER_THAN_EQUAL(Compare.GREATER_THAN_EQUAL),;

        private final Compare compare;
        Enum(final Compare compare) {
            this.compare = compare;
        }

        @Override public boolean compare(int a, int b) { return compare.compare(a, b); }
        @Override public boolean compare(long a, long b) { return compare.compare(a, b); }
        @Override public boolean compare(double a, double b) { return compare.compare(a, b); }
        @Override public boolean compare(float a, float b) { return compare.compare(a, b); }
        @Override public boolean compare(byte a, byte b) { return compare.compare(a, b); }
    }
}
