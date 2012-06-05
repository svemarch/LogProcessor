package tools;

import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 3/24/12
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlRequestsWrapper {
    private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
    private Connection connection;
    private Statement statement;

    public SqlRequestsWrapper() {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:hive://localhost:10000/default", "", "");
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public boolean init() throws SQLException {
        statement = connection.createStatement();
        return true;
    }

    public void close() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    
    public ResultSet executeQuery(String query) throws SQLException {
        return statement.executeQuery(query);
    }
    
    public long countLinesInTable(String tableName) throws SQLException {
        String query = "SELECT count(*) as count from "+tableName;
        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();
        return resultSet.getLong("count");
    }

}
