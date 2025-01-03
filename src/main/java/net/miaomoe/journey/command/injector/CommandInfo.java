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

package net.miaomoe.journey.command.injector;

import lombok.*;
import lombok.experimental.Accessors;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.annotation.Description;
import net.miaomoe.journey.command.impl.AbstractCommand;
import net.miaomoe.journey.utils.Preconditions;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@Setter
@Getter
@ToString
@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandInfo {
    private final @NotNull String name;
    private final @NotNull AbstractCommand command;
    private @Nullable String permission;
    @Description(description = "Not available for 1.13+ server")
    private @Nullable String noPermissionMessage;
    private @Nullable String description;
    private @Nullable Set<String> aliases;

    @NotNull PluginCommand asPluginCommand() throws RuntimeException {
        final @NotNull Journey<?> journey = command.getJourney();
        final @NotNull Plugin plugin = checkNotNull(journey, "journey").getPlugin();
        try {
            final PluginCommand pluginCommand = (PluginCommand) BukkitCommandInjector
                    .getInstance()
                    .getPluginCommandConstructor()
                    .getValue()
                    .invokeWithArguments(name, plugin);
            return applyToPluginCommand(pluginCommand);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull PluginCommand applyToPluginCommand(@NotNull final PluginCommand pluginCommand) {
        checkNotNull(pluginCommand, "pluginCommand").setExecutor(command);
        pluginCommand.setTabCompleter(command);
        pluginCommand.setPermission(permission);
        pluginCommand.setPermissionMessage(noPermissionMessage);
        pluginCommand.setDescription(description == null ? "No description." : description);
        pluginCommand.setAliases(new ArrayList<>(Optional.ofNullable(aliases).orElse(new HashSet<>())));
        return pluginCommand;
    }

    public @NotNull CommandInfo addAliases(final @NotNull String @NotNull... aliases) {
        for (final String alias : checkNotNull(aliases, "aliases")) {
            (this.aliases = Optional.ofNullable(this.aliases).orElse(new HashSet<>())).add(alias);
        }
        return this;
    }

    public @NotNull CommandInfo addAliases(final @NotNull List<@NotNull String> aliases) {
        for (final String alias : checkNotNull(aliases, "aliases")) {
            (this.aliases = Optional.ofNullable(this.aliases).orElse(new HashSet<>())).add(alias);
        }
        return this;
    }

    public @NotNull CommandInfo setAliases(final @NotNull String... aliases) {
        this.aliases = new HashSet<>(Arrays.asList(checkNotNull(aliases, "aliases")));
        return this;
    }

    public @NotNull CommandInfo setAliases(final @NotNull List<@NotNull String> aliases) {
        this.aliases = new HashSet<>(checkNotNull(aliases, "aliases"));
        return this;
    }

    public static @NotNull CommandInfo of(@NotNull final String name, @NotNull final AbstractCommand command) {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkArgument(!name.isEmpty(), "name cannot be empty");
        Preconditions.checkArgument(!name.contains(" "), "name cannot contains space");
        return new CommandInfo(name.toLowerCase(Locale.ROOT), command);
    }
}
