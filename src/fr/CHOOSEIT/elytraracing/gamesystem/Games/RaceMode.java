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
import fr.CHOOSEIT.elytraracing.*;
import fr.CHOOSEIT.elytraracing.SqlHandle.DataBase;
import fr.CHOOSEIT.elytraracing.api.elytraracing.erEnum.GameType;
import fr.CHOOSEIT.elytraracing.gamesystem.*;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.optiHelper.McHelper;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class RaceMode extends Game {

    public RaceMode(String name, GameDurationType gameDurationType, int maxplayer, int minplayer, Player host, Map map) {
        super(name, gameDurationType, maxplayer, minplayer, host, new ArrayList<Map>(Collections.singletonList(map)));
    }

    @Override
    public void updateBoard() {
        ScoreboardConfig scoreboardConfig = Main.scoreboardConfig;
        updatePrivateBoardSaver(scoreboardConfig.RaceMode.get(getGameState(), isHosted(), false), new ArrayList<>(getGamePlayers()));
        if (getGameSpectators().isEmpty()) {
            return;
        }
        updatePrivateBoardSaver(scoreboardConfig.RaceMode.get(getGameState(), isHosted(), true), new ArrayList<>(getGameSpectators()));
    }

    @Override
    public GameType getType() {
        return GameType.RACE;
    }

    @Override
    public void EndRound() {
        super.EndRound();
        End();
    }

    @Override
    public ArrayList<Scoring> GlobalRank(boolean finishedMoment, boolean refresh) {
        return RaceRank(finishedMoment);
    }

    @Override
    public void Command_Execution() {
        super.Command_Execution();
        ArrayList<Player> ranking = getRanking();
        HashMap<Integer, String> per_rank = cmc.COMMAND_EXECUTION_PER_RANK_RACEMODE;
        for (int i = 0; i < ranking.size(); i++) {
            if (per_rank.containsKey(i + 1) && getGamePlayers().contains(getGamePlayer(ranking.get(i).getUniqueId()))) {
                for (String s : per_rank.get(i + 1).split("§%§")) {
                    PlayerSaver.addCommandExe(ranking.get(i), new Utils.CommandExe(s, 0));
                }
            } else if (per_rank.containsKey(-999)) {
                for (String s : per_rank.get(-999).split("§%§")) {
                    PlayerSaver.addCommandExe(ranking.get(i), new Utils.CommandExe(s, 0));
                }
            }
        }
        maxGameChallenge(ranking);
    }

    private void maxGameChallenge(ArrayList<Player> ranking) {
        if (Main.cmc().CHALLENGE_MAXGAME) {
            int finished = 0;
            for (Player player : ranking) {
                finished = DataBase.getMapFinished(player.getName());
                if (finished >= Main.cmc().CHALLENGE_MAXGAME_PER_PLAYER && !DataBase.madeChallenge(DataBase.Challenges.MAXGAME, player.getName())) {
                    for (String s : Main.cmc().CHALLENGE_MAXGAME_COMPLETION_COMMAND.split("§%§")) {
                        PlayerSaver.addCommandExe(player, new Utils.CommandExe(s, 0));
                    }
                    DataBase.completeChallenge(DataBase.Challenges.MAXGAME, player.getName());
                }
            }
        }
    }

    @Override
    public void showRanking() {
        super.showRanking();
        ArrayList<Scoring> rank = RaceRank(true);
        if (getGameState() == GameState.FINISHED) {
            for (int i = 0; i < rank.size(); i++) {
                if (getGamePlayers().contains(getGamePlayer(rank.get(i).player.getUniqueId()))) {
                    rank.get(i).player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_YOURRANK.replace("{RANK}", Integer.toString(i + 1)), null));
                }
            }
        }
    }

    @Override
    public void showEndRank() {
        showRanking();
        Command_Execution();
    }

    @Override
    public String getGameType() {
        return "RACEMODE";
    }

    public void addWinGame(GamePlayer winner) {
        OptiIncrementation(winner, "player_game_won_racemode", 1);
    }

    @Override
    public void updateHistory(int playerid, long time, int idgame) {
        super.updateHistory(playerid, time, idgame);

        ArrayList<Scoring> scorings = RaceRank(true);
        GamePlayer winner = getGamePlayer(scorings.get(0).player.getUniqueId());

        if ((getPlayerInformationSaver().hasFinished(winner.getSpigotPlayer()) && getPlayerInformationSaver().getFinished_TIME(winner.getSpigotPlayer()) > 0) || Main.cmc().WIN_RACEMODE_WITHOUT_FINISHING) {
            addWinGame(winner);
        }

        int rank = 0;
        for (Scoring scoring : scorings) {
            rank++;
            OptiIncrementation(getGamePlayer(scoring.player.getUniqueId()), "player_game_played_racemode", 1);
            playerid = Main.currentDataBase.get_player_id(scoring.player);
            time = getPlayerInformationSaver().getFinished_TIME(scoring.player);
            if (playerid > 0) {
                int finalPlayerid1 = playerid;
                long finalTime = time;
                int finalRank = rank;
                Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
                    @Override
                    public void run() {
                        Main.currentDataBase.addPlayerHistory(idgame, finalPlayerid1, finalRank, -1, finalTime);
                    }
                });

            }
        }
    }

    @Override
    public void Interact_DNF(GamePlayer player, ItemStack itemStack) {
        super.Interact_DNF(player, itemStack);
        if (cmc.DNF_INSTANT_QUIT) {
            LeaveMovementItem(player, itemStack);
        }
    }

    @Override
    public ArrayList<Scoring> getScoreBoard() {
        return (ArrayList<Scoring>) RaceRank(getGameState().equals(GameState.FINISHED)).clone();
    }

    @Override
    public void makePodium() {
        super.makePodium();
        ArrayList<Location> fireworksspawnloc = new ArrayList<>();
        ArrayList<SimpleLocation> podiumarr = getCurrentmap().getLocations_podium();


        Location _loc;
        Game g;
        ArrayList<Integer> arr = new ArrayList<>();

        int a = 0;
        Player p;

        for (Scoring scoring : RaceRank(true)) {
            p = scoring.player;
            a++;
            if (podiumarr.size() > a && podiumarr.get(a) != null) {
                g = Find(p);
                if (p.isOnline() && g != null && g == this) {

                    SimpleLocation _locT = podiumarr.get(a - 1);
                    if (_locT == null){
                        continue;
                    }
                    _loc = _locT.getlocation();
                    fireworksspawnloc.add(_loc);
                    p.teleport(_loc);
                    p.setGameMode(GameMode.ADVENTURE);
                    p.setVelocity(new Vector(0, 0, 0));
                    arr.add(a - 1);
                    McHelper.setGliding(p, false, Main.customMessageConfig.DISABLE_ELYTRA);

                    Location final_loc1 = _loc;
                    Player finalP = p;

                    Utils.AroundBlocks(_loc, Material.BARRIER, p);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            Utils.AroundBlocks(final_loc1, Material.AIR, finalP);
                        }
                    }, 30);
                }
            }
        }
        if (cmc.PODIUM_FIREWORKS) {
            SpawnFireworksPodium(fireworksspawnloc, cmc.PODIUM_FIREWORK_NUMBER, this);
        }
    }

    private static String ID = "%%__USER__%%";

    @Override
    public String getMOTDModifiedString(String s) {
        s = s.replace("{MODE_INFORMATION}", Main.cmc().ONEGAME_MOTD_RACEMODE_INFORMATION);
        return s;
    }

    @Override
    public void executeCommandForAllPlayers() {
        ArrayList<String> commands = new ArrayList<String>(Arrays.asList(Main.cmc().COMMAND_EXECUTION_AT_START_RACEMODE.split("§%§")));
        getGamePlayers(true).forEach(p ->
                commands.forEach(c ->
                        new Utils.CommandExe(c, p.getScore()).ExeCOMMAND(p.getSpigotPlayer())));
    }
}
