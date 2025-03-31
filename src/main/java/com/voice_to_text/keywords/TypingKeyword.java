package com.voice_to_text.keywords;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class TypingKeyword implements KeywordStrategy 
{
    private final Robot robot;

    public TypingKeyword() 
    {
        try 
        {
            robot = new Robot();
        } 
        catch (AWTException e) 
        {
            throw new RuntimeException("Failed to initialize Robot", e);
        }
    }

    @Override
    public void execute(String text) 
    {
        for (char c : text.toCharArray()) 
        {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (keyCode != KeyEvent.VK_UNDEFINED) 
            {
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
            }
        }
    }
}
