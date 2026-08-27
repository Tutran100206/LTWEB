package org.example.services;

import org.example.dao.UserDAO;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public boolean login(String username, String password) {
        return userDAO.checkLogin(username, password);
    }
}