package au.edu.curtin.gridgame.model;

public class Equipment extends Item {
    private double mass;
    private boolean required; // Item must exist somewhere in the game - in order to win

    public Equipment(double mass, int value, String description, boolean required)
    {
        this.mass = mass;
        this.required = required;
        super.setValue(value);
        super.setDescription(description);
    }

    public boolean isRequired()
    {
        return this.required;
    }
    public double getMass() {
        return mass;
    }
    public void setMass(double mass) {
        this.mass = mass;
    }
}