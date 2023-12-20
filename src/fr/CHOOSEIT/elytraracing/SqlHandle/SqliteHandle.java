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
import fr.CHOOSEIT.elytraracing.filesystem.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteHandle extends DataBase
{


    public SqliteHandle(CustomMessageConfig cmc)
    {
        super(cmc);
        DataBase.cmc = cmc;

        getSqlConnection();

    }
    private void initdb(Connection connection)
    {
        String table = "CREATE TABLE IF NOT EXISTS playerinfo ( 'player_id' INTEGER DEFAULT 0 UNIQUE," +
                "'player_uuid' TEXT NOT NULL UNIQUE," +
                "'player_username' TEXT NOT NULL," +
                "'player_game_played_racemode' INTEGER DEFAULT 0," +
                "'player_game_played_grandprix' INTEGER DEFAULT 0," +
                "'player_game_won_racemode' INTEGER DEFAULT 0," +
                "'player_game_won_grandprix' INTEGER DEFAULT 0," +
                "'player_map_played' INTEGER DEFAULT 0," +
                "'player_map_finished' INTEGER DEFAULT 0," +
                "PRIMARY KEY('player_id' AUTOINCREMENT));";
        try
        {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        table = "CREATE TABLE IF NOT EXISTS mapinfo ( 'map_id' INTEGER DEFAULT 0 UNIQUE, 'map_uuid' TEXT NOT NULL UNIQUE, 'map_name' TEXT NOT NULL, PRIMARY KEY('map_id' AUTOINCREMENT));";
        try
        {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        table = "CREATE TABLE IF NOT EXISTS player_records ('map_id' INTEGER NOT NULL, 'player_id' INTEGER NOT NULL, 'player_time' INTEGER NOT NULL);";
        try
        {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        table = "CREATE TABLE IF NOT EXISTS player_map_stats ('player_id' INTEGER NOT NULL, " +
                "'map_id' INTEGER NOT NULL, " +
                "'player_time_played' INTEGER DEFAULT 0, " +
                "'player_time_finished' INTEGER DEFAULT 0, " +
                "'player_time_won' INTEGER DEFAULT 0" +
                ");";
        try
        {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        table = "CREATE TABLE IF NOT EXISTS player_score ('player_id' INTEGER NOT NULL, " +
                "'score_total' INTEGER DEFAULT 0 " +
                ");";
        try
        {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        table = "CREATE TABLE IF NOT EXISTS game_history ( " +
                "game_id INTEGER NOT NULL," +
                "game_type TEXT NOT NULL," +
                "game_date INTEGER NOT NULL," +
                "game_player_number INTEGER NOT NULL)";
        try
        {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        table = "CREATE TABLE IF NOT EXISTS player_history ( " +
                "game_id INTEGER NOT NULL," +
                "player_id INTEGER NOT NULL," +
                "player_rank INTEGER NOT NULL," +
                "player_score INTEGER NOT NULL," +
                "player_time INTEGER NOT NULL)";
        try
        {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        table = "CREATE TABLE IF NOT EXISTS player_challenge ( " +
                "player_id INTEGER NOT NULL," +
                "player_challenge_maxgame INTEGER DEFAULT 0)";
        try
        {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static String getSqlPath()
    {
        if(cmc.SQL_LOCATION.startsWith("$$"))
        {
            return cmc.SQL_LOCATION.substring(2,cmc.SQL_LOCATION.length());
        }
        return Main.instance.getDataFolder() + cmc.SQL_LOCATION;
    }
    public Connection getSqlConnection()
    {
        if(!cmc.SQL_USE_CUSTOM_SQL)
        {
            File dataFolder = new File(getSqlPath(), dbname+".db");
            if(!dataFolder.exists())
            {
                try {
                    FileUtils.createFile(dataFolder);
                    dataFolder.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                if(connection != null && !connection.isClosed())
                {
                    return connection;
                }
                connection = DriverManager.getConnection("jdbc:sqlite:"+dataFolder);
                initdb(connection);
                return connection;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
