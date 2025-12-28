package me.pizza.pizzalib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component getRichMessage(String message) {
        return (message == null) ? null : MINI_MESSAGE.deserialize("<!i>" + message);
    }

    public static Component getRichMessage(String message, TagResolver... tagResolvers) {
        return (message == null) ? null : MINI_MESSAGE.deserialize("<!i>" + message, tagResolvers);
    }
}
