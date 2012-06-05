package ru.aptu.warehouse.tester.utils.executionPlan.validators;

import ru.aptu.warehouse.tester.core.Launch;
import ru.aptu.warehouse.tester.core.Task;
import ru.aptu.warehouse.tester.core.execution.Scenario;
import ru.aptu.warehouse.tester.core.execution.TaskSolution;
import ru.aptu.warehouse.tester.core.execution.Test;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.NullPlanElement;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.TestPlanException;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.WrongReference;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScenarioValidator extends Validator{
    private Launch launch;

    public ScenarioValidator(Launch launch) {
        this.launch = launch;
    }

    @Override
    public void validate() throws TestPlanException {
        Scenario scenario = this.launch.getScenario();
        if (scenario.getLoader() != null) loaderValidate();
        if (scenario.getTests() == null || scenario.getTests().isEmpty()) {
            throw new NullPlanElement("scenario", "tests");
        }
        testsValidate();
    }

    private void loaderValidate() throws TestPlanException {
        Scenario scenario = this.launch.getScenario();
        if (scenario.getLoader().getInitProperties() == null) {
            throw new NullPlanElement("scenario.loader", "init");
        }
        if (scenario.getLoader().getExecutor() == null) {
            throw new NullPlanElement("scenario.loader", "executor");
        }
        boolean found = false;
        for (Task task: launch.getTasks().getTasks()) {
            if (task.getType().equals("load")) found = true;
        }
        if (!found) {
            throw new TestPlanException("Error: in element 'launch.tasks' " +
                    "should be one task with type=load " +
                    "for using 'scenario.loader'");
        }
    }

    private void testsValidate() throws TestPlanException {
        List<Test> testList = launch.getScenario().getTests();
        if (testList == null || testList.isEmpty()) {
            throw new NullPlanElement("scenario", "tests");
        }
        for (Test test: testList) {
            boolean hasTestRunner = test.getRunner() != null;                   
            if (test.getTasks() == null || test.getTasks().isEmpty()) {
                throw new NullPlanElement("test", "tasks");
            }
            for (TaskSolution taskSolution: test.getTasks()) {
                if (taskSolution.getTask().getName() == null
                        || taskSolution.getTask().getName().isEmpty()) {
                    throw new NullPlanElement("test.tasks.task", "name");
                }
                if (launch.getTasks().findByName(taskSolution.getTask().getName()) == null) {
                    throw new WrongReference("launch.tasks", "test.tasks.task", "name",
                            taskSolution.getTask().getName());
                }
                if (taskSolution.getExecutor() == null && !hasTestRunner) {
                    throw new NullPlanElement("test.task", "executor");
                }
            }
        }
    }
}
