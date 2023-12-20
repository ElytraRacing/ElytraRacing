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

import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.Pages.*;
import fr.CHOOSEIT.elytraracing.GUI.Pages.HologramsPage.HologramsPage;
import fr.CHOOSEIT.elytraracing.GUI.Pages.MapPages.ParticleMapPage;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.gamesystem.*;
import fr.CHOOSEIT.elytraracing.gamesystem.Games.RaceMode;
import fr.CHOOSEIT.elytraracing.holograms.Holograms;
import fr.CHOOSEIT.elytraracing.holograms.HologramsCreator;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class IconObject extends VisibleObject
{
    public IconObject(Page page, String id, int locX, int locY) {
        super(page, id, locX, locY, 1, 1);
    }
    public IconObject(Page page, String id, int locX, int locY, int sizeX, int sizeY) {
        super(page, id, locX, locY, sizeX, sizeY);
    }
    public IconObject(Page page, String id, int slot, int sizeX, int sizeY) {
        super(page, id, slot, sizeX, sizeY);
    }
    public IconObject(Page page, String id, int slot) {
        super(page, id, slot, 1, 1);
    }

    @Override
    protected void clickEvent(ClickType clickType)
    {
        CustomMessageConfig cmc = Main.cmc();
        Page pageg = getPage();
        if(getPage() instanceof MapPage && !(getPage() instanceof ParticleMapPage))
        {
            MapPage page = (MapPage) pageg;
            if(getId().equals("PARTICLE"))
            {

                ParticleMapPage.ParticlePageType particlePageType = (ParticleMapPage.ParticlePageType)getInformation();
                ParticleMapPage particleMapPage = new ParticleMapPage(page.getViewer(), page.getMap(), particlePageType);
                particleMapPage.open();
                particleMapPage.displayPage(1);
                page.delete();
            }
            else if(getId().equals("MapDB"))
            {
                ConfirmationPage cp = new ConfirmationPage("Confirmation: Clear database", page.getViewer(), page, "§fClear saved information in the database", Collections.singletonList(""));
                ArrayList<Object> info = new ArrayList<Object>(Arrays.asList("Map", page.getMap(), "ClearDB"));
                cp.setInformationObjects(info);
                cp.open();
            }
            else if(getId().equals("STARTGAME"))
            {
                if(Game.Find(page.getViewer()) != null)
                {
                    page.sendViewerMessage(Main.cmc().MSG_LEAVEYOURGAME);
                    return;
                }
                if(!page.getMap().isValid())
                {
                    page.sendViewerMessage(Main.cmc().MSG_MAP_NOTAVAILABLE);
                    return;
                }
                page.sendViewerMessage(Main.cmc().TELEPORTING);
                Game game = new RaceMode(page.getViewer().getName()+"-"+UUID.randomUUID().toString().substring(0,4), GameDurationType.HOSTDURATION, Main.cmc().GUI_MAPTESTING_MAXPLAYER, 0, page.getViewer(), page.getMap());
                game.setPermission(Main.cmc().GUI_MAPTESTING_PERMISSION);
                game.rawPlayerjoin(page.getViewer());
                page.closeView();
            }

        }
        else if(getPage() instanceof ParticleMapPage)
        {
            ParticleMapPage page = (ParticleMapPage) pageg;

            if(getId().equals("PAGE_PLUS"))
            {
                int currentpage = page.getPageNumber();
                page.displayPage(currentpage+1);
            }
            else if(getId().equals("PAGE_MINUS"))
            {
                int currentpage = page.getPageNumber();
                if(currentpage == 1)
                {
                    new MapPage(page.getViewer(), page.getMap()).open();
                    page.delete();
                    return;
                }
                page.displayPage(currentpage-1);
            }
            else if(getId().equals("PARTICLE_SET"))
            {
                page.setParticle((String)getInformation(), page.getParticlePageType());
            }
        }
        else if(getPage() instanceof ErTrainMapChoosePage)
        {
            ErTrainMapChoosePage page = (ErTrainMapChoosePage) pageg;
            if(getId().equals("PAGE_PLUS"))
            {
                int currentpage = page.getPageNumber();
                page.displayPage(currentpage+1);
            }
            else if(getId().equals("PAGE_MINUS"))
            {
                int currentpage = page.getPageNumber();
                if(currentpage == 1)
                {
                    page.closeView();
                    page.delete();
                    return;
                }
                page.displayPage(currentpage-1);
            }
            else if(getId().equals("SET"))
            {
                ElytraRacingCommand.ExCommandTrain(page.getViewer(), (String)getInformation());
            }
        }
        else if(getPage() instanceof MapChoosePage)
        {
            MapChoosePage page = (MapChoosePage) pageg;
            if(getId().equals("PAGE_PLUS"))
            {
                int currentpage = page.getPageNumber();
                page.displayPage(currentpage+1);
            }
            else if(getId().equals("PAGE_MINUS"))
            {
                int currentpage = page.getPageNumber();
                if(currentpage == 1)
                {
                    page.closeView();
                    page.delete();
                    return;
                }
                page.displayPage(currentpage-1);
            }
            else if(getId().equals("SELECT"))
            {
                MapPage mp = new MapPage(page.getViewer(), (Map)getInformation());
                mp.open();
            }
        }
        else if(getPage() instanceof HologramsChoosePage)
        {
            HologramsChoosePage page = (HologramsChoosePage) pageg;
            if(getId().equals("PAGE_PLUS"))
            {
                int currentpage = page.getPageNumber();
                page.displayPage(currentpage+1);
            }
            else if(getId().equals("PAGE_MINUS"))
            {
                int currentpage = page.getPageNumber();
                if(currentpage == 1)
                {
                    page.closeView();
                    page.delete();
                    return;
                }
                page.displayPage(currentpage-1);
            }
            else if(getId().equals("SELECT"))
            {
                Holograms h = Holograms.getHolo((String)getInformation());
                if(h != null)
                {
                    if(clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.SHIFT_LEFT))
                    {
                        HologramsPage hologramsPage = new HologramsPage(page.getViewer(), h);
                        hologramsPage.open();
                    }
                    else
                    {
                        page.getViewer().teleport(h.location);
                        page.sendViewerMessage(Main.cmc().TELEPORTING);
                        return;
                    }
                }
                else
                {
                    page.sendViewerMessage(Main.cmc().ERROR_MESSAGE);
                }

            }
            else if(getId().equals("CREATE"))
            {
                Holograms.HOLOGRAM_TYPE ht = (Holograms.HOLOGRAM_TYPE)getInformation();
                if(ht != null)
                {
                    Holograms.HardCreate(page.getViewer().getLocation(), ht, page.getViewer());
                    page.getViewer().closeInventory();
                }
                else
                {
                    page.sendViewerMessage(Main.cmc().ERROR_MESSAGE);
                }

            }
        }
        else if(getPage() instanceof HologramsPage)
        {
            HologramsPage page = (HologramsPage) pageg;
            Holograms h = page.getHolograms();
            if(getId().equals("LOC"))
            {
                if(clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.SHIFT_LEFT))
                {
                    page.getViewer().teleport(h.location);
                    page.sendViewerMessage(Main.cmc().TELEPORTING);
                    return;

                }
                else
                {
                    HologramsCreator hc = Main.hologramsSaver.getHologramCreator(h);
                    if(hc != null)
                    {
                        hc.setLocation(page.getViewer().getLocation());
                        Main.serialization.saveHolograms();
                        h.setLocation(page.getViewer().getLocation());
                        page.sendViewerMessage(Main.cmc().MSG_SAVE_SUCCESSFUL);
                    }
                    else
                    {
                        page.sendViewerMessage(Main.cmc().ERROR_MESSAGE);
                    }

                }

            }
            else if(getId().equals("REFRESH"))
            {
                h.clear();
                h.update();
            }
            else if(getId().equals("DELETE"))
            {
                ConfirmationPage cp = new ConfirmationPage("Delete confirmation", page.getViewer(), page, "Delete hologram: " + h.name, new ArrayList<>(Arrays.asList("§fConfirmation for deleting the hologram named: " + h.name)));
                cp.setInformationObjects(new ArrayList<Object>(Arrays.asList("HOLOGRAM", "DELETE", h)));
                cp.open();
            }
        }
        else if(getPage() instanceof HostPage)
        {
            HostPage hp = (HostPage) getPage();
            Game game = hp.getGame();
            Player player = hp.getViewer();
            if(getId().equalsIgnoreCase("STARTEND"))
            {
                if(game.getGameState().equals(GameState.WAITING))
                {
                    game.askStartTimer();
                    player.sendMessage(cmc.basicsetting(cmc.MSG_TIMERSTART, player));
                }
                else
                {
                    game.askCancelTimer();
                    player.sendMessage(cmc.basicsetting(cmc.MSG_TIMERCANCEL, player));
                }

            }
            else if(getId().equalsIgnoreCase("ALERT"))
            {
                ERHostCommand.sendAlert(player, game);

            }
        }
    }
}
