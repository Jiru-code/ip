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

    private MrYapper mryapper = new MrYapper();

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/noobuser.png"));
    private final Image mryapperImage  = new Image(this.getClass().getResourceAsStream("/images/mryapper.png"));

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
    
            // Controller from FXML should be a MainWindow
            Object ctrl = fxmlLoader.getController();
            assert ctrl instanceof MainWindow : "FXML controller is not MainWindow";
            MainWindow mw = (MainWindow) ctrl;
    
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
            stage.setScene(scene);
    
            // mryapper must be constructed already
            assert mryapper != null : "MrYapper not initialised";
            mw.setYapper(mryapper);
    
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void initialize() {
        // FXML injections should be non-null if the fx:id matches
        assert scrollPane != null : "scrollPane not injected";
        assert dialogContainer != null : "dialogContainer not injected";
        assert userInput != null : "userInput not injected";
        assert sendButton != null : "sendButton not injected";
    
        // Must run on JavaFX Application Thread
        assert javafx.application.Platform.isFxApplicationThread() : "Not on FX thread";
    
        scrollPane.setFitToWidth(true);
        dialogContainer.heightProperty().addListener((obs, oldVal, newVal) -> scrollPane.setVvalue(1.0));
    }
    public void setYapper(MrYapper mryapper) {
        this.mryapper = mryapper;
        // Show greeting from the bot
        dialogContainer.getChildren().add(
            DialogBox.getMrYapperDialog("Hello! I'm your best yapper Mr Yapper!\n"
            + "Tell me all about your tasks you need to do!", 
            mryapperImage
            )
        );
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }
        
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