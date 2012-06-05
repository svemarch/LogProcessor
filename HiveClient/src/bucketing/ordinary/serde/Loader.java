package bucketing.ordinary.serde;

import ru.aptu.warehouse.tester.execution.LoadExecutor;
import ru.aptu.warehouse.tester.services.Logger;
import tools.SqlRequestsWrapper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/29/12
 * Time: 12:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Loader implements LoadExecutor {
    private SqlRequestsWrapper sqlRequestsWrapper;
    private Logger logger;

    public Loader() {
        this.sqlRequestsWrapper = new SqlRequestsWrapper();

    }

    public static String REQUEST_LOG_NAME = "request_log";
    public static String CLICK_LOG_NAME = "click_log";

    private static String TMP_REQUEST_TABLE_NAME = "tmp_request_log";
    private static String TMP_CLICK_TABLE_NAME = "tmp_click_log";

    @Override
    public void init() {
        try {
            sqlRequestsWrapper.init();
            sqlRequestsWrapper.executeQuery("CREATE TABLE IF NOT EXISTS "+ REQUEST_LOG_NAME +" (" +
                    "request_id STRING, position_num INT, " +
                    "company_id STRING, base STRING, " +
                    "dt STRING, region_id INT) " +
                    "CLUSTERED by (request_id, position_num) SORTED BY (request_id ASC) INTO 4 BUCKETS " +
                    "ROW FORMAT SERDE 'serde.regex.RegexSerDe' " +
                    "WITH SERDEPROPERTIES (" +
                    "\"input.log\" = \"showLog\" ) ");
            sqlRequestsWrapper.executeQuery("CREATE TABLE IF NOT EXISTS "+ CLICK_LOG_NAME +" (" +
                    "request_id STRING, position_num INT, path STRING, dt STRING) " +
                    "CLUSTERED by (request_id, position_num) SORTED BY (request_id ASC) INTO 4 BUCKETS " +
                    "ROW FORMAT SERDE 'serde.regex.RegexSerDe' " +
                    "WITH SERDEPROPERTIES (" +
                    "\"input.log\" = \"clickLog\" ) ") ;

            sqlRequestsWrapper.executeQuery(
                    "CREATE TABLE IF NOT EXISTS "+ TMP_REQUEST_TABLE_NAME +" (" +
                            "dt STRING, request_id STRING," +
                            "position_num INT, company_id STRING," +
                            "base STRING, region_id INT)" +
                            "ROW FORMAT SERDE 'serde.regex.RegexSerDe' " +
                            "WITH SERDEPROPERTIES (" +
                            "\"input.log\" = \"showLog\" ) ");

            sqlRequestsWrapper.executeQuery(
                    "CREATE TABLE IF NOT EXISTS "+ TMP_CLICK_TABLE_NAME +" (" +
                            "dt STRING, request_id STRING," +
                            "position_num INT, path STRING)" +
                            "ROW FORMAT SERDE 'serde.regex.RegexSerDe' " +
                            "WITH SERDEPROPERTIES (" +
                            "\"input.log\" = \"clickLog\" ) ");

        } catch (SQLException e) {
            logger.write("Error: cannot createTable");
        } finally {
            sqlRequestsWrapper.close();
        }
        logger.createTable(REQUEST_LOG_NAME, "Table for primary data: info about requests");
        logger.createTable(CLICK_LOG_NAME, "Table for primary data: info about clicks");
        logger.createTable(TMP_CLICK_TABLE_NAME, "Temporary table for primary data: info about clicks");
        logger.createTable(TMP_REQUEST_TABLE_NAME, "Temporary table for primary data: info about requests");
    }

    @Override
    public void clear() {
        try {
            sqlRequestsWrapper.init();
            sqlRequestsWrapper.executeQuery("DROP TABLE IF EXISTS " + REQUEST_LOG_NAME);
            sqlRequestsWrapper.executeQuery("DROP TABLE IF EXISTS " + CLICK_LOG_NAME);
            sqlRequestsWrapper.executeQuery("DROP TABLE IF EXISTS "+ TMP_REQUEST_TABLE_NAME);
            sqlRequestsWrapper.executeQuery("DROP TABLE IF EXISTS "+ TMP_CLICK_TABLE_NAME);
        } catch (SQLException e) {
            logger.write("Error: cannot drop table. "+e.getMessage());
        } finally {
            sqlRequestsWrapper.close();
        }
    }

    @Override
    public void load(List<String> args) {
        try {
            sqlRequestsWrapper.init();
            loadRequestData(args.get(0));
            loadClickData(args.get(1));
        } catch (SQLException e) {
            logger.write(e.getMessage());
        } finally {
            sqlRequestsWrapper.close();
        }
    }

    private void loadRequestData(String sourceFile) {
        String importDataToTmp = "LOAD DATA local inpath '" + sourceFile + "'OVERWRITE into table " + TMP_REQUEST_TABLE_NAME;
        String partitionLog =
            "FROM " + TMP_REQUEST_TABLE_NAME +
                    " INSERT OVERWRITE TABLE " + REQUEST_LOG_NAME +
                    " SELECT request_id, position_num, company_id, base, dt, region_id ";

        try {
            long curTime = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(importDataToTmp);
            long diff = System.currentTimeMillis() - curTime;
            long linesCount = sqlRequestsWrapper.countLinesInTable(TMP_REQUEST_TABLE_NAME);
            logger.addMeasureResult("tmp_import_request", "load_task", linesCount, diff);

            curTime = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(partitionLog);
            long diffPart = System.currentTimeMillis() - curTime;
            logger.addMeasureResult("dest_import_request", "load_task", linesCount, diffPart);
            logger.addMeasureResult("import_request", "load_task", linesCount, diff+diffPart);
            logger.setTableState(REQUEST_LOG_NAME, 0, linesCount);

        } catch (SQLException e) {
            logger.write("Error: sqlExeption "+ e.getMessage());
        }
    }

    private void loadClickData(String sourceFile) {
        String importDataToTmp = "LOAD DATA local inpath '" + sourceFile + "' OVERWRITE INTO TABLE "+ TMP_CLICK_TABLE_NAME;
        String partitionLog =
                "FROM " + TMP_CLICK_TABLE_NAME+
                        " INSERT OVERWRITE TABLE " + CLICK_LOG_NAME +
                        " SELECT request_id, position_num, path, dt";
        try {
            long curTime = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(importDataToTmp);
            long diff = System.currentTimeMillis() - curTime;
            long linesCount = sqlRequestsWrapper.countLinesInTable(TMP_REQUEST_TABLE_NAME);
            logger.addMeasureResult("tmp_import_click", "load_task", linesCount, diff);

            curTime = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(partitionLog);
            long diffPart = System.currentTimeMillis() - curTime;
            logger.addMeasureResult("dest_import_click", "load_task", linesCount, diffPart);
            logger.addMeasureResult("import_click", "load_task", linesCount, diff+diffPart);
            logger.setTableState(REQUEST_LOG_NAME, 0, linesCount);
        } catch (SQLException e) {
            logger.write("Error: sqlExeption "+ e.getMessage());
        }
    }


    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
