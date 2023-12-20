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
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.maskShape.RotatedConstantShape;
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.maskShape.TranslationConstantShape;

import java.util.Arrays;

public class SquareParticle implements ShapeParticle {

    private final float[][] coords;

    public SquareParticle(int nbOfPoint, float length){
        float[][] coordsCreator = new float[(nbOfPoint + 2)*4][3];
        int i = 0;
        final float xStart = length / 2.0f;
        final float yStart = xStart;
        final ShapeParticle lineParticle = new LineParticle(nbOfPoint, length);
        float[][] coords1 = new TranslationConstantShape(lineParticle, 0, yStart, 0).coord();
        for (int i1 = 0; i1 < coords1.length; i1++) {
            coordsCreator[i++] = coords1[i1];
        }
        float[][] coords2 = new TranslationConstantShape(lineParticle, 0, -yStart, 0).coord();
        for (int i1 = 0; i1 < coords2.length; i1++) {
            coordsCreator[i++] = coords2[i1];
        }
        float[][] coords3 = new TranslationConstantShape(new RotatedConstantShape(lineParticle, 0, 0, (float) Math.PI/2), xStart, 0, 0).coord();
        for (int i1 = 0; i1 < coords3.length; i1++) {
            coordsCreator[i++] = coords3[i1];
        }
        float[][] coords4 = new TranslationConstantShape(new RotatedConstantShape(lineParticle, 0, 0, (float) Math.PI/2), -xStart, 0, 0).coord();
        for (int i1 = 0; i1 < coords4.length; i1++) {
            coordsCreator[i++] = coords4[i1];
        }
        coords = coordsCreator;
    }

    @Override
    public float[][] coord() {
        return Arrays.copyOf(coords, coords.length);
    }
}
