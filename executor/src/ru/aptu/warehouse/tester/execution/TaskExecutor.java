package ru.aptu.warehouse.tester.execution;

import ru.aptu.warehouse.tester.services.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TaskExecutor {
    public void execute();
    public void setLogger(Logger logger);
}
