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

import lombok.*;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.sender.Sender;
import net.miaomoe.journey.utils.Preconditions;
import net.miaomoe.journey.utils.math.Compare;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.BiFunction;

@Getter
@ToString
@AllArgsConstructor
@SuppressWarnings("unused")
public final class CommandInvocation {
    @NotNull private final Journey<?> journey;
    @NotNull private final CommandSender bukkitSender;
    @NotNull private final String aliases;
    @NotNull private final String[] args;
    @NotNull private final Sender<?, ?> sender;

    public <T> @NotNull T getArg(@NotNull final ArgType<T> type, final int index) throws IllegalArgumentException, IndexOutOfBoundsException {
        return Preconditions.checkNotNull(type, "type").parse(args[index]);
    }

    public boolean checkArgsLength(final @NotNull Compare compare, final int excepted) {
        return Preconditions.checkNotNull(compare, "compare").compare(excepted, getArgs().length);
    }

    public <T> @NotNull T getArg(
            @NotNull final ArgType<T> type,
            final int index,
            final BiFunction<@Nullable String, @NotNull RuntimeException, @NotNull T> function
    ) {
        String arg = null;
        try {
            return Preconditions.checkNotNull(type, "type").parse(arg = args[index]);
        } catch (final RuntimeException exception) {
            return Preconditions.checkNotNull(function, "function").apply(arg, exception);
        }
    }

    public @NotNull CommandInvocation clipArgsAndCopy(final int index) throws IllegalArgumentException {
        Preconditions.checkArgument(index < 0 || index >= getArgs().length, "Index out of bounds");
        return new CommandInvocation(getJourney(), getBukkitSender(), getAliases(), Arrays.copyOfRange(getArgs(), index, getArgs().length), getSender());
    }

    @FunctionalInterface
    public interface ArgType<T> {
        @NotNull T parse(@NotNull String arg) throws IllegalArgumentException;
        ArgType<Player> PLAYER = arg -> {
            Player player;
            if ((player = Bukkit.getPlayer(arg)) == null) {
                try {
                    player = Bukkit.getPlayer(UUID.fromString(arg));
                } catch (final Exception ignore) {

                }
                if (player == null) throw new IllegalArgumentException("Player not found");
            }
            return player;
        };
        ArgType<Integer> INT = Integer::parseInt;
        ArgType<Long> LONG = Long::parseLong;
        ArgType<Double> DOUBLE = Double::parseDouble;
        ArgType<String> STRING = it -> it;
    }
}
