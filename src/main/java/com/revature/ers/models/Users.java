package com.revature.ers.models;

public class Users {

    private String user_id;
    private String username;
    private String email;
    private String password;
    private String given_name;
    private String surname;
    private boolean is_active;
    private String role_id;

    public Users(){

    }

    public Users(String user_id, String username, String email, String password, String given_name, String surname, boolean is_active, String role_id) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.given_name = given_name;
        this.surname = surname;
        this.is_active = is_active;
        this.role_id = role_id;
    }

    public Users(String user_id, String username, String email, String password, String given_name, String surname) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.given_name = given_name;
        this.surname = surname;
    }

    public Users(String username, String id, String role) {
        this.username = username;
        this.role_id = id;
        this.role_id = role;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }
}
