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

package fr.CHOOSEIT.elytraracing.gamesystem;

import fr.CHOOSEIT.elytraracing.GUI.Pages.ParticleGameChoosePage;
import fr.CHOOSEIT.elytraracing.PlayerInformation.GamePlayer;
import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import fr.CHOOSEIT.elytraracing.*;
import fr.CHOOSEIT.elytraracing.GUI.Pages.HostPage;
import fr.CHOOSEIT.elytraracing.SqlHandle.DataBase;
import fr.CHOOSEIT.elytraracing.addon.particle.file.ParticleConfig;
import fr.CHOOSEIT.elytraracing.addon.particle.PlayerParticle;
import fr.CHOOSEIT.elytraracing.api.elytraracing.event.*;
import fr.CHOOSEIT.elytraracing.api.elytraracing.viewer.ERGame;
import fr.CHOOSEIT.elytraracing.event.PlayerInteractEvent;
import fr.CHOOSEIT.elytraracing.gamesystem.Games.GrandPrixMode;
import fr.CHOOSEIT.elytraracing.gamesystem.Games.Training;
import fr.CHOOSEIT.elytraracing.gamesystem.timer.AbstractGameTimer;
import fr.CHOOSEIT.elytraracing.gamesystem.timer.GameTimer;
import fr.CHOOSEIT.elytraracing.gamesystem.timer.InfiniteGameTimer;
import fr.CHOOSEIT.elytraracing.gamesystem.timer.eTimer;
import fr.CHOOSEIT.elytraracing.mapsystem.ObjectClass;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.CheckPoint;
import fr.CHOOSEIT.elytraracing.mapsystem.DLV;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.MapCommand;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.End;
import fr.CHOOSEIT.elytraracing.mapsystem.linkplayer.LinkPlayer;
import fr.CHOOSEIT.elytraracing.optiHelper.McHelper;
import fr.CHOOSEIT.elytraracing.scoreboard.Board;
import fr.CHOOSEIT.elytraracing.scoreboard.BoardHandler;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


public abstract class Game implements ERGame {
    public static ArrayList<Game> gamelist = new ArrayList<>();

    protected CustomMessageConfig cmc = Main.customMessageConfig;

    private int maxplayer, minplayer;
    private boolean isOpen;

    private final eTimer mainTimer, showTimer, moveTimer, boardTimer, checkerTimer;

    private long started_time;
    private String name;
    private String UUID;
    private GameState gameState;
    private GameMoment gameMoment;
    private GameDurationType gameDurationType;
    private UUID host;
    private int playeratstartsize;
    private Map currentmap;
    private int CurrentMapIndex;
    private ArrayList<Map> maps;
    private int Alertsent = 0;
    private boolean interRounds = false;
    private String permission = null;

    private PlayerInformationSaver playerInformationSaver;

    private ArrayList<GamePlayer> Specs_Ends = new ArrayList<>();
    private TYPE_MODIFICATION modification;
    public ArrayList<Scoring> score;
    private SpecMouvement specMouvement;

    public int playerfinished = 0;

    protected ArrayList<Player> PlayerDNF = new ArrayList<>();
    private java.util.Map<UUID, GamePlayer> gamePlayerMap, gameSpectatorsMap;


    public static boolean ContainsPlayerGAMELIST(Player player, PlayerMode playerMode) {
        for (Game game : gamelist) {
            if (game.ContainsPlayer(player, playerMode)) {
                return true;
            }
        }
        return false;
    }

    public static Game Find(Player player, PlayerMode playerMode) {
        for (Game game : gamelist) {
            if (game.ContainsPlayer(player, playerMode)) {
                return game;
            }
        }
        return null;
    }

    public static Game Find(Player player) {
        return Find(player, PlayerMode.ALIVE);
    }

    public static Game Find(String name) {
        for (Game game : gamelist) {
            if (game.name.equalsIgnoreCase(name)) {
                return game;
            }
        }
        return null;
    }

    public static boolean NameAlreadyExist(String name) {
        return Find(name) != null;
    }

    public Game(String name, GameDurationType gameDurationType, int maxplayer, int minplayer, Player host, ArrayList<Map> maps) {
        this.started_time = -1;
        this.maxplayer = maxplayer;
        this.minplayer = minplayer;
        this.name = name;
        this.gameDurationType = gameDurationType;
        this.isOpen = !gameDurationType.equals(GameDurationType.HOSTDURATION);
        this.modification = modification;
        this.gamePlayerMap = new HashMap<UUID, GamePlayer>();
        this.gameSpectatorsMap = new HashMap<UUID, GamePlayer>();

        if (maps.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.CurrentMapIndex = 0;
        this.currentmap = maps.get(this.CurrentMapIndex);
        this.maps = maps;

        this.host = host != null ? host.getUniqueId() : null;
        gameState = GameState.WAITING;
        this.UUID = java.util.UUID.randomUUID().toString();

        this.gameMoment = GameMoment.NONE;

        mainTimer = initMainTimer();
        showTimer = initShowTimer();
        moveTimer = initMoveTimer();
        boardTimer = initBoardTimer();
        checkerTimer = initCheckerTimer();

        gamelist.add(this);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getUUID() {
        return UUID;
    }

    public static enum TYPE_MODIFICATION {
        DEFAULT
    }

    public String getName() {
        return name;
    }

    public void askStartTimer(){
        mainTimer.start();
    }
    public void askCancelTimer(){
        mainTimer.cancel();
    }
    public void askCancelCheckerTimer(){
        checkerTimer.cancel();
    }

    public eTimer initBoardTimer(){
        int reloadInterval = (int) Main.scoreboardConfig.TickReloadInterval;
        if (reloadInterval <= 0) {
            reloadInterval = 20;
        }
        return new InfiniteGameTimer(reloadInterval) {
            @Override
            protected void event(int timeLeft) {
                super.event(timeLeft);
                if (Main.scoreboardConfig.isEnable()) {
                    updateBoard();
                }
            }
        };
    }
    public long getCurrentDeltaTime(){
        return started_time <= 0 ? 0 : System.currentTimeMillis() - started_time;
    }

    public eTimer initCheckerTimer(){
        return new InfiniteGameTimer(17L) {
            @Override
            protected void event(int timeLeft) {
                super.event(timeLeft);
                if (System.currentTimeMillis() - started_time > getCurrentmap().getMaxTime()) {
                    checkerTimer.cancel();
                    for (GamePlayer player : new ArrayList<>(getGamePlayers())) {
                        if (!player.finishedPlayingMap()) {
                            EndMap(player, false);
                        }
                    }
                } else {
                    String msg = cmc.basicsettingnoprefix(cmc.ACTIONBAR_TIME.replace("{TIME}", Utils.timediffToString(currentmap.getMaxTime() - (System.currentTimeMillis() - started_time), false)), null);
                    getPlayersAbleToSee().forEach(player -> Utils.sendActionbar(player.getSpigotPlayer(), msg));
                }
            }

            @Override
            protected void earlyCancelEvent() {
                super.earlyCancelEvent();
                getPlayersAbleToSee().forEach(player -> Utils.sendActionbar(player.getSpigotPlayer(), " "));
            }
        };
    }

    public eTimer initShowTimer(){
        if (cmc.RENDER_DISTANCE_CP) {
            return new InfiniteGameTimer(cmc.RELOADINTERVALOBJECTS) {
                @Override
                protected void event(int timeLeft) {
                    super.event(timeLeft);
                    for (GamePlayer player : getGamePlayers()) {
                        ArrayList<Integer> primaryCPToShow = new ArrayList<>();
                        ArrayList<Integer> lastCpPassed = getPlayerInformationSaver().getInfoSave(player.getSpigotPlayer()).getPassedCPMultiLaps();
                        for (int i = 0; i < cmc.RENDER_DISTANCE_CP_VALUE; i++) {
                            if (i == lastCpPassed.size()) {
                                break;
                            }
                            primaryCPToShow.add(lastCpPassed.get(lastCpPassed.size() - i - 1));
                        }
                        int nextCp;
                        for (int i = 0; i < cmc.RENDER_DISTANCE_CP_VALUE; i++) {
                            nextCp = getNextCp(player, i + 1);
                            if (!primaryCPToShow.contains(nextCp) && nextCp != -1) {
                                primaryCPToShow.add(nextCp);
                            }
                        }
                        currentmap.ShowCheckpoints(player.getSpigotPlayer(), Game.this, primaryCPToShow, getCurrentDeltaTime());
                    }
                    getGameSpectators().forEach(s -> currentmap.ShowEverything(s.getSpigotPlayer(), true, getCurrentDeltaTime()));
                }
            };
        }
        else
        {
            return new InfiniteGameTimer(15) {
                @Override
                protected void event(int timeLeft) {
                    super.event(timeLeft);
                    getGamePlayers().forEach(p -> currentmap.ShowCheckpoints(p.getSpigotPlayer(), Game.this, null, getCurrentDeltaTime()));
                    getGameSpectators().forEach(s -> currentmap.ShowEverything(s.getSpigotPlayer(), true, getCurrentDeltaTime()));
                }
            };
        }
    }

    public eTimer initMoveTimer(){
        return new InfiniteGameTimer(100) {
            @Override
            protected void event(int timeLeft) {
                super.event(timeLeft);
                Float min_dis = 999999999999999f;
                Float co_dis;
                GamePlayer min_player = null;
                for (GamePlayer spectator : getGameSpectators()) {
                    min_dis = -1f;
                    for (GamePlayer player : getGamePlayers()) {
                        if (!getPlayerInformationSaver().hasFinished(player.getSpigotPlayer())) {
                            if (min_dis == -1) {
                                min_dis = (float) Utils.distance(spectator.getLocation(), player.getLocation());
                                min_player = player;
                            } else {
                                co_dis = (float) Utils.distance(spectator.getLocation(), player.getLocation());
                                if (co_dis < min_dis) {
                                    min_dis = co_dis;
                                    min_player = player;
                                }
                            }
                        }
                    }
                    if (min_player != null && min_dis > cmc.AutoTP_Spec_Distance) {
                        spectator.teleport(min_player);
                    }
                    getSpecMouvement().refreshMouvement(spectator.getSpigotPlayer());

                }
                for (GamePlayer player : Specs_Ends.stream().filter(GamePlayer::isPlaying).collect(Collectors.toList())) {
                    min_dis = -1f;
                    for (GamePlayer player1 : getGamePlayers()) {
                        if (!getPlayerInformationSaver().hasFinished(player1.getSpigotPlayer())) {
                            if (min_dis == -1) {
                                min_dis = (float) Utils.distance(player.getLocation(), player1.getLocation());
                                min_player = player1;
                            } else {
                                co_dis = (float) Utils.distance(player.getLocation(), player1.getLocation());
                                if (co_dis < min_dis) {
                                    min_dis = co_dis;
                                    min_player = player1;
                                }
                            }
                        }

                    }
                    if (min_player != null && min_dis > cmc.AutoTP_Spec_Distance) {
                        player.teleport(min_player.getSpigotPlayer());
                    }
                    getSpecMouvement().refreshMouvement(player.getSpigotPlayer());

                }
            }
        };
    }

    public eTimer initMainTimer(){
        int septimer = 0;
        if (gameDurationType == GameDurationType.SERVERDURATION)
            septimer = cmc.TimerStart;
        else if (gameDurationType == GameDurationType.HOSTDURATION)
            septimer = cmc.TimerStart_host;

        return new GameTimer(septimer + 1,20L) {

            @Override
            public void earlyCancelEvent() {
                if (gameState == GameState.STARTING) {
                    gameState = GameState.WAITING;
                }
            }

            @Override
            public void startEvent() {
                gameState = GameState.STARTING;
            }

            @Override
            public void event(int timeLeft) {
                if(cmc.MSG_STEP_TIMER.containsKey(timeLeft)){
                    String message = cmc.basicsetting(cmc.MSG_STEP_TIMER.get(timeLeft), null);
                    getPlayersAbleToSee().forEach(ps -> ps.sendMessage(message));
                }
            }

            @Override
            public void endEvent() {
                Start();
            }
        };
    }

    public void UpdateHeader() {
    }

    public void quitSpec(Game game, Player player) {
        GamePlayer gamePlayer = getGamePlayer(player.getUniqueId());
        if (game.Specs_Ends.contains(gamePlayer)) {
            game.Specs_Ends.remove(gamePlayer);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(game.getCurrentmap().getLocation_end());
        }
    }

    public void Start(GamePlayer p) {
        p.teleport(currentmap.getLocation_start());
        getPlayerInformationSaver().clear(p.getSpigotPlayer());
        getPlayerInformationSaver().setBackCP(p.getSpigotPlayer(), false);
    }

    public void giveFireworksStart(GamePlayer p) {
        int fireworkstoadd = getCurrentmap().getFireworks();
        if (fireworkstoadd > 64)
            fireworkstoadd = 64;
        if (fireworkstoadd < 0)
            fireworkstoadd = 0;

        if (fireworkstoadd > 0)
            p.getSpigotPlayer().getInventory().setItem(getSlot(cmc.ITEM_FIREWORK_SLOT, 2), new ItemStack(Material.getMaterial("FIREWORK"), fireworkstoadd));
    }

    public PlayerInformationSaver getPlayerInformationSaver() {
        if (playerInformationSaver == null)
            verifyPlayerInformation();
        return playerInformationSaver;
    }

    public void Interact(Player player, PlayerInteractEvent.InteractAction interactAction, Game game, ItemStack itemStack) {
        GamePlayer gamePlayer = getGamePlayer(player.getUniqueId());
        switch (interactAction) {
            case DNF:
                game.Interact_DNF(gamePlayer, itemStack);
                break;
            case BED:
                game.Interact_Leave(gamePlayer, itemStack);
                break;
            case ONECP:
                game.Interact_1CPBack(gamePlayer, itemStack);
                break;
            case TWOCP:
                game.Interact_2CPBack(gamePlayer, itemStack);
                break;
            case RESTART:
                game.Interact_Restart(gamePlayer, itemStack);
                break;
            case SPEC:
                game.Interact_Spec(gamePlayer, itemStack);
                break;
            case HOST:
                game.Interact_Host(gamePlayer, itemStack);
                break;
            case PARTICLE:
                game.Interact_Particle(gamePlayer, itemStack);
                break;
        }
    }

    public boolean back1CP(Player player) {
        return back1CP(getGamePlayer(player.getUniqueId()));
    }

    public boolean back1CP(GamePlayer player) {

        DLV playerdlv = getPlayerInformationSaver().getFromEnd(player.getSpigotPlayer(), 1);
        if (playerdlv != null) {
            if(currentmap.isNO_FAIL())
            {
                playerRestart(player);
                return false;
            }
            playerdlv.Set(player.getSpigotPlayer(), this, next_checkpoint(player));
            return true;
        }
        return false;
    }

    public boolean back2CP(Player player) {
        return back2CP(getGamePlayer(player.getUniqueId()));
    }

    public boolean back2CP(GamePlayer player) {
        DLV playerdlv = getPlayerInformationSaver().getFromEnd(player.getSpigotPlayer(), 2);
        if (playerdlv != null) {
            if(currentmap.isNO_FAIL())
            {
                playerRestart(player);
                return false;
            }
            playerdlv.Set(player.getSpigotPlayer(), this, next_checkpoint2(player));
            return true;
        }
        return false;
    }

    protected void verifyPlayerInformation() {
        if (playerInformationSaver != null) {
            playerInformationSaver.delete();
        }
        playerInformationSaver = new PlayerInformationSaver(this);
    }

    protected void sendMapInformations(GamePlayer p, Map m) {
        sendMapDifficulty(p, m);
        if (m.getNumberoflaps() > 1) {
            sendMapLaps(p, m);
        }

    }

    protected void sendMapLaps(GamePlayer p, Map m) {
        p.sendMessage(m.getlapsMessage());
    }

    protected void sendMapDifficulty(GamePlayer p, Map m) {
        if (cmc.MAP_DIFICULTY) {
            p.sendMessage(cmc.basicsetting(cmc.MAP_DIFFICULTY_MESSAGE.replace("{DIFFICULTY}", m.getDifficultyMSG()), p.getSpigotPlayer()));
        }
    }

    protected void setupPlayer(GamePlayer p) {
        p.teleport(currentmap.getLocation_start());
        p.getSpigotPlayer().getInventory().clear();
        p.setGameMode(GameMode.ADVENTURE);
    }

    protected void giveStartItems(GamePlayer p) {
        ItemStack ELYTRA = Utils.getItemStack("ELYTRA", Arrays.asList("ELYTRA:0"), "", p.getName());
        if(ELYTRA != null) {
            ItemMeta itemMeta = null;
            if(!ELYTRA.hasItemMeta()){
                Material m = Utils.getMaterial("ELYTRA");
                if(m != null)
                    itemMeta = Bukkit.getItemFactory().getItemMeta(m);
                else{
                    m = Utils.getMaterial("ELYTRA:0");
                    if(m != null)
                        itemMeta = Bukkit.getItemFactory().getItemMeta(m);
                }
            }
            else{
                itemMeta = ELYTRA.getItemMeta();

            }
            if(itemMeta != null){
                itemMeta.addEnchant(Enchantment.DURABILITY, 32000, true);
                if (Main.cmc().ELYTRA_MODELDATA >= 0) {
                    Utils.setCustomModelData(itemMeta, Main.cmc().ELYTRA_MODELDATA);
                }

                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                ELYTRA.setItemMeta(itemMeta);
            }
            if (!cmc.DISABLE_ELYTRA) {
                p.getInventory().setChestplate(ELYTRA);
            }
        }

        p.getInventory().setItem(getSlot(cmc.ITEM_1CP_BACK_SLOT, 0), Utils.getItemStack(cmc.ITEM_1CP_BACK_MATERIAL, Arrays.asList("REDSTONE"), cmc.basicsettingnoprefix(cmc.ITEM_1CP_BACK, null), p.getName()));
        giveFireworksStart(p);
        p.getInventory().setItem(getSlot(cmc.ITEM_CUSTOM_SLOT, 3), Utils.getItemStack(cmc.ITEM_CUSTOM_MATERIAL, Arrays.asList("AIR"), cmc.basicsettingnoprefix(cmc.ITEM_CUSTOM, null), p.getName()));
        p.getInventory().setItem(getSlot(cmc.ITEM_2CP_BACK_SLOT, 1), Utils.getItemStack(cmc.ITEM_2CP_BACK_MATERIAL, Arrays.asList("REDSTONE_BLOCK"), cmc.basicsettingnoprefix(cmc.ITEM_2CP_BACK, null), p.getName()));
        p.getInventory().setItem(getSlot(cmc.ITEM_RESTART_SLOT, 4), Utils.getItemStack(cmc.ITEM_RESTART_MATERIAL, Arrays.asList("LEGACY_PISTON_BASE", "PISTON_BASE", "PISTON"), cmc.basicsettingnoprefix(cmc.ITEM_RESTART, null), p.getName()));

        p.getInventory().setItem(getSlot(cmc.ITEM_DNF_SLOT, 8), Utils.getItemStack(cmc.ITEM_DNF_MATERIAL, Arrays.asList("COMPARATOR", "REDSTONE_COMPARATOR", "LEGACY_REDSTONE_COMPARATOR_OFF"), cmc.basicsettingnoprefix(cmc.ITEM_DNF, null), p.getName()));
    }

    public List<GamePlayer> getGamePlayers() {
        return getGamePlayers(true);
    }
    public int numberOfPlayers(){
        return getGamePlayers().size();
    }

    public List<GamePlayer> getGamePlayers(boolean forcealive) {
        return new ArrayList<>(gamePlayerMap.values().stream().filter(player -> player.getSpigotPlayer() != null && (!forcealive | player.isPlaying())).collect(Collectors.toList()));
    }

    public List<GamePlayer> getGameSpectators() {
        return new ArrayList<>(gameSpectatorsMap.values());
    }

    protected void startMoment() {
        getGamePlayers().forEach(p -> p.mutualShow(getGamePlayers()));
    }

    public void addCurrentMapIndex(int value) {
        CurrentMapIndex += value;
    }

    public void setNewMap(int value) {
        this.currentmap = this.getMaps().get(value);
    }

    public void executeCommandForAllPlayers(){}

    public void Start() {
        Bukkit.getPluginManager().callEvent(new ElytraRacingRaceStartEvent(this));

        verifyPlayerInformation();
        gameMoment = GameMoment.STARTINGCANTMOVE;
        gameState = GameState.STARTED;
        Specs_Ends.clear();
        getGamePlayers().forEach(GamePlayer::resetValuesRound);
        UpdateHeader();

        getGamePlayers().forEach(player -> {
            sendMapInformations(player, currentmap);
            setupPlayer(player);
            giveStartItems(player);
            PlayerParticle.start(player.getSpigotPlayer());

            if (MapCommand.ShowMapCP.containsKey(player)) {
                Bukkit.getScheduler().cancelTask(MapCommand.ShowMapCP.get(player));
                MapCommand.ShowMapCP.remove(player);
            }
        });
        getGameSpectators().forEach(gp -> {
            Player player = gp.getSpigotPlayer();
            getPlayerInformationSaver().finish(player, 0, -1);
            player.teleport(currentmap.getLocation_start());
        });
        showTimer.start();
        moveTimer.start();
        for (GamePlayer player : getPlayersAbleToSee()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*Main.cmc().TIMER_MAPSTART_START, 0, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*Main.cmc().TIMER_MAPSTART_START, 201, false));
        }
        playeratstartsize = gamePlayerMap.size();
        getGamePlayers().forEach(p -> {
            p.mutualHide(getGamePlayers());
            p.checkAtStart();
        });

        interRounds = false;
        new AbstractGameTimer(Main.cmc().TIMER_MAPSTART_START + 2   + 1, 20L) {
            @Override
            protected void event(int timeLeft) {
                if(Game.this == null) cancel();

                if(timeLeft != 0){
                    String title = cmc.basicsettingnoprefix(cmc.MSG_STEP_TIMER_MAPSTART_TITLE.getOrDefault(timeLeft-1, ""), null);
                    String subTitle = cmc.basicsettingnoprefix(cmc.MSG_STEP_TIMER_MAPSTART_SUBTITLE.getOrDefault(timeLeft-1, ""), null);
                    getPlayersAbleToSee().forEach(player -> Utils.SendTitle(player.getSpigotPlayer(), title, subTitle, 0, 20, 0));
                    switch (timeLeft - 1){
                        case 3:
                            getPlayersAbleToSee().forEach(player -> Utils.playSoundDispatcher(player.getSpigotPlayer(), getCurrentmap().getLocation_start(), cmc.SOUND_START_3, "BLOCK_NOTE_BASS", cmc.SOUND_START_3, "minecraft:block.note_block.bass", cmc.SOUND_START_3_VOLUME, cmc.SOUND_START_3_PITCH));
                            break;
                        case 2:
                            getPlayersAbleToSee().forEach(player -> Utils.playSoundDispatcher(player.getSpigotPlayer(), getCurrentmap().getLocation_start(), cmc.SOUND_START_2, "BLOCK_NOTE_BASS", cmc.SOUND_START_2, "minecraft:block.note_block.bass", cmc.SOUND_START_2_VOLUME, cmc.SOUND_START_2_PITCH));
                            break;
                        case 1:
                            getPlayersAbleToSee().forEach(player -> Utils.playSoundDispatcher(player.getSpigotPlayer(), getCurrentmap().getLocation_start(), cmc.SOUND_START_1, "BLOCK_NOTE_BASS", cmc.SOUND_START_1, "minecraft:block.note_block.bass", cmc.SOUND_START_1_VOLUME, cmc.SOUND_START_1_PITCH));
                            break;
                        case 0:
                            getPlayersAbleToSee().forEach(p -> Utils.playSoundDispatcher(p.getSpigotPlayer(), getCurrentmap().getLocation_start(), cmc.SOUND_START_START, "BLOCK_NOTE_PLING", cmc.SOUND_START_START, "minecraft:block.note_block.pling", cmc.SOUND_START_START_VOLUME, cmc.SOUND_START_START_PITCH));
                            getGamePlayers().forEach(p -> p.teleport(getCurrentmap().getLocation_start()));
                            executeCommandForAllPlayers();
                            started_time = System.currentTimeMillis();
                            checkerTimer.start();
                            gameMoment = GameMoment.NONE;
                            break;
                    }
                }
                else{
                    startMoment();
                }
            }
        }.start();
    }

    public GameMoment getGameMoment() {
        return gameMoment;
    }

    public void privateUpdateBoard(ArrayList<GamePlayer> ps, ScoreboardConfig.ScoreBoardSaver scoreBoardSaver) {
        Board b;
        ArrayList<Scoring> cpscore = gameState.equals(GameState.STARTED) || gameState.equals(GameState.FINISHED) && !interRounds ? RaceRank(false) : null;
        ArrayList<Scoring> globalscore = gameState.equals(GameState.STARTED) || gameState.equals(GameState.FINISHED) ? GlobalRank(false, false) : null;
        List<String> modified = modifyListStringGlobal(scoreBoardSaver.getLines(), cpscore, globalscore);
        String title = ".";
        if (ps != null) {
            for (GamePlayer player : ps) {
                title = modifyListString(Arrays.asList(scoreBoardSaver.getTitle()), player, cpscore, globalscore).get(0);
                title = modifyListStringGlobal(Arrays.asList(title), cpscore, globalscore).get(0);
                b = BoardHandler.getBoard(player.getSpigotPlayer().getUniqueId(), title);
                b.setLines(modifyListString(new ArrayList<>(modified), player, cpscore, globalscore));
            }
        }
    }

    public void updateBoard() {
    }

    public void updatePrivateBoardSaver(ScoreboardConfig.ScoreBoardSaver scoreBoardSaver, ArrayList<GamePlayer> ps) {
        if (!(scoreBoardSaver == null || scoreBoardSaver.getTitle() == null || scoreBoardSaver.getLines() == null)) {
            privateUpdateBoard(ps, scoreBoardSaver);
        }
    }

    public List<String> modifyListStringGlobal(List<String> list, ArrayList<Scoring> cpscore, ArrayList<Scoring> globalscore) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, boardReplacerGlobal(list.get(i), cpscore, globalscore));
        }
        return list;
    }

    public List<String> modifyListString(List<String> list, GamePlayer p, ArrayList<Scoring> cpscore, ArrayList<Scoring> globalscore) {
        if (Main.hookPlaceHolderAPI) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, PlaceholderAPI.setPlaceholders(p.getSpigotPlayer(), list.get(i)));
            }
        }
        for (int i = 0; i < list.size(); i++) {
            list.set(i, boardReplacer(list.get(i), p, cpscore, globalscore));
        }
        return list;
    }

    @Override
    public ArrayList<Scoring> getRaceRank() {
        return RaceRank(false);
    }

    public ArrayList<Scoring> RaceRank(boolean finishedMoment) {
        if (this instanceof Training) {
            return null;
        }
        ArrayList<Scoring> cpscore = new ArrayList<>();
        int n = 0;
        int maxpossiblen = currentmap.getMaxPossibleN();
        PlayerInformationSaver.PlayerInformationInstance playerInformationInstance;
        for (GamePlayer player : getGamePlayers()) {
            playerInformationInstance = getPlayerInformationSaver().getInfoSave(player.getSpigotPlayer());
            if (playerInformationInstance.hasFinished() || finishedMoment) {
                n = playerInformationInstance.getFinished_TCP() + playerInformationInstance.getFinished_LAPS();
            } else {
                n = playerInformationInstance.getTotalCPPassed() + playerInformationInstance.getFinishedlaps();
            }

            if (playerInformationInstance.getFinished_TIME() > 0) {
                n = maxpossiblen - playerInformationInstance.getIdFinished();
            }
            cpscore.add(new Scoring(player.getSpigotPlayer(), n));
        }
        Collections.sort(cpscore, Collections.reverseOrder());
        return cpscore;
    }

    public String globalrankReplacerGlobal(String s, ArrayList<Scoring> globalscore) {
        if (globalscore == null) {
            return s;
        }
        for (int i = 1; i <= 15; i++) {
            if (i > globalscore.size()) {
                s = s.replace("{RANK_PLAYER_" + i + "}", Main.cmc().NOBODY);
                s = s.replace("{RANK_PLAYER_" + i + "_SCORE}", "0");
                continue;
            }
            s = s.replace("{RANK_PLAYER_" + i + "}", globalscore.get(i - 1).player.getName() + "");
            s = s.replace("{RANK_PLAYER_" + i + "_SCORE}", globalscore.get(i - 1).value + "");
        }
        return s;
    }

    public String rankReplacerGlobal(String s, ArrayList<Scoring> cpscore) {
        if (cpscore == null) {
            return s;
        }
        for (int i = 1; i <= 15; i++) {
            if (i > cpscore.size()) {
                s = s.replace("{RACE_RANK_PLAYER_" + i + "}", Main.cmc().NOBODY);
                s = s.replace("{RACE_RANK_PLAYER_" + i + "_CPS}", "0");
                s = s.replace("{RACE_RANK_PLAYER_" + i + "_LAPS}", "0");
                continue;
            }
            s = s.replace("{RACE_RANK_PLAYER_" + i + "}", cpscore.get(i - 1).player.getName() + "");
            if (getPlayerInformationSaver().hasFinished(cpscore.get(i - 1).player)) {
                s = s.replace("{RACE_RANK_PLAYER_" + i + "_CPS}", " ");
                s = s.replace("{RACE_RANK_PLAYER_" + i + "_LAPS}", " ");
                continue;
            }
            s = s.replace("{RACE_RANK_PLAYER_" + i + "_CPS}", getPlayerInformationSaver().getNumberCPPassed(cpscore.get(i - 1).player) + "");
            s = s.replace("{RACE_RANK_PLAYER_" + i + "_LAPS}", (getPlayerInformationSaver().getFinishedlaps(cpscore.get(i - 1).player) + 1) + "");
        }
        return s;
    }

    public String boardReplacerGlobal(String s, ArrayList<Scoring> cpscore, ArrayList<Scoring> globalscore) {
        int needed_players = minplayer - getGamePlayers().size();

        s = rankReplacerGlobal(s, cpscore);
        s = globalrankReplacerGlobal(s, globalscore);
        s = CustomMessageConfig.hexColor(s);
        if (this instanceof Training) {
            int player_id_best = 0;
            int map_id = Main.currentDataBase.get_map_id(currentmap);
            String name_best = Main.cmc().NONE;
            String time_best = Utils.timediffToString((long) 0);
            Connection connection = Main.currentDataBase.getSqlConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT pr.player_id as id_best, pi.player_username as username_best FROM player_records as pr INNER JOIN playerinfo as pi WHERE map_id = " + map_id + " AND pi.player_id = pr.player_id ORDER BY player_time ASC LIMIT 1");
                ps.execute();
                ResultSet rs = ps.getResultSet();
                if (rs.next()) {
                    player_id_best = rs.getInt("id_best");
                    name_best = rs.getString("username_best");
                }
                rs.close();
                ps.close();
                if (player_id_best > 0) {
                    DataBase.Playerinfos pi = DataBase.Playerinfos.getPlayerInfos(player_id_best);
                    if (pi != null) {
                        DataBase.PlayerMapinfos e = pi.getMap(currentmap.uuid.toString());
                        if (e != null) {
                            time_best = Utils.TimediffToStringCustom(e.player_time, cmc.basicsettingnoprefix(cmc.REPLAY_FORMAT_16MAXCHAR, null));

                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            s = s
                    .replace("{MAP_WR_TIME}", time_best)
                    .replace("{MAP_WR_PLAYER}", name_best)
            ;
        }

        return s
                .replace("{CURRENT_PLAYER}", getGamePlayers().size() + "")
                .replace("{MAX_PLAYER}", maxplayer + "")
                .replace("{MIN_PLAYER}", isHosted() ? 0 + "" : minplayer + "")
                .replace("{NEEDED_PLAYER}", needed_players >= 0 ? needed_players + "" : 0 + "")
                .replace("{CURRENT_MAP}", currentmap.getName())
                .replace("{NEXT_MAP}", getNextMapName())
                .replace("{CURRENT_MAP_DIFFICULTY}", currentmap.getDifficultyMSG())
                .replace("{NEXT_MAP_DIFFICULTY}", getNextMapDifficulty())
                .replace("{STARTING_COUNTDOWN}", gameState.equals(GameState.STARTING) ? mainTimer.getTimerLeft() + "" : 0 + "")
                .replace("{TIME_LEFT}", started_time == 0 || started_time == -1 ? Utils.timediffToString(currentmap.getMaxTime(), false) : Utils.timediffToString(Math.max(0, currentmap.getMaxTime() - (System.currentTimeMillis() - started_time)), false))
                .replace("{MAX_LAPS}", "" + currentmap.getNumberoflaps())
                .replace("{MAX_CPS}", "" + currentmap.getCpNumber())
                .replace("{CURRENT_ROUND}", "" + (getCurrentMapIndex() + 1))
                .replace("{MAX_ROUND}", "" + maps.size())
                .replace("{HOST_NAME}", isHosted() ? name : "ElytraRacing")
                .replace("{HOST}", isHosted() ? (host != null ? getGameHost().getSpigotPlayer().getName() : Main.cmc().CONSOLE_USERNAME) : Main.cmc().NONE)

                .replace("{LINK}", Main.scoreboardConfig.link != null ? Main.cmc().basicsettingnoprefix(Main.scoreboardConfig.link) : "")
                .replace("&", "§")
                ;
    }

    public GamePlayer getGameHost() {
        return getGamePlayer(host);
    }

    public boolean isHost(Player p) {
        if (!isHosted()) {
            return false;
        }
        return getGameHost().equals(getGamePlayer(p.getUniqueId()));
    }

    protected GamePlayer getGamePlayer(UUID uuid) {
        return gamePlayerMap.get(uuid);
    }

    public String getNextMapName() {
        return cmc.NONE;
    }

    public String getNextMapDifficulty() {
        return cmc.NONE;
    }

    public int PersonalRaceRank(Player p, ArrayList<Scoring> cpscore) {
        return PersonalRaceRank(getGamePlayer(p.getUniqueId()), cpscore);
    }

    public int PersonalRaceRank(GamePlayer p, ArrayList<Scoring> cpscore) {
        if (cpscore == null) {
            return 0;
        }
        int n = 1;
        for (Scoring scoring : cpscore) {

            if (scoring.player == p.getSpigotPlayer()) {
                return n;
            }
            n++;
        }
        return 0;
    }

    public String boardReplacer(String s, GamePlayer gamePlayer, ArrayList<Scoring> cpscore, ArrayList<Scoring> globalscore) {
        Player p = gamePlayer.getSpigotPlayer();
        int player_id = Main.currentDataBase.get_player_id(gamePlayer.getSpigotPlayer());
        long map_pb = 0;
        long map_rank = 0;
        long map_winrate = 0;
        long next_map_winrate = 0;
        long next_map_rank = 0;
        long next_map_pb = 0;
        if (player_id > 0) {
            DataBase.Playerinfos infos = DataBase.Playerinfos.getPlayerInfos(player_id);
            if (infos.getMap(currentmap.getUUID()) != null) {
                DataBase.PlayerMapinfos mapinfo = infos.getMap(currentmap.getUUID());
                map_pb = mapinfo.player_time;
                map_rank = mapinfo.ranking;
                map_winrate = mapinfo.winrate;
            }
            if (getCurrentMapIndex() + 1 < maps.size() && infos.getMap(maps.get(getCurrentMapIndex() + 1).getUUID()) != null) {
                DataBase.PlayerMapinfos mapinfo = infos.getMap(maps.get(getCurrentMapIndex() + 1).getUUID());
                next_map_pb = mapinfo.player_time;
                next_map_rank = mapinfo.ranking;
                next_map_winrate = mapinfo.winrate;
            }
        }
        if (gameState.equals(GameState.STARTED) || gameState.equals(GameState.FINISHED)) {

            PlayerInformationSaver.PlayerInformationInstance playerInformationInstance = getPlayerInformationSaver().getInfoSave(p);
            s = s
                    .replace("{SELF_LAPS}", "" + (playerInformationInstance.getFinishedlaps() + 1))
                    .replace("{SELF_CPS}", "" + (playerInformationInstance.getNumberCPPassed()))
                    .replace("{SELF_RACE_RANK_PLAYER}", PersonalRaceRank(p, cpscore) + "")
                    .replace("{SELF_RANK_PLAYER}", PersonalRaceRank(p, globalscore) + "")
                    .replace("{SELF_RANK_PLAYER_SCORE}", String.valueOf(gamePlayer.getScore()))
                    .replace("&", "§");
        }

        return s

                .replace("{SELF_MAP_PB}", map_pb > 0 ? Utils.TimediffToStringCustom(map_pb, cmc.basicsettingnoprefix(cmc.basicsettingnoprefix(cmc.REPLAY_FORMAT_16MAXCHAR, null), null)) : "" + Main.cmc().NONE)
                .replace("{SELF_MAP_RANK}", map_pb > 0 ? "" + map_rank : "" + Main.cmc().NONE)
                .replace("{SELF_MAP_WINRATE}", map_pb > 0 ? map_winrate + "" : "" + Main.cmc().NONE)
                .replace("{SELF_NEXT_MAP_PB}", next_map_pb > 0 ? Utils.TimediffToStringCustom(next_map_pb, cmc.basicsettingnoprefix(cmc.REPLAY_FORMAT_16MAXCHAR, null)) : "" + Main.cmc().NONE)
                .replace("{SELF_NEXT_MAP_RANK}", next_map_pb > 0 ? "" + next_map_rank : "" + Main.cmc().NONE)
                .replace("{SELF_NEXT_MAP_WINRATE}", next_map_pb > 0 ? next_map_winrate + "" : "" + Main.cmc().NONE)
                .replace("&", "§")
                ;
    }

    public int getNextCp(GamePlayer player, int range) {
        ArrayList<Integer> integers = getPlayerInformationSaver().getPassedCP(player.getSpigotPlayer(), true);
        int skipped = 0;
        boolean found = false;
        int i = 0;
        for (i = 0; i < getCurrentmap().getCheckpointsList().size(); i++) {
            if (!integers.contains(i)) {
                ++skipped;
                if (range == skipped) {
                    found = true;
                    break;
                }
            }
        }
        return found ? i : -1;
    }

    public int getSlot(int slot, int defaultslot) {
        if (slot >= 0 && slot <= 8) {
            return slot;
        }
        return defaultslot;
    }

    public ArrayList<Map> getMaps() {
        return (ArrayList<Map>) maps.clone();
    }

    public int getCurrentMapIndex() {
        return CurrentMapIndex;
    }

    public void EndRound() {
        if (interRounds) {
            return;
        }

        if (gameState == GameState.STARTED) {
            OptiIncrementation_player_map_stats(getGamePlayer(getRanking().get(0).getUniqueId()), currentmap, "player_time_won", 1);
        }
        checkerTimer.cancel();
        started_time = -1;
        interRounds = true;
    }

    public abstract ArrayList<Scoring> GlobalRank(boolean finishedMoment, boolean refresh);

    public void ShowScoreRanking(boolean endMoment) {
        score = GlobalRank(true, endMoment);
        String MESSAGE = "";
        String InterMESSAGE = "";
        int end = cmc.RankingMaxPlayers;
        if (score.size() < end) {
            end = score.size();
        }
        for (int i = 0; i < end; i++) {
            if (cmc.MSG_PER_RANK_SCORE.containsKey(i + 1)) {
                InterMESSAGE = cmc.basicsettingnoprefix(cmc.MSG_PER_RANK_SCORE.get(i + 1), score.get(i).player);
                InterMESSAGE = InterMESSAGE.replace("{PLAYER}", score.get(i).player.getName()).replace("{SCORE}", Integer.toString(score.get(i).value)).replace("{RANKING}", Integer.toString(i + 1));
                MESSAGE += InterMESSAGE + "\n";
            }

        }
        final String FinalMessage = MESSAGE.toString();
        getPlayersAbleToSee().forEach(player -> player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_PRE_SCORE, null)));
        getPlayersAbleToSee().forEach(player -> player.sendMessage(FinalMessage));
        String msg = "";
        if (end < score.size()) {
            for (int i = end - 1; i < score.size(); i++) {
                if (PlayerModeof(score.get(i).player) == PlayerMode.ALIVE) {
                    msg = cmc.basicsettingnoprefix(cmc.MSG_YOURSCORE, score.get(i).player).replace("{RANK}", Integer.toString(i + 1)).replace("{SCORE}", Integer.toString(score.get(i).value));
                    score.get(i).player.sendMessage(msg);
                }
            }
        }

    }

    public boolean isPlaying(Player player) {
        GamePlayer gamePlayer = getGamePlayer(player.getUniqueId());
        if (gamePlayer == null) {
            return false;
        }
        return gamePlayer.isPlaying();
    }

    public void Command_Execution() {
        if (gameDurationType == GameDurationType.HOSTDURATION && !cmc.COMMAND_EXECUTION_PER_RANK_IN_HOST) {
            return;
        }
    }

    public void showRanking() {
        ArrayList<Scoring> rank = RaceRank(true);
        String MESSAGE = "";
        String InterMESSAGE = "";
        int rankid = 0;
        int maxp = cmc.RankingMaxPlayers;
        long time;
        for (Scoring scoring : rank) {
            rankid++;

            if (rankid > maxp) {
                break;
            }
            if (cmc.MSG_PER_RANK_TIME.containsKey(rankid)) {
                InterMESSAGE = cmc.basicsettingnoprefix(cmc.MSG_PER_RANK_TIME.get(rankid), null);
                InterMESSAGE = InterMESSAGE.replace("{PLAYER}", scoring.player.getName()).replace("{RANKING}", Integer.toString(rankid));
                time = getPlayerInformationSaver().getFinished_TIME(scoring.player);
                if (time > 0) {
                    InterMESSAGE = InterMESSAGE.replace("{ARG_RANK_TIME}", cmc.basicsettingnoprefix(cmc.MSG_ARG_RANK_TIME_TIME.replace("{TIME}", Utils.timediffToString(time)), null));
                } else {
                    InterMESSAGE = InterMESSAGE.replace("{ARG_RANK_TIME}", cmc.basicsettingnoprefix(cmc.MSG_ARG_RANK_TIME_DNF, null));
                }
                MESSAGE += InterMESSAGE + "\n";
            }
        }

        final String FinalMessage = MESSAGE.toString();
        getPlayersAbleToSee().forEach(player -> player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_PRE_RANK, null)));
        getPlayersAbleToSee().forEach(player -> player.sendMessage(FinalMessage));
    }

    public ArrayList<Player> getRanking() {
        ArrayList<Player> ranking = new ArrayList<>();
        ArrayList<Scoring> racerank = RaceRank(true);
        racerank.forEach(o -> ranking.add(o.player));
        return ranking;
    }

    public boolean EndVerified(Player player) {
        return EndVerified(getGamePlayer(player.getUniqueId()));
    }

    public boolean EndVerified(GamePlayer player) {
        return EndMap(player, true);
    }

    public void OptiIncrementation(GamePlayer player, String value, int incrementValue) {
        if (gameDurationType == GameDurationType.HOSTDURATION) {
            if (cmc.SQL_SAVE_HOST_STATS) {
                Main.currentDataBase.IncrementValue_playerinfo(player.getSpigotPlayer(), value, incrementValue);
            }
        } else if (gameDurationType == GameDurationType.SERVERDURATION) {
            Main.currentDataBase.IncrementValue_playerinfo(player.getSpigotPlayer(), value, incrementValue);
        }
    }

    public void OptiIncrementation_player_map_stats(GamePlayer player, Map map, String value, int incrementValue) {
        if (gameDurationType == GameDurationType.HOSTDURATION) {
            if (cmc.SQL_SAVE_HOST_STATS) {
                Main.currentDataBase.IncrementValue_player_map_stats(player.getSpigotPlayer(), map, value, incrementValue);
            }
        } else if (gameDurationType == GameDurationType.SERVERDURATION) {
            Main.currentDataBase.IncrementValue_player_map_stats(player.getSpigotPlayer(), map, value, incrementValue);
        }
    }

    public Location next_checkpoint2(GamePlayer p) {
        if (getPlayerInformationSaver().getPassedCP(p.getSpigotPlayer(), false).isEmpty()) {
            return next_checkpoint(p);
        } else {
            ArrayList<Integer> ints = getPlayerInformationSaver().getPassedCP(p.getSpigotPlayer(), false);
            int sizeints = ints.size();
            return getCurrentmap().getCheckpointsList().get(sizeints - 1).getLocation();
        }

    }

    public Location next_checkpoint(GamePlayer p) {
        Location next = null;
        if (getCurrentmap().isNextCheckpointAngleAvailable() && getCurrentmap().getAngleType() == CheckPoint.TYPE_ANGLE_CHECKPOINT.NEXT_CHECKPOINT) {
            int cpint = getPlayerInformationSaver().getCurrentCP(p.getSpigotPlayer());
            if (cpint == getCurrentmap().getCheckpointsList().size()) {
                next = getCurrentmap().getEndsList().get(0).getLocation();
            } else {
                next = getCurrentmap().getCheckpointsList().get(cpint).getLocation();
            }
        }
        return next;


    }

    public void CheckpointVerified(int id_order, Player player) {
        CheckpointVerified(id_order, getGamePlayer(player.getUniqueId()));
    }

    public void CheckpointVerified(int id_order, GamePlayer player) {

        CheckPoint cp = getCurrentmap().getCheckpointsList().get(id_order);
        getPlayerInformationSaver().setBackCP(player.getSpigotPlayer(), false);
        cp.ApplyBoost(player.getSpigotPlayer());
        if (cp.getBoostMultiplier() < 0) {

        }
        int slot_firework = cmc.ITEM_FIREWORK_SLOT;
        if (slot_firework < 0 || slot_firework > 8) {
            slot_firework = 2;
        }
        ItemStack fireworks = player.getInventory().getItem(slot_firework);
        int currentfirework = 0;
        if (fireworks != null && fireworks.getType() == Material.getMaterial("FIREWORK")) {
            currentfirework = fireworks.getAmount();
        }
        getPlayerInformationSaver().addDLV(player.getSpigotPlayer(), false, cp);
        getPlayerInformationSaver().addPassedCP(player.getSpigotPlayer(), id_order, false);
        if (!cp.getLinkedTo_id_order(getCurrentmap()).isEmpty()) {
            cp.getLinkedTo_id_order(getCurrentmap()).forEach(o -> getPlayerInformationSaver().addPassedCP(player.getSpigotPlayer(), o, true));
        }
        int i = 0;
        ArrayList<Integer> integers = getPlayerInformationSaver().getPassedCP(player.getSpigotPlayer(), true);
        for (i = 0; i < getCurrentmap().getCheckpointsList().size(); i++) {
            if (!integers.contains(i)) {
                break;
            }
        }
        getPlayerInformationSaver().addnextCP(player.getSpigotPlayer(), i);

        if (cmc.LineHelper && cmc.NEXT_CHECKPOINT_PARTICLE != null && getCurrentmap().getCheckpointsList().size() > i) {
            CheckPoint ncp = getCurrentmap().getCheckpointsList().get(i);
            int data = 0;
            drawLineNextObject(player, cp, ncp);
        } else if (cmc.LineHelper && cmc.NEXT_CHECKPOINT_PARTICLE != null && getCurrentmap().getCheckpointsList().size() == i) {
            drawLineNextObject(player, cp, currentmap.getEnd(0));
        }

        int checkpoint_passed = getPlayerInformationSaver().getNumberCPPassed(player.getSpigotPlayer());
        getPlayerInformationSaver().addNumberCPPassed(player.getSpigotPlayer());
        checkpoint_passed++;
        int max_checkpoint = 0;
        ArrayList<Integer> c = new ArrayList<>();
        ArrayList<CheckPoint> cps = getCurrentmap().getCheckpointsList();
        for (int i1 = 0; i1 < cps.size(); i1++) {
            if (c.contains(i1)) {
                continue;
            }

            if (!cps.get(i1).getLinkedTo_id_order(getCurrentmap()).isEmpty()) {
                c.addAll(cps.get(i1).getLinkedTo_id_order(getCurrentmap()));
            }
            max_checkpoint++;
        }
        player.sendMessage(cmc.basicsetting(cmc.MSG_CHECKPOINT_VERIFIED.replace("{CHECKPOINT_PASSED}", Integer.toString(checkpoint_passed)).replace("{CHECKPOINT_MAX}", Integer.toString(max_checkpoint)), player.getSpigotPlayer()));


    }

    public void End() {
        End(false);
    }

    public abstract void showEndRank();

    public void saveInformation() {
    }

    public void updateHistory(int playerid, long time, int idgame) {
    }

    ;

    public abstract String getGameType();

    public void makePodium() {
    }

    ;

    public boolean IsInfiniteFireworks(){
        Map map = getCurrentmap();
        if (map != null){
            return map.isInfiniteFireworks();
        }
        return false;
    }

    public void End(boolean force) {
        if (gameState != GameState.STARTED) {
            return;
        }

        gameState = GameState.FINISHED;
        if (!force) {
            showEndRank();
        }
        saveInformation();

        for (GamePlayer player : getPlayersAbleToSee()) {
            Utils.ClearEVERYTHING(player.getSpigotPlayer());
            player.getSpigotPlayer().getInventory().setItem(getSlot(cmc.ITEM_BED_SLOT, 8), Utils.getItemStack(cmc.ITEM_BED_MATERIAL, Arrays.asList("BED", "LEGACY_BED_BLOCK"), cmc.basicsettingnoprefix(cmc.ITEM_BED, null), player.getName()));
        }
        checkerTimer.cancel();
        showTimer.cancel();
        moveTimer.cancel();

        int idgame = Main.currentDataBase.sizeGameHistory() + 1;
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                Main.currentDataBase.addGameHistory(idgame, getGameType(), playeratstartsize);
            }
        });
        int playerid = 0;
        long time = -1;
        ArrayList<Scoring> racerank = RaceRank(true);
        if (!racerank.isEmpty()) {
            updateHistory(playerid, time, idgame);
        }
        getGameSpectators().forEach(o -> o.teleport(currentmap.getLocation_end()));

        if (currentmap.isPodium() && currentmap.getLocations_podium() != null && !racerank.isEmpty()) {
            makePodium();
        }

        int time_end = 2;
        if (cmc.END_TIME >= 0) {
            time_end += cmc.END_TIME;
        }
        if (force) {
            time_end = 0;
        }

        Bukkit.getPluginManager().callEvent(new ElytraRacingGameEndEvent(this));


        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                getPlayerInformationSaver().fullClear();
                getPlayersAbleToSee().forEach(player -> PlayerLeave(player, true));
                boolean recreate = false;
                String Na = name;
                if (GameDurationType.SERVERDURATION == gameDurationType) {
                    recreate = true;
                }
                Delete();

                if (recreate) {
                    for (AutoGames autoGames : Main.autoGamesSaver.autoGamesList) {
                        if (autoGames.getName().equalsIgnoreCase(Na)) {
                            if (autoGames.isEnabled()) {
                                autoGames.create();
                            }
                        }
                    }
                }
            }
        }, 20 * time_end);
    }

    public void SpawnFireworksPodium(ArrayList<Location> locs, int numb_left, Game game) {
        if (numb_left == 0) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (game != null) {
                    locs.forEach(loc -> Utils.spawnRandomFireWorks(loc, game));
                    SpawnFireworksPodium(locs, numb_left - 1, game);
                }

            }
        }, 10);
    }

    public void VerifyEndRound() {
        if (interRounds) {
            return;
        }
        if (!getPlayerInformationSaver().stillNotFinished()) {
            EndRound();
        } else {
            final Game thisgame = this;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                @Override
                public void run() {
                    if (thisgame != null && gameState == GameState.STARTED) {
                        int n = getPlayerInformationSaver().getPlayerNotFinished().size();
                        Game _g;
                        for (Player player1 : getPlayerInformationSaver().getPlayerNotFinished()) {
                            _g = Game.Find(player1);
                            if (!player1.isOnline()) {
                                n--;
                            } else {
                                if (_g != null && _g != thisgame) {
                                    n--;
                                }
                            }
                        }
                        if (n <= 0) {
                            EndRound();
                        }
                    }

                }
            }, 2);

        }
    }

    public boolean isHosted() {
        return gameDurationType.equals(GameDurationType.HOSTDURATION);
    }

    public boolean EndMap(GamePlayer player, boolean Finished) {
        if (Finished) {
            getPlayerInformationSaver().finishedlaps(player.getSpigotPlayer());
            if (getPlayerInformationSaver().getFinishedlaps(player.getSpigotPlayer()) < currentmap.getNumberoflaps()) {
                getPlayerInformationSaver().addnextCP(player.getSpigotPlayer(), 0);
                getPlayerInformationSaver().clearNumberCPPassed(player.getSpigotPlayer());
                getPlayerInformationSaver().clearPassedCP(player.getSpigotPlayer());
                return true;
            }
        }
        Utils.ClearEVERYTHING(player.getSpigotPlayer());
        player.teleport(currentmap.getLocation_end());
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().setItem(getSlot(cmc.ITEM_BED_SLOT, 8), Utils.getItemStack(cmc.ITEM_BED_MATERIAL, Arrays.asList("BED", "LEGACY_BED_BLOCK"), cmc.basicsettingnoprefix(cmc.ITEM_BED, null), player.getName()));
        if (cmc.SPECTATOR_ITEM) {
            player.getInventory().setItem(getSlot(cmc.ITEM_SPEC_SLOT, 4), Utils.getItemStack(cmc.ITEM_SPEC_MATERIAL, Arrays.asList("ENDER_EYE", "EYE_OF_ENDER"), cmc.basicsettingnoprefix(cmc.ITEM_SPEC, null), player.getName()));
        }
        long current_time = System.currentTimeMillis();
        long diff;
        if (getPlayerInformationSaver().hasStarted(player.getSpigotPlayer())) {
            diff = current_time - getPlayerInformationSaver().getStartTime(player.getSpigotPlayer());
        } else {
            diff = current_time - started_time;
        }


        if (Finished) {
            playerfinished++;
            getPlayerInformationSaver().finish(player.getSpigotPlayer(), playerfinished, diff);
        } else {
            getPlayerInformationSaver().finish(player.getSpigotPlayer(), 0, -1);
        }
        getPlayerInformationSaver().clear(player.getSpigotPlayer());

        boolean pb = false;
        getPlayerInformationSaver().getInfoSave(player.getSpigotPlayer()).setFinished();


        if (Finished) {
            OptiIncrementation(player, "player_map_finished", 1);
            OptiIncrementation(player, "player_map_played", 1);
            OptiIncrementation_player_map_stats(player, currentmap, "player_time_played", 1);
            OptiIncrementation_player_map_stats(player, currentmap, "player_time_finished", 1);

            if (getPlayerInformationSaver().hasStarted(player.getSpigotPlayer())) {
                diff = current_time - getPlayerInformationSaver().getStartTime(player.getSpigotPlayer());
            } else {
                diff = current_time - started_time;
            }
            player.setTimeFinished(current_time);
            pb = Main.currentDataBase.SaveTime(player.getSpigotPlayer(), currentmap, diff, this);

            String adding = "";

            if (pb) {
                adding += cmc.basicsettingnoprefix(cmc.PERSONAL_BEST, null);

            }
            String finalAdding = adding;
            long finalDiff = diff;
            getPlayersAbleToSee().forEach(player1 -> player1.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_ENDMAP, player.getSpigotPlayer()).replace("{PLAYER}", player.getName()).replace("{ARG}", Utils.timediffToString(finalDiff)) + finalAdding));
            int player_id = Main.currentDataBase.get_player_id(player.getSpigotPlayer());
            if (player_id > 0) {
                DataBase.Playerinfos.askUpdate(player_id);
            }
        } else {
            OptiIncrementation_player_map_stats(player, currentmap, "player_time_played", 1);
            OptiIncrementation(player, "player_map_played", 1);

            PlayerDNF.add(player.getSpigotPlayer());
            getPlayersAbleToSee().forEach(player1 -> player1.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_ENDMAP, player.getSpigotPlayer()).replace("{PLAYER}", player.getName()).replace("{ARG}", cmc.basicsettingnoprefix(cmc.MSG_ARG_RANK_TIME_DNF, null))));

        }
        if (Finished) {
            Bukkit.getPluginManager().callEvent(new ElytraRacingPlayerEndMapEvent(this, player.getSpigotPlayer(), diff));
        }

        EndedMapVerifyEndRound();
        return false;
    }

    public void EndedMapVerifyEndRound() {
        VerifyEndRound();
    }

    public void WarningStop() {

        End();
    }

    public void drawLineNextObject(Player player, ObjectClass objectClass1, ObjectClass objectClass2) {
        drawLineNextObject(getGamePlayer(player.getUniqueId()), objectClass1, objectClass2);
    }

    public void drawLineNextObject(GamePlayer player, ObjectClass objectClass1, ObjectClass objectClass2) {
        Map map = getCurrentmap();
        if (objectClass2 instanceof End) {
            if (objectClass1.getLineHelperto().isEmpty()) {
                for (Integer integer : getLineHelpersTo(objectClass1, objectClass2, player.getSpigotPlayer())) {
                    drawLineObjects(player, objectClass1, map.getEnd(integer));
                }
            } else {
                for (Integer integer : objectClass1.getLineHelperto()) {
                    drawLineObjects(player, objectClass1, map.getEnd(integer));
                }
            }
        } else if (objectClass2 instanceof CheckPoint) {
            if (objectClass1.getLineHelperto().isEmpty()) {
                for (Integer integer : getLineHelpersTo(objectClass1, objectClass2, player.getSpigotPlayer())) {
                    drawLineObjects(player, objectClass1, map.getCheckpoint(integer));
                }
            } else {
                for (Integer integer : objectClass1.getLineHelperto()) {
                    drawLineObjects(player, objectClass1, map.getCheckpoint(integer));
                }
            }
        }
    }

    private void drawLineObjects(GamePlayer player, ObjectClass objectClass1, ObjectClass objectClass2) {
        if (objectClass1 == null || objectClass2 == null) {
            return;
        }
        Utils.drawLine(objectClass1.getX(getCurrentDeltaTime()), objectClass1.getY(getCurrentDeltaTime()), objectClass1.getZ(getCurrentDeltaTime()), objectClass2.getX(getCurrentDeltaTime()), objectClass2.getY(getCurrentDeltaTime()), objectClass2.getZ(getCurrentDeltaTime())).forEach(floats -> Utils.sendParticle(player.getSpigotPlayer(), cmc.NEXT_CHECKPOINT_PARTICLE, floats[0], floats[1], floats[2], 0.1f, 0.1f, 0.1f, 2));
    }

    public ArrayList<Integer> getLineHelpersTo(ObjectClass objectClass, ObjectClass next, Player player) {
        Map map = currentmap;
        ArrayList<Integer> lines = new ArrayList<>();
        if (next instanceof CheckPoint) {
            lines.addAll(((CheckPoint) next).getLinkedTo_id_order(map));
            lines.add(next.getID(map.getCheckpointsList()));
        } else if (next instanceof End) {
            for (int i = 0; i < map.getEndsList().size(); i++) {
                lines.add(i);
            }
        }
        return lines;
    }

    public String rawPlayerjoin(Player p) {
        String msg = playerJoin(p, null);
        if (msg == null) {
            if (Main.hookParties) {
                PartiesAPI partiesAPI = Parties.getApi();
                PartyPlayer partyPlayer = partiesAPI.getPartyPlayer(p.getUniqueId());
                if (partyPlayer.isInParty()) {
                    Party party = partiesAPI.getParty(partyPlayer.getPartyId());
                    if (party != null) {
                        if (!party.isFixed() && party.getLeader().equals(partyPlayer.getPlayerUUID())) {

                            Set<PartyPlayer> onlinePlayers = party.getOnlineMembers();
                            for (PartyPlayer onlinePlayer : onlinePlayers) {
                                Player playerInParty = Bukkit.getPlayer(onlinePlayer.getPlayerUUID());
                                if (playerInParty != null && !playerInParty.getUniqueId().equals(p.getUniqueId())) {
                                    playerJoin(playerInParty, p);
                                }
                            }
                        }
                    }
                }
            }
        }
        return msg;
    }

    private void joinAsPlayer(Player p) {
        if (sendEventJoin(p)) {
            return;
        }

        joinPreparation(p);
        GamePlayer gamePlayer = new GamePlayer(p);
        gamePlayerMap.put(p.getUniqueId(), gamePlayer);

        p.teleport(currentmap.getLocation_lobby());
        p.setGameMode(GameMode.ADVENTURE);
        Utils.ClearEVERYTHING(p);
        Utils.ReloadShowHide(p);
        p.getInventory().setItem(getSlot(cmc.ITEM_BED_SLOT, 8), Utils.getItemStack(cmc.ITEM_BED_MATERIAL, Arrays.asList("BED", "LEGACY_BED_BLOCK"), cmc.basicsettingnoprefix(cmc.ITEM_BED, null), p.getName()));

        if(ParticleConfig.particleConfig.ENABLE){
            p.getInventory().setItem(getSlot(cmc.ITEM_PARTICLE_SLOT, 2), Utils.getItemStack(cmc.ITEM_PARTICLE_MATERIAL, Arrays.asList("MAGMA_CREAM"), cmc.basicsettingnoprefix(cmc.ITEM_PARTICLE, null), p.getName()));
        }

        if (host != null && isHost(p)) {
            p.getInventory().setItem(getSlot(cmc.ITEM_HOST_SLOT, 4), Utils.getItemStack(cmc.ITEM_HOST_MATERIAL, Arrays.asList("CLOCK", "WATCH"), cmc.basicsettingnoprefix(cmc.ITEM_HOST, null), p.getName()));
        }
        else{
            ItemStack is = Utils.getItemStack(cmc.ITEM_PAPERMAPINFO_MATERIAL, Arrays.asList("PAPER"), getMapInfoTitle(), p.getName());
            ItemMeta im = is.getItemMeta();
            im.setLore(getMapInfoDescription(p));
            is.setItemMeta(im);
            p.getInventory().setItem(getSlot(cmc.ITEM_PAPERMAPINFO_SLOT, 4), is);
        }
        p.setGameMode(GameMode.ADVENTURE);
        p.teleport(currentmap.getLocation_lobby());
        p.updateInventory();

        String MESSAGE = cmc.basicsetting(cmc.MSG_GAME_PLAYERJOIN, p);
        MESSAGE = MESSAGE.replace("{PLAYER_NAME}", p.getName()).replace("{PLAYER_SIZE}", Integer.toString(getGamePlayers().size())).replace("{MAX_PLAYER}", Integer.toString(maxplayer));
        if (gameDurationType == GameDurationType.SERVERDURATION) {
            if (minplayer > getGamePlayers().size()) {
                MESSAGE += cmc.basicsettingnoprefix(cmc.MSG_GAME_PLAYERJOIN_PLAYERNEEDEDLEFT, p);
                MESSAGE = MESSAGE.replace("{PLAYER_NEEDED}", Integer.toString(minplayer - getGamePlayers().size()));
            }

        }

        String finalMessage = MESSAGE;
        getPlayersAbleToSee().forEach(ps -> ps.sendMessage(finalMessage));
        checkEnoughPlayers();
        if (!boardTimer.isRunning()) {
            boardTimer.start();
        }
    }
    private void checkEnoughPlayers(){
        if (gameDurationType == GameDurationType.SERVERDURATION && getGamePlayers().size() >= minplayer) {
            if(!mainTimer.isRunning())
                mainTimer.start();
            else
                mainTimer.reset();
        }
    }

    private void joinAsSpectator(Player p) {
        if (sendEventJoin(p)) {
            return;
        }
        joinPreparation(p);
        GamePlayer gamePlayer = new GamePlayer(p);
        gameSpectatorsMap.put(p.getUniqueId(), gamePlayer);
        Utils.ReloadShowHide(p);
        p.teleport(getCurrentmap().getLocation_start());
        p.setGameMode(GameMode.SPECTATOR);
        p.sendMessage(cmc.basicsetting(cmc.MSG_SPEC_JOINING, p));
        TextComponent message = new TextComponent(cmc.basicsettingnoprefix(cmc.STUCKSPECTATOR, null));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/er near"));
        p.spigot().sendMessage(message);
        getPlayerInformationSaver().finish(p, 0, -1);
    }

    private void joinPreparation(Player p) {
        PlayerSaver.Save(p);
        p.setExp(0);
        p.setLevel(0);
        Utils.ClearEVERYTHING(p);
    }

    private boolean sendEventJoin(Player p) {
        ElytraRacingJoinGameEvent joinEvent = new ElytraRacingJoinGameEvent(this, p);
        Bukkit.getServer().getPluginManager().callEvent(joinEvent);

        return joinEvent.isCancelled();
    }

    private String playerJoin(Player p, Player partyWith) {
        if (Game.ContainsPlayerGAMELIST(p, PlayerMode.SEEING)) {
            p.sendMessage(cmc.basicsetting(cmc.MSG_LEAVEYOURGAME, p));
            return cmc.basicsettingnoprefix(cmc.MSG_LEAVEYOURGAME, p);
        }
        if(!isOpen() && !(partyWith != null && !partyWith.getUniqueId().equals(host)) && !p.getUniqueId().equals(host)){
            p.sendMessage(cmc.basicsetting(cmc.MSG_GAMENOTOPEN, p));
            return cmc.basicsettingnoprefix(cmc.MSG_GAMENOTOPEN, p);
        }
        if (permission != null && !p.hasPermission(permission)) {
            p.sendMessage(cmc.basicsetting(cmc.PERMISSION_MESSAGE_HOST, p));
            return cmc.basicsettingnoprefix(cmc.PERMISSION_MESSAGE_HOST, p);
        }
        if (!Main.rightVersion(p) && cmc.FORCE_KICK_VERSION_BELOW_19) {
            p.sendMessage(cmc.basicsetting(cmc.PLAYER_VERSION_BELOW_19, p));
            return cmc.basicsettingnoprefix(cmc.PLAYER_VERSION_BELOW_19, p);
        }
        MapCommand.hide(p);
        LinkPlayer.delete(p);
        if(PlayerParticle.has(p))  PlayerParticle.delete(p);
        PlayerParticle.selectLastUsed(p, this);
        if (gameState == GameState.STARTED || gameState == GameState.FINISHED) {
            if (p.hasPermission(cmc.PERM_SPECGAME)) {
                joinAsSpectator(p);
                return null;
            } else {
                p.sendMessage(cmc.basicsetting(cmc.MSG_GAMESTARTED, p));
            }
            return cmc.basicsettingnoprefix(cmc.MSG_GAMESTARTED, p);

        }
        if (getGamePlayers().size() == maxplayer) {
            p.sendMessage(cmc.basicsetting(cmc.MSG_GAMEFULL, p));
            return cmc.basicsettingnoprefix(cmc.MSG_GAMEFULL, p);
        }
        joinAsPlayer(p);
        return null;
    }

    public int getMinPlayer() {
        return minplayer;
    }

    public void setMaxPlayer(int maxplayer) {
        this.maxplayer = maxplayer;
    }

    public void setMinPlayer(int minplayer) {
        this.minplayer = minplayer;
    }

    public int getMaxPlayer() {
        return maxplayer;
    }

    public Collection<GamePlayer> getPlayersAbleToSee() {
        Collection<GamePlayer> r = getGamePlayers();
        r.addAll(getGameSpectators());
        return r;
    }

    public void PlayerLeave(Player p) {
        PlayerLeave(p, false);
    }

    public void PlayerLeave(GamePlayer p) {
        PlayerLeave(p, false);
    }

    public void removePlayerFrom(GamePlayer p, PlayerMode playerMode) {
        switch (playerMode) {
            case SPEC:
                gameSpectatorsMap.remove(p.getSpigotPlayer().getUniqueId());
                break;
            case ALIVE:
                p.left();
                break;
        }
    }

    public boolean isFull() {
        if (getGamePlayers().size() >= maxplayer) {
            return true;
        }
        return false;
    }

    public boolean isInGame(Player p) {
        return getGamePlayer(p.getUniqueId()) != null;
    }

    public boolean specEnd(Player p) {
        if (!isInGame(p)) return false;
        return Specs_Ends.contains(getGamePlayer(p.getUniqueId()));
    }

    public void setGameDurationType(GameDurationType gameDurationType) {
        this.gameDurationType = gameDurationType;
    }

    protected void playerLeaveImportants(Player p, boolean clearTab) {
        if (Main.WarningSTOP) {
            PlayerSaver.SetIf(p, clearTab);
            Utils.ReloadShowHide(p);
            return;
        }
        PlayerSaver.SetIf(p, clearTab);
    }

    public void PlayerLeave(Player p, boolean SilenceMode) {
        GamePlayer gamePlayer = getGamePlayer(p.getUniqueId());
        PlayerMode pm = PlayerModeof(p);
        if (pm == PlayerMode.SPEC && gameSpectatorsMap.containsKey(p.getUniqueId())) {
            gameSpectatorsMap.remove(p.getUniqueId());
            playerLeaveImportants(p, this instanceof GrandPrixMode);
            return;
        }
        if (gamePlayer != null) {
            PlayerLeave(gamePlayer, SilenceMode);
        }
    }

    public void silenceKick(GamePlayer p){

    }

    public void PlayerLeave(GamePlayer p, boolean SilenceMode) {
        PlayerParticle.delete(p.getSpigotPlayer());
        playerLeaveImportants(p.getSpigotPlayer(), this instanceof GrandPrixMode);
        boolean deletegame = false;
        PlayerMode pm = PlayerModeof(p.getSpigotPlayer());
        if (pm == PlayerMode.NONE) {
            return;
        }

        if (gameState == GameState.FINISHED) {
            removePlayerFrom(p, pm);
        } else if (gameState == GameState.WAITING) {
            removePlayerFrom(p, pm);
            if (!SilenceMode) {
                String MESSAGE = cmc.basicsetting(cmc.MSG_GAME_PLAYERLEAVE, p.getSpigotPlayer());
                MESSAGE = MESSAGE.replace("{PLAYER_NAME}", p.getName()).replace("{PLAYER_SIZE}", Integer.toString(getGamePlayers().size())).replace("{MAX_PLAYER}", Integer.toString(maxplayer));
                String finalMESSAGE = MESSAGE;
                getPlayersAbleToSee().forEach(player -> player.sendMessage(finalMESSAGE));
            }

            if (gameDurationType == GameDurationType.HOSTDURATION) {
                if (getGameHost().equals(p)) {

                    getPlayersAbleToSee().forEach(player -> PlayerLeave(player, true));
                    deletegame = true;
                }
            }
            Utils.ReloadShowHide(p.getSpigotPlayer());
            if (deletegame) {
                Delete();
            }
        } else if (gameState == GameState.STARTING) {
            removePlayerFrom(p, pm);
            if (!SilenceMode) {
                String MESSAGE = cmc.basicsetting(cmc.MSG_GAME_PLAYERLEAVE, p.getSpigotPlayer());
                MESSAGE = MESSAGE.replace("{PLAYER_NAME}", p.getName()).replace("{PLAYER_SIZE}", Integer.toString(getGamePlayers().size())).replace("{MAX_PLAYER}", Integer.toString(maxplayer));
                String finalMESSAGE = MESSAGE;
                getPlayersAbleToSee().forEach(player -> player.sendMessage(finalMESSAGE));
            }
            if (gameDurationType == GameDurationType.HOSTDURATION) {
                if (getGameHost().equals(p)) {
                    getPlayersAbleToSee().forEach(player -> PlayerLeave(player, true));
                    deletegame = true;
                }
            }
            if (gameDurationType == GameDurationType.SERVERDURATION && getGamePlayers().size() < minplayer) {
                mainTimer.cancel();
                String MESSAGE1 = cmc.basicsetting(cmc.MSG_GAME_NOTENOUGHPLAYERS, p.getSpigotPlayer());
                getPlayersAbleToSee().forEach(ps -> ps.sendMessage(MESSAGE1));
            }

            Utils.ReloadShowHide(p.getSpigotPlayer());
            if (deletegame) {
                Delete();
            }
        } else if (gameState == GameState.STARTED) {
            Game _g = Find(p.getSpigotPlayer());
            if (_g != null && _g == this) {
                VerifyEndRound();
            }
            removePlayerFrom(p, pm);
            Utils.ReloadShowHide(p.getSpigotPlayer());

        }

        if (getGamePlayers().size() == 0) {
            End(true);
        }
        Utils.ReloadShowHide(p.getSpigotPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (Main.kickPlayerAfterGame()) {
                    Utils.kickPlayer(p.getSpigotPlayer(), "", Main.cmc().ONEGAME_LOBBYSERVER);
                }
            }
        }, 10);

        BoardHandler.getBoard(p.getSpigotPlayer(), "").delete();
        if (boardTimer.isRunning() && getGamePlayers().size() == 0) {
            boardTimer.cancel();
        }

        ElytraRacingLeaveGameEvent leaveEvent = new ElytraRacingLeaveGameEvent(this, p.getSpigotPlayer());
        Bukkit.getServer().getPluginManager().callEvent(leaveEvent);

    }

    private static String YWNjZXB0ZWR1c2Vy = "%%__USER__%%";

    public PlayerMode PlayerModeof(Player p) {
        if (gamePlayerMap.containsKey(p.getUniqueId()) && gamePlayerMap.get(p.getUniqueId()).isPlaying()) {
            return PlayerMode.ALIVE;
        } else if (gameSpectatorsMap.containsKey(p.getUniqueId())) {
            return PlayerMode.SPEC;
        } else {
            return PlayerMode.NONE;
        }
    }

    public void cancelTimers(boolean cancelBoard){
        checkerTimer.cancel();
        showTimer.cancel();
        moveTimer.cancel();
        if(cancelBoard) boardTimer.cancel();
    }

    public void Delete() {
        mainTimer.cancel();
        cancelTimers(true);
        gamelist.remove(this);

        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean ContainsPlayer(Player p, PlayerMode playerMode) {
        switch (playerMode) {
            case SPEC:
                return gameSpectatorsMap.containsKey(p.getUniqueId());
            case ALIVE:
                return gamePlayerMap.containsKey(p.getUniqueId()) && gamePlayerMap.get(p.getUniqueId()).isPlaying();
            case SEEING:
                return ((gamePlayerMap.containsKey(p.getUniqueId()) && gamePlayerMap.get(p.getUniqueId()).isPlaying()) || gameSpectatorsMap.containsKey(p.getUniqueId()));
            case ALL:
                return (gamePlayerMap.containsKey(p.getUniqueId()) && gamePlayerMap.get(p.getUniqueId()).isPlaying()) || gameSpectatorsMap.containsKey(p.getUniqueId());
        }
        return false;
    }

    public long timestarted() {
        return System.currentTimeMillis() - started_time;
    }

    public Game instance() {
        return this;
    }

    public SpecMouvement getSpecMouvement() {
        if (specMouvement == null) {
            specMouvement = new SpecMouvement();
        }
        return specMouvement;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isRunning() {
        return !(gameState == GameState.FINISHED);
    }

    public Map getCurrentmap() {
        return currentmap;
    }

    public GameDurationType getGameDurationType() {
        return gameDurationType;
    }

    public int getAlertsent() {
        return Alertsent;
    }

    public void setAlertsent(int alertsent) {
        Alertsent = alertsent;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void LeaveMovementItem(GamePlayer player, ItemStack itemStack) {
        itemStack.setType(Material.LEATHER);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                Game game = Game.Find(player.getSpigotPlayer());
                if (game == null) {
                    return;
                }
                game.PlayerLeave(getGamePlayer(player.getSpigotPlayer().getUniqueId()));
            }
        }, 3);
    }

    public void Interact_1CPBack(GamePlayer player, ItemStack itemStack) {
        back1CP(player);
        getPlayerInformationSaver().setBackCP(player.getSpigotPlayer(), false);
        if (back1CP(player)) {
        } else if (!getPlayerInformationSaver().hasFinished(player.getSpigotPlayer())) {
            player.teleport(getCurrentmap().getLocation_start());
            getPlayerInformationSaver().setPassedAdditionalObjects(player.getSpigotPlayer(), new ArrayList<>());
            McHelper.setGliding(player.getSpigotPlayer(), false, Main.customMessageConfig.DISABLE_ELYTRA);
        }
    }

    public void Interact_2CPBack(GamePlayer player, ItemStack itemStack) {
        if (back2CP(player)) {

            getPlayerInformationSaver().setBackCP(player.getSpigotPlayer(), true);
            McHelper.setGliding(player.getSpigotPlayer(), false, Main.customMessageConfig.DISABLE_ELYTRA);
        } else if (back1CP(player)) {
            McHelper.setGliding(player.getSpigotPlayer(), false, Main.customMessageConfig.DISABLE_ELYTRA);
        } else if (!getPlayerInformationSaver().hasFinished(player.getSpigotPlayer())) {
            player.teleport(getCurrentmap().getLocation_start());
            getPlayerInformationSaver().setPassedAdditionalObjects(player.getSpigotPlayer(), new ArrayList<>());
            McHelper.setGliding(player.getSpigotPlayer(), false);
        }
    }
    public void playerRestart(GamePlayer player){
        player.teleport(getCurrentmap().getLocation_start());
        McHelper.setGliding(player.getSpigotPlayer(), false);
        if (!getPlayerInformationSaver().hasFinished(player.getSpigotPlayer())) {
            getPlayerInformationSaver().clear(player.getSpigotPlayer());
            giveFireworksStart(player);
        }
    }

    public void Interact_Restart(GamePlayer player, ItemStack itemStack) {
        playerRestart(player);
    }

    public void Interact_Leave(GamePlayer player, ItemStack itemStack) {
        LeaveMovementItem(player, itemStack);
    }

    public void Interact_DNF(GamePlayer player, ItemStack itemStack) {
        EndMap(player, false);
    }

    public String getMOTDModifiedString(String s) {
        return s;
    }

    public boolean spectatingOfEndRound(Player p) {
        if (!isInGame(p)) return false;
        return Specs_Ends.contains(getGamePlayer(p.getUniqueId()));
    }

    public void Interact_Spec(GamePlayer player, ItemStack itemStack) {
        if (cmc.SPEC_PERM != null && !player.getSpigotPlayer().hasPermission(cmc.SPEC_PERM)) {
            player.sendMessage(cmc.basicsettingnoprefix(cmc.SPEC_PERM_MESSAGE, null));
            return;
        }
        player.setGameMode(GameMode.SPECTATOR);
        if (!Specs_Ends.contains(player)) {
            Specs_Ends.add(player);
        }
        net.md_5.bungee.api.chat.TextComponent message = new TextComponent(cmc.basicsettingnoprefix(cmc.SPECTATORMODE, null));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/er quitspec"));
        player.getSpigotPlayer().spigot().sendMessage(message);


        message = new TextComponent(cmc.basicsettingnoprefix(cmc.STUCKSPECTATOR, null));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/er near"));
        player.getSpigotPlayer().spigot().sendMessage(message);
    }

    public void Interact_Host(GamePlayer player, ItemStack itemStack) {
        HostPage hp = new HostPage(player.getSpigotPlayer(), this);
        hp.open();
        hp.View();
    }
    public void Interact_Particle(GamePlayer player, ItemStack itemStack) {
        ParticleGameChoosePage hp = new ParticleGameChoosePage(player.getSpigotPlayer(), this);
        hp.displayPage(1);
        hp.open();
    }

    public boolean isInterRounds() {
        return interRounds;
    }

    @Override
    public Player getHost() {
        return getGamePlayer(host).getSpigotPlayer();
    }

    @Override
    public List<Player> getPlayerList() {
        ArrayList<Player> playerList = new ArrayList<>();
        getGamePlayers().forEach(ps -> playerList.add(ps.getSpigotPlayer()));
        return playerList;
    }

    @Override
    public List<Player> getSpecList() {
        ArrayList<Player> playerList = new ArrayList<>();
        getGameSpectators().forEach(ps -> playerList.add(ps.getSpigotPlayer()));
        return playerList;
    }
    public String getMapInfoTitle(){
        return cmc.basicsettingnoprefix(cmc.MAP_INGAME_INFO_MENU_TITLE);
    }
    public List<String> getMapInfoDescription(Player p){
        DataBase.Playerinfos pi = DataBase.Playerinfos.getPlayerInfos(Main.currentDataBase.get_player_id(p));
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> descriptionlines = new ArrayList<String>(Arrays.asList(cmc.MAP_INGAME_INFO_DESCRIPTION));
        int i = 1;
        for (Map map : maps) {
            DataBase.PlayerMapinfos mi = pi.getMap(map.getUUID());
            boolean hasMapInfo = mi != null;
            for (String descriptionline : descriptionlines) {
                list.add(cmc.basicsettingnoprefix(descriptionline
                    .replace("{ORDER}", String.valueOf(i))
                        .replace("{MAP_NAME}", map.getName())
                        .replace("{PLAYER_TIME}", hasMapInfo ? String.valueOf(Utils.timediffToString(mi.player_time)) : cmc.TIME_NOT_FOUND)
                        .replace("{PLAYER_WINRATE}", hasMapInfo ? String.valueOf(mi.winrate) : "0")
                        .replace("{PLAYER_RANK}", hasMapInfo ? String.valueOf(mi.ranking) : cmc.RANK_NOT_FOUND)
                    ));
            }
            i++;
        }
        return list;
    }
}