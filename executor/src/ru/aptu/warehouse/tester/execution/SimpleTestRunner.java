package ru.aptu.warehouse.tester.execution;

import ru.aptu.warehouse.tester.core.execution.TaskSolution;
import ru.aptu.warehouse.tester.core.execution.Test;
import ru.aptu.warehouse.tester.services.Logger;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleTestRunner implements TestRunner {
    private Test test;
    private Logger logger;

    public SimpleTestRunner(Test test) {
        this.test = test;
    }

    @Override
    public void run(List<String> args) {
        for (TaskSolution solution: test.getTasks()) {
            solution.getExecutor().execute();
        }
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
