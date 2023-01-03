package org.bananasamirite.robotmotionprofile.geometry;

public class Position {
    private final double x;
    private final double y;
    private final double rotation;

    public Position(double x, double y, double rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRotation() {
        return rotation;
    }
}
