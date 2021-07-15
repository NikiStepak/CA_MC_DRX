package data;


import javafx.scene.paint.Color;

public class Cell implements Cloneable{

    private int id;
    private int state;
    private Color colorRGB;
    private int energy;
    private int x,y;

    // Constructor
    public Cell(int x, int y) {
        this.id = -1;
        this.state = 0;
        this.colorRGB = Color.WHITE;
        this.x = x;
        this.y = y;
    }

    // Getters =============================================================
    public int getState() {
        return state;
    }

    public Color getColorRGB() {
        return colorRGB;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getEnergy() {
        return energy;
    }
    // =====================================================================

    // Setter
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    // set all fields of Cell
    public void setCell(int id, Color color){
        this.id = id;
        this.state = 1;
        this.colorRGB = color;
    }

    // set all fields of Cell on the basic of other Cell
    public void setCell(Cell other){
        this.id = other.getId();
        this.state = 1;
        this.colorRGB = other.getColorRGB();
    }

    @Override // method which copy Cell object
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Cells{" +
                "state=" + state +
                ", colorRGB=" + colorRGB +
                '}';
    }
}
