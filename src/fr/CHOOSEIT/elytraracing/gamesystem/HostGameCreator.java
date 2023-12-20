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

import fr.CHOOSEIT.elytraracing.gamesystem.Games.GrandPrixMode;
import fr.CHOOSEIT.elytraracing.gamesystem.Games.RaceMode;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HostGameCreator {
    public ArrayList<Map> maps;
    public String name;
    public Player creator;
    public int maxPlayer;

    public HostGameCreator(Player creator, String name) {
        this.name = name;
        this.creator = creator;
        this.maxPlayer = 1;
        this.maps = new ArrayList<>();
    }

    public ArrayList<Map> getMaps() {
        return maps;
    }

    public String getName() {
        return name;
    }

    public Player getCreator() {
        return creator;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public Game build(){
        if(maps.size() == 0){
            return null;
        }
        if(maps.size() == 1){
            return new RaceMode(name, GameDurationType.HOSTDURATION, maxPlayer, 0, creator, maps.get(0));
        }
        else
        {
            return new GrandPrixMode(name, GameDurationType.HOSTDURATION, maxPlayer, 0, creator, maps);
        }
    }
}
