package com.seeyon.apps.ext.batchupdate.util;

import java.sql.*;

public class JdbcTool {
    /**
     * 获取连接
     *
     * @return
     */
    public static Connection getMidConnection() {
        PropUtil configTools = new PropUtil();
        String driverName = configTools.getValueByKey("midDataLink.driver");
        String url = configTools.getValueByKey("midDataLink.url");
        String username = configTools.getValueByKey("midDataLink.username");
        String password = configTools.getValueByKey("midDataLink.password");
        Connection connection = null;
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * @param connection
     */
    public static void closeConnection(Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closePrepareStatement(PreparedStatement ps) {
        if (null != ps) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeResultSet(ResultSet ps) {
        if (null != ps) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void closeStatement(Statement ps) {
        if (null != ps) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
