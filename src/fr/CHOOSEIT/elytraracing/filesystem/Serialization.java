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

package fr.CHOOSEIT.elytraracing.filesystem;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import fr.CHOOSEIT.elytraracing.PlayerInformation.GamePlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.gamesystem.AutoGames;
import fr.CHOOSEIT.elytraracing.gamesystem.AutoGamesSaver;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.gamesystem.GameDurationType;
import fr.CHOOSEIT.elytraracing.holograms.HologramsSaver;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.signssystem.SignsSaver;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


public class Serialization {

    public String IDENTIFIER = "%%__USER__%%";
    private Gson gson;
    public static CustomMessageConfig cmc;
    private File saveDIR_MAPS;
    private File saveDIR_SYSTEM;
    public File saveDIR_MessageConfig;

    public Serialization() {
        this.gson = createGsonInstance();

        saveDIR_MAPS = new File(Main.instance.getDataFolder(), "/maps/");
        saveDIR_SYSTEM = new File(Main.instance.getDataFolder(), "/system/");
        saveDIR_MessageConfig = new File(Main.instance.getDataFolder(), "/");
    }


    private Gson createGsonInstance() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    public String serialize(Object c) {
        return this.gson.toJson(c);
    }

    public <T> T deserialize(String json, Class<T> c, String filename) {
        try{
            return this.gson.<T>fromJson(json, c);
        }
        catch (JsonSyntaxException e)
        {
            logErrorSeparatorStart(filename);

            jsonErrorAnalyser(e, filename);
            logErrorExplainer(" ", filename);
            logErrorExplainer(" ", filename);
            logErrorExplainer("Cause: " + e.getCause().toString(), filename);

            logErrorSeparatorEnd(filename);

            logErrorExplainer("Complete error:", filename);
            throw e;
        }
    }
    public void logErrorSeparatorStart(String filename){
        logErrorExplainer("An error occured while loading the file: " + filename, filename);
        logErrorSeparatorEnd(filename);
    }
    public void logErrorSeparatorEnd(String filename){
        logErrorExplainer(" ", filename);
        logErrorExplainer(" ", filename);
        logErrorExplainer(" ", filename);
    }
    public void logErrorExplainer(String error, String filename){
        Utils.log_err("[ERROR - ElytraRacing File - "+filename+"] " + error+ "%n");
    }

    public void jsonErrorAnalyser(JsonSyntaxException e, String filename){
        if(e.getCause() instanceof MalformedJsonException){
            MalformedJsonException mje = (MalformedJsonException) e.getCause();
            String message = mje.getMessage();
            if(message.startsWith("Unterminated object")){
                logErrorExplainer("It seems that part of a variable/value/element is missing.", filename);
                logErrorExplainer(
                        "Sometimes caused by forgetting \",\" at the end of a line that have " +
                        "other elements following it.", filename);
                logErrorExplainer("Or it can happen that a \'}\' is missing at the end of an object initialization", filename);
            }
            else if(message.startsWith("Unterminated array")){
                logErrorExplainer("It seems that a \']\' is missing at the end of a list of elements", filename);
            }
            else if(message.startsWith("Expected ':'")){
                logErrorExplainer("It seems that a \':\' is missing between a variable name and its value", filename);
                logErrorExplainer("It may also be that there is a problem in the JSON format of an object", filename);
                logErrorExplainer("Example here: " +
                        "https://chooseit.gitbook.io/elytraracing/particlescreator/particle-informations#example-of-a-particle-rainbow-circle-following-player-rotation", filename);
                logErrorExplainer("Format Variable-Value: \"<Variable name>\":\"<Value>\"", filename);
            }
            else {
                unSupportedError(filename);
            }
        }
        else if(e.getCause() instanceof IllegalStateException){
            IllegalStateException ise = (IllegalStateException) e.getCause();
            if(ise.getMessage().startsWith("Expected BEGIN_ARRAY but was")){
                logErrorExplainer("It seems that a \'[\' is missing at the beginning of a list of elements", filename);
            }
            else if(ise.getMessage().startsWith("Expected BEGIN_OBJECT but was")){
                logErrorExplainer("It seems that a \'{\' is missing at the beginning of an object initialization", filename);
            }
            else if(ise.getMessage().startsWith("Expected a") && ise.getMessage().contains("but was")){
                logErrorExplainer("It seems that there is a Type error", filename);
                logErrorExplainer("Format JSON example:", filename);
                logErrorExplainer("\"Number\":1", filename);
                logErrorExplainer("\"Text\":\"Some Text\"", filename);
                logErrorExplainer("\"boolean(true/false)\":true", filename);
                logErrorExplainer("\"list of elements(have to be for the same type)\":[true, false, true, true]", filename);
            }
            else {
                unSupportedError(filename);
            }
        }
        else{
            unSupportedError(filename);
        }
    }

    public void unSupportedError(String filename){
        logErrorExplainer("Unsupported error, the file does not exist or it seems that some parts are missing", filename);
    }

    public String serialize(Map o) {
        return this.gson.toJson(o);
    }

    public Map deserialize(String json, String filename) {
        return deserialize(json, Map.class, filename);
    }

    public String serialize(CustomMessageConfig customMessageConfig) {
        return this.gson.toJson(customMessageConfig);
    }

    public CustomMessageConfig deserializeMessage(String json) {
        return deserialize(json, CustomMessageConfig.class, "Config.json");
    }

    public void LoadMessageConfig() {
        LoadMessageConfig(true);
    }

    public CustomMessageConfig LoadMessageConfig(boolean save) {
        final Serialization serialization = Main.instance.getSerialization();
        String json = FileUtils.loadContent(new File(saveDIR_MessageConfig, "Config.json"));
        CustomMessageConfig customMessageConfig = serialization.deserializeMessage(json);
        if (customMessageConfig != null) {
            if (save) {
                Main.customMessageConfig = customMessageConfig;
                saveMessageConfig();
            }
            return customMessageConfig;
        } else {
            if (save) {
                try {
                    Main.customMessageConfig = new CustomMessageConfig();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                saveMessageConfig();
            } else {
                try {
                    return new CustomMessageConfig();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void saveConfig(File fileDir, String fileName, Object object) {
        final Serialization serialization = Main.instance.getSerialization();
        final File file = new File(fileDir, fileName);
        final String json1 = serialization.serialize(object);
        FileUtils.saveFile(file, json1);
    }

    public void saveMap(Map map) {
        if (map == null) {
            return;
        }
        map.setIds();
        saveConfig(saveDIR_MAPS, map.getName() + ".json", map);
    }

    public void saveMessageConfig() {
        saveConfig(saveDIR_MessageConfig, "Config.json", Main.customMessageConfig);
    }

    public void saveAutoGames() {
        saveConfig(saveDIR_MessageConfig, "Autogames.json", Main.autoGamesSaver);
    }

    public void saveSigns() {
        saveConfig(saveDIR_SYSTEM, "Signs.json", Main.signsSaver);
    }

    public void saveHolograms() {
        saveConfig(saveDIR_MessageConfig, "Holograms.json", Main.hologramsSaver);
    }

    public void loadHolograms() {
        final Serialization serialization = Main.instance.getSerialization();
        String json = FileUtils.loadContent(new File(saveDIR_MessageConfig, "Holograms.json"));
        HologramsSaver hologramsSaver = serialization.deserialize(json, HologramsSaver.class, "Holograms.json");
        if (hologramsSaver != null) {
            hologramsSaver.init();
            Main.hologramsSaver = hologramsSaver;
            saveHolograms();
        } else {
            Main.hologramsSaver = new HologramsSaver();
            saveHolograms();
        }
    }

    public void loadAutoGames() {
        if (Main.autoGamesSaver != null && Main.autoGamesSaver.autoGamesList != null) {
            Game g;
            for (AutoGames autoGames : Main.autoGamesSaver.autoGamesList) {
                g = autoGames.getGame();
                if (g != null) {
                    g.setGameDurationType(GameDurationType.OTHER);
                    for (GamePlayer player : new ArrayList<>(g.getGamePlayers())) {
                        g.PlayerLeave(player, true);
                    }
                    g.Delete();
                }
            }
        }
        final Serialization serialization = Main.instance.getSerialization();
        String json = FileUtils.loadContent(new File(saveDIR_MessageConfig, "Autogames.json"));
        AutoGamesSaver autoGamesSaver = serialization.deserialize(json, AutoGamesSaver.class, "Autogames.json");
        if (autoGamesSaver != null) {
            autoGamesSaver.init();
            Main.autoGamesSaver = autoGamesSaver;
            saveAutoGames();
        } else {
            Main.autoGamesSaver = new AutoGamesSaver();
            saveAutoGames();
        }
    }

    public void loadSigns() {
        final Serialization serialization = Main.instance.getSerialization();
        String json = FileUtils.loadContent(new File(saveDIR_SYSTEM, "Signs.json"));
        SignsSaver signsSaver = serialization.deserialize(json, SignsSaver.class, "Signs.json");
        if (signsSaver != null) {
            signsSaver.init();
            Main.signsSaver = signsSaver;
            saveSigns();
        } else {
            signsSaver = new SignsSaver();
            Main.signsSaver = signsSaver;
            saveSigns();
        }
    }

    public ArrayList<File> listFilesForFolder(final File folder) {
        if (!folder.exists()) {
            return new ArrayList<>();
        }
        ArrayList<File> files = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                files.addAll(listFilesForFolder(fileEntry));
            } else {
                files.add(fileEntry);
            }
        }
        return files;
    }


    public void LoadMaps() {
        ArrayList<File> files = listFilesForFolder(saveDIR_MAPS);

        final Serialization serialization = Main.instance.getSerialization();
        String json;
        Map map;
        for (File file : files) {
            if (file.exists()) {
                if(file.getName().startsWith(".")) continue;
                json = FileUtils.loadContent(file);
                map = serialization.deserialize(json, file.getName());
                ConsoleCommandSender ccs = Bukkit.getServer().getConsoleSender();
                if (map != null) {
                    Utils.log_print("[ElytraRacing] -> Loading: " + map.getName());
                    if (Map.UUIDLoaded(map.uuid)) {

                        Utils.log_err("[ElytraRacing]" + "----------------------");
                        ccs.sendMessage(ChatColor.RED + "[ElytraRacing] -> UUID already loaded: " + map.uuid);
                        ccs.sendMessage(ChatColor.RED + "[ElytraRacing] -> Set the UUID of the map to null to generate a new UUID for the database. Map: " + map.getName());
                        Utils.log_err("[ElytraRacing]" + "----------------------");

                        Utils.log_err("");
                        continue;
                    }
                    Map.maplist.add(map);
                    if (map.getDifficulty() <= 0 && cmc != null) {
                        map.setDifficulty(cmc.MAP_DIFFICULTY_DEFAULT);

                    }
                    if (map.getUUID() == null || map.getUUID().isEmpty()) {
                        map.setUuid(UUID.randomUUID().toString());
                    }
                    if ( (map.getSimpleLocation_lobby() != null && Bukkit.getWorld(map.getSimpleLocation_lobby().getWorldname()) == null)) {
                        Utils.log_err("[ElytraRacing]" + "----------------------");
                        ccs.sendMessage(ChatColor.RED + "[ElytraRacing] -> Please load the following world: " + map.getSimpleLocation_lobby().getWorldname());
                        Utils.log_err("[ElytraRacing]" + "----------------------");
                    }
                    if ( (map.getSimpleLocation_end() != null && Bukkit.getWorld(map.getSimpleLocation_end().getWorldname()) == null)) {
                        Utils.log_err("[ElytraRacing]" + "----------------------");
                        ccs.sendMessage(ChatColor.RED + "[ElytraRacing] -> Please load the following world: " + map.getSimpleLocation_end().getWorldname());
                        Utils.log_err("[ElytraRacing]" + "----------------------");
                    }
                    if ( (map.getSimpleLocation_start() != null && Bukkit.getWorld(map.getSimpleLocation_start().getWorldname()) == null)) {
                        Utils.log_err("[ElytraRacing]" + "----------------------");
                        ccs.sendMessage(ChatColor.RED + "[ElytraRacing] -> Please load the following world: " + map.getSimpleLocation_start().getWorldname());
                        Utils.log_err("[ElytraRacing]" + "----------------------");
                    }
                    map.serializationVerifications();
                    saveMap(map);

                }
            }
        }

    }

    public void Delete(Map map) {
        File file = new File(saveDIR_MAPS, map.getName() + ".json");
        file.delete();
    }


}
