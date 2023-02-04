package org.bananasamirite.robotmotionprofile.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bananasamirite.robotmotionprofile.Waypoint;
import org.bananasamirite.robotmotionprofile.TankMotionProfile.ProfileMethod;
import org.bananasamirite.robotmotionprofile.data.task.CommandTask;
import org.bananasamirite.robotmotionprofile.data.task.GeneratedWaypointTask;
import org.bananasamirite.robotmotionprofile.data.task.TrajectoryTask;
import org.bananasamirite.robotmotionprofile.data.task.WaypointTask;
import org.bananasamirite.robotmotionprofile.data.waypoint.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Trajectory {
    private static final ObjectMapper mapper = new ObjectMapper();

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
            property = "type") @JsonSubTypes({

            @JsonSubTypes.Type(value = CommandTask.class, name = "COMMAND"),
            @JsonSubTypes.Type(value = WaypointTask.class, name = "WAYPOINTS_PATH"),
            @JsonSubTypes.Type(value = GeneratedWaypointTask.class, name = "GENERATED_PATH")
    })
    private List<TrajectoryTask> tasks;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
            property = "type") @JsonSubTypes({
            @JsonSubTypes.Type(value = CommandWaypoint.class, name = "COMMAND"),
            @JsonSubTypes.Type(value = SplineWaypoint.class, name = "SPLINE"),
    })
    private List<Waypoint> waypoints; 

    private RobotConfiguration config; 

    public Trajectory() {}

    public Trajectory(List<Waypoint> waypoints, List<TrajectoryTask> tasks, RobotConfiguration config) {
        this.waypoints = waypoints; 
        this.tasks = tasks;
        this.config = config;
    }

    public List<TrajectoryTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<TrajectoryTask> tasks) {
        this.tasks = tasks;
    }

    public RobotConfiguration getConfig() {
        return config; 
    }

    public void setConfig(RobotConfiguration config) {
        this.config = config; 
    }

    public List<Waypoint> getWaypoints() {
        return this.waypoints; 
    }

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints; 
    }

    public static Trajectory fromFile(File file) throws IOException {
        return mapper.readValue(file, Trajectory.class);
    }

    public static Trajectory fromWaypoint(List<Waypoint> waypoints, RobotConfiguration config) {
        List<TrajectoryTask> tasks = new ArrayList<>(); 
        List<Waypoint> currentSplinePoints = new ArrayList<>(); 
        for (Waypoint w : waypoints) {
            if (w instanceof CommandWaypoint) {
                currentSplinePoints.add(w);
                WaypointTask t = new WaypointTask(currentSplinePoints, ProfileMethod.TIME, config.getConstraints()); 
                tasks.add(t); 
                tasks.add(new CommandTask((CommandWaypoint) w)); 
                currentSplinePoints = new ArrayList<>(); 
            } else if (w instanceof SplineWaypoint) {
                currentSplinePoints.add(w); 
            }
        }
        if (currentSplinePoints.size() != 0) tasks.add(new WaypointTask(currentSplinePoints, ProfileMethod.TIME, config.getConstraints())); 
        return new Trajectory(waypoints, tasks, config); 
    }

    public String toJsonString() throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }

    public void toJsonFile(File f) throws IOException {
        mapper.writeValue(f, this);
    }

    // TODO: remove testing; this just for reference rn
    public static void main(String[] args) throws JsonProcessingException {
        // List<TrajectoryTask> tasks = new ArrayList<>();
        // tasks.add(new CommandTask("testCommand", "a", 1.0, 2));
        // List<Waypoint> waypoints = List.of(new Waypoint(0, 0, 0, 1, 1), new Waypoint(1, 1, Math.toRadians(90), 1, 1));
        // TankMotionProfile.TankMotionProfileConstraints constraints = new TankMotionProfile.TankMotionProfileConstraints(1, 1);
        // TankMotionProfile p = new TankMotionProfile(ParametricSpline.fromWaypoints(waypoints), TankMotionProfile.ProfileMethod.TIME, constraints);
        // tasks.add(new WaypointTask(waypoints, TankMotionProfile.ProfileMethod.TIME, constraints));
        // tasks.add(new GeneratedWaypointTask(waypoints, TankMotionProfile.ProfileMethod.TIME, constraints, p.getNodes()));
        // String t = mapper.writeValueAsString(new Trajectory(tasks, new RobotConfiguration(
        //     5, 5, 
        //     new TankMotionProfile.TankMotionProfileConstraints(1, 1)
        // )));
        // System.out.println(t);
    }
}
