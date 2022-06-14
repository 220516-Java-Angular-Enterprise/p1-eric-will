package com.revature.ers.daos;

import com.revature.ers.dtos.requests.ResetUserPass;
import com.revature.ers.models.Users;
import com.revature.ers.util.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO implements CrudDAO<Users>{

    //get database connection
    Connection con = DatabaseConnection.getCon();

    //note that this DAO does not encrypt/decrypt passwords yet
    @Override
    public void update(Users user) {
        //This method updates *every* field of the database for a user.  Use with care!
        try{
            PreparedStatement ps = con.prepareStatement("Update ers_users SET username = ?, email = ?, PASSWORD = ?, given_name = ?, surname = ?, is_active = ?, role_id = ? WHERE user_id = ?");
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4,user.getGiven_name());
            ps.setString(5, user.getSurname());
            ps.setBoolean(6, user.isIs_active());
            ps.setString(7, user.getRole_id());
            ps.setString(8, user.getUser_id());
            ps.executeUpdate();
        } catch (SQLException e){
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
    }

    public void updateIsActive(Users user) {
        //This method updates *every* field of the database for a user.  Use with care!
        try{
            System.out.println("here in UserDAO is active");
            System.out.println(user.isIs_active());
            PreparedStatement ps = con.prepareStatement("Update ers_users SET is_active = ? WHERE username = ?");
            ps.setBoolean(1, user.isIs_active());
            ps.setString(2, user.getUsername());
            ps.executeUpdate();
        } catch (SQLException e){
            System.out.println("Error");
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM ers_users where user_id = ?");
            ps.setString(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
    }
    @Override
    public Users getById(String id) {
        Users user = new Users();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_users WHERE user_id = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user.setUser_id(id);
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("PASSWORD"));
                user.setGiven_name(rs.getString("given_name"));
                user.setSurname(rs.getString("surname"));
                user.setIs_active(rs.getBoolean("is_active"));
                user.setRole_id(rs.getString("role_id"));
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
        return user;
    }
    @Override
    public List<Users> getAll() {
        List<Users> users = new ArrayList<>();
        //This will not work with the current database schema, because it does not have a surname column (as of 06/05)
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_users");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users user = new Users();
                user.setUser_id(rs.getString("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("PASSWORD"));
                user.setGiven_name(rs.getString("given_name"));
                user.setSurname(rs.getString("surname"));
                user.setIs_active(rs.getBoolean("is_active"));
                user.setRole_id(rs.getString("role_id"));
                users.add(user);
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }

        return users;

    }
    @Override
    public void save(Users user){
        try{
            //This will not work with the current database schema, because it does not have a surname column (as of 06/05)
            PreparedStatement ps = con.prepareStatement("INSERT INTO ers_users (user_id, username, email, password, given_name, surname, is_active, role_id) VALUES (?, ?, ?, crypt(?, gen_salt('bf')), ?, ?, ?, ?)");
            ps.setString(1, user.getUser_id());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5,user.getGiven_name());
            ps.setString(6, user.getSurname());
            ps.setBoolean(7, user.isIs_active());
            ps.setString(8, user.getRole_id());
            ps.executeUpdate();

        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
    }

    public Users getByUsernameandPassword(String username, String password) {
        Users user = null;

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_users WHERE username = ? AND password = crypt(?, password)");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                user = new Users(rs.getString("username"), rs.getString("user_id"), rs.getString("role_id"));
            }

        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
        return user;
    }

    public void reject(Users user) {
        //This method updates *every* field of the database for a user.  Use with care!
        try{
            PreparedStatement ps = con.prepareStatement("Update ers_users SET is_active = ?, role_id = ? WHERE username = ?");
            ps.setBoolean(1, user.isIs_active());
            ps.setString(2, user.getRole_id());
            ps.setString(3, user.getUsername());
            ps.executeUpdate();
        } catch (SQLException e){
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<Users> getAllPending() {
        List<Users> users = new ArrayList<>();
        //This will not work with the current database schema, because it does not have a surname column (as of 06/05)
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ers_users WHERE is_active = FALSE  AND role_id  != 'BANNED'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users user = new Users();
                user.setUser_id(rs.getString("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("PASSWORD"));
                user.setGiven_name(rs.getString("given_name"));
                user.setSurname(rs.getString("surname"));
                user.setIs_active(rs.getBoolean("is_active"));
                user.setRole_id(rs.getString("role_id"));
                users.add(user);
            }
        } catch (SQLException e) {
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }

        return users;

    }

    public void changePass(ResetUserPass request, String pass) {
        //This method updates *every* field of the database for a user.  Use with care!
        try{
            PreparedStatement ps = con.prepareStatement("Update ers_users SET password = ? WHERE username = ?");
            ps.setString(1, pass);
            ps.setString(2, request.getUsername());
            ps.executeUpdate();
        } catch (SQLException e){
            System.out.println("Error");
            //Need to create a custom sql exception throw to UserService. UserService should handle error logging.
            throw new RuntimeException(e.getMessage());
        }

    }
}
