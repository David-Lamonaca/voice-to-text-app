package com.voice_to_text.keywords;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class ExecuteKeyword implements KeywordStrategy 
{
    @Override
    public void execute(String appPath) 
    {
         File file = new File(appPath);
        if (!file.exists()) 
        {
            System.out.println("File/Application not found: " + appPath);
            return;
        }

        try 
        {
            if (file.isFile()) 
            {
                Desktop.getDesktop().open(file);
            } 
            else 
            {
                new ProcessBuilder(appPath).start();
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
