package org.bananasamirite.robotmotionprofile;

public class Waypoint {
    protected double runTime;
    protected double x;
    protected double y;
    protected double angle;
    protected double weight;
    protected TankMotionProfile.TankMotionProfileConstraints constraints; 

    public Waypoint(double x, double y, double angle, double weight, double runTime, protected TankMotionProfile.TankMotionProfileConstraints constraints) {
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
}
