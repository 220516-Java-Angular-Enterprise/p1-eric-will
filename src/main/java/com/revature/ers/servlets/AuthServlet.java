package com.revature.ers.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.ers.dtos.requests.LoginRequest;
import com.revature.ers.dtos.responses.Principal;
import com.revature.ers.services.TokenServices;
import com.revature.ers.services.UsersServices;
import com.revature.ers.util.annotations.Inject;
import com.revature.ers.util.custom_exceptions.InvalidAuthenticationException;
import com.revature.ers.util.custom_exceptions.InvalidRequestException;
import com.revature.ers.util.custom_exceptions.NotAuthorizedException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Timestamp;

public class AuthServlet extends HttpServlet {
    @Inject
    private final ObjectMapper mapper;
    private final UsersServices userService;
    private final TokenServices tokenServices;
    @Inject
    public AuthServlet(ObjectMapper mapper, UsersServices userService,TokenServices tokenServices){
        this.mapper=mapper;
        this.userService=userService;
        this.tokenServices = tokenServices;
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpStrings httpStrings = new HttpStrings();
        try {
            LoginRequest request = mapper.readValue(req.getInputStream(), LoginRequest.class);
            Principal principal = new Principal(userService.login(request));

            // give token to user by puting token  in header

            String token = tokenServices.generateToken(principal);
            resp.setHeader("Authorization", token);
            resp.setContentType("application/json");
            resp.getWriter().write(mapper.writeValueAsString(principal));
            System.out.println(request.getUsername() + " has logged in." + (new Timestamp(System.currentTimeMillis())) );
            resp.setStatus(200);
        } catch (InvalidRequestException e) {
            resp.getWriter().write(httpStrings.fourOFour(req.getRequestURL().toString()));
            resp.setStatus(404);
        } catch (InvalidAuthenticationException e){
            resp.getWriter().write(httpStrings.httpStr(401, "Invalid Authentication", "Invalid Authorization"));
            resp.setStatus(401);
        }  catch (NotAuthorizedException e){
            resp.setContentType("application/html");
            resp.getWriter().write(httpStrings.httpStr(403, "Not Authorized", "You are not authorize to access this page"));
            resp.setStatus(403);
        }  catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        resp.getWriter().write("I am an auth servlet.  Woof.");
    }

}
