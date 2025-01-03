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

package net.miaomoe.journey.sender;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.adventure.PresetsSerializer;
import net.miaomoe.journey.functions.extend.BukkitBridge;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@SuppressWarnings({"unchecked", "unused"})
@RequiredArgsConstructor
public abstract class Sender<T extends CommandSender, U extends Sender<?, ?>> implements BukkitBridge<T> {
    @Getter private final @NotNull Journey<?> journey;
    private final @NotNull T sender;
    @Getter private final @NotNull String name;

    @Override
    public final @NotNull T getBukkitObject() {
        return sender;
    }

    public @NotNull U sendMessage(final @NotNull Component component) {
        //getBukkitObject().sendMessage(PresetsSerializer.serialize(PresetsSerializer.legacySection, component));
        getJourney().audiences().sender(getBukkitObject()).sendMessage(component);
        return getThis();
    }

    @SuppressWarnings("UnusedReturnValue")
    public final @NotNull U sendMessage(final @NotNull PresetsSerializer<?> serializer, final @NotNull String message) {
        return sendMessage(PresetsSerializer.deserialize(serializer, message));
    }

    public final U sendMessage(final @NotNull Component @NotNull... components) {
        for (final @NotNull Component component : components) sendMessage(checkNotNull(component, "component"));
        return getThis();
    }

    public final U sendMessage(final @NotNull PresetsSerializer<?> serializer, final @NotNull String @NotNull... messages) {
        final Component[] components = new Component[messages.length];
        for (int i = 0; i < components.length; i++) components[i] = PresetsSerializer.deserialize(serializer, messages[i]);
        return sendMessage(components);
    }

    @SuppressWarnings("UnusedReturnValue")
    public final U sendMessage(final @NotNull PresetsSerializer<?> serializer, final @Nullable List<String> messages) {
        return sendMessage(serializer, Optional.ofNullable(messages).orElse(Collections.emptyList()).toArray(new String[0]));
    }

    public final boolean hasPermission(final @NotNull String permission) {
        return getBukkitObject().hasPermission(checkNotNull(permission, "permission"));
    }

    public U addMetadata(final @NotNull String key, final @NotNull Object value) {
        throw new UnsupportedOperationException("Not support on this sender type.");
    }

    public U removeMetadata(final @NotNull String key) {
        throw new UnsupportedOperationException("Not support on this sender type");
    }

    public boolean hasMetadata(final @NotNull String key) {
        return false;
    }

    private @NotNull U getThis() { return (U) this; }

    public final boolean isPlayer() { return sender instanceof Player; }
    public final boolean isConsole() { return sender instanceof ConsoleCommandSender; }

    public static PlayerSender getSender(final @NotNull Journey<?> journey, final @NotNull Player player) {
        return new PlayerSender(journey, player);
    }

    public static Sender<?, ?> getSender(final @NotNull Journey<?> journey, final @NotNull CommandSender sender) {
        if (checkNotNull(sender, "sender") instanceof Player) {
            return getSender(journey, (Player) sender);
        } else if (sender instanceof ConsoleCommandSender) {
            return new ConsoleSender(journey, (ConsoleCommandSender) sender);
        } else {
            return new UnknownSender(journey, sender);
        }
    }

    public @NotNull <R extends Sender<?, ?>> R cast(final @NotNull Class<R> targetClass, final boolean unchecked) {
        return unchecked ? (R) this : targetClass.cast(this);
    }

    public @NotNull <R extends Sender<?, ?>> R cast(final @NotNull Class<R> targetClass) {
        return cast(targetClass, false);
    }
}
