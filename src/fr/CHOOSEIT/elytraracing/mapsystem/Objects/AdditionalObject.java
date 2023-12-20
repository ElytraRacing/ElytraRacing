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

package fr.CHOOSEIT.elytraracing.mapsystem.Objects;

import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.api.elytraracing.viewer.erObject.ERAdditionalObject;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class AdditionalObject extends ObjectClass implements ERAdditionalObject {

    public AdditionalObject(float x, float y, float z, String world, int particle_amount, float x_degrees, float y_degrees, float z_degrees, float radius_size) {
        super(x, y, z, world, particle_amount, x_degrees, y_degrees, z_degrees);
        this.radius_size = radius_size;
        this.fireworks = 0;
        setShape("Circle");
    }

    public void setFireworks(int fireworks) {
        if (fireworks > 64) {
            fireworks = 64;
        }
        if (fireworks < 0) {
            fireworks = 0;
        }
        this.fireworks = fireworks;
    }

    @Override
    public void ReloadShow() {
        int PRECISION = 2 * Main.customMessageConfig.ParticleRatio;
        if (PRECISION < 0 || PRECISION > 50) {
            PRECISION = 15;
        }

        ArrayList<SimpleLocation> arrayList = getShape().CreateShape(this, PRECISION, radius_size);
        RenderObjectCache.Save(this, arrayList);
    }

    public boolean Through(Location last, Location loc, long deltaTime) {
        return getShape().Through(this, last, loc, radius_size, getDeltaX(deltaTime), getDeltaY(deltaTime), getDeltaZ(deltaTime));
    }

    public void setBoostmultiplier(float boostmultiplier) {
        this.boostmultiplier = boostmultiplier;
    }

    public float getBoostmultiplier() {
        return boostmultiplier;
    }

    public int getFireworks() {
        return fireworks;
    }

    public void applyPlus(Player player) {
        int slot_firework = cmc.ITEM_FIREWORK_SLOT;
        if (slot_firework < 0 || slot_firework > 8) {
            slot_firework = 2;
        }
        ItemStack fireworks = player.getInventory().getItem(slot_firework);
        int currentfirework = 0;
        if (fireworks != null && fireworks.getType() == Material.getMaterial("FIREWORK")) {
            currentfirework = fireworks.getAmount();
        }

        int newfirework = currentfirework + this.fireworks;
        if (newfirework > 64) {
            newfirework = 64;
        }
        if (fireworks != null) {
            fireworks.setAmount(newfirework);
        } else {
            player.getInventory().setItem(slot_firework, new ItemStack(Material.getMaterial("FIREWORK"), this.fireworks));
        }
        if (boostmultiplier != 0) {
            Utils.ApplyBoost(player, boostmultiplier);
        }
    }

    public float[] getCoord() {
        return new float[]{x, y, z};
    }

    @Override
    public void setID_JFI(Map map) {
        ID_justforindication = getID(map.getAdditionalObjectsList());
    }

    public float getRadiusSize() {
        return radius_size;
    }

    public void setRadius_size(float radius_size) {
        this.radius_size = radius_size;
    }

    private float radius_size;
    private int fireworks;
    private float boostmultiplier;
}
