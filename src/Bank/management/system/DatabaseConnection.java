package Bank.management.system;

import java.sql.*;

public class DatabaseConnection implements AutoCloseable {
    private Connection c;
    private Statement s;

    public DatabaseConnection()throws  SQLException{
            c = DriverManager.getConnection("jdbc:mysql:///bankmagementsystem", "root", "root123");
            s = c.createStatement();

    }
    public Statement getStatement() {
        return s;
    }
    public Connection getConnection() {
        return c;
    }

    @Override
    public void close() throws SQLException {
        if (s != null && !s.isClosed()) s.close();
        if (c != null && !c.isClosed()) c.close();
    }
}
