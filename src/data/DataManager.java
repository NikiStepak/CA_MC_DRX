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
    public List<Cell> list;

    private final String[] neighbourhood_tab = {"von NEUMANN", "PENTAGONAL - random", "HEXAGONAL - right",
            "HEXAGONAL - left", "HEXAGONAL - random", "MOORE", "with RADIUS"};

    private boolean[] neighbour = new boolean[8];

    // Constructor
    public DataManager() {
        boundaryCondition = new BoundaryCondition();
    }

    public DataManager(int xSize, int ySize, int rows, int columns, String neighbourhood, String condition, Double radius) {
        setSize(xSize, ySize);
        set(rows * columns, neighbourhood, condition, radius);

        this.rows = rows;
        this.columns = columns;

        setCells();
    }

    public DataManager(int xSize, int ySize, int amount, double radiusNu, String neighbourhood, String condition, Double radius) {
        setSize(xSize, ySize);
        set(amount, neighbourhood, condition, radius);

        this.radiusNu = radiusNu;

        setCellsRadius();
    }

    public DataManager(int xSize, int ySize, int amount, String neighbourhood, String condition, Double radius) {
        setSize(xSize, ySize);
        set(amount, neighbourhood, condition, radius);

        setCellsRandom();
    }

    public DataManager(int xSize, int ySize, String neighbourhood, String condition, Double radius) {
        setSize(xSize, ySize);
        set(0, neighbourhood, condition, radius);
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
                cells[x][y] = new Cell(x, y);

        colors = new Colors(xSize * ySize);
    }

    private void set(int amount, String neighbourhood, String condition, Double radius) {
        this.amount = amount;

        this.neighbourhood = neighbourhood;

        if (!neighbourhood.equals(this.neighbourhood_tab[1]) && !neighbourhood.equals(this.neighbourhood_tab[6]))
            setNeighbour();
        else if (neighbourhood.equals(this.neighbourhood_tab[6]))
            this.radius = radius;

        boundaryCondition = new BoundaryCondition();
        this.condition = boundaryCondition.getCondition(condition);

        this.list = new LinkedList<>();
    }

    private int n = 0;

    private void countN(){
        for (boolean b: this.neighbour)
            if(b)
                this.n++;
    }

    private void setNeighbour() {
        for (int i = 1; i < 8; i += 2)
            this.neighbour[i] = true;
        if (this.neighbourhood.equals(this.neighbourhood_tab[2])) {
            this.neighbour[2] = true;
            this.neighbour[6] = true;
        } else if (this.neighbourhood.equals(this.neighbourhood_tab[3])) {
            this.neighbour[0] = true;
            this.neighbour[4] = true;
        } else if (this.neighbourhood.equals(this.neighbourhood_tab[5])) {
            for (int i = 0; i < 8; i += 2)
                this.neighbour[i] = true;
        }
    }

    private void setNeighbourHex(int rand) {
        if (rand == 0) {
            this.neighbour[2] = true;
            this.neighbour[6] = true;
            this.neighbour[0] = false;
            this.neighbour[4] = false;
        } else {
            this.neighbour[0] = true;
            this.neighbour[4] = true;
            this.neighbour[2] = false;
            this.neighbour[6] = false;
        }
    }

    private void setNeighbourPent(int rand) {
        if (rand == 0) {
            this.neighbour = new boolean[]{true, true, false, false, false, true, true, true};
        }
        if (rand == 1) {
            this.neighbour = new boolean[]{false, true, true, true, true, true, false, false};
        }
        if (rand == 2) {
            this.neighbour = new boolean[]{false, false, false, true, true, true, true, true};
        }
        if (rand == 3) {
            this.neighbour = new boolean[]{true, true, true, true, false, false, false, true};
        }
    }
    // ==========================================================================================================

    // methods which return DataManager tab =====================================================================
    public String[] getBoundaryCondition_tab() {
        return boundaryCondition.getBoundaryCondition_tab();
    }

    public String[] getNeighbourhood_tab() {
        return neighbourhood_tab;
    }
    // ==========================================================================================================

    // methods which set Cell with status = 1 in cells tab ======================================================
    public void setCellTick(int x, int y) {
        if (this.cells[x][y].getState() == 0) {
            Color color = colors.getColor();
            this.cells[x][y].setCell(this.amount, color);
            this.amount++;
        }
    }

    private void setCellsRadius() {
        int x, y, i = 0, x_dist, y_dist, id = 0;
        double dist;
        Set<String> coordinates = new HashSet<>();
        Set<String> coordinatedSet = new HashSet<>();
        int[] xy = new int[this.amount * 2];
        Color color;
        boolean isRad;
        while (coordinatedSet.size() < this.amount) {
            x = random.nextInt(this.xSize);
            y = random.nextInt(this.ySize);
            if (coordinates.add(x + " - " + y)) {
                if (coordinatedSet.size() != 0) {
                    isRad = false;
                    for (int j = 0; j < i; j++) {
                        x_dist = xy[j];
                        y_dist = xy[j + 1];
                        dist = distance(x, y, x_dist, y_dist);
                        if (dist < this.radiusNu) {
                            isRad = true;
                            break;
                        }
                    }
                    if (!isRad) {
                        coordinatedSet.add(x + "-" + y);
                        xy[i] = x;
                        xy[i + 1] = y;
                        i += 2;
                        color = colors.getColor();
                        this.cells[x][y].setCell(id, color);
                        id++;
                    }
                } else {
                    coordinatedSet.add(x + "-" + y);
                    xy[i] = x;
                    xy[i + 1] = y;
                    i += 2;
                    color = colors.getColor();
                    this.cells[x][y].setCell(id, color);
                    id++;
                }
            }
        }
    }

    private void setCellsRandom() {
        int x, y, id = 0;
        Set<String> stringSet = new HashSet<>();
        Color color;
        while (stringSet.size() < this.amount) {
            x = random.nextInt(this.xSize);
            y = random.nextInt(this.ySize);
            if (stringSet.add(x + " - " + y)) {
                color = colors.getColor();

                this.cells[x][y].setCell(id, color);

                id++;
            }
        }
    }

    private void setCells() {
        double c = (double) this.xSize / (this.columns + 1);
        double r = (double) this.ySize / (this.rows + 1);
        Color color;
        int x, y, id = 0;

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
    private boolean cell;

    private Cell checkNeighbour(int x, int y) {
        if (this.cells[x][y].getState() == 1) {
            return this.cells[x][y];
        }
        return null;
    }

    // method which is one iteration of all code
    public boolean CA() {
        boolean start = false;
        int rand;
        Cell[][] cells_copy;
        try {
            cells_copy = copy();
        } catch (CloneNotSupportedException e) {
            return false;
        }

        if (this.neighbourhood.equals(this.neighbourhood_tab[6])) {
            int x0, y0, x1, y1;
            double minDist, dist;
            boolean k;
            for (int i = 0; i < this.xSize; i++) {
                for (int j = 0; j < this.ySize; j++) {
                    if (this.cells[i][j].getState() == 0) {
                        start = true;
                        minDist = Double.MAX_VALUE;
                        x1 = -1;
                        y1 = -1;
                        x0 = -1;
                        y0 = -1;
                        k = false;
                        for (int x = (int) -this.radius; x <= this.radius; x++) {
                            for (int y = (int) -this.radius; y <= this.radius; y++) {
                                if (i + x >= 0 && j + y >= 0 && i + x < this.xSize && j + y < this.ySize) {
                                    if (this.cells[i + x][j + y].getState() == 1) {
                                        dist = distance(i, j, i + x, j + y);
                                        if (dist < minDist) {
                                            minDist = dist;
                                            x0 = i + x;
                                            y0 = j + y;
                                            x1 = x0;
                                            y1 = y0;
                                            k = true;
                                        }
                                    }
                                } else if (this.condition) {
                                    if (i + x < 0) {
                                        if (j + y < 0) {
                                            if (this.cells[this.xSize + x + i][this.ySize + y + j].getState() == 1) {
                                                dist = distance(i, j, i + x, j + y);
                                                if (dist < minDist) {
                                                    minDist = dist;
                                                    x0 = this.xSize + x + i;
                                                    y0 = this.ySize + y + j;
                                                    x1 = i + x;
                                                    y1 = j + y;
                                                    k = true;
                                                }
                                            }
                                        } else if (j + y >= this.ySize) {
                                            if (this.cells[this.xSize + x + i][y - 1].getState() == 1) {
                                                dist = distance(i, j, i + x, j + y);
                                                if (dist < minDist) {
                                                    minDist = dist;
                                                    x0 = this.xSize + x + i;
                                                    y0 = y - 1;
                                                    x1 = i + x;
                                                    y1 = j + y;
                                                    k = true;
                                                }
                                            }
                                        } else if (this.cells[this.xSize + x + i][j + y].getState() == 1) {
                                            dist = distance(i, j, i + x, j + y);
                                            if (dist < minDist) {
                                                minDist = dist;
                                                x0 = this.xSize + x + i;
                                                y0 = j + y;
                                                x1 = i + x;
                                                y1 = j + y;
                                                k = true;
                                            }
                                        }
                                    } else if (i + x >= this.xSize) {
                                        if (j + y < 0) {
                                            if (this.cells[x - 1][this.ySize + y + j].getState() == 1) {
                                                dist = distance(i, j, i + x, j + y);
                                                if (dist < minDist) {
                                                    minDist = dist;
                                                    x0 = x - 1;
                                                    y0 = this.ySize + y + j;
                                                    x1 = i + x;
                                                    y1 = j + y;
                                                    k = true;
                                                }
                                            }
                                        } else if (j + y >= this.ySize) {
                                            if (this.cells[x - 1][y - 1].getState() == 1) {
                                                dist = distance(i, j, i + x, j + y);
                                                if (dist < minDist) {
                                                    minDist = dist;
                                                    x0 = x - 1;
                                                    y0 = y - 1;
                                                    x1 = i + x;
                                                    y1 = j + y;
                                                    k = true;
                                                }
                                            }
                                        } else if (this.cells[x - 1][j + y].getState() == 1) {
                                            dist = distance(i, j, i + x, j + y);
                                            if (dist < minDist) {
                                                minDist = dist;
                                                x0 = x - 1;
                                                y0 = j + y;
                                                x1 = i + x;
                                                y1 = j + y;
                                                k = true;
                                            }
                                        }
                                    } else if (j + y < 0) {
                                        if (this.cells[i + x][this.ySize + y + j].getState() == 1) {
                                            dist = distance(i, j, i + x, j + y);
                                            if (dist < minDist) {
                                                minDist = dist;
                                                x0 = i + x;
                                                y0 = this.ySize + y + j;
                                                x1 = i + x;
                                                y1 = j + y;
                                                k = true;
                                            }
                                        }
                                    } else if (j + y >= this.ySize) {
                                        if (this.cells[i + x][y - 1].getState() == 1) {
                                            dist = distance(i, j, i + x, j + y);
                                            if (dist < minDist) {
                                                minDist = dist;
                                                x0 = i + x;
                                                y0 = y - 1;
                                                x1 = i + x;
                                                y1 = j + y;
                                                k = true;
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        if (k) {
                            if (distance(i, j, x1, y1) <= this.radius) {
                                cells_copy[i][j].setCell(this.cells[x0][y0]);
                            }
                        }
                    }
                }
            }
        } else {
            Cell copyCell;
            for (int i = 0; i < this.xSize; i++) {
                for (int j = 0; j < this.ySize; j++) {
                    if (this.cells[i][j].getState() == 0) {
                        copyCell = null;
                        if (this.neighbourhood.equals(this.neighbourhood_tab[4])) {
                            rand = random.nextInt(2);
                            setNeighbourHex(rand);
                        } else if (this.neighbourhood.equals(this.neighbourhood_tab[1])) {
                            rand = random.nextInt(4);
                            setNeighbourPent(rand);
                        }

                        start = true;
                        this.neighbours.clear();
                        if (this.neighbour[0]){
                            if (i - 1 >= 0 && j - 1 >= 0) {
                                copyCell = checkNeighbour(i - 1, j - 1);
                            } else if (this.condition) {
                                if (i - 1 < 0 && j - 1 < 0)
                                    copyCell = checkNeighbour(this.xSize - 1, this.ySize - 1);
                                else if (i - 1 < 0)
                                    copyCell = checkNeighbour(this.xSize - 1, j - 1);
                                else
                                    copyCell = checkNeighbour(i - 1, this.ySize - 1);
                            }
                        }
                        if(copyCell==null && this.neighbour[1]){
                            if (j - 1 >= 0) {
                                copyCell = checkNeighbour(i, j - 1);
                            } else if (this.condition) {
                                copyCell = checkNeighbour(i, this.ySize - 1);
                            }
                        }
                        if(copyCell==null && this.neighbour[2]) {
                            if (i + 1 < this.xSize && j - 1 >= 0) {
                                copyCell = checkNeighbour(i + 1, j - 1);
                            } else if (this.condition) {
                                if (i + 1 >= this.xSize && j - 1 < 0)
                                    copyCell = checkNeighbour(0, this.ySize - 1);
                                else if (j - 1 < 0)
                                    copyCell = checkNeighbour(i + 1, this.ySize - 1);
                                else
                                    copyCell = checkNeighbour(0, j - 1);
                            }
                        }
                        if(copyCell==null && this.neighbour[3]){
                            if (i + 1 < this.xSize) {
                                copyCell = checkNeighbour(i+1, j);
                            } else if (this.condition) {
                                copyCell = checkNeighbour(0, j);
                            }
                        }
                        if(copyCell==null && this.neighbour[4]) {
                            if (i + 1 < this.xSize && j + 1 < this.ySize) {
                                copyCell = checkNeighbour(i + 1, j + 1);
                            } else if (this.condition) {
                                if (i + 1 >= this.xSize && j + 1 >= this.ySize)
                                    copyCell = checkNeighbour(0, 0);
                                else if (i + 1 >= this.xSize)
                                    copyCell = checkNeighbour(0, j+1);
                                else
                                    copyCell = checkNeighbour(i+1, 0);
                            }
                        }
                        if(copyCell==null && this.neighbour[5]){
                            if (j + 1 < this.ySize) {
                                copyCell = checkNeighbour(i, j+1);
                            } else if (this.condition) {
                                copyCell = checkNeighbour(i, 0);
                            }
                        }
                        if(copyCell==null && this.neighbour[6]) {
                            if (i - 1 >= 0 && j + 1 < this.ySize) {
                                copyCell = checkNeighbour(i - 1, j + 1);
                            } else if (this.condition) {
                                if (i - 1 < 0 && j + 1 >= this.ySize)
                                    copyCell = checkNeighbour(this.xSize - 1, 0);
                                else if (i - 1 < 0)
                                    copyCell = checkNeighbour(this.xSize - 1, j+1);
                                else
                                    copyCell = checkNeighbour(i - 1, 0);

                            }
                        }
                        if(copyCell==null && this.neighbour[7]){
                            if (i - 1 >= 0) {
                                copyCell = checkNeighbour(i-1, j);
                            } else if (this.condition) {
                                copyCell = checkNeighbour(this.xSize - 1, j);
                            }
                        }

                        if (copyCell != null) {
                            cells_copy[i][j].setCell(copyCell);
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
        for (int i = 0; i < this.xSize; i++) {
            for (int j = 0; j < this.ySize; j++) {
                tab[i][j] = (Cell) this.cells[i][j].clone();
            }
        }
        return tab;
    }

    // method which return distance between two Cells
    private double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private double minDistance(double dist1, double dist2) {
        if (dist1 < dist2)
            return dist1;
        else return dist2;
    }

    public BufferedImage convertToImage(int width, int height, int cellSize) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Color colorFx;
        java.awt.Color colorAwt;
        for (int i = 0; i < this.xSize; i++) {
            for (int j = 0; j < this.ySize; j++) {
                colorFx = this.cells[i][j].getColorRGB();
                colorAwt = new java.awt.Color((float) colorFx.getRed(), (float) colorFx.getGreen(), (float) colorFx.getBlue(), (float) colorFx.getOpacity());
                for (int x = 0; x < cellSize; x++)
                    for (int y = 0; y < cellSize; y++)
                        bufferedImage.setRGB(i * cellSize + x, j * cellSize + y, colorAwt.getRGB());
            }
        }
        return bufferedImage;
    }


    public List energy_visualization() {
        int id;
        boolean edge;
        if (this.neighbourhood.equals(this.neighbourhood_tab[6])) {
            int[] xy;
            for (int i = 0; i < this.xSize; i++) {
                for (int j = 0; j < this.ySize; j++) {
                    edge = false;
                    id = this.cells[i][j].getId();
                    for (int x = 0; x <= this.radius; x++)
                        for (int y = 0; y <= this.radius; y++) {
                            if (i - x >= 0) {
                                if (j - y >= 0) {
                                    if (distance(i, j, i - x, j - y) <= this.radius) {
                                        if (id != this.cells[i - x][j - y].getId())
                                            edge = true;
                                    }
                                } else if (this.condition) {
                                    if (distance(i, j, i - x, j - y) <= this.radius) {
                                        if (id != this.cells[i - x][this.ySize - y - 1].getId())
                                            edge = true;
                                    }
                                }

                                if (j + y < this.ySize) {
                                    if (distance(i, j, i - x, j + y) <= this.radius) {
                                        if (id != this.cells[i - x][j + y].getId())
                                            edge = true;
                                    }
                                } else if (this.condition) {
                                    if (distance(i, j, i - x, j + y) <= this.radius) {
                                        if (id != this.cells[i - x][y - 1].getId())
                                            edge = true;
                                    }
                                }
                            }

                            if (i + x < this.xSize) {
                                if (j - y >= 0) {
                                    if (distance(i, j, i + x, j - y) <= this.radius) {
                                        if (id != this.cells[i + x][j - y].getId())
                                            edge = true;
                                    }
                                }

                                if (j + y < this.ySize) {
                                    if (distance(i, j, i + x, j + y) <= this.radius) {
                                        if (id != this.cells[i + x][j + y].getId())
                                            edge = true;
                                    }
                                }
                            }
                        }
                    if (edge)
                        this.list.add(this.cells[i][j]);
                }
            }
        } else
            for (int i = 0; i < this.xSize; i++)
                for (int j = 0; j < this.ySize; j++) {
                    edge = false;
                    id = this.cells[i][j].getId();
                    if (this.neighbourhood.equals(this.neighbourhood_tab[4]) || this.neighbourhood.equals(this.neighbourhood_tab[1])) {
                        this.neighbour = new boolean[]{true, true, true, true, true, true, true, true};
                    }

                    if (this.neighbour[0]) {
                        if (i - 1 >= 0 && j - 1 >= 0) {
                            if (id != this.cells[i - 1][j - 1].getId())
                                edge = true;
                        } else if (this.condition) {
                            if (id != this.cells[this.xSize - 1][this.ySize - 1].getId()) {
                                edge = true;
                            }
                        }
                    }
                    if (!edge && this.neighbour[1]) {
                        if (j - 1 >= 0) {
                            if (id != this.cells[i][j - 1].getId())
                                edge = true;
                        } else if (this.condition) {
                            if (id != this.cells[i][this.ySize - 1].getId())
                                edge = true;
                        }
                    }
                    if (!edge && this.neighbour[2]) {
                        if (i + 1 < this.xSize && j - 1 >= 0) {
                            if (id != this.cells[i + 1][j - 1].getId())
                                edge = true;
                        } else if (this.condition) {
                            if (id != this.cells[0][this.ySize - 1].getId())
                                edge = true;
                        }
                    }
                    if (!edge && this.neighbour[3]) {
                        if (i + 1 < this.xSize) {
                            if (id != this.cells[i + 1][j].getId())
                                edge = true;
                        } else if (this.condition) {
                            if (id != this.cells[0][j].getId())
                                edge = true;
                        }
                    }
                    if (!edge && this.neighbour[4]) {
                        if (i + 1 < this.xSize && j + 1 < this.ySize) {
                            if (id != this.cells[i + 1][j + 1].getId())
                                edge = true;
                        } else if (this.condition) {
                            if (id != this.cells[0][0].getId())
                                edge = true;
                        }
                    }
                    if (!edge && this.neighbour[5]) {
                        if (j + 1 < this.ySize) {
                            if (id != this.cells[i][j + 1].getId())
                                edge = true;
                        } else if (this.condition) {
                            if (id != this.cells[i][0].getId())
                                edge = true;
                        }
                    }
                    if (!edge && this.neighbour[6]) {
                        if (i - 1 >= 0 && j + 1 < this.ySize) {
                            if (id != this.cells[i - 1][j + 1].getId())
                                edge = true;
                        } else if (this.condition) {
                            if (id != this.cells[this.xSize - 1][0].getId())
                                edge = true;
                        }
                    }
                    if (!edge == this.neighbour[7]) {
                        if (i - 1 >= 0) {
                            if (id != this.cells[i - 1][j].getId())
                                edge = true;
                        } else if (this.condition) {
                            if (id != this.cells[this.xSize - 1][j].getId())
                                edge = true;
                        }
                    }

                    if (edge) {
                        this.list.add(this.cells[i][j]);
                    }
                }
        return this.list;
    }

    public boolean MC(double kT) {
        boolean start = false;
        int rand;
        Cell[][] cells_copy;
        try {
            cells_copy = copy();
        } catch (CloneNotSupportedException e) {
            return false;
        }

        if (this.neighbourhood.equals(this.neighbourhood_tab[6])) {
            int x0, y0, x1, y1;
            double minDist, dist;
            boolean k;
            for (int i = 0; i < this.xSize; i++) {
                for (int j = 0; j < this.ySize; j++) {
                    if (this.cells[i][j].getState() == 0) {
                        start = true;
                        minDist = Double.MAX_VALUE;
                        x1 = -1;
                        y1 = -1;
                        x0 = -1;
                        y0 = -1;
                        k = false;
                        for (int x = (int) -this.radius; x <= this.radius; x++) {
                            for (int y = (int) -this.radius; y <= this.radius; y++) {
                                if (i + x >= 0 && j + y >= 0 && i + x < this.xSize && j + y < this.ySize) {
                                    if (this.cells[i + x][j + y].getState() == 1) {
                                        dist = distance(i, j, i + x, j + y);
                                        if (dist < minDist) {
                                            minDist = dist;
                                            x0 = i + x;
                                            y0 = j + y;
                                            x1 = x0;
                                            y1 = y0;
                                            k = true;
                                        }
                                    }
                                } else if (this.condition) {
                                    if (i + x < 0) {
                                        if (j + y < 0) {
                                            if (this.cells[this.xSize + x + i][this.ySize + y + j].getState() == 1) {
                                                dist = distance(i, j, i + x, j + y);
                                                if (dist < minDist) {
                                                    minDist = dist;
                                                    x0 = this.xSize + x + i;
                                                    y0 = this.ySize + y + j;
                                                    x1 = i + x;
                                                    y1 = j + y;
                                                    k = true;
                                                }
                                            }
                                        } else if (j + y >= this.ySize) {
                                            if (this.cells[this.xSize + x + i][y - 1].getState() == 1) {
                                                dist = distance(i, j, i + x, j + y);
                                                if (dist < minDist) {
                                                    minDist = dist;
                                                    x0 = this.xSize + x + i;
                                                    y0 = y - 1;
                                                    x1 = i + x;
                                                    y1 = j + y;
                                                    k = true;
                                                }
                                            }
                                        } else if (this.cells[this.xSize + x + i][j + y].getState() == 1) {
                                            dist = distance(i, j, i + x, j + y);
                                            if (dist < minDist) {
                                                minDist = dist;
                                                x0 = this.xSize + x + i;
                                                y0 = j + y;
                                                x1 = i + x;
                                                y1 = j + y;
                                                k = true;
                                            }
                                        }
                                    } else if (i + x >= this.xSize) {
                                        if (j + y < 0) {
                                            if (this.cells[x - 1][this.ySize + y + j].getState() == 1) {
                                                dist = distance(i, j, i + x, j + y);
                                                if (dist < minDist) {
                                                    minDist = dist;
                                                    x0 = x - 1;
                                                    y0 = this.ySize + y + j;
                                                    x1 = i + x;
                                                    y1 = j + y;
                                                    k = true;
                                                }
                                            }
                                        } else if (j + y >= this.ySize) {
                                            if (this.cells[x - 1][y - 1].getState() == 1) {
                                                dist = distance(i, j, i + x, j + y);
                                                if (dist < minDist) {
                                                    minDist = dist;
                                                    x0 = x - 1;
                                                    y0 = y - 1;
                                                    x1 = i + x;
                                                    y1 = j + y;
                                                    k = true;
                                                }
                                            }
                                        } else if (this.cells[x - 1][j + y].getState() == 1) {
                                            dist = distance(i, j, i + x, j + y);
                                            if (dist < minDist) {
                                                minDist = dist;
                                                x0 = x - 1;
                                                y0 = j + y;
                                                x1 = i + x;
                                                y1 = j + y;
                                                k = true;
                                            }
                                        }
                                    } else if (j + y < 0) {
                                        if (this.cells[i + x][this.ySize + y + j].getState() == 1) {
                                            dist = distance(i, j, i + x, j + y);
                                            if (dist < minDist) {
                                                minDist = dist;
                                                x0 = i + x;
                                                y0 = this.ySize + y + j;
                                                x1 = i + x;
                                                y1 = j + y;
                                                k = true;
                                            }
                                        }
                                    } else if (j + y >= this.ySize) {
                                        if (this.cells[i + x][y - 1].getState() == 1) {
                                            dist = distance(i, j, i + x, j + y);
                                            if (dist < minDist) {
                                                minDist = dist;
                                                x0 = i + x;
                                                y0 = y - 1;
                                                x1 = i + x;
                                                y1 = j + y;
                                                k = true;
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        if (k) {
                            if (distance(i, j, x1, y1) <= this.radius) {
                                cells_copy[i][j].setCell(this.cells[x0][y0]);
                            }
                        }
                    }
                }
            }
        } else {
            int i, j, id;
            int energyAfter;
            double p;
            energy_visualization();
            Cell c, randC;
            while (this.list.size() != 0) {
                id = random.nextInt(this.list.size());
                c = this.list.remove(id);
                i = c.getX();
                j = c.getY();
                if (this.neighbourhood.equals(this.neighbourhood_tab[4])) {
                    rand = random.nextInt(2);
                    setNeighbourHex(rand);
                } else if (this.neighbourhood.equals(this.neighbourhood_tab[1])) {
                    rand = random.nextInt(4);
                    setNeighbourPent(rand);
                }
                if (this.n == 0)
                    countN();
                start = true;
                this.neighbours.clear();
                if (this.neighbour[0]) {
                    if (i - 1 >= 0 && j - 1 >= 0) {
                        this.neighbours.addCell(this.cells[i - 1][j - 1]);
                    } else if (this.condition) {
                        this.neighbours.addCell(this.cells[this.xSize - 1][this.ySize - 1]);
                    }
                }
                if (this.neighbour[1]) {
                    if (j - 1 >= 0) {
                        this.neighbours.addCell(this.cells[i][j - 1]);
                    } else if (this.condition) {
                        this.neighbours.addCell(this.cells[i][this.ySize - 1]);
                    }
                }
                if (this.neighbour[2]) {
                    if (i + 1 < this.xSize && j - 1 >= 0) {
                        this.neighbours.addCell(this.cells[i + 1][j - 1]);
                    } else if (this.condition) {
                        this.neighbours.addCell(this.cells[0][this.ySize - 1]);
                    }
                }
                if (this.neighbour[3]) {
                    if (i + 1 < this.xSize) {
                        this.neighbours.addCell(this.cells[i + 1][j]);
                    } else if (this.condition) {
                        this.neighbours.addCell(this.cells[0][j]);
                    }
                }
                if (this.neighbour[4]) {
                    if (i + 1 < this.xSize && j + 1 < this.ySize) {
                        this.neighbours.addCell(this.cells[i + 1][j + 1]);
                    } else if (this.condition) {
                        this.neighbours.addCell(this.cells[0][0]);
                    }
                }
                if (this.neighbour[5]) {
                    if (j + 1 < this.ySize) {
                        this.neighbours.addCell(this.cells[i][j + 1]);
                    } else if (this.condition) {
                        this.neighbours.addCell(this.cells[i][0]);
                    }
                }
                if (this.neighbour[6]) {
                    if (i - 1 >= 0 && j + 1 < this.ySize) {
                        this.neighbours.addCell(this.cells[i - 1][j + 1]);
                    } else if (this.condition) {
                        this.neighbours.addCell(this.cells[this.xSize - 1][0]);
                    }
                }
                if (this.neighbour[7]) {
                    if (i - 1 >= 0) {
                        this.neighbours.addCell(this.cells[i - 1][j]);
                    } else if (this.condition) {
                        this.neighbours.addCell(this.cells[this.xSize - 1][j]);
                    }
                }
                c.setEnergy(this.n - this.neighbours.amount(c.getId()));
                randC = this.neighbours.randomCell();
                energyAfter = this.n - this.neighbours.amount(randC.getId());
                energyAfter -= c.getEnergy();
                if (energyAfter <= 0)
                    p = 1;
                else {
                    p = Math.round(Math.exp(-energyAfter / kT));
                }

                if (p == 1) {
                    System.out.println("true");
                    c.setCell(randC);
                }
            }
        }
        return start;
    }
}
