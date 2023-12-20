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

package fr.CHOOSEIT.elytraracing.gamesystem;

import com.mysql.fabric.xmlrpc.base.Array;
import fr.CHOOSEIT.elytraracing.gamesystem.Games.GrandPrixMode;
import fr.CHOOSEIT.elytraracing.gamesystem.Games.RaceMode;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AutoGames {

    private static String USER = "%%__USER__%%";

    private String name;
    private ArrayList<String> maps;
    private int minplayers;
    private int maxplayers;
    private int mapnumber;
    private boolean randomMapSorting;
    private boolean enabled;
    private String queue = "play";
    private String permission = null;


    public AutoGames(String name, ArrayList<String> maps, int minplayers, int maxplayers, int mapnumber, boolean randomMapSorting, boolean enabled) {
        this.name = name;
        this.maps = maps;
        this.minplayers = minplayers;
        this.maxplayers = maxplayers;
        this.mapnumber = mapnumber;
        this.randomMapSorting = randomMapSorting;
        this.enabled = enabled;
    }

    public AutoGames(String name) {
        this.name = name;
        this.maps = new ArrayList<String>(Arrays.asList("", "", ""));
        this.minplayers = 2;
        this.maxplayers = 10;
        this.mapnumber = 1;
        this.randomMapSorting = true;
        this.enabled = true;
        this.permission = null;

    }

    public Game getGame() {
        for (Game game : Game.gamelist) {
            if (game.getName().equalsIgnoreCase(getName()) && game.getGameDurationType().equals(GameDurationType.SERVERDURATION)) {

                return game;
            }
        }
        return null;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void create() {
        if (name.equalsIgnoreCase("")) {
            Utils.log_print("[ElytraRacing] Game's name can't be: '' ");
            return;
        }
        ArrayList<Map> list_map = new ArrayList<>();
        boolean ok = true;
        for (String map : maps) {
            ok = false;
            for (Map mapl : Map.maplist) {
                if (mapl.getName().equalsIgnoreCase(map)) {
                    list_map.add(mapl);
                    ok = true;
                    break;
                }
            }

            if (!ok) {
                Utils.log_print("[ElytraRacing] Map named: '" + map + "': can't be found " + name);
            }

        }
        if (mapnumber <= 0) {
            Utils.log_print("[ElytraRacing] 'mapnumber' have to be greater or equals to 1");
            return;
        }
        if (minplayers <= 0) {
            Utils.log_print("[ElytraRacing] 'minplayers' have to be greater or equals to 1");
            return;
        }
        if (maxplayers <= 0) {
            Utils.log_print("[ElytraRacing] 'maxplayers' have to be greater or equals to 1");
            return;
        }
        if (maxplayers < minplayers) {
            Utils.log_print("[ElytraRacing] 'maxplayers' have to be greater or equals to 'minplayers' ");
            return;
        }
        if (list_map.size() == 0) {
            Utils.log_print("[ElytraRacing] No maps found for the autoGame: " + name);
            return;
        }
        if (!enabled) {
            return;
        }
        int nmap = mapnumber;
        if (list_map.size() < mapnumber) {
            nmap = list_map.size();
        }
        ArrayList<Map> maps_left = (ArrayList<Map>) list_map.clone();
        ArrayList<Map> map_final = new ArrayList<>();
        Random r = new Random();
        Map _m;
        if (randomMapSorting) {
            for (int i = 0; i < nmap; i++) {
                _m = maps_left.get(r.nextInt(maps_left.size()));
                maps_left.remove(_m);
                map_final.add(_m);
            }
        } else {
            for (int i = 0; i < nmap; i++) {
                _m = maps_left.get(i);
                map_final.add(_m);
            }
        }


        Game g;
        if (map_final.size() > 1) {
            g = new GrandPrixMode(name, GameDurationType.SERVERDURATION, maxplayers, minplayers, null, map_final);
        } else {
            g = new RaceMode(name, GameDurationType.SERVERDURATION, maxplayers, minplayers, null, map_final.get(0));
        }

        g.setPermission(permission);
    }

    public String getQueue() {
        return queue;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }
}
