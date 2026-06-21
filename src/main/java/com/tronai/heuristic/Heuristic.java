package com.tronai.heuristic;


import com.tronai.model.Game;
import com.tronai.model.Player;
import com.tronai.util.Move;
import com.tronai.util.Position;

import java.util.List;

// Heuristic.java
public interface Heuristic {
    int evaluatePosition(Game game, Position pos);
    List<Move> suggestMoves(Game game, Player player);
}
