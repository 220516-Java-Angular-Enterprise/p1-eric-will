package com.revature.ers.dtos.requests;

import com.revature.ers.models.Users;

public class NewUserRequest {
    private String username;
    private String password;

    private final String role = "DEFAULT";

    public NewUserRequest(){
        super();
    }

    public NewUserRequest(String username, String password) {
        this.username = username;
        this.password = password;
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

    public String getRole() {
        return role;
    }

    public Users extractUser(){
        Users newUser=  new Users(username, null, role);
        newUser.setPassword(this.password);
        return newUser;
    }
}
