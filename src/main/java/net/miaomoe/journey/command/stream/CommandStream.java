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

package net.miaomoe.journey.command.stream;

import lombok.SneakyThrows;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.adventure.PresetsSerializer;
import net.miaomoe.journey.command.CommandInvocation;
import net.miaomoe.journey.utils.Preconditions;
import net.miaomoe.journey.utils.ThreadUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.logging.Level;

@SuppressWarnings("unused")
public final class CommandStream extends AbstractCommandStream<CommandStream> {
    public CommandStream(@NotNull CommandInvocation invocation) {
        super(invocation);
    }

    @SneakyThrows
    public @NotNull CommandStream runAt(final @NotNull ThreadUtil.Unconditional thread, final @NotNull Runnable runnable) {
        if (Preconditions.checkNotNull(thread, "thread").isCurrentThread()) {
            runnable.run();
        } else {
            final Journey<?> journey = invocation().getJourney();
            journey.getThreadUtil().run(thread, () -> {
                try {
                    runnable.run();
                } catch (final Throwable e) {
                    if (e instanceof StreamBreakException) {
                        final CommandSender sender = invocation().getBukkitSender();
                        if (sender instanceof Player && !((Player) sender).isOnline()) return;
                        Optional
                                .ofNullable(((StreamBreakException) e).getResult())
                                .ifPresent(it -> it.forEach(msg -> invocation().getSender().sendMessage(PresetsSerializer.miniMessage, msg)));
                    } else {
                        journey.getPlugin().getLogger().log(Level.WARNING, "Unhandled exception on stream commands. - Thread " + Thread.currentThread().getName(), e);
                    }
                }
            });
        }
        return this;
    }
}
