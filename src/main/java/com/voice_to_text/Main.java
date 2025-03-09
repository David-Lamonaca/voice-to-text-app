package com.voice_to_text;

import com.jfoenix.controls.JFXButton;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application 
{
    private HBox titleBar;
    private Button recordButton;
    private boolean isRecording = false;
    private AudioRecorder audioRecorder;
    private SpeechToText speechToText;
    private TextFieldInteractor textFieldInteractor;
    private String lastRecognizedText = "";

    private double xOffset = 0.0;
    private double yOffset = 0.0;

    @Override
    public void start(Stage primaryStage) 
    {
        setupTopbar(primaryStage);

        // Overall Layout
        recordButton = new Button("Record");
        VBox layout = new VBox(10, titleBar, recordButton);
        layout.setStyle
        (
            "-fx-background-color: #1e2124; " +
            "-fx-padding: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-radius: 10px; " +      
            "-fx-border-color: #333; " +       
            "-fx-border-width: 2px;"          
        );

        Scene scene = new Scene(layout, 400, 300);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize components
        String modelPath = "C:\\_Programming_Stuff\\_desktopApps\\voice-to-text-app\\vosk-model-small-en-us-0.15";
        speechToText = new SpeechToText(modelPath);
        audioRecorder = new AudioRecorder();
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

    public void resetTypedText()
    {
        textFieldInteractor.resetTypedText();
    }

    private void setupTopbar(Stage primaryStage)
    {
        // Home Icon
        ImageView homeIcon = new ImageView(new Image(getClass().getResource("/icons/home.png").toExternalForm()));
        homeIcon.setFitWidth(30);
        homeIcon.setFitHeight(30);
        JFXButton homeButton = new JFXButton("", homeIcon);
        homeButton.setOnAction(e -> loadMainApp());

        // Settings Icon
        ImageView settingsIcon = new ImageView(new Image(getClass().getResource("/icons/settings.png").toExternalForm()));
        settingsIcon.setFitWidth(30);
        settingsIcon.setFitHeight(30);
        JFXButton settingsButton = new JFXButton("", settingsIcon);
        settingsButton.setOnAction(e -> loadSettingsPage());

        // Minimize Icon
        ImageView minimizeIcon = new ImageView(new Image(getClass().getResource("/icons/minimize.png").toExternalForm()));
        minimizeIcon.setFitWidth(30);
        minimizeIcon.setFitHeight(30);
        JFXButton minimizeButton = new JFXButton("", minimizeIcon);
        minimizeButton.setOnAction(e -> primaryStage.setIconified(true));

        // Close Icon
        ImageView closeIcon = new ImageView(new Image(getClass().getResource("/icons/close.png").toExternalForm()));
        closeIcon.setFitWidth(30);
        closeIcon.setFitHeight(30);
        JFXButton closeButton = new JFXButton("", closeIcon);
        closeButton.setOnAction(e -> System.exit(0));

        // Title Bar
        HBox topLeft = new HBox(5, homeButton, settingsButton);
        HBox topRight = new HBox(5, minimizeButton, closeButton);
        titleBar = new HBox(195, topLeft, topRight);

        // Enable dragging
        titleBar.setOnMousePressed(event -> 
        {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> 
        {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });  
    }

    private void loadMainApp() {
        System.out.println("Main App Loaded");
    }

    private void loadSettingsPage() {
        System.out.println("Settings Page Loaded");
    }

    public static void main(String[] args) 
    {
        launch(args);
    }
}
