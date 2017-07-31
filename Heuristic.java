package pacman.entries.pacman.algo;

import pacman.game.Constants.*;
import pacman.game.Game;

/**
 * Heuristic evaluation function to determine the value of the state.
 */
public class Heuristic {
    private static final double PILL_MULT = 1;
    private static final double POWER_MULT = 100;
    private static final double GHOST_MULT = 1000;
    private static final int GHOST_DIST = 10;

    public static double h(Game game){
        double total = 0; //setup a tally
        //check if game is over
        if(game.gameOver()){
            if(game.getNumberOfActivePowerPills() == 0 & game.getNumberOfPills() == 0){
                //if won
                return Double.MIN_VALUE;
            }else{
                //if lost
                return Double.MIN_VALUE;
            }
        }

        int nodeIndex = game.getPacmanCurrentNodeIndex(); //setup pacman index

        //check for pills
        for(int pillIndex : game.getActivePillsIndices()){
            //use the shortest distance from the pacman (smaller the distance from pacman better the move)
            total += PILL_MULT/(game.getShortestPathDistance(nodeIndex,pillIndex));
        }

        //check for power pills
        for(int powerIndex : game.getActivePowerPillsIndices()){
            //use the same concept as power pills with higher multiplier
            total += POWER_MULT/(game.getShortestPathDistance(nodeIndex,powerIndex));
        }


        //check for ghosts
        for(GHOST type : GHOST.values()){
            //setup ghost values
            int ghostIndex = game.getGhostCurrentNodeIndex(type);
            int ghostDistance = game.getShortestPathDistance(nodeIndex,ghostIndex);
            int edible = game.getGhostEdibleTime(type);
            int lair = game.getGhostLairTime(type);
            //check if ghost is edible and not in the box
            if(edible != 0 && lair != 0){
                if(ghostDistance < GHOST_DIST){
                    total += 2*GHOST_MULT/(ghostDistance);
                }else{
                    total -= GHOST_MULT/(ghostDistance);
                }
            }
            //if its not then lower total value to avoid ghost
            else{
                if(ghostDistance > GHOST_DIST) {
                    total += ghostDistance*GHOST_MULT;
                }
            }

        }

        return total+game.getScore();

    }
}
