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

package fr.CHOOSEIT.elytraracing.addon.particle;

import org.bukkit.entity.Player;

public class MainParticleTest {

    public MainParticleTest(Player p){
        /*
        ShapeParticle pp = new RotatedPlayerShape(new CircleParticle(5, 0.75f), p);
        ColorCreator cc = new RainbowColor(20000, true);
        new InfiniteGameTimer(1) {
            @Override
            protected void event(int timeLeft) {
                super.event(timeLeft);
                Location loc = p.getLocation();
                float x = (float) (loc.getX());
                float y = (float) (loc.getY());
                float z = (float) (loc.getZ());
                float[][] pl = pp.coord();
                RGBConstant rgb = cc.getColor();
                for (int i = 0; i < pl.length; i++) {
                    Utils.sendColoredParticle(p, "REDSTONE",x + pl[i][0], y + pl[i][1], z + pl[i][2], rgb.r, rgb.g, rgb.b, 1);
                }
            }
        }.start();

         */
    }
}
