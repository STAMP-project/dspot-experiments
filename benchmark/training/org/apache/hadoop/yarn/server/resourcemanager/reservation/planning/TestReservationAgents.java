/**
 * ***************************************************************************
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
 * ***************************************************************************
 */
package org.apache.hadoop.yarn.server.resourcemanager.reservation.planning;


import java.util.Random;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.Plan;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystemTestUtil;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * General purpose ReservationAgent tester.
 */
@RunWith(Parameterized.class)
@SuppressWarnings("VisibilityModifier")
public class TestReservationAgents {
    @Parameterized.Parameter(0)
    public Class agentClass;

    @Parameterized.Parameter(1)
    public boolean allocateLeft;

    @Parameterized.Parameter(2)
    public String recurrenceExpression;

    @Parameterized.Parameter(3)
    public int numOfNodes;

    private long step;

    private Random rand = new Random(2);

    private ReservationAgent agent;

    private Plan plan;

    private ResourceCalculator resCalc = new DefaultResourceCalculator();

    private Resource minAlloc = Resource.newInstance(1024, 1);

    private Resource maxAlloc = Resource.newInstance((32 * 1023), 32);

    private long timeHorizon = ((2 * 24) * 3600) * 1000;// 2 days


    private static final Logger LOG = LoggerFactory.getLogger(TestReservationAgents.class);

    @Test
    public void test() throws Exception {
        long period = Long.parseLong(recurrenceExpression);
        for (int i = 0; i < 1000; i++) {
            ReservationDefinition rr = createRandomRequest(i);
            if (rr != null) {
                ReservationId reservationID = ReservationSystemTestUtil.getNewReservationId();
                try {
                    agent.createReservation(reservationID, "u1", plan, rr);
                } catch (PlanningException p) {
                    // happens
                }
            }
        }
    }
}

