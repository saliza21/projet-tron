package com.tronai.view;

import com.tronai.config.ConfigLoader;
import com.tronai.model.Game;
import com.tronai.model.Player;
import com.tronai.model.Team;
import com.tronai.util.Cell;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

public class GameView {

    private final GridPane gridPane;
    private final VBox stateSection;
    private final Color[][] teamColors; // Array to store colors for each team
    private final Game game;
    private final int CELL_SIZE;

    public GameView(Game game) {
        this.game = game;
        this.gridPane = new GridPane();
        this.gridPane.setMaxSize(game.getWidth(),game.getHeight());
        this.CELL_SIZE = ConfigLoader.getIntProperty("cell.size");
        this.teamColors = new Color[][] {
                // Row for Team 1: first is team color, then player colors
                {Color.RED, Color.PINK, Color.DARKRED, Color.FIREBRICK},
                // Row for Team 2: first is team color, then player colors
                {Color.BLUE, Color.LIGHTBLUE, Color.DARKBLUE, Color.NAVY},
                // Row for Team 3: first is team color, then player colors
                {Color.GREEN, Color.LIGHTGREEN, Color.DARKGREEN, Color.FORESTGREEN},
                // Row for Team 4: first is team color, then player colors
                {Color.YELLOW, Color.LIGHTYELLOW, Color.GOLD, Color.ORANGE}
        };
        this.stateSection = new VBox();
        this.stateSection.setSpacing(10); // Add spacing between elements
        this.stateSection.setStyle("-fx-padding: 10; -fx-background-color: #373636;");

        update(game);

    }


    public void update(Game model){
        updateGrid(model);
        updateStateSection(model);
    }

    private void updateGrid(Game model) {
        int width = model.getWidth();
        int height = model.getHeight();
        Cell[][] grid = model.getGrid();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Rectangle cell = new Rectangle(this.CELL_SIZE, this.CELL_SIZE);
                Text text = new Text();
                text.setFont(Font.font(10)); // Set font size for the player ID

                if (grid[y][x].isEmpty()) {
                    cell.setFill(Color.BLACK); // Empty cell
                    text.setText("");
//                    cell.setStroke(Color.GRAY); // No text for empty cells
                } else {
                    int teamId = grid[y][x].getOwner().getTeam().getId();
//                    cell.setStroke(getTeamColor(teamId));
                    cell.setFill(getPlayerColor(teamId, grid[y][x].getOwner().getId())); // Color based on team ID
                    if(grid[y][x].isPlayer()) {
                        cell.setFill(getPlayerColor(teamId, grid[y][x].getOwner().getId()));
                        text.setText("x");
                    }
                }

                // Use a StackPane to overlay the text on top of the rectangle
                StackPane stackPane = new StackPane();
                stackPane.getChildren().addAll(cell, text);

                gridPane.add(stackPane, x, y);
            }
        }
    }

    private void updateStateSection(Game model) {
        stateSection.getChildren().clear(); // Clear the state section before updating

        // Add a title for the state section
        Label title = new Label("Game State");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: white");
        stateSection.getChildren().add(title);
        if(model.getPlayers().size() == 0) return;
        // Display current player info
        Player currentPlayer = model.getCurrentPlayer();
        if (currentPlayer != null) {
            Label currentPlayerLabel = new Label("Current Player: Player " + currentPlayer.getId() +
                    " (Team " + currentPlayer.getTeam().getId() + ")");
            String currentPlayerColorHex = "#" + getPlayerColor(currentPlayer.getTeam().getId(), currentPlayer.getId()).toString().substring(2, 8);
            currentPlayerLabel.setStyle("-fx-text-fill: "+currentPlayerColorHex+";");
            stateSection.getChildren().add(currentPlayerLabel);
        }

        // Display team and player status
        for (Team team : model.getTeams()) {
            Label teamLabel = new Label("Team " + team.getId() + ": "+ team.getAlgo().toString());
            String teamColorHex = "#" + getTeamColor(team.getId()).toString().substring(2, 8);
            teamLabel.setStyle("-fx-text-fill: "+teamColorHex+";");
            stateSection.getChildren().add(teamLabel);

            for (Player player : team.getPlayers()) {
                Label playerLabel = new Label("  Player " + player.getId() + ": " + (player.isAlive()?"Alive":"Dead"));
                String playerColorHex = "#" + getPlayerColor(team.getId(), player.getId()).toString().substring(2, 8);
                playerLabel.setStyle("-fx-text-fill: "+playerColorHex+";");
                stateSection.getChildren().add(playerLabel);
            }
        }
    }

    private Color getTeamColor(int teamId) {
        // Ensure the teamId is within the bounds of the teamColors array
        if (teamId >= 0 && teamId < teamColors.length) {
            return teamColors[teamId][0];
        } else {
            return Color.GRAY; // Default color for unknown teams
        }
    }

    private Color getPlayerColor(int teamId, int playerId) {
        // Ensure the teamId is within the bounds of the teamColors array
        if (teamId >= 0 && teamId < teamColors.length) {
            return teamColors[teamId][playerId];
        } else {
            return Color.GRAY; // Default color for unknown teams
        }
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        stateSection.setMinHeight(50*this.game.getPlayers().size()*this.game.getTeams().size());
        stateSection.setMinWidth(200);
        root.setCenter(gridPane);
        root.setRight(stateSection);
        root.setMinHeight(Math.max(gridPane.getHeight(),stateSection.getHeight()));
        return root;
    }

    public GridPane getGridPane() {
        return gridPane;
    }


}