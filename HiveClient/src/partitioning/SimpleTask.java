package partitioning;

import ru.aptu.warehouse.tester.execution.TaskExecutor;
import ru.aptu.warehouse.tester.services.Logger;
import tools.SqlRequestsWrapper;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/29/12
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleTask implements TaskExecutor {
    private Logger logger;
    private SqlRequestsWrapper sqlRequestsWrapper;

    public SimpleTask() {
        this.sqlRequestsWrapper = new SqlRequestsWrapper();
    }

    @Override
    public void execute() {
        String tableName = "company_show";
        String dropTable = "DROP TABLE IF EXISTS " + tableName;
        String createTable =
                "CREATE TABLE IF NOT EXISTS "+ tableName +" (" +
                        "company_id STRING, count_shows INT, base STRING, region_id INT) " +
                        "PARTITIONED by (dt STRING) " +
                        "ROW FORMAT delimited " +
                        "fields terminated by '\t' " +
                        "lines terminated  by '\n' " +
                        "STORED AS textfile";
        String fillData =
                "FROM request_log " +
                        " INSERT OVERWRITE TABLE company_show " +
                        "PARTITION (dt) " +
                        "SELECT company_id, count(1) as count_shows, base, region_id, dt  " +
                        "GROUP BY dt, company_id, base, region_id";
        try {
            this.sqlRequestsWrapper.init();
            sqlRequestsWrapper.executeQuery(dropTable);
            sqlRequestsWrapper.executeQuery(createTable);
            long curTime = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(fillData);
            long diff = System.currentTimeMillis() - curTime;
            logger.addMeasureResult("aggregation", "simple_task",
                    logger.getTableState("request_log").getLinesCount(), diff);
        } catch (SQLException e) {
            logger.write(e.getMessage());
        } finally {
            sqlRequestsWrapper.close();
        }
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}

