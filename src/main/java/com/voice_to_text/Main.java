package com.voice_to_text;

import com.voice_to_text.listeners.ActiveWindowTracker;
import com.voice_to_text.listeners.GlobalKeyListener;
import com.voice_to_text.managers.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application 
{
    private AudioRecorder audioRecorder;
    private SpeechToText speechToText;
    private TextFieldInteractor textFieldInteractor;
    private String lastRecognizedText = "";

    @Override
    public void start(Stage primaryStage) 
    {
        SceneManager.getInstance().init(primaryStage);
        SceneManager.getInstance().loadMainApp();

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();

        // Initialize components
        String modelPath = "C:\\_Programming_Stuff\\_desktopApps\\voice-to-text-app\\vosk-model-small-en-us-0.15";
        speechToText = new SpeechToText(modelPath);
        audioRecorder = AudioRecorder.getInstance();
        textFieldInteractor = new TextFieldInteractor();

        // Detect when window loses focus, and a certain key is pressed.
        startActiveWindowMonitor();
        GlobalKeyListener.register(this);

        // Audio data listener
        audioRecorder.setAudioDataListener(audioData -> 
        {
            String transcribedText = speechToText.transcribe(audioData);
            if (!transcribedText.isEmpty() && !isUnintentionalRepetition(transcribedText)) 
            {
                javafx.application.Platform.runLater(() -> 
                {
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
    
    public void resetTypedText() 
    {
        textFieldInteractor.resetTypedText();
    }

    public static void main(String[] args) 
    {
        launch(args);
    }
}
