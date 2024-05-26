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

import lombok.Getter;
import lombok.NonNull;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.annotation.Description;
import net.miaomoe.journey.command.impl.AbstractCommand;
import net.miaomoe.journey.command.injector.BukkitCommandInjector;
import net.miaomoe.journey.command.injector.CommandInfo;
import net.miaomoe.journey.utils.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@SuppressWarnings("unused")
public final class CommandManager implements CommandRegistrable<AbstractCommand, CommandManager> {
    @Getter private final @NotNull Journey<?> journey;
    private final @NotNull JavaPlugin plugin;
    public CommandManager(final @NonNull Journey<?> journey) {
        this.journey = checkNotNull(journey, "journey");
        this.plugin = journey.getPlugin();
    }

    private final Map<String, String> aliasMap = new HashMap<>();
    private final Map<String, CommandInfo> commands = new HashMap<>();

    @Override
    @Deprecated
    @Description(description = "Use register(CommandInfo...) to register commands.")
    public @NotNull CommandManager register(final @NotNull AbstractCommand command, final @NotNull String name, final @NotNull String @NotNull ... aliases) {
        return registers(true, CommandInfo.of(name, command).addAliases(aliases));
    }

    public @NotNull CommandManager registers(final boolean sync, final @NotNull CommandInfo @NotNull ... commands) {
        Preconditions.checkArgument(Bukkit.isPrimaryThread(), "Cannot register commands in a non-primary thread!");
        final BukkitCommandInjector injector = BukkitCommandInjector.getInstance();
        for (final @NotNull CommandInfo command : commands) {
            PluginCommand bukkitCommand;
            try {
                if ((bukkitCommand = plugin.getCommand(command.name())) != null) {
                    command.applyToPluginCommand(bukkitCommand);
                } else {
                    injector.register(command);
                }
            } catch (final Throwable e) {
                warn("Failed to create PluginCommands object for " + command, e);
                continue;
            }
            if (command.aliases() != null && !command.aliases().isEmpty()) {
                command.aliases().remove(command.name());
                command.aliases().forEach(it -> aliasMap.put(it, command.name()));
            }
            this.commands.put(command.name(), command);
        }
        return sync ? syncCommands() : this;
    }

    public @NotNull CommandManager registers(final @NotNull CommandInfo @NotNull ... commands) {
        return registers(true, commands);
    }

    public @NotNull CommandManager register(final boolean sync, final @NotNull CommandInfo info) {
        return registers(sync, info);
    }

    public @NotNull CommandManager registers(final @NotNull CommandInfo info) {
        return registers(false, info);
    }

    @Description(description = {
            "Re-sync commands for 1.13+ server. (If it is not synchronized. You can't use the command)",
            "(Requires on the main thread and is very expensive to call!)",
            "Bulk updates is recommend. instead of sync for every command to register."
    })
    public @NotNull CommandManager syncCommands() {
        Throwable throwable;
        if ((throwable = BukkitCommandInjector.getInstance().syncCommands(getJourney())) != null && !(throwable instanceof NoSuchMethodException)) {
            warn("Failed to sync commands", throwable);
        }
        return this;
    }

    private void warn(final @NotNull String message, final @Nullable Throwable throwable) {
        getJourney().getPlugin().getLogger().log(Level.WARNING, message, throwable);
    }

    @Override
    public @NotNull CommandManager unregister(final @NotNull String name) {
        return unregister(name, false);
    }

    public @NotNull CommandManager unregister(final @NotNull String name, final boolean sync) {
        final CommandInfo command = checkNotNull(commands.get(name), "command");
        final BukkitCommandInjector injector = BukkitCommandInjector.getInstance();
        final SimpleCommandMap commandMap = injector.getCommandMap();
        PluginCommand bukkitCommand;
        if ((bukkitCommand = plugin.getCommand(command.name())) != null) {
            bukkitCommand.unregister(commandMap);
        } else if (
                (bukkitCommand = plugin.getCommand(command.name())) != null
                && bukkitCommand.getExecutor() instanceof AbstractCommand
                && bukkitCommand.getTabCompleter() instanceof AbstractCommand
        ) {
            if (((AbstractCommand) bukkitCommand.getExecutor()).getJourney() == this.getJourney())
                bukkitCommand.unregister(commandMap);
        }
        return sync ? syncCommands() : this;
    }

    public @NotNull CommandManager unregister(final @NotNull String @NotNull ... names) {
        for (final String name : names) unregister(name, false);
        return syncCommands();
    }

    @Override public boolean isRegistered(@NotNull String name) {
        return isAliases(name) ? isRegistered(aliasMap.get(name)) : commands.containsKey(name);
    }

    @Override public boolean isAliases(@NotNull String name) {
        return aliasMap.containsKey(checkNotNull(name, "name"));
    }

    @Override
    public @NotNull String getCommandName(@NotNull String alias) throws IllegalArgumentException {
        return checkNotNull(aliasMap.get(checkNotNull(alias, "alias")), null, s -> new IllegalArgumentException("Alias not registered"));
    }
}
