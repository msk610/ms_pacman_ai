package pacman.entries.pacman.algo;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.entries.pacman.structs.PathNode;
import pacman.game.Constants;
import pacman.game.Constants.*;
import pacman.game.Game;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Algorithm to run evolutonary algorithm.
 */
public class Evolution extends Controller<MOVE> {
    private StarterGhosts g =new StarterGhosts();

    //find a random move to make
    public MOVE randomMove(PathNode child){
        int size = child.moves().length;
        Random r = new Random();
        int choose = r.nextInt(size);
        int i = 0;
        for(MOVE move : child.moves()){
            if(i == choose){
                return  move;
            }
            ++i;
        }

        return MOVE.NEUTRAL;
    }

    //find the best move of a child
    public MOVE worstMove(PathNode child){
        MOVE move = MOVE.NEUTRAL;
        double score = Double.MAX_VALUE;
        for(MOVE m : child.moves()){
            //advance and get score
            Game d  = child.state.copy();
            d.advanceGame(m,g.getMove());
            double val = Heuristic.h(d);
            if(score > val){
                score = val;
                move = m;
            }
        }
        //return the worst move
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

        //go through children and make each child do a worst move
        for(PathNode child : children){
            //find their next worst move
            MOVE worst = worstMove(child);
            //make them make the worst move
            child.state.advanceGame(worst,g.getMove());
        }


        //pick the top two children
        while(children.size() != 2){
            int deleteIndex = 0;
            double childScore = Double.MIN_VALUE;
            for(int i =0; i < children.size(); ++i){
                if(Heuristic.h(children.get(i).state) < childScore){
                    childScore = Heuristic.h(children.get(i).state);
                    deleteIndex = i;
                }
            }
            children.remove(deleteIndex);
            priorMoves.remove(deleteIndex);
        }


        //mutate the top children (random move)
        for(PathNode child : children){
            child.state.advanceGame(randomMove(child),g.getMove());
        }

        if(children.size()  == 2) {

            //now get the best mutated child
            if (Heuristic.h(children.get(0).state) > Heuristic.h(children.get(1).state)) {
                System.out.println(priorMoves.get(0));
                return priorMoves.get(0);
            } else {
                System.out.println(priorMoves.get(1));
                return priorMoves.get(1);
            }
        }
        else{
            System.out.println(priorMoves.get(1));
            return priorMoves.get(0);
        }

    }
}
