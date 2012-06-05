package ru.aptu.warehouse.tester.core.execution;

import ru.aptu.warehouse.tester.execution.TestRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    private int id;
    private String mode;
    private String description;
    private boolean iterative = false;
    private TestRunner runner;
    private List<TaskSolution> tasks;

    public Test() {}

    public Test(Test test) {
        this.mode = test.mode;
        this.description = test.description;
        this.runner = test.runner;
        this.tasks = new ArrayList<TaskSolution>();
        for (TaskSolution taskSolution: test.getTasks()) {
            this.tasks.add(new TaskSolution(taskSolution));
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIterative() {
        return iterative;
    }

    public void setIterative(boolean iterative) {
        this.iterative = iterative;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TestRunner getRunner() {
        return runner;
    }

    public void setRunner(TestRunner runner) {
        this.runner = runner;
    }

    public List<TaskSolution> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskSolution> tasks) {
        this.tasks = tasks;
    }
}
