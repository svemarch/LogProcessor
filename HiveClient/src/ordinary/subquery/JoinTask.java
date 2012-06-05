package ordinary.subquery;

import ru.aptu.warehouse.tester.execution.TaskExecutor;
import ru.aptu.warehouse.tester.services.Logger;
import tester.launcher.environment.ProcessDataAction;
import tools.SqlRequestsWrapper;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/29/12
 * Time: 8:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class JoinTask implements TaskExecutor {
    private Logger logger;
    private SqlRequestsWrapper sqlRequestsWrapper;

    public JoinTask() {
        this.sqlRequestsWrapper = new SqlRequestsWrapper();
    }

    @Override
    public void execute() {
        String dropTable = "DROP TABLE IF EXISTS company_clicks";
        String aggregateClicks =
                "CREATE TABLE IF NOT EXISTS company_clicks " +
                        "AS SELECT dt, company_id, base, region_id, path, count (1) as count_clicks " +
                        "FROM (" +
                        "SELECT s.dt, s.company_id, s.region_id, s.base, r.path " +
                        "FROM request_log s JOIN click_log r " +
                        "ON (s.dt = r.dt AND s.request_id=r.request_id AND s.position_num=r.position_num ) ) aggr_clicks " +
                        "GROUP BY dt, company_id, region_id, base, path";
        try{
            sqlRequestsWrapper.init();
            sqlRequestsWrapper.executeQuery(dropTable);
            long time = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(aggregateClicks);
            long diff = System.currentTimeMillis() - time;
            long dataSize = logger.getTableState("request_log").getLinesCount() 
                    + logger.getTableState("click_log").getLinesCount();
            logger.addMeasureResult("whole_task", "join_task", dataSize, diff);
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
