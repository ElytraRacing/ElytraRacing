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

package fr.CHOOSEIT.elytraracing.GUI.Pages.HologramsPage;

import fr.CHOOSEIT.elytraracing.AdminGUIConfig;
import fr.CHOOSEIT.elytraracing.GUI.HeadDatabase;
import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.Pages.HologramsChoosePage;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.IconObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.TrueFalseObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.holograms.Holograms;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HologramsPage extends Page {
    public AdminGUIConfig agui = Main.adminGUIConfig;
    private Holograms holograms;

    public HologramsPage(Player viewer, Holograms holograms) {
        super(Main.adminGUIConfig.c(Main.adminGUIConfig.pageHologramPage), viewer, 3);
        this.holograms = holograms;
        agui = Main.adminGUIConfig;
    }

    @Override
    public void update(VisibleObject visibleObject) {
        if (visibleObject.getId().equals("INFO")) {
            visibleObject.setDisplayName("§f" + holograms.name);
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7x: §c" + holograms.location.getX());
            lore.add("§7y: §c" + holograms.location.getY());
            lore.add("§7z: §c" + holograms.location.getZ());
            lore.add("§7" + agui.c(agui.world + ": §c" + HologramsChoosePage.requireNonNullWorld(holograms.location.getWorld())));
            lore.add("§7" + agui.c(agui.map + ": §c" + HologramsChoosePage.requireNonNullMap(holograms.map)));
            lore.add("§7" + agui.c(agui.type + ": §c" + holograms.hologram_type));
            lore.add(" ");
            lore.add("§7" + agui.c(agui.name) + ": §c" + holograms.name);
            visibleObject.setDescription(lore);
            ItemStack icon = Holograms.getHead(holograms.hologram_type);
            ItemStack paper = new ItemStack(Material.PAPER);
            visibleObject.setItem(icon != null ? icon : paper);
            visibleObject.display();
        }
    }

    public Holograms getHolograms() {
        return holograms;
    }

    @Override
    public void open() {
        super.open();
        View();
    }

    @Override
    public void View() {
        IconObject vo = new IconObject(this, "INFO", 4, 1, 1);
        vo.setInformation(holograms.name);
        vo.addDisplay(true);

        vo = new IconObject(this, "NAME", 2, 1, 1, 1);
        vo.setItem(HeadDatabase.ORANGE_N);
        vo.setDisplayName("§f" + agui.c(agui.changeName));
        vo.setDescription(new ArrayList<>(Arrays.asList(" ", "§f" + agui.c(agui.changeHologramName) + ": ", "§c" + agui.c(agui.cmdChangeHologramName))));
        vo.addDisplay();

        vo = new IconObject(this, "MAP", 6, 1, 1, 1);
        vo.setItem(HeadDatabase.GREEN_M);
        vo.setDisplayName("§f" + agui.c(agui.changeMap));
        vo.setDescription(new ArrayList<>(Arrays.asList(" ", "§f" + agui.c(agui.changeHologramMap) + ": ", "§c" + agui.c(agui.cmdChangeHologramMap))));
        vo.addDisplay();

        vo = new IconObject(this, "LOC", 4, 1, 1, 1);
        vo.setItem(HeadDatabase.RED_L);
        vo.setDisplayName("§f" + agui.c(agui.localisation));
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§7x: §c" + holograms.location.getX());
        lore.add("§7y: §c" + holograms.location.getY());
        lore.add("§7z: §c" + holograms.location.getZ());
        lore.add("§7" + agui.c(agui.world + ": §c" + HologramsChoosePage.requireNonNullWorld(holograms.location.getWorld())));
        lore.add(" ");
        lore.add("§f" + agui.c(agui.leftTeleported));
        lore.add("§f" + agui.c(agui.rightChangeLocation));
        vo.setDescription(lore);
        vo.addDisplay();

        vo = new IconObject(this, "REFRESH", 3, 2, 1, 1);
        vo.setItem(HeadDatabase.STONE_REFRESH);
        vo.setDisplayName("§f" + agui.c(agui.refreshDisplay));
        vo.setDescription(new ArrayList<>(Arrays.asList(" ", "§f" + agui.c(agui.clickRefreshElement))));
        vo.addDisplay();

        vo = new IconObject(this, "DELETE", 5, 2, 1, 1);
        vo.setItem(HeadDatabase.FALSE);
        vo.setDisplayName("§c" + agui.c(agui.delete));
        vo.setDescription(new ArrayList<>(Arrays.asList(" ", "§f" + agui.c(agui.deleteHologram))));
        vo.addDisplay();
    }


}
