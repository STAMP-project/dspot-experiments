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
package org.apache.flink.runtime.executiongraph;


import ExecutionState.CANCELED;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.checkpoint.JobManagerTaskRestore;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests that the execution vertex handles locality preferences well.
 */
public class ExecutionVertexLocalityTest extends TestLogger {
    private final JobID jobId = new JobID();

    private final JobVertexID sourceVertexId = new JobVertexID();

    private final JobVertexID targetVertexId = new JobVertexID();

    /**
     * This test validates that vertices that have only one input stream try to
     * co-locate their tasks with the producer.
     */
    @Test
    public void testLocalityInputBasedForward() throws Exception {
        final int parallelism = 10;
        final TaskManagerLocation[] locations = new TaskManagerLocation[parallelism];
        final ExecutionGraph graph = createTestGraph(parallelism, false);
        // set the location for all sources to a distinct location
        for (int i = 0; i < parallelism; i++) {
            ExecutionVertex source = graph.getAllVertices().get(sourceVertexId).getTaskVertices()[i];
            TaskManagerLocation location = new TaskManagerLocation(ResourceID.generate(), InetAddress.getLoopbackAddress(), (10000 + i));
            locations[i] = location;
            initializeLocation(source, location);
        }
        // validate that the target vertices have no location preference
        for (int i = 0; i < parallelism; i++) {
            ExecutionVertex target = graph.getAllVertices().get(targetVertexId).getTaskVertices()[i];
            Iterator<CompletableFuture<TaskManagerLocation>> preference = target.getPreferredLocations().iterator();
            Assert.assertTrue(preference.hasNext());
            Assert.assertEquals(locations[i], preference.next().get());
            Assert.assertFalse(preference.hasNext());
        }
    }

    /**
     * This test validates that vertices with too many input streams do not have a location
     * preference any more.
     */
    @Test
    public void testNoLocalityInputLargeAllToAll() throws Exception {
        final int parallelism = 100;
        final ExecutionGraph graph = createTestGraph(parallelism, true);
        // set the location for all sources to a distinct location
        for (int i = 0; i < parallelism; i++) {
            ExecutionVertex source = graph.getAllVertices().get(sourceVertexId).getTaskVertices()[i];
            TaskManagerLocation location = new TaskManagerLocation(ResourceID.generate(), InetAddress.getLoopbackAddress(), (10000 + i));
            initializeLocation(source, location);
        }
        // validate that the target vertices have no location preference
        for (int i = 0; i < parallelism; i++) {
            ExecutionVertex target = graph.getAllVertices().get(targetVertexId).getTaskVertices()[i];
            Iterator<CompletableFuture<TaskManagerLocation>> preference = target.getPreferredLocations().iterator();
            Assert.assertFalse(preference.hasNext());
        }
    }

    /**
     * This test validates that stateful vertices schedule based in the state's location
     * (which is the prior execution's location).
     */
    @Test
    public void testLocalityBasedOnState() throws Exception {
        final int parallelism = 10;
        final TaskManagerLocation[] locations = new TaskManagerLocation[parallelism];
        final ExecutionGraph graph = createTestGraph(parallelism, false);
        // set the location for all sources and targets
        for (int i = 0; i < parallelism; i++) {
            ExecutionVertex source = graph.getAllVertices().get(sourceVertexId).getTaskVertices()[i];
            ExecutionVertex target = graph.getAllVertices().get(targetVertexId).getTaskVertices()[i];
            TaskManagerLocation randomLocation = new TaskManagerLocation(ResourceID.generate(), InetAddress.getLoopbackAddress(), (10000 + i));
            TaskManagerLocation location = new TaskManagerLocation(ResourceID.generate(), InetAddress.getLoopbackAddress(), (20000 + i));
            locations[i] = location;
            initializeLocation(source, randomLocation);
            initializeLocation(target, location);
            setState(source.getCurrentExecutionAttempt(), CANCELED);
            setState(target.getCurrentExecutionAttempt(), CANCELED);
        }
        // mimic a restart: all vertices get re-initialized without actually being executed
        for (ExecutionJobVertex ejv : graph.getVerticesTopologically()) {
            ejv.resetForNewExecution(System.currentTimeMillis(), graph.getGlobalModVersion());
        }
        // set new location for the sources and some state for the targets
        for (int i = 0; i < parallelism; i++) {
            // source location
            ExecutionVertex source = graph.getAllVertices().get(sourceVertexId).getTaskVertices()[i];
            TaskManagerLocation randomLocation = new TaskManagerLocation(ResourceID.generate(), InetAddress.getLoopbackAddress(), (30000 + i));
            initializeLocation(source, randomLocation);
            // target state
            ExecutionVertex target = graph.getAllVertices().get(targetVertexId).getTaskVertices()[i];
            target.getCurrentExecutionAttempt().setInitialState(Mockito.mock(JobManagerTaskRestore.class));
        }
        // validate that the target vertices have the state's location as the location preference
        for (int i = 0; i < parallelism; i++) {
            ExecutionVertex target = graph.getAllVertices().get(targetVertexId).getTaskVertices()[i];
            Iterator<CompletableFuture<TaskManagerLocation>> preference = target.getPreferredLocations().iterator();
            Assert.assertTrue(preference.hasNext());
            Assert.assertEquals(locations[i], preference.next().get());
            Assert.assertFalse(preference.hasNext());
        }
    }
}

