package ru.aptu.warehouse.tester.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class LaunchTaskList {
    private List<Task> tasks = new ArrayList<Task>();

    public LaunchTaskList() {
    }

    public LaunchTaskList(List<Task> tasks) {
        for (Task task: tasks) {
            add(task, true);
        }
    }
    
    public Task findByName(String name) {
        for (Task task: this.tasks) {
            if (task.getName().equals(name)) return task;
        }
        return null;
    }
    
    public boolean add(Task task) {
        return add(task, false);
    }
    
    private boolean add(Task task, boolean replaceIfExists) {
        Task existed = findByName(task.getName()); 
        if (existed != null && !replaceIfExists) return false;
        if (existed != null) this.tasks.remove(existed);
        this.tasks.add(task);
        return true;
    }
    
    public void replace(Task newTask) {
        add(newTask, true);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
