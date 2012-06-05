package ru.aptu.warehouse.tester.execution;

import ru.aptu.warehouse.tester.services.Logger;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface LoadExecutor {
    public void init();
    public void clear();
    public void load(List<String> args);
    public void setLogger(Logger logger);

}
