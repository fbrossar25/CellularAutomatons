package application.gui;

import javafx.animation.AnimationTimer;

public class CanvasUpdateTimer extends AnimationTimer {
    CellularAutomatonCanvas canvas;

    public CanvasUpdateTimer(CellularAutomatonCanvas canvas) {
        super();
        if (canvas == null)
            throw new IllegalArgumentException("canvas cannot be null");
        this.canvas = canvas;
    }

    @Override
    public void handle(long now) {
        canvas.draw();
    }

}
