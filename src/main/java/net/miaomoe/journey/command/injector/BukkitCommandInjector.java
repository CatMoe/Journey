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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.utils.Lazy;
import net.miaomoe.journey.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Locale;

import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitCommandInjector {
    @Getter private static final BukkitCommandInjector instance = new BukkitCommandInjector();

    // maybe not exist in legacy server
    private final Lazy<MethodHandle> syncCommandMethod = new Lazy<>(() ->
            MethodHandles.
                    publicLookup()
                    .findVirtual(Bukkit.getServer().getClass(), "syncCommands", MethodType.methodType(void.class))
    );
    private final Lazy<MethodHandle> commandsMap = new Lazy<>(() ->
            MethodHandles
                    .publicLookup()
                    .findVirtual(Bukkit.getServer().getClass(), "getCommandMap", MethodType.methodType(SimpleCommandMap.class))
    );

    @Getter(AccessLevel.PROTECTED) final Lazy<MethodHandle> pluginCommandConstructor = new Lazy<>(() -> {
        final Constructor<PluginCommand> pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        pluginCommandConstructor.setAccessible(true);
        return MethodHandles.lookup().unreflectConstructor(pluginCommandConstructor);
    });

    public @NotNull SimpleCommandMap getCommandMap() {
        try {
            return (SimpleCommandMap) commandsMap.getValue().invoke(Bukkit.getServer());
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void register(final @NotNull CommandInfo info) {
        getCommandMap().register(getPrefix(info), info.asPluginCommand());
    }

    private @NotNull String getPrefix(final @NotNull CommandInfo info) {
        return checkNotNull(info, "info").command().getJourney().getPlugin().getName().toLowerCase(Locale.ROOT);
    }

    @SneakyThrows
    public @Nullable Throwable syncCommands(final @NotNull Journey<?> journey) {
        return journey.getThreadUtil().get(ThreadUtil.SERVER_THREAD, this::syncCommandsUnsafely).get();
    }

    @SneakyThrows
    private @Nullable Throwable syncCommandsUnsafely() {
        try {
            syncCommandMethod.getValue().invoke(Bukkit.getServer());
            return null;
        } catch (Throwable throwable) {
            return throwable;
        }
    }
}
