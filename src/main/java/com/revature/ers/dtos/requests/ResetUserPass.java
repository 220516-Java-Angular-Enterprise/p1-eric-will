package com.revature.ers.dtos.requests;

import com.revature.ers.models.Users;

public class ResetUserPass {
    private String username;

    public ResetUserPass(){

    }

    public ResetUserPass(String username) {
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
        return out;
    }
}
