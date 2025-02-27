package com.voice_to_text;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener 
{
    private final Main mainApp;

    public GlobalKeyListener(Main mainApp) 
    {
        this.mainApp = mainApp;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) 
    {
        if (e.getKeyCode() == NativeKeyEvent.VC_ENTER) 
        {
            mainApp.resetTypedText();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {}

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}

    public static void register(Main mainApp) {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF); // Disable unnecessary logs
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new GlobalKeyListener(mainApp));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
