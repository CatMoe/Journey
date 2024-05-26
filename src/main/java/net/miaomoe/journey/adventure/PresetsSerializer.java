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

package net.miaomoe.journey.adventure;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.miaomoe.journey.annotation.Description;
import net.miaomoe.journey.utils.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

import static net.miaomoe.journey.utils.Preconditions.checkNotNull;

@FunctionalInterface
@SuppressWarnings("unused")
@Description(description = "提供较为常见的序列化组件类. 以避免分配多个相同的序列化类")
public interface PresetsSerializer<T extends ComponentSerializer<Component, ?, String>> {
    @SuppressWarnings("DeprecatedIsStillUsed") @Deprecated @NotNull Lazy<@NotNull T> lazyProvider();
    default @NotNull T get() { return lazyProvider().getValue(); }

    @Description(description = "经典宽松的MiniMessage格式的解析器")
    PresetsSerializer<MiniMessage> miniMessage = () -> new Lazy<>(MiniMessage::miniMessage);
    @Description(description = "严格的MiniMessage格式解析器 要求所有标签正确编写和闭合. 但非常适合将序列化后的字符串插入到其它的消息中")
    PresetsSerializer<MiniMessage> strict = () -> new Lazy<>(() -> MiniMessage.builder().strict(true).build());
    @Description(description = "用于解析旧版颜色(分节符 §)符号到组件的解析器. 不支持点击事件和悬浮事件")
    PresetsSerializer<LegacyComponentSerializer> legacySection = () -> new Lazy<>(LegacyComponentSerializer::legacySection);
    @Description(description = "与legacySection的唯一区别是分节符在该解析器中应被替换成&")
    PresetsSerializer<LegacyComponentSerializer> legacyAmpersand = () -> new Lazy<>(LegacyComponentSerializer::legacyAmpersand);
    @Description(description = "纯文本解析器")
    PresetsSerializer<PlainTextComponentSerializer> plainText = () -> new Lazy<>(PlainTextComponentSerializer::plainText);

    static @NotNull String serialize(final @NotNull PresetsSerializer<?> serializer, final @NotNull Component component) {
        return Private.invoke(serializer, component, "component", ComponentSerializer::serialize);
    }

    static @NotNull Component deserialize(final @NotNull PresetsSerializer<?> serializer, final @NotNull String message) {
        return Private.invoke(serializer, message, "message", ComponentSerializer::deserialize);
    }

    @UtilityClass
    class Private {
        private static <T extends ComponentSerializer<Component, ?, String>> T get(final PresetsSerializer<T> serializer) {
            return checkNotNull(checkNotNull(serializer, "serializer").get(), "serializer");
        }

        private static <T extends ComponentSerializer<Component, ?, String>, U, R> R invoke(
                final PresetsSerializer<T> serializer,
                final U value,
                final @NotNull String name,
                final @NotNull BiFunction<T, U, R> function
        ) {
            return function.apply(get(serializer), checkNotNull(value, name));
        }
    }
}
