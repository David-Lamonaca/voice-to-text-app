package com.voice_to_text.layouts;

import com.voice_to_text.AudioRecorder;
import com.voice_to_text.managers.SettingsManager;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class HomeLayout extends VBox 
{
    private final Circle statusCircle;
    private final Label statusLabel;
    private ImageView micView;

    public HomeLayout() 
    {
        // Image setup
        Image micIcon = new Image(getClass().getResourceAsStream("/icons/mic.png"));
        micView = new ImageView(micIcon);
        micView.setFitWidth(100);
        micView.setFitHeight(100);

        // Circle border
        statusCircle = new Circle(120);
        statusCircle.setStrokeWidth(4);
        statusCircle.setFill(Color.TRANSPARENT);

        // Status text
        statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        // Stack mic over circle
        StackPane circleWithIcon = new StackPane(statusCircle, micView);
        StackPane.setAlignment(micView, Pos.CENTER);
        StackPane.setAlignment(statusCircle, Pos.CENTER);
        
        // VBox Layout
        VBox.setVgrow(this, Priority.ALWAYS);
        setSpacing(20);
        setAlignment(Pos.CENTER);
        getChildren().addAll(circleWithIcon, statusLabel);

        updateStatus();
        SettingsManager.getInstance().inputModeProperty()
            .addListener((obs, oldVal, newVal) -> 
        {
            updateStatus();
        });

        AudioRecorder.getInstance().pushToTalkHeldProperty()
            .addListener((obs, oldVal, newVal) -> 
        {
            updateStatus();
        });
    }

    public void updateStatus() 
    {
        Platform.runLater(() -> 
        {
            String inputModeValue = SettingsManager.getInstance().getSetting("inputMode");
            boolean isPttKeyHeld = AudioRecorder.getInstance().isPushToTalkHeld(); 

            boolean listening = inputModeValue.equals("Voice Activity") || 
                            (inputModeValue.equals("Push to Talk") && isPttKeyHeld);

            if (listening) 
            {
                statusCircle.setStroke(Color.LIMEGREEN);
                statusLabel.setText("Listening...");
            } 
            else 
            {
                statusCircle.setStroke(Color.RED);
                statusLabel.setText("NOT Listening...");
            }
        });   
    }
}
