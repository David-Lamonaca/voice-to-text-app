package com.voice_to_text;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import com.voice_to_text.managers.SettingsManager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class AudioRecorder 
{
    private static AudioRecorder instance;
    private TargetDataLine targetDataLine;
    private final BooleanProperty pushToTalkHeld = new SimpleBooleanProperty(false);
    private final BooleanProperty keywordActivationHeld  = new SimpleBooleanProperty(false);
    private AudioDataListener audioDataListener;

    public interface AudioDataListener 
    {
        void onAudioData(byte[] audioData);
    }

    private AudioRecorder() 
    {
    } 

    public static AudioRecorder getInstance() 
    {
        if (instance == null) 
        {
            instance = new AudioRecorder();
        }
        return instance;
    }

    public void setAudioDataListener(AudioDataListener listener) 
    {
        this.audioDataListener = listener;
    }

    public BooleanProperty pushToTalkHeldProperty() 
    {
        return pushToTalkHeld;
    }

    public boolean isPushToTalkHeld() 
    {
        return pushToTalkHeld.get();
    }

    public void setPushToTalkHeld(boolean value) 
    {
        pushToTalkHeld.set(value);
    }

    public BooleanProperty keywordActivationHeldProperty() 
    {
        return keywordActivationHeld;
    }

    public boolean isKeywordActivationHeld() 
    {
        return keywordActivationHeld.get();
    }

    public void setKeywordActivationHeld(boolean value) 
    {
        keywordActivationHeld.set(value);
    }

    public void updateRecordingState() 
    {
        if (isListening()) 
        {
            startRecording();
        } 
        else 
        {
            stopRecording();
        }
    }

    private void startRecording() 
    {
        if (targetDataLine != null && targetDataLine.isOpen()) 
        {
            return; 
        }
    
        try 
        {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(format);
            targetDataLine.start();
    
            new Thread(() -> 
            {
                byte[] buffer = new byte[4096];
                while (isListening()) 
                {
                    int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                    if (bytesRead > 0 && audioDataListener != null) 
                    {
                        byte[] audioData = new byte[bytesRead];
                        System.arraycopy(buffer, 0, audioData, 0, bytesRead);
                        audioDataListener.onAudioData(audioData);
                    }
                }
            }).start();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    private  void stopRecording() 
    {
        if (targetDataLine != null) 
        {
            targetDataLine.stop();
            targetDataLine.close();
        }
    }

    private boolean isListening() 
    {
        String inputModeValue = SettingsManager.getInstance().getSetting("inputMode");
        return inputModeValue.equals("Voice Activity") || 
            (inputModeValue.equals("Push to Talk") && pushToTalkHeld.get());
    }
}