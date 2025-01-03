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

package net.miaomoe.journey.inventory;

import net.miaomoe.journey.Journey;
import net.miaomoe.journey.JourneyLoader;
import net.miaomoe.journey.functions.exceptionally.ExceptionBiConsumer;
import net.miaomoe.journey.utils.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@SuppressWarnings("unused")
public interface JourneyInventory<P extends JavaPlugin> extends InventoryHolder {
    @NotNull Journey<P> getJourney();
    @NotNull Player getPlayer();

    void onInventoryClick(final @NotNull InventoryClickEvent event);
    void onInventoryClose(final @NotNull InventoryCloseEvent event);

    default boolean isClosed() { return false; }

    final class InventoryListener implements Listener {

        public InventoryListener() throws IllegalAccessException {
            Preconditions.checkInvoker(this.getClass(), JourneyLoader.class, "onEnable");
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            cast(e, JourneyInventory::onInventoryClick);
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            cast(e, JourneyInventory::onInventoryClose);
        }

        private <T extends InventoryEvent> void cast(
                final @NotNull T event,
                final @NotNull ExceptionBiConsumer<JourneyInventory<?>, T> consumer
        ) {
            final InventoryHolder holder = event.getInventory().getHolder();
            if (holder instanceof JourneyInventory) {
                final JourneyInventory<?> journeyInventory = (JourneyInventory<?>) holder;
                if (journeyInventory.isClosed()) return;
                try {
                    consumer.accept(journeyInventory, event);
                } catch (Throwable throwable) {
                    journeyInventory
                            .getJourney()
                            .getPlugin()
                            .getLogger()
                            .log(Level.WARNING, "Unhandled exception - " + journeyInventory, throwable);
                }
            }
        }
    }
}
