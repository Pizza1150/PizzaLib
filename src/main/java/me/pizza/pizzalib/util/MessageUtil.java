package me.pizza.pizzalib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageUtil {

    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component getRichMessage(@Nullable String message) {
        return (message == null) ? null : MINI_MESSAGE.deserialize("<!i>" + message);
    }

    public static Component getRichMessage(@Nullable String message, TagResolver... tagResolvers) {
        return (message == null) ? null : MINI_MESSAGE.deserialize("<!i>" + message, tagResolvers);
    }

    @NotNull
    public static String toString(@NotNull Component component) {
        return MINI_MESSAGE.serialize(component);
    }
}
