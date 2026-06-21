package com.tronai.util;

public class Move {

    public final int dx;
    public final int dy;

    public Move(Direction dir) {
        this.dx = dir.dx;
        this.dy = dir.dy;
    }

    public Direction getDirection() {
        for (Direction direction : Direction.values()) {
            if (direction.dx == this.dx && direction.dy == this.dy) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid move direction: (" + dx + ", " + dy + ")");
    }

}