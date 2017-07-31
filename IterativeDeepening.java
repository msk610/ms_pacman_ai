package pacman.entries.pacman.algo;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.entries.pacman.structs.PathNode;
import pacman.game.Constants.*;
import pacman.game.Game;

import java.util.Stack;

/**
 * Iterative Deepening First Search Algorithm class.
 */
public class IterativeDeepening extends Controller<MOVE> {
    //Members
    private PathNode bestNode; //store the best node
    private int maxDepth, currentDepth; //set max depth to check how far to go down and current depth to search
    private Stack<MOVE>path; //path of moves to follow
    private StarterGhosts ghosts; //ghost AI

    //constructor
    public IterativeDeepening(int depth, StarterGhosts ghostAI){
        //initialize variables
        maxDepth = depth;
        bestNode = null;
        path = new Stack<MOVE>();
        ghosts = ghostAI;
        currentDepth = 1;
    }

    //Method to visit Nodes in the tree
    private void visit(PathNode node){
        for(MOVE move : node.moves()){
            Game copy = node.state.copy(); //copy state
            //advance the state
            copy.advanceGame(move,ghosts.getMove());
            //call advance a few times to get updated score
            for(int i=0; i < 5; ++i){
                copy.advanceGame(MOVE.NEUTRAL.opposite(),ghosts.getMove());
            }
            PathNode child = new PathNode(copy,node,node.depth+1); //make child node

            //go visit the child
            if(child.depth < currentDepth){
                visit(child);
            }

            //evaluate the child node
            if(child.state.gameOver()){ //check game over or max depth reached
                if(child.state.getNumberOfActivePills() == 0 && child.state.getNumberOfActivePowerPills() == 0){
                    //if won the game then break out of the loop
                    bestNode = child;
                    break;
                }else{
                    //otherwise return
                    return;
                }
            }
            else{ //if the game is not over
                //otherwise set best node
                if(bestNode == null){
                    if(child.state.getScore() > 0) {
                        bestNode = child;
                    }
                }
                else{
                    if(child.state.getScore() > bestNode.state.getScore()){
                        bestNode = child;
                    }
                }
            }
        }
    }


    //overrided method to get the next best move
    @Override
    public MOVE getMove(Game game, long timeDue) {
        //if the path stack is empty
        if(path.isEmpty()){
            //setup root node of tree and visit
            PathNode root = new PathNode(game,null,0);
            while(currentDepth < maxDepth) {
                visit(root);
                currentDepth++;
            }
            while(bestNode.depth > 0){
                path.push(bestNode.move());
                bestNode = bestNode.parent;
            }
        }

        //if the stack is still empty then no best path to make
        if(path.isEmpty()){
            return MOVE.NEUTRAL;
        }

        //return next path to make from the stack
        return path.pop();
    }
}
