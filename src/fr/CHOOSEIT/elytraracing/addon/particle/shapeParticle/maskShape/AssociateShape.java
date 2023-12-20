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

package fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.maskShape;

import fr.CHOOSEIT.elytraracing.addon.particle.ShapeParticle;

public class AssociateShape implements ShapeParticle {

    private final ShapeParticle first, second;

    public AssociateShape(ShapeParticle first, ShapeParticle second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public float[][] coord() {
        float[][] firstcoords = first.coord();
        float[][] secondcoords = second.coord();

        float[][] coords = new float[firstcoords.length + secondcoords.length][3];
        int index = 0;
        for (float[] firstcoord : firstcoords)
            coords[index++] = firstcoord;
        for (float[] secondcoord : secondcoords)
            coords[index++] = secondcoord;
        return coords;
    }
}
