package fr.CHOOSEIT.elytraracing.scoreboard;

import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.packetHandler.ClassGetter;
import fr.CHOOSEIT.elytraracing.packetHandler.ClassHelper;
import fr.CHOOSEIT.elytraracing.packetHandler.PacketHandler;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

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


    public Board(String title) {
        lines = new ArrayList<>();
        viewers = new HashSet<>();
        id = "c_" + Double.toString(Math.random()).substring(2, 7);

        if (title.length() > 16) {
            this.title = title.substring(0, 15);
        } else {
            this.title = title;

        }


    }

    public boolean isViewer(UUID uuid) {
        return viewers.contains(uuid);
    }

    public String getTitle() {
        return title;
    }

    public void createfor(UUID uuid) {
        if (Bukkit.getPlayer(uuid) != null) {
            create(uuid);
            display(uuid);
        }

    }

    enum BoardAction {
        CREATE,
        REMOVE,
        UPDATE
    }

    public void delete() {
        for (UUID viewer : getViewers()) {
            deletefor(viewer);
        }
        viewers.clear();
    }


    public void setLine(int lineID, String line) {
        List<String> linescurrent = new ArrayList<>(this.lines);
        linescurrent.set(lineID, line);
        setLines(linescurrent, viewers, false);
    }

    public void setLines(List<String> lines) {
        setLines(lines, viewers, false);
    }

    public void setLines(List<String> lines, Set<UUID> players, boolean personnal) {
        int charlimit = 30;
        if (Main.getVersion() >= 13) {
            charlimit = 500;
        }


        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).length() > charlimit) {
                lines.set(i, lines.get(i).substring(0, charlimit - 1));
            }
        }
        List<String> oldLines = new ArrayList<>(this.lines);
        List<String> oldLinesCopy = new ArrayList<>(this.lines);
        int size_oldlines = oldLines.size();
        if (!personnal) {
            this.lines = lines;
        }
        int size_newlines = lines.size();

        int diff;
        if (size_newlines != size_oldlines) {
            if (size_oldlines > size_newlines) {
                diff = size_oldlines - size_newlines;
                if (diff < 0) {
                    diff = 0;
                }
                for (int i = 0; i < diff; i++) {
                    TeamsHandler(size_oldlines - 1, TeamHandlerMode.REMOVE, players, lines);
                    ScoreBoardScore(size_oldlines - 1, ScoreBoardScore.REMOVE, players);
                    size_oldlines--;
                }
            } else {
                diff = size_newlines - size_oldlines;

                for (int i = 0; i < diff; i++) {
                    ScoreBoardScore(size_oldlines + i, ScoreBoardScore.CHANGE, players);
                    TeamsHandler(size_oldlines + i, TeamHandlerMode.CREATE, players, lines);
                }
            }
        } else if (personnal) {

            for (int i = 0; i < size_newlines; i++) {
                ScoreBoardScore(i, ScoreBoardScore.CHANGE, players);
                TeamsHandler(i, TeamHandlerMode.CREATE, players, lines);
            }
        }

        for (int i = 0; i < size_newlines; i++) {
            if (i >= oldLinesCopy.size() || !lines.get(i).equals(oldLinesCopy.get(i)) || personnal) {
                TeamsHandler(i, TeamHandlerMode.UPDATE, players, lines);
            }
        }
    }

    private void ScoreBoardScore(int lineID, ScoreBoardScore scoreBoardScore, Set<UUID> players) {
        Class<?> pclass = ClassGetter.PacketPlayOutScoreboardScore.getClassRight();
        if (lineID > 15) {
            return;
        }
        try {
            if (Main.getVersion() >= 17) {
                Class penum = Class.forName("net.minecraft.server.ScoreboardServer$Action");
                Object enumm = null;
                switch (scoreBoardScore) {
                    case CHANGE:
                        enumm = penum.getDeclaredField("a").get(null);
                        break;
                    case REMOVE:
                        enumm = penum.getDeclaredField("b").get(null);
                        break;
                }
                Object p = pclass.getConstructors()[0].newInstance(enumm, id, ChatColor.values()[invertforscore(lineID)].toString(), invertforscore(lineID));
                PacketHandler.sendPacket(p, new ArrayList<>(players));
                return;

            }

            Object p = pclass.newInstance();
            ClassHelper.setField(p, "a", ChatColor.values()[invertforscore(lineID)].toString());
            Class<?> penum = null;
            if (Main.getVersion() >= 13) {
                penum = p.getClass().getDeclaredField("d").getType();
            } else {
                penum = ClassHelper.getDeclaredClass(p.getClass(), "EnumScoreboardAction");
            }

            switch (scoreBoardScore) {
                case CHANGE:
                    ClassHelper.setField(p, "d", penum.getDeclaredField("CHANGE").get(null));
                    break;
                case REMOVE:
                    ClassHelper.setField(p, "d", penum.getDeclaredField("REMOVE").get(null));
                    break;
            }
            if (scoreBoardScore == ScoreBoardScore.CHANGE) {
                ClassHelper.setField(p, "b", id);
                ClassHelper.setField(p, "c", invertforscore(lineID));
            }
            PacketHandler.sendPacket(p, new ArrayList<>(players));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    enum ScoreBoardScore {
        CHANGE,
        REMOVE
    }

    enum TeamHandlerMode {
        CREATE,
        REMOVE,
        UPDATE
    }

    private void TeamsHandler(int lineID, TeamHandlerMode teamHandlerMode) {
        TeamsHandler(lineID, teamHandlerMode, viewers, null);
    }

    private void TeamsHandler(int lineID, TeamHandlerMode teamHandlerMode, Set<UUID> players, List<String> newlines) {
        if (lineID > 15) {
            return;
        }

        Class<?> pclass = ClassGetter.PacketPlayOutScoreboardTeam.getClassRight();
        try {
            String line = "";
            if (lineID < newlines.size() && newlines != null) {
                line = newlines.get(lineID);
            } else if (lineID < lines.size()) {
                line = lines.get(lineID);
            }
            if (line.isEmpty()) {
                if (lineID > 8) {
                    for (int i = 0; i < lineID - 8; i++) {
                        line += "§c";
                    }
                } else {
                    for (int i = 0; i < lineID; i++) {
                        line += "§f";
                    }
                }

            }
            int charlimit = 16;

            String prefix, suffix = null;
            if (line == null || line.isEmpty()) {
                prefix = "";
            } else if (line.length() <= charlimit) {
                prefix = line;
            } else {
                int cutindex = charlimit;
                if (line.charAt(charlimit - 1) == ChatColor.COLOR_CHAR) cutindex = charlimit - 1;
                prefix = line.substring(0, cutindex);
                String suffix_ = line.substring(cutindex);
                ChatColor potentialColorAdder = null;
                if (suffix_.length() >= 2 && suffix_.charAt(0) == ChatColor.COLOR_CHAR) {
                    potentialColorAdder = ChatColor.getByChar(suffix_.charAt(1));
                }
                String lastColor = ChatColor.getLastColors(prefix);
                boolean addColor = potentialColorAdder == null || potentialColorAdder.isFormat();
                suffix = (addColor ? (lastColor.isEmpty() ? ChatColor.RESET.toString() : lastColor) : "") + suffix_;
            }

            List<String> score = Collections.singletonList(ChatColor.values()[invertforscore(lineID)].toString());

            if (Main.getVersion() >= 17) {


                Class scoreboardteam = ClassGetter.ScoreboardTeam.getClassRight();
                Object newfaketeam = scoreboardteam.getConstructors()[0].newInstance(null, "");
                Class bPacketClass = ClassHelper.getDeclaredClass(pclass, "b");

                Object bPacket = bPacketClass.getConstructors()[0].newInstance(newfaketeam);

                Optional OPacket = Optional.of(bPacket);
                if (teamHandlerMode == TeamHandlerMode.REMOVE) {
                    Object packet = ClassHelper.getConstructorByArgs(pclass, 4).newInstance(id + "%" + lineID, teamHandlerMode.ordinal(), OPacket, score);
                    ClassHelper.setField(packet, "j", null);
                    PacketHandler.sendPacket(packet, new ArrayList<>(players));
                } else {
                    ClassHelper.setField(bPacket, "b", createString(prefix));
                    ClassHelper.setField(bPacket, "c", suffix == null ? createString("") : createString(suffix));
                    ClassHelper.setField(bPacket, "d", "always");
                    ClassHelper.setField(bPacket, "e", "always");
                    ClassHelper.setField(bPacket, "f", Class.forName("net.minecraft.EnumChatFormat").getDeclaredField("v").get(null));
                    Object packet = ClassHelper.getConstructorByArgs(pclass, 4).newInstance(id + "%" + lineID, teamHandlerMode.ordinal(), OPacket, score);
                    if (teamHandlerMode.equals(TeamHandlerMode.CREATE)) {
                        ClassHelper.setField(packet, "j", Collections.singletonList(ChatColor.values()[invertforscore(lineID)].toString()));
                    } else {
                        ClassHelper.setField(packet, "j", null);
                    }

                    PacketHandler.sendPacket(packet, new ArrayList<>(players));
                }


                return;
            }

            Object p = pclass.newInstance();
            ClassHelper.setField(p, "a", id + "%" + lineID);
            ClassHelper.setField(p, "i", teamHandlerMode.ordinal());
            if (teamHandlerMode == TeamHandlerMode.CREATE || teamHandlerMode == TeamHandlerMode.UPDATE) {

                ClassHelper.setField(p, "c", createString(prefix));
                ClassHelper.setField(p, "d", suffix == null ? createString("") : createString(suffix));
                ClassHelper.setField(p, "e", "always");

                if (teamHandlerMode == TeamHandlerMode.CREATE) {
                    ClassHelper.setField(p, "h", score);
                }
            }
            PacketHandler.sendPacket(p, new ArrayList<>(players));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public int invertforscore(int id) {
        return 15 - id;
    }

    public void updateTitle(String title) {
        if (title.length() > 32) {
            this.title = title.substring(0, 31);
        } else {
            this.title = title;
        }
        update();
    }

    public Object getPacketPlaytOutScoreboardObjective(int d) {

        Class pclass = ClassGetter.PacketPlayOutScoreboardObjective.getClassRight();

        try {
            if (Main.getVersion() < 17) {

                Object p = pclass.newInstance();

                ClassHelper.setField(p, "a", id);
                ClassHelper.setField(p, "b", createString(title));
                ClassHelper.setField(p, "c", getISCField("INTEGER"));
                ClassHelper.setField(p, "d", d);
                return p;
            } else {
                Constructor cons = ClassHelper.getConstructorByArgs(pclass, 2);
                Object packet = cons.newInstance(createFakeScoreboardObjective(), d);
                ClassHelper.setField(packet, "d", id);
                ClassHelper.setField(packet, "e", createString(title));
                ClassHelper.setField(packet, "f", getISCField("a"));
                ClassHelper.setField(packet, "g", d);
                return packet;
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getISCField(String s) {
        Class ISC = ClassGetter.IScoreboardCriteria.getClassRight();
        Class ISCenum = ClassHelper.getDeclaredClass(ISC, "EnumScoreboardHealthDisplay");
        try {
            return ISCenum.getField(s).get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

    private Object createString(String s) {
        if (Main.getVersion() >= 13) {
            return createIChatComponent(s);
        }
        return s;
    }

    private Object createIChatComponent(String s) {
        Method Ma = null;
        try {
            Class<?> CIChatBaseComponent = ClassGetter.IChatBaseComponent.getClassRight();
            Class<?> CChatSerializer = CIChatBaseComponent.getDeclaredClasses()[0];
            Ma = CChatSerializer.getMethod("a", String.class);
            return Ma.invoke(CChatSerializer, "{\"text\": \"" + s + "\"}");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void create(UUID uuid) {
        Object p = getPacketPlaytOutScoreboardObjective(0);
        PacketHandler.sendPacket(p, uuid);
    }

    private void update() {
        Object p = getPacketPlaytOutScoreboardObjective(2);
        PacketHandler.sendPacket(p, new ArrayList<>(viewers));
    }

    private void deletefor(UUID uuid) {
        Object p = getPacketPlaytOutScoreboardObjective(1);
        PacketHandler.sendPacket(p, uuid);
    }

    private Object createFakeScoreboardObjective() {
        Class classScoreboardObjective = ClassGetter.ScoreboardObjective.getClassRight();
        try {
            return classScoreboardObjective.getConstructors()[0].newInstance(null, id, null, createString(title), getISCField("a"));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void display(UUID uuid) {
        try {
            if (Main.getVersion() >= 17) {

                Class classDisplay = ClassGetter.PacketPlayOutScoreboardDisplayObjective.getClassRight();
                ClassHelper.getConstructorByArgs(classDisplay, 2).newInstance(1, createFakeScoreboardObjective());

                Object packet = ClassHelper.getConstructorByArgs(classDisplay, 2).newInstance(1, createFakeScoreboardObjective());
                ClassHelper.setField(packet, "b", id);
                ClassHelper.setField(packet, "a", 1);

                PacketHandler.sendPacket(packet, uuid);
            } else {
                Object p = ClassGetter.PacketPlayOutScoreboardDisplayObjective.getClassRight().newInstance();
                ClassHelper.setField(p, "a", 1);
                ClassHelper.setField(p, "b", id);
                PacketHandler.sendPacket(p, uuid);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public List<String> getLines() {
        return lines;
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
