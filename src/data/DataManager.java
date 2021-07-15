package data;

import javafx.scene.paint.Color;
import data.BoundaryCondition;

import java.awt.image.BufferedImage;
import java.util.*;

public class DataManager {
    public int xSize, ySize, amount, rows, columns;
    private double radius, radiusNu;
    public Cell[][] cells;
    private String neighbourhood;
    private Random random;
    private boolean condition;
    private Neighbours neighbours;
    private Colors colors;
    private BoundaryCondition boundaryCondition;

    private final String[] neighbourhood_tab = {"von NEUMANN", "PENTAGONAL - random", "HEXAGONAL - right",
            "HEXAGONAL - left", "HEXAGONAL - random", "MOORE", "with RADIUS"};

    private boolean[] neighbour = new boolean[8];

    // Constructor
    public DataManager() {
        boundaryCondition = new BoundaryCondition();
    }

    public DataManager(int xSize, int ySize, int rows, int columns, String neighbourhood, String condition, Double radius){
        setSize(xSize,ySize);
        set(rows * columns,neighbourhood,condition,radius);

        this.rows = rows;
        this.columns = columns;

        setCells();
    }

    public DataManager(int xSize, int ySize,int amount, double radiusNu, String neighbourhood, String condition, Double radius){
        setSize(xSize,ySize);
        set(amount,neighbourhood,condition,radius);

        this.radiusNu = radiusNu;

        setCellsRadius();
    }

    public DataManager(int xSize, int ySize, int amount, String neighbourhood, String condition, Double radius){
        setSize(xSize,ySize);
        set(amount,neighbourhood,condition,radius);

        setCellsRandom();
    }

    public DataManager (int xSize, int ySize, String neighbourhood, String condition, Double radius){
        setSize(xSize,ySize);
        set(0,neighbourhood,condition,radius);
    }
    // methods which set fields of DataManager ==================================================================
    private void setSize(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;

        random = new Random();
        this.neighbours = new Neighbours();

        cells = new Cell[xSize][ySize];
        for (int x = 0; x < xSize; x++)
            for (int y = 0; y < ySize; y++)
                cells[x][y] = new Cell();

        colors = new Colors(xSize*ySize);
    }

    private void set(int amount, String neighbourhood, String condition, Double radius){
        this.amount = amount;

        this.neighbourhood = neighbourhood;

        if (!neighbourhood.equals(this.neighbourhood_tab[1]) && !neighbourhood.equals(this.neighbourhood_tab[6]))
            setNeighbour();
        else if (neighbourhood.equals(this.neighbourhood_tab[6]))
            this.radius = radius;

        boundaryCondition = new BoundaryCondition();
        this.condition = boundaryCondition.getCondition(condition);
    }

    private void setNeighbour(){
        for (int i=1; i<8; i+=2)
            this.neighbour[i] = true;

        if(this.neighbourhood.equals(this.neighbourhood_tab[2])){
            this.neighbour[2] = true;
            this.neighbour[6] = true;
        }
        else if (this.neighbourhood.equals(this.neighbourhood_tab[3])){
            this.neighbour[0] = true;
            this.neighbour[4] = true;
        }
        else if (this.neighbourhood.equals(this.neighbourhood_tab[5])){
            for (int i=0; i<8; i+=2)
                this.neighbour[i] = true;
        }
    }

    private void setNeighbourHex(int rand){
        if(rand == 0){
            this.neighbour[2] = true;
            this.neighbour[6] = true;
            this.neighbour[0] = false;
            this.neighbour[4] = false;
        }
        else {
            this.neighbour[0] = true;
            this.neighbour[4] = true;
            this.neighbour[2] = false;
            this.neighbour[6] = false;
        }
    }

    private void setNeighbourPent(int rand){
        if(rand == 0){
            this.neighbour = new boolean[]{true, true,false,false,false,true,true,true};
        }
        if(rand == 1){
            this.neighbour = new boolean[]{false,true, true,true,true,true,false,false};
        }
        if(rand == 2){
            this.neighbour = new boolean[]{false,false,false,true, true,true,true,true};
        }
        if(rand == 3){
            this.neighbour = new boolean[]{true, true,true,true,false,false,false,true};
        }
    }
    // ==========================================================================================================

    // methods which return DataManager tab =====================================================================
    public String[] getBoundaryCondition_tab(){
        return boundaryCondition.getBoundaryCondition_tab();
    }

    public String[] getNeighbourhood_tab(){
        return neighbourhood_tab;
    }
    // ==========================================================================================================

    // methods which set Cell with status = 1 in cells tab ======================================================
    public void setCellTick(int x, int y){
        if(this.cells[x][y].getState()==0){
            Color color = colors.getColor();
            this.cells[x][y].setCell(this.amount, color);
            this.amount++;
        }
    }

    private void setCellsRadius() {
        int x, y, i=0, x_dist, y_dist, id=0;
        double dist;
        Set<String> coordinates = new HashSet<>();
        Set<String> coordinatedSet = new HashSet<>();
        int[] xy = new int[this.amount*2];
        Color color;
        boolean isRad;
        while (coordinatedSet.size()<this.amount){
            x = random.nextInt(this.xSize);
            y = random.nextInt(this.ySize);
            if(coordinates.add(x+" - "+y)){
                if(coordinatedSet.size()!=0){
                    isRad = false;
                    for(int j = 0; j < i; j++) {
                        x_dist = xy[j];
                        y_dist = xy[j+1];
                        dist = distance(x, y, x_dist, y_dist);
                        if (dist < this.radiusNu) {
                            isRad = true;
                            break;
                        }
                    }
                    if (!isRad){
                        coordinatedSet.add(x+"-"+y);
                        xy[i] = x;
                        xy[i+1] = y;
                        i+=2;
                        color = colors.getColor();
                        this.cells[x][y].setCell(id, color);
                        id++;
                    }
                }
                else {
                    coordinatedSet.add(x+"-"+y);
                    xy[i] = x;
                    xy[i+1] = y;
                    i+=2;
                    color = colors.getColor();
                    this.cells[x][y].setCell(id, color);
                    id++;
                }
            }
        }
    }

    private void setCellsRandom(){
        int x, y, id=0;
        Set<String> stringSet = new HashSet<>();
        Color color;
        while(stringSet.size()<this.amount){
            x = random.nextInt(this.xSize);
            y = random.nextInt(this.ySize);
            if(stringSet.add(x+" - "+y)) {
                color = colors.getColor();

                this.cells[x][y].setCell(id,color);

                id++;
            }
        }
    }

    private void setCells() {
        double c = (double) this.xSize / (this.columns + 1);
        double r = (double) this.ySize / (this.rows + 1);
        Color color;
        int x, y, id=0;

        for (int i = 1; i <= this.columns; i++)
            for (int j = 1; j <= this.rows; j++) {
                x = (int) (c * i);
                y = (int) (r * j);

                color = colors.getColor();

                this.cells[x][y].setCell(id, color);

                id++;
            }
    }
    // ==========================================================================================================

    // method which is one iteration of all code
    public boolean startCode() {
        boolean start = false;
        int x, y, rand;
        Cell[][] cells_copy;
        try {
            cells_copy = copy();
        } catch (CloneNotSupportedException e) {
            return false;
        }

        if(this.neighbourhood.equals(this.neighbourhood_tab[6])){
            int[] xy;
            for (int i =0; i<this.xSize;i++){
                for (int j=0;j<this.ySize;j++){
                    if(this.cells[i][j].getState()==0){
                        start = true;
                        xy = findMinDist(i,j);
                        x = xy[0];
                        y = xy[1];
                        if(distance(i,j,x,y)<=this.radius) {
                            cells_copy[i][j].setCell(this.cells[x][y]);
                        }
                    }
                }
            }
        }
        else {
            for (int i = 0; i < this.xSize; i++) {
                for (int j = 0; j < this.ySize; j++) {
                    if (this.cells[i][j].getState() == 0) {
                        if (this.neighbourhood.equals(this.neighbourhood_tab[4])) {
                            rand = random.nextInt(2);
                            setNeighbourHex(rand);
                        }
                        else if (this.neighbourhood.equals(this.neighbourhood_tab[1])) {
                            rand = random.nextInt(4);
                            setNeighbourPent(rand);
                        }
                        start = true;
                        this.neighbours.clear();
                        if (this.neighbour[0]) {
                            if (i - 1 >= 0 && j - 1 >= 0) {
                                this.neighbours.addCell(this.cells[i-1][j-1]);
                            } else if (this.condition) {
                                this.neighbours.addCell(this.cells[this.xSize - 1][this.ySize - 1]);
                            }
                        }
                        if (this.neighbour[1]) {
                            if (j - 1 >= 0) {
                                this.neighbours.addCell(this.cells[i][j-1]);
                            } else if (this.condition) {
                                this.neighbours.addCell(this.cells[i][this.ySize - 1]);
                            }
                        }
                        if (this.neighbour[2]) {
                            if (i + 1 < this.xSize && j - 1 >= 0) {
                                this.neighbours.addCell(this.cells[i+1][j-1]);
                            } else if (this.condition) {
                                this.neighbours.addCell(this.cells[0][this.ySize-1]);
                            }
                        }
                        if (this.neighbour[3]) {
                            if (i + 1 < this.xSize) {
                                this.neighbours.addCell(this.cells[i+1][j]);
                            } else if (this.condition) {
                                this.neighbours.addCell(this.cells[0][j]);
                            }
                        }
                        if (this.neighbour[4]) {
                            if (i + 1 < this.xSize && j + 1 < this.ySize) {
                                this.neighbours.addCell(this.cells[i+1][j+1]);
                            } else if (this.condition) {
                                this.neighbours.addCell(this.cells[0][0]);
                            }
                        }
                        if (this.neighbour[5]) {
                            if (j + 1 < this.ySize) {
                                this.neighbours.addCell(this.cells[i][j+1]);
                            } else if (this.condition) {
                                this.neighbours.addCell(this.cells[i][0]);
                            }
                        }
                        if (this.neighbour[6]) {
                            if (i - 1 >= 0 && j + 1 < this.ySize) {
                                this.neighbours.addCell(this.cells[i-1][j+1]);
                            } else if (this.condition) {
                                this.neighbours.addCell(this.cells[this.xSize-1][0]);
                            }
                        }
                        if (this.neighbour[7]) {
                            if (i - 1 >= 0) {
                                this.neighbours.addCell(this.cells[i-1][j]);
                            } else if (this.condition) {
                                this.neighbours.addCell(this.cells[this.xSize-1][j]);
                            }
                        }
                        if (this.neighbours.size() > 0) {
                            cells_copy[i][j].setCell(this.neighbours.findMax());
                        }
                    }
                }
            }
        }
        this.cells = cells_copy;
        return start;
    }

    // method which copy tab of Cells
    private Cell[][] copy() throws CloneNotSupportedException {
        Cell[][] tab = new Cell[this.xSize][this.ySize];
        for (int i =0; i < this.xSize; i++){
            for (int j = 0; j < this.ySize; j++){
                tab[i][j] = (Cell) this.cells[i][j].clone();
            }
        }
        return tab;
    }

    // method which return distance between two Cells
    private double distance(int x1, int y1, int x2, int y2){
        return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }

    // method which find coordinates of Cell which status equals 1 and is the nearest - if neighbourhood equals "with RADIUS"
    private int[] findMinDist(int i, int j){
        double minDist = Double.MAX_VALUE, dist;
        int[] xy = new int[2];
        for (int x=0;x<this.xSize;x++) {
            for (int y = 0; y < this.ySize; y++) {
                if (this.cells[x][y].getState() == 1) {
                    dist = distance(i, j, x, y);
                    if (dist < minDist) {
                        minDist = dist;
                        xy[0] = x;
                        xy[1] = y;
                    }
                }
            }
        }
        return xy;
    }

    public BufferedImage convertToImage(int width, int height, int cellSize){
        BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Color colorFx;
        java.awt.Color colorAwt;
        for(int i=0;i<this.xSize;i++){
            for (int j=0;j<this.ySize;j++) {
                colorFx = this.cells[i][j].getColorRGB();
                colorAwt = new java.awt.Color((float) colorFx.getRed(), (float) colorFx.getGreen(), (float) colorFx.getBlue(), (float) colorFx.getOpacity());
                for (int x = 0; x < cellSize; x++)
                    for (int y = 0; y < cellSize; y++)
                        bufferedImage.setRGB(i*cellSize + x, j*cellSize + y, colorAwt.getRGB());
            }
        }
        return bufferedImage;
    }
}