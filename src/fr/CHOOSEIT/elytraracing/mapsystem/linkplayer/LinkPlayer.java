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

import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class LinkPlayer {
    static HashMap<Player, LinkPlayerInstance> linkPlayerInstanceHashMap = new HashMap<>();

    public static boolean existLink(Player player) {
        return linkPlayerInstanceHashMap.containsKey(player) && linkPlayerInstanceHashMap.get(player).getMap() != null && linkPlayerInstanceHashMap.get(player).getObjectClass() != null;
    }

    public static LinkPlayerInstance getLinkPlayer(Player player) {
        if (existLink(player)) {
            return linkPlayerInstanceHashMap.get(player);
        }
        LinkPlayerInstance linkPlayerInstance = new LinkPlayerInstance(player);
        linkPlayerInstanceHashMap.put(player, linkPlayerInstance);
        return linkPlayerInstance;
    }

    public static void delete(Player player) {
        if (existLink(player)) {
            getLinkPlayer(player).delete();
            linkPlayerInstanceHashMap.remove(player);
        }
    }

    public static class LinkPlayerInstance {
        private Player player;
        private Map map;
        private ObjectClass objectClass;
        private double PRECISION_L;
        private double PRECISION_D;

        public LinkPlayerInstance(Player player, Map map, ObjectClass objectClass) {
            this.map = map;
            this.player = player;
            this.objectClass = objectClass;
            PRECISION_L = Main.cmc().ITEM_DEFAULT_PRECISION_LOC;
            PRECISION_D = Main.cmc().ITEM_DEFAULT_PRECISION_DEGREE;
        }

        public double getPRECISION_D() {
            return PRECISION_D;
        }

        public double getPRECISION_L() {
            return PRECISION_L;
        }

        public void setPRECISION_D(double PRECISION_D) {
            this.PRECISION_D = PRECISION_D;
        }

        public void setPRECISION_L(double PRECISION_L) {
            this.PRECISION_L = PRECISION_L;
        }

        public LinkPlayerInstance(Player player) {
            this(player, null, null);
        }

        private void delete() {
            try {
                this.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }


        public Player getPlayer() {
            return player;
        }

        public Map getMap() {
            return map;
        }

        public ObjectClass getObjectClass() {
            return objectClass;
        }

        public void setMap(Map map) {
            this.map = map;
        }

        public void setObjectClass(ObjectClass objectClass) {
            this.objectClass = objectClass;
        }
    }

    public static void giveItems(Player player) {
        for (LinkItems value : LinkItems.values()) {
            player.getInventory().addItem(value.getItem());
        }
        player.sendMessage(Main.cmc().basicsetting(Main.cmc().LOCK_ITEMS_GIVE, player));
    }

}

