package com.revature.ers.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.ers.dtos.requests.NewReimbRequest;
import com.revature.ers.dtos.requests.ResolveReimbRequest;
import com.revature.ers.dtos.requests.UpdateReimbDescr;
import com.revature.ers.dtos.responses.Principal;
import com.revature.ers.models.Reimbursements;
import com.revature.ers.models.Users;
import com.revature.ers.services.ReimbursementsServices;
import com.revature.ers.services.TokenServices;
import com.revature.ers.util.annotations.Inject;
import com.revature.ers.util.custom_exceptions.ForbiddenUserException;
import com.revature.ers.util.custom_exceptions.InvalidRequestException;
import com.revature.ers.util.custom_exceptions.ResourceConflictException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReimbursementServlet extends HttpServlet {
    @Inject
    private final ObjectMapper mapper;
    private final ReimbursementsServices reimbService;
    private final TokenServices tokenServices;

    @Inject
    public ReimbursementServlet(ObjectMapper mapper, ReimbursementsServices reimbService, TokenServices tokenServices) {
        this.mapper = mapper;
        this.reimbService = reimbService;
        this.tokenServices = tokenServices;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //post request makes a new reimb
            NewReimbRequest request = mapper.readValue(req.getInputStream(), NewReimbRequest.class);
            Principal requestor = tokenServices.extractRequestDetails(req.getHeader("Authorization"));
            //check authorization
            if (requestor == null){
                resp.setStatus(401); // unauthorized user
                return;
            }
            if (!requestor.getRole().equals("DEFAULT")){
                resp.setStatus(403); // V E R B O T E N, only default users create requests.
                return;
            }
            Reimbursements createdReimb = reimbService.register(request, requestor.getId());
            resp.setStatus(201); // CREATED
            resp.setContentType("application/json");
            resp.getWriter().write(mapper.writeValueAsString(createdReimb.getReimb_id()));
        } catch (InvalidRequestException e) {
            resp.setStatus(404); // BAD REQUEST
//        } catch (ResourceConflictException e) {
//            resp.setStatus(409); // RESOURCE CONFLICT will not throw unless we introduce an input validation for duplicates by checking the timestamp or description or something
        } catch (Exception e) {
            e.printStackTrace();
           resp.setStatus(500);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //get authorization from token
        Principal requester = tokenServices.extractRequestDetails(req.getHeader("Authorization"));

        //only registered users can fetch reimbursements.  set status to 401 and return if no token.
        if(requester==null){
            resp.setStatus(401); //unauthorized user
            return;
        }
        List<Reimbursements> userReimbs = new ArrayList<>();
        //parse the uri. indexing is host/ers/reimb/
        String[] uris = req.getRequestURI().split("/");
        switch (requester.getRole()){
            /*switch case: user is a DEFAULT
            This case has the following functions:
             host/ers/reimb/  displays a list of all pending for this user
                host/ers/reimb/history displays history (approved/denied) without sorting
                    host/ers/reimb/history/submitdate displays history sorted by submitdate
                    host/ers/reimb/history/resolvedate displays history sorted by resolve date
                    host/ers/reimb/history/type displays history sorted by request type.

             !!filtering not implemented!!

            invalid uris should give a 404.

            */
            case "DEFAULT":
                //get all reimbursements with this user as the author.
                userReimbs = reimbService.getByAuthorID(requester.getId());
                //without additional uris, just filter to pending, set status to ok.
                if (uris.length == 3) {
                    userReimbs = userReimbs.stream().filter(reimb -> reimb.getStatus_id().equals("PENDING")).collect(Collectors.toList());
                    resp.setContentType("application/json");
                    resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                    resp.setStatus(200);
                    return;
                } else {
                    switch (uris[3]) {
                        case "history":
                            //filter by "not-pending"
                            userReimbs = userReimbs.stream().filter(reimb -> !reimb.getStatus_id().equals("PENDING")).collect(Collectors.toList());
                            //supports sorting by type or date with additional uri
                            if (uris.length > 4){
                                switch (uris[4]){
                                    //sort by date submitted.  currently can't choose between ascending or descending.
                                    case "submitted":
                                        userReimbs=userReimbs.stream().sorted((r1, r2) -> r1.getSubmitted().compareTo(r2.getSubmitted())).collect(Collectors.toList());
                                        resp.setContentType("application/json");
                                        resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                        resp.setStatus(200);
                                        return;
                                    //sort by date resolved.  currently can't choose between ascending or descending.
                                    case "resolved":
                                        userReimbs=userReimbs.stream().sorted((r1, r2) -> r1.getResolved().compareTo(r2.getResolved())).collect(Collectors.toList());
                                        resp.setContentType("application/json");
                                        resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                        resp.setStatus(200);
                                        return;
                                        //sort by type
                                    case "type":
                                        userReimbs=userReimbs.stream().sorted((r1, r2) -> r1.getType_id().compareTo(r2.getType_id())).collect(Collectors.toList());
                                        resp.setContentType("application/json");
                                        resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                        resp.setStatus(200);
                                        return;
                                    default:
                                        resp.setStatus(404); //query type not found
                                        return;
                                }
                            } else {
                                resp.setContentType("application/json");
                                resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                resp.setStatus(200);
                                return;
                            }
                        default:
                            resp.setStatus(404); //query type not found
                            return;
                    }
                }
            case "FINMAN":
            /*switch case: user is a FINMAN
            This case has the following functions:
             host/ers/reimb/  displays a list of all pending
                host/ers/reimb/history displays history (approved/denied) resolved by this user, without sorting
                    host/ers/reimb/history/submitdate displays history sorted by submitdate
                    host/ers/reimb/history/resolvedate displays history sorted by resolve date
                    host/ers/reimb/history/type displays history sorted by request type.

             !!filtering not implemented!!

            invalid uris should give a 404.
            */

                //without additional uris, just get all pending, set status to ok.
                if (uris.length == 3) {
                    userReimbs = reimbService.getAllPending();
                    resp.setContentType("application/json");
                    resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                    resp.setStatus(200);
                    return;
                } else {
                    //otherwise, get all past reimbursement requests with this user as the resolver
                    userReimbs = reimbService.getByResolverID(requester.getId());
                    switch (uris[3]) {
                        case "history":
                            //supports sorting by type or date with additional uri
                            if (uris.length > 4){
                                switch (uris[4]){
                                    //sort by date submitted.  currently can't choose between ascending or descending.
                                    case "submitted":
                                        userReimbs=userReimbs.stream().sorted((r1, r2) -> r1.getSubmitted().compareTo(r2.getSubmitted())).collect(Collectors.toList());
                                        resp.setContentType("application/json");
                                        resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                        resp.setStatus(200);
                                        return;
                                    //sort by date resolved.  currently can't choose between ascending or descending.
                                    case "resolved":
                                        userReimbs=userReimbs.stream().sorted((r1, r2) -> r1.getResolved().compareTo(r2.getResolved())).collect(Collectors.toList());
                                        resp.setContentType("application/json");
                                        resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                        resp.setStatus(200);
                                        //sort by type
                                    case "type":
                                        userReimbs=userReimbs.stream().sorted((r1, r2) -> r1.getType_id().compareTo(r2.getType_id())).collect(Collectors.toList());
                                        resp.setContentType("application/json");
                                        resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                        resp.setStatus(200);
                                        return;
                                    default:
                                        resp.setStatus(404); //query type not found
                                        return;
                                }
                            } else {
                                resp.setContentType("application/json");
                                resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                resp.setStatus(200);
                                return;
                            }
                        case "submitted":
                            userReimbs=userReimbs.stream().sorted((r1, r2) -> r1.getSubmitted().compareTo(r2.getSubmitted())).collect(Collectors.toList());
                            resp.setContentType("application/json");
                            resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                            resp.setStatus(200);
                            return;
                        default:
                            resp.setStatus(404); //query type not found
                            return;
                    }
                }
            //default is either BANNED or ADMIN, or something else that somehow slipped past the if statement at the start of the method.
            //Either way, default case is a 403.
            default:
                resp.setStatus(403); //FORBIDDEN
                return;
        }



    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
        Put request functionality:
        For DEFAULT: update a pending request by altering the description or (once I get receipt images working...) uploading a receipt.
        For FINMAN: update a pending request by approving or denying it.
        For ADMIN: nothing - they don't interact with this
        For BANNED: nothing - they don't interact with this

         */

        try {
            //check authorization
            Principal requestor = tokenServices.extractRequestDetails(req.getHeader("Authorization"));
            //if empty, needs to be authorized
            if (requestor == null){
                resp.setStatus(401); // unauthorized user
                return;
            }
            //admins and banned folks don't use this page
            if (requestor.getRole().equals("BANNED") || requestor.getRole().equals("ADMIN")){
                resp.setStatus(403); // V E R B O T E N, only finmans and defaults use this.
                return;
            }

            String[] uris = req.getRequestURI().split("/");
            //doPut does not work at host/ers/reimb
            if (uris.length==3){
                resp.setStatus(400);
                return;
            }
            //check URI for what to do
            /*
            Possible paths: host/ers/reimb/update for DEFAULT
                            host/ers/reimb/resolve for FINMAN
             */
            else if (uris.length==4){
                switch(uris[3]){
                    //resolve a request
                    case "resolve":
                        //if user is not a FINMAN, reject
                        if (!requestor.getRole().equals("FINMAN")){
                            resp.setStatus(403); //forbidden
                            return;
                        }
                        //new ResolveReimbRequest object with requestor as resolver id
                        ResolveReimbRequest resRequest = mapper.readValue(req.getInputStream(), ResolveReimbRequest.class);
                        resRequest.setResolver_id(requestor.getId());
                        //return the reimbursement we made, just in case we want to display anything.
                        Reimbursements resolvedReimb = reimbService.resolve(resRequest);
                        resp.setStatus(200); // SUCCESS
                        resp.setContentType("application/json");
                        resp.getWriter().write(mapper.writeValueAsString(resolvedReimb.getReimb_id()));
                        resp.getWriter().write(mapper.writeValueAsString(resolvedReimb.getStatus_id()));
                        return;
                    //update a request's description
                    case "updateDesc":
                        if (!requestor.getRole().equals("DEFAULT")){
                            resp.setStatus(403); //forbidden
                            return;
                        }
                        //new UpdateReimbDescr request
                        UpdateReimbDescr descrRequest = mapper.readValue(req.getInputStream(), UpdateReimbDescr.class);
                        //check that the one updating is the one who made the request
                        //return the reimbursement we made, just in case we want to display anything.
                        Reimbursements descrReimb = reimbService.updateDescr(descrRequest, requestor.getId());
                        resp.setStatus(200); // SUCCESS
                        resp.setContentType("application/json");
                        resp.getWriter().write(mapper.writeValueAsString(descrReimb.getReimb_id()));
                        resp.getWriter().write(mapper.writeValueAsString(descrReimb.getDescription()));
                        resp.setStatus(403); //Forbidden - user is not the one who issued the reimbursement request
                        return;
                        //default case is not understood
                    default:
                        resp.setStatus(404);
                        return;

                }
            } else {
                //if URI has indeces > 4, throw a 404
                resp.setStatus(404); //not found
                return;
            }
        } catch (InvalidRequestException e) {
            resp.setStatus(404); // BAD REQUEST
        } catch (ForbiddenUserException e) {
            resp.setStatus(403); //FORBIDDEN
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }
}
