package fr.CHOOSEIT.elytraracing.scoreboard;

import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.packetHandler.ClassGetter;
import fr.CHOOSEIT.elytraracing.packetHandler.ClassHelper;
import fr.CHOOSEIT.elytraracing.packetHandler.PacketHandler;


import fr.CHOOSEIT.elytraracing.scoreboard.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/*
This code is inspired by MrMicky's implementation of scoreboards in Minecraft
Credit: https://github.com/MrMicky-FR/FastBoard

MIT License

Copyright (c) 2019 MrMicky

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
public class Board {


    private List<String> lines;
    private Set<UUID> viewers;
    private String id;
    private String title;

    private HashMap<UUID, FastBoard> fastboards = new HashMap<>();


    public Board(String title) {
        lines = new ArrayList<>();
        viewers = new HashSet<>();
        id = "c_" + Double.toString(Math.random()).substring(2, 7);

        updateTitle(title);
    }
    public void delete() {
        for (UUID viewer : getViewers()) {
            deletefor(viewer);
        }
        viewers.clear();
    }
    public void createfor(UUID uuid) {
        if (Bukkit.getPlayer(uuid) != null) {
            create(uuid);
        }
    }

    private void create(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        FastBoard newboard = new FastBoard(player);
        fastboards.put(uuid, newboard);
    }

    public void setLines(List<String> lines, UUID uuid){
        FastBoard board = fastboards.get(uuid);
        if(board == null){
            return;
        }
        board.updateLines(lines);
        updateTitle(this.title);
    }

    public void setLines(List<String> lines, Set<UUID> players, boolean personnal) {
        players.forEach(uuid -> setLines(lines, uuid));
        if(!personnal){
            this.lines = lines;
        }
    }

    public boolean isViewer(UUID uuid) {
        return viewers.contains(uuid);
    }

    public String getTitle() {
        return title;
    }

    enum BoardAction {
        CREATE,
        REMOVE,
        UPDATE
    }

    public void setLine(int lineID, String line) {
        List<String> linescurrent = new ArrayList<>(this.lines);
        linescurrent.set(lineID, line);
        setLines(linescurrent, viewers, false);
    }

    public void setLines(List<String> lines) {
        setLines(lines, viewers, false);
    }

    public void updateTitle(String title) {
        if (title.length() > 32) {
            this.title = title.substring(0, 31);
        } else {
            this.title = title;
        }
        fastboards.values().forEach(b -> b.updateTitle(this.title));
    }

    private void deletefor(UUID uuid) {
        FastBoard board = fastboards.remove(uuid);
        if(board != null){
            board.delete();
        }
    }

    public Set<UUID> getViewers() {
        return viewers;
    }

    public void setViewers(Set<UUID> viewers) {
        this.viewers.clear();
        viewers.forEach(this::deletefor);
        viewers.forEach(this::addViewer);
    }

    public void reload(UUID uuid) {
        setLines(lines, new HashSet<UUID>(Collections.singleton(uuid)), true);
    }

    public void addViewer(UUID uuid) {
        viewers.add(uuid);
        createfor(uuid);
        reload(uuid);

    }

    public void removeViewer(UUID uuid) {
        viewers.remove(uuid);
        deletefor(uuid);
    }
}
