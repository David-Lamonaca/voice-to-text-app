package com.voice_to_text.managers;

import java.util.HashMap;
import java.util.Map;

import com.voice_to_text.KeywordItem;
import com.voice_to_text.keywords.ExecuteKeyword;
import com.voice_to_text.keywords.KeyPressKeyword;
import com.voice_to_text.keywords.KeywordStrategy;
import com.voice_to_text.keywords.TypingKeyword;

public class KeywordManager 
{
    private static KeywordManager instance;
    private final Map<String, KeywordStrategy> strategyMap;

    private KeywordManager() 
    {
        strategyMap = new HashMap<>();
        strategyMap.put("Execute", new ExecuteKeyword());
        strategyMap.put("KeyPress", new KeyPressKeyword());
        strategyMap.put("Typing", new TypingKeyword());
    }

    public static KeywordManager getInstance() 
    {
        if (instance == null) 
        {
            instance = new KeywordManager();
        }
        return instance;
    }

    public void executeKeyword(String spokenText) 
    {
        Map<String, KeywordItem> keywords = SettingsManager.getInstance().getKeywords();
        KeywordItem keywordItem = keywords.get(spokenText);
        if (keywordItem == null) 
        {
            return;
        }

        KeywordStrategy strategy = strategyMap.get(keywordItem.getType());
        if (strategy != null) 
        {
            strategy.execute(keywordItem.getValue());
        } 
    }
}
