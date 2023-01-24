package org.bananasamirite.robotmotionprofile.data;

import org.bananasamirite.robotmotionprofile.TankMotionProfile;

public class RobotConfiguration {

    private double dimensionX; 
    private double dimensionY; 
    private TankMotionProfile.TankMotionProfileConstraints constraints;

    public RobotConfiguration() {}

    public RobotConfiguration(double dimensionX, double dimensionY, TankMotionProfile.TankMotionProfileConstraints constraints) {
        this.dimensionX = dimensionX; 
        this.dimensionY = dimensionY; 
        this.constraints = constraints; 
    }

    public double getDimensionX() {
        return dimensionX; 
    }

    public void setDimensionX(double x) {
        this.dimensionX = x; 
    }

    public double getDimensionY() {
        return dimensionY; 
    }

    public void setDimensionY(double y) {
        this.dimensionY = y; 
    }

    public TankMotionProfile.TankMotionProfileConstraints getConstraints() {
        return constraints; 
    }

    public void setConstraints(TankMotionProfile.TankMotionProfileConstraints constraints) {
        this.constraints = constraints; 
    }
}