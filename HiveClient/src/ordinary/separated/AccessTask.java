package ordinary.separated;

import ru.aptu.warehouse.tester.execution.TaskExecutor;
import ru.aptu.warehouse.tester.services.Logger;
import tools.SqlRequestsWrapper;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/28/12
 * Time: 1:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class AccessTask implements TaskExecutor {
    private Logger logger;
    private SqlRequestsWrapper sqlRequestsWrapper;
    
    public AccessTask() {
        sqlRequestsWrapper = new SqlRequestsWrapper();        
    }
    
    @Override
    public void execute() {
        String query =
                "SELECT * " +
                        "FROM request_log" +
                        " WHERE dt='2012-04-04' and region_id = 10000";
        String queryAllFilters =
                "SELECT * " +
                        "FROM request_log" +
                        " WHERE dt='2012-04-04' AND region_id=225 AND base !='yabs'";
        String querySorting =
                "SELECT * " +
                        "FROM request_log" +
                        " WHERE dt >= '2012-04-03' AND dt <= '2012-04-07' AND " +
                        "region_id = 10000 AND base = 'yabs' " +
                        "SORT BY dt ASC";
        String queryAggregating =
                "SELECT company_id, base, count(1) " +
                        "FROM request_log" +
                        " WHERE dt >= '2012-04-03' AND dt <= '2012-04-08' AND " +
                        "region_id = 225 " +
                        "GROUP BY company_id, base";
        try {
            sqlRequestsWrapper.init();
            long tableSize = logger.getTableState("request_log").getLinesCount();
            long time = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(query);
            long diff = System.currentTimeMillis() - time;
            logger.addMeasureResult("select(2)", "access_task", tableSize, diff);

            time = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(queryAllFilters);
            diff = System.currentTimeMillis() - time;
            logger.addMeasureResult("select(3)", "access_task", tableSize, diff);

            time = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(querySorting);
            diff = System.currentTimeMillis() - time;
            logger.addMeasureResult("all(sort)", "access_task", tableSize, diff);

            time = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(queryAggregating);
            diff = System.currentTimeMillis() - time;
            logger.addMeasureResult("all(aggregate)", "access_task", tableSize, diff);
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
