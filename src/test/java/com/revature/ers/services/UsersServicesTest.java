package com.revature.ers.services;

import com.revature.ers.daos.UsersDAO;
import com.revature.ers.models.Users;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.jupiter.api.Assertions.*;

class UsersServicesTest {
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private UsersDAO mockDAO;

    @InjectMocks
    private UsersServices usersServices;

    @Rule
    public MockitoRule initRule = MockitoJUnit.rule();

    @Spy
    Users fakeUser= new Users();

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