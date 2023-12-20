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

package fr.CHOOSEIT.elytraracing.api.elytraracing.viewer;

import fr.CHOOSEIT.elytraracing.api.elytraracing.erEnum.GameType;
import fr.CHOOSEIT.elytraracing.gamesystem.GameState;
import fr.CHOOSEIT.elytraracing.gamesystem.PlayerMode;
import fr.CHOOSEIT.elytraracing.gamesystem.Scoring;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public interface ERGame {
    List<Player> getPlayerList();

    List<Player> getSpecList();

    GameType getType();

    String getName();

    ArrayList<Map> getMaps();

    int getCurrentMapIndex();

    ERMap getCurrentmap();

    Player getHost();

    GameState getGameState();

    String getUUID();

    String getPermission();

    int getMinPlayer();

    int getMaxPlayer();

    void setMaxPlayer(int maxplayer);

    void setMinPlayer(int minplayer);

    List<Scoring> getScoreBoard();

    List<Scoring> getRaceRank();
}
