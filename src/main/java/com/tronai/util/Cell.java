package com.tronai.util;

import com.tronai.model.Player;

public class Cell {

    private Boolean isEmpty;
    private Boolean isPlayer;
    private Player owner;

    public Cell() {
        isEmpty = true;
        isPlayer = false;
    }

    public Cell(Player owner) {
        isEmpty = false;
        this.owner = owner;
    }

    public Cell(Player owner, Boolean isPlayerExist) {
        isEmpty = false;
        isPlayer = isPlayerExist;
        this.owner = owner;
    }

    public void setOwner(Player player) {
        this.owner = player;
        isEmpty = false;
    }

    public void setIsPlayer(Boolean value) {
        isPlayer = value;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

}
