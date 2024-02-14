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

package fr.CHOOSEIT.elytraracing.gamesystem.Games;

import fr.CHOOSEIT.elytraracing.PlayerInformation.GamePlayer;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.PlayerSaver;
import fr.CHOOSEIT.elytraracing.ScoreboardConfig;
import fr.CHOOSEIT.elytraracing.SqlHandle.DataBase;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.api.elytraracing.erEnum.GameType;
import fr.CHOOSEIT.elytraracing.gamesystem.GameDurationType;
import fr.CHOOSEIT.elytraracing.gamesystem.PlayerMode;
import fr.CHOOSEIT.elytraracing.gamesystem.Scoring;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.scoreboard.BoardHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class Training extends RaceMode {

    public Training(Player player, Map map) {
        super("§" + player.getName() + "-training", GameDurationType.HOSTDURATION, 1, 1, player, map);
    }

    @Override
    public void updateBoard() {
        ScoreboardConfig scoreboardConfig = Main.scoreboardConfig;
        updatePrivateBoardSaver(scoreboardConfig.Training.get(ScoreboardConfig.BoardState.TRAINING), new ArrayList<>(getGamePlayers()));
    }

    public GamePlayer getPlayer() {
        if (getGamePlayers().isEmpty()) {
            return null;
        }
        return getGamePlayers().get(0);
    }

    @Override
    public void Start() {
        this.PlayerDNF.clear();
        askCancelCheckerTimer();
        GamePlayer p = getPlayer();
        p.resetValuesRound();
        if (p != null && cmc.CHALLENGE_MAXGAME && Main.currentDataBase.getMapFinished(p.getName()) >= Main.cmc().CHALLENGE_MAXGAME_PER_PLAYER && !DataBase.madeChallenge(DataBase.Challenges.MAXGAME, p.getName())) {
            for (String s : Main.cmc().CHALLENGE_MAXGAME_COMPLETION_COMMAND.split("§%§")) {
                PlayerSaver.addCommandExe(p.getSpigotPlayer(), new Utils.CommandExe(s, 0));
            }
            DataBase.completeChallenge(DataBase.Challenges.MAXGAME, p.getName());
            PlayerLeave(p);
        }
        super.Start();
    }

    @Override
    protected void sendMapDifficulty(GamePlayer p, Map m) {

    }

    @Override
    protected void giveStartItems(GamePlayer p) {
        super.giveStartItems(p);
        p.getInventory().setItem(getSlot(cmc.ITEM_BED_SLOT, 8), Utils.getItemStack(cmc.ITEM_BED_MATERIAL, Arrays.asList("BED", "LEGACY_BED_BLOCK"), cmc.basicsettingnoprefix(cmc.ITEM_BED, null), p.getName()));

    }

    @Override
    public ArrayList<Scoring> getRaceRank() {
        return null;
    }

    @Override
    public ArrayList<Scoring> getScoreBoard() {
        return null;
    }

    @Override
    public void OptiIncrementation(GamePlayer player, String value, int incrementValue) {
        Main.currentDataBase.IncrementValue_playerinfo(player.getSpigotPlayer(), value, incrementValue);
    }

    private static String USERNAME = "%%__USER__%%";

    @Override
    public void OptiIncrementation_player_map_stats(GamePlayer player, Map map, String value, int incrementValue) {
        Main.currentDataBase.IncrementValue_player_map_stats(player.getSpigotPlayer(), map, value, incrementValue);
    }

    @Override
    public GameType getType() {
        return GameType.TRAINING;
    }

    @Override
    public void EndedMapVerifyEndRound() {
        Start();
    }

    @Override
    public void PlayerLeave(GamePlayer p, boolean SilenceMode) {
        playerLeaveImportants(p.getSpigotPlayer(), false);
        removePlayerFrom(p, PlayerMode.ALIVE);
        BoardHandler.getBoard(p.getSpigotPlayer(), "").delete();
        Delete();
        Utils.ReloadShowHide(p.getSpigotPlayer());
    }

    @Override
    public void playerRestart(GamePlayer player) {
        Start();
    }
}
