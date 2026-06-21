package com.tronai.algo;

import com.tronai.model.Game;
import com.tronai.model.Player;
import com.tronai.util.Move;

import java.util.List;

public class MaxN implements IAI {
    private static final int MAX_DEPTH = 5;

    public Move findBestMove(Game game, Player currentPlayer) {
        return maxn(game, currentPlayer, 0).move;
    }

    private Result maxn(Game game, Player currentPlayer, int depth) {
        if (depth == MAX_DEPTH || game.isGameOver()) {
            double score = evaluateState(game, currentPlayer);
            return new Result(null, score);
        }

        Move bestMove = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        List<Move> possibleMove =  game.getPossibleMoves(currentPlayer);
        for (Move move : possibleMove) {
            game.makeMove(move);
            Result result = maxn(game, game.getNextPlayer(), depth + 1);
            game.undoMove();

            if (result.score > bestScore) {
                bestScore = result.score;
                bestMove = move;
            }
        }
        return !possibleMove.isEmpty()?new Result(possibleMove.get(0),bestScore) :new Result(bestMove, bestScore);
    }

    private double evaluateState(Game game, Player player) {
        return game.getControlledAreaBy(player) / (game.getWidth() * game.getHeight());
    }

    private static class Result {
        Move move;
        double score;

        Result(Move move, double score) {
            this.move = move;
            this.score = score;
        }
    }
}