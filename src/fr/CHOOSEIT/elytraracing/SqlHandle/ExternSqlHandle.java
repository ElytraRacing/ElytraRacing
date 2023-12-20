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

public class ExternSqlHandle extends DataBase {


    public ExternSqlHandle(CustomMessageConfig cmc) {
        super(cmc);
        DataBase.cmc = cmc;

        getSqlConnection();
    }

    @Override
    public Connection getSqlConnection() {
        if (cmc.SQL_USE_CUSTOM_SQL) {
            try {
                if (connection != null && !connection.isClosed()) {
                    return connection;
                }
                connection = DriverManager.getConnection("jdbc:" + cmc.SQL_CUSTOM_SQL_TYPE + "://" + cmc.SQL_CUSTOM_SQL_HOST + ":" + cmc.SQL_CUSTOM_SQL_PORT + "/" + cmc.SQL_CUSTOM_SQL_DATABASE, cmc.SQL_CUSTOM_SQL_USER, cmc.SQL_CUSTOM_SQL_PASSWORD);
                initdb(connection);
                return connection;
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
        return null;
    }

    private void initdb(Connection connection) {
        String db = "CREATE DATABASE IF NOT EXISTS " + cmc.SQL_CUSTOM_SQL_DATABASE + " CHARACTER SET latin1 COLLATE latin1_swedish_ci";
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(db);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String table = "CREATE TABLE IF NOT EXISTS playerinfo ( player_id INTEGER(22) UNIQUE AUTO_INCREMENT," +
                "player_uuid VARCHAR(50) NOT NULL UNIQUE," +
                "player_username VARCHAR(32) NOT NULL," +
                "player_game_played_racemode INTEGER(22) DEFAULT 0," +
                "player_game_played_grandprix INTEGER(22) DEFAULT 0," +
                "player_game_won_racemode int(22) DEFAULT 0," +
                "player_game_won_grandprix int(22) DEFAULT 0," +
                "player_map_played int(22) DEFAULT 0," +
                "player_map_finished int(22) DEFAULT 0," +
                "PRIMARY KEY(player_id));";
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table = "CREATE TABLE IF NOT EXISTS mapinfo ( map_id int(22) UNIQUE AUTO_INCREMENT, map_uuid varchar(50) NOT NULL UNIQUE, map_name varchar(100) NOT NULL, PRIMARY KEY(map_id));";
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table = "CREATE TABLE IF NOT EXISTS player_records (map_id int(22) NOT NULL, player_id int(22) NOT NULL, player_time int(22) NOT NULL);";
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table = "CREATE TABLE IF NOT EXISTS player_map_stats (player_id int(22) NOT NULL, " +
                "map_id int(22) NOT NULL, " +
                "player_time_played int(22) DEFAULT 0, " +
                "player_time_finished int(22) DEFAULT 0, " +
                "player_time_won int(22) DEFAULT 0" +
                ");";
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table = "CREATE TABLE IF NOT EXISTS player_score (player_id int(22) NOT NULL, " +
                "score_total int(22) DEFAULT 0" +
                ");";
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table = "CREATE TABLE IF NOT EXISTS game_history ( " +
                "game_id int(22) NOT NULL," +
                "game_type varchar(100) NOT NULL," +
                "game_date bigint(255) NOT NULL," +
                "game_player_number int(4) NOT NULL)";
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table = "CREATE TABLE IF NOT EXISTS player_history ( " +
                "game_id int(22) NOT NULL," +
                "player_id int(10) NOT NULL," +
                "player_rank int(4) NOT NULL," +
                "player_score int(4) NOT NULL," +
                "player_time int(25) NOT NULL)";
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table = "CREATE TABLE IF NOT EXISTS player_challenge ( " +
                "player_id int(10) NOT NULL," +
                "player_challenge_maxgame int(3) DEFAULT 0)";
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(table);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
