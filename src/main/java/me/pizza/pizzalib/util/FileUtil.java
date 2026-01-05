package me.pizza.pizzalib.util;

import java.io.File;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class FileUtil {

    @Nullable
    public static File copyDefaultFile(Plugin plugin, String path) {
        File file = new File(plugin.getDataFolder(), path);

        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            plugin.getLogger().severe("Failed to create directory: " + parent);
            return null;
        }

        if (!file.exists())
            plugin.saveResource(path, false);

        return file;
    }

    public static File[] listFiles(Plugin plugin, String path) {
        File folder = new File(plugin.getDataFolder(), path);
        if (!folder.exists() || !folder.isDirectory())
            return new File[0];

        File[] files = folder.listFiles(file -> file.getName().endsWith(".yml"));
        return (files == null) ? new File[0] : files;
    }
}
