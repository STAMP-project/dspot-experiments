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


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.junit.Assert;
import org.junit.Test;


public class TestInMemoryReservationAllocation {
    private String user = "yarn";

    private String planName = "test-reservation";

    private ResourceCalculator resCalc;

    private Resource minAlloc;

    private Random rand = new Random();

    @Test
    public void testBlocks() {
        ReservationId reservationID = ReservationId.newInstance(rand.nextLong(), rand.nextLong());
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationDefinition rDef = ReservationSystemTestUtil.createSimpleReservationDefinition(start, ((start + (alloc.length)) + 1), alloc.length);
        Map<ReservationInterval, Resource> allocations = generateAllocation(start, alloc, false, false);
        ReservationAllocation rAllocation = new InMemoryReservationAllocation(reservationID, rDef, user, planName, start, ((start + (alloc.length)) + 1), allocations, resCalc, minAlloc);
        doAssertions(rAllocation, reservationID, rDef, allocations, start, alloc);
        Assert.assertFalse(rAllocation.containsGangs());
        for (int i = 0; i < (alloc.length); i++) {
            Assert.assertEquals(Resource.newInstance((1024 * (alloc[i])), alloc[i]), rAllocation.getResourcesAtTime((start + i)));
        }
    }

    @Test
    public void testSteps() {
        ReservationId reservationID = ReservationId.newInstance(rand.nextLong(), rand.nextLong());
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationDefinition rDef = ReservationSystemTestUtil.createSimpleReservationDefinition(start, ((start + (alloc.length)) + 1), alloc.length);
        Map<ReservationInterval, Resource> allocations = generateAllocation(start, alloc, true, false);
        ReservationAllocation rAllocation = new InMemoryReservationAllocation(reservationID, rDef, user, planName, start, ((start + (alloc.length)) + 1), allocations, resCalc, minAlloc);
        doAssertions(rAllocation, reservationID, rDef, allocations, start, alloc);
        Assert.assertFalse(rAllocation.containsGangs());
        for (int i = 0; i < (alloc.length); i++) {
            Assert.assertEquals(Resource.newInstance((1024 * ((alloc[i]) + i)), ((alloc[i]) + i)), rAllocation.getResourcesAtTime((start + i)));
        }
    }

    @Test
    public void testSkyline() {
        ReservationId reservationID = ReservationId.newInstance(rand.nextLong(), rand.nextLong());
        int[] alloc = new int[]{ 0, 5, 10, 10, 5, 0 };
        int start = 100;
        ReservationDefinition rDef = ReservationSystemTestUtil.createSimpleReservationDefinition(start, ((start + (alloc.length)) + 1), alloc.length);
        Map<ReservationInterval, Resource> allocations = generateAllocation(start, alloc, true, false);
        ReservationAllocation rAllocation = new InMemoryReservationAllocation(reservationID, rDef, user, planName, start, ((start + (alloc.length)) + 1), allocations, resCalc, minAlloc);
        doAssertions(rAllocation, reservationID, rDef, allocations, start, alloc);
        Assert.assertFalse(rAllocation.containsGangs());
        for (int i = 0; i < (alloc.length); i++) {
            Assert.assertEquals(Resource.newInstance((1024 * ((alloc[i]) + i)), ((alloc[i]) + i)), rAllocation.getResourcesAtTime((start + i)));
        }
    }

    @Test
    public void testZeroAlloaction() {
        ReservationId reservationID = ReservationId.newInstance(rand.nextLong(), rand.nextLong());
        int[] alloc = new int[]{  };
        long start = 0;
        ReservationDefinition rDef = ReservationSystemTestUtil.createSimpleReservationDefinition(start, ((start + (alloc.length)) + 1), alloc.length);
        Map<ReservationInterval, Resource> allocations = new HashMap<ReservationInterval, Resource>();
        ReservationAllocation rAllocation = new InMemoryReservationAllocation(reservationID, rDef, user, planName, start, ((start + (alloc.length)) + 1), allocations, resCalc, minAlloc);
        doAssertions(rAllocation, reservationID, rDef, allocations, ((int) (start)), alloc);
        Assert.assertFalse(rAllocation.containsGangs());
    }

    @Test
    public void testGangAlloaction() {
        ReservationId reservationID = ReservationId.newInstance(rand.nextLong(), rand.nextLong());
        int[] alloc = new int[]{ 10, 10, 10, 10, 10, 10 };
        int start = 100;
        ReservationDefinition rDef = ReservationSystemTestUtil.createSimpleReservationDefinition(start, ((start + (alloc.length)) + 1), alloc.length);
        boolean isGang = true;
        Map<ReservationInterval, Resource> allocations = generateAllocation(start, alloc, false, isGang);
        ReservationAllocation rAllocation = new InMemoryReservationAllocation(reservationID, rDef, user, planName, start, ((start + (alloc.length)) + 1), allocations, resCalc, minAlloc, isGang);
        doAssertions(rAllocation, reservationID, rDef, allocations, start, alloc);
        Assert.assertTrue(rAllocation.containsGangs());
        for (int i = 0; i < (alloc.length); i++) {
            Assert.assertEquals(Resource.newInstance((1024 * (alloc[i])), alloc[i]), rAllocation.getResourcesAtTime((start + i)));
        }
    }
}

