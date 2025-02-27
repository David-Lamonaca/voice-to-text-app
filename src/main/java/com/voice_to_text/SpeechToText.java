package com.voice_to_text;

import java.io.IOException;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import com.google.gson.Gson;

public class SpeechToText 
{
    private final Model model;
    private final Recognizer recognizer;
    private final Gson gson;

    public SpeechToText(String modelPath) 
    {
        LibVosk.setLogLevel(LogLevel.WARNINGS);
        try 
        {
            model = new Model(modelPath);
            recognizer = new Recognizer(model, 16000);
            gson = new Gson();
        } 
        catch (IOException e) 
        {
            throw new RuntimeException("Failed to initialize Vosk model or recognizer", e);
        }
    }

    /**
     * Transcribes audio data and returns the recognized text.
     *
     * @param audioData The audio data to transcribe.
     * @return The recognized text, or an empty string if no text is recognized.
     */
    public String transcribe(byte[] audioData) 
    {
        recognizer.acceptWaveForm(audioData, audioData.length);
        String result = recognizer.getPartialResult(); 

        VoskResponse response = gson.fromJson(result, VoskResponse.class);
        String partialText = response.getPartial() != null ? response.getPartial() : "";
        return partialText;
    }

    /**
     * Resets the recognizer to clear the internal buffer.
     */
    public void resetRecognizer() 
    {
        recognizer.reset();
    }

    /**
     * Returns the final transcription result.
     *
     * @return The final recognized text.
     */
    public String getFinalResult() 
    {
        String result = recognizer.getFinalResult();

        VoskResponse response = gson.fromJson(result, VoskResponse.class);
        return response.getText() != null ? response.getText() : "";
    }
}