package net.gegy1000.agarbot.api;

public enum GameMode
{
    FFA(""), PARTY("party"), EXPERIMENTAL("experimental"), TEAMS("teams");

    String name;

    GameMode(String name)
    {
        this.name = name;
    }

    public String getFriendlyName()
    {
        return name;
    }
}
