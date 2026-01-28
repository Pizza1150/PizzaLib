package me.pizza.pizzalib.util;

import me.pizza.pizzalib.PizzaLib;
import net.kyori.adventure.util.TriState;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.codehaus.plexus.util.FileUtils;

public final class WorldUtil {

    private WorldUtil() {
    }

    private static final Queue<CloneTask> QUEUE = new ArrayDeque<>();
    private static final Set<String> CREATED_WORLDS = new HashSet<>();
    private static boolean RUNNING = false;

    public static CompletableFuture<World> cloneWorld(String from, String to) {
        CompletableFuture<World> future = new CompletableFuture<>();

        synchronized (QUEUE) {
            QUEUE.add(new CloneTask(from, to, future));
        }

        tryRunNext();
        return future;
    }

    public static void shutdown() {
        synchronized (QUEUE) {
            QUEUE.clear();
            RUNNING = false;
        }

        new HashSet<>(CREATED_WORLDS).forEach(WorldUtil::deleteWorld);
    }

    private static void tryRunNext() {
        synchronized (QUEUE) {
            if (RUNNING) return;

            CloneTask task = QUEUE.poll();
            if (task == null) return;

            RUNNING = true;
            execute(task);
        }
    }

    private static void finish() {
        synchronized (QUEUE) {
            RUNNING = false;
        }
        tryRunNext();
    }

    private static void execute(CloneTask task) {
        World fromWorld = Bukkit.getWorld(task.from);
        if (fromWorld == null) {
            task.future.completeExceptionally(
                    new IllegalStateException("World '" + task.from + "' is not loaded")
            );
            finish();
            return;
        }

        File sourceDir = fromWorld.getWorldFolder();
        File targetDir = new File(Bukkit.getWorldContainer(), task.to);

        if (targetDir.exists()) {
            task.future.completeExceptionally(
                    new IllegalStateException("World folder already exists '" + task.to + "'")
            );
            finish();
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(PizzaLib.plugin, () -> {
            try {
                if (!sourceDir.exists())
                    throw new IllegalStateException("Source world folder not found");

                for (File file : Objects.requireNonNull(sourceDir.listFiles())) {
                    String name = file.getName();

                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(file, new File(targetDir, name));
                    } else if (!name.contains("session") && !name.contains("uid.dat")) {
                        FileUtils.copyFile(file, new File(targetDir, name));
                    }
                }

                WorldCreator creator = new WorldCreator(task.to)
                        .keepSpawnLoaded(TriState.FALSE);

                Bukkit.getScheduler().runTask(PizzaLib.plugin, () -> {
                    try {
                        World world = creator.createWorld();
                        CREATED_WORLDS.add(task.to);
                        task.future.complete(world);
                    } catch (Throwable t) {
                        task.future.completeExceptionally(t);
                    } finally {
                        finish();
                    }
                });

            } catch (Throwable t) {
                task.future.completeExceptionally(t);
                finish();
            }
        });
    }

    public static void deleteWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            PizzaLib.plugin.getLogger().warning("World '" + worldName + "' not found");
            return;
        }

        World mainWorld = Bukkit.getWorlds().getFirst();
        if (world.equals(mainWorld)) {
            PizzaLib.plugin.getLogger().warning("Cannot delete main world");
            return;
        }

        world.getPlayers().forEach(p ->
                p.teleport(mainWorld.getSpawnLocation())
        );

        if (!Bukkit.unloadWorld(world, false)) {
            PizzaLib.plugin.getLogger().warning("Cannot unload world '" + worldName + "'");
            return;
        }

        File dir = new File(Bukkit.getWorldContainer(), worldName);
        if (!dir.exists()) return;

        try {
            FileUtils.deleteDirectory(dir);
        } catch (Exception ignored) {
            PizzaLib.plugin.getLogger().warning("Cannot delete world's folder '" + worldName + "'");
        } finally {
            CREATED_WORLDS.remove(worldName);
        }
    }

    private record CloneTask(String from, String to, CompletableFuture<World> future) {
    }
}
