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

public abstract class TranslationVariableShape implements ShapeParticle {
    private final ShapeParticle underlyingParticle;
    private float xDelta, yDelta, zDelta;

    public TranslationVariableShape(ShapeParticle underlyingParticle) {
        this(underlyingParticle, 0, 0, 0);
    }

    public TranslationVariableShape(ShapeParticle underlyingParticle, float xDelta, float yDelta, float zDelta) {
        this.underlyingParticle = underlyingParticle;
        this.xDelta = xDelta;
        this.yDelta = yDelta;
        this.zDelta = zDelta;
    }

    @Override
    public float[][] coord() {
        float[][] coords = underlyingParticle.coord();
        for (int i = 0; i < coords.length; i++) {
            coords[i] = Utils.add(coords[i], xDelta, yDelta, zDelta);
        }
        return coords;
    }

    public ShapeParticle getUnderlyingParticle() {
        return underlyingParticle;
    }

    public float getxDelta() {
        return xDelta;
    }

    public void setxDelta(float xDelta) {
        this.xDelta = xDelta;
    }

    public float getyDelta() {
        return yDelta;
    }

    public void setyDelta(float yDelta) {
        this.yDelta = yDelta;
    }

    public float getzDelta() {
        return zDelta;
    }

    public void setzDelta(float zDelta) {
        this.zDelta = zDelta;
    }
}
