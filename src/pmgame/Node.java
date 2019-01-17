package pmgame;

public class Node {

    private int fieldtype;
    private Node parent;
    private int hValue; // heuristic waycost to pacman
    private int fValue; // g+h where g is const 10 (initial value is 0)
    private int x;
    private int y;

    public Node(int fieldtype, int x, int y) {
        this.fieldtype = fieldtype;
        this.x = x;
        this.y = y;
    }

    public int getFieldtype() {
        return fieldtype;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int gethValue() {
        return hValue;
    }

    public void sethValue(int hValue) {
        this.hValue = hValue;
    }

    public int getfValue() {
        return fValue;
    }

    public void setfValue(int fValue) {
        this.fValue = fValue;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
