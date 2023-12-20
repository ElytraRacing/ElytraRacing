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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicComposite implements ShapeParticle {

    //ShapeParticle and basicApplier have to be non null
    private final ShapeParticle shapeParticle, basicApplier;

    public BasicComposite(ShapeParticle shapeParticle, ShapeParticle basicApplier) {
        this.shapeParticle = shapeParticle;
        this.basicApplier = basicApplier;
    }

    @Override
    public float[][] coord() {
        float[][] previouscoords = shapeParticle.coord();
        List<float[][]> newcoords = new ArrayList<>();
        float[][] applierCoords = basicApplier.coord();
        for (int i = 0; i < previouscoords.length; i++) {
            newcoords.add(translationCoords(applierCoords.clone(), previouscoords[i][0], previouscoords[i][1], previouscoords[i][2]));
        }
        return flatten(Collections.unmodifiableList(newcoords), applierCoords.length);
    }

    private float[][] translationCoords(float[][] basicCoords, float dx, float dy, float dz){
        if(basicCoords.length == 0) return basicCoords;
        float[][] newcoords = new float[basicCoords.length][basicCoords[0].length];
        for (int i = 0; i < basicCoords.length; i++) {
            newcoords[i] = new float[]{basicCoords[i][0] + dx, basicCoords[i][1] + dy, basicCoords[i][2] + dz};
        }
        return newcoords;
    }

    private float[][] flatten(List<float[][]> listOfCoords, int itemInEachList){
        float[][] coords = new float[listOfCoords.size()*itemInEachList][3];
        int index = 0;
        for (float[][] co : listOfCoords) {
            for (int i = 0; i < co.length; i++) {
                coords[index++] = co[i];
            }
        }
        return coords;
    }
}
