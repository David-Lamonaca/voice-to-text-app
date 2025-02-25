package com.voice_to_text;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private TextArea textArea;
    private Button recordButton;
    private boolean isRecording = false;
    private AudioRecorder audioRecorder;
    private SpeechToText speechToText;
    private TextFieldInteractor textFieldInteractor;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Real-Time Voice to Text");

        // Initialize UI components
        textArea = new TextArea();
        textArea.setEditable(false);
        recordButton = new Button("Record");

        // Set up the layout
        VBox vbox = new VBox(textArea, recordButton);
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize SpeechToText, AudioRecorder, and TextFieldInteractor
        String modelPath = "C:\\_Programming_Stuff\\_desktopApps\\voice-to-text-app\\vosk-model-small-en-us-0.15";
        speechToText = new SpeechToText(modelPath);
        audioRecorder = new AudioRecorder();
        textFieldInteractor = new TextFieldInteractor();

        // Set up the audio data listener
        audioRecorder.setAudioDataListener(audioData -> {
            String transcribedText = speechToText.transcribe(audioData);
            javafx.application.Platform.runLater(() -> {
                textArea.appendText(transcribedText + "\n"); // Display in the app's text area
                textFieldInteractor.typeText(transcribedText); // Type into the active text field
            });
        });

        // Add event handler for the Record button
        recordButton.setOnAction(event -> {
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });
    }

    private void startRecording() {
        isRecording = true;
        recordButton.setText("Stop");
        textArea.appendText("Recording...\n");
        audioRecorder.startRecording();
    }

    private void stopRecording() {
        isRecording = false;
        recordButton.setText("Record");
        textArea.appendText("Stopped recording.\n");
        audioRecorder.stopRecording();
    }

    public static void main(String[] args) {
        launch(args);
    }
}