package com.tronai.heuristic;

// DangerousnessHeuristic.java
import com.tronai.model.Game;
import com.tronai.model.Player;
import com.tronai.util.Direction;
import com.tronai.util.Move;
import com.tronai.util.Position;

import java.util.ArrayList;
import java.util.List;

public class DangerousnessHeuristic implements Heuristic {

    @Override
    public int evaluatePosition(Game game, Position pos) {
        int score = 0;

        // Proximity to other players
        for (Player player : game.getPlayers()) {
            if (!player.equals(game.getCurrentPlayer())) {
                Position otherPos = player.getPosition();
                int dist = Math.abs(otherPos.getX() - pos.getX()) + Math.abs(otherPos.getY() - pos.getY());
                if (dist <= 2) { // Danger zone
                    score -= 10;
                }
            }
        }

        // Options for movement
        int moveOptions = 0;
        for (Direction dir : Direction.values()) {
            Position newPos = game.calculateNewPosition(pos, new Move(dir));
            if (game.isWithinBounds(newPos) && game.getGrid()[newPos.getY()][newPos.getX()].isEmpty()) {
                moveOptions++;
            }
        }
        score += moveOptions * 5;

        // Controlled area
        score += game.getControlledAreaBy(game.getCurrentPlayer());

        // Presence of trace
        if (game.getGrid()[pos.getY()][pos.getX()].getOwner() != null) {
            score -= 5;
        }

        // Proximity to edges
        if (pos.getX() <= 1 || pos.getX() >= game.getWidth() - 2 ||
                pos.getY() <= 1 || pos.getY() >= game.getHeight() - 2) {
            score -= 10;
        }

        return score;
    }

    @Override
    public List<Move> suggestMoves(Game game, Player player) {
        List<Move> possibleMoves = new ArrayList<>();
        Position pos = player.getPosition();
        for (Direction dir : Direction.values()) {
            Position newPos = game.calculateNewPosition(pos, new Move(dir));
            if (game.isWithinBounds(newPos) && game.getGrid()[newPos.getY()][newPos.getX()].isEmpty()) {
                possibleMoves.add(new Move(dir));
            }
        }

        // Sort moves by heuristic score
        possibleMoves.sort((m1, m2) -> {
            Position pos1 = game.calculateNewPosition(pos, m1);
            Position pos2 = game.calculateNewPosition(pos, m2);
            return Integer.compare(evaluatePosition(game, pos2), evaluatePosition(game, pos1));
        });

        return possibleMoves;
    }
}