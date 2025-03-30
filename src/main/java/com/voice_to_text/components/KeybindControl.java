package com.voice_to_text.components;

import com.voice_to_text.managers.SettingsManager;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class KeybindControl extends VBox 
{
    private final Label keyLabel;
    private final Button editButton;
    private final String settingKey;

    public KeybindControl(String labelText, String settingKey) 
    {
        this.settingKey = settingKey;

        Label labelTextLabel = new Label(labelText);
        labelTextLabel.getStyleClass().add("settings-label");

        keyLabel = new Label(SettingsManager.getInstance().getSetting(settingKey));
        keyLabel.getStyleClass().add("keybind-label");

        editButton = new Button("Edit Keybind");
        editButton.getStyleClass().add("keybind-button");

        setupEditButton();

        // Invisible spacer to push the button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox keybindBox = new HBox(10, keyLabel, spacer, editButton);
        keybindBox.getStyleClass().add("keybind-box");
        keybindBox.setAlignment(Pos.CENTER_LEFT);

        this.getChildren().addAll(labelTextLabel, keybindBox);
        this.setSpacing(5);
    }

    private void setupEditButton() 
    {
        editButton.setOnAction(e -> 
        {
            editButton.setText("Press any key...");

            // Create a key event handler
            EventHandler<KeyEvent> keyListener = new EventHandler<>() 
            {
                @Override
                public void handle(KeyEvent event) 
                {
                    if (event.getCode() != KeyCode.ESCAPE) 
                    {
                        // User selects a new key
                        String newKey = event.getCode().toString();
                        SettingsManager.getInstance().updateSetting(settingKey, newKey);
                        keyLabel.setText(newKey);
                    }

                    // Restore button text and remove event handler
                    editButton.setText("Edit Keybind");
                    editButton.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, this);
                }
            };

            // Add the event handler to listen for key presses
            editButton.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyListener);
        });
    }
}
