package de.nononitas.plotborder;

import de.nononitas.plotborder.util.Metrics;
import de.nononitas.plotborder.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class PlotBorder extends JavaPlugin {

    public static final String PREFIX = "§ePlotBorder §7» §r";
    public static final HashMap<UUID, Integer> guiPage = new HashMap<>();
    private static PlotBorder plugin;
    public final String CONFIG_VERSION = "3";
    public final String CURRENT_VERSION = this.getDescription().getVersion();

    public static String getColoredConfigString(String section) {
        String coloredString = getPlugin().getConfig().getString(section);
        coloredString = color(coloredString);
        return coloredString;
    }

    private static void setMetaData(Player p, String metaKey, Object metaValue) {
        p.setMetadata(metaKey, new FixedMetadataValue(getPlugin(), metaValue));
    }

    private static Object getMetaData(Player player, String metaKey) {
        for (MetadataValue value : player.getMetadata(metaKey)) {
            return value.value();
        }
        throw new NullPointerException("Nothing found");
    }

    public static boolean hasPlayerCooldown(Player p) {
        if (p.hasMetadata("rand-cooldown")) {
            int dif = (int) (((long) getMetaData(p, "rand-cooldown") - System.currentTimeMillis()) / 1000L);
            return dif > 0;
        }
        return false;
    }

    public static int getCooldown(Player p) {
        if (hasPlayerCooldown(p)) {
            return (int) (((long) getMetaData(p, "rand-cooldown") - System.currentTimeMillis()) / 1000L);
        }

        return 0;
    }

    public static void addCooldown(Player p) {
        setMetaData(p, "rand-cooldown", System.currentTimeMillis() + (getPlugin().getConfig().getInt("cooldown") * 1000));
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static PlotBorder getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        String version = Bukkit.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf('v'));

        if (!version.contains("v1_14_R") && !version.contains("v1_13_R") && !version.contains("v1_15_R") &&
                !version.contains("v1_16_R") && !version.contains("v1_17_R") && !version.contains("v1_18_R") && !version.contains("v1_19_R") && !version.contains("v1_20_R") ) {
            this.getLogger().severe(ChatColor.RED + "Incompatible Version");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlotSquared") == null) {
            this.getLogger().info("§4Plugin disabled. Please install PlotSquared!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        String plotVersion = Bukkit.getPluginManager().getPlugin("PlotSquared").getDescription().getVersion();
        if (!plotVersion.startsWith("6.") && !plotVersion.startsWith("7.")) {
            this.getLogger().severe(ChatColor.RED + "Incompatible Plotsquared Version");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        createConfig();
        reloadConfig();
        Updater.updateConfig();
        initEvents();
        initCmds();
        initBstats();
        Updater.updatcheckConsole();


    }

    private void initEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new GUIListener(), this);
        pm.registerEvents(new Listener() {
            @EventHandler
            public void Listener(PlayerJoinEvent event) {
                Player p = event.getPlayer();
                if (p.hasPermission("plotborder.admin") && PlotBorder.getPlugin().getConfig().getBoolean("update-notify")) {
                    Updater.updatecheck(p, false);
                }
            }
        }, this);
    }

    private void initCmds() {

        this.getCommand("wall").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                Gui.openGui(Gui.Type.WALL, p, 0);
                guiPage.put(p.getUniqueId(), 0);
            }
            return true;
        });
        this.getCommand("rand").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                Gui.openGui(Gui.Type.BORDER, p, 0);
                guiPage.put(p.getUniqueId(), 0);
            }
            return true;
        });
        this.getCommand("plotborder").setExecutor(new PlotBorderCmd());
        this.getCommand("plotborder").setTabCompleter(new PlotBorderCmd());
    }

    private void createConfig() {
        File customConfigFile = new File(getDataFolder(), "config.yml");
        if (!customConfigFile.exists()) {
            this.saveDefaultConfig();
        }
    }

    private void initBstats() {
        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SingleLineChart("players", () -> Bukkit.getOnlinePlayers().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("servers", () -> 1));


    }


}