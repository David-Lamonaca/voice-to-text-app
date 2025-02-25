package com.voice_to_text;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class TextFieldInteractor {
    private Robot robot;
    private static final Map<Character, Integer> SPECIAL_CHARACTERS = new HashMap<>();


    static {
        // Map special characters to their base keycodes and whether Shift is required
        SPECIAL_CHARACTERS.put(' ', KeyEvent.VK_SPACE);
        SPECIAL_CHARACTERS.put('\n', KeyEvent.VK_ENTER);
        SPECIAL_CHARACTERS.put('\t', KeyEvent.VK_TAB);
        SPECIAL_CHARACTERS.put('!', KeyEvent.VK_1); // Shift + 1
        SPECIAL_CHARACTERS.put('@', KeyEvent.VK_2); // Shift + 2
        SPECIAL_CHARACTERS.put('#', KeyEvent.VK_3); // Shift + 3
        SPECIAL_CHARACTERS.put('$', KeyEvent.VK_4); // Shift + 4
        SPECIAL_CHARACTERS.put('%', KeyEvent.VK_5); // Shift + 5
        SPECIAL_CHARACTERS.put('^', KeyEvent.VK_6); // Shift + 6
        SPECIAL_CHARACTERS.put('&', KeyEvent.VK_7); // Shift + 7
        SPECIAL_CHARACTERS.put('*', KeyEvent.VK_8); // Shift + 8
        SPECIAL_CHARACTERS.put('(', KeyEvent.VK_9); // Shift + 9
        SPECIAL_CHARACTERS.put(')', KeyEvent.VK_0); // Shift + 0
        SPECIAL_CHARACTERS.put('-', KeyEvent.VK_MINUS);
        SPECIAL_CHARACTERS.put('_', KeyEvent.VK_MINUS); // Shift + -
        SPECIAL_CHARACTERS.put('=', KeyEvent.VK_EQUALS);
        SPECIAL_CHARACTERS.put('+', KeyEvent.VK_EQUALS); // Shift + =
        SPECIAL_CHARACTERS.put('[', KeyEvent.VK_OPEN_BRACKET);
        SPECIAL_CHARACTERS.put(']', KeyEvent.VK_CLOSE_BRACKET);
        SPECIAL_CHARACTERS.put('{', KeyEvent.VK_OPEN_BRACKET); // Shift + [
        SPECIAL_CHARACTERS.put('}', KeyEvent.VK_CLOSE_BRACKET); // Shift + ]
        SPECIAL_CHARACTERS.put(';', KeyEvent.VK_SEMICOLON);
        SPECIAL_CHARACTERS.put(':', KeyEvent.VK_SEMICOLON); // Shift + ;
        SPECIAL_CHARACTERS.put('\'', KeyEvent.VK_QUOTE);
        SPECIAL_CHARACTERS.put('"', KeyEvent.VK_QUOTE); // Shift + '
        SPECIAL_CHARACTERS.put(',', KeyEvent.VK_COMMA);
        SPECIAL_CHARACTERS.put('.', KeyEvent.VK_PERIOD);
        SPECIAL_CHARACTERS.put('/', KeyEvent.VK_SLASH);
        SPECIAL_CHARACTERS.put('?', KeyEvent.VK_SLASH); // Shift + /
        SPECIAL_CHARACTERS.put('\\', KeyEvent.VK_BACK_SLASH);
        SPECIAL_CHARACTERS.put('|', KeyEvent.VK_BACK_SLASH); // Shift + \
    }

    public TextFieldInteractor() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * Types the given text into the active text field.
     *
     * @param text The text to type.
     */
    public void typeText(String text) {
        for (char c : text.toCharArray()) {
            typeCharacter(c);
        }
    }

    /**
     * Types a single character into the active text field.
     *
     * @param c The character to type.
     */
    private void typeCharacter(char c) {
        System.out.println("Typing character: " + c); // Log the character
        if (SPECIAL_CHARACTERS.containsKey(c)) {
            // Handle special characters
            int keyCode = SPECIAL_CHARACTERS.get(c);
    
            // Check if Shift is required
            boolean shiftRequired = isShiftRequired(c);
    
            if (shiftRequired) {
                robot.keyPress(KeyEvent.VK_SHIFT);
            }
    
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
    
            if (shiftRequired) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
        } else {
            // Handle regular characters
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (KeyEvent.CHAR_UNDEFINED == keyCode) {
                System.err.println("Unsupported character: " + c);
                return; // Skip unsupported characters
            }
    
            // Handle uppercase letters
            if (Character.isUpperCase(c)) {
                robot.keyPress(KeyEvent.VK_SHIFT);
            }
    
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
    
            if (Character.isUpperCase(c)) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
        }
    }

    private boolean isShiftRequired(char c) {
        return "!@#$%^&*()_+{}:\"<>?~|".indexOf(c) != -1;
    }
}