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
package org.apache.hadoop.yarn.client.api.impl;


import ReservationSystemTestUtil.reservationQ;
import java.io.File;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationListRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationListResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateResponse;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import org.apache.hadoop.yarn.api.records.ReservationRequests;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.UTCClock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This class is to test class {@link YarnClient) and {@link YarnClientImpl}
 * with Reservation.
 */
@RunWith(Parameterized.class)
public class TestYarnClientWithReservation {
    protected static final String TEST_DIR = new File(System.getProperty("test.build.data", "/tmp")).getAbsolutePath();

    protected static final String FS_ALLOC_FILE = new File(TestYarnClientWithReservation.TEST_DIR, "test-fs-queues.xml").getAbsolutePath();

    public enum SchedulerType {

        CAPACITY,
        FAIR;}

    private TestYarnClientWithReservation.SchedulerType schedulerType;

    public TestYarnClientWithReservation(TestYarnClientWithReservation.SchedulerType scheduler) {
        this.schedulerType = scheduler;
    }

    @Test
    public void testCreateReservation() throws Exception {
        MiniYARNCluster cluster = setupMiniYARNCluster();
        YarnClient client = setupYarnClient(cluster);
        try {
            Clock clock = new UTCClock();
            long arrival = clock.getTime();
            long duration = 60000;
            long deadline = ((long) (arrival + (1.05 * duration)));
            ReservationSubmissionRequest sRequest = submitReservationTestHelper(client, arrival, deadline, duration);
            // Submit the reservation again with the same request and make sure it
            // passes.
            client.submitReservation(sRequest);
            // Submit the reservation with the same reservation id but different
            // reservation definition, and ensure YarnException is thrown.
            arrival = clock.getTime();
            ReservationDefinition rDef = sRequest.getReservationDefinition();
            rDef.setArrival((arrival + duration));
            sRequest.setReservationDefinition(rDef);
            try {
                client.submitReservation(sRequest);
                Assert.fail(("Reservation submission should fail if a duplicate " + ("reservation id is used, but the reservation definition has been " + "updated.")));
            } catch (Exception e) {
                Assert.assertTrue((e instanceof YarnException));
            }
        } finally {
            // clean-up
            if (client != null) {
                client.stop();
            }
            cluster.stop();
        }
    }

    @Test
    public void testUpdateReservation() throws Exception {
        MiniYARNCluster cluster = setupMiniYARNCluster();
        YarnClient client = setupYarnClient(cluster);
        try {
            Clock clock = new UTCClock();
            long arrival = clock.getTime();
            long duration = 60000;
            long deadline = ((long) (arrival + (1.05 * duration)));
            ReservationSubmissionRequest sRequest = submitReservationTestHelper(client, arrival, deadline, duration);
            ReservationDefinition rDef = sRequest.getReservationDefinition();
            ReservationRequest rr = rDef.getReservationRequests().getReservationResources().get(0);
            ReservationId reservationID = sRequest.getReservationId();
            rr.setNumContainers(5);
            arrival = clock.getTime();
            duration = 30000;
            deadline = ((long) (arrival + (1.05 * duration)));
            rr.setDuration(duration);
            rDef.setArrival(arrival);
            rDef.setDeadline(deadline);
            ReservationUpdateRequest uRequest = ReservationUpdateRequest.newInstance(rDef, reservationID);
            ReservationUpdateResponse uResponse = client.updateReservation(uRequest);
            Assert.assertNotNull(uResponse);
            System.out.println(("Update reservation response: " + uResponse));
        } finally {
            // clean-up
            if (client != null) {
                client.stop();
            }
            cluster.stop();
        }
    }

    @Test
    public void testListReservationsByReservationId() throws Exception {
        MiniYARNCluster cluster = setupMiniYARNCluster();
        YarnClient client = setupYarnClient(cluster);
        try {
            Clock clock = new UTCClock();
            long arrival = clock.getTime();
            long duration = 60000;
            long deadline = ((long) (arrival + (1.05 * duration)));
            ReservationSubmissionRequest sRequest = submitReservationTestHelper(client, arrival, deadline, duration);
            ReservationId reservationID = sRequest.getReservationId();
            ReservationListRequest request = ReservationListRequest.newInstance(reservationQ, reservationID.toString(), (-1), (-1), false);
            ReservationListResponse response = client.listReservations(request);
            Assert.assertNotNull(response);
            Assert.assertEquals(1, response.getReservationAllocationState().size());
            Assert.assertEquals(response.getReservationAllocationState().get(0).getReservationId().getId(), reservationID.getId());
            Assert.assertEquals(response.getReservationAllocationState().get(0).getResourceAllocationRequests().size(), 0);
        } finally {
            // clean-up
            if (client != null) {
                client.stop();
            }
            cluster.stop();
        }
    }

    @Test
    public void testListReservationsByTimeInterval() throws Exception {
        MiniYARNCluster cluster = setupMiniYARNCluster();
        YarnClient client = setupYarnClient(cluster);
        try {
            Clock clock = new UTCClock();
            long arrival = clock.getTime();
            long duration = 60000;
            long deadline = ((long) (arrival + (1.05 * duration)));
            ReservationSubmissionRequest sRequest = submitReservationTestHelper(client, arrival, deadline, duration);
            // List reservations, search by a point in time within the reservation
            // range.
            arrival = clock.getTime();
            ReservationId reservationID = sRequest.getReservationId();
            ReservationListRequest request = ReservationListRequest.newInstance(reservationQ, "", (arrival + (duration / 2)), (arrival + (duration / 2)), true);
            ReservationListResponse response = client.listReservations(request);
            Assert.assertNotNull(response);
            Assert.assertEquals(1, response.getReservationAllocationState().size());
            Assert.assertEquals(response.getReservationAllocationState().get(0).getReservationId().getId(), reservationID.getId());
            // List reservations, search by time within reservation interval.
            request = ReservationListRequest.newInstance(reservationQ, "", 1, Long.MAX_VALUE, true);
            response = client.listReservations(request);
            Assert.assertNotNull(response);
            Assert.assertEquals(1, response.getReservationAllocationState().size());
            Assert.assertEquals(response.getReservationAllocationState().get(0).getReservationId().getId(), reservationID.getId());
            // Verify that the full resource allocations exist.
            Assert.assertTrue(((response.getReservationAllocationState().get(0).getResourceAllocationRequests().size()) > 0));
            // Verify that the full RDL is returned.
            ReservationRequests reservationRequests = response.getReservationAllocationState().get(0).getReservationDefinition().getReservationRequests();
            Assert.assertEquals("R_ALL", reservationRequests.getInterpreter().toString());
            Assert.assertTrue(((reservationRequests.getReservationResources().get(0).getDuration()) == duration));
        } finally {
            // clean-up
            if (client != null) {
                client.stop();
            }
            cluster.stop();
        }
    }

    @Test
    public void testListReservationsByInvalidTimeInterval() throws Exception {
        MiniYARNCluster cluster = setupMiniYARNCluster();
        YarnClient client = setupYarnClient(cluster);
        try {
            Clock clock = new UTCClock();
            long arrival = clock.getTime();
            long duration = 60000;
            long deadline = ((long) (arrival + (1.05 * duration)));
            ReservationSubmissionRequest sRequest = submitReservationTestHelper(client, arrival, deadline, duration);
            // List reservations, search by invalid end time == -1.
            ReservationListRequest request = ReservationListRequest.newInstance(reservationQ, "", 1, (-1), true);
            ReservationListResponse response = client.listReservations(request);
            Assert.assertNotNull(response);
            Assert.assertEquals(1, response.getReservationAllocationState().size());
            Assert.assertEquals(response.getReservationAllocationState().get(0).getReservationId().getId(), sRequest.getReservationId().getId());
            // List reservations, search by invalid end time < -1.
            request = ReservationListRequest.newInstance(reservationQ, "", 1, (-10), true);
            response = client.listReservations(request);
            Assert.assertNotNull(response);
            Assert.assertEquals(1, response.getReservationAllocationState().size());
            Assert.assertEquals(response.getReservationAllocationState().get(0).getReservationId().getId(), sRequest.getReservationId().getId());
        } finally {
            // clean-up
            if (client != null) {
                client.stop();
            }
            cluster.stop();
        }
    }

    @Test
    public void testListReservationsByTimeIntervalContainingNoReservations() throws Exception {
        MiniYARNCluster cluster = setupMiniYARNCluster();
        YarnClient client = setupYarnClient(cluster);
        try {
            Clock clock = new UTCClock();
            long arrival = clock.getTime();
            long duration = 60000;
            long deadline = ((long) (arrival + (1.05 * duration)));
            ReservationSubmissionRequest sRequest = submitReservationTestHelper(client, arrival, deadline, duration);
            // List reservations, search by very large start time.
            ReservationListRequest request = ReservationListRequest.newInstance(reservationQ, "", Long.MAX_VALUE, (-1), false);
            ReservationListResponse response = client.listReservations(request);
            // Ensure all reservations are filtered out.
            Assert.assertNotNull(response);
            Assert.assertEquals(response.getReservationAllocationState().size(), 0);
            duration = 30000;
            deadline = sRequest.getReservationDefinition().getDeadline();
            // List reservations, search by start time after the reservation
            // end time.
            request = ReservationListRequest.newInstance(reservationQ, "", (deadline + duration), (deadline + (2 * duration)), false);
            response = client.listReservations(request);
            // Ensure all reservations are filtered out.
            Assert.assertNotNull(response);
            Assert.assertEquals(response.getReservationAllocationState().size(), 0);
            arrival = clock.getTime();
            // List reservations, search by end time before the reservation start
            // time.
            request = ReservationListRequest.newInstance(reservationQ, "", 0, (arrival - duration), false);
            response = client.listReservations(request);
            // Ensure all reservations are filtered out.
            Assert.assertNotNull(response);
            Assert.assertEquals(response.getReservationAllocationState().size(), 0);
            // List reservations, search by very small end time.
            request = ReservationListRequest.newInstance(reservationQ, "", 0, 1, false);
            response = client.listReservations(request);
            // Ensure all reservations are filtered out.
            Assert.assertNotNull(response);
            Assert.assertEquals(response.getReservationAllocationState().size(), 0);
        } finally {
            // clean-up
            if (client != null) {
                client.stop();
            }
            cluster.stop();
        }
    }

    @Test
    public void testReservationDelete() throws Exception {
        MiniYARNCluster cluster = setupMiniYARNCluster();
        YarnClient client = setupYarnClient(cluster);
        try {
            Clock clock = new UTCClock();
            long arrival = clock.getTime();
            long duration = 60000;
            long deadline = ((long) (arrival + (1.05 * duration)));
            ReservationSubmissionRequest sRequest = submitReservationTestHelper(client, arrival, deadline, duration);
            ReservationId reservationID = sRequest.getReservationId();
            // Delete the reservation
            ReservationDeleteRequest dRequest = ReservationDeleteRequest.newInstance(reservationID);
            ReservationDeleteResponse dResponse = client.deleteReservation(dRequest);
            Assert.assertNotNull(dResponse);
            System.out.println(("Delete reservation response: " + dResponse));
            // List reservations, search by non-existent reservationID
            ReservationListRequest request = ReservationListRequest.newInstance(reservationQ, reservationID.toString(), (-1), (-1), false);
            ReservationListResponse response = client.listReservations(request);
            Assert.assertNotNull(response);
            Assert.assertEquals(0, response.getReservationAllocationState().size());
        } finally {
            // clean-up
            if (client != null) {
                client.stop();
            }
            cluster.stop();
        }
    }
}

