package com.tronai.model;

import java.util.ArrayList;
import java.util.List;

import com.tronai.util.AlgorithmType;

public class Team {

  private final int id;
  private final String name;
  private final List<Player> players;
  private AlgorithmType algo = AlgorithmType.MAXN;

  public Team(int id, String name) {
    this.id = id;
    this.name = name;
    this.players = new ArrayList<>();
  }

  public void addPlayer(Player player) {
    players.add(player);
  }

  public void setAlgo(AlgorithmType algo) {
    this.algo = algo;
  }

  public AlgorithmType getAlgo(){
      return algo;
  }
  public List<Player> getPlayers() {
    return players;
  }

  public boolean hasAlivePlayers() {
    return players.stream().anyMatch(Player::isAlive);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

}
