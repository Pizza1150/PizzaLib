package me.pizza.pizzalib.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

public class CooldownUtil {

    private CooldownUtil() {
    }

    private static final Map<String, Long> COOLDOWNS = new ConcurrentHashMap<>();

    public static void applyCooldown(Player player, String identifier, long durationSeconds) {
        String key = player.getUniqueId() + ":" + identifier;
        long expireAt = System.currentTimeMillis() + (durationSeconds * 1000);
        COOLDOWNS.put(key, expireAt);
    }

    public static void removeCooldown(Player player, String identifier) {
        String key = player.getUniqueId() + ":" + identifier;
        COOLDOWNS.remove(key);
    }

    public static boolean isOnCooldown(Player player, String identifier) {
        String key = player.getUniqueId() + ":" + identifier;

        Long expireAt = COOLDOWNS.get(key);

        if (expireAt == null)
            return false;

        if (System.currentTimeMillis() > expireAt) {
            COOLDOWNS.remove(key);
            return false;
        }

        return true;
    }

    public static long getRemaining(Player player, String identifier) {
        String key = player.getUniqueId() + ":" + identifier;

        Long expireAt = COOLDOWNS.get(key);

        if (expireAt == null)
            return 0;

        long now = System.currentTimeMillis();
        if (now > expireAt) {
            COOLDOWNS.remove(key);
            return 0;
        }

        return expireAt - now;
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
}