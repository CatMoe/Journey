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

package net.miaomoe.journey.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.miaomoe.journey.Journey;
import net.miaomoe.journey.JourneyLoader;
import net.miaomoe.journey.functions.exceptionally.ExceptionSupplier;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class ThreadUtil {
    @NotNull private final Journey<?> journey;
    public static final Unconditional SERVER_THREAD = new Unconditional() {
        @Override public void run(@NotNull ThreadUtil util, @NotNull Runnable runnable) {
            if (isCurrentThread()) runnable.run(); else {
                checkStartup(util);
                Bukkit.getScheduler().runTask(util.getJourney().getPlugin(), runnable);
            }
        }
        @SneakyThrows
        @Override public <T> CompletableFuture<T> get(@NotNull ThreadUtil util, @NotNull ExceptionSupplier<T> supplier) {
            final CompletableFuture<T> future = new CompletableFuture<>();
            if (isCurrentThread()) {
                future.complete(supplier.get());
            } else {
                checkStartup(util);
                this.run(util, () -> future.complete(supplier.asSupplier().get()));
            }
            return future;
        }
        @Override public boolean isCurrentThread() { return Bukkit.isPrimaryThread(); }

        private void checkStartup(final @NotNull ThreadUtil util) {
            if (!JourneyLoader.getInstance().isFullStartUp()) {
                util.getJourney().getPlugin().getLogger().log(
                        Level.WARNING,
                        "Try commit tasks to scheduler. This task will be run when startup finished. If not, Run this task directly in main thread.",
                        new IllegalStateException()
                );
            }
        }
    };
    public static final Unconditional ASYNC = new Unconditional() {
        @Override public void run(@NotNull ThreadUtil util, @NotNull Runnable runnable) {
            CompletableFuture.runAsync(runnable);
        }
        @Override public <T> CompletableFuture<T> get(@NotNull ThreadUtil util, @NotNull ExceptionSupplier<T> supplier) {
            return CompletableFuture.supplyAsync(supplier.asSupplier());
        }
        @Override public boolean isCurrentThread() { return false; }
    };
    @Deprecated public static final Unconditional CURRENT = new Unconditional() {
        @Override public void run(@NotNull ThreadUtil util, @NotNull Runnable runnable) { runnable.run(); }
        @Override public <T> CompletableFuture<T> get(@NotNull ThreadUtil util, @NotNull ExceptionSupplier<T> supplier) {
            final CompletableFuture<T> future = new CompletableFuture<>();
            future.complete(ExceptionSupplier.get(supplier));
            return future;
        }
        @Override public boolean isCurrentThread() { return true; }
    };

    public interface Unconditional {
        void run(@NotNull final ThreadUtil util, @NotNull final Runnable runnable) throws Exception;
        <T> CompletableFuture<T> get(@NotNull final ThreadUtil util, @NotNull final ExceptionSupplier<T> supplier) throws Exception;
        boolean isCurrentThread();
    }

    @SneakyThrows public void run(final Unconditional thread, final Runnable runnable) { thread.run(this, runnable); }
    @SneakyThrows public <T> CompletableFuture<T> get(final Unconditional thread, final ExceptionSupplier<T> supplier) { return thread.get(this, supplier); }
}
