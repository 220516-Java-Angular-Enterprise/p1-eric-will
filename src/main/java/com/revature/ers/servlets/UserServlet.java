package com.revature.ers.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.ers.dtos.requests.ApproveNewUser;
import com.revature.ers.dtos.requests.NewUserRequest;
import com.revature.ers.dtos.requests.RejectUser;
import com.revature.ers.dtos.requests.ResetUserPass;
import com.revature.ers.dtos.responses.Principal;
import com.revature.ers.models.Users;
import com.revature.ers.services.TokenServices;
import com.revature.ers.services.UsersServices;
import com.revature.ers.util.annotations.Inject;
import com.revature.ers.util.custom_exceptions.InvalidRequestException;
import com.revature.ers.util.custom_exceptions.ResourceConflictException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UserServlet extends HttpServlet {
    @Inject
    private final ObjectMapper mapper;
    private final UsersServices userService;
    private final TokenServices tokenServices;

    @Inject
    public UserServlet(ObjectMapper mapper, UsersServices userService, TokenServices tokenServices) {
        this.mapper = mapper;
        this.userService = userService;
        this.tokenServices = tokenServices;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            NewUserRequest request = mapper.readValue(req.getInputStream(), NewUserRequest.class);
            System.out.println("here");
            Users createdUser = userService.register(request);
            resp.setStatus(201); // CREATED
            resp.setContentType("application/json");
            resp.getWriter().write(mapper.writeValueAsString(createdUser.getUser_id()));
        } catch (InvalidRequestException e) {
            resp.setStatus(404); // BAD REQUEST
        } catch (ResourceConflictException e) {
            resp.setStatus(409); // RESOURCE CONFLICT
        } catch (Exception e) {
            e.printStackTrace();
           resp.setStatus(500);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Principal requester = tokenServices.extractRequestDetails(req.getHeader("Authorization"));

        // ---------------- Reject no Login -------------------
        if(requester == null){
            resp.setContentType("application/html");
            resp.getWriter().write("<h1>403</h1>");
            resp.getWriter().write("<h1>Access Denied you are not allowed to see this page</h1>");
            resp.setStatus(403);
            return;
        }
        // ---------------- Reject not Admin -------------------
        if(!requester.getRole().equals("ADMIN")) {
            resp.getWriter().write("<h1>403</h1>");
            resp.getWriter().write("<h1>Access Denied " + requester.getRole() + " are not allowed to view these pages</h1>");
            resp.setStatus(403);
            return;
        }

        String[] uris = req.getRequestURI().split("/");
        String query =req.getQueryString();


        if (uris.length >= 4 && uris[3].contains("search")) {
            List<Users> users = userService.getAllUsers();


            if (query != null){
                // System.out.println(query.charAt(0)); what to search
                //System.out.println(query.substring(2,query.length()));  searching

                //query  username
                if(query.charAt(0) == 'u'){
                    String search = query.substring(2);
                    List<Users> t = users.stream().filter(user -> dist(user.getUsername().toLowerCase(),search.toLowerCase()) < 5).collect(Collectors.toList());
                    resp.setContentType("application/json");
                    resp.getWriter().write(mapper.writeValueAsString(t));
                    return;
                // query roles
                }else if(query.charAt(0) == 'r') {
                    String search = query.substring(2);
                    List<Users> t = users.stream().filter(user -> dist( user.getRole_id().toLowerCase(), search.toLowerCase()) < 2).collect(Collectors.toList());
                    resp.setContentType("application/json");
                    resp.getWriter().write(mapper.writeValueAsString(t));
                    return;
                }else if(query.equals("pending")) {
                    // might change this to sql command
                    List<Users> t = userService.getAllPending();
                    resp.setContentType("application/json");
                    resp.getWriter().write(mapper.writeValueAsString(t));
                    return;
                }
                else {
                    resp.setContentType("application/html");
                    resp.getWriter().write("<h1>404 Page not found</h1>");
                    resp.getWriter().write("<h1>Invalid Query</h1>");
                    resp.getWriter().write("<h2>Valid Prompts</h1>");
                    resp.getWriter().write("<ul>" +
                            "<li> u: for users</li>" +
                            "<li> r: for role</li>" +
                            "<li> pending: for pending</li>" +
                            "</ul>");
                    resp.setStatus(404);
                    return;
                }

            }

            // ----------------- GET ALL USERS
            // default for users/search if admin

            //searching all users
            resp.setContentType("application/json");
            resp.getWriter().write(mapper.writeValueAsString(users));
            // ----------------------------------------

        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String[] uris = req.getRequestURI().split("/");

        // ---------------------- Approve users ----------------------------
        if (uris.length >= 4 && uris[3].equals("approve")) {

            // get auth---------

            // -------------

            ApproveNewUser request = mapper.readValue(req.getInputStream(), ApproveNewUser.class);
            userService.approveUser(request);

            resp.setContentType("application/html");
            resp.getWriter().write("<h1>Approval Successful!</h1>");
            resp.getWriter().write("<h2>" + request.getUsername() + "has been approved! </h2>");
            resp.setStatus(202);
        }
        // ---------------------- Approve users ----------------------------

        // ---------------------- Reject users ----------------------------
        if (uris.length >= 4 && uris[3].equals("reject")) {

            // get auth---------

            // -------------

            RejectUser request = mapper.readValue(req.getInputStream(), RejectUser.class);
            userService.reject(request);

            resp.setContentType("application/html");
            resp.getWriter().write("<h1>Reject Successful!</h1>");
            resp.getWriter().write("<h2>" + request.getUsername() + "has been rejected! </h2>");
            resp.setStatus(202);
        }
        // ---------------------- Reject users ----------------------------

        // ---------------------- Reset users ----------------------------
        if (uris.length >= 4 && uris[3].equals("reset")) {
            String pass = randomPass();

            //ResetUserPass request = mapper.readValue(req.getInputStream(), ResetUserPass.class);

            //userService.changePass(request,pass);
            resp.setContentType("application/html");
            resp.getWriter().write("<h1>Reset Successful!</h1>");
            resp.getWriter().write("<h2> Username new password is:" + pass  + "</h2>");
            resp.setStatus(202);

            // get auth---------

            // -------------

            //ResetUserPass request = mapper.readValue(req.getInputStream(), ResetUserPass.class);
            //userService.reject(request);
        }
        // ---------------------- Reset users ----------------------------


    }

    public int dist( String s, String ss ) {

        char[] s1 = s.toCharArray();
        char[] s2 = ss.toCharArray();

        // memoize only previous line of distance matrix
        int[] prev = new int[ s2.length + 1 ];

        for( int j = 0; j < s2.length + 1; j++ ) {
            prev[ j ] = j;
        }

        for( int i = 1; i < s1.length + 1; i++ ) {

            // calculate current line of distance matrix
            int[] curr = new int[ s2.length + 1 ];
            curr[0] = i;

            for( int j = 1; j < s2.length + 1; j++ ) {
                int d1 = prev[ j ] + 1;
                int d2 = curr[ j - 1 ] + 1;
                int d3 = prev[ j - 1 ];
                if ( s1[ i - 1 ] != s2[ j - 1 ] ) {
                    d3 += 1;
                }
                curr[ j ] = Math.min( Math.min( d1, d2 ), d3 );
            }

            // define current line of distance matrix as previous
            prev = curr;
        }
        return prev[ s2.length ];
    }

    public String randomPass(){
        String specialChar = "";
        specialChar += RandomStringUtils.random(3, 65, 90, true, true);
        specialChar += RandomStringUtils.random(4, 97, 122, true, true);
        specialChar += RandomStringUtils.random(4, 65, 90, true, true);
        specialChar += RandomStringUtils.random(1, 63, 65, false, false);
        specialChar += RandomStringUtils.randomNumeric(3);
        return specialChar;

    }




}
