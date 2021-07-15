package javafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import data.Cell;
import data.DataManager;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Tab DRXTab, MCTab;
    @FXML
    private ComboBox<String> nucleationComboBox, neighbourhoodComboBox, boundaryConditionComboBox;
    @FXML
    private TextField xSizeField, ySizeField, fileNameField, radiusField, rowField, columnField, amountField, iterField, kTField, aField, bField, timeField, dtField, sizePackageField;
    @FXML
    public Button startCAButton, stopCAButton, elementsButton, saveButton, iterationCAButton, startMCButton, stopMCButton;
    @FXML
    private Slider xSlider, ySlider;
    @FXML
    private Label errorLabelCA, errorLabelMC, endLabel, timeLabel;
    @FXML
    private Canvas canvasPane;

    private DataManager dm;
    private boolean error = true, tick = false, start = true;
    private GraphicsContext gc;

    private int xSize, ySize, rows, columns, amount, cellSize, iter;
    private double radius = 0.0, radiusNu, kT;
    private String neighbourhood;
    private String nucleation;
    private String condition;
    private final String errorStyle = "-fx-border-color: RED;", style = "";
    private final String[] nucleation_tab = {"HOMOGENOUS", "with RADIUS", "RANDOM", "TICKING"};
    private double t,dt, ti=0;

    Timeline timelineCA, timelineMC, timelineDRX;

    // Override methods which initialize our controls in window ========================================================
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dm = new DataManager();
        neighbourhoodComboBox.getItems().addAll(dm.getNeighbourhood_tab());
        nucleationComboBox.getItems().addAll(nucleation_tab);
        boundaryConditionComboBox.getItems().addAll(dm.getBoundaryCondition_tab());

        errorLabelCA.setVisible(false);
        rowField.setVisible(false);
        columnField.setVisible(false);
        amountField.setVisible(false);
        radiusField.setVisible(false);

        iterationCAButton.setDisable(true);
        startCAButton.setDisable(true);
        stopCAButton.setDisable(true);
        saveButton.setDisable(true);

        DRXTab.setDisable(true);
        endLabel.setVisible(false);
        MCTab.setDisable(true);
        errorLabelMC.setVisible(false);
        stopMCButton.setDisable(true);

        gc = canvasPane.getGraphicsContext2D();

        timelineCA = new Timeline(new KeyFrame(Duration.millis(50), actionEvent -> {
            start = dm.CA();
            if (!start)
                stopCAAction(actionEvent);
            draw();
        }));
        timelineCA.setCycleCount(Timeline.INDEFINITE);
        timelineCA.setAutoReverse(false);

        timelineMC = new Timeline(new KeyFrame(Duration.millis(100), actionEvent -> {
            dm.MC(kT);
            iter--;
            if (iter == 0)
                stopMCAction(actionEvent);
            draw();
        }));
        timelineMC.setCycleCount(Timeline.INDEFINITE);
        timelineMC.setAutoReverse(false);

        timelineDRX = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
            if(ti<t) {
                dm.DRX((int) (ti / dt));
                draw();
                ti += dt;
                timeLabel.setText(String.valueOf(ti));
            }
            else {
                endLabel.setVisible(true);
                ti = 0;
                timelineDRX.stop();
            }
        }));
        timelineDRX.setCycleCount(Timeline.INDEFINITE);
        timelineDRX.setAutoReverse(false);

    }

    // Button Action methods ===========================================================================================
    public void elementsAction(ActionEvent actionEvent) {
        gc.clearRect(0, 0, canvasPane.getWidth(), canvasPane.getHeight());
        error = false;

        checkSizeSection();
        checkNeighbourhoodSection();
        checkNucleationSection();
        checkBoundaryConditionSection();

        if (error) {
            errorLabelCA.setVisible(true);
        } else {
            errorLabelCA.setVisible(false);

            iterationCAButton.setDisable(false);
            startCAButton.setDisable(false);
            stopCAButton.setDisable(true);
            saveButton.setDisable(false);
            DRXTab.setDisable(true);
            MCTab.setDisable(true);

            if (nucleation.equals(nucleation_tab[0]))
                dm.DM1(xSize, ySize, rows, columns, neighbourhood, condition, radius);
            else if (nucleation.equals(nucleation_tab[1]))
                dm.DM2(xSize, ySize, amount, radiusNu, neighbourhood, condition, radius);
            else if (nucleation.equals(nucleation_tab[2]))
                dm.DM3(xSize, ySize, amount, neighbourhood, condition, radius);
            else if (nucleation.equals(nucleation_tab[3])) {
                dm.DM4(xSize, ySize, neighbourhood, condition, radius);
                tick = true;
            }

            cellSize = Math.min(730 / xSize, 745 / ySize);

            if (cellSize == 0) {
                cellSize = 1;
            }

            canvasPane.setWidth(xSize * cellSize);
            canvasPane.setHeight(ySize * cellSize);
            gc.setStroke(Color.RED);
            gc.strokeRect(0, 0, xSize * cellSize, ySize * cellSize);

            draw();
        }
    }

    public void startCAAction(ActionEvent actionEvent) {
        tick = false;
        this.ti = 0;
        elementsButton.setDisable(true);
        iterationCAButton.setDisable(true);
        startCAButton.setDisable(true);
        stopCAButton.setDisable(false);
        saveButton.setDisable(true);
        timelineCA.play();

    }

    public void stopCAAction(ActionEvent actionEvent) {
        stopCAButton.setDisable(true);
        if (start) {
            startCAButton.setDisable(false);
            iterationCAButton.setDisable(false);
        } else MCTab.setDisable(false);
        elementsButton.setDisable(false);
        saveButton.setDisable(false);
        timelineCA.pause();
    }

    public void iterationCAAction(ActionEvent actionEvent) {
        tick = false;
        start = dm.CA();
        if (!start) {
            iterationCAButton.setDisable(true);
            startCAButton.setDisable(true);
            MCTab.setDisable(false);
        }
        draw();
    }

    public void saveAction(ActionEvent actionEvent) {
        String fileName = fileNameField.getText();
        if (fileName.equals(""))
            fileNameField.setStyle(errorStyle);
        else {
            File file = new File(fileName + ".bmp");

            try {
                ImageIO.write(dm.convertToImage((int) canvasPane.getWidth(), (int) canvasPane.getHeight(), cellSize), "bmp", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void iterationMCAction(ActionEvent actionEvent) {
        DRXTab.setDisable(false);
        checkMCProperties(true);
        this.ti = 0;
        if (!error) {
            dm.MC(0.1);
            draw();
        }
    }

    boolean isStart = false;
    public void drxAction(ActionEvent actionEvent){
        if(this.ti==0) {
            this.t = Double.parseDouble(timeField.getText());
            this.dt = Double.parseDouble(dtField.getText());
            dm.DRXProperties(Double.parseDouble(aField.getText()), Double.parseDouble(bField.getText()), t, dt, Double.parseDouble(sizePackageField.getText()));
            endLabel.setVisible(false);
        }
        isStart = !isStart;
        if(isStart){
            timelineDRX.play();
        }
        else
            timelineDRX.stop();
    }

    public void energyAction(ActionEvent actionEvent) {
        List<Cell> list = dm.energy_visualization();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, xSize * cellSize, ySize * cellSize);

        for (Cell c : list) {
            switch (c.getEnergy()) {
                case 1 -> gc.setFill(Color.NAVY);
                case 2 -> gc.setFill(Color.MEDIUMBLUE);
                case 3 -> gc.setFill(Color.ROYALBLUE);
                case 4 -> gc.setFill(Color.DODGERBLUE);
                case 5 -> gc.setFill(Color.DEEPSKYBLUE);
                case 6 -> gc.setFill(Color.AQUA);
                case 7 -> gc.setFill(Color.SKYBLUE);
                case 8 -> gc.setFill(Color.LIGHTCYAN);
            }
            gc.fillRect(c.getX() * cellSize, c.getY() * cellSize, cellSize, cellSize);
        }
    }

    public void densityAction(ActionEvent actionEvent) {
        List<Cell> list = dm.density_visualization();
        gc.setFill(Color.rgb(152,102,0));
        gc.fillRect(0, 0, xSize * cellSize, ySize * cellSize);
        double d = (dm.maxDensity-dm.minDensity)/ 11;

        for (Cell c : list) {
            switch ((int) (c.density / d)) {
                case 0 -> gc.setFill(Color.PALEGREEN);
                case 1 -> gc.setFill(Color.LIGHTGREEN);
                case 2 -> gc.setFill(Color.YELLOWGREEN);
                case 3 -> gc.setFill(Color.GREENYELLOW);
                case 4 -> gc.setFill(Color.CHARTREUSE);
                case 5 -> gc.setFill(Color.LAWNGREEN);
                case 6 -> gc.setFill(Color.SPRINGGREEN);
                case 7 -> gc.setFill(Color.LIME);
                case 8 -> gc.setFill(Color.LIMEGREEN);
                case 9 -> gc.setFill(Color.FORESTGREEN);
                case 10 -> gc.setFill(Color.GREEN);
                case 11 -> gc.setFill(Color.DARKGREEN);
            }
            gc.fillRect(c.getX() * cellSize, c.getY() * cellSize, cellSize, cellSize);
        }
    }

    public void startMCAction(ActionEvent actionEvent) {
        DRXTab.setDisable(false);
        checkMCProperties(false);
        this.ti = 0;
        if (!error) {
            startMCButton.setDisable(true);
            stopMCButton.setDisable(false);
            timelineMC.play();
        }
    }

    public void stopMCAction(ActionEvent actionEvent) {
        stopMCButton.setDisable(true);
        startMCButton.setDisable(false);
        timelineMC.stop();
    }

    // Canvas methods ==================================================================================================
    public void canvasPressed(MouseEvent mouseEvent) {
        if (tick) {
            int x, y;
            x = (int) (mouseEvent.getX() / cellSize);
            y = (int) (mouseEvent.getY() / cellSize);
            dm.setCellTick(x, y);
            gc.setFill(dm.cells[x][y].getColorRGB());
            gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
        }
        else {
            int x, y;
            x = (int) (mouseEvent.getX() / cellSize);
            y = (int) (mouseEvent.getY() / cellSize);
            System.out.println(dm.cells[x][y].toString());
        }
    }

    private void draw() {
        Color color;
        for (int i = 0; i < dm.xSize; i++) {
            for (int j = 0; j < dm.ySize; j++) {
                if (dm.cells[i][j].getState() == 1) {
                    color = dm.cells[i][j].getColorRGB();
                    gc.setFill(color);
                    gc.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    // ComboBox Action methods =========================================================================================
    public void nucleationAction(ActionEvent actionEvent) {
        nucleationComboBox.setStyle(style);
        nucleation = nucleationComboBox.getValue();
        if (nucleation.equals(nucleation_tab[0])) {
            rowField.setVisible(true);
            rowField.promptTextProperty().set("ROWS");
            columnField.setVisible(true);
            columnField.promptTextProperty().set("COLUMNS");
            amountField.setVisible(false);
            tick = false;
        } else if (nucleation.equals(nucleation_tab[1])) {
            rowField.setVisible(true);
            rowField.promptTextProperty().set("AMOUNT");
            columnField.setVisible(true);
            columnField.promptTextProperty().set("RADIUS");
            amountField.setVisible(false);
            tick = false;
        } else if (nucleation.equals(nucleation_tab[2])) {
            rowField.setVisible(false);
            columnField.setVisible(false);
            amountField.setVisible(true);
            tick = false;
        } else {
            rowField.setVisible(false);
            columnField.setVisible(false);
            amountField.setVisible(false);
            tick = true;
        }
    }

    public void boundaryConditionAction(ActionEvent actionEvent) {
        boundaryConditionComboBox.setStyle(style);
    }

    public void neighbourhoodAction(ActionEvent actionEvent) {
        neighbourhoodComboBox.setStyle(style);

        neighbourhood = neighbourhoodComboBox.getValue();
        radiusField.setVisible(neighbourhood.equals(dm.getNeighbourhood_tab()[6]));
    }

    // Field Action methods ============================================================================================
    public void rowTyped(KeyEvent keyEvent) {
        rowField.setStyle(style);
    }

    public void columnTyped(KeyEvent keyEvent) {
        columnField.setStyle(style);
    }

    public void radiusTyped(KeyEvent keyEvent) {
        radiusField.setStyle(style);
    }

    public void fileNameTyped(KeyEvent keyEvent) {
        fileNameField.setStyle(style);
    }

    public void amountTyped(KeyEvent keyEvent) {
        amountField.setStyle(style);
    }

    public void xSizeTyped(KeyEvent keyEvent) {
        xSizeField.setStyle(style);

        try {
            xSize = Integer.parseInt(xSizeField.getText());
            if (xSize > xSlider.getMax()) {
                xSize = (int) xSlider.getMax();
                xSizeField.setText(String.valueOf(xSize));
            } else if (xSize < xSlider.getMin()) {
                xSize = (int) xSlider.getMin();
                xSizeField.setText(String.valueOf(xSize));
            }
            xSlider.setValue(xSize);
        } catch (NumberFormatException exception) {
            xSizeField.setStyle(errorStyle);
        }
    }

    public void ySizeTyped(KeyEvent keyEvent) {
        ySizeField.setStyle(style);

        try {
            ySize = Integer.parseInt(ySizeField.getText());
            if (ySize > ySlider.getMax()) {
                ySize = (int) ySlider.getMax();
                ySizeField.setText(String.valueOf(ySize));
            } else if (ySize < ySlider.getMin()) {
                ySize = (int) ySlider.getMin();
                ySizeField.setText(String.valueOf(ySize));
            }
            ySlider.setValue(ySize);
        } catch (NumberFormatException exception) {
            ySizeField.setStyle(errorStyle);
        }
    }

    public void iterTyped(KeyEvent keyEvent) {
        iterField.setStyle(style);
    }

    public void kTTyped(KeyEvent keyEvent) {
        kTField.setStyle(style);
    }

    // Slider Action methods ===========================================================================================
    public void xSizeDragged(MouseEvent mouseEvent) {
        xSize = (int) xSlider.getValue();
        xSizeField.setText(String.valueOf(xSize));
        xSizeField.setStyle(style);
    }

    public void ySizeDragged(MouseEvent mouseEvent) {
        ySize = (int) ySlider.getValue();
        ySizeField.setText(String.valueOf(ySize));
        ySizeField.setStyle(style);
    }

    // methods which check Section =====================================================================================
    private void checkNeighbourhoodSection() {
        neighbourhood = neighbourhoodComboBox.getValue();
        if (neighbourhood == null) {
            error = true;
            neighbourhoodComboBox.setStyle(errorStyle);
        } else if (neighbourhood.equals(dm.getNeighbourhood_tab()[6])) {
            try {
                radius = Double.parseDouble((radiusField.getText()));
                if (radius < 0) {
                    error = true;
                    rowField.setStyle(errorStyle);
                }
            } catch (NumberFormatException exception) {
                error = true;
                radiusField.setStyle(errorStyle);
            }
        }
    }

    private void checkNucleationSection() {
        nucleation = nucleationComboBox.getValue();
        if (nucleation == null) {
            error = true;
            nucleationComboBox.setStyle(errorStyle);
        } else if (nucleation.equals(nucleation_tab[0])) {
            try {
                rows = Integer.parseInt(rowField.getText());
                if (rows > ySize || rows < 0) {
                    error = true;
                    rowField.setStyle(errorStyle);
                }
            } catch (NumberFormatException exception) {
                error = true;
                rowField.setStyle(errorStyle);
            }
            try {
                columns = Integer.parseInt(columnField.getText());
                if (columns > xSize || columns < 0) {
                    error = true;
                    columnField.setStyle(errorStyle);
                }
            } catch (NumberFormatException exception) {
                error = true;
                columnField.setStyle(errorStyle);
            }
        } else if (nucleation.equals(nucleation_tab[1])) {
            try {
                amount = Integer.parseInt(rowField.getText());
                if (amount < 0) {
                    error = true;
                    rowField.setStyle(errorStyle);
                }
            } catch (NumberFormatException exception) {
                error = true;
                rowField.setStyle(errorStyle);
            }
            try {
                radiusNu = Double.parseDouble((columnField.getText()));
                if (radiusNu < 0) {
                    error = true;
                    columnField.setStyle(errorStyle);
                }
            } catch (NumberFormatException exception) {
                error = true;
                columnField.setStyle(errorStyle);
            }
            if (!error)
                if (amount * Math.PI * Math.pow(radiusNu, 2) > xSize * ySize) {
                    error = true;
                    rowField.setStyle(errorStyle);
                    columnField.setStyle(errorStyle);
                }
        } else if (nucleation.equals(nucleation_tab[2])) {
            try {
                amount = Integer.parseInt(amountField.getText());
                if (amount < 0 || amount > xSize * ySize) {
                    error = true;
                    amountField.setStyle(errorStyle);
                }
            } catch (NumberFormatException exception) {
                error = true;
                amountField.setStyle(errorStyle);
            }
        }
    }

    private void checkBoundaryConditionSection() {
        condition = boundaryConditionComboBox.getValue();
        if (condition == null) {
            error = true;
            boundaryConditionComboBox.setStyle(errorStyle);
        }
    }

    private void checkSizeSection() {
        try {
            xSize = Integer.parseInt(xSizeField.getText());
        } catch (NumberFormatException exception) {
            error = true;
            xSizeField.setStyle(errorStyle);
        }
        try {
            ySize = Integer.parseInt(ySizeField.getText());
        } catch (NumberFormatException exception) {
            error = true;
            ySizeField.setStyle(errorStyle);
        }
    }

    private void checkMCProperties(boolean it) {
        error = false;
        if (!it) {
            try {
                iter = Integer.parseInt(iterField.getText());
                if (iter < 0) {
                    error = true;
                    iterField.setStyle(errorStyle);
                }
            } catch (NumberFormatException exception) {
                error = true;
                iterField.setStyle(errorStyle);
            }
        }

        try {
            kT = Double.parseDouble(kTField.getText());
            if (kT < 0.1 || kT > 6) {
                error = true;
                kTField.setStyle(errorStyle);
            }
        } catch (NumberFormatException exception) {
            error = true;
            kTField.setStyle(errorStyle);
        }


        errorLabelMC.setVisible(error);
    }
}
