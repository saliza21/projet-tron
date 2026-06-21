package com.tronai.controller;

import com.tronai.algo.AlgoFactory;
import com.tronai.algo.IAI;
import com.tronai.algo.MaxN;
import com.tronai.config.ConfigLoader;
import com.tronai.model.Game;
import com.tronai.model.Team;
import com.tronai.util.Direction;
import com.tronai.util.Move;
import com.tronai.util.Position;
import com.tronai.view.GameView;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class GameController {
    private final Game game;
    private final GameView gameView;
    private final Timer timer;

    private AnimationTimer gameLoop;
    private long lastUpdate = 0;
    private boolean isGameLoopRunning = false;

    public GameController(Game game, GameView gameView) {
        this.game = game;
        this.gameView = gameView;
        this.timer = new Timer();
        this.game.initializeTeamsAndPlayers(ConfigLoader.getIntProperty("number.of.players"), ConfigLoader.getIntProperty("number.of.teams"));
    }


    private void updateView() {
        Platform.runLater(() -> {
            this.gameView.update(game);
        });
    }

    public void startGame() {
        updateView();
        if (ConfigLoader.getIntProperty("auto") == 1) {
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (now - lastUpdate >= 1_000_000_000L) { // 1 second in nanoseconds
                        lastUpdate = now;
                        autoMove();
                    }
                }
            };
            gameLoop.start();
            isGameLoopRunning = true;
        }
    }

    public void move(Direction direction) {
        game.makeMove(new Move(direction));
        updateView();
        if(game.isGameOver())showGameOverDialog();
    }

    public void autoMove() {

        if (game.isGameOver()) {
            return;
        }

        IAI algo = AlgoFactory.createAlgorithm(game.getCurrentPlayer().getTeam().getAlgo().toString());
        Move bestMove = algo.findBestMove(game.copy(), game.getCurrentPlayer());
        Position newPos = game.calculateNewPosition(game.getCurrentPlayer().getPosition(), bestMove);
        System.out.println(String.format("T:%s; P:%s | (%d,%d) -> (%d,%d) " , game.getCurrentPlayer().getTeam().getId(),game.getCurrentPlayer().getId(),game.getCurrentPlayer().getPosition().getX(),game.getCurrentPlayer().getPosition().getY() , newPos.getX(), newPos.getY()));

//        System.out.println("Auto move: " + bestMove);

        if (bestMove != null) {
            game.makeMove(bestMove);
        } else {
            game.getCurrentPlayer().die();
            game.nextTurn();
        }

        updateView();

        // Check if the game is now over
        if (game.isGameOver()) {
            Platform.runLater(() -> {
                if (isGameLoopRunning) {
                    gameLoop.stop();
                    isGameLoopRunning = false;
                }
                showGameOverDialog();
                Platform.exit();
            });
        }
    }

    public void stopAutoMove() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void showGameOverDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);

        // Determine the winner
        String winnerMessage = "The game is over!";
        List<Team> winningTeams = new ArrayList<>();
        for (Team team : game.getTeams()) {
            if (team.hasAlivePlayers()) {
                winningTeams.add(team);
            }
        }

        if (winningTeams.size() == 1) {
            winnerMessage += " Team " + winningTeams.get(0).getId() + " wins!";
        }

        alert.setContentText(winnerMessage);
        alert.showAndWait(); // Display the dialog and wait for user acknowledgment
    }

}