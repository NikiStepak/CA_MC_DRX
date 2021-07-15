package javafx;

import com.sun.prism.Image;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import data.DataManager;
import data.BoundaryCondition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private ComboBox<String> nucleationComboBox, neighbourhoodComboBox, boundaryConditionComboBox;
    @FXML
    private TextField xSizeField, ySizeField, fileNameField, radiusField, rowField, columnField, amountField;
    @FXML
    public Button startButton, stopButton, elementsButton, saveButton, iterationButton;
    @FXML
    private Slider xSlider, ySlider;
    @FXML
    private Label errorLabel;
    @FXML
    private Canvas canvasPane;

    private DataManager dm;
    private boolean error = true;
    private GraphicsContext gc;

    private int xSize, ySize, rows, columns, amount, cellSize;
    private double radius = 0.0, radiusNu;
    private String neighbourhood, nucleation, condition, fileName;
    private String errorStyle = "-fx-border-color: RED;", style = "";

    private String nucleation_tab[]={"HOMOGENOUS", "with RADIUS", "RANDOM", "TICKING"};

    private BoundaryCondition boundaryCondition;
    Timeline timeline;

    boolean tick = false, start = true;

    // Override methods which initialize our controls in window ========================================================
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dm = new DataManager();
        neighbourhoodComboBox.getItems().addAll(dm.getNeighbourhood_tab());
        nucleationComboBox.getItems().addAll(nucleation_tab);
        boundaryConditionComboBox.getItems().addAll(dm.getBoundaryCondition_tab());

        errorLabel.setVisible(false);
        rowField.setVisible(false);
        columnField.setVisible(false);
        amountField.setVisible(false);
        radiusField.setVisible(false);

        iterationButton.setDisable(true);
        startButton.setDisable(true);
        stopButton.setDisable(true);
        saveButton.setDisable(true);

        gc = canvasPane.getGraphicsContext2D();

        timeline = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                start = dm.startCode();
                if (!start)
                    stopAction(actionEvent);
                draw();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);

    }

    // Button Action methods ===========================================================================================
    public void elementsAction(ActionEvent actionEvent) {
        gc.clearRect(0,0,canvasPane.getWidth(),canvasPane.getHeight());
        error = false;

        checkSizeSection();
        checkNeighbourhoodSection();
        checkNucleationSection();
        checkBoundaryConditionSection();

        if (error) {
            errorLabel.setVisible(true);
        }
        else {
            errorLabel.setVisible(false);

            iterationButton.setDisable(false);
            startButton.setDisable(false);
            stopButton.setDisable(true);
            saveButton.setDisable(false);

            if (nucleation.equals(nucleation_tab[0]))
                dm = new DataManager(xSize,ySize,rows, columns,neighbourhood,condition,radius);
            else if (nucleation.equals(nucleation_tab[1]))
                dm = new DataManager(xSize,ySize,amount, radiusNu,neighbourhood,condition,radius);
            else if (nucleation.equals(nucleation_tab[2]))
                dm = new DataManager(xSize,ySize,amount,neighbourhood,condition,radius);
            else if (nucleation.equals(nucleation_tab[3]))
                dm = new DataManager(xSize,ySize,neighbourhood,condition,radius);

            int xCellSize, yCellSize;
            xCellSize = 730 / xSize;
            yCellSize = 745 / ySize;
            cellSize = Math.min(xCellSize, yCellSize);

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

    public void startAction(ActionEvent actionEvent) {
        elementsButton.setDisable(true);
        iterationButton.setDisable(true);
        startButton.setDisable(true);
        stopButton.setDisable(false);
        saveButton.setDisable(true);
        timeline.play();

    }

    public void stopAction(ActionEvent actionEvent) {
        stopButton.setDisable(true);
        if(start) {
            startButton.setDisable(false);
            iterationButton.setDisable(false);
        }
        elementsButton.setDisable(false);
        saveButton.setDisable(false);
        timeline.pause();
    }

    public void iterationAction(ActionEvent actionEvent) {
        dm.startCode();
        draw();
    }

    public void saveAction(ActionEvent actionEvent) {
        fileName = fileNameField.getText();
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

    // Canvas methods ==================================================================================================
    public void canvasPressed(MouseEvent mouseEvent) {
        if(tick){
            int x, y;
            x = (int) (mouseEvent.getX() / cellSize);
            y = (int) (mouseEvent.getY() / cellSize);
            System.out.println(x+", "+y);
            dm.setCellTick(x,y);
            gc.setFill(dm.cells[x][y].getColorRGB());
            gc.fillRect(x*cellSize,y*cellSize,cellSize,cellSize);
        }
    }

    private void draw() {
        Color color;
        for (int i = 0; i < dm.xSize; i++){
            for (int j = 0; j < dm.ySize; j++){
                if(dm.cells[i][j].getState()==1){
                    color = dm.cells[i][j].getColorRGB();
                    gc.setFill(color);
                    gc.fillRect(i*cellSize,j*cellSize,cellSize,cellSize);
                }
            }
        }
    }

    // ComboBox Action methods =========================================================================================
    public  void nucleationAction(ActionEvent actionEvent){
        nucleationComboBox.setStyle(style);
        nucleation = nucleationComboBox.getValue();
        if(nucleation.equals(nucleation_tab[0])){
            rowField.setVisible(true);
            rowField.promptTextProperty().set("ROWS");
            columnField.setVisible(true);
            columnField.promptTextProperty().set("COLUMNS");
            amountField.setVisible(false);
            tick = false;
        }
        else if(nucleation.equals(nucleation_tab[1])){
            rowField.setVisible(true);
            rowField.promptTextProperty().set("AMOUNT");
            columnField.setVisible(true);
            columnField.promptTextProperty().set("RADIUS");
            amountField.setVisible(false);
            tick = false;
        }
        else if (nucleation.equals(nucleation_tab[2])){
            rowField.setVisible(false);
            columnField.setVisible(false);
            amountField.setVisible(true);
            tick = false;
        }
        else {
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

    public void xSizeType(KeyEvent keyEvent) {
        xSizeField.setStyle(style);

        try{
            xSize = Integer.parseInt(xSizeField.getText());
            if(xSize > xSlider.getMax()){
                xSize = (int) xSlider.getMax();
                xSizeField.setText(String.valueOf(xSize));
            }
            else if(xSize < xSlider.getMin()){
                xSize = (int) xSlider.getMin();
                xSizeField.setText(String.valueOf(xSize));
            }
            xSlider.setValue(xSize);
        }catch (NumberFormatException exception){
            xSizeField.setStyle(errorStyle);
        }
    }

    public void ySizeTyped(KeyEvent keyEvent) {
        ySizeField.setStyle(style);

        try {
            ySize = Integer.parseInt(ySizeField.getText());
            if(ySize > ySlider.getMax()){
                ySize = (int) ySlider.getMax();
                ySizeField.setText(String.valueOf(ySize));
            }
            else if (ySize < ySlider.getMin()){
                ySize = (int) ySlider.getMin();
                ySizeField.setText(String.valueOf(ySize));
            }
            ySlider.setValue(ySize);
        }catch (NumberFormatException exception){
            ySizeField.setStyle(errorStyle);
        }
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
    private void checkNeighbourhoodSection(){
        neighbourhood = neighbourhoodComboBox.getValue();
        if (neighbourhood == null) {
            error = true;
            neighbourhoodComboBox.setStyle(errorStyle);
        }
        else if (neighbourhood.equals(dm.getNeighbourhood_tab()[6])){
            try {
                radius = Double.parseDouble((radiusField.getText()));
                if (radius < 0){
                    error = true;
                    rowField.setStyle(errorStyle);
                }
            }catch (NumberFormatException exception) {
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
                if (columns > xSize || columns < 0){
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
                if (amount < 0){
                    error = true;
                    rowField.setStyle(errorStyle);
                }
            } catch (NumberFormatException exception) {
                error = true;
                rowField.setStyle(errorStyle);
            }
            try {
                radiusNu = Double.parseDouble((columnField.getText()));
                if (radiusNu < 0){
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
                if (amount < 0 || amount > xSize*ySize){
                    error = true;
                    amountField.setStyle(errorStyle);
                }
            } catch (NumberFormatException exception) {
                error = true;
                amountField.setStyle(errorStyle);
            }
        }
    }

    private void checkBoundaryConditionSection(){
        condition = boundaryConditionComboBox.getValue();
        if (condition ==null) {
            error = true;
            boundaryConditionComboBox.setStyle(errorStyle);
        }
    }

    private void checkSizeSection(){
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

}