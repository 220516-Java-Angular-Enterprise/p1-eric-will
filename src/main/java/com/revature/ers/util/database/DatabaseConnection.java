package com.revature.ers.util.database;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/* Notice that this class is a Singleton design pattern. */
public class DatabaseConnection {
    /* instantiating Connection object */
    private static Connection con = null;

    /* instantiating Properties object to retrieve properties url, username, password */
    private static final Properties prop = new Properties();

    static {
        try {
            /* importing the jdbc jar file into jvm */
            Class.forName("org.postgresql.Driver");

            /* using prop object to load url, username, password */
            prop.load(new FileReader("src/main/resources/db.properties"));

            /* actually getting this connection */
            con = DriverManager.getConnection(prop.getProperty("url"), prop.getProperty("username"), prop.getProperty("password"));

            /* throw Exception if connection was not successful */
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /* getter for connection */
    public static Connection getCon() {
        return con;
    }
}