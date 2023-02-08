package org.bananasamirite.robotmotionprofile.data.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.bananasamirite.robotmotionprofile.ParametricSpline;
import org.bananasamirite.robotmotionprofile.TankMotionProfile;
import org.bananasamirite.robotmotionprofile.Waypoint;
import org.bananasamirite.robotmotionprofile.data.waypoint.CommandWaypoint;
import org.bananasamirite.robotmotionprofile.data.waypoint.SplineWaypoint;

import java.util.ArrayList;
import java.util.List;

public class WaypointTask extends TrajectoryTask {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
            property = "type") @JsonSubTypes({
            @JsonSubTypes.Type(value = CommandWaypoint.class, name = "COMMAND"),
            @JsonSubTypes.Type(value = SplineWaypoint.class, name = "SPLINE"),
    })
    private List<Waypoint> waypoints = new ArrayList<>();
    private TankMotionProfile.ProfileMethod method;
    private TankMotionProfile.TankMotionProfileConstraints constraints;
    private boolean reversed; 

    public WaypointTask() {}

    public WaypointTask(List<Waypoint> waypoints, TankMotionProfile.ProfileMethod method, TankMotionProfile.TankMotionProfileConstraints constraints, boolean reversed) {
        this.waypoints = waypoints;
        this.method = method;
        this.constraints = constraints;
        this.reversed = reversed;
        System.out.println(reversed);
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Waypoint> waypoints) {
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

    public ParametricSpline getSpline() {
        return ParametricSpline.fromWaypoints(waypoints, reversed);
    }

    public boolean getReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public TankMotionProfile createProfile() {
        return new TankMotionProfile(getSpline(), method, constraints);
    }
}
