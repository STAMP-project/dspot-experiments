/**
 * *****************************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * *****************************************************************************
 */
package org.apache.hadoop.yarn.server.resourcemanager.reservation;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationListRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationDeleteRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationListRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationSubmissionRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationUpdateRequestPBImpl;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.Clock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestReservationInputValidator {
    private static final Logger LOG = LoggerFactory.getLogger(TestReservationInputValidator.class);

    private static final String PLAN_NAME = "test-reservation";

    private Clock clock;

    private Map<String, Plan> plans = new HashMap<String, Plan>(1);

    private ReservationSystem rSystem;

    private Plan plan;

    private ReservationInputValidator rrValidator;

    @Test
    public void testSubmitReservationNormal() {
        ReservationSubmissionRequest request = createSimpleReservationSubmissionRequest(1, 1, 1, 5, 3);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
        } catch (YarnException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(plan);
    }

    @Test
    public void testSubmitReservationDoesNotExist() {
        ReservationSubmissionRequest request = new ReservationSubmissionRequestPBImpl();
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertEquals(("The queue is not specified. Please try again with a " + "valid reservable queue."), message);
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testSubmitReservationInvalidPlan() {
        ReservationSubmissionRequest request = createSimpleReservationSubmissionRequest(1, 1, 1, 5, 3);
        Mockito.when(rSystem.getPlan(TestReservationInputValidator.PLAN_NAME)).thenReturn(null);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.endsWith(" is not managed by reservation system. Please try again with a valid reservable queue."));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testSubmitReservationNoDefinition() {
        ReservationSubmissionRequest request = new ReservationSubmissionRequestPBImpl();
        request.setQueue(TestReservationInputValidator.PLAN_NAME);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertEquals(("Missing reservation definition. Please try again by " + "specifying a reservation definition."), message);
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testSubmitReservationInvalidDeadline() {
        ReservationSubmissionRequest request = createSimpleReservationSubmissionRequest(1, 1, 1, 0, 3);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("The specified deadline: 0 is the past"));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testSubmitReservationInvalidRR() {
        ReservationSubmissionRequest request = createSimpleReservationSubmissionRequest(0, 0, 1, 5, 3);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("No resources have been specified to reserve"));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testSubmitReservationEmptyRR() {
        ReservationSubmissionRequest request = createSimpleReservationSubmissionRequest(1, 0, 1, 5, 3);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("No resources have been specified to reserve"));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testSubmitReservationInvalidDuration() {
        ReservationSubmissionRequest request = createSimpleReservationSubmissionRequest(1, 1, 1, 3, 4);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("The time difference"));
            Assert.assertTrue(message.contains("must  be greater or equal to the minimum resource duration"));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testSubmitReservationExceedsGangSize() {
        ReservationSubmissionRequest request = createSimpleReservationSubmissionRequest(1, 1, 1, 5, 4);
        Resource resource = Resource.newInstance(512, 1);
        Mockito.when(plan.getTotalCapacity()).thenReturn(resource);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("The size of the largest gang in the reservation definition"));
            Assert.assertTrue(message.contains("exceed the capacity available "));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testSubmitReservationValidRecurrenceExpression() {
        ReservationSubmissionRequest request = createSimpleReservationSubmissionRequest(1, 1, 1, 5, 3, "600000");
        plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
        } catch (YarnException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(plan);
    }

    @Test
    public void testSubmitReservationNegativeRecurrenceExpression() {
        ReservationSubmissionRequest request = createSimpleReservationSubmissionRequest(1, 1, 1, 5, 3, "-1234");
        plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("Negative Period : "));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testSubmitReservationMaxPeriodIndivisibleByRecurrenceExp() {
        long indivisibleRecurrence = ((YarnConfiguration.DEFAULT_RM_RESERVATION_SYSTEM_MAX_PERIODICITY) / 2) + 1;
        String recurrenceExp = Long.toString(indivisibleRecurrence);
        ReservationSubmissionRequest request = createSimpleReservationSubmissionRequest(1, 1, 1, 5, 3, recurrenceExp);
        plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("The maximum periodicity:"));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testSubmitReservationInvalidRecurrenceExpression() {
        // first check recurrence expression
        ReservationSubmissionRequest request = createSimpleReservationSubmissionRequest(1, 1, 1, 5, 3, "123abc");
        plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("Invalid period "));
            TestReservationInputValidator.LOG.info(message);
        }
        // now check duration
        request = createSimpleReservationSubmissionRequest(1, 1, 1, 50, 3, "10");
        plan = null;
        try {
            plan = rrValidator.validateReservationSubmissionRequest(rSystem, request, ReservationSystemTestUtil.getNewReservationId());
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("Duration of the requested reservation:"));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testUpdateReservationNormal() {
        ReservationUpdateRequest request = createSimpleReservationUpdateRequest(1, 1, 1, 5, 3);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
        } catch (YarnException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(plan);
    }

    @Test
    public void testUpdateReservationNoID() {
        ReservationUpdateRequest request = new ReservationUpdateRequestPBImpl();
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("Missing reservation id. Please try again by specifying a reservation id."));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testUpdateReservationDoesnotExist() {
        ReservationUpdateRequest request = createSimpleReservationUpdateRequest(1, 1, 1, 5, 4);
        ReservationId rId = request.getReservationId();
        Mockito.when(rSystem.getQueueForReservation(rId)).thenReturn(null);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.equals(MessageFormat.format("The specified reservation with ID: {0} is unknown. Please try again with a valid reservation.", rId)));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testUpdateReservationInvalidPlan() {
        ReservationUpdateRequest request = createSimpleReservationUpdateRequest(1, 1, 1, 5, 4);
        Mockito.when(rSystem.getPlan(TestReservationInputValidator.PLAN_NAME)).thenReturn(null);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.endsWith(" is not associated with any valid plan. Please try again with a valid reservation."));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testUpdateReservationNoDefinition() {
        ReservationUpdateRequest request = new ReservationUpdateRequestPBImpl();
        request.setReservationId(ReservationSystemTestUtil.getNewReservationId());
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("Missing reservation definition. Please try again by specifying a reservation definition."));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testUpdateReservationInvalidDeadline() {
        ReservationUpdateRequest request = createSimpleReservationUpdateRequest(1, 1, 1, 0, 3);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("The specified deadline: 0 is the past"));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testUpdateReservationInvalidRR() {
        ReservationUpdateRequest request = createSimpleReservationUpdateRequest(0, 0, 1, 5, 3);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("No resources have been specified to reserve"));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testUpdateReservationEmptyRR() {
        ReservationUpdateRequest request = createSimpleReservationUpdateRequest(1, 0, 1, 5, 3);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("No resources have been specified to reserve"));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testUpdateReservationInvalidDuration() {
        ReservationUpdateRequest request = createSimpleReservationUpdateRequest(1, 1, 1, 3, 4);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.contains("must  be greater or equal to the minimum resource duration"));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testUpdateReservationExceedsGangSize() {
        ReservationUpdateRequest request = createSimpleReservationUpdateRequest(1, 1, 1, 5, 4);
        Resource resource = Resource.newInstance(512, 1);
        Mockito.when(plan.getTotalCapacity()).thenReturn(resource);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("The size of the largest gang in the reservation definition"));
            Assert.assertTrue(message.contains("exceed the capacity available "));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testUpdateReservationValidRecurrenceExpression() {
        ReservationUpdateRequest request = createSimpleReservationUpdateRequest(1, 1, 1, 5, 3, "600000");
        plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
        } catch (YarnException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(plan);
    }

    @Test
    public void testUpdateReservationNegativeRecurrenceExpression() {
        ReservationUpdateRequest request = createSimpleReservationUpdateRequest(1, 1, 1, 5, 3, "-1234");
        plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("Negative Period : "));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testUpdateReservationInvalidRecurrenceExpression() {
        // first check recurrence expression
        ReservationUpdateRequest request = createSimpleReservationUpdateRequest(1, 1, 1, 5, 3, "123abc");
        plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("Invalid period "));
            TestReservationInputValidator.LOG.info(message);
        }
        // now check duration
        request = createSimpleReservationUpdateRequest(1, 1, 1, 50, 3, "10");
        plan = null;
        try {
            plan = rrValidator.validateReservationUpdateRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("Duration of the requested reservation:"));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testDeleteReservationNormal() {
        ReservationDeleteRequest request = new ReservationDeleteRequestPBImpl();
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        request.setReservationId(reservationID);
        ReservationAllocation reservation = Mockito.mock(ReservationAllocation.class);
        Mockito.when(plan.getReservationById(reservationID)).thenReturn(reservation);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationDeleteRequest(rSystem, request);
        } catch (YarnException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(plan);
    }

    @Test
    public void testDeleteReservationNoID() {
        ReservationDeleteRequest request = new ReservationDeleteRequestPBImpl();
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationDeleteRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith("Missing reservation id. Please try again by specifying a reservation id."));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testDeleteReservationDoesnotExist() {
        ReservationDeleteRequest request = new ReservationDeleteRequestPBImpl();
        ReservationId rId = ReservationSystemTestUtil.getNewReservationId();
        request.setReservationId(rId);
        Mockito.when(rSystem.getQueueForReservation(rId)).thenReturn(null);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationDeleteRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.equals(MessageFormat.format("The specified reservation with ID: {0} is unknown. Please try again with a valid reservation.", rId)));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testDeleteReservationInvalidPlan() {
        ReservationDeleteRequest request = new ReservationDeleteRequestPBImpl();
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        request.setReservationId(reservationID);
        Mockito.when(rSystem.getPlan(TestReservationInputValidator.PLAN_NAME)).thenReturn(null);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationDeleteRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.endsWith(" is not associated with any valid plan. Please try again with a valid reservation."));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testListReservationsNormal() {
        ReservationListRequest request = new ReservationListRequestPBImpl();
        request.setQueue(ReservationSystemTestUtil.reservationQ);
        request.setEndTime(1000);
        request.setStartTime(0);
        Mockito.when(rSystem.getPlan(ReservationSystemTestUtil.reservationQ)).thenReturn(this.plan);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationListRequest(rSystem, request);
        } catch (YarnException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(plan);
    }

    @Test
    public void testListReservationsInvalidTimeIntervalDefaults() {
        ReservationListRequest request = new ReservationListRequestPBImpl();
        request.setQueue(ReservationSystemTestUtil.reservationQ);
        // Negative time gets converted to default values for Start Time and End
        // Time which are 0 and Long.MAX_VALUE respectively.
        request.setEndTime((-2));
        request.setStartTime((-1));
        Mockito.when(rSystem.getPlan(ReservationSystemTestUtil.reservationQ)).thenReturn(this.plan);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationListRequest(rSystem, request);
        } catch (YarnException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(plan);
    }

    @Test
    public void testListReservationsInvalidTimeInterval() {
        ReservationListRequest request = new ReservationListRequestPBImpl();
        request.setQueue(ReservationSystemTestUtil.reservationQ);
        request.setEndTime(1000);
        request.setStartTime(2000);
        Mockito.when(rSystem.getPlan(ReservationSystemTestUtil.reservationQ)).thenReturn(this.plan);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationListRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.equals(("The specified end time must be " + "greater than the specified start time.")));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testListReservationsEmptyQueue() {
        ReservationListRequest request = new ReservationListRequestPBImpl();
        request.setQueue("");
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationListRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.equals(("The queue is not specified. Please try again with a valid " + "reservable queue.")));
            TestReservationInputValidator.LOG.info(message);
        }
    }

    @Test
    public void testListReservationsNullPlan() {
        ReservationListRequest request = new ReservationListRequestPBImpl();
        request.setQueue(ReservationSystemTestUtil.reservationQ);
        Mockito.when(rSystem.getPlan(ReservationSystemTestUtil.reservationQ)).thenReturn(null);
        Plan plan = null;
        try {
            plan = rrValidator.validateReservationListRequest(rSystem, request);
            Assert.fail();
        } catch (YarnException e) {
            Assert.assertNull(plan);
            String message = e.getMessage();
            Assert.assertTrue(message.equals(((("The specified queue: " + (ReservationSystemTestUtil.reservationQ)) + " is not managed by reservation system.") + " Please try again with a valid reservable queue.")));
            TestReservationInputValidator.LOG.info(message);
        }
    }
}

