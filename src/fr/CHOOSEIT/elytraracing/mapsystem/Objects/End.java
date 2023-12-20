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
import fr.CHOOSEIT.elytraracing.api.elytraracing.viewer.erObject.EREnd;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import org.bukkit.Location;

import java.util.ArrayList;

public class End extends ObjectClass implements EREnd {
    public static CustomMessageConfig cmc = Main.customMessageConfig;

    public boolean Through(Location last, Location loc, long deltaTime) {
        return getShape().Through(this, last, loc, radius_size, getDeltaX(deltaTime), getDeltaY(deltaTime), getDeltaZ(deltaTime));
    }

    public void setRadius_size(float size) {
        this.radius_size = size;
        ReloadShow();
    }

    public End(float x, float y, float z, String world, float radius_size, int radius_bold, float x_degrees, float y_degrees, float z_degrees) {
        super(x, y, z, world, radius_bold, x_degrees, y_degrees, z_degrees);
        this.radius_size = radius_size;
        setShape("Circle");
    }

    @Override
    public void setID_JFI(Map map) {
        ID_justforindication = getID(map.getEndsList());
    }

    public void ReloadShow() {

        int PRECISION = 2 * Main.customMessageConfig.ParticleRatio;
        if (PRECISION < 0 || PRECISION > 50) {
            PRECISION = 15;
        }

        ArrayList<SimpleLocation> arrayList = getShape().CreateShape(this, PRECISION, radius_size);
        RenderObjectCache.Save(this, arrayList);
    }

    public float[] getCoord() {
        return new float[]{x, y, z};
    }

    public float getRadiusSize() {
        return radius_size;
    }

    private float radius_size;
}
