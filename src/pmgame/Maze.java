package pmgame;

import java.util.HashSet;
import java.util.Set;

/**
 *  Maze for Pacman
 *  0 = Street
 *  1 = Wall
 * -1 = Waypoint (crossing)
 * -2 = Ghostcave
 *  3 = Tunnel left
 *  4 = Tunnel right
 */


public class Maze {

    private static int mazeSize = 17;
    private static Node[][] nodeMaze = new Node[mazeSize][mazeSize];
    private static final int WAYCOST = 10;
    private static Set<Node> open = new HashSet<>();
    private static Set<Node> closed = new HashSet<>();


    public static Node[][] getNodeMaze() {
        return nodeMaze;
    }

    public static int[][] maze = {
            {1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, -1, 0, -1, 0, 0, -1, 1, 0, 1, -1, 0, 0, 0, 0, -1, 1},
            {1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, -1, 0, -1, 0, 0, -1, 0, -1, 0, -1, 0, 0, -1, 1, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, -1, 0, -1, 1},
            {1, 0, 1, -1, -1, 1, -1, 0, -1, 0, -1, 0, -1, -1, 1, 0, 1},
            {1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1},
            {1, -1, 0, -1, -1, 0, -1, 1, -2, 1, 0, 1, -1, 0, 0, -1, 1},
            {1, 1, 1, 0, 1, 1, -1, -2, -2, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, -1, 0, -1, -1, 0, -1, 1, -2, 1, 0, 1, -1, 0, 0, -1, 1},
            {1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1},
            {1, 0, 1, -1, -1, 1, -1, 0, -1, 0, -1, 0, -1, -1, 1, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, -1, 0, -1, 1},
            {1, -1, 0, -1, 0, 0, -1, 0, -1, 0, -1, 0, 0, -1, 1, 0, 1},
            {1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, -1, 0, -1, 0, 0, -1, 1, 0, 1, -1, 0, 0, 0, 0, -1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 1, 1, 1}

    };


    // create Nodes
    public static void createNodes() {
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                nodeMaze[i][j] = new Node(maze[i][j], i, j);
            }
        }
    }

    public static int getZeros() {
        int result = 1;
        for (int[] ints : maze) {
            for (int anInt : ints) {
                if (anInt == 0 || anInt == -1) {
                    result++;
                }
            }
        }
        return result;
    }

    public static void setHeuristic() {

        // get PM position
        int xPM = Main.pm.getPMLocation()[0];
        int yPM = Main.pm.getPMLocation()[1];

        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                nodeMaze[i][j].sethValue(Math.abs(xPM - i) + Math.abs(yPM - j));
            }
        }
    }

    // A* Algorithm for Ghosts to chase Pacman
    public static direction getPathDir(int startX, int startY) {

        // reset parents of nodes and clear lists
        resetAAlgo();

        int x = startX;
        int y = startY;

        direction dir;

        // If startNode is not next to PM find path to PM
        if (!next2PM(startX, startY)) {

            // calculate heuristics to current pacman position:
            setHeuristic();

            // reset f value of start node
            nodeMaze[x][y].setfValue(0);

            while (!next2PM(x, y)) {

                // remove current node from open list and add node to closed list
                open.remove(nodeMaze[x][y]);
                closed.add(nodeMaze[x][y]);
                // add all neibhbors nodes to
                // list if not wall and if not in closed (if in open, no worries because of set)
                if (nodeMaze[x - 1][y].getFieldtype() != 1 && !closed.contains(nodeMaze[x - 1][y])) {
                    open.add(nodeMaze[x - 1][y]);
                }
                ;
                if (nodeMaze[x + 1][y].getFieldtype() != 1 && !closed.contains(nodeMaze[x + 1][y])) {
                    open.add(nodeMaze[x + 1][y]);
                }
                ;
                if (nodeMaze[x][y + 1].getFieldtype() != 1 && !closed.contains(nodeMaze[x][y + 1])) {
                    open.add(nodeMaze[x][y + 1]);
                }
                ;
                if (nodeMaze[x][y - 1].getFieldtype() != 1 && !closed.contains(nodeMaze[x][y - 1])) {
                    open.add(nodeMaze[x][y - 1]);
                }
                ;
                // calculate all f values for all nodes in open list with no parent
                Node minFnode = open.iterator().next();
                for (Node node : open) {
                    if (node.getParent() == null) {
                        // set f value for node
                        node.setfValue(node.gethValue() + WAYCOST);
                        //set parent node
                        node.setParent(nodeMaze[x][y]);
                    }
                    if (node.getfValue() < minFnode.getfValue()) {
                        minFnode = node;
                    }
                }
                // set next node, to node with smallest F-Value
                x = minFnode.getX();
                y = minFnode.getY();
            }

            Node nextNode = nodeMaze[x][y];
            Node startNode = nodeMaze[startX][startY];

            // Find node next to ghost in A* path
            while (nextNode.getParent() != startNode) {
                Node tmp = nextNode;
                nextNode = tmp.getParent();
            }

            // get direction from startnode to nextnode
            dir = direction.getDirectionToNode(startNode, nextNode);

        } else {
            //If ghost is next to PM, get direction from startnode to pacman
            int xPM = Main.pm.getPMLocation()[0];
            int yPM = Main.pm.getPMLocation()[1];
            dir = direction.getDirectionToNode(nodeMaze[startX][startY], nodeMaze[xPM][yPM]);
        }

        return dir;
    }

    private static void resetAAlgo() {
        //purge lists and add cave to closed
        open.clear();
        closed.clear();

        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                nodeMaze[i][j].setParent(null);
                if (nodeMaze[i][j].getFieldtype() == -2) {
                    closed.add(nodeMaze[i][j]);
                }
            }
        }
    }


    public static boolean next2PM(int x, int y) {
        int xPM = Main.pm.getPMLocation()[0];
        int yPM = Main.pm.getPMLocation()[1];
        int diffX = Math.abs(xPM - x);
        int diffY = Math.abs(yPM - y);
        return (diffX == 0 && diffY == 1 || diffX == 1 && diffY == 0);
    }

    public static int getMazeSize() {
        return mazeSize;
    }


/*    public static void printHValue() {
        System.out.println("H Value");
        for (int i = 0; i < maze.length; i++) {
            String res = "";
            for (int j = 0; j < maze[i].length; j++) {
                res = res + "\t" + nodeMaze[j][i].gethValue();
            }
            System.out.println(res);
        }
    }

    public static void printFValue() {
        System.out.println("F Value");
        for (int i = 0; i < maze.length; i++) {
            String res = "";
            for (int j = 0; j < maze[i].length; j++) {
                res = res + "\t" + nodeMaze[j][i].getfValue();
            }
            System.out.println(res);
        }
    }
*/

}
