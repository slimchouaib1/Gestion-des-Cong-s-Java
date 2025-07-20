package tn.bfpme.utils;

import java.sql.*;

public class MyDataBase {
    private static MyDataBase instance;

    /*
     private final String URL = "jdbc:mysql://bfpmeconge.cj4ciugme4xd.eu-west-3.rds.amazonaws.com:3306/bfpmeconge";
     private final String USERNAME = "admin";
     private final String PASSWORD = "ALAmoussa123";
    */

    //private final String URL = "jdbc:mysql://192.168.1.68:3306/bfpmeconge";
    //private final String URL = "jdbc:mysql://10.10.50.94:3306/bfpmeconge";
    private final String URL = "jdbc:mysql://127.0.0.1:3306/bfpmeconge";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    private Connection cnx;

    private MyDataBase() {
        try {
            cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("DATABASE: Successful");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("DATABASE: Failed");
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnx;
    }

}
