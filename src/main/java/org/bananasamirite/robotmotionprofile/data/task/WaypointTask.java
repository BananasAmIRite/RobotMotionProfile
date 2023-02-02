package org.bananasamirite.robotmotionprofile.data.task;

import org.bananasamirite.robotmotionprofile.ParametricSpline;
import org.bananasamirite.robotmotionprofile.TankMotionProfile;
import org.bananasamirite.robotmotionprofile.Waypoint;
import org.bananasamirite.robotmotionprofile.data.waypoint.SplineWaypoint;

import java.util.ArrayList;
import java.util.List;

public class WaypointTask extends TrajectoryTask {

    private List<SplineWaypoint> waypoints = new ArrayList<>();
    private TankMotionProfile.ProfileMethod method;
    private TankMotionProfile.TankMotionProfileConstraints constraints;

    public WaypointTask() {}

    public WaypointTask(List<SplineWaypoint> waypoints, TankMotionProfile.ProfileMethod method, TankMotionProfile.TankMotionProfileConstraints constraints) {
        this.waypoints = waypoints;
        this.method = method;
        this.constraints = constraints;
    }

    public List<SplineWaypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<SplineWaypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public TankMotionProfile.TankMotionProfileConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(TankMotionProfile.TankMotionProfileConstraints constraints) {
        this.constraints = constraints;
    }

    public TankMotionProfile.ProfileMethod getMethod() {
        return method;
    }

    public void setMethod(TankMotionProfile.ProfileMethod method) {
        this.method = method;
    }

    public TankMotionProfile createProfile() {
        return new TankMotionProfile(ParametricSpline.fromWaypoints(waypoints), method, constraints);
    }
}
