package au.edu.curtin.gridgame.model;

import java.io.Serializable;

public abstract class Item implements Serializable
{
    private String description;
    private int value;

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}