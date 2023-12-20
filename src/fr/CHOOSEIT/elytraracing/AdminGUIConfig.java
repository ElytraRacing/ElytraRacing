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

import fr.CHOOSEIT.elytraracing.filesystem.FileUtils;
import fr.CHOOSEIT.elytraracing.filesystem.Serialization;
import org.bukkit.entity.Player;

import java.io.File;

public class AdminGUIConfig {
    public static String filename = "AdminGUI.json";

    public void save() {
        Main.adminGUIConfig = this;
        Main.serialization.saveConfig(Main.serialization.saveDIR_MessageConfig, filename, this);
    }

    public static void load() {
        load(true);
    }

    public static void load(boolean save) {
        String json = FileUtils.loadContent(new File(Main.serialization.saveDIR_MessageConfig, filename));
        AdminGUIConfig adminGUIConfig = Main.serialization.deserialize(json, AdminGUIConfig.class, filename);
        if (adminGUIConfig != null) {
            if (save) {
                adminGUIConfig.save();
            }
            Main.adminGUIConfig = adminGUIConfig;
        } else if (save) {
            adminGUIConfig = new AdminGUIConfig();
            adminGUIConfig.save();
            Main.adminGUIConfig = adminGUIConfig;
        } else {
            Main.adminGUIConfig = new AdminGUIConfig();
        }
    }

    public String c(String MSG) {
        if (MSG == null) {
            return "";
        }
        return MSG.replaceAll("&", "§");
    }

    public String world = "World";
    public String id = "ID";
    public String map = "Map";
    public String type = "Type";
    public String normal = "normal";
    public String ifnext = "ifnext";
    public String name = "Name";
    public String linkto = "Linkto";
    public String rotations = "Rotations";
    public String shape = "Shape";
    public String radius = "Radius";
    public String valid = "Valid";
    public String enabled = "Enabled";
    public String checkpoints = "Checkpoint(s)";
    public String ends = "End(s)";
    public String aos = "Additional object(s)";
    public String locations = "Locations";
    public String start = "Start";
    public String end = "End";
    public String lobby = "Lobby";
    public String boostMultiplier = "Boost multiplier";
    public String fireworks = "Firework(s)";
    public String cpangle = "Checkpoint angle";
    public String laps = "Lap(s)";
    public String lap = "lap";
    public String lapMapLong = "Here you can modify the number of laps on this map";
    public String thereisare = "There is/are";
    public String inMapCp = "checkpoint(s) in the map";
    public String inMapEnd = "end(s) in the map";
    public String inMapAo = "additional object(s) in the map";
    public String clickModificationCp = "Click to modify a checkpoint";
    public String clickModificationEnd = "Click to modify an end";
    public String clickModificationAo = "Click to modify an additional object";
    public String currentDifficulty = "Current map difficulty";
    public String clickDifficulty = "Click here to modify the difficulty";
    public String respawnCpAngle = "Checkpoint respawn angle";
    public String respawnCpAngleL1 = "Click modify the angle used for the map";
    public String respawnCpAngleL2 = "Information:";
    public String PLAYER = "PLAYER";
    public String respawnCpAngleL3 = "- Use the angle that the player had when he entered the checkpoint";
    public String NEXT_CHECKPOINT = "NEXT_CHECKPOINT";
    public String Experimental = "Experimental";
    public String respawnCpAngleL4 = "- Create an angle to redirect the player toward the next checkpoint";
    public String respawnCpAngleL5 = "This feature is only working with maps that contains §c§l1 end and no linked checkpoints";
    public String clickHereDefine = "Click here to define";
    public String defaultParticleCP = "default particle for checkpoints";
    public String defaultParticleEnd = "default particle for ends";
    public String defaultParticleAO = "default particle for additional objects";
    public String mapAvailableTraining = "Map available for training";
    public String clickmapAvailableTraining = "Click here to add or remove this map of the training set";
    public String dbUUID = "Database UUID";
    public String clickDB = "Click here to clear saved informations in the database";
    public String podiumConfiguration = "Podium configuration";
    public String qPodium = "Is the podium system is activated for this map ?";
    public String qPodiumL1 = "Command to add a new location for the podium:";
    public String qPodiumL2 = "/ermap map [map] podium setlocation <placement... 1, 2, 3..>";
    public String qPodiumL3 = "Command to teleport a location of the podium:";
    public String qPodiumL4 = "/ermap map [map] podium tp <placement... 1, 2, 3..>";
    public String startHost = "Start an host";
    public String createHostMap = "Click here to create an host using this map";

    public String maxtimepossible = "Max. time possible";
    public String difficulty = "Difficulty";
    public String podium = "Podium";
    public String particles = "Particles";
    public String changeName = "Change name";
    public String confirm = "Confirm";
    public String cancel = "Cancel";
    public String currentLaps = "Current lap(s)";
    public String currentMaxTime = "Current max. time";
    public String currentMaxTimeModification = "Here you can modify the maximum possible time on the map";
    public String minute = "minute";
    public String divideMinutes = "Divise by 2 current minutes";
    public String doubleMinutes = "Double current minutes";
    public String firework = "firework";
    public String divideFirework = "Divise by 2 current fireworks";
    public String doubleFirework = "Double current fireworks";
    public String second = "second";
    public String divideSeconds = "Divise by 2 current seconds";
    public String doubleSeconds = "Double current seconds";
    public String changeHologramName = "Change name of the hologram";
    public String cmdChangeHologramName = "/erconfig holograms setname [current_name] [new_name]";
    public String changeMap = "Change map";
    public String changeHologramMap = "Change map associated to the hologram";
    public String cmdChangeHologramMap = "/erconfig holograms setmap [hologram] [map]";
    public String localisation = "Localisation";
    public String leftTeleported = "Left click to be teleported";
    public String clickTeleported = "Click to be teleported";
    public String shiftclickDefineLocation = "Shift click to define this location";
    public String rightTeleported = "Right click to be teleported";
    public String leftConfigure = "Left click to configure";
    public String rightChangeLocation = "Right click to change location";
    public String rightDefineLocation = "Right click to define location";
    public String refreshDisplay = "Refresh display";
    public String clickRefreshElement = "Click here to refresh displayed elements on the hologram";
    public String delete = "Delete";
    public String personalMaps = "Personal maps";
    public String clickCreateNewHolograms = "Click here to create a new hologram of this type";
    public String personalStats = "Personal stats";
    public String rankscore = "Rank score";
    public String rankmap = "Rank map";
    public String rankmapL1 = "You have to define a map for this hologram.";
    public String rankmapL2 = "/erconfig holograms setmap [hologram] [map]";
    public String rankwonr = "Rank won racemode";
    public String rankwong = "Rank won grandprix";
    public String deleteHologram = "Click here to delete this hologram";
    public String currentParticle = "Current particle";
    public String currentParticleLongMap = "Here you can see the current particle set for this map";
    public String clickChangeParticle = "Click here to change the particle";
    public String currentFireworkAmountMap = "Here you can modify the number of fireworks on the map";
    public String clickChangePage = "Click here to change page";
    public String particleAmount = "Particle amount";
    public String information = "Information";
    public String clickDefineParticleObject = "Click here to define the particle of the object";
    public String currentLocation = "Current location";
    public String currentParticleAmount = "Current particle amount";
    public String particleAmountLong = "Here you can modify the particle amount displayed";
    public String currentFireworksAmount = "Current firework amount";
    public String fireworksAmountLong = "Here you can modify the number of firework given by this object";
    public String leftclick = "Left click";
    public String shiftleftclick = "Shift left click";
    public String rightclick = "Right click";
    public String shiftrightclick = "Shift right click";
    public String currentRadius = "Current radius";
    public String radiusmodification = "Here you can modify the radius";
    public String helpMessage = "Help message";
    public String helpMessageL1 = "Click here to display the list of commands for ";
    public String helpMessageL2 = "this object that will automatically complete arguments";
    public String helpMessageL2Map = "this map that will automatically complete arguments";
    public String currentBoostMultiplier = "Current boost multiplier";
    public String boostMultiplierModification = "Here you can modify the boost multiplier applied to the player";
    public String closeMenu = "Click here to close this menu";
    public String linkWithYou = "Link this object with you";
    public String linkModificationL1 = "Click here to link this object with you and be able to modify it.";
    public String linkModificationL2 = "With this link you will be able";
    public String linkModificationL3 = "to modify this object using items";
    public String linkModificationL4 = "Left click to link the object.";
    public String linkModificationL5 = "Right click to get items";
    public String linkModificationL6 = "If you are too far from the object it will";
    public String linkModificationL7 = "automatically unlock you.";
    public String clickDelete = "CLick here to delete this object.";
    public String informationConfirmationDeleteAO = "Delete an additional object";
    public String informationConfirmationDeleteCP = "Delete a checkpoint";
    public String informationConfirmationDeleteEND = "Delete an end";
    public String createNew = "Create a new";
    public String clickCreateNew = "Click here to create a new";
    public String clickCreateNewCP = "checkpoint at your location";
    public String clickCreateNewAO = "additional object at your location";
    public String clickCreateNewEnd = "end at your location";
    public String isLocationLobby = "Is lobby location is defined ?";
    public String isLocationStart = "Is start location is defined ?";
    public String isLocationEnd = "Is end location is defined ?";
    public String locationLobby = "Lobby location";
    public String locationStart = "Start location";
    public String locationEnd = "End location";
    public String specAndWalls = "Spec and walls";
    public String qspecAndWalls = "Spec can pass through walls ?";
    public String clickChangeValue = "Click here to change the value";
    public String showMap = "Show map";
    public String clickShowMap = "Click here to show/hide this map";
    public String currentParticleSet = "Here you can see the current particle set";

    public String pageHologramPage = "Hologram configuration";
    public String pageAOCP = "List of additional objects";
    public String pageAOP = "Additional object configuration";
    public String pageConfirmationDeleteObject = "Confirmation of deletion";
    public String pageCPCP = "List of checkpoints";
    public String pageCPP = "Checkpoint configuration";
    public String pageECP = "List of ends";
    public String pageEP = "End configuration";
    public String pageHCP = "List of holograms";
    public String pageMCP = "List of maps";
    public String pageMS = "Map settings:";
}
