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

import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.api.elytraracing.viewer.erObject.ERCheckPoint;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CheckPoint extends ObjectClass implements ERCheckPoint {
    public static CustomMessageConfig cmc = Main.customMessageConfig;

    public void ApplyBoost(Player player) {
        Utils.ApplyBoost(player, boostMultiplier);
    }

    public boolean Through(Location last, Location loc, long deltaTime) {
        return getShape().Through(this, last, loc, radius_size, getDeltaX(deltaTime), getDeltaY(deltaTime), getDeltaZ(deltaTime));
    }

    public void setRadius_size(float size) {
        this.radius_size = size;
        ReloadShow();
    }


    public enum TYPE_ANGLE_CHECKPOINT {
        PLAYER,
        NEXT_CHECKPOINT
    }


    public ArrayList<Integer> wrotelinked() {
        if (LinkedTo_id_order == null) {
            return new ArrayList<>();
        }
        return LinkedTo_id_order;
    }

    public ArrayList<Integer> getLinkedTo_id_order(Map map) {
        int id = getID(map.getCheckpointsList());
        ArrayList arr = new ArrayList<Integer>(searchLinks(null, id, map));
        arr.remove((Object) id);
        return arr;
    }

    @Override
    public void setID_JFI(Map map) {
        ID_justforindication = getID(map.getCheckpointsList());
    }

    public void AddLink(int myid_order, int id_order) {
        if (LinkedTo_id_order == null) {
            LinkedTo_id_order = new ArrayList<>();
            LinkedTo_id_order.add(id_order);
        } else if (!LinkedTo_id_order.contains(id_order) && myid_order != id_order) {
            LinkedTo_id_order.add(id_order);
        }
    }


    public void RemoveLink(int id_order) {
        if (LinkedTo_id_order != null && LinkedTo_id_order.contains(id_order)) {
            ArrayList<Integer> newarr = new ArrayList<>();
            for (Integer integer : LinkedTo_id_order) {
                if (integer == id_order) {
                    continue;
                }
                newarr.add(integer);
            }

            LinkedTo_id_order = newarr;
        }
    }

    public static Set<Integer> searchLinks(Set<Integer> current, int id, Map map) {
        if (current == null) {
            current = new HashSet<>();
        }
        if (map == null) {
            return current;
        }
        ArrayList<CheckPoint> cps = map.getCheckpointsList();
        if (cps == null || id >= cps.size() || cps.get(id) == null) {
            return current;
        }
        CheckPoint crr = cps.get(id);
        for (Integer integer : crr.wrotelinked()) {
            if (integer != id && !current.contains(integer)) {
                current.add(integer);
                current.addAll(searchLinks(current, integer, map));
            }
        }
        return new HashSet<>(current);

    }

    public CheckPoint(float x, float y, float z, String world, float radius_size, int radius_bold, float x_degrees, float y_degrees, float z_degrees, float boostMultiplier) {
        super(x, y, z, world, radius_bold, x_degrees, y_degrees, z_degrees);
        this.radius_size = radius_size;
        this.boostMultiplier = boostMultiplier;
        setShape("Circle");
    }

    public void ReloadShow() {

        int PRECISION = 2 * Main.customMessageConfig.ParticleRatio;
        if (PRECISION < 0 || PRECISION > 50) {
            PRECISION = 15;
        }

        ArrayList<SimpleLocation> arrayList = getShape().CreateShape(this, PRECISION, radius_size);
        RenderObjectCache.Save(this, arrayList);
    }

    public void setBoostMultiplier(float boostMultiplier) {
        this.boostMultiplier = boostMultiplier;
    }

    public float getBoostMultiplier() {
        return boostMultiplier;
    }

    public float[] getCoord() {
        return new float[]{x, y, z};
    }

    public float getRadiusSize() {
        return radius_size;
    }

    private float radius_size;
    private float boostMultiplier;

    protected ArrayList<Integer> LinkedTo_id_order = new ArrayList<>();
}
