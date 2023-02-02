package org.bananasamirite.robotmotionprofile.data.waypoint; 

import org.bananasamirite.robotmotionprofile.Waypoint;
import org.bananasamirite.robotmotionprofile.TankMotionProfile;

import java.util.*; 

public class CommandWaypoint extends Waypoint {
    private String commandName;
    private List<Object> parameters;

    public CommandWaypoint() {}

    public CommandWaypoint(double x, double y, double angle, double weight, double runTime, String commandName, Object... parameters) {
        this(x, y, angle, weight, runTime, new TankMotionProfile.TankMotionProfileConstraints(0, 0), commandName, parameters); 
    }

    public CommandWaypoint(double x, double y, double angle, double weight, double runTime, TankMotionProfile.TankMotionProfileConstraints constraints, String commandName, Object... parameters) {
        super(x, y, angle, weight, runTime, constraints); 
        this.commandName = commandName;
        this.parameters = Arrays.asList(parameters);
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }
}