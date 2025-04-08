package com.voice_to_text.listeners;

import java.io.IOException;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.voice_to_text.AudioRecorder;
import com.voice_to_text.Main;
import com.voice_to_text.managers.SettingsManager;

public class GlobalKeyListener implements NativeKeyListener 
{
    public GlobalKeyListener() 
    {
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
                return;
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
                return;
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

    public static void register()
    {
        try 
        {
            System.setProperty("jnativehook.lib.name", "JNativeHook");
            System.setProperty("jnativehook.lib.path", getNativeLibDir());
            
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
        } 
        catch (SecurityException | NativeHookException e) 
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

    private static String getNativeLibDir() 
    {
        // Create a writable temp folder in AppData
        String tmpPath = System.getProperty("user.home") + "/.voicecontrol/native/";
        java.io.File nativeDir = new java.io.File(tmpPath);
        nativeDir.mkdirs();

        // Copy the DLL from resources into that folder
        try (java.io.InputStream in = Main.class.getResourceAsStream("JNativeHook.dll")) 
        {
            java.nio.file.Path outPath = java.nio.file.Paths.get(tmpPath + "JNativeHook.dll");
            java.nio.file.Files.copy(in, outPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } 
        catch (NullPointerException | IOException e) 
        {
        }

        return tmpPath;
    }

}
