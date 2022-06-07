package com.revature.ers.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.ers.dtos.requests.NewUserRequest;
import com.revature.ers.services.UsersServices;
import com.revature.ers.util.annotations.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class UserServlet extends HttpServlet {
    @Inject
    private final ObjectMapper mapper;
    private final UsersServices userService;

    @Inject
    public UserServlet(ObjectMapper mapper, UsersServices userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            NewUserRequest request = mapper.readValue(req.getInputStream(), NewUserRequest.class);
            //User createdUser = userService.register(request);
            //resp.setStatus(201); // CREATED
            //resp.setContentType("application/json");
            //resp.getWriter().write(mapper.writeValueAsString(createdUser.getId()));
//        } catch (InvalidRequestException e) {
            //resp.setStatus(404); // BAD REQUEST
//        } catch (ResourceConflictException e) {
 //           resp.setStatus(409); // RESOURCE CONFLICT
        } catch (Exception e) {
            e.printStackTrace();
           resp.setStatus(500);
        }
    }
}
