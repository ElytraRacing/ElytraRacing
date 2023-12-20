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

import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.Pages.HologramsPage.HologramsPage;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.IconObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.TrueFalseObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.holograms.Holograms;
import fr.CHOOSEIT.elytraracing.holograms.HologramsCreator;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.AdditionalObject;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.CheckPoint;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.End;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ConfirmationPage extends Page {


    private String explaintitle;
    private List<String> explaindesc;

    Page previouspage;

    public ConfirmationPage(String pageName, Player viewer, Page previouspage, String explainttitle, List<String> explaindesc) {
        super(pageName, viewer);
        this.previouspage = previouspage;
        this.explaintitle = explainttitle;
        this.explaindesc = explaindesc;
        setup();
    }

    @Override
    public void setup() {
        TrueFalseObject left = new TrueFalseObject(this, "LEFT", 2, 2, false);
        left.setDisplayName("§c" + Main.adminGUIConfig.c(Main.adminGUIConfig.cancel));
        left.addDisplay();

        IconObject middle = new IconObject(this, "MIDDLE", 4, 2, 1, 1);
        middle.setDisplayName(explaintitle);
        middle.setDescription(explaindesc);
        middle.addItem(new ItemStack(Material.PAPER));
        middle.addDisplay();

        TrueFalseObject right = new TrueFalseObject(this, "RIGHT", 6, 2, true);
        right.setDisplayName("§a" + Main.adminGUIConfig.c(Main.adminGUIConfig.confirm));
        right.addDisplay();
    }

    @Override
    public void View() {

    }

    @Override
    public Object getInformation() {
        return null;
    }

    public void confirm() {
        boolean goprevious = true;
        if (getInformationObjects() != null) {
            ArrayList<Object> infos = getInformationObjects();
            if (infos.get(0).equals("Map")) {
                Map map = (Map) infos.get(1);
                if (infos.get(2).equals("ClearDB")) {
                    int map_id = Main.currentDataBase.get_map_id(map);
                    if (map_id > 0) {
                        Main.currentDataBase.Deleteinformations_Map(map_id);
                        sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
                    }
                }
            } else if (infos.get(0).equals("DELETE_OBJECT")) {
                if (infos.get(1).equals("END")) {
                    End end = (End) infos.get(2);
                    Map map = (Map) infos.get(3);
                    map.getEndsList().remove(end.getID(map.getEndsList()));
                    end.Delete();
                    Main.instance.getSerialization().saveMap(map);
                    sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
                } else if (infos.get(1).equals("CP")) {
                    CheckPoint checkpoint = (CheckPoint) infos.get(2);
                    Map map = (Map) infos.get(3);
                    map.getCheckpointsList().remove(checkpoint.getID(map.getCheckpointsList()));
                    checkpoint.Delete();
                    Main.instance.getSerialization().saveMap(map);
                    sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
                } else if (infos.get(1).equals("AO")) {
                    AdditionalObject additionalObject = (AdditionalObject) infos.get(2);
                    Map map = (Map) infos.get(3);
                    map.getAdditionalObjectsList().remove(additionalObject.getID(map.getAdditionalObjectsList()));
                    additionalObject.Delete();
                    Main.instance.getSerialization().saveMap(map);
                    sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
                }
            } else if (infos.get(0).equals("HOLOGRAM")) {
                if (infos.get(1).equals("DELETE")) {
                    Holograms holograms = (Holograms) infos.get(2);
                    HologramsCreator hc = Main.hologramsSaver.getHologramCreator(holograms);
                    if (hc != null) {
                        Main.hologramsSaver.Holograms.remove(hc);
                        Holograms.hologramslist.remove(holograms);
                        holograms.delete();
                        Main.serialization.saveHolograms();
                        sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
                        goprevious = false;
                    } else {
                        sendViewerMessage(Main.cmc().ERROR_MESSAGE);
                    }
                }
            }
        }
        if (goprevious) {
            goPreviousPage(true);
        } else {
            getViewer().closeInventory();
        }


    }

    public void goPreviousPage(boolean confirmed) {
        try {
            previouspage.open();
            previouspage.loadObjects();
        } catch (ConcurrentModificationException e) {

        }

        if (getInformationObjects() != null) {
            ArrayList<Object> infos = getInformationObjects();
            if (infos.get(0).equals("Map")) {
                Map map = (Map) infos.get(1);
                new MapPage(getViewer(), map).open();
            } else if (infos.get(0).equals("HOLOGRAM")) {
                Holograms holograms = (Holograms) infos.get(2);
                HologramsPage hp = new HologramsPage(getViewer(), holograms);
                hp.open();
            } else if (infos.get(0).equals("DELETE_OBJECT")) {
                if (infos.get(1).equals("END")) {
                    if (confirmed) {
                        EndChoosePage endChoosePage = new EndChoosePage(getViewer(), (Map) infos.get(3));
                        endChoosePage.open();
                        endChoosePage.displayPage(1);
                    } else {
                        EndPage endPage = new EndPage(getViewer(), (Map) infos.get(3), (End) infos.get(2));
                        endPage.View();
                        endPage.open();
                    }

                } else if (infos.get(1).equals("CP")) {
                    if (confirmed) {
                        CheckpointChoosePage p = new CheckpointChoosePage(getViewer(), (Map) infos.get(3));
                        p.open();
                        p.displayPage(1);
                    } else {
                        CheckpointPage checkpointPage = new CheckpointPage(getViewer(), (Map) infos.get(3), (CheckPoint) infos.get(2));
                        checkpointPage.View();
                        checkpointPage.open();
                    }

                } else if (infos.get(1).equals("AO")) {
                    if (confirmed) {
                        AdditionalObjectChoosePage additionalObjectChoosePage = new AdditionalObjectChoosePage(getViewer(), (Map) infos.get(3));
                        additionalObjectChoosePage.open();
                        additionalObjectChoosePage.displayPage(1);
                    } else {
                        AdditionalObjectPage additionalObjectPage = new AdditionalObjectPage(getViewer(), (Map) infos.get(3), (AdditionalObject) infos.get(2));
                        additionalObjectPage.View();
                        additionalObjectPage.open();
                    }

                }
            }

        }
        this.delete();
    }

    public void decline() {
        goPreviousPage(false);
    }
}
