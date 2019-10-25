package com.yukino.utils;

import java.sql.*;

public class MySQLHelper extends DatabaseHelper {
    public MySQLHelper() {
        super();
    }

    @Override
    public boolean connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            setConnection(DriverManager.getConnection("jdbc:mysql://47.102.105.203:3306/android","admin","1234567"));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
