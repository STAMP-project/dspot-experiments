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
package org.apache.hadoop.yarn.server.resourcemanager.reservation.planning;


import ReservationRequestInterpreter.R_ALL;
import ReservationRequestInterpreter.R_ANY;
import ReservationRequestInterpreter.R_ORDER;
import ReservationRequestInterpreter.R_ORDER_NO_GAP;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import org.apache.hadoop.yarn.api.records.ReservationRequests;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.impl.pb.ReservationDefinitionPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ReservationRequestsPBImpl;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.InMemoryPlan;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationAllocation;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSchedulerConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystemTestUtil;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
@SuppressWarnings("VisibilityModifier")
public class TestGreedyReservationAgent {
    @Parameterized.Parameter(0)
    public boolean allocateLeft;

    @Parameterized.Parameter(1)
    public String recurrenceExpression;

    private static final Logger LOG = LoggerFactory.getLogger(TestGreedyReservationAgent.class);

    ReservationAgent agent;

    InMemoryPlan plan;

    Resource minAlloc = Resource.newInstance(1024, 1);

    ResourceCalculator res = new DefaultResourceCalculator();

    Resource maxAlloc = Resource.newInstance((1024 * 8), 8);

    Random rand = new Random();

    long step;

    @SuppressWarnings("javadoc")
    @Test
    public void testSimple() throws PlanningException {
        prepareBasicPlan();
        // create a request with a single atomic ask
        ReservationDefinition rr = new ReservationDefinitionPBImpl();
        rr.setArrival((5 * (step)));
        rr.setDeadline((20 * (step)));
        rr.setRecurrenceExpression(recurrenceExpression);
        ReservationRequest r = ReservationRequest.newInstance(Resource.newInstance(2048, 2), 10, 5, (10 * (step)));
        ReservationRequests reqs = new ReservationRequestsPBImpl();
        reqs.setReservationResources(Collections.singletonList(r));
        rr.setReservationRequests(reqs);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        agent.createReservation(reservationID, "u1", plan, rr);
        Assert.assertTrue("Agent-based allocation failed", (reservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == 3));
        ReservationAllocation cs = plan.getReservationById(reservationID);
        System.out.println((("--------AFTER SIMPLE ALLOCATION (queue: " + reservationID) + ")----------"));
        System.out.println(plan.toString());
        System.out.println(plan.toCumulativeString());
        if (allocateLeft) {
            for (long i = 5 * (step); i < (15 * (step)); i++) {
                Assert.assertTrue("Agent-based allocation unexpected", Resources.equals(cs.getResourcesAtTime(i), Resource.newInstance((2048 * 10), (2 * 10))));
            }
        } else {
            for (long i = 10 * (step); i < (20 * (step)); i++) {
                Assert.assertTrue("Agent-based allocation unexpected", Resources.equals(cs.getResourcesAtTime(i), Resource.newInstance((2048 * 10), (2 * 10))));
            }
        }
    }

    @SuppressWarnings("javadoc")
    @Test
    public void testSharingPolicyFeedback() throws PlanningException {
        prepareBasicPlan();
        // let's constraint the instantaneous allocation and see the
        // policy kicking in during planning
        float instConstraint = 40;
        float avgConstraint = 40;
        ReservationSchedulerConfiguration conf = ReservationSystemTestUtil.createConf(plan.getQueueName(), 100000, instConstraint, avgConstraint);
        plan.getSharingPolicy().init(plan.getQueueName(), conf);
        // create a request with a single atomic ask
        ReservationDefinition rr = new ReservationDefinitionPBImpl();
        rr.setArrival((5 * (step)));
        rr.setDeadline((100 * (step)));
        rr.setRecurrenceExpression(recurrenceExpression);
        ReservationRequest r = ReservationRequest.newInstance(Resource.newInstance(2048, 2), 20, 20, (10 * (step)));
        ReservationRequests reqs = new ReservationRequestsPBImpl();
        reqs.setReservationResources(Collections.singletonList(r));
        rr.setReservationRequests(reqs);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        agent.createReservation(reservationID, "u3", plan, rr);
        ReservationId reservationID2 = ReservationSystemTestUtil.getNewReservationId();
        agent.createReservation(reservationID2, "u3", plan, rr);
        ReservationDefinition rr3 = new ReservationDefinitionPBImpl();
        rr3.setArrival((5 * (step)));
        rr3.setDeadline((100 * (step)));
        ReservationRequest r3 = ReservationRequest.newInstance(Resource.newInstance(2048, 2), 45, 45, (10 * (step)));
        ReservationRequests reqs3 = new ReservationRequestsPBImpl();
        reqs3.setReservationResources(Collections.singletonList(r3));
        rr3.setReservationRequests(reqs3);
        ReservationId reservationID3 = ReservationSystemTestUtil.getNewReservationId();
        try {
            // RR3 is simply too big to fit
            agent.createReservation(reservationID3, "u3", plan, rr3);
            Assert.fail();
        } catch (PlanningException pe) {
            // expected
        }
        Assert.assertTrue("Agent-based allocation failed", (reservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == 4));
        ReservationAllocation cs = plan.getReservationById(reservationID);
        ReservationAllocation cs2 = plan.getReservationById(reservationID2);
        ReservationAllocation cs3 = plan.getReservationById(reservationID3);
        Assert.assertNotNull(cs);
        Assert.assertNotNull(cs2);
        Assert.assertNull(cs3);
        System.out.println((("--------AFTER SIMPLE ALLOCATION (queue: " + reservationID) + ")----------"));
        System.out.println(plan.toString());
        System.out.println(plan.toCumulativeString());
        if (allocateLeft) {
            for (long i = 5 * (step); i < (15 * (step)); i++) {
                Assert.assertTrue("Agent-based allocation unexpected", Resources.equals(cs.getResourcesAtTime(i), Resource.newInstance((2048 * 20), (2 * 20))));
            }
            for (long i = 15 * (step); i < (25 * (step)); i++) {
                // RR2 is pushed out by the presence of RR
                Assert.assertTrue("Agent-based allocation unexpected", Resources.equals(cs2.getResourcesAtTime(i), Resource.newInstance((2048 * 20), (2 * 20))));
            }
        } else {
            for (long i = 90 * (step); i < (100 * (step)); i++) {
                Assert.assertTrue("Agent-based allocation unexpected", Resources.equals(cs.getResourcesAtTime(i), Resource.newInstance((2048 * 20), (2 * 20))));
            }
            for (long i = 80 * (step); i < (90 * (step)); i++) {
                Assert.assertTrue("Agent-based allocation unexpected", Resources.equals(cs2.getResourcesAtTime(i), Resource.newInstance((2048 * 20), (2 * 20))));
            }
        }
    }

    @Test
    public void testOrder() throws PlanningException {
        prepareBasicPlan();
        // create a completely utilized segment around time 30
        int[] f = new int[]{ 100, 100 };
        ReservationDefinition rDef = ReservationSystemTestUtil.createSimpleReservationDefinition((30 * (step)), ((30 * (step)) + ((f.length) * (step))), ((f.length) * (step)), 1, recurrenceExpression);
        Assert.assertTrue(plan.toString(), plan.addReservation(new org.apache.hadoop.yarn.server.resourcemanager.reservation.InMemoryReservationAllocation(ReservationSystemTestUtil.getNewReservationId(), rDef, "u1", "dedicated", (30 * (step)), ((30 * (step)) + ((f.length) * (step))), ReservationSystemTestUtil.generateAllocation((30 * (step)), step, f), res, minAlloc), false));
        // create a chain of 4 RR, mixing gang and non-gang
        ReservationDefinition rr = new ReservationDefinitionPBImpl();
        rr.setArrival((0 * (step)));
        rr.setDeadline((70 * (step)));
        rr.setRecurrenceExpression(recurrenceExpression);
        ReservationRequests reqs = new ReservationRequestsPBImpl();
        reqs.setInterpreter(R_ORDER);
        ReservationRequest r = ReservationRequest.newInstance(Resource.newInstance(2048, 2), 10, 1, (10 * (step)));
        ReservationRequest r2 = ReservationRequest.newInstance(Resource.newInstance(1024, 1), 10, 10, (20 * (step)));
        List<ReservationRequest> list = new ArrayList<ReservationRequest>();
        list.add(r);
        list.add(r2);
        list.add(r);
        list.add(r2);
        reqs.setReservationResources(list);
        rr.setReservationRequests(reqs);
        // submit to agent
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        agent.createReservation(reservationID, "u1", plan, rr);
        // validate
        Assert.assertTrue("Agent-based allocation failed", (reservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == 4));
        ReservationAllocation cs = plan.getReservationById(reservationID);
        if (allocateLeft) {
            Assert.assertTrue(cs.toString(), check(cs, (0 * (step)), (10 * (step)), 20, 1024, 1));
            Assert.assertTrue(cs.toString(), check(cs, (10 * (step)), (30 * (step)), 10, 1024, 1));
            Assert.assertTrue(cs.toString(), check(cs, (32 * (step)), (42 * (step)), 20, 1024, 1));
            Assert.assertTrue(cs.toString(), check(cs, (42 * (step)), (62 * (step)), 10, 1024, 1));
        } else {
            Assert.assertTrue(cs.toString(), check(cs, (0 * (step)), (10 * (step)), 20, 1024, 1));
            Assert.assertTrue(cs.toString(), check(cs, (10 * (step)), (30 * (step)), 10, 1024, 1));
            Assert.assertTrue(cs.toString(), check(cs, (40 * (step)), (50 * (step)), 20, 1024, 1));
            Assert.assertTrue(cs.toString(), check(cs, (50 * (step)), (70 * (step)), 10, 1024, 1));
        }
        System.out.println((("--------AFTER ORDER ALLOCATION (queue: " + reservationID) + ")----------"));
        System.out.println(plan.toString());
        System.out.println(plan.toCumulativeString());
    }

    @Test
    public void testOrderNoGapImpossible() throws PlanningException {
        prepareBasicPlan();
        // create a completely utilized segment at time 30
        int[] f = new int[]{ 100, 100 };
        ReservationDefinition rDef = ReservationSystemTestUtil.createSimpleReservationDefinition(30, ((30 * (step)) + ((f.length) * (step))), ((f.length) * (step)), 1, recurrenceExpression);
        Assert.assertTrue(plan.toString(), plan.addReservation(new org.apache.hadoop.yarn.server.resourcemanager.reservation.InMemoryReservationAllocation(ReservationSystemTestUtil.getNewReservationId(), rDef, "u1", "dedicated", (30 * (step)), ((30 * (step)) + ((f.length) * (step))), ReservationSystemTestUtil.generateAllocation((30 * (step)), step, f), res, minAlloc), false));
        // create a chain of 4 RR, mixing gang and non-gang
        ReservationDefinition rr = new ReservationDefinitionPBImpl();
        rr.setArrival(0L);
        rr.setDeadline(70L);
        ReservationRequests reqs = new ReservationRequestsPBImpl();
        reqs.setInterpreter(R_ORDER_NO_GAP);
        ReservationRequest r = ReservationRequest.newInstance(Resource.newInstance(2048, 2), 10, 1, 10);
        ReservationRequest r2 = ReservationRequest.newInstance(Resource.newInstance(1024, 1), 10, 10, 20);
        List<ReservationRequest> list = new ArrayList<ReservationRequest>();
        list.add(r);
        list.add(r2);
        list.add(r);
        list.add(r2);
        reqs.setReservationResources(list);
        rr.setReservationRequests(reqs);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        boolean result = false;
        try {
            // submit to agent
            result = agent.createReservation(reservationID, "u1", plan, rr);
            Assert.fail();
        } catch (PlanningException p) {
            // expected
        }
        // validate
        Assert.assertFalse("Agent-based allocation should have failed", result);
        Assert.assertTrue("Agent-based allocation should have failed", ((plan.getAllReservations().size()) == 3));
        System.out.println((("--------AFTER ORDER_NO_GAP IMPOSSIBLE ALLOCATION (queue: " + reservationID) + ")----------"));
        System.out.println(plan.toString());
        System.out.println(plan.toCumulativeString());
    }

    @Test
    public void testOrderNoGap() throws PlanningException {
        prepareBasicPlan();
        // create a chain of 4 RR, mixing gang and non-gang
        ReservationDefinition rr = new ReservationDefinitionPBImpl();
        rr.setArrival((0 * (step)));
        rr.setDeadline((60 * (step)));
        rr.setRecurrenceExpression(recurrenceExpression);
        ReservationRequests reqs = new ReservationRequestsPBImpl();
        reqs.setInterpreter(R_ORDER_NO_GAP);
        ReservationRequest r = ReservationRequest.newInstance(Resource.newInstance(2048, 2), 10, 1, (10 * (step)));
        ReservationRequest r2 = ReservationRequest.newInstance(Resource.newInstance(1024, 1), 10, 10, (20 * (step)));
        List<ReservationRequest> list = new ArrayList<ReservationRequest>();
        list.add(r);
        list.add(r2);
        list.add(r);
        list.add(r2);
        reqs.setReservationResources(list);
        rr.setReservationRequests(reqs);
        rr.setReservationRequests(reqs);
        // submit to agent
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        agent.createReservation(reservationID, "u1", plan, rr);
        System.out.println((("--------AFTER ORDER ALLOCATION (queue: " + reservationID) + ")----------"));
        System.out.println(plan.toString());
        System.out.println(plan.toCumulativeString());
        // validate
        Assert.assertTrue("Agent-based allocation failed", (reservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == 3));
        ReservationAllocation cs = plan.getReservationById(reservationID);
        Assert.assertTrue(cs.toString(), check(cs, (0 * (step)), (10 * (step)), 20, 1024, 1));
        Assert.assertTrue(cs.toString(), check(cs, (10 * (step)), (30 * (step)), 10, 1024, 1));
        Assert.assertTrue(cs.toString(), check(cs, (30 * (step)), (40 * (step)), 20, 1024, 1));
        Assert.assertTrue(cs.toString(), check(cs, (40 * (step)), (60 * (step)), 10, 1024, 1));
    }

    @Test
    public void testSingleSliding() throws PlanningException {
        prepareBasicPlan();
        // create a single request for which we need subsequent (tight) packing.
        ReservationDefinition rr = new ReservationDefinitionPBImpl();
        rr.setArrival((100 * (step)));
        rr.setDeadline((120 * (step)));
        rr.setRecurrenceExpression(recurrenceExpression);
        ReservationRequests reqs = new ReservationRequestsPBImpl();
        reqs.setInterpreter(R_ALL);
        ReservationRequest r = ReservationRequest.newInstance(Resource.newInstance(1024, 1), 200, 10, (10 * (step)));
        List<ReservationRequest> list = new ArrayList<ReservationRequest>();
        list.add(r);
        reqs.setReservationResources(list);
        rr.setReservationRequests(reqs);
        // submit to agent
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        agent.createReservation(reservationID, "u1", plan, rr);
        // validate results, we expect the second one to be accepted
        Assert.assertTrue("Agent-based allocation failed", (reservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == 3));
        ReservationAllocation cs = plan.getReservationById(reservationID);
        Assert.assertTrue(cs.toString(), check(cs, (100 * (step)), (120 * (step)), 100, 1024, 1));
        System.out.println((("--------AFTER packed ALLOCATION (queue: " + reservationID) + ")----------"));
        System.out.println(plan.toString());
        System.out.println(plan.toCumulativeString());
    }

    @Test
    public void testAny() throws PlanningException {
        prepareBasicPlan();
        // create an ANY request, with an impossible step (last in list, first
        // considered),
        // and two satisfiable ones. We expect the second one to be returned.
        ReservationDefinition rr = new ReservationDefinitionPBImpl();
        rr.setArrival((100 * (step)));
        rr.setDeadline((120 * (step)));
        rr.setRecurrenceExpression(recurrenceExpression);
        ReservationRequests reqs = new ReservationRequestsPBImpl();
        reqs.setInterpreter(R_ANY);
        ReservationRequest r = ReservationRequest.newInstance(Resource.newInstance(1024, 1), 5, 5, (10 * (step)));
        ReservationRequest r2 = ReservationRequest.newInstance(Resource.newInstance(2048, 2), 10, 5, (10 * (step)));
        ReservationRequest r3 = ReservationRequest.newInstance(Resource.newInstance(1024, 1), 110, 110, (10 * (step)));
        List<ReservationRequest> list = new ArrayList<ReservationRequest>();
        list.add(r);
        list.add(r2);
        list.add(r3);
        reqs.setReservationResources(list);
        rr.setReservationRequests(reqs);
        // submit to agent
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        boolean res = agent.createReservation(reservationID, "u1", plan, rr);
        // validate results, we expect the second one to be accepted
        Assert.assertTrue("Agent-based allocation failed", res);
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == 3));
        ReservationAllocation cs = plan.getReservationById(reservationID);
        if (allocateLeft) {
            Assert.assertTrue(cs.toString(), check(cs, (100 * (step)), (110 * (step)), 5, 1024, 1));
        } else {
            Assert.assertTrue(cs.toString(), check(cs, (110 * (step)), (120 * (step)), 20, 1024, 1));
        }
        System.out.println((("--------AFTER ANY ALLOCATION (queue: " + reservationID) + ")----------"));
        System.out.println(plan.toString());
        System.out.println(plan.toCumulativeString());
    }

    @Test
    public void testAnyImpossible() throws PlanningException {
        prepareBasicPlan();
        // create an ANY request, with all impossible alternatives
        ReservationDefinition rr = new ReservationDefinitionPBImpl();
        rr.setArrival(100L);
        rr.setDeadline(120L);
        rr.setRecurrenceExpression(recurrenceExpression);
        ReservationRequests reqs = new ReservationRequestsPBImpl();
        reqs.setInterpreter(R_ANY);
        // longer than arrival-deadline
        ReservationRequest r1 = ReservationRequest.newInstance(Resource.newInstance(1024, 1), 35, 5, 30);
        // above max cluster size
        ReservationRequest r2 = ReservationRequest.newInstance(Resource.newInstance(1024, 1), 110, 110, 10);
        List<ReservationRequest> list = new ArrayList<ReservationRequest>();
        list.add(r1);
        list.add(r2);
        reqs.setReservationResources(list);
        rr.setReservationRequests(reqs);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        boolean result = false;
        try {
            // submit to agent
            result = agent.createReservation(reservationID, "u1", plan, rr);
            Assert.fail();
        } catch (PlanningException p) {
            // expected
        }
        // validate results, we expect the second one to be accepted
        Assert.assertFalse("Agent-based allocation should have failed", result);
        Assert.assertTrue("Agent-based allocation should have failed", ((plan.getAllReservations().size()) == 2));
        System.out.println((("--------AFTER ANY IMPOSSIBLE ALLOCATION (queue: " + reservationID) + ")----------"));
        System.out.println(plan.toString());
        System.out.println(plan.toCumulativeString());
    }

    @Test
    public void testAll() throws PlanningException {
        prepareBasicPlan();
        // create an ALL request
        ReservationDefinition rr = new ReservationDefinitionPBImpl();
        rr.setArrival((100 * (step)));
        rr.setDeadline((120 * (step)));
        rr.setRecurrenceExpression(recurrenceExpression);
        ReservationRequests reqs = new ReservationRequestsPBImpl();
        reqs.setInterpreter(R_ALL);
        ReservationRequest r = ReservationRequest.newInstance(Resource.newInstance(1024, 1), 5, 5, (10 * (step)));
        ReservationRequest r2 = ReservationRequest.newInstance(Resource.newInstance(2048, 2), 10, 10, (20 * (step)));
        List<ReservationRequest> list = new ArrayList<ReservationRequest>();
        list.add(r);
        list.add(r2);
        reqs.setReservationResources(list);
        rr.setReservationRequests(reqs);
        // submit to agent
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        agent.createReservation(reservationID, "u1", plan, rr);
        // validate results, we expect the second one to be accepted
        Assert.assertTrue("Agent-based allocation failed", (reservationID != null));
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == 3));
        ReservationAllocation cs = plan.getReservationById(reservationID);
        if (allocateLeft) {
            Assert.assertTrue(cs.toString(), check(cs, (100 * (step)), (110 * (step)), 25, 1024, 1));
            Assert.assertTrue(cs.toString(), check(cs, (110 * (step)), (120 * (step)), 20, 1024, 1));
        } else {
            Assert.assertTrue(cs.toString(), check(cs, (100 * (step)), (110 * (step)), 20, 1024, 1));
            Assert.assertTrue(cs.toString(), check(cs, (110 * (step)), (120 * (step)), 25, 1024, 1));
        }
        System.out.println((("--------AFTER ALL ALLOCATION (queue: " + reservationID) + ")----------"));
        System.out.println(plan.toString());
        System.out.println(plan.toCumulativeString());
    }

    @Test
    public void testAllImpossible() throws PlanningException {
        prepareBasicPlan();
        // create an ALL request, with an impossible combination, it should be
        // rejected, and allocation remain unchanged
        ReservationDefinition rr = new ReservationDefinitionPBImpl();
        rr.setArrival(100L);
        rr.setDeadline(120L);
        rr.setRecurrenceExpression(recurrenceExpression);
        ReservationRequests reqs = new ReservationRequestsPBImpl();
        reqs.setInterpreter(R_ALL);
        ReservationRequest r = ReservationRequest.newInstance(Resource.newInstance(1024, 1), 55, 5, 10);
        ReservationRequest r2 = ReservationRequest.newInstance(Resource.newInstance(2048, 2), 55, 5, 20);
        List<ReservationRequest> list = new ArrayList<ReservationRequest>();
        list.add(r);
        list.add(r2);
        reqs.setReservationResources(list);
        rr.setReservationRequests(reqs);
        ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
        boolean result = false;
        try {
            // submit to agent
            result = agent.createReservation(reservationID, "u1", plan, rr);
            Assert.fail();
        } catch (PlanningException p) {
            // expected
        }
        // validate results, we expect the second one to be accepted
        Assert.assertFalse("Agent-based allocation failed", result);
        Assert.assertTrue("Agent-based allocation failed", ((plan.getAllReservations().size()) == 2));
        System.out.println((("--------AFTER ALL IMPOSSIBLE ALLOCATION (queue: " + reservationID) + ")----------"));
        System.out.println(plan.toString());
        System.out.println(plan.toCumulativeString());
    }
}

