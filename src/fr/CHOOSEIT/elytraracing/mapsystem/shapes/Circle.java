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

package fr.CHOOSEIT.elytraracing.mapsystem.shapes;

import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import org.bukkit.Location;

import java.util.ArrayList;

public class Circle implements IShape {
    public boolean Through(ObjectClass objectClass, Location last, Location loc, float radius_size, float deltaX, float deltaY, float deltaZ) {
        if (!objectClass.getWorld().equalsIgnoreCase(loc.getWorld().getName())) {
            return false;
        }
        float[] vectorplayer = {(float) (loc.getX() - last.getX()), (float) (loc.getY() - last.getY()), (float) (loc.getZ() - last.getZ())};
        float[] coordsplayer = {(float) (vectorplayer[0] + loc.getX()), (float) (vectorplayer[1] + loc.getY()), (float) (vectorplayer[2] + loc.getZ())};
        float[] middle = Utils.add(CreatePointWithAngle(objectClass, 0, 0), deltaX, deltaY, deltaZ);
        if (Utils.Distance(middle, coordsplayer) > radius_size + 1) {
            return false;
        }

        float[] coords1 = Utils.add(CreatePointWithAngle(objectClass, 0, radius_size), deltaX, deltaY, deltaZ);
        float[] coords2 = Utils.add(CreatePointWithAngle(objectClass, 180, radius_size), deltaX, deltaY, deltaZ);
        float[] coords3 = Utils.add(CreatePointWithAngle(objectClass, 90, radius_size), deltaX, deltaY, deltaZ);
        float[] coords4 = Utils.add(CreatePointWithAngle(objectClass, 270, radius_size), deltaX, deltaY, deltaZ);

        float[] vectorA = {coords2[0] - coords1[0], coords2[1] - coords1[1], coords2[2] - coords1[2]};
        float MagVectorA = (float) Math.sqrt(Math.pow(vectorA[0], 2) + Math.pow(vectorA[1], 2) + Math.pow(vectorA[2], 2));
        vectorA = new float[]{vectorA[0] / MagVectorA, vectorA[1] / MagVectorA, vectorA[2] / MagVectorA};
        float[] vectorB = {coords4[0] - coords3[0], coords4[1] - coords3[1], coords4[2] - coords3[2]};
        float MagVectorB = (float) Math.sqrt(Math.pow(vectorB[0], 2) + Math.pow(vectorB[1], 2) + Math.pow(vectorB[2], 2));
        vectorB = new float[]{vectorB[0] / MagVectorB, vectorB[1] / MagVectorB, vectorB[2] / MagVectorB};

        float[] vectorN = {vectorA[1] * vectorB[2] - vectorA[2] * vectorB[1], vectorA[2] * vectorB[0] - vectorA[0] * vectorB[2], vectorA[0] * vectorB[1] - vectorA[1] * vectorB[0]};
        float MagVectorN = (float) Math.sqrt(Math.pow(vectorN[0], 2) + Math.pow(vectorN[1], 2) + Math.pow(vectorN[2], 2));
        vectorN = new float[]{vectorN[0] / MagVectorN, vectorN[1] / MagVectorN, vectorN[2] / MagVectorN};
        float d = -(vectorN[0] * coords1[0] + vectorN[1] * coords1[1] + vectorN[2] * coords1[2]);
        float rp = vectorN[0] * vectorplayer[0] + vectorN[1] * vectorplayer[1] + vectorN[2] * vectorplayer[2];
        float lp = vectorN[0] * coordsplayer[0] + vectorN[1] * coordsplayer[1] + vectorN[2] * coordsplayer[2] + d;
        if (rp == 0) {
            return false;
        }
        float t = lp / -rp;
        if (t == 0) {
            return false;
        }
        float[] coordInter = {coordsplayer[0] + vectorplayer[0] * t, coordsplayer[1] + vectorplayer[1] * t, coordsplayer[2] + vectorplayer[2] * t};
        if (Utils.Distance(coordInter, middle) <= radius_size && Utils.Distance(coordInter, coordsplayer) <= 2) {
            return true;
        }
        return false;
    }

    public float[] CreatePointWithAngle(ObjectClass objectClass, float angle, float radius_size) {
        double angle_x = Math.toRadians(objectClass.getXDegrees());
        double angle_y = Math.toRadians(objectClass.getYDegrees());
        double angle_z = Math.toRadians(objectClass.getZDegrees());

        double x = objectClass.getX(), y = objectClass.getY(), z = objectClass.getZ();

        float[] coords = new float[3];
        coords[0] = (float) (radius_size * Math.cos(Math.toRadians(angle)));
        coords[1] = (float) (radius_size * Math.sin(Math.toRadians(angle)));
        coords[2] = 1;
        coords = objectClass.rotateX(angle_x, coords[0], coords[1], coords[2]);
        coords = objectClass.rotateY(angle_y, coords[0], coords[1], coords[2]);
        coords = objectClass.rotateZ(angle_z, coords[0], coords[1], coords[2]);
        coords[0] += x;
        coords[1] += y;
        coords[2] += z;

        return coords;
    }

    @Override
    public ArrayList<SimpleLocation> CreateShape(ObjectClass objectClass, int PRECISION, float radius_size) {
        double angle_x = Math.toRadians(objectClass.getXDegrees());
        double angle_y = Math.toRadians(objectClass.getYDegrees());
        double angle_z = Math.toRadians(objectClass.getZDegrees());

        double x = objectClass.getX(), y = objectClass.getY(), z = objectClass.getZ();

        ArrayList<SimpleLocation> arrayList = new ArrayList<>();
        for (int i = 0; i < 360 / PRECISION; i++) {
            float[] coords = new float[3];
            coords[0] = (float) (radius_size * Math.cos(Math.toRadians(PRECISION * i)));
            coords[1] = (float) (radius_size * Math.sin(Math.toRadians(PRECISION * i)));
            coords[2] = 1;

            coords = objectClass.rotateX(angle_x, coords[0], coords[1], coords[2]);
            coords = objectClass.rotateY(angle_y, coords[0], coords[1], coords[2]);
            coords = objectClass.rotateZ(angle_z, coords[0], coords[1], coords[2]);

            coords[0] += x;
            coords[1] += y;
            coords[2] += z;

            arrayList.add(new SimpleLocation(coords));

        }
        return arrayList;
    }
}
