package com.revature.ers.daos;

import com.revature.ers.models.Reimbursements;
import com.revature.ers.models.Users;
import com.revature.ers.util.database.DatabaseConnection;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReimbursementsDAO implements CrudDAO<Reimbursements>{


    Connection con = DatabaseConnection.getCon();
    //column names: REIMB_ID, AMOUNT, SUBMITTED, RESOLVED, DESCRIPTION, RECEIPT, PAYMENT_ID, AUTHOR_ID, RESOLVER_ID, STATUS_ID, TYPE_ID
    @Override
    public void save(Reimbursements obj) {
        try{
            //For this to work, a new Reimbursements object probably ought to initialize its null-capable columns to null.
            PreparedStatement ps = con.prepareStatement("INSERT INTO ers_reimbursements (reimb_id, amount, submitted, resolved, description, receipt, payment_id, author_id, resolver_id, status_id, type_id) VALUES (?, ?, ?, ?, ?, NULL, ?, ?, ?, ?, ?)");
            ps.setString(1, obj.getReimb_id());
            ps.setDouble(2, Double.parseDouble(new DecimalFormat("###0.00").format(obj.getAmount())));
            ps.setTimestamp(3, obj.getSubmitted());
            ps.setTimestamp(4,obj.getResolved());
            ps.setString(5, obj.getDescription());
//            ps.setBlob(6, obj.getReceipt());
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
        Reimbursements reimb = new Reimbursements();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_reimbursements WHERE reimb_id = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                reimb.setReimb_id(id);
                reimb.setAmount(rs.getDouble("amount"));
                reimb.setSubmitted(rs.getTimestamp("submitted"));
                reimb.setResolved(rs.getTimestamp("resolved"));
                reimb.setDescription(rs.getString("description"));
                reimb.setReceipt(rs.getBlob("receipt"));
                reimb.setPayment_id(rs.getString("payment_id"));
                reimb.setAuthor_id(rs.getString("author_id"));
                reimb.setResolver_id(rs.getString("resolver_id"));
                reimb.setStatus_id(rs.getString("status_id"));
                reimb.setType_id(rs.getString("type_id"));
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
        return reimb;
    }

    public List<String> getAllReimbTypes() {
        List<String> reimbTypes = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT type_id FROM ers_reimbursement_types");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                reimbTypes.add(rs.getString("type_id"));
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
        return reimbTypes;
    }
    @Override
    public List<Reimbursements> getAll() {
        List<Reimbursements> reimbs = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_reimbursements");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Reimbursements reimb = new Reimbursements(rs.getString("reimb_id"),
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
                reimbs.add(reimb);
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
        return reimbs;
    }

    public List<Reimbursements> getByAuthorID(String user_id) {
        List<Reimbursements> reimbs = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_reimbursements WHERE author_id = ?");
            ps.setString(1, user_id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Reimbursements reimb = new Reimbursements(rs.getString("reimb_id"),
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
                reimbs.add(reimb);
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
        return reimbs;
    }
    public List<Reimbursements> getByResolverID(String resolver_id) {
        List<Reimbursements> reimbs = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_reimbursements WHERE resolver_id = ?");
            ps.setString(1, resolver_id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Reimbursements reimb = new Reimbursements(rs.getString("reimb_id"),
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
                reimbs.add(reimb);
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
        return reimbs;
    }

    public List<Reimbursements> getAllAuthorIDPending(String user_id) {
        List<Reimbursements> reimbs = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_reimbursements WHERE author_id = ? AND status_id = 'PENDING'");
            ps.setString(1, user_id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Reimbursements reimb = new Reimbursements(rs.getString("reimb_id"),
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
                reimbs.add(reimb);
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
        return reimbs;
    }
    public List<Reimbursements> getAllUsernamePending(String username) {
        List<Reimbursements> reimbs = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_reimbursements join join ers_users on ers_reimbursements.author_id=ers_users.user_id WHERE username = ? AND status_id = 'PENDING'");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Reimbursements reimb = new Reimbursements(rs.getString("reimb_id"),
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
                reimbs.add(reimb);
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
        return reimbs;
    }

    public List<Reimbursements> getAllPending() {
        List<Reimbursements> reimbs = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_reimbursements WHERE status_id = 'PENDING'");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Reimbursements reimb = new Reimbursements(rs.getString("reimb_id"),
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
                reimbs.add(reimb);
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
        return reimbs;
    }
    public List<Reimbursements> getAllComplete() {
        List<Reimbursements> reimbs = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_reimbursements WHERE status_id != 'PENDING'");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Reimbursements reimb = new Reimbursements(rs.getString("reimb_id"),
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
                reimbs.add(reimb);
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
        return reimbs;
    }
    public void resolve(Reimbursements obj) {
        try{
            PreparedStatement ps = con.prepareStatement("Update ers_reimbursements SET (status_id, resolved, resolver_id) = (?, ?, ?) WHERE reimb_id = ?");
            ps.setString(1, obj.getStatus_id());
            ps.setTimestamp(2,obj.getResolved());
            ps.setString(3,obj.getResolver_id());
            ps.setString(4,obj.getReimb_id());
            ps.executeUpdate();
        } catch (SQLException e){
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
    }
    public void updateDescr(Reimbursements obj) {
        try{
            PreparedStatement ps = con.prepareStatement("Update ers_reimbursements SET description = ? WHERE reimb_id = ?");
            ps.setString(1, obj.getDescription());
            ps.setString(2,obj.getReimb_id());
            ps.executeUpdate();
        } catch (SQLException e){
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
    }

}
