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

    public String selectFunction(String command, int columnsLength) {
        try {
            ResultSet resultSet = this.statement.executeQuery(command);

        StringBuilder result = new StringBuilder();

            while (resultSet.next()) {
                for (int i = 1; i <= columnsLength; i++) {
                    result.append(resultSet.getString(i)).append("_");
                }
                result.append(";");
            }
            return result.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void executeFunction(String command) {
        try {
            statement.execute(command);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
