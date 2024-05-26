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

package net.miaomoe.journey.sender;

import net.kyori.adventure.text.Component;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.adventure.PresetsSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class ConsoleSender extends Sender<ConsoleCommandSender, ConsoleSender> {
    protected ConsoleSender(@NotNull Journey<?> journey, @NotNull ConsoleCommandSender sender) {
        super(journey, sender, "console");
    }

    protected ConsoleSender(@NotNull Journey<?> journey) {
        this(journey, Bukkit.getConsoleSender());
    }

    @Override
    public @NotNull ConsoleSender sendMessage(@NotNull Component component) {
        getBukkitObject().sendMessage(PresetsSerializer.serialize(PresetsSerializer.legacySection,  component));
        return this;
    }

    public static boolean canMerge(final CommandSender sender) { return sender instanceof ConsoleCommandSender; }
}
