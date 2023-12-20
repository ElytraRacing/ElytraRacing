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

import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.mapsystem.DLV;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PlayerInformationSaver {
    static CustomMessageConfig cmc = Main.cmc();

    private Game game;
    private HashMap<Player, PlayerInformationInstance> hashsaver;

    public PlayerInformationSaver(Game game) {
        this.game = game;
        hashsaver = new HashMap<>();
    }

    public void delete() {
        hashsaver.keySet().forEach(o -> hashsaver.get(o).delete());
        hashsaver.clear();
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private int getFireWorkSlot() {
        int slot_firework = cmc.ITEM_FIREWORK_SLOT;
        if (slot_firework < 0 || slot_firework > 8) {
            slot_firework = 2;
        }
        return slot_firework;
    }

    private int getFireWorkAmount(Player player) {
        ItemStack fireworks = player.getInventory().getItem(getFireWorkSlot());
        int currentfirework = 0;
        if (fireworks != null && fireworks.getType() == Material.getMaterial("FIREWORK")) {
            currentfirework = fireworks.getAmount();
        }
        return currentfirework;
    }

    public void addDLV(Player player, boolean isLap, ObjectClass cp) {
        DLV newDLV = new DLV(new SimpleLocation(player.getLocation()), player.getVelocity(), getFireWorkAmount(player), getPassedAdditionalObjects(player), cp);
        getInfoSave(player).add(newDLV);
        if (isLap) {
            getInfoSave(player).addDLVLap(newDLV);
        }
    }

    public void clear(Player player) {
        clear(player, true);
    }

    public void clear(Player player, boolean lapstoo) {
        if (hasInfoSave(player)) {
            hashsaver.get(player).clear(lapstoo);
        }
    }

    public void finishedlaps(Player player) {
        getInfoSave(player).finishedALap();
    }

    public int getFinishedlaps(Player player) {
        return getInfoSave(player).getFinishedlaps();
    }

    public void fullClear() {
        hashsaver.keySet().forEach(o -> hashsaver.get(o).clear());
    }

    public void clearNumberCPPassed(Player player) {
        getInfoSave(player).clearNumberCPPassed();
    }

    public DLV getFromEnd(Player player, int i) {
        return getInfoSave(player).getFromEnd(i);
    }

    public int getTotalCPPassed(Player player) {
        return getInfoSave(player).getTotalCPPassed();
    }

    public int getIdFinished(Player player) {
        return getInfoSave(player).getIdFinished();
    }

    public Location getLastLoc(Player player) {
        if (hasInfoSave(player)) {
            return hashsaver.get(player).getLastLoc();
        }
        return null;

    }

    public ArrayList<Integer> getPassedCP(Player player, boolean full) {
        return getInfoSave(player).getPassedCP(full);
    }

    public ArrayList<Integer> getPassedCPMultiLaps(Player player) {
        return getInfoSave(player).getPassedCPMultiLaps();
    }

    public void clearPassedCP(Player player) {
        getInfoSave(player).clearPassedCP();
    }

    public void addPassedCP(Player player, int id, boolean islinked) {
        getInfoSave(player).addPassedCP(id, islinked);
    }

    private boolean hasInfoSave(Player player) {
        if (!hashsaver.containsKey(player)) {
            hashsaver.put(player, new PlayerInformationInstance(player, game));
            return false;
        }
        return true;
    }

    public void setPassedAdditionalObjects(Player player, ArrayList<Integer> passedAdditionalObjects) {
        getInfoSave(player).setPassedAdditionalObjects(passedAdditionalObjects);
    }

    public void addPassedAdditionalObject(Player player, int id) {
        getInfoSave(player).addPassedAdditionalObject(id);
    }

    public ArrayList<Integer> getPassedAdditionalObjects(Player player) {
        return getInfoSave(player).getPassedAdditionalObjects();
    }

    public boolean hasPassedAdditionalObject(Player player, int id) {
        return getInfoSave(player).hasPassedAdditionalObject(id);
    }

    public long getFinished_TIME(Player player) {
        return getInfoSave(player).getFinished_TIME();
    }

    public PlayerInformationInstance getInfoSave(Player player) {
        hasInfoSave(player);
        return hashsaver.get(player);
    }

    public DLV getLastLapDLV(Player player) {
        return getInfoSave(player).getLastLapDLV();
    }

    public void setLastLoc(Player player) {
        getInfoSave(player).setLastLoc(player.getLocation().clone());
    }

    public boolean isBackCP(Player player) {
        return getInfoSave(player).isBackCP();
    }

    public void setBackCP(Player player, boolean b) {
        getInfoSave(player).setBackCP(b);
    }

    public void start(Player player) {
        getInfoSave(player).start();
    }

    public long getStartTime(Player player) {
        return getInfoSave(player).getStarttime();
    }

    public boolean hasStarted(Player player) {
        return getInfoSave(player).hasStarted();
    }

    public void addNumberCPPassed(Player player) {
        getInfoSave(player).addNumberCPPassed();
    }

    public int getNumberCPPassed(Player player) {
        return getInfoSave(player).getNumberCPPassed();
    }

    public void addnextCP(Player player, int id) {
        getInfoSave(player).addnextCP(id);
    }

    public int getCurrentCP(Player player) {
        return getInfoSave(player).getCurrentCP();
    }

    public boolean hasFinished(Player player) {
        return getInfoSave(player).hasFinished();
    }

    public void finish(Player player, int playerfinished, long time) {
        getInfoSave(player).finish(playerfinished, time);
    }

    public ArrayList<Player> getPlayerNotFinished() {
        ArrayList<Player> notfinished = new ArrayList<>();
        for (Player player : hashsaver.keySet()) {
            if (!getInfoSave(player).hasFinished()) {
                notfinished.add(player);
            }
        }
        return notfinished;
    }

    public boolean stillNotFinished() {
        return !getPlayerNotFinished().isEmpty();
    }


    static class PlayerInformationInstance {
        private Player player;
        private ArrayList<DLV> dlvs;
        private Location lastLoc;
        private boolean backCP;
        private long starttime;
        private int numberCPPassed;
        private int totalCPPassed;
        private ArrayList<Integer> nextCP;
        private ArrayList<Integer> passedCP;
        private ArrayList<Integer> fullpassedCP;
        private ArrayList<Integer> fullpassedCPMutlipleLaps;
        private ArrayList<DLV> lapsDLV;
        private ArrayList<Integer> passedAdditionalObjects;
        private boolean finished;
        private int idFinished;
        private int finishedlaps;


        private int finished_TCP;
        private int finished_LAPS;
        private long finished_TIME;

        private final Game game;

        public PlayerInformationInstance(Player player, Game game) {
            this.player = player;
            dlvs = new ArrayList<>();
            nextCP = new ArrayList<>();
            passedCP = new ArrayList<>();
            fullpassedCP = new ArrayList<>();
            fullpassedCPMutlipleLaps = new ArrayList<>();
            lapsDLV = new ArrayList<>();
            passedAdditionalObjects = new ArrayList<>();
            defaultValues();

            this.game = game;

        }

        public int getFinished_LAPS() {
            return finished_LAPS;
        }

        public int getFinished_TCP() {
            return finished_TCP;
        }

        public int getTotalCPPassed() {
            return totalCPPassed;
        }

        public ArrayList<Integer> getPassedCP(boolean fullpassedcp) {
            if (fullpassedcp) {
                return fullpassedCP;
            }
            return passedCP;
        }

        public ArrayList<Integer> getPassedCPMultiLaps() {
            return (ArrayList<Integer>) fullpassedCPMutlipleLaps.clone();
        }

        public void addPassedCP(int id, boolean islinked) {
            fullpassedCP.add(id);
            if (!islinked) {
                passedCP.add(id);
                fullpassedCPMutlipleLaps.add(id);
            }
        }

        public void finishedALap() {
            passedAdditionalObjects.clear();
            finishedlaps++;
        }

        public void addDLVLap(DLV dlv) {
            lapsDLV.add(dlv);
        }

        public DLV getLastLapDLV() {
            if (lapsDLV.isEmpty()) {
                return null;
            }
            return lapsDLV.get(lapsDLV.size() - 1);
        }

        public int getFinishedlaps() {
            return finishedlaps;
        }

        public void addnextCP(int id) {
            nextCP.add(id);
        }

        public int getCurrentCP() {
            if (nextCP.isEmpty()) {
                return 0;
            }
            return nextCP.get(nextCP.size() - 1);
        }

        public void clearPassedCP() {
            passedCP.clear();
            fullpassedCP.clear();
        }

        public void clearNumberCPPassed() {
            numberCPPassed = 0;
        }

        public void addNumberCPPassed() {
            totalCPPassed++;
            numberCPPassed++;
        }

        public int getNumberCPPassed() {
            return numberCPPassed;
        }

        public boolean isBackCP() {
            return backCP;
        }

        public void setBackCP(boolean backCP) {
            this.backCP = backCP;
        }

        public Location getLastLoc() {
            return lastLoc;
        }

        public void setLastLoc(Location lastLoc) {
            this.lastLoc = lastLoc;
        }

        public Player getPlayer() {
            return player;
        }

        public void add(DLV dlv) {
            dlvs.add(dlv);
        }

        public boolean hasStarted() {
            return starttime != -1;
        }

        public long getStarttime() {
            return starttime;
        }

        public void start() {
            starttime = System.currentTimeMillis();
        }

        public boolean hasFinished() {
            return finished;
        }

        public int getIdFinished() {
            return idFinished;
        }

        public void setFinished() {
            finished = true;
        }

        public void finish(int playerfinished, long time) {
            setFinished();
            idFinished = playerfinished;
            finished_TCP = totalCPPassed;
            finished_LAPS = finishedlaps;
            finished_TIME = time;
        }

        public long getFinished_TIME() {
            return finished_TIME;
        }

        public DLV getFromEnd(int i) {
            //1 = last
            //2 = second from the bottom
            int size = dlvs.size();
            int id = size - i;
            if (id >= size || id < 0) {
                return null;
            }
            return dlvs.get(id);
        }

        public void clear() {
            clear(false);
        }

        public void clear(boolean lapstoo) {
            defaultValues();
            dlvs.clear();
            nextCP.clear();
            passedCP.clear();
            fullpassedCP.clear();
            passedAdditionalObjects.clear();
            if (lapstoo) {
                lapsDLV.clear();
                fullpassedCPMutlipleLaps.clear();
            }

        }

        public void setPassedAdditionalObjects(ArrayList<Integer> passedAdditionalObjects) {
            this.passedAdditionalObjects = (ArrayList<Integer>) passedAdditionalObjects.clone();
        }

        public void addPassedAdditionalObject(int id) {
            this.passedAdditionalObjects.add(id);
        }

        public ArrayList<Integer> getPassedAdditionalObjects() {
            return (ArrayList<Integer>) passedAdditionalObjects.clone();
        }

        public boolean hasPassedAdditionalObject(int id) {
            return passedAdditionalObjects.contains(id);
        }

        private void defaultValues() {
            lastLoc = null;
            backCP = false;
            starttime = -1;
            numberCPPassed = 0;
            nextCP.add(0);
            finished = false;
            finishedlaps = 0;
            totalCPPassed = 0;
        }

        public void delete() {
            nextCP.clear();
            dlvs.clear();
            fullpassedCP.clear();
            fullpassedCPMutlipleLaps.clear();
            passedCP.clear();
            lapsDLV.clear();
            passedAdditionalObjects.clear();
            try {
                this.finalize();
            } catch (Throwable throwable) {

            }
        }
    }

}

