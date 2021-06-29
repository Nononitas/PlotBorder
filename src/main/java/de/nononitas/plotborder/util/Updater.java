package de.nononitas.plotborder.util;

import de.nononitas.plotborder.PlotBorder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Updater {
    private static final String urlString = "https://api.spigotmc.org/legacy/update.php?resource=72592";

    public static void updatecheck(CommandSender sender, boolean getFeedbackIfFalse) {
        if(sender.hasPermission("plotborder.admin")) {

            Bukkit.getScheduler().runTaskAsynchronously(PlotBorder.getPlugin(), () -> {
                try {
                    URL url = new URL(urlString);
                    String version = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream())).readLine();
                    Bukkit.getScheduler().runTask(PlotBorder.getPlugin(), () -> {
                        if(!version.equals(PlotBorder.getPlugin().CURRENT_VERSION)) {
                            sender.sendMessage(PlotBorder.prefix + "§cUpdate available: §l§e" + version + "§r§c! You are on §l§e" + PlotBorder.getPlugin().CURRENT_VERSION + "§r§c!");
                            sender.sendMessage(PlotBorder.prefix + "§cSpigot: https://www.spigotmc.org/resources/72592/");

                        } else if(getFeedbackIfFalse) {
                            sender.sendMessage(PlotBorder.prefix + "§cNo updates available!");
                        }
                    });


                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            });

        }


    }

    public static void updatcheckConsole() {
        Bukkit.getScheduler().runTaskAsynchronously(PlotBorder.getPlugin(), () -> {
            try {
                URL url = new URL(urlString);
                String version = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream())).readLine();
                Bukkit.getScheduler().runTask(PlotBorder.getPlugin(), () -> {
                    if(!version.equals(PlotBorder.getPlugin().CURRENT_VERSION)) {
                        Bukkit.getConsoleSender().sendMessage(PlotBorder.prefix + "§cUpdate available: §l§e" + version + "§r§c. You are on §l§e" + PlotBorder.getPlugin().CURRENT_VERSION + "§r§c!");
                        Bukkit.getConsoleSender().sendMessage(PlotBorder.prefix + "§cSpigot: https://www.spigotmc.org/resources/72592/");
                    }
                });

                updateConfig();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    private static void updateConfig() {

        if(!PlotBorder.getPlugin().getConfig().getString("version").equals(PlotBorder.getPlugin().CONFIG_VERSION)) {
            Bukkit.getScheduler().runTaskAsynchronously(PlotBorder.getPlugin(), () -> {
                try {
                    Bukkit.getScheduler().runTask(PlotBorder.getPlugin(), () -> {
                        Bukkit.getConsoleSender().sendMessage(PlotBorder.prefix + "§eNew config file created, please use §cconfig-" + PlotBorder.getPlugin().getConfig().getString("version") + ".yml§e and copy your written options in the new config.yml");
                    });

                    Files.copy(Paths.get(PlotBorder.getPlugin().getDataFolder() + "/config.yml"), Paths.get(PlotBorder.getPlugin().getDataFolder() + "/config-" + PlotBorder.getPlugin().getConfig().getString("version") + ".yml"), StandardCopyOption.REPLACE_EXISTING);

                    File file = new File(PlotBorder.getPlugin().getDataFolder(), "config.yml");
                    file.delete();
                    PlotBorder.getPlugin().saveDefaultConfig();
                    PlotBorder.getPlugin().reloadConfig();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
