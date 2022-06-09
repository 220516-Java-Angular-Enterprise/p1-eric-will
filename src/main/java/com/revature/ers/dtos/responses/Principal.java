package com.revature.ers.dtos.responses;

import com.revature.ers.models.Users;

public class Principal {
    private String user_id;
    private String username;
    private String role;

    public Principal (){ super();}

    public Principal(Users user) {
        this.user_id = user.getUser_id();
        this.username = user.getUsername();
        this.role = user.getRole_id();
    }

    public Principal(String user_id, String username, String role) {
        this.user_id = user_id;
        this.username = username;
        this.role = role;
    }

    public String getId() {
        return user_id;
    }

    public void setId(String id) {
        this.user_id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Principal{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
