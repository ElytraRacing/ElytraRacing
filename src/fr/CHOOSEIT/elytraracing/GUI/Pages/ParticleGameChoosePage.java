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
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.TrueFalseObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.addon.particle.PlayerParticle;
import fr.CHOOSEIT.elytraracing.addon.particle.file.ParticleConfig;
import fr.CHOOSEIT.elytraracing.addon.particle.file.ParticleCreator;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ParticleGameChoosePage extends Page implements MultiPagesModule {

    private Game game;
    public ParticleGameChoosePage(Player viewer, Game game) {
        super(ParticleConfig.particleConfig.GUI_PARTICLE_TITLE, viewer, 3);
        this.game = game;
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
        List<ParticleCreator> infos = new ArrayList<>();
        infos.add(ParticleCreator.NONE);
        ParticleCreator.getParticlelist().forEach(p -> {
            if(p.isPlayable() && getViewer().hasPermission(ParticleConfig.prefixPermission+p.getName().toLowerCase())) infos.add(p);
        });
        int size = infos.size();

        int startpage = MultiPagesModule.getIDPageStart(page, 2);
        int index = 0;

        Set<Integer> skipIndexs = new HashSet<>();
        skipIndexs.add(22);
        int deltaIndex = 0;

        TrueFalseObject disable = new TrueFalseObject(this, "DISABLEPARTICLE", 4,2, PlayerParticle.isDisableParticle(getViewer()));

        disable.setObjectAction((o, ct) -> PlayerParticle.setDisableParticle(getViewer().getUniqueId(), !disable.getStatus()));
        disable.setUpdateActionOnInteraction((o) -> {
            disable.setStatus(PlayerParticle.isDisableParticle(getViewer()));
            disable.setDisplayName(disable.getStatus() ? ParticleConfig.particleConfig.GUI_DISABLE_OTHERS_PARTICLES_SELECTED : ParticleConfig.particleConfig.GUI_DISABLE_OTHERS_PARTICLES_UNSELECTED);
            disable.setDescription(disable.getStatus() ? ParticleConfig.particleConfig.GUI_DISABLE_OTHERS_PARTICLES_LORE_SELECTED : ParticleConfig.particleConfig.GUI_DISABLE_OTHERS_PARTICLES_LORE_UNSELECTED);
        });
        disable.addDisplay();

        for (int i = 0; i < 36 + skipIndexs.size(); i++) {
            index = i + startpage - deltaIndex;
            if(skipIndexs.contains(i)){
                ++deltaIndex;
                continue;
            }
            if (index >= size) {
                break;
            }

            IconObject vo = new IconObject(this, "SELECTORPARTICLE", i, 1, 1);
            ParticleCreator pc = infos.get(index);
            vo.setItem(pc.getItem());
            vo.setUpdateActionOnInteraction((o) ->{
                vo.setDisplayName((isSelected(pc.isNone() ? null : pc.getName()) ?  ParticleConfig.particleConfig.GUI_ITEM_PARTICLE_TITLE_SELECTED : ParticleConfig.particleConfig.GUI_ITEM_PARTICLE_TITLE
                ).replace("{PARTICLE_NAME}", pc.getName()));
                ItemStack is = vo.getItems().get(0);
                ItemMeta im = is.getItemMeta();
                if(im != null){
                    if(isSelected(pc.isNone() ? null : pc.getName()) && ParticleConfig.particleConfig.GUI_ENCHANT_SELECTED){
                        im.addEnchant(Enchantment.DURABILITY, 1, false);
                        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    } else {
                        im.removeEnchant(Enchantment.DURABILITY);
                    }
                    is.setItemMeta(im);
                }
            });
            if(pc.equals(ParticleCreator.NONE)){
                vo.setObjectAction((o, ct) -> {
                    if(PlayerParticle.selectNone(getViewer()))
                        sendViewerMessage(ParticleConfig.particleConfig.MSG_SAVE_SUCCESSFUL);
                });
            }
            else{
                vo.setObjectAction((o, ct) -> {
                    if(PlayerParticle.select(getViewer(), pc, game))
                        sendViewerMessage(ParticleConfig.particleConfig.MSG_SAVE_SUCCESSFUL);
                });
            }

            vo.addDisplay(true);
        }
        if (index < size - 1) {
            IconObject vo = new IconObject(this, "PAGE_PLUS", 8, 4, 1, 1);
            vo.addItem(HeadDatabase.BLUE_RIGHT_ARROW);
            vo.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGERIGHT, null));
            vo.setDescription(Arrays.asList("§7" + ParticleConfig.particleConfig.GUI_CLICKCHANGE + ""));
            vo.addDisplay();
        }

        IconObject vo = new IconObject(this, "PAGE_MINUS", 0, 4, 1, 1);
        vo.addItem(HeadDatabase.RED_LEFT_ARROW);
        vo.setDisplayName(Main.cmc().basicsettingnoprefix(Main.cmc().CHANGEPAGELEFT, null));
        vo.setDescription(Arrays.asList("§7" + ParticleConfig.particleConfig.GUI_CLICKCHANGE + ""));
        vo.addDisplay();

    }
    private boolean isSelected(String particle){
        if(particle == null && !PlayerParticle.has(getViewer())) return true;

        return (PlayerParticle.has(getViewer())
                && PlayerParticle.getNameOfParticle(getViewer()).equalsIgnoreCase(particle));
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }
}
