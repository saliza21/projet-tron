package com.tronai.view;

import com.tronai.config.ConfigLoader;
import com.tronai.controller.GameController;
import com.tronai.heuristic.DangerousnessHeuristic;
import com.tronai.heuristic.Heuristic;
import com.tronai.model.Game;
import com.tronai.util.AlgorithmType;
import com.tronai.util.Direction;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class ViewApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    // Initialize Model (Game)
    Heuristic heuristic = new DangerousnessHeuristic();
    Game game = new Game(ConfigLoader.getIntProperty("grid.width"), ConfigLoader.getIntProperty("grid.height"),
        heuristic); // Example: 10x10 grid with 2 players
    GameView gameView = new GameView(game);
    GameController gameController = new GameController(game, gameView);
    selectTeamsAlgorithm(game);
    gameController.startGame();
    // Set up the scene and stage
    Scene scene = new Scene(gameView.getView());
    scene.setOnKeyPressed(event -> handleKeyPress(event, gameController));
    stage.setTitle("Tron Game - AI");
    stage.setScene(scene);
    stage.setMinHeight(scene.getHeight());
    stage.setMinWidth(scene.getWidth());
    stage.setOnCloseRequest(event -> {
      System.out.println("Closing the application...");
      Platform.exit(); // Ensures proper shutdown of JavaFX
      System.exit(0); // Ensures the JVM exits completely
    });
    stage.show();

  }

  private void handleKeyPress(KeyEvent event, GameController gameController) {
    KeyCode keyCode = event.getCode();
    switch (keyCode) {
      case ENTER:
        gameController.autoMove();
      case UP:
        gameController.move(Direction.UP); // Move up
        break;
      case DOWN:
        gameController.move(Direction.DOWN); // Move down
        break;
      case LEFT:
        gameController.move(Direction.LEFT); // Move left
        break;
      case RIGHT:
        gameController.move(Direction.RIGHT); // Move right
        break;
      default:
        break;
    }
  }

  private void selectTeamsAlgorithm(Game game) {
    List<AlgorithmType> teamAlgorithms = new ArrayList<>();
    for (int i = 0; i < game.getTeams().size(); i++) {
      AlgorithmType selectedAlgorithm = showAlgorithmDialog("Team " + (i + 1), i, game);
      teamAlgorithms.add(selectedAlgorithm);
    }
  }

  private AlgorithmType showAlgorithmDialog(String teamName, int teamIndex, Game game) {
    List<AlgorithmType> choices = Collections.unmodifiableList(Arrays.asList(AlgorithmType.values()));
    ChoiceDialog<AlgorithmType> dialog = new ChoiceDialog<>(choices.get(0), choices);
    dialog.setTitle("Algorithm Selection");
    dialog.setHeaderText("Choose an algorithm for " + teamName);
    dialog.setContentText("Algorithm:");

    Optional<AlgorithmType> result = dialog.showAndWait();
    if (result.isPresent()) {

      game.getTeams().get(teamIndex).setAlgo(result.get());
    }
    return result.orElse(AlgorithmType.MAXN);
  }

}
