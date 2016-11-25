package hu.matech.FindInEngine;

/**
 * It can contain two doubles an x and a y coordinate.
 * @version 1.0
 */
public class Coordinates {
    protected double x;
    protected double y;

    /**
     * Create an instance.
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Subtracting from a copy of the instance x and y the others x and y value.The value of this object is unchanged.
     * @param b The other Coordinates.
     * @return A copy of this minus b.
     */
    public Coordinates sub(Coordinates b){
        double newX = this.x - b.x;
        double newY = this.y - b.y;
        return new Coordinates(newX, newY);
    }

    /**
     * Returns the value of x.
     * @return The value of x.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the value of x.
     * @param x The value to set.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns the value of y.
     * @return The value of y.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the value of y.
     * @param y the value to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns x and y in the format (x,y).
     * @return (x,y)
     */
    public String toString(){
        return "(" + x + "," + y + ")";
    }
}
