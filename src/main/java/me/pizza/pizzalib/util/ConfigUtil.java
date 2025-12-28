package me.pizza.pizzalib.util;

import me.pizza.pizzalib.PizzaLib;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

@SuppressWarnings("UnstableApiUsage")
public class ConfigUtil {

    public static ItemStack buildItem(ConfigurationSection section) {
        Objects.requireNonNull(section, "ConfigurationSection cannot be null");

        ItemStack item;

        try {
            item = new ItemStack(Material.valueOf(section.getString("material", "STONE")));
        } catch (IllegalArgumentException e) {
            item = new ItemStack(Material.STONE);
            PizzaLib.plugin.getLogger().warning(section.getCurrentPath() + ": " + e.getMessage());
        }

        item.editMeta(itemMeta -> {
            // Name
            Component name = (section.getRichMessage("name") == null)
                    ? Component.empty()
                    : section.getRichMessage("name");

            itemMeta.itemName(name);

            // Lore
            List<Component> lore = section.getStringList("lore").stream()
                    .map(MessageUtil::getRichMessage)
                    .toList();

            itemMeta.lore(lore);

            // Custom model data
            CustomModelDataComponent customModelDataComponent = itemMeta.getCustomModelDataComponent();
            customModelDataComponent.setFloats(section.getFloatList("custom-model-data.floats"));
            customModelDataComponent.setStrings(section.getStringList("custom-model-data.strings"));

            itemMeta.setCustomModelDataComponent(customModelDataComponent);

            // Tooltip style
            String tooltipStyle = section.getString("tooltip-style", "");
            if (!tooltipStyle.isEmpty())
                itemMeta.setTooltipStyle(NamespacedKey.fromString(tooltipStyle));

            // Tooltip display
            itemMeta.setHideTooltip(section.getBoolean("hide-tooltip"));
        });

        return item;
    }

    public static Sound buildSound(ConfigurationSection section) {
        Objects.requireNonNull(section, "ConfigurationSection cannot be null");

        return Sound.sound(
                Key.key(section.getString("sound", "")),
                Sound.Source.MASTER,
                (float) section.getDouble("volume"),
                (float) section.getDouble("pitch"));
    }
}
