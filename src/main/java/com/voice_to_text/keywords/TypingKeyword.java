package com.voice_to_text.keywords;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class TypingKeyword implements KeywordStrategy 
{
    private final Robot robot;
    private static final Map<Character, Integer> keyMap = new HashMap<>();
    private static final String shiftRequiredChars = ":!@#$%^&*()_+{}|<>?\"";

    static 
    {
        // Letters
        for (char c = 'a'; c <= 'z'; c++) keyMap.put(c, KeyEvent.VK_A + (c - 'a'));
        for (char c = 'A'; c <= 'Z'; c++) keyMap.put(c, KeyEvent.VK_A + (c - 'A'));

        // Numbers
        for (char c = '0'; c <= '9'; c++) keyMap.put(c, KeyEvent.VK_0 + (c - '0'));

        // Common keys
        keyMap.put(' ', KeyEvent.VK_SPACE);
        keyMap.put('.', KeyEvent.VK_PERIOD);
        keyMap.put(',', KeyEvent.VK_COMMA);
        keyMap.put('-', KeyEvent.VK_MINUS);
        keyMap.put('=', KeyEvent.VK_EQUALS);
        keyMap.put(';', KeyEvent.VK_SEMICOLON);
        keyMap.put('/', KeyEvent.VK_SLASH);
        keyMap.put('\\', KeyEvent.VK_BACK_SLASH);
        keyMap.put('\'', KeyEvent.VK_QUOTE);
        keyMap.put('[', KeyEvent.VK_OPEN_BRACKET);
        keyMap.put(']', KeyEvent.VK_CLOSE_BRACKET);
        keyMap.put('`', KeyEvent.VK_BACK_QUOTE);

        // Shift-required characters
        keyMap.put(':', KeyEvent.VK_SEMICOLON);
        keyMap.put('?', KeyEvent.VK_SLASH);
        keyMap.put('"', KeyEvent.VK_QUOTE);
        keyMap.put('{', KeyEvent.VK_OPEN_BRACKET);
        keyMap.put('}', KeyEvent.VK_CLOSE_BRACKET);
        keyMap.put('|', KeyEvent.VK_BACK_SLASH);
        keyMap.put('~', KeyEvent.VK_BACK_QUOTE);
        keyMap.put('!', KeyEvent.VK_1);
        keyMap.put('@', KeyEvent.VK_2);
        keyMap.put('#', KeyEvent.VK_3);
        keyMap.put('$', KeyEvent.VK_4);
        keyMap.put('%', KeyEvent.VK_5);
        keyMap.put('^', KeyEvent.VK_6);
        keyMap.put('&', KeyEvent.VK_7);
        keyMap.put('*', KeyEvent.VK_8);
        keyMap.put('(', KeyEvent.VK_9);
        keyMap.put(')', KeyEvent.VK_0);
        keyMap.put('_', KeyEvent.VK_MINUS);
        keyMap.put('+', KeyEvent.VK_EQUALS);
    }

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
            typeCharacter(c);
        }
    }

    private void typeCharacter(char c) 
    {
        boolean shiftRequired = Character.isUpperCase(c) || shiftRequiredChars.indexOf(c) != -1;
        Integer keyCode = keyMap.get(c);

        if (keyCode == null) 
        {
            System.out.println("Unsupported character: " + c);
            return;
        }

        try 
        {
            if (shiftRequired) 
            {
                robot.keyPress(KeyEvent.VK_SHIFT);
            }

            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);

            if (shiftRequired) 
            {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}
