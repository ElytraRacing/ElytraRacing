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

package fr.CHOOSEIT.elytraracing.mapsystem;

import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.api.elytraracing.viewer.ERMap;
import fr.CHOOSEIT.elytraracing.api.elytraracing.viewer.erObject.ERAdditionalObject;
import fr.CHOOSEIT.elytraracing.api.elytraracing.viewer.erObject.ERCheckPoint;
import fr.CHOOSEIT.elytraracing.api.elytraracing.viewer.erObject.EREnd;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.gamesystem.PlayerInformationSaver;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.AdditionalObject;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.CheckPoint;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.End;
import fr.CHOOSEIT.elytraracing.mapsystem.linkplayer.LinkPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public class Map implements ERMap {
    public static CustomMessageConfig cmc = Main.customMessageConfig;
    public static ArrayList<Map> maplist = new ArrayList<>();


    public int getMaxPossibleN() {
        int maxn = getCpNumber() * getNumberoflaps() + 1;
        int maxpossiblen = 99999 + maxn;
        return maxpossiblen;
    }

    public void serializationVerifications(){
        forEachObjectClass(ObjectClass::serializationVerifications);
    }


    public static boolean UUIDLoaded(String UUID) {
        for (Map map : maplist) {
            if (map.uuid.equals(UUID)) {
                return true;
            }
        }
        return false;
    }

    public static Map createMap(Player creator, String name) {
        for (Map m : maplist) {
            if (m.getName().equalsIgnoreCase(name)) {
                if (creator != null) {
                    creator.sendMessage(cmc.basicsetting(cmc.MSG_MAPNAMEEXIST, creator));
                }
                return null;
            }
        }
        return new Map(name);
    }

    public int getCpNumber() {
        return getCheckpointsList().size();
    }

    public int getEndNumber() {
        return getEndsList().size();
    }

    private Map(String name) {
        this.name = name;
        MAP_TIME_MAX_SECONDS = 30;
        MAP_TIME_MAX_MINUTES = 1;
        location_end = null;
        location_start = null;
        location_lobby = null;
        difficulty = cmc.MAP_DIFFICULTY_DEFAULT;
        uuid = UUID.randomUUID().toString();
        maplist.add(this);
    }

    public String getlapsMessage() {
        return cmc.basicsetting(cmc.MSG_NUMBER_LAP.replace("{LAPS}", numberoflaps + ""), null);
    }


    public long getMaxTime() {
        return (MAP_TIME_MAX_SECONDS * 1000 + MAP_TIME_MAX_MINUTES * 60000);
    }

    public void ShowCheckpoints(Player player, Game game, long deltaTime) {
        ArrayList<Player> arr = new ArrayList<>();
        arr.add(player);
        ShowCheckpoints(arr, game, null, deltaTime);
    }

    public void ShowCheckpoints(Player player, Game game, ArrayList<Integer> cpToShowPrimary, long deltaTime) {
        ArrayList<Player> arr = new ArrayList<>();
        arr.add(player);
        ShowCheckpoints(arr, game, cpToShowPrimary, deltaTime);
    }

    public void setFireworks(int fireworks) {
        if (fireworks > 64) {
            fireworks = 64;
        }
        if (fireworks < 0) {
            fireworks = 0;
        }
        this.fireworks = fireworks;
    }

    public void ShowEverything(Player player, boolean ingame, long deltaTime) {
        ShowEverything(new ArrayList<Player>(Arrays.asList(player)), ingame, deltaTime);
    }

    public void ShowEverything(ArrayList<Player> player, boolean ingame, long deltaTime) {
        if (!ingame) {
            Location ploc;
            Location oloc;
            LinkPlayer.LinkPlayerInstance linkPlayer;
            for (Player player1 : player) {
                if (LinkPlayer.existLink(player1)) {
                    linkPlayer = LinkPlayer.getLinkPlayer(player1);
                    ploc = player1.getLocation();
                    oloc = linkPlayer.getObjectClass().getLocation();
                    if (Utils.distance(ploc, oloc) >= cmc.UNLOCK_DISTANCE) {
                        player1.sendMessage(cmc.basicsetting(cmc.UNLOCK_OBJECT, player1));
                        LinkPlayer.delete(player1);
                        return;
                    }
                    Utils.drawLine(oloc, ploc).forEach(floats -> Utils.sendParticle(player1, "SPELL_INSTANT", floats[0], floats[1], floats[2], 0f, 0f, 0f, 1));
                }
            }
        }
        Checkpoints.forEach(checkPoint -> checkPoint.show(player, default_particle_effect_random, default_particle_effect_random_ifnext, false, deltaTime));
        Ends.forEach(checkPoint -> checkPoint.show(player, default_particle_effect_end, default_particle_effect_end_ifnext, false, deltaTime));
        if (default_particle_effect_additonalObject == null) {
            default_particle_effect_additonalObject = "SMOKE_LARGE";
            save();
        }
        if (default_particle_effect_additonalObject_ifnext == null) {
            default_particle_effect_additonalObject_ifnext = "SPELL_INSTANT";
            save();
        }
        getAdditionalObjectsList().forEach(o -> o.show(player, default_particle_effect_additonalObject, default_particle_effect_additonalObject_ifnext, false, deltaTime));
    }

    public ArrayList<AdditionalObject> getAdditionalObjectsList() {
        if (AdditionalObjects == null) {
            AdditionalObjects = new ArrayList<>();
            save();
        }
        return AdditionalObjects;
    }

    public ArrayList<ERAdditionalObject> getAdditionalObjects() {
        ArrayList<ERAdditionalObject> o = new ArrayList<>();
        if (AdditionalObjects == null) {
            return o;
        }
        for (AdditionalObject ad : AdditionalObjects) {
            o.add(ad);
        }
        return o;
    }

    public String getDefault_particle_effect_additonalObject() {
        return default_particle_effect_additonalObject;
    }


    public String getDefault_particle_effect_additonalObject_ifnext() {
        return default_particle_effect_additonalObject_ifnext;
    }

    public void setDefault_particle_effect_additonalObject(String default_particle_effect_additonalObject) {
        this.default_particle_effect_additonalObject = default_particle_effect_additonalObject;
    }

    public void setDefault_particle_effect_additonalObject_ifnext(String default_particle_effect_additonalObject_ifnext) {
        this.default_particle_effect_additonalObject_ifnext = default_particle_effect_additonalObject_ifnext;
    }

    public void ShowCheckpoints(ArrayList<Player> player, Game game, ArrayList<Integer> cpToShowPrimary, long deltaTime) {
        final Map map = this;
        if (game == null) {
            ShowEverything(player, false, System.currentTimeMillis());
            return;
        }
        if (cpToShowPrimary == null) {
            int currcp;
            PlayerInformationSaver pis = game.getPlayerInformationSaver();
            ArrayList<Player> finishedplayer = new ArrayList<>();
            for (Player player1 : player) {

                if (!game.getPlayerInformationSaver().hasFinished(player1) && !game.getSpecList().contains(player1)) {
                    //Show additional objects
                    for (int i = 0; i < getAdditionalObjectsList().size(); i++) {
                        AdditionalObjects.get(i).show(player1, default_particle_effect_additonalObject, default_particle_effect_additonalObject_ifnext, !pis.hasPassedAdditionalObject(player1, i), deltaTime);
                    }
                    //Show cp and end
                    currcp = game.getPlayerInformationSaver().getCurrentCP(player1);
                    CheckPoint cp;
                    for (int i = 0; i < Checkpoints.size(); i++) {
                        cp = Checkpoints.get(i);
                        cp.show(player1, default_particle_effect_random, default_particle_effect_random_ifnext, currcp == i || (cp.getLinkedTo_id_order(map) != null && cp.getLinkedTo_id_order(map).contains(currcp)), deltaTime);
                    }
                    Ends.forEach(o -> o.show(player1, default_particle_effect_end, default_particle_effect_end_ifnext, false, deltaTime));
                } else {
                    finishedplayer.add(player1);
                }
            }
            ShowEverything(finishedplayer, true, deltaTime);
        } else {
            int currcp;
            PlayerInformationSaver pis = game.getPlayerInformationSaver();
            ArrayList<Player> finishedplayer = new ArrayList<>();
            for (Player player1 : player) {

                if (!game.getPlayerInformationSaver().hasFinished(player1) && !game.getSpecList().contains(player1)) {
                    //Show additional objects
                    for (int i = 0; i < getAdditionalObjectsList().size(); i++) {
                        AdditionalObjects.get(i).show(player1, default_particle_effect_additonalObject, default_particle_effect_additonalObject_ifnext, !pis.hasPassedAdditionalObject(player1, i), deltaTime);
                    }
                    //Show cp and end
                    currcp = game.getPlayerInformationSaver().getCurrentCP(player1);
                    CheckPoint cp;
                    CheckPoint cpLinked;
                    for (Integer integer : cpToShowPrimary) {
                        if (integer >= Checkpoints.size()) {
                            continue;
                        }
                        cp = Checkpoints.get(integer);
                        cp.show(player1, default_particle_effect_random, default_particle_effect_random_ifnext, currcp == integer || (cp.getLinkedTo_id_order(map) != null && cp.getLinkedTo_id_order(map).contains(currcp)), deltaTime);
                        for (int i = 0; i < cp.getLinkedTo_id_order(this).size(); i++) {
                            cpLinked = Checkpoints.get(i);
                            cpLinked.show(player1, default_particle_effect_random, default_particle_effect_random_ifnext, currcp == integer || (cpLinked.getLinkedTo_id_order(map) != null && cpLinked.getLinkedTo_id_order(map).contains(currcp)), deltaTime);
                        }
                    }

                    Ends.forEach(o -> o.show(player1, default_particle_effect_end, default_particle_effect_end_ifnext, false, deltaTime));
                } else {
                    finishedplayer.add(player1);
                }
            }
            ShowEverything(finishedplayer, true, deltaTime);
        }


    }

    public static Map Find(String name) {
        for (Map m : maplist) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public boolean isValid() {
        if (Ends.size() > 0 && location_start != null && location_end != null && location_lobby != null) {
            if (location_lobby.getlocation().getWorld() != null && location_start.getlocation().getWorld() != null && location_end.getlocation().getWorld() != null) {
                return true;
            }

        }
        return false;
    }

    public boolean isAvailable() {
        if (isValid() && isEnabled()) {
            return true;
        }
        return false;
    }

    public End getEnd(int id) {
        if (id >= getEndsList().size()) {
            return null;
        }
        return getEndsList().get(id);
    }

    public CheckPoint getCheckpoint(int id) {
        if (id >= getCheckpointsList().size()) {
            return null;
        }
        return getCheckpointsList().get(id);
    }


    public String getName() {
        return name;
    }

    public ArrayList<CheckPoint> getCheckpointsList() {
        if (Checkpoints == null) {
            return new ArrayList<>();
        }
        return Checkpoints;
    }

    public ArrayList<ERCheckPoint> getCheckpoints() {
        ArrayList<ERCheckPoint> cps = new ArrayList<>();
        if (Checkpoints == null) {
            return cps;
        }
        for (CheckPoint checkpoint : Checkpoints) {
            cps.add(checkpoint);
        }
        return cps;
    }

    public void addCheckpoint(CheckPoint checkPoint) {
        Checkpoints.add(checkPoint);
    }

    public void addAdditionalObject(AdditionalObject additionalObject) {
        AdditionalObjects.add(additionalObject);
    }

    public void addEnds(End end) {
        Ends.add(end);
    }

    public ArrayList<EREnd> getEnds() {
        ArrayList<EREnd> ends = new ArrayList<>();
        if (Ends == null) {
            return ends;
        }
        for (End end : Ends) {
            ends.add(end);
        }
        return ends;
    }

    public ArrayList<End> getEndsList() {
        if (Ends == null) {
            return new ArrayList<>();
        }
        return Ends;
    }

    public void InsertCheckpoint(CheckPoint checkPoint, int id_order) {
        Checkpoints.add(id_order, checkPoint);
    }

    public Location getLobbyLocation() {
        if (location_lobby == null) {
            return null;
        }
        return location_lobby.getlocation();
    }

    public Location getStartLocation() {
        if (location_start == null) {
            return null;
        }
        return location_start.getlocation();
    }

    public Location getEndLocation() {
        if (location_end == null) {
            return null;
        }
        return location_end.getlocation();
    }

    public Location getLocation_lobby() {
        if (location_lobby == null) {
            return null;
        }
        if (cmc.TELEPORT_SOLVER) {
            return location_lobby.getlocation().clone().add(0, 0.5, 0);
        }
        return location_lobby.getlocation();
    }

    public SimpleLocation getSimpleLocation_lobby() {
        return location_lobby;
    }

    public SimpleLocation getSimpleLocation_end() {
        return location_end;
    }

    public SimpleLocation getSimpleLocation_start() {
        return location_start;
    }

    public Location getLocation_start() {
        if (location_start == null) {
            return null;
        }
        if (cmc.TELEPORT_SOLVER) {
            return location_start.getlocation().clone().add(0, 0.5, 0);
        }
        return location_start.getlocation();
    }

    public Location getLocation_end() {
        if (location_end == null) {
            return null;
        }
        if (cmc.TELEPORT_SOLVER) {
            return location_end.getlocation().clone().add(0, 0.5, 0);
        }
        return location_end.getlocation();
    }

    public void setLocation_lobby(Location location_lobby) {
        this.location_lobby = new SimpleLocation(location_lobby);
    }

    public void setLocation_start(Location location_start) {
        this.location_start = new SimpleLocation(location_start);
    }

    public void setLocation_end(Location location_end) {
        this.location_end = new SimpleLocation(location_end);
    }

    public void setMAP_TIME_MAX_MINUTES(int MAP_TIME_MAX_MINUTES) {
        if (MAP_TIME_MAX_MINUTES < 0) {
            MAP_TIME_MAX_MINUTES = 0;
        }
        this.MAP_TIME_MAX_MINUTES = MAP_TIME_MAX_MINUTES;
    }

    public void setMAP_TIME_MAX_SECONDS(int MAP_TIME_MAX_SECONDS) {
        if (MAP_TIME_MAX_SECONDS > 59) {
            MAP_TIME_MAX_SECONDS = 59;
        }
        if (MAP_TIME_MAX_SECONDS < 0) {
            MAP_TIME_MAX_SECONDS = 0;
        }
        this.MAP_TIME_MAX_SECONDS = MAP_TIME_MAX_SECONDS;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public void setNumberoflaps(int numberoflaps) {
        this.numberoflaps = numberoflaps;
    }

    public int getNumberoflaps() {
        if (numberoflaps <= 0) {
            numberoflaps = 1;
            save();
        }
        return numberoflaps;
    }

    public boolean isEnabled() {
        return Enabled;
    }

    public int getMapTimeMinute() {
        return MAP_TIME_MAX_MINUTES;
    }

    public int getMapTimeSecond() {
        return MAP_TIME_MAX_SECONDS;
    }

    public void setPodium(boolean podium) {
        this.podium = podium;
    }

    public boolean isPodium() {
        return podium;
    }

    public void setLocations_podium(int placement, SimpleLocation loc) {
        if (locations_podium == null) {
            locations_podium = new ArrayList<>();
        }
        while (locations_podium.size() < placement) {
            locations_podium.add(null);
        }
        locations_podium.set(placement - 1, loc);
    }

    public Location getLocations_podium(int placement) {
        if (locations_podium == null) {
            locations_podium = null;
        }
        if (locations_podium.size() < placement) {
            return null;
        }
        return locations_podium.get(placement - 1).getlocation();
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getDifficultyMSG() {
        StringBuilder msg = new StringBuilder();
        if (cmc.MAP_DIFFICULTY_MAX == difficulty && cmc.MAP_DIFFICULTY_SYMBOL_MAX) {
            msg.append(cmc.basicsettingnoprefix(cmc.MAP_DIFFICULTY_SYMBOL_ACTIVECOLOR_MAX, null));
            for (int i = 0; i < cmc.MAP_DIFFICULTY_MAX; i++) {
                msg.append(cmc.basicsettingnoprefix(cmc.MAP_DIFFICULTY_SYMBOL, null));
            }
            return msg.toString();
        }
        for (int i = 0; i < cmc.MAP_DIFFICULTY_MAX; i++) {
            if (i < difficulty - 1) {
                msg.append(cmc.basicsettingnoprefix(cmc.MAP_DIFFICULTY_SYMBOL_ACTIVECOLOR, null));
            } else if (i == difficulty - 1) {
                msg.append(cmc.basicsettingnoprefix(cmc.MAP_DIFFICULTY_SYMBOL_ACTIVECOLOR_LAST, null));
            } else {
                msg.append(cmc.basicsettingnoprefix(cmc.MAP_DIFFICULTY_SYMBOL_COLOR, null));
            }
            msg.append(cmc.basicsettingnoprefix(cmc.MAP_DIFFICULTY_SYMBOL, null));
        }
        return msg.toString();
    }

    public static Map getMap(String UUID) {
        for (Map map : maplist) {
            if (map.getUUID().equals(UUID)) {
                return map;
            }
        }
        return null;
    }

    public static Map getMapName(String Name) {
        for (Map map : maplist) {
            if (map.getName().equalsIgnoreCase(Name)) {
                return map;
            }
        }
        return null;
    }

    public boolean isNextCheckpointAngleAvailable() {
        return getEndsList().size() == 1;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDifficulty(int difficulty) {
        if (difficulty > cmc.MAP_DIFFICULTY_MAX) {
            difficulty = cmc.MAP_DIFFICULTY_MAX;
        }
        if (difficulty < 1) {
            difficulty = 1;
        }
        this.difficulty = difficulty;
    }

    public ArrayList<SimpleLocation> getLocations_podium() {
        return locations_podium;
    }

    public int getFireworks() {
        return fireworks;
    }

    public CheckPoint.TYPE_ANGLE_CHECKPOINT getAngleType() {
        if (CHECKPOINT_ANGLE_TYPE == null) {
            return CheckPoint.TYPE_ANGLE_CHECKPOINT.PLAYER;
        }
        return CHECKPOINT_ANGLE_TYPE;
    }

    public void setAngleType(CheckPoint.TYPE_ANGLE_CHECKPOINT CHECKPOINT_ANGLE_TYPE) {
        this.CHECKPOINT_ANGLE_TYPE = CHECKPOINT_ANGLE_TYPE;
    }

    public void save() {
        Main.instance.getSerialization().saveMap(this);
    }

    public void setIds() {
        getCheckpointsList().forEach(o -> o.setID_JFI(this));
        getEndsList().forEach(o -> o.setID_JFI(this));
        getAdditionalObjectsList().forEach(o -> o.setID_JFI(this));
    }

    public String getDefault_particle_effect_end() {
        return default_particle_effect_end;
    }

    public String getDefault_particle_effect_end_ifnext() {
        return default_particle_effect_end_ifnext;
    }

    public String getDefault_particle_effect_random() {
        return default_particle_effect_random;
    }

    public String getDefault_particle_effect_random_ifnext() {
        return default_particle_effect_random_ifnext;
    }

    public void setDefault_particle_effect_end(String default_particle_effect_end) {
        this.default_particle_effect_end = default_particle_effect_end;
    }

    public void setDefault_particle_effect_end_ifnext(String default_particle_effect_end_ifnext) {
        this.default_particle_effect_end_ifnext = default_particle_effect_end_ifnext;
    }

    public void setDefault_particle_effect_random(String default_particle_effect_random) {
        this.default_particle_effect_random = default_particle_effect_random;
    }

    public void setDefault_particle_effect_random_ifnext(String default_particle_effect_random_ifnext) {
        this.default_particle_effect_random_ifnext = default_particle_effect_random_ifnext;
    }

    public boolean isNO_FAIL() {
        return NO_FAIL;
    }

    public boolean isInfiniteFireworks(){
        return infinite_fireworks;
    }

    private void forEachObjectClass(Consumer<ObjectClass> consumer){
        if(Checkpoints != null)
            Checkpoints.forEach(consumer);
        if(Ends != null)
            Ends.forEach(consumer);
        if(AdditionalObjects != null)
            AdditionalObjects.forEach(consumer);
    }

    public boolean canPassThroughWall() {
        return !spec_cant_pass_through_wall;
    }

    public void setSpecAbletopass(boolean can) {
        this.spec_cant_pass_through_wall = !can;
    }

    private String name;
    private boolean Enabled = false;

    private ArrayList<CheckPoint> Checkpoints = new ArrayList<>();
    private ArrayList<End> Ends = new ArrayList<>();
    private ArrayList<AdditionalObject> AdditionalObjects = new ArrayList<>();

    private int difficulty;

    private String default_particle_effect_random = "FLAME";
    private String default_particle_effect_end = "HEART";

    private String default_particle_effect_random_ifnext = "VILLAGER_HAPPY";
    private String default_particle_effect_end_ifnext = "HEART";

    private String default_particle_effect_additonalObject = "SMOKE_LARGE";
    private String default_particle_effect_additonalObject_ifnext = "SPELL_INSTANT";


    private boolean spec_cant_pass_through_wall;

    private SimpleLocation location_lobby;
    private SimpleLocation location_start;
    private SimpleLocation location_end;

    public CheckPoint.TYPE_ANGLE_CHECKPOINT CHECKPOINT_ANGLE_TYPE = CheckPoint.TYPE_ANGLE_CHECKPOINT.PLAYER;

    private int fireworks = 0;

    public String uuid;

    public boolean NO_FAIL = false; // If the player crash/ comeback1cp or comeback2cp it will force the player to restart

    private ArrayList<SimpleLocation> locations_podium = new ArrayList<>();
    private boolean podium = false;

    private int MAP_TIME_MAX_SECONDS;
    private int MAP_TIME_MAX_MINUTES;

    private int numberoflaps = 1;

    private boolean infinite_fireworks = false;

}
