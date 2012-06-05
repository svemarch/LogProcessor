package ru.aptu.warehouse.tester.services;

import ru.aptu.warehouse.tester.core.*;
import ru.aptu.warehouse.tester.core.execution.Scenario;
import ru.aptu.warehouse.tester.core.execution.TaskSolution;
import ru.aptu.warehouse.tester.core.execution.Test;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.TestPlanException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class LaunchLogger implements Logger{
    private Launch launch;
    private Test currentTest;
    private DbService db;
    private BufferedWriter writer;

    public LaunchLogger(DbService dbService, Launch launch, String logFile) {
        this.launch = launch;
        this.db = dbService;
        try {
            this.writer = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException e) {
            new TestPlanException("Warn: cannot open file "+logFile+" for writing");
        }
    }
    
    public void setCurrentTest(int order) {
        this.currentTest = this.launch.getScenario().getTests().get(order);
    }
    

    @Override
    public void createTable(String name, String description) {
        Table table = new Table(name, description);
        int tableId = db.getTableId(table, this.launch.getWarehouse().getId());
        if (tableId == 0) tableId = db.addTable(table, this.launch.getWarehouse().getId());
        db.addTableState(tableId, this.launch.getId(), 0, 0);
        table.setId(tableId);
        table.addState(0, 0);
        launch.getWarehouse().getTables().add(table);
    }

    @Override
    public void setTableState(String tableName, long bytesCount, long linesCount) {
        Table table = this.launch.getWarehouse().getTableByName(tableName);
        if (table != null) {
            table.addState(bytesCount, linesCount);
            db.addTableState(table.getId(), this.launch.getId(), bytesCount, linesCount);
        } else {
            write("Warn: try to add state for not created table "+table+". The state was added and table was created.");
        }
    }
                        
    @Override
    public void addMeasureResult(String measure, String task, long dataSize, long millis) {
        int taskId = 0;
        int measureId = 0;
        for (TaskSolution solution: this.currentTest.getTasks()) {
            if (solution.getTask().getName().equals(task)) {
                taskId = solution.getId();
                measureId = solution.getTask().getMeasureByName(measure).getId();
            }
        }
        db.addTaskOperation(taskId, measureId, dataSize, millis);
    }

    @Override
    public void addMeasureResult(String measure, String task, String taskMode, long dataSize, long millis) {
        int taskId = 0;
        int measureId = 0;
        for (TaskSolution solution: this.currentTest.getTasks()) {
            if (solution.getTask().getName().equals(task) && solution.getMode().equals(taskMode)) {
                taskId = solution.getId();
                measureId = solution.getTask().getMeasureByName(measure).getId();
            }
        }
        db.addTaskOperation(taskId, measureId, dataSize, millis);
    }
    
    

    @Override
    public Table getTableState(String tableName) {
        Table table = this.launch.getWarehouse().getTableByName(tableName);
        if (table != null) {
            return table;
        }
        return null;
    }

    @Override
    public List<Table> getAllTables() {
        return this.launch.getWarehouse().getTables();
    }

    @Override
    public void write(String message) {
        try {
            writer.write(message+"\n");
        } catch (IOException e) {
            System.out.println("Warn: cannot write into file for log.\n"+message);
        }
    }
}
