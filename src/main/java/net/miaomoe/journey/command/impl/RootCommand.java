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

import lombok.Setter;
import lombok.experimental.Accessors;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.adventure.PresetsSerializer;
import net.miaomoe.journey.annotation.Description;
import net.miaomoe.journey.command.CommandInvocation;
import net.miaomoe.journey.command.CommandInvocation.ArgType;
import net.miaomoe.journey.command.CommandRegistrable;
import net.miaomoe.journey.sender.Sender;
import net.miaomoe.journey.utils.Preconditions;
import net.miaomoe.journey.utils.math.Compare;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static net.miaomoe.journey.utils.Preconditions.checkArgument;
import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@Accessors(fluent = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class RootCommand extends AbstractCommand implements CommandRegistrable<AbstractCommand, RootCommand> {
    public RootCommand(final @NotNull Journey<?> journey) {
        super(journey);
    }

    public RootCommand(
            final @NotNull Journey<?> journey,
            final @Nullable List<String> helpTip,
            final @Nullable List<String> unknownSubTip
    ) {
        this(journey);
        this.helpTip = helpTip;
        this.unknownSubTip = unknownSubTip;
    }

    @Description(description = "当未键入任何子命令时显示的消息")
    @Setter private @Nullable List<String> helpTip = null;
    @Description(description = "当未找到指定的子命令时发送的消息")
    @Setter private @Nullable List<String> unknownSubTip = null;
    private final Map<String, AbstractCommand> subCommands = new HashMap<>();
    private final Map<String, Set<String>> linkedAliases = new HashMap<>();

    @Override public void onCommand(@NotNull CommandInvocation invocation) {
        if (invocation.checkArgsLength(Compare.EQUAL, 0)) {
            sendMessage(invocation.getSender(), helpTip);
        } else {
            final Optional<AbstractCommand> command = Optional.ofNullable(subCommands.get(invocation.getArg(
                    ArgType.STRING,
                    0
            ).toLowerCase(Locale.ROOT)));
            if (command.isPresent()) {
                command.get().onCommand(invocation.clipArgsAndCopy(1));
            } else {
                sendMessage(invocation.getSender(), unknownSubTip);
            }
        }
    }

    @Override public @NotNull RootCommand register(
            final @NotNull AbstractCommand command,
            final @NotNull String name,
            final @NotNull String @NotNull ... aliases
    ) {
        final String n = checkNotNull(name, "name").toLowerCase(Locale.ROOT);
        checkArgument(!subCommands.containsKey(n), "Command " + name + " already registered");
        checkArgument(!name.contains(" "), "Command " + name + " is invalid (Cannot register name with space)");
        subCommands.put(n, checkNotNull(command, "command"));
        for (final @NotNull String alias : checkNotNull(aliases, "aliases")) {
            register(command, checkNotNull(alias, "alias"));
            linkedAliases.computeIfAbsent(name, k -> new HashSet<>()).add(alias);
        }
        return this;
    }

    @Override public @NotNull RootCommand unregister(final @NotNull String name) {
        final String n = checkNotNull(name, "name").toLowerCase(Locale.ROOT);
        checkArgument(subCommands.containsKey(n), "Command " + name + " not registered");
        subCommands.remove(n);
        Optional.ofNullable(linkedAliases.get(n)).ifPresent(set -> {
            set.forEach(subCommands::remove);
            linkedAliases.remove(n);
        });
        return this;
    }

    private void sendMessage(final Sender<?, ?> sender, final @Nullable List<String> messages) {
        checkNotNull(sender, "sender").sendMessage(PresetsSerializer.miniMessage, messages);
    }

    @Override public boolean isRegistered(final @NotNull String name) {
        return subCommands.containsKey(checkNotNull(name, "name").toLowerCase(Locale.ROOT));
    }

    @Override public boolean isAliases(final @NotNull String name) {
        final String n = checkNotNull(name, "name").toLowerCase(Locale.ROOT);
        if (subCommands.containsKey(n)) {
            return linkedAliases.containsKey(n);
        }
        return false;
    }

    @Override public @NotNull String getCommandName(@NotNull String alias) throws IllegalArgumentException {
        final String n = checkNotNull(alias, "aliases").toLowerCase(Locale.ROOT);
        Preconditions.checkArgument(subCommands.containsKey(n), "Alias not registered");
        for (final Map.Entry<String, Set<String>> entry : linkedAliases.entrySet()) {
            if (entry.getValue().contains(n)) { return entry.getKey(); }
        }
        throw new IllegalArgumentException("Alias not registered");
    }

    @Override @Nullable public List<String> onTabComplete(@NotNull CommandInvocation invocation) {
        if (invocation.checkArgsLength(Compare.EQUAL, 1)) {
            return subCommands.keySet().stream().filter(it -> !isAliases(it)).collect(Collectors.toList());
        } else if (invocation.checkArgsLength(Compare.GREATER_THAN, 1)) {
            Optional<AbstractCommand> command = Optional.ofNullable(subCommands.get(invocation.getArg(ArgType.STRING, 0).toLowerCase(Locale.ROOT)));
            if (command.isPresent()) {
                return command.get().onTabComplete(invocation.clipArgsAndCopy(1));
            }
        }
        return null;
    }
}
