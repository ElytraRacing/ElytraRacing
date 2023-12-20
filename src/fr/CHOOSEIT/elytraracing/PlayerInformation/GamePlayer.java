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

package fr.CHOOSEIT.elytraracing.PlayerInformation;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public final class GamePlayer {
    private final int TIME_UNDEFINED = -1;

    private final UUID uuid;
    private Player player;
    private boolean atStart;
    private boolean isPlaying;
    private long timeFinished;
    private boolean DNF;
    private int score;

    public GamePlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.atStart = false;
        this.timeFinished = TIME_UNDEFINED;
        this.DNF = false;
        this.score = 0;

        join();
    }

    public void addScore(int value) {
        score += value;
    }

    public int getScore() {
        return score;
    }

    public void setDNF(boolean DNF) {
        this.DNF = DNF;
    }

    public boolean isDNF() {
        return DNF;
    }

    public boolean finishedPlayingMap() {
        return isDNF() || getTimeFinished() != TIME_UNDEFINED;
    }

    public void setTimeFinished(long timeFinished) {
        this.timeFinished = timeFinished;
    }

    public long getTimeFinished() {
        return timeFinished;
    }

    public void clearTimeFinished() {
        setTimeFinished(TIME_UNDEFINED);
    }

    public void resetValuesRound() {
        clearTimeFinished();
        setDNF(false);
    }

    public void checkAtStart() {
        this.atStart = true;
        this.isPlaying = true;
    }

    public void left() {
        this.isPlaying = false;
    }
    //TODO
    public void join(){
        this.isPlaying = true;
        updatePlayer();
    }
    private void updatePlayer(){
        player = Bukkit.getPlayer(uuid);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public Player getSpigotPlayer() {
        if (player == null || !player.isOnline() || !isPlaying) {
            updatePlayer();
        }
        return player;
    }

    public Location getLocation() {
        return getSpigotPlayer().getLocation();
    }

    public void teleport(Location location) {
        getSpigotPlayer().teleport(location);
    }

    public void teleport(GamePlayer player) {
        teleport(player.getSpigotPlayer());
    }

    public void teleport(Entity player) {
        getSpigotPlayer().teleport(player);
    }

    public String getName() {
        return getSpigotPlayer().getName();
    }

    public PlayerInventory getInventory() {
        return getSpigotPlayer().getInventory();
    }

    public void setGameMode(GameMode gameMode) {
        getSpigotPlayer().setGameMode(gameMode);
    }

    public GameMode getGameMode() {
        return getSpigotPlayer().getGameMode();
    }

    public void sendMessage(String message) {
        getSpigotPlayer().sendMessage(message);
    }

    public void show(Collection<GamePlayer> playerList) {
        playerList.forEach(this::show);
    }

    public void show(GamePlayer player) {
        if (!this.equals(player)) {
            this.getSpigotPlayer().showPlayer(player.getSpigotPlayer());
        }
    }

    public void show(Player player) {
        if (!this.equals(player)) {
            this.getSpigotPlayer().showPlayer(player);
        }
    }

    public void hide(Collection<GamePlayer> playerList) {
        playerList.forEach(this::hide);
    }

    public void hide(GamePlayer player) {
        if (!this.equals(player)) {
            this.getSpigotPlayer().showPlayer(player.getSpigotPlayer());
        }
    }

    public void mutualShow(GamePlayer player) {
        player.show(this);
        this.show(player);
    }

    public void mutualShow(Collection<GamePlayer> playerList) {
        playerList.forEach(this::mutualShow);
    }

    public void mutualHide(GamePlayer player) {
        player.hide(this);
        this.hide(player);
    }

    public void mutualHide(Collection<GamePlayer> playerList) {
        playerList.forEach(this::mutualHide);
    }

    public void addPotionEffect(PotionEffect potionEffect) {
        getSpigotPlayer().addPotionEffect(potionEffect);
    }

    public boolean isAtStart() {
        return atStart;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GamePlayer && uuid.equals(((GamePlayer) obj).uuid);
    }
}
