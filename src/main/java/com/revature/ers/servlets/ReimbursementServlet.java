package com.revature.ers.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.ers.dtos.requests.NewReimbRequest;
import com.revature.ers.models.Reimbursements;
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
            Reimbursements createdReimb = reimbService.register(request);
            resp.setStatus(201); // CREATED
            resp.setContentType("application/json");
            resp.getWriter().write(mapper.writeValueAsString(createdReimb.getReimb_id()));
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
        resp.getWriter().write("I am an auth servlet.  Woof.");
        resp.setStatus(418);
    }
}
