package com.revature.ers.services;

import com.revature.ers.daos.UsersDAO;
import com.revature.ers.dtos.requests.LoginRequest;
import com.revature.ers.dtos.requests.NewUserRequest;
import com.revature.ers.dtos.responses.Principal;
import com.revature.ers.models.Users;
import com.revature.ers.util.custom_exceptions.InvalidAuthenticationException;
import com.revature.ers.util.custom_exceptions.InvalidRequestException;
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

                    // ecrypt password
                     user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

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
        if (isValidInfo(user)){
            return user;
        } else throw new InvalidAuthenticationException("Invalid credentials");
    }

    private boolean checkPass(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    private boolean isValidInfo(Users user) {
        if(user.getUsername() == null) return false;
        return true;
    }

    public List<Users> getAllUsers(){
        return usersDAO.getAll();
    }
}
