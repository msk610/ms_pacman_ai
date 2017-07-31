package pacman.entries.pacman.algo;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.entries.pacman.structs.PathNode;
import pacman.game.Constants;
import pacman.game.Constants.*;
import pacman.game.Game;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Cross Over .
 */
public class EvolutionCross extends Controller<MOVE> {
    private StarterGhosts g =new StarterGhosts();

    //find the best move of a child
    public MOVE bestMove(PathNode child){
        MOVE move = MOVE.NEUTRAL;
        double score = Double.MIN_VALUE;
        for(MOVE m : child.moves()){
            //advance and get score
            Game d  = child.state.copy();
            d.advanceGame(m,g.getMove());
            double val = Heuristic.h(d);
            if(score < val){
                score = val;
                move = m;
            }
        }
        //return the best move
        return move;
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        PathNode root = new PathNode(game.copy(),null,0);
        Stack<PathNode> population = new Stack<PathNode>();
        List<PathNode> children = new LinkedList<PathNode>();
        List<MOVE>priorMoves = new LinkedList<MOVE>();


        for(MOVE move : game.getPossibleMoves(game.getPacmanCurrentNodeIndex())){
            //check all moves
            Game c = game.copy();
            c.advanceGame(move,g.getMove());
            //make child and add them
            PathNode child = new PathNode(c,root,root.depth+1);
            children.add(child);
            priorMoves.add(move);
        }

        //pick the top two children
        while(children.size() != 2){
            int deleteIndex = 0;
            double childScore = 0;
            for(int i =0; i < children.size(); ++i){
                if(Heuristic.h(children.get(i).state) < childScore){
                    childScore = Heuristic.h(children.get(i).state);
                    deleteIndex = i;
                }
            }
            children.remove(deleteIndex);
            priorMoves.remove(deleteIndex);
        }

        if(children.size() == 2){
            //make the first child do the next best move of the second child
            MOVE firstChildBest = bestMove(children.get(0));
            MOVE secondChildBest = bestMove(children.get(1));
            children.get(0).state.advanceGame(secondChildBest,g.getMove());
            children.get(1).state.advanceGame(firstChildBest,g.getMove());
        }


        //now get the best cross over
        double childScore = Double.MIN_VALUE;
        int moveIndex = 0;
        for(int i =0; i < children.size(); ++i){
            if(Heuristic.h(children.get(i).state) > childScore){
                childScore = Heuristic.h(children.get(i).state);
                moveIndex = i;
            }
        }

        //return the best child's move to make
        return priorMoves.get(moveIndex);
    }
}
