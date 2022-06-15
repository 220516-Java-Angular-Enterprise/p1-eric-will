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
import com.revature.ers.util.custom_exceptions.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Timestamp;
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
            if (requestor == null) {
                throw new NotAuthorizedException("User is not authenticated.");
            }
            if (!requestor.getRole().equals("DEFAULT")) {
                throw new ForbiddenUserException("Only default users may register reimbursement requests.");
            }
            Reimbursements createdReimb = reimbService.register(request, requestor.getId());
            resp.setStatus(201); // CREATED
            System.out.println(requestor.getUsername()+"created reimbursement request "+createdReimb.getReimb_id()+" at "+String.valueOf(createdReimb.getSubmitted()));
            resp.setContentType("application/json");
            resp.getWriter().write(mapper.writeValueAsString(createdReimb.getReimb_id()));
        } catch (NotAuthorizedException e){
            resp.setStatus(401);
            resp.getWriter().write(new HttpStrings().httpStr(401,"UNAUTHORIZED",e.getMessage()));
        } catch (ForbiddenUserException e){
            resp.setStatus(403); // V E R B O T E N, only default users create requests.
            resp.getWriter().write(new HttpStrings().httpStr(403,"FORBIDDEN",e.getMessage()));
        }
        catch (InvalidRequestException e) {
            resp.setStatus(404); // BAD REQUEST
            resp.getWriter().write(new HttpStrings().httpStr(404,"NOT FOUND",e.getMessage()));
       } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write(new HttpStrings().httpStr(500,"INTERNAL SERVER ERROR",e.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //get authorization from token
        try {
            Principal requester = tokenServices.extractRequestDetails(req.getHeader("Authorization"));

            //only registered users can fetch reimbursements.  set status to 401 and return if no token.
            if (requester == null) {
                throw new NotAuthorizedException("User is not authenticated.");
            }
            List<Reimbursements> userReimbs = new ArrayList<>();
            //parse the uri. indexing is host/ers/reimb/
            String[] uris = req.getRequestURI().split("/");
            switch (requester.getRole()) {
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
                                if (uris.length > 4) {
                                    switch (uris[4]) {
                                        //sort by date submitted.  currently can't choose between ascending or descending.
                                        case "submitted":
                                            userReimbs = userReimbs.stream().sorted((r1, r2) -> r2.getSubmitted().compareTo(r1.getSubmitted())).collect(Collectors.toList());
                                            resp.setContentType("application/json");
                                            resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                            resp.setStatus(200);
                                            return;
                                        //sort by date resolved.  currently can't choose between ascending or descending.
                                        case "resolved":
                                            userReimbs = userReimbs.stream().sorted((r1, r2) -> r2.getResolved().compareTo(r1.getResolved())).collect(Collectors.toList());
                                            resp.setContentType("application/json");
                                            resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                            resp.setStatus(200);
                                            return;
                                        //sort by type
                                        case "type":
                                            userReimbs = userReimbs.stream().sorted((r1, r2) -> r2.getType_id().compareTo(r1.getType_id())).collect(Collectors.toList());
                                            resp.setContentType("application/json");
                                            resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                            resp.setStatus(200);
                                            return;
                                        default:
                                            throw new NotFoundException("The supplied URI does not point to a defined http request.");
                                    }
                                } else {
                                    resp.setContentType("application/json");
                                    resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                    resp.setStatus(200);
                                    return;
                                }
                            default:
                                throw new NotFoundException("The supplied URI does not point to a defined http request.");
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
                                if (uris.length > 4) {
                                    switch (uris[4]) {
                                        //sort by date submitted.  currently can't choose between ascending or descending.
                                        case "submitted":
                                            userReimbs = userReimbs.stream().sorted((r1, r2) -> r2.getSubmitted().compareTo(r1.getSubmitted())).collect(Collectors.toList());
                                            resp.setContentType("application/json");
                                            resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                            resp.setStatus(200);
                                            return;
                                        //sort by date resolved.  currently can't choose between ascending or descending.
                                        case "resolved":
                                            userReimbs = userReimbs.stream().sorted((r1, r2) -> r2.getResolved().compareTo(r1.getResolved())).collect(Collectors.toList());
                                            resp.setContentType("application/json");
                                            resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                            resp.setStatus(200);
                                            //sort by type
                                        case "type":
                                            userReimbs = userReimbs.stream().sorted((r1, r2) -> r2.getType_id().compareTo(r1.getType_id())).collect(Collectors.toList());
                                            resp.setContentType("application/json");
                                            resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                            resp.setStatus(200);
                                            return;
                                        default:
                                            throw new NotFoundException("The supplied URI does not point to a defined http request.");
                                    }
                                } else {
                                    resp.setContentType("application/json");
                                    resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                    resp.setStatus(200);
                                    return;
                                }
                            case "submitted":
                                userReimbs = userReimbs.stream().sorted((r1, r2) -> r2.getSubmitted().compareTo(r1.getSubmitted())).collect(Collectors.toList());
                                resp.setContentType("application/json");
                                resp.getWriter().write(mapper.writeValueAsString(userReimbs));
                                return;
                            default:
                                throw new NotFoundException("The supplied URI does not point to a defined http request.");
                        }
                    }
                    //default is either BANNED or ADMIN, or something else that somehow slipped past the if statement at the start of the method.
                    //Either way, default case is a 403.
                default:
                    throw new ForbiddenUserException("only finance managers and default users may query reimbursements.");
            }

        } catch (InvalidRequestException e) {
            resp.setStatus(400); // BAD REQUEST
            resp.getWriter().write(new HttpStrings().httpStr(400,"BAD REQUEST",e.getMessage()));
        } catch (NotAuthorizedException e) {
            resp.setStatus(401);// UNAUTHORIZED
            resp.getWriter().write(new HttpStrings().httpStr(401,"UNAUTHORIZED",e.getMessage()));
        } catch (ForbiddenUserException e) {
            resp.setStatus(403); //FORBIDDEN
            resp.getWriter().write(new HttpStrings().httpStr(403,"FORBIDDEN",e.getMessage()));
        } catch (NotFoundException e) {
            resp.setStatus(404); //NOT FOUND
            resp.getWriter().write(new HttpStrings().httpStr(404,"NOT FOUND",e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write(new HttpStrings().httpStr(500,"INTERNAL SERVER ERROR",e.getMessage()));
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
            //if empty, needs to be authorized. Abort.
            if (requestor == null){
                throw new NotAuthorizedException("User is not authenticated.");
            }
            //admins and banned folks don't use this page
            if (requestor.getRole().equals("BANNED") || requestor.getRole().equals("ADMIN")){
                throw new ForbiddenUserException("User is forbidden from accessing this resource.");
            }

            String[] uris = req.getRequestURI().split("/");
            //doPut does not work at host/ers/reimb
            if (uris.length==3){
                throw new InvalidRequestException("No PUT request exists for host/ers/reimb/");
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
                            throw new ForbiddenUserException("Only finance managers may resolve reimbursement requests.");
                        }
                        //new ResolveReimbRequest object with requestor as resolver id
                        ResolveReimbRequest resRequest = mapper.readValue(req.getInputStream(), ResolveReimbRequest.class);
                        resRequest.setResolver_id(requestor.getId());
                        //return the reimbursement we made, just in case we want to display anything.
                        Reimbursements resolvedReimb = reimbService.resolve(resRequest);
                        resp.setStatus(200); // SUCCESS
                        System.out.println(requestor.getUsername()+" resolved reimbursement request "+resRequest.getReimb_id()+" at "+String.valueOf(resolvedReimb.getResolved()));
                        resp.setContentType("application/json");
                        resp.getWriter().write(mapper.writeValueAsString(resolvedReimb.getReimb_id()));
                        resp.getWriter().write(mapper.writeValueAsString(resolvedReimb.getStatus_id()));
                        return;
                    //update a request's description
                    case "updatedescr":
                        if (!requestor.getRole().equals("DEFAULT")){
                            throw new ForbiddenUserException("Only employees may update their pending reimbursement requests.");
                        }
                        //new UpdateReimbDescr request
                        UpdateReimbDescr descrRequest = mapper.readValue(req.getInputStream(), UpdateReimbDescr.class);
                        //check that the one updating is the one who made the request
                        //return the reimbursement we made, just in case we want to display anything.
                        Reimbursements descrReimb = reimbService.updateDescr(descrRequest, requestor.getId());
                        resp.setStatus(200); // SUCCESS
                        System.out.println(requestor.getUsername()+" updated reimbursement request "+descrRequest.getReimb_id()+" to \""+descrRequest.getDescription()+"\" at "+String.valueOf(new Timestamp(System.currentTimeMillis())));
                        resp.setContentType("application/json");
                        resp.getWriter().write(mapper.writeValueAsString(descrReimb.getReimb_id()));
                        resp.getWriter().write(mapper.writeValueAsString(descrReimb.getDescription()));
                        return;
                        //default case is not understood
                    default:
                        throw new NotFoundException("No PUT request exists for host/ers/reimb/"+uris[3]);
                }
            } else {
                //if URI has indeces > 4, throw a 404
                throw new NotFoundException("No PUT request exists for "+req.getRequestURI());
            }
        } catch (InvalidRequestException e) {
            resp.setStatus(400); // BAD REQUEST
            resp.getWriter().write(new HttpStrings().httpStr(400, "BAD REQUEST", e.getMessage()));
        } catch (NotAuthorizedException e){
            resp.setStatus(401);
            resp.getWriter().write(new HttpStrings().httpStr(401,"UNAUTHORIZED",e.getMessage()));
        } catch (ForbiddenUserException e){
            resp.setStatus(403); // V E R B O T E N, only default users create requests.
            resp.getWriter().write(new HttpStrings().httpStr(403,"FORBIDDEN",e.getMessage()));
        }
        catch (NotFoundException e) {
            resp.setStatus(404); // BAD REQUEST
            resp.getWriter().write(new HttpStrings().httpStr(404,"NOT FOUND",e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write(new HttpStrings().httpStr(500,"INTERNAL SERVER ERROR",e.getMessage()));
        }
    }
}
