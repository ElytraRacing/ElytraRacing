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

package fr.CHOOSEIT.elytraracing.GUI.Pages;

import fr.CHOOSEIT.elytraracing.AdminGUIConfig;
import fr.CHOOSEIT.elytraracing.GUI.HeadDatabase;
import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.Pages.MapPages.ParticleMapPage;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.*;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.MapCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapPage extends Page {
    public static AdminGUIConfig agui = Main.adminGUIConfig;
    private Map map;

    public MapPage(Player viewer, Map map) {
        this(viewer, map, Main.adminGUIConfig.c(Main.adminGUIConfig.pageMS) + " " + map.getName());
    }

    public MapPage(Player viewer, Map map, String name) {

        super(name, viewer);
        this.map = map;
    }

    @Override
    public void update(VisibleObject visibleObject) {
        if (visibleObject.getId().equals("MapInfo")) {
            List<String> l = new ArrayList<>();
            l.add("§f» §7" + agui.c(agui.name) + ": §6" + map.getName());
            l.add(" ");
            l.add("§f» §7" + agui.c(agui.valid) + ": " + Main.cmc().booleantoString(map.isValid()) + " " + "§f«» §7" + agui.c(agui.enabled) + ": " + Main.cmc().booleantoString(map.isEnabled()));
            l.add(" ");
            int n = map.getCpNumber();
            int n2 = map.getEndNumber();
            int n3 = map.getAdditionalObjectsList().size();
            l.add("§f» §7" + agui.c(agui.checkpoints) + ": §c" + n + " " + "§f«» §7" + agui.c(agui.ends) + ": §c" + n2 + " " + "§f«» §7" + agui.c(agui.aos) + ": §c" + n3);
            l.add("§f» §7" + agui.c(agui.locations) + ": " + "§f» §7" + agui.c(agui.lobby) + ": " + Main.cmc().booleantoString(map.getLocation_lobby() != null) + " " + "§f«» §7" + agui.c(agui.start) + ": " + Main.cmc().booleantoString(map.getLocation_start() != null) + " " + "§f«» §7" + agui.c(agui.end) + ": " + Main.cmc().booleantoString(map.getLocation_end() != null));
            l.add(" ");
            int intt = map.getMapTimeSecond();
            String s = "" + intt;
            if (intt < 10) {
                s = "0" + intt;
            }
            intt = map.getMapTimeMinute();
            String m = "" + intt;
            if (intt < 10) {
                m = "0" + intt;
            }
            l.add("§f» §7" + agui.c(agui.maxtimepossible) + ": §c" + m + "m:" + s + "s");
            l.add("§f» §7" + agui.c(agui.difficulty) + ": " + map.getDifficultyMSG());
            n = map.getFireworks();
            l.add("§f» §7" + agui.c(agui.fireworks) + ": §c" + n + " §f«» §7" + agui.c(agui.cpangle) + ": §c" + map.getAngleType());
            l.add("§f» §7" + agui.c(agui.podium) + ": " + Main.cmc().booleantoString(map.isPodium()));
            l.add("§f» §7" + agui.c(agui.laps) + ": §c" + map.getNumberoflaps());
            l.add(" ");

            l.add("§8» " + map.getUUID());
            visibleObject.setDescription(l);
            visibleObject.display();
        } else if (visibleObject.getId().startsWith("Location")) {
            Map map = getMap();
            SimpleLocation loc = null;
            String firstline = "";
            if (visibleObject.getId().equals("LocationLobby")) {
                loc = map.getSimpleLocation_lobby();
                firstline = "§f" + agui.c(agui.isLocationLobby) + ": ";
                visibleObject.setDisplayName("§7" + agui.c(agui.locationLobby) + "");
            } else if (visibleObject.getId().equals("LocationStart")) {
                loc = map.getSimpleLocation_start();
                firstline = "§f" + agui.c(agui.isLocationStart) + ": ";
                visibleObject.setDisplayName("§7" + agui.c(agui.locationStart) + "");
            } else if (visibleObject.getId().equals("LocationEnd")) {
                loc = map.getSimpleLocation_end();
                firstline = "§f" + agui.c(agui.isLocationEnd) + ": ";
                visibleObject.setDisplayName("§7" + agui.c(agui.locationEnd) + "");
            }


            boolean is = loc != null;

            List<String> desc = new ArrayList<>(Arrays.asList(firstline + Main.cmc().booleantoString(is)));
            if (is) {
                desc.add(" ");
                desc.add("§7" + agui.c(agui.world) + " = " + loc.getWorldname());
                desc.add("§7x = " + loc.getX());
                desc.add("§7y = " + loc.getY());
                desc.add("§7z = " + loc.getZ());
                desc.add("§7yaw = " + loc.getYeild() + ", pitch= " + loc.getPitch());
                desc.add(" ");
                desc.add("§f" + agui.c(agui.clickTeleported) + "");
            }
            desc.add("§f" + agui.c(agui.shiftclickDefineLocation) + "");
            desc.add(" ");
            visibleObject.setDescription(desc);
            ((TrueFalseObject) visibleObject).setStatus(is);
        } else if (visibleObject.getId().equals("isValid")) {
            Map map = getMap();
            ((TrueFalseObject) visibleObject).setStatus(map.isValid());
            visibleObject.display();
        } else if (visibleObject.getId().equals("MaxTimeMin") || visibleObject.getId().equals("MaxTimeSec")) {
            int intt = map.getMapTimeSecond();
            String s = "" + intt;
            if (intt < 10) {
                s = "0" + intt;
            }
            intt = map.getMapTimeMinute();
            String m = "" + intt;
            if (intt < 10) {
                m = "0" + intt;
            }
            visibleObject.setDisplayName("§7" + agui.c(agui.currentMaxTime) + ": §c" + m + "m:" + s + "s");
            if (visibleObject.getId().equals("MaxTimeMin")) {
                ((NumberObject1x2) visibleObject).setNumber(map.getMapTimeMinute());
            } else {
                ((NumberObject1x2) visibleObject).setNumber(map.getMapTimeSecond());
            }


        }
    }

    @Override
    public void setup() {
        TrueFalseObject trueFalseObject = new TrueFalseObject(this, "isEnabled", 2, 0, this.map.isEnabled());
        trueFalseObject.addDisplay();
        trueFalseObject = new TrueFalseObject(this, "isValid", 6, 0, this.map.isValid());
        trueFalseObject.addDisplay(true);

        IconObject iconObject = new IconObject(this, "MapInfo", 4, 0, 1, 1);
        iconObject.addItem(new ItemStack(Material.PAPER));
        iconObject.setDisplayName(agui.c(agui.information));
        iconObject.addDisplay(true);

        NumberObject1x2 numberObject1x2 = new NumberObject1x2(this, "MaxTimeMin", 2, 1, map.getMapTimeMinute());
        int intt = map.getMapTimeSecond();
        String s = "" + intt;
        if (intt < 10) {
            s = "0" + intt;
        }
        intt = map.getMapTimeMinute();
        String m = "" + intt;
        if (intt < 10) {
            m = "0" + intt;
        }
        numberObject1x2.setDisplayName("§7" + agui.c(agui.currentMaxTime) + ": §c" + m + "m:" + s + "s");
        numberObject1x2.setDescription(Arrays.asList("§7" + agui.c(agui.currentMaxTimeModification) + "",
                "",
                "§2" + agui.c(agui.leftclick) + ": + 1 " + agui.c(agui.minute) + "",
                "§2" + agui.c(agui.shiftleftclick) + ": " + agui.c(agui.doubleMinutes) + "",
                "§c" + agui.c(agui.rightclick) + ": - 1 " + agui.c(agui.minute) + "",
                "§c" + agui.c(agui.shiftrightclick) + ": " + agui.c(agui.divideMinutes)
        ));
        numberObject1x2.addDisplay();

        VisibleObject vo = new VisibleObject(this, "TimeMaxSeparator", 4, 1, 1, 1);
        vo.addItem(HeadDatabase.WHITE_SEPARATOR);
        vo.setDisplayName("");
        vo.addDisplay();

        NumberObject1x2 numberObject1x22 = new NumberObject1x2(this, "MaxTimeSec", 5, 1, map.getMapTimeSecond());
        numberObject1x2.setDisplayName("§7" + agui.c(agui.currentMaxTime) + ": §c" + m + "m:" + s + "s");
        numberObject1x2.setDescription(Arrays.asList("§7" + agui.c(agui.currentMaxTimeModification) + "",
                "",
                "§2" + agui.c(agui.leftclick) + ": + 1 " + agui.c(agui.second) + "",
                "§2" + agui.c(agui.shiftleftclick) + ": " + agui.c(agui.doubleSeconds) + "",
                "§c" + agui.c(agui.rightclick) + ": - 1 " + agui.c(agui.second) + "",
                "§c" + agui.c(agui.shiftrightclick) + ": " + agui.c(agui.divideSeconds)
        ));
        numberObject1x22.addDisplay();
        numberObject1x22.addLinkedObject(numberObject1x2);
        numberObject1x2.addLinkedObject(numberObject1x22);

        vo = new VisibleObject(this, "TimeMaxSeparator", 4, 1, 1, 1);
        vo.addItem(HeadDatabase.WHITE_SEPARATOR);
        vo.setDisplayName(" ");
        vo.addDisplay();

        ItemNumberObject itemNumberObject = new ItemNumberObject(this, "Map_FireWork", 4, 2, map.getFireworks(), new ItemStack(Material.getMaterial("FIREWORK")));
        itemNumberObject.setDisplayName("§7" + agui.c(agui.currentFireworksAmount) + ": §c" + map.getFireworks());
        itemNumberObject.setDescription(Arrays.asList("§7" + agui.c(agui.currentFireworkAmountMap) + "",
                "",
                "§2" + agui.c(agui.leftclick) + ": + 1 " + agui.c(agui.firework) + "",
                "§2" + agui.c(agui.shiftleftclick) + ": " + agui.c(agui.doubleFirework) + "",
                "§c" + agui.c(agui.rightclick) + ": - 1 " + agui.c(agui.firework) + "",
                "§c" + agui.c(agui.shiftrightclick) + ": " + agui.c(agui.divideFirework)
        ));
        itemNumberObject.addDisplay();

        itemNumberObject = new ItemNumberObject(this, "Map_Laps", 5, 2, map.getNumberoflaps(), HeadDatabase.PURPLE_L);
        itemNumberObject.setDisplayName("§7" + agui.c(agui.currentLaps) + ": §c" + map.getNumberoflaps());
        itemNumberObject.setDescription(Arrays.asList("§7" + agui.c(agui.lapMapLong) + "",
                "",
                "§2" + agui.c(agui.leftclick) + ": + 1 " + agui.c(agui.lap) + "",
                "§c" + agui.c(agui.rightclick) + ": - 1 " + agui.c(agui.lap) + ""
        ));
        itemNumberObject.addDisplay();


        itemNumberObject = new ItemNumberObject(this, "PageMapCheckpoints", 1, 2, map.getCpNumber(), HeadDatabase.GREEN_C);
        itemNumberObject.setDisplayName("§7" + agui.c(agui.thereisare) + " §c" + map.getCpNumber() + " §7" + agui.c(agui.inMapCp) + "");
        itemNumberObject.setDescription(Arrays.asList("§7" + agui.c(agui.clickModificationCp) + ""));
        itemNumberObject.addDisplay();

        itemNumberObject = new ItemNumberObject(this, "PageMapEnds", 2, 2, map.getEndNumber(), HeadDatabase.RED_E);
        itemNumberObject.setDisplayName("§7" + agui.c(agui.thereisare) + " §c" + map.getEndNumber() + " §7" + agui.c(agui.inMapEnd) + "");
        itemNumberObject.setDescription(Arrays.asList("§7" + agui.c(agui.clickModificationEnd) + ""));
        itemNumberObject.addDisplay();

        itemNumberObject = new ItemNumberObject(this, "PageMapAdditionalObjects", 3, 2, map.getAdditionalObjectsList().size(), HeadDatabase.GRAY_A);
        itemNumberObject.setDisplayName("§7" + agui.c(agui.thereisare) + " §c" + map.getAdditionalObjectsList().size() + " §7" + agui.c(agui.inMapAo) + "");
        itemNumberObject.setDescription(Arrays.asList("§7" + agui.c(agui.clickModificationAo) + ""));
        itemNumberObject.addDisplay();


        itemNumberObject = new ItemNumberObject(this, "Map_Difficulty", 7, 2, map.getDifficulty(), HeadDatabase.STAR);
        itemNumberObject.setDisplayName("§7" + agui.c(agui.currentDifficulty) + ": " + map.getDifficultyMSG());
        itemNumberObject.setDescription(Arrays.asList("§7" + agui.c(agui.clickDifficulty) + "",
                "",
                "§2" + agui.c(agui.leftclick) + ": + 1 " + Main.cmc().MAP_DIFFICULTY_SYMBOL,
                "§c" + agui.c(agui.rightclick) + ": - 1 " + Main.cmc().MAP_DIFFICULTY_SYMBOL));
        itemNumberObject.addDisplay();

        SwapperObject swapperObject = new SwapperObject(this, "MapAngle", 6, 2, map.getAngleType().ordinal(), 1);
        swapperObject.setDisplayName("§7" + agui.c(agui.respawnCpAngle) + ": §2" + map.getAngleType().toString());
        swapperObject.addItem(HeadDatabase.ORANGE_P);
        swapperObject.addItem(HeadDatabase.ORANGE_N);
        swapperObject.setDescription(Arrays.asList("§7" + agui.c(agui.respawnCpAngleL1) + "",
                "",
                "§f" + agui.c(agui.respawnCpAngleL2) + "",
                "§f* §6" + agui.c(agui.PLAYER) + "",
                "  §f" + agui.c(agui.respawnCpAngleL3) + "",
                "",
                "§f* §6" + agui.c(agui.NEXT_CHECKPOINT) + " §c§o(" + agui.c(agui.Experimental) + ")",
                "  §f" + agui.c(agui.respawnCpAngleL4) + "",
                "  §c" + agui.c(agui.respawnCpAngleL5) + ""

        ));
        swapperObject.addDisplay();


        trueFalseObject = new TrueFalseObject(this, "LocationLobby", 4, 3, map.getLocation_lobby() != null);
        trueFalseObject.addDisplay();

        trueFalseObject = new TrueFalseObject(this, "LocationStart", 3, 3, map.getLocation_start() != null);
        trueFalseObject.addDisplay();

        trueFalseObject = new TrueFalseObject(this, "LocationEnd", 5, 3, map.getLocation_end() != null);
        trueFalseObject.addDisplay();


        ItemStack paper = new ItemStack(Material.PAPER);
        IconObject icon = new IconObject(this, "PARTICLE", 1, 4, 1, 1);
        icon.addItem(paper);
        icon.setDisplayName("§7" + agui.c(agui.currentParticle) + ": §c" + map.getDefault_particle_effect_random());
        icon.setDescription(Arrays.asList("§7" + agui.c(agui.clickHereDefine) + " §6" + agui.c(agui.defaultParticleCP) + ""));
        icon.setInformation(ParticleMapPage.ParticlePageType.CHECKPOINT);
        icon.addDisplay();

        icon = new IconObject(this, "PARTICLE", 2, 4, 1, 1);
        icon.addItem(paper);
        icon.setDisplayName("§7" + agui.c(agui.currentParticle) + ": §c" + map.getDefault_particle_effect_random_ifnext());
        icon.setDescription(Arrays.asList("§7" + agui.c(agui.clickHereDefine) + " §6" + agui.c(agui.defaultParticleCP) + " (" + agui.c(agui.ifnext) + ")"));
        icon.setInformation(ParticleMapPage.ParticlePageType.CHECKPOINT_IFNEXT);
        icon.addDisplay();

        icon = new IconObject(this, "PARTICLE", 3, 4, 1, 1);
        icon.addItem(paper);
        icon.setDisplayName("§7" + agui.c(agui.currentParticle) + ": §c" + map.getDefault_particle_effect_end());
        icon.setDescription(Arrays.asList("§7" + agui.c(agui.clickHereDefine) + " §6" + agui.c(agui.defaultParticleEnd) + ""));
        icon.setInformation(ParticleMapPage.ParticlePageType.END);
        icon.addDisplay();

        icon = new IconObject(this, "PARTICLE", 4, 4, 1, 1);
        icon.addItem(paper);
        icon.setDisplayName("§7" + agui.c(agui.currentParticle) + ": §c" + map.getDefault_particle_effect_end_ifnext());
        icon.setDescription(Arrays.asList("§7" + agui.c(agui.clickHereDefine) + " §6" + agui.c(agui.defaultParticleEnd) + " (" + agui.c(agui.ifnext) + ")"));
        icon.setInformation(ParticleMapPage.ParticlePageType.END_IFNEXT);
        icon.addDisplay();

        icon = new IconObject(this, "PARTICLE", 5, 4, 1, 1);
        icon.addItem(paper);
        icon.setDisplayName("§7" + agui.c(agui.currentParticle) + ": §c" + map.getDefault_particle_effect_additonalObject());
        icon.setDescription(Arrays.asList("§7" + agui.c(agui.clickHereDefine) + " §6d" + agui.c(agui.defaultParticleAO) + ""));
        icon.setInformation(ParticleMapPage.ParticlePageType.AO);
        icon.addDisplay();

        icon = new IconObject(this, "PARTICLE", 6, 4, 1, 1);
        icon.addItem(paper);
        icon.setDisplayName("§7" + agui.c(agui.currentParticle) + ": §c" + map.getDefault_particle_effect_additonalObject_ifnext());
        icon.setDescription(Arrays.asList("§7" + agui.c(agui.clickHereDefine) + " §6" + agui.c(agui.defaultParticleAO) + " (" + agui.c(agui.ifnext) + ")"));
        icon.setInformation(ParticleMapPage.ParticlePageType.AO_IFNEXT);
        icon.addDisplay();

        trueFalseObject = new TrueFalseObject(this, "TRAINING", 7, 4, Main.cmc().ER_TRAIN_MAPS.contains(map.getName()));
        trueFalseObject.setDisplayName("§f" + agui.c(agui.mapAvailableTraining) + "");
        trueFalseObject.setDescription(new ArrayList<>(Arrays.asList("", "§f" + agui.c(agui.clickmapAvailableTraining) + "")));
        trueFalseObject.addDisplay();

        icon = new IconObject(this, "MapDB", 7, 3, 1, 1);
        icon.setDisplayName("§7" + agui.c(agui.dbUUID) + ": " + map.getUUID());
        icon.addItem(HeadDatabase.GRAY_C);
        icon.setDescription(Arrays.asList("§7" + agui.c(agui.clickDB) + ""));
        icon.addDisplay();

        trueFalseObject = new TrueFalseObject(this, "PodiumSystem", 1, 3, map.isPodium());
        trueFalseObject.setDisplayName("§7" + agui.c(agui.podiumConfiguration) + "");
        trueFalseObject.setDescription(Arrays.asList("§f" + agui.c(agui.qPodium) + " : " + Main.cmc().booleantoString(map.isPodium()),
                " ",
                "§7" + agui.c(agui.qPodiumL1) + "",
                "§c" + agui.c(agui.qPodiumL2) + "",
                "§7" + agui.c(agui.qPodiumL3) + "",
                "§c" + agui.c(agui.qPodiumL4) + ""));
        trueFalseObject.addDisplay();

        icon = new IconObject(this, "STARTGAME", 8, 4, 1, 1);
        icon.addItem(HeadDatabase.IRON_RIGHT_ARROW);
        icon.setDisplayName("§a" + agui.c(agui.startHost) + "");
        icon.setDescription(Arrays.asList("§f" + agui.c(agui.createHostMap) + ""));
        icon.addDisplay();

        icon = new IconObject(this, "HELP", 8, 0);
        icon.setItem(HeadDatabase.GREEN_H);
        icon.setDisplayName("§a" + agui.c(agui.helpMessage) + "");
        icon.setDescription(new ArrayList<>(Arrays.asList("", "§7" + agui.c(agui.helpMessageL1) + " ", "§7" + agui.c(agui.helpMessageL2Map) + "")));
        icon.addDisplay();

        icon = new IconObject(this, "EXIT", 0, 4);
        icon.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGELEFT, null));
        icon.setItem(HeadDatabase.RED_LEFT_ARROW);
        icon.setDescription(Arrays.asList("§7" + agui.c(agui.closeMenu) + ""));
        icon.addDisplay();

        trueFalseObject = new TrueFalseObject(this, "PASSWALLS", 6, 3, map.canPassThroughWall());
        trueFalseObject.setDisplayName("§7" + agui.c(agui.specAndWalls) + "");
        trueFalseObject.setDescription(Arrays.asList("§f" + agui.c(agui.qspecAndWalls) + " " + Main.cmc().booleantoString(map.canPassThroughWall()), "", "§7" + agui.c(agui.clickChangeValue) + ""));
        trueFalseObject.addDisplay();

        trueFalseObject = new TrueFalseObject(this, "SHOWMAP", 2, 3, MapCommand.isShowingMap(getViewer(), map));
        trueFalseObject.setDisplayName("§7" + agui.c(agui.showMap) + "");
        trueFalseObject.setDescription(Arrays.asList("§7" + agui.c(agui.clickShowMap) + ""));
        trueFalseObject.addDisplay();
    }

    @Override
    public void View() {

    }


    @Override
    public void click(VisibleObject visibleObject, ClickType clickType) {
        super.click(visibleObject, clickType);
        if (visibleObject.getId().equals("PageMapEnds")) {
            EndChoosePage endChoosePage = new EndChoosePage(getViewer(), map);
            endChoosePage.open();
            endChoosePage.displayPage(1);
        } else if (visibleObject.getId().equals("HELP")) {
            MapCommand.HelpMessage(getViewer(), MapCommand.HelpMessage.DEFAULT, map);
        } else if (visibleObject.getId().equals("EXIT")) {
            MapChoosePage mapChoosePage = new MapChoosePage(getViewer());
            mapChoosePage.displayPage(1);
            mapChoosePage.open();
        } else if (visibleObject.getId().equals("PageMapCheckpoints")) {
            CheckpointChoosePage checkpointPage = new CheckpointChoosePage(getViewer(), map);
            checkpointPage.open();
            checkpointPage.displayPage(1);
        } else if (visibleObject.getId().equals("PageMapAdditionalObjects")) {
            AdditionalObjectChoosePage additionalObjectChoosePage = new AdditionalObjectChoosePage(getViewer(), map);
            additionalObjectChoosePage.open();
            additionalObjectChoosePage.displayPage(1);
        } else if (visibleObject.getId().equals("TRAINING")) {
            TrueFalseObject trueFalseObject = (TrueFalseObject) visibleObject;
            if (Main.cmc().ER_TRAIN_MAPS.contains(map.getName())) {
                Main.cmc().ER_TRAIN_MAPS.remove(map.getName());
                trueFalseObject.setStatus(false);
            } else {
                Main.cmc().ER_TRAIN_MAPS.add(map.getName());
                trueFalseObject.setStatus(true);
            }
            sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
            visibleObject.display();
            Main.serialization.saveMessageConfig();
        } else if (visibleObject.getId().equals("PASSWALLS")) {
            boolean c = !((TrueFalseObject) visibleObject).getStatus();
            ((TrueFalseObject) visibleObject).setStatus(c);
            map.setSpecAbletopass(c);
            visibleObject.setDescription(Arrays.asList("§f" + agui.c(agui.qspecAndWalls) + " " + Main.cmc().booleantoString(map.canPassThroughWall()), "", "§7" + agui.c(agui.clickChangeValue) + ""));

            map.save();
            visibleObject.display();
        }
    }

    public Map getMap() {
        return map;
    }

    @Override
    public Object getInformation() {
        Object o = map;
        return o;
    }

    @Override
    public void open() {
        super.open();
        setup();
    }
}
