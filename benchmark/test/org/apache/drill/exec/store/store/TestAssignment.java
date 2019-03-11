/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.store;


import java.util.Iterator;
import java.util.List;
import org.apache.drill.exec.proto.CoordinationProtos.DrillbitEndpoint;
import org.apache.drill.exec.store.schedule.AssignmentCreator;
import org.apache.drill.exec.store.schedule.CompleteFileWork;
import org.apache.drill.shaded.guava.com.google.common.collect.Iterators;
import org.apache.drill.shaded.guava.com.google.common.collect.ListMultimap;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;


public class TestAssignment {
    private static final long FILE_SIZE = 1000;

    private static List<DrillbitEndpoint> endpoints;

    private static final int numEndPoints = 30;

    private final int widthPerNode = 23;

    @Test
    public void testBalanceAcrossNodes() throws Exception {
        int numChunks = ((widthPerNode) * (TestAssignment.numEndPoints)) + 100;
        List<CompleteFileWork> chunks = generateChunks(numChunks);
        Iterator<DrillbitEndpoint> incomingEndpointsIterator = Iterators.cycle(TestAssignment.endpoints);
        List<DrillbitEndpoint> incomingEndpoints = Lists.newArrayList();
        List<Integer> expectedAssignments = Lists.newArrayList();
        List<Integer> actualAssignments = Lists.newArrayList();
        final int width = (widthPerNode) * (TestAssignment.numEndPoints);
        for (int i = 0; i < width; i++) {
            incomingEndpoints.add(incomingEndpointsIterator.next());
        }
        // Calculate expected assignments for each node.
        final int numAssignmentsPerNode = numChunks / (TestAssignment.numEndPoints);
        int leftOver = numChunks - (numAssignmentsPerNode * (TestAssignment.numEndPoints));
        for (int i = 0; i < (TestAssignment.numEndPoints); i++) {
            int additional = (leftOver > 0) ? 1 : 0;
            expectedAssignments.add((numAssignmentsPerNode + additional));
            if (leftOver > 0) {
                leftOver--;
            }
        }
        ListMultimap<Integer, CompleteFileWork> mappings = AssignmentCreator.getMappings(incomingEndpoints, chunks);
        // Verify that all fragments have chunks assigned.
        for (int i = 0; i < width; i++) {
            Assert.assertTrue(("no mapping for entry " + i), (((mappings.get(i)) != null) && ((mappings.get(i).size()) > 0)));
        }
        // Verify actual and expected assignments per node match.
        // Compute actual assignments for each node.
        for (int i = 0; i < (TestAssignment.numEndPoints); i++) {
            int numAssignments = 0;
            int index = i;
            while (index < ((TestAssignment.numEndPoints) * (widthPerNode))) {
                numAssignments += mappings.get(index).size();
                index += TestAssignment.numEndPoints;
            } 
            actualAssignments.add(numAssignments);
        }
        for (int i = 0; i < (TestAssignment.numEndPoints); i++) {
            Assert.assertTrue(((actualAssignments.get(i)) == (expectedAssignments.get(i))));
        }
    }

    @Test
    public void manyFiles() throws Exception {
        List<CompleteFileWork> chunks = generateChunks(1000);
        Iterator<DrillbitEndpoint> incomingEndpointsIterator = Iterators.cycle(TestAssignment.endpoints);
        List<DrillbitEndpoint> incomingEndpoints = Lists.newArrayList();
        final int width = (widthPerNode) * (TestAssignment.numEndPoints);
        for (int i = 0; i < width; i++) {
            incomingEndpoints.add(incomingEndpointsIterator.next());
        }
        ListMultimap<Integer, CompleteFileWork> mappings = AssignmentCreator.getMappings(incomingEndpoints, chunks);
        for (int i = 0; i < width; i++) {
            Assert.assertTrue(("no mapping for entry " + i), (((mappings.get(i)) != null) && ((mappings.get(i).size()) > 0)));
        }
    }
}

