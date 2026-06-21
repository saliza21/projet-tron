package com.tronai.algo;

import com.tronai.model.Game;
import com.tronai.model.Player;
import com.tronai.util.Move;
import java.util.List;

public class Paranoid implements IAI {

    private static final int MAX_DEPTH = 5;

    @Override
    public Move findBestMove(Game game, Player currentPlayer) {
        return paranoid(game, currentPlayer, 0).move;
    }

    private Result paranoid(Game game, Player currentPlayer, int depth) {
        if (depth == MAX_DEPTH || game.isGameOver()) {
            double score = evaluateState(game, currentPlayer);
            return new Result(null, score);
        }

        // Phase 1: Maximiser pour le joueur courant
        if (currentPlayer.equals(game.getCurrentPlayer())) {
            return maxStep(game, currentPlayer, depth);
        }

        // Phase 2: Minimiser pour les adversaires
        else {
            return minStep(game, currentPlayer, depth);
        }
    }

    private Result maxStep(Game game, Player currentPlayer, int depth) {
        Move bestMove = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        List<Move> possibleMoves = game.getPossibleMoves(currentPlayer);
        for (Move move : possibleMoves) {
            game.makeMove(move);
            Result result = paranoid(game, game.getNextPlayer(), depth + 1);
            game.undoMove();

            if (result.score > bestScore) {
                bestScore = result.score;
                bestMove = move;
            }
        }

        return new Result(bestMove, bestScore);
    }

    private Result minStep(Game game, Player currentPlayer, int depth) {
        Move worstMove = null;
        double worstScore = Double.POSITIVE_INFINITY;

        List<Move> possibleMoves = game.getPossibleMoves(game.getCurrentPlayer());
        for (Move move : possibleMoves) {
            game.makeMove(move);
            Result result = paranoid(game, game.getNextPlayer(), depth + 1);
            game.undoMove();

            if (result.score < worstScore) {
                worstScore = result.score;
                worstMove = move;
            }
        }

        return new Result(worstMove, worstScore);
    }

    private double evaluateState(Game game, Player player) {
        // Évaluation basée sur la zone contrôlée par le joueur
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