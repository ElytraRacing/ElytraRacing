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

package fr.CHOOSEIT.elytraracing.mapsystem.linkplayer;

import fr.CHOOSEIT.elytraracing.GUI.HeadDatabase;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public enum LinkItems {


    LOCK(HeadDatabase.getCustomHead(HeadDatabase.WHITE_LOCK, "§fLock/UnLock", new ArrayList<>(Arrays.asList("", "§fLeft click to unlock an object", "§fRight click to lock the nearest object of the map you are showing")))),
    X_LOCATION(HeadDatabase.getCustomHead(HeadDatabase.ORANGE_X, "§6X location", new ArrayList<>(Arrays.asList("", "§fLeft click increase X axis location value according to your precision", "§fRight click decrease X axis location value according to your precision", "", "§7Change precision: /erconfig lprecision <precision>")))),
    Y_LOCATION(HeadDatabase.getCustomHead(HeadDatabase.ORANGE_Y, "§6Y location", new ArrayList<>(Arrays.asList("", "§fLeft click increase Y axis location value according to your precision", "§fRight click decrease Y axis location value according to your precision", "", "§7Change precision: /erconfig lprecision <precision>")))),
    Z_LOCATION(HeadDatabase.getCustomHead(HeadDatabase.ORANGE_Z, "§6Z location", new ArrayList<>(Arrays.asList("", "§fLeft click increase Z axis location value according to your precision", "§fRight click decrease Z axis location value according to your precision", "", "§7Change precision: /erconfig lprecision <precision>")))),
    X_DEGREE(HeadDatabase.getCustomHead(HeadDatabase.BLUE_X, "§6X degree", new ArrayList<>(Arrays.asList("", "§fLeft click increase X axis rotation value according to your precision", "§fRight click decrease X axis rotation value according to your precision", "", "§7Change precision: /erconfig dprecision <precision>")))),
    Y_DEGREE(HeadDatabase.getCustomHead(HeadDatabase.BLUE_Y, "§6Y degree", new ArrayList<>(Arrays.asList("", "§fLeft click increase Y axis rotation value according to your precision", "§fRight click decrease Y axis rotation value according to your precision", "", "§7Change precision: /erconfig dprecision <precision>")))),
    Z_DEGREE(HeadDatabase.getCustomHead(HeadDatabase.BLUE_Z, "§6Z degree", new ArrayList<>(Arrays.asList("", "§fLeft click increase Z axis rotation value according to your precision", "§fRight click decrease Z axis rotation value according to your precision", "", "§7Change precision: /erconfig dprecision <precision>")))),
    MONITOR(HeadDatabase.getCustomHead(HeadDatabase.MONITOR, "§fOpen configuration", new ArrayList<>(Arrays.asList("", "§fClick here to open the configuration menu for the object", "")))),

    MAP(HeadDatabase.getCustomHead(HeadDatabase.GREEN_M, "§fOpen map configuration", new ArrayList<>(Arrays.asList("", "§fClick here to open the configuration menu for the map", ""))));

    private ItemStack item;

    LinkItems(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }
}
