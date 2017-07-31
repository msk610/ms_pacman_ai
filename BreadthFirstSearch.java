package pacman.entries.pacman.algo;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.entries.pacman.structs.PathNode;
import pacman.game.Constants.*;
import pacman.game.Game;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Depth First Search Algorithm class.
 */
public class BreadthFirstSearch extends Controller<MOVE>{
    //Members
    private PathNode bestNode; //store the best node
    private int maxDepth; //set max depth to check how far to go down
    private Stack<MOVE> path; //path of moves to follow
    private StarterGhosts ghosts; //ghost AI
    private Queue<PathNode> nodes; //nodes to visit

    //Constructor
    public BreadthFirstSearch(int depth, StarterGhosts ghostAI){
        //initialize variables
        maxDepth = depth;
        bestNode = null;
        path = new Stack<MOVE>();
        ghosts = ghostAI;
        nodes = new LinkedList<PathNode>();
    }

    //Method to visit Nodes in the tree
    private void visit(){
        PathNode node = nodes.remove();
        //loop through possible moves
        for(MOVE move : node.moves()){
            Game copy = node.state.copy(); //copy state
            copy.advanceGame(move,ghosts.getMove()); //advance state
            //loop through a few times to update the state
            for(int i =0; i < 5; ++i){
                copy.advanceGame(MOVE.NEUTRAL,ghosts.getMove());
            }
            PathNode child = new PathNode(copy,node,node.depth+1); //make child node
            nodes.add(child);
            if(child.depth == maxDepth || child.state.gameOver()){ //check game over or max depth reached
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
                if(bestNode == null) { //if the best node hasn't been set
                    if(child.state.getScore() > 0){
                        bestNode = child;
                    }
                }
                else{ //otherwise
                    if(child.state.getScore() > bestNode.state.getScore()){
                        bestNode = child;
                    }

                }
            }
        }
        //make the recursive call
        visit();
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        //if the path queue is empty
        if(path.isEmpty()){
            //setup root node of tree and visit
            PathNode root = new PathNode(game,null,0);
            nodes.add(root);
            visit();
            //loop through and store the path
            while(bestNode.depth > 0){
                path.push(bestNode.move());
                bestNode = bestNode.parent;
            }
        }

        //if the stack is still empty then no best path to make
        if(path.isEmpty()){
            return MOVE.NEUTRAL;
        }

        //return next path to make from the queue
        return path.pop();
    }
}
