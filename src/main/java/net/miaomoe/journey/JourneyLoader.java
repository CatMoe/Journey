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

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.relocation.Relocation;
import lombok.Getter;
import net.miaomoe.journey.inventory.JourneyInventory;
import net.miaomoe.journey.utils.Preconditions;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@SuppressWarnings({"unused"})
public final class JourneyLoader extends JavaPlugin {
    @Getter private static JourneyLoader instance = null;
    @Getter private boolean enable = false;
    @Getter private boolean fullStartUp = false;
    private final Map<Class<? extends JavaPlugin>, Journey<?>> journeyMap = new ConcurrentHashMap<>();

    public JourneyLoader() {
        instance = this;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        final LibraryManager libraryManager = new BukkitLibraryManager(this);
        libraryManager.addMavenLocal();
        libraryManager.addMavenCentral();
        libraryManager.addSonatype();
        libraryManager.addRepository("https://s01.oss.sonatype.org/content/repositories/snapshots/");
        libraryManager.addRepository("https://repo.codemc.org/repository/maven-public/");
        final String adventureVersion = "4.17.0";
        final List<LibraryBundle> libraryBundleList = new ArrayList<LibraryBundle>(){{
            add(new LibraryBundle(
                    "net.kyori.adventure.text.minimessage.MiniMessage",
                    simpleBuilder("net.kyori", "adventure-text-minimessage", adventureVersion))
            );
            add(new LibraryBundle(
                    "{serializer}gson.GsonComponentSerializer",
                    simpleBuilder("net.kyori", "adventure-text-serializer-gson", adventureVersion)
            ));
            add(new LibraryBundle(
                    "{serializer}legacy.LegacyComponentSerializer",
                    simpleBuilder("net.kyori", "{serializer}legacy", adventureVersion)
            ));
            add(new LibraryBundle(
                    "{serializer}plain.PlainTextComponentSerializer",
                    simpleBuilder("net.kyori", "{serializer}plain", adventureVersion)
            ));
            add(new LibraryBundle(
                    "net.kyori.adventure.platform.bukkit.BukkitAudiences",
                    simpleBuilder("net.kyori", "adventure-platform-bukkit", "4.3.2")
            ));
        }};
        final Library[] array = libraryBundleList
                .stream()
                .filter(bundle -> !isClassPresets(bundle.getPresets()))
                .map(LibraryBundle::getLibrary)
                .toArray(Library[]::new);
        libraryManager.loadLibraries(array);
    }

    @Getter
    private static class LibraryBundle {
        final @NotNull String presets;
        final @NotNull Library library;
        public LibraryBundle(final @NotNull String presets, final @NotNull Library library) {
            this.presets = checkNotNull(presets, "presets").replace("{serializer}", "net.kyori.adventure.text.serializer.");
            this.library = checkNotNull(library, "library");
        }
    }

    @SuppressWarnings("SameParameterValue")
    private Library simpleBuilder(
            final String groupId,
            final String artifactId,
            final String version,
            final Relocation relocation
    ) {
        final Library.Builder builder = Library
                .builder()
                .groupId(groupId.replace(".", "{}"))
                .artifactId(artifactId.replace("{serializer}", "adventure-text-serializer-"))
                .version(version)
                .resolveTransitiveDependencies(true);
        if (relocation != null) builder.relocate(relocation);
        return builder.build();
    }

    @SuppressWarnings("SameParameterValue")
    private Library simpleBuilder(
            final String groupId,
            final String artifactId,
            final String version
    ) {
        return simpleBuilder(groupId, artifactId, version, null);
    }

    private boolean isClassPresets(final String name) {
        if (name.isEmpty()) return false;
        try {
            Class.forName(name);
            return true;
        } catch (final Exception ignore) {
            return false;
        }
    }

    @Override
    public void onEnable() {
        enable = true;
        getServer().getScheduler().runTask(this, () -> fullStartUp = true);
        final PluginManager pluginManager = getServer().getPluginManager();
        try {
            pluginManager.registerEvents(new JourneyInventory.InventoryListener(), this);
        } catch (final IllegalAccessException exception) {
            getLogger().log(Level.SEVERE, "Access inventory listener failed", exception);
        }
        final Journey<JourneyLoader> journey = getJourney(this);
    }

    @SuppressWarnings("unchecked")
    public static <P extends JavaPlugin> @NotNull Journey<P> getJourney(final @NotNull P plugin) {
        final JourneyLoader loader = getInstance();
        checkNotNull(loader, "Loader is not initialized");
        Preconditions.checkArgument(
                loader.isEnable(),
                "Loader is not enabled. (Wait when JourneyLoader#onEnable triggered, Add Journey in your plugin.yml 'depends' or 'softdepend')"
        );
        return (Journey<P>) loader.journeyMap.computeIfAbsent(checkNotNull(plugin, "plugin").getClass(), key -> new Journey<>(plugin));
    }

    @SuppressWarnings("unchecked")
    public static <P extends JavaPlugin> @NotNull Journey<P> getJourney(final @NotNull Class<P> pluginClass) {
        final JourneyLoader loader = getInstance();
        checkNotNull(loader, "Loader is not initialized");
        return (Journey<P>) checkNotNull(loader.journeyMap.get(pluginClass), "This plugins not registered Journey!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (final Journey<?> journey : journeyMap.values()) { journey.disable(); }
    }
}
