package com.revature.ers.dtos.requests;

import com.revature.ers.models.Reimbursements;

import java.sql.Blob;
import java.sql.Timestamp;

public class ResolveReimbRequest {

    private String reimb_id;
    private String resolver_id;
    private String status_id;

    public String getReimb_id() {
        return reimb_id;
    }

    public void setReimb_id(String reimb_id) {
        this.reimb_id = reimb_id;
    }

    public String getResolver_id() {
        return resolver_id;
    }

    public void setResolver_id(String resolver_id) {
        this.resolver_id = resolver_id;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }
    public Reimbursements extractReimb(){
        Reimbursements reimb = new Reimbursements();
        reimb.setReimb_id(this.reimb_id);
        reimb.setResolver_id(this.resolver_id);
        reimb.setStatus_id(this.status_id);
        return reimb;
    }
}
