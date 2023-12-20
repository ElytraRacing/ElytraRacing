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
import fr.CHOOSEIT.elytraracing.mapsystem.MapCommand;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.End;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import fr.CHOOSEIT.elytraracing.mapsystem.linkplayer.LinkPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EndPage extends Page {
    End end;
    Map map;
    public static AdminGUIConfig agui = Main.adminGUIConfig;

    public EndPage(Player viewer, Map map, End end) {
        super(Main.adminGUIConfig.c(Main.adminGUIConfig.pageEP), viewer, 3);
        this.end = end;
        this.map = map;
    }

    public End getEnd() {
        return end;
    }

    @Override
    public void View() {
        IconObject io = new IconObject(this, "INFO", 4);
        io.setItem(new ItemStack(Material.PAPER));
        io.setDisplayName("§f" + agui.c(agui.information) + "");
        io.addDisplay(true);

        io = new IconObject(this, "PARTICLE_NORMAL", 3, 2);
        io.setItem(new ItemStack(Material.PAPER));
        io.setDisplayName("§7" + agui.c(agui.currentParticle) + ": §c" + ObjectClass.particleAsString(end.getEnumParticle()));
        io.setDescription(new ArrayList<>(Arrays.asList("§7" + agui.c(agui.clickDefineParticleObject) + "")));
        io.addDisplay();

        io = new IconObject(this, "PARTICLE_IFNEXT", 5, 2);
        io.setItem(new ItemStack(Material.PAPER));
        io.setDisplayName("§7" + agui.c(agui.currentParticle) + ": §c" + ObjectClass.particleAsString(end.getEnumParticle_IFNEXT()));
        io.setDescription(new ArrayList<>(Arrays.asList("§7" + agui.c(agui.clickDefineParticleObject) + " §6(" + agui.c(agui.ifnext) + ")")));
        io.addDisplay();

        NumberObject1x2 no = new NumberObject1x2(this, "ID", 1, 1, end.getID(map.getEndsList()));
        no.setDisplayName("§f» §7" + agui.c(agui.id) + ": §6" + end.getID(map.getEndsList()));
        no.addDisplay();

        io = new IconObject(this, "LOCATION", 4, 1);
        io.setItem(HeadDatabase.RED_L);
        io.setDisplayName("§7" + agui.c(agui.currentLocation) + "");
        List<String> l = new ArrayList<>();
        l.add("§f» §7" + agui.c(agui.world) + ": §6" + end.getWorld());
        l.add("§f» §7x: §6" + end.getLocation().getX());
        l.add("§f» §7y: §6" + end.getLocation().getY());
        l.add("§f» §7z: §6" + end.getLocation().getZ());
        l.add(" ");
        l.add("§f" + agui.c(agui.leftTeleported) + "");
        l.add("§f" + agui.c(agui.rightDefineLocation) + "");
        io.setDescription(l);
        io.addDisplay();

        ItemNumberObject ino = new ItemNumberObject(this, "PARTICLE_AMOUNT", 6, 1, end.getParticleAmount(), HeadDatabase.GREEN_A);
        ino.setDisplayName("§7" + agui.c(agui.currentParticleAmount) + ": §c" + end.getParticleAmount());
        l = new ArrayList<>();
        l.add("§f" + agui.c(agui.particleAmountLong) + "");
        l.add(" ");
        l.add("§2" + agui.c(agui.leftclick) + ": +1");
        l.add("§c" + agui.c(agui.rightclick) + ": -1");
        ino.setDescription(l);
        ino.setMaxnumber(10);
        ino.setNumber(end.getParticleAmount());
        ino.addDisplay();

        ino = new ItemNumberObject(this, "SIZE", 7, 1, (int) end.getRadiusSize(), HeadDatabase.PURPLE_S);
        ino.setDisplayName("§7" + agui.c(agui.currentRadius) + ": §c" + end.getRadiusSize());
        l = new ArrayList<>();
        l.add("§f" + agui.c(agui.radiusmodification) + "");
        l.add(" ");
        l.add("§2" + agui.c(agui.leftclick) + ": +1");
        l.add("§2" + agui.c(agui.shiftleftclick) + ": +0.1");
        l.add("§c" + agui.c(agui.rightclick) + ": -1");
        l.add("§c" + agui.c(agui.shiftrightclick) + ": -0.1");
        ino.setDescription(l);
        ino.setNumber((int) end.getRadiusSize());
        ino.addDisplay();

        io = new IconObject(this, "EXIT", 0, 2);
        io.setItem(HeadDatabase.RED_LEFT_ARROW);
        io.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGELEFT, null));
        io.setDescription(Arrays.asList("§7" + agui.c(agui.closeMenu) + ""));
        io.addDisplay();

        io = new IconObject(this, "LOCK", 8, 0);
        io.setItem(HeadDatabase.WHITE_LOCK);
        io.setDisplayName("§a" + agui.c(agui.linkWithYou));
        io.setDescription(new ArrayList<>(Arrays.asList("", "§7" + agui.c(agui.linkModificationL1), "", "§7" + agui.c(agui.linkModificationL2), "§7" + agui.c(agui.linkModificationL3), "", "§f" + agui.c(agui.linkModificationL4), "§f" + agui.c(agui.linkModificationL5), "", "§c" + agui.c(agui.linkModificationL6), "§c" + agui.c(agui.linkModificationL7))));
        io.addDisplay();

        io = new IconObject(this, "HELP", 8, 2);
        io.setItem(HeadDatabase.GREEN_H);
        io.setDisplayName("§a" + agui.c(agui.helpMessage) + "");
        io.setDescription(new ArrayList<>(Arrays.asList("", "§7" + agui.c(agui.helpMessageL1) + "", "§7" + agui.c(agui.helpMessageL2) + "")));
        io.addDisplay();

        io = new IconObject(this, "DELETE", 0, 0);
        io.setItem(HeadDatabase.RED_D);
        io.setDisplayName("§c" + agui.c(agui.delete) + "");
        io.setDescription(new ArrayList<>(Arrays.asList("", "§7" + agui.c(agui.clickDelete) + "", "")));
        io.addDisplay();

    }

    private static String NONCEDLW = "%%__NONCE__%%";

    @Override
    public void click(VisibleObject visibleObject, ClickType clickType) {
        super.click(visibleObject, clickType);
        if (visibleObject.getId().equals("PARTICLE_NORMAL")) {
            ParticleObjectPage pop = new ParticleObjectPage(getViewer(), map, end, ParticleObjectPage.TypeMoment.NORMAL);
            pop.setInformationObjects(new ArrayList<>(Arrays.asList("END", end)));
            pop.displayPage(1);
            pop.open();
        } else if (visibleObject.getId().equals("HELP")) {
            MapCommand.HelpMessage(getViewer(), MapCommand.HelpMessage.END, map, end.getID(map.getEndsList()));
        } else if (visibleObject.getId().equals("DELETE")) {
            ConfirmationPage confirmationPage = new ConfirmationPage(agui.c(agui.pageConfirmationDeleteObject), getViewer(), this, "§f" + agui.c(agui.informationConfirmationDeleteEND) + "", new ArrayList<>(Arrays.asList("", "§7" + agui.c(agui.id) + ": §c" + end.getID(map.getEndsList()), "")));
            confirmationPage.setInformationObjects(new ArrayList<Object>(Arrays.asList("DELETE_OBJECT", "END", end, map)));
            confirmationPage.open();
        } else if (visibleObject.getId().equals("EXIT")) {
            EndChoosePage endChoosePage = new EndChoosePage(getViewer(), map);
            endChoosePage.open();
            endChoosePage.displayPage(1);
        } else if (visibleObject.getId().equals("PARTICLE_AMOUNT")) {
            int n = end.getParticleAmount();

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
            end.setParticle_amount(n);
            end.ReloadShow();
            map.save();
            visibleObject.setDisplayName("§7" + agui.c(agui.currentParticleAmount) + ": §c" + n);
            ((ItemNumberObject) visibleObject).setNumber(n);


        } else if (visibleObject.getId().equals("LOCK")) {
            if (clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.SHIFT_LEFT)) {
                LinkPlayer.LinkPlayerInstance linkPlayerInstance = LinkPlayer.getLinkPlayer(getViewer());
                linkPlayerInstance.setMap(map);
                linkPlayerInstance.setObjectClass(end);

                if (!MapCommand.isShowingMap(getViewer(), map)) {
                    MapCommand.show(getViewer(), map);
                }
            } else {
                LinkPlayer.giveItems(getViewer());
            }
        } else if (visibleObject.getId().equals("SIZE")) {
            float n = end.getRadiusSize();

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
            end.setRadius_size(n);
            end.ReloadShow();
            map.save();
            visibleObject.setDisplayName("§7" + agui.c(agui.currentRadius) + ": §c" + n);
            ((ItemNumberObject) visibleObject).setNumber((int) n);
        } else if (visibleObject.getId().equals("PARTICLE_IFNEXT")) {
            ParticleObjectPage pop = new ParticleObjectPage(getViewer(), map, end, ParticleObjectPage.TypeMoment.IFNEXT);
            pop.setInformationObjects(new ArrayList<>(Arrays.asList("END", end)));
            pop.displayPage(1);
            pop.open();
        } else if (visibleObject.getId().equals("LOCATION")) {
            if (clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.SHIFT_LEFT)) {
                getViewer().teleport(end.getLocation());
                sendViewerMessage(Main.cmc().TELEPORTING);
                return;

            } else {
                end.setLocation(getViewer().getLocation());
                end.ReloadShow();
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
            l.add("§f» §7" + agui.c(agui.id) + ": §6" + end.getID(map.getEndsList()));
            l.add(" ");
            l.add("§f» §7" + agui.c(agui.particles) + ": " + "§f» §7" + agui.c(agui.normal) + ": §a" + ObjectClass.particleAsString(end.getEnumParticle()) + " §f«» §7" + agui.c(agui.ifnext) + ": §a" + ObjectClass.particleAsString(end.getEnumParticle_IFNEXT()));
            l.add("§f» §7" + agui.c(agui.particleAmount) + ": §a" + end.getParticleAmount());
            l.add(" ");
            l.add("§f» §7" + agui.c(agui.world) + ": §6" + end.getWorld());
            l.add("§f» §7x: §6" + end.getLocation().getX());
            l.add("§f» §7y: §6" + end.getLocation().getY());
            l.add("§f» §7z: §6" + end.getLocation().getZ());
            l.add(" ");
            l.add("§f» §7" + agui.c(agui.rotations) + ": " + "§f» §7x: §c" + end.getXDegrees() + " §f«» §7y: §c" + end.getYDegrees() + " §f«» §7z: §c" + end.getZDegrees());
            l.add(" ");
            l.add("§f» §7" + agui.c(agui.shape) + ": §6" + end.getShapeStr());
            l.add("§f» §7" + agui.c(agui.radius) + ": §6" + end.getRadiusSize());
            io.setDescription(l);
        }
        visibleObject.display();
    }


}
