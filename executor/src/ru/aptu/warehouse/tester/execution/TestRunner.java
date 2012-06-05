package ru.aptu.warehouse.tester.execution;

import ru.aptu.warehouse.tester.services.Logger;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TestRunner {
    public void run(List<String> args);
    public void setLogger(Logger logger);
}
