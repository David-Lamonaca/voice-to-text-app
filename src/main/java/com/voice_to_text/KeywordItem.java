package com.voice_to_text;

public class KeywordItem {
    private String keyword;
    private String type;
    private String value;

    public KeywordItem(String keyword, String type, String value) 
    {
        this.keyword = keyword;
        this.type = type;
        this.value = value;
    }

    public String getKeyword() 
    {
        return keyword;
    }

    public void setKeyword(String keyword) 
    {
        this.keyword = keyword;
    }

    public String getType() 
    {
        return type;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getValue() 
    {
        return value;
    }

    public void setValue(String value) 
    {
        this.value = value;
    }
}

