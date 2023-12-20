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

package fr.CHOOSEIT.elytraracing.SqlHandle;

import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.gamesystem.Scoring;
import fr.CHOOSEIT.elytraracing.holograms.Holograms;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class DataBase {
    String dbname;
    public static CustomMessageConfig cmc;
    Connection connection;

    public DataBase(CustomMessageConfig cmc) {
        this.cmc = cmc;
        dbname = "ElytraRacing";
    }

    public abstract Connection getSqlConnection();

    public int sizeGameHistory() {
        try {
            connection = getSqlConnection();
            PreparedStatement p = connection.prepareStatement("SELECT COUNT(*) as count FROM game_history");
            p.execute();
            ResultSet r = p.getResultSet();
            int count = 0;
            if (r.next()) {
                count = r.getInt("count");
            }
            p.close();
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addGameHistory(int id, String gameType, int playerSize) {
        try {
            connection = getSqlConnection();
            PreparedStatement p = connection.prepareStatement("INSERT INTO game_history (game_id, game_type, game_date, game_player_number) VALUES (?,?,?,?)");
            p.setInt(1, id);
            p.setString(2, gameType);
            p.setLong(3, System.currentTimeMillis());
            p.setInt(4, playerSize);
            p.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class QueryCache {
        static HashMap<String, QueryCache> queriesCache = new HashMap<>();
        private String queryID;
        private Object result;
        private long date;

        public QueryCache(String queryID, Object result) {
            this.queryID = queryID;
            this.result = result;
            this.date = System.currentTimeMillis();
        }

        public String getQuery() {
            return queryID;
        }

        public boolean stillAvailable() {
            if (System.currentTimeMillis() - date < cmc.SQL_STATS_INTERVAL) {
                return true;
            }
            return false;
        }

        public void remove() {
            queriesCache.remove(this.getQuery());
        }

        private static QueryCache getCache(String queryID) {
            return queriesCache.getOrDefault(queryID, null);
        }

        public Object getResult() {
            return result;
        }

        public static Object getResult(String queryID) {
            QueryCache qc = getCache(queryID);
            if (qc != null) {
                if (qc.stillAvailable()) {
                    return qc.getResult();
                } else {
                    qc.remove();
                    return null;
                }
            }
            return null;
        }

        public static void put(String queryID, Object result) {
            queriesCache.put(queryID, new QueryCache(queryID, result));
        }

    }

    public int getTopPlayerId(int rank, Map map){
        String queryID = "top_map_" + map.getName() + "_" + rank + "_usr";
        Object result = QueryCache.getResult(queryID);

        if (result == null) {
            try {
                connection = getSqlConnection();
                PreparedStatement s = connection.prepareStatement("SELECT player_id FROM player_records WHERE map_id = " + get_map_id(map) + " ORDER BY player_time ASC LIMIT " + cmc.HOLOGRAMS_RANK_LIMIT + " OFFSET " + (rank - 1));
                s.execute();
                ResultSet rs = s.getResultSet();
                if (rs.next()) {
                    result = rs.getInt("player_id");
                    QueryCache.put(queryID, result);
                }
                rs.close();
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (result != null) {
            return (int) result;
        } else {
            return -1;
        }
    }

    public String getLastGameUsername(int gameid, int rank) {
        String queryID = "ls_" + gameid + "_" + rank + "_usr";
        Object result = QueryCache.getResult(queryID);
        if (result == null) {
            String username = cmc.basicsettingnoprefix(cmc.PLACEHOLDER_NOTFOUND_PLAYER, null);
            try {
                connection = getSqlConnection();
                PreparedStatement p = connection.prepareStatement("SELECT pi.player_username as username FROM player_history as playerh INNER JOIN playerinfo as pi ON pi.player_id = playerh.player_id WHERE playerh.game_id = ? AND playerh.player_rank = ? LIMIT 1");
                p.setInt(1, gameid);
                p.setInt(2, rank);
                p.execute();
                ResultSet rs = p.getResultSet();
                if (rs.next()) {
                    username = rs.getString("username");
                }
                p.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            QueryCache.put(queryID, username);
            return username;
        } else {
            return (String) result;
        }

    }

    public void addPlayerHistory(int idgame, int playerid, int playerrank, int playerscore, long playertime) {
        try {
            connection = getSqlConnection();
            PreparedStatement p = connection.prepareStatement("INSERT INTO player_history (game_id, player_id, player_rank, player_score, player_time) VALUES (?,?,?,?,?)");
            p.setInt(1, idgame);
            p.setInt(2, playerid);
            p.setInt(3, playerrank);
            p.setInt(4, playerscore);
            p.setLong(5, playertime);
            p.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean ExistPlayer(Player player) {
        try {
            boolean exist = false;
            connection = getSqlConnection();
            PreparedStatement p = connection.prepareStatement("SELECT player_uuid FROM playerinfo WHERE player_uuid = ? ");
            p.setString(1, player.getUniqueId().toString());
            p.execute();
            ResultSet r = p.getResultSet();
            if (r.next()) {
                exist = true;
            }
            p.close();
            return exist;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean ExistPlayer(String player) {
        try {
            boolean exist = false;
            connection = getSqlConnection();
            PreparedStatement p = connection.prepareStatement("SELECT player_username FROM playerinfo WHERE player_username = ? ");
            p.setString(1, player);
            p.execute();
            ResultSet r = p.getResultSet();
            if (r.next()) {
                exist = true;
            }
            p.close();
            return exist;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean ExistPlayer_score(String player) {
        try {
            connection = getSqlConnection();
            boolean exist = false;
            int player_id = get_player_id(player);
            PreparedStatement p = connection.prepareStatement("SELECT player_id FROM player_score WHERE player_id = ? ");
            p.setInt(1, player_id);
            p.execute();
            ResultSet r = p.getResultSet();
            if (r.next()) {
                exist = true;
            }
            p.close();
            return exist;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean ExistPlayer_score(int player) {
        try {
            connection = getSqlConnection();
            boolean exist = false;
            PreparedStatement p = connection.prepareStatement("SELECT player_id FROM player_score WHERE player_id = ? ");
            p.setInt(1, player);
            p.execute();
            ResultSet r = p.getResultSet();
            if (r.next()) {
                exist = true;
            }
            p.close();
            return exist;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void UpdateScoreHolograms(Holograms h) {
        if (h == null) {
            return;
        }


        try {
            DataBase dataBase = Main.currentDataBase;
            Connection connection = dataBase.getSqlConnection();
            PreparedStatement s = connection.prepareStatement("SELECT * FROM player_score ORDER BY score_total DESC LIMIT " + cmc.HOLOGRAMS_RANK_LIMIT);
            s.execute();
            ResultSet rs = s.getResultSet();

            int line = 1;
            String title = cmc.basicsettingnoprefix(cmc.HOLOGRAMS_RANK_SCORE_TITLE, null);
            String[] titlelist = title.split("\n");
            for (String titleslines : titlelist) {
                h.setline(line, titleslines);
                line++;
            }
            line++;
            Playerinfos playerinfos;
            String msgline;
            HashMap<Integer, String> perRank = cmc.HOLOGRAMS_RANK_SCORE_SPECIFIC;
            int rank = 1;
            while (rs.next()) {
                playerinfos = Playerinfos.getPlayerInfos(rs.getInt("player_id"));
                if (perRank.containsKey(line - 2)) {
                    msgline = perRank.get(line - 2);
                } else {
                    msgline = perRank.get(-1);
                }
                if (playerinfos.score_total == 0 && !cmc.HOLOGRAMS_0_SHOW) {
                    continue;
                } else {
                    if (!cmc.HOLOGRAMS_RANK_DUPLICATION) {
                        msgline = msgline.replace("{RANK}", String.valueOf(rank)).replace("{SCORE}", String.valueOf(playerinfos.score_total)).replace("{USERNAME}", playerinfos.username);
                        h.setline(line, cmc.basicsettingnoprefix(msgline, null));
                    } else {
                        msgline = msgline.replace("{RANK}", String.valueOf(playerinfos.rank_score_total)).replace("{SCORE}", String.valueOf(playerinfos.score_total)).replace("{USERNAME}", playerinfos.username);
                        h.setline(line, cmc.basicsettingnoprefix(msgline, null));
                    }
                    line++;
                    rank++;
                }
            }
            int ranki = rank;
            if (cmc.HOLOGRAMS_FAKE_LINES && perRank.containsKey(-2)) {
                for (int i = 0; i < cmc.HOLOGRAMS_RANK_LIMIT - ranki + 1; i++) {
                    h.setline(line + i, cmc.basicsettingnoprefix(perRank.get(-2), null).replace("{RANK}", String.valueOf(rank)));
                    rank++;

                }
            }
            rs.close();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateWonRacemodeHolograms(Holograms h) {
        if (h == null) {
            return;
        }

        try {
            DataBase dataBase = Main.currentDataBase;
            Connection connection = dataBase.getSqlConnection();
            PreparedStatement s = connection.prepareStatement("SELECT player_id FROM playerinfo ORDER BY player_game_won_racemode DESC LIMIT " + cmc.HOLOGRAMS_RANK_LIMIT);
            s.execute();
            ResultSet rs = s.getResultSet();

            int line = 1;
            String title = cmc.basicsettingnoprefix(cmc.HOLOGRAMS_RANK_WON_RACEMODE_TITLE, null);
            String[] titlelist = title.split("\n");
            for (String titleslines : titlelist) {
                h.setline(line, titleslines);
                line++;
            }

            line++;
            Playerinfos playerinfos;
            String msgline;
            HashMap<Integer, String> perRank = cmc.HOLOGRAMS_RANK_WON_RACEMODE_SPECIFIC;
            int rank = 1;
            while (rs.next()) {
                playerinfos = Playerinfos.getPlayerInfos(rs.getInt("player_id"));
                if (perRank.containsKey(line - 2)) {
                    msgline = perRank.get(line - 2);
                } else {
                    msgline = perRank.get(-1);
                }
                if (playerinfos.player_game_won_racemode == 0 && !cmc.HOLOGRAMS_0_SHOW) {
                    continue;
                } else {
                    if (!cmc.HOLOGRAMS_RANK_DUPLICATION) {
                        msgline = msgline.replace("{RANK}", String.valueOf(rank)).replace("{SCORE}", String.valueOf(playerinfos.player_game_won_racemode)).replace("{USERNAME}", playerinfos.username);
                        h.setline(line, cmc.basicsettingnoprefix(msgline, null));
                    } else {
                        msgline = msgline.replace("{RANK}", String.valueOf(playerinfos.rank_won_racemode)).replace("{SCORE}", String.valueOf(playerinfos.player_game_won_racemode)).replace("{USERNAME}", playerinfos.username);
                        h.setline(line, cmc.basicsettingnoprefix(msgline, null));
                    }

                    line++;
                    rank++;
                }
            }
            int ranki = rank;
            if (cmc.HOLOGRAMS_FAKE_LINES && perRank.containsKey(-2)) {
                for (int i = 0; i < cmc.HOLOGRAMS_RANK_LIMIT - ranki + 1; i++) {
                    h.setline(line + i, cmc.basicsettingnoprefix(perRank.get(-2), null).replace("{RANK}", String.valueOf(rank)));
                    rank++;

                }
            }
            rs.close();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateWonGrandprixHolograms(Holograms h) {
        if (h == null) {
            return;
        }


        try {
            DataBase dataBase = Main.currentDataBase;
            Connection connection = dataBase.getSqlConnection();
            PreparedStatement s = connection.prepareStatement("SELECT player_id FROM playerinfo ORDER BY player_game_won_grandprix DESC LIMIT " + cmc.HOLOGRAMS_RANK_LIMIT);
            s.execute();
            ResultSet rs = s.getResultSet();

            int line = 1;
            String title = cmc.basicsettingnoprefix(cmc.HOLOGRAMS_RANK_WON_GRANDPRIX_TITLE, null);
            String[] titlelist = title.split("\n");
            for (String titleslines : titlelist) {
                h.setline(line, titleslines);
                line++;
            }
            line++;
            Playerinfos playerinfos;
            String msgline;
            HashMap<Integer, String> perRank = cmc.HOLOGRAMS_RANK_WON_GRANDPRIX_SPECIFIC;
            int rank = 1;
            while (rs.next()) {
                playerinfos = Playerinfos.getPlayerInfos(rs.getInt("player_id"));
                if (perRank.containsKey(line - 2)) {
                    msgline = perRank.get(line - 2);
                } else {
                    msgline = perRank.get(-1);
                }
                if (playerinfos.player_game_won_grandprix == 0 && !cmc.HOLOGRAMS_0_SHOW) {
                    continue;
                } else {
                    if (!cmc.HOLOGRAMS_RANK_DUPLICATION) {
                        msgline = msgline.replace("{RANK}", String.valueOf(rank)).replace("{SCORE}", String.valueOf(playerinfos.player_game_won_grandprix)).replace("{USERNAME}", playerinfos.username);
                        h.setline(line, cmc.basicsettingnoprefix(msgline, null));
                    } else {
                        msgline = msgline.replace("{RANK}", String.valueOf(playerinfos.rank_won_grandprix)).replace("{SCORE}", String.valueOf(playerinfos.player_game_won_grandprix)).replace("{USERNAME}", playerinfos.username);
                        h.setline(line, cmc.basicsettingnoprefix(msgline, null));
                    }


                    line++;
                    rank++;
                }

            }
            int ranki = rank;
            if (cmc.HOLOGRAMS_FAKE_LINES && perRank.containsKey(-2)) {
                for (int i = 0; i < cmc.HOLOGRAMS_RANK_LIMIT - ranki + 1; i++) {
                    h.setline(line + i, cmc.basicsettingnoprefix(perRank.get(-2), null).replace("{RANK}", String.valueOf(rank)));
                    rank++;
                }
            }
            rs.close();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateMapRankHolograms(Holograms h) {
        if (h == null) {
            return;
        }
        Map m = h.map;
        if (m == null) {
            return;
        }
        try {
            DataBase dataBase = Main.currentDataBase;
            Connection connection = dataBase.getSqlConnection();
            PreparedStatement s = connection.prepareStatement("SELECT player_id FROM player_records WHERE map_id = " + dataBase.get_map_id(m) + " ORDER BY player_time ASC LIMIT " + cmc.HOLOGRAMS_RANK_LIMIT);
            s.execute();
            ResultSet rs = s.getResultSet();

            int line = 1;
            String title = cmc.basicsettingnoprefix(cmc.HOLOGRAMS_RANK_MAP_TITLE.replace("{MAP}", m.getName()), null);
            String[] titlelist = title.split("\n");
            for (String titleslines : titlelist) {
                h.setline(line, titleslines);
                line++;
            }
            line++;
            Playerinfos playerinfos;
            PlayerMapinfos mapinfos;
            String msgline;
            HashMap<Integer, String> perRank = cmc.HOLOGRAMS_RANK_MAP_SPECIFIC;
            int rank = 1;
            while (rs.next()) {
                playerinfos = Playerinfos.getPlayerInfos(rs.getInt("player_id"));
                if (perRank.containsKey(line - 2)) {
                    msgline = perRank.get(line - 2);
                } else {
                    msgline = perRank.get(-1);
                }
                mapinfos = playerinfos.getMap(h.map.uuid);
                if (mapinfos == null) {
                    return;
                }
                if (mapinfos.player_time == 0 && !cmc.HOLOGRAMS_0_SHOW) {
                    continue;
                } else {
                    if (!cmc.HOLOGRAMS_RANK_DUPLICATION) {
                        msgline = msgline.replace("{RANK}", String.valueOf(rank)).replace("{SCORE}", String.valueOf(Utils.timediffToString(mapinfos.player_time))).replace("{USERNAME}", playerinfos.username);
                        h.setline(line, cmc.basicsettingnoprefix(msgline, null));
                    } else {
                        msgline = msgline.replace("{RANK}", String.valueOf(mapinfos.ranking)).replace("{SCORE}", String.valueOf(Utils.timediffToString(mapinfos.player_time))).replace("{USERNAME}", playerinfos.username);
                        h.setline(line, cmc.basicsettingnoprefix(msgline, null));
                    }
                    line++;
                    rank++;
                }
            }
            int ranki = rank;
            if (cmc.HOLOGRAMS_FAKE_LINES && perRank.containsKey(-2)) {
                for (int i = 0; i < cmc.HOLOGRAMS_RANK_LIMIT - ranki + 1; i++) {
                    h.setline(line + i, cmc.basicsettingnoprefix(perRank.get(-2), null).replace("{RANK}", String.valueOf(rank)));
                    rank++;
                }
            }
            rs.close();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void Deleteinformations_Map(int map_id) {
        Connection connection = Main.currentDataBase.getSqlConnection();
        try {

            Statement s = connection.createStatement();
            s.addBatch("DELETE FROM player_map_stats WHERE map_id = " + map_id);
            s.addBatch("DELETE FROM player_records WHERE map_id = " + map_id);
            s.executeBatch();
            s.clearBatch();
            s.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static HashMap<Integer, Playerinfos> playerinfoscache = new HashMap<>();

    public static class PlayerMapinfos {
        public String map_uuid;
        public int winrate;
        public int win;
        public long player_time;
        public long ranking;

        PlayerMapinfos(String map_uuid, int winrate, long player_time, long ranking, int win) {
            this.map_uuid = map_uuid;
            this.winrate = winrate;
            this.player_time = player_time;
            this.ranking = ranking;
            this.win = win;
        }
    }

    public static enum Challenges {
        MAXGAME("maxgame");

        private String s;

        Challenges(String s) {
            this.s = s;
        }
    }

    public static boolean ExistPlayerChallenge(int player_id) {
        try {
            boolean exist = false;
            PreparedStatement p = Main.currentDataBase.getSqlConnection().prepareStatement("SELECT player_id FROM player_challenge WHERE player_id = ? ");
            p.setInt(1, player_id);
            p.execute();
            ResultSet r = p.getResultSet();
            if (r.next()) {
                exist = true;
            }
            p.close();
            return exist;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addPlayerChallenge(int player_id) {
        try {
            PreparedStatement p = Main.currentDataBase.getSqlConnection().prepareStatement("INSERT INTO player_challenge (player_id) VALUES (?)");
            p.setInt(1, player_id);
            p.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void completeChallenge(Challenges challenges, String player) {
        int player_id = Main.currentDataBase.get_player_id(player);
        if (!ExistPlayerChallenge(player_id)) {
            addPlayerChallenge(player_id);
        }
        try {
            PreparedStatement p = Main.currentDataBase.getSqlConnection().prepareStatement("UPDATE player_challenge SET player_challenge_" + challenges.s + " = 1 WHERE player_id = " + player_id + ";");
            p.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean madeChallenge(Challenges challenge, String player) {
        int player_id = Main.currentDataBase.get_player_id(player);
        if (!ExistPlayerChallenge(player_id)) {
            addPlayerChallenge(player_id);
            return false;
        }
        try {
            PreparedStatement p = Main.currentDataBase.getSqlConnection().prepareStatement("SELECT player_challenge_" + challenge.s + " FROM player_challenge WHERE player_id = ?");
            p.setInt(1, player_id);
            p.execute();
            ResultSet r = p.getResultSet();
            int challengeresult = 0;
            if (r.next()) {
                challengeresult = r.getInt("player_challenge_" + challenge.s);
            }
            p.close();
            if (challengeresult == 1) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getMapFinished(String player) {
        int player_id = Main.currentDataBase.get_player_id(player);
        Playerinfos.askUpdate(player_id);
        Playerinfos playerinfos = Playerinfos.getPlayerInfos(player_id);
        if (playerinfos == null) {
            return 0;
        }
        return playerinfos.player_map_finished;
    }

    public static class Playerinfos {

        public int player_game_played_racemode, player_game_played_grandprix, player_game_won_racemode, player_game_won_grandprix, player_map_played, player_map_finished;
        public long made;
        public int rank_won_grandprix, rank_won_racemode, rank_score_total;
        public int score_total;
        public String uuid;
        public String username;
        public ArrayList<PlayerMapinfos> playerMapinfos = new ArrayList<>();

        public PlayerMapinfos getMap(String MapUUID) {
            for (PlayerMapinfos pmi : playerMapinfos) {
                if (pmi.map_uuid.equals(MapUUID)) {
                    return pmi;
                }
            }
            return null;
        }

        public void delete() {
            try {
                this.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        public static void askUpdate(int player_id) {
            if (playerinfoscache.containsKey(player_id)) {
                playerinfoscache.get(player_id).delete();
                playerinfoscache.remove(player_id);
            }

        }

        public static Playerinfos getPlayerInfos(int player_id) {
            if (playerinfoscache.containsKey(player_id)) {
                if (System.currentTimeMillis() - playerinfoscache.get(player_id).made < cmc.SQL_STATS_INTERVAL) {
                    return playerinfoscache.get(player_id);
                }
            }
            return new Playerinfos(player_id);
        }

        private long getRanking(int player_id, int map_id) {
            Connection connec = Main.currentDataBase.getSqlConnection();
            PreparedStatement rank = null;
            try {
                rank = connec.prepareStatement("SELECT count(*) + 1 as rank FROM (SELECT player_time, player_id FROM player_records WHERE map_id = " + map_id + ") as alias1 WHERE player_time < (SELECT player_time FROM (SELECT player_time, player_id FROM player_records WHERE map_id = " + map_id + " AND player_id = " + player_id + ")as alias2)");
                rank.execute();
                ResultSet r = rank.getResultSet();
                long rankk = -1;
                if (r.next()) {
                    rankk = r.getLong("rank");
                }
                rank.close();
                return rankk;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1;

        }

        public Playerinfos(int player_id) {
            Connection connec = Main.currentDataBase.getSqlConnection();
            try {
                PreparedStatement p = connec.prepareStatement("SELECT player_uuid,player_game_played_racemode,player_game_played_grandprix,player_game_won_racemode,player_game_won_grandprix,player_map_played,player_map_finished FROM playerinfo WHERE player_id = ?");
                p.setInt(1, player_id);
                p.execute();
                ResultSet r = p.getResultSet();
                if (r.next()) {
                    this.uuid = r.getString("player_uuid");
                    this.player_game_played_racemode = r.getInt("player_game_played_racemode");
                    this.player_game_played_grandprix = r.getInt("player_game_played_grandprix");
                    this.player_game_won_racemode = r.getInt("player_game_won_racemode");
                    this.player_game_won_grandprix = r.getInt("player_game_won_grandprix");
                    this.player_map_played = r.getInt("player_map_played");
                    this.player_map_finished = r.getInt("player_map_finished");

                }
                p.close();

                PreparedStatement rank = connec.prepareStatement("SELECT count(*) + 1 AS rank FROM playerinfo WHERE player_game_won_grandprix > (SELECT player_game_won_grandprix FROM playerinfo WHERE player_id = " + player_id + ");");


                rank.execute();
                r = rank.getResultSet();
                if (r.next()) {
                    this.rank_won_grandprix = r.getInt("rank");
                }

                PreparedStatement user = connec.prepareStatement("SELECT player_username FROM playerinfo WHERE player_id  = " + player_id);
                user.execute();
                r = user.getResultSet();
                if (r.next()) {
                    this.username = r.getString("player_username");
                }

                rank = connec.prepareStatement("SELECT count(*) + 1 AS rank FROM playerinfo WHERE player_game_won_racemode > (SELECT player_game_won_racemode FROM playerinfo WHERE player_id = " + player_id + ");");
                rank.execute();
                r = rank.getResultSet();
                if (r.next()) {
                    this.rank_won_racemode = r.getInt("rank");
                }
                rank.close();

                rank = connec.prepareStatement("SELECT count(*) + 1 AS rank FROM player_score WHERE score_total > (SELECT score_total FROM player_score WHERE player_id = " + player_id + ");");
                rank.execute();
                r = rank.getResultSet();
                if (r.next()) {
                    this.rank_score_total = r.getInt("rank");
                }
                rank.close();


                PreparedStatement score = connec.prepareStatement("SELECT score_total FROM player_score WHERE player_id = " + player_id + ";");
                score.execute();
                r = score.getResultSet();
                if (r.next()) {
                    this.score_total = r.getInt("score_total");
                }
                score.close();

                p = connec.prepareStatement("SELECT map_uuid, winrate, player_time, player_time_won, player_records.map_id as mapid \n" +
                        "FROM mapinfo \n" +
                        "INNER JOIN (SELECT (player_time_won + 0.0)/player_time_played as winrate,map_id, player_time_won FROM player_map_stats WHERE player_id = " + player_id + ") AS playerwinrate \n" +
                        "INNER JOIN player_records \n" +
                        "ON playerwinrate.map_id = mapinfo.map_id AND playerwinrate.map_id = player_records.map_id AND player_id = " + player_id + " \n" +
                        "ORDER BY winrate DESC LIMIT 15");
                p.execute();
                r = p.getResultSet();
                while (r.next()) {
                    this.playerMapinfos.add(new PlayerMapinfos(r.getString("map_uuid"), Math.round(r.getFloat("winrate") * 100), r.getLong("player_time"), getRanking(player_id, r.getInt("mapid")), r.getInt("player_time_won")));
                }
                p.close();
                this.made = System.currentTimeMillis();
                if (playerinfoscache.containsKey(player_id) && playerinfoscache.get(player_id) != null) {
                    playerinfoscache.get(player_id).finalize();
                }
                playerinfoscache.put(player_id, this);


            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void AddScore(ArrayList<Scoring> ranks) {
        connection = getSqlConnection();

        try {
            Statement s = connection.createStatement();
            for (Scoring rank : ranks) {
                int player_id = get_player_id(rank.player);
                if (!ExistPlayer_score(player_id)) {
                    s.addBatch("INSERT INTO player_score (player_id, score_total) VALUES (" + player_id + ", " + rank.value + ");");
                } else {
                    if (rank.value == 0) {
                        break;
                    }
                    s.addBatch("UPDATE player_score SET score_total = " + (getScore_total(player_id) + rank.value) + " WHERE player_id = " + player_id + ";");
                }
            }
            s.executeBatch();
            s.clearBatch();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int getScore_total(int player_id) {

        try {
            connection = getSqlConnection();
            PreparedStatement p = connection.prepareStatement("SELECT score_total FROM player_score WHERE player_id = ?");
            p.setInt(1, player_id);
            p.execute();
            int i = 0;
            ResultSet r = p.getResultSet();
            if (r.next()) {
                i = r.getInt("score_total");
            }
            p.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void SavePlayer(Player player) {
        connection = getSqlConnection();
        PreparedStatement ps;
        if (ExistPlayer(player)) {
            if (Bukkit.getServer().getOnlineMode()) {
                try {
                    ps = connection.prepareStatement("UPDATE playerinfo SET player_username = ? WHERE player_uuid = ?");
                    ps.setString(1, player.getName());
                    ps.setString(2, player.getUniqueId().toString());
                    ps.executeUpdate();
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        try {
            ps = connection.prepareStatement("INSERT INTO playerinfo (player_uuid, player_username) VALUES (?, ?);");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private boolean ExistMap(Map map) {
        connection = getSqlConnection();
        try {
            boolean exist = false;
            PreparedStatement p = connection.prepareStatement("SELECT map_uuid FROM mapinfo WHERE map_uuid = ? ");
            p.setString(1, map.getUUID());
            p.execute();
            ResultSet r = p.getResultSet();
            if (r.next()) {
                exist = true;
            }
            p.close();
            return exist;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void SaveMap(Map map) {
        if (ExistMap(map)) {
            return;
        }
        connection = getSqlConnection();

        try {
            PreparedStatement p = connection.prepareStatement("INSERT INTO mapinfo (map_uuid, map_name) VALUES (?, ?);");
            p.setString(1, map.getUUID());
            p.setString(2, map.getName());
            p.executeUpdate();
            p.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int get_player_id(Player player) {
        connection = getSqlConnection();

        try {
            PreparedStatement p = connection.prepareStatement("SELECT player_id FROM playerinfo WHERE player_uuid = ?");
            p.setString(1, player.getUniqueId().toString());
            p.execute();
            int i = -1;
            ResultSet r = p.getResultSet();
            if (r.next()) {
                i = r.getInt("player_id");
            }
            p.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int get_player_id(String player) {
        connection = getSqlConnection();

        try {
            PreparedStatement p = connection.prepareStatement("SELECT player_id FROM playerinfo WHERE player_username LIKE ?");
            p.setString(1, player);
            p.execute();
            int i = -1;
            ResultSet r = p.getResultSet();
            if (r.next()) {
                i = r.getInt("player_id");
            }
            p.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int get_map_id(Map map) {
        if (map == null) {
            return -1;
        }
        connection = getSqlConnection();

        try {
            PreparedStatement p = connection.prepareStatement("SELECT map_id FROM mapinfo WHERE map_uuid = ?");
            p.setString(1, map.getUUID());
            p.execute();
            int i = -1;
            ResultSet r = p.getResultSet();
            if (r.next()) {
                i = r.getInt("map_id");
            }
            p.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int player_time(int player_id, int map_id) {
        connection = getSqlConnection();

        try {
            PreparedStatement p = connection.prepareStatement("SELECT player_time FROM player_records WHERE player_id = ? AND map_id = ?");
            p.setInt(1, player_id);
            p.setInt(2, map_id);
            p.execute();
            int i = -1;
            ResultSet r = p.getResultSet();
            if (r.next()) {
                i = r.getInt("player_time");
            }
            p.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getValue_playerinfo(Player player, String value) {
        connection = getSqlConnection();

        try {
            PreparedStatement p = connection.prepareStatement("SELECT " + value + " FROM playerinfo WHERE player_uuid = ?");
            p.setString(1, player.getUniqueId().toString());
            p.execute();
            int i = -1;
            ResultSet r = p.getResultSet();
            if (r.next()) {
                i = r.getInt(value);
            }
            p.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getValue_player_map_info(int player_id, int map_id, String value) {
        connection = getSqlConnection();

        try {
            PreparedStatement p = connection.prepareStatement("SELECT " + value + " FROM player_map_stats WHERE map_id = ? AND player_id = ?");
            p.setInt(1, map_id);
            p.setInt(2, player_id);
            p.execute();
            int i = -1;
            ResultSet r = p.getResultSet();
            if (r.next()) {
                i = r.getInt(value);
            }
            p.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void IncrementValue_playerinfo(Player player, String value, int incrementvalue) {
        connection = getSqlConnection();
        int current_value = getValue_playerinfo(player, value);
        if (current_value == -1) {
            return;
        }
        try {

            PreparedStatement ps = connection.prepareStatement("UPDATE playerinfo SET " + value + " = ? WHERE player_uuid = ?");

            ps.setInt(1, current_value + incrementvalue);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void IncrementValue_player_map_stats(Player player, Map map, String value, int incrementvalue) {
        connection = getSqlConnection();
        int player_id = get_player_id(player);
        int map_id = get_map_id(map);
        if (map_id == -1 || player_id == -1) {
            return;
        }

        int current_value = getValue_player_map_info(player_id, map_id, value);
        if (current_value == -1) {
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO player_map_stats (player_id, map_id, " + value + ") VALUES (?,?,?)");
                ps.setInt(1, player_id);
                ps.setInt(2, map_id);
                ps.setInt(3, incrementvalue);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        try {

            PreparedStatement ps = connection.prepareStatement("UPDATE player_map_stats SET " + value + " = ? WHERE player_id = ? AND map_id = ?");

            ps.setInt(1, current_value + incrementvalue);
            ps.setInt(2, player_id);
            ps.setInt(3, map_id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean SaveTime(Player player, Map map, long time, Game g) {
        connection = getSqlConnection();

        try {
            int player_id = get_player_id(player);
            int map_id = get_map_id(map);
            if (map_id == -1 || player_id == -1) {
                return false;
            }
            long record = -1;
            record = player_time(player_id, map_id);
            if (record == -1) {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO player_records (map_id, player_id, player_time) VALUES (?,?,?)");
                ps.setInt(1, map_id);
                ps.setInt(2, player_id);
                ps.setLong(3, time);
                ps.executeUpdate();
                ps.close();

                return false;
            } else if (record > time) {
                PreparedStatement ps = connection.prepareStatement("UPDATE player_records SET player_time = ? WHERE player_id = ? AND map_id = ?");
                ps.setLong(1, time);
                ps.setInt(2, player_id);
                ps.setInt(3, map_id);
                ps.executeUpdate();
                ps.close();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clearDB(DBTypeClear dbTypeClear) throws SQLException {
        connection = getSqlConnection();
        PreparedStatement ps = connection.prepareStatement("UPDATE " + dbTypeClear.getTable() + " SET " + dbTypeClear.getColumn() + " = " + dbTypeClear.getValue());
        ps.executeUpdate();
        ps.close();
    }

    public enum DBTypeClear {
        SCORE("player_score", "score_total", "0");

        private String s;
        private String value;
        private String table;

        DBTypeClear(String table, String s, String value) {
            this.s = s;
            this.value = value;
            this.table = table;
        }

        public String getTable() {
            return table;
        }

        public String getColumn() {
            return s;
        }

        public String getValue() {
            return value;
        }
    }
}
