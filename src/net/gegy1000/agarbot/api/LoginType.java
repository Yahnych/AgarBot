package net.gegy1000.agarbot.api;

public enum LoginType
{
    FACEBOOK, GOOGLE;

    public int getContextId()
    {
        return ordinal() + 1;
    }
}
