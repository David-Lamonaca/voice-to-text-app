package com.voice_to_text;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application 
{
    private TextArea textArea;
    private Button recordButton;
    private boolean isRecording = false;
    private AudioRecorder audioRecorder;
    private SpeechToText speechToText;
    private TextFieldInteractor textFieldInteractor;
    private String lastRecognizedText = "";

    @Override
    public void start(Stage primaryStage) 
    {
        primaryStage.setTitle("Real-Time Voice to Text");

        // UI components
        textArea = new TextArea();
        textArea.setEditable(false);
        recordButton = new Button("Record");

        VBox vbox = new VBox(textArea, recordButton);
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize components
        String modelPath = "C:\\_Programming_Stuff\\_desktopApps\\voice-to-text-app\\vosk-model-small-en-us-0.15";
        speechToText = new SpeechToText(modelPath);
        audioRecorder = new AudioRecorder();
        textFieldInteractor = new TextFieldInteractor();

        // Detect when window loses focus
        startActiveWindowMonitor();

        // Audio data listener
        audioRecorder.setAudioDataListener(audioData -> 
        {
            String transcribedText = speechToText.transcribe(audioData);
            if (!transcribedText.isEmpty() && !isUnintentionalRepetition(transcribedText)) 
            {
                javafx.application.Platform.runLater(() -> 
                {
                    textArea.appendText(transcribedText + "\n");

                    try 
                    {
                        Thread.sleep(500);
                    } 
                    catch (InterruptedException e) 
                    {
                        e.printStackTrace();
                    }

                    textFieldInteractor.typeText(transcribedText);
                    if (!transcribedText.equals(lastRecognizedText)) 
                    {
                        lastRecognizedText = transcribedText;
                    }
                });
                speechToText.resetRecognizer();
            }
        });

        recordButton.setOnAction(event -> 
        {
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });
    }

    private void startActiveWindowMonitor() 
    {
        new Thread(() -> 
        {
            String lastWindowTitle = ActiveWindowTracker.getActiveWindowTitle();
    
            while (true) 
            {
                try 
                {
                    Thread.sleep(500);
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
    
                String currentWindowTitle = ActiveWindowTracker.getActiveWindowTitle();
                if (!currentWindowTitle.equals(lastWindowTitle)) 
                {
                    textFieldInteractor.resetTypedText();
                    lastWindowTitle = currentWindowTitle;
                }
            }
        }).start();
    }
    
    private boolean isUnintentionalRepetition(String transcribedText) 
    {
        if (lastRecognizedText == null || lastRecognizedText.trim().isEmpty()) 
        {
            return false; 
        }
    
        // Normalize the text by trimming and converting to lowercase
        String currentText = transcribedText.trim().toLowerCase();
        String lastText = lastRecognizedText.trim().toLowerCase();
    
        // Allow intentional repetitions (e.g., "because because")
        if (currentText.equals(lastText)) 
        {
            return false;
        }
    
        // Block unintentional repetitions caused by the recognizer
        // Check if the new text is a strict prefix of the previous text
        boolean isUnintentional = currentText.length() < lastText.length() &&
                                  lastText.startsWith(currentText);
        return isUnintentional;
    }


    private void startRecording() 
    {
        isRecording = true;
        recordButton.setText("Stop");
        audioRecorder.startRecording();
    }

    private void stopRecording() 
    {
        isRecording = false;
        recordButton.setText("Record");
        audioRecorder.stopRecording();
    }

    public static void main(String[] args) 
    {
        launch(args);
    }
}
