package me.pizza.pizzalib.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class CooldownUtil {

    private static final Map<String, Long> COOLDOWNS = new HashMap<>();

    public static void applyCooldown(Player player, String identifier, long durationSeconds) {
        long expireAt = System.currentTimeMillis() + (durationSeconds * 1000);
        COOLDOWNS.put(buildKey(player, identifier), expireAt);
    }

    public static void removeCooldown(Player player, String identifier) {
        COOLDOWNS.remove(buildKey(player, identifier));
    }

    public static boolean isOnCooldown(Player player, String identifier) {
        String key = buildKey(player, identifier);

        Long expireAt = COOLDOWNS.get(key);

        if (expireAt == null) {
            return false;
        }

        if (System.currentTimeMillis() > expireAt) {
            COOLDOWNS.remove(key);
            return false;
        }

        return true;
    }

    public static long getRemaining(Player player, String identifier) {
        String key = buildKey(player, identifier);

        Long expireAt = COOLDOWNS.get(key);

        if (expireAt == null) {
            return 0;
        }

        if (System.currentTimeMillis() > expireAt) {
            COOLDOWNS.remove(key);
            return 0;
        }

        return expireAt - System.currentTimeMillis();
    }

    public static String getFormatted(Player player, String identifier) {
        long remaining = getRemaining(player, identifier);
        if (remaining == 0)
            return "";

        long seconds = remaining / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    private static String buildKey(Player player, String identifier) {
        return player.getUniqueId().toString() + ":" + identifier;
    }
}
