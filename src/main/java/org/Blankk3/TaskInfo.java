package org.Blankk3;

import java.util.List;
import org.Blankk3.TaskItem;

public class TaskInfo
{
    private String name;
    private List<String> locations;
    private List<String> slayerMasters;
    private List<String> requiredItems;
    private List<TaskItem> bringItems;
    private List<TaskItem> drops;
    private String wiki;

    public String getName() { return name; }
    public List<String> getLocations() { return locations; }
    public List<String> getSlayerMasters() { return slayerMasters; }
    public List<String> getRequiredItems() { return requiredItems; }
    public List<TaskItem> getBringItems() { return bringItems; }
    public List<TaskItem> getDrops() { return drops; }
    public String getWiki() { return wiki; }
}