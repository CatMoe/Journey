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

package net.miaomoe.journey.command.impl;

import lombok.SneakyThrows;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.command.CommandInvocation;
import net.miaomoe.journey.functions.ExceptionConsumer;
import net.miaomoe.journey.functions.ExceptionFunction;
import net.miaomoe.journey.utils.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class FunctionCommand extends AbstractCommand {
    private final @NotNull ExceptionConsumer<@NotNull CommandInvocation> onCommand;
    private final @Nullable ExceptionFunction<@NotNull CommandInvocation, @Nullable List<String>> onTabComplete;

    public FunctionCommand(
            final @NotNull Journey<?> journey,
            final @NotNull ExceptionConsumer<CommandInvocation> onCommand,
            final @Nullable ExceptionFunction<@NotNull CommandInvocation, @Nullable List<String>> onTabComplete
    ) {
        super(journey);
        this.onCommand = Preconditions.checkNotNull(onCommand, "onCommand");
        this.onTabComplete = onTabComplete;
    }

    public FunctionCommand(final @NotNull Journey<?> journey, final @NotNull ExceptionConsumer<CommandInvocation> onCommand) {
        this(journey, onCommand, null);
    }

    @Override @SneakyThrows public void onCommand(@NotNull CommandInvocation invocation) {
        onCommand.accept(invocation);
    }

    @Override @SneakyThrows @Nullable public List<String> onTabComplete(@NotNull CommandInvocation invocation) {
        return onTabComplete == null ? null : onTabComplete.apply(invocation);
    }
}
