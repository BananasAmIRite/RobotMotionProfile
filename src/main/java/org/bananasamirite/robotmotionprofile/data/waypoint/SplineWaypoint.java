package org.bananasamirite.robotmotionprofile.data.waypoint; 

import org.bananasamirite.robotmotionprofile.TankMotionProfile;
import org.bananasamirite.robotmotionprofile.Waypoint;

public class SplineWaypoint extends Waypoint {

    public SplineWaypoint(double x, double y, double angle, double weight, double runTime, TankMotionProfile.TankMotionProfileConstraints constraints) {
        super(x, y, angle, weight, runTime, constraints);
    }
}