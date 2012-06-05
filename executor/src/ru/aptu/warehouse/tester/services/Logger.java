package ru.aptu.warehouse.tester.services;

import ru.aptu.warehouse.tester.core.Table;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Logger {
    public void createTable(String name, String description);
    public void setTableState(String tableName, long bytesCount, long linesCount);
    public void addMeasureResult(String measure, String task, long dataSize, long millis);
    public void addMeasureResult(String measure, String task, String taskMode, long dataSize, long millis);

    public Table getTableState(String tableName);
    public List<Table> getAllTables();
    public void write(String message);
}
