package au.edu.curtin.gridgame.model;

import java.io.Serializable;
import java.util.List;

public class Player implements Serializable
{
    private int rowLocation;
    private int colLocation;
    private int cash;
    private double health;
    private List<Equipment> equipment;

    public Player(int colLocation, int rowLocation, int cash, double health, double equipmentMass, List<Equipment> equipment)
    {
        this.rowLocation = rowLocation;
        this.colLocation = colLocation;
        this.cash = cash;
        this.health = health;
        this.equipment = equipment;
    }

    // Consumes a food object.
    // Returns the players health after eating the food.
    public double eatFood(Food food)
    {
        //Percentage chance of poisoning given.
        if(food.getPoison() > 0)
        {
            if(Double.valueOf(Math.random()*100+1) < food.getPoison()) // Poisoned
            {
                this.health = Math.max(this.health - (food.getHealth()/2), 0.00); ; // Minus half of the original health benefit.
            }
            else // Not poisoned
            {
                this.health = Math.min(this.health + food.getHealth(), 100.00);
            }
        }
        else // Guaranteed not poisonous.
        {
            this.health  = Math.min(this.health + food.getHealth(), 100.00);
        }

        return this.health;
    }

    public int getRowLocation() {
        return rowLocation;
    }
    public void setRowLocation(int rowLocation) {
        this.rowLocation = rowLocation;
    }

    public int getColLocation() {
        return colLocation;
    }
    public void setColLocation(int colLocation) {
        this.colLocation = colLocation;
    }

    public int getCash() {
        return cash;
    }
    public void setCash(int cash) {
        this.cash = cash;
    }

    public double getHealth() {
        return health;
    }
    public void setHealth(double health) {
        this.health = health;
    }

    public double getEquipmentMass()
    {
        double mass = 0.0;

        for (Equipment e : equipment) {
            mass += e.getMass();
        }

        return mass;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }
    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    public void addEquipment(Equipment newEquipment)
    {
        this.equipment.add(newEquipment);
    }
    public void removeEquipment(Equipment remEquipment)
    {
        this.equipment.remove(remEquipment);
    }
}