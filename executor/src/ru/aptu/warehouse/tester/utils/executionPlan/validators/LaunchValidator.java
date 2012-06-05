package ru.aptu.warehouse.tester.utils.executionPlan.validators;

import ru.aptu.warehouse.tester.core.Launch;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.NullPlanElement;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.TestPlanException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class LaunchValidator extends Validator{
    private Launch launch;

    public LaunchValidator(Launch launch) {
        this.launch = launch;
    }

    @Override
    public void validate() throws TestPlanException {
        if (launch.getMode() == null || launch.getMode().isEmpty())
            throw new NullPlanElement("launch", "mode");
        if (launch.getCluster() == null) {
            throw new NullPlanElement("launch", "cluster");
        } else {
            new ClusterValidator(launch.getCluster()).validate();
        }
        if (launch.getWarehouse() == null) {
            throw new NullPlanElement("launch", "warehouse");
        } else {
            new WarehouseValidator(launch.getWarehouse()).validate();
        }
        if (launch.getTasks() == null || launch.getTasks().getTasks().isEmpty()) {
            throw new NullPlanElement("launch", "tasks");
        } else {
            new TasksValidator(launch.getTasks().getTasks());
        }
        if (launch.getScenario() == null) {
            throw new NullPlanElement("launch", "scenario");
        } else {
            new ScenarioValidator(launch).validate();
        }
    }
}
