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
import com.revature.ers.util.custom_exceptions.NotFoundException;
import com.revature.ers.util.custom_exceptions.ResourceConflictException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.sql.Timestamp;
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
        HttpStrings httpStrings = new HttpStrings();
        NewUserRequest request = new NewUserRequest();
        String[] uris = req.getRequestURI().split("/");
        try {
            request = mapper.readValue(req.getInputStream(), NewUserRequest.class);
            if (uris.length == 4 && uris[3].equals("signup")) {request.setRole("DEFAULT");}
            else if (uris.length == 4 && uris[3].equals("manager-sign-up")) {request.setRole("FINMAN");}
            else{
                throw new NotFoundException("Not Found");
            }
            Users createdUser = userService.register(request);
            System.out.println(request.getUsername() + " has signup." + (new Timestamp(System.currentTimeMillis())) );
            resp.setStatus(201); // CREATED
            resp.setContentType("application/json");
            resp.getWriter().write(mapper.writeValueAsString(createdUser.getUser_id()));
        } catch (NotFoundException e) {
            resp.getWriter().write(httpStrings.fourOFour(req.getRequestURL().toString()));
            resp.setStatus(404);
        } catch (ResourceConflictException e) {
            resp.getWriter().write(httpStrings.httpStr(409, "Rejected",request.getUsername() + " has already been taken"));
            resp.setStatus(409); // RESOURCE CONFLICT
        } catch (InvalidRequestException e){
            resp.getWriter().write(httpStrings.httpStr(400, "Invalid input",e.toString()));
            resp.setStatus(400);

        } catch (Exception e) {
            resp.getWriter().write(httpStrings.httpStr(500, "Server side error",e.toString()));
            resp.setStatus(500);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Principal requester = tokenServices.extractRequestDetails(req.getHeader("Authorization"));
        HttpStrings httpStrings = new HttpStrings();

        // ---------------- Reject no Login -------------------
        if(requester == null){
            resp.setContentType("application/html");
            resp.getWriter().write( httpStrings.fourOThree("No Login"));
            resp.setStatus(403);
            return;
        }
        // ---------------- Reject not Admin -------------------
        if(!requester.getRole().equals("ADMIN")) {
            resp.getWriter().write( httpStrings.fourOThree(requester.getRole()));
            resp.setStatus(403);
            return;
        }

        String[] uris = req.getRequestURI().split("/");
        String query =req.getQueryString();



        if (uris.length == 4 && uris[3].contains("search")) {
            List<Users> users = userService.getAllUsers();

            if (query != null){

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

                    resp.getWriter().write(httpStrings.fourOFour(req.getRequestURL().toString()+ "?" + req.getQueryString()));
                    resp.setStatus(404);
                    return;
                }

            }

            // ----------------- GET ALL USERS
            // default for users/search if admin

            //searching all users
            resp.setContentType("application/json");
            resp.getWriter().write(mapper.writeValueAsString(users));
            resp.setStatus(200);
            // ----------------------------------------

        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Principal requester = tokenServices.extractRequestDetails(req.getHeader("Authorization"));
        HttpStrings httpStrings = new HttpStrings();

        // ---------------- Reject no Login -------------------
        if(requester == null){
            resp.setContentType("application/html");
            resp.getWriter().write( httpStrings.fourOThree("No login"));
            resp.setStatus(403);
            return;
        }
        // ---------------- Reject not Admin -------------------
        if(!requester.getRole().equals("ADMIN")) {
            resp.getWriter().write( httpStrings.fourOThree(requester.getRole()));
            resp.setStatus(403);
            return;
        }

        String[] uris = req.getRequestURI().split("/");

        // ---------------------- Approve users ----------------------------
        if (uris.length >= 4 && uris[3].equals("approve")) {

            // get auth---------

            // -------------

            ApproveNewUser request = mapper.readValue(req.getInputStream(), ApproveNewUser.class);
            userService.approveUser(request);

            resp.setContentType("application/html");
            resp.getWriter().write(httpStrings.httpStr(202, "User Approval",request.getUsername() + " has been approved!"));
            System.out.println(request.getUsername() + " has been approved." + (new Timestamp(System.currentTimeMillis())) );
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
            resp.getWriter().write(httpStrings.httpStr(202, "User Rejection ",request.getUsername() + " has been rejected."));
            System.out.println(request.getUsername() + " has been rejected." + (new Timestamp(System.currentTimeMillis())) );
            resp.setStatus(202);
        }
        // ---------------------- Reject users ----------------------------

        // ---------------------- Reset users ----------------------------
        if (uris.length >= 4 && uris[3].equals("reset")) {
            String pass = randomPass();

            ResetUserPass request = mapper.readValue(req.getInputStream(), ResetUserPass.class);

            userService.changePass(request,pass);
            resp.setContentType("application/html");
            resp.getWriter().write(httpStrings.httpStr(202, "Password reset",request.getUsername() + " password has been reset to: <b>" + pass + "</b>"));
            System.out.println(request.getUsername() + " has been reset." + (new Timestamp(System.currentTimeMillis())) );
            resp.setStatus(202);

            // get auth---------

            // -------------

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
