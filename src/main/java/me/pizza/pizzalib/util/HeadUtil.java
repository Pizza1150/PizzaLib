package me.pizza.pizzalib.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import me.pizza.pizzalib.PizzaLib;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings("UnstableApiUsage")
public class HeadUtil implements Listener {

    private static final NamespacedKey KEY = new NamespacedKey(PizzaLib.plugin, "head");

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        save(event.getPlayer());
    }

    public static ItemStack getPlayerHead(OfflinePlayer player) {
        if (player.isOnline())
            return buildHead((Player) player);

        byte[] bytes = player.getPersistentDataContainer().get(KEY, PersistentDataType.BYTE_ARRAY);
        if (bytes == null || bytes.length == 0)
            return new ItemStack(Material.PLAYER_HEAD);

        return ItemStack.deserializeBytes(bytes);
    }

    private static ItemStack buildHead(Player player) {
        ItemStack head = ItemStack.of(Material.PLAYER_HEAD);
        head.setData(
                DataComponentTypes.PROFILE,
                ResolvableProfile.resolvableProfile(player.getPlayerProfile()));

        return head;
    }

    private static void save(Player player) {
        player.getPersistentDataContainer().set(
                KEY,
                PersistentDataType.BYTE_ARRAY,
                buildHead(player).serializeAsBytes());
    }

    public static void saveAll() {
        Bukkit.getOnlinePlayers().forEach(HeadUtil::save);
    }
}
