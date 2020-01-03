package com.zhangshen147;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 建立和关闭数据库连接
 */
public class JdbcUtil {
    public static String DRIVERNAME = null;
    public static String URL = null;
    public static String USER = null;
    public static String PASSWORD = null;
    public static Connection conn = null;

    static {
        try {
            Properties props = new Properties();
            InputStream in = JdbcUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
            assert in != null;
            props.load(in);
            DRIVERNAME = props.getProperty("drivername");
            URL = props.getProperty("url");
            USER = props.getProperty("user");
            PASSWORD = props.getProperty("password");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName(DRIVERNAME);
                conn = DriverManager.getConnection(URL,USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeResource(Connection conn, PreparedStatement st) {
        try {
            if (st != null) {
                st.close();
            }
            if(conn!=null){
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void closeResource(Connection conn, ResultSet rs, PreparedStatement st) throws SQLException {
        try {
            if(rs!=null){
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if(conn!=null){
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
