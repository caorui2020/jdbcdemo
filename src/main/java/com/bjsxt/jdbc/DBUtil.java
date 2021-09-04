package com.bjsxt.jdbc;

import org.apache.commons.beanutils.BeanUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class DBUtil {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

//    static{
//        ResourceBundle bundle = ResourceBundle.getBundle("db");
//        driver = bundle.getString("jdbc.driver");
//        url = bundle.getString("jdbc.url");
//        username = bundle.getString("jdbc.username");
//        password = bundle.getString("jdbc.password");
//        try {
//            Class.forName(driver);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    static {
        InputStream is = DBUtil.class.getResourceAsStream("/db.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
            driver = properties.getProperty("jdbc.driver");
            url = properties.getProperty("jdbc.url");
            username = properties.getProperty("jdbc.username");
            password = properties.getProperty("jdbc.password");
            Class.forName(driver);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }

    }

    public static Connection getConnection(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url,username,password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public static void closeAll(ResultSet rs, Statement statement,Connection conn){
        if (rs != null){
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (statement != null){
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (conn != null){
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static void rollback(Connection conn){
        try {
            conn.rollback();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static int executeUpdate(String sql,Object[] param) throws Exception{
        Connection connection = null;
        PreparedStatement ps = null;
        int rows = 0;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(sql);
            int parameterCount = ps.getParameterMetaData().getParameterCount();
            if (parameterCount > (param == null?0:param.length)){
                throw new Exception("参数不正确");
            }
            for (int i = 0; i < parameterCount; i++) {
                ps.setObject(i+1,param[i]);
            }
            rows = ps.executeUpdate();
            connection.commit();
        } catch (SQLException throwables) {
            rollback(connection);
            throwables.printStackTrace();
        } finally {
            closeAll(null,ps,connection);
        }
        return rows;
    }

    public static <T> List<T> executeQuery(String sql,Object[] param,Class<T> tClass) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<T> list = new ArrayList<>();
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            int parameterCount = ps.getParameterMetaData().getParameterCount();
            if (parameterCount > (param == null?0:param.length)){
                throw new Exception("参数不正确");
            }
            for (int i = 0; i < parameterCount; i++) {
                ps.setObject(i+1,param[i]);
            }
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()){
                T t = tClass.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = metaData.getColumnName(i+1);
                    Object value = rs.getObject(i+1);
//                    Object value = rs.getObject(columnName);
                    BeanUtils.setProperty(t,columnName,value);
                }
                list.add(t);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(rs,ps,conn);
        }
        return list;
    }
}
