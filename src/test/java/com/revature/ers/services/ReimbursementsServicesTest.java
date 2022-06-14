package com.revature.ers.services;

import com.revature.ers.daos.ReimbursementsDAO;
import com.revature.ers.dtos.requests.NewReimbRequest;
import com.revature.ers.dtos.requests.ResolveReimbRequest;
import com.revature.ers.dtos.requests.UpdateReimbDescr;
import com.revature.ers.models.Reimbursements;
import com.revature.ers.util.custom_exceptions.ForbiddenUserException;
import com.revature.ers.util.custom_exceptions.InvalidRequestException;
import com.revature.ers.util.custom_exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)

class ReimbursementsServicesTest {
//create mock objects and mock dependency injections:
    @Spy
    private ReimbursementsDAO mockReimbDAO;
    @InjectMocks
    private ReimbursementsServices mockReimbService;
    @Spy
    Reimbursements mockReimb = new Reimbursements();
    @Spy
    NewReimbRequest mockNewReimbRequest = new NewReimbRequest();
    @Spy
    ResolveReimbRequest mockResolveReimbRequest = new ResolveReimbRequest();
    @Spy
    UpdateReimbDescr mockUpdateReimbDescr = new UpdateReimbDescr();

    @Test
    void register() {

        //verify that the method correctly creates a reimbursement and returns it from the request.
        mockNewReimbRequest.setAmount(1.00);
        mockNewReimbRequest.setDescription("foo");
        mockNewReimbRequest.setType_id("MISC");
        doNothing().when(mockReimbDAO).save(any());
        assertEquals(mockNewReimbRequest.getDescription(), mockReimbService.register(mockNewReimbRequest, "bar").getDescription());

        //verify that having a nonexistent reimbursement type throws invalidrequestexception
        mockNewReimbRequest.setType_id("wrong");
        assertThrows(InvalidRequestException.class, () -> mockReimbService.register(mockNewReimbRequest,"bar"));


    }

    @Test
    void getAllPending() {
    //        return reimbDAO.getAllPending();

        //method only calls the DAO method

    }

    @Test
    void getByAuthorID() {
        //        return reimbDAO.getByAuthorID(userID);

        //method only calls the DAO method



    }

    @Test
    void getByResolverID() {
        //        return reimbDAO.getByResolverID(resolverID);

        //method only calls the DAO method


    }

    @Test
    void resolve() {


        mockResolveReimbRequest.setResolver_id("tester1");
        mockResolveReimbRequest.setReimb_id("test1");
        mockResolveReimbRequest.setStatus_id("APPROVED");
        //verify that does not work if id does not match a reimb in database
        when(mockReimbDAO.getById(any())).thenReturn(null);
        assertThrows(InvalidRequestException.class,()->mockReimbService.resolve(mockResolveReimbRequest));

        //verify that does not work if retrieved reimb is not pending
        mockReimb.setStatus_id("DENIED");
        when(mockReimbDAO.getById(any())).thenReturn(mockReimb);
        assertThrows(InvalidRequestException.class, () -> mockReimbService.resolve(mockResolveReimbRequest));
        mockReimb.setStatus_id("APPROVED");
        assertThrows(InvalidRequestException.class, () -> mockReimbService.resolve(mockResolveReimbRequest));

        //will return the reimbursement retrieved by getById
        mockReimb.setStatus_id("PENDING");
        mockReimb.setReimb_id("test1");
        assertEquals(mockReimb.getReimb_id(), mockReimbService.resolve(mockResolveReimbRequest).getReimb_id());

        //verify that does not work if new status is incorrect (even without this catch, there would be an SQL error, since other status_IDs
        //  would not have a corresponding key
        mockResolveReimbRequest.setStatus_id("PENDING");
        assertThrows(InvalidRequestException.class, () -> mockReimbService.resolve(mockResolveReimbRequest));

    }

    @Test
    void updateDescr() {

        mockUpdateReimbDescr.setReimb_id("1");
        mockUpdateReimbDescr.setDescription("updated");
        //verify that does not work if returned reimbursement is null, which occurs when the reimb_id isn't in the database
        when(mockReimbDAO.getById(any())).thenReturn(null);
        assertThrows(InvalidRequestException.class, ()->mockReimbService.updateDescr(mockUpdateReimbDescr,"foo"));
        //verify that does not work if user has the incorrect userID
        mockReimb.setAuthor_id("bar");
        mockReimb.setStatus_id("PENDING");
        when(mockReimbDAO.getById(any())).thenReturn(mockReimb);
        assertThrows(ForbiddenUserException.class, ()->mockReimbService.updateDescr(mockUpdateReimbDescr,"foo"));
        //verify that does not work if retrieved reimb is not pending
        mockReimb.setStatus_id("APPROVED");
        assertThrows(InvalidRequestException.class, ()->mockReimbService.updateDescr(mockUpdateReimbDescr, "bar"));
    }
}