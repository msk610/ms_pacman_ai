package pacman.entries.pacman.structs;

import pacman.game.Constants.*;
import pacman.game.Game;


/**
 * Data Structure implemented to manage nodes for tree search algorithms
 */
public class PathNode {
    //Members
    public Game state;
    public PathNode parent;
    public int depth;

    //Constructor
    public PathNode(Game currentState, PathNode parentNode, int deep){
        //initialize members
        state = currentState;
        parent = parentNode;
        depth = deep;
    }

    //copy constructor
    public PathNode(PathNode node){
        state = node.state.copy();
        parent = node.parent;
        depth = node.depth;
    }

    //Method to check if passed in node is parent
    public boolean isParent(PathNode node){
        return parent == node;
    }

    //Method to get the possible moves of the node
    public MOVE[] moves(){
        return state.getPossibleMoves(state.getPacmanCurrentNodeIndex());
    }

    //Method to get move to make
    public MOVE move(){
        return state.getPacmanLastMoveMade();
    }

}
