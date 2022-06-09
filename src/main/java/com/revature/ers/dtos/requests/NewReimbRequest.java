package com.revature.ers.dtos.requests;

import com.revature.ers.models.Reimbursements;
import com.revature.ers.models.Users;

import java.sql.Blob;
import java.sql.Timestamp;

public class NewReimbRequest {

    private Double amount;
    private String description;
    private String type_id;

    public NewReimbRequest() {
        super();
    }

    public NewReimbRequest(Double amount, String description, String type_id) {
        this.amount = amount;
        this.description = description;
        this.type_id = type_id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }
    public Reimbursements extractReimb(){
        Reimbursements reimb = new Reimbursements(this.amount, this.description, this.type_id);
        reimb.setStatus_id("PENDING");
        return reimb;
    }

}
