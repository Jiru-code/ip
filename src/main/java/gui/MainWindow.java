package gui;

import mryapper.MrYapper;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * A GUI for MrYapper using FXML, orchestrates the input/ouput flow.
 */
public class MainWindow extends Application {

    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    @FXML private Button sendButton;

    private MrYapper mryapper;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/noobuser.png"));
    private final Image mryapperImage  = new Image(this.getClass().getResourceAsStream("/images/mryapper.png"));

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setYapper(mryapper); 
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Auto-scroll to bottom as messages are added
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    public void setYapper(MrYapper mryapper) {
        this.mryapper = mryapper;
        // Show greeting from the bot
        dialogContainer.getChildren().add(
            DialogBox.getMrYapperDialog("Hello! I'm Mr Yapper\nWhat can I do for you?", mryapperImage)
        );
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) return;

        String response = mryapper.getResponse(input);

        if ("bye".equalsIgnoreCase(input.trim())) {
            dialogContainer.getChildren().add(DialogBox.getMrYapperDialog(response, mryapperImage));
            userInput.clear();
            javafx.application.Platform.exit();
            return;
        }

        dialogContainer.getChildren().addAll(
            DialogBox.getUserDialog(input, userImage),
            DialogBox.getMrYapperDialog(response, mryapperImage)
        );

        userInput.clear();
    }
}