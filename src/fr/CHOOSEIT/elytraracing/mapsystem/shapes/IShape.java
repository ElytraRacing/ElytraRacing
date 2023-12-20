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
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;

public interface IShape {
    ArrayList<IShape> shapes = new ArrayList<IShape>(Arrays.asList(new Circle()));

    boolean Through(ObjectClass objectClass, Location last, Location loc, float radius_size, float deltaX, float deltaY, float deltaZ);

    float[] CreatePointWithAngle(ObjectClass objectClass, float angle, float radius_size);

    ArrayList<SimpleLocation> CreateShape(ObjectClass objectClass, int PRECISION, float radius_size);

    static IShape getShape(String s) {
        for (IShape shape : shapes) {
            if (shape.getClass().getName().equalsIgnoreCase(IShape.class.getPackage().getName() + "." + s)) {
                return shape;
            }
        }
        return shapes.get(0);
    }
}
