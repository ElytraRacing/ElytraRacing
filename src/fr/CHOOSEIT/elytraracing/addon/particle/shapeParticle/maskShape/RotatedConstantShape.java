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
import fr.CHOOSEIT.elytraracing.utils.Utils;

public class RotatedConstantShape implements ShapeParticle{
    private final ShapeParticle shapeParticle;
    private final float angleRadiansX;
    private final float angleRadiansY;
    private final float angleRadiansZ;

    public RotatedConstantShape(ShapeParticle shapeParticle, float angleRadiansX, float angleRadiansY, float angleRadiansZ) {
        this.shapeParticle = shapeParticle;
        this.angleRadiansX = angleRadiansX;
        this.angleRadiansY = angleRadiansY;
        this.angleRadiansZ = angleRadiansZ;
    }

    @Override
    public float[][] coord() {
        float[][] coords = shapeParticle.coord();
        for (int i = 0; i < coords.length; i++) {
            coords[i] = Utils.rotate(angleRadiansX, angleRadiansY, angleRadiansZ, coords[i]);
        }
        return coords;
    }
}
