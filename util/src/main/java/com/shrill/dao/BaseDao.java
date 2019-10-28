package com.shrill.dao;

import java.sql.*;

public class BaseDao {

    /**
     * 数据库连接url
     */
    String url = "jdbc:mysql://localhost:3306/yxkj?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    Connection connection = null;
    PreparedStatement PreparedStatement = null;
    ResultSet rs = null;

    /**
     * 获取Connection对象
     *
     * @return
     */
    public Connection getConnection() {
        try {
            //连接驱动
            Class.forName("com.mysql.jdbc.Driver");
            //获取Connection对象
            connection = DriverManager.getConnection(url, "root", "12345678");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 关闭数据库连接
     */
    public void close() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (PreparedStatement != null) {
                PreparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据sql设置PreparedStatement对象
     *
     * @param sql
     */
    public void setPreparedStatement(String sql) {
        Connection con = getConnection();
        try {
            PreparedStatement = con.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取ResultSet对象
     *
     * @param sql
     * @return
     */
    public ResultSet getRs(String sql) {
        setPreparedStatement(sql);
        try {
            rs = PreparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 获取ResultSet对象
     *
     * @param sql
     * @return
     */
    public ResultSet getRs(String sql, Object Object) {
        setPreparedStatement(sql);
        try {
            //设置占位符的值
            PreparedStatement.setObject(1, Object);
            rs = PreparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 获取ResultSet对象
     *
     * @param sql
     * @return
     */
    public ResultSet getRs(String sql, Object[] Object) {
        setPreparedStatement(sql);
        try {
            //设置占位符的值
            for (int i = 0; i < Object.length; i++) {
                PreparedStatement.setObject(i + 1, Object[i]);
            }
            rs = PreparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 增，删，改操作
     *
     * @param sql
     * @param object
     * @return
     */
    public int executeUpdate(String sql, Object[] object) {
        try {
            setPreparedStatement(sql);
            if (object != null) {
                for (int i = 0; i < object.length; i++) {
                    PreparedStatement.setObject(i + 1, object[i]);
                }
                return PreparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
        return 0;
    }

    /**
     * 增，删，改操作
     *
     * @param sql
     * @param object
     * @return
     */
    public int executeUpdate(String sql, Object object) {
        try {
            setPreparedStatement(sql);
            if (object != null) {
                PreparedStatement.setObject(1, object);
                return PreparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
        return 0;
    }
}
