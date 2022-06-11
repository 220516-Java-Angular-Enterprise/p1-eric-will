package com.revature.ers.dtos.requests;

import com.revature.ers.models.Users;

public class RejectUser {
    private String username;

    public RejectUser(){

    }

    public RejectUser(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Users extractUser(){
        Users out = new Users();
        out.setUsername(this.getUsername());
        out.setIs_active(false);
        out.setRole_id("BANNED");
        return out;
    }
}
