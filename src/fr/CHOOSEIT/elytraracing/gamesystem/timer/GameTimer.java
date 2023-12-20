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

package fr.CHOOSEIT.elytraracing.gamesystem.timer;

import fr.CHOOSEIT.elytraracing.Main;
import org.bukkit.Bukkit;

public abstract class GameTimer implements eTimer {
    private static String USER = "%%__USER__%%";
    private final int UNDEFINED = -1;

    private final int reset;
    private int id, timerLeft;
    private final long sep, delay;

    public GameTimer(int reset, long sep, long delay) {
        this.reset = reset;
        this.id = UNDEFINED;
        this.sep = sep;
        this.delay = delay;
    }
    public GameTimer(int reset, long sep) {
        this(reset, sep, 0L);
    }

    protected abstract void event(int timeLeft);
    protected abstract void endEvent();
    protected abstract void startEvent();
    protected abstract void earlyCancelEvent();

    @Override
    public void reset(){
        setTimerLeft(reset);
    }

    protected void setTimerLeft(int timerLeft) {
        this.timerLeft = timerLeft;
    }

    @Override
    public void start(){
        if(isRunning()) return;
        this.timerLeft = reset;
        startEvent();
        id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                timerLeft--;
                event(timerLeft);
                if(timerLeft == 0){
                    stop();
                    endEvent();
                }
            }
        }, delay, sep);
    }
    @Override
    public boolean isRunning(){
        return id != UNDEFINED;
    }
    private void stop(){
        if(isRunning()) Bukkit.getServer().getScheduler().cancelTask(id);
        id = UNDEFINED;
    }
    @Override
    public void cancel(){
        stop();
        earlyCancelEvent();
    }

    @Override
    public int getTimerLeft() {
        return timerLeft;
    }
}
