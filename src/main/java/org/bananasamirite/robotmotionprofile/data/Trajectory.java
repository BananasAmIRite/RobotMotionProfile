package org.bananasamirite.robotmotionprofile.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bananasamirite.robotmotionprofile.ParametricSpline;
import org.bananasamirite.robotmotionprofile.TankMotionProfile;
import org.bananasamirite.robotmotionprofile.Waypoint;
import org.bananasamirite.robotmotionprofile.data.task.CommandTask;
import org.bananasamirite.robotmotionprofile.data.task.GeneratedWaypointTask;
import org.bananasamirite.robotmotionprofile.data.task.TrajectoryTask;
import org.bananasamirite.robotmotionprofile.data.task.WaypointTask;

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

    private RobotConfiguration config; 

    public Trajectory() {}

    public Trajectory(List<TrajectoryTask> tasks, RobotConfiguration config) {
        this.tasks = tasks;
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

    public static Trajectory fromFile(File file) throws IOException {
        return mapper.readValue(file, Trajectory.class);
    }

    // TODO: remove testing; this just for reference rn
    public static void main(String[] args) throws JsonProcessingException {
        List<TrajectoryTask> tasks = new ArrayList<>();
        tasks.add(new CommandTask("testCommand", "a", 1.0, 2));
        List<Waypoint> waypoints = List.of(new Waypoint(0, 0, 0, 1, 1), new Waypoint(1, 1, Math.toRadians(90), 1, 1));
        TankMotionProfile.TankMotionProfileConstraints constraints = new TankMotionProfile.TankMotionProfileConstraints(1, 1);
        TankMotionProfile p = new TankMotionProfile(ParametricSpline.fromWaypoints(waypoints), TankMotionProfile.ProfileMethod.TIME, constraints);
        tasks.add(new WaypointTask(waypoints, TankMotionProfile.ProfileMethod.TIME, constraints));
        tasks.add(new GeneratedWaypointTask(waypoints, TankMotionProfile.ProfileMethod.TIME, constraints, p.getNodes()));
        String t = mapper.writeValueAsString(new Trajectory(tasks, new RobotConfiguration(
            5, 5, 
            new TankMotionProfile.TankMotionProfileConstraints(1, 1)
        )));
        System.out.println(t);
    }
}
