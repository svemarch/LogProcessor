package ordinary;

import common.LoadScripts;
import org.apache.pig.ExecType;
import org.apache.pig.PigServer;
import org.apache.pig.tools.pigstats.PigStats;
import ru.aptu.warehouse.tester.execution.LoadExecutor;
import ru.aptu.warehouse.tester.services.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/29/12
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class Loader implements LoadExecutor{
    private PigServer pigServer;
    private Logger logger;

    public Loader() {
        try {
           pigServer = new PigServer(ExecType.MAPREDUCE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        logger.createTable("request_log", "Table for primary data: info about requests");
        logger.createTable("click_log", "Table for primary data: info about clicks");
    }

    @Override
    public void clear() {
        try {
            if (pigServer.existsFile("request_log")) {
                pigServer.deleteFile("request_log");
            }
            if (pigServer.existsFile("click_log")) {
                pigServer.deleteFile("click_log");
            }
        } catch (IOException e) {
            logger.write(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void load(List<String> args) {
        try {
            long curTime = System.currentTimeMillis();
            pigServer.registerQuery(LoadScripts.getLoadRequestLog(args.get(0)));
            PigStats requestStats = pigServer.store("request_log", "request_log").getStatistics();
            long requestLoad = System.currentTimeMillis() - curTime;
            logger.addMeasureResult("import_request", "load_task", requestStats.getRecordWritten(), requestLoad);
            logger.setTableState("request_log", 0, requestStats.getRecordWritten());


            curTime = System.currentTimeMillis();
            pigServer.registerQuery(LoadScripts.getLoadClickLog(args.get(1)));
            PigStats clickStats = pigServer.store("click_log", "click_log").getStatistics();
            long clickLoad = System.currentTimeMillis() - curTime;
            logger.addMeasureResult("import_click", "load_task", clickStats.getRecordWritten(), clickLoad);
            logger.setTableState("click_log", 0, clickStats.getRecordWritten());

        } catch (IOException e) {
            logger.write(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
