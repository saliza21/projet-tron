package com.tronai.model;

import com.tronai.heuristic.Heuristic;
import com.tronai.util.Cell;
import com.tronai.util.Direction;
import com.tronai.util.Move;
import com.tronai.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Game {

    private final int width;
    private final int height;
    private final Cell[][] grid;
    private final List<Player> players;
    private final List<Team> teams;
    private int currentPlayerIndex;
    private final List<Move> moveHistory;
    private Heuristic heuristic;

    public Game(int width, int height, Heuristic heuristic) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];
        this.players = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.moveHistory = new ArrayList<>();
        this.heuristic = heuristic;
        initializeGrid();
    }

    private void initializeGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Cell();
            }
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
        Position pos = player.getPosition();
        grid[pos.getY()][pos.getX()] = new Cell(player, true);
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    private void updateGrid(Player player, Position pos) {
        grid[player.getPosition().getY()][player.getPosition().getX()].setIsPlayer(false);
        grid[pos.getY()][pos.getX()] = new Cell(player, true);
        player.setPosition(pos);
    }


    public boolean makeMove(Move move) {
        Player currentPlayer = getCurrentPlayer();


        if (currentPlayer.isAlive() && !isValidMove(move)) {
            currentPlayer.die();
            nextTurn();
            return false;
        } else if (!currentPlayer.isAlive()) {
            nextTurn();
            return false;
        }

        Position newPos = calculateNewPosition(currentPlayer.getPosition(), move);
//        System.out.println(String.format("T:%s; P:%s | (%d,%d) -> (%d,%d) " , currentPlayer.getTeam().getId(),currentPlayer.getId(),currentPlayer.getPosition().getX(),currentPlayer.getPosition().getY() , newPos.getX(), newPos.getY()));
        updateGrid(currentPlayer, newPos);
        currentPlayer.setPosition(newPos);
        moveHistory.add(move);

        if (!hasValidMoves(currentPlayer)) {
            currentPlayer.die();
        }

        nextTurn();
        return true;
    }

    public Position calculateNewPosition(Position currentPos, Move move) {
        return new Position(
                currentPos.getX() + move.getDirection().dx,
                currentPos.getY() + move.getDirection().dy
        );
    }

    public boolean isValidMove(Move move) {
        Player currentPlayer = getCurrentPlayer();
        Position currentPos = currentPlayer.getPosition();
        Position newPos = calculateNewPosition(currentPos, move);

        return isWithinBounds(newPos) && grid[newPos.getY()][newPos.getX()].isEmpty();
    }

    public boolean isWithinBounds(Position pos) {
        return pos.getX() >= 0 && pos.getX() < width && pos.getY() >= 0 && pos.getY() < height;
    }

    public boolean hasValidMoves(Player player) {
        Position pos = player.getPosition();
        for (Direction dir : Direction.values()) {
            Position newPos = calculateNewPosition(pos, new Move(dir));
            if (isWithinBounds(newPos) && grid[newPos.getY()][newPos.getX()].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void nextTurn() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (!getCurrentPlayer().isAlive() && !isGameOver());
    }

    public Player getNextPlayer() {
        return players.get((currentPlayerIndex + 1) % players.size());
    }

    public List<Move> getPossibleMoves(Player player) {
        List<Move> possibleMoves = new ArrayList<>();
        Position currentPlayerPosition = player.getPosition();

        // Iterate through all possible directions
        for (Direction direction : Direction.values()) {
            Position newPosition = calculateNewPosition(currentPlayerPosition, new Move(direction));

            // Check if the move is valid
            if (isWithinBounds(newPosition) && grid[newPosition.getY()][newPosition.getX()].isEmpty()) {
                possibleMoves.add(new Move(direction));
            }
        }

        return possibleMoves;
    }

    public boolean isGameOver() {
        // Le jeu est terminé si une seule équipe a des joueurs en vie
        long teamsAlive = teams.stream().filter(Team::hasAlivePlayers).count();
        return teamsAlive <= 1;
    }

    public Team getWinningTeam() {
        if (!isGameOver()) return null;
        return teams.stream()
                .filter(Team::hasAlivePlayers)
                .findFirst()
                .orElse(null);
    }

    private Player getPlayerById(int playerId) {
        return players.stream().filter(p -> p.getId() == playerId).findFirst().orElse(null);
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getWidth() {
        return width;
    }

    public void initializeTeamsAndPlayers(int numberOfPlayers, int numberOfTeams) {
        // Validation checks
        if (numberOfTeams <= 0) {
            throw new IllegalArgumentException("Number of teams must be at least 1.");
        }
        if (numberOfPlayers < numberOfTeams) {
            throw new IllegalArgumentException("Cannot have more teams than players.");
        }

        int playersPerTeamBase = numberOfPlayers / numberOfTeams;
        int remainder = numberOfPlayers % numberOfTeams; // Extra players to distribute

        for (int t = 0; t < numberOfTeams; t++) {
            Team team = new Team(t + 1, "Team " + (t + 1));
            this.addTeam(team);

            // Calculate players in this team (base + 1 if there's a remainder)
            int playersInThisTeam = playersPerTeamBase + (t < remainder ? 1 : 0);

            for (int p = 0; p < playersInThisTeam; p++) {
                // Calculate start position (adjust parameters if needed)
                Position startPos = calculateStartPosition(
                        t,
                        p,
                        numberOfTeams,
                        playersInThisTeam, // Use actual players in this team
                        getWidth(),
                        getHeight()
                );

                // Create and add the player (ensure unique IDs if needed)
                Player player = new Player(
                        p + 1, // Adjust this if IDs must be globally unique
                        startPos,
                        "Player " + (t + 1) + "-" + (p + 1),
                        team
                );

                team.addPlayer(player);
                addPlayer(player);
            }
        }
    }

    private static Position calculateStartPosition(
            int teamIndex, int playerIndex,
            int numTeams, int playersPerTeam,
            int width, int height
    ) {
        // Positionner les joueurs aux coins et le long des bords
        int x, y;

        if (numTeams == 2) {
            // Pour 2 équipes, placer les joueurs aux extrémités opposées
            x = (teamIndex == 0) ? 2 : width - 3;
            y = height / 2 + (playerIndex - playersPerTeam / 2);
        } else {
            // Pour plus d'équipes, répartir autour du plateau
            double angle = 2 * Math.PI * teamIndex / numTeams;
            x = (int) (width / 2 + (width / 3) * Math.cos(angle));
            y = (int) (height / 2 + (height / 3) * Math.sin(angle));
        }

        return new Position(x, y);
    }

    public int getControlledAreaBy(Player player) {
        int controlledArea = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = grid[y][x];
                if (cell.getOwner() != null && cell.getOwner().getId() == player.getId()) {
                    controlledArea++;
                }
            }
        }

        return --controlledArea;
    }

    public Game copy() {
        // Create a new Game instance with the same dimensions
        Game copiedGame = new Game(this.width, this.height, this.heuristic);

        // Copy players
        for (Player player : this.players) {
            Player copiedPlayer = new Player(player.getId(), player.getPosition(), player.getName(), player.getTeam());
            if (!player.isAlive()) copiedPlayer.die();
            copiedGame.addPlayer(copiedPlayer);
        }

        // Copy teams
        for (Team team : this.teams) {
            Team copiedTeam = new Team(team.getId(), team.getName());
            copiedGame.addTeam(copiedTeam);

            // Add copied players to their respective teams
            for (Player player : team.getPlayers()) {
                Player copiedPlayer = copiedGame.getPlayers().stream()
                        .filter(p -> p.getId() == player.getId())
                        .findFirst()
                        .orElse(null);
                if (copiedPlayer != null) {
                    copiedTeam.addPlayer(copiedPlayer);
                }
            }
        }

        // Copy grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell originalCell = this.grid[y][x];
                Player cellOwner = originalCell.getOwner();
                Player copiedOwner = null;

                // Find the corresponding copied player as the owner
                if (cellOwner != null) {
                    copiedOwner = copiedGame.getPlayers().stream()
                            .filter(p -> p.getId() == cellOwner.getId())
                            .findFirst()
                            .orElse(null);
                    copiedGame.grid[y][x] = new Cell(copiedOwner, originalCell.isPlayer());
                } else {
                    copiedGame.grid[y][x] = new Cell();
                }

                // Create a new Cell with the copied owner and isPlayer flag
            }
        }

        // Copy move history
        for (Move move : this.moveHistory) {
            copiedGame.moveHistory.add(move);
        }

        // Copy current player index
        copiedGame.currentPlayerIndex = this.currentPlayerIndex;

        return copiedGame;
    }

    public boolean undoMove() {
        // Check if there are any moves to undo
        if (moveHistory.isEmpty()) {
            return false; // No moves to undo
        }

        // Retrieve the last move from the history
        Move lastMove = moveHistory.remove(moveHistory.size() - 1);

        Player currentPlayer = getCurrentPlayer();

        // Calculate the previous position before the last move
        Position currentPosition = currentPlayer.getPosition();
        Position previousPosition = new Position(
                currentPosition.getX() - lastMove.dx,
                currentPosition.getY() - lastMove.dy
        );

        // Ensure the previous position is valid
        if (!isWithinBounds(previousPosition)) {
            return false; // Invalid undo operation
        }

        // Update the grid to remove the trail left by the player
        Cell currentCell = grid[currentPosition.getY()][currentPosition.getX()];
        currentCell.setIsPlayer(false); // Remove the player from the current cell
        currentCell.setOwner(null);     // Clear ownership of the cell

        // Restore the previous cell as the player's position
        Cell previousCell = grid[previousPosition.getY()][previousPosition.getX()];
        previousCell.setIsPlayer(true); // Mark the previous cell as the player's position
        previousCell.setOwner(currentPlayer); // Set the player as the owner of the previous cell

        // Update the player's position
        currentPlayer.setPosition(previousPosition);

        // If the player died during the last move, restore their alive status
        if (!currentPlayer.isAlive()) {
            currentPlayer.setIsAlive(true);
        }

        // Adjust the current player index if necessary
        if (currentPlayerIndex > 0) {
            currentPlayerIndex--; // Go back to the previous player
        } else {
            currentPlayerIndex = players.size() - 1; // Wrap around to the last player
        }

        return true; // Undo successful
    }

    public int getHeight() {
        return height;
    }


    public List<Player> getTeammates(Player currentPlayer) {
        if (currentPlayer == null || currentPlayer.getTeam() == null) {
            return new ArrayList<>();
        }

        Team currentTeam = currentPlayer.getTeam();
        return this.getPlayers().stream()
                .filter(player -> player.getTeam().equals(currentPlayer.getTeam()) && !player.equals(currentPlayer)) // Exclude the current player
                .collect(Collectors.toList());
    }

    public List<Player> getOpponents() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.getTeam() == null) {
            return new ArrayList<>();
        }

        Team currentTeam = currentPlayer.getTeam();
        return players.stream()
                .filter(player -> player.getTeam() != null && !player.getTeam().equals(currentTeam))
                .collect(Collectors.toList());
    }

    public List<Move> getBestMoves(Player player) {
        return heuristic.suggestMoves(this, player);
    }

}
