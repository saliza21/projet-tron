package com.tronai.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.tronai.util.Position;
import com.tronai.util.Move;
import com.tronai.util.Direction;

import java.util.List;

public class GameTest {
    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game(5, 5); // Create a game with a 5x5 grid
    }

    @Test
    public void testAddPlayer() {
        Player player = new Player(1, new Position(0, 0), "Player 1", null);
        game.addPlayer(player);
        assertEquals(player, game.getGrid()[0][0].getOwner());
        assertTrue(game.getGrid()[0][0].isPlayer());
    }

    @Test
    public void testMakeMoveValid() {
        Player player = new Player(1, new Position(0, 0), "Player 1", null);
        game.addPlayer(player);
        Move move = new Move(Direction.RIGHT);
        assertTrue(game.makeMove(move));
        assertTrue(new Position(1, 0).equals(player.getPosition()));
    }

    @Test
    public void testMakeMoveInvalid() {
        Player player = new Player(1, new Position(0, 0), "Player 1", null);
        game.addPlayer(player);
        Move move = new Move(Direction.UP); // Invalid move (out of bounds)
        assertFalse(game.makeMove(move),"le joueur ne peux effectuer un mouvement");
        assertTrue(new Position(0, 0).equals(player.getPosition()),"le joueur reste dans la position initial");
        assertFalse(player.isAlive(),"le joueur est mouri");
    }

    @Test
    public void testIsValidMove() {
        Player player = new Player(1, new Position(0, 0), "Player 1", null);
        game.addPlayer(player);
        Move validMove = new Move(Direction.RIGHT);
        Move invalidMove = new Move(Direction.UP); // Out of bounds
        assertTrue(game.isValidMove(validMove));
        assertFalse(game.isValidMove(invalidMove));
    }

    @Test
    public void testHasValidMoves() {
        Player player = new Player(1, new Position(0, 0), "Player 1", null);
        game.addPlayer(player);
        assertTrue(game.hasValidMoves(player)); // Should have valid moves
        game.makeMove(new Move(Direction.RIGHT)); // Move to (1, 0)
        assertTrue(game.hasValidMoves(player)); // Still has valid moves
        game.makeMove(new Move(Direction.RIGHT)); // Move to (2, 0)
        game.makeMove(new Move(Direction.RIGHT)); // Move to (3, 0)
        game.makeMove(new Move(Direction.RIGHT)); // Move to (4, 0)
        game.makeMove(new Move(Direction.RIGHT)); // Move to (4, 0)
        assertTrue(game.hasValidMoves(player)); // No valid moves left
    }

    @Test
    public void testNextTurn() {
        Player player1 = new Player(1, new Position(0, 0), "Player 1", null);
        Player player2 = new Player(2, new Position(1, 0), "Player 2", null);
        game.addPlayer(player1);
        game.addPlayer(player2);
        assertEquals(player1, game.getCurrentPlayer());
        game.nextTurn();
        assertEquals(player2, game.getCurrentPlayer());
    }

    @Test
    public void testIsGameOver() {
        Team team1 = new Team(1, "Team 1");
        Team team2 = new Team(2, "Team 2");
        game.addTeam(team1);
        game.addTeam(team2);
        Player player1 = new Player(1, new Position(0, 0), "Player 1", team1);
        Player player2 = new Player(2, new Position(1, 0), "Player 2", team2);
        game.addPlayer(player1);
        game.addPlayer(player2);
        assertFalse(game.isGameOver());
        player1.die(); // Simulate player 1 dying
        assertTrue(game.isGameOver());
    }

    @Test
    public void testUndoMove() {
        Player player = new Player(1, new Position(0, 0), "Player 1", null);
        game.addPlayer(player);
        game.makeMove(new Move(Direction.RIGHT)); // Move to (1, 0)
        assertTrue(new Position(1, 0).equals( player.getPosition()));
        game.undoMove(); // Undo the move
        assertTrue(new Position(0, 0).equals( player.getPosition()));
    }

    @Test
    public void testGetPossibleMoves() {
        Player player = new Player(1, new Position(2, 2), "Player 1", null);
        game.addPlayer(player);
        List<Move> possibleMoves = game.getPossibleMoves(player);
        assertEquals(4, possibleMoves.size()); // Player should have four possible moves (up, down, left, right)
    }

    @Test
    public void testControlledArea() {
        Player player = new Player(1, new Position(0, 0), "Player 1", null);
        game.addPlayer(player);
        game.makeMove(new Move(Direction.RIGHT)); // Move to (1, 0)
        game.makeMove(new Move(Direction.RIGHT)); // Move to (2, 0)
        assertEquals(2, game.getControlledAreaBy(player)); // Should control 2 cells
    }

    @Test
    public void testGetTeammates() {
        Team team = new Team(1, "Team 1");
        Player player1 = new Player(1, new Position(0, 0), "Player 1", team);
        Player player2 = new Player(2, new Position(1, 0), "Player 2", team);
        game.addPlayer(player1);
        game.addPlayer(player2);
        List<Player> teammates = game.getTeammates(player1);
        assertEquals(1, teammates.size());
        assertEquals(player2, teammates.get(0));
    }

    @Test
    public void testGetOpponents() {
        Team team1 = new Team(1, "Team 1");
        Team team2 = new Team(2, "Team 2");
        Player player1 = new Player(1, new Position(0, 0), "Player 1", team1);
        Player player2 = new Player(2, new Position(1, 0), "Player 2", team2);
        game.addPlayer(player1);
        game.addPlayer(player2);
        List<Player> opponents = game.getOpponents();
        assertEquals(1, opponents.size());
        assertEquals(player2, opponents.get(0));
    }
}