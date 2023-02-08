package org.bananasamirite.robotmotionprofile.data.task;

import org.bananasamirite.robotmotionprofile.TankMotionProfile;
import org.bananasamirite.robotmotionprofile.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class GeneratedWaypointTask extends WaypointTask {

    private List<TankMotionProfile.MotionProfileNode> nodes = new ArrayList<>();

    public GeneratedWaypointTask() {}

    public GeneratedWaypointTask(List<Waypoint> waypoints, TankMotionProfile.ProfileMethod method, TankMotionProfile.TankMotionProfileConstraints constraints, List<TankMotionProfile.MotionProfileNode> nodes, boolean reversed) {
        super(waypoints, method, constraints, reversed);
        this.nodes = nodes;
    }

    public List<TankMotionProfile.MotionProfileNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<TankMotionProfile.MotionProfileNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public TankMotionProfile createProfile() {
        return new TankMotionProfile(this.nodes);
    }
}
