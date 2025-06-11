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
import fr.CHOOSEIT.elytraracing.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

public class CustomMessageConfig {
    public CustomMessageConfig() throws NoSuchFieldException {

        if (MSG_STEP_TIMER.isEmpty()) {
            MSG_STEP_TIMER.put(30, "Your game is starting in &c30 &fseconds");
            MSG_STEP_TIMER.put(10, "Your game is starting in &c10 &fseconds");
            MSG_STEP_TIMER.put(5, "Your game is starting in &c5 &fseconds");
            MSG_STEP_TIMER.put(4, "Your game is starting in &64 &fseconds");
            MSG_STEP_TIMER.put(3, "Your game is starting in &23 &fseconds");
            MSG_STEP_TIMER.put(2, "Your game is starting in &a2 &fseconds");
            MSG_STEP_TIMER.put(1, "Your game is starting in &f1 &fsecond");
            MSG_STEP_TIMER.put(0, "Your game is starting...");
        }
        if (MSG_STEPROUND_TIMER.isEmpty()) {
            MSG_STEPROUND_TIMER.put(15, "Next round is starting in &c15 &fseconds");
            MSG_STEPROUND_TIMER.put(5, "Next round is starting in &c5 &fseconds");
            MSG_STEPROUND_TIMER.put(4, "Next round is starting in &64 &fseconds");
            MSG_STEPROUND_TIMER.put(3, "Next round is starting in &23 &fseconds");
            MSG_STEPROUND_TIMER.put(2, "Next round is starting in &a2 &fseconds");
            MSG_STEPROUND_TIMER.put(1, "Next round is starting in &f1 &fsecond");
            MSG_STEPROUND_TIMER.put(0, "Next round is starting...");
        }
        if (MSG_STEP_TIMER_MAPSTART_TITLE.isEmpty()) {
            MSG_STEP_TIMER_MAPSTART_TITLE.put(3, "");
            MSG_STEP_TIMER_MAPSTART_TITLE.put(2, "");
            MSG_STEP_TIMER_MAPSTART_TITLE.put(1, "");
            MSG_STEP_TIMER_MAPSTART_TITLE.put(0, "");
        }
        if (MSG_STEP_TIMER_MAPSTART_SUBTITLE.isEmpty()) {
            MSG_STEP_TIMER_MAPSTART_SUBTITLE.put(3, "");
            MSG_STEP_TIMER_MAPSTART_SUBTITLE.put(2, "");
            MSG_STEP_TIMER_MAPSTART_SUBTITLE.put(1, "");
            MSG_STEP_TIMER_MAPSTART_SUBTITLE.put(0, "");
        }
        if (MSG_PER_RANK_TIME.isEmpty()) {
            MSG_PER_RANK_TIME.put(8, "&c{RANKING}&f: &f{PLAYER} &f>> {ARG_RANK_TIME}");
            MSG_PER_RANK_TIME.put(7, "&c{RANKING}&f: &f{PLAYER} &f>> {ARG_RANK_TIME}");
            MSG_PER_RANK_TIME.put(6, "&c{RANKING}&f: &f{PLAYER} &f>> {ARG_RANK_TIME}");
            MSG_PER_RANK_TIME.put(5, "&c{RANKING}&f: &f{PLAYER} &f>> {ARG_RANK_TIME}");
            MSG_PER_RANK_TIME.put(4, "&6{RANKING}&f: &6{PLAYER} &f>> {ARG_RANK_TIME}");
            MSG_PER_RANK_TIME.put(3, "&2{RANKING}&f: &2{PLAYER} &f>> {ARG_RANK_TIME}");
            MSG_PER_RANK_TIME.put(2, "&a{RANKING}&f: &a{PLAYER} &f>> {ARG_RANK_TIME}");
            MSG_PER_RANK_TIME.put(1, "&e{RANKING}&f: &e{PLAYER} &f>> {ARG_RANK_TIME}");
        }
        if (MSG_PER_RANK_SCORE.isEmpty()) {
            MSG_PER_RANK_SCORE.put(8, "&c{RANKING}&f: &f{PLAYER} &f>> {SCORE}");
            MSG_PER_RANK_SCORE.put(7, "&c{RANKING}&f: &f{PLAYER} &f>> {SCORE}");
            MSG_PER_RANK_SCORE.put(6, "&c{RANKING}&f: &f{PLAYER} &f>> {SCORE}");
            MSG_PER_RANK_SCORE.put(5, "&c{RANKING}&f: &f{PLAYER} &f>> {SCORE}");
            MSG_PER_RANK_SCORE.put(4, "&6{RANKING}&f: &6{PLAYER} &f>> {SCORE}");
            MSG_PER_RANK_SCORE.put(3, "&2{RANKING}&f: &2{PLAYER} &f>> {SCORE}");
            MSG_PER_RANK_SCORE.put(2, "&a{RANKING}&f: &a{PLAYER} &f>> {SCORE}");
            MSG_PER_RANK_SCORE.put(1, "&e{RANKING}&f: &e{PLAYER} &f>> {SCORE}");
        }
        if (SCORING_PER_RANK.isEmpty()) {
            SCORING_PER_RANK.put(1, 15);
            SCORING_PER_RANK.put(2, 12);
            SCORING_PER_RANK.put(3, 10);
            SCORING_PER_RANK.put(4, 9);
            SCORING_PER_RANK.put(5, 8);
            SCORING_PER_RANK.put(6, 7);
            SCORING_PER_RANK.put(7, 6);
            SCORING_PER_RANK.put(8, 5);
            SCORING_PER_RANK.put(9, 4);
            SCORING_PER_RANK.put(10, 3);
            SCORING_PER_RANK.put(11, 2);
            SCORING_PER_RANK.put(12, 1);
        }

    }

    public boolean replaceBy(CustomMessageConfig custommessage) {
        try {
            Field[] fs = custommessage.getClass().getFields();
            for (Field field : fs) {
                this.getClass().getDeclaredField(field.getName()).set(this, field.get(custommessage));
            }
            return true;
        } catch (IllegalAccessException e) {
            Utils.log_print("[ElytraRacing] Error ! 'ReplaceBy' Catch: 1");
            Utils.log_print("[ElytraRacing] Please restart the server");
        } catch (NoSuchFieldException e) {
            Utils.log_print("[ElytraRacing] Error ! 'ReplaceBy' Catch: 2");
            Utils.log_print("[ElytraRacing] Please restart the server");
        }
        return false;

    }

    public String DOCUMENTATION_LINK = "https://chooseit.gitbook.io/elytraracing";
    public String PREFIX = "&fElytraRacing &c>> &f";

    public int RELOADINTERVALOBJECTS = 10;
    public boolean auto_add_prefix = true;
    public int max_info_per_page = 10;
    public int ParticleRatio = 4;
    public boolean ER_SHOW_TRAIN = true;
    public boolean ER_SHOW_PLAY = true;
    public String PERMISSION_SIGN = "elytraracing.sign";
    public String PERMISSION_ER_PLAY = "elytraracing.play";
    public String PERMISSION_ER_TRAIN = "elytraracing.train";
    public ArrayList<String> ER_TRAIN_MAPS = new ArrayList<>();
    public boolean ER_TRAIN_ENABLE = true;
    public String ER_TRAIN_ENABLED = "&c";
    public String ER_TRAIN_MAP_NOTFOUND = "&cThis map is not eligible for training";
    public String ER_TRAIN_CHOOSE_MAP_GUI_TITLE = "Choose a map";
    public int SIGN_RELOAD_SEC = 3;
    public boolean TELEPORT_SOLVER = false;
    public int UNLOCK_DISTANCE = 100;
    public String UNLOCK_OBJECT = "&fUnlocked object";
    public String LOCK_OBJECT = "&fLinked this object with you &asuccessfully";
    public String LOCK_ITEMS_GIVE = "&fItems have been &asuccessfully &fgiven";
    public String LOCK_OBJECT_NEAR_NOTFOUND = "&cNo object have been found near you";
    public double ITEM_DEFAULT_PRECISION_LOC = 0.25;
    public double ITEM_DEFAULT_PRECISION_DEGREE = 2.5;
    public boolean SEPERATE_GAMES_HIDE_PLAYERS = true;
    public boolean CUSTOM_AND_SEPARATED_CHAT = false;
    public String CUSTOM_CHAT_FORMAT = "&6{PLAYER} &f>> {MESSAGE}";
    public int PARTICLE_VIEW_DISTANCE = 250;

    public int HOLOGRAMS_UPDATEINTERVAL = 60000;
    public float HOLOGRAMS_LINESPACE = 0.29f;
    public int HOLOGRAMS_RANK_LIMIT = 15;
    public boolean HOLOGRAMS_FAKE_LINES = false;
    public int HOLOGRAMS_PERSONAL_MAPS_MAX = 10;
    public boolean HOLOGRAMS_0_SHOW = true;
    public boolean HOLOGRAMS_RANK_DUPLICATION = true;

    public String REPLAY_FORMAT_16MAXCHAR = "&7{MINUTES}m {SECONDS}.{MILLISECONDS}s";

    public String SQL_LOCATION = "/database";
    public int SQL_STATS_INTERVAL = 10000;
    public boolean SQL_USE_CUSTOM_SQL = false;
    public String SQL_CUSTOM_SQL_HOST = "";
    public String SQL_CUSTOM_SQL_PORT = "";
    public String SQL_CUSTOM_SQL_DATABASE = "";
    public String SQL_CUSTOM_SQL_USER = "";
    public String SQL_CUSTOM_SQL_PASSWORD = "";
    public String SQL_CUSTOM_SQL_TYPE = "mysql";
    public boolean SQL_SAVE_HOST_STATS = true;

    public int TimerStart = 30;

    public int TimerStart_host = 10;
    public int Max_maps_per_games = 4;
    public int Max_alert_host = 3;

    public int GUI_MAPTESTING_MAXPLAYER = 10;
    public String GUI_MAPTESTING_PERMISSION = null;

    public int AutoTP_Spec_Distance = 75;

    public boolean TP_ON_HIT = true;
    public boolean LineHelper = true;
    public String NEXT_CHECKPOINT_PARTICLE = "END_ROD";
    public boolean CHECKPOINT_TELEPORT_MIDDLE = false;
    public int ELYTRA_MODELDATA = -1;
    public int TimerRound = 15;
    public boolean MAP_DIFICULTY = true;
    public int MAP_DIFFICULTY_DEFAULT = 3;
    public int MAP_DIFFICULTY_MAX = 5;
    public String MAP_DIFFICULTY_SYMBOL = "✮";
    public String MAP_DIFFICULTY_SYMBOL_ACTIVECOLOR = "&e";
    public String MAP_DIFFICULTY_SYMBOL_ACTIVECOLOR_MAX = "&c";
    public boolean MAP_DIFFICULTY_SYMBOL_MAX = true;
    public String MAP_DIFFICULTY_SYMBOL_ACTIVECOLOR_LAST = "&6";
    public String MAP_DIFFICULTY_SYMBOL_COLOR = "&7";
    public String MAP_DIFFICULTY_MESSAGE = "&7Map difficulty: {DIFFICULTY}";
    public int DNF_POINT = -1;
    public boolean TABLIST_GRANDPRIX = true;
    public boolean COMMANDS_WHITELIST = false;
    public String[] COMMANDS_WHITELIST_COMMANDS = new String[]{"msg", "rl", "reload"};
    public String COMMANDS_WHITELIST_BYPASS_PERMISSION = "elytraracing.wlcommands.bypass";
    public String COMMANDS_MESSAGE = "&cYou can't perform this command here !";
    public boolean SPECTATOR_ITEM = true;
    public boolean STATIC_ITEMS = true;

    public int RankingMaxPlayers = 8;
    public boolean PODIUM_FIREWORKS = true;
    public int PODIUM_FIREWORK_NUMBER = 10;
    public int END_TIME = 30;

    public boolean DISABLE_ELYTRA = false;

    public boolean COMMAND_EXECUTION_PER_RANK_IN_HOST = false;
    public HashMap<Integer, String> COMMAND_EXECUTION_PER_RANK_GRANDPRIX = new HashMap<Integer, String>() {{
        put(-1, "[CONSOLE]/give {SELF_PLAYER} minecraft:cookie {SCORE} §%§[PLAYER]/tell {SELF_PLAYER} you executed a command");
        put(0, "[PLAYER]/tell {SELF_PLAYER} you executed a command");
    }};
    public HashMap<Integer, String> COMMAND_EXECUTION_PER_RANK_RACEMODE = new HashMap<Integer, String>() {{
        put(-1, "[CONSOLE]/give {SELF_PLAYER} minecraft:cookie 5 §%§[PLAYER]/tell {SELF_PLAYER} you executed a command");
        put(0, "[PLAYER]/tell {SELF_PLAYER} you executed a command");
    }};

    public String COMMAND_EXECUTION_AT_START_RACEMODE = "";
    public String COMMAND_EXECUTION_AT_START_GRANDPRIX = "";

    public String PERMISSION_MESSAGE = "&cYou are not allowed to perform this command";
    public String PERMISSION_MESSAGE_HOST = "&cYou are not allowed to join this game";


    public String MSG_NUMBER_LAP = "&eNumber of lap(s): {LAPS}";

    //Material:ID
    public String HOLOGRAM_NOT_FOUND = "&cThis hologram can't be found.";
    public String ITEM_1CP_BACK = "&cPrevious checkpoint";
    public String ITEM_1CP_BACK_MATERIAL = null;
    public int ITEM_1CP_BACK_SLOT = 0;
    public String ITEM_2CP_BACK = "&c2 checkpoints back";
    public String ITEM_2CP_BACK_MATERIAL = null;
    public int ITEM_2CP_BACK_SLOT = 1;
    public String ITEM_RESTART = "&cRESTART";
    public String ITEM_RESTART_MATERIAL = null;
    public int ITEM_RESTART_SLOT = 4;
    public String ITEM_DNF = "&cDNF";
    public String ITEM_DNF_MATERIAL = null;
    public int ITEM_DNF_SLOT = 8;
    public String ITEM_BED = "&cLeave game";
    public String ITEM_BED_MATERIAL = null;
    public int ITEM_BED_SLOT = 8;
    public String ITEM_SPEC = "&aJoin spec mode";
    public String ITEM_SPEC_MATERIAL = null;
    public int ITEM_SPEC_SLOT = 4;
    public String ITEM_CUSTOM = "&aJoin spec mode";
    public String ITEM_CUSTOM_MATERIAL = null;
    public int ITEM_CUSTOM_SLOT = 3;
    public String ITEM_HOST = "&aHost Configuration";
    public String ITEM_HOST_MATERIAL = null;
    public int ITEM_HOST_SLOT = 4;
    public String ITEM_PARTICLE = "&aParticle Selector";
    public String ITEM_PARTICLE_MATERIAL = null;
    public int ITEM_PARTICLE_SLOT = 2;
    public int ITEM_FIREWORK_SLOT = 2;
    public int ITEM_PAPERMAPINFO_SLOT = 4;
    public String ITEM_PAPERMAPINFO_MATERIAL = null;
    public String ITEM_HOSTSEPARATION_TITLE = "&fMap Selection";
    public String ITEM_HOSTSEPARATION_DESCRIPTION = "&a⬇  ⬇  ⬇  ⬇  ⬇  ⬇";
    public String ITEM_HOSTSEPARATION_MATERIAL = null;
    public String ITEM_HOSTMAP_TITLE_SELECTED = "&a{MAP_NAME}";
    public String ITEM_HOSTMAP_TITLE_NOTSELECTED = "&f{MAP_NAME}";
    public String ITEM_HOSTMAP_MATERIAL_SELECTED = null;
    public String ITEM_HOSTMAP_MATERIAL_NOTSELECTED = null;
    public String HOSTMAP_CREATOR_INVENTORY_TITLE = "Host creator";

    public String ALERT_HOST = "&6{PLAYER} &ajust started to host &7(Click here to join)";
    public String ERROR_MESSAGE = "&cAn error was encounter during process";

    public String ACTIONBAR_TIME = "&7Time left: &c{TIME}";
    public String MSG_PAGE = "&7Page &c{PAGE} &7of &c{MAX_PAGE}";
    public String MSG_ENDMAP = "&6{PLAYER} &7-> &c{ARG}";
    public String MSG_GAMEFULL = "&cThis game is already full";
    public String MSG_GAMESTARTED = "&cThis game is already started";
    public String LOCATIONNOTDEFINED = "&cThis location isn't defined !";
    public String MSG_ERHOST_LISTGAMES = "&aList of games &7&o(hover): ";
    public String MSG_ERMAP_LISTGAMES_GAMESFORMAT = "&c>> &f{GAME_NAME} ";

    public String MSG_ERMAP_LISTCHECKPOINT = "&aList of checkpoints &7&o(hover): ";
    public String DEFAULT = "&6DEFAULT";
    public String NONE = "&6NONE";
    public String NOBODY = "Nobody";
    public String MSG_ERMAP_LISTCHECKPOINT_CHECKPOINTFORMAT = "&f#{CHECKPOINT_ID_ORDER} &c>> &f({X},{Y},{Z}) &7&o(Click to teleport)";

    public String MSG_ERMAP_LISTEND = "&aList of ends &7&o(hover): ";
    public String MSG_ERMAP_LISTEND_ENDFORMAT = "&f#{END_ID_ORDER} &c>> &f({X},{Y},{Z}) &7&o(Click to teleport)";


    public String YES = "&aYes";
    public String NO = "&cNo";
    public String CHECK = "&a✓";
    public String CROSS = "&cx";

    public String MSG_STATE_WAITING = "&aWaiting";
    public String MSG_STATE_STARTING = "&6Starting";
    public String MSG_STATE_STARTED = "&cStarted";
    public String MSG_SPEC_JOINING = "&7Joining the game as spectator...";
    public String PERM_SPECGAME = "elytraracing.specgame";

    public String MSG_NOGAMETOLEAVE = "&cNo need to leave, you're not in a game yet";
    public String MSG_GAMENOTEXIST = "&cThis game does not exist";
    public String MSG_NOTYOURHOST = "&cYou aren't the host of this game";
    public String MSG_TIMERSTART = "&aTimer started";
    public String MSG_TIMERCANCEL = "&cTimer cancelled";
    public String MSG_HOST_NOTEXIST = "&cYour host can't be found";
    public String MSG_LEAVEYOURGAME = "&cYou are already in a game";
    public String MSG_GAMENOTOPEN = "&cThis is not open";
    public String MSG_MAPCREATING = "&aCreating map...";
    public String MSG_MAPNAMEEXIST = "&cThis map name already exists";
    public String MSG_GAMENAME_EXIST = "&cThis game name already exists";
    public String MSG_HOST_CREATING = "&aCreating host...";
    public String MSG_CHECKPOINT_VERIFIED = "&fCheckpoint passed ! ({CHECKPOINT_PASSED} / {CHECKPOINT_MAX})";
    public String MSG_LAP_VERIFIED = "&eLap completed ! ({LAP_PASSED} / {LAP_MAX})";
    public String MSG_GAME_PLAYERJOIN = "&6{PLAYER_NAME} &fjoined the race ! &7&o({PLAYER_SIZE} / {MAX_PLAYER})";
    public String MSG_GAME_PLAYERJOIN_PLAYERNEEDEDLEFT = " &c({PLAYER_NEEDED} left needed)";
    public String MSG_GAME_PLAYERLEAVE = "&6{PLAYER_NAME} &fleft the race ! &7&o({PLAYER_SIZE} / {MAX_PLAYER})";
    public String MSG_GAME_NOTENOUGHPLAYERS = "&cTimer cancelled, not enough players";
    public String PLAYER_NOTFOUND = "&cPlayer not found.";
    public String ERHOST_PLAYER_NOTFOUND = "&cThis player is not in your game.";
    public String MSG_MAP_NOTAVAILABLE = "&cThis map is not available !";
    public String MSG_MAP_NOTAVAILABLE2 = "&c{MAP} isn't available !";
    public String MSG_MAPNOTFOUND2 = "&c{ARG} can't be found";
    public String MSG_ALERT = "&cYou already sent too many alerts";
    public String PARTICLE_LIST = "&7Particle list (Click to activate): ";
    public String PARTICLE_LIST_ITEMS = " &7{item}";
    public String PARTICLE_LIST_ITEMS_HOVER = "&7{item}: Click to activate";
    public String PARTICLE_LIST_NOTFOUND = "&cThis particle type can't be found";
    public String GRANDPRIX = "Grandprix";
    public String RACEMODE = "Race";

    public String PERSONAL_BEST = " &6&k&llol &6&lPersonal Best&k&llol";
    public String TIME_NOT_FOUND = " - ";
    public String RANK_NOT_FOUND = "-";

    public String ER_PLAY_GAME = "&cNo game have been found !";

    public HashMap<Integer, String> MSG_STEP_TIMER = new HashMap<>();
    public HashMap<Integer, String> MSG_STEPROUND_TIMER = new HashMap<>();


    public int TIMER_MAPSTART_START = 3;
    public HashMap<Integer, String> MSG_STEP_TIMER_MAPSTART_TITLE = new HashMap<Integer, String>() {{
        put(3, "&c3");
        put(2, "&62");
        put(1, "&e1");
        put(0, "&c>>  GO  <<");
    }};
    public HashMap<Integer, String> MSG_STEP_TIMER_MAPSTART_SUBTITLE = new HashMap<Integer, String>() {{
        put(3, "");
        put(2, "");
        put(1, "");
        put(0, "");
    }};

    public HashMap<Integer, String> MSG_PER_RANK_TIME = new HashMap<>();
    public HashMap<Integer, String> MSG_PER_RANK_SCORE = new HashMap<>();
    public HashMap<Integer, Integer> SCORING_PER_RANK = new HashMap<>();
    public String MSG_PRE_RANK = "&7\nRanking:\n";
    public String MSG_PRE_SCORE = "&7\nScoreboard:\n";
    public String MSG_YOURRANK = "&7Your rank on this map: &c{RANK}";
    public String MSG_YOURSCORE = "&7Your overall rank is: &c{RANK} &7with &c{SCORE} &7points";
    public String MSG_ARG_RANK_TIME_DNF = "&cDNF";
    public String MSG_ARG_RANK_TIME_TIME = "&c{TIME}";
    public String TELEPORTING = "&7Teleporting...";
    public String TIMEFORMAT = "{MINUTES}m {SECONDS}.{MILLISECONDS}s";
    public String TIMEFORMAT_WM = "{MINUTES}m {SECONDS}s";

    public String PLACEHOLDER_NOTFOUND_PLAYER = "None";
    public String PLACEHOLDER_DEFAULT_NUMBER = "0";
    public String PLACEHOLDER_DEFAULT_TEXT = "";
    public String PLACEHOLDER_DEFAULT_RANKING = "-";

    public String ERMAP_MAP_DELETE = "&cThe map will be deleted after a reload/restart";

    public String MSG_MAPNOTFOUND = "&cThis map can't be found";
    public String MSG_SAVE_SUCCESSFUL = "&fChanges have been &asucessfully &fsaved";
    public String MSG_FILE_RELOAD = "&fFile(s) have been &asucessfully &freloaded.";
    public String MSG_SHOW_ON = "&aEnabling ...";
    public String MSG_SHOW_OFF = "&cDisabling ...";
    public String MSG_CHECKPOINT_NOTFOUND = "&cThis checkpoint can't be found";
    public String MSG_END_NOTFOUND = "&cThis end can't be found";
    public String ID_ORDER_ARG = "&c[id_order] must be an integer";
    public String ID_ORDER_TARGET_ARG = "&c[id_order_target] must be an integer";
    public String DEGREES_ARG = "&c[rotation] must be an integer";
    public String MULTIPLIER_ARG = "&c[amount] must be a float";
    public String SIZE_ARG = "&c[size] must be a float";
    public String AMOUNT_ARG = "&c[amount] must be an integer";
    public String SECONDS_ARG = "&c[seconds] must be an integer between 0 and 59";
    public String MINUTES_ARG = "&c[minutes] must be an integer greater than 0";
    public String MAX_PLAYER_ARG = "&c[slots] must be an integer greater than 0";
    public String PAGE_ARG = "&c[page] must be an integer greater than 0";
    public String ERCONFIG_SETITEM_ID_NOT_FOUND = "&cThis [id_item] can't be found";
    public String ERCONFIG_SETITEM_ITEM_NOT_FOUND = "&cThe item in your hand can not be found";
    public String SPEC_PERM = null;
    public String SPEC_PERM_MESSAGE = "&cYou don't have permission to spectate a game.";
    public String HOST_GAMENOTOPEN = "&cYour game is not open";

    public String SPECTATORMODE = "&aYou have entered in spectator mode &7(Click here to leave or type '/er quitspec')";
    public String STUCKSPECTATOR = "&aUse /er near §fto teleport to the nearest player";

    public String AVAILABLE_MAPS = "&aAvailable maps: {MAPS}";


    public String ERGAMES_LIST = "&6List of autogames: {AUTOGAMES}";
    public String ERGAMES_ENABLED_PREFIX = "&a";
    public String ERGAMES_DISABLED_PREFIX = "&c";
    public String ERGAMES_LIST_SEPERATION_PREFIX = "&7";
    public String ERGAMES_NOTFOUND = "&cThis autogame can't be found";
    public String ERGAMES_ALREADYGAME = "&cYou can't enable this autogame because there is already a game with the same name";

    public String TABLIST_RANK_SCORE_TITLE = "&7Scoreboard";
    public HashMap<Integer, String> TABLIST_RANK_SCORE_SPECIFIC = new HashMap<Integer, String>() {{
        put(-1, "&9{RANK} &7- &9{USERNAME} &7- &c{SCORE}");
        put(1, "&e{RANK} &7- &e{USERNAME} &7- &c{SCORE}");
        put(2, "&7{RANK} &7- &7{USERNAME} &7- &c{SCORE}");
        put(3, "&6{RANK} &7- &6{USERNAME} &7- &c{SCORE}");
    }};
    public String TABLIST_GRANDPRIX_PERSONAL_PERSONAL = "&6Personal stats on this map:";
    public String TABLIST_GRANDPRIX_PERSONAL_TIME = "&9Best personal time: &c{TIME}";
    public String TABLIST_GRANDPRIX_PERSONAL_RANK = "&9Your rank: &c{RANK}";
    public String TABLIST_GRANDPRIX_PERSONAL_WINRATE = "&9Your winrate: &c{WINRATE} %";


    public String HOLOGRAMS_RANK_SCORE_TITLE = "&6&lTop 15 Highest scores";
    public HashMap<Integer, String> HOLOGRAMS_RANK_SCORE_SPECIFIC = new HashMap<Integer, String>() {{
        put(-2, "&9{RANK} &7- &9-------- &7- &c0");
        put(-1, "&9{RANK} &7- &9{USERNAME} &7- &c{SCORE}");
        put(1, "&e{RANK} &7- &e{USERNAME} &7- &c{SCORE}");
        put(2, "&7{RANK} &7- &7{USERNAME} &7- &c{SCORE}");
        put(3, "&6{RANK} &7- &6{USERNAME} &7- &c{SCORE}");
    }};
    public String HOLOGRAMS_RANK_WON_RACEMODE_TITLE = "&6&lTop 15 Highest races won";
    public HashMap<Integer, String> HOLOGRAMS_RANK_WON_RACEMODE_SPECIFIC = new HashMap<Integer, String>() {{
        put(-2, "&9{RANK} &7- &9-------- &7- &c0");
        put(-1, "&9{RANK} &7- &9{USERNAME} &7- &c{SCORE}");
        put(1, "&e{RANK} &7- &e{USERNAME} &7- &c{SCORE}");
        put(2, "&7{RANK} &7- &7{USERNAME} &7- &c{SCORE}");
        put(3, "&6{RANK} &7- &6{USERNAME} &7- &c{SCORE}");
    }};
    public String HOLOGRAMS_RANK_WON_GRANDPRIX_TITLE = "&6&lTop 15 Highest grandprix won";
    public HashMap<Integer, String> HOLOGRAMS_RANK_WON_GRANDPRIX_SPECIFIC = new HashMap<Integer, String>() {{
        put(-2, "&9{RANK} &7- &9-------- &7- &c0");
        put(-1, "&9{RANK} &7- &9{USERNAME} &7- &c{SCORE}");
        put(1, "&e{RANK} &7- &e{USERNAME} &7- &c{SCORE}");
        put(2, "&7{RANK} &7- &7{USERNAME} &7- &c{SCORE}");
        put(3, "&6{RANK} &7- &6{USERNAME} &7- &c{SCORE}");
    }};
    public String HOLOGRAMS_RANK_MAP_TITLE = "&6&lTop 15 Best times on {MAP}";
    public HashMap<Integer, String> HOLOGRAMS_RANK_MAP_SPECIFIC = new HashMap<Integer, String>() {{
        put(-2, "&9{RANK} &7- &9-------- &7- &c0");
        put(-1, "&9{RANK} &7- &9{USERNAME} &7- &c{SCORE}");
        put(1, "&e{RANK} &7- &e{USERNAME} &7- &c{SCORE}");
        put(2, "&7{RANK} &7- &7{USERNAME} &7- &c{SCORE}");
        put(3, "&6{RANK} &7- &6{USERNAME} &7- &c{SCORE}");
    }};
    public String HOLOGRAMS_PERSONAL_MAPS_TITLE = "&6&lPersonal stats on maps";
    public String HOLOGRAMS_PERSONAL_MAPS_SPECIFIC = "&6{MAP_NAME} &7: &c{PLAYER_TIME} &7(#{PLAYER_RANK}) (winrate: {WINRATE}%)";
    public String HOST_MAPCHOOSER_CHANGEPAGE = "Click here to change page";

    public boolean DNF_INSTANT_QUIT = false;


    public String[] MSG_LISTMAPS_INFO = new String[]{"&a>> {MAP_NAME}",
            " ",
            "&f>> &7Valid: {VALID}",
            "&f>> &7Enabled: {ENABLED}",
            " ",
            "&f>> &7Checkpoints: &c{CHECKPOINTS}",
            "&f>> &7Ends: &c{ENDS}",

            "&f>> &7Lobby location: &c{LOBBY}",
            "&f>> &7Start location: &c{START}",
            "&f>> &7End location: &c{END}",
            "&f>> &7Podium: &c{PODIUM}",
            " ",
            "&f>> &7Time max.: &c{TIME}",
            "&f>> &7Difficulty: &c{DIFFICULTY}",
            " "};
    public String HOST_TITLE_PERM = "&7Permission: &c{PERMISSION}";
    public String[] HOST_DESC_PERM = new String[]{
            " ",
            "&f/erhost setperm &c[permission/none]",
            "&fTo define the permission to join your game.",
            " "};
    public String HOST_TITLE_ALERT = "&aAlert";
    public String[] HOST_DESC_ALERT = new String[]{
            " ",
            "&fClick here to broadcast an invitation to join your game",
            " "};
    public String HOST_TITLE_START = "&aStart";
    public String[] HOST_DESC_START = new String[]{
            " ",
            "&fClick here to start the game",
            " "};
    public String HOST_TITLE_END = "&cCancel countdown";
    public String[] HOST_DESC_END = new String[]{
            " ",
            "&fClick here to stop the countdown",
            " "};
    public String HOST_TITLE_SLOTS = "&cSlots";
    public String[] HOST_DESC_SLOTS = new String[]{
            " ",
            "&fNumber of slots: &c{SLOTS}",
            " ",
            "&7Left click: +1 (+ Shift: +10)",
            "&7Right click: -1 (+ Shift: -10)",
            " "};
    public String HOST_TITLE_OPEN = "&fYour host is currently: &aOpen";
    public String HOST_TITLE_CLOSED = "&fYour host is currently: &cClosed";
    public String HOSTCREATOR_CREATE_TITLE = "&aCreate the host";
    public String[] HOSTCREATOR_CREATE_DESCRIPTION = new String[]{
            " ",
            "&fName: &c{NAME}",
            " ",
            "&fNumber of slots: &c{SLOTS}",
            "&fNumber of maps: &c{MAP_NUMBER}",
            "&fMode: &c{MODE}",
            " "};
    public String HOSTCREATOR_MAPS_TITLE = "&fNumber of maps: &c{MAP_NUMBER}";
    public String HOSTCREATOR_MAPS_DESCRIPTION = "&f{ORDER}. {MAP_NAME}";
    public String HOSTCREATOR_MAXPLAYERS_TITLE = "&fSlots: {SLOTS}";
    public String[] HOST_MAXPLAYERS_SLOTS = new String[]{
            " ",
            "&fNumber of slots: &c{SLOTS}",
            " ",
            "&7Left click: +1 (+ Shift: +10)",
            "&7Right click: -1 (+ Shift: -10)",
            " "};
    public HashMap<String, String> STATES = new HashMap<String, String>() {{
        put("WAITING", "&aWaiting");
        put("STARTING", "&6Starting");
        put("STARTED", "&cIngame");
        put("FINISHED", "&7Finishing");
    }};
    public String[] SIGN_FORMAT_ONEQUEUE_ONEGAME = new String[]{
            "&f[ElytraRacing]",
            "&6{MAP}",
            "{STATE}",
            "&c{PLAYERS}/{MAX_PLAYERS}"};
    public String[] SIGN_FORMAT = new String[]{
            "&f[ElytraRacing]",
            "&6{MAP}",
            "&c{PLAYERS}/{MAX_PLAYERS}",
            "&7Click to join"};
    public String[] SIGN_FORMAT_DISABLED = new String[]{
            "&f[ElytraRacing]",
            " ",
            "&4DISABLED",
            " "};
    public String[] MSG_LISTCHECKPOINT_INFO = new String[]{
            " ",
            "&f>> &7ID: {ID_ORDER}",
            " ",
            "&f>> &7x: &c{X}",
            "&f>> &7y: &c{Y}",
            "&f>> &7z: &c{Z}",
            "&f>> &7World: &c{WORLD}",
            " ",
            "&f>> &7Radius: &c{RADIUS}",
            "&f>> &7Particle amount: &c{PARTICLE_AMOUNT}",
            "&f>> &7Boost multiplier: &c{BOOST_MULTIPLIER}",
            "&f>> &7",
            "&f>> &7Particle type: &c{PARTICLE_TYPE}",
            "&f>> &7Linked to: &c{LINK}"};
    public String[] MSG_LISTGAMES_INFO = new String[]{"&a>> {GAME_NAME}",
            " ",
            "&f>> &7Players: &c{PLAYER_SIZE}",
            "&f>> &7min. players: &c{MIN_PLAYER}",
            "&f>> &7max. players: &c{MAX_PLAYER}",
            " ",
            "&f>> &7State: &c{GAME_STATE}"};
    public String[] MSG_LISTEND_INFO = new String[]{
            " ",
            "&f>> &7ID: {ID_ORDER}",
            " ",
            "&f>> &7x: &c{X}",
            "&f>> &7y: &c{Y}",
            "&f>> &7z: &c{Z}",
            "&f>> &7World: &c{WORLD}",
            " ",
            "&f>> &7Radius: &c{RADIUS}",
            "&f>> &7Particle amount: &c{PARTICLE_AMOUNT}",
            "&f>> &7",
            "&f>> &7Particle type: &c{PARTICLE_TYPE}"};

    public String ERSTATS_MAP = "&6&lPersonal stats on maps";
    public String ERSTATS_MAP_EACH = "&6>> {MAP_NAME} &9: {PLAYER_TIME} &9(#{PLAYER_RANK}) \n&6> &9(winrate: {WINRATE}%)";
    public String CHANGEPAGE = "&7Click here to change page";
    public String CHANGEPAGERIGHT = "&9➡";
    public String CHANGEPAGELEFT = "&c⬅";
    public String[] ERTRAIN_MAP_INFO = new String[]{
            "&fYour time: &6{PLAYER_TIME}",
            "&fYour rank: &6#{PLAYER_RANK}",
            "&fYour winrate: &6{WINRATE}%", " "
    };
    public String[] ERTRAIN_MAP_INFO_PLUS = new String[]{
            "", "&7Difficulty: {DIFFICULTY}", "&7Lap(s): &c{LAPS}", "", "&7Click here to select this map"
    };
    public String ERTRAIN_MAP_INFO_TITLE = "&f{MAP_NAME}";
    public boolean FORCE_KICK_VERSION_BELOW_19 = true;
    public String PLAYER_VERSION_BELOW_19 = "&cYou don't have the right version to play this game";
    public String[] USAGE_ERSTATS_SHOW = new String[]{
            "&6&lPersonal stats: {USERNAME}",
            "&7Races played: &c{PLAYER_GAME_PLAYED_RACEMODE} ",
            "&7Races won: &c{PLAYER_GAME_WON_RACEMODE} &9(#{RANK_WON_RACEMODE}) &7({PERCENTAGE_WINRATE_RACEMODE}%)",
            "&7Grandprix played: &c{PLAYER_GAME_PLAYED_GRANDPRIX} ",
            "&7Grandprix won: &c{PLAYER_GAME_WON_GRANDPRIX} &9(#{RANK_WON_GRANDPRIX}) &7({PERCENTAGE_WINRATE_GRANDPRIX}%)",
            "&7Score: &c{SCORE_TOTAL} &9(#{RANK_SCORE_TOTAL})",
            "&6Favorite map: &6{FAVORITE_MAP} &7&o(winrate: {FAVORITE_MAP_WINRATE}%)",
            "[HOVERMESSAGE_MAP_STATS]&9Personal stats on maps &7&o(hover)",
    };
    public String[] HOLOGRAMS_PERSONALSTATS = new String[]{
            "&6&lPersonal stats:",
            "&7Races played: &c{PLAYER_GAME_PLAYED_RACEMODE} ",
            "&7Races won: &c{PLAYER_GAME_WON_RACEMODE} &9(#{RANK_WON_RACEMODE}) &7({PERCENTAGE_WINRATE_RACEMODE}%)",
            "&7Grandprix played: &c{PLAYER_GAME_PLAYED_GRANDPRIX} ",
            "&7Grandprix won: &c{PLAYER_GAME_WON_GRANDPRIX} &9(#{RANK_WON_GRANDPRIX}) &7({PERCENTAGE_WINRATE_GRANDPRIX}%)",
            "&7Score: &c{SCORE_TOTAL} &9(#{RANK_SCORE_TOTAL})",
            "&6Favorite map: &6{FAVORITE_MAP} &7&o(winrate: {FAVORITE_MAP_WINRATE}%)",
    };
    public String MAP_INGAME_INFO_MENU_TITLE = "&fMaps information";
    public String[] MAP_INGAME_INFO_DESCRIPTION = {"&f{ORDER}. {MAP_NAME}","&f-> Your stats: {PLAYER_TIME},#{PLAYER_RANK} ({PLAYER_WINRATE}%)"};
    public String NOTENOUGH_MAP_HOSTCREATOR = "&cYou need at least 1 map to create a host";

    public boolean ONEGAME = false;
    public String ONEGAME_AUTOJOINGAME = "";
    public boolean ONEGAME_KICK_AFTER_GAME = true;
    public String ONEGAME_LOBBYSERVER = "";
    public boolean ONEGAME_REMOVEJOINQUITMESSAGES = true;

    public boolean ONEGAME_MOTD_USE = false;
    public String[] ONEGAME_MOTD = new String[]{"&f>> ElytraRacing: {STATE} &7[-] Map: &6{MAP}", "§f>> &7Playing: §c{PLAYING} &7[-] Mode: {MODE_INFORMATION}"};
    public String ONEGAME_MOTD_RACEMODE_INFORMATION = "&aRacemode";
    public String ONEGAME_MOTD_GRANDPRIX_INFORMATION = "&aGrandPrix &f{ROUND}/{ROUND_MAX}";
    public boolean ONEGAME_MOTD_CHANGE_MAX_PLAYERS = true;

    public boolean CHALLENGE_MAXGAME = false;
    public int CHALLENGE_MAXGAME_PER_PLAYER = 10;
    public String CHALLENGE_MAXGAME_COMPLETION_COMMAND = "[CONSOLE]/give {SELF_PLAYER} minecraft:cookie §%§[PLAYER]/tell {SELF_PLAYER} you executed a command";

    public boolean PLAY_SOUND = true;
    public String SOUND_START_3 = "";
    public float SOUND_START_3_VOLUME = 1f;
    public float SOUND_START_3_PITCH = 1f;
    public String SOUND_START_2 = "";
    public float SOUND_START_2_VOLUME = 1f;
    public float SOUND_START_2_PITCH = 1f;
    public String SOUND_START_1 = "";
    public float SOUND_START_1_VOLUME = 1f;
    public float SOUND_START_1_PITCH = 1f;
    public String SOUND_START_START = "";
    public float SOUND_START_START_VOLUME = 1f;
    public float SOUND_START_START_PITCH = 1f;
    public String SOUND_CHECKPOINT_PASSED = null;
    public float SOUND_CHECKPOINT_PASSED_VOLUME = 1f;
    public float SOUND_CHECKPOINT_PASSED_PITCH = 1f;
    public String SOUND_END_PASSED = null;
    public float SOUND_END_PASSED_VOLUME = 1f;
    public float SOUND_END_PASSED_PITCH = 1f;
    public String SOUND_ADDITIONAL_OBJECT_PASSED = null;
    public float SOUND_ADDITIONAL_OBJECT_PASSED_VOLUME = 1f;
    public float SOUND_ADDITIONAL_OBJECT_PASSED_PITCH = 1f;
    public float SOUND_FIREWORKS_VOLUME = 1f;
    public float SOUND_FIREWORKS_PITCH = 1f;

    public boolean WIN_RACEMODE_WITHOUT_FINISHING = true;

    public String CONSOLE_USERNAME = "server";

    public boolean RENDER_DISTANCE_CP = false;
    public int RENDER_DISTANCE_CP_VALUE = 2;

    public boolean HOOK_PARTIES_ENABLE = true;


    public String removeColors(String MSG) {
        return MSG.replaceAll("&", "");
    }

    public String basicsetting(String MSG, Player p) {
        String m = "";
        if (auto_add_prefix) {
            m += basicsettingnoprefix(PREFIX, p);
        }
        m += basicsettingnoprefix(MSG, p);
        return m;
    }

    public List<String> basicsettingnoprefix(List<String> MSG, Player p) {
        List<String> finallist = new ArrayList<>();
        MSG.forEach(o -> finallist.add(basicsettingnoprefix(o, p)));
        return finallist;
    }

    public String basicsettingnoprefix(String MSG){
        return basicsettingnoprefix(MSG, null);
    }
    public String basicsettingnoprefix(String MSG, Player p) {
        if (MSG == null) {
            return "";
        }
        if (Main.hookPlaceHolderAPI) {
            MSG = PlaceholderAPI.setPlaceholders(p, MSG);
        }
        return hexColor(MSG.replaceAll("&", "§"));
    }

    public void save() {
        final File file = new File(Main.serialization.saveDIR_MessageConfig, "Config.json");
        final String json1 = Main.serialization.serialize(Main.customMessageConfig);
        FileUtils.saveFile(file, json1);
    }


    public static String hexColor(String MSG) {
        if (MSG == null) {
            return "";
        }
        String s = MSG;
        if (Main.getVersion() >= 16) {
            Matcher matcher = Main.hexpattern.matcher(s);
            String color;
            while (matcher.find()) {
                color = s.substring(matcher.start(), matcher.end());

                Method[] ms = ChatColor.class.getMethods();
                for (Method m : ms) {
                    if (m.getName().equals("of") && m.getParameterCount() == 1 && m.getParameterTypes()[0] == color.getClass()) {
                        try {
                            s = s.replace(color, m.invoke(null, color) + "");
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        matcher = Main.hexpattern.matcher(s);
                    }
                }

            }
        }
        return s;
    }


    public String GameStateToString(GameState gameState) {
        switch (gameState) {
            case STARTED:
            case FINISHED:
                return MSG_STATE_STARTED;
            case WAITING:
                return MSG_STATE_WAITING;
            case STARTING:
                return MSG_STATE_STARTING;
        }
        return MSG_STATE_WAITING;
    }

    public String booleantoString(Boolean bo) {
        if (bo) {
            return YES;
        } else {
            return NO;
        }
    }

    public static Object getStaticMessages(String MESSAGE) {
        try {
            return StaticMessages.class.getField(MESSAGE).get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

}
