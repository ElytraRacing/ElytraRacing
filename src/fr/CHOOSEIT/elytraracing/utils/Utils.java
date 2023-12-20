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

package fr.CHOOSEIT.elytraracing.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.GUI.HeadDatabase;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.PlayerInformation.GamePlayer;
import fr.CHOOSEIT.elytraracing.addon.particle.file.ParticleConfig;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.gamesystem.PlayerMode;


import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import fr.CHOOSEIT.elytraracing.packetHandler.ClassGetter;
import fr.CHOOSEIT.elytraracing.packetHandler.PacketHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

public class Utils {


    public static boolean isInteger(String s){
        try {
            int i = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    public static boolean isFloat(String s){
        try {
            float i = Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    public static boolean isDouble(String s){
        try {
            double i = Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    public static boolean isInteger(String[] s){
        try {
            for (String s1 : s) {
                int i = Integer.parseInt(s1);
            }
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    public static boolean isFloat(String[] s){
        try {
            for (String s1 : s){
                float i = Float.parseFloat(s1);
            }

            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    public static boolean isDouble(String[] s){
        try {
            for (String s1 : s) {
                double i = Double.parseDouble(s1);
            }
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public static void setCustomModelData(ItemMeta im, int modeldata) {
        if (Main.getVersion() >= 14) {
            try {
                Method m = im.getClass().getDeclaredMethod("setCustomModelData", Integer.class);
                m.setAccessible(true);
                m.invoke(im, modeldata);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean hasCustomModelData(ItemMeta im) {
        if (Main.getVersion() >= 14) {
            try {
                Method m = im.getClass().getDeclaredMethod("hasCustomModelData");
                m.setAccessible(true);
                return (boolean) m.invoke(im);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static int getCustomModelData(ItemMeta im) {
        if (Main.getVersion() >= 14) {
            try {
                Method m = im.getClass().getDeclaredMethod("getCustomModelData");
                m.setAccessible(true);
                return (int) m.invoke(im);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private static CustomMessageConfig cmc = Main.customMessageConfig;

    public static boolean IsInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static double distance(Location loc, Location loc2) {
        if (loc.getWorld().equals(loc2.getWorld())) {
            return loc.distance(loc2);
        }
        return Double.MAX_VALUE;
    }

    public static Integer min(int a, ArrayList<Integer> list) {
        if (list == null || list.size() == 0) {
            return a;
        }
        List<Integer> slist = new ArrayList<>(list);
        slist.add(a);
        Collections.sort(slist);
        return slist.get(0);
    }

    public static int clamp(int a, int value, int b){
        return Math.max(a, Math.min(value, b));
    }

    public static void SendTitle(Player player, String Title, String SubTitle, int fadein, int delay, int fadeout) {
        try {
            if (Title.isEmpty()) {
                Title = "§a";
            }
            if (Main.getVersion() >= 18) {

                Class<?> packetTitleClass = ClassGetter.ClientboundSetTitleTextPacket.getClassRight();
                Class<?> packetSubTitleClass = ClassGetter.ClientboundSetSubtitleTextPacket.getClassRight();
                Class<?> packetTitleAnimationClass = ClassGetter.ClientboundSetTitlesAnimationPacket.getClassRight();

                Class<?> CIChatBaseComponent = ClassGetter.IChatBaseComponent.getClassRight();
                Class<?> CChatSerializer = CIChatBaseComponent.getDeclaredClasses()[0];

                Method Ma = CChatSerializer.getMethod("a", String.class);
                Object VTITLE = Ma.invoke(CChatSerializer, "{\"text\": \"" + Title + "\"}");
                Object packetTitle = packetTitleClass.getConstructor(CIChatBaseComponent).newInstance(VTITLE);

                Object VSUBTITLE = Ma.invoke(CChatSerializer, "{\"text\": \"" + SubTitle + "\"}");
                Object packetSubTitle = packetSubTitleClass.getConstructor(CIChatBaseComponent).newInstance(VSUBTITLE);

                Object packetAnimation = packetTitleAnimationClass.getConstructor(int.class, int.class, int.class).newInstance(fadein, delay, fadeout);

                PacketHandler.sendPacket(player, new ArrayList<>(Arrays.asList(packetTitle, packetSubTitle, packetAnimation)));
            } else if (Main.getVersion() >= 16) {
                for (Method method : player.getClass().getMethods()) {
                    if (method.getName().equals("sendTitle")) {
                        try {
                            method.invoke(player, Title, SubTitle, fadein, delay, fadeout);
                        } catch (IllegalArgumentException e) {
                            continue;
                        }
                    }
                }
            } else {
                Class<?> packetclass = Main.instance.getVersionClass("net.minecraft.server.", ".PacketPlayOutTitle");
                Class<?> CEnumTitleAction = packetclass.getDeclaredClasses()[0];
                Object FTITLE = CEnumTitleAction.getField("TITLE").get(null);
                Object FSUBTITLE = CEnumTitleAction.getField("SUBTITLE").get(null);

                Class<?> CIChatBaseComponent = Main.instance.getVersionClass("net.minecraft.server.", ".IChatBaseComponent");
                Class<?> CChatSerializer = CIChatBaseComponent.getDeclaredClasses()[0];

                Method Ma = CChatSerializer.getMethod("a", String.class);
                Object VTITLE = Ma.invoke(CChatSerializer, "{\"text\": \"" + Title + "\"}");
                Object VSUBTITLE = Ma.invoke(CChatSerializer, "{\"text\": \"" + SubTitle + "\"}");

                Constructor<?> Constructor = packetclass.getConstructor(CEnumTitleAction, CIChatBaseComponent, int.class, int.class, int.class);
                Object packet1 = Constructor.newInstance(FTITLE, VTITLE, fadein, delay, fadeout);
                Object packet2 = Constructor.newInstance(FSUBTITLE, VSUBTITLE, fadein, delay, fadeout);

                PacketHandler.sendPacket(player, new ArrayList<>(Arrays.asList(packet1, packet2)));
            }


        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    public static boolean IsFloat(String s) {
        try {
            Float.parseFloat(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static float Distance(float[] coords1, float[] coords2) {
        return (float) Math.sqrt(Math.pow(coords2[0] - coords1[0], 2) + Math.pow(coords2[1] - coords1[1], 2) + Math.pow(coords2[2] - coords1[2], 2));
    }

    public static float Distance(double[] coords1, double[] coords2) {
        return (float) Math.sqrt(Math.pow(coords2[0] - coords1[0], 2) + Math.pow(coords2[1] - coords1[1], 2) + Math.pow(coords2[2] - coords1[2], 2));
    }

    public static float[] add(float[] vector, float x, float y, float z){
        return new float[]{vector[0] + x, vector[1] + y, vector[2] + z};
    }

    public static boolean IsDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static void ReloadShowHide(Player player) {
        if (!Main.customMessageConfig.SEPERATE_GAMES_HIDE_PLAYERS) {
            return;
        }
        for (Player ps : Bukkit.getOnlinePlayers()) {
            ps.hidePlayer(player);
            player.hidePlayer(ps);
        }
        Game game = Game.Find(player, PlayerMode.ALIVE);
        if (game == null) {
            game = Game.Find(player, PlayerMode.SPEC);
            if (game != null) {
                for (Player onlinePlayer : game.getPlayerList()) {
                    player.showPlayer(onlinePlayer);
                }
            } else {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (Game.Find(onlinePlayer, PlayerMode.ALIVE) == null && Game.Find(onlinePlayer, PlayerMode.SPEC) == null) {
                        player.showPlayer(onlinePlayer);
                        onlinePlayer.showPlayer(player);
                    }
                }
            }
        } else {
            for (Player onlinePlayer : game.getPlayerList()) {
                player.showPlayer(onlinePlayer);
                onlinePlayer.showPlayer(player);
            }
        }


    }

    public static ItemStack getItemStack(String material, List<String> materialspossibledefault, String name) {
        return getItemStack(material, materialspossibledefault, name, null);
    }

    public static ItemStack getItemStack(String material, String name){
        return getItemStack(material, name, null);
    }
    public static ItemStack getItemStack(String material, String name, String playername){
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta im = item.getItemMeta();
        if (material != null) {
            String m = material;
            if (m.startsWith("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubm")) {
                return HeadDatabase.head(m, name);
            } else {
                if (m.startsWith("/")) {
                    m = m.substring(1, m.length());
                    if(playername != null)
                        m = m.replace("{PLAYER}", playername);
                    String finalM = m;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), finalM);
                        }
                    }, 3);

                    return null;
                }
                short data = 0;
                int modeldata = -1;
                if (material.contains(":")) {
                    String[] s = material.split(":");

                    if (s.length == 3) {
                        modeldata = Integer.parseInt(s[2]);
                    }
                    if (Utils.IsInteger(s[1])) {
                        m = s[0];
                        data = Short.parseShort(s[1]);
                    } else {
                        m = null;
                    }
                }
                Material finalmaterial = Utils.getMaterial(m);
                if (finalmaterial != null) {
                    if (modeldata > -1 && Main.getVersion() > 14) {
                        Utils.setCustomModelData(im, modeldata);
                    }
                    im.setDisplayName(name);
                    item.setItemMeta(im);
                    item.setType(finalmaterial);
                    item.setDurability(data);
                    return item;
                }
            }
        }
        return null;
    }

    public static ItemStack getItemStack(String material, List<String> materialspossibledefault, String name, String playername) {
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        item.setItemMeta(im);
        ItemStack result = getItemStack(material, name, playername);
        if(result != null) return result;
        for (String s : materialspossibledefault) {
            ItemStack r = getItemStack(s, name, playername);
            if(r != null) return r;
        }
        return item;
    }

    public static void ApplyBoost(Player player, float Multi) {
        if (Multi == 0) {
            return;
        }
        if (Multi < 0) {
            Vector vector = player.getLocation().getDirection();
            vector = vector.normalize();
            vector = vector.multiply(-Multi);
            player.setVelocity(vector);
            return;
        }
        Vector vector = player.getLocation().getDirection();
        vector = vector.normalize();
        vector = vector.multiply(Multi);
        vector = vector.add(player.getVelocity());
        //player.setVelocity(new Vector().zero());
        player.setVelocity(vector);
    }

    public static String timediffToString(Long diff) {
        return TimediffToStringCustom(diff, Main.customMessageConfig.TIMEFORMAT);
    }

    public static String TimediffToStringCustom(Long diff, String FORMAT) {
        int minutes, seconds, milli;
        minutes = (int) (diff / 60000);
        diff -= minutes * 60000;
        seconds = (int) (diff / 1000);
        diff -= seconds * 1000;
        milli = Math.toIntExact(diff);
        String message = new String(FORMAT);

        StringBuilder seconds_text = new StringBuilder();
        if(seconds < 10 && minutes > 0){
            seconds_text.append("0");
        }
        seconds_text.append(Integer.toString(seconds));

        StringBuilder milli_text = new StringBuilder();
        if(milli < 10){
            milli_text.append("00");
        } else if (milli < 100) {
            milli_text.append("0");
        }
        milli_text.append(Integer.toString(milli));

        message = message.replace("{MINUTES}", Integer.toString(minutes)).replace("{SECONDS}", seconds_text.toString()).replace("{MILLISECONDS}", milli_text.toString());
        return message;
    }

    public static void spawnRandomFireWorks(Location loc, Game game) {
        if (Main.instance.getVersion() <= 9) {
            return;
        }
        org.bukkit.entity.Entity e = Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, EntityType.FIREWORK);
        Firework fw = (Firework) e;
        FireworkMeta fwm = fw.getFireworkMeta();

        Random r = new Random();
        int rand = r.nextInt(4) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        switch (rand) {
            case 1:
                type = FireworkEffect.Type.BALL;
                break;
            case 2:
                type = FireworkEffect.Type.BALL_LARGE;
                break;
            case 3:
                type = FireworkEffect.Type.BURST;
                break;
            case 4:
                type = FireworkEffect.Type.CREEPER;
                break;
            case 5:
                type = FireworkEffect.Type.STAR;
                break;
        }

        ArrayList<Color> colors = new ArrayList<>(Arrays.asList(Color.AQUA, Color.FUCHSIA, Color.AQUA, Color.BLACK, Color.GRAY, Color.GREEN, Color.BLUE, Color.LIME, Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.PURPLE, Color.RED, Color.SILVER, Color.WHITE, Color.YELLOW));
        int color1 = r.nextInt(colors.size());
        int color2 = r.nextInt(colors.size());
        Color c1 = colors.get(color1);
        Color c2 = colors.get(color2);

        FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(c1).withFade(c2).with(type).trail(false).build();


        fwm.addEffect(effect);

        int rp = r.nextInt(2) + 1;
        fwm.setPower(rp);

        fw.setFireworkMeta(fwm);
        fw.setFireTicks(10);

        try {
            e.getClass().getMethod("setSilent", boolean.class).invoke(e, true);
            fw.getClass().getMethod("setSilent", boolean.class).invoke(fw, true);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }


        Class clas = ClassGetter.PacketPlayOutEntityDestroy.getClassRight();

        try {
            Constructor cons = clas.getConstructor(int[].class);
            Object o = cons.newInstance(new int[]{fw.getEntityId()});
            Game _g = null;
            for (Player canseeplayers : loc.getWorld().getPlayers()) {
                game.getPlayersAbleToSee().forEach(p -> playSoundDispatcher(p.getSpigotPlayer(), e.getLocation(), "ENTITY_FIREWORK_LAUNCH", "ENTITY_FIREWORK_LAUNCH", "minecraft:entity.firework.launch", "minecraft:entity.firework.launch", cmc.SOUND_FIREWORKS_VOLUME, cmc.SOUND_FIREWORKS_PITCH));

                _g = Game.Find(canseeplayers, PlayerMode.SPEC);
                if (_g != null && _g == game) {
                    continue;
                }
                _g = Game.Find(canseeplayers);
                if (_g != null && _g == game) {
                    continue;
                }
                PacketHandler.sendPacket(o, canseeplayers);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    public static void ClearHeader(Player player) {
        if (!Main.customMessageConfig.TABLIST_GRANDPRIX) {
            return;
        }
        try {

            String headerfieldname = "header";
            String footerfieldname = "footer";
            if (Main.instance.getVersion() <= 12) {
                headerfieldname = "a";
                footerfieldname = "b";
            }
            Class<?> PacketPlayOutPlayerListHeaderFooter_class = ClassGetter.PacketPlayOutPlayerListHeaderFooter.getClassRight();
            Class<?> IChatBaseComponent_CLASS = ClassGetter.IChatBaseComponent.getClassRight();
            Object packet = null;
            Object message;
            Class<?> ChatSerializer = IChatBaseComponent_CLASS.getDeclaredClasses()[0];
            if (Main.instance.getVersion() <= 9) {
                message = ChatSerializer.getMethod("a", String.class).invoke(ChatSerializer, "{\"text\":\"\"}");
            } else {
                message = ChatSerializer.getMethod("a", String.class).invoke(ChatSerializer, "{\"text\":\"\"}");
            }
            if (Main.getVersion() >= 17) {
                packet = PacketPlayOutPlayerListHeaderFooter_class.getConstructor(IChatBaseComponent_CLASS, IChatBaseComponent_CLASS).newInstance(message, message);
                PacketHandler.sendPacket(packet, player);
                return;
            } else {
                packet = PacketPlayOutPlayerListHeaderFooter_class.getConstructor().newInstance();
                Field footerfield = packet.getClass().getDeclaredField(footerfieldname);
                footerfield.setAccessible(true);
                footerfield.set(packet, message);
                Field headerfield = packet.getClass().getDeclaredField(headerfieldname);
                headerfield.setAccessible(true);
                headerfield.set(packet, message);
                PacketHandler.sendPacket(packet, player);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void playSoundDispatcher(ArrayList<Player> players, Location location, String sound_10, String sound_10_default, String sound_11, String sound_11_default, float v1, float v2) {
        if (Main.instance.getVersion() == 10) {
            if (sound_10 == null) {
                return;
            }
            if (sound_10.isEmpty()) {
                //playing default sound because not defined
                try {
                    for (Player player : players) {
                        playSound(player, location, sound_10_default, v1, v2);
                    }
                } catch (Exception e) {

                }
            } else {
                //playing sound because defined
                try {
                    for (Player player : players) {
                        playSound(player, location, sound_10, v1, v2);
                    }
                } catch (Exception e) {
                    //playing default sound because not defined
                    try {
                        for (Player player : players) {
                            playSound(player, location, sound_10_default, v1, v2);
                        }
                    } catch (Exception e2) {

                    }
                }
            }

        } else if (Main.instance.getVersion() >= 11) {
            if (sound_11 == null) {
                return;
            }
            if (sound_11.isEmpty()) {
                //playing default sound because not defined
                try {
                    for (Player player : players) {
                        playSound(player, location, sound_11_default, v1, v2);
                    }
                } catch (Exception e) {

                }
            } else {
                //playing sound because defined
                try {
                    for (Player player : players) {
                        playSound(player, location, sound_11, v1, v2);
                    }
                } catch (Exception e) {
                    //playing default sound because not defined
                    try {
                        for (Player player : players) {
                            playSound(player, location, sound_11_default, v1, v2);
                        }
                    } catch (Exception e2) {

                    }
                }
            }
        }
    }

    public static void playSoundDispatcher(Player player, Location location, String sound_10, String sound_10_default, String sound_11, String sound_11_default, float v1, float v2) {
        playSoundDispatcher(new ArrayList<>(Arrays.asList(player)), location, sound_10, sound_10_default, sound_11, sound_11_default, v1, v2);
    }

    private static void playSound(Player player, Location location, String sound, float v1, float v2) throws Exception {
        if (!cmc.PLAY_SOUND) {
            return;
        }
        if (Main.instance.getVersion() == 10) {
            Class Soundcl = Class.forName("org.bukkit.Sound");
            Method m_playsound = Player.class.getMethod("playSound", Location.class, Soundcl, float.class, float.class);
            Object SOUNDOBJECT = Soundcl.getMethod("valueOf", String.class).invoke(Soundcl, sound);
            m_playsound.invoke(player, location, SOUNDOBJECT, v1, v2);
        } else if (Main.instance.getVersion() >= 11) {
            Class SoundCategorieclass = Class.forName("org.bukkit.SoundCategory");
            Method m = Player.class.getMethod("playSound", Location.class, String.class, SoundCategorieclass, float.class, float.class);
            Object master = SoundCategorieclass.getMethod("valueOf", String.class).invoke(SoundCategorieclass, "MASTER");
            m.invoke(player, location, sound, master, v1, v2);
        }
    }

    private static void sendBlockChange(Player p, Location var1, Material var2, byte var3){
        p.sendBlockChange(var1, var2, var3);
    }
    public static void AroundBlocks(Location location, Material m, Player p) {
        Location _l;
        _l = location.clone().add(1, 0, 0);
        if (_l.getBlock().getType() == Material.AIR) {
            sendBlockChange(p, _l, m, (byte) 0);
        }
        _l = location.clone().add(0, 0, 1);
        if (_l.getBlock().getType() == Material.AIR) {
            sendBlockChange(p, _l, m, (byte) 0);
        }
        _l = location.clone().add(-1, 0, 0);
        if (_l.getBlock().getType() == Material.AIR) {
            sendBlockChange(p, _l, m, (byte) 0);
        }
        _l = location.clone().add(0, 0, -1);
        if (_l.getBlock().getType() == Material.AIR) {
            sendBlockChange(p, _l, m, (byte) 0);
        }

        _l = location.clone().add(1, 1, 0);
        if (_l.getBlock().getType() == Material.AIR) {
            sendBlockChange(p, _l, m, (byte) 0);
        }
        _l = location.clone().add(0, 1, 1);
        if (_l.getBlock().getType() == Material.AIR) {
            sendBlockChange(p, _l, m, (byte) 0);
        }
        _l = location.clone().add(-1, 1, 0);
        if (_l.getBlock().getType() == Material.AIR) {
            sendBlockChange(p, _l, m, (byte) 0);
        }
        _l = location.clone().add(0, 1, -1);
        if (_l.getBlock().getType() == Material.AIR) {
            sendBlockChange(p, _l, m, (byte) 0);
        }

    }

    public static ArrayList<Float[]> drawLine(Location loc1, Location loc2) {
        return drawLine((float) loc1.getX(), (float) loc1.getY(), (float) loc1.getZ(), (float) loc2.getX(), (float) loc2.getY(), (float) loc2.getZ());
    }

    public static float[] rotateX(double angle_radians, float x, float y, float z) {
        float[] coords = new float[3];
        coords[0] = x;
        coords[1] = (float) (y * Math.cos(angle_radians) - z * Math.sin(angle_radians));
        coords[2] = (float) (y * Math.sin(angle_radians) + z * Math.cos(angle_radians));
        return coords;
    }

    public static float[] rotateY(double angle_radians, float x, float y, float z) {
        float[] coords = new float[3];
        coords[0] = (float) (x * Math.cos(angle_radians) + z * Math.sin(angle_radians));
        coords[1] = y;
        coords[2] = (float) (z * Math.cos(angle_radians) - x * Math.sin(angle_radians));
        return coords;
    }

    public static float[] rotateZ(double angle_radians, float x, float y, float z) {
        float[] coords = new float[3];
        coords[0] = (float) (x * Math.cos(angle_radians) - y * Math.sin(angle_radians));
        coords[1] = (float) (x * Math.sin(angle_radians) + y * Math.cos(angle_radians));
        coords[2] = z;
        return coords;
    }

    public static float[] rotate(ObjectClass objectClass, float x, float y, float z) {
        return rotate(objectClass.getXDegrees(), objectClass.getYDegrees(), objectClass.getZDegrees(), x, y, z);
    }
    public static float[] rotate(float angleRadiansX, float angleRadiansY, float angleRadiansZ, float x, float y, float z) {
        float[] coords = {x, y, z};
        coords = rotateX(angleRadiansX, coords[0], coords[1], coords[2]);
        coords = rotateY(angleRadiansY, coords[0], coords[1], coords[2]);
        coords = rotateZ(angleRadiansZ, coords[0], coords[1], coords[2]);
        return coords;
    }

    public static float[] rotate(ObjectClass objectClass, float[] coordsini) {
        return rotate(objectClass, coordsini[0], coordsini[1], coordsini[2]);
    }
    public static float[] rotate(float angleRadiansX, float angleRadiansY, float angleRadiansZ, float[] coordsini) {
        return rotate(angleRadiansX, angleRadiansY, angleRadiansZ, coordsini[0], coordsini[1], coordsini[2]);
    }

    public static ArrayList<Float[]> drawLine(float x1, float y1, float z1, float x2, float y2, float z2) {
        ArrayList<Float[]> locations = new ArrayList<>();
        Float[] vector = new Float[]{x2 - x1, y2 - y1, z2 - z1};
        float mag = (float) Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2) + Math.pow(vector[2], 2));
        vector = new Float[]{vector[0] / mag, vector[1] / mag, vector[2] / mag};
        int intmag = (int) Math.floor(mag);

        locations.add(new Float[]{x1 + vector[0], y1 + vector[1], z1 + vector[2]});
        for (int i = 1; i < intmag; i++) {
            locations.add(new Float[]{locations.get(i - 1)[0] + vector[0], locations.get(i - 1)[1] + vector[1], locations.get(i - 1)[2] + vector[2]});
        }
        return locations;
    }

    public static boolean existMaterial(String material) {
        try {
            Field m = Material.class.getDeclaredField((String) material);
            Material ma = (Material) m.get(null);
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }

    public static Material getMaterial(String material) {
        try {
            Field m = Material.class.getDeclaredField((String) material);
            Material ma = (Material) m.get(null);

            return ma;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    public static void ParticleListClickable(Player player, int id_order, String argument, String type, Map map) {
        StringBuilder pamsg = new StringBuilder();

        pamsg.append("{\"text\":\"" + cmc.basicsettingnoprefix(cmc.PARTICLE_LIST_ITEMS.replace("{item}", "DEFAULT"), player) + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + "/ermap map " + map.getName() + " " + type + " " + id_order + " " + argument + " " + "DEFAULT" + "\"}" +
                ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + cmc.basicsettingnoprefix(cmc.PARTICLE_LIST_ITEMS_HOVER.replace("{item}", "DEFAULT"), player) + "\"}}");
        for (int i = 0; i < Main.availableParticle.size(); i++) {
            pamsg.append(",{\"text\":\"" + cmc.basicsettingnoprefix(cmc.PARTICLE_LIST_ITEMS.replace("{item}", Main.availableParticle.get(i)), player) + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + "/ermap map " + map.getName() + " " + type + " " + id_order + " " + argument + " " + Main.availableParticle.get(i) + "\"}" +
                    ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + cmc.basicsettingnoprefix(cmc.PARTICLE_LIST_ITEMS_HOVER.replace("{item}", Main.availableParticle.get(i)), player) + "\"}}");

        }
        pamsg.append(",{\"text\":\"" + cmc.basicsettingnoprefix(cmc.PARTICLE_LIST_ITEMS.replace("{item}", "INVISIBLE"), null) + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + "/ermap map " + map.getName() + " " + type + " " + id_order + " " + argument + " " + "INVISIBLE" + "\"}" +
                ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + cmc.basicsettingnoprefix(cmc.PARTICLE_LIST_ITEMS_HOVER.replace("{item}", "INVISIBLE"), player) + "\"}}");

        String Base = "{\"text\":\"" + cmc.basicsettingnoprefix(cmc.PARTICLE_LIST, player) + "\"" + ", \"extra\":[" + pamsg + "]}";
        PacketHandler.sendPacketMessage(player, Base);
    }

    public static void sendParticle(Player player, String particle, float x, float y, float z, float offsetx, float offsety, float offsetz, int amount) {
        if (particle.equalsIgnoreCase("INVISIBLE")) {
            return;
        }
        player.spawnParticle(Particle.valueOf(particle), x, y, z, amount, 0, 0, 0, 0, null);
    }
    public static void sendParticle(Set<Player> list, String particle, float x, float y, float z, float offsetx, float offsety, float offsetz, int amount) {
        list.forEach(p -> sendParticle(p, particle, x, y, z, offsetx, offsety, offsetz, amount));
    }


    public static boolean isParticle(String particle){
        return Main.availableParticle.contains(particle);
    }
    public static boolean isColoredParticle(String particle){
        return ParticleConfig.listOfSupportedColoredParticle.contains(particle);
    }
    public static void sendColoredParticle(Player player, String particle, float x, float y, float z, double rcoef, double gcoef, double bcoef, int amount) {
        if (isColoredParticle(particle)) {
            if(Main.getVersion() >= 13){
                Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB((int) (rcoef*255), (int) (gcoef*255), (int) (bcoef*255)), 1);
                player.spawnParticle(Particle.valueOf(particle), x, y, z, amount, 0, 0, 0, 0, dust);
            }
            else
            {
                for (int i = 0; i < amount; i++)
                    player.spawnParticle(Particle.valueOf(particle), x, y, z, 0, rcoef, gcoef, bcoef);
            }
        }
    }
    public static void sendColoredParticle(Set<Player> list, String particle, float x, float y, float z, float offsetx, float offsety, float offsetz, int amount) {
        list.forEach(p -> sendColoredParticle(p, particle, x, y, z, offsetx, offsety, offsetz, amount));
    }

    public static void sendParticle(Player player, String particle, Location location, int amount) {
        sendParticle(player, particle, (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, amount);
    }

    public static String timediffToString(Long diff, boolean milliseconds) {
        if (milliseconds) {
            return timediffToString(diff);
        }
        int minutes, seconds;
        minutes = (int) (diff / 60000);
        diff -= minutes * 60000;
        seconds = (int) (diff / 1000);
        String message = Main.customMessageConfig.TIMEFORMAT_WM;
        message = message.replace("{MINUTES}", Integer.toString(minutes)).replace("{SECONDS}", Integer.toString(seconds));
        return message;
    }

    public static void sendActionbar(Player player, String s) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(s).create());
    }


    public static ItemStack name(ItemStack is, String s) {
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(s);
        is.setItemMeta(itemMeta);
        return is;
    }

    public static void log_print(String message){
        Bukkit.getLogger().log(Level.INFO, message);
    }
    public static void log_err(String message){
        Bukkit.getLogger().log(Level.SEVERE, message);
    }

    public static void ClearEVERYTHING(Player player) {
        player.getInventory().clear();
        ItemStack AIR = new ItemStack(Material.AIR);
        //player.setHealth(20);
        //player.setMaxHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        for (PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }
        player.getInventory().setHelmet(AIR);
        player.getInventory().setChestplate(AIR);
        player.getInventory().setLeggings(AIR);
        player.getInventory().setBoots(AIR);
        player.getInventory().setItemInOffHand(AIR);

    }

    public static void kickPlayer(Player player, String reason, String servername) {
        //verify everything if servername is not null or empty...
        if (!reason.isEmpty()) {
            player.sendMessage(reason);
        }
        if (servername != null && !servername.isEmpty()) {
            teleportAnotherServer(player, servername);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        player.kickPlayer("");
                    }
                }
            }, 20 * 3);

        } else {
            player.kickPlayer(reason);
        }

    }

    public static void teleportAnotherServer(Player player, String servername) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(servername);

        player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
    }

    public static class CommandExe {
        public String command = "";
        public int score;

        public CommandExe(String command, int score) {
            this.command = command;
            this.score = score;
        }

        public void ExeCOMMAND(Player player) {
            if (player == null) {
                return;
            }
            if (command.startsWith("[CONSOLE]")) {
                command = command.replace("[CONSOLE] /", "").replace("[CONSOLE]/", "");
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("{SELF_PLAYER}", player.getName()).replace("{SCORE}", String.valueOf(score)));
            } else if (command.startsWith("[PLAYER]")) {
                command = command.replace("[PLAYER] /", "").replace("[PLAYER]/", "");
                player.performCommand(command.replace("{SELF_PLAYER}", player.getName()).replace("{SCORE}", String.valueOf(score)));
            }
        }
    }
}
