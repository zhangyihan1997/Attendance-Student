package com.yukino.utils;

import java.sql.*;

public abstract class DatabaseHelper {
    private Connection connection;
    private Statement statement;

    public DatabaseHelper() {
        this.connect();
    }

    void setConnection(Connection connection) {
        this.connection = connection;
    }

    public abstract boolean connect();

    public void queryNoReturn(String query) throws SQLConnectionException {
        if (connection == null) {
            // throw exception if SQLite not successful connect.
            throw new SQLConnectionException("SQLite connection error.");
        } else {

            try {
                statement = connection.createStatement();
                statement.execute(query);
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public ResultSet queryWithReturn(String query) throws SQLConnectionException {
        ResultSet rs = null;
        if (connection == null) {
            // throw exception if SQLite not successful connect.
            throw new SQLConnectionException("SQLite connection error.");
        } else {
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return rs;
        }
    }

    public void queryUpdate(String query) throws SQLConnectionException {
        if (connection == null) {
            // throw exception if SQLite not successful connect.
            throw new SQLConnectionException("SQLite connection error.");
        }
        else {
            try {
                statement = connection.createStatement();
                statement.executeUpdate(query);
                connection.commit(); // This statement cause SQLException
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
