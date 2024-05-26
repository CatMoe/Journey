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

package net.miaomoe.journey.command;

import net.miaomoe.journey.command.impl.AbstractCommand;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface CommandRegistrable<T extends AbstractCommand, R> {
    @NotNull R register(final @NotNull T command, final @NotNull String name, final @NotNull String@NotNull... aliases);
    @NotNull R unregister(final @NotNull String name);
    boolean isRegistered(final @NotNull String name);
    boolean isAliases(final @NotNull String name);
    @NotNull String getCommandName(final @NotNull String alias) throws IllegalArgumentException;
}
