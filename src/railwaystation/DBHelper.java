package railwaystation;

import java.sql.*;

public class DBHelper {

    private static final String URL = "jdbc:sqlserver://localhost;database=RailwayStation;encrypt=false;";
    private static final String USER = "railwaystation";
    private static final String PASSWORD = "123456";
    private static final DBHelper INSTANCE = new DBHelper();
    private Statement statement;
    private DBHelper() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DBHelper getInstance() {
        return INSTANCE;
    }

    public void close() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
