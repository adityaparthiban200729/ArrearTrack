package in.college.arreartrack.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Small JDBC connection factory; credentials are read from environment variables, never source code. */
public final class Database {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/arreartrack?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private Database() { }

    public static Connection getConnection() throws SQLException {
        String url = env("DB_URL", DEFAULT_URL);
        String user = env("DB_USER", "root");
        String password = env("DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null ? fallback : value;
    }
}
