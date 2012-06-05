package ru.aptu.warehouse.tester.core.report;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 6/1/12
 * Time: 3:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class Report {
    public enum Level{TEST, LAUNCH}
    
    private int launchId;
    private int testId;
    private Level level;
    private String taskName;
    private String measureName;
    private String title;
    private String description;
    private List<GraphData> reportData;
    private String pathToSave;

    public Report() {
    }

    public Report(Level level, String taskName, String measureName) {
        this.level = level;
        this.taskName = taskName;
        this.measureName = measureName;
    }

    public Report(Level level, String taskName, String measureName, String pathToSave) {
        this.level = level;
        this.taskName = taskName;
        this.measureName = measureName;
        this.pathToSave = pathToSave;
    }

    public int getLaunchId() {
        return launchId;
    }

    public void setLaunchId(int launchId) {
        this.launchId = launchId;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<GraphData> getReportData() {
        return reportData;
    }

    public void setReportData(List<GraphData> reportData) {
        this.reportData = reportData;
    }

    public String getPathToSave() {
        return pathToSave;
    }

    public void setPathToSave(String pathToSave) {
        this.pathToSave = pathToSave;
    }
}
