package sample;

/**
 * Created by cozy on 10/16/16.
 */
public class Node implements Comparable<Node> {
    private String key;
    private Node prev = null;
    public Board board;

    public Node(Board board){
        key = board.toString();
        this.board = new Board(key);
    }

    public int getHeuristic(){
        return board.heuristic();
    }

    public Node getPrev(){
        return prev;
    }

    public void setPrev(Node prev){
        this.prev = prev;
    }

    // compares heuristic values of Nodes
    public int compareTo(Node other){
        return this.getHeuristic() - other.getHeuristic();
    }

    @Override public String toString(){
        return key;
    }
}
