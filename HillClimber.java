package pacman.entries.pacman.algo;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.*;
import pacman.game.Game;

/**
 * Class to run Hill Climber algorithm to find the next best move to make for the pacman.
 */
public class HillClimber extends Controller<MOVE> {

    private StarterGhosts ghosts;

    public HillClimber(StarterGhosts ghostAI){
        ghosts = ghostAI;
    }


    @Override
    public MOVE getMove(Game game, long timeDue) {
        MOVE best = game.getPacmanLastMoveMade();
        double max = Heuristic.h(game);

        for(MOVE move : game.getPossibleMoves(game.getPacmanCurrentNodeIndex())){
            //explore each state
            Game copy = game.copy();
            //advance state
            copy.advanceGame(move,ghosts.getMove());
            //get the heuristic value
            double hScore = Heuristic.h(copy);
            //set max
            if(hScore > max){
                max = hScore;
                best = move;
            }
        }

        //return best state
        return best;
    }
}
