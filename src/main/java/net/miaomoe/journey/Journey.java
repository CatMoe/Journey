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

import lombok.Getter;
import lombok.ToString;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.miaomoe.journey.attribute.Attribute;
import net.miaomoe.journey.command.CommandManager;
import net.miaomoe.journey.modules.Module;
import net.miaomoe.journey.utils.Lazy;
import net.miaomoe.journey.utils.Preconditions;
import net.miaomoe.journey.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static net.miaomoe.journey.utils.Preconditions.checkArgument;
import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@SuppressWarnings("unused")
@ToString
public final class Journey<P extends JavaPlugin> implements JourneyMethods<P>, Attribute.ProxiedAttributeMap {
    private final @NotNull AbstractUnmodifiable unmodifiable = new AbstractUnmodifiable() {};
    private final @NotNull Lazy<BukkitAudiences> audience;
    private final @NotNull ThreadUtil threadUtil = new ThreadUtil(this);
    private final @NotNull CommandManager commandManager;
    @Getter private final @NotNull Attribute.AttributeMap attributeMap = Attribute.createSynchronizeMap();
    private static abstract class AbstractUnmodifiable {
        private boolean isDisabled = false;
        private final Map<Class<? extends Module>, Module> modules = new HashMap<>();
    }

    @Getter private final @NotNull P plugin;
    Journey(final @NotNull P plugin) {
        this.plugin = checkNotNull(plugin, "plugin");
        this.audience = new Lazy<>(() -> BukkitAudiences.create(plugin));
        this.commandManager = new CommandManager(this);
        for (final Function<Journey<?>, Module> modules : Module.alwaysRegister) {
            registerModule(modules.apply(this));
        }
    }

    private void checkDisabled() { checkArgument(!unmodifiable.isDisabled, "Cannot access audiences when Journey disabled."); }

    public @NotNull BukkitAudiences audiences() {
        checkDisabled();
        return audience.getValue();
    }

    public void disable() {
        checkDisabled();
        checkArgument(Bukkit.isPrimaryThread(), "Cannot disable Journey when not in main thread");
        unmodifiable.isDisabled = true;
    }

    public @NotNull ThreadUtil getThreadUtil() {
        checkDisabled();
        return threadUtil;
    }

    public @NotNull CommandManager getCommandManager() {
        checkDisabled();
        return commandManager;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> @NotNull T getModule(final @NotNull Class<T> moduleClass) {
        checkDisabled();
        return (T) checkNotNull(unmodifiable.modules.get(moduleClass), "module");
    }

    public <T extends Module> @NotNull T registerModule(final @NotNull T module) {
        checkDisabled();
        checkArgument(
                !unmodifiable.modules.containsKey(checkNotNull(module, "module").getClass()),
                "Module already registered"
        );
        unmodifiable.modules.put(module.getClass(), module);
        return module;
    }

    public <T extends Module> @NotNull T registerModule(final @NotNull Function<Journey<P>, T> module) {
        return registerModule(module.apply(this));
    }

    public <T extends Module> @NotNull T unregisterModule(final @NotNull Class<T> moduleClass) {
        checkDisabled();
        final T module = getModule(moduleClass);
        checkArgument(module.canUnregister(), "Cannot unregister module due canUnregister() method return false");
        unmodifiable.modules.remove(moduleClass);
        return module;
    }

    public boolean isRegistered(final @NotNull Class<? extends Module> moduleClass) {
        return unmodifiable.modules.containsKey(Preconditions.checkNotNull(moduleClass, "moduleClass"));
    }
}
