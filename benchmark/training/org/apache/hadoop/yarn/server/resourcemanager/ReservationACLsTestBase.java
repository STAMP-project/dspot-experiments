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


import ReservationRequestInterpreter.R_ALL;
import java.util.Collections;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import org.apache.hadoop.yarn.api.records.ReservationRequests;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ReservationACLsTestBase extends ACLsTestBase {
    private final int defaultDuration = 600000;

    private final ReservationRequest defaultRequest = ReservationRequest.newInstance(BuilderUtils.newResource(1024, 1), 1, 1, defaultDuration);

    private final ReservationRequests defaultRequests = ReservationRequests.newInstance(Collections.singletonList(defaultRequest), R_ALL);

    private Configuration configuration;

    private boolean useFullQueuePath;

    public ReservationACLsTestBase(Configuration conf, boolean useFullPath) {
        configuration = conf;
        useFullQueuePath = useFullPath;
    }

    @Test
    public void testApplicationACLs() throws Exception {
        registerNode("test:1234", 8192, 8);
        String queueA = (!(useFullQueuePath)) ? ACLsTestBase.QUEUEA : ((CapacitySchedulerConfiguration.ROOT) + ".") + (ACLsTestBase.QUEUEA);
        String queueB = (!(useFullQueuePath)) ? ACLsTestBase.QUEUEB : ((CapacitySchedulerConfiguration.ROOT) + ".") + (ACLsTestBase.QUEUEB);
        String queueC = (!(useFullQueuePath)) ? ACLsTestBase.QUEUEC : ((CapacitySchedulerConfiguration.ROOT) + ".") + (ACLsTestBase.QUEUEC);
        // Submit Reservations
        // Users of queue A can submit reservations on QueueA.
        verifySubmitReservationSuccess(ACLsTestBase.QUEUE_A_USER, queueA);
        verifySubmitReservationSuccess(ACLsTestBase.QUEUE_A_ADMIN, queueA);
        // Users of queue B cannot submit reservations on QueueA.
        verifySubmitReservationFailure(ACLsTestBase.QUEUE_B_USER, queueA);
        verifySubmitReservationFailure(ACLsTestBase.QUEUE_B_ADMIN, queueA);
        // Users of queue B can submit reservations on QueueB.
        verifySubmitReservationSuccess(ACLsTestBase.QUEUE_B_USER, queueB);
        verifySubmitReservationSuccess(ACLsTestBase.QUEUE_B_ADMIN, queueB);
        // Users of queue A cannot submit reservations on QueueB.
        verifySubmitReservationFailure(ACLsTestBase.QUEUE_A_USER, queueB);
        verifySubmitReservationFailure(ACLsTestBase.QUEUE_A_ADMIN, queueB);
        // Everyone can submit reservations on QueueC.
        verifySubmitReservationSuccess(ACLsTestBase.QUEUE_B_USER, queueC);
        verifySubmitReservationSuccess(ACLsTestBase.QUEUE_B_ADMIN, queueC);
        verifySubmitReservationSuccess(ACLsTestBase.QUEUE_A_USER, queueC);
        verifySubmitReservationSuccess(ACLsTestBase.QUEUE_A_ADMIN, queueC);
        verifySubmitReservationSuccess(ACLsTestBase.COMMON_USER, queueC);
        // List Reservations
        // User with List Reservations, or Admin ACL can list everyone's
        // reservations.
        verifyListReservationSuccess(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_A_USER, queueA);
        verifyListReservationSuccess(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_A_ADMIN, queueA);
        verifyListReservationSuccess(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_A_USER, queueA);
        // User without Admin or Reservation ACL can only list their own
        // reservations by id.
        verifyListReservationSuccess(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_A_ADMIN, queueA);
        verifyListReservationFailure(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_A_USER, queueA);
        verifyListReservationFailure(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_A_ADMIN, queueA);
        verifyListReservationByIdSuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_A_USER, queueA);
        verifyListReservationByIdFailure(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_A_ADMIN, queueA);
        // User with List Reservations, or Admin ACL can list everyone's
        // reservations.
        verifyListReservationSuccess(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_B_USER, queueB);
        verifyListReservationSuccess(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_B_ADMIN, queueB);
        verifyListReservationSuccess(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_B_USER, queueB);
        // User without Admin or Reservation ACL can only list their own
        // reservations by id.
        verifyListReservationSuccess(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_B_ADMIN, queueB);
        verifyListReservationFailure(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_B_USER, queueB);
        verifyListReservationFailure(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_B_ADMIN, queueB);
        verifyListReservationByIdSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_B_USER, queueB);
        verifyListReservationByIdFailure(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_B_ADMIN, queueB);
        // Users with Admin ACL in one queue cannot list reservations in
        // another queue
        verifyListReservationFailure(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_A_ADMIN, queueA);
        verifyListReservationFailure(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_A_USER, queueA);
        verifyListReservationFailure(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_B_ADMIN, queueB);
        verifyListReservationFailure(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_B_USER, queueB);
        // All users can list reservations on QueueC because acls are enabled
        // but not defined.
        verifyListReservationSuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_A_ADMIN, queueC);
        verifyListReservationSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_A_ADMIN, queueC);
        verifyListReservationSuccess(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_A_ADMIN, queueC);
        verifyListReservationSuccess(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_A_ADMIN, queueC);
        verifyListReservationSuccess(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_A_USER, queueC);
        verifyListReservationSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_A_USER, queueC);
        verifyListReservationSuccess(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_A_USER, queueC);
        verifyListReservationSuccess(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_A_USER, queueC);
        verifyListReservationByIdSuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_A_ADMIN, queueC);
        verifyListReservationByIdSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_A_ADMIN, queueC);
        verifyListReservationByIdSuccess(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_A_ADMIN, queueC);
        verifyListReservationByIdSuccess(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_A_ADMIN, queueC);
        verifyListReservationByIdSuccess(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_A_USER, queueC);
        verifyListReservationByIdSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_A_USER, queueC);
        verifyListReservationByIdSuccess(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_A_USER, queueC);
        verifyListReservationByIdSuccess(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_A_USER, queueC);
        // Delete Reservations
        // Only the user who made the reservation or an admin can delete it.
        verifyDeleteReservationSuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_A_USER, queueA);
        verifyDeleteReservationSuccess(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_A_USER, queueA);
        // A non-admin cannot delete another user's reservation.
        verifyDeleteReservationFailure(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_A_USER, queueA);
        verifyDeleteReservationFailure(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_A_USER, queueA);
        verifyDeleteReservationFailure(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_A_USER, queueA);
        // Only the user who made the reservation or an admin can delete it.
        verifyDeleteReservationSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_B_USER, queueB);
        verifyDeleteReservationSuccess(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_B_USER, queueB);
        // A non-admin cannot delete another user's reservation.
        verifyDeleteReservationFailure(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_B_USER, queueB);
        verifyDeleteReservationFailure(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_B_USER, queueB);
        verifyDeleteReservationFailure(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_B_USER, queueB);
        // All users can delete any reservation on QueueC because acls are enabled
        // but not defined.
        verifyDeleteReservationSuccess(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_B_ADMIN, queueC);
        verifyDeleteReservationSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_B_ADMIN, queueC);
        verifyDeleteReservationSuccess(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_B_ADMIN, queueC);
        verifyDeleteReservationSuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_B_ADMIN, queueC);
        verifyDeleteReservationSuccess(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_B_ADMIN, queueC);
        // Update Reservation
        // Only the user who made the reservation or an admin can update it.
        verifyUpdateReservationSuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_A_USER, queueA);
        verifyUpdateReservationSuccess(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_A_USER, queueA);
        // A non-admin cannot update another user's reservation.
        verifyUpdateReservationFailure(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_A_USER, queueA);
        verifyUpdateReservationFailure(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_A_USER, queueA);
        verifyUpdateReservationFailure(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_A_USER, queueA);
        // Only the user who made the reservation or an admin can update it.
        verifyUpdateReservationSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_B_USER, queueB);
        verifyUpdateReservationSuccess(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_B_USER, queueB);
        // A non-admin cannot update another user's reservation.
        verifyUpdateReservationFailure(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_B_USER, queueB);
        verifyUpdateReservationFailure(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_B_USER, queueB);
        verifyUpdateReservationFailure(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_B_USER, queueB);
        // All users can update any reservation on QueueC because acls are enabled
        // but not defined.
        verifyUpdateReservationSuccess(ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUE_B_ADMIN, queueC);
        verifyUpdateReservationSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_B_ADMIN, queueC);
        verifyUpdateReservationSuccess(ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUE_B_ADMIN, queueC);
        verifyUpdateReservationSuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_B_ADMIN, queueC);
        verifyUpdateReservationSuccess(ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUE_B_ADMIN, queueC);
    }
}

