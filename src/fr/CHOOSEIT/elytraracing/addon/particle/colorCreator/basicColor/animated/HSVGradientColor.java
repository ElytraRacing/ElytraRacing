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

package fr.CHOOSEIT.elytraracing.addon.particle.colorCreator.basicColor.animated;

import fr.CHOOSEIT.elytraracing.addon.particle.RGBConstant;

public class HSVGradientColor extends AnimatedColor {
    private final float hsvStart, hsvEnd;
    private boolean addingDirection;
    private boolean bounceBack;

    public HSVGradientColor(long duration, float hsvStartCoef, float hsvEndCoef, boolean addingDirection) {
        this(duration, hsvStartCoef, hsvEndCoef, addingDirection, false);
    }
    public HSVGradientColor(long duration, float hsvStartCoef, float hsvEndCoef, boolean addingDirection, boolean bounceBack) {
        super(duration);
        this.hsvStart = hsvStartCoef;
        this.hsvEnd = hsvEndCoef;
        this.addingDirection = addingDirection;
        this.bounceBack = bounceBack;
    }

    public void setAddingDirection(boolean addingDirection) {
        this.addingDirection = addingDirection;
    }

    @Override
    public RGBConstant getColor() {
        float factor = getFactor();
        if(bounceBack && hasReset()) addingDirection = !addingDirection;

        float calcfactor = addingDirection ? factor : 1 - factor;
        return HSVtoRGB(calcfactor * Math.abs(hsvStart - hsvEnd) + Math.min(hsvStart, hsvEnd), 1);
    }

    private RGBConstant HSVtoRGB(float hCoef, double saturation){
        return HSVtoRGB(hCoef, saturation, 1);
    }
    private RGBConstant HSVtoRGB(float hCoef, double saturation, double alpha) {
        /**
         * I lost the credit for this function, if you find it send me a message
         */
        double r = 0, g = 0, b = 0, f, p, q, t;
        int i;
        i = (int) (hCoef * 6);
        f = hCoef * 6 - i;
        p = alpha * (1 - saturation);
        q = alpha * (1 - f * saturation);
        t = alpha * (1 - (1 - f) * saturation);
        switch (i % 6) {
            case 0:
                r = alpha; g = t; b = p;
                break;
            case 1:
                r = q; g = alpha; b = p;
                break;
            case 2:
                r = p; g = alpha; b = t;
                break;
            case 3:
                r = p; g = q; b = alpha;
                break;
            case 4:
                r = t; g = p; b = alpha;
                break;
            case 5:
                r = alpha; g = p; b = q;
                break;
        }
        return new RGBConstant(
                (float) r,
                (float) g,
                (float) b);
    }

    //We suppose that saturation and alpha are always 1
    public static float RGBtoHSV(int r, int g, int b){
        /**
         * I lost the credit for this function, if you find it send me a message
         */
        float cH = 0;
        r=r/255; g=g/255; b=b/255;
        int minRGB = Math.min(r,Math.min(g,b));
        int maxRGB = Math.max(r,Math.max(g,b));

        if (minRGB == maxRGB) {
            return 0;
        }

        // Colors other than black-gray-white:
        float d = (r==minRGB) ? g-b : ((b==minRGB) ? r-g : b-r);
        float h = (r==minRGB) ? 3 : ((b==minRGB) ? 1 : 5);
        cH = 60*(h - d/(maxRGB - minRGB));
        return cH;
    }

}
