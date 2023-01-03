package org.bananasamirite.robotmotionprofile;

public class Waypoint {
    protected double runTime;
    protected double x;
    protected double y;
    protected double angle;
    protected double weight;

    public Waypoint(double x, double y, double angle, double weight, double runTime) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.weight = weight;
        this.runTime = runTime;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public double getWeight() {
        return weight;
    }

    public double getRunTime() {
        return runTime;
    }
}
