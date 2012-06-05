package ru.aptu.warehouse.tester.core.execution;

import ru.aptu.warehouse.tester.core.Task;
import ru.aptu.warehouse.tester.execution.TaskExecutor;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskSolution {
    int id;
    Task task;
    TaskExecutor executor;
    String mode;

    public TaskSolution() {}

    public TaskSolution(TaskSolution taskSolution) {
        this.task = taskSolution.getTask();
        this.executor = taskSolution.executor;
        this.mode = taskSolution.mode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(TaskExecutor executor) {
        this.executor = executor;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
