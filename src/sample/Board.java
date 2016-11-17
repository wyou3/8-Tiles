package sample; /**
 * Created by cozy on 10/16/16.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/*
* Board class contains a 3 x 3 board with the objective to
* obtain the solution board displayed below
*
* [ 1 2 3 ]
* [ 4 5 6 ]
* [ 7 8 9 ]
*
* This class allows for a randomly set board, and user
* defined board, half of which are unsolvable.
*
* This class also support automated solving of a board
*/

public class Board {

    // 3 x 3 board
    private ArrayList<Integer> board = new ArrayList<>(9);

    public Board(){
        for (int i = 0; i < 9; i++)
            board.add(i);
        Collections.shuffle(board);
    }
    public Board(String board){
        for(int i=0; i<9; ++i)
            this.board.add(Character.getNumericValue(board.charAt(i)));
    }

    public void printBoard(int moveNum){
        System.out.println(moveNum + ".");
        System.out.println("    " + printPiece(board.get(0)) + " " + printPiece(board.get(1)) + " " + printPiece(board.get(2))); // row 1
        System.out.println("    " + printPiece(board.get(3)) + " " + printPiece(board.get(4)) + " " + printPiece(board.get(5))); // row 2
        System.out.println("    " + printPiece(board.get(6)) + " " + printPiece(board.get(7)) + " " + printPiece(board.get(8))); // row 3
    }
    public void printBoard(){
        System.out.println("    " + printPiece(board.get(0)) + " " + printPiece(board.get(1)) + " " + printPiece(board.get(2))); // row 1
        System.out.println("    " + printPiece(board.get(3)) + " " + printPiece(board.get(4)) + " " + printPiece(board.get(5))); // row 2
        System.out.println("    " + printPiece(board.get(6)) + " " + printPiece(board.get(7)) + " " + printPiece(board.get(8))); // row 3
    }
    private String printPiece(int piece){
        return (piece == 0) ? " " : Integer.toString(piece);
    }

    public void findSolution(){
        System.out.println("Solving puzzle automatically...........................");
        Node startNode = new Node(this);
        SearchTree searchTree = new SearchTree(startNode);
        searchTree.findSolution();
    }

    // checks to see if each piece on board is in the correct position
    public boolean solutionFound(){
        for(int i=0; i<8; i++)
            if (board.get(i) != i+1)
                return false;
        return true;
    }

    public boolean moveIsPossible(int userPieceIndex, int emptyPieceIndex){
        boolean availableMove = false;

        // check if empty piece touches user piece
        if ((userPieceIndex - 3 >= 0) && (emptyPieceIndex == userPieceIndex - 3)) {
            availableMove = true;
        } else if ((userPieceIndex + 3 <= 8) && (emptyPieceIndex == userPieceIndex + 3)) {
            availableMove = true;
        } else if ((userPieceIndex != 0 && userPieceIndex != 3 && userPieceIndex != 6) && (emptyPieceIndex == userPieceIndex - 1)) {
            availableMove = true;
        } else if ((userPieceIndex != 2 && userPieceIndex != 5 && userPieceIndex != 8) && (emptyPieceIndex == userPieceIndex + 1)) {
            availableMove = true;
        }

        return  availableMove;
    }

    // loops user's inputs until user solves puzzle
    // or user decides to automatically solve
    public boolean move(){
        Scanner reader = new Scanner(System.in);
        boolean moveNotFound = true;

        while(moveNotFound) {
            System.out.print("Piece to move: ");
            String userInput = reader.next();
            int userMovePiece = -1;

            if(userInput.equals("s")){
                findSolution();
                return false;
            }else{
                userMovePiece = Integer.parseInt(userInput);
            }

            int userPieceIndex = board.indexOf(userMovePiece);
            int emptyPieceIndex = board.indexOf(0);
            boolean availableMove = moveIsPossible(userPieceIndex, emptyPieceIndex);

            // execute move if possible move was found
            if (availableMove) {
                board.set(emptyPieceIndex, board.get(userPieceIndex));
                board.set(userPieceIndex, 0);
                moveNotFound = false;
            } else {
                System.out.println("*** Invalid move.  Please retry. \n");
            }
        }

        System.out.println("\n");
        return true;
    }
    public boolean move(int piece){
        int userPieceIndex = board.indexOf(piece);
        int emptyPieceIndex = board.indexOf(0);
        boolean availableMove = moveIsPossible(userPieceIndex, emptyPieceIndex);

        // execute move if possible move was found
        if (availableMove) {
            board.set(emptyPieceIndex, board.get(userPieceIndex));
            board.set(userPieceIndex, 0);
        }
        return availableMove;
    }

    // checks to see if the piece at pieceIndex is
    // in the correct position on the board
    private boolean pieceInCorrectPlace(int pieceIndex, int pieceVal){
        // check if piece is already in correct place
        if(pieceVal != 9){
            if(pieceVal == pieceIndex + 1)
                return true;
        }else{
            if(pieceIndex == 8)
                return true;
        }
        return false;
    }

    // methods check to see if piece at pieceIndex can move right or left
    private boolean canMoveRight(int pieceIndex){
        if(pieceIndex == 2 || pieceIndex == 5 || pieceIndex == 8) { return false; }
        return true;
    }
    private  boolean canMoveLeft(int pieceIndex){
        if(pieceIndex == 0 || pieceIndex == 3 || pieceIndex == 6) { return  false; }
        return true;
    }

    // calculates the total heuristic of the current board
    // by calculating the sum of each piece's heuristic
    public int heuristic(){
        int totalHeuristic = 0;
        for(int i=0; i<9; i++){
            totalHeuristic += getHeuristicForPiece(i);
        }
        return totalHeuristic;
    }
    private int getHeuristicForPiece(int pieceIndex){
        int numMoves = 0;
        int currPieceIndex = pieceIndex;
        int pieceVal = board.get(pieceIndex);

        if(pieceInCorrectPlace(currPieceIndex, pieceVal)) { return numMoves; }

        // special case for 0;
        if(pieceVal == 0){ pieceVal = 9; }

        // calculate heuristic
        while(!pieceInCorrectPlace(currPieceIndex, pieceVal)){
            // set defaults
            int deltaUp = -1, deltaDown = -1, deltaLeft = -1, deltaRight = -1;

            if(currPieceIndex+1 > pieceVal){
                if(currPieceIndex - 3 >= 0) { deltaUp = Math.abs((currPieceIndex - 3) - (pieceVal - 1)); }
                if(currPieceIndex - 1 >= 0 && canMoveLeft(currPieceIndex)
                        && pieceVal - 1 != 2 && pieceVal -1 != 5) {
                    deltaLeft = Math.abs((currPieceIndex - 1) - (pieceVal - 1));
                }

                // compare moves
                if(deltaUp == -1 && deltaLeft != -1){
                    currPieceIndex -= 1;
                }else if(deltaUp != -1 && deltaLeft == -1){
                    currPieceIndex -= 3;
                }else{
                    if(deltaUp < deltaLeft){
                        currPieceIndex -= 3;
                    }else{
                        currPieceIndex -= 1;
                    }
                }
            }else if(currPieceIndex+1 < pieceVal){
                if(currPieceIndex + 3 <= 8) { deltaDown = Math.abs((currPieceIndex + 3) - (pieceVal - 1)); }
                if(currPieceIndex + 1 <= 8 && canMoveRight(currPieceIndex)
                        && pieceVal - 1 != 3 && pieceVal -1 != 6) {
                    deltaRight = Math.abs((currPieceIndex + 1) - (pieceVal - 1));
                }

                // compare moves
                if(deltaDown == -1 && deltaRight != -1){
                    currPieceIndex += 1;
                }else if(deltaDown != -1 && deltaRight == -1){
                    currPieceIndex += 3;
                }else{
                    if(deltaDown < deltaRight){
                        currPieceIndex += 3;
                    }else{
                        currPieceIndex += 1;
                    }
                }
            }

            numMoves++;
        }

        return numMoves;
    }
    public void printHeuristic(){
        int totalHeuristic = 0;
        for(int i=0; i<9; i++){
            totalHeuristic += getHeuristicForPiece(i);
        }
        System.out.println("Heuristic value: " + totalHeuristic + "\n");
    }

    @Override public String toString() {
        String board = "";
        for(int i=0; i<9; ++i)
            board += Integer.toString(this.board.get(i));
        return board;
    }

    /****************** Functions used only in finding solution *******************/

    // following functions returns a board relative to
    // empty spot, or Null if such move is possible
    public Board pieceAboveEmptySpot(){
        int indexZero = board.indexOf(0);
        if(indexZero <= 2)
            return null;
        ArrayList<Integer> newBoardList = new ArrayList<>(9);

        for(int piece : this.board) {
            newBoardList.add(piece);
        }
        newBoardList.set(indexZero, board.get(indexZero - 3));
        newBoardList.set(indexZero - 3, 0);

        //convert array list to string
        String newBoard = "";
        for (int i : newBoardList){
            newBoard += i;
        }

        return new Board(newBoard);
    }
    public Board pieceBelowEmptySpot(){
        int indexZero = board.indexOf(0);
        if(indexZero >= 6)
            return null;
        ArrayList<Integer> newBoardList = new ArrayList<>(9);

        for(int piece : this.board) {
            newBoardList.add(piece);
        }

        newBoardList.set(indexZero, board.get(indexZero + 3));
        newBoardList.set(indexZero + 3, 0);

        //convert array list to string
        String newBoard = "";
        for (int i : newBoardList){
            newBoard += i;
        }

        return new Board(newBoard);
    }
    public Board pieceRightOfEmptySpot(){
        int indexZero = board.indexOf(0);
        if(indexZero == 2 || indexZero == 5 || indexZero == 8)
            return null;
        ArrayList<Integer> newBoardList = new ArrayList<>(9);

        for(int piece : this.board) {
            newBoardList.add(piece);
        }

        newBoardList.set(indexZero, board.get(indexZero + 1));
        newBoardList.set(indexZero + 1, 0);

        //convert array list to string
        String newBoard = "";
        for (int i : newBoardList){
            newBoard += i;
        }

        return new Board(newBoard);
    }
    public Board pieceLeftOfEmptySpot(){
        int indexZero = board.indexOf(0);
        if(indexZero == 0 || indexZero == 3 || indexZero == 6)
            return null;
        ArrayList<Integer> newBoardList = new ArrayList<>(9);

        for(int piece : this.board) {
            newBoardList.add(piece);
        }
        newBoardList.set(indexZero, board.get(indexZero - 1));
        newBoardList.set(indexZero - 1, 0);

        //convert array list to string
        String newBoard = "";
        for (int i : newBoardList){
            newBoard += i;
        }

        return new Board(newBoard);
    }
}

