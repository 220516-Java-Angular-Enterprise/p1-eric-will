package com.revature.ers.daos;

import com.revature.ers.models.Reimbursements;
import com.revature.ers.models.Users;
import com.revature.ers.util.database.DatabaseConnection;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.List;

public class ReimbursementsDAO implements CrudDAO<Reimbursements>{


    Connection con = DatabaseConnection.getCon();
    //column names: REIMB_ID, AMOUNT, SUBMITTED, RESOLVED, DESCRIPTION, RECEIPT, PAYMENT_ID, AUTHOR_ID, RESOLVER_ID, STATUS_ID, TYPE_ID
    @Override
    public void save(Reimbursements obj) {
        try{
            //For this to work, a new Reimbursements object probably ought to initialize its null-capable columns to null.
            PreparedStatement ps = con.prepareStatement("INSERT INTO ers_reimbursements (reimb_id, amount, submitted, resolved, description, receipt, payment_id, author_id, resolver_id, status_id, type_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, obj.getReimb_id());
            ps.setDouble(2, Double.parseDouble(new DecimalFormat("###0.00").format(obj.getAmount())));
            ps.setTimestamp(3, obj.getSubmitted());
            ps.setTimestamp(4,obj.getResolved());
            ps.setString(5, obj.getDescription());
            ps.setBlob(6, obj.getReceipt());
            ps.setString(7, obj.getPayment_id());
            ps.setString(8, obj.getAuthor_id());
            ps.setString(9, obj.getResolver_id());
            ps.setString(10, obj.getStatus_id());
            ps.setString(11, obj.getType_id());
            ps.executeUpdate();
        } catch (SQLException e){
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public void update(Reimbursements obj) {
//This method updates *every* field in the database.  Use with care!
        try{
            PreparedStatement ps = con.prepareStatement("Update ers_reimbursements SET amount = ?, submitted = ?, resolved = ?, description = ?, receipt = ?, payment_id = ?, author_id = ?, resolver_id = ?, status_id = ?, type_id = ? WHERE reimb_id = ?");
            ps.setString(11, obj.getReimb_id());
            ps.setDouble(1, Double.parseDouble(new DecimalFormat("###0.00").format(obj.getAmount())));
            ps.setTimestamp(2, obj.getSubmitted());
            ps.setTimestamp(3,obj.getResolved());
            ps.setString(4, obj.getDescription());
            ps.setBlob(5, obj.getReceipt());
            ps.setString(6, obj.getPayment_id());
            ps.setString(7, obj.getAuthor_id());
            ps.setString(8, obj.getResolver_id());
            ps.setString(9, obj.getStatus_id());
            ps.setString(10, obj.getType_id());
            ps.executeUpdate();
        } catch (SQLException e){
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM ers_reimbursements where reimb_id = ?");
            ps.setString(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Reimbursements getById(String id) {
//this implementation uses the parameterized constructor because the model does not currently have a no-arg constructor.
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_reimbursements WHERE reimb_id = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
                Reimbursements reimb = new Reimbursements(id,
                        rs.getDouble("amount"),
                        rs.getTimestamp("submitted"),
                        rs.getTimestamp("resolved"),
                        rs.getString("description"),
                        rs.getBlob("receipt"),
                        rs.getString("payment_id"),
                        rs.getString("author_id"),
                        rs.getString("resolver_id"),
                        rs.getString("status_id"),
                        rs.getString("type_id"));
                        return reimb;
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Reimbursements> getAll() {
        return null;
    }
}
