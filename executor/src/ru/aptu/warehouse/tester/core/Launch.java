package ru.aptu.warehouse.tester.core;

import ru.aptu.warehouse.tester.core.execution.Scenario;
import ru.aptu.warehouse.tester.core.execution.TaskSolution;
import ru.aptu.warehouse.tester.core.execution.Test;
import ru.aptu.warehouse.tester.core.report.Report;
import ru.aptu.warehouse.tester.services.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/22/12
 * Time: 7:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class Launch {
    private int id;
    private String mode;
    private String description;

    private Cluster cluster;
    private Warehouse warehouse;
    private LaunchTaskList tasks;
    private Scenario scenario;
    private List<Report> reports = new ArrayList<Report>();

    public Launch() {}

    public Launch(Launch launch) {
        this.mode = launch.mode;
        this.description = launch.description;
        this.cluster = new Cluster(launch.getCluster());
        this.warehouse = new Warehouse(launch.getWarehouse());
        this.tasks = new LaunchTaskList(launch.getTasks().getTasks());
        this.scenario = new Scenario(launch.getScenario());
    }
    
    public void setExecutorsLogger(Logger logger) {
        if (scenario.getLoader() != null) scenario.getLoader().getExecutor().setLogger(logger);
        for (Test test: scenario.getTests()) {
            if (test.getRunner() != null) {
                test.getRunner().setLogger(logger);
                for (TaskSolution taskSolution: test.getTasks()) {
                    if (taskSolution.getExecutor() != null)
                        taskSolution.getExecutor().setLogger(logger);
                }
            }

        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public LaunchTaskList getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = new LaunchTaskList();
        this.tasks.setTasks(tasks);
    }

    public void setTasks(LaunchTaskList tasks) {
        this.tasks = tasks;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public void addReport(Report report) {
        if (reports == null) reports = new ArrayList<Report>();
        reports.add(report);
    }

}
