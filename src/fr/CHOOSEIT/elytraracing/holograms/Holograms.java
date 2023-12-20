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

package fr.CHOOSEIT.elytraracing.holograms;

import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.GUI.HeadDatabase;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.SqlHandle.DataBase;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.gamesystem.ElytraRacingCommand;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.packetHandler.ClassGetter;
import fr.CHOOSEIT.elytraracing.packetHandler.PacketHandler;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;
import java.util.*;


public class Holograms {
    CustomMessageConfig cmc = Main.customMessageConfig;
    public static ArrayList<Holograms> hologramslist = new ArrayList<>();

    public ArrayList<Entity> lines;
    public Location location;
    public HOLOGRAM_TYPE hologram_type;
    public Map map;
    public String name;

    public static enum HOLOGRAM_TYPE {
        PERSONAL_STATS,
        PERSONAL_MAPS,
        RANK_SCORE,
        RANK_WON_RACEMODE,
        RANK_WON_GRANDPRIX,
        RANK_MAP;
    }


    public static Holograms getHolo(String name) {
        for (Holograms holograms : hologramslist) {
            if (holograms.name.equalsIgnoreCase(name)) {
                return holograms;
            }
        }
        return null;
    }

    public Holograms(Location location, HOLOGRAM_TYPE hologram_type, Map map, String name) {
        if (Main.getVersion() <= 9) {

        }
        if (location.getWorld() == null) {
            return;
        }
        lines = new ArrayList<>();

        this.location = location;
        this.hologram_type = hologram_type;
        this.map = map;
        if (name == null) {
            this.name = UUID.randomUUID().toString().substring(0, 6);
        } else {
            this.name = name;
        }

        hologramslist.add(this);
        setup();
    }

    public void clear() {
        for (Entity entity : lines) {
            if (entity != null) {
                entity.remove();

            }

        }
        lines.clear();
    }

    public void delete() {
        clear();
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void setLocation(Location location) {
        this.location = location;

        clear();
        setup();
        update();


    }

    public void HardDelete() {
        for (Entity entity : lines) {
            if (entity != null) {
                entity.remove();

            }

        }
    }

    private void createarmorstand(String s) {
        if (Main.getVersion() <= 9) {
            return;
        }
        Location newloc = location.clone().add(0, -cmc.HOLOGRAMS_LINESPACE * lines.size(), 0);
        if (location.getWorld() == null) {
            return;
        }
        for (Entity nearbyEntity : location.getWorld().getNearbyEntities(newloc, 0.01, 0.01, 0.01)) {
            if (nearbyEntity.getType() == EntityType.ARMOR_STAND) {
                nearbyEntity.remove();
            }
        }
        Entity e = location.getWorld().spawnEntity(newloc, EntityType.ARMOR_STAND);
        lines.add(e);
        if (s.isEmpty()) {
            e.setCustomNameVisible(false);
        } else {
            e.setCustomName(s);
            e.setCustomNameVisible(true);
        }
        e.setInvulnerable(true);
        try {
            e.getClass().getMethod("setGravity", boolean.class).invoke(e, false);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        ArmorStand a = (ArmorStand) e;
        a.setVisible(false);
        a.setGravity(false);


    }

    public void setline(int line, String s) {

        if (line <= 0) {
            return;
        }
        if (lines.size() >= line) {
            if (Objects.requireNonNull(lines.get(line - 1).getCustomName()).equalsIgnoreCase(s)) {
                return;
            }
            lines.get(line - 1).setCustomName(s);
        } else {

            int tospawn = line - lines.size() - 1;
            for (int i = 0; i < tospawn; i++) {
                createarmorstand("");
            }
            createarmorstand(s);
        }
    }

    public void setup() {
        if (Main.getVersion() <= 9) {
            return;
        }
        if (hologram_type == HOLOGRAM_TYPE.PERSONAL_STATS) {
            for (int i = 0; i < cmc.HOLOGRAMS_PERSONALSTATS.length; i++) {
                createarmorstand(".");
            }
        }
    }

    public void update() {
        if (!isAvailableVersion()) {
            return;
        }
        switch (hologram_type) {
            case RANK_SCORE:
                Main.currentDataBase.UpdateScoreHolograms(this);
                break;
            case RANK_WON_RACEMODE:
                Main.currentDataBase.UpdateWonRacemodeHolograms(this);
                break;
            case RANK_WON_GRANDPRIX:
                Main.currentDataBase.UpdateWonGrandprixHolograms(this);
                break;
            case RANK_MAP:
                Main.currentDataBase.UpdateMapRankHolograms(this);
                break;
            case PERSONAL_STATS:
                personalupdate();
                break;
            case PERSONAL_MAPS:
                personalupdate_maps();
                break;

        }
    }
    public static boolean isAvailableVersion(){
        return !(Main.getVersion() <= 9 || Main.getVersion() >= 19);
    }
    private void setpersonalline(Player player, int line, String s) {
        if (!isAvailableVersion()) {
            return;
        }
        if (line <= 0) {
            return;
        }

        String base;


        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            Class<?> IChatBaseComponent_CLASS = ClassGetter.IChatBaseComponent.getClassRight();
            Class<?> ChatSerializer = IChatBaseComponent_CLASS.getDeclaredClasses()[0];

            Entity target = lines.get(line - 1);
            Object optchat_W;
            if (s != null) {
                base = "{\"text\":\"{MESSAGE}\"}";
                base = base.replace("{MESSAGE}", s);
                optchat_W = ChatSerializer.getMethod("a", String.class).invoke(ChatSerializer, base);
            } else {
                optchat_W = null;
            }


            Class<?> DataWatcher_class = ClassGetter.DataWatcher.getClassRight();
            Object dww_Handle = target.getClass().getMethod("getHandle").invoke(target);

            Object dww_DataWatcher = null;

            if (Main.getVersion() >= 18) {
                dww_DataWatcher = dww_Handle.getClass().getMethod("ai").invoke(dww_Handle);
            } else {
                dww_DataWatcher = dww_Handle.getClass().getMethod("getDataWatcher").invoke(dww_Handle);
            }


            Object optchat_F = Optional.ofNullable(optchat_W);
            Class<?> DataWatcherObject_CLASS = ClassGetter.DataWatcherObject.getClassRight();
            Class<?> DataWatcherRegistry_CLASS = ClassGetter.DataWatcherRegistry.getClassRight();
            Class<?> DataWatcherSerializer_CLASS = ClassGetter.DataWatcherSerializer.getClassRight();
            Class<?> PacketPlayOutEntityMetadata_class = ClassGetter.PacketPlayOutEntityMetadata.getClassRight();
            if (Main.instance.getVersion() >= 13) {
                Object f = DataWatcherRegistry_CLASS.getMethod("a", int.class).invoke(DataWatcherRegistry_CLASS, 6);
                Constructor constructorDatawatcherObject = DataWatcherObject_CLASS.getConstructor(int.class, DataWatcherSerializer_CLASS);
                Object instanceDatawatcherObject = constructorDatawatcherObject.newInstance(2, f);
                Method SetDataWatcher_class = null;
                if (Main.getVersion() >= 18) {
                    SetDataWatcher_class = DataWatcher_class.getMethod("b", DataWatcherObject_CLASS, Object.class);
                }
                for (int i = 0; i < 20; i++) {
                    if (DataWatcher_class.getMethods()[i].getName().equals("set")) {
                        SetDataWatcher_class = DataWatcher_class.getMethods()[i];
                    }
                }
                assert SetDataWatcher_class != null;
                SetDataWatcher_class.invoke(dww_DataWatcher, instanceDatawatcherObject, optchat_F);
                Constructor constructorpacket = PacketPlayOutEntityMetadata_class.getConstructor(int.class, dww_DataWatcher.getClass(), boolean.class);
                Object packet = constructorpacket.newInstance(target.getEntityId(), dww_DataWatcher, false);
                PacketHandler.sendPacket(packet, player);
            } else {
                base = s;
                if (Main.getVersion() <= 9) {
                    return;
                }
                Object d = DataWatcherRegistry_CLASS.getMethod("a", int.class).invoke(DataWatcherRegistry_CLASS, 4);
                Constructor constructorDatawatcherObject = DataWatcherObject_CLASS.getConstructor(int.class, DataWatcherSerializer_CLASS);
                Object instanceDatawatcherObject = constructorDatawatcherObject.newInstance(2, d);
                Method SetDataWatcher_class = null;
                for (int i = 0; i < 20; i++) {
                    if (DataWatcher_class.getMethods()[i].getName().equals("set")) {
                        SetDataWatcher_class = DataWatcher_class.getMethods()[i];
                    }
                }
                assert SetDataWatcher_class != null;
                SetDataWatcher_class.invoke(dww_DataWatcher, instanceDatawatcherObject, base);
                Constructor constructorpacket = PacketPlayOutEntityMetadata_class.getConstructor(int.class, dww_DataWatcher.getClass(), boolean.class);
                Object packet = constructorpacket.newInstance(target.getEntityId(), dww_DataWatcher, false);
                PacketHandler.sendPacket(packet, player);
            }

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }


    }

    public <T> Optional<T> getOptional(Class<T> type) {
        return Optional.empty();
    }

    public void personalupdate(Player player) {
        if (Main.getVersion() <= 9) {
            return;
        }
        DataBase dataBase = Main.currentDataBase;
        DataBase.Playerinfos playerinfos;
        int i;
        String[] perso = cmc.HOLOGRAMS_PERSONALSTATS;
        playerinfos = DataBase.Playerinfos.getPlayerInfos(dataBase.get_player_id(player));

        i = 1;
        for (String s : perso) {
            setpersonalline(player, i, cmc.basicsettingnoprefix(ElytraRacingCommand.Stats_message(player, s, playerinfos), player));
            i++;
        }
    }

    private void personalupdate() {
        if (Main.getVersion() <= 9) {
            return;
        }
        DataBase dataBase = Main.currentDataBase;
        DataBase.Playerinfos playerinfos;
        int i;
        String[] perso = cmc.HOLOGRAMS_PERSONALSTATS;
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerinfos = DataBase.Playerinfos.getPlayerInfos(dataBase.get_player_id(player));

            i = 1;
            for (String s : perso) {
                setpersonalline(player, i, cmc.basicsettingnoprefix(ElytraRacingCommand.Stats_message(player, s, playerinfos), player));

                i++;
            }

        }


    }

    public void personalupdate_maps(Player player) {
        if (Main.getVersion() <= 9) {
            return;
        }
        DataBase dataBase = Main.currentDataBase;
        DataBase.Playerinfos playerinfos;
        int i = 1;
        playerinfos = DataBase.Playerinfos.getPlayerInfos(dataBase.get_player_id(player));
        int max = cmc.HOLOGRAMS_PERSONAL_MAPS_MAX;
        String title = cmc.basicsettingnoprefix(ElytraRacingCommand.Stats_message(player, cmc.HOLOGRAMS_PERSONAL_MAPS_TITLE, playerinfos), player);
        String[] titlelist = title.split("\n");
        for (int i1 = 0; i1 <= lines.size(); i1++) {
            setpersonalline(player, i1, "");
        }
        for (String s : titlelist) {
            setline(i, ".");
            setpersonalline(player, i, s);
            i++;
        }

        String BaseMessage;
        Map m;
        for (DataBase.PlayerMapinfos playerMapinfo : playerinfos.playerMapinfos) {

            m = Map.getMap(playerMapinfo.map_uuid);
            if (m == null) {
                continue;
            }
            BaseMessage = cmc.HOLOGRAMS_PERSONAL_MAPS_SPECIFIC;
            BaseMessage = BaseMessage.replace("{WIN}", String.valueOf(playerMapinfo.win)).replace("{PLAYER_RANK}", String.valueOf(playerMapinfo.ranking)).replace("{MAP_NAME}", m.getName()).replace("{PLAYER_TIME}", Utils.timediffToString(playerMapinfo.player_time)).replace("{WINRATE}", String.valueOf(playerMapinfo.winrate));
            BaseMessage = cmc.basicsettingnoprefix(BaseMessage, player);
            setline(i + 1, ".");
            setpersonalline(player, i + 1, BaseMessage);

            i++;

            if (max == 1) {
                break;
            }
            max--;
        }


        i++;
    }

    private void personalupdate_maps() {
        if (Main.getVersion() <= 9) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            personalupdate_maps(player);

        }
    }

    public static ItemStack getHead(HOLOGRAM_TYPE hologram_type) {
        switch (hologram_type) {
            case PERSONAL_MAPS:
                return HeadDatabase.ORANGE_P;
            case PERSONAL_STATS:
                return HeadDatabase.BLUE_P;
            case RANK_MAP:
                return HeadDatabase.GREEN_M;
            case RANK_WON_RACEMODE:
                return HeadDatabase.RED_R;
            case RANK_SCORE:
                return HeadDatabase.PURPLE_S;
            case RANK_WON_GRANDPRIX:
                return HeadDatabase.YELLOW_G;
            default:
                return null;
        }
    }

    public static void HardCreate(Location location, Holograms.HOLOGRAM_TYPE hologram_type, Player sender) {
        HologramsCreator hc = (new HologramsCreator((float) location.getX(), (float) location.getY(), (float) location.getZ(), location.getWorld().getName(), hologram_type, null, null));
        Main.hologramsSaver.Holograms.add(hc);
        Main.serialization.saveHolograms();
        hc.create();

        hc.getHologram().update();
        if (sender != null) {

            sender.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().MSG_SAVE_SUCCESSFUL, sender));
            if (hologram_type.equals(HOLOGRAM_TYPE.RANK_MAP)) {
                sender.sendMessage("§cDefine a map for this hologram:");
                TextComponent cmd = new TextComponent("§c/erconfig holograms setmap " + hc.getName() + " [map]");
                cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/erconfig holograms setmap " + hc.getName()));
                sender.spigot().sendMessage(cmd);
            }
        }
    }
}
