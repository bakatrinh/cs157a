package filehub.demo;

import java.sql.*;
import java.util.ArrayList;

public class FileModel {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://p3plcpnl0569.prod.phx3.secureserver.net:3306/cs157a";
    static final String USER = "cs157a_main";
    static final String PASS = "cs157a_db";

    public static ArrayList<Groups> isInGroup() {
        ArrayList<Groups> returnGroup = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER).newInstance();

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();
            String myQuery;
            myQuery = "SELECT * FROM groups";
            ResultSet allGroup = stmt.executeQuery(myQuery);

            if (allGroup != null) {
                try {
                    while (allGroup.next()) {
                        int id = allGroup.getInt("id");
                        String group_name = allGroup.getString("group_name");
                        int group_owner = allGroup.getInt("group_owner");
                        String group_password = allGroup.getString("group_password");
                        String group_status = allGroup.getString("group_status");
                        String created_on = allGroup.getString("created_on");
                        System.out.println(created_on);
                        returnGroup.add(new Groups(id, group_name, group_owner, group_password, group_status, created_on));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            stmt.close();
            conn.close();
            allGroup.close();
            return returnGroup;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return null;
    }

    public static void insertGroupTest() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER).newInstance();

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();
            String myQuery;
            myQuery = "INSERT INTO groups (group_name, group_owner, group_password, group_status) " +
                    "VALUES ('chicken', 3, 'frog', 'Active');";
            stmt.executeUpdate(myQuery);

            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}