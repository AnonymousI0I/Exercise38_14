package Exercise34_01;

import java.sql.*;

/** Minimal DAO: creates the table on first run, then view / insert / update. */
public class StaffDAO {

    private static final String URL = "jdbc:sqlite:staff.db";   // file in cwd

    public StaffDAO() {
        try { createTableIfMissing(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    /* ----- internal helpers ----- */

    private Connection conn() throws SQLException { return DriverManager.getConnection(URL); }

    private void createTableIfMissing() throws SQLException {
        String ddl = """
            create table if not exists Staff(
              id        char(9)  primary key,
              lastName  varchar(15),
              firstName varchar(15),
              mi        char(1),
              address   varchar(20),
              city      varchar(20),
              state     char(2),
              telephone char(10),
              email     varchar(40)
            );""";
        try (Connection c = conn(); Statement s = c.createStatement()) {
            s.executeUpdate(ddl);
        }
    }

    /* ----- public API ----- */

    /** return ResultSet for caller to read & close */
    public ResultSet find(String id) throws SQLException {
        var ps = conn().prepareStatement("select * from Staff where id = ?");
        ps.setString(1, id);
        return ps.executeQuery();
    }

    /** insert a brand-new staff row */
    public void insert(Object[] v) throws SQLException {
        String sql = "insert into Staff values (?,?,?,?,?,?,?,?,?)";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < v.length; i++) ps.setObject(i + 1, v[i]);
            ps.executeUpdate();
        }
    }

    /** update every column except the primary-key id (v[8]) */
    public int update(Object[] v) throws SQLException {
        String sql = """
            update Staff set lastName=?, firstName=?, mi=?, address=?,
                              city=?, state=?, telephone=?, email=? where id=?""";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < v.length; i++) ps.setObject(i + 1, v[i]);
            return ps.executeUpdate();   // rows affected
        }
    }
}
