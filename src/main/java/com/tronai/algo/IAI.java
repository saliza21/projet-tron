package com.tronai.algo;

import com.tronai.model.Game;
import com.tronai.model.Player;
import com.tronai.util.Move;

public interface IAI {
    Move findBestMove(Game game, Player player);
}
