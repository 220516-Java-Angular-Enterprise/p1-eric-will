package com.revature.ers.dtos.requests;

import com.revature.ers.models.Reimbursements;

public class UpdateReimbDescr {
    private String reimb_id;
    private String description;

    public String getReimb_id() {
        return reimb_id;
    }

    public void setReimb_id(String reimb_id) {
        this.reimb_id = reimb_id;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Reimbursements extractReimb(){
        Reimbursements reimb = new Reimbursements();
        reimb.setReimb_id(this.reimb_id);
        reimb.setDescription(this.description);
        return reimb;
    }
}
