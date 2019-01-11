package pmgame;

import java.util.List;
import java.util.Random;


/*
 *  Enumerations for all possible directions to walk
 */

public enum direction {
    N, S, E, W;

    private static final List<direction> VALUES = List.of(values());
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    //get a random Direction for the ghosts with no AI
    public static direction getRandomDirection() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

/*    // native approach
    public static direction getDirection2Pacman(int x, int y){
        int pmX = Main.pm.getPMLocation()[0];
        int pmY = Main.pm.getPMLocation()[1];

        int deltaX = pmX-x;
        int deltaY = pmY-y;

        if(Math.abs(deltaX)>=Math.abs(deltaY)){
            if(pmX<x){
                //go west
                return W;
            }else{
                //go ost
                return E;
            }
        }else{
            if(pmY<y){
                //go north
                return N;
            }else{
                //go south
                return S;
            }
        }
    }
*/

    public static direction getDirectionToNode(Node from, Node to) {

        if (to.getX() > from.getX()) {
            //go east
            return E;
        } else if (to.getX() < from.getX()) {
            //go west
            return W;
        } else if (to.getY() > from.getY()) {
            //go south
            return S;
        } else {
            //go north
            return N;
        }
    }

}

