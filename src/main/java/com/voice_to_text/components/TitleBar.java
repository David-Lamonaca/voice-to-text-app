package com.voice_to_text.components;

import com.jfoenix.controls.JFXButton;
import com.voice_to_text.managers.SceneManager;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class TitleBar extends HBox 
{
    private double xOffset = 0;
    private double yOffset = 0;

    public TitleBar(Stage primaryStage) 
    {
        setSpacing(10);
        setPadding(new Insets(5, 5, 5, 5));
       

        JFXButton homeButton = new IconButton("/icons/home.png", 30, 30, () -> { SceneManager.getInstance().loadMainApp(); });
        JFXButton settingsButton = new IconButton("/icons/settings.png", 30, 30, () -> { SceneManager.getInstance().loadSettingsPage(); });
        JFXButton minimizeButton = new IconButton("/icons/minimize.png", 30, 30, () -> primaryStage.setIconified(true));
        JFXButton closeButton = new IconButton("/icons/close.png", 30, 30, () -> { Platform.exit(); System.exit(0); });

        // Invisible spacer to push items to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Arrange buttons
        HBox topLeft = new HBox(5, homeButton, settingsButton);
        HBox topRight = new HBox(5, minimizeButton, closeButton);
        getChildren().addAll(topLeft, spacer, topRight);

        // Enable window dragging
        setOnMousePressed(event -> 
        {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        setOnMouseDragged(event -> 
        {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
    }
}

