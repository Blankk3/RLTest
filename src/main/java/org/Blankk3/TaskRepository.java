package org.Blankk3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class TaskRepository
{
    private static final String RESOURCE_PATH = "tasks.json";

    private final Gson gson = new GsonBuilder().create();

    public List<TaskInfo> loadTasks()
    {
        // Looks for tasks.json in the same resources package as this class:
        // src/main/resources/org/Blankk3/tasks.json
        InputStream in = getClass().getResourceAsStream(RESOURCE_PATH);
        if (in == null)
        {
            // If this happens, resource path is wrong
            return Collections.emptyList();
        }

        try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8))
        {
            TaskData data = gson.fromJson(reader, TaskData.class);
            if (data == null || data.getTasks() == null)
            {
                return Collections.emptyList();
            }
            return data.getTasks();
        }
        catch (Exception e)
        {
            // For now just fail gracefully; later we can log in the plugin
            return Collections.emptyList();
        }
    }
}