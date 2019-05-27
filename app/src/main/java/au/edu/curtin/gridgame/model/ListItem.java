package au.edu.curtin.gridgame.model;

public class ListItem
{
    // description, health/poison, mass, value
    private String description;
    private String healthPoison;
    private String mass;
    private String value;
    private Item item;

    public ListItem(Item item, String description, String healthPoison, String mass, String value)
    {
        this.description = description;
        this.healthPoison = healthPoison;
        this.mass = mass;
        this.value = value;
        this.item = item;
    }

    public String getDescription()
    {
        return description;
    }

    public String getHealthPoison()
    {
        return healthPoison;
    }

    public String getMass()
    {
        return mass;
    }

    public String getValue()
    {
        return value;
    }

    public Item getItem()
    {
        return this.item;
    }
}