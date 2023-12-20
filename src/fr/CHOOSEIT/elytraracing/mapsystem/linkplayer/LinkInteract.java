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

import fr.CHOOSEIT.elytraracing.GUI.Pages.AdditionalObjectPage;
import fr.CHOOSEIT.elytraracing.GUI.Pages.CheckpointPage;
import fr.CHOOSEIT.elytraracing.GUI.Pages.EndPage;
import fr.CHOOSEIT.elytraracing.GUI.Pages.MapPage;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.MapCommand;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.AdditionalObject;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.CheckPoint;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.End;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.ArrayList;

public class LinkInteract {
    public static void InteractLinkObject(Player player, LinkItems linkItems, Action action) {
        if (LinkPlayer.existLink(player) || linkItems.equals(LinkItems.LOCK) || linkItems.equals(LinkItems.MAP)) {
            LinkPlayer.LinkPlayerInstance linkPlayerInstance = LinkPlayer.getLinkPlayer(player);
            Map map = linkPlayerInstance.getMap();
            if (map == null) {
                map = MapCommand.getShowingMap(player);
                if (map == null) {
                    return;
                }
            }
            ObjectClass objectClass = linkPlayerInstance.getObjectClass();
            boolean save = false;
            switch (linkItems) {
                case LOCK:
                    if (isLeftClick(action)) {
                        LinkPlayer.delete(player);
                        player.sendMessage(Main.cmc().basicsetting(Main.cmc().UNLOCK_OBJECT, player));
                    } else if (isRightClick(action)) {
                        Map showingMap = MapCommand.getShowingMap(player);
                        if (showingMap != null) {
                            ArrayList<ObjectClass> objects = new ArrayList<>();
                            objects.addAll(showingMap.getEndsList());
                            objects.addAll(showingMap.getCheckpointsList());
                            objects.addAll(showingMap.getAdditionalObjectsList());
                            ObjectClass nearest = null;
                            int distancenear = Main.cmc().UNLOCK_DISTANCE;
                            Location ploc = player.getLocation();
                            int _d = 0;
                            for (ObjectClass object : objects) {
                                _d = (int) Utils.distance(object.getLocation(), ploc);
                                if (_d < distancenear) {
                                    distancenear = _d;
                                    nearest = object;
                                }
                            }
                            if (nearest == null) {
                                player.sendMessage(Main.cmc().basicsetting(Main.cmc().LOCK_OBJECT_NEAR_NOTFOUND, player));
                                return;
                            }
                            LinkPlayer.LinkPlayerInstance lp = LinkPlayer.getLinkPlayer(player);
                            lp.setMap(showingMap);
                            lp.setObjectClass(nearest);
                            player.sendMessage(Main.cmc().basicsetting(Main.cmc().LOCK_OBJECT, player));

                            return;
                        }
                        player.sendMessage(Main.cmc().basicsetting(Main.cmc().MSG_MAPNOTFOUND, player));
                    }
                    break;
                case X_LOCATION:
                    if (isLeftClick(action)) {
                        objectClass.setLocation(objectClass.getLocation().add(linkPlayerInstance.getPRECISION_L(), 0, 0));
                    } else if (isRightClick(action)) {
                        objectClass.setLocation(objectClass.getLocation().add(-linkPlayerInstance.getPRECISION_L(), 0, 0));
                    }
                    save = true;
                    break;
                case Y_LOCATION:
                    if (isLeftClick(action)) {
                        objectClass.setLocation(objectClass.getLocation().add(0, linkPlayerInstance.getPRECISION_L(), 0));
                    } else if (isRightClick(action)) {
                        objectClass.setLocation(objectClass.getLocation().add(0, -linkPlayerInstance.getPRECISION_L(), 0));
                    }
                    save = true;
                    break;
                case Z_LOCATION:
                    if (isLeftClick(action)) {
                        objectClass.setLocation(objectClass.getLocation().add(0, 0, linkPlayerInstance.getPRECISION_L()));
                    } else if (isRightClick(action)) {
                        objectClass.setLocation(objectClass.getLocation().add(0, 0, -linkPlayerInstance.getPRECISION_L()));
                    }
                    save = true;
                    break;
                case X_DEGREE:
                    if (isLeftClick(action)) {
                        objectClass.setX_degrees((float) (objectClass.getXDegrees() + linkPlayerInstance.getPRECISION_D()));
                    } else if (isRightClick(action)) {
                        objectClass.setX_degrees((float) (objectClass.getXDegrees() - linkPlayerInstance.getPRECISION_D()));
                    }
                    save = true;
                    break;
                case Y_DEGREE:
                    if (isLeftClick(action)) {
                        objectClass.setY_degrees((float) (objectClass.getYDegrees() + linkPlayerInstance.getPRECISION_D()));
                    } else if (isRightClick(action)) {
                        objectClass.setY_degrees((float) (objectClass.getYDegrees() - linkPlayerInstance.getPRECISION_D()));
                    }
                    save = true;
                    break;
                case Z_DEGREE:
                    if (isLeftClick(action)) {
                        objectClass.setZ_degrees((float) (objectClass.getZDegrees() + linkPlayerInstance.getPRECISION_D()));
                    } else if (isRightClick(action)) {
                        objectClass.setZ_degrees((float) (objectClass.getZDegrees() - linkPlayerInstance.getPRECISION_D()));
                    }
                    save = true;
                    break;
                case MONITOR:
                    OpenConfig(objectClass, map, player);
                    break;
                case MAP:
                    MapPage mapPage = new MapPage(player, map);
                    mapPage.View();
                    mapPage.open();
                    break;
            }
            if (save) {
                objectClass.ReloadShow();
                map.save();
            }


        }
    }

    public static void OpenConfig(ObjectClass objectClass, Map map, Player player) {
        if (objectClass instanceof CheckPoint) {
            CheckpointPage checkpointPage = new CheckpointPage(player, map, (CheckPoint) objectClass);
            checkpointPage.View();
            checkpointPage.open();
        } else if (objectClass instanceof End) {
            EndPage endPage = new EndPage(player, map, (End) objectClass);
            endPage.View();
            endPage.open();
        } else if (objectClass instanceof AdditionalObject) {
            AdditionalObjectPage additionalObjectPage = new AdditionalObjectPage(player, map, (AdditionalObject) objectClass);
            additionalObjectPage.View();
            additionalObjectPage.open();
        }
    }

    public static boolean isRightClick(Action action) {
        return action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR);
    }

    public static boolean isLeftClick(Action action) {
        return action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR);
    }
}
