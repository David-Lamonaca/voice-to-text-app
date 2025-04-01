package com.voice_to_text.layouts;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.voice_to_text.AudioRecorder;
import com.voice_to_text.KeywordItem;
import com.voice_to_text.components.IconButton;
import com.voice_to_text.components.KeybindControl;
import com.voice_to_text.managers.SceneManager;
import com.voice_to_text.managers.SettingsManager;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SettingsLayout extends VBox 
{
    public SettingsLayout() 
    {
        super(10);
        setPadding(new Insets(10));

        // **Input Mode Dropdown**
        Label inputModeLabel = new Label("Input Mode:");
        inputModeLabel.getStyleClass().add("settings-label");

        ComboBox<String> inputModeDropdown = new ComboBox<>();
        inputModeDropdown.getStyleClass().add("keybind-box");
        inputModeDropdown.setMaxWidth(Double.MAX_VALUE);
        inputModeDropdown.getItems().addAll("Voice Activity", "Push to Talk");
        inputModeDropdown.setValue(SettingsManager.getInstance().getSetting("inputMode"));
        inputModeDropdown.setOnAction(e -> 
        {
            SettingsManager.getInstance().updateSetting("inputMode", inputModeDropdown.getValue());
            AudioRecorder.getInstance().updateRecordingState();
        });

        // **Keybind Controls**
        KeybindControl pushToTalkKey = new KeybindControl("Push to Talk Key", "pushToTalkKey");
        KeybindControl keywordActivationKey = new KeybindControl("Keyword Activation Key", "keywordActivationKey");

        // **Keywords Section**
        Label keywordsLabel = new Label("Voice Keywords:");
        keywordsLabel.getStyleClass().add("settings-label");

        ListView<HBox> keywordList = new ListView<>();
        keywordList.setId("keywords-list");
        updateKeywordList(keywordList);

        Button addKeywordButton = new Button("Add Keyword");
        addKeywordButton.getStyleClass().add("keybind-button");
        addKeywordButton.setOnAction(e -> showKeywordModal(null, null, null, SettingsManager.getInstance().getKeywords().keySet(),
        newKeyItem -> 
        {
            SettingsManager.getInstance().addKeyword(newKeyItem.getKeyword(), newKeyItem);
            updateKeywordList(keywordList);
        }));

        // Invisible spacer to push items to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Layout for keyword label + add button
        HBox keywordHeader = new HBox(10, keywordsLabel, spacer, addKeywordButton);
        keywordHeader.setSpacing(10);

        // **Scroll Container for Keywords**
        VBox keywordSection = new VBox(5, keywordHeader, keywordList);
        keywordSection.setPadding(new Insets(5));

        // **Main Settings Layout**
        VBox settingsContent = new VBox(15, 
            inputModeLabel, inputModeDropdown, 
            pushToTalkKey, keywordActivationKey, 
            keywordSection
        );

        // **Make the Settings Page Scrollable**
        ScrollPane scrollPane = new ScrollPane(settingsContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
        
        getChildren().addAll(scrollPane);
    }

    private void updateKeywordList(ListView<HBox> keywordList) 
    {
        keywordList.getItems().clear();

        for (Map.Entry<String, KeywordItem> entry : SettingsManager.getInstance().getKeywords().entrySet()) 
        {
            String keyword = entry.getKey();
            String type = entry.getValue().getType();
            String value = entry.getValue().getValue();

            // Left Side (Keyword + Type + Value)
            Label keywordLabel = new Label(keyword + " (" + type + ")");
            keywordLabel.getStyleClass().add("keyword-label");

            Label valueLabel = new Label(value);
            valueLabel.getStyleClass().add("value-label");

            VBox leftSide = new VBox(5, keywordLabel, valueLabel);
            leftSide.setAlignment(Pos.CENTER_LEFT);

            // Right Side (Edit & Delete Buttons)
            IconButton editButton = new IconButton("/icons/edit.png", 18, 18, () -> 
            {
                showKeywordModal(keyword, type, value, SettingsManager.getInstance().getKeywords().keySet(),
                newKeyItem -> 
                {
                    SettingsManager.getInstance().addKeyword(newKeyItem.getKeyword(), newKeyItem);
                    updateKeywordList(keywordList);
                });
            });

            IconButton deleteButton = new IconButton("/icons/delete.png", 18, 18, () -> 
            {
                showDeleteConfirmation(keyword, () -> 
                {
                    SettingsManager.getInstance().removeKeyword(keyword);
                    updateKeywordList(keywordList);
                });
            });

            VBox rightSide = new VBox(5, editButton, deleteButton);
            rightSide.setAlignment(Pos.CENTER_RIGHT);

            // Invisible spacer to push items to the right
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Combine into HBox
            HBox keywordItem = new HBox(10, leftSide, spacer, rightSide);
            keywordItem.setAlignment(Pos.CENTER_LEFT);
            keywordItem.setPadding(new Insets(5));
            keywordItem.getStyleClass().add("keyword-item");

            keywordList.getItems().add(keywordItem);
        }
    }

    private void showKeywordModal(String existingKeyword, String existingType, String existingValue, 
        Set<String> existingKeywords, Consumer<KeywordItem> onSave) 
    {
        Stage owner = SceneManager.getInstance().getPrimaryStage();
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initStyle(StageStyle.UNDECORATED);
        modalStage.initOwner(owner);
        
        // Form fields
        Label keywordFieldLabel = new Label("Keyword Name:");
        keywordFieldLabel.getStyleClass().add("settings-label");

        HBox keywordFieldBox = new HBox();
        keywordFieldBox.getStyleClass().add("keybind-box");
        
        TextField keywordField = new TextField(existingKeyword != null ? existingKeyword : "");
        keywordField.getStyleClass().add("keybind-label");

        HBox.setHgrow(keywordField, Priority.ALWAYS);
        keywordField.setMaxWidth(Double.MAX_VALUE);
        keywordFieldBox.getChildren().add(keywordField);

        if (existingKeyword != null && existingKeyword.length() > 0) 
        {
            keywordField.setEditable(false);
            keywordField.setStyle("-fx-opacity: 0.7; -fx-background-color: rgba(255, 255, 255, 0.2);"); 
        }

        Label keywordErrorLabel = new Label();
        keywordErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        keywordErrorLabel.setVisible(false);
        keywordErrorLabel.setManaged(false);

        VBox keywordBox = new VBox(5, keywordFieldLabel, keywordFieldBox, keywordErrorLabel);

        Label typeChoiceBoxLabel = new Label("Keyword Type:");
        typeChoiceBoxLabel.getStyleClass().add("settings-label");

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getStyleClass().add("keybind-box");
        typeComboBox.setMinWidth(owner.getWidth());
        typeComboBox.getItems().addAll("Execute", "KeyPress", "Typing");
        typeComboBox.setValue(existingType != null ? existingType : "Typing");

        Label valueFieldLabel = new Label("Command or File Path:");
        valueFieldLabel.getStyleClass().add("settings-label");

        // --- FIELD 1 --- Execute TextField + file picker
        HBox executeFieldBox = new HBox();
        executeFieldBox.setSpacing(5);
        executeFieldBox.setAlignment(Pos.CENTER_LEFT);
        executeFieldBox.getStyleClass().add("keybind-box");

        TextField executeField = new TextField();
        executeField.setEditable(false);
        executeField.getStyleClass().add("keybind-label");

        Button selectFileButton = new Button("Browse");
        selectFileButton.getStyleClass().add("keybind-button");
        selectFileButton.setOnAction(e -> 
        {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(modalStage);
            if (selectedFile != null) executeField.setText(selectedFile.getAbsolutePath());
        });

        HBox.setHgrow(executeField, Priority.ALWAYS);
        executeField.setMaxWidth(Double.MAX_VALUE);
        executeFieldBox.getChildren().addAll(executeField, selectFileButton);

        // --- FIELD 2 --- KeyPress TextField (listens to keys)
        HBox keyPressFieldBox = new HBox();
        keyPressFieldBox.setSpacing(5);
        keyPressFieldBox.setAlignment(Pos.CENTER_LEFT);
        keyPressFieldBox.getStyleClass().add("keybind-box");

        TextField keyPressField = new TextField();
        keyPressField.setEditable(false);
        keyPressField.getStyleClass().add("keybind-label");

        Button editKeyButton = new Button("Edit Keybind");
        editKeyButton.getStyleClass().add("keybind-button");
        editKeyButton.setOnAction(e -> 
        {
            editKeyButton.setText("Press any key...");

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
                        keyPressField.setText(newKey);
                    }

                    // Restore button text and remove event handler
                    editKeyButton.setText("Edit Keybind");
                    editKeyButton.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, this);
                }
            };

            // Add the event handler to listen for key presses
            editKeyButton.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyListener);
        });

        HBox.setHgrow(keyPressField, Priority.ALWAYS);
        keyPressField.setMaxWidth(Double.MAX_VALUE);
        keyPressFieldBox.getChildren().addAll(keyPressField, editKeyButton);

        // --- FIELD 3 --- Typing TextField (normal)
        HBox typingFieldBox = new HBox();
        typingFieldBox.getStyleClass().add("keybind-box");
        
        TextField typingField = new TextField();
        typingField.getStyleClass().add("keybind-label");

        HBox.setHgrow(typingField, Priority.ALWAYS);
        typingField.setMaxWidth(Double.MAX_VALUE);
        typingFieldBox.getChildren().add(typingField);

        Label valueErrorLabel = new Label();
        valueErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        valueErrorLabel.setVisible(false);
        valueErrorLabel.setManaged(false);

        VBox valueContainer = new VBox(5, valueFieldLabel, executeFieldBox, keyPressFieldBox, typingFieldBox, valueErrorLabel);
        TextField startingFieldBox = switch (typeComboBox.getValue()) 
        {
            case "Execute" -> executeField;
            case "KeyPress" -> keyPressField;
            case "Typing" -> typingField;
            default -> null;
        };
        startingFieldBox.setText(existingValue != null ? existingValue : "");

        // --- Visibility Control ---
        Runnable clearAllValidationErrors = () -> 
        {
            clearValidationError(keywordFieldBox, keywordErrorLabel, modalStage);
            clearValidationError(executeFieldBox, valueErrorLabel, modalStage);
            clearValidationError(keyPressFieldBox, valueErrorLabel, modalStage);
            clearValidationError(typingFieldBox, valueErrorLabel, modalStage);
        };

        Runnable updateValueFieldUI = () -> 
        {
            String selected = typeComboBox.getValue();
            executeFieldBox.setVisible("Execute".equals(selected));
            keyPressFieldBox.setVisible("KeyPress".equals(selected));
            typingFieldBox.setVisible("Typing".equals(selected));

            executeFieldBox.setManaged("Execute".equals(selected));
            keyPressFieldBox.setManaged("KeyPress".equals(selected));
            typingFieldBox.setManaged("Typing".equals(selected));

            switch (selected) 
            {
                case "Execute" -> valueFieldLabel.setText("Select a file:");
                case "KeyPress" -> valueFieldLabel.setText("Press a key:");
                case "Typing" -> valueFieldLabel.setText("Enter text:");
            }
        };
        
        updateValueFieldUI.run();
        typeComboBox.setOnAction(e -> 
        {
            clearAllValidationErrors.run();
            updateValueFieldUI.run();
        });

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
        saveButton.setOnAction(e -> 
        {
            boolean isValid = true;
            String keyword = keywordField.getText().trim();
            String type = typeComboBox.getValue();
            String value = switch (type) 
            {
                case "Execute" -> executeField.getText().trim();
                case "KeyPress" -> keyPressField.getText().trim();
                case "Typing"  -> typingField.getText().trim();
                default        -> "";
            };
            
            if (keyword.isEmpty()) 
            {
                showValidationError(keywordFieldBox, keywordErrorLabel, "Required field", modalStage);
                isValid = false;
            }
            else if (existingKeywords.contains(keyword) && existingKeyword == null) 
            {
                showValidationError(keywordFieldBox, keywordErrorLabel, "Keyword already exists", modalStage);
                isValid = false;
            }
            else
            {
                clearValidationError(keywordFieldBox, keywordErrorLabel, modalStage);
            }

            HBox activeFieldBox = switch (type) 
            {
                case "Execute" -> executeFieldBox;
                case "KeyPress" -> keyPressFieldBox;
                case "Typing" -> typingFieldBox;
                default -> null;
            };
            
            if (activeFieldBox != null) 
            {
                if (value.isEmpty()) 
                {
                    showValidationError(activeFieldBox, valueErrorLabel, "Required field", modalStage);
                    isValid = false;
                } 
                else 
                {
                    clearValidationError(activeFieldBox, valueErrorLabel, modalStage);
                }
            }

            if(isValid)
            {
                onSave.accept(new KeywordItem(keyword, type, value)); 
                modalStage.close();
            }
        });
        cancelButton.setOnAction(e -> modalStage.close());

        VBox layout = new VBox
        (
            10, keywordBox, typeChoiceBoxLabel, typeComboBox, 
            valueContainer,new HBox(10, saveButton, cancelButton
        ));
        layout.setMinWidth(owner.getWidth());
        layout.setStyle("-fx-background-color: #2e2e2e; -fx-padding: 20px; -fx-border-color: #333; -fx-border-width: 5px;");

        Scene scene = new Scene(layout);
        modalStage.setScene(scene);
        modalStage.getScene().getStylesheets().add(SceneManager.getStylesheet());
        modalStage.setOnShown(e -> 
        {
            double centerX = owner.getX() + (owner.getWidth() / 2) - (modalStage.getWidth() / 2);
            double centerY = owner.getY() + (owner.getHeight() / 2) - (modalStage.getHeight() / 2);
            modalStage.setX(centerX);
            modalStage.setY(centerY);
        });
        modalStage.showAndWait();
    }

    private void showValidationError(HBox field, Label errorLabel, String message, Stage modalStage) 
    {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        modalStage.sizeToScene();
    }

    private void clearValidationError(HBox field, Label errorLabel, Stage modalStage) 
    {
        field.setStyle(""); 
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        modalStage.sizeToScene();
    }

    private void showDeleteConfirmation(String keyword, Runnable onConfirm) 
    {
        Stage owner = SceneManager.getInstance().getPrimaryStage();
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.initOwner(owner);

        Label message = new Label("Are you sure you want to delete\nKeyword: " + keyword + "?");
        message.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        Button confirmButton = new Button("Delete");
        confirmButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        confirmButton.setOnAction(e -> {
            onConfirm.run();
            modal.close();
        });
    
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> modal.close());
    
        HBox buttonBox = new HBox(10, cancelButton, confirmButton);
        buttonBox.setAlignment(Pos.CENTER);
    
        VBox layout = new VBox(15, message, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2e2e2e; -fx-padding: 20px; -fx-border-color: #333; -fx-border-width: 5px;");
        
        Scene scene = new Scene(layout);
        modal.setScene(scene);
        modal.setOnShown(e -> 
        {
            double centerX = owner.getX() + (owner.getWidth() / 2) - (modal.getWidth() / 2);
            double centerY = owner.getY() + (owner.getHeight() / 2) - (modal.getHeight() / 2);
            modal.setX(centerX);
            modal.setY(centerY);
        });
        modal.showAndWait();
    }    
}
