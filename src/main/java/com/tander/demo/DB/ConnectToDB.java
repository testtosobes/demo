package com.tander.demo.DB;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectToDB {
    public Connection getConnection() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/tander", "postgres", "1234");
            c.setAutoCommit(false);
            System.out.println("-- Opened database successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return c;
    }
}
