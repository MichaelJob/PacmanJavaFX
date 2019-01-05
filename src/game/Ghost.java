package game;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

public class Ghost {

    public int x;
    public int y;
    public int gh;  //this ghosts color number
    public direction dir = direction.E;
    public ImageView ghost = new ImageView();
    public Image gh1 = new Image(Main.class.getResourceAsStream("./resources/orange.gif"));
    public Image gh2 = new Image(Main.class.getResourceAsStream("./resources/blue.gif"));
    public Image gh3 = new Image(Main.class.getResourceAsStream("./resources/red.gif"));
    public Image gh4 = new Image(Main.class.getResourceAsStream("./resources/pinky.gif"));
    public Image ghWeak = new Image(Main.class.getResourceAsStream("./resources/deadghost.gif"));
    public boolean weak = false;    //Ghosts can be weak, then they are slower and get eaten by pacman


    public boolean AI = true;

    public Ghost(int x, int y, int gh, boolean AI) {
        this.x = x;
        this.y = y;
        this.gh = gh;
        this.AI = AI;
        ghost.setFitHeight(21);
        ghost.setFitWidth(21);
        switch (gh) {   // gh is which color of ghost
            case 1:
                ghost.setImage(gh1);
                break;
            case 2:
                ghost.setImage(gh2);
                break;
            case 3:
                ghost.setImage(gh3);
                break;
            case 4:
                ghost.setImage(gh4);
                break;
        }
        Main.playground.add(ghost, x, y);
    }

    public void go() {
        Platform.runLater(() -> {
            //get out of the cave:
            if (Maze.getNodeMaze()[x][y].getFieldtype() == -2) {
                //try to go north
                dir = direction.N;
                if (canWalk()) {
                    Main.playground.getChildren().remove(ghost);
                    y--;
                    Main.playground.add(ghost, x, y);
                } else {                        // next try to go east
                    dir = direction.W;
                    if (canWalk()) {
                        Main.playground.getChildren().remove(ghost);
                        x--;
                        Main.playground.add(ghost, x, y);
                    } else {
                        //go east 2 steps
                        dir = direction.E;
                        if (canWalk()) {
                            Main.playground.getChildren().remove(ghost);
                            x = x + 2;
                            Main.playground.add(ghost, x, y);
                        }
                    }
                }
            } else if (Maze.getNodeMaze()[x][y].getFieldtype() == 4 && dir == direction.E) {     // are we standing on 3 or 4? (in tunnel)
                // remove from old position
                Main.playground.getChildren().remove(ghost);
                x = 0;
                y = 9;
                // add ghost to new position
                Main.playground.add(ghost, x, y);
            } else if (Maze.getNodeMaze()[x][y].getFieldtype() == 3 && dir == direction.W) {
                // remove from old position
                Main.playground.getChildren().remove(ghost);
                x = 19;
                y = 9;
                // add ghost to new position
                Main.playground.add(ghost, x, y);
            } else {  //*************** run around chasing Pacman **********************
                if (AI) { // AI
                    if (Maze.getNodeMaze()[x][y].getFieldtype() == 0 || Maze.getNodeMaze()[x][y].getFieldtype() == 4 || Maze.getNodeMaze()[x][y].getFieldtype() == 3) {
                        if (canWalk()) {
                            // remove from old position
                            Main.playground.getChildren().remove(ghost);
                            switch (dir) {
                                case E:
                                    x++;
                                    break;
                                case W:
                                    x--;
                                    break;
                                case N:
                                    y--;
                                    break;
                                case S:
                                    y++;
                                    break;
                            }
                            // add ghost to new position
                            try {
                                Main.playground.add(ghost, x, y);
                            } catch (Exception e) {
                                System.out.println(dir + " " + x + " " + y);
                                e.printStackTrace();
                            }
                        } else {
                            // Ghost cannot walk -> get new dir
                            //change direction depending on location of pacman:
                            dir = Maze.getPathDir(x, y);
                        }
                    } else if (Maze.getNodeMaze()[x][y].getFieldtype() == -1) {
                        // on waypoint

                        //change direction depending on location of pacman:
                        dir = Maze.getPathDir(x, y);

                        // remove from old position
                        Main.playground.getChildren().remove(ghost);
                        switch (dir) {
                            case E:
                                x++;
                                break;
                            case W:
                                x--;
                                break;
                            case N:
                                y--;
                                break;
                            case S:
                                y++;
                                break;
                        }
                        // add ghost to new position
                        try {
                            Main.playground.add(ghost, x, y);
                        } catch (Exception e) {
                            System.out.println(dir + " " + x + " " + y);
                            e.printStackTrace();
                        }

                    }
                } else {   // ********* no AI - walk around randomly
                    if (Maze.getNodeMaze()[x][y].getFieldtype() < 1 || Maze.getNodeMaze()[x][y].getFieldtype() == 4
                            || Maze.getNodeMaze()[x][y].getFieldtype() == 3) {
                        //run around randomly:
                        //change direction from time to time (randomly) also if they probably can walk:
                        Random r = new Random();
                        float chance = r.nextFloat();
                        if (chance <= 0.25f) {
                            dir = direction.getRandomDirection();
                        }
                        if (canWalk()) {
                            // remove from old position
                            Main.playground.getChildren().remove(ghost);
                            switch (dir) {
                                case E:
                                    x++;
                                    break;
                                case W:
                                    x--;
                                    break;
                                case N:
                                    y--;
                                    break;
                                case S:
                                    y++;
                                    break;
                            }
                            // add ghost to new position
                            try {
                                Main.playground.add(ghost, x, y);
                            } catch (Exception e) {
                                System.out.println(dir + " " + x + " " + y);
                                e.printStackTrace();
                            }

                        } else {
                            // Ghost cannot walk -> change direction now!
                            dir = direction.getRandomDirection();
                        }
                    }
                }
            }
        });
    }

    // ghosts can walk on 0 (street) and 2 (ghost cave) but of course not on 1 (wall)
    public boolean canWalk() {
        switch (dir) {
            case E:
                if (x + 1 > 19) {
                    return false;
                } else {
                    return Maze.getNodeMaze()[x + 1][y].getFieldtype() < 1;
                }
            case W:
                if (x - 1 < 0) {
                    return false;
                } else {
                    return Maze.getNodeMaze()[x - 1][y].getFieldtype() < 1;
                }
            case N:
                if (y - 1 < 0) {
                    return false;
                } else {
                    return Maze.getNodeMaze()[x][y - 1].getFieldtype() < 1;
                }
            case S:
                if (y + 1 > 19) {
                    return false;
                } else {
                    return Maze.getNodeMaze()[x][y + 1].getFieldtype() < 1;
                }
            default:
                return false;
        }
    }

    public void setWeak() {
        weak = true;
        //make all of them blue
        ghost.setImage(ghWeak);
        //make all of them stupid
        AI = false;
    }

    public void unsetWeak() {
        weak = false;
        switch (gh) {   // gh is which color of ghost
            case 1:
                ghost.setImage(gh1);
                break;
            case 2:
                ghost.setImage(gh2);
                break;
            case 3:
                ghost.setImage(gh3);
                break;
            case 4:
                ghost.setImage(gh4);
                break;
        }

    }


    public void terminate() {
        Main.playground.getChildren().remove(ghost);
    }

    //Getter & Setter
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setAI(boolean AI) {
        this.AI = AI;
    }
}
