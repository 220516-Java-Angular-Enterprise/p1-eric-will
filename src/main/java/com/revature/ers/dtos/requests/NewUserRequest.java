package com.revature.ers.dtos.requests;

import com.revature.ers.models.Users;

public class NewUserRequest {
    private String username;
    private String password;
    private String email;
    private String given_name;
    private String surname;
    private final boolean is_active  = false;
    private String role;

    public NewUserRequest(){
        super();
    }

    public NewUserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public NewUserRequest(String username, String password, String email, String given_name, String surname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.given_name = given_name;
        this.surname = surname;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Users extractUser(){
        Users newUser=  new Users(username, null, role);
        newUser.setPassword(this.password);
        newUser.setEmail(this.email);
        newUser.setGiven_name(this.given_name);
        newUser.setIs_active(this.is_active);
        newUser.setSurname(this.surname);
        return newUser;
    }
}
