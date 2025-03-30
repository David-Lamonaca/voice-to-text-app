package com.voice_to_text;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class AudioRecorder 
{
    private static AudioRecorder instance;
    private TargetDataLine targetDataLine;
    private final BooleanProperty pushToTalkHeld = new SimpleBooleanProperty(false);
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

    public void setAudioDataListener(AudioDataListener listener) 
    {
        this.audioDataListener = listener;
    }

    public void startRecording() 
    {
        try 
        {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(format);
            targetDataLine.start();
    
            pushToTalkHeld.set(true);
    
            new Thread(() -> 
            {
                byte[] buffer = new byte[4096];
                while (pushToTalkHeld.get()) 
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
    

    public void stopRecording() {
        pushToTalkHeld.set(false);
        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
        }
    }
}