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
    private static final String RESOURCE_PATH = "/org/Blankk3/tasks.json";

    private final Gson gson = new GsonBuilder().create();

    public List<TaskInfo> loadTasks()
    {
        InputStream in = getClass().getResourceAsStream(RESOURCE_PATH);
        if (in == null)
        {
            System.out.println("tasks.json not found at: " + RESOURCE_PATH);
            return Collections.emptyList();
        }

        try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8))
        {
            TaskData data = gson.fromJson(reader, TaskData.class);
            System.out.println("Loaded tasks: " + (data == null || data.getTasks() == null ? "null" : data.getTasks().size()));

            if (data == null || data.getTasks() == null)
            {
                return Collections.emptyList();
            }

            return data.getTasks();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}