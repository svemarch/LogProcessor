package ru.aptu.warehouse.tester.utils.executionPlan;

import ru.aptu.warehouse.tester.core.*;
import ru.aptu.warehouse.tester.core.execution.Scenario;
import ru.aptu.warehouse.tester.core.execution.TaskSolution;
import ru.aptu.warehouse.tester.core.execution.Test;
import ru.aptu.warehouse.tester.core.report.Report;
import ru.aptu.warehouse.tester.execution.SimpleTestRunner;
import ru.aptu.warehouse.tester.services.DbService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 6:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlanBuilder {
    private Launch launch;
    private DbService db;

    public PlanBuilder(Launch launch, DbService dbService) {
        this.launch = launch;
        this.db = dbService;
    }

    public Launch build() {
        registerAllInfo();
        return launch;
    }

    private void registerAllInfo() {
        registerTasks();
        registerWarehouse();
        registerCluster();
        registerLaunch();
        registerScenario();
        initializeReports();
    }
    
    private String getLaunchFullDescription()  {
        StringBuilder sb = new StringBuilder();
        if (launch.getDescription() !=null)
            sb.append("Launch: "+ launch.getDescription()+"\n");
        if (launch.getMode() != null)
            sb.append("Launch mode: " + launch.getMode() + "\n");
        return sb.toString();
    }

    private void initializeReports() {
        for (Report report: this.launch.getReports()) {
            report.setLaunchId(launch.getId());
            if (report.getLevel() == Report.Level.TEST) {
                int testOrder = report.getTestId();
                if (launch.getScenario().getLoader() != null)
                    testOrder += 1;
                report.setTestId(launch.getScenario().getTests().get(testOrder).getId());
                report.setTitle(report.getMeasureName()+" ("+ report.getTaskName()+")");
                StringBuilder sb = new StringBuilder();
                sb.append(getLaunchFullDescription());
                if (report.getLevel() == Report.Level.TEST) { 
                    Test test = launch.getScenario().getTests().get(testOrder); 
                    if (test.getDescription() != null)
                        sb.append("Test: "+ test.getDescription() + "\n");
                    if (test.getMode() != null)
                        sb.append("Test mode: " + test.getMode() + "\n");
                }
                report.setDescription(sb.toString());
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(getLaunchFullDescription());
                Task  task = launch.getTasks().findByName(report.getTaskName()); 
                sb.append("Task: " + task.getDescription());
                report.setDescription(sb.toString());
            }
            report.setPathToSave(buildPathToSave(report));
        }
    }
    
    private String buildPathToSave(Report report) {
        StringBuilder reportFileName = new StringBuilder();
        if (report.getPathToSave() != null && !report.getPathToSave().endsWith("/")) {
            reportFileName.append("/");
        }
        reportFileName.append(launch.getMode().replace(" ", "_")+"/");
        reportFileName.append(launch.getId()+"/");
        if (report.getLevel() == Report.Level.TEST)
            reportFileName.append(report.getTestId()+"/");
        reportFileName.append(report.getTaskName().replace(" ", "_")+"_");
        reportFileName.append(report.getMeasureName().replace(" ", "_"));
        if (report.getPathToSave() != null) {
            return report.getPathToSave().concat(reportFileName.toString());
        }
        return reportFileName.toString();
    }

    private void registerTasks() {
        List<Task> registeredTasks = new ArrayList<Task>();
        for (Task task: launch.getTasks().getTasks()) {
            int taskId = db.getTaskId(task);
            if (taskId == 0) {
                registeredTasks.add(db.addTask(task));
            } else {
                task.setId(taskId);
                registeredTasks.add(db.updateTask(task));
            }
        }
        launch.getTasks().setTasks(registeredTasks);
    }

    private void registerWarehouse() {
        Warehouse warehouse = this.launch.getWarehouse();
        int warehouseId = db.getWarehouseId(warehouse);
        warehouse.setId(warehouseId == 0 ? db.addWarehouse(warehouse) : warehouseId);
    }

    private void registerCluster() {
        Cluster cluster = this.launch.getCluster();
        int clusterId = db.getClusterId(cluster);
        cluster.setId(clusterId == 0 ? db.addCluster(cluster) : clusterId);
    }

    private void registerLaunch() {
        this.launch.setId(db.addLaunch(this.launch));
    }

    private void registerScenario() {
        if (this.launch.getScenario().getLoader() != null) {
            registerLoader();
            registerTests(1);
        }else {
            registerTests(0);
        }
    }

    private void registerLoader() {
        Scenario scenario = this.launch.getScenario();
        Test loadTest = new Test();
        loadTest.setMode(scenario.getLoader().getMode());
        loadTest.setDescription("Iterative loading data into the warehouse");
        int testId = db.addTest(loadTest, launch.getId());
        List<TaskSolution> tasks = new ArrayList<TaskSolution>();
        for (Task task: this.launch.getTasks().getTasks()) {
            if (task.getType().equals("load")) {
                TaskSolution taskSolution = new TaskSolution();
                taskSolution.setTask(task);
                taskSolution.setMode(loadTest.getMode());
                taskSolution.setId(db.addTestTask(taskSolution, testId, 0));
                tasks.add(taskSolution);
            }
        }
        loadTest.setId(testId);
        loadTest.setTasks(tasks);
        scenario.getTests().add(0, loadTest);
    }

    private void registerTests(int startIndex) {
        List<Test> testList = this.launch.getScenario().getTests();
        for (int i = startIndex; i < testList.size(); ++i) {
            Test test = testList.get(i);
            test.setId(db.addTest(test, launch.getId()));
            for (int j = 0; j < test.getTasks().size(); ++j) {
                TaskSolution taskSolution = test.getTasks().get(j);
                taskSolution.setTask(launch.getTasks().findByName(taskSolution.getTask().getName()));
                taskSolution.setId(db.addTestTask(taskSolution, test.getId(), j));
            }
            if (test.getRunner() == null) test.setRunner(new SimpleTestRunner(test));
        }
    }

}
