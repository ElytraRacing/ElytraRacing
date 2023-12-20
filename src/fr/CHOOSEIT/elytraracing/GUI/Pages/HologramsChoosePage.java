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
import fr.CHOOSEIT.elytraracing.GUI.Modules.MultiPagesModule;
import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.IconObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.holograms.Holograms;
import fr.CHOOSEIT.elytraracing.holograms.HologramsCreator;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HologramsChoosePage extends Page implements MultiPagesModule {
    public static AdminGUIConfig agui = Main.adminGUIConfig;

    public HologramsChoosePage(Player viewer) {
        super(Main.adminGUIConfig.c(Main.adminGUIConfig.pageHCP), viewer, 5);
    }

    @Override
    public void setup() {

    }

    @Override
    public void View() {

    }


    @Override
    public Object getInformation() {
        return null;
    }

    int pageNumber;

    @Override
    public void displayPage(int page) {
        if (page == 0) {
            page = 1;
        }
        removeObjects();
        pageNumber = page;

        ArrayList<Holograms> infos = new ArrayList<>();
        Holograms _h;

        if (Main.hologramsSaver != null) {
            for (HologramsCreator hologram : Main.hologramsSaver.Holograms) {
                _h = hologram.getHologram();
                if (_h != null) {
                    infos.add(_h);
                }
            }
        }
        int size = infos.size();
        IconObject vo;
        ItemStack paper = new ItemStack(Material.PAPER);

        int startpage = MultiPagesModule.getIDPageStart(page, 36);
        int index = 0;

        List<String> lore;
        Holograms holo;
        ItemStack icon;

        for (int i = 0; i < 36; i++) {
            index = i + startpage;
            if (index >= size) {
                break;
            }

            vo = new IconObject(this, "SELECT", i, 1, 1);
            holo = infos.get(index);
            icon = Holograms.getHead(holo.hologram_type);
            vo.addItem(icon != null ? icon : paper);
            vo.setInformation(holo.name);
            vo.setDisplayName("§f" + holo.name);

            lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7x: §c" + holo.location.getX());
            lore.add("§7y: §c" + holo.location.getY());
            lore.add("§7z: §c" + holo.location.getZ());
            lore.add("§7" + agui.c(agui.world) + ": §c" + requireNonNullWorld(holo.location.getWorld()));
            lore.add("§7" + agui.c(agui.map) + ": §c" + requireNonNullMap(holo.map));
            lore.add("§7" + agui.c(agui.type) + ": §c" + holo.hologram_type);
            lore.add(" ");
            lore.add("§7" + agui.c(agui.name) + ": §c" + holo.name);
            lore.add(" ");
            lore.add("§f" + agui.c(agui.leftConfigure) + "");
            lore.add("§f" + agui.c(agui.rightTeleported) + "");
            vo.setDescription(lore);
            vo.addDisplay();

        }
        if (index < size - 1) {
            vo = new IconObject(this, "PAGE_PLUS", 8, 4, 1, 1);
            vo.addItem(HeadDatabase.BLUE_RIGHT_ARROW);
            vo.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGERIGHT, null));
            vo.setDescription(Arrays.asList("§7" + agui.c(agui.clickChangePage) + ""));
            vo.addDisplay();
        }

        vo = new IconObject(this, "PAGE_MINUS", 0, 4, 1, 1);
        vo.addItem(HeadDatabase.RED_LEFT_ARROW);
        vo.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGELEFT, null));
        vo.setDescription(Arrays.asList("§7" + agui.c(agui.clickChangePage) + ""));
        vo.addDisplay();

        vo = new IconObject(this, "CREATE", 1, 4, 1, 1);
        Holograms.HOLOGRAM_TYPE type = Holograms.HOLOGRAM_TYPE.PERSONAL_MAPS;
        icon = Holograms.getHead(type);
        vo.addItem(icon != null ? icon : paper);
        vo.setInformation(type);
        vo.setDisplayName("§f" + agui.c(agui.personalMaps) + "");
        lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§f" + agui.c(agui.type) + ": §c" + type);
        lore.add("§f" + agui.c(agui.clickCreateNewHolograms) + "");
        lore.add(" ");
        vo.setDescription(lore);
        vo.addDisplay();

        type = Holograms.HOLOGRAM_TYPE.PERSONAL_STATS;
        vo = new IconObject(this, "CREATE", 7, 4, 1, 1);
        icon = Holograms.getHead(type);
        vo.addItem(icon != null ? icon : paper);
        vo.setInformation(type);
        vo.setDisplayName("§f" + agui.c(agui.personalStats) + "");
        lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§f" + agui.c(agui.type) + ": §c" + type);
        lore.add("§f" + agui.c(agui.clickCreateNewHolograms) + "");
        lore.add(" ");
        vo.setDescription(lore);
        vo.addDisplay();

        type = Holograms.HOLOGRAM_TYPE.RANK_SCORE;
        vo = new IconObject(this, "CREATE", 2, 4, 1, 1);
        icon = Holograms.getHead(type);
        vo.addItem(icon != null ? icon : paper);
        vo.setInformation(type);
        vo.setDisplayName("§f" + agui.c(agui.rankscore) + "");
        lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§f" + agui.c(agui.type) + ": §c" + type);
        lore.add("§f" + agui.c(agui.clickCreateNewHolograms) + "");
        lore.add(" ");
        vo.setDescription(lore);
        vo.addDisplay();


        type = Holograms.HOLOGRAM_TYPE.RANK_MAP;
        vo = new IconObject(this, "CREATE", 6, 4, 1, 1);
        icon = Holograms.getHead(type);
        vo.addItem(icon != null ? icon : paper);
        vo.setInformation(type);
        vo.setDisplayName("§f" + agui.c(agui.rankmap) + "");
        lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§f" + agui.c(agui.type) + ": §c" + type);
        lore.add("§f" + agui.c(agui.clickCreateNewHolograms) + "");
        lore.add("§c§l" + agui.c(agui.rankmapL1) + "");
        lore.add("§c" + agui.c(agui.rankmapL2) + "");
        lore.add(" ");
        vo.setDescription(lore);
        vo.addDisplay();

        type = Holograms.HOLOGRAM_TYPE.RANK_WON_RACEMODE;
        vo = new IconObject(this, "CREATE", 3, 4, 1, 1);
        icon = Holograms.getHead(type);
        vo.addItem(icon != null ? icon : paper);
        vo.setInformation(type);
        vo.setDisplayName("§f" + agui.c(agui.rankwonr));
        lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§f" + agui.c(agui.type) + ": §c" + type);
        lore.add("§f" + agui.c(agui.clickCreateNewHolograms) + "");
        lore.add(" ");
        vo.setDescription(lore);
        vo.addDisplay();

        type = Holograms.HOLOGRAM_TYPE.RANK_WON_GRANDPRIX;
        vo = new IconObject(this, "CREATE", 5, 4, 1, 1);
        icon = Holograms.getHead(type);
        vo.addItem(icon != null ? icon : paper);
        vo.setInformation(type);
        vo.setDisplayName("§f" + agui.c(agui.rankwong));
        lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§f" + agui.c(agui.type) + ": §c" + type);
        lore.add("§f" + agui.c(agui.clickCreateNewHolograms) + "");
        lore.add(" ");
        vo.setDescription(lore);
        vo.addDisplay();

    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    public static String requireNonNullWorld(World o) {
        if (o == null) {
            return Main.cmc().basicsettingnoprefix(Main.cmc().NONE, null);
        }
        return o.getName();
    }

    public static String requireNonNullMap(Map o) {
        if (o == null) {
            return Main.cmc().basicsettingnoprefix(Main.cmc().NONE, null);
        }
        return o.getName();
    }
}
