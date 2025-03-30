package com.voice_to_text.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.voice_to_text.components.TitleBar;
import com.voice_to_text.layouts.HomeLayout;
import com.voice_to_text.layouts.SettingsLayout;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SceneManager 
{
    private static SceneManager instance;
    private static Stage primaryStage;
    private static TitleBar titleBar;
    private static final Map<String, Parent> layouts = new HashMap<>();

    private SceneManager() 
    {
    } 

    public static SceneManager getInstance() 
    {
        if (instance == null) 
        {
            instance = new SceneManager();
        }

        return instance;
    }

    public void init(Stage stage) 
    {
        primaryStage = stage;
        titleBar = new TitleBar(primaryStage);
        layouts.put("home", new HomeLayout());
    }

    public Stage getPrimaryStage()
    {
        return primaryStage;
    }

    public static String getStylesheet() 
    {
        return SceneManager.class.getResource("/styles/styles.css").toExternalForm();
    }
    

    public void loadMainApp() 
    {
        switchScene("home", HomeLayout::new);
    }

    public void loadSettingsPage() 
    {
        switchScene("settings", SettingsLayout::new);
    }

    private void switchScene(String sceneKey, Supplier<Parent> layoutSupplier) 
    {
        if (!layouts.containsKey(sceneKey)) 
        {
            layouts.put(sceneKey, layoutSupplier.get());
        }
    
        primaryStage.setScene(createSceneWithTitleBar(layouts.get(sceneKey)));
    }

    private Scene createSceneWithTitleBar(Parent layout) 
    {
        VBox root = new VBox();
        root.setStyle
        (
            "-fx-background-color: #1e2124; " +
            "-fx-padding: 5px; " +
            "-fx-background-radius: 5px; " +
            "-fx-border-radius: 5px; " +      
            "-fx-border-color: #333; " +       
            "-fx-border-width: 2px;"          
        );

        root.getChildren().add(titleBar);
        root.getChildren().add(layout);

        Scene scene = new Scene(root, 400, 525);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getStylesheet());
        return scene;
    }
}
