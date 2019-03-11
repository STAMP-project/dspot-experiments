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
package org.apache.hadoop.yarn.server.resourcemanager.reservation.planning;


import ReservationRequestInterpreter.R_ALL;
import ReservationRequestInterpreter.R_ANY;
import ReservationRequestInterpreter.R_ORDER;
import ReservationRequestInterpreter.R_ORDER_NO_GAP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.jcip.annotations.NotThreadSafe;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.InMemoryPlan;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.RLESparseResourceAllocation;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationAllocation;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationInterval;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystemTestUtil;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.planning.StageAllocatorLowCostAligned.DurationInterval;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests the {@code AlignedPlannerWithGreedy} agent.
 */
@RunWith(Parameterized.class)
@NotThreadSafe
@SuppressWarnings("VisibilityModifier")
public class TestAlignedPlanner {
    @Parameterized.Parameter(0)
    public String recurrenceExpression;

    static final String NONPERIODIC = "0";

    static final String THREEHOURPERIOD = "10800000";

    static final String ONEDAYPERIOD = "86400000";

    private static final Logger LOG = LoggerFactory.getLogger(TestAlignedPlanner.class);

    private ReservationAgent agentRight;

    private ReservationAgent agentLeft;

    private InMemoryPlan plan;

    private final Resource minAlloc = Resource.newInstance(1024, 1);

    private final ResourceCalculator res = new DefaultResourceCalculator();

    private final Resource maxAlloc = Resource.newInstance((1024 * 8), 8);

    private final Random rand = new Random();

    private Resource clusterCapacity;

    private long step;

    @Test
    public void testSingleReservationAccept() throws PlanningException {
        // Prepare basic plan
        int numJobsInScenario = initializeScenario1();
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((5 * (step)), (20 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(2048, 2), 10, 5, (10 * (step))) }, R_ORDER, "u1");
        // Add reservation
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        agentRight.createReservation(reservationID, "u1", plan, rr1);
        // CHECK: allocation was accepted
        Assert.assertTrue("Agent-based allocation failed", (reservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == (numJobsInScenario + 1)));
        // Get reservation
        ReservationAllocation alloc1 = plan.getReservationById(reservationID);
        // Verify allocation
        Assert.assertTrue(alloc1.toString(), check(alloc1, (10 * (step)), (20 * (step)), 10, 2048, 2));
        System.out.println("--------AFTER AGENT----------");
        System.out.println(plan.toString());
    }

    @Test
    public void testOrderNoGapImpossible() throws PlanningException {
        // Prepare basic plan
        int numJobsInScenario = initializeScenario2();
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (15 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, step)// Duration
        , // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, step) }, R_ORDER_NO_GAP, "u1");
        // Add reservation
        try {
            ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
            agentRight.createReservation(reservationID, "u1", plan, rr1);
            Assert.fail();
        } catch (PlanningException e) {
            // Expected failure
        }
        // CHECK: allocation was not accepted
        Assert.assertTrue("Agent-based allocation should have failed", ((plan.getAllReservations().size()) == numJobsInScenario));
    }

    @Test
    public void testOrderNoGapImpossible2() throws PlanningException {
        // Prepare basic plan
        int numJobsInScenario = initializeScenario2();
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (13 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, step)// Duration
        , // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 10, 10, step) }, R_ORDER_NO_GAP, "u1");
        // Add reservation
        try {
            ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
            agentRight.createReservation(reservationID, "u1", plan, rr1);
            Assert.fail();
        } catch (PlanningException e) {
            // Expected failure
        }
        // CHECK: allocation was not accepted
        Assert.assertTrue("Agent-based allocation should have failed", ((plan.getAllReservations().size()) == numJobsInScenario));
    }

    @Test
    public void testOrderImpossible() throws PlanningException {
        // Prepare basic plan
        int numJobsInScenario = initializeScenario2();
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (15 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, (2 * (step)))// Duration
        , // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, step) }, R_ORDER, "u1");
        // Add reservation
        try {
            ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
            agentRight.createReservation(reservationID, "u1", plan, rr1);
            Assert.fail();
        } catch (PlanningException e) {
            // Expected failure
        }
        // CHECK: allocation was not accepted
        Assert.assertTrue("Agent-based allocation should have failed", ((plan.getAllReservations().size()) == numJobsInScenario));
    }

    @Test
    public void testAnyImpossible() throws PlanningException {
        // Prepare basic plan
        int numJobsInScenario = initializeScenario2();
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (15 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, (3 * (step)))// Duration
        , // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, (2 * (step))) }, R_ANY, "u1");
        // Add reservation
        try {
            ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
            agentRight.createReservation(reservationID, "u1", plan, rr1);
            Assert.fail();
        } catch (PlanningException e) {
            // Expected failure
        }
        // CHECK: allocation was not accepted
        Assert.assertTrue("Agent-based allocation should have failed", ((plan.getAllReservations().size()) == numJobsInScenario));
    }

    @Test
    public void testAnyAccept() throws PlanningException {
        // Prepare basic plan
        int numJobsInScenario = initializeScenario2();
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (15 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, step)// Duration
        , // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, (2 * (step))) }, R_ANY, "u1");
        // Add reservation
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        agentRight.createReservation(reservationID, "u1", plan, rr1);
        // CHECK: allocation was accepted
        Assert.assertTrue("Agent-based allocation failed", (reservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == (numJobsInScenario + 1)));
        // Get reservation
        ReservationAllocation alloc1 = plan.getReservationById(reservationID);
        // Verify allocation
        Assert.assertTrue(alloc1.toString(), check(alloc1, (14 * (step)), (15 * (step)), 20, 1024, 1));
    }

    @Test
    public void testAllAccept() throws PlanningException {
        // Prepare basic plan
        int numJobsInScenario = initializeScenario2();
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (15 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, step)// Duration
        , // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, step) }, R_ALL, "u1");
        // Add reservation
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        agentRight.createReservation(reservationID, "u1", plan, rr1);
        // CHECK: allocation was accepted
        Assert.assertTrue("Agent-based allocation failed", (reservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == (numJobsInScenario + 1)));
        // Get reservation
        ReservationAllocation alloc1 = plan.getReservationById(reservationID);
        // Verify allocation
        Assert.assertTrue(alloc1.toString(), check(alloc1, (10 * (step)), (11 * (step)), 20, 1024, 1));
        Assert.assertTrue(alloc1.toString(), check(alloc1, (14 * (step)), (15 * (step)), 20, 1024, 1));
    }

    @Test
    public void testAllImpossible() throws PlanningException {
        // Prepare basic plan
        int numJobsInScenario = initializeScenario2();
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (15 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, step)// Duration
        , // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, (2 * (step))) }, R_ALL, "u1");
        // Add reservation
        try {
            ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
            agentRight.createReservation(reservationID, "u1", plan, rr1);
            Assert.fail();
        } catch (PlanningException e) {
            // Expected failure
        }
        // CHECK: allocation was not accepted
        Assert.assertTrue("Agent-based allocation should have failed", ((plan.getAllReservations().size()) == numJobsInScenario));
    }

    @Test
    public void testUpdate() throws PlanningException {
        // Create flexible reservation
        ReservationDefinition rrFlex = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (14 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 100, 1, (2 * (step))) }, R_ALL, "u1");
        // Create blocking reservation
        ReservationDefinition rrBlock = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (11 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 100, 100, step) }, R_ALL, "u1");
        // Create reservation IDs
        ReservationId flexReservationID = ReservationSystemTestUtil.getNewReservationId();
        ReservationId blockReservationID = ReservationSystemTestUtil.getNewReservationId();
        // Add block, add flex, remove block, update flex
        agentRight.createReservation(blockReservationID, "uBlock", plan, rrBlock);
        agentRight.createReservation(flexReservationID, "uFlex", plan, rrFlex);
        agentRight.deleteReservation(blockReservationID, "uBlock", plan);
        agentRight.updateReservation(flexReservationID, "uFlex", plan, rrFlex);
        // CHECK: allocation was accepted
        Assert.assertTrue("Agent-based allocation failed", (flexReservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == 1));
        // Get reservation
        ReservationAllocation alloc1 = plan.getReservationById(flexReservationID);
        // Verify allocation
        Assert.assertTrue(alloc1.toString(), check(alloc1, (10 * (step)), (14 * (step)), 50, 1024, 1));
    }

    @Test
    public void testImpossibleDuration() throws PlanningException {
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (15 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, (10 * (step))) }, R_ALL, "u1");
        // Add reservation
        try {
            ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
            agentRight.createReservation(reservationID, "u1", plan, rr1);
            Assert.fail();
        } catch (PlanningException e) {
            // Expected failure
        }
        // CHECK: allocation was not accepted
        Assert.assertTrue("Agent-based allocation should have failed", ((plan.getAllReservations().size()) == 0));
    }

    @Test
    public void testLoadedDurationIntervals() throws PlanningException {
        int numJobsInScenario = initializeScenario3();
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (13 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 80, 10, step) }, R_ALL, "u1");
        // Add reservation
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        agentRight.createReservation(reservationID, "u1", plan, rr1);
        // CHECK: allocation was accepted
        Assert.assertTrue("Agent-based allocation failed", (reservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == (numJobsInScenario + 1)));
        // Get reservation
        ReservationAllocation alloc1 = plan.getReservationById(reservationID);
        // Verify allocation
        Assert.assertTrue(alloc1.toString(), check(alloc1, (10 * (step)), (11 * (step)), 20, 1024, 1));
        Assert.assertTrue(alloc1.toString(), check(alloc1, (11 * (step)), (12 * (step)), 20, 1024, 1));
        Assert.assertTrue(alloc1.toString(), check(alloc1, (12 * (step)), (13 * (step)), 40, 1024, 1));
    }

    @Test
    public void testCostFunction() throws PlanningException {
        // Create large memory reservation
        ReservationDefinition rr7Mem1Core = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (11 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance((7 * 1024), 1), 1, 1, step) }, R_ALL, "u1");
        // Create reservation
        ReservationDefinition rr6Mem6Cores = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (11 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance((6 * 1024), 6), 1, 1, step) }, R_ALL, "u2");
        // Create reservation
        ReservationDefinition rr = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (12 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 1, 1, step) }, R_ALL, "u3");
        // Create reservation IDs
        ReservationId reservationID1 = ReservationSystemTestUtil.getNewReservationId();
        ReservationId reservationID2 = ReservationSystemTestUtil.getNewReservationId();
        ReservationId reservationID3 = ReservationSystemTestUtil.getNewReservationId();
        // Add all
        agentRight.createReservation(reservationID1, "u1", plan, rr7Mem1Core);
        agentRight.createReservation(reservationID2, "u2", plan, rr6Mem6Cores);
        agentRight.createReservation(reservationID3, "u3", plan, rr);
        // Get reservation
        ReservationAllocation alloc3 = plan.getReservationById(reservationID3);
        Assert.assertTrue(alloc3.toString(), check(alloc3, (10 * (step)), (11 * (step)), 0, 1024, 1));
        Assert.assertTrue(alloc3.toString(), check(alloc3, (11 * (step)), (12 * (step)), 1, 1024, 1));
    }

    @Test
    public void testFromCluster() throws PlanningException {
        // int numJobsInScenario = initializeScenario3();
        List<ReservationDefinition> list = new ArrayList<ReservationDefinition>();
        // Create reservation
        list.add(// Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition(1425716392178L, 1425722262791L, new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 7, 1, 587000) }, R_ALL, "u1"));
        list.add(// Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition(1425716406178L, 1425721255841L, new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 6, 1, 485000) }, R_ALL, "u2"));
        list.add(// Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition(1425716399178L, 1425723780138L, new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 6, 1, 738000) }, R_ALL, "u3"));
        list.add(// Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition(1425716437178L, 1425722968378L, new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 7, 1, 653000) }, R_ALL, "u4"));
        list.add(// Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition(1425716406178L, 1425721926090L, new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 6, 1, 552000) }, R_ALL, "u5"));
        list.add(// Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition(1425716379178L, 1425722238553L, new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 6, 1, 586000) }, R_ALL, "u6"));
        list.add(// Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition(1425716407178L, 1425722908317L, new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 7, 1, 650000) }, R_ALL, "u7"));
        list.add(// Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition(1425716452178L, 1425722841562L, new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 6, 1, 639000) }, R_ALL, "u8"));
        list.add(// Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition(1425716384178L, 1425721766129L, new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 7, 1, 538000) }, R_ALL, "u9"));
        list.add(// Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition(1425716437178L, 1425722507886L, new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 5, 1, 607000) }, R_ALL, "u10"));
        // Add reservation
        int i = 1;
        for (ReservationDefinition rr : list) {
            ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
            agentRight.createReservation(reservationID, ("u" + (Integer.toString(i))), plan, rr);
            ++i;
        }
        // CHECK: allocation was accepted
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == (list.size())));
    }

    @Test
    public void testSingleReservationAcceptAllocateLeft() throws PlanningException {
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((10 * (step)), (35 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, (10 * (step)))// Duration
        , // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, (10 * (step))) }, R_ORDER, "u1");
        // Add reservation
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        agentLeft.createReservation(reservationID, "u1", plan, rr1);
        // CHECK: allocation was accepted
        Assert.assertTrue("Agent-based allocation failed", (reservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == 1));
        // Get reservation
        ReservationAllocation alloc1 = plan.getReservationById(reservationID);
        // Verify allocation
        Assert.assertTrue(alloc1.toString(), check(alloc1, (10 * (step)), (30 * (step)), 20, 1024, 1));
    }

    @Test
    public void testLeftSucceedsRightFails() throws PlanningException {
        // Prepare basic plan
        int numJobsInScenario = initializeScenario2();
        // Create reservation
        ReservationDefinition rr1 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((7 * (step)), (16 * (step)), new ReservationRequest[]{ // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, (2 * (step)))// Duration
        , // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 20, 20, (2 * (step))) }, R_ORDER, "u1");
        ReservationDefinition rr2 = // Job arrival time
        // Job deadline
        // Duration
        createReservationDefinition((14 * (step)), (16 * (step)), new ReservationRequest[]{ // Capability
        // Num containers
        // Concurrency
        ReservationRequest.newInstance(Resource.newInstance(1024, 1), 100, 100, (2 * (step))) }, R_ORDER, "u2");
        // Add 1st reservation
        ReservationId reservationID1 = ReservationSystemTestUtil.getNewReservationId();
        agentLeft.createReservation(reservationID1, "u1", plan, rr1);
        // CHECK: allocation was accepted
        Assert.assertTrue("Agent-based allocation failed", (reservationID1 != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == (numJobsInScenario + 1)));
        // Get reservation
        ReservationAllocation alloc1 = plan.getReservationById(reservationID1);
        // Verify allocation
        Assert.assertTrue(alloc1.toString(), check(alloc1, (7 * (step)), (11 * (step)), 20, 1024, 1));
        // Add second reservation
        ReservationId reservationID2 = ReservationSystemTestUtil.getNewReservationId();
        agentLeft.createReservation(reservationID2, "u2", plan, rr2);
        // CHECK: allocation was accepted
        Assert.assertTrue("Agent-based allocation failed", (reservationID2 != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == (numJobsInScenario + 2)));
        // Get reservation
        ReservationAllocation alloc2 = plan.getReservationById(reservationID2);
        // Verify allocation
        Assert.assertTrue(alloc2.toString(), check(alloc2, (14 * (step)), (16 * (step)), 100, 1024, 1));
        agentLeft.deleteReservation(reservationID1, "u1", plan);
        agentLeft.deleteReservation(reservationID2, "u2", plan);
        // Now try to allocate the same jobs with agentRight. The second
        // job should fail
        // Add 1st reservation
        ReservationId reservationID3 = ReservationSystemTestUtil.getNewReservationId();
        agentRight.createReservation(reservationID3, "u1", plan, rr1);
        // CHECK: allocation was accepted
        Assert.assertTrue("Agent-based allocation failed", (reservationID3 != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == (numJobsInScenario + 1)));
        // Add 2nd reservation
        try {
            ReservationId reservationID4 = ReservationSystemTestUtil.getNewReservationId();
            agentRight.createReservation(reservationID4, "u2", plan, rr2);
            Assert.fail();
        } catch (PlanningException e) {
            // Expected failure
        }
    }

    @Test
    public void testValidateOrderNoGap() {
        // 
        // Initialize allocations
        // 
        RLESparseResourceAllocation allocation = new RLESparseResourceAllocation(res);
        allocation.addInterval(new ReservationInterval((10 * (step)), (13 * (step))), Resource.newInstance(1024, 1));
        // curAlloc
        Map<ReservationInterval, Resource> curAlloc = new HashMap<ReservationInterval, Resource>();
        // 
        // Check cases
        // 
        // 1. allocateLeft = false, succeed when there is no gap
        curAlloc.clear();
        curAlloc.put(new ReservationInterval((9 * (step)), (10 * (step))), Resource.newInstance(1024, 1));
        Assert.assertTrue("validateOrderNoFap() should have succeeded", IterativePlanner.validateOrderNoGap(allocation, curAlloc, false));
        // 2. allocateLeft = false, fail when curAlloc has a gap
        curAlloc.put(new ReservationInterval((7 * (step)), (8 * (step))), Resource.newInstance(1024, 1));
        Assert.assertFalse("validateOrderNoGap() failed to identify a gap in curAlloc", IterativePlanner.validateOrderNoGap(allocation, curAlloc, false));
        // 3. allocateLeft = false, fail when there is a gap between curAlloc and
        // allocations
        curAlloc.clear();
        curAlloc.put(new ReservationInterval((8 * (step)), (9 * (step))), Resource.newInstance(1024, 1));
        Assert.assertFalse(("validateOrderNoGap() failed to identify a gap between " + "allocations and curAlloc"), IterativePlanner.validateOrderNoGap(allocation, curAlloc, false));
        // 4. allocateLeft = true, succeed when there is no gap
        curAlloc.clear();
        curAlloc.put(new ReservationInterval((13 * (step)), (14 * (step))), Resource.newInstance(1024, 1));
        Assert.assertTrue("validateOrderNoFap() should have succeeded", IterativePlanner.validateOrderNoGap(allocation, curAlloc, true));
        // 5. allocateLeft = true, fail when there is a gap between curAlloc and
        // allocations
        curAlloc.put(new ReservationInterval((15 * (step)), (16 * (step))), Resource.newInstance(1024, 1));
        Assert.assertFalse("validateOrderNoGap() failed to identify a gap in curAlloc", IterativePlanner.validateOrderNoGap(allocation, curAlloc, true));
        // 6. allocateLeft = true, fail when curAlloc has a gap
        curAlloc.clear();
        curAlloc.put(new ReservationInterval((14 * (step)), (15 * (step))), Resource.newInstance(1024, 1));
        Assert.assertFalse(("validateOrderNoGap() failed to identify a gap between " + "allocations and curAlloc"), IterativePlanner.validateOrderNoGap(allocation, curAlloc, true));
    }

    @Test
    public void testGetDurationInterval() throws PlanningException {
        DurationInterval durationInterval = null;
        // Create netRLERes:
        // - 4GB & 4VC between [10,20) and [30,40)
        // - 8GB & 8VC between [20,30)
        RLESparseResourceAllocation netRLERes = new RLESparseResourceAllocation(res);
        netRLERes.addInterval(new ReservationInterval((10 * (step)), (40 * (step))), Resource.newInstance(4096, 4));
        netRLERes.addInterval(new ReservationInterval((20 * (step)), (30 * (step))), Resource.newInstance(4096, 4));
        // Create planLoads:
        // - 5GB & 5VC between [20,30)
        RLESparseResourceAllocation planLoads = new RLESparseResourceAllocation(res);
        planLoads.addInterval(new ReservationInterval((20 * (step)), (30 * (step))), Resource.newInstance(5120, 5));
        // Create planModifications:
        // - 1GB & 1VC between [25,35)
        RLESparseResourceAllocation planModifications = new RLESparseResourceAllocation(res);
        planModifications.addInterval(new ReservationInterval((25 * (step)), (35 * (step))), Resource.newInstance(1024, 1));
        // Set requested resources
        Resource requestedResources = Resource.newInstance(1024, 1);
        // 1.
        // currLoad: should start at 20*step, end at 30*step with a null value
        // (in getTotalCost(), after the for loop we will have loadPrev == null
        // netAvailableResources: should start exactly at startTime (10*step),
        // end exactly at endTime (30*step) with a null value
        durationInterval = StageAllocatorLowCostAligned.getDurationInterval((10 * (step)), (30 * (step)), planLoads, planModifications, clusterCapacity, netRLERes, res, step, requestedResources);
        Assert.assertEquals(durationInterval.numCanFit(), 4);
        Assert.assertEquals(durationInterval.getTotalCost(), 0.55, 1.0E-5);
        // 2.
        // currLoad: should start at 20*step, end at 31*step with a null value
        // (in getTotalCost, after the for loop we will have loadPrev == null)
        // netAvailableResources: should start exactly at startTime (10*step),
        // end exactly at endTime (31*step) with a null value
        durationInterval = StageAllocatorLowCostAligned.getDurationInterval((10 * (step)), (31 * (step)), planLoads, planModifications, clusterCapacity, netRLERes, res, step, requestedResources);
        System.out.println(durationInterval);
        Assert.assertEquals(durationInterval.numCanFit(), 3);
        Assert.assertEquals(durationInterval.getTotalCost(), 0.56, 1.0E-5);
        // 3.
        // currLoad: should start at 20*step, end at 30*step with a null value
        // (in getTotalCost, after the for loop we will have loadPrev == null)
        // netAvailableResources: should start exactly startTime (15*step),
        // end exactly at endTime (30*step) with a null value
        durationInterval = StageAllocatorLowCostAligned.getDurationInterval((15 * (step)), (30 * (step)), planLoads, planModifications, clusterCapacity, netRLERes, res, step, requestedResources);
        Assert.assertEquals(durationInterval.numCanFit(), 4);
        Assert.assertEquals(durationInterval.getTotalCost(), 0.55, 1.0E-5);
        // 4.
        // currLoad: should start at 20*step, end at 31*step with a null value
        // (in getTotalCost, after the for loop we will have loadPrev == null)
        // netAvailableResources: should start exactly at startTime (15*step),
        // end exactly at endTime (31*step) with a value other than null
        durationInterval = StageAllocatorLowCostAligned.getDurationInterval((15 * (step)), (31 * (step)), planLoads, planModifications, clusterCapacity, netRLERes, res, step, requestedResources);
        System.out.println(durationInterval);
        Assert.assertEquals(durationInterval.numCanFit(), 3);
        Assert.assertEquals(durationInterval.getTotalCost(), 0.56, 1.0E-5);
        // 5.
        // currLoad: should only contain one entry at startTime
        // (22*step), therefore loadPrev != null and we should enter the if
        // condition after the for loop in getTotalCost
        // netAvailableResources: should only contain one entry at startTime
        // (22*step)
        durationInterval = StageAllocatorLowCostAligned.getDurationInterval((22 * (step)), (23 * (step)), planLoads, planModifications, clusterCapacity, netRLERes, res, step, requestedResources);
        System.out.println(durationInterval);
        Assert.assertEquals(durationInterval.numCanFit(), 8);
        Assert.assertEquals(durationInterval.getTotalCost(), 0.05, 1.0E-5);
        // 6.
        // currLoad: should start at 39*step, end at 41*step with a null value
        // (in getTotalCost, after the for loop we will have loadPrev == null)
        // netAvailableResources: should start exactly at startTime (39*step),
        // end exactly at endTime (41*step) with a null value
        durationInterval = StageAllocatorLowCostAligned.getDurationInterval((39 * (step)), (41 * (step)), planLoads, planModifications, clusterCapacity, netRLERes, res, step, requestedResources);
        System.out.println(durationInterval);
        Assert.assertEquals(durationInterval.numCanFit(), 0);
        Assert.assertEquals(durationInterval.getTotalCost(), 0, 1.0E-5);
    }
}

