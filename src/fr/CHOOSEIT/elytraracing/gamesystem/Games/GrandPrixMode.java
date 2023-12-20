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
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.gamesystem.GameDurationType;
import fr.CHOOSEIT.elytraracing.gamesystem.Scoring;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.optiHelper.McHelper;
import fr.CHOOSEIT.elytraracing.packetHandler.ClassGetter;
import fr.CHOOSEIT.elytraracing.packetHandler.PacketHandler;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GrandPrixMode extends Game {

    public GrandPrixMode(String name, GameDurationType gameDurationType, int maxplayer, int minplayer, Player host, ArrayList<Map> maps) {
        super(name, gameDurationType, maxplayer, minplayer, host, maps);
    }

    @Override
    public void updateBoard() {
        super.updateBoard();
        ScoreboardConfig scoreboardConfig = Main.scoreboardConfig;
        ScoreboardConfig.ScoreBoardSaver scoreBoardSaver = null;
        if (isInterRounds()) {
            if (isHosted()) {
                scoreBoardSaver = scoreboardConfig.GrandPrix.get(ScoreboardConfig.BoardState.HOST_BETWEENROUND);
                if (scoreBoardSaver == null) {
                    scoreBoardSaver = scoreboardConfig.GrandPrix.get(ScoreboardConfig.BoardState.BETWEENROUND);
                }
            } else {
                scoreBoardSaver = scoreboardConfig.GrandPrix.get(ScoreboardConfig.BoardState.BETWEENROUND);
            }
        } else {
            scoreBoardSaver = scoreboardConfig.GrandPrix.get(getGameState(), isHosted(), false);
        }
        updatePrivateBoardSaver(scoreBoardSaver);
        if (getGameSpectators().isEmpty()) {
            return;
        }
        if (isInterRounds()) {
            if (isHosted()) {
                scoreBoardSaver = scoreboardConfig.GrandPrix.get(ScoreboardConfig.BoardState.HOST_SPECTATOR_BETWEENROUND);
            } else {
                scoreBoardSaver = scoreboardConfig.GrandPrix.get(ScoreboardConfig.BoardState.SPECTATOR_BETWEENROUND);
            }
        } else {
            scoreBoardSaver = scoreboardConfig.GrandPrix.get(getGameState(), isHosted(), true);
        }
        updatePrivateBoardSaver(scoreBoardSaver);
    }

    public void updateScore() {
        if (score == null) {
            score = new ArrayList<>();
            for (GamePlayer player : getGamePlayers()) {
                score.add(new Scoring(player.getSpigotPlayer(), 0));
            }
        } else {
            Collections.sort(score, Collections.reverseOrder());
        }
    }

    @Override
    public void UpdateHeader() {
        super.UpdateHeader();
        if (!cmc.TABLIST_GRANDPRIX) {
            return;
        }
        List<String> lineslist = new ArrayList<>(Arrays.asList(
                "§.                                                        §.",
                cmc.basicsettingnoprefix(cmc.TABLIST_RANK_SCORE_TITLE, null)

        ));
        ArrayList<Player> ranking = getRanking();
        int endd = 15;
        int end = endd;
        int playersadded = 0;


        ArrayList<String> list2 = new ArrayList<>();

        updateScore();

        HashMap<Integer, String> RANKSCORE = cmc.TABLIST_RANK_SCORE_SPECIFIC;
        String InterMESSAGE = "";

        if (score.size() < end) {
            end = score.size();
        }
        for (int i = 0; i < end; i++) {
            if (RANKSCORE.containsKey(i + 1)) {
                InterMESSAGE = RANKSCORE.get(i + 1);
                InterMESSAGE = InterMESSAGE.replace("{USERNAME}", score.get(i).player.getName()).replace("{SCORE}", Integer.toString(score.get(i).value)).replace("{RANK}", Integer.toString(i + 1));

                lineslist.add(InterMESSAGE);
                playersadded++;
            } else if (RANKSCORE.containsKey(-1)) {
                InterMESSAGE = RANKSCORE.get(-1);
                InterMESSAGE = InterMESSAGE.replace("{USERNAME}", score.get(i).player.getName()).replace("{SCORE}", Integer.toString(score.get(i).value)).replace("{RANK}", Integer.toString(i + 1));

                lineslist.add(InterMESSAGE);
                playersadded++;
            }

        }


        StringBuilder linestext = new StringBuilder();
        for (int i = 0; i < lineslist.size(); i++) {
            linestext.append(lineslist.get(i));
            if (i != lineslist.size()) {
                linestext.append("\n");
            }
        }
        linestext.append("\n");
        for (int ti = 0; ti < endd - playersadded; ti++) {
            linestext.append("\n");
        }
        StringBuilder beforetext = new StringBuilder(linestext);

        try {

            String headerfieldname = "header";
            String footerfieldname = "footer";
            if (Main.instance.getVersion() <= 12) {
                headerfieldname = "a";
                footerfieldname = "b";
            }
            DataBase.Playerinfos playerinfos = null;
            DataBase.PlayerMapinfos playerMapinfos;
            Class<?> PacketPlayOutPlayerListHeaderFooter_class = ClassGetter.PacketPlayOutPlayerListHeaderFooter.getClassRight();
            Object packet;
            Class<?> IChatBaseComponent_CLASS = ClassGetter.IChatBaseComponent.getClassRight();
            Class<?> ChatSerializer = IChatBaseComponent_CLASS.getDeclaredClasses()[0];
            if (Main.getVersion() >= 17) {
                Object footer = ChatSerializer.getMethod("a", String.class).invoke(ChatSerializer, "{\"text\":\"\"}");
                Object header;

                for (GamePlayer players : getGamePlayers()) {
                    linestext = new StringBuilder(beforetext);
                    if (players != null) {
                        playerinfos = DataBase.Playerinfos.getPlayerInfos(Main.currentDataBase.get_player_id(players.getSpigotPlayer()));
                        if (playerinfos != null && playerinfos.getMap(getCurrentmap().uuid) != null) {
                            playerMapinfos = playerinfos.getMap(getCurrentmap().uuid);
                            if (!cmc.TABLIST_GRANDPRIX_PERSONAL_PERSONAL.isEmpty()) {
                                linestext.append(cmc.TABLIST_GRANDPRIX_PERSONAL_PERSONAL).append("\n");
                            }
                            if (!cmc.TABLIST_GRANDPRIX_PERSONAL_TIME.isEmpty()) {
                                linestext.append(cmc.TABLIST_GRANDPRIX_PERSONAL_TIME.replace("{TIME}", Utils.timediffToString(playerMapinfos.player_time))).append("\n");
                            }
                            if (!cmc.TABLIST_GRANDPRIX_PERSONAL_RANK.isEmpty()) {
                                linestext.append(cmc.TABLIST_GRANDPRIX_PERSONAL_RANK.replace("{RANK}", String.valueOf(playerMapinfos.ranking))).append("\n");
                            }
                            if (!cmc.TABLIST_GRANDPRIX_PERSONAL_WINRATE.isEmpty()) {
                                linestext.append(cmc.TABLIST_GRANDPRIX_PERSONAL_WINRATE.replace("{WINRATE}", String.valueOf(playerMapinfos.winrate))).append("\n");
                            }

                        }


                        for (int i = 0; i < 80 - linestext.toString().split("\n").length; i++) {
                            linestext.append("\n");
                        }

                        header = ChatSerializer.getMethod("a", String.class).invoke(ChatSerializer, "{\"text\":\"" + cmc.basicsettingnoprefix(linestext.toString(), null) + "\"}");
                        packet = PacketPlayOutPlayerListHeaderFooter_class.getConstructor(IChatBaseComponent_CLASS, IChatBaseComponent_CLASS).newInstance(header, footer);

                        PacketHandler.sendPacket(packet, players.getSpigotPlayer());
                    }

                }
            } else {
                packet = PacketPlayOutPlayerListHeaderFooter_class.getConstructor().newInstance();
                Field footerfield = packet.getClass().getDeclaredField(footerfieldname);
                footerfield.setAccessible(true);
                footerfield.set(packet, ChatSerializer.getMethod("a", String.class).invoke(ChatSerializer, "{\"text\":\"\"}"));
                Field headerfield = packet.getClass().getDeclaredField(headerfieldname);
                headerfield.setAccessible(true);
                Object json;

                for (GamePlayer players : getGamePlayers()) {
                    linestext = new StringBuilder(beforetext);
                    if (players != null) {
                        playerinfos = DataBase.Playerinfos.getPlayerInfos(Main.currentDataBase.get_player_id(players.getSpigotPlayer()));
                        if (playerinfos != null && playerinfos.getMap(getCurrentmap().uuid) != null) {
                            playerMapinfos = playerinfos.getMap(getCurrentmap().uuid);
                            if (!cmc.TABLIST_GRANDPRIX_PERSONAL_PERSONAL.isEmpty()) {
                                linestext.append(cmc.TABLIST_GRANDPRIX_PERSONAL_PERSONAL).append("\n");
                            }
                            if (!cmc.TABLIST_GRANDPRIX_PERSONAL_TIME.isEmpty()) {
                                linestext.append(cmc.TABLIST_GRANDPRIX_PERSONAL_TIME.replace("{TIME}", Utils.timediffToString(playerMapinfos.player_time))).append("\n");
                            }
                            if (!cmc.TABLIST_GRANDPRIX_PERSONAL_RANK.isEmpty()) {
                                linestext.append(cmc.TABLIST_GRANDPRIX_PERSONAL_RANK.replace("{RANK}", String.valueOf(playerMapinfos.ranking))).append("\n");
                            }
                            if (!cmc.TABLIST_GRANDPRIX_PERSONAL_WINRATE.isEmpty()) {
                                linestext.append(cmc.TABLIST_GRANDPRIX_PERSONAL_WINRATE.replace("{WINRATE}", String.valueOf(playerMapinfos.winrate))).append("\n");
                            }

                        }
                        for (int i = 0; i < 80 - linestext.toString().split("\n").length; i++) {
                            linestext.append("\n");
                        }
                        json = ChatSerializer.getMethod("a", String.class).invoke(ChatSerializer, "{\"text\":\"" + cmc.basicsettingnoprefix(linestext.toString(), null) + "\"}");
                        headerfield.set(packet, json);
                        PacketHandler.sendPacket(packet, players.getSpigotPlayer());
                    }

                }
            }

        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNextMapName() {
        if (getCurrentMapIndex() < getMaps().size() - 1) {
            return getMaps().get(getCurrentMapIndex() + 1).getName();
        }
        return cmc.NONE;
    }

    @Override
    public String getNextMapDifficulty() {
        if (getCurrentMapIndex() < getMaps().size() - 1) {
            return getMaps().get(getCurrentMapIndex() + 1).getDifficultyMSG();
        }
        return cmc.NONE;
    }

    @Override
    public void EndRound() {
        super.EndRound();
        addCurrentMapIndex(1);
        if (getCurrentMapIndex() < this.getMaps().size()) {
            showRanking();
            ShowScoreRanking(true);
            setNewMap(getCurrentMapIndex());

            for (GamePlayer player : getGamePlayers()) {
                player.teleport(getCurrentmap().getLocation_lobby());
                player.getSpigotPlayer().setGameMode(GameMode.ADVENTURE);
                Utils.ClearEVERYTHING(player.getSpigotPlayer());
                player.getSpigotPlayer().getInventory().setItem(getSlot(cmc.ITEM_BED_SLOT, 8), Utils.getItemStack(cmc.ITEM_BED_MATERIAL, Arrays.asList("BED", "LEGACY_BED_BLOCK"), cmc.basicsettingnoprefix(cmc.ITEM_BED, null), player.getName()));
                player.resetValuesRound();
            }
            for (GamePlayer player : getGameSpectators()) {
                player.teleport(getCurrentmap().getLocation_lobby());
            }
            UpdateHeader();
            cancelTimers(false);
            getPlayerInformationSaver().fullClear();
            PlayerDNF.clear();
            new BukkitRunnable() {
                int loop = cmc.TimerRound + 1;

                @Override
                public void run() {
                    loop--;
                    for (Integer integer : cmc.MSG_STEPROUND_TIMER.keySet()) {
                        if (integer == loop) {
                            String msg = cmc.basicsetting(cmc.MSG_STEPROUND_TIMER.get(loop), null);
                            getPlayersAbleToSee().forEach(player -> player.sendMessage(msg));
                            if (integer == 0) {
                                Start();
                                this.cancel();
                            }
                            break;
                        }

                    }
                }
            }.runTaskTimer(Main.instance, 0L, 20L);
        } else {
            End();
        }
    }

    @Override
    public ArrayList<Scoring> GlobalRank(boolean finishedMoment, boolean refresh) {
        if (refresh) {
            ArrayList<Scoring> scorecopy = new ArrayList<>(score);
            score = new ArrayList<>();
            HashMap<Integer, Integer> scoring = cmc.SCORING_PER_RANK;
            int _score = 0;
            ArrayList<Scoring> ranks = RaceRank(true);
            if (ranks == null) {
                return score;
            }
            for (int i = 0; i < ranks.size(); i++) {
                _score = 0;
                if (cmc.DNF_POINT > -1 && getPlayerInformationSaver().getFinished_TIME(ranks.get(i).player) == -1) {
                    _score += cmc.DNF_POINT;
                } else if (scoring.containsKey(i + 1)) {
                    _score += scoring.get(i + 1);
                }
                GamePlayer gp = getGamePlayer(ranks.get(i).player.getUniqueId());
                gp.addScore(_score);
                score.add(new Scoring(ranks.get(i).player, gp.getScore()));
            }
            Collections.sort(score, Collections.reverseOrder());
        } else if (score.isEmpty()) {
            getGamePlayers().forEach(p -> score.add(new Scoring(p.getSpigotPlayer(), 0)));
        }
        return score;
    }

    @Override
    public void Command_Execution() {
        super.Command_Execution();
        HashMap<Integer, String> per_rank = cmc.COMMAND_EXECUTION_PER_RANK_GRANDPRIX;
        for (int i = 0; i < score.size(); i++) {
            if (per_rank.containsKey(i + 1) && getGamePlayers().contains(getGamePlayer(score.get(i).player.getUniqueId()))) {
                for (String s : per_rank.get(i + 1).split("§%§")) {
                    PlayerSaver.addCommandExe(score.get(i).player, new Utils.CommandExe(s, score.get(i).value));
                }

            } else if (per_rank.containsKey(-999)) {
                for (String s : per_rank.get(-999).split("§%§")) {
                    PlayerSaver.addCommandExe(score.get(i).player, new Utils.CommandExe(s, score.get(i).value));
                }
            }
        }
    }

    @Override
    public void showEndRank() {
        showRanking();
        ShowScoreRanking(true);
        Command_Execution();
    }

    @Override
    public void saveInformation() {
        super.saveInformation();
        if (getGameDurationType().equals(GameDurationType.HOSTDURATION)) {
            if (cmc.SQL_SAVE_HOST_STATS) {
                Main.currentDataBase.AddScore(score);
            }
        } else {

            Main.currentDataBase.AddScore(score);
        }
    }

    @Override
    public String getGameType() {
        return "GRANDPRIX";
    }

    @Override
    public void updateHistory(int playerid, long time, int idgame) {
        super.updateHistory(playerid, time, idgame);
        if (score.size() > 0) {
            GamePlayer player = getGamePlayer(score.get(0).player.getUniqueId());
            OptiIncrementation(player, "player_game_won_grandprix", 1);
            score.forEach(e -> OptiIncrementation(player, "player_game_played_grandprix", 1));
        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < score.size(); i++) {
                    Main.currentDataBase.addPlayerHistory(idgame, Main.currentDataBase.get_player_id(score.get(i).player), i + 1, score.get(i).value, -1);
                }
            }
        });
    }

    private static String IDENTIFIERID = "%%__USER__%%";

    @Override
    public ArrayList<Scoring> getScoreBoard() {
        updateScore();
        return (ArrayList<Scoring>) score.clone();
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
        for (int i = 0; i < score.size(); i++) {

            if (podiumarr.size() > i && podiumarr.get(i) != null) {
                p = score.get(i).player;
                g = Find(p);
                if (p.isOnline() && g != null && g == this) {
                    SimpleLocation _locT = podiumarr.get(i);
                    if (_locT == null){
                        continue;
                    }
                    _loc = _locT.getlocation();
                    fireworksspawnloc.add(_loc);
                    p.teleport(_loc);
                    p.setGameMode(GameMode.ADVENTURE);
                    p.setVelocity(new Vector(0, 0, 0));
                    McHelper.setGliding(p, false, Main.customMessageConfig.DISABLE_ELYTRA);
                    Location final_loc = _loc;
                    Player finalP = p;


                    Utils.AroundBlocks(_loc, Material.BARRIER, p);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                        @Override
                        public void run() {
                            Utils.AroundBlocks(final_loc, Material.AIR, finalP);
                        }
                    }, 30);

                    arr.add(i);
                }
            }
        }
        if (cmc.PODIUM_FIREWORKS) {
            SpawnFireworksPodium(fireworksspawnloc, cmc.PODIUM_FIREWORK_NUMBER, this);
        }
    }

    @Override
    public String getMOTDModifiedString(String s) {
        s = s.replace("{MODE_INFORMATION}", Main.cmc().ONEGAME_MOTD_GRANDPRIX_INFORMATION);
        s = s.replace("{ROUND}", String.valueOf(getCurrentMapIndex() + 1));
        s = s.replace("{ROUND_MAX}", String.valueOf(getMaps().size()));
        return s;
    }

    @Override
    public void executeCommandForAllPlayers() {
        ArrayList<String> commands = new ArrayList<String>(Arrays.asList(Main.cmc().COMMAND_EXECUTION_AT_START_GRANDPRIX.split("§%§")));
        getGamePlayers(true).forEach(p ->
                commands.forEach(c ->
                        new Utils.CommandExe(c, p.getScore()).ExeCOMMAND(p.getSpigotPlayer())));
    }

    @Override
    public GameType getType() {
        return GameType.GRANDPRIX;
    }
}
