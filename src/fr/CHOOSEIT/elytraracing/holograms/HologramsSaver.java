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

package fr.CHOOSEIT.elytraracing.holograms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class HologramsSaver {
    public ArrayList<HologramsCreator> Holograms = new ArrayList<HologramsCreator>(Arrays.asList(
            new HologramsCreator(0, 0, 0, "<worldname>", fr.CHOOSEIT.elytraracing.holograms.Holograms.HOLOGRAM_TYPE.PERSONAL_STATS, null),
            new HologramsCreator(0, 0, 0, "<worldname>", fr.CHOOSEIT.elytraracing.holograms.Holograms.HOLOGRAM_TYPE.RANK_MAP, "<map_name>"),
            new HologramsCreator(0, 0, 0, "<worldname>", fr.CHOOSEIT.elytraracing.holograms.Holograms.HOLOGRAM_TYPE.RANK_MAP, "<map_name>"),
            new HologramsCreator(0, 0, 0, "<worldname>", fr.CHOOSEIT.elytraracing.holograms.Holograms.HOLOGRAM_TYPE.RANK_SCORE, null),
            new HologramsCreator(0, 0, 0, "<worldname>", fr.CHOOSEIT.elytraracing.holograms.Holograms.HOLOGRAM_TYPE.RANK_WON_GRANDPRIX, null),
            new HologramsCreator(0, 0, 0, "<worldname>", fr.CHOOSEIT.elytraracing.holograms.Holograms.HOLOGRAM_TYPE.RANK_WON_RACEMODE, "<map_name>"),
            new HologramsCreator(0, 0, 0, "<worldname>", fr.CHOOSEIT.elytraracing.holograms.Holograms.HOLOGRAM_TYPE.PERSONAL_MAPS, "null")

    ));

    public HologramsCreator getHologramCreator(Holograms h) {
        for (HologramsCreator hologram : Holograms) {
            if (hologram.getName().equalsIgnoreCase(h.name)) {
                return hologram;
            }
        }
        return null;
    }

    public void init() {
        Holograms.forEach(HologramsCreator::create);
    }
}
