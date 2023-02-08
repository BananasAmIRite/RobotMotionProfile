package org.bananasamirite.robotmotionprofile.data.waypoint; 

import org.bananasamirite.robotmotionprofile.TankMotionProfile;
import org.bananasamirite.robotmotionprofile.Waypoint;

public class SplineWaypoint extends Waypoint {
    private boolean reversed;

    public SplineWaypoint() {}

    public SplineWaypoint(double x, double y, double angle, double weight, double runTime, boolean reversed) {
        this(x, y, angle, weight, runTime, new TankMotionProfile.TankMotionProfileConstraints(0, 0), reversed); 
    }

    public SplineWaypoint(double x, double y, double angle, double weight, double runTime, TankMotionProfile.TankMotionProfileConstraints constraints, boolean reversed) {
        super(x, y, angle, weight, runTime, constraints); 
        this.reversed = reversed; 
    }

    public boolean isReversed() {
        return this.reversed; 
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed; 
    }
}