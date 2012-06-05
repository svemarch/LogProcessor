package ru.aptu.warehouse.tester.services;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.aptu.warehouse.tester.core.*;
import ru.aptu.warehouse.tester.core.execution.TaskSolution;
import ru.aptu.warehouse.tester.core.execution.Test;
import ru.aptu.warehouse.tester.core.report.GraphData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class DbService {
    private JdbcTemplate jdbcTemplate;

    public DbService(LoggerProperty properties) throws SQLException {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(
                properties.get("db.url"),
                properties.get("db.username"),
                properties.get("db.pwd"));
        driverManagerDataSource.setDriverClassName(properties.get("db.driver.class"));
        this.jdbcTemplate = new JdbcTemplate(driverManagerDataSource);
        driverManagerDataSource.getConnection();
    }
    

    public int getTaskId(Task task) {
        String query = "SELECT id from task WHERE name = ? AND description = ? AND type = ?";
        try {
            return jdbcTemplate.queryForInt(query, task.getName(), task.getDescription(), task.getType());
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    public Task addTask(Task task) {
        String query = "INSERT INTO task (name, description, type) VALUES(?, ?, ?)";
        jdbcTemplate.update(query, task.getName(), task.getDescription(), task.getType());
        Task registeredTask = new Task(task);
        registeredTask.setId(getTaskId(task));
        for (Measure measure: registeredTask.getMeasures()) {
            int internalId = getMeasureId(measure);
            if (internalId == 0) internalId = addMeasure(measure);
            int externalId = addTaskMeasure(internalId, registeredTask.getId());
            measure.setId(externalId);
        }
        return registeredTask;
    }

    public Task updateTask(Task task) {
        Task updatedTask = new Task(task);
        updatedTask.setId(task.getId());
        for (Measure measure: updatedTask.getMeasures()) {
            int internalId = getMeasureId(measure);
            int externalId = 0;
            if (internalId == 0) {
                internalId = addMeasure(measure);
            } else {
                externalId = getTaskMeasureId(internalId, updatedTask.getId());
            }
            if (externalId == 0)
                externalId = addTaskMeasure(internalId, updatedTask.getId());
            measure.setId(externalId);
        }
        return updatedTask;
    }

    public int getMeasureId(Measure measure) {
        String query = "SELECT id FROM measure WHERE name = ? AND description = ?";
        try {
            return jdbcTemplate.queryForInt(query, measure.getName(), measure.getDescription());
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    public int addMeasure(Measure measure) {
        String query = "INSERT INTO measure (name, description) VALUES (?, ?)";
        jdbcTemplate.update(query, measure.getName(), measure.getDescription());
        return getMeasureId(measure);
    }


    public int getTaskMeasureId(int measureId, int taskId) {
        String query = "SELECT id FROM task_measure WHERE task_id = ? AND measure_id = ?";
        try {
            return jdbcTemplate.queryForInt(query, taskId, measureId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public int addTaskMeasure(int measureId, int taskId) {
        String query = "INSERT INTO task_measure (task_id, measure_id) VALUES (?, ?)";
        jdbcTemplate.update(query, taskId, measureId);
        return getTaskMeasureId(measureId, taskId);
    }
    
    public int getClusterId(Cluster cluster) {
        String query = "SELECT id from cluster WHERE nodes_cnt = ? AND mode = ? AND description = ?";
        try {
            return  jdbcTemplate.queryForInt(query, cluster.getNodesCount(),
                cluster.getMode(), cluster.getDescription());
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    public int addCluster(Cluster cluster) {
        String query = "INSERT INTO cluster (nodes_cnt, mode, description) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, cluster.getNodesCount(),
                cluster.getMode(), cluster.getDescription());
        return getClusterId(cluster);
    }
    
    public int getWarehouseId(Warehouse warehouse) {
        String query = "SELECT id FROM warehouse WHERE type = ? AND name = ? AND description = ?";
        try {
            return jdbcTemplate.queryForInt(query, warehouse.getType(),
                    warehouse.getName(), warehouse.getDescription());
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    public int addWarehouse(Warehouse warehouse) {
        String query = "INSERT INTO warehouse (name, description, type) VALUES(?, ?, ?)";
        jdbcTemplate.update(query, warehouse.getName(),
                warehouse.getDescription(), warehouse.getType());
        return getWarehouseId(warehouse);
    }
    
    public int getLastLaunchId(Launch launch) {
        String query = "SELECT max(id) FROM launch " +
                "WHERE mode = ? AND description = ? AND cluster_id = ? AND warehouse_id = ?";
        try {
            return jdbcTemplate.queryForInt(query, launch.getMode(), launch.getDescription(),
                    launch.getCluster().getId(), launch.getWarehouse().getId());
        } catch (EmptyResultDataAccessException e) {
            return  0;
        }
    }
    
    public int addLaunch(Launch launch) {
        String query = "INSERT INTO launch (mode, description, cluster_id, warehouse_id) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(query, launch.getMode(), launch.getDescription(),
                launch.getCluster().getId(), launch.getWarehouse().getId());
        return getLastLaunchId(launch);
    } 
    
    public int getTestId(Test test, int launchId) {
        String query = "SELECT max(id) FROM test WHERE launch_id = ?  AND mode = ? AND description = ?";
        try {
            return jdbcTemplate.queryForInt(query, launchId, test.getMode(), test.getDescription());
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    public int addTest(Test test, int launchId) {
        String query = "INSERT INTO test (launch_id, mode, description) VALUES(?, ?, ?)";
        jdbcTemplate.update(query, launchId, test.getMode(), test.getDescription());
        return getTestId(test, launchId);
    }
    
    public int addTestTask(TaskSolution task, int testId, int order) {
        String query = "INSERT INTO test_task (test_id, task_id, mode, task_order) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(query, testId, task.getTask().getId(), task.getMode(), order);
        return getTestTaskId(task, testId, order);
    }
    
    public int getTestTaskId(TaskSolution task, int testId, int order) {
        String query = "SELECT max(id) FROM test_task WHERE task_id = ? AND test_id = ? AND task_order = ?";
        try {
            return jdbcTemplate.queryForInt(query, task.getTask().getId(), testId, order);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    public int addTable(Table table, int warehouseId) {
        String query = "INSERT INTO warehouse_table (warehouse_id, name, description) VALUES(?, ?, ?)";
        jdbcTemplate.update(query, warehouseId, table.getName(), table.getDescription());
        return getTableId(table, warehouseId);
    }
    
    public int getTableId(Table table, int warehouseId) {
        String query = "SELECT id FROM warehouse_table WHERE warehouse_id = ? AND name = ?";
        try {
            return jdbcTemplate.queryForInt(query, warehouseId, table.getName());
        } catch (EmptyResultDataAccessException e) {
            return  0;
        }
    }
    
    public void addTableState(int tableId, int launchId, long bytesCnt, long linesCnt) {
        String query = "INSERT INTO table_state (launch_id, table_id, bytes_cnt, lines_cnt) VALUES(?,?,?,?)";
        jdbcTemplate.update(query, launchId, tableId, bytesCnt, linesCnt);
    }

    public void addTaskOperation(int taskId, int measureId, long dataSize, long millis) {
        String query = "INSERT INTO task_operation (test_task_id, task_measure_id, data_size, millis) VALUES(?,?,?,?)";
        jdbcTemplate.update(query, taskId, measureId, dataSize, millis);
    }

    public Map<Integer,GraphData> getTestData(int testId, int taskId, int taskMeasureId) {
        final Map<Integer, GraphData> result = new HashMap<Integer, GraphData>();
        String query =
            "SELECT top.test_task_id, top.data_size, top.millis " +
            "FROM task_operation top, test_task_id tt " +
            "WHERE top.task_measure_id = ? AND top.test_task_id = tt.id " +
                    "AND tt.test_id = ? AND tt.task_id = ?";
        jdbcTemplate.query(query, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                Integer testTaskId = resultSet.getInt("test_task_id");
                Long millis = resultSet.getLong("millis");
                Long dataSize = resultSet.getLong("data_size");
                GraphData.MeasurePoint point = new GraphData.MeasurePoint(dataSize, millis);
                GraphData graphData = result.get(testTaskId);
                if (graphData == null) graphData = new GraphData();
                graphData.addPoint(point);
                result.put(testTaskId, graphData);
            }
        }, taskMeasureId, testId, taskId);
        return result;
    }
    
    public Map<Integer, GraphData> getLaunchData(int launchId, int taskId, int taskMeasureId) {
        final Map<Integer, GraphData> result = new HashMap<Integer, GraphData>();
        String query =
            "SELECT top.test_task_id, top.data_size, top.millis " +
                    "FROM task_operation top, test_task tt, test t " +
                    "WHERE top.task_measure_id = ? AND top.test_task_id = tt.id " +
                    "AND tt.task_id = ? AND tt.test_id=t.id AND t.launch_id = ?";
          jdbcTemplate.query(query, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                Integer testTaskId = resultSet.getInt("test_task_id");
                Long millis = resultSet.getLong("millis");
                Long dataSize = resultSet.getLong("data_size");
                GraphData.MeasurePoint point = new GraphData.MeasurePoint(dataSize, millis);
                GraphData graphData = result.get(testTaskId);
                if (graphData == null) graphData = new GraphData();
                graphData.addPoint(point);
                result.put(testTaskId, graphData);
            }
        }, taskMeasureId, taskId, launchId);
        return result;
    }
    
    public String getTestTaskMode(int testTaskId) {
        String query = "SELECT mode FROM test_task WHERE id = ?";
        final String[] result = new String[1];
        jdbcTemplate.query(query, new
                RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet resultSet) throws SQLException {
                        result[0] = resultSet.getString("mode");
                    }
                }, testTaskId);
        return result[0];
    }
    

}
