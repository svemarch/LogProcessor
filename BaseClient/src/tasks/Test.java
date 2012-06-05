package tasks;


import ru.aptu.warehouse.tester.execution.TestRunner;
import ru.aptu.warehouse.tester.services.Logger;
import tasks.join.JoinTaskManager;
import tasks.simple.SimpleTaskManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/30/12
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class Test implements TestRunner {
    private Logger logger;

    @Override
    public void run(List<String> args) {

        try {
            ResultLoader resultLoader = new ResultLoader();

            SimpleTaskManager simpleTaskManager = new SimpleTaskManager(args.subList(0, 1), args.get(2), logger);
            simpleTaskManager.setLoader(resultLoader);
            simpleTaskManager.execute();

            JoinTaskManager joinTaskManager = new JoinTaskManager(args.subList(0, 2), args.get(3), logger);
            joinTaskManager.setLoader(resultLoader);
            joinTaskManager.execute();

        } catch (IOException e) {
            logger.write(e.getMessage());
        } catch (SQLException e) {
            logger.write(e.getMessage());
        } catch (InterruptedException e) {
            logger.write(e.getMessage());
        } catch (ExecutionException e) {
            logger.write(e.getMessage());
        }
    }

    @Override
    public void setLogger(Logger logger) {
       this.logger = logger;
    }
}
