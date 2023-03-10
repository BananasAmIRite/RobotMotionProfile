package org.bananasamirite.robotmotionprofile.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bananasamirite.robotmotionprofile.ParametricSpline;
import org.bananasamirite.robotmotionprofile.TankMotionProfile;
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

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

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

    // TODO: phase out legacy system in favor of JUST parsing every time cuz its better
    public static Trajectory parseFromFile(File file) throws IOException {
        Trajectory trajectory = fromFile(file);
        return fromWaypoint(trajectory.waypoints, trajectory.config);
    }

    public static Trajectory fromWaypoint(List<Waypoint> waypoints, RobotConfiguration config) {
        List<TrajectoryTask> tasks = new ArrayList<>(); 
        List<Waypoint> currentSplinePoints = new ArrayList<>(); 
        for (Waypoint currentWaypoint : waypoints) {
            if (currentSplinePoints.size() != 0 && (currentWaypoint instanceof CommandWaypoint || 
            ((SplineWaypoint) currentSplinePoints.get(0)).isReversed() != ((SplineWaypoint) currentWaypoint).isReversed())) {
                currentSplinePoints.add(currentWaypoint); 
                // unfill the list into a spline
                WaypointTask t = new WaypointTask(currentSplinePoints, ProfileMethod.TIME, config.getConstraints(), ((SplineWaypoint) currentSplinePoints.get(0)).isReversed()); 
                tasks.add(t); 
                currentSplinePoints = new ArrayList<>(); 
            }

            if (currentWaypoint instanceof CommandWaypoint) tasks.add(new CommandTask((CommandWaypoint) currentWaypoint)); 
            if (currentWaypoint instanceof SplineWaypoint) currentSplinePoints.add(currentWaypoint); 
        }
        if (currentSplinePoints.size() != 0) tasks.add(new WaypointTask(currentSplinePoints, ProfileMethod.TIME, config.getConstraints(), ((SplineWaypoint) currentSplinePoints.get(0)).isReversed())); 
        return new Trajectory(waypoints, tasks, config); 
    }

    public String toJsonString() throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }

    public void toJsonFile(File f) throws IOException {
        mapper.writeValue(f, this);
    }
}
