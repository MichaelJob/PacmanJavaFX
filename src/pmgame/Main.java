package pmgame;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static pmgame.Pacman.pacmanIV;

public class Main extends Application {

    private static int mazeSize;
    public static volatile GridPane playground = new GridPane();
    private static Button btStart;
    private static ComboBox nrAIGhostsCheckbox;

    private static Button btQuit = new Button("Exit");
    public static Scene scene;
    public static volatile Pacman pm;
    private static volatile Ghost g1;
    private static volatile Ghost g2;
    private static volatile Ghost g3;
    private static volatile Ghost g4;
    public static volatile List<Ghost> ghosts = new ArrayList<>();
    public static IntegerProperty Lives = new SimpleIntegerProperty(3);
    public static IntegerProperty Points = new SimpleIntegerProperty(0);
    public static Label title;
    public static Label lblnrAIGhosts;
    private static ScheduledExecutorService ses;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Border Pane to start with
            BorderPane root = new BorderPane();
            root.getStyleClass().add("root");

            title = new Label("PacmanJFX");
            Label points = new Label("Points: " + Points);
            Label lives = new Label("Lives: " + Lives);
            lblnrAIGhosts = new Label("Number of AI Ghosts:");
            lblnrAIGhosts.getStyleClass().add("labelAIGhosts");
            nrAIGhostsCheckbox = new ComboBox(FXCollections.observableArrayList(0, 1, 2, 3, 4));
            nrAIGhostsCheckbox.setValue(1);
            //bindings
            lives.textProperty().bind(Lives.asString());
            points.textProperty().bind(Points.asString());
            btStart = new Button("New game");
            title.getStyleClass().add("title");
            points.getStyleClass().add("points");
            lives.getStyleClass().add("lives");
            HBox topBox = new HBox();
            topBox.setAlignment(Pos.CENTER);
            topBox.getChildren().addAll(lives, title, points);
            topBox.setPadding(new Insets(25, 25, 25, 25));
            topBox.setSpacing(55);
            root.setTop(topBox);
            BorderPane.setAlignment(topBox, Pos.CENTER);
            // GridPane in Center as playground
            playground.setAlignment(Pos.CENTER);
            playground.setHgap(0);
            playground.setVgap(0);
            BorderPane.setAlignment(playground, Pos.CENTER);
            HBox bottom = new HBox();
            bottom.setAlignment(Pos.CENTER);
            bottom.getChildren().addAll(lblnrAIGhosts, nrAIGhostsCheckbox, btStart, btQuit);
            bottom.setPadding(new Insets(25, 25, 25, 25));
            bottom.setSpacing(25);
            root.setBottom(bottom);

            root.setCenter(playground);
            scene = new Scene(root, 1024, 1024);
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(false);

            EventHandlers();

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //start JavaFX-Pacman
    public static void main(String[] args) {
        launch(args);

    }


    // create Matrix
    private static void createMatrix() {
        mazeSize = Maze.getMazeSize();
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                Label field = new Label();
                field.setMinSize(20, 20);
                field.setPrefSize(35, 35);
                field.setAlignment(Pos.CENTER);
                field.setId(i + "/" + j);
                if (Maze.getNodeMaze()[i][j].getFieldtype() == 1) {
                    field.getStyleClass().add("wall");
                } else if (Maze.getNodeMaze()[i][j].getFieldtype() == -1 || Maze.getNodeMaze()[i][j].getFieldtype() == 0 || Maze.getNodeMaze()[i][j].getFieldtype() > 2) {
                    field.setText("\u25CF");
                }
                playground.add(field, i, j);
            }
        }
    }


    private static void EventHandlers() {

        // make the pacman change direction
        // scene.setOnKeyPressed(e -> pm.setDirection(e.getCode())); for JDK 8 and older, bug in JDK 9, 10 !
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> pm.setDirection(e.getCode()));

        btStart.setOnAction(e -> StartGame());

        btQuit.setOnAction(e -> System.exit(0));

    }

    private static void StartGame() {
        //reset lives, points and clear ghosts, remove pacman
        reset();
        Maze.createNodes();
        //make Playfield:
        createMatrix();

        int nrAiGhosts = (int) nrAIGhostsCheckbox.getValue();
        nrAIGhostsCheckbox.setDisable(true);
        btStart.setDisable(true);

        // place pacman and ghosts

        g1 = new Ghost(7, 8, 1, false);
        g2 = new Ghost(8, 8, 2, false);
        g3 = new Ghost(9, 8, 3, false);
        g4 = new Ghost(8, 8, 4, false);

        ghosts.add(g1);
        ghosts.add(g2);
        ghosts.add(g3);
        ghosts.add(g4);

        // Set AI ghosts
        for (int i = 0; i < nrAiGhosts; i++) {
            ghosts.get(i).setAI(true);
        }

        pm = new Pacman(2, 8);

        ses = Executors.newSingleThreadScheduledExecutor();
        synchronized (ses) {
            ses.scheduleWithFixedDelay(() -> pm.go(), 0, 400, TimeUnit.MILLISECONDS);
            for (int i = 0; i < ghosts.size(); i++) {
                final int j = i;
                // set different speeds for each ghost
                ses.scheduleWithFixedDelay(() -> ghosts.get(j).go(), 0, 500 + (j * 25), TimeUnit.MILLISECONDS);
            }
            //make all ghosts weak and stupid from time to time
            ghosts.forEach(g -> ses.scheduleWithFixedDelay(g::setWeak, 7, 16, TimeUnit.SECONDS));
            ses.scheduleWithFixedDelay(Main::unsetWeakGhosts, 12, 16, TimeUnit.SECONDS);
        }
    }

    private static void unsetWeakGhosts() {
        ghosts.forEach(Ghost::unsetWeak);
        // make some of the ghosts smart again
        for (int i = 0; i < (int) nrAIGhostsCheckbox.getValue(); i++) {
            ghosts.get(i).setAI(true);
        }
    }

    private static void reset() {
        title.setText("PacmanJFX");
        if (null != ses) {
            ses.shutdown();
        }
        Lives.set(3);
        Points.set(0);
        nrAIGhostsCheckbox.setDisable(false);
        playground.getChildren().remove(pacmanIV);
        ghosts.forEach(g -> playground.getChildren().remove(g.ghost));
        ghosts.clear();
    }

    public static void win() {
        if (null != ses) {
            ses.shutdown();
        }
        ghosts.forEach(g -> playground.getChildren().remove(g.ghost));
        ghosts.clear();
        Alert win = new Alert(Alert.AlertType.INFORMATION, "Congrats - You win");
        win.showAndWait();
        btStart.setDisable(true);
    }

    public static void gameover() {
        if (null != ses) {
            ses.shutdown();
        }
        playground.getChildren().remove(pacmanIV);
        title.setText("GAME OVER");
        btStart.setDisable(true);
    }

}
