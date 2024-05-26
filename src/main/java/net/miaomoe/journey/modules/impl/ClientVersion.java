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

package net.miaomoe.journey.modules.impl;

import lombok.Getter;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.functions.ExceptionFunction;
import net.miaomoe.journey.modules.Module;
import net.miaomoe.journey.utils.Lazy;
import net.miaomoe.journey.utils.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.function.Function;

@Getter
public class ClientVersion implements Module {
    private final @NotNull Journey<?> journey;

    public ClientVersion(final Journey<?> journey) {
        this.journey = Preconditions.checkNotNull(journey, "journey");
    }

    private static boolean isInstalled(final @NotNull String plugin) { return Bukkit.getPluginManager().isPluginEnabled(plugin); }

    private static final Lazy<Function<Player, OptionalInt>> paperPlayerVersion = new Lazy<>(() -> {
        final MethodHandle handle = MethodHandles
                .publicLookup()
                .findVirtual(
                        Class.forName("com.destroystokyo.paper.network.NetworkClient"),
                        "getProtocolVersion",
                        MethodType.methodType(int.class)
                );
        return asOptionalFunction(handle::invoke);
    });

    private static final Lazy<Function<Player, OptionalInt>> viaVersion = new Lazy<>(() -> {
        Preconditions.checkArgument(isInstalled("ViaVersion"), "ViaVersion not installed");
        final String prefix = "com.viaversion.viaversion.api.";
        final Object via = MethodHandles
                .publicLookup()
                .findStatic(
                        Class.forName(prefix + "Via"), "getAPI",
                        MethodType.methodType(Class.forName(prefix + "ViaAPI"))
                )
                .invokeExact();
        final MethodHandle playerVersion = MethodHandles
                .publicLookup()
                .findVirtual(via.getClass(), "getPlayerVersion", MethodType.methodType(int.class, Player.class));
        return asOptionalFunction(player -> playerVersion.invoke(via, player));
    });

    private static final Lazy<Function<Player, OptionalInt>> protocolLib = new Lazy<>(() -> {
        Preconditions.checkArgument(isInstalled("ProtocolLib"), "ProtocolLib is not installed");
        final String prefix = "com.comphenix.protocol.";
        final Object protocolManager = MethodHandles
                .publicLookup()
                .findStatic(
                        Class.forName(prefix + "ProtocolLibrary"), "getProtocolManager",
                        MethodType.methodType(Class.forName(prefix + "ProtocolManager"))
                )
                .invokeExact();
        final MethodHandle playerVersion = MethodHandles
                .publicLookup()
                .findVirtual(protocolManager.getClass(), "getProtocolVersion", MethodType.methodType(int.class, Player.class));
        return asOptionalFunction(player -> playerVersion.invoke(protocolManager, player));
    });

    private static final Lazy<Function<Player, OptionalInt>> packetEvents = new Lazy<>(() -> {
       Preconditions.checkArgument(isInstalled("PacketEvents"), "PacketEvents is not installed");
       final String prefix = "com.github.retrooper.packetevents.";
       final Object api = MethodHandles
               .publicLookup()
               .findStatic(Class.forName(prefix + "PacketEvents"), "getAPI", MethodType.methodType(Class.forName(prefix + "PacketEventsAPI")))
               .invokeExact();
       final Object playerManager = MethodHandles
               .publicLookup()
               .findVirtual(api.getClass(), "getPlayerManager", MethodType.methodType(Class.forName(prefix + "manager.player.PlayerManager")))
               .invoke(api);
       final Class<?> userClass = Class.forName(prefix + "protocol.player.User");
       final MethodHandle userMethod = MethodHandles.publicLookup().findVirtual(playerManager.getClass(), "getUser", MethodType.methodType(userClass, Object.class));
       final Class<?> versionClass = Class.forName(prefix + "protocol.player.ClientVersion");
       final MethodHandle versionMethod = MethodHandles.publicLookup().findVirtual(userClass, "getVersion", MethodType.methodType(versionClass));
       final MethodHandle versionId = MethodHandles.publicLookup().findVirtual(versionClass, "getProtocolVersion", MethodType.methodType(int.class));
       return asOptionalFunction(player -> versionId.invoke(versionMethod.invoke(userMethod.invoke(playerManager, player))));
    });

    private static Function<Player, OptionalInt> asOptionalFunction(final @NotNull ExceptionFunction<Player, Object> func) {
        return player -> { try { return OptionalInt.of((int) func.apply(player)); } catch (final Throwable throwable) { return OptionalInt.empty(); } };
    }

    @SuppressWarnings({"deprecation", "JavaLangInvokeHandleSignature"})
    private static final Lazy<Integer> serverVersion = new Lazy<>(() -> (int) MethodHandles
            .publicLookup()
            .findStatic(UnsafeValues.class, "getProtocolVersion", MethodType.methodType(int.class))
            .invokeExact()
    );

    public int protocolVersion(final @NotNull Player player) throws IllegalArgumentException {
        Preconditions.checkNotNull(player, "player");
        for (final Lazy<Function<Player, OptionalInt>> lazy : Arrays.asList(viaVersion, packetEvents, paperPlayerVersion, protocolLib)) {
            if (lazy.getCaused() != null) continue;
            final Function<Player, OptionalInt> func = lazy.getValue();
            final OptionalInt optionalInt = func.apply(player);
            if (optionalInt.isPresent()) return optionalInt.getAsInt();
        }
        if (serverVersion.getCaused() == null)
            try { return Preconditions.checkNotNull(serverVersion.getValue(), "serverVersion"); } catch (final Throwable ignore) {}
        throw new IllegalArgumentException("Not any plugins/method to provide client version");
    }
}
