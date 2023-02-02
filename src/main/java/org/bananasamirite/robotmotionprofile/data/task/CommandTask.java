package org.bananasamirite.robotmotionprofile.data.task;

import java.util.Arrays;
import java.util.List;

import org.bananasamirite.robotmotionprofile.data.waypoint.CommandWaypoint;

public class CommandTask extends TrajectoryTask {
    private CommandWaypoint waypoint; 

    public CommandTask() {}

    public void setWaypoint(CommandWaypoint waypoint) {
        this.waypoint = waypoint; 
    }

    public CommandWaypoint getWaypoint() {
        return this.waypoint; 
    }
}
