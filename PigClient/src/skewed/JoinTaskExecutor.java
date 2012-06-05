package skewed;

import org.apache.pig.PigServer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/6/12
 * Time: 7:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class JoinTaskExecutor {
    private PigServer pigServer;
    private int index;

    public static String TASK_NAME = "join task";
    public static String TASK_DESCRIPTION = "This task uses join two logs and aggregation";
    public static String TASK_MODE = "skewed";

    public JoinTaskExecutor(PigServer pigServer) {
        this.pigServer = pigServer;
    }

    public void run() throws IOException {
        registerTables();
        String logPath = "logPath";
        String joinLogPath = "joinPath";
        pigServer.registerQuery(
                "company_show_log = LOAD '"+ logPath + "' AS (" +
                        "dt:chararray, request_id:chararray, position_num:int, " +
                        "company_id:chararray, base:chararray, region_id:int);");
        pigServer.registerQuery(
                "clicks = LOAD '" + joinLogPath +"' AS (" +
                        "dt:chararray, request_id:chararray, position_num:int, path:chararray);");
        pigServer.registerQuery(
                "tmp = " +
                        "JOIN clicks BY (dt, request_id, position_num), " +
                        "company_show_log BY (dt, request_id, position_num) USING 'skewed';");
                        /*"JOIN company_show_log BY (dt, request_id, position_num), " +
                        "clicks BY (dt, request_id, position_num) USING 'skewed';");*/
        pigServer.registerQuery("company_clicks_intermediate = FOREACH tmp GENERATE " +
                "$0 as dt, $2 as position_num, $3 as company_id, $4 as base, $5 as region_id, $9 as path;");
        pigServer.registerQuery("grouped_clicks = GROUP company_clicks_intermediate BY (dt, company_id, path, region_id);");
        pigServer.registerQuery("company_clicks = FOREACH grouped_clicks GENERATE FLATTEN(group), COUNT(company_clicks_intermediate.position_num) as count_clicks;");
        if (pigServer.existsFile("company_clicks")) pigServer.deleteFile("company_clicks");
        long time = System.currentTimeMillis();
        pigServer.store("company_clicks", "company_clicks");
        long diff = System.currentTimeMillis() - time;
        System.out.println("join task " + diff + " millis");

    }
    
    private void registerTables() {
        
    }
}
