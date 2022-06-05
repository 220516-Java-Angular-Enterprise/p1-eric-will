package com.revature.ers.models;

public class UserRoles {

    private String role_id;
    private String role;

    public UserRoles(){

    }

    public UserRoles(String role_id, String role) {
        this.role_id = role_id;
        this.role = role;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
