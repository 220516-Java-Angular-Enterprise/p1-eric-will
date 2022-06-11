package com.revature.ers.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.ers.dtos.requests.NewReimbRequest;
import com.revature.ers.dtos.responses.Principal;
import com.revature.ers.models.Reimbursements;
import com.revature.ers.models.Users;
import com.revature.ers.services.ReimbursementsServices;
import com.revature.ers.services.TokenServices;
import com.revature.ers.util.annotations.Inject;
import com.revature.ers.util.custom_exceptions.InvalidRequestException;
import com.revature.ers.util.custom_exceptions.ResourceConflictException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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
//            resp.setStatus(409); // RESOURCE CONFLICT will not throw unless we introduce an input validation for duplicates
        } catch (Exception e) {
            e.printStackTrace();
           resp.setStatus(500);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //get authorization from token
        Principal requester = tokenServices.extractRequestDetails(req.getHeader("Authorization"));
        List<Reimbursements> pendingReimbs = null;

        //only registered users can fetch reimbursements
        if(requester==null){
            resp.setStatus(401); //unauthorized user
            return;
        }
        //parse URI
        String[] uris = req.getRequestURI().split("/");
        //parse the uri. indexing is host/ers/reimb/
        if (uris.length>=4 && uris[3].contains("pending")){

        }
        if (uris.length>=4 && uris[3].contains("history")){
            String query =req.getQueryString();
            //check authorization
            switch (requester.getRole()){
                //switch case: user is a DEFAULT
                case "DEFAULT":
                    //get all reimbursements that are not pending.  This is handled entirely by the DAO.
                    pendingReimbs = reimbService.getAllUserIDPending(requester.getId());
                    //for a null query just display list, set status to ok.
                    if (query == null) {
                        resp.setContentType("application/json");
                        resp.getWriter().write(mapper.writeValueAsString(pendingReimbs));
                        return;
                    } else {
                        switch (query) {
                            case "date":
                                //sort by date
                                pendingReimbs = pendingReimbs.stream()
                                        .sorted((r1, r2) -> r1.getSubmitted().compareTo(r2.getSubmitted()))
                                        .collect(Collectors.toList());
                                resp.setContentType("application/json");
                                resp.getWriter().write(mapper.writeValueAsString(pendingReimbs));
                                return;
                            //for type query, sort by type
                            case "type":
                                pendingReimbs = pendingReimbs.stream()
                                        .sorted((r1, r2) -> r1.getType_id().compareTo(r2.getType_id()))
                                        .collect(Collectors.toList());
                                resp.setContentType("application/json");
                                resp.getWriter().write(mapper.writeValueAsString(pendingReimbs));
                                return;
                            default:
                                resp.setStatus(404); //query type not found
                                return;
                        }
                    }
                //switch case: user is a FINMAN
                case "FINMAN":
                    //gets all previously approved/denied requests by this finman
                    pendingReimbs = reimbService.getAllComplete().stream()
                            .filter(reimb -> reimb.getResolver_id().equals(requester.getId()))
                            .collect(Collectors.toList());

                    if (query == null) {
                        //for a null query just display list, set status to ok.
                        resp.setContentType("application/json");
                        resp.getWriter().write(mapper.writeValueAsString(pendingReimbs));
                        return;
                    } else {
                        switch (query) {
                            case "date":
                                //sort by date
                                pendingReimbs = pendingReimbs.stream()
                                        .sorted((r1, r2) -> r1.getSubmitted().compareTo(r2.getSubmitted()))
                                        .collect(Collectors.toList());
                                resp.setContentType("application/json");
                                resp.getWriter().write(mapper.writeValueAsString(pendingReimbs));
                                return;
                            //for type query, sort by type
                            case "type":
                                pendingReimbs = pendingReimbs.stream()
                                        .sorted((r1, r2) -> r1.getType_id().compareTo(r2.getType_id()))
                                        .collect(Collectors.toList());
                                resp.setContentType("application/json");
                                resp.getWriter().write(mapper.writeValueAsString(pendingReimbs));
                                return;
                            case "user":
                                pendingReimbs = pendingReimbs.stream()
                                        .sorted((r1, r2) -> r1.getAuthor_id().compareTo(r2.getAuthor_id()))
                                        .collect(Collectors.toList());
                                resp.setContentType("application/json");
                                resp.getWriter().write(mapper.writeValueAsString(pendingReimbs));
                            default:
                                resp.setStatus(404); //query type not found
                                return;
                        }
                    }
                //default is either BANNED or ADMIN.  Either way, they probably don't need to see this.
                default:
                    resp.setStatus(403); //FORBIDDEN
                    return;
            //obtain all reimbursement requests
        }


        }
        if (uris.length>=4 && uris[3].contains("filter")){

        }
    }
}
