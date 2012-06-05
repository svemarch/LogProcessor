package tasks;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/30/12
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResultLoader {
    private JdbcTemplate jdbcTemplate;

    public ResultLoader() throws SQLException {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(
                "jdbc:mysql://localhost/preprocessing_test",
                "root",
                "");
        driverManagerDataSource.setDriverClassName("org.gjt.mm.mysql.Driver");
        this.jdbcTemplate = new JdbcTemplate(driverManagerDataSource);
        driverManagerDataSource.getConnection();
    }
    
    public void loadResult(String sourceFile, String tableName) {
        jdbcTemplate.update("LOAD DATA LOCAL INFILE '" + sourceFile +"' IGNORE INTO TABLE "+tableName);
    }
   
}
