package com.example.moneymanager.database;

public class UserBean {
    int id;
    String username;
    String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserBean() {
    }

    public UserBean(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
