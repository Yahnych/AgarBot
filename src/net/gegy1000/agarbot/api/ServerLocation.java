package net.gegy1000.agarbot.api;

public enum ServerLocation
{
    LONDON("EU", "London"), ATLANTA("US", "Atlanta"), FREMONT("US", "Fremont"), BRAZIL("BR", "Brazil"), TOKYO("JP", "Tokyo"), CHINA("CN", "China"), SINGAPORE("SG", "Singapore");

    String region;
    String name;

    ServerLocation(String region, String name)
    {
        this.region = region;
        this.name = name;
    }

    public String getFriendlyName()
    {
        return region + "-" + name;
    }
}
