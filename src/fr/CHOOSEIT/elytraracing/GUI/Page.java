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

package fr.CHOOSEIT.elytraracing.GUI;

import fr.CHOOSEIT.elytraracing.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public abstract class Page {
    private ArrayList<VisibleObject> objects = new ArrayList<>();
    private ArrayList<VisibleObject> importantObjects = new ArrayList<>();
    private Inventory screen;
    private String pageName;
    private ArrayList<Object> informationObjects;
    private boolean tempSave = false;
    private Player viewer;
    private int size;

    public Page(String pageName, Player viewer, int size) {
        this.viewer = viewer;
        this.size = size;
        setPageName(pageName);
        createDisplay();
    }

    public Page(String pageName, Player viewer) {
        this.viewer = viewer;
        size = 5;
        setPageName(pageName);
        createDisplay();
    }

    public void setSize(int size) {
        this.size = size;
        reloadDisplay();
    }

    public int getSize() {
        return size;
    }

    public void update(VisibleObject visibleObject) {

    }

    public ArrayList<Object> getInformationObjects() {
        return informationObjects;
    }

    public void setInformationObjects(ArrayList<Object> informationObjects) {
        this.informationObjects = informationObjects;
    }

    public void setInformationObject(Object o) {
        this.informationObjects = new ArrayList<>(Collections.singletonList(o));
    }

    public Object getInformationObject() {
        if (informationObjects == null) {
            return null;
        }
        return this.informationObjects.get(0);
    }

    public void sendViewerMessage(String s) {
        viewer.sendMessage(Main.cmc().basicsettingnoprefix(s, viewer));
    }

    public void setup() {

    }

    public abstract void View();

    public void loadObjects() {
        objects.forEach(o -> addDisplay(o, false));
    }

    private void updateView() {
        clearPage();
        loadObjects();
    }

    public void setScreen(Inventory screen) {
        this.screen = screen;
        removeObjects();
    }

    public void createDisplay() {
        screen = Bukkit.createInventory(null, this.size * 9, pageName);
    }

    public void reloadDisplay() {
        createDisplay();
        updateView();
        open();
    }

    public void clearPage() {
        ItemStack air = new ItemStack(Material.AIR);
        for (int i = 0; i < this.size * 9; i++) {
            setItem(air, i);
        }

    }

    public void removeObjects() {
        clearPage();
        objects.clear();
    }

    public void addDisplay(VisibleObject visibleObject, boolean importantobject) {
        addDisplay(visibleObject, true, importantobject);
    }

    void addDisplay(VisibleObject visibleObject, boolean addtoArray, boolean importantobject) {
        int x = visibleObject.getLocX();
        int y = visibleObject.getLocY();
        int sizeX = visibleObject.getSizeX();
        int sideY = visibleObject.getSizeY();
        if (isValidPlacementSize(x, y, sizeX, sideY)) {
            if (addtoArray) {
                objects.add(visibleObject);
                if (importantobject) {
                    importantObjects.add(visibleObject);
                }

            }
            visibleObject.display();
        }
    }

    public void setTempSave(boolean tempSave) {
        this.tempSave = tempSave;
    }

    public boolean isTempSave() {
        return tempSave;
    }

    public void delete() {
        if (tempSave) {
            return;
        }
        removeObjects();
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void setItem(ItemStack itemStack, int locX, int locY) {
        setItem(itemStack, locToSlot(locX, locY));
    }

    private void setItem(ItemStack itemStack, int slot) {
        if (itemStack == null) {
            return;
        }
        screen.setItem(slot, itemStack);


    }

    private void reloadImportantObject() {
        importantObjects.forEach(VisibleObject::update);
    }

    private int locToSlot(int locX, int locY) {
        int slot = locX % 9;
        slot += (locY % 5) * 9;
        return slot;
    }

    public int[] slotToLoc(int slot) {
        int[] loc = new int[]{-1, -1};
        loc[0] = slot % 9;
        loc[1] = slot / 9;
        return loc;
    }

    private void click(InventoryClickEvent e) {
        int slot = e.getRawSlot();
        if (slot >= this.size * 9 || slot < 0) {
            e.setCancelled(true);
            return;
        }
        int[] locs = slotToLoc(slot);
        int locX = locs[0], locY = locs[1];
        VisibleObject touch = touching(locX, locY);
        if (touch != null) {
            e.setCancelled(!touch.isMovable());
            switch (e.getClick()) {
                case LEFT:
                case RIGHT:
                case MIDDLE:
                case SHIFT_RIGHT:
                case SHIFT_LEFT:
                    touch.handleClick(e.getClick());
                    click(touch, e.getClick());
                    executeImportant();
                    reloadImportantObject();
                    break;
            }

        }

    }

    public void click(VisibleObject visibleObject, ClickType clickType) {

    }

    private VisibleObject touching(int locX, int locY) {
        for (VisibleObject object : objects) {
            if (object.istouching(locX, locY)) {
                return object;
            }
        }
        return null;
    }

    private boolean isValidPlacementSize(int locX, int locY, int sizeX, int sizeY) {
        return isValidPlacement(locX, locY) && isValidPlacement(locX + sizeX - 1, locY + sizeY - 1);
    }

    private void executeImportant() {
    }

    ;

    private boolean isValidPlacement(int locX, int locY) {
        return isValidPlacement(locToSlot(locX, locY));
    }

    private boolean isValidPlacement(int slot) {
        return slot >= 0 && slot < this.size * 9;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void open() {
        viewer.closeInventory();
        viewer.openInventory(screen);

        tempSave = false;

        PlayerPageInformation.put(viewer, new PageInformation(this, getInformation()));

    }

    private void close() {
        delete();
    }

    public void closeView() {
        getViewer().closeInventory();
    }

    public Object getInformation() {
        return null;
    }

    public ArrayList<VisibleObject> getObjects() {
        return objects;
    }

    public Player getViewer() {
        return viewer;
    }


    private static HashMap<Player, PageInformation> PlayerPageInformation = new HashMap<>();

    public static class PageInformation {
        private Page page;
        private Object information;

        public PageInformation(Page page, Object information) {
            this.page = page;
            this.information = information;
        }

        public Page getPage() {
            return page;
        }

        public Object getInformation() {
            return information;
        }

        public static PageInformation getPageInformation(Player p) {
            if (PlayerPageInformation.containsKey(p)) {
                return PlayerPageInformation.get(p);
            }
            return null;
        }

        public static void removePlayer(Player p) {
            PlayerPageInformation.remove(p);
        }

        public static void PlayerCloseInventory(Player p) {
            PageInformation pageInformation = getPageInformation(p);
            if (pageInformation != null) {
                Page page = pageInformation.getPage();
                page.close();
                removePlayer(p);
            }
        }

        public static void click(InventoryClickEvent inventoryClickEvent) {
            Player p = (Player) inventoryClickEvent.getWhoClicked();
            PageInformation pageInformation = getPageInformation(p);
            if (pageInformation != null && pageInformation.getPage().pageName.equals(inventoryClickEvent.getView().getTitle()) && p.equals(pageInformation.getPage().getViewer())) {
                pageInformation.getPage().click(inventoryClickEvent);
            } else {
                removePlayer(p);
            }
        }
    }


}
