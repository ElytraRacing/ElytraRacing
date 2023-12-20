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

package fr.CHOOSEIT.elytraracing.GUI.Pages;

import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.GUI.HeadDatabase;
import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.IconObject;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.NumberObject1x2;
import fr.CHOOSEIT.elytraracing.GUI.VisibleObjects.TrueFalseObject;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.gamesystem.ERHostCommand;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.gamesystem.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HostPage extends Page {
    Game game;

    public HostPage(Player viewer, Game game) {
        super("Host configuration", viewer, 3);
        this.game = game;
    }

    @Override
    public void update(VisibleObject visibleObject) {
        if (visibleObject.getId().equalsIgnoreCase("STARTEND")) {
            if (game.getGameState().equals(GameState.WAITING)) {
                visibleObject.setItem(HeadDatabase.IRON_RIGHT_ARROW);
                visibleObject.setDisplayName(Main.cmc().HOST_TITLE_START);
                visibleObject.setDescription(Main.cmc().basicsettingnoprefix(Arrays.asList(Main.cmc().HOST_DESC_START), null));
                visibleObject.display();
            } else {
                visibleObject.setItem(HeadDatabase.FALSE);
                visibleObject.setDisplayName(Main.cmc().HOST_TITLE_END);
                visibleObject.setDescription(Main.cmc().basicsettingnoprefix(Arrays.asList(Main.cmc().HOST_DESC_END), null));
                visibleObject.display();
            }

        }
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void View() {
        CustomMessageConfig cmc = Main.cmc();
        IconObject io = new IconObject(this, "STARTEND", 26);
        io.addDisplay(true);

        io = new IconObject(this, "ALERT", 10);
        io.setItem(HeadDatabase.GREEN_A);
        io.setDisplayName(cmc.HOST_TITLE_ALERT);
        io.setDescription(Arrays.asList(cmc.HOST_DESC_ALERT));
        io.addDisplay();

        io = new IconObject(this, "PERM", 16);
        io.setItem(HeadDatabase.ORANGE_P);
        io.setDisplayName(cmc.HOST_TITLE_PERM.replace("{PERMISSION}", permstr(game.getPermission())));
        io.setDescription(Arrays.asList(cmc.HOST_DESC_PERM));
        io.setObjectAction((o, ct) -> {
            ERHostCommand.HelpMessage(getViewer());
            closeView();
        });
        io.addDisplay();

        NumberObject1x2 maxPlayers = new NumberObject1x2(this, "MAX_PLAYER_COUNTS", 2, 1, game.getMaxPlayer());
        maxPlayers.setUpdateActionOnInteraction(o -> {
            List<String> a = Arrays.asList(cmc.HOST_DESC_SLOTS);
            a = a.stream().map(e -> e.replace("{SLOTS}", String.valueOf(game.getMaxPlayer()))).collect(Collectors.toList());
            maxPlayers.setDescription(a);
            maxPlayers.setDisplayName(cmc.HOST_TITLE_SLOTS.replace("{SLOTS}", String.valueOf(game.getMaxPlayer())));
        });
        maxPlayers.setObjectAction((o, ct) -> {
            int adder = ((ct.equals(ClickType.RIGHT) || ct.equals(ClickType.SHIFT_RIGHT) ? -1 : 1) * (ct.equals(ClickType.SHIFT_RIGHT) || ct.equals(ClickType.SHIFT_LEFT) ? 10 : 1));
            int a = Utils.clamp(game.numberOfPlayers(), game.getMaxPlayer() + adder,
                    Integer.MAX_VALUE);
            game.setMaxPlayer(a);
            maxPlayers.setNumber(a);
        });
        maxPlayers.addDisplay();

        TrueFalseObject open = new TrueFalseObject(this, "OPENHOST", 4,1, game.isOpen());
        open.setObjectAction((o, ct) -> {
            game.setOpen(!open.getStatus());
        });
        open.setUpdateActionOnInteraction((o) -> {
            open.setStatus(game.isOpen());
            open.setDisplayName(cmc.basicsettingnoprefix(game.isOpen() ? cmc.HOST_TITLE_OPEN : cmc.HOST_TITLE_CLOSED));
        });
        open.addDisplay();

        NumberObject1x2 mapCount = new NumberObject1x2(this, "MAP_COUNT", 5, 1, game.getMaps().size());
        mapCount.setDisplayName(game.getMapInfoTitle());
        mapCount.setDescription(game.getMapInfoDescription(getViewer()));
        mapCount.addDisplay();

    }



    public static String permstr(String perm) {
        if (perm == null) {
            return Main.cmc().NONE;
        }
        return perm;
    }
}
