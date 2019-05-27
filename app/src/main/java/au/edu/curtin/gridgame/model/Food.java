package au.edu.curtin.gridgame.model;

public class Food extends Item
{
    private double health;
    private double poison;

    // Poison describes the percentage chance of the food being poisonous
    public Food(double health, double poison, int value, String description)
    {
        this.health = health;
        this.poison = poison;

        super.setValue(value);
        super.setDescription(description);
    }

    public double getPoison()
    {
        return poison;
    }

    public void setPoison(double poison)
    {
        this.poison = poison;
    }

    public double getHealth()
    {
        return health;
    }

    public void setHealth(double health)
    {
        this.health = health;
    }
}
