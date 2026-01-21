import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public Connection getConnection() {
        try {
            String db_url = System.getenv("DB_URL");
            String db_user = System.getenv("DB_USER");
            String db_password = System.getenv("DB_PASSWORD");
            return DriverManager.getConnection(db_url, db_user, db_password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
