package com.lucene.jsoup.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC操作类
 * @author liangjilong
 *
 */
public class JDBC {
	final private static String USERNAME = "root";
	final private static String PASSWORD = "root";
	final private static String DRIVER = "com.mysql.jdbc.Driver";
	final private static String DBURL = "jdbc:mysql://localhost/weixin?characterEncoding=utf-8";
	
	private JDBC(){}
    static {//注册驱动
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
  
    /**
     * 获取数据库连接
     * @return
     * @throws SQLException
     */
    public static Connection getDBConn() throws SQLException {
        return DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
    }

    /**
     * 释放资源
     * @param rs
     * @param ps
     * @param conn
     */
	public static void closeDB(ResultSet rs,Statement ps,Connection conn){
		if(null!=rs){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(null!=ps){
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(null!=conn){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
