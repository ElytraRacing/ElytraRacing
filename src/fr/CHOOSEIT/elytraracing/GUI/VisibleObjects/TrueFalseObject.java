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
import fr.CHOOSEIT.elytraracing.GUI.Pages.ConfirmationPage;
import fr.CHOOSEIT.elytraracing.GUI.Pages.MapPage;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.MapCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrueFalseObject extends VisibleObject
{

    private boolean status;

    public TrueFalseObject(Page page, String id, int locX, int locY, boolean status) {
        super(page, id, locX, locY, 1, 1);
        setStatus(status);
    }

    @Override
    public void display()
    {
        page.setItem(cookItem(status ? HeadDatabase.TRUE : HeadDatabase.FALSE), getLocX(), getLocY());
    }

    @Override
    protected void clickEvent(ClickType clickType)
    {
        Page pageg = getPage();
        if(getId().equals("EXIT") && !(pageg instanceof MapPage))
        {
            pageg.closeView();
        }
        else if(getId().equals("isEnabled"))
        {
            MapPage page = (MapPage) pageg;
            Map map = page.getMap();
            boolean isEnable = map.isEnabled();
            map.setEnabled(!isEnable);
            setStatus(!isEnable);
            map.save();
            page.sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
            display();

        }
        else if(getId().equals("SHOWMAP"))
        {
            MapPage page = (MapPage) pageg;
            Map map = page.getMap();
            if(status == false)
            {
                MapCommand.show(page.getViewer(), map);
            }
            else
            {
                MapCommand.hide(page.getViewer());
            }
            setStatus(!status);
            display();

        }
        else if(getId().equals("PodiumSystem"))
        {
            MapPage page = (MapPage) pageg;
            Map map = page.getMap();
            map.setPodium(!map.isPodium());
            setStatus(map.isPodium());
            map.save();
            setDescription(Arrays.asList("§fIs the podium system is activated for this map ? : " + Main.cmc().booleantoString(map.isPodium()),
                    " ",
                    "§7Command to add a new location for the podium:",
                    "§c/ermap map "+map.getName()+" podium setlocation <placement... 1, 2, 3..>",
                    "§7Command to teleport a location of the podium:",
                    "§c/ermap map "+map.getName()+" podium tp <placement... 1, 2, 3..>"));
            page.sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
            display();

        }
        else if(getId().startsWith("Location"))
        {
            MapPage page = (MapPage) pageg;
            Map map = page.getMap();
            Player viewer = page.getViewer();
            if((clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.RIGHT)))
            {
                if(getId().equals("LocationLobby") && map.getLocation_lobby() != null)
                {
                    viewer.teleport(map.getLocation_lobby());
                    page.sendViewerMessage(Main.cmc().TELEPORTING);
                }
                else if(getId().equals("LocationStart") && map.getLocation_start() != null)
                {
                    viewer.teleport(map.getLocation_start());
                    page.sendViewerMessage(Main.cmc().TELEPORTING);
                }
                else if(getId().equals("LocationEnd") && map.getLocation_end() != null)
                {
                    viewer.teleport(map.getLocation_end());
                    page.sendViewerMessage(Main.cmc().TELEPORTING);
                }


            }
            else if((clickType.equals(ClickType.SHIFT_LEFT) || clickType.equals(ClickType.SHIFT_RIGHT)))
            {
                if(getId().equals("LocationLobby"))
                {
                    map.setLocation_lobby(viewer.getLocation());
                    page.sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
                }
                else if(getId().equals("LocationStart"))
                {
                    map.setLocation_start(viewer.getLocation());
                    page.sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
                }
                else if(getId().equals("LocationEnd"))
                {
                    map.setLocation_end(viewer.getLocation());
                    page.sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
                }

                map.save();
                update();
                display();

            }
        }
        else if(pageg instanceof ConfirmationPage)
        {
            if(getId().equals("LEFT"))
            {
                ((ConfirmationPage)pageg).decline();
            }
            else if(getId().equals("RIGHT"))
            {
                ((ConfirmationPage)pageg).confirm();
            }
        }

    }

    public boolean getStatus()
    {
        return status;
    }

    public void setStatus(boolean status)
    {
        this.status = status;

        switch (getId())
        {
            case "isEnabled":
                if(status)
                    setDisplayName("§aEnabled");
                else
                {
                    setDisplayName("§cDisabled");
                }
                setDescription(Arrays.asList("§fHere you can know if your map is enabled or disabled","","§7Click here to enable or disable the map",""));
                break;
            case "isValid":
                if(status)
                    setDisplayName("§aYour map is valid");
                else
                {
                    setDisplayName("§cYour map is not valid");
                }
                setDescription(Arrays.asList("§fHere you can know if your map is valid or not",
                        "",
                        "§fWhat is making a map valid ?",
                        "§f- At least 1 checkpoint",
                        "§f- At least 1 end",
                        "§f- Spawn, Lobby and End locations have to be define"
                ));
                break;
        }

    }
}
