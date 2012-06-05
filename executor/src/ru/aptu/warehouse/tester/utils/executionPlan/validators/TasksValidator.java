package ru.aptu.warehouse.tester.utils.executionPlan.validators;

import ru.aptu.warehouse.tester.core.LaunchTaskList;
import ru.aptu.warehouse.tester.core.Measure;
import ru.aptu.warehouse.tester.core.Task;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.InvalidValue;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.NullPlanElement;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.TestPlanException;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.UniquePlanElements;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class TasksValidator extends Validator{
    private List<Task> tasks;

    public TasksValidator(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void validate() throws TestPlanException {
        LaunchTaskList uniqueTaskList = new LaunchTaskList();
        for (Task task: tasks) {
            taskValidate(task);
            if (!uniqueTaskList.add(task)) {
                throw new UniquePlanElements("tasks", "name", task.getName());
            }
        }
    }
    
    private void measureValidate(Measure measure) throws TestPlanException {
        if (measure.getName() == null || measure.getName().isEmpty()) {
            throw new NullPlanElement("measure", "name");
        }
    }
    
    private void taskValidate(Task task) throws TestPlanException {
        if (task.getName() == null || task.getName().isEmpty()) {
            throw new NullPlanElement("task", "name");
        }
        if (task.getType() == null || task.getType().isEmpty()) {
            throw new NullPlanElement("task", "type");
        }
        if (Task.Type.valueOf(task.getType().toUpperCase()) == null) {
            throw new InvalidValue("task", "type", task.getType(), Task.Type.values().toString());
        }
        if (task.getMeasures() == null || task.getMeasures().isEmpty()) {
            throw new NullPlanElement("task", "measures");
        }
        for (Measure measure: task.getMeasures()) {
            measureValidate(measure);
        }
    }
}
