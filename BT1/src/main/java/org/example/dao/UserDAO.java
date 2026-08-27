package org.example.dao;

public class UserDAO {

    public boolean checkLogin(String username, String password) {
        return username.equals("admin") && password.equals("123");
    }
}