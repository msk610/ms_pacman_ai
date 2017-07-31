package pacman.entries.pacman.algo;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.*;
import pacman.game.Game;

import java.util.Random;

/**
 * Simulated Annealing algorithm.
 */
public class SimulatedAnnealing extends Controller<MOVE> {
    private StarterGhosts ghosts;

    public SimulatedAnnealing(){
        ghosts = new StarterGhosts();
    }
    @Override
    public MOVE getMove(Game game, long timeDue) {
        //get the set of moves
        MOVE[] moves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());

        //generate index at random (random move) and a probability
        Random random = new Random();
        int index = random.nextInt(moves.length);
        MOVE move = moves[index];
        double proabability = random.nextDouble();

        //make copy and advance
        Game copy = game.copy();
        copy.advanceGame(move,ghosts.getMove());

        //setup scheduler
        int scheduler = copy.getNumberOfActivePills() + copy.getPacmanNumberOfLivesRemaining()+copy.getNumberOfActivePowerPills();

        double difference = Heuristic.h(copy) - Heuristic.h(game);

        //if difference is greater make the move
        if(difference > 0){
            return move;
        }else{
            //check the probability
            if(proabability > Math.exp(difference/scheduler)){
                return move;
            }
        }

        return MOVE.NEUTRAL;
    }
}
