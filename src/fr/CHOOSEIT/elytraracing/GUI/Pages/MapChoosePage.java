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
import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.GUI.HeadDatabase;
import fr.CHOOSEIT.elytraracing.GUI.Modules.MultiPagesModule;
import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.IconObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapChoosePage extends Page implements MultiPagesModule {

    public static AdminGUIConfig agui = Main.adminGUIConfig;

    public MapChoosePage(Player viewer) {
        super(Main.adminGUIConfig.c(Main.adminGUIConfig.pageMCP), viewer, 5);
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
        CustomMessageConfig cmc = Main.cmc();
        removeObjects();
        pageNumber = page;

        ArrayList<Map> infos = new ArrayList<>(Map.maplist);
        int size = infos.size();
        IconObject vo;
        ItemStack paper = new ItemStack(Material.PAPER);

        int startpage = MultiPagesModule.getIDPageStart(page, 36);
        int index = 0;

        List<String> lore;
        Map map;
        ItemStack icon;

        for (int i = 0; i < 36; i++) {
            index = i + startpage;
            if (index >= size) {
                break;
            }

            vo = new IconObject(this, "SELECT", i, 1, 1);
            map = infos.get(index);
            vo.addItem(paper);
            vo.setInformation(map);
            vo.setDisplayName("§f" + map.getName());
            List<String> l = new ArrayList<>();
            l.add("§f» §7" + agui.c(agui.name) + ": §6" + map.getName());
            l.add(" ");
            l.add("§f» §7" + agui.c(agui.valid) + ": " + cmc.booleantoString(map.isValid()) + " " + "§f«» §7" + agui.c(agui.enabled) + ": " + cmc.booleantoString(map.isEnabled()));
            l.add(" ");
            int n = map.getCpNumber();
            int n2 = map.getEndNumber();
            int n3 = map.getAdditionalObjectsList().size();
            l.add("§f» §7" + agui.c(agui.checkpoints) + ": §c" + n + " " + "§f«» §7" + agui.c(agui.ends) + ": §c" + n2 + " " + "§f«» §7" + agui.c(agui.aos) + ": §c" + n3);
            l.add("§f» §7" + agui.c(agui.locations) + ": " + "§f» §7" + agui.c(agui.lobby) + ": " + cmc.booleantoString(map.getLocation_lobby() != null) + " " + "§f«» §7" + agui.c(agui.start) + ": " + cmc.booleantoString(map.getLocation_start() != null) + " " + "§f«» §7" + agui.c(agui.end) + ": " + cmc.booleantoString(map.getLocation_end() != null));
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
            l.add("§f» §7" + agui.c(agui.podium) + ": " + cmc.booleantoString(map.isPodium()));
            l.add("§f» §7" + agui.c(agui.laps) + ": §c" + map.getNumberoflaps());
            l.add(" ");

            l.add("§8» " + map.getUUID());
            vo.setDescription(l);
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

    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    public static String requireNonNullWorld(World o) {
        if (o == null) {
            return "None";
        }
        return o.getName();
    }

    public static String requireNonNullMap(Map o) {
        if (o == null) {
            return "None";
        }
        return o.getName();
    }
}
