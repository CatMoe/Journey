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

package net.miaomoe.journey.modules;

import net.miaomoe.journey.Journey;
import net.miaomoe.journey.modules.impl.ClientVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface Module {
    @NotNull Journey<?> getJourney();
    default boolean canUnregister() {
        return false;
    }

    List<Function<Journey<?>, Module>> alwaysRegister = new ArrayList<Function<Journey<?>, Module>>(){{
        final PluginManager pluginManager = Bukkit.getPluginManager();
        /*
        final Map<String, Function<Journey<?>, Module>> modules = new HashMap<String, Function<Journey<?>, Module>>(){{
            put("ViaVersion", JourneyViaApi::new);
        }};
        for (final String plugin : modules.keySet()) {
            if (pluginManager.isPluginEnabled(plugin)) add(modules.get(plugin));
        }
         */
        add(ClientVersion::new);
    }};
}
