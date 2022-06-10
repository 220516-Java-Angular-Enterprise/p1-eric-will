package com.revature.ers.services;

import com.revature.ers.daos.ReimbursementsDAO;
import com.revature.ers.daos.UsersDAO;
import com.revature.ers.dtos.requests.NewReimbRequest;
import com.revature.ers.dtos.requests.NewUserRequest;
import com.revature.ers.models.Reimbursements;
import com.revature.ers.models.Users;
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

    public List<Reimbursements> getAll(){
        return reimbDAO.getAll();
    }

    public List<Reimbursements> getAllPending(){
        return reimbDAO.getAllPending();
    }

    public List<Reimbursements> getAllUserIDPending(String userID){
        return reimbDAO.getAllUserIDPending(userID);
    }
    public List<Reimbursements> getAllUsernamePending(String username){
        return reimbDAO.getAllUsernamePending(username);
    }
    public List<Reimbursements> getAllComplete(){
        return reimbDAO.getAllComplete();
    }
}
