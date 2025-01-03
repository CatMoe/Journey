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

package net.miaomoe.journey.command.stream;

import lombok.SneakyThrows;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.adventure.PresetsSerializer;
import net.miaomoe.journey.command.CommandInvocation;
import net.miaomoe.journey.command.impl.AbstractCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class StreamCommand extends AbstractCommand {
    private final @NotNull Consumer<CommandStream> onCommand;
    private final @Nullable Consumer<TabCompleteStream> onTabComplete;

    public StreamCommand(
            final @NotNull Journey<?> journey,
            final @NotNull Consumer<CommandStream> onCommand,
            final @Nullable Consumer<TabCompleteStream> onTabComplete
    ) {
        super(journey);
        this.onCommand = onCommand;
        this.onTabComplete = onTabComplete;
    }

    public StreamCommand(final @NotNull Journey<?> journey, final @NotNull Consumer<CommandStream> onCommand) {
        this(journey, onCommand, null);
    }

    @Override
    @SneakyThrows
    public void onCommand(@NotNull CommandInvocation invocation) {
        invoke(CommandStream::new, onCommand, invocation).ifPresent(it -> invocation.getSender().sendMessage(PresetsSerializer.miniMessage, it));
    }

    @SneakyThrows
    private <T extends AbstractCommandStream<?>> @NotNull Optional<List<String>> invoke(
            final Function<@NotNull CommandInvocation, T> create,
            final Consumer<T> consumer,
            final @NotNull CommandInvocation invocation
    ) {
        try {
            consumer.accept(create.apply(invocation));
        } catch (final Throwable e) {
            if (e instanceof StreamBreakException) {
                final StreamBreakException breaker = (StreamBreakException) e;
                if (breaker.getCause() != null) throw breaker.getCause();
                return Optional.ofNullable(breaker.getResult());
            } else throw e;
        }
        return Optional.empty();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandInvocation invocation) {
        Optional<List<String>> result = (onTabComplete == null ? Optional.empty() : invoke(TabCompleteStream::new, onTabComplete, invocation));
        return result.orElse(null);
    }
}
