import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    static Connection con;
    static Statement stmt;
    static PreparedStatement pstmt;
    String Driver = "";
    static String url = "jdbc:mysql://localhost:3306/yut?&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false";
    static String userid = "root";
    static String pwd = "password";
    static ResultSet resultSet;

    public static void connection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("driver load success");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("DataBase connect ready...");
            con = DriverManager.getConnection(url, userid, pwd);
            System.out.println("DabaBase connect success");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }







    public static ResultSet dbExecuteQuery(String queryStmt) throws SQLException, ClassNotFoundException {
        try {
            System.out.println("SQL : " + queryStmt + "\n");
            stmt = con.createStatement();
            resultSet = stmt.executeQuery(queryStmt);
        }
        catch (SQLException e) {
            System.out.println("DBconnect - executeQuery fail");
            throw e;
        }
        finally {
        }
        return resultSet;
    }



    public static void dbExecuteUpdate(String sqlStmt) throws SQLException, ClassNotFoundException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sqlStmt);
        }
        catch (SQLException e) {
            System.out.println("DBconnect executeUpdate fail");
            throw e;
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }



    public static ResultSet dbExecuteQueryPstmt(String queryStmt) throws SQLException, ClassNotFoundException {
        try {
            System.out.println("SQL : " + queryStmt + "\n");
            pstmt=con.prepareStatement(queryStmt);
            resultSet = stmt.executeQuery(queryStmt);
        }
        catch (SQLException e) {
            System.out.println("DBconnect - executeQuery fail");
            throw e;
        }
        finally {
        }
        return resultSet;
    }


    public static void dbExecuteUpdatePstmt(String sqlStmt) throws SQLException, ClassNotFoundException {
        Statement stmt = null;
        try {
            pstmt = con.prepareStatement(sqlStmt);
            pstmt.executeUpdate(sqlStmt);
        }
        catch (SQLException e) {
            System.out.println("DBconnect executeUpdate fail");
            throw e;
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

}