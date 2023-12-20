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

import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.GUI.HeadDatabase;
import fr.CHOOSEIT.elytraracing.GUI.Modules.MultiPagesModule;
import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.IconObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.NumberObject1x2;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.gamesystem.HostGameCreator;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HostMapChoosePage extends Page implements MultiPagesModule {

    public static CustomMessageConfig cmc = Main.customMessageConfig;
    private HostGameCreator creator;

    public HostMapChoosePage(Player viewer, String name) {
        super(cmc.HOSTMAP_CREATOR_INVENTORY_TITLE, viewer, 5);
        creator = new HostGameCreator(viewer, name);
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

        for (int i = 1; i < 8; i++) {
            IconObject ironbar = new IconObject(this, "SELECT", i + 9, 1, 1);
            ironbar.addItem(Utils.getItemStack(cmc.ITEM_HOSTSEPARATION_MATERIAL, Arrays.asList("WHITE_STAINED_GLASS_PANE:0","STAINED_GLASS_PANE:0"),cmc.basicsettingnoprefix(cmc.ITEM_HOSTSEPARATION_TITLE)));
            ironbar.setDescription(Collections.singletonList(cmc.basicsettingnoprefix(cmc.ITEM_HOSTSEPARATION_DESCRIPTION)));
            ironbar.addDisplay();
        }

        for (int i = 0; i < 27; i++) {
            index = i + startpage;
            if (index >= size) {
                break;
            }

            vo = new IconObject(this, "SELECT", i + 18, 1, 1);
            Map map = infos.get(index);
            vo.addItem(
                    !creator.getMaps().contains(map) ?
                            Utils.getItemStack(cmc.ITEM_HOSTMAP_MATERIAL_NOTSELECTED, Arrays.asList("PAPER"), "") :
                            Utils.getItemStack(cmc.ITEM_HOSTMAP_MATERIAL_SELECTED, Arrays.asList("EMERALD"), "")
            );
            vo.setInformation(map);
            vo.setDisplayName(cmc.basicsettingnoprefix((creator.getMaps().contains(map) ? cmc.ITEM_HOSTMAP_TITLE_SELECTED : cmc.ITEM_HOSTMAP_TITLE_NOTSELECTED).replace("{MAP_NAME}", map.getName())));

            vo.setObjectAction((o, c) -> {
                if(!creator.getMaps().contains(map) && creator.getMaps().size() < cmc.Max_maps_per_games)
                    creator.getMaps().add(map);
                else
                    creator.getMaps().remove(map);

                displayPage(getPageNumber());
            });
            vo.addDisplay();
        }
        if (index < size - 1) {
            vo = new IconObject(this, "PAGE_PLUS", 8, 1, 1, 1);
            vo.addItem(HeadDatabase.BLUE_RIGHT_ARROW);
            vo.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGERIGHT, null));
            vo.setDescription(Arrays.asList("§7" + cmc.basicsettingnoprefix(cmc.HOST_MAPCHOOSER_CHANGEPAGE) + ""));
            vo.setObjectAction((o, a) -> {
                HostMapChoosePage pageh = HostMapChoosePage.this;
                int currentpage = pageh.getPageNumber();
                pageh.displayPage(currentpage+1);
            });
            vo.addDisplay();
        }
        else{
            IconObject ironbar = new IconObject(this, "SELECT", 17, 1, 1);
            ironbar.addItem(Utils.getItemStack(cmc.ITEM_HOSTSEPARATION_MATERIAL, Arrays.asList("WHITE_STAINED_GLASS_PANE","STAINED_GLASS_PANE"),cmc.basicsettingnoprefix(cmc.ITEM_HOSTSEPARATION_TITLE)));
            ironbar.setDescription(Collections.singletonList(cmc.basicsettingnoprefix(cmc.ITEM_HOSTSEPARATION_DESCRIPTION)));
            ironbar.addDisplay();
        }
        if(page > 1){
            vo = new IconObject(this, "PAGE_MINUS", 0, 1, 1, 1);
            vo.addItem(HeadDatabase.RED_LEFT_ARROW);
            vo.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGELEFT, null));
            vo.setDescription(Arrays.asList("§7" + cmc.basicsettingnoprefix(cmc.HOST_MAPCHOOSER_CHANGEPAGE) + ""));
            vo.setObjectAction((o, a) -> {
                HostMapChoosePage pageh = HostMapChoosePage.this;
                int currentpage = pageh.getPageNumber();
                if(currentpage == 1)
                {
                    pageh.delete();
                    return;
                }
                pageh.displayPage(currentpage-1);
            });
            vo.addDisplay();
        }
        else{
            IconObject ironbar = new IconObject(this, "SELECT", 9, 1, 1);
            ironbar.addItem(Utils.getItemStack(cmc.ITEM_HOSTSEPARATION_MATERIAL, Arrays.asList("WHITE_STAINED_GLASS_PANE","STAINED_GLASS_PANE"),cmc.basicsettingnoprefix(cmc.ITEM_HOSTSEPARATION_TITLE)));
            ironbar.setDescription(Collections.singletonList(cmc.basicsettingnoprefix(cmc.ITEM_HOSTSEPARATION_DESCRIPTION)));
            ironbar.addDisplay();
        }

        IconObject io = new IconObject(this, "", 8);
        io.setItem(HeadDatabase.IRON_RIGHT_ARROW);
        io.setUpdateActionOnInteraction((o) -> {
            List<String> a = Arrays.asList(cmc.HOSTCREATOR_CREATE_DESCRIPTION);
            a = a.stream().map(e -> e.replace("{SLOTS}", String.valueOf(creator.getMaxPlayer()))
                    .replace("{MODE}", creator.maps.size() > 1 ? cmc.GRANDPRIX : cmc.RACEMODE)
                    .replace("{MAP_NUMBER}", String.valueOf(creator.maps.size()))
                    .replace("{NAME}", String.valueOf(creator.name))

            ).collect(Collectors.toList());
            io.setDescription(a);
            io.setDisplayName(cmc.basicsettingnoprefix(cmc.HOSTCREATOR_CREATE_TITLE));
        });
        io.setObjectAction((o, c) -> {
            if(creator.maps.size() == 0){
                getViewer().sendMessage(cmc.basicsetting(cmc.NOTENOUGH_MAP_HOSTCREATOR, getViewer()));
                return;
            }
            creator.build().rawPlayerjoin(getViewer());
            delete();
        });
        io.addDisplay(true);

        NumberObject1x2 maxPlayers = new NumberObject1x2(this, "MAX_PLAYER_COUNTS", 2, 0, creator.getMaxPlayer());
        maxPlayers.setUpdateActionOnInteraction(o -> {
            List<String> a = Arrays.asList(cmc.HOST_MAXPLAYERS_SLOTS);
            a = a.stream().map(e -> e.replace("{SLOTS}", String.valueOf(creator.getMaxPlayer()))).collect(Collectors.toList());
            maxPlayers.setDescription(a);
            maxPlayers.setDisplayName(cmc.HOSTCREATOR_MAXPLAYERS_TITLE.replace("{SLOTS}", String.valueOf(creator.getMaxPlayer())));
        });
        maxPlayers.setObjectAction((o, ct) -> {
            int adder = ((ct.equals(ClickType.RIGHT) || ct.equals(ClickType.SHIFT_RIGHT) ? -1 : 1) * (ct.equals(ClickType.SHIFT_RIGHT) || ct.equals(ClickType.SHIFT_LEFT) ? 10 : 1));
            int a = Utils.clamp(1, creator.getMaxPlayer() + adder,
                    Integer.MAX_VALUE);
            creator.setMaxPlayer(a);
            maxPlayers.setNumber(a);
        });
        maxPlayers.addDisplay();

        NumberObject1x2 nbOfMaps = new NumberObject1x2(this, "", 5, 0, creator.maps.size());
        nbOfMaps.setUpdateActionOnInteraction(o -> {
            nbOfMaps.setNumber(creator.maps.size());
            nbOfMaps.setDisplayName(cmc.HOSTCREATOR_MAPS_TITLE.replace("{MAP_NUMBER}", String.valueOf(creator.maps.size())));
            List<String> desc = new ArrayList<>();
            int i = 0;
            for (Map map : creator.maps) {
                desc.add(cmc.HOSTCREATOR_MAPS_DESCRIPTION.replace("{ORDER}", String.valueOf(++i)).replace("{MAP_NAME}", map.getName()));
            }
            nbOfMaps.setDescription(desc);
        });
        nbOfMaps.addDisplay(true);


    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

}

