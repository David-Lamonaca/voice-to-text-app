package com.voice_to_text.components;

import com.jfoenix.controls.JFXButton;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconButton extends JFXButton 
{
    public IconButton(String iconPath, double width, double height, Runnable action) 
    {
        super("");
        this.getStyleClass().add("icon-button");

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitWidth(width);
        icon.setFitHeight(height);

        this.setGraphic(icon);
        this.setOnAction(e -> action.run());
    }
}
