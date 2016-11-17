package sample; /**
 * Created by cozy on 10/16/16.
 */

import java.util.Scanner;

public class TilesDriver {
    public static void main(String[] args) {
        printGamePrompt();
        int gameOption = inputUserGameOption(); // user input: 1 or 2
        Board board = null;

        // gameOption 1: create random board
        // gameOption 2: generate board from user input
        if(gameOption == 1 || gameOption == 2) {

            if(gameOption == 1) {
                board = new Board();
            }else if(gameOption == 2) {
                String inputBoard = inputUserBoard();
                board = new Board(inputBoard);
            }

            printInitialBoard(board);

            int moveNum = 1;

            // execute user moves until user finds solution or
            // user inputs 's' to automatically solve the board
            while (!board.solutionFound() && board.move()) {
                moveNum++;
                board.printBoard(moveNum);
                board.printHeuristic();
                if(board.solutionFound()){System.out.println("Solution Found!");}
            }
        }else {
            System.out.println("Exiting program.");
        }
    }

    private static void printGamePrompt(){
        System.out.println("Author: Dale Reed \n" +
                "Class: CS 342, Fall 2016 \n" +
                "Program: #3, 8 Tiles. \n");

        System.out.println("Welcome to the 8-tiles puzzle. \n" +
                "Place the tiles in ascending numerical order.  For each  \n" +
                "move enter the piece to be moved into the blank square, \n" +
                "or 0 to exit the program. \n");
    }
    private static int inputUserGameOption(){
        System.out.println("Choose a game option: \n" +
                "  1. Start playing \n" +
                "  2. Set the starting configuration");

        System.out.print("Enter your choice --> ");
        Scanner reader = new Scanner(System.in);
        return reader.nextInt();
    }
    private static void printInitialBoard(Board b){
        // generate board and print initial board
        System.out.println();
        System.out.println("Initial board is:");
        b.printBoard(1);
        b.printHeuristic();
    }
    private static String inputUserBoard(){
        System.out.println("Some boards such as 728045163 are impossible.\n" +
                "            Others such as 245386107 are possible.");
        System.out.print("Enter a string of 6 digits (including 0) for the board --> ");
        Scanner reader = new Scanner(System.in);
        return reader.next();
    }
}
