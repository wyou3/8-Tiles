package sample;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

/**
 * Created by Jin on 11/9/16.
 */
public class Controller {
    @FXML private List<Button> buttonList; // Array of 9 buttons on the game board
    @FXML private Button exit;             // Exit button

    private Board board;                   // Game board currently in play
    private int[] boardSeq = new int[9];   // User set board sequence
    private int boardSeqIndex = 0;         // Used to track users set piece in order

    /*
    * Moves piece user clicks to empty slot, if move is valid
     */
    EventHandler pieceMove = new EventHandler() {
        @Override
        public void handle(Event event) {
            /*
            * Check if piece that user selects
            * is a valid, moves piece if it is
            * and updates board accordingly
            */
            Object obj = event.getSource();
            if(obj instanceof Button) {
                String pieceString = ((Button)obj).getText();
                int piece = 0;

                if (pieceString != "") piece = Integer.parseInt(pieceString);
                else return;

                board.move(piece);
                updateBoardGUI(board);
                checkBoardSolved(board);
            }
        }
    };

    /*
    * Sets board piece into board sequence when user sets board
     */
    EventHandler setPiece = new EventHandler() {
        @Override
        /*
        * 1. Checks if all pieces have been set into board sequence by user
        * 2. Inputs selected piece if sequence has not fully set
        * 3. Updates board to set board ing play if sequence has been set
         */
        public void handle(Event event) {
            if (boardSeqSet() == false) {
                Object obj = event.getSource();
                if(obj instanceof Button) {
                    String pieceString = ((Button) obj).getText();
                    if (pieceString == "") {
                        return;
                    }
                    int piece = Integer.parseInt(pieceString);

                    boardSeq[boardSeqIndex] = piece;
                    boardSeqIndex++;
                    ((Button) obj).setText("");
                }
            }
            boardSeqSet();
        }
    };

    /*
    * Event handler for "New Board" button
    * - Creates random new board and sets it to be played
     */
    public void newBoard() {
        /*
        *   Creates a random board and sets it
        *   into play
         */

        board = new Board();
        int i = 0;
        for (Button b : buttonList) {
            if (board.toString().charAt(i) != '0')
                b.setText(Character.toString(board.toString().charAt(i)));
            i++;

            b.removeEventHandler(MOUSE_CLICKED, setPiece);
            b.addEventHandler(MOUSE_CLICKED, pieceMove);
        }
        updateBoardGUI(board);
    }

    /*
    * Event handler for "Set Board" button
    * - Sets up GUI to allow userto set up board sequence
     */
    public void setBoard() {
        /*
        * Changes the board display and game board button
        * event handlers to allow user to set board sequence
         */
        boardSeqIndex = 0;
        int i = 1;
        for (Button b : buttonList) {
            b.setText(Integer.toString(i));
            i++;

            // modify event handler to handle selected piece sequence
            b.removeEventHandler(MOUSE_CLICKED, pieceMove);
            b.addEventHandler(MOUSE_CLICKED, setPiece);
        }
    }

    /*
    * Event handler for "Solve" button
    * - Solves board in play if board is solvable and
    *   animates the solution path, other wise outputs
    *   that board is not solvable
     */
    public void solve() {
        Node startNode = new Node(board);
        SearchTree searchTree = new SearchTree(startNode);
        ArrayList<Node> solutionPath = searchTree.findSolution();

        if (solutionPath == null && !board.solutionFound()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message");
            alert.setContentText("Board is unsolvable.\n No solution found.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {
            if (solutionPath != null) {
                Thread gameLoop = new Thread(() ->
                {
                    // Updates board every 300ms for every solution step
                    for(Node n : solutionPath){
                        try
                        {
                            Thread.sleep(300);
                        }
                        catch (InterruptedException ex) {}
                        Platform.runLater(() ->
                        {
                            updateBoardGUI(n.board);
                        });
                    }
                });
                gameLoop.start();
                board = solutionPath.get(solutionPath.size() - 1).board;
            }
        }
    }

    /*
    * Event handler for "Exit" button
    * - Stops all processes and exits GUI
     */
    public void exit(){
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
    }

    /*
    * Updates current board that is in play
    * @param Board board        Board updating GUI
     */
    public void updateBoardGUI(Board board) {
        int i = 0;
        for (Button b : buttonList) {
            if (board.toString().charAt(i) == '0')
                b.setText("");
            else
                b.setText(Character.toString(board.toString().charAt(i)));
            i++;
        }
    }

    /*
    * Checks if current board has been solved
    * @param Board board        Board checked if solved
     */
    private void checkBoardSolved(Board board) {
        if (board.solutionFound()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message");
            alert.setContentText("You Solved the Board!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }

    /*
    *  Checks if all pieces have been set into board
    *  sequence when user sets board
     */
    private boolean boardSeqSet(){
        for(Button b: buttonList){
            if(b.getText() != "")
                return false;
        }

        StringBuilder setBoard = new StringBuilder("123456780");
        int strIndex;
        char piece;
        for(int i=0; i<9; i++){
            strIndex = boardSeq[i] - 1;
            piece = (char)(i + '0');
            setBoard.setCharAt(strIndex, piece);
        }
        board = new Board(setBoard.toString());
        updateBoardGUI(board);

        for(Button b: buttonList){
            b.removeEventHandler(MOUSE_CLICKED, setPiece);
            b.addEventHandler(MOUSE_CLICKED, pieceMove);
        }

        return true;
    }
}
