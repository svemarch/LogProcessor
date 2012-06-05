package bucketing;

import ru.aptu.warehouse.tester.execution.TaskExecutor;
import ru.aptu.warehouse.tester.services.Logger;
import tools.SqlRequestsWrapper;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/29/12
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class JoinTask  implements TaskExecutor {
    private Logger logger;
    private SqlRequestsWrapper sqlRequestsWrapper;

    public JoinTask() {
        this.sqlRequestsWrapper = new SqlRequestsWrapper();
    }

    @Override
    public void execute() {
        String createCompanyClicksTable =
                "CREATE TABLE company_clicks_tmp " +
                        "AS SELECT s.dt, s.company_id, s.region_id, s.base, c.path " +
                        "FROM request_log s JOIN click_log c " +
                        "ON (s.dt = c.dt AND s.request_id = c.request_id AND s.position_num = c.position_num)";

        try {
            sqlRequestsWrapper.init();
            sqlRequestsWrapper.executeQuery("drop table IF EXISTS company_clicks_tmp");

            if (logger.getTableState("company_clicks_tmp") == null) {
                logger.createTable("company_clicks_tmp", "table");
            }
            logger.setTableState("company_clicks_tmp", 0, 0);

            long time = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(createCompanyClicksTable);
            long diff = System.currentTimeMillis() - time;
            long dataSize = logger.getTableState("click_log").getLinesCount()
                    + logger.getTableState("request_log").getLinesCount();
            logger.addMeasureResult("join", "join_task", dataSize, diff);
            long tableSize = sqlRequestsWrapper.countLinesInTable("company_clicks_tmp");
            logger.setTableState("company_clicks_tmp", 0, tableSize);


            String aggregateClicks =
                    "CREATE TABLE company_clicks AS SELECT " +
                            "dt, company_id, base, region_id, path, count(1) as count_clicks " +
                            "FROM company_clicks_tmp " +
                            "GROUP BY dt, company_id, path, region_id, base";

            sqlRequestsWrapper.executeQuery("drop table IF EXISTS company_clicks");
            if (logger.getTableState("company_clicks") == null) {
                logger.createTable("company_clicks", "table");
            }
            logger.setTableState("company_clicks", 0, 0);

            time = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(aggregateClicks);
            long aggrDiff = System.currentTimeMillis() - time;
            long resultDataSize = logger.getTableState("company_clicks_tmp").getLinesCount();
            logger.addMeasureResult("aggregation", "join_task", resultDataSize, aggrDiff);
            long clicksSize = sqlRequestsWrapper.countLinesInTable("company_clicks");
            logger.setTableState("company_clicks", 0, clicksSize);

            logger.addMeasureResult("whole_task", "join_task", dataSize+resultDataSize, diff+aggrDiff);

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
