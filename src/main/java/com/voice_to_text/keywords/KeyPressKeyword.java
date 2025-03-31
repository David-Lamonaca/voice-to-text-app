package com.voice_to_text.keywords;

import java.awt.Robot;
import java.awt.event.KeyEvent;

public class KeyPressKeyword implements KeywordStrategy 
{
    private final Robot robot;

    public KeyPressKeyword() 
    {
        try 
        {
            robot = new Robot();
        } 
        catch (Exception e) 
        {
            throw new RuntimeException("Failed to initialize Robot", e);
        }
    }

    @Override
    public void execute(String key) 
    {
        int keyCode = getKeyCodeFromString(key);
        if (keyCode != -1) 
        {
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        }
    }

    private int getKeyCodeFromString(String key) 
    {
        switch (key.toLowerCase()) 
        {
            case "enter": return KeyEvent.VK_ENTER;
            case "space": return KeyEvent.VK_SPACE;
            case "tab": return KeyEvent.VK_TAB;
            case "backspace": return KeyEvent.VK_BACK_SPACE;
            case "escape": return KeyEvent.VK_ESCAPE;
            case "ctrl": return KeyEvent.VK_CONTROL;
            case "shift": return KeyEvent.VK_SHIFT;
            case "alt": return KeyEvent.VK_ALT;
            default:
                 return (key.length() == 1) 
                    ? KeyEvent.getExtendedKeyCodeForChar(key.charAt(0))
                    : -1;
        }
    }
}
