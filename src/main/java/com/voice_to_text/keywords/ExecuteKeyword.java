package com.voice_to_text.keywords;

import java.io.IOException;

public class ExecuteKeyword implements KeywordStrategy 
{
    @Override
    public void execute(String appPath) 
    {
        try 
        {
            System.out.println("execute() " + appPath);
            new ProcessBuilder(appPath).start();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
