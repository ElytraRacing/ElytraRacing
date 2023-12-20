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

import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.Pages.MapPage;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObject;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.CheckPoint;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.event.inventory.ClickType;

public class SwapperObject extends VisibleObject
{

    int currentID;
    int maxID;

    public SwapperObject(Page page, String id, int locX, int locY, int idstart, int maxID) {
        super(page, id, locX, locY, 1, 1);
        this.maxID = maxID;
        setCurrentID(idstart);
    }

    public void setCurrentID(int currentID)
    {
        if(currentID > maxID)
        {
            currentID = 0;
        }
        if(currentID < 0)
        {
            currentID = maxID;
        }
        this.currentID = currentID;
        display();
    }

    @Override
    public void display() {
        page.setItem(cookItem(prepareItem(currentID)), getLocX(), getLocY());
    }

    @Override
    protected void clickEvent(ClickType clickType)
    {
        Page pageg = getPage();
        if(getId().startsWith("Map"))
        {
            MapPage page = (MapPage) pageg;
            Map map = page.getMap();
            if(getId().equals("MapAngle"))
            {
                if(currentID == 0)//PLAYER
                {
                    map.setAngleType(CheckPoint.TYPE_ANGLE_CHECKPOINT.NEXT_CHECKPOINT); //Move to NEXT_CHECKPOINT
                }
                else
                {
                    map.setAngleType(CheckPoint.TYPE_ANGLE_CHECKPOINT.PLAYER);
                }
                setDisplayName("§7Checkpoint respawn angle: §2" + map.getAngleType().toString());
                setCurrentID(currentID+1);

            }
            map.save();
        }

        setLastAction(clickType);

    }

}
