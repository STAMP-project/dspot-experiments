/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager;


import java.util.Map;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateResponse;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.proto.YarnProtos.ReservationAllocationStateProto;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore.RMState;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.Plan;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystemTestUtil;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.junit.Assert;
import org.junit.Test;


public class TestReservationSystemWithRMHA extends RMHATestBase {
    @Test
    public void testSubmitReservationAndCheckAfterFailover() throws Exception {
        startRMs();
        addNodeCapacityToPlan(RMHATestBase.rm1, 102400, 100);
        ClientRMService clientService = getClientRMService();
        ReservationId reservationID = getNewReservation(clientService).getReservationId();
        // create a reservation
        ReservationSubmissionRequest request = createReservationSubmissionRequest(reservationID);
        ReservationSubmissionResponse response = null;
        try {
            response = clientService.submitReservation(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertNotNull(reservationID);
        LOG.info(("Submit reservation response: " + reservationID));
        // Do the failover
        explicitFailover();
        RMHATestBase.rm2.registerNode("127.0.0.1:1", 102400, 100);
        RMState state = getRMContext().getStateStore().loadState();
        Map<ReservationId, ReservationAllocationStateProto> reservationStateMap = state.getReservationState().get(ReservationSystemTestUtil.reservationQ);
        Assert.assertNotNull(reservationStateMap);
        Assert.assertNotNull(reservationStateMap.get(reservationID));
    }

    @Test
    public void testUpdateReservationAndCheckAfterFailover() throws Exception {
        startRMs();
        addNodeCapacityToPlan(RMHATestBase.rm1, 102400, 100);
        ClientRMService clientService = getClientRMService();
        ReservationId reservationID = getNewReservation(clientService).getReservationId();
        // create a reservation
        ReservationSubmissionRequest request = createReservationSubmissionRequest(reservationID);
        ReservationSubmissionResponse response = null;
        try {
            response = clientService.submitReservation(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertNotNull(reservationID);
        LOG.info(("Submit reservation response: " + reservationID));
        ReservationDefinition reservationDefinition = request.getReservationDefinition();
        // Change any field
        long newDeadline = (reservationDefinition.getDeadline()) + 100;
        reservationDefinition.setDeadline(newDeadline);
        ReservationUpdateRequest updateRequest = ReservationUpdateRequest.newInstance(reservationDefinition, reservationID);
        RMHATestBase.rm1.updateReservationState(updateRequest);
        // Do the failover
        explicitFailover();
        RMHATestBase.rm2.registerNode("127.0.0.1:1", 102400, 100);
        RMState state = getRMContext().getStateStore().loadState();
        Map<ReservationId, ReservationAllocationStateProto> reservationStateMap = state.getReservationState().get(ReservationSystemTestUtil.reservationQ);
        Assert.assertNotNull(reservationStateMap);
        ReservationAllocationStateProto reservationState = reservationStateMap.get(reservationID);
        Assert.assertEquals(newDeadline, reservationState.getReservationDefinition().getDeadline());
    }

    @Test
    public void testDeleteReservationAndCheckAfterFailover() throws Exception {
        startRMs();
        addNodeCapacityToPlan(RMHATestBase.rm1, 102400, 100);
        ClientRMService clientService = getClientRMService();
        ReservationId reservationID = getNewReservation(clientService).getReservationId();
        // create a reservation
        ReservationSubmissionRequest request = createReservationSubmissionRequest(reservationID);
        ReservationSubmissionResponse response = null;
        try {
            response = clientService.submitReservation(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertNotNull(reservationID);
        // Delete the reservation
        ReservationDeleteRequest deleteRequest = ReservationDeleteRequest.newInstance(reservationID);
        clientService.deleteReservation(deleteRequest);
        // Do the failover
        explicitFailover();
        RMHATestBase.rm2.registerNode("127.0.0.1:1", 102400, 100);
        RMState state = getRMContext().getStateStore().loadState();
        Assert.assertNull(state.getReservationState().get(ReservationSystemTestUtil.reservationQ));
    }

    @Test
    public void testSubmitReservationFailoverAndDelete() throws Exception {
        startRMs();
        addNodeCapacityToPlan(RMHATestBase.rm1, 102400, 100);
        ClientRMService clientService = getClientRMService();
        ReservationId reservationID = getNewReservation(clientService).getReservationId();
        // create a reservation
        ReservationSubmissionRequest request = createReservationSubmissionRequest(reservationID);
        ReservationSubmissionResponse response = null;
        try {
            response = clientService.submitReservation(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertNotNull(reservationID);
        LOG.info(("Submit reservation response: " + reservationID));
        ReservationDefinition reservationDefinition = request.getReservationDefinition();
        // Do the failover
        explicitFailover();
        addNodeCapacityToPlan(RMHATestBase.rm2, 102400, 100);
        // check if reservation exists after failover
        Plan plan = getRMContext().getReservationSystem().getPlan(ReservationSystemTestUtil.reservationQ);
        validateReservation(plan, reservationID, reservationDefinition);
        // delete the reservation
        ReservationDeleteRequest deleteRequest = ReservationDeleteRequest.newInstance(reservationID);
        ReservationDeleteResponse deleteResponse = null;
        clientService = RMHATestBase.rm2.getClientRMService();
        try {
            deleteResponse = clientService.deleteReservation(deleteRequest);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(deleteResponse);
        Assert.assertNull(plan.getReservationById(reservationID));
    }

    @Test
    public void testFailoverAndSubmitReservation() throws Exception {
        startRMs();
        addNodeCapacityToPlan(RMHATestBase.rm1, 102400, 100);
        // Do the failover
        explicitFailover();
        addNodeCapacityToPlan(RMHATestBase.rm2, 102400, 100);
        ClientRMService clientService = getClientRMService();
        ReservationId reservationID = getNewReservation(clientService).getReservationId();
        // create a reservation
        ReservationSubmissionRequest request = createReservationSubmissionRequest(reservationID);
        ReservationSubmissionResponse response = null;
        try {
            response = clientService.submitReservation(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertNotNull(reservationID);
        LOG.info(("Submit reservation response: " + reservationID));
        ReservationDefinition reservationDefinition = request.getReservationDefinition();
        // check if reservation is submitted successfully
        Plan plan = getRMContext().getReservationSystem().getPlan(ReservationSystemTestUtil.reservationQ);
        validateReservation(plan, reservationID, reservationDefinition);
    }

    @Test
    public void testSubmitReservationFailoverAndUpdate() throws Exception {
        startRMs();
        addNodeCapacityToPlan(RMHATestBase.rm1, 102400, 100);
        ClientRMService clientService = getClientRMService();
        ReservationId reservationID = getNewReservation(clientService).getReservationId();
        // create a reservation
        ReservationSubmissionRequest request = createReservationSubmissionRequest(reservationID);
        ReservationSubmissionResponse response = null;
        try {
            response = clientService.submitReservation(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertNotNull(reservationID);
        LOG.info(("Submit reservation response: " + reservationID));
        ReservationDefinition reservationDefinition = request.getReservationDefinition();
        // Do the failover
        explicitFailover();
        addNodeCapacityToPlan(RMHATestBase.rm2, 102400, 100);
        // check if reservation exists after failover
        Plan plan = getRMContext().getReservationSystem().getPlan(ReservationSystemTestUtil.reservationQ);
        validateReservation(plan, reservationID, reservationDefinition);
        // update the reservation
        long newDeadline = (reservationDefinition.getDeadline()) + 100;
        reservationDefinition.setDeadline(newDeadline);
        ReservationUpdateRequest updateRequest = ReservationUpdateRequest.newInstance(reservationDefinition, reservationID);
        ReservationUpdateResponse updateResponse = null;
        clientService = RMHATestBase.rm2.getClientRMService();
        try {
            updateResponse = clientService.updateReservation(updateRequest);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(updateResponse);
        validateReservation(plan, reservationID, reservationDefinition);
    }

    @Test
    public void testSubmitUpdateReservationFailoverAndDelete() throws Exception {
        startRMs();
        addNodeCapacityToPlan(RMHATestBase.rm1, 102400, 100);
        ClientRMService clientService = getClientRMService();
        ReservationId reservationID = getNewReservation(clientService).getReservationId();
        // create a reservation
        ReservationSubmissionRequest request = createReservationSubmissionRequest(reservationID);
        ReservationSubmissionResponse response = null;
        try {
            response = clientService.submitReservation(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertNotNull(reservationID);
        LOG.info(("Submit reservation response: " + reservationID));
        ReservationDefinition reservationDefinition = request.getReservationDefinition();
        // check if reservation is submitted successfully
        Plan plan = getRMContext().getReservationSystem().getPlan(ReservationSystemTestUtil.reservationQ);
        validateReservation(plan, reservationID, reservationDefinition);
        // update the reservation
        long newDeadline = (reservationDefinition.getDeadline()) + 100;
        reservationDefinition.setDeadline(newDeadline);
        ReservationUpdateRequest updateRequest = ReservationUpdateRequest.newInstance(reservationDefinition, reservationID);
        ReservationUpdateResponse updateResponse = null;
        try {
            updateResponse = clientService.updateReservation(updateRequest);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(updateResponse);
        validateReservation(plan, reservationID, reservationDefinition);
        // Do the failover
        explicitFailover();
        addNodeCapacityToPlan(RMHATestBase.rm2, 102400, 100);
        // check if reservation exists after failover
        plan = getRMContext().getReservationSystem().getPlan(ReservationSystemTestUtil.reservationQ);
        validateReservation(plan, reservationID, reservationDefinition);
        // delete the reservation
        ReservationDeleteRequest deleteRequest = ReservationDeleteRequest.newInstance(reservationID);
        ReservationDeleteResponse deleteResponse = null;
        clientService = RMHATestBase.rm2.getClientRMService();
        try {
            deleteResponse = clientService.deleteReservation(deleteRequest);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(deleteResponse);
        Assert.assertNull(plan.getReservationById(reservationID));
    }

    @Test
    public void testReservationResizeAfterFailover() throws Exception {
        startRMs();
        addNodeCapacityToPlan(RMHATestBase.rm1, 102400, 100);
        ClientRMService clientService = getClientRMService();
        ReservationId resID1 = getNewReservation(clientService).getReservationId();
        // create a reservation
        ReservationSubmissionRequest request = createReservationSubmissionRequest(resID1);
        ReservationDefinition reservationDefinition = request.getReservationDefinition();
        ReservationSubmissionResponse response = null;
        try {
            response = clientService.submitReservation(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertNotNull(resID1);
        LOG.info(("Submit reservation response: " + resID1));
        ReservationId resID2 = getNewReservation(clientService).getReservationId();
        request.setReservationId(resID2);
        try {
            response = clientService.submitReservation(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertNotNull(resID2);
        LOG.info(("Submit reservation response: " + resID2));
        ReservationId resID3 = getNewReservation(clientService).getReservationId();
        request.setReservationId(resID3);
        try {
            response = clientService.submitReservation(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(response);
        Assert.assertNotNull(resID3);
        LOG.info(("Submit reservation response: " + resID3));
        // allow the reservations to become active
        waitForReservationActivation(RMHATestBase.rm1, resID1, ReservationSystemTestUtil.reservationQ);
        // validate reservations before failover
        Plan plan = getRMContext().getReservationSystem().getPlan(ReservationSystemTestUtil.reservationQ);
        validateReservation(plan, resID1, reservationDefinition);
        validateReservation(plan, resID2, reservationDefinition);
        validateReservation(plan, resID3, reservationDefinition);
        ResourceScheduler scheduler = getResourceScheduler();
        QueueInfo resQ1 = scheduler.getQueueInfo(resID1.toString(), false, false);
        Assert.assertEquals(0.05, resQ1.getCapacity(), 0.001F);
        QueueInfo resQ2 = scheduler.getQueueInfo(resID2.toString(), false, false);
        Assert.assertEquals(0.05, resQ2.getCapacity(), 0.001F);
        QueueInfo resQ3 = scheduler.getQueueInfo(resID3.toString(), false, false);
        Assert.assertEquals(0.05, resQ3.getCapacity(), 0.001F);
        // Do the failover
        explicitFailover();
        addNodeCapacityToPlan(RMHATestBase.rm2, 5120, 5);
        // check if reservations exists after failover
        plan = getRMContext().getReservationSystem().getPlan(ReservationSystemTestUtil.reservationQ);
        validateReservation(plan, resID1, reservationDefinition);
        validateReservation(plan, resID3, reservationDefinition);
        // verify if the reservations have been resized
        scheduler = RMHATestBase.rm2.getResourceScheduler();
        resQ1 = scheduler.getQueueInfo(resID1.toString(), false, false);
        Assert.assertEquals((1.0F / 3.0F), resQ1.getCapacity(), 0.001F);
        resQ2 = scheduler.getQueueInfo(resID2.toString(), false, false);
        Assert.assertEquals((1.0F / 3.0F), resQ2.getCapacity(), 0.001F);
        resQ3 = scheduler.getQueueInfo(resID3.toString(), false, false);
        Assert.assertEquals((1.0F / 3.0F), resQ3.getCapacity(), 0.001F);
    }
}

