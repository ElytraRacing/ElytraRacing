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
import fr.CHOOSEIT.elytraracing.GUI.VisibleObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.IconObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.ItemNumberObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.NumberObject1x2;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.MapCommand;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.AdditionalObject;
import fr.CHOOSEIT.elytraracing.mapsystem.linkplayer.LinkPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdditionalObjectPage extends Page {
    AdditionalObject additionalObject;
    Map map;
    public AdminGUIConfig agui = Main.adminGUIConfig;

    public AdditionalObjectPage(Player viewer, Map map, AdditionalObject additionalObject) {
        super(Main.adminGUIConfig.c(Main.adminGUIConfig.pageAOP), viewer, 3);
        this.additionalObject = additionalObject;
        this.map = map;
    }

    public AdditionalObject getAdditionalObject() {
        return additionalObject;
    }

    @Override
    public void View() {
        IconObject io = new IconObject(this, "INFO", 4);
        io.setItem(new ItemStack(Material.PAPER));
        io.setDisplayName("§f" + agui.c(agui.information));
        io.addDisplay(true);

        io = new IconObject(this, "PARTICLE_NORMAL", 3, 2);
        io.setItem(new ItemStack(Material.PAPER));
        io.setDisplayName("§7" + agui.c(agui.currentParticle) + ": §c" + ObjectClass.particleAsString(additionalObject.getEnumParticle()));
        io.setDescription(new ArrayList<>(Arrays.asList("§7" + agui.c(agui.clickDefineParticleObject))));
        io.addDisplay();

        io = new IconObject(this, "PARTICLE_IFNEXT", 5, 2);
        io.setItem(new ItemStack(Material.PAPER));
        io.setDisplayName("§7" + agui.c(agui.currentParticle) + ": §c" + ObjectClass.particleAsString(additionalObject.getEnumParticle_IFNEXT()));
        io.setDescription(new ArrayList<>(Arrays.asList("§7" + agui.c(agui.clickDefineParticleObject) + " §6(" + agui.c(agui.ifnext) + ")")));
        io.addDisplay();

        NumberObject1x2 no = new NumberObject1x2(this, "ID", 1, 1, additionalObject.getID(map.getAdditionalObjectsList()));
        no.setDisplayName("§f» §7" + agui.c(agui.id) + ": §6" + additionalObject.getID(map.getAdditionalObjectsList()));
        no.addDisplay();

        io = new IconObject(this, "LOCATION", 4, 1);
        io.setItem(HeadDatabase.RED_L);
        io.setDisplayName("§7" + agui.c(agui.currentLocation));
        List<String> l = new ArrayList<>();
        l.add("§f» §7" + agui.c(agui.world) + ": §6" + additionalObject.getWorld());
        l.add("§f» §7x: §6" + additionalObject.getLocation().getX());
        l.add("§f» §7y: §6" + additionalObject.getLocation().getY());
        l.add("§f» §7z: §6" + additionalObject.getLocation().getZ());
        l.add(" ");
        l.add("§f" + agui.c(agui.leftTeleported) + "");
        l.add("§f" + agui.c(agui.rightDefineLocation) + "");
        io.setDescription(l);
        io.addDisplay();

        ItemNumberObject ino = new ItemNumberObject(this, "PARTICLE_AMOUNT", 6, 1, additionalObject.getParticleAmount(), HeadDatabase.GREEN_A);
        ino.setDisplayName("§7" + agui.c(agui.currentParticleAmount) + ": §c" + additionalObject.getParticleAmount());
        l = new ArrayList<>();
        l.add("§f" + agui.c(agui.particleAmountLong));
        l.add(" ");
        l.add("§2" + agui.c(agui.leftclick) + ": +1");
        l.add("§c" + agui.c(agui.rightclick) + ": -1");
        ino.setDescription(l);
        ino.setMaxnumber(10);
        ino.setNumber(additionalObject.getParticleAmount());
        ino.addDisplay();

        ino = new ItemNumberObject(this, "FIREWORK", 6, 2, additionalObject.getFireworks(), new ItemStack(Material.getMaterial("FIREWORK")));
        ino.setDisplayName("§7" + agui.c(agui.currentFireworksAmount) + ": §c" + additionalObject.getFireworks());
        l = new ArrayList<>();
        l.add("§f" + agui.c(agui.fireworksAmountLong));
        l.add(" ");
        l.add("§2" + agui.c(agui.leftclick) + ": +1");
        l.add("§c" + agui.c(agui.rightclick) + ": -1");
        ino.setDescription(l);
        ino.setMaxnumber(64);
        ino.setNumber(additionalObject.getFireworks());
        ino.addDisplay();


        ino = new ItemNumberObject(this, "SIZE", 7, 1, (int) additionalObject.getRadiusSize(), HeadDatabase.PURPLE_S);
        ino.setDisplayName("§7" + agui.c(agui.currentRadius) + ": §c" + additionalObject.getRadiusSize());
        l = new ArrayList<>();
        l.add("§f" + agui.c(agui.radiusmodification));
        l.add(" ");
        l.add("§2" + agui.c(agui.leftclick) + ": +1");
        l.add("§2" + agui.c(agui.shiftleftclick) + ": +0.1");
        l.add("§c" + agui.c(agui.rightclick) + ": -1");
        l.add("§c" + agui.c(agui.shiftrightclick) + ": -0.1");
        ino.setDescription(l);
        ino.setNumber((int) additionalObject.getRadiusSize());
        ino.addDisplay();

        ino = new ItemNumberObject(this, "BOOST", 2, 2, (int) additionalObject.getBoostmultiplier(), HeadDatabase.YELLOW_B);
        ino.setDisplayName("§7" + agui.c(agui.currentBoostMultiplier) + ": §c" + additionalObject.getBoostmultiplier());
        l = new ArrayList<>();
        l.add("§f" + agui.c(agui.boostMultiplierModification));
        l.add(" ");
        l.add("§2" + agui.c(agui.leftclick) + ": +0.5");
        l.add("§2" + agui.c(agui.shiftleftclick) + ": +0.05");
        l.add("§c" + agui.c(agui.rightclick) + ": -0.5");
        l.add("§c" + agui.c(agui.shiftrightclick) + ": -0.05");
        ino.setDescription(l);
        ino.setNumber((int) additionalObject.getBoostmultiplier());
        ino.addDisplay();


        io = new IconObject(this, "EXIT", 0, 2);
        io.setItem(HeadDatabase.RED_LEFT_ARROW);
        io.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGELEFT, null));
        io.setDescription(Arrays.asList("§7" + agui.c(agui.closeMenu)));
        io.addDisplay();

        io = new IconObject(this, "LOCK", 8, 0);
        io.setItem(HeadDatabase.WHITE_LOCK);
        io.setDisplayName("§a" + agui.c(agui.linkWithYou));
        io.setDescription(new ArrayList<>(Arrays.asList("", "§7" + agui.c(agui.linkModificationL1), "", "§7" + agui.c(agui.linkModificationL2), "§7" + agui.c(agui.linkModificationL3), "", "§f" + agui.c(agui.linkModificationL4), "§f" + agui.c(agui.linkModificationL5), "", "§c" + agui.c(agui.linkModificationL6), "§c" + agui.c(agui.linkModificationL7))));
        io.addDisplay();

        io = new IconObject(this, "DELETE", 0, 0);
        io.setItem(HeadDatabase.RED_D);
        io.setDisplayName("§c" + agui.c(agui.delete));
        io.setDescription(new ArrayList<>(Arrays.asList("", "§7" + agui.c(agui.clickDelete), "")));
        io.addDisplay();

    }

    @Override
    public void click(VisibleObject visibleObject, ClickType clickType) {
        super.click(visibleObject, clickType);
        if (visibleObject.getId().equals("PARTICLE_NORMAL")) {
            ParticleObjectPage pop = new ParticleObjectPage(getViewer(), map, additionalObject, ParticleObjectPage.TypeMoment.NORMAL);
            pop.setInformationObjects(new ArrayList<>(Arrays.asList("AO", additionalObject)));
            pop.displayPage(1);
            pop.open();
        } else if (visibleObject.getId().equals("DELETE")) {
            ConfirmationPage confirmationPage = new ConfirmationPage(agui.c(agui.pageConfirmationDeleteObject), getViewer(), this, "§f" + agui.c(agui.informationConfirmationDeleteAO), new ArrayList<>(Arrays.asList("", "§7" + agui.c(agui.id) + ": §c" + additionalObject.getID(map.getAdditionalObjectsList()), "")));
            confirmationPage.setInformationObjects(new ArrayList<Object>(Arrays.asList("DELETE_OBJECT", "AO", additionalObject, map)));
            confirmationPage.open();
        } else if (visibleObject.getId().equals("EXIT")) {
            AdditionalObjectChoosePage additionalObjectChoosePage = new AdditionalObjectChoosePage(getViewer(), map);
            additionalObjectChoosePage.open();
            additionalObjectChoosePage.displayPage(1);
        } else if (visibleObject.getId().equals("FIREWORK")) {
            int n = additionalObject.getFireworks();

            switch (clickType) {
                case LEFT:
                case SHIFT_LEFT:
                    n++;
                    if (n == 65) {
                        n = 64;
                    }
                    break;
                case RIGHT:
                case SHIFT_RIGHT:
                    n--;
                    if (n == -1) {
                        n = 0;
                    }
                    break;
            }
            additionalObject.setFireworks(n);
            additionalObject.ReloadShow();
            map.save();
            visibleObject.setDisplayName("§7" + agui.c(agui.currentFireworksAmount) + ": §c" + n);
            ((ItemNumberObject) visibleObject).setNumber(n);


        } else if (visibleObject.getId().equals("BOOST")) {
            float n = additionalObject.getBoostmultiplier();

            switch (clickType) {
                case LEFT:
                    n += 0.5;
                    break;
                case SHIFT_LEFT:
                    n += 0.05;
                    break;
                case RIGHT:
                    n -= 0.5;
                    break;
                case SHIFT_RIGHT:
                    n -= 0.05;
                    break;
            }
            if (n < -0.95f) {
                n = -0.95f;
            }
            n = (float) (Math.round(n * 100) / 100.0);
            additionalObject.setBoostmultiplier(n);
            additionalObject.ReloadShow();
            map.save();
            visibleObject.setDisplayName("§7" + agui.c(agui.currentBoostMultiplier) + ": §c" + n);
            ((ItemNumberObject) visibleObject).setNumber((int) n);
        } else if (visibleObject.getId().equals("PARTICLE_AMOUNT")) {
            int n = additionalObject.getParticleAmount();

            switch (clickType) {
                case LEFT:
                case SHIFT_LEFT:
                    n++;
                    if (n == 11) {
                        n = 10;
                    }
                    break;
                case RIGHT:
                case SHIFT_RIGHT:
                    n--;
                    if (n == 0) {
                        n = 1;
                    }
                    break;
            }
            additionalObject.setParticle_amount(n);
            additionalObject.ReloadShow();
            map.save();
            visibleObject.setDisplayName("§7" + agui.c(agui.currentParticleAmount) + ": §c" + n);
            ((ItemNumberObject) visibleObject).setNumber(n);


        } else if (visibleObject.getId().equals("SIZE")) {
            float n = additionalObject.getRadiusSize();

            switch (clickType) {
                case LEFT:
                    n++;
                    break;
                case SHIFT_LEFT:
                    n += 0.1;
                    break;
                case RIGHT:
                    n--;
                    if (n <= 0) {
                        n = 0.1f;
                    }
                    break;
                case SHIFT_RIGHT:
                    n -= 0.1;
                    if (n <= 0.1) {
                        n = 0.1f;
                    }
                    break;
            }
            n = (float) (Math.round(n * 10) / 10.0);
            additionalObject.setRadius_size(n);
            additionalObject.ReloadShow();
            map.save();
            visibleObject.setDisplayName("§7" + agui.c(agui.currentRadius) + ": §c" + n);
            ((ItemNumberObject) visibleObject).setNumber((int) n);
        } else if (visibleObject.getId().equals("PARTICLE_IFNEXT")) {
            ParticleObjectPage pop = new ParticleObjectPage(getViewer(), map, additionalObject, ParticleObjectPage.TypeMoment.IFNEXT);
            pop.setInformationObjects(new ArrayList<>(Arrays.asList("AO", additionalObject)));
            pop.displayPage(1);
            pop.open();
        } else if (visibleObject.getId().equals("LOCK")) {
            if (clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.SHIFT_LEFT)) {
                LinkPlayer.LinkPlayerInstance linkPlayerInstance = LinkPlayer.getLinkPlayer(getViewer());
                linkPlayerInstance.setMap(map);
                linkPlayerInstance.setObjectClass(additionalObject);

                if (!MapCommand.isShowingMap(getViewer(), map)) {
                    MapCommand.show(getViewer(), map);
                }
            } else {
                LinkPlayer.giveItems(getViewer());
            }
        } else if (visibleObject.getId().equals("LOCATION")) {
            if (clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.SHIFT_LEFT)) {
                getViewer().teleport(additionalObject.getLocation());
                sendViewerMessage(Main.cmc().TELEPORTING);
                return;

            } else {
                additionalObject.setLocation(getViewer().getLocation());
                additionalObject.ReloadShow();
                map.save();
                sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);

            }
        }
    }

    @Override
    public void update(VisibleObject visibleObject) {
        super.update(visibleObject);
        if (visibleObject.getId().equalsIgnoreCase("INFO")) {
            IconObject io = (IconObject) visibleObject;

            List<String> l = new ArrayList<>();
            l.add("§f» §7" + agui.c(agui.id) + ": §6" + additionalObject.getID(map.getAdditionalObjectsList()));
            l.add(" ");
            l.add("§f» §7" + agui.c(agui.particles) + ": " + "§f» §7" + agui.c(agui.normal) + ": §a" + ObjectClass.particleAsString(additionalObject.getEnumParticle()) + " §f«» §7" + agui.c(agui.ifnext) + ": §a" + ObjectClass.particleAsString(additionalObject.getEnumParticle_IFNEXT()));
            l.add("§f» §7" + agui.c(agui.particleAmount) + ": §a" + additionalObject.getParticleAmount());
            l.add(" ");
            l.add("§f» §7" + agui.c(agui.world) + ": §6" + additionalObject.getWorld());
            l.add("§f» §7x: §6" + additionalObject.getLocation().getX());
            l.add("§f» §7y: §6" + additionalObject.getLocation().getY());
            l.add("§f» §7z: §6" + additionalObject.getLocation().getZ());
            l.add(" ");
            l.add("§f» §7" + agui.c(agui.rotations) + ": " + "§f» §7x: §c" + additionalObject.getXDegrees() + " §f«» §7y: §c" + additionalObject.getYDegrees() + " §f«» §7z: §c" + additionalObject.getZDegrees());
            l.add(" ");
            l.add("§f» §7" + agui.c(agui.shape) + ": §6" + additionalObject.getShapeStr());
            l.add("§f» §7" + agui.c(agui.radius) + ": §6" + additionalObject.getRadiusSize());
            l.add(" ");
            l.add("§f» §7" + agui.c(agui.boostMultiplier) + ": §6" + additionalObject.getBoostmultiplier());
            l.add("§f» §7" + agui.c(agui.fireworks) + ": §6" + additionalObject.getFireworks());
            io.setDescription(l);
        }
        visibleObject.display();
    }


}
