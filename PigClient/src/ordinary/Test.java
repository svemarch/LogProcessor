package ordinary;

import common.LoadScripts;
import org.apache.pig.PigServer;
import org.apache.pig.tools.pigstats.InputStats;
import org.apache.pig.tools.pigstats.PigStats;
import ru.aptu.warehouse.tester.execution.TestRunner;
import ru.aptu.warehouse.tester.services.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/29/12
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test implements TestRunner {
    private Logger logger;
    private PigServer pigServer;

    public Test() {
        try {
            pigServer = new PigServer("mapreduce");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run(List<String> args) {
        loadLogs(args.get(0), args.get(1));
        runSimpleTask();
        runJoinTask();
        runAccessTask();
    }
    
    private void loadLogs(String requestPath, String clickPath) {
        try {
            pigServer.registerQuery(LoadScripts.getLoadRequestLog(requestPath));
            pigServer.registerQuery(LoadScripts.getLoadClickLog(clickPath));

            logger.createTable("request_log", "Table for primary data: info about requests");
            logger.createTable("click_log", "Table for primary data: info about clicks");
        } catch (IOException e) {
            logger.write(e.getMessage());
        }
    }

    private void runSimpleTask() {
        try {

            pigServer.registerQuery("grouped_records = GROUP request_log BY (dt, company_id, base, region_id);");
            pigServer.registerQuery("company_shows = FOREACH grouped_records GENERATE FLATTEN(group), COUNT(request_log.request_id) as count_shows;");
            if (pigServer.existsFile("company_shows")) pigServer.deleteFile("company_shows");
            long time = System.currentTimeMillis();
            PigStats stats = pigServer.store("company_shows", "company_shows").getStatistics();
            long diff = System.currentTimeMillis() - time;
            long dataSize = 0;
            for (InputStats inputStats: stats.getInputStats()) {
                if (inputStats.getName().equals("request_log"))
                    dataSize = inputStats.getNumberRecords();
            }
            logger.addMeasureResult("aggregation", "simple_task", dataSize, diff);
        } catch (IOException e) {
            logger.write(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private void runJoinTask() {
        try {
            pigServer.registerQuery(
                    "tmp = " +
                            "JOIN request_log BY (dt, request_id, position_num), " +
                            "click_log BY (dt, request_id, position_num);");
            pigServer.registerQuery(
                    "company_clicks_intermediate = FOREACH tmp GENERATE " +
                        "$0 as dt, $2 as position_num, $3 as company_id, $4 as base, $5 as region_id, $9 as path;");
            pigServer.registerQuery(
                    "grouped_clicks = GROUP company_clicks_intermediate BY (dt, company_id, path, region_id);");
            pigServer.registerQuery(
                    "company_clicks = FOREACH grouped_clicks " +
                            "GENERATE FLATTEN(group), COUNT(company_clicks_intermediate.position_num) as count_clicks;");
            if (pigServer.existsFile("company_clicks")) pigServer.deleteFile("company_clicks");
            long time = System.currentTimeMillis();
            PigStats stats = pigServer.store("company_clicks", "company_clicks").getStatistics();
            long diff = System.currentTimeMillis() - time;
            long dataSize = 0;
            for (InputStats inputStats: stats.getInputStats()) {
                if (inputStats.getName().equals("request_log")
                        || inputStats.getName().equals("click_log")) dataSize += inputStats.getNumberRecords();
            }
            logger.addMeasureResult("whole_task", "join_task", dataSize, diff);
        } catch (IOException e) {
            logger.write(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private void runAccessTask() {
        try {
            pigServer.registerQuery(
                    "select_2 = FILTER request_log BY dt =='2012-04-03' and region_id == 213;");

            pigServer.registerQuery(
                    "select_3 = FILTER request_log BY dt =='2012-04-03' AND region_id == 225 AND base !='yabs';"
            );

            pigServer.registerQuery(
                    "all_sort_tmp = FILTER request_log BY dt >= '2012-04-03' AND dt <= '2012-04-07' AND  " +
                            "  region_id == 213 AND base == 'yabs'; ");
            pigServer.registerQuery(
                    "all_sort = ORDER all_sort_tmp BY request_id;"
            );

            pigServer.registerQuery(
                    "all_aggregate_filtered = FILTER request_log BY dt >= '2012-04-03' AND dt <= '2012-04-08' AND " +
                            "region_id == 225;"
            );
            pigServer.registerQuery(
                    "all_aggregate_grouped = GROUP all_aggregate_filtered BY (company_id, base);"
            );
            pigServer.registerQuery(
                    "all_aggregate = FOREACH all_aggregate_grouped GENERATE FLATTEN(group), " +
                            "COUNT(all_aggregate_filtered.request_id) as count_shows;"
            );

            if (pigServer.existsFile("select_2")) pigServer.deleteFile("select_2");
            long start = System.currentTimeMillis();
            PigStats stats = pigServer.store("select_2", "select_2").getStatistics();
            long diff = System.currentTimeMillis() - start;
            long dataSize = stats.getNumberRecords("request_log");
            logger.addMeasureResult("select(2)", "access_task", dataSize, diff);

            if (pigServer.existsFile("select_3")) pigServer.deleteFile("select_3");
            start = System.currentTimeMillis();
            stats = pigServer.store("select_3", "select_3").getStatistics();
            diff = System.currentTimeMillis() - start;
            logger.addMeasureResult("select(3)", "access_task", dataSize, diff);

            if (pigServer.existsFile("all_sort")) pigServer.deleteFile("all_sort");
            start = System.currentTimeMillis();
            stats = pigServer.store("all_sort", "all_sort").getStatistics();
            diff = System.currentTimeMillis() - start;
            logger.addMeasureResult("all(sort)", "access_task", dataSize, diff);

            if (pigServer.existsFile("all_aggregate")) pigServer.deleteFile("all_aggregate");
            start = System.currentTimeMillis();
            stats = pigServer.store("all_aggregate", "all_aggregate").getStatistics();
            diff = System.currentTimeMillis() - start;
            logger.addMeasureResult("all(aggregate)", "access_task", dataSize, diff);

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
