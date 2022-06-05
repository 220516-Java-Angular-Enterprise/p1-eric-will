package com.revature.ers.models;

public class ReimbursementStatus {

    private String status_id;
    private String status;

    public ReimbursementStatus(){

    }

    public ReimbursementStatus(String status_id, String status) {
        this.status_id = status_id;
        this.status = status;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
