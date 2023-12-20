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

import fr.CHOOSEIT.elytraracing.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class AutoGamesSaver {

    private static String USER = "%%__USER__%%";
    public String DEFAULT_QUEUE = "play";
    public ArrayList<String> ENABLED_QUEUES = new ArrayList<String>(Arrays.asList(DEFAULT_QUEUE));
    public ArrayList<String> DISABLED_QUEUES = new ArrayList<String>(Arrays.asList());

    public boolean isEnabled(String queue) {
        for (String qu : ENABLED_QUEUES) {
            if (qu.equalsIgnoreCase(queue)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDisabled(String queue) {
        for (String qu : DISABLED_QUEUES) {
            if (qu.equalsIgnoreCase(queue)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<AutoGames> autoGamesList = new ArrayList<AutoGames>(Arrays.asList(new AutoGames("default")));


    public void init() {
        for (AutoGames autoGames : autoGamesList) {
            if (!autoGames.isEnabled()) {
                continue;
            }
            if (!autoGames.getName().equalsIgnoreCase("")) {
                Utils.log_print("[ElytraRacing] -> Loading autogame: " + autoGames.getName());
            }

            autoGames.create();
        }
    }

    public AutoGames getAutogame(String name) {
        for (AutoGames autoGames : autoGamesList) {
            if (autoGames.getName().equalsIgnoreCase(name)) {
                return autoGames;
            }
        }
        return null;
    }

}
