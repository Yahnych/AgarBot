package net.gegy1000.agarbot.api;

public class ServerData
{
    private String ip;
    private String token;

    public ServerData(String ip, String token)
    {
        this.ip = ip;
        this.token = token;
    }

    public String getIp()
    {
        return ip;
    }

    public String getToken()
    {
        return token;
    }
}
