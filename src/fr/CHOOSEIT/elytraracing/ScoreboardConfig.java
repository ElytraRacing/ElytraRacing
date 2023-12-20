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

package fr.CHOOSEIT.elytraracing;

import fr.CHOOSEIT.elytraracing.filesystem.FileUtils;
import fr.CHOOSEIT.elytraracing.gamesystem.GameState;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ScoreboardConfig {
    public static String filename = "Scoreboard.json";

    public void save() {
        setField(this);
        final File file = new File(Main.serialization.saveDIR_MessageConfig, filename);
        final String json1 = Main.serialization.serialize(this);
        FileUtils.saveFile(file, json1);
    }

    public static void load() {
        load(true);
    }

    public static void load(boolean save) {
        String json = FileUtils.loadContent(new File(Main.serialization.saveDIR_MessageConfig, filename));
        ScoreboardConfig config = Main.serialization.deserialize(json, ScoreboardConfig.class, filename);
        if (config != null) {
            if (save) {
                config.save();
            }
            setField(config);
        } else if (save) {
            config = new ScoreboardConfig();
            config.save();
            setField(config);
        } else {
            setField(new ScoreboardConfig());
        }
    }

    public static void setField(ScoreboardConfig scoreboardConfig) {
        Main.scoreboardConfig = scoreboardConfig;
    }

    public String c(String MSG) {
        if (MSG == null) {
            return "";
        }
        return MSG.replaceAll("&", "§");
    }

    public static class ScoreBoardSaver {
        public ScoreBoardSaver(String title, List<String> lines) {
            this.title = title;
            this.lines = lines;
        }

        String title;
        List<String> lines;

        public List<String> getLines() {
            if (lines == null) {
                return null;
            }
            return new ArrayList<>(lines);
        }

        public String getTitle() {
            if (title == null) {
                return null;
            }
            return new String(title);
        }
    }

    public static class ScoreboardStateSaver {
        private HashMap<BoardState, ScoreBoardSaver> perState = new HashMap<>();

        public void set(BoardState boardState, ScoreBoardSaver scoreBoardSaver) {
            perState.put(boardState, scoreBoardSaver);
        }

        public ScoreBoardSaver get(BoardState boardState) {
            if (boardState == BoardState.STARTING && !perState.containsKey(BoardState.STARTING)) {
                return get(BoardState.WAITING);
            }
            return perState.get(boardState);
        }

        public ScoreBoardSaver get(GameState gameState, boolean hosted, boolean spec) {
            if (hosted) {
                ScoreBoardSaver ssaver = get(BoardState.translate(gameState, true, spec));
                if (ssaver == null) {
                    return get(BoardState.translate(gameState, false, spec));
                } else {
                    return ssaver;
                }
            }
            return get(BoardState.translate(gameState, false, spec));
        }

        public void set(GameState gameState, ScoreBoardSaver scoreBoardSaver) {
            set(BoardState.translate(gameState, false, false), scoreBoardSaver);
        }
    }

    public static enum BoardState {
        WAITING,
        STARTING,
        STARTED,
        FINISHED,
        BETWEENROUND,
        HOST_WAITING,
        HOST_STARTING,
        HOST_STARTED,
        HOST_FINISHED,
        HOST_SPECTATOR,
        HOST_SPECTATOR_FINISHED,
        HOST_BETWEENROUND,
        HOST_SPECTATOR_BETWEENROUND,
        TRAINING,
        SPECTATOR,
        SPECTATOR_FINISHED,
        SPECTATOR_BETWEENROUND;


        public static BoardState translate(GameState gameState, boolean hosted, boolean spec) {
            if (spec) {
                if (hosted) {
                    if (gameState == GameState.FINISHED) {
                        return HOST_SPECTATOR_FINISHED;
                    } else {
                        return HOST_SPECTATOR;
                    }

                }
                if (gameState == GameState.FINISHED) {
                    return SPECTATOR_FINISHED;
                }
                return SPECTATOR;
            }
            if (hosted) {
                switch (gameState) {
                    case WAITING:
                        return HOST_WAITING;
                    case STARTING:
                        return HOST_STARTING;
                    case STARTED:
                        return HOST_STARTED;
                    case FINISHED:
                        return HOST_FINISHED;
                }
            }
            switch (gameState) {
                case WAITING:
                    return WAITING;
                case STARTING:
                    return STARTING;
                case STARTED:
                    return STARTED;
                case FINISHED:
                    return FINISHED;
            }
            return null;
        }
    }

    public static class ScoreboardRaceMode extends ScoreboardStateSaver {
        public ScoreboardRaceMode() {
            set(GameState.WAITING, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "&fMap: {CURRENT_MAP}", "&fMode: &aRace", "", "&cNeed {NEEDED_PLAYER} player(s) to start", "", "{LINK}")));
            set(GameState.STARTING, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "&fMap: {CURRENT_MAP}", "&fMode: &aRace", "", "&cStarting in {STARTING_COUNTDOWN} second(s)", "", "{LINK}")));
            set(GameState.STARTED, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "Time left: &c{TIME_LEFT}", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "", "&e1st - {RACE_RANK_PLAYER_1}", "&72nd - {RACE_RANK_PLAYER_2}", "&63rd - {RACE_RANK_PLAYER_3}", "Your rank: {SELF_RACE_RANK_PLAYER}", "", "Checkpoints: &6{SELF_CPS}/{MAX_CPS}", "Laps: &6{SELF_LAPS}/{MAX_LAPS}", "", "{LINK}")));
            set(GameState.FINISHED, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "&e1st - {RANK_PLAYER_1}", "&72nd - {RANK_PLAYER_2}", "&63rd - {RANK_PLAYER_3}", "Your rank: {SELF_RANK_PLAYER}", "", "{LINK}")));

            set(BoardState.SPECTATOR, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "Time left: &c{TIME_LEFT}", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "", "&e1st - {RACE_RANK_PLAYER_1}", "&72nd - {RACE_RANK_PLAYER_2}", "&63rd - {RACE_RANK_PLAYER_3}", "&f4th - {RACE_RANK_PLAYER_4}", "&f5th - {RACE_RANK_PLAYER_5}", "", "{LINK}")));
            set(BoardState.HOST_SPECTATOR, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "Time left: &c{TIME_LEFT}", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "", "&e1st - {RACE_RANK_PLAYER_1}", "&72nd - {RACE_RANK_PLAYER_2}", "&63rd - {RACE_RANK_PLAYER_3}", "&f4th - {RACE_RANK_PLAYER_4}", "&f5th - {RACE_RANK_PLAYER_5}", "", "{LINK}")));

            set(BoardState.SPECTATOR_FINISHED, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "&e1st - {RANK_PLAYER_1}", "&72nd - {RANK_PLAYER_2}", "&63rd - {RANK_PLAYER_3}", "&f4th - {RANK_PLAYER_4}", "&f5th - {RANK_PLAYER_5}", "", "{LINK}")));
            set(BoardState.HOST_SPECTATOR_FINISHED, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "&e1st - {RANK_PLAYER_1}", "&72nd - {RANK_PLAYER_2}", "&63rd - {RANK_PLAYER_3}", "&f4th - {RANK_PLAYER_4}", "&f5th - {RANK_PLAYER_5}", "", "{LINK}")));

            set(BoardState.HOST_WAITING, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "&fMap: {CURRENT_MAP}", "&fMode: &aRace", "", "Host: {HOST}", "", "{LINK}")));
            set(BoardState.HOST_STARTING, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "&fMap: {CURRENT_MAP}", "&fMode: &aRace", "", "Host: {HOST}", "&cStarting in {STARTING_COUNTDOWN} second(s)", "", "{LINK}")));
            set(BoardState.HOST_STARTED, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "Time left: &c{TIME_LEFT}", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "", "&e1st - {RACE_RANK_PLAYER_1}", "&72nd - {RACE_RANK_PLAYER_2}", "&63rd - {RACE_RANK_PLAYER_3}", "Your rank: {SELF_RACE_RANK_PLAYER}", "", "Checkpoints: &6{SELF_CPS}/{MAX_CPS}", "Laps: &6{SELF_LAPS}/{MAX_LAPS}", "", "{LINK}")));
            set(BoardState.HOST_FINISHED, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "&e1st - {RANK_PLAYER_1}", "&72nd - {RANK_PLAYER_2}", "&63rd - {RANK_PLAYER_3}", "Your rank: {SELF_RANK_PLAYER}", "", "{LINK}")));


        }
    }

    public static class ScoreboardGrandPrix extends ScoreboardStateSaver {
        public ScoreboardGrandPrix() {
            set(GameState.WAITING, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "&fMap: {CURRENT_MAP}", "&fMode: &aGrandPrix", "Round: 1/{MAX_ROUND}", "", "&cNeed {NEEDED_PLAYER} player(s) to start", "", "{LINK}")));
            set(GameState.STARTING, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "&fMap: {CURRENT_MAP}", "&fMode: &aGrandPrix", "Round: 1/{MAX_ROUND}", "", "&cStarting in {STARTING_COUNTDOWN} second(s)", "", "{LINK}")));
            set(GameState.STARTED, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "Time left: &c{TIME_LEFT}", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "Round: {CURRENT_ROUND}/{MAX_ROUND}", "", "&e1st - {RACE_RANK_PLAYER_1}", "&72nd - {RACE_RANK_PLAYER_2}", "&63rd - {RACE_RANK_PLAYER_3}", "Your rank: {SELF_RACE_RANK_PLAYER}", "", "Checkpoints: &6{SELF_CPS}/{MAX_CPS}", "Laps: &6{SELF_LAPS}/{MAX_LAPS}", "", "{LINK}")));
            set(GameState.FINISHED, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "&e1st - {RANK_PLAYER_1} - {RANK_PLAYER_1_SCORE}", "&72nd - {RANK_PLAYER_2} - {RANK_PLAYER_2_SCORE}", "&63rd - {RANK_PLAYER_3} - {RANK_PLAYER_3_SCORE}", "Your rank: {SELF_RANK_PLAYER}", "Your score: {SELF_RANK_PLAYER_SCORE}", "", "{LINK}")));
            set(BoardState.BETWEENROUND, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "Next map: {CURRENT_MAP}", "{CURRENT_MAP_DIFFICULTY}", "&6PB: {SELF_MAP_PB}", "", "&e1st - {RANK_PLAYER_1} - {RANK_PLAYER_1_SCORE}", "&72nd - {RANK_PLAYER_2} - {RANK_PLAYER_2_SCORE}", "&63rd - {RANK_PLAYER_3} - {RANK_PLAYER_3_SCORE}", "Your rank: {SELF_RANK_PLAYER}", "Your score: {SELF_RANK_PLAYER_SCORE}", "", "{LINK}")));

            set(BoardState.SPECTATOR, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "Time left: &c{TIME_LEFT}", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "Round: {CURRENT_ROUND}/{MAX_ROUND}", "", "&e1st - {RACE_RANK_PLAYER_1}", "&72nd - {RACE_RANK_PLAYER_2}", "&63rd - {RACE_RANK_PLAYER_3}", "&f4th - {RACE_RANK_PLAYER_4}", "&f5th - {RACE_RANK_PLAYER_5}", "", "{LINK}")));
            set(BoardState.SPECTATOR_FINISHED, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "&e1st - {RANK_PLAYER_1} - {RANK_PLAYER_1_SCORE}", "&72nd - {RANK_PLAYER_2} - {RANK_PLAYER_2_SCORE}", "&63rd - {RANK_PLAYER_3} - {RANK_PLAYER_3_SCORE}", "&f4th - {RANK_PLAYER_4} - {RANK_PLAYER_4_SCORE}", "&f5th - {RANK_PLAYER_5} - {RANK_PLAYER_5_SCORE}", "", "{LINK}")));
            set(BoardState.SPECTATOR_BETWEENROUND, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "Next map: {CURRENT_MAP}", "{CURRENT_MAP_DIFFICULTY}", "&6PB: {SELF_MAP_PB}", "", "&e1st - {RANK_PLAYER_1} - {RANK_PLAYER_1_SCORE}", "&72nd - {RANK_PLAYER_2} - {RANK_PLAYER_2_SCORE}", "&63rd - {RANK_PLAYER_3} - {RANK_PLAYER_3_SCORE}", "&f4th - {RANK_PLAYER_4} - {RANK_PLAYER_4_SCORE}", "&f5th - {RANK_PLAYER_5} - {RANK_PLAYER_5_SCORE}", "", "{LINK}")));

            set(BoardState.HOST_WAITING, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "&fMap: {CURRENT_MAP}", "&fMode: &aGrandPrix", "Round: 1/{MAX_ROUND}", "", "Host: {HOST}", "", "{LINK}")));
            set(BoardState.HOST_STARTING, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "&fMap: {CURRENT_MAP}", "&fMode: &aGrandPrix", "Round: 1/{MAX_ROUND}", "", "Host: {HOST}", "&cStarting in {STARTING_COUNTDOWN} second(s)", "", "{LINK}")));
            set(BoardState.HOST_STARTED, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "Time left: &c{TIME_LEFT}", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "Round: {CURRENT_ROUND}/{MAX_ROUND}", "", "&e1st - {RACE_RANK_PLAYER_1}", "&72nd - {RACE_RANK_PLAYER_2}", "&63rd - {RACE_RANK_PLAYER_3}", "Your rank: {SELF_RACE_RANK_PLAYER}", "", "Checkpoints: &6{SELF_CPS}/{MAX_CPS}", "Laps: &6{SELF_LAPS}/{MAX_LAPS}", "", "{LINK}")));
            set(BoardState.HOST_FINISHED, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "&e1st - {RANK_PLAYER_1} - {RANK_PLAYER_1_SCORE}", "&72nd - {RANK_PLAYER_2} - {RANK_PLAYER_2_SCORE}", "&63rd - {RANK_PLAYER_3} - {RANK_PLAYER_3_SCORE}", "Your rank: {SELF_RANK_PLAYER}", "Your score: {SELF_RANK_PLAYER_SCORE}", "", "{LINK}")));
            set(BoardState.HOST_BETWEENROUND, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "Next map: {CURRENT_MAP}", "{CURRENT_MAP_DIFFICULTY}", "&6PB: {SELF_MAP_PB}", "", "&e1st - {RANK_PLAYER_1} - {RANK_PLAYER_1_SCORE}", "&72nd - {RANK_PLAYER_2} - {RANK_PLAYER_2_SCORE}", "&63rd - {RANK_PLAYER_3} - {RANK_PLAYER_3_SCORE}", "Your rank: {SELF_RANK_PLAYER}", "Your score: {SELF_RANK_PLAYER_SCORE}", "", "{LINK}")));

            set(BoardState.HOST_SPECTATOR, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "Time left: &c{TIME_LEFT}", "&fPlayer(s): {CURRENT_PLAYER}/{MAX_PLAYER}", "Round: {CURRENT_ROUND}/{MAX_ROUND}", "", "&e1st - {RACE_RANK_PLAYER_1}", "&72nd - {RACE_RANK_PLAYER_2}", "&63rd - {RACE_RANK_PLAYER_3}", "&f4th - {RACE_RANK_PLAYER_4}", "&f5th - {RACE_RANK_PLAYER_5}", "", "{LINK}")));
            set(BoardState.HOST_SPECTATOR_FINISHED, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "&e1st - {RANK_PLAYER_1} - {RANK_PLAYER_1_SCORE}", "&72nd - {RANK_PLAYER_2} - {RANK_PLAYER_2_SCORE}", "&63rd - {RANK_PLAYER_3} - {RANK_PLAYER_3_SCORE}", "&f4th - {RANK_PLAYER_4} - {RANK_PLAYER_4_SCORE}", "&f5th - {RANK_PLAYER_5} - {RANK_PLAYER_5_SCORE}", "", "{LINK}")));
            set(BoardState.HOST_SPECTATOR_BETWEENROUND, new ScoreBoardSaver("{HOST_NAME}", Arrays.asList("", "Next map: {CURRENT_MAP}", "{CURRENT_MAP_DIFFICULTY}", "&6PB: {SELF_MAP_PB}", "", "&e1st - {RANK_PLAYER_1} - {RANK_PLAYER_1_SCORE}", "&72nd - {RANK_PLAYER_2} - {RANK_PLAYER_2_SCORE}", "&63rd - {RANK_PLAYER_3} - {RANK_PLAYER_3_SCORE}", "&f4th - {RANK_PLAYER_4} - {RANK_PLAYER_4_SCORE}", "&f5th - {RANK_PLAYER_5} - {RANK_PLAYER_5_SCORE}", "", "{LINK}")));
        }
    }

    public static class ScoreboardTraining extends ScoreboardStateSaver {
        public ScoreboardTraining() {
            set(BoardState.TRAINING, new ScoreBoardSaver("ElytraRacing", Arrays.asList("", "&6PB: {SELF_MAP_PB}", "&fRank: {SELF_MAP_RANK}", "", "WR holder: {MAP_WR_PLAYER}", "&6Time: &c{MAP_WR_TIME}", "", "Time left: &c{TIME_LEFT}", "&fDifficulty: {CURRENT_MAP_DIFFICULTY}", "Checkpoints: &6{SELF_CPS}/{MAX_CPS}", "Laps: &6{SELF_LAPS}/{MAX_LAPS}", "", "{LINK}")));
        }
    }

    public boolean isEnable() {
        return Enable;
    }

    public boolean Enable = false;
    public int TickReloadInterval = 20;
    public String link = "chooseit.io/er";

    public ScoreboardRaceMode RaceMode = new ScoreboardRaceMode();
    public ScoreboardGrandPrix GrandPrix = new ScoreboardGrandPrix();
    public ScoreboardTraining Training = new ScoreboardTraining();


}
