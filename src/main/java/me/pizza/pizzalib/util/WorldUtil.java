package me.pizza.pizzalib.util;

import me.pizza.pizzalib.PizzaLib;
import net.kyori.adventure.util.TriState;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.codehaus.plexus.util.FileUtils;

public class WorldUtil {

    public static CompletableFuture<World> cloneWorld(String from, String to) {
        CompletableFuture<World> future = new CompletableFuture<>();

        World fromWorld = Bukkit.getWorld(from);
        if (fromWorld == null) {
            future.completeExceptionally(
                    new IllegalStateException("World '" + from + "' is not loaded or does not exist")
            );
            return future;
        }

        File worldContainer = Bukkit.getWorldContainer();
        File sourceDir = fromWorld.getWorldFolder();
        File targetDir = new File(worldContainer, to);

        Bukkit.getScheduler().runTaskAsynchronously(PizzaLib.plugin, () -> {
            try {
                if (!sourceDir.exists())
                    throw new IllegalStateException("Source world folder not found");

                for (File file : Objects.requireNonNull(sourceDir.listFiles())) {
                    String fileName = file.getName();

                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(file, new File(targetDir, fileName));
                    } else if (!fileName.contains("session") && !fileName.contains("uid.dat")) {
                        FileUtils.copyFile(file, new File(targetDir, fileName));
                    }
                }

                WorldCreator creator = new WorldCreator(to)
                        .keepSpawnLoaded(TriState.FALSE);

                Bukkit.getScheduler().runTask(PizzaLib.plugin, () -> {
                    try {
                        future.complete(creator.createWorld());
                    } catch (Exception e) {
                        PizzaLib.plugin.getLogger().severe("Failed to create world '" + to + "'");
                        future.completeExceptionally(e);
                    }
                });
            } catch (Exception e) {
                PizzaLib.plugin.getLogger().severe("Failed to copy world files from '" + from + "'");
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public static void deleteWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            PizzaLib.plugin.getLogger().warning("World not found '" + worldName + "'");
            return;
        }

        World mainWorld = Bukkit.getWorlds().getFirst();
        if (world.equals(mainWorld)) {
            PizzaLib.plugin.getLogger().severe("Cannot delete main world '" + worldName + "'");
            return;
        }

        world.getPlayers().forEach(player ->
                player.teleport(mainWorld.getSpawnLocation())
        );

        if (!Bukkit.unloadWorld(world, false)) {
            PizzaLib.plugin.getLogger().severe("Failed to unload world '" + worldName + "'");
            return;
        }

        File worldDir = new File(Bukkit.getWorldContainer(), worldName);
        if (worldDir.exists() && worldDir.isDirectory()) {
            Bukkit.getScheduler().runTaskAsynchronously(PizzaLib.plugin, () -> {
                try {
                    FileUtils.deleteDirectory(worldDir);
                } catch (Exception e) {
                    PizzaLib.plugin.getLogger().severe("Failed to delete world folder '" + worldName + "'");
                }
            });
        }
    }
}
