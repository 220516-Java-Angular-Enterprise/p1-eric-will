package com.revature.ers.services;

import com.revature.ers.daos.UsersDAO;
import com.revature.ers.dtos.requests.LoginRequest;
import com.revature.ers.models.Users;
import com.revature.ers.util.custom_exceptions.NotAuthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServicesTest {

    @Spy
    private UsersDAO mockDAO;

    @InjectMocks
    private UsersServices usersServices;

    @Spy
    Users fakeUser= new Users();
    //LoginRequest fakeLoginRequest = new LoginRequest();

    @Test
    void login() {

        LoginRequest testRequest1 = new LoginRequest();
        testRequest1.setUsername("UsernameTaken1");
        testRequest1.setPassword("P@ssword32");
        fakeUser = new Users("1", testRequest1.getUsername(), " ", testRequest1.getPassword(),
                "", "",true,"BANNED");

        doReturn(fakeUser).when(mockDAO).getByUsernameandPassword("Banned1","P@ssword32");

        assertThrows(NotAuthorizedException.class, () -> usersServices.login(testRequest1));


        // Works
        LoginRequest testRequest3 = new LoginRequest();

    }

    @Test
    void getAllUsers() {
    }

    @Test
    void approveUser() {
    }

    @Test
    void reject() {
    }

    @Test
    void getAllPending() {
    }

    @Test
    void changePass() {
    }
}