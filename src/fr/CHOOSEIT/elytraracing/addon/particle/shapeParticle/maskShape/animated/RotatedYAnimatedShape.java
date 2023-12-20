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

package fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.maskShape.animated;

import fr.CHOOSEIT.elytraracing.addon.particle.ShapeParticle;
import fr.CHOOSEIT.elytraracing.utils.Utils;

public class RotatedYAnimatedShape extends MaskAnimatedShape {
    private final boolean adding;
    private final float deltaAngle;
    public RotatedYAnimatedShape(long duration, ShapeParticle shapeParticle, boolean adding) {
        this(duration, 0, shapeParticle, adding);
    }

    public RotatedYAnimatedShape(float angle, ShapeParticle shapeParticle) {
        this(0, angle, shapeParticle, true);
    }
    public RotatedYAnimatedShape(long duration, float angle, ShapeParticle shapeParticle, boolean adding) {
        super(duration, shapeParticle);
        this.deltaAngle = angle;
        this.adding = adding;
    }

    @Override
    public float[][] coord() {
        float[][] coords = shapeParticle.coord();
        for (int i = 0; i < coords.length; i++) {
            coords[i] = Utils.rotate( 0, (float) (Math.toRadians(deltaAngle) + (float) (adding ? 2*Math.PI*getFactor() : 1 - 2*Math.PI*getFactor())), 0, coords[i]);
        }
        return coords;
    }
}
