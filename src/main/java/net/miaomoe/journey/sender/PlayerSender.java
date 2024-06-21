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

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.adventure.PresetsSerializer;
import net.miaomoe.journey.functions.extend.UUIDHolder;
import net.miaomoe.journey.modules.impl.ClientVersion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@SuppressWarnings("unused")
public final class PlayerSender extends Sender<Player, PlayerSender> implements UUIDHolder {
    private final Audience audience;
    PlayerSender(Journey<?> journey, Player player) {
        super(journey, player, player.getName());
        this.audience = journey.audiences().sender(player);
    }

    @Override
    public @NotNull UUID uuid() {
        return getBukkitObject().getUniqueId();
    }

    public int protocolVersion() {
        return getJourney().getModule(ClientVersion.class).protocolVersion(getBukkitObject());
    }

    public @NotNull PlayerSender sendActionbar(final @NotNull String message) {
        return sendActionBar(PresetsSerializer.deserialize(PresetsSerializer.miniMessage, checkNotNull(message, "message")));
    }

    public @NotNull PlayerSender sendActionBar(final @NotNull Component message) {
        audience.sendActionBar(checkNotNull(message, "message"));
        return this;
    }

    public static boolean canMerge(final @NotNull CommandSender sender) { return sender instanceof Player; }

}
