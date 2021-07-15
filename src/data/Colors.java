package data;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Colors {
    private final List<Color> colors;
    private final Random random;

    // Constructor
    public Colors() {
        random = new Random();
        this.colors = new ArrayList<>();
        initColors();
    }

    private void initColors(){
        for (int r = 0; r <= 255; r ++)
            for (int g = 0; g <= 255; g ++)
                for (int b = 0; b <= 255; b ++)
                    this.colors.add(Color.rgb(r, g, b));
    }

    // method which return random color
    public Color getColor(){
        if (colors.size()>0) {
            int id = random.nextInt(this.colors.size());
            Color color = this.colors.get(id);
            this.colors.remove(id);
            return color;
        }
        else {
            initColors();
            return getColor();
        }
    }
}