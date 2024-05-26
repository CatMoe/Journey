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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.command.CommandInvocation;
import net.miaomoe.journey.sender.Sender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public abstract class AbstractCommand implements CommandExecutor, TabCompleter {
    private final @NotNull Journey<?> journey;

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        this.onCommand(new CommandInvocation(journey, sender, s, strings, Sender.getSender(getJourney(), sender)));
        return false;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        return Optional
                .ofNullable(this.onTabComplete(new CommandInvocation(journey, sender, s, strings, Sender.getSender(getJourney(), sender))))
                .orElse(Collections.emptyList());
    }

    public abstract void onCommand(final @NotNull CommandInvocation invocation);
    public abstract @Nullable List<String> onTabComplete(final @NotNull CommandInvocation invocation);
}
