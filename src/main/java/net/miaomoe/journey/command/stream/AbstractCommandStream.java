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

package net.miaomoe.journey.command.stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.miaomoe.journey.command.CommandInvocation;
import net.miaomoe.journey.functions.exceptionally.ExceptionBiConsumer;
import net.miaomoe.journey.functions.exceptionally.ExceptionConsumer;
import net.miaomoe.journey.utils.math.Compare;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@Getter
@ToString
@Accessors(fluent = true)
@RequiredArgsConstructor
@SuppressWarnings({"unchecked", "unused"})
public abstract class AbstractCommandStream<T extends AbstractCommandStream<?>> {
    private final @NotNull CommandInvocation invocation;

    public final @NotNull T ifExpress(
            final boolean express,
            final @NotNull Runnable ifTrue,
            final @NotNull Runnable ifFalse
    ) {
        (express ? checkNotNull(ifTrue, "ifTrue") : checkNotNull(ifFalse, "ifFalse")).run();
        return (T) this;
    }

    public final @NotNull T run(final @NotNull Runnable runnable) {
        runnable.run();
        return (T) this;
    }

    public final @NotNull T tryCatching(
            final @NotNull ExceptionConsumer<@NotNull CommandInvocation> func,
            final @NotNull Consumer<@NotNull Throwable> onException
    ) {
        try {
            func.accept(invocation());
        } catch (final Throwable throwable) {
            if (throwable instanceof StreamBreakException) throw (StreamBreakException) throwable;
            onException.accept(throwable);
        }
        return (T) this;
    }

    public final @NotNull T breakIf(final boolean express, final @NotNull String @NotNull... args) {
        return breakIf(express, Arrays.asList(args));
    }

    public final @NotNull T breakIf(final @NotNull Predicate<CommandInvocation> predicate, final @NotNull String @NotNull... args) {
        return breakIf(predicate.test(invocation()), args);
    }

    public final @NotNull T breakIf(final boolean express, final @NotNull List<String> args) {
        if (express) throw new StreamBreakException(args);
        return (T) this;
    }

    public final void breakExceptionally(final @NotNull Throwable cause) {
        throw new StreamBreakException(cause);
    }

    public final void breakStream(final @NotNull String @NotNull ... args) {
        breakStream(Arrays.asList(args));
    }

    public final void breakStream(final @NotNull List<String> args) {
        throw new StreamBreakException(args);
    }

    @SneakyThrows
    public <A> @NotNull T withArg(
            final @NotNull CommandInvocation.ArgType<A> argType,
            final int index,
            final @NotNull ExceptionBiConsumer<@NotNull A, @NotNull CommandInvocation> func
    ) throws IllegalArgumentException, IndexOutOfBoundsException {
        func.accept(invocation().getArg(argType, index), invocation());
        return (T) this;
    }

    public @NotNull T withArg(final int index, final @NotNull ExceptionBiConsumer<@NotNull String, @NotNull CommandInvocation> func) throws IndexOutOfBoundsException {
        return withArg(CommandInvocation.ArgType.STRING, index, func);
    }

    public boolean checkArgsLength(final @NotNull Compare compare, final int excepted) {
        return invocation().checkArgsLength(compare, excepted);
    }

    public boolean checkPermission(final @NotNull String permission) {
        return invocation.checkPermission(permission);
    }
}
