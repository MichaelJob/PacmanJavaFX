package game;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;


public class Pacman {

    private static int x;
    private static int y;
    private static direction dir = direction.E;
    protected static ImageView pacmanIV = new ImageView(); // use only one ImageView
    private static Image pcE = new Image(Main.class.getResourceAsStream("./resources/pacmanE.gif"));
    private static Image pcN = new Image(Main.class.getResourceAsStream("./resources/pacmanN.gif"));
    private static Image pcW = new Image(Main.class.getResourceAsStream("./resources/pacmanW.gif"));
    private static Image pcS = new Image(Main.class.getResourceAsStream("./resources/pacmanS.gif"));
    private static Sound sound = new Sound();
    private static int yellowDots;
    private static int eatenDots = 0;
    private static int mazeSize;

    protected Pacman(int x, int y) {
        Pacman.x = x;
        Pacman.y = y;
        pacmanIV.setFitHeight(23);
        pacmanIV.setFitWidth(23);
        pacmanIV.setSmooth(true);
        pacmanIV.setCache(true);
        pacmanIV.setEffect(new DropShadow(3, Color.GOLDENROD));
        pacmanIV.setImage(pcE); // start direction is East
        mazeSize = Maze.getMazeSize();
        yellowDots = Maze.getZeros();

    }

    // if new direction is not walkable set old direction again
    protected static void setDirection(KeyCode newDir) {
        direction temp = dir;
        switch (newDir) {
            case UP:
                dir = direction.N;
                if (!canWalk()) {
                    dir = temp;
                }
                break;
            case DOWN:
                dir = direction.S;
                if (!canWalk()) {
                    dir = temp;
                }
                break;
            case LEFT:
                dir = direction.W;
                if (!canWalk()) {
                    dir = temp;
                }
                break;
            case RIGHT:
                dir = direction.E;
                if (!canWalk()) {
                    dir = temp;
                }
                break;
            default:
                break;
        }
    }

    //Pacman can walk on streets (0) and left/right tunnel (3/4) as well on waypoints -1
    private static boolean canWalk() {
        int mazeSizeMaxIndex = mazeSize - 1;
        switch (dir) {
            case E:
                if (x + 1 > mazeSizeMaxIndex) {
                    return false;
                } else {
                    return Maze.getNodeMaze()[x + 1][y].getFieldtype() < 1 || Maze.getNodeMaze()[x + 1][y].getFieldtype() == 4;
                }
            case W:
                if (x - 1 < 0) {
                    return false;
                } else {
                    return Maze.getNodeMaze()[x - 1][y].getFieldtype() < 1 || Maze.getNodeMaze()[x - 1][y].getFieldtype() == 3;
                }
            case N:
                if (y - 1 < 0) {
                    return false;
                } else {
                    return Maze.getNodeMaze()[x][y - 1].getFieldtype() < 1;
                }
            case S:
                if (y + 1 > mazeSizeMaxIndex) {
                    return false;
                } else {
                    return Maze.getNodeMaze()[x][y + 1].getFieldtype() < 1;
                }
            default:
                return false;
        }
    }

    protected static void go() {

        Platform.runLater(() -> {
            // get current Label
            Label dot = (Label) Main.scene.lookup("#" + x + "/" + y);
            // are we standing on 3 or 4?
            if (Maze.getNodeMaze()[x][y].getFieldtype() == 4 && dir == direction.E) {
                // remove from old position
                Main.playground.getChildren().remove(pacmanIV);
                dot.setText("");
                x = 0;
                y = 8;
                // add pacman to new position
                Main.playground.add(pacmanIV, x, y);
                return;
            } else if (Maze.getNodeMaze()[x][y].getFieldtype() == 3 && dir == direction.W) {
                // remove from old position
                Main.playground.getChildren().remove(pacmanIV);
                dot.setText("");
                x = 16;
                y = 8;
                // add pacman to new position
                Main.playground.add(pacmanIV, x, y);
                return;
            }
            if (canWalk()) {
                // play sound if yellow dot is here :: \u25CF
                if (dot.getText().equals("\u25CF")) {
                    sound.playWaka();
                    // remove yellow dot
                    dot.setText("");
                    eatenDots++;
                    Main.Points.setValue(Main.Points.getValue() + 1);
                    //check if all yellow points are gone
                    if (eatenDots == yellowDots) {
                        Main.win();
                    }
                } else {
                    sound.stopWaka();
                }
                // remove from old position
                Main.playground.getChildren().remove(pacmanIV);
                switch (dir) {
                    case E:
                        x++;
                        pacmanIV.setImage(pcE);
                        break;
                    case W:
                        x--;
                        pacmanIV.setImage(pcW);
                        break;
                    case N:
                        y--;
                        pacmanIV.setImage(pcN);
                        break;
                    case S:
                        y++;
                        pacmanIV.setImage(pcS);
                        break;
                }
                // add pacman in correct direction to new position
                Main.playground.add(pacmanIV, x, y);
            } else {
                // stop sound
                sound.stopWaka();
            }
            checkCollision();
        });

    }

    //met a ghost?
    private static void checkCollision() {
        Main.ghosts.forEach(g -> {
            if (x == g.getX() && y == g.getY()) {
                if (g.weak) {
                    g.terminate(); //kill this ghost
                    g.x = 7;  //rebirth at cave
                    g.y = 8;
                    Main.Points.setValue(Main.Points.getValue() + 50);
                } else {
                    //Pacman dies and restart Pacman if lives left
                    Main.Lives.setValue(Main.Lives.getValue() - 1);
                    sound.playPMdies();
                    if (Main.Lives.getValue() == 0) {
                        Main.gameover();
                    } else {    //reset pacman to start position
                        x = 2;
                        y = 8;
                    }
                }
            }
        });
    }


    public int[] getPMLocation() {
        int[] loc = new int[]{x, y};
        return loc;
    }
}