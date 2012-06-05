package ordinary.serde;

import ru.aptu.warehouse.tester.execution.LoadExecutor;
import ru.aptu.warehouse.tester.services.Logger;
import tools.SqlRequestsWrapper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/29/12
 * Time: 9:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegexSerdeLoadExecutor implements LoadExecutor {
    private SqlRequestsWrapper sqlRequestsWrapper;
    private Logger logger;

    public RegexSerdeLoadExecutor() {
        this.sqlRequestsWrapper = new SqlRequestsWrapper();

    }

    public static String REQUEST_LOG_NAME = "request_log";
    public static String CLICK_LOG_NAME = "click_log";

    @Override
    public void init() {
        try {
            sqlRequestsWrapper.init();
            sqlRequestsWrapper.executeQuery("CREATE TABLE IF NOT EXISTS "+ REQUEST_LOG_NAME +" (" +
                    "dt STRING, request_id STRING," +
                    "position_num INT, company_id STRING," +
                    "base STRING, region_id INT)" +
                    "ROW FORMAT SERDE 'serde.regex.RegexSerDe' " +
                    "WITH SERDEPROPERTIES (" +
                    "\"input.log\" = \"showLog\" ) ");
            sqlRequestsWrapper.executeQuery("CREATE TABLE IF NOT EXISTS "+ CLICK_LOG_NAME +" (" +
                    "dt STRING, request_id STRING," +
                    "position_num INT, path STRING ) " +
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

    }

    @Override
    public void clear() {
        try {
            sqlRequestsWrapper.init();
            sqlRequestsWrapper.executeQuery("DROP TABLE IF EXISTS " + REQUEST_LOG_NAME);
            sqlRequestsWrapper.executeQuery("DROP TABLE IF EXISTS " + CLICK_LOG_NAME);

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
        String importShowData = "LOAD DATA local inpath '" + sourceFile + "' INTO TABLE " + REQUEST_LOG_NAME;
        long curTime = System.currentTimeMillis();
        try {
            sqlRequestsWrapper.executeQuery(importShowData);
            long diff = System.currentTimeMillis() - curTime;
            long linesCount = sqlRequestsWrapper.countLinesInTable(REQUEST_LOG_NAME);
            logger.setTableState(REQUEST_LOG_NAME, 0, linesCount);
            logger.addMeasureResult("import_request", "load_task", linesCount, diff);
        } catch (SQLException e) {
            logger.write("Error: sqlExeption "+ e.getMessage());
            return;
        }
    }

    private void loadClickData(String sourceFile) {
        String importShowData = "LOAD DATA local inpath '" + sourceFile + "' INTO TABLE " + CLICK_LOG_NAME;
        long curTime = System.currentTimeMillis();
        try {
            sqlRequestsWrapper.executeQuery(importShowData);
            long diff = System.currentTimeMillis() - curTime;
            long linesCount = sqlRequestsWrapper.countLinesInTable(CLICK_LOG_NAME);
            logger.setTableState(CLICK_LOG_NAME, 0, linesCount);
            logger.addMeasureResult("import_click", "load_task", linesCount, diff);
        } catch (SQLException e) {
            logger.write("Error: sqlExeption "+ e.getMessage());
            return;
        }
    }


    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
