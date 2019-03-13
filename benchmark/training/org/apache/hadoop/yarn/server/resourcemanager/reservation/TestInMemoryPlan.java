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


import java.sql.Timestamp;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.planning.Planner;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.planning.ReservationAgent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.UTCClock;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Testing the class {@link InMemoryPlan}.
 */
@SuppressWarnings("checkstyle:nowhitespaceafter")
public class TestInMemoryPlan {
    private String user = "yarn";

    private String planName = "test-reservation";

    private ResourceCalculator resCalc;

    private Resource minAlloc;

    private Resource maxAlloc;

    private Resource totalCapacity;

    private Clock clock;

    private QueueMetrics queueMetrics;

    private SharingPolicy policy;

    private ReservationAgent agent;

    private Planner replanner;

    private RMContext context;

    private long maxPeriodicity;

    @Test
    public void testAddReservation() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        doAssertions(plan, rAllocation);
        checkAllocation(plan, alloc, start, 0);
    }

    @Test(expected = PlanningException.class)
    public void testOutOfRange() throws PlanningException {
        maxPeriodicity = 100;
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, maxPeriodicity, context, new UTCClock());
        // we expect the plan to complaint as the range 330-150 > 50
        RLESparseResourceAllocation availableBefore = plan.getAvailableResourceOverTime(user, null, 150, 330, 50);
    }

    @Test
    public void testAddPeriodicReservation() throws PlanningException {
        maxPeriodicity = 100;
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, maxPeriodicity, context, new UTCClock());
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{ 10, 50 };
        int start = 10;
        long period = 20;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc, String.valueOf(period));
        // use periodicity of 1hr
        rAllocation.setPeriodicity(period);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        doAssertions(plan, rAllocation);
        checkAllocation(plan, alloc, start, period);
        RLESparseResourceAllocation available = plan.getAvailableResourceOverTime(user, null, 130, 170, 50);
        // the reservation has period 20 starting at 10, and the interaction with
        // the period 50 request means that every 10 we expect a "90GB" point
        Assert.assertEquals(92160, available.getCapacityAtTime(130).getMemorySize());
        Assert.assertEquals(92160, available.getCapacityAtTime(140).getMemorySize());
        Assert.assertEquals(92160, available.getCapacityAtTime(150).getMemorySize());
    }

    @Test
    public void testAddEmptyReservation() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{  };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAddReservationAlreadyExists() {
        // First add a reservation
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        doAssertions(plan, rAllocation);
        checkAllocation(plan, alloc, start, 0);
        // Try to add it again
        try {
            plan.addReservation(rAllocation, false);
            Assert.fail("Add should fail as it already exists");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().endsWith("already exists"));
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        doAssertions(plan, rAllocation);
    }

    @Test
    public void testUpdateReservation() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        // First add a reservation
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        doAssertions(plan, rAllocation);
        RLESparseResourceAllocation userCons = plan.getConsumptionForUserOverTime(user, start, (start + (alloc.length)));
        for (int i = 0; i < (alloc.length); i++) {
            Assert.assertEquals(Resource.newInstance((1024 * (alloc[i])), alloc[i]), plan.getTotalCommittedResources((start + i)));
            Assert.assertEquals(Resource.newInstance((1024 * (alloc[i])), alloc[i]), userCons.getCapacityAtTime((start + i)));
        }
        // Now update it
        start = 110;
        int[] updatedAlloc = new int[]{ 0, 5, 10, 10, 5, 0 };
        rAllocation = createReservationAllocation(reservationID, start, updatedAlloc, true);
        try {
            plan.updateReservation(rAllocation);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        doAssertions(plan, rAllocation);
        userCons = plan.getConsumptionForUserOverTime(user, start, (start + (updatedAlloc.length)));
        for (int i = 0; i < (updatedAlloc.length); i++) {
            Assert.assertEquals(Resource.newInstance((1024 * ((updatedAlloc[i]) + i)), ((updatedAlloc[i]) + i)), plan.getTotalCommittedResources((start + i)));
            Assert.assertEquals(Resource.newInstance((1024 * ((updatedAlloc[i]) + i)), ((updatedAlloc[i]) + i)), userCons.getCapacityAtTime((start + i)));
        }
    }

    @Test
    public void testUpdatePeriodicReservation() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        // First add a reservation
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{ 10, 20 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        // use periodicity of 1hr
        long period = 3600000;
        rAllocation.getReservationDefinition().setRecurrenceExpression(String.valueOf(period));
        rAllocation.setPeriodicity(period);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        System.out.println(plan.toString());
        doAssertions(plan, rAllocation);
        checkAllocation(plan, alloc, start, period);
        // Now update it
        start = 110;
        int[] updatedAlloc = new int[]{ 30, 40, 50 };
        rAllocation = createReservationAllocation(reservationID, start, updatedAlloc);
        rAllocation.getReservationDefinition().setRecurrenceExpression(String.valueOf(period));
        rAllocation.setPeriodicity(period);
        try {
            plan.updateReservation(rAllocation);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        doAssertions(plan, rAllocation);
        checkAllocation(plan, updatedAlloc, start, period);
    }

    @Test
    public void testUpdateNonExistingReservation() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        // Try to update a reservation without adding
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.updateReservation(rAllocation);
            Assert.fail("Update should fail as it does not exist in the plan");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().endsWith("does not exist in the plan"));
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNull(plan.getReservationById(reservationID));
    }

    @Test
    public void testDeleteReservation() {
        // First add a reservation
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc, true);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        doAssertions(plan, rAllocation);
        RLESparseResourceAllocation userCons = plan.getConsumptionForUserOverTime(user, start, (start + (alloc.length)));
        for (int i = 0; i < (alloc.length); i++) {
            Assert.assertEquals(Resource.newInstance((1024 * ((alloc[i]) + i)), ((alloc[i]) + i)), plan.getTotalCommittedResources((start + i)));
            Assert.assertEquals(Resource.newInstance((1024 * ((alloc[i]) + i)), ((alloc[i]) + i)), userCons.getCapacityAtTime((start + i)));
        }
        // Now delete it
        try {
            plan.deleteReservation(reservationID);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNull(plan.getReservationById(reservationID));
        userCons = plan.getConsumptionForUserOverTime(user, start, (start + (alloc.length)));
        for (int i = 0; i < (alloc.length); i++) {
            Assert.assertEquals(Resource.newInstance(0, 0), plan.getTotalCommittedResources((start + i)));
            Assert.assertEquals(Resource.newInstance(0, 0), userCons.getCapacityAtTime((start + i)));
        }
    }

    @Test
    public void testDeletePeriodicReservation() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        // First add a reservation
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{ 10, 20 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        // use periodicity of 1hr
        long period = 3600000;
        rAllocation.getReservationDefinition().setRecurrenceExpression(String.valueOf(period));
        rAllocation.setPeriodicity(period);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        System.out.println(plan.toString());
        doAssertions(plan, rAllocation);
        checkAllocation(plan, alloc, start, period);
        // Now delete it
        try {
            plan.deleteReservation(reservationID);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNull(plan.getReservationById(reservationID));
        System.out.print(plan);
        checkAllocation(plan, new int[]{ 0, 0 }, start, period);
    }

    @Test
    public void testDeleteNonExistingReservation() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        // Try to delete a reservation without adding
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.deleteReservation(reservationID);
            Assert.fail("Delete should fail as it does not exist in the plan");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().endsWith("does not exist in the plan"));
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNull(plan.getReservationById(reservationID));
    }

    @Test
    public void testArchiveCompletedReservations() {
        SharingPolicy sharingPolicy = Mockito.mock(SharingPolicy.class);
        Plan plan = new InMemoryPlan(queueMetrics, sharingPolicy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID1 = ReservationSystemTestUtil.getNewReservationId();
        // First add a reservation
        int[] alloc1 = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID1, start, alloc1);
        Assert.assertNull(plan.getReservationById(reservationID1));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        doAssertions(plan, rAllocation);
        checkAllocation(plan, alloc1, start, 0);
        // Now add another one
        ReservationId reservationID2 = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc2 = new int[]{ 0, 5, 10, 5, 0 };
        rAllocation = createReservationAllocation(reservationID2, start, alloc2, true);
        Assert.assertNull(plan.getReservationById(reservationID2));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(plan.getReservationById(reservationID2));
        RLESparseResourceAllocation userCons = plan.getConsumptionForUserOverTime(user, start, (start + (alloc2.length)));
        for (int i = 0; i < (alloc2.length); i++) {
            Assert.assertEquals(Resource.newInstance((1024 * (((alloc1[i]) + (alloc2[i])) + i)), (((alloc1[i]) + (alloc2[i])) + i)), plan.getTotalCommittedResources((start + i)));
            Assert.assertEquals(Resource.newInstance((1024 * (((alloc1[i]) + (alloc2[i])) + i)), (((alloc1[i]) + (alloc2[i])) + i)), userCons.getCapacityAtTime((start + i)));
        }
        // Now archive completed reservations
        Mockito.when(clock.getTime()).thenReturn(106L);
        Mockito.when(sharingPolicy.getValidWindow()).thenReturn(1L);
        try {
            // will only remove 2nd reservation as only that has fallen out of the
            // archival window
            plan.archiveCompletedReservations(clock.getTime());
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(plan.getReservationById(reservationID1));
        Assert.assertNull(plan.getReservationById(reservationID2));
        checkAllocation(plan, alloc1, start, 0);
        Mockito.when(clock.getTime()).thenReturn(107L);
        try {
            // will remove 1st reservation also as it has fallen out of the archival
            // window
            plan.archiveCompletedReservations(clock.getTime());
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        userCons = plan.getConsumptionForUserOverTime(user, start, (start + (alloc1.length)));
        Assert.assertNull(plan.getReservationById(reservationID1));
        for (int i = 0; i < (alloc1.length); i++) {
            Assert.assertEquals(Resource.newInstance(0, 0), plan.getTotalCommittedResources((start + i)));
            Assert.assertEquals(Resource.newInstance(0, 0), userCons.getCapacityAtTime((start + i)));
        }
    }

    @Test
    public void testGetReservationsById() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        // Verify that get by reservation id works.
        Set<ReservationAllocation> rAllocations = plan.getReservations(reservationID, null, "");
        Assert.assertTrue(((rAllocations.size()) == 1));
        Assert.assertTrue(((rAllocation.compareTo(((ReservationAllocation) (rAllocations.toArray()[0])))) == 0));
        // Verify that get by reservation id works even when time range
        // and user is invalid.
        ReservationInterval interval = new ReservationInterval(0, 0);
        rAllocations = plan.getReservations(reservationID, interval, "invalid");
        Assert.assertTrue(((rAllocations.size()) == 1));
        Assert.assertTrue(((rAllocation.compareTo(((ReservationAllocation) (rAllocations.toArray()[0])))) == 0));
    }

    @Test
    public void testGetReservationsByInvalidId() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        // If reservationId is null, then nothing is returned.
        ReservationId invalidReservationID = ReservationSystemTestUtil.getNewReservationId();
        Set<ReservationAllocation> rAllocations = plan.getReservations(invalidReservationID, null, "");
        Assert.assertTrue(((rAllocations.size()) == 0));
    }

    @Test
    public void testGetReservationsByTimeInterval() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        // Verify that get by time interval works if the selection interval
        // completely overlaps with the allocation.
        ReservationInterval interval = new ReservationInterval(rAllocation.getStartTime(), rAllocation.getEndTime());
        Set<ReservationAllocation> rAllocations = plan.getReservations(null, interval, "");
        Assert.assertTrue(((rAllocations.size()) == 1));
        Assert.assertTrue(((rAllocation.compareTo(((ReservationAllocation) (rAllocations.toArray()[0])))) == 0));
        // Verify that get by time interval works if the selection interval
        // falls within the allocation
        long duration = (rAllocation.getEndTime()) - (rAllocation.getStartTime());
        interval = new ReservationInterval(((rAllocation.getStartTime()) + (duration * ((long) (0.3)))), ((rAllocation.getEndTime()) - (duration * ((long) (0.3)))));
        rAllocations = plan.getReservations(null, interval, "");
        Assert.assertTrue(((rAllocations.size()) == 1));
        Assert.assertTrue(((rAllocation.compareTo(((ReservationAllocation) (rAllocations.toArray()[0])))) == 0));
        // Verify that get by time interval selects 1 allocation if the end
        // time of the selection interval falls right at the start of the
        // allocation.
        interval = new ReservationInterval(0, rAllocation.getStartTime());
        rAllocations = plan.getReservations(null, interval, "");
        Assert.assertTrue(((rAllocations.size()) == 1));
        Assert.assertTrue(((rAllocation.compareTo(((ReservationAllocation) (rAllocations.toArray()[0])))) == 0));
        // Verify that get by time interval selects no reservations if the start
        // time of the selection interval falls right at the end of the allocation.
        interval = new ReservationInterval(rAllocation.getEndTime(), Long.MAX_VALUE);
        rAllocations = plan.getReservations(null, interval, "");
        Assert.assertTrue(((rAllocations.size()) == 0));
        // Verify that get by time interval selects no reservations if the
        // selection interval and allocation interval do not overlap.
        interval = new ReservationInterval(0, ((rAllocation.getStartTime()) / 2));
        rAllocations = plan.getReservations(null, interval, "");
        Assert.assertTrue(((rAllocations.size()) == 0));
    }

    @Test
    public void testGetReservationsAtTime() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        Set<ReservationAllocation> rAllocations = plan.getReservationsAtTime(rAllocation.getStartTime());
        Assert.assertTrue(((rAllocations.size()) == 1));
        Assert.assertTrue(((rAllocation.compareTo(((ReservationAllocation) (rAllocations.toArray()[0])))) == 0));
    }

    @Test
    public void testGetReservationSearchIntervalBeforeReservationStart() {
        // Reservation duration is 10 minutes
        long reservationStart = Timestamp.valueOf("2050-12-03 10:37:37").getTime();
        long reservationEnd = Timestamp.valueOf("2050-12-03 10:47:37").getTime();
        long searchStart = Timestamp.valueOf("2050-12-03 10:10:37").getTime();
        long searchEnd = Timestamp.valueOf("2050-12-03 10:20:37").getTime();
        // 10 minute period in milliseconds.
        long period = (10 * 60) * 1000;
        // Negative test because even though the reservation would be encompassed
        // if it was interpolated, it should not be picked up. Also test only one
        // cycle because if we test more cycles, some of them will pass.
        testNegativeGetRecurringReservationsHelper(reservationStart, reservationEnd, searchStart, searchEnd, 1, period, 10);
    }

    @Test
    public void testGetReservationSearchIntervalGreaterThanPeriod() {
        // Reservation duration is 10 minutes
        long reservationStart = Timestamp.valueOf("2050-12-03 10:37:37").getTime();
        long reservationEnd = Timestamp.valueOf("2050-12-03 10:47:37").getTime();
        // 1 Hour search interval will for sure encompass the recurring
        // reservation with 20 minute recurrence.
        long searchStart = Timestamp.valueOf("2050-12-03 10:57:37").getTime();
        long searchEnd = Timestamp.valueOf("2050-12-03 11:57:37").getTime();
        // 20 minute period in milliseconds.
        long period = (20 * 60) * 1000;
        testPositiveGetRecurringReservationsHelper(reservationStart, reservationEnd, searchStart, searchEnd, 100, period, 10);
    }

    @Test
    public void testGetReservationReservationFitWithinSearchInterval() {
        // Reservation duration is 10 minutes
        long reservationStart = Timestamp.valueOf("2050-12-03 10:37:37").getTime();
        long reservationEnd = Timestamp.valueOf("2050-12-03 10:47:37").getTime();
        // Search interval fits the entire reservation but is smaller than the
        // period.
        long searchStart = Timestamp.valueOf("2050-12-03 10:36:37").getTime();
        long searchEnd = Timestamp.valueOf("2050-12-03 10:48:37").getTime();
        // 20 minute period in milliseconds.
        long period = (20 * 60) * 1000;
        testPositiveGetRecurringReservationsHelper(reservationStart, reservationEnd, searchStart, searchEnd, 100, period, 10);
    }

    @Test
    public void testGetReservationReservationStartTimeOverlap() {
        // Reservation duration is 10 minutes
        long reservationStart = Timestamp.valueOf("2050-12-03 10:37:37").getTime();
        long reservationEnd = Timestamp.valueOf("2050-12-03 10:47:37").getTime();
        // Search interval fits the starting portion of the reservation.
        long searchStart = Timestamp.valueOf("2050-12-03 11:36:37").getTime();
        long searchEnd = Timestamp.valueOf("2050-12-03 11:38:37").getTime();
        // 60 minute period in milliseconds.
        long period = (60 * 60) * 1000;
        testPositiveGetRecurringReservationsHelper(reservationStart, reservationEnd, searchStart, searchEnd, 100, period, 10);
    }

    @Test
    public void testGetReservationReservationEndTimeOverlap() {
        // Reservation duration is 10 minutes
        long reservationStart = Timestamp.valueOf("2050-12-03 10:37:37").getTime();
        long reservationEnd = Timestamp.valueOf("2050-12-03 10:47:37").getTime();
        // Search interval fits the ending portion of the reservation.
        long searchStart = Timestamp.valueOf("2050-12-03 11:46:37").getTime();
        long searchEnd = Timestamp.valueOf("2050-12-03 11:48:37").getTime();
        // 60 minute period in milliseconds.
        long period = (60 * 60) * 1000;
        testPositiveGetRecurringReservationsHelper(reservationStart, reservationEnd, searchStart, searchEnd, 100, period, 10);
    }

    @Test
    public void testGetReservationSearchIntervalFitsInReservation() {
        // Reservation duration is 10 minutes
        long reservationStart = Timestamp.valueOf("2050-12-03 10:37:37").getTime();
        long reservationEnd = Timestamp.valueOf("2050-12-03 10:47:37").getTime();
        // Search interval fits the within the reservation.
        long searchStart = Timestamp.valueOf("2050-12-03 10:40:37").getTime();
        long searchEnd = Timestamp.valueOf("2050-12-03 10:43:37").getTime();
        // 60 minute period in milliseconds.
        long period = (60 * 60) * 1000;
        testPositiveGetRecurringReservationsHelper(reservationStart, reservationEnd, searchStart, searchEnd, 100, period, 10);
    }

    @Test
    public void testNegativeGetReservationSearchIntervalCloseToEndTime() {
        // Reservation duration is 10 minutes
        long reservationStart = Timestamp.valueOf("2050-12-03 10:37:37").getTime();
        long reservationEnd = Timestamp.valueOf("2050-12-03 10:47:37").getTime();
        // Reservation does not fit within search interval, but is close to the end
        // time.
        long searchStart = Timestamp.valueOf("2050-12-03 10:48:37").getTime();
        long searchEnd = Timestamp.valueOf("2050-12-03 10:50:37").getTime();
        // 60 minute period in milliseconds.
        long period = (60 * 60) * 1000;
        testNegativeGetRecurringReservationsHelper(reservationStart, reservationEnd, searchStart, searchEnd, 100, period, 10);
    }

    @Test
    public void testNegativeGetReservationSearchIntervalCloseToStartTime() {
        // Reservation duration is 10 minutes
        long reservationStart = Timestamp.valueOf("2050-12-03 10:37:37").getTime();
        long reservationEnd = Timestamp.valueOf("2050-12-03 10:47:37").getTime();
        // Search interval does not fit within the reservation but is close to
        // the start time.
        long searchStart = Timestamp.valueOf("2050-12-03 11:30:37").getTime();
        long searchEnd = Timestamp.valueOf("2050-12-03 11:35:37").getTime();
        // 60 minute period in milliseconds.
        long period = (60 * 60) * 1000;
        testNegativeGetRecurringReservationsHelper(reservationStart, reservationEnd, searchStart, searchEnd, 100, period, 10);
    }

    @Test
    public void testReservationIntervalGreaterThanPeriodInOrderWhenShifted() {
        // Reservation duration is 10 minutes
        long reservationStart = Timestamp.valueOf("2050-12-03 10:37:37").getTime();
        long reservationEnd = Timestamp.valueOf("2050-12-03 10:47:37").getTime();
        // Search interval is more than 2 hours, but after shifting, and turning
        // it into periodic values, we expect 13 minutes and 18 minutes
        // respectively for the search start and search end. After shifting and
        // turning into periodic, the reservation interval will be 0 and 10
        // minutes respectively for the search start and search end. At first
        // sight, it would appear that the reservation does not fall within the
        // search interval, when it does in reality.
        long searchStart = Timestamp.valueOf("2050-12-03 9:50:37").getTime();
        long searchEnd = Timestamp.valueOf("2050-12-03 11:55:37").getTime();
        // 60 minute period in milliseconds.
        long period = (60 * 60) * 1000;
        testPositiveGetRecurringReservationsHelper(reservationStart, reservationEnd, searchStart, searchEnd, 100, period, 10);
    }

    @Test
    public void testEnsureReservationEndNotNegativeWhenShifted() {
        // Reservation duration is 10 minutes
        long reservationStart = Timestamp.valueOf("2050-12-03 9:57:37").getTime();
        long reservationEnd = Timestamp.valueOf("2050-12-03 10:07:37").getTime();
        // If the reservation end is made periodic, and then shifted, then it can
        // end up negative. This test guards against this scenario.
        long searchStart = Timestamp.valueOf("2050-12-03 9:58:37").getTime();
        long searchEnd = Timestamp.valueOf("2050-12-03 10:08:37").getTime();
        // 60 minute period in milliseconds.
        long period = (60 * 60) * 1000;
        testPositiveGetRecurringReservationsHelper(reservationStart, reservationEnd, searchStart, searchEnd, 100, period, 10);
    }

    @Test
    public void testGetReservationsWithNoInput() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationAllocation rAllocation = createReservationAllocation(reservationID, start, alloc);
        Assert.assertNull(plan.getReservationById(reservationID));
        try {
            plan.addReservation(rAllocation, false);
        } catch (PlanningException e) {
            Assert.fail(e.getMessage());
        }
        // Verify that getReservations defaults to getting all reservations if no
        // reservationID, time interval, and user is provided,
        Set<ReservationAllocation> rAllocations = plan.getReservations(null, null, "");
        Assert.assertTrue(((rAllocations.size()) == 1));
        Assert.assertTrue(((rAllocation.compareTo(((ReservationAllocation) (rAllocations.toArray()[0])))) == 0));
    }

    @Test
    public void testGetReservationsWithNoReservation() {
        Plan plan = new InMemoryPlan(queueMetrics, policy, agent, totalCapacity, 1L, resCalc, minAlloc, maxAlloc, planName, replanner, true, context);
        // Verify that get reservation returns no entries if no queries are made.
        ReservationInterval interval = new ReservationInterval(0, Long.MAX_VALUE);
        Set<ReservationAllocation> rAllocations = plan.getReservations(null, interval, "");
        Assert.assertTrue(((rAllocations.size()) == 0));
    }
}

