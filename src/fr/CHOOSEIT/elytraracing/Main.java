/**
 * MIT License
 *
 * Copyright (c) 2023 CHOOSEIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fr.CHOOSEIT.elytraracing;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import fr.CHOOSEIT.elytraracing.PlayerInformation.GamePlayer;
import fr.CHOOSEIT.elytraracing.GUI.PageHandlerEvents;
import fr.CHOOSEIT.elytraracing.SqlHandle.DataBase;
import fr.CHOOSEIT.elytraracing.SqlHandle.ExternSqlHandle;
import fr.CHOOSEIT.elytraracing.SqlHandle.SqliteHandle;
import fr.CHOOSEIT.elytraracing.addon.particle.file.ParticleConfig;
import fr.CHOOSEIT.elytraracing.api.placeholderapi.PlaceholderAPIExpansions;
import fr.CHOOSEIT.elytraracing.autocompleter.TabCompleterER;
import fr.CHOOSEIT.elytraracing.autocompleter.TabCompleterERGAMES;
import fr.CHOOSEIT.elytraracing.autocompleter.TabCompleterERHOST;
import fr.CHOOSEIT.elytraracing.autocompleter.TabCompleterERMAP;
import fr.CHOOSEIT.elytraracing.event.*;
import fr.CHOOSEIT.elytraracing.filesystem.Serialization;
import fr.CHOOSEIT.elytraracing.gamesystem.*;
import fr.CHOOSEIT.elytraracing.holograms.Holograms;
import fr.CHOOSEIT.elytraracing.holograms.HologramsSaver;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.MapCommand;
import fr.CHOOSEIT.elytraracing.scoreboard.Board;
import fr.CHOOSEIT.elytraracing.scoreboard.BoardHandler;
import fr.CHOOSEIT.elytraracing.signssystem.SignsSaver;
import fr.CHOOSEIT.elytraracing.signssystem.SignsSystem;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class Main extends JavaPlugin {
    public static final Pattern hexpattern = Pattern.compile("#[a-fA-F0-9]{6}");
    public static Serialization serialization;
    public static Main instance;
    public static CustomMessageConfig customMessageConfig;
    public static ArrayList<String> availableParticle = new ArrayList<>();
    public static AutoGamesSaver autoGamesSaver;
    public static SignsSaver signsSaver;
    public static boolean WarningSTOP = false;

    public static HologramsSaver hologramsSaver;
    public static DataBase currentDataBase;

    public static AdminGUIConfig adminGUIConfig;
    public static ScoreboardConfig scoreboardConfig;


    public static boolean hookPlaceHolderAPI = false;
    public static boolean hookParties = false;
    public static boolean hookViaVersion = false;


    public static boolean oneGame() {
        return Main.cmc().ONEGAME;
    }

    public static boolean kickPlayerAfterGame() {
        return oneGame() && Main.cmc().ONEGAME_KICK_AFTER_GAME;
    }


    @Override
    public void onEnable() {
        super.onEnable();

        if (System.getProperty("file.encoding").equals("cp1252")) {
            System.setProperty("file.encoding", "UTF-8");
        }
        instance = this;
        this.serialization = new Serialization();

        AdminGUIConfig.load();
        ScoreboardConfig.load();
        serialization.LoadMessageConfig();
        serialization.LoadMaps();
        serialization.loadAutoGames();
        serialization.loadSigns();

        if (kickPlayerAfterGame() && Main.cmc().ONEGAME_LOBBYSERVER != null && !Main.cmc().ONEGAME_LOBBYSERVER.isEmpty()) {
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }
        if (Holograms.isAvailableVersion()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                @Override
                public void run() {
                    serialization.loadHolograms();
                }
            }, 1);

        }
        for (Particle value : Particle.values()) {
            try {
                Bukkit.getWorlds().get(0).spawnParticle(value, 0, 0, 0, 1, 0, 0, 0, 0, null);
                availableParticle.add(value.toString());
            } catch (Exception ignored) {
            }
        }
        if (!customMessageConfig.SQL_USE_CUSTOM_SQL) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            currentDataBase = new SqliteHandle(customMessageConfig);
        } else {
            currentDataBase = new ExternSqlHandle(customMessageConfig);
        }
        for (Map map : Map.maplist) {
            currentDataBase.SaveMap(map);
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            currentDataBase.SavePlayer(onlinePlayer);
        }
        Utils.log_print("[ElytraRacing] Looking for hooks...");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Utils.log_print("[ElytraRacing] PlaceHolderAPI enabled");
            new PlaceholderAPIExpansions(this, currentDataBase).register();
            hookPlaceHolderAPI = true;
        }
        if(Bukkit.getPluginManager().getPlugin("ViaVersion") != null){
            Utils.log_print("[ElytraRacing] ViaVersion enabled");
            hookViaVersion = true;
        }
        if (Bukkit.getPluginManager().getPlugin("Parties") != null && cmc().HOOK_PARTIES_ENABLE) {
            if (Bukkit.getPluginManager().getPlugin("Parties").isEnabled()) {
                Utils.log_print("[ElytraRacing] Parties enabled");
                hookParties = true;
            }
        }
        Utils.log_print("[ElytraRacing] Server version found: " + Main.instance.getVersion());
        Utils.log_print("[ElytraRacing] Available particles for your version: " + availableParticle);
        Utils.log_print("[ElytraRacing]  ");
        Utils.log_print("[ElytraRacing] Hi ! CHOOSEIT here thanks for using my plugin <3");
        Utils.log_print("[ElytraRacing]  ");

        if (oneGame() && cmc().ONEGAME_AUTOJOINGAME.isEmpty()) {
            Utils.log_print("[ERROR][ERROR][ERROR]");
            Utils.log_print("[ElytraRacing]  ");
            Utils.log_print("[ElytraRacing] \"OneGame\" is activated but the game to join is not set");
            Utils.log_print("[ElytraRacing]  ");
            Utils.log_print("[ERROR][ERROR][ERROR]");
        }

        int pluginId = 9797;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new Metrics.SimplePie("number_of_map_per_servers", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return String.valueOf(Map.maplist.size());
            }
        }));
        getCommand("erhost").setExecutor(new ERHostCommand());
        getCommand("erhost").setTabCompleter(new TabCompleterERHOST());

        getCommand("er").setExecutor(new ElytraRacingCommand());
        getCommand("er").setTabCompleter(new TabCompleterER());
        getCommand("ermap").setExecutor(new MapCommand());
        getCommand("ermap").setTabCompleter(new TabCompleterERMAP());
        getCommand("ergames").setExecutor(new ERGames());
        getCommand("ergames").setTabCompleter(new TabCompleterERGAMES());
        getCommand("erconfig").setExecutor(new ERConfig());

        getServer().getPluginManager().registerEvents(new PlayerMoveEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEvent(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageEvent(), this);
        getServer().getPluginManager().registerEvents(new PlaceBreakLiquidEvent(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageeEntityEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDrop(), this);
        getServer().getPluginManager().registerEvents(new FoodLevel(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveEvent(), this);
        getServer().getPluginManager().registerEvents(new OnCommand(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        getServer().getPluginManager().registerEvents(new SignsSystem(null), this);
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new PageHandlerEvents(), this);

        if (customMessageConfig.CUSTOM_AND_SEPARATED_CHAT) {
            getServer().getPluginManager().registerEvents(new PlayerOnChat(), this);
        }
        if (getVersion() > 9) {
            int intervalupdate = ((int) customMessageConfig.HOLOGRAMS_UPDATEINTERVAL / 1000) * 20;
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    Holograms.hologramslist.forEach(Holograms::update);
                }
            }, 0, intervalupdate);
        }
        if (Main.hologramsSaver == null) {
            Main.hologramsSaver = new HologramsSaver();
        }


        ParticleConfig.load();
    }

    public static CustomMessageConfig cmc() {
        return customMessageConfig;
    }

    @Override
    public void onDisable() {
        try {
            if (currentDataBase != null) {
                currentDataBase.getSqlConnection().close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        WarningSTOP = true;
        for (Game game : ((ArrayList<Game>) Game.gamelist.clone())) {
            game.setGameState(GameState.FINISHED);
            for (GamePlayer player : game.getPlayersAbleToSee()) {
                try {
                    PlayerSaver.SetIf(player.getSpigotPlayer(), false);
                    Bukkit.getOnlinePlayers().forEach(bp -> {
                        if (!bp.equals(player.getSpigotPlayer())) {
                            player.show(bp);
                            bp.showPlayer(player.getSpigotPlayer());
                        }
                    });
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }
        Holograms.hologramslist.forEach(Holograms::HardDelete);
        BoardHandler.boards.values().forEach(Board::delete);
    }

    public static int getVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        String p = version.split("_")[1];
        return Integer.parseInt(p);
    }
    public static int getSubVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        String[] split = version.split("_");
        if (split.length > 2){
            String s = split[2];
            String s1 = s.replaceAll("[^\\d.]", "");
            if (s1.length() > 0){
                return Integer.parseInt(s1);
            }
            return 0;
        }
        return 0;
    }

    public static Class<?> getVersionClass(String firstPart, String secondPart) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName(firstPart + version + secondPart);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getVersionClass() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getNMSClass(String name, String version) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int getVersion(Player p){
        if(hookViaVersion){
            ViaAPI api = Via.getAPI();
            int version = api.getPlayerVersion(p.getUniqueId());
            return version;
        }
        return -1;
    }
    public static boolean rightVersion(Player p){
        int playerVersion = getVersion(p);
        if(playerVersion == -1) return true;
        return playerVersion >= 107;
    }

    public Serialization getSerialization() {
        return serialization;
    }

}


