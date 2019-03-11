package com.baeldung.algorithms.enumstatemachine;


import LeaveRequestState.Approved;
import LeaveRequestState.Escalated;
import org.junit.Assert;
import org.junit.Test;

import static LeaveRequestState.Approved;
import static LeaveRequestState.Escalated;
import static LeaveRequestState.Submitted;


public class LeaveRequestStateUnitTest {
    @Test
    public void givenLeaveRequest_whenStateEscalated_thenResponsibleIsTeamLeader() {
        LeaveRequestState state = Escalated;
        Assert.assertEquals(state.responsiblePerson(), "Team Leader");
    }

    @Test
    public void givenLeaveRequest_whenStateApproved_thenResponsibleIsDepartmentManager() {
        LeaveRequestState state = Approved;
        Assert.assertEquals(state.responsiblePerson(), "Department Manager");
    }

    @Test
    public void givenLeaveRequest_whenNextStateIsCalled_thenStateIsChanged() {
        LeaveRequestState state = Submitted;
        state = state.nextState();
        Assert.assertEquals(state, Escalated);
        state = state.nextState();
        Assert.assertEquals(state, Approved);
        state = state.nextState();
        Assert.assertEquals(state, Approved);
    }
}

