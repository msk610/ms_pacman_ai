package pacman.entries.pacman.algo;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.entries.pacman.structs.PathNode;
import pacman.game.Constants.*;
import pacman.game.Game;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Class to run A* algorithm.
 */
public class Astar extends Controller<MOVE> {

    //members
    private StarterGhosts ghosts; //ghost AI
    private int maxDepth; //max depth
    private Stack<MOVE>path; //path to follow

    //constructor
    public Astar(int depth){
        ghosts = new StarterGhosts();
        path = new Stack<MOVE>();
        maxDepth = depth;
    }

    //method to find the best path
    public void findPath(Game game){
        //setup root
        PathNode root  = new PathNode(game.copy(),null,0);
        //setup best value and node
        PathNode bestNode = null;
        Double bestScore = Double.MIN_VALUE;
        //setup stack for children

        Stack<PathNode> children = new Stack<PathNode>();
        //setup stack for costs
        Stack<Double>costs = new Stack<Double>();

        //add root to children
        children.push(root);
        costs.push(0.0);

        //loop through the children
        while (!children.isEmpty()){
            //evaluate the node and the cost
            PathNode current = children.pop();
            Double currentCost = costs.pop();

            //evaluate the total value with best
            Double currentTotal = Heuristic.h(current.state)+currentCost;
            if(currentTotal > bestScore){
                bestNode = current;
                bestScore = currentTotal;
            }

            //check if game over
            if (current.state.gameOver()) {
                //if won
                if (current.state.getNumberOfActivePowerPills() == 0 && current.state.getNumberOfActivePills() == 0) {
                    bestNode = current;
                    break;
                }
                //if lost
                else {
                    return;
                }
            }
            //otherwise
            else {
                if(current.depth < maxDepth) {
                    //setup a list of states to loop through and setup their associated cost
                    LinkedList<Double> loopCosts = new LinkedList<Double>();
                    LinkedList<PathNode> loopThrough = new LinkedList<PathNode>();

                    //get the possible children
                    Game state = current.state;
                    for (MOVE move : state.getPossibleMoves(state.getPacmanCurrentNodeIndex())) {
                        //make a copy state for children and advance the state
                        Game copy = current.state.copy();
                        copy.advanceGame(move, ghosts.getMove());
                        PathNode newNode = new PathNode(copy, current, current.depth + 1);

                        //store the children and their cost along with the current cost
                        Double costToMove = currentCost + Heuristic.h(newNode.state);
                        loopCosts.add(costToMove);
                        loopThrough.add(newNode);
                    }

                    //make list sorted to visit lower path first
                    while (!loopThrough.isEmpty()) {
                        //setup variables to store index and cost
                        int index = 0;
                        double cost = 0.0;
                        for (int i = 0; i < loopCosts.size(); ++i) {
                            Double childCost = loopCosts.get(i);
                            if (childCost > cost) {
                                cost = childCost;
                                index = i;
                            }

                        }
                        //should have the best cost index (add to nodes to visit)
                        costs.push(loopCosts.remove(index));
                        children.push(loopThrough.remove(index));
                    }
                }
            }
        }

        if(bestNode != null) {
            while (bestNode.depth > 0) { //loop through and store the path
                path.push(bestNode.state.getPacmanLastMoveMade());
                bestNode = bestNode.parent;
            }
        }
    }
    
    @Override
    public MOVE getMove(Game game, long timeDue) {
        if(path.isEmpty()){ //check path is empty
            findPath(game);
        }

        //check if path still empty if it is then return neutral
        if(path.isEmpty()){
            return MOVE.NEUTRAL;
        }

        //otherwise pop off the move
        return path.pop();
    }
}
