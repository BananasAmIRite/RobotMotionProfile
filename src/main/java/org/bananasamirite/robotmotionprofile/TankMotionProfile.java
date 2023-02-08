package org.bananasamirite.robotmotionprofile;

import org.bananasamirite.robotmotionprofile.geometry.Position;

import java.util.ArrayList;
import java.util.List;

public class TankMotionProfile {
    private final List<MotionProfileNode> nodes;
    private ParametricSpline spline;

    public TankMotionProfile(ParametricSpline spline, ProfileMethod type, TankMotionProfileConstraints constraints) {
        this.spline = spline;
        this.nodes = type == ProfileMethod.DISTANCE ? calculateDistanceMotionProfile(spline, constraints, 1E-3) : calculateTimeMotionProfile(spline, constraints, 1E-3);
    }

    public TankMotionProfile(List<MotionProfileNode> nodes) {
        this.nodes = nodes;
    }

    private List<MotionProfileNode> calculateTimeMotionProfile(ParametricSpline spline, TankMotionProfileConstraints constraints, double timeSize) {
        List<MotionProfileNode> nodes = new ArrayList<>();

        {
            // NOTE: find the velocities first, then do acceleration and time
            // forward pass velocity
            double distTravelled = 0; 
            MotionProfileNode lastNode = new MotionProfileNode(0, 0,
                    new Position(
                            spline.getXAtTime(0),
                            spline.getYAtTime(0),
                            Math.atan2(spline.getDyAtTime(0), spline.getDxAtTime(0))
                    ),
                    spline.signedCurvatureAt(0), 0, 0, 0, 0
            );
            nodes.add(lastNode);

            for (double i = timeSize; i < spline.getTotalTime(); i += timeSize) {

                TankMotionProfile.TankMotionProfileConstraints pointConstraints = spline.getLastWaypointAtTime(i).getConstraints();

                TankMotionProfile.TankMotionProfileConstraints finalConstraint = pointConstraints.getMaxAcceleration() == 0 ? constraints : pointConstraints;

                double nodeLength = Math.sqrt(
                        Math.pow(spline.getYAtTime(i) - spline.getYAtTime(i-timeSize), 2) +
                                Math.pow(spline.getXAtTime(i) - spline.getXAtTime(i-timeSize), 2)
                );
                distTravelled += nodeLength; 

                double radius = spline.signedRadiusAt(i);

                // vf^2 = v0^2+2ad

                double newLinearVelocity = Math.sqrt(Math.pow(lastNode.velocity, 2) + 2 * finalConstraint.maxAcceleration * nodeLength);
                double angularVelocity = finalConstraint.maxVelocity / (Math.abs(radius) + 1);
                double maxLinearVelocity = finalConstraint.maxVelocity - angularVelocity;
                newLinearVelocity = Math.min(newLinearVelocity, maxLinearVelocity);

                MotionProfileNode node = new MotionProfileNode(newLinearVelocity, 0, new Position(
                                spline.getXAtTime(i),
                                spline.getYAtTime(i),
                                Math.atan2(spline.getDyAtTime(i), spline.getDxAtTime(i))
                ), 1 / radius, 0, distTravelled, i, 0);

                lastNode = node;
                nodes.add(node);
            }
        }

        // backward pass velocity
        {
            MotionProfileNode lastNode = nodes.get(nodes.size() - 1);
            lastNode.velocity = 0;
            lastNode.acceleration = 0;
            for (int i = nodes.size() - 2; i >= 0; i--) {
                MotionProfileNode curNode = nodes.get(i);
//                if (curNode.velocity - lastNode.velocity < 0) continue;
                double nodeLength = Math.sqrt(
                        Math.pow(spline.getYAtTime(lastNode.splineTime) - spline.getYAtTime(curNode.splineTime), 2) +
                                Math.pow(spline.getXAtTime(lastNode.splineTime) - spline.getXAtTime(curNode.splineTime), 2)
                );
                double newLinearVelocity = Math.sqrt(Math.pow(lastNode.velocity, 2) + 2 * constraints.maxAcceleration * nodeLength);

                double radius = spline.signedRadiusAt(curNode.splineTime);
                double angularVelocity = constraints.maxVelocity / (Math.abs(radius) + 1);
                double maxLinearVelocity = constraints.maxVelocity - angularVelocity;
                newLinearVelocity = Math.min(curNode.velocity, Math.min(newLinearVelocity, maxLinearVelocity));

                curNode.velocity = newLinearVelocity;
                lastNode = curNode;
            }
        }

        double totalTime = 0;

        for (int i = 0; i < nodes.size() - 1; i++) {
            MotionProfileNode n = nodes.get(i);
            MotionProfileNode nextNode = nodes.get(i+1);

            double ds = nextNode.distanceTravelled - n.distanceTravelled;

            // a = (vf^2-v0^2)/2d
            n.acceleration = (Math.pow(nextNode.velocity, 2) - Math.pow(n.velocity, 2)) / (2 * ds);
            n.time = n.acceleration == 0 ? ds / n.velocity : (nextNode.velocity - n.velocity) / n.acceleration;

            nextNode.totalTime = totalTime + n.time;
            totalTime += n.time;
        }

        if (spline.isReversed()) {
            for (MotionProfileNode n : nodes) {
                n.acceleration *= -1;
                n.velocity *= -1;
            }
        }

        return nodes;
    }

    // TODO: offload most of the computations to preprocessing somewhere else (maybe points creator program)
    // TODO: gotta apply voltage constraints as well maybe
    private List<MotionProfileNode> calculateDistanceMotionProfile(ParametricSpline spline, TankMotionProfileConstraints constraints, double nodeLength) {
        List<MotionProfileNode> nodes = new ArrayList<>();

        {
            // NOTE: find the velocities first, then do acceleration and time
            // forward pass velocity
            MotionProfileNode lastNode = new MotionProfileNode(0, 0,
                    new Position(
                            spline.getXAtTime(0),
                            spline.getYAtTime(0),
                            Math.atan2(spline.getDyAtTime(0), spline.getDxAtTime(0))
                    ),
                    spline.signedCurvatureAt(0), 0, 0, 0, 0
            );
            nodes.add(lastNode);

            for (double i = nodeLength; i < spline.getTotalLength(); i += nodeLength) {
                double splineTime = calculateTimeFromSplineDistance(spline, i, 1E-1);
                double radius = spline.signedRadiusAt(splineTime);

                // vf^2 = v0^2+2ad
                double newLinearVelocity = Math.sqrt(Math.pow(lastNode.velocity, 2) + 2 * constraints.maxAcceleration * nodeLength);
                double angularVelocity = constraints.maxVelocity / (Math.abs(radius) + 1);
                double maxLinearVelocity = constraints.maxVelocity - angularVelocity;
                newLinearVelocity = Math.min(newLinearVelocity, maxLinearVelocity);

                MotionProfileNode node = new MotionProfileNode(newLinearVelocity, 0, new Position(
                                spline.getXAtTime(splineTime),
                                spline.getYAtTime(splineTime),
                                Math.atan2(spline.getDyAtTime(splineTime), spline.getDxAtTime(splineTime))
                ), 1 / radius, 0, i, splineTime, 0);

                lastNode = node;
                nodes.add(node);
            }
        }

        // backward pass velocity
        {
            MotionProfileNode lastNode = nodes.get(nodes.size() - 1);
            lastNode.velocity = 0;
            lastNode.acceleration = 0;
            for (int i = nodes.size() - 2; i >= 0; i--) {
                MotionProfileNode curNode = nodes.get(i);
//                if (curNode.velocity - lastNode.velocity < 0) continue;
                double newLinearVelocity = Math.sqrt(Math.pow(lastNode.velocity, 2) + 2 * constraints.maxAcceleration * nodeLength);

                double radius = spline.signedRadiusAt(curNode.splineTime);
                double angularVelocity = constraints.maxVelocity / (Math.abs(radius) + 1);
                double maxLinearVelocity = constraints.maxVelocity - angularVelocity;
                newLinearVelocity = Math.min(curNode.velocity, Math.min(newLinearVelocity, maxLinearVelocity));

                curNode.velocity = newLinearVelocity;
                lastNode = curNode;
            }
        }

        double totalTime = 0;

        for (int i = 0; i < nodes.size() - 1; i++) {
            MotionProfileNode n = nodes.get(i);
            MotionProfileNode nextNode = nodes.get(i+1);

            double ds = nextNode.distanceTravelled - n.distanceTravelled;

            // a = (vf^2-v0^2)/2d
            n.acceleration = (Math.pow(nextNode.velocity, 2) - Math.pow(n.velocity, 2)) / (2 * ds);
            n.time = n.acceleration == 0 ? ds / n.velocity : (nextNode.velocity - n.velocity) / n.acceleration;

            nextNode.totalTime = totalTime + n.time;
            totalTime += n.time;
        }

        if (spline.isReversed()) {
            for (MotionProfileNode n : nodes) {
                n.acceleration *= -1;
                n.velocity *= -1;
            }
        }

        return nodes;
    }

    private double calculateTimeFromSplineDistance(ParametricSpline spline, double distance, double precision) {
        double timeLower = 0;
        double timeUpper = spline.getTotalTime();
        while (true) {
            double time = (timeLower + timeUpper) / 2;
            double dist = spline.getArcLengthAtTime(time, precision);
            System.out.println(Math.abs(timeUpper - timeLower));
            if (Math.abs(distance - dist) <= precision || Math.abs(timeUpper - timeLower) < 1E-9) return time; // either we've zeroed in on a time or the distance is precise enough
            if (distance - dist < 0) timeUpper = time;
            if (distance - dist > 0) timeLower = time;
        }
    }

    public List<MotionProfileNode> getNodes() {
        return this.nodes;
    }

    @Deprecated
    public MotionProfileState getStateAtTime_old(double time) {
        double totalTime = 0;
        for (MotionProfileNode node : this.nodes) {
            if (totalTime <= time && totalTime + node.time >= time) {
                double dt = time - totalTime;
                double ds = node.velocity * dt + node.acceleration * dt * dt / 2;
                double splineTime = calculateTimeFromSplineDistance(spline, node.distanceTravelled + ds, 1E-1);

                return new MotionProfileState(
                        node.velocity + node.acceleration * dt,
                        dt,
                        time,
                        new Position(
                                spline.getXAtTime(splineTime), spline.getYAtTime(splineTime),
                                Math.atan2(spline.getDyAtTime(splineTime), spline.getDxAtTime(splineTime))
                        ),
                        spline.signedCurvatureAt(splineTime), node.acceleration
                );
            }
            totalTime += node.time;
        }
        return this.nodes.get(this.nodes.size() - 1).asState();
    }

    // less expensive to calculate, more preprocessing (small nodeLength) required for good results
    // simulates robot position using the average angular accelerations and velocities, which generates a curve close to the original curve
    // good thing is, this works without the original spline, so we can preprocess elsewhere and save to a file
    public MotionProfileState getStateAtTime(double time) {
        double totalTime = 0;  
        for (int i = 0; i < this.nodes.size()-1; i++) {
            MotionProfileNode node = this.nodes.get(i);
            MotionProfileNode nextNode = this.nodes.get(i+1);
            if (totalTime <= time && totalTime + node.time >= time) {
                double dt = time - totalTime;
                if (dt == 0) return node.asState();

                // https://www.desmos.com/calculator/zpzf9dpnh2

                double angularVelocity = node.velocity * node.curvature;
                double angularAcceleration = (nextNode.velocity * nextNode.curvature - node.velocity * node.curvature) / (node.time);

                double dx = IntegrationUtils.integrate((t) -> (node.velocity + node.acceleration * t) *
                        Math.cos(node.pose.getRotation() + angularVelocity * t + angularAcceleration * t * t / 2), 0, dt, dt*1E-2);
                double dy = IntegrationUtils.integrate((t) -> (node.velocity + node.acceleration * t) *
                        Math.sin(node.pose.getRotation() + angularVelocity * t + angularAcceleration * t * t / 2), 0, dt, dt*1E-2);

                double vx = (node.velocity + node.acceleration * dt) *
                        Math.cos(node.pose.getRotation() + angularVelocity * dt + angularAcceleration * dt * dt / 2);
                double vy = (node.velocity + node.acceleration * dt) *
                        Math.sin(node.pose.getRotation() + angularVelocity * dt + angularAcceleration * dt * dt / 2);

                double ax = node.acceleration * Math.cos(node.pose.getRotation() + angularVelocity * dt + angularAcceleration * dt * dt / 2)
                        - (node.velocity + node.acceleration * dt)*(angularVelocity+angularAcceleration * dt) * Math.sin(node.pose.getRotation() + angularVelocity * dt + angularAcceleration * dt * dt / 2);
                double ay = node.acceleration * Math.sin(node.pose.getRotation() + angularVelocity * dt + angularAcceleration * dt * dt / 2)
                        + (node.velocity + node.acceleration * dt)*(angularVelocity+angularAcceleration * dt) * Math.cos(node.pose.getRotation() + angularVelocity * dt + angularAcceleration * dt * dt / 2);

                return new MotionProfileState(
                        node.velocity + node.acceleration * dt,
                        dt,
                        time,
                        new Position(node.pose.getX() + dx, node.pose.getY() + dy,
                                Math.atan2(vy, vx) + (this.spline.isReversed() ? Math.PI : 0) // if the direction is reversed, the robot's heading will be 180 degrees the robot's velocity vector
                        ),
                        (vx*ay-vy*ax)/Math.pow(vx*vx+vy*vy, 3/2.0),
                        node.acceleration
                );
            }
            totalTime += node.time;
        }
        return this.nodes.get(this.nodes.size() - 1).asState();
    }

    public double getTotalTime() {
        MotionProfileNode lastNode = this.nodes.get(this.nodes.size() - 1) 
        return lastNode.totalTime + lastNode.time; 
    }

    public static class MotionProfileState {
        private final double velocity;
        private final double time;
        private final double totalTime;
        private final Position pose;
        private final double curvature;
        private final double acceleration;

        public MotionProfileState(double velocity, double time, double totalTime, Position pose, double curvature, double acceleration) {
            this.velocity = velocity;
            this.time = time;
            this.totalTime = totalTime;
            this.pose = pose;
            this.curvature = curvature;
            this.acceleration = acceleration;
        }

        public double getVelocity() {
            return velocity;
        }

        public Position getPose() {
            return pose;
        }

        public double getTime() {
            return time;
        }

        public double getCurvature() {
            return curvature;
        }

        public double getTotalTime() {
            return totalTime;
        }

        public double getAcceleration() {
            return acceleration;
        }

        @Override
        public String toString() {
            return "MotionProfileState{" +
                    "velocity=" + velocity +
                    ", pose=" + pose +
                    ", acceleration=" + acceleration +
                    '}';
        }
    }

    public static class MotionProfileNode {
        private double velocity;
        private double time;
        private Position pose;
        private double curvature;
        private double distanceTravelled; 
        private double acceleration; 
        private double splineTime;
        private double totalTime;

        public MotionProfileNode() {}

        public MotionProfileNode(double velocity, double time, Position pose, double curvature, double acceleration, double distanceTravelled, double splineTime, double totalTime) {
            this.velocity = velocity;
            this.time = time;
            this.pose = pose;
            this.curvature = curvature;
            this.acceleration = acceleration;
            this.distanceTravelled = distanceTravelled; 
            this.splineTime = splineTime;
            this.totalTime = totalTime;
        }

        public double getVelocity() {
            return velocity;
        }

        public double getAcceleration() {
            return acceleration;
        }

        public double getTime() {
            return time;
        }

        public Position getPose() {
            return pose;
        }

        public double getCurvature() {
            return curvature;
        }

        public double getTotalTime() {
            return totalTime;
        }

        public void setVelocity(double velocity) {
            this.velocity = velocity;
        }

        public void setAcceleration(double acceleration) {
            this.acceleration = acceleration;
        }

        public void setCurvature(double curvature) {
            this.curvature = curvature;
        }

        public void setDistanceTravelled(double distanceTravelled) {
            this.distanceTravelled = distanceTravelled;
        }

        public void setPose(Position pose) {
            this.pose = pose;
        }

        public void setSplineTime(double splineTime) {
            this.splineTime = splineTime;
        }

        public void setTotalTime(double totalTime) {
            this.totalTime = totalTime;
        }

        public void setTime(double time) {
            this.time = time;
        }

        public MotionProfileState asState() {
            return new MotionProfileState(velocity, time, totalTime, pose, curvature, acceleration);
        }

        @Override
        public String toString() {
            return "MotionProfileNode{" +
                    "velocity=" + velocity +
                    ", time=" + time +
                    ", pose=" + pose +
                    ", curvature=" + curvature +
                    ", acceleration=" + acceleration +
                    ", distanceTravelled=" + distanceTravelled +
                    ", splineTime=" + splineTime +
                    ", totalTime=" + totalTime +
                    '}';
        }
    }

    public static class TankMotionProfileConstraints {
        private double maxVelocity;
        private double maxAcceleration;

        public TankMotionProfileConstraints() {}
        public TankMotionProfileConstraints(double maxVel, double maxAccel) {
            this.maxVelocity = maxVel;
            this.maxAcceleration = maxAccel;
        }

        public double getMaxAcceleration() {
            return maxAcceleration;
        }

        public double getMaxVelocity() {
            return maxVelocity;
        }

        public void setMaxAcceleration(double maxAcceleration) {
            this.maxAcceleration = maxAcceleration;
        }

        public void setMaxVelocity(double maxVelocity) {
            this.maxVelocity = maxVelocity;
        }
    }

    public enum ProfileMethod {
        DISTANCE, 
        TIME
    }
}
