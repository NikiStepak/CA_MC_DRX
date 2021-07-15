package data;


import javafx.scene.paint.Color;

public class Cell implements Cloneable {

    private int id;
    private int state;
    private Color colorRGB;
    private int energy;
    private final int x, y;
    public double density;
    public int dislocation_state;

    // Constructor
    public Cell(int x, int y) {
        this.id = -1;
        this.state = 0;
        this.colorRGB = Color.WHITE;
        this.x = x;
        this.y = y;
        this.energy = 0;
        this.density = 0;
        this.dislocation_state = 0;
    }

    public Cell(Cell cell, int id, Color color) {
        this.id = id;
        this.state = 1;
        this.colorRGB = color;
        this.x = cell.x;
        this.y = cell.y;
        this.energy = 0;
        this.density = 0;
        this.dislocation_state = 1;
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
    public void setCell(int id, Color color) {
        this.id = id;
        this.state = 1;
        this.colorRGB = color;
    }

    // set all fields of Cell on the basic of other Cell
    public void setCell(Cell other) {
        this.id = other.getId();
        this.state = 1;
        this.colorRGB = other.getColorRGB();
    }

    public void setCellDRX(Cell other) {
        this.id = other.getId();
        this.dislocation_state = 1;
        this.density = 0;
        this.colorRGB = other.getColorRGB();
    }

    @Override // method which copy Cell object
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Cell{" +
                "id=" + id +
                ", state=" + state +
                ", colorRGB=" + colorRGB +
                ", energy=" + energy +
                ", x=" + x +
                ", y=" + y +
                ", density=" + density +
                ", dislocation_state=" + dislocation_state +
                '}';
    }
}
