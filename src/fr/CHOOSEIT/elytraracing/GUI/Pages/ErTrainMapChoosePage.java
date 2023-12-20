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

import fr.CHOOSEIT.elytraracing.GUI.HeadDatabase;
import fr.CHOOSEIT.elytraracing.GUI.Modules.MultiPagesModule;
import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.IconObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.SqlHandle.DataBase;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ErTrainMapChoosePage extends Page implements MultiPagesModule {

    public ErTrainMapChoosePage(String pageName, Player viewer) {
        super(pageName, viewer, 5);
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
        pageNumber = page;
        removeObjects();
        ItemStack paper = new ItemStack(Material.PAPER);
        ArrayList<String> infos = new ArrayList<>();

        for (String map : Main.cmc().ER_TRAIN_MAPS) {
            if (Map.getMapName(map) != null && Map.getMapName(map).isValid() && Map.getMapName(map).isEnabled()) {
                infos.add(map);
            }
        }
        int size = infos.size();
        IconObject vo;

        int startpage = MultiPagesModule.getIDPageStart(page, 36);
        int index = 0;

        List<String> lore;
        DataBase.Playerinfos pinfo = DataBase.Playerinfos.getPlayerInfos(Main.currentDataBase.get_player_id(getViewer()));
        Map map;

        for (int i = 0; i < 36; i++) {
            index = i + startpage;
            if (index >= size) {
                break;
            }

            vo = new IconObject(this, "SET", i, 1, 1);
            vo.addItem(paper);
            vo.setInformation(infos.get(index));
            map = Map.getMapName(infos.get(index));
            DataBase.PlayerMapinfos mapinfos = pinfo.getMap(map.getUUID());
            vo.setDisplayName(messageTransformation(Main.cmc().ERTRAIN_MAP_INFO_TITLE, mapinfos, map));
            lore = new ArrayList<>();
            if (mapinfos != null) {
                for (String s : Main.cmc().ERTRAIN_MAP_INFO) {
                    lore.add(messageTransformation(s, mapinfos, map));
                }
            }
            for (String s : Main.cmc().ERTRAIN_MAP_INFO_PLUS) {
                lore.add(messageTransformation(s, mapinfos, map));
            }
            vo.setDescription(lore);
            vo.addDisplay();
        }
        if (index < size - 1) {
            vo = new IconObject(this, "PAGE_PLUS", 8, 4, 1, 1);
            vo.addItem(HeadDatabase.BLUE_RIGHT_ARROW);
            vo.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGERIGHT, null));
            vo.setDescription(Arrays.asList(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGE, null)));
            vo.addDisplay();
        }

        vo = new IconObject(this, "PAGE_MINUS", 0, 4, 1, 1);
        vo.addItem(HeadDatabase.RED_LEFT_ARROW);
        vo.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGELEFT, null));
        vo.setDescription(Arrays.asList(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGE, null)));
        vo.addDisplay();

    }

    public String messageTransformation(String s, DataBase.PlayerMapinfos mapinfos, Map map) {
        if (mapinfos != null) {
            s = s.replace("{PLAYER_TIME}", Utils.timediffToString(mapinfos.player_time)).replace("{PLAYER_RANK}", String.valueOf(mapinfos.ranking)).replace("{WINRATE}", String.valueOf(mapinfos.winrate));
        }
        if (map != null) {
            s = s.replace("{DIFFICULTY}", map.getDifficultyMSG()).replace("{MAP_NAME}", map.getName()).replace("{LAPS}", String.valueOf(map.getNumberoflaps()));
        }
        return s;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }
}
