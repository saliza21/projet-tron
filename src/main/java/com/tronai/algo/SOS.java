package com.tronai.algo;

import com.tronai.model.Game;
import com.tronai.model.Player;
import com.tronai.util.Move;
import java.util.List;

public class SOS implements IAI {

    private static final int MAX_DEPTH = 5;

    @Override
    public Move findBestMove(Game game, Player currentPlayer) {
        return sos(game, currentPlayer, 0).move;
    }

    private Result sos(Game game, Player currentPlayer, int depth) {
        if (depth == MAX_DEPTH || game.isGameOver()) {
            double score = evaluateState(game, currentPlayer);
            return new Result(null, score);
        }

        // Phase 1: Maximizing for the current player
        if (currentPlayer.equals(game.getCurrentPlayer())) {
            return maxStep(game, currentPlayer, depth);
        }

        // Phase 2: Minimizing for opponents
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
            Result result = sos(game, game.getNextPlayer(), depth + 1);
            game.undoMove();

            // Combine self-interest, others' interest, and social welfare
            double combinedScore = result.score + evaluateTeammates(game, currentPlayer) - evaluateOpponents(game);

            if (combinedScore > bestScore) {
                bestScore = combinedScore;
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
            Result result = sos(game, game.getNextPlayer(), depth + 1);
            game.undoMove();

            // Penalize moves that benefit the opponent disproportionately
            double combinedScore = result.score - evaluateOpponents(game);

            if (combinedScore < worstScore) {
                worstScore = combinedScore;
                worstMove = move;
            }
        }

        return new Result(worstMove, worstScore);
    }

    private double evaluateState(Game game, Player player) {
        // Evaluate the controlled area for the current player
        return game.getControlledAreaBy(player) / (game.getWidth() * game.getHeight());
    }

    private double evaluateTeammates(Game game, Player currentPlayer) {
        // Sum the controlled areas of all teammates
        double totalTeammateScore = 0.0;
        for (Player teammate : game.getTeammates(currentPlayer)) {
            totalTeammateScore += game.getControlledAreaBy(teammate) / (game.getWidth() * game.getHeight());
        }
        return totalTeammateScore;
    }

    private double evaluateOpponents(Game game) {
        // Sum the controlled areas of all opponents
        double totalOpponentScore = 0.0;
        for (Player opponent : game.getOpponents()) {
            totalOpponentScore += game.getControlledAreaBy(opponent) / (game.getWidth() * game.getHeight());
        }
        return totalOpponentScore;
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