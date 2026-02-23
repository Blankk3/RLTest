package org.Blankk3;

import java.util.List;

public class TaskInfo
{
    private String name;
    private List<String> locations;
    private List<String> slayerMasters;
    private List<String> requiredItems;
    private List<String> bringItems;
    private List<String> drops;
    private String wiki;

    public String getName() { return name; }
    public List<String> getLocations() { return locations; }
    public List<String> getSlayerMasters() { return slayerMasters; }
    public List<String> getRequiredItems() { return requiredItems; }
    public List<String> getBringItems() { return bringItems; }
    public List<String> getDrops() { return drops; }
    public String getWiki() { return wiki; }
}