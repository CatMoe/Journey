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

package net.miaomoe.journey;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.miaomoe.journey.command.CommandManager;
import net.miaomoe.journey.modules.Module;
import net.miaomoe.journey.utils.ThreadUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public interface JourneyMethods<P extends JavaPlugin> {
    @NotNull BukkitAudiences audiences();
    void disable();
    @NotNull ThreadUtil getThreadUtil();
    @NotNull CommandManager getCommandManager();
    <T extends Module> @NotNull T getModule(@NotNull Class<T> moduleClass);
    default <T extends Module> @NotNull Optional<T> getModuleSafely(@NotNull Class<T> moduleClass) {
        try { return Optional.of(getModule(moduleClass)); } catch (Exception e) { return Optional.empty(); }
    }
    <T extends Module> @NotNull T registerModule(@NotNull T module);
    <T extends Module> @NotNull T registerModule(final @NotNull Function<Journey<P>, T> module);
    <T extends Module> @NotNull T unregisterModule(@NotNull Class<T> moduleClass);
    boolean isRegistered(@NotNull Class<? extends Module> moduleClass);

    interface Proxy<P extends JavaPlugin> extends JourneyMethods<P> {
        @NotNull Journey<P> getJourney();
        @Override default @NotNull BukkitAudiences audiences() {
            return getJourney().audiences();
        }
        @Override default void disable() {
            getJourney().disable();
        }
        @Override default @NotNull ThreadUtil getThreadUtil() {
            return getJourney().getThreadUtil();
        }
        @Override default @NotNull CommandManager getCommandManager() {
            return getJourney().getCommandManager();
        }
        @Override default <T extends Module> @NotNull T getModule(@NotNull Class<T> moduleClass) {
            return getJourney().getModule(moduleClass);
        }
        @Override default <T extends Module> @NotNull T registerModule(@NotNull T module) {
            return getJourney().registerModule(module);
        }
        @Override default <T extends Module> @NotNull T registerModule(final @NotNull Function<Journey<P>, T> function) {
            return getJourney().registerModule(function);
        }
        @Override default <T extends Module> @NotNull T unregisterModule(@NotNull Class<T> moduleClass) {
            return getJourney().unregisterModule(moduleClass);
        }
        @Override default boolean isRegistered(@NotNull Class<? extends Module> moduleClass) {
            return getJourney().isRegistered(moduleClass);
        }
    }
}
