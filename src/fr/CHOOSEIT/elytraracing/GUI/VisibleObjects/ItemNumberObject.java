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

package fr.CHOOSEIT.elytraracing.GUI.VisibleObjects;

import fr.CHOOSEIT.elytraracing.GUI.HeadDatabase;
import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.Pages.MapPage;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ItemNumberObject extends VisibleObject
{
    public int number;
    public int maxnumber;

    public ItemNumberObject(Page page, String id, int locX, int locY, int n, ItemStack itemStack) {
        super(page, id, locX, locY, 1, 1);
        maxnumber = 64;
        addItem(itemStack);
        setNumber(n);
    }

    public void setMaxnumber(int maxnumber) {
        this.maxnumber = maxnumber;
    }

    public int getMaxnumber() {
        return maxnumber;
    }

    public void setNumber(int n)
    {
        this.number = n;
        display();
    }

    @Override
    protected void clickEvent(ClickType clickType)
    {
        Page pageg = getPage();
        if(getId().startsWith("Map"))
        {
            MapPage page = (MapPage) pageg;
            Map map = page.getMap();
            if(getId().equals("Map_FireWork"))
            {
                switch (clickType)
                {
                    case LEFT:
                        map.setFireworks(map.getFireworks() + 1);
                        break;
                    case RIGHT:
                        map.setFireworks(map.getFireworks() - 1);
                        break;
                    case SHIFT_LEFT:
                        map.setFireworks(map.getFireworks() * 2);
                        break;
                    case SHIFT_RIGHT:
                        map.setFireworks(map.getFireworks() / 2);
                        break;
                }
                setDisplayName("§7Current firework amount: §c" + map.getFireworks());
                setNumber(map.getFireworks());


            }
            else if(getId().equals("Map_Laps"))
            {
                switch (clickType)
                {
                    case LEFT:
                    case SHIFT_LEFT:
                        map.setNumberoflaps(map.getNumberoflaps() + 1);
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        map.setNumberoflaps(map.getNumberoflaps() - 1);
                        break;
                }
                setDisplayName("§7Current laps: §c" + map.getNumberoflaps());
                setNumber(map.getNumberoflaps());


            }
            else if(getId().equals("Map_Difficulty"))
            {
                switch (clickType)
                {
                    case LEFT:
                    case SHIFT_LEFT:
                        map.setDifficulty(map.getDifficulty() + 1);
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        map.setDifficulty(map.getDifficulty() - 1);
                        break;
                }
                setDisplayName("§7Current map difficulty: " + map.getDifficultyMSG());
                setNumber(map.getDifficulty());


            }
            map.save();

        }

        setLastAction(clickType);

    }

    @Override
    public void update() {
        setNumber(number);
    }

    @Override
    public void display()
    {

        if(number >= maxnumber)
        {
            number = maxnumber;
        }
        if(number <= 0)
        {
            number = 1;
        }
        ItemStack itemStack = cookItem(getItems().get(0).clone());
        itemStack.setAmount(number);
        page.setItem(itemStack, getLocX(), getLocY());
    }
}
