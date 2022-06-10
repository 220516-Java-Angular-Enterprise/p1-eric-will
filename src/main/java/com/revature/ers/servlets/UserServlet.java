package com.revature.ers.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.ers.dtos.requests.NewUserRequest;
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

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        if(requester != null) {
            resp.getWriter().write("<h1>" + requester.getRole() + "</h1>");
        }

        String[] uris = req.getRequestURI().split("/");
        String query =req.getQueryString();
        System.out.println(query);

        for(String s:uris){
            System.out.println(s);
        }

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
                    List<Users> t = users.stream().filter(user -> !user.isIs_active()).filter(user -> user.getRole_id() != "BANNED").collect(Collectors.toList());
                    resp.setContentType("application/json");
                    resp.getWriter().write(mapper.writeValueAsString(t));
                    return;
                }
                else {
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
        // searching users

    }

    public static int dist( String s, String ss ) {

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


}
