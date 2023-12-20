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

package fr.CHOOSEIT.elytraracing.scoreboard;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BoardHandler {
    public static HashMap<UUID, Board> boards = new HashMap<>();

    public static Board getBoard(UUID uuid, String title) {
        if (boards.containsKey(uuid)) {
            Board b = boards.get(uuid);
            if (b != null) {
                if (b.isViewer(uuid)) {
                    if (!b.getTitle().equals(title)) {
                        b.updateTitle(title);
                    }
                    return b;
                }
                if (b.getViewers().size() == 0) {
                    b.delete();
                }
            }

        }
        Board b = new Board(title);
        b.addViewer(uuid);
        boards.put(uuid, b);
        return b;
    }

    public static Board getBoard(Player player, String title) {
        return getBoard(player.getUniqueId(), title);
    }


}
