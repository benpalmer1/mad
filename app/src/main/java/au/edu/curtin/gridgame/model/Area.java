package au.edu.curtin.gridgame.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Area implements Serializable
{
    private boolean town;
    private int terrain;
    private String description;
    private boolean starred;
    private boolean explored;
    private List<Item> items;

    public Area(boolean isTown, int terrain)
    {
        this.terrain = terrain;
        this.town = isTown;
        items = new ArrayList<>();
    }

    public boolean isTown()
    {
        return town;
    }

    public int getTerrain()
    {
        return this.terrain;
    }

    public void setTown(boolean town)
    {
        this.town = town;
    }

    public List<Item> getItems()
    {
        return items;
    }

    public void setItems(List<Item> items)
    {
        this.items = items;
    }

    public void addItem(Item item)
    {
        items.add(item);
    }

    public void addItems(List<Item> items)
    {
        this.items.addAll(items);
    }

    public void removeItem(Item item)
    {
        items.remove(item);
    }
}