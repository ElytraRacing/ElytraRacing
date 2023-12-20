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
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VisibleObject {

    private int sizeX, sizeY;
    private int locX, locY;
    private ArrayList<ItemStack> items = new ArrayList<>();
    private boolean movable;
    private String displayName;
    private List<String> description;
    protected Page page;
    private String id;
    private ClickType lastAction;
    private Object information;
    private ArrayList<VisibleObject> linkedObjects = new ArrayList<>();
    private ObjectAction objectAction;
    private UpdateAction updateActionOnInteraction;

    public VisibleObject(Page page, String id, int locX, int locY, int sizeX, int sizeY) {
        this.id = id;
        this.page = page;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.locX = locX;
        this.locY = locY;
        this.movable = false;
    }

    public VisibleObject(Page page, String id, int slot, int sizeX, int sizeY) {
        this.id = id;
        this.page = page;
        int[] locs = page.slotToLoc(slot);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.locX = locs[0];
        this.locY = locs[1];
        this.movable = false;
    }

    public ArrayList<VisibleObject> getLinkedObjects() {
        return linkedObjects;
    }

    public void addLinkedObject(VisibleObject visibleObject) {
        linkedObjects.add(visibleObject);
    }

    public void updateLinkedObjects() {
        for (VisibleObject linkedObject : linkedObjects) {
            linkedObject.update();
            linkedObject.display();
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }
    public void setDescription(String description) {
        setDescription(Collections.singletonList(description));
    }

    public Object getInformation() {
        return information;
    }

    public void setInformation(Object information) {
        this.information = information;
    }

    public boolean isMovable() {
        return movable;
    }

    public String getId() {
        return id;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }


    public ArrayList<ItemStack> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemStack> items) {
        this.items = (ArrayList<ItemStack>) items.clone();
    }

    public void setItem(ItemStack item) {
        this.items = new ArrayList<>(Arrays.asList(item));
    }

    public void addItem(ItemStack item) {
        items.add(item.clone());
    }

    public void addDisplay() {
        addDisplay(false);
    }

    public void addDisplay(boolean importantObject) {
        update();
        page.addDisplay(this, importantObject);
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getLocX() {
        return locX;
    }

    public int getLocY() {
        return locY;
    }

    protected boolean existItem(int id) {
        return id >= 0 && id < items.size();
    }

    protected ItemStack prepareItem(int id) {
        if (existItem(id)) {
            return items.get(id);
        } else {
            return null;
        }
    }

    public void update() {
        page.update(this);
        updateActionOnInteraction();
    }

    public void display() {
        int idItem = 0;
        int endX = locX + sizeX, endY = locY + sizeY;
        for (int x = locX; x < endX; x++) {
            for (int y = locY; y < endY; y++) {
                if (existItem(idItem)) {
                    page.setItem(cookItem(prepareItem(idItem)), x, y);
                    idItem++;
                    continue;
                }
                return;
            }
        }
    }

    public boolean istouching(int locX, int locY) {
        return locX >= this.locX && locX < this.locX + sizeX && locY >= this.locY && locY < this.locY + sizeY;
    }

    public static ItemStack getRenderItem(String material, List<String> materialspossibledefault, String name) {

        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta im = item.getItemMeta();
        if (material != null) {
            String m = material;
            short data = 0;
            if (material.contains(":")) {
                String[] s = material.split(":");
                if (Utils.IsInteger(s[1])) {
                    m = s[0];
                    data = Short.parseShort(s[1]);
                } else {
                    m = null;
                }
            }
            Material finalmaterial = Utils.getMaterial(m);
            if (finalmaterial != null) {
                im.setDisplayName(name);
                item.setItemMeta(im);
                item.setType(finalmaterial);
                item.setDurability(data);
                return item;
            }
        }
        if (materialspossibledefault != null && !materialspossibledefault.isEmpty()) {
            String next = materialspossibledefault.get(0);
            return getRenderItem(next, materialspossibledefault.subList(1, materialspossibledefault.size() - 1), name);
        }

        return item;
    }
    public void updateActionOnInteraction(){
        if(updateActionOnInteraction != null){
            updateActionOnInteraction.action(this);
            display();
        }
    }

    public void setUpdateActionOnInteraction(UpdateAction updateActionOnInteraction) {
        this.updateActionOnInteraction = updateActionOnInteraction;
    }

    public void handleClick(ClickType clickType){
        clickEvent(clickType);
        if(objectAction != null){
            objectAction.action(this, clickType);
        }
        updateActionOnInteraction();
    }

    protected void clickEvent(ClickType clickType) {
        ClickHandler.ClickHandler(this, clickType);
        lastAction = clickType;
    }

    public void setObjectAction(ObjectAction objectAction) {
        this.objectAction = objectAction;
    }

    public ClickType getLastAction() {
        return lastAction;
    }

    public void setLastAction(ClickType lastAction) {
        this.lastAction = lastAction;
    }

    public Page getPage() {
        return page;
    }

    public ItemStack cookItem(ItemStack i) {
        if (i == null) {
            return i;
        }
        ItemStack copy = i.clone();
        ItemMeta im = copy.getItemMeta();
        if (displayName != null)
            im.setDisplayName(Main.cmc().basicsettingnoprefix(displayName, null));
        if (description != null)
            im.setLore(Main.cmc().basicsettingnoprefix(description, null));
        copy.setItemMeta(im);
        return copy;
    }

    @FunctionalInterface
    public interface ObjectAction{
        void action(VisibleObject vi, ClickType clickType);
    }
    @FunctionalInterface
    public interface UpdateAction{
        void action(VisibleObject vi);
    }

}
