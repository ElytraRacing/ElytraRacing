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

package fr.CHOOSEIT.elytraracing.GUI.Pages.MapPages;

import fr.CHOOSEIT.elytraracing.AdminGUIConfig;
import fr.CHOOSEIT.elytraracing.GUI.HeadDatabase;
import fr.CHOOSEIT.elytraracing.GUI.Modules.MultiPagesModule;
import fr.CHOOSEIT.elytraracing.GUI.Pages.MapPage;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.IconObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParticleMapPage extends MapPage implements MultiPagesModule {

    public static enum ParticlePageType {
        CHECKPOINT,
        END,
        CHECKPOINT_IFNEXT,
        END_IFNEXT,
        AO,
        AO_IFNEXT
    }

    int pageNumber;
    ParticlePageType particlePageType;
    public static AdminGUIConfig agui = Main.adminGUIConfig;

    public ParticleMapPage(Player viewer, Map map, ParticlePageType particlePageType) {
        super(viewer, map);
        this.particlePageType = particlePageType;
    }

    @Override
    public void update(VisibleObject visibleObject) {
        if (visibleObject.getId().equals("PARTICLE")) {
            String prefix = "§7" + agui.c(agui.currentParticle + ": §c");
            switch (getParticlePageType()) {

                case CHECKPOINT:
                    visibleObject.setDisplayName(prefix + getMap().getDefault_particle_effect_random());
                    break;
                case CHECKPOINT_IFNEXT:
                    visibleObject.setDisplayName(prefix + getMap().getDefault_particle_effect_random_ifnext());
                    break;
                case END:
                    visibleObject.setDisplayName(prefix + getMap().getDefault_particle_effect_end());
                    break;
                case END_IFNEXT:
                    visibleObject.setDisplayName(prefix + getMap().getDefault_particle_effect_end_ifnext());
                    break;
                case AO:
                    visibleObject.setDisplayName(prefix + getMap().getDefault_particle_effect_additonalObject());
                    break;
                case AO_IFNEXT:
                    visibleObject.setDisplayName(prefix + getMap().getDefault_particle_effect_additonalObject_ifnext());
                    break;
            }
            visibleObject.display();
        }
    }

    public ParticlePageType getParticlePageType() {
        return particlePageType;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    public void setParticle(String p, ParticlePageType particlePageType) {
        Map map = getMap();
        if (p.equals("DEFAULT")) {
            p = null;
        }
        if (particlePageType == null) {
            return;
        }

        switch (particlePageType) {
            case CHECKPOINT:
                map.setDefault_particle_effect_random(p);
                break;
            case CHECKPOINT_IFNEXT:
                map.setDefault_particle_effect_random_ifnext(p);
                break;
            case END:
                map.setDefault_particle_effect_end(p);
                break;
            case END_IFNEXT:
                map.setDefault_particle_effect_end_ifnext(p);
                break;
            case AO:
                map.setDefault_particle_effect_additonalObject(p);
                break;
            case AO_IFNEXT:
                map.setDefault_particle_effect_additonalObject_ifnext(p);
        }
        sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
        map.save();
    }

    @Override
    public void displayPage(int page) {
        if (page == 0) {
            page = 1;
        }
        pageNumber = page;

        removeObjects();

        ItemStack paper = new ItemStack(Material.PAPER);
        ArrayList<String> particules = new ArrayList<>(Arrays.asList("INVISIBLE"));
        particules.addAll(Main.availableParticle);
        int size = particules.size();
        IconObject vo;

        int startpage = MultiPagesModule.getIDPageStart(page, 36);
        int index = 0;

        List<String> lore = new ArrayList<String>(Arrays.asList("§7" + agui.c(agui.clickChangeParticle)));

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
            vo.setDescription(Arrays.asList("§7" + agui.c(agui.clickChangePage)));
            vo.addDisplay();
        }

        vo = new IconObject(this, "PAGE_MINUS", 0, 4, 1, 1);
        vo.addItem(HeadDatabase.RED_LEFT_ARROW);
        vo.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGELEFT, null));
        vo.setDescription(Arrays.asList("§7" + agui.c(agui.clickChangePage)));
        vo.addDisplay();

        IconObject icon = new IconObject(this, "PARTICLE", 4, 4, 1, 1);
        icon.addItem(new ItemStack(Material.NETHER_STAR));
        icon.setDescription(Arrays.asList("§7" + agui.c(agui.currentParticleLongMap)));
        icon.addDisplay(true);
    }

    @Override
    public void setup() {
    }

    @Override
    public Object getInformation() {
        Object o = new ArrayList<Object>(Arrays.asList(getMap(), this.particlePageType));
        return o;
    }
}
