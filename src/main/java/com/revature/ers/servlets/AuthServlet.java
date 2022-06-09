package com.revature.ers.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.ers.dtos.requests.LoginRequest;
import com.revature.ers.dtos.responses.Principal;
import com.revature.ers.services.UsersServices;
import com.revature.ers.util.annotations.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AuthServlet extends HttpServlet {
    @Inject
    private final ObjectMapper mapper;
    private final UsersServices userService;
    @Inject
    public AuthServlet(ObjectMapper mapper, UsersServices userService){
        this.mapper=mapper;
        this.userService=userService;
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            LoginRequest request = mapper.readValue(req.getInputStream(), LoginRequest.class);
            Principal principal = new Principal(userService.login(request));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        resp.getWriter().write("I am an auth servlet.  Woof.");
    }
}
