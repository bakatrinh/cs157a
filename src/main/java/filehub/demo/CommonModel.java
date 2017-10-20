package filehub.demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class CommonModel {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://p3plcpnl0569.prod.phx3.secureserver.net:3306/cs157a";
    static final String USER = "cs157a_main";
    static final String PASS = "cs157a_db";

//    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/cs157a?useSSL=false";
//    static final String USER = "root";
//    static final String PASS = "1234";

    public static Boolean isLettersNumbersUnderscoreOnlyString(String string) {
        String regex = "^[a-zA-Z0-9_]*$";
        return string.matches(regex);
    }

    public static Boolean isLettersNumbersUnderscoreSpaceOnlyString(String string) {
        String regex = "^[a-zA-Z0-9_. ]*$";
        return string.matches(regex);
    }

    public static Boolean isValidFileName(String string) {
        String regex = "^[a-zA-Z0-9_.)('\\[\\] ]*$";
        return string.matches(regex);
    }

    public static Boolean isValidEmailString(String string) {
        String regex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        return string.matches(regex);
    }

    public static String timeStampToFormalDate(String timestamp) throws ParseException {
        String returnString = "";
        if (timestamp != null && !timestamp.isEmpty()) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormatter.parse(timestamp);

            DateFormat targetFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");
            returnString = targetFormat.format(date);
        }
        return returnString;
    }

    public static Boolean isLoggedIn(HttpServletRequest request, HttpSession session) {
        Boolean returnBoolean = false;
        if (request.getSession() != null && session.getAttribute("user_id") != null) {
            returnBoolean = true;
        }
        return returnBoolean;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String todayDateInYMD() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static String todayDateInMDY() {
        return new SimpleDateFormat("MM-dd-yyy").format(new Date());
    }

    public static String timestampInSQLFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static String getFullName(String user_id) {
        String returnString = "";
        if (user_id == null || user_id.isEmpty()) {
            return returnString;
        }
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(CommonModel.JDBC_DRIVER).newInstance();

            conn = DriverManager.getConnection(CommonModel.DB_URL, CommonModel.USER, CommonModel.PASS);

            stmt = conn.createStatement();
            String myQuery;
            myQuery = "SELECT first_name,last_name FROM user " +
                    "WHERE (id='" + user_id + "' AND login_status='Active')";
            ResultSet sqlResult = stmt.executeQuery(myQuery);
            if (sqlResult != null && sqlResult.next()) {
                String first_name = sqlResult.getString(1);
                String last_name = sqlResult.getString(2);
                if (first_name != null && !first_name.isEmpty()) {
                    returnString = returnString + first_name;
                }
                if (last_name != null && !last_name.isEmpty()) {
                    returnString = returnString + " " + last_name;
                }
                sqlResult.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                conn.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return returnString;
    }

    public static boolean isInGroup(int user_id, int group_id) {
        boolean returnBoolean = false;
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(CommonModel.JDBC_DRIVER).newInstance();

            conn = DriverManager.getConnection(CommonModel.DB_URL, CommonModel.USER, CommonModel.PASS);

            stmt = conn.createStatement();
            String myQuery;
            myQuery = "SELECT * FROM group_members WHERE (user_id='" + user_id + "' AND group_id='" + group_id + "')";
            ResultSet sqlResult = stmt.executeQuery(myQuery);
            if (sqlResult != null) {
                if (sqlResult.isBeforeFirst()) {
                    returnBoolean = true;
                } else {
                    returnBoolean = false;
                }
                sqlResult.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                conn.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return returnBoolean;
    }

    public static String getGroupName(String group_id) {
        String returnString = "";
        if (group_id == null || group_id.isEmpty()) {
            return returnString;
        }
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(CommonModel.JDBC_DRIVER).newInstance();

            conn = DriverManager.getConnection(CommonModel.DB_URL, CommonModel.USER, CommonModel.PASS);

            stmt = conn.createStatement();
            String myQuery;
            myQuery = "SELECT group_name FROM groups " +
                    "WHERE (id='" + group_id + "')";
            ResultSet sqlResult = stmt.executeQuery(myQuery);
            if (sqlResult != null && sqlResult.next()) {
                returnString = sqlResult.getString(1);
                sqlResult.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                conn.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return returnString;
    }

    public static boolean isCodeAlreadyOnDB(String code) {
        boolean returnBoolean = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName(CommonModel.JDBC_DRIVER).newInstance();

            conn = DriverManager.getConnection(CommonModel.DB_URL, CommonModel.USER, CommonModel.PASS);

            String myQuery;
            myQuery = "SELECT url_code FROM file_url WHERE (url_code = ?)";
            pstmt = conn.prepareStatement(myQuery);
            pstmt.setString(1, code);
            ResultSet sqlResult = pstmt.executeQuery();
            if (sqlResult != null) {
                if (sqlResult.isBeforeFirst()) {
                    returnBoolean = true;
                }
                sqlResult.close();
            }
            if (!returnBoolean) {
                String myQuery2;
                myQuery2 = "SELECT id FROM group_invites WHERE (id = ?)";
                pstmt = conn.prepareStatement(myQuery2);
                pstmt.setString(1, code);
                ResultSet sqlResult2 = pstmt.executeQuery();
                if (sqlResult2 != null) {
                    if (sqlResult2.isBeforeFirst()) {
                        returnBoolean = true;
                    }
                    sqlResult2.close();
                }
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                conn.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return returnBoolean;
    }

    public static String getCode() {
        String returnString = "";
        Random r = new Random();

        String characters = "123456789abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
        for (int i = 0; i < 10; i++) {
            returnString = returnString + characters.charAt(r.nextInt(characters.length()));
        }
        return returnString;
    }

    public static String generateRandomCode() {
        String returnString = getCode();
        while (isCodeAlreadyOnDB(returnString)) {
            returnString = getCode();
        }
        return returnString;
    }
}