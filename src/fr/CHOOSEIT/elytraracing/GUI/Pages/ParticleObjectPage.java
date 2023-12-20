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
import fr.CHOOSEIT.elytraracing.GUI.VisibleObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.IconObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.AdditionalObject;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.CheckPoint;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.End;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParticleObjectPage extends MapPage implements MultiPagesModule {
    int pageNumber;
    ObjectClass objectClass;
    TypeMoment typeMoment;

    public enum TypeMoment {
        NORMAL,
        IFNEXT
    }

    public ParticleObjectPage(Player viewer, Map map, ObjectClass objectClass, TypeMoment typeMoment) {
        super(viewer, map);
        this.objectClass = objectClass;
        this.typeMoment = typeMoment;
    }

    public ObjectClass getObjectClass() {
        return objectClass;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    public void setParticle(String p) {
        Map map = getMap();
        if (p.equals("DEFAULT")) {
            p = null;
        }

        switch (typeMoment) {
            case NORMAL:
                objectClass.setEnumParticle(p);
                break;
            case IFNEXT:
                objectClass.setEnumParticle_IFNEXT(p);
                break;
        }
        sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
        map.save();
    }

    @Override
    public void click(VisibleObject visibleObject, ClickType clickType) {
        super.click(visibleObject, clickType);

        if (visibleObject.getId().equals("PAGE_PLUS")) {
            int currentpage = getPageNumber();
            displayPage(currentpage + 1);
        } else if (visibleObject.getId().equals("PAGE_MINUS")) {
            int currentpage = getPageNumber();
            if (currentpage == 1) {
                if (getInformationObjects().get(0).equals("END")) {
                    EndPage endPage = new EndPage(getViewer(), getMap(), (End) getInformationObjects().get(1));
                    endPage.View();
                    endPage.open();
                } else if (getInformationObjects().get(0).equals("CHECKPOINT")) {
                    CheckpointPage cpage = new CheckpointPage(getViewer(), getMap(), (CheckPoint) getInformationObjects().get(1));
                    cpage.View();
                    cpage.open();
                } else if (getInformationObjects().get(0).equals("AO")) {
                    AdditionalObjectPage page = new AdditionalObjectPage(getViewer(), getMap(), (AdditionalObject) getInformationObjects().get(1));
                    page.View();
                    page.open();
                }
                delete();
                return;
            }
            displayPage(currentpage - 1);
        } else if (visibleObject.getId().equals("PARTICLE_SET")) {
            setParticle((String) visibleObject.getInformation());
        }
    }

    @Override
    public void displayPage(int page) {
        if (page == 0) {
            page = 1;
        }
        pageNumber = page;

        removeObjects();

        ItemStack paper = new ItemStack(Material.PAPER);
        ArrayList<String> particules = new ArrayList<>(Arrays.asList("DEFAULT", "INVISIBLE"));
        particules.addAll(Main.availableParticle);
        int size = particules.size();
        IconObject vo;

        int startpage = MultiPagesModule.getIDPageStart(page, 36);
        int index = 0;

        List<String> lore = new ArrayList<String>(Arrays.asList("§7" + agui.c(agui.clickChangeParticle) + ""));

        for (int i = 0; i < 36; i++) {
            index = i + startpage;
            if (index >= size) {
                break;
            }

            vo = new IconObject(this, "PARTICLE_SET", i, 1, 1);
            vo.addItem(paper);
            vo.setInformation(particules.get(index));
            vo.setDisplayName("§f" + particules.get(index));
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

        IconObject icon = new IconObject(this, "PARTICLE", 4, 4, 1, 1);
        icon.addItem(new ItemStack(Material.NETHER_STAR));
        icon.setDescription(Arrays.asList("§7" + agui.c(agui.currentParticleSet) + ""));
        icon.addDisplay(true);
    }

    @Override
    public void setup() {
    }

    @Override
    public void update(VisibleObject visibleObject) {
        super.update(visibleObject);
        if (visibleObject.getId().equals("PARTICLE")) {
            if (typeMoment.equals(TypeMoment.IFNEXT)) {
                visibleObject.setDisplayName("§c" + ObjectClass.particleAsString(objectClass.getEnumParticle_IFNEXT()));
            } else if (typeMoment.equals(TypeMoment.NORMAL)) {
                visibleObject.setDisplayName("§c" + ObjectClass.particleAsString(objectClass.getEnumParticle()));
            }


        }
        visibleObject.display();
    }
}
