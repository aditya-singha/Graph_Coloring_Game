import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Graph Coloring Game");

        Pane pane = new Pane();

        pane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));

        Label gameLabel = new Label("Graph Coloring Game");
        gameLabel.setFont(Font.font("Lucida Console", 70));
        gameLabel.setLayoutX(50);
        gameLabel.setLayoutY(50);

        pane.getChildren().addAll(gameLabel);

        Button mode1Button = new Button("To the bitter end");
        Button mode2Button = new Button("Random order");
        Button mode3Button = new Button("I changed my mind");

        gameLabel.setId("main-header");
        mode1Button.setId("mode-button");
        mode2Button.setId("mode-button");
        mode3Button.setId("mode-button");

        pane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        mode1Button.setLayoutX(50);
        mode1Button.setLayoutY(500);

        mode2Button.setLayoutX(50);
        mode2Button.setLayoutY(550);

        mode3Button.setLayoutX(50);
        mode3Button.setLayoutY(600);

        mode1Button.setOnAction(e -> {
            modeOne mode1 = new modeOne();
            mode1.start(stage);
        });

        mode2Button.setOnAction(e -> {
            modeTwo mode2 = new modeTwo();
            mode2.start(stage);
        });

        mode3Button.setOnAction(e -> {
            modeThree mode3 = new modeThree();
            mode3.start(stage);
        });

        pane.getChildren().addAll(mode1Button, mode2Button, mode3Button);

        Screen screen = Screen.getPrimary();
        stage.setWidth(screen.getVisualBounds().getWidth());
        stage.setHeight(screen.getVisualBounds().getHeight());
        stage.setScene(new Scene(pane));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}