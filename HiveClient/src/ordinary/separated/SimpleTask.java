package ordinary.separated;

import ru.aptu.warehouse.tester.execution.TaskExecutor;
import ru.aptu.warehouse.tester.services.Logger;
import tools.SqlRequestsWrapper;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/28/12
 * Time: 1:18 AM
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
                "CREATE TABLE IF NOT EXISTS " + tableName +
                        " AS SELECT " +
                        "dt, company_id, base, region_id, COUNT(1) as count_shows " +
                        "FROM request_log"  +
                        " GROUP BY dt, company_id, base, region_id ";
        try {
            this.sqlRequestsWrapper.init();
            sqlRequestsWrapper.executeQuery(dropTable);
            long curTime = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(createTable);
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
