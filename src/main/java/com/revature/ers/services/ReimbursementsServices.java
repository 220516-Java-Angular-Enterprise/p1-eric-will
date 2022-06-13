package com.revature.ers.services;

import com.revature.ers.daos.ReimbursementsDAO;
import com.revature.ers.daos.UsersDAO;
import com.revature.ers.dtos.requests.NewReimbRequest;
import com.revature.ers.dtos.requests.NewUserRequest;
import com.revature.ers.dtos.requests.ResolveReimbRequest;
import com.revature.ers.dtos.requests.UpdateReimbDescr;
import com.revature.ers.models.Reimbursements;
import com.revature.ers.models.Users;
import com.revature.ers.util.custom_exceptions.ForbiddenUserException;
import com.revature.ers.util.custom_exceptions.InvalidRequestException;
import com.revature.ers.util.custom_exceptions.ResourceConflictException;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ReimbursementsServices {
    private final ReimbursementsDAO reimbDAO;

    public ReimbursementsServices(ReimbursementsDAO reimbDAO) {
        this.reimbDAO = reimbDAO;
    }

    public Reimbursements register(NewReimbRequest request, String author_id) {
        Reimbursements reimb = request.extractReimb();
        reimb.setAuthor_id(author_id);
        if (isValidReimbType(request.getType_id())) {
            reimb.setReimb_id(UUID.randomUUID().toString());
            reimb.setSubmitted(new Timestamp(System.currentTimeMillis()));
            reimbDAO.save(reimb);
        } else throw new InvalidRequestException("Invalid reimbursement type.");
        return reimb;
    }

    private boolean isValidReimbType(String reimbType) {
        List<String> reimbTypes = reimbDAO.getAllReimbTypes();
        for (String rtype : reimbTypes) {
            if (rtype.equals(reimbType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPending(String reimb_id){
        if (reimbDAO.getById(reimb_id).getStatus_id().equals("PENDING")){
            return true;
        } else {return false;}
    }

    public List<Reimbursements> getAll(){
        return reimbDAO.getAll();
    }

    public List<Reimbursements> getAllPending(){
        return reimbDAO.getAllPending();
    }
    public List<Reimbursements> getByAuthorID(String userID){
        return reimbDAO.getByAuthorID(userID);
    }

    public List<Reimbursements> getByResolverID(String resolverID){
        return reimbDAO.getByResolverID(resolverID);
    }
    public Reimbursements getById(String reimb_id){
        return reimbDAO.getById(reimb_id);
    }

    public List<Reimbursements> getAllAuthorIDPending(String userID){
        return reimbDAO.getAllAuthorIDPending(userID);
    }
    public List<Reimbursements> getAllUsernamePending(String username){
        return reimbDAO.getAllUsernamePending(username);
    }
    public List<Reimbursements> getAllComplete(){
        return reimbDAO.getAllComplete();
    }

    public Reimbursements resolve(ResolveReimbRequest request){
        Reimbursements reimb = request.extractReimb();
        if (reimb.getStatus_id().equals("APPROVED") || reimb.getStatus_id().equals("DENIED")) {
            if (isPending(reimb.getReimb_id())) {
                reimb.setResolved(new Timestamp(System.currentTimeMillis()));
                reimbDAO.resolve(reimb);
                return reimb;
            } else throw new InvalidRequestException("Cannot resolve an already resolved reimbursement request");
        } else throw new InvalidRequestException("A request must be resolved as either APPROVED or DENIED");
    }
    public Reimbursements updateDescr(UpdateReimbDescr request, String user_id){
        Reimbursements reimb = request.extractReimb();
        Reimbursements targetReimb = reimbDAO.getById(reimb.getReimb_id());
        if (targetReimb.getStatus_id().equals("PENDING")) {
            if (targetReimb.getAuthor_id().equals(user_id)) {
                reimbDAO.updateDescr(reimb);
                return reimb;
            } else throw new ForbiddenUserException("Only the request's author may update its description");
        } else throw new InvalidRequestException("Can only update PENDING requests.");
    }
}
