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
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.event.inventory.ClickType;

public class NumberObject1x2 extends VisibleObject
{
    public int number;


    public NumberObject1x2(Page page, String id, int locX, int locY, int defaultValue) {
        super(page, id, locX, locY, 2, 1);
        setNumber(defaultValue);
    }

    public void setNumber(int n)
    {
        this.number = n;
        display();
    }

    public int getNumber() {
        return number;
    }

    @Override
    protected void clickEvent(ClickType clickType)
    {
        Page pageg = getPage();
        if(getId().startsWith("MaxTime"))
        {
            MapPage page = (MapPage) pageg;
            Map map = page.getMap();
            if(getId().equals("MaxTimeMin"))
            {
                switch (clickType)
                {
                    case LEFT:
                        map.setMAP_TIME_MAX_MINUTES(map.getMapTimeMinute() + 1);
                        break;
                    case RIGHT:
                        map.setMAP_TIME_MAX_MINUTES(map.getMapTimeMinute() - 1);
                        break;
                    case SHIFT_LEFT:
                        map.setMAP_TIME_MAX_MINUTES(map.getMapTimeMinute() * 2);
                        break;
                    case SHIFT_RIGHT:
                        map.setMAP_TIME_MAX_MINUTES(map.getMapTimeMinute() / 2);
                        break;
                }
                int intt = map.getMapTimeSecond();
                String s = ""+intt;
                if(intt < 10)
                {
                    s = "0"+intt;
                }
                intt = map.getMapTimeMinute();
                String m = ""+intt;
                if(intt < 10)
                {
                    m = "0"+intt;
                }
                setDisplayName("§7Current max time: §c" + m +"m:"+s+"s");
                update();
                updateLinkedObjects();


            }
            else if(getId().equals("MaxTimeSec"))
            {
                switch (clickType)
                {
                    case LEFT:
                        map.setMAP_TIME_MAX_SECONDS(map.getMapTimeSecond() + 1);
                        break;
                    case RIGHT:
                        map.setMAP_TIME_MAX_SECONDS(map.getMapTimeSecond() - 1);
                        break;
                    case SHIFT_LEFT:
                        map.setMAP_TIME_MAX_SECONDS(map.getMapTimeSecond() * 2);
                        break;
                    case SHIFT_RIGHT:
                        map.setMAP_TIME_MAX_SECONDS(map.getMapTimeSecond() / 2);
                        break;
                }
                update();
                updateLinkedObjects();

            }

            map.save();
        }

        setLastAction(clickType);

    }

    @Override
    public void display()
    {
        if(number >= 100)
        {
            number = 99;
        }
        if(number < 0)
        {
            number = 0;
        }
        int f = number / 10;
        int b = number % 10;

        page.setItem(cookItem(HeadDatabase.getNumber("WHITE", f)), getLocX(), getLocY());
        page.setItem(cookItem(HeadDatabase.getNumber("WHITE", b)), getLocX() + 1, getLocY());
    }
}
