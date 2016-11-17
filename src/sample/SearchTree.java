package sample; /**
 * Created by cozy on 10/16/16.
 */

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static java.lang.Thread.sleep;

// This class either finds the solution to a board
// or shows that no solution is possible

public class SearchTree extends Controller{
    private Map<String, Node> searchPath;
    private PriorityQueue<Node> unsearchedQueue = new PriorityQueue<>();

    private Node head;
    private Node tail;
    private Node bestHeuristic;

    public SearchTree(Node startNode){
        searchPath = new HashMap<>();
        searchPath.put(startNode.toString(), startNode);
        head = startNode;
        tail = startNode;
        bestHeuristic = startNode;
    }

    public ArrayList<Node> findSolution(){
        while(true){
            addNewPathsToQueue();
            Node bestNextMove = unsearchedQueue.peek();

            // NEW MOVE FOUND
            if(bestNextMove != null){
                tail = bestNextMove;
                searchPath.put(bestNextMove.toString(), bestNextMove);
                unsearchedQueue.remove(bestNextMove);
                if(bestNextMove.getHeuristic() < bestHeuristic.getHeuristic())
                    bestHeuristic = bestNextMove;
            }

            // SOLUTION FOUND
            if(tail.getHeuristic() == 0) {
                ArrayList<Node> solution_path = solutionGUI();
                return solution_path;
            }

            // NO SOLUTION FOUND
            if(bestNextMove == null){
                printNoSolutionFound();
                return null;
            }
        }
    }

    /***** all helpers for findSolution *****/
    // Prints solution path by iterating the
    // tree from end to start and saving the
    // summarized solution
    private void printSolution(){
        Node temp = tail;
        if(searchPath.size() >= 7) {
            // saves first 3 steps and
            // last 4 steps of solution path
            Node stepsShown[] = new Node[7];
            int stepNums[] = new int[7];

            // save last 4 steps
            for(int i=6; i>2; i--){
                stepsShown[i] = temp;
                temp = temp.getPrev();
            }
            for(int i=6; i>2; i--){
                stepNums[i] = searchPath.size() - (6-i);
            }

            // iterates until first 3 steps
            while(temp.getPrev().getPrev() != head)
                temp = temp.getPrev();

            // save first 3 steps
            int stepNum = 3;
            while(temp != null) {
                stepsShown[stepNum-1] = temp;
                temp = temp.getPrev();
                stepNums[stepNum-1] = stepNum;
                stepNum--;
            }

            // print 7 steps of solution path
            for (int i=0; i<7; i++) {
                stepsShown[i].board.printBoard(stepNums[i]);
                stepsShown[i].board.printHeuristic();
                if(i == 2){System.out.println(".\n.\n.\n");}
            }

        // prints entire solution if solution
        // path is less than 7 steps long
        }else{
            Node stepsShown[] = new Node[searchPath.size()];
            int stepNums[] = new int[searchPath.size()];

            int stepNum = searchPath.size();
            while(temp != null){
                stepsShown[stepNum-1] = temp;
                stepNums[stepNum-1] = stepNum;
                temp = temp.getPrev();
                stepNum--;
            }
            for(int i=0; i<searchPath.size(); i++){
                stepsShown[i].board.printBoard(stepNums[i]);
                stepsShown[i].board.printHeuristic();
            }
        }
        System.out.println("Done");
    }
    private ArrayList<Node> solutionGUI(){
        ArrayList<Node> solutionPath = new ArrayList<>(10);
        Node tmp = tail;

        while(tmp != null){
            solutionPath.add(0, tmp);
            tmp = tmp.getPrev();
        }

        return solutionPath;
    }

    private void printNoSolutionFound(){
        System.out.println();
        System.out.println();
        System.out.println("All " + (searchPath.size()+2) + " moves have been tried. ");
        System.out.println("That puzzle is impossible to solve.  Best board found was:");
        bestHeuristic.board.printBoard();
        System.out.println("Heuristic: " + bestHeuristic.getHeuristic() + "\n");
        System.out.println("Exiting program.");
    }

    private void addNewPathsToQueue() {
        tryMoveUp();
        tryMoveDown();
        tryMoveLeft();
        tryMoveRight();
    }

    private void tryMoveUp(){
        Board newBoard = tail.board.pieceAboveEmptySpot();
        if(newBoard != null){
            Node newNode = new Node(newBoard);
            boolean checkDup = searchPath.containsKey(newNode.toString());
            if(!checkDup) {
                unsearchedQueue.add(newNode);
                newNode.setPrev(tail);
            }
        }
    }
    private void tryMoveDown(){
        Board newBoard = tail.board.pieceBelowEmptySpot();
        if(newBoard != null) {
            Node newNode = new Node(newBoard);
            boolean checkDup = searchPath.containsKey(newNode.toString());
            if(!checkDup) {
                unsearchedQueue.add(newNode);
                newNode.setPrev(tail);
            }
        }
    }
    private void tryMoveLeft(){
        Board newBoard = tail.board.pieceRightOfEmptySpot();
        if(newBoard != null) {
            Node newNode = new Node(newBoard);
            boolean checkDup = searchPath.containsKey(newNode.toString());
            if(!checkDup) {
                unsearchedQueue.add(newNode);
                newNode.setPrev(tail);
            }
        }
    }
    private void tryMoveRight(){
        Board newBoard = tail.board.pieceLeftOfEmptySpot();
        if(newBoard != null){
            Node newNode = new Node(newBoard);
            boolean checkDup = searchPath.containsKey(newNode.toString());
            if(!checkDup) {
                unsearchedQueue.add(newNode);
                newNode.setPrev(tail);
            }
        }
    }
    /***** all helpers for findSolution *****/
}
