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

package fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.basicShape;

import fr.CHOOSEIT.elytraracing.addon.particle.ShapeParticle;

import java.util.Arrays;

public class CircleParticle implements ShapeParticle {
    private final float[][] coords;

    public CircleParticle(int nbOfPoint, float radius){
        float[][] coordsCreator = new float[nbOfPoint][3];
        double sep = 2* Math.PI / nbOfPoint;
        for (int i = 0; i < coordsCreator.length; i++) {
            coordsCreator[i][0] = (float) (radius * Math.cos(sep*i));
            coordsCreator[i][1] = (float) (radius * Math.sin(sep*i));
            coordsCreator[i][2] = 0;
        }
        coords = coordsCreator;
    }

    @Override
    public float[][] coord() {
        return Arrays.copyOf(coords, coords.length);
    }
}
