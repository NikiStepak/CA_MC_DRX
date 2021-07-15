package data;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Colors {
    private final List<Color> colors;
    private final Random random;

    // Constructor
    public Colors(int listSize) {
        random = new Random();
        this.colors = new ArrayList<>();

        int x = (int) Math.pow(listSize, (double) 1 / 3);
        x++;
        x = 255/x;
        x++;

        for (int r = 0; r <= 255; r += x)
            for (int g = 0; g <= 255; g += x)
                for (int b = 0; b <= 255; b += x)
                    this.colors.add(Color.rgb(r, g, b));
    }

    // method which return random color
    public Color getColor(){
        int id = random.nextInt(this.colors.size());
        Color color = this.colors.get(id);
        this.colors.remove(id);
        return color;
    }
}