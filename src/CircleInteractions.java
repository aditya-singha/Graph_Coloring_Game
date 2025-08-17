import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CircleInteractions {

    private String mode = "COLORINGMODE";
    private Stack<Circle> colorStack = new Stack<>();
    private Circle lastColoredCircle;
    private ColorPicker colorPicker;
    private Map<Circle, List<Circle>> adjacencyMap = new HashMap<>();
    private Color selectedColor = Color.WHITE;

    public CircleInteractions(ColorPicker colorPicker) {
        this.colorPicker = colorPicker;
        colorPicker.setOnAction(event -> {
            selectedColor = colorPicker.getValue();
        });
    }

    public void switchMode(String newMode) {
        this.mode = newMode;
    }

    public void colorCircle(Circle circle) {
        if (mode.equals("COLORINGMODE") && !colorStack.contains(circle) && circle.getFill().equals(Color.TRANSPARENT)) {

            List<Circle> adjacentCircles = adjacencyMap.getOrDefault(circle, new ArrayList<>());

            boolean flag = false;
            for (Circle adjacent : adjacentCircles) {

                if (adjacent.getFill().equals(selectedColor)) {
                    ReadGraph readGraph = new ReadGraph();
                    readGraph.giveWarning("Cannot color adjacent nodes with the same color.");
                    flag = true;
                    break;
                }

            }

            if (flag == false) {
                circle.setFill(selectedColor);
                colorStack.push(circle);
                lastColoredCircle = circle;
            }

        }
    }

    public void undoColor() {
        if (mode.equals("COLORINGMODE") && !colorStack.isEmpty()) {
            Circle circle = colorStack.pop();
            circle.setFill(javafx.scene.paint.Color.TRANSPARENT);
            lastColoredCircle = colorStack.isEmpty() ? null : colorStack.peek();
        }
    }

    public void makeDraggable(Circle circle, double areaX, double areaY, double areaWidth, double areaHeight) {
        circle.setOnMousePressed(event -> {
            if ("NODEMOVEMENT".equals(mode)) {
                circle.setUserData(new double[] { event.getSceneX(), event.getSceneY() });
            }
        });

        circle.setOnMouseDragged(event -> {
            if ("NODEMOVEMENT".equals(mode)) {
                double[] delta = (double[]) circle.getUserData();
                double deltaX = event.getSceneX() - delta[0];
                double deltaY = event.getSceneY() - delta[1];

                double newX = circle.getCenterX() + deltaX;
                double newY = circle.getCenterY() + deltaY;

                newX = Math.max(areaX + circle.getRadius(), Math.min(areaX + areaWidth - circle.getRadius(), newX));
                newY = Math.max(areaY + circle.getRadius(), Math.min(areaY + areaHeight - circle.getRadius(), newY));

                circle.setCenterX(newX);
                circle.setCenterY(newY);

                circle.setUserData(new double[] { event.getSceneX(), event.getSceneY() });
            }
        });
    }

    public void addAdjacent(Circle circle, Circle adjacentCircle) {
        adjacencyMap.computeIfAbsent(circle, k -> new ArrayList<>()).add(adjacentCircle);
    }
}