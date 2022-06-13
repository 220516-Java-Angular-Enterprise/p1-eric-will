package com.revature.ers.services;

import com.revature.ers.daos.UsersDAO;
import com.revature.ers.dtos.requests.*;
import com.revature.ers.dtos.responses.Principal;
import com.revature.ers.models.Users;
import com.revature.ers.util.custom_exceptions.InvalidAuthenticationException;
import com.revature.ers.util.custom_exceptions.InvalidRequestException;
import com.revature.ers.util.custom_exceptions.NotAuthorizedException;
import com.revature.ers.util.custom_exceptions.ResourceConflictException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UsersServices {

    private final UsersDAO usersDAO;
    public UsersServices(UsersDAO usersDAO){
        this.usersDAO = usersDAO;
    }

    public Users register(NewUserRequest request){
        Users user = request.extractUser();
        String username = user.getUsername();
        if (isUniqueUsername(username)){
            if(isValidUsername(username)){
                if(isValidPassword(user.getPassword())){
                    user.setUser_id(UUID.randomUUID().toString());


                    usersDAO.save(user);
                } else throw new InvalidRequestException("Invalid password. Minimum eight characters, at least one letter, one number and one special character.");
            } else throw new InvalidRequestException("Invalid username. Username needs to be 8-20 characters long.");
        } else throw new ResourceConflictException("Username is already taken :(");

        return user;
    }

    private List<String> getAllUserNames(){
        return usersDAO.getAll().stream().map(Users::getUsername).collect(Collectors.toList());
    }

    public boolean isUniqueUsername(String username) {
        List<String> usernames = getAllUserNames();
        return !usernames.contains(username);
    }

    public boolean isValidUsername(String username) {
        return username.matches("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$");
    }

    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
    }

    public Users login(LoginRequest request) {
        Users user =  usersDAO.getByUsernameandPassword(request.getUsername(), request.getPassword());

        if (isValidInfo(user) &&  !user.getRole_id().equals("BANNED")){
            return user;
        } else if (user == null){
            throw new InvalidAuthenticationException("Invalid credentials");
        }else{
            throw new NotAuthorizedException("Not allowed to login");
        }
    }

    private boolean isValidInfo(Users user) {
        if(user == null) return false;
        return true;
    }

    public List<Users> getAllUsers(){
        return usersDAO.getAll();
    }

    public void approveUser(ApproveNewUser user){
        usersDAO.updateIsActive(user.extractUser());
    }

    public void reject(RejectUser request) {
        usersDAO.reject(request.extractUser());
    }

    public List<Users> getAllPending(){
        return usersDAO.getAllPending();
    }

    public void changePass(ResetUserPass request, String pass) {
        usersDAO.changePass(request,pass);
    }
}
