package com.revature.ers.services;

import com.revature.ers.daos.UsersDAO;
import com.revature.ers.dtos.requests.*;
import com.revature.ers.models.Users;
import com.revature.ers.util.custom_exceptions.InvalidAuthenticationException;
import com.revature.ers.util.custom_exceptions.InvalidRequestException;
import com.revature.ers.util.custom_exceptions.NotAuthorizedException;
import com.revature.ers.util.custom_exceptions.ResourceConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Spy
    LoginRequest fakeLoginRequest = new LoginRequest();

    @Spy
    NewUserRequest fakeUserRequest = new NewUserRequest();

    @Spy
    List<String> fakeUsername = new ArrayList<>();

    @Spy
    List<Users> fakeUserList = new ArrayList<>();

    @Spy
    ApproveNewUser fakeApproved = new ApproveNewUser();

    @Spy
    ResetUserPass fakeReset = new ResetUserPass();

    @Spy
    RejectUser fakeReject = new RejectUser();



    @Test
    void login() {

        // Banned ------------------------------
        fakeLoginRequest.setUsername("Username1");
        fakeLoginRequest.setPassword("P@ssword32");
        fakeUser = new Users("1", fakeLoginRequest.getUsername(), " ", fakeLoginRequest.getPassword(),
                "", "",true,"BANNED");

        doReturn(fakeUser).when(mockDAO).getByUsernameandPassword("Username1","P@ssword32");
        assertThrows(NotAuthorizedException.class, () -> usersServices.login(fakeLoginRequest));
        // ------------------------------------------

        // Invalid User cred

        doReturn(null).when(mockDAO).getByUsernameandPassword("Username1","P@ssword32");
        assertThrows(InvalidAuthenticationException.class, () -> usersServices.login(fakeLoginRequest));

        // ------------------------------------------

        // Works
        LoginRequest testRequest3 = new LoginRequest();

        fakeUser = new Users("1", fakeLoginRequest.getUsername(), " ", fakeLoginRequest.getPassword(),
                "", "",true,"DEFAULT");

        doReturn(fakeUser).when(mockDAO).getByUsernameandPassword("Username1","P@ssword32");
        assertEquals(fakeUser, usersServices.login(fakeLoginRequest));
        // ------------------------------------------

    }

    @Test
    void register(){

        // Taken Username
        fakeUser.setUsername("Taken1");
        fakeUserList.add(fakeUser);
        fakeUserRequest.setUsername("Taken1");
        doReturn(fakeUserList).when(mockDAO).getAll();
        assertThrows(ResourceConflictException.class, ()-> usersServices.register(fakeUserRequest));
        //------------Invalid Username
        fakeUserRequest.setUsername("no");
        assertThrows(InvalidRequestException.class, ()-> usersServices.register(fakeUserRequest));
        //------------Invalid Password
        fakeUserRequest.setUsername("Phutondog123");
        fakeUserRequest.setPassword("no");
        assertThrows(InvalidRequestException.class, ()-> usersServices.register(fakeUserRequest));
        //--------------Valid User
        fakeUserRequest.setUsername("Phutondog1234");
        fakeUserRequest.setPassword("P@ssword1234");

        doNothing().when(mockDAO).save(any());
        assertEquals(fakeUserRequest.extractUser().getUsername(),usersServices.register(fakeUserRequest).getUsername());
        assertEquals(fakeUserRequest.extractUser().getPassword(),usersServices.register(fakeUserRequest).getPassword());
    }


    @Test
    void getAllUsers() {

        // Test when only called once
        doReturn(null).when(mockDAO).getAll();
        usersServices.getAllUsers();
        verify(mockDAO, times(1)).getAll();

        //-----------------------------------

        // Test Users in list
        fakeUserList.add(new Users("Fake1", "1", "DEFAULT"));
        fakeUserList.add(new Users("Fake2", "2", "FINMAN"));
        fakeUserList.add(new Users("Fake3", "3", "FINMAN"));
        fakeUserList.add(new Users("Fake4", "4", "DEFAULT"));
        fakeUserList.add(new Users("Fake5", "5", "ADMIN"));

        doReturn(fakeUserList).when(mockDAO).getAll();
        assertEquals(5,usersServices.getAllUsers().size());
        assertEquals(2, (int) usersServices.getAllUsers().stream().filter(user -> user.getRole_id().equals("DEFAULT")).count());
        assertEquals(1, (int) usersServices.getAllUsers().stream().filter(user -> user.getRole_id().equals("ADMIN")).count());
        assertEquals(2, (int) usersServices.getAllUsers().stream().filter(user -> user.getRole_id().equals("FINMAN")).count());

    }

    @Test
    void getAllPending() {

        // Test when only called once
        doReturn(null).when(mockDAO).getAllPending();
        usersServices.getAllPending();
        verify(mockDAO, times(1)).getAllPending();

        //-----------------------------------

        // Test Users in list
        fakeUserList.add(new Users("1","Fake1", "1", "fake@email.com","fakePass","fakeName",false, "DEFAULT"));
        fakeUserList.add(new Users("2","Fake2", "2", "fake@email.com","fakePass","fakeName",false, "DEFAULT"));
        fakeUserList.add(new Users("3","Fake3", "3", "fake@email.com","fakePass","fakeName",false, "DEFAULT"));
        fakeUserList.add(new Users("4","Fake4", "4", "fake@email.com","fakePass","fakeName",false, "DEFAULT"));
        fakeUserList.add(new Users("5","Fake5", "5", "fake@email.com","fakePass","fakeName",false, "DEFAULT"));

        doReturn(fakeUserList).when(mockDAO).getAllPending();
        assertEquals(5,usersServices.getAllPending().size());
        assertEquals(5, (int) usersServices.getAllPending().stream().filter(user -> !user.isIs_active()).count());
    }

    @Test
    void approveUser() {
        // Test when only called once
        doNothing().when(mockDAO).updateIsActive(any());
        usersServices.approveUser(fakeApproved);
        verify(mockDAO, times(1)).updateIsActive(any());
        //-----------------------------------
    }

    @Test
    void reject() {
        // Test when only called once
        doNothing().when(mockDAO).reject(any());
        usersServices.reject(fakeReject);
        verify(mockDAO, times(1)).reject(any());
        //-----------------------------------
    }



    @Test
    void changePass() {
        // Test when only called once
        doNothing().when(mockDAO).changePass(any(),any());
        usersServices.changePass(fakeReset, "fakepass");
        verify(mockDAO, times(1)).changePass(any(),any());
        //-----------------------------------
    }
}