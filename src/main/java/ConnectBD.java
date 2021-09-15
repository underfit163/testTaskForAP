import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectBD {

    private static Properties prop;

    public Connection getConnection() throws SQLException, IOException {
        if (prop == null) {
            prop = new Properties();
            FileInputStream in = new FileInputStream("src/main/resources/database.properties");
            prop.load(in);
            in.close();
        }

        String drivers = prop.getProperty("jdbc.drivers");
        if (drivers != null) {
            System.setProperty("jdbc.drivers", drivers);
        }
        String url = prop.getProperty("jdbc.url");
        String username = prop.getProperty("jdbc.username");
        String password = prop.getProperty("jdbc.password");
        Connection conn = DriverManager.getConnection(url, username, password);
        System.out.println("Successfully connected");
        return conn;
    }
}
