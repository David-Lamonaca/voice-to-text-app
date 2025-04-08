package com.voice_to_text.managers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.voice_to_text.KeywordItem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SettingsManager 
{
    private static final Path SETTINGS_FILE = Paths.get(System.getProperty("user.home"), ".voicecontrol", "settings.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private static SettingsManager instance;
    private Settings settings;

    private SettingsManager() 
    {
        loadSettings();
    }

    public static synchronized SettingsManager getInstance() 
    {
        if (instance == null) 
        {
            instance = new SettingsManager();
        }

        return instance;
    }

    public static class Settings 
    {
        private String inputMode = "Voice Activity";
        private String pushToTalkKey = "CONTROL";
        private String keywordActivationKey = "SHIFT";
        private final Map<String, KeywordItem> keywords = new HashMap<>();

        private transient StringProperty inputModeProperty;

        public Settings() 
        {
        }
    }

    private void loadSettings() 
    {
        try (Reader reader = Files.newBufferedReader(SETTINGS_FILE)) 
        {
            Type settingsType = new TypeToken<Settings>() {}.getType();
            settings = GSON.fromJson(reader, settingsType);
        } 
        catch (FileNotFoundException e) 
        {
            settings = new Settings();
            saveSettings();
        } 
        catch (IOException e) 
        {
            settings = new Settings();
            saveSettings();
        }
    }

    public void saveSettings() 
    {
        try 
        {
            Files.createDirectories(SETTINGS_FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(SETTINGS_FILE)) 
            {
                GSON.toJson(settings, writer);
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public Settings getSettings() 
    {
        return settings;
    }

    public String getSetting(String key) 
    {
        String value = "DEFAULT";
        switch (key) 
        {
            case "inputMode" -> value = settings.inputMode;
            case "pushToTalkKey" -> value = settings.pushToTalkKey;
            case "keywordActivationKey" -> value = settings.keywordActivationKey;
        }
        return value;
    }

    public void updateSetting(String key, String value) 
    {
        switch (key) 
        {
            case "inputMode" ->  
            {
                settings.inputMode = value;
                if (value != null) settings.inputModeProperty.set(value);
            }
            case "pushToTalkKey" -> settings.pushToTalkKey = value;
            case "keywordActivationKey" -> settings.keywordActivationKey = value;
        }
        saveSettings();
    }

    public Map<String, KeywordItem> getKeywords() 
    {
        return settings.keywords;
    }

    public void addKeyword(String word, KeywordItem values) 
    {
        settings.keywords.put(word.toLowerCase(), values);
        saveSettings();
    }

    public void removeKeyword(String word) 
    {
        settings.keywords.remove(word);
        saveSettings();
    }

    public StringProperty inputModeProperty()
    {
        if (settings.inputModeProperty == null)
        {
            settings.inputModeProperty = new SimpleStringProperty(settings.inputMode);
            settings.inputModeProperty.addListener((obs, oldVal, newVal) -> settings.inputMode = newVal);
        }
        return settings.inputModeProperty;
    }
}
