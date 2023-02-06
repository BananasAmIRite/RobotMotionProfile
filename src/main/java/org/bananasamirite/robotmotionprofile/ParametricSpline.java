package org.bananasamirite.robotmotionprofile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParametricSpline {

    private final List<TimedPath> paths;
    private final double totalTime; 
    private final boolean reversed; 

    private ParametricSpline(List<TimedPath> paths, boolean reversed) {
        this.paths = paths;
        this.reversed = reversed; 
        this.totalTime = this.paths.stream().reduce(0d, (partial, path) -> partial + path.getPath().getRunTime(), Double::sum);
    }

    public double getTotalLength() {
        return getArcLengthAtTime(getTotalTime());
    }

    public double getArcLengthAtTime(double time) {
        return getArcLengthAtTime(time, 0.01);
    }

    public double getArcLengthAtTime(double time, double precision) {
        return IntegrationUtils.integrate((t) -> Math.sqrt(Math.pow(getDxAtTime(t), 2) + Math.pow(getDyAtTime(t), 2)), 0, time, precision);
    }

    public double getTotalTime() {
        return totalTime; 
    }

    private TimedPath getPathAtTime(double time) {
        if (reversed) time = this.totalTime - time; 
        double totalTime = 0;
        for (TimedPath p : paths) {
            totalTime += p.getPath().getRunTime();
            if (totalTime >= time) return p;
        }
        throw new RuntimeException(); // quite literally
    }

    public double getXAtTime(double time) {
        TimedPath path = getPathAtTime(time);
        return path.getPath().getXAt(time - path.getStartTime());
    }

    public double getYAtTime(double time) {
        TimedPath path = getPathAtTime(time);
        return path.getPath().getYAt(time - path.getStartTime());
    }

    public double getDxAtTime(double time) {
        TimedPath path = getPathAtTime(time);
        return path.getPath().getDxAt(time - path.getStartTime());
    }

    public double getDyAtTime(double time) {
        TimedPath path = getPathAtTime(time);
        return path.getPath().getDyAt(time - path.getStartTime());
    }

    public double getDdxAtTime(double time) {
        TimedPath path = getPathAtTime(time);
        return path.getPath().getDdxAt(time - path.getStartTime());
    }

    public double getDdyAtTime(double time) {
        TimedPath path = getPathAtTime(time);
        return path.getPath().getDdyAt(time - path.getStartTime());
    }

    public double signedCurvatureAt(double time) {
        return (getDxAtTime(time) * getDdyAtTime(time) + getDyAtTime(time) * getDdxAtTime(time)) / Math.pow(Math.pow(getDxAtTime(time), 2) + Math.pow(getDyAtTime(time), 2), 3.0/2);
    }

    public double signedRadiusAt(double time) {
        return 1/signedCurvatureAt(time);
    }

    public Waypoint getLastWaypointAtTime(double time) {
        return getPathAtTime(time).getPath().getStartPoint();
    }

    public boolean isReversed() {
        return reversed; 
    }

    public static ParametricSpline fromWaypoints(Waypoint[] waypoints) {
        return fromWaypoints(waypoints, false); 
    }

    public static ParametricSpline fromWaypoints(List<Waypoint> waypoints) {
        return fromWaypoints(waypoints, false); 
    }

    public static ParametricSpline fromWaypoints(Waypoint[] waypoints, boolean reversed) {
        return fromWaypoints(Arrays.stream(waypoints).collect(Collectors.toList()));
    }

    public static ParametricSpline fromWaypoints(List<Waypoint> waypoints, boolean reversed) {
        List<TimedPath> paths = new ArrayList<>(); 
        waypoints = new ArrayList<>(waypoints);
        if (reversed) Collections.reverse(waypoints);
        double timeSoFar = 0;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Path p = new Path(waypoints.get(i), waypoints.get(i+1));
            paths.add(new TimedPath(p, timeSoFar));
            timeSoFar += p.getRunTime();
        }
        return new ParametricSpline(paths, reversed);
    }
}
