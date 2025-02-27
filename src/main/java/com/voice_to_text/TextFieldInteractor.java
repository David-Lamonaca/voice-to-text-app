package com.voice_to_text;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class TextFieldInteractor 
{
    private Robot robot;
    private static final Map<Character, Integer> SPECIAL_CHARACTERS = new HashMap<>();
    private StringBuilder typedText = new StringBuilder(); // Track typed words

    static 
    {
        SPECIAL_CHARACTERS.put(' ', KeyEvent.VK_SPACE);
        SPECIAL_CHARACTERS.put('\n', KeyEvent.VK_ENTER);
        SPECIAL_CHARACTERS.put('\t', KeyEvent.VK_TAB);
        SPECIAL_CHARACTERS.put('!', KeyEvent.VK_1);
        SPECIAL_CHARACTERS.put('@', KeyEvent.VK_2);
        SPECIAL_CHARACTERS.put('#', KeyEvent.VK_3);
        SPECIAL_CHARACTERS.put('$', KeyEvent.VK_4);
        SPECIAL_CHARACTERS.put('%', KeyEvent.VK_5);
        SPECIAL_CHARACTERS.put('^', KeyEvent.VK_6);
        SPECIAL_CHARACTERS.put('&', KeyEvent.VK_7);
        SPECIAL_CHARACTERS.put('*', KeyEvent.VK_8);
        SPECIAL_CHARACTERS.put('(', KeyEvent.VK_9);
        SPECIAL_CHARACTERS.put(')', KeyEvent.VK_0);
        SPECIAL_CHARACTERS.put('-', KeyEvent.VK_MINUS);
        SPECIAL_CHARACTERS.put('_', KeyEvent.VK_MINUS);
        SPECIAL_CHARACTERS.put('=', KeyEvent.VK_EQUALS);
        SPECIAL_CHARACTERS.put('+', KeyEvent.VK_EQUALS);
        SPECIAL_CHARACTERS.put('[', KeyEvent.VK_OPEN_BRACKET);
        SPECIAL_CHARACTERS.put(']', KeyEvent.VK_CLOSE_BRACKET);
        SPECIAL_CHARACTERS.put('{', KeyEvent.VK_OPEN_BRACKET);
        SPECIAL_CHARACTERS.put('}', KeyEvent.VK_CLOSE_BRACKET);
        SPECIAL_CHARACTERS.put(';', KeyEvent.VK_SEMICOLON);
        SPECIAL_CHARACTERS.put(':', KeyEvent.VK_SEMICOLON);
        SPECIAL_CHARACTERS.put('\'', KeyEvent.VK_QUOTE);
        SPECIAL_CHARACTERS.put('"', KeyEvent.VK_QUOTE);
        SPECIAL_CHARACTERS.put(',', KeyEvent.VK_COMMA);
        SPECIAL_CHARACTERS.put('.', KeyEvent.VK_PERIOD);
        SPECIAL_CHARACTERS.put('/', KeyEvent.VK_SLASH);
        SPECIAL_CHARACTERS.put('?', KeyEvent.VK_SLASH);
        SPECIAL_CHARACTERS.put('\\', KeyEvent.VK_BACK_SLASH);
        SPECIAL_CHARACTERS.put('|', KeyEvent.VK_BACK_SLASH);
    }

    public TextFieldInteractor() 
    {
        try 
        {
            robot = new Robot();
        } 
        catch (AWTException e) 
        {
            e.printStackTrace();
        }
    }

    public void typeText(String text) 
    {
        String[] words = text.split("\\s+");

        for (String word : words) 
        {
            if (typedText.length() > 0) 
            {
                typeCharacter(' ');
            }

            for (char c : word.toCharArray()) 
            {
                typeCharacter(c);
            }
            typedText.append(word); 
        }
    }

    private void typeCharacter(char c) 
    {
        if (SPECIAL_CHARACTERS.containsKey(c)) 
        {
            int keyCode = SPECIAL_CHARACTERS.get(c);
            boolean shiftRequired = isShiftRequired(c);

            if (shiftRequired) robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
            if (shiftRequired) robot.keyRelease(KeyEvent.VK_SHIFT);
        } 
        else 
        {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (KeyEvent.CHAR_UNDEFINED == keyCode) 
            {
                System.err.println("Unsupported character: " + c);
                return;
            }

            if (Character.isUpperCase(c)) robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
            if (Character.isUpperCase(c)) robot.keyRelease(KeyEvent.VK_SHIFT);
        }
    }

    private boolean isShiftRequired(char c) 
    {
        return "!@#$%^&*()_+{}:\"<>?~|".indexOf(c) != -1;
    }

    public void resetTypedText() 
    {
        typedText.setLength(0);
    }
}
