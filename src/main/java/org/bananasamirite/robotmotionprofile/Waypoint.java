package org.bananasamirite.robotmotionprofile;

public class Waypoint {
    protected double runTime;
    protected double x;
    protected double y;
    protected double angle;
    protected double weight;
    protected TankMotionProfile.TankMotionProfileConstraints constraints;

    public Waypoint() {}

    public Waypoint(double x, double y, double angle, double weight, double runTime) {
        this(x, y, angle, weight, runTime, new TankMotionProfile.TankMotionProfileConstraints(0, 0));
    }

    public Waypoint(double x, double y, double angle, double weight, double runTime, TankMotionProfile.TankMotionProfileConstraints constraints) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.weight = weight;
        this.runTime = runTime;
        this.constraints = constraints; 
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

    public TankMotionProfile.TankMotionProfileConstraints getConstraints() {
        return constraints; 
    }

    public void setConstraints(TankMotionProfile.TankMotionProfileConstraints constraints) {
        this.constraints = constraints;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setRunTime(double runTime) {
        this.runTime = runTime;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
