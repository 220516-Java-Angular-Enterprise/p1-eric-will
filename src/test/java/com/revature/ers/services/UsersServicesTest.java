package com.revature.ers.services;

import com.revature.ers.daos.UsersDAO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class UsersServicesTest {

    @Mock
    private UsersDAO mockDAO;

    @InjectMocks
    private UsersServices usersServices;

    @Test
    void register() {
    }

    @Test
    void isUniqueUsername() {
    }

    @Test
    void isValidUsername() {
    }

    @Test
    void isValidPassword() {
    }

    @Test
    void login() {
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