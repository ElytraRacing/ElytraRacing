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

package fr.CHOOSEIT.elytraracing.api.placeholderapi;

import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.SqlHandle.DataBase;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import javax.xml.crypto.Data;

public class PlaceholderAPIExpansions extends PlaceholderExpansion
{
    DataBase db;
    private Main plugin;

    public PlaceholderAPIExpansions(Main plugin, DataBase db)
    {
        this.plugin = plugin;
        this.db = db;
    }


    @Override
    public boolean canRegister()
    {
        return true;
    }
    @Override
    public boolean persist()
    {
        return true;
    }
    @Override
    public String getIdentifier() {
        return "elytraracing";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params)
    {
        String[] args = params.split("_");
        if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("playing"))
            {
                int n = 0;
                for (Game game : Game.gamelist) {
                    for (Player player1 : game.getPlayerList()) {
                        if(player1 != null && player1.isOnline())
                        {
                            n++;
                        }
                    }
                }
                return String.valueOf(n);
            }
        }
        else if(args.length >= 2)
        {
            int player_id = -1;
            if(args[0].equals("self"))
            {
                if(player == null)
                {
                    return "";
                }
                player_id = db.get_player_id(player.getName());
            }
            else
            {
                player_id = db.get_player_id(args[0]);
            }
            // {<player>/self_<map>/favorite_arg..>
            if(player_id >= 0)
            {

                DataBase.Playerinfos playerinfos = DataBase.Playerinfos.getPlayerInfos(player_id);
                if(playerinfos != null)
                {
                    if(args[1].equals("playedracemode"))
                    {
                        return String.valueOf(playerinfos.player_game_played_racemode);
                    }
                    else if(args[1].equals("playedgrandprix"))
                    {
                        return String.valueOf(playerinfos.player_game_played_grandprix);
                    }
                    else if(args[1].equals("wonracemode"))
                    {
                        return String.valueOf(playerinfos.player_game_won_racemode);
                    }
                    else if(args[1].equals("wongrandprix"))
                    {
                        return String.valueOf(playerinfos.player_game_won_grandprix);
                    }
                    else if(args[1].equals("mapplayed"))
                    {
                        return String.valueOf(playerinfos.player_map_played);
                    }
                    else if(args[1].equals("mapfinished"))
                    {
                        return String.valueOf(playerinfos.player_map_finished);
                    }
                    else if(args[1].equals("score"))
                    {
                        return String.valueOf(playerinfos.score_total);
                    }
                    else if(args[1].equals("rank") && args.length >= 3)
                    {
                        if(args[2].equals("playedracemode"))
                        {
                            return String.valueOf(playerinfos.rank_won_racemode);
                        }
                        else if(args[2].equals("playedgrandprix"))
                        {
                            return String.valueOf(playerinfos.rank_won_grandprix);
                        }
                        else if(args[2].equals("score"))
                        {
                            return String.valueOf(playerinfos.rank_score_total);
                        }
                        return "";
                    }
                    else
                    {
                        DataBase.PlayerMapinfos playerMapinfos = null;
                        Map m = Map.Find(args[1]);
                        if(args[1].equals("favorite"))
                        {
                            playerMapinfos = playerinfos.playerMapinfos.get(0);
                        }
                        else if(m != null)
                        {
                            playerMapinfos = playerinfos.getMap(m.uuid);
                        }
                        if(playerMapinfos != null && args.length >= 3)
                        {
                            if(args[2].equals("winrate"))
                            {
                                return String.valueOf(playerMapinfos.winrate);
                            }
                            else if(args[2].equals("ranking"))
                            {
                                return String.valueOf(playerMapinfos.ranking);
                            }
                            else if(args[2].equals("playertime"))
                            {
                                return Utils.timediffToString(playerMapinfos.player_time);
                            }
                            else if(args[2].equals("name"))
                            {
                                return Map.getMap(playerMapinfos.map_uuid).getName();
                            }
                            else if(args[2].equals("won"))
                            {
                                return String.valueOf(playerMapinfos.win);
                            }
                        }
                        else
                        {
                            if(args[2].equals("winrate")|| args[2].equals("won") || args[2].equals("playertime"))
                            {
                                return Main.cmc().PLACEHOLDER_DEFAULT_NUMBER;
                            }
                            else if(args[2].equals("ranking"))
                            {
                                return Main.cmc().PLACEHOLDER_DEFAULT_RANKING;
                            }
                            else if(args[2].equals("name"))
                            {
                                return m.getName();
                            }
                        }
                    }
                }
                else
                {
                    if(args[1].equals("playedracemode") ||
                            args[1].equals("playedgrandprix") ||
                            args[1].equals("wonracemode") ||
                            args[1].equals("wongrandprix") ||
                            args[1].equals("mapplayed")||
                            args[1].equals("mapfinished") ||
                            args[1].equals("score"))
                    {
                        return Main.cmc().PLACEHOLDER_DEFAULT_NUMBER;
                    }
                    else if(args[1].equals("rank"))
                    {
                        if(args[2].equals("playedracemode") || args[2].equals("playedgrandprix") || args[2].equals("score"))
                        {
                            return Main.cmc().PLACEHOLDER_DEFAULT_NUMBER;
                        }
                        return "";
                    }
                    if(args.length >= 3) {
                        if (args[2].equals("winrate") || args[2].equals("won") || args[2].equals("playertime")) {
                            return Main.cmc().PLACEHOLDER_DEFAULT_NUMBER;
                        } else if (args[2].equals("ranking")) {
                            return Main.cmc().PLACEHOLDER_DEFAULT_RANKING;
                        } else if (args[2].equals("name")) {
                            return Main.cmc().PLACEHOLDER_DEFAULT_TEXT;
                        }
                    }
                }

            }
            else
            {
                if(args[1].equals("playedracemode") ||
                        args[1].equals("playedgrandprix") ||
                        args[1].equals("wonracemode") ||
                        args[1].equals("wongrandprix") ||
                        args[1].equals("mapplayed")||
                        args[1].equals("mapfinished") ||
                        args[1].equals("score"))
                {
                    return Main.cmc().PLACEHOLDER_DEFAULT_NUMBER;
                }
                else if(args[1].equals("rank"))
                {
                    if(args[2].equals("playedracemode") || args[2].equals("playedgrandprix") || args[2].equals("score"))
                    {
                        return Main.cmc().PLACEHOLDER_DEFAULT_NUMBER;
                    }
                    return "";
                }
                if(args.length >= 3) {
                    if (args[2].equals("winrate") || args[2].equals("won") || args[2].equals("playertime")) {
                        return Main.cmc().PLACEHOLDER_DEFAULT_NUMBER;
                    } else if (args[2].equals("ranking")) {
                        return Main.cmc().PLACEHOLDER_DEFAULT_RANKING;
                    } else if (args[2].equals("name")) {
                        return Main.cmc().PLACEHOLDER_DEFAULT_TEXT;
                    }
                }

                if(args[0].equals("lastgame"))
                {
                    if(Utils.IsInteger(args[1]))
                    {
                        int rank = Integer.parseInt(args[1]);
                        if(args[2].equals("username"))
                        {
                            return db.getLastGameUsername(db.sizeGameHistory(), rank);
                        }
                        return "";
                    }
                    return Main.cmc().PLACEHOLDER_DEFAULT_TEXT;
                } else if(args[0].equals("top")) {
                    if (args[1].equals("map")){
                        // .._top_map_<map>_..
                        Map m = Map.Find(args[2]);
                        if(m != null) {
                            if (Utils.IsInteger(args[3])) {
                                int rank = Integer.parseInt(args[3]);
                                //.._<rank>_..
                                if (args[4].equals("username")) {

                                    int id = Main.currentDataBase.getTopPlayerId(rank, m);
                                    if (id != -1){
                                        DataBase.Playerinfos playerinfos = DataBase.Playerinfos.getPlayerInfos(id);
                                        return playerinfos.username;
                                    } else {
                                        return Main.cmc().PLACEHOLDER_NOTFOUND_PLAYER;
                                    }

                                } else if (args[4].equals("time")){

                                    int id = Main.currentDataBase.getTopPlayerId(rank, m);
                                    if (id != -1){
                                        DataBase.Playerinfos playerinfos = DataBase.Playerinfos.getPlayerInfos(id);
                                        DataBase.PlayerMapinfos mapInfos = playerinfos.getMap(m.uuid);
                                        if (mapInfos != null) {
                                            return Utils.timediffToString(mapInfos.player_time);
                                        }
                                    }
                                    return Utils.timediffToString(0L);

                                } else if (args[4].equals("win")){

                                    int id = Main.currentDataBase.getTopPlayerId(rank, m);
                                    if (id != -1){
                                        DataBase.Playerinfos playerinfos = DataBase.Playerinfos.getPlayerInfos(id);
                                        DataBase.PlayerMapinfos mapInfos = playerinfos.getMap(m.uuid);
                                        if (mapInfos != null){
                                            return String.valueOf(mapInfos.win);
                                        }
                                    }
                                    return Main.cmc().PLACEHOLDER_DEFAULT_NUMBER;
                                } else if (args[4].equals("winrate")){

                                    int id = Main.currentDataBase.getTopPlayerId(rank, m);
                                    if (id != -1){
                                        DataBase.Playerinfos playerinfos = DataBase.Playerinfos.getPlayerInfos(id);
                                        DataBase.PlayerMapinfos mapInfos = playerinfos.getMap(m.uuid);
                                        if (mapInfos != null){
                                            return String.valueOf(mapInfos.winrate);
                                        }
                                    }
                                    return Main.cmc().PLACEHOLDER_DEFAULT_NUMBER;
                                }
                                return "";
                            }
                        }
                    }

                    return Main.cmc().PLACEHOLDER_DEFAULT_TEXT;
                }
            }

        }
        return "";
    }
}
