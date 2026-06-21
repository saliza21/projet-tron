package com.tronai.model;

import com.tronai.util.Position;

public class Player {

    private final int id;
    private String name;
    private Position position;
    private final Team team;
    private boolean isAlive;

    public Player(int id, Position position, String name,Team team) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.team = team;
        this.isAlive = true;
    }

    public int getId() {
        return id;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void die() {
        this.isAlive = false;
    }

    public boolean equals(Player obj) {
        return this.team.getId()==obj.team.getId() && this.id == obj.id && this.name == obj.name && this.position.equals(obj.position);
    }

}
