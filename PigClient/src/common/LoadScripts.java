package common;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/29/12
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoadScripts {
    public static String getLoadRequestLog(String logPath) {
        return "request_log = LOAD '"+ logPath + "' using PigStorage('\t') AS (" +
                "dt:chararray, request_id:chararray, position_num:int, " +
                "company_id:chararray, base:chararray, region_id:int);";
    }
    
    public static String getLoadClickLog(String logPath) {
        return "click_log = LOAD '"+ logPath + "' AS (" +
                "dt:chararray, request_id:chararray, position_num:int, " +
                "company_id:chararray, base:chararray, region_id:int);";
    }
    
}
