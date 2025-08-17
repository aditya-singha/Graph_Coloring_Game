import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.beans.binding.Bindings;

public class modeOne {
    private Pane innerPane;
    private CircleInteractions circleInteractions;
    private Text time = new Text("00:00:00.000");
    private AnimationTimer timer;
    private boolean timerRunning = false;
    private String score;
    private int chromaticNumber;
    private boolean isPaused = false;
    private long startTime;
    private long pausedTime = 0;
    private Button autoButton;
    private ArrayList<ArrayList<Integer>> graphColoring;
    private Color[] colors = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PURPLE, Color.PINK,
            Color.BROWN, Color.GRAY, Color.CYAN };

    public void start(Stage stage) {
        stage.setTitle("To the bitter end");
        Pane pane = new Pane();

        pane.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));// background color

        Button backButton = new Button("Home");// go back to home button
        backButton.setLayoutX(10);
        backButton.setLayoutY(10);

        backButton.setOnAction(e -> {
            Main launcher = new Main();
            launcher.start(stage);
        });

        pane.getChildren().addAll(backButton);

        innerPane = createBorderedArea(pane);

        hintButton(pane);// hint button diplay

        ColorPicker colorPicker = createColorPalette(pane);// create color palette

        Label guide1 = new Label("COLOR CIRCLE");
        guide1.setTextFill(Color.WHITE);
        guide1.setLayoutX(280);
        guide1.setLayoutY(15);

        Label guide2 = new Label("MOVE CIRCLE");
        guide2.setTextFill(Color.WHITE);
        guide2.setLayoutX(460);
        guide2.setLayoutY(15);

        Label guide3 = new Label("VERTICES");
        guide3.setTextFill(Color.WHITE);
        guide3.setLayoutX(770);
        guide3.setLayoutY(15);

        Label guide4 = new Label("EDGES");
        guide4.setTextFill(Color.WHITE);
        guide4.setLayoutX(965);
        guide4.setLayoutY(15);

        pane.getChildren().addAll(guide1, guide2, guide3, guide4);

        newGameViaBox(pane, colorPicker, innerPane); // to enable user to start new game by entering values in widget

        newGameViaTxt(pane, colorPicker, stage, innerPane); // to enable user to start new game by passing text file

        circleInteractions = new CircleInteractions(colorPicker); // init new object to handle circle interactions for
                                                                  // modeOne object

        autoButton = new Button("AUTO");
        autoButton.setLayoutX(220);
        autoButton.setLayoutY(10);
        Tooltip tooltip = new Tooltip("Give up and autocomplete.");
        Tooltip.install(autoButton, tooltip);

        pane.getChildren().addAll(autoButton);

        autoButton.setOnAction(e -> {
            giveUp(pane);
        });

        finishGame(pane); // button to finish game

        Button undoButton = createUndoButton(pane); // undo button

        SwitchButton switchColor = createSwitchButtonColor(pane); // toggle coloring mode

        SwitchButton switchMove = createSwitchButtonMove(pane); // toggle circle movement node

        switchColor.switchOnProperty().set(false);

        switchColor.switchOnProperty().set(true); // set as true from beginning

        // make coloring and circle movement mutually exclusive

        switchColor.switchOnProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                switchMove.switchOnProperty().set(false);
                circleInteractions.switchMode("COLORINGMODE");
            } else {
                switchMove.switchOnProperty().set(true);
                circleInteractions.switchMode("NODEMOVEMENT");
            }
        });

        switchMove.switchOnProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                switchColor.switchOnProperty().set(false);
                circleInteractions.switchMode("NODEMOVEMENT");
            } else {
                switchColor.switchOnProperty().set(true);
                circleInteractions.switchMode("COLORINGMODE");
            }
        });

        time.setFont(Font.font("Arial", 18)); // display stopwatch
        time.setLayoutX(1350);
        time.setLayoutY(30);
        pane.getChildren().add(time);

        Screen screen = Screen.getPrimary();
        stage.setWidth(screen.getVisualBounds().getWidth());
        stage.setHeight(screen.getVisualBounds().getHeight());
        stage.setScene(new Scene(pane));
    }

    /**
     * hint button
     * 
     * @param pane
     */
    public void hintButton(Pane pane) {
        Button button = new Button("i");
        button.setLayoutX(70);
        button.setLayoutY(10);
        pane.getChildren().addAll(button);

        button.setOnAction(e -> {
            String colorSuggestionCreation = "";
            int firstSuggestionColoringSize = graphColoring.get(0).size();
            for (int i = 0; i < firstSuggestionColoringSize; i++) {
                if (i == firstSuggestionColoringSize - 1) {
                    colorSuggestionCreation += graphColoring.get(0).get(i);
                } else {
                    colorSuggestionCreation = colorSuggestionCreation + graphColoring.get(0).get(i) + ", ";
                }
            }
            String colorSuggestionProposal = colorSuggestionCreation;
            dialogBox("Hint", "You can color vertices " + colorSuggestionProposal + " with the same color");
        });
    }

    /**
     * creating static bordered canvas via rectangle widget
     * 
     * @param pane
     * @return new innerPane which will hold circle objects
     */
    private Pane createBorderedArea(Pane pane) {
        Screen screen = Screen.getPrimary();
        Rectangle borderArea = new Rectangle(10, 50, 1450, 750);
        borderArea.setFill(Color.BLACK);
        borderArea.setStroke(Color.WHITE);
        borderArea.setStrokeWidth(2);

        innerPane = new Pane(); // Inner pane to hold circles and lines
        innerPane.setLayoutX(10);
        innerPane.setLayoutY(50);
        innerPane.setPrefSize(1450, 750);

        pane.getChildren().addAll(borderArea, innerPane);
        return innerPane;
    }

    /**
     * create colorPicker widget
     * 
     * @param pane
     * @return new colorPicker widget
     */
    private ColorPicker createColorPalette(Pane pane) {
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setPrefWidth(110);
        colorPicker.setLayoutX(100);
        colorPicker.setLayoutY(10);
        pane.getChildren().add(colorPicker);
        return colorPicker;
    }

    /**
     * sets up UI components for new game including vertex and edge spinners, and
     * generate button.
     * validates edge count and generates canvas via createCanvasViaBox if valid.
     * 
     * @param pane
     * @param colorPicker
     */
    private void newGameViaBox(Pane pane, ColorPicker colorPicker, Pane innerPane) {
        ReadGraph readGraph = new ReadGraph();
        Spinner<Integer> numberOfVertices = new Spinner<>(2, 2147483647, 10);
        numberOfVertices.setMaxWidth(100);
        numberOfVertices.setEditable(true);
        numberOfVertices.setLayoutX(845);
        numberOfVertices.setLayoutY(10);
        pane.getChildren().add(numberOfVertices);

        Spinner<Integer> numberOfEdges = new Spinner<>(1, 2147483647, 10);
        numberOfEdges.setMaxWidth(100);
        numberOfEdges.setEditable(true);
        numberOfEdges.setLayoutX(1030);
        numberOfEdges.setLayoutY(10);
        pane.getChildren().add(numberOfEdges);

        Button button = new Button("GO");
        button.setLayoutX(1160);
        button.setLayoutY(10);
        pane.getChildren().add(button);

        button.setOnAction(e -> {
            if (numberOfEdges.getValue() >= (numberOfVertices.getValue() - 1) && numberOfEdges
                    .getValue() <= numberOfVertices.getValue() * (numberOfVertices.getValue() - 1) / 2) {
                if (numberOfVertices.getValue() > 50) {
                    innerPane.getChildren().clear();
                    resetTimer();
                    setLimiter();
                } else {
                    innerPane.getChildren().clear();
                    createCanvasViaBox(innerPane, numberOfVertices.getValue(), colorPicker, numberOfEdges.getValue());
                }
            } else {
                readGraph.giveWarning(
                        "Minimum number of vertices is 2. Given n vertices, minimum edges: n - 1, maximum edges: n(n-1)/2.");
            }

        });
    }

    /**
     * generates and positions non-overlapping circles within innerPane.
     * ensures non collision within circle objects and makes them draggable by
     * makeDraggable function
     * binds each circle to a click event for coloring via colorCircle function
     * draws a specified number of edges connecting the circles randomly.
     * 
     * @param innerPane
     * @param numCircles
     * @param colorPicker
     * @param numEdges
     */
    private void createCanvasViaBox(Pane innerPane, int numCircles, ColorPicker colorPicker, int numEdges) {
        double radius = 15;
        ArrayList<Circle> circles = new ArrayList<>();

        for (int i = 0; i < numCircles; i++) {
            Circle circle = new Circle(radius);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(1);

            Text index = new Text(String.valueOf(i + 1));
            index.setFill(Color.WHITE);

            boolean placed = false;
            while (!placed) {
                // generate random x and y coordinates within bounds of innerPane
                double x = radius + Math.random() * (innerPane.getWidth() - 2 * radius);
                double y = radius + Math.random() * (innerPane.getHeight() - 2 * radius);
                circle.setCenterX(x);
                circle.setCenterY(y);

                // check for collisions with already placed circles
                boolean collision = false;
                for (Circle other : circles) {
                    double dx = circle.getCenterX() - other.getCenterX();
                    double dy = circle.getCenterY() - other.getCenterY();
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance < 2 * radius) {// circles overlap if distance is less than diameter
                        collision = true;
                        break;
                    }
                }

                if (!collision) {
                    // add circle to pane and list once placement is valid
                    innerPane.getChildren().addAll(circle, index);
                    circles.add(circle);
                    placed = true;
                    // position the text at the center of the circle
                    index.setX(circle.getCenterX() - index.getLayoutBounds().getWidth() / 2);
                    index.setY(circle.getCenterY() + index.getLayoutBounds().getHeight() / 4);
                    index.xProperty().bind(circle.centerXProperty().subtract(index.getLayoutBounds().getWidth() / 2));
                    index.yProperty().bind(circle.centerYProperty().add(index.getLayoutBounds().getHeight() / 4));
                    // enable drag-and-drop functionality for circle
                    circleInteractions.makeDraggable(circle, 0, 0, innerPane.getWidth(), innerPane.getHeight());
                    // set up click event to color circle
                    circle.setOnMouseClicked(e -> circleInteractions.colorCircle(circle));
                }
            }
        }
        // draw edges between circles after all are placed
        drawEdgesFromBox(circles, innerPane, numEdges);

        resetTimer();
        startTimer();
    }

    /**
     * connects circles using edges to form spanning tree and additional random
     * edges.
     * ensures no duplicate or reverse-direction edges, using set for tracking.
     * maintains specified number of edges while preserving graph properties.
     *
     * @param circles
     * @param innerPane
     * @param numEdges
     */
    private void drawEdgesFromBox(ArrayList<Circle> circles, Pane innerPane, int numEdges) {
        int[][] lines = new int[numEdges][2];
        int n = circles.size();

        List<Line> edges = new ArrayList<>();
        Set<String> edgeSet = new HashSet<>(); // tracks edges to prevent duplicates
        Random rand = new Random();
        List<Integer> availableVertices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            availableVertices.add(i);
        }
        Collections.shuffle(availableVertices); // randomizes vertex order for tree construction

        int[] parent = new int[n]; // parent array for tree structure
        Arrays.fill(parent, -1);

        // generate a spanning tree by connecting shuffled vertices
        for (int i = 1; i < n; i++) {
            int u = availableVertices.get(i - 1);
            int v = availableVertices.get(i);

            lines[i - 1][0] = u + 1;
            lines[i - 1][1] = v + 1;

            addEdge(u, v, circles, innerPane, edges, edgeSet); // create and add edge
            parent[v] = u; // update parent relationship
        }

        // add extra random edges until desired edge count is reached
        int counter = edges.size();
        while (counter < numEdges) {
            int u = rand.nextInt(n);
            int v = rand.nextInt(n);

            // ensure u and v are distinct and edge is unique
            if (u != v && !edgeSet.contains(u + "-" + v) && !edgeSet.contains(v + "-" + u)) {
                lines[counter][0] = u + 1;
                lines[counter][1] = v + 1;
                addEdge(u, v, circles, innerPane, edges, edgeSet);
                counter++;
            }
        }

        autoButton.setUserData(circles);

        ChromaticNumber lessResistance = new ChromaticNumber(lines);
        lessResistance.calculateChromaticNumber();
        chromaticNumber = lessResistance.getChromaticNumber();
        graphColoring = lessResistance.getGraphColoring();
    }

    /**
     * creates edge between two circles and adds it to edge list and set.
     * binds line endpoints to circle centers for dynamic updates.
     * 
     * @param u
     * @param v
     * @param circles
     * @param innerPane
     * @param edges
     * @param edgeSet
     */

    private void addEdge(int u, int v, ArrayList<Circle> circles, Pane innerPane, List<Line> edges,
            Set<String> edgeSet) {
        Circle circle1 = circles.get(u);
        Circle circle2 = circles.get(v);

        circleInteractions.addAdjacent(circle1, circle2);
        circleInteractions.addAdjacent(circle2, circle1);

        Line line = new Line();
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(0.8);

        // Bind the start and end points of the line to the circle properties
        // dynamically
        line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
            double dx = circle2.getCenterX() - circle1.getCenterX();
            double dy = circle2.getCenterY() - circle1.getCenterY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            double ratio = circle1.getRadius() / distance;
            return circle1.getCenterX() + dx * ratio;
        }, circle1.centerXProperty(), circle2.centerXProperty(), circle1.radiusProperty(), circle2.radiusProperty()));

        line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
            double dx = circle2.getCenterX() - circle1.getCenterX();
            double dy = circle2.getCenterY() - circle1.getCenterY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            double ratio = circle1.getRadius() / distance;
            return circle1.getCenterY() + dy * ratio;
        }, circle1.centerYProperty(), circle2.centerYProperty(), circle1.radiusProperty(), circle2.radiusProperty()));

        line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
            double dx = circle2.getCenterX() - circle1.getCenterX();
            double dy = circle2.getCenterY() - circle1.getCenterY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            double ratio = circle2.getRadius() / distance;
            return circle2.getCenterX() - dx * ratio;
        }, circle1.centerXProperty(), circle2.centerXProperty(), circle1.radiusProperty(), circle2.radiusProperty()));

        line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
            double dx = circle2.getCenterX() - circle1.getCenterX();
            double dy = circle2.getCenterY() - circle1.getCenterY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            double ratio = circle2.getRadius() / distance;
            return circle2.getCenterY() - dy * ratio;
        }, circle1.centerYProperty(), circle2.centerYProperty(), circle1.radiusProperty(), circle2.radiusProperty()));

        innerPane.getChildren().add(line);
        edges.add(line);
        edgeSet.add(u + "-" + v);
    }

    /**
     * start new game from text file by calling loadTextFile function
     * 
     * @param pane
     * @param colorPicker
     * @param stage
     */
    private void newGameViaTxt(Pane pane, ColorPicker colorPicker, Stage stage, Pane innerPane) {

        Button button = new Button(".TXT");

        button.setOnAction(e -> {
            innerPane.getChildren().clear();
            resetTimer();
            loadTextFile(stage, colorPicker, innerPane);
        });

        button.setMaxWidth(50);

        button.setLayoutX(1220);
        button.setLayoutY(10);

        pane.getChildren().addAll(button);

    }

    /**
     * load text file via filechooser widget and pass to processTextFile if not null
     * 
     * @param stage
     * @param colorPicker
     */
    private void loadTextFile(Stage stage, ColorPicker colorPicker, Pane innerPane) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open text file.");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            processTextfile(file, colorPicker, innerPane);
        }
    }

    /**
     * pass to function
     * 
     * @param file
     * @param colorPicker
     */
    private void processTextfile(File file, ColorPicker colorPicker, Pane innerPane) {
        ReadGraph readGraph = new ReadGraph();
        loadDataFromTxt(readGraph.processTextfile(file), colorPicker, innerPane);

    }

    /**
     * extracts vertex count and edges, and generates canvas via createCanvasViaTxt.
     * 
     * @param e
     * @param colorPicker
     */
    private void loadDataFromTxt(Object[] e, ColorPicker colorPicker, Pane innerPane) {

        int numVertices = (Integer) e[0];
        ColEdge[] edges = (ColEdge[]) e[2];
        int[][] edgeArray = (int[][]) e[3];

        SpecialCases specialCases = new SpecialCases(numVertices);

        for (int[] edge : edgeArray) {
            specialCases.addEdge(edge[0], edge[1]);
        }

        if (specialCases.isBipartite()) {
            chromaticNumber = 2;
        }

        if (specialCases.isTree()) {
            chromaticNumber = 2;
        }

        if (specialCases.isStar()) {
            chromaticNumber = 2;
        }

        if (specialCases.isComplete()) {
            chromaticNumber = numVertices;
        }

        if (specialCases.isCycle()) {
            if (numVertices % 2 == 0) {
                // case even
                chromaticNumber = 2;
            } else {
                // case odd
                chromaticNumber = 3;
            }
        }

        if (specialCases.isWheel()) {
            if (numVertices % 2 == 0) {
                // case even
                chromaticNumber = 4;
            } else {
                // case odd
                chromaticNumber = 3;
            }
        }

        ChromaticNumber lessResistance = new ChromaticNumber(edgeArray);
        lessResistance.calculateChromaticNumber();
        chromaticNumber = lessResistance.getChromaticNumber();
        graphColoring = lessResistance.getGraphColoring();

        innerPane.getChildren().clear();

        if (numVertices > 50) {
            resetTimer();
            setLimiter();
        } else {
            createCanvasViaTxt(innerPane, numVertices, colorPicker, edges);
        }
    }

    /**
     * same implementation as createCanvasViaBox
     * 
     * @param innerPane
     * @param numCircles
     * @param colorPicker
     * @param edges
     */
    private void createCanvasViaTxt(Pane innerPane, int numCircles, ColorPicker colorPicker, ColEdge[] edges) {
        double radius = 15;
        ArrayList<Circle> circles = new ArrayList<>();

        for (int i = 0; i < numCircles; i++) {
            Circle circle = new Circle(radius);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(1);
            Text index = new Text(String.valueOf(i + 1));
            index.setFill(Color.WHITE);
            boolean placed = false;
            while (!placed) {
                double x = radius + Math.random() * (innerPane.getWidth() - 2 * radius);
                double y = radius + Math.random() * (innerPane.getHeight() - 2 * radius);
                circle.setCenterX(x);
                circle.setCenterY(y);

                boolean collision = false;
                for (Circle other : circles) {
                    double dx = circle.getCenterX() - other.getCenterX();
                    double dy = circle.getCenterY() - other.getCenterY();
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance < 2 * radius) {
                        collision = true;
                        break;
                    }
                }

                if (!collision) {
                    innerPane.getChildren().addAll(circle, index);
                    circles.add(circle);
                    placed = true;
                    index.setX(circle.getCenterX() - index.getLayoutBounds().getWidth() / 2);
                    index.setY(circle.getCenterY() + index.getLayoutBounds().getHeight() / 4);
                    index.xProperty().bind(circle.centerXProperty().subtract(index.getLayoutBounds().getWidth() / 2));
                    index.yProperty().bind(circle.centerYProperty().add(index.getLayoutBounds().getHeight() / 4));
                    circleInteractions.makeDraggable(circle, 0, 0, innerPane.getWidth(), innerPane.getHeight());
                    circle.setOnMouseClicked(e -> circleInteractions.colorCircle(circle));
                }
            }
        }

        drawEdgesFromTxt(circles, innerPane, edges);
        resetTimer();
        startTimer();
    }

    /**
     * same implementation as drawEdgesFromBox
     * 
     * @param circles
     * @param innerPane
     * @param edges
     */
    private void drawEdgesFromTxt(ArrayList<Circle> circles, Pane innerPane, ColEdge[] edges) {
        List<Line> edgeLines = new ArrayList<>();

        for (ColEdge edge : edges) {
            int u = edge.u - 1;
            int v = edge.v - 1;

            Circle circle1 = circles.get(u);
            Circle circle2 = circles.get(v);

            circleInteractions.addAdjacent(circle1, circle2);
            circleInteractions.addAdjacent(circle2, circle1);

            Line line = new Line();
            line.setStroke(Color.WHITE);
            line.setStrokeWidth(0.8);

            // Bind the start and end points of the line to the circle properties
            // dynamically
            line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
                double dx = circle2.getCenterX() - circle1.getCenterX();
                double dy = circle2.getCenterY() - circle1.getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                double ratio = circle1.getRadius() / distance;
                return circle1.getCenterX() + dx * ratio;
            }, circle1.centerXProperty(), circle2.centerXProperty(), circle1.radiusProperty(),
                    circle2.radiusProperty()));

            line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
                double dx = circle2.getCenterX() - circle1.getCenterX();
                double dy = circle2.getCenterY() - circle1.getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                double ratio = circle1.getRadius() / distance;
                return circle1.getCenterY() + dy * ratio;
            }, circle1.centerYProperty(), circle2.centerYProperty(), circle1.radiusProperty(),
                    circle2.radiusProperty()));

            line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
                double dx = circle2.getCenterX() - circle1.getCenterX();
                double dy = circle2.getCenterY() - circle1.getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                double ratio = circle2.getRadius() / distance;
                return circle2.getCenterX() - dx * ratio;
            }, circle1.centerXProperty(), circle2.centerXProperty(), circle1.radiusProperty(),
                    circle2.radiusProperty()));

            line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
                double dx = circle2.getCenterX() - circle1.getCenterX();
                double dy = circle2.getCenterY() - circle1.getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                double ratio = circle2.getRadius() / distance;
                return circle2.getCenterY() - dy * ratio;
            }, circle1.centerYProperty(), circle2.centerYProperty(), circle1.radiusProperty(),
                    circle2.radiusProperty()));

            innerPane.getChildren().add(line);
            edgeLines.add(line);
        }

        autoButton.setUserData(circles);
    }

    /**
     * create button widget to undo circle coloring by calling undoColor function of
     * circleInteractions class
     * 
     * @param pane
     * @return new button Widget
     */
    private Button createUndoButton(Pane pane) {
        Button button = new Button("UNDO COLORING");
        button.setLayoutX(630);
        button.setLayoutY(10);
        pane.getChildren().add(button);
        button.setOnAction(e -> circleInteractions.undoColor());
        return button;
    }

    /**
     * create switchbutton to switch mode
     * 
     * @param pane
     * @return new switchbutton object
     */
    private SwitchButton createSwitchButtonColor(Pane pane) {
        SwitchButton switchButton = new SwitchButton();
        switchButton.setLayoutX(375);
        switchButton.setLayoutY(10);
        pane.getChildren().addAll(switchButton);
        return switchButton;
    }

    /**
     * create switchbutton to switch mode
     * 
     * @param pane
     * @return new switchbutton object
     */
    private SwitchButton createSwitchButtonMove(Pane pane) {
        SwitchButton switchButton = new SwitchButton();
        switchButton.setLayoutX(545);
        switchButton.setLayoutY(10);
        pane.getChildren().addAll(switchButton);
        return switchButton;
    }

    /**
     * display dialog box with message passed as argument
     * 
     * @param string
     * @param content
     */
    private void dialogBox(String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * button to finish game, and display score
     * 
     * @param pane
     */
    private void finishGame(Pane pane) {
        Button button = new Button("FINISH");
        button.setLayoutX(1280);
        button.setLayoutY(10);
        pane.getChildren().addAll(button);

        button.setOnAction(e -> {

            if (!innerPane.getChildren().isEmpty()) {
                if (innerPane.getChildren().stream().filter(node -> node instanceof Circle)
                        .allMatch(node -> ((Circle) node).getFill() != null
                                && !((Circle) node).getFill().equals(Color.TRANSPARENT))) {
                    resetTimer();
                    int result = colorScore();
                    if (result == chromaticNumber || result < chromaticNumber) {
                        dialogBox("Game over", "Time taken: " + score
                                + "\nYou found an ideal solution. Indeed, the chromatic number is " + result + ".");
                    } else if (result > chromaticNumber) {
                        dialogBox("Game over", "Time taken: " + score
                                + "\nUnfortunately, you used more colors than the ideal.\nThe chromatic number is "
                                + chromaticNumber + ".");
                    }
                    innerPane.getChildren().clear();
                } else {
                    pauseTimer();
                    dialogBox("Game in progress", "Game has not finished yet. You need to colorize all vertices.");
                    resumeTimer();
                }
            } else {
                dialogBox("Game did not start", "You have not started a game yet.");
            }
        });
    }

    /**
     * start stopwatch
     */
    private void startTimer() {
        if (timerRunning)
            return;

        startTime = System.currentTimeMillis();
        timerRunning = true;
        timer = new AnimationTimer() {
            public void handle(long now) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                time.setText(formatTime(elapsedTime));
                timeScore(elapsedTime);
            }
        };
        timer.start();

    }

    /**
     * stop and reset stopwatch
     */
    public void resetTimer() {

        if (timer != null)
            timer.stop();
        timerRunning = false;
        isPaused = false;
        time.setText("00:00:00.000");
    }

    /*
     * pause timer
     */
    public void pauseTimer() {
        if (!timerRunning || isPaused)
            return;
        pausedTime = System.currentTimeMillis() - startTime;
        timer.stop();
        isPaused = true;
    }

    /*
     * resume timer
     */
    public void resumeTimer() {
        if (!isPaused)
            return;

        startTime = System.currentTimeMillis() - pausedTime;
        timerRunning = true;
        isPaused = false;

        timer = new AnimationTimer() {
            public void handle(long now) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                time.setText(formatTime(elapsedTime));
                timeScore(elapsedTime);
            }
        };
        timer.start();
    }

    /**
     * format time in H:M:S:MS to display to user
     * 
     * @param elapsedTime
     * @return
     */
    public String formatTime(long elapsedTime) {
        return String.format("%02d:%02d:%02d.%03d", elapsedTime / 3600000, (elapsedTime % 3600000) / 60000,
                (elapsedTime % 60000) / 1000, elapsedTime % 1000);
    }

    /**
     * edit score in real time
     * 
     * @param elapsedTime
     */
    private void timeScore(long elapsedTime) {
        score = formatTime(elapsedTime);
    }

    /**
     * return number of colors used by user to color graph
     * 
     * @return
     */
    private int colorScore() {
        List<Color> list = new ArrayList<>();
        for (Node node : innerPane.getChildren()) {
            if (node instanceof Circle circle) {
                Color color = (Color) circle.getFill();
                if (!list.contains(color))
                    list.add(color);
            }
        }

        return list.size();

    }

    /**
     * Set limit to 50 circles, no graph is drawn as it would be difficult for the
     * user to distinguish vertices and edges
     * If the graph has been created randomly, we will not calculate the chromatic
     * because, at that point, edges have not been created yet.
     */
    private void setLimiter() {
        if (chromaticNumber == 0) {
            dialogBox("Number of vertices is limited",
                    "We do not display graphs which have more than 50 vertices.\nSince vertices and edges are created randomly,\nwe do not know how they should be beforehand.\nHence, we cannot provide you with a chromatic number.");
        } else {
            dialogBox("Number of vertices is limited",
                    "We do not display graphs which have more than 50 vertices.\nHowever, the chromatic number of this graph is "
                            + chromaticNumber);
        }
    }

    private void sendCopyToAuto(ArrayList<Circle> circles, Button button) {
        button.setUserData(circles);
    }

    /*
     * give up function
     * autocomplete function to be included
     */
    private void giveUp(Pane pane) {

        innerPane.getChildren().stream()
                .filter(node -> node instanceof Circle)
                .findFirst()
                .ifPresentOrElse(
                        circle -> {
                            // If there are circles

                            resetTimer();

                            innerPane.getChildren().stream()
                                    .filter(node -> node instanceof Circle)
                                    .forEach(node -> {
                                        Circle c = (Circle) node;
                                        c.setFill(Color.TRANSPARENT);
                                        c.setStroke(Color.WHITE);
                                        c.setStrokeWidth(1);
                                    });

                            // graph autocompletion goes here

                            @SuppressWarnings("unchecked")
                            ArrayList<Circle> circles = (ArrayList<Circle>) autoButton.getUserData();

                            for (int i = 0; i < graphColoring.size(); i++) {
                                ArrayList<Integer> innerList = graphColoring.get(i);
                                for (int j = 0; j < innerList.size(); j++) {
                                    int circleIndex = innerList.get(j) - 1;
                                    circles.get(circleIndex).setFill(colors[i]);
                                }
                            }

                            // dialog box for user to clear graph once they have viewed autocompleted graph

                            dialogBox("Waiting for input", "Click to clear pane and start new game.");
                            innerPane.getChildren().clear();
                        },
                        () -> {
                            // If there are no circles

                            dialogBox("Warning", "No circles found.");
                        });

    }

}