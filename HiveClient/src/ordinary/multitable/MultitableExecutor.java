package ordinary.multitable;

import ru.aptu.warehouse.tester.execution.TaskExecutor;
import ru.aptu.warehouse.tester.services.Logger;
import tools.SqlRequestsWrapper;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/29/12
 * Time: 9:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class MultitableExecutor implements TaskExecutor {
    private Logger logger;
    private SqlRequestsWrapper sqlRequestsWrapper;

    public MultitableExecutor() {
        this.sqlRequestsWrapper = new SqlRequestsWrapper();
    }

    @Override
    public void execute() {
        try {
            sqlRequestsWrapper.init();
            String createDetailedShowsTable =
                    "CREATE TABLE IF NOT EXISTS company_detailed_shows (" +
                            "dt STRING, company_id STRING, base STRING, region_id INT, shows_count INT, position_num INT)";
            String dropDetailedShowsTable = "DROP TABLE IF EXISTS company_detailed_shows";

            String createShowsTable =
                    "CREATE TABLE IF NOT EXISTS company_shows (" +
                            "dt STRING, company_id STRING, shows_count INT)";
            String dropShowsTable = "DROP TABLE IF EXISTS company_shows";

            String logProcess =
                    "FROM request_log" +
                            " INSERT OVERWRITE TABLE company_detailed_shows " +
                            "SELECT dt, company_id, base, region_id, position_num, count(1) as shows_count " +
                            "GROUP BY dt, company_id, base, region_id, position_num " +
                            "INSERT OVERWRITE TABLE company_shows " +
                            "SELECT dt, company_id, count(1) as shows_count " +
                            "GROUP BY dt, company_id";
            sqlRequestsWrapper.executeQuery(dropShowsTable);
            sqlRequestsWrapper.executeQuery(createShowsTable);
            sqlRequestsWrapper.executeQuery(dropDetailedShowsTable);
            sqlRequestsWrapper.executeQuery(createDetailedShowsTable);
            long time = System.currentTimeMillis();
            sqlRequestsWrapper.executeQuery(logProcess);
            long diff = System.currentTimeMillis() - time;
            long dataSize = logger.getTableState("request_log").getLinesCount();
            logger.addMeasureResult("whole_task", "two_part_task", "multitable", dataSize, diff);
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
