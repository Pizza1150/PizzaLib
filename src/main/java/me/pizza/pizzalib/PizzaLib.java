package me.pizza.pizzalib;

import me.pizza.pizzalib.util.HeadUtil;

import org.bukkit.plugin.java.JavaPlugin;

public final class PizzaLib extends JavaPlugin {

    public static PizzaLib plugin;

    @Override
    public void onEnable() {
        plugin = this;

        getServer().getPluginManager().registerEvents(new HeadUtil(), this);
    }

    @Override
    public void onDisable() {
        HeadUtil.saveAll();
    }
}
