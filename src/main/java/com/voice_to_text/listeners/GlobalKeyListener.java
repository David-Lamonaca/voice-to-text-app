package com.voice_to_text.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.voice_to_text.AudioRecorder;
import com.voice_to_text.Main;
import com.voice_to_text.managers.SettingsManager;

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
        String pttKey = SettingsManager.getInstance().getSetting("pushToTalkKey");
        if (pttKey != null) 
        {
            int pushToTalkKey = getKeyCodeFromName(pttKey);
            if (pushToTalkKey != -1 && e.getKeyCode() == pushToTalkKey) 
            {
                AudioRecorder.getInstance().setPushToTalkHeld(true);
                AudioRecorder.getInstance().updateRecordingState();
            }
        }

        String keyString = SettingsManager.getInstance().getSetting("keywordActivationKey");
        if (keyString != null) 
        {
            int activationKey = getKeyCodeFromName(keyString);
            if (activationKey != -1 && e.getKeyCode() == activationKey) 
            {
                AudioRecorder.getInstance().setKeywordActivationHeld(true);
            }
        }

    /*  if (e.getKeyCode() == NativeKeyEvent.VC_ENTER) 
        {
            mainApp.resetTypedText();
        } */
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) 
    {
        String pttKey = SettingsManager.getInstance().getSetting("pushToTalkKey");
        if (pttKey != null) 
        {
            int pushToTalkKey = getKeyCodeFromName(pttKey);
            if (pushToTalkKey != -1 && e.getKeyCode() == pushToTalkKey) 
            {
                AudioRecorder.getInstance().setPushToTalkHeld(false);
                AudioRecorder.getInstance().updateRecordingState();
            }
        }

        String keyString = SettingsManager.getInstance().getSetting("keywordActivationKey");
        if (keyString != null) 
        {
            int activationKey = getKeyCodeFromName(keyString);
            if (activationKey != -1 && e.getKeyCode() == activationKey) 
            {
                AudioRecorder.getInstance().setKeywordActivationHeld(false);
            }
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}

    public static void register(Main mainApp) 
    {
        try 
        {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF); 
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new GlobalKeyListener(mainApp));
        } 
        catch (NativeHookException | SecurityException e) 
        {
            e.printStackTrace();
        }
    }

    private int getKeyCodeFromName(String keyName) 
    {
        try 
        {
            return (int) NativeKeyEvent.class.getField("VC_" + keyName.toUpperCase()).get(null);
        } 
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) 
        {
            System.err.println("Invalid key name: " + keyName);
            return -1; 
        }
    }
}
