package websocket.v2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBDao {
    public Connection start(){
        Connection conn=null;
        try {
            Class.forName("com.mysql.jc.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test1", "root", "root");
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }
}
