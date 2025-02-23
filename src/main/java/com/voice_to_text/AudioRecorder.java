package com.voice_to_text;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder {
    private TargetDataLine targetDataLine;
    private boolean isRecording;
    private AudioDataListener audioDataListener;

    public interface AudioDataListener {
        void onAudioData(byte[] audioData);
    }

    public void setAudioDataListener(AudioDataListener listener) {
        this.audioDataListener = listener;
    }

    public void startRecording() {
        try {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(format);
            targetDataLine.start();

            isRecording = true;

            new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (isRecording) {
                    int count = targetDataLine.read(buffer, 0, buffer.length);
                    if (count > 0 && audioDataListener != null) {
                        audioDataListener.onAudioData(buffer);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        isRecording = false;
        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
        }
    }
}