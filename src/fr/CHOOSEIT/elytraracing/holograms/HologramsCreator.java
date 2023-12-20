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

package fr.CHOOSEIT.elytraracing.holograms;

import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class HologramsCreator {
    private float x, y, z;
    private String world, hologram_type, option_map, name;

    public HologramsCreator(float x, float y, float z, String worldname, Holograms.HOLOGRAM_TYPE hologram_type, String option_map) {
        this(x, y, z, worldname, hologram_type, option_map, UUID.randomUUID().toString().substring(0, 6));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOption_map(String option_map) {
        this.option_map = option_map;
    }

    public void setLocation(Location loc) {
        this.x = (float) loc.getX();
        this.y = (float) loc.getY();
        this.z = (float) loc.getZ();
        this.world = loc.getWorld().getName();
    }

    public Holograms getHologram() {
        for (Holograms holograms : Holograms.hologramslist) {
            if (holograms != null && holograms.name.equals(name) && ((holograms.map == null && option_map == null) || (holograms.map != null && holograms.map.getName().equalsIgnoreCase(option_map)))) {
                return holograms;
            }
        }
        return null;
    }

    public HologramsCreator(float x, float y, float z, String worldname, Holograms.HOLOGRAM_TYPE hologram_type, String option_map, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = worldname;
        this.hologram_type = hologram_type.toString();
        this.option_map = option_map;
        if (name == null) {
            this.name = UUID.randomUUID().toString().substring(0, 6);
        } else {
            this.name = name;
        }

    }

    public void create() {
        if (this.world.equals("<worldname>")) {
            return;
        }
        World w = Bukkit.getWorld(this.world);
        if (w == null) {
            Utils.log_print("[ElytraRacing] The world '" + this.world + "' can't be found");
            return;
        }
        Holograms.HOLOGRAM_TYPE hologramtype = Holograms.HOLOGRAM_TYPE.valueOf(this.hologram_type);
        if (hologramtype == null) {
            Utils.log_print("[ElytraRacing] Hologram type '" + this.hologram_type + "' can't be found");
            return;
        }
        if (name == null || name.isEmpty()) {
            name = UUID.randomUUID().toString().substring(0, 6);
        }

        if (hologramtype.equals(Holograms.HOLOGRAM_TYPE.RANK_MAP)) {
            Map m = Map.Find(option_map);
            if (m == null) {
                Utils.log_print("[ElytraRacing] The map '" + this.option_map + "' can't be found");
            }
            Holograms h = new Holograms(new Location(w, x, y, z), hologramtype, m, name);
        }
        Holograms h = new Holograms(new Location(w, x, y, z), hologramtype, null, name);
    }
}
