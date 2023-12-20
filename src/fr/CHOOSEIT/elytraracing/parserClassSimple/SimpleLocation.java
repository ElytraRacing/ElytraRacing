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

package fr.CHOOSEIT.elytraracing.parserClassSimple;

import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleLocation {
    public SimpleLocation() {

    }

    public SimpleLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yeild = loc.getYaw();
        this.pitch = loc.getPitch();

        this.Worldname = loc.getWorld().getName();
    }

    public String getWorldname() {
        return Worldname;
    }

    public SimpleLocation(float[] coords) {
        this.x = coords[0];
        this.y = coords[1];
        this.z = coords[2];
        this.yeild = 0;
        this.pitch = 0;
        this.Worldname = null;
    }

    public SimpleLocation(float[] coords, String worldname) {
        this.x = coords[0];
        this.y = coords[1];
        this.z = coords[2];
        this.yeild = 0;
        this.pitch = 0;
        this.Worldname = worldname;
    }

    public Location getlocation() {
        if (Worldname == null) {
            return null;
        }

        return new Location(Bukkit.getWorld(Worldname), x, y, z, yeild, pitch);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String CreateString() {
        String s = "";
        s = x + ":" + y + ":" + z + ":" + yeild + ":" + pitch + ":" + Worldname;
        return s;
    }

    public String CreateSimpleString() {
        String s = "";
        s = x + ":" + y + ":" + z + ":" + Worldname;
        return s;
    }

    public static SimpleLocation fromSimpleString(String s) {
        List<String> strings = new ArrayList<String>(Arrays.asList(s.split(":")));
        SimpleLocation sl = new SimpleLocation();
        if (Utils.IsDouble(strings.get(0)) && Utils.IsDouble(strings.get(1)) && Utils.IsDouble(strings.get(2)) && !Utils.IsDouble(strings.get(3))) {
            sl.x = Double.parseDouble(strings.get(0));
            sl.y = Double.parseDouble(strings.get(1));
            sl.z = Double.parseDouble(strings.get(2));
            sl.yeild = 0;
            sl.pitch = 0;
            sl.Worldname = strings.get(5);
            return sl;
        } else {
            return null;
        }

    }

    public static SimpleLocation fromString(String s) {
        List<String> strings = new ArrayList<String>(Arrays.asList(s.split(":")));
        SimpleLocation sl = new SimpleLocation();
        if (Utils.IsDouble(strings.get(0)) && Utils.IsDouble(strings.get(1)) && Utils.IsDouble(strings.get(2)) && Utils.IsDouble(strings.get(3)) && Utils.IsDouble(strings.get(4)) && !Utils.IsDouble(strings.get(5))) {
            sl.x = Double.parseDouble(strings.get(0));
            sl.y = Double.parseDouble(strings.get(1));
            sl.z = Double.parseDouble(strings.get(2));
            sl.yeild = Float.parseFloat(strings.get(3));
            sl.pitch = Float.parseFloat(strings.get(4));
            sl.Worldname = strings.get(5);
            return sl;
        } else {
            return null;
        }

    }

    public float getPitch() {
        return pitch;
    }

    public float getYeild() {
        return yeild;
    }

    double x, y, z;
    float yeild, pitch;
    String Worldname;
}
