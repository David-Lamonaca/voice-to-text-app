package com.voice_to_text;

import java.io.IOException;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

public class SpeechToText {
    private final Model model;
    private final Recognizer recognizer;

    public SpeechToText(String modelPath) {
        LibVosk.setLogLevel(LogLevel.WARNINGS);
        try {
            model = new Model(modelPath);
            recognizer = new Recognizer(model, 16000);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Vosk model or recognizer", e);
        }
    }

    public String transcribe(byte[] audioData) {
        recognizer.acceptWaveForm(audioData, audioData.length);
        return recognizer.getPartialResult();
    }

    public String getFinalResult() {
        return recognizer.getFinalResult();
    }
}