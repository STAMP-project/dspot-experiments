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
package org.apache.flink.runtime.jobgraph;


import DistributedCache.DistributedCacheEntry;
import DistributionPattern.ALL_TO_ALL;
import DistributionPattern.POINTWISE;
import ResultPartitionType.PIPELINED;
import java.io.IOException;
import java.util.List;
import org.apache.flink.api.common.InvalidProgramException;
import org.apache.flink.api.common.cache.DistributedCache;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.apache.flink.runtime.blob.PermanentBlobKey;
import org.apache.flink.util.InstantiationUtil;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


public class JobGraphTest extends TestLogger {
    @Test
    public void testSerialization() {
        try {
            JobGraph jg = new JobGraph("The graph");
            // add some configuration values
            {
                jg.getJobConfiguration().setString("some key", "some value");
                jg.getJobConfiguration().setDouble("Life of ", Math.PI);
            }
            // add some vertices
            {
                JobVertex source1 = new JobVertex("source1");
                JobVertex source2 = new JobVertex("source2");
                JobVertex target = new JobVertex("target");
                target.connectNewDataSetAsInput(source1, POINTWISE, PIPELINED);
                target.connectNewDataSetAsInput(source2, ALL_TO_ALL, PIPELINED);
                jg.addVertex(source1);
                jg.addVertex(source2);
                jg.addVertex(target);
            }
            // de-/serialize and compare
            JobGraph copy = CommonTestUtils.createCopySerializable(jg);
            Assert.assertEquals(jg.getName(), copy.getName());
            Assert.assertEquals(jg.getJobID(), copy.getJobID());
            Assert.assertEquals(jg.getJobConfiguration(), copy.getJobConfiguration());
            Assert.assertEquals(jg.getNumberOfVertices(), copy.getNumberOfVertices());
            for (JobVertex vertex : copy.getVertices()) {
                JobVertex original = jg.findVertexByID(vertex.getID());
                Assert.assertNotNull(original);
                Assert.assertEquals(original.getName(), vertex.getName());
                Assert.assertEquals(original.getNumberOfInputs(), vertex.getNumberOfInputs());
                Assert.assertEquals(original.getNumberOfProducedIntermediateDataSets(), vertex.getNumberOfProducedIntermediateDataSets());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTopologicalSort1() {
        try {
            JobVertex source1 = new JobVertex("source1");
            JobVertex source2 = new JobVertex("source2");
            JobVertex target1 = new JobVertex("target1");
            JobVertex target2 = new JobVertex("target2");
            JobVertex intermediate1 = new JobVertex("intermediate1");
            JobVertex intermediate2 = new JobVertex("intermediate2");
            target1.connectNewDataSetAsInput(source1, POINTWISE, PIPELINED);
            target2.connectNewDataSetAsInput(source1, POINTWISE, PIPELINED);
            target2.connectNewDataSetAsInput(intermediate2, POINTWISE, PIPELINED);
            intermediate2.connectNewDataSetAsInput(intermediate1, POINTWISE, PIPELINED);
            intermediate1.connectNewDataSetAsInput(source2, POINTWISE, PIPELINED);
            JobGraph graph = new JobGraph("TestGraph", source1, source2, intermediate1, intermediate2, target1, target2);
            List<JobVertex> sorted = graph.getVerticesSortedTopologicallyFromSources();
            Assert.assertEquals(6, sorted.size());
            JobGraphTest.assertBefore(source1, target1, sorted);
            JobGraphTest.assertBefore(source1, target2, sorted);
            JobGraphTest.assertBefore(source2, target2, sorted);
            JobGraphTest.assertBefore(source2, intermediate1, sorted);
            JobGraphTest.assertBefore(source2, intermediate2, sorted);
            JobGraphTest.assertBefore(intermediate1, target2, sorted);
            JobGraphTest.assertBefore(intermediate2, target2, sorted);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTopologicalSort2() {
        try {
            JobVertex source1 = new JobVertex("source1");
            JobVertex source2 = new JobVertex("source2");
            JobVertex root = new JobVertex("root");
            JobVertex l11 = new JobVertex("layer 1 - 1");
            JobVertex l12 = new JobVertex("layer 1 - 2");
            JobVertex l13 = new JobVertex("layer 1 - 3");
            JobVertex l2 = new JobVertex("layer 2");
            root.connectNewDataSetAsInput(l13, POINTWISE, PIPELINED);
            root.connectNewDataSetAsInput(source2, POINTWISE, PIPELINED);
            root.connectNewDataSetAsInput(l2, POINTWISE, PIPELINED);
            l2.connectNewDataSetAsInput(l11, POINTWISE, PIPELINED);
            l2.connectNewDataSetAsInput(l12, POINTWISE, PIPELINED);
            l11.connectNewDataSetAsInput(source1, POINTWISE, PIPELINED);
            l12.connectNewDataSetAsInput(source1, POINTWISE, PIPELINED);
            l12.connectNewDataSetAsInput(source2, POINTWISE, PIPELINED);
            l13.connectNewDataSetAsInput(source2, POINTWISE, PIPELINED);
            JobGraph graph = new JobGraph("TestGraph", source1, source2, root, l11, l13, l12, l2);
            List<JobVertex> sorted = graph.getVerticesSortedTopologicallyFromSources();
            Assert.assertEquals(7, sorted.size());
            JobGraphTest.assertBefore(source1, root, sorted);
            JobGraphTest.assertBefore(source2, root, sorted);
            JobGraphTest.assertBefore(l11, root, sorted);
            JobGraphTest.assertBefore(l12, root, sorted);
            JobGraphTest.assertBefore(l13, root, sorted);
            JobGraphTest.assertBefore(l2, root, sorted);
            JobGraphTest.assertBefore(l11, l2, sorted);
            JobGraphTest.assertBefore(l12, l2, sorted);
            JobGraphTest.assertBefore(l2, root, sorted);
            JobGraphTest.assertBefore(source1, l2, sorted);
            JobGraphTest.assertBefore(source2, l2, sorted);
            JobGraphTest.assertBefore(source2, l13, sorted);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTopologicalSort3() {
        // --> op1 --
        // /         \
        // (source) -           +-> op2 -> op3
        // \         /
        // ---------
        try {
            JobVertex source = new JobVertex("source");
            JobVertex op1 = new JobVertex("op4");
            JobVertex op2 = new JobVertex("op2");
            JobVertex op3 = new JobVertex("op3");
            op1.connectNewDataSetAsInput(source, POINTWISE, PIPELINED);
            op2.connectNewDataSetAsInput(op1, POINTWISE, PIPELINED);
            op2.connectNewDataSetAsInput(source, POINTWISE, PIPELINED);
            op3.connectNewDataSetAsInput(op2, POINTWISE, PIPELINED);
            JobGraph graph = new JobGraph("TestGraph", source, op1, op2, op3);
            List<JobVertex> sorted = graph.getVerticesSortedTopologicallyFromSources();
            Assert.assertEquals(4, sorted.size());
            JobGraphTest.assertBefore(source, op1, sorted);
            JobGraphTest.assertBefore(source, op2, sorted);
            JobGraphTest.assertBefore(op1, op2, sorted);
            JobGraphTest.assertBefore(op2, op3, sorted);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTopoSortCyclicGraphNoSources() {
        try {
            JobVertex v1 = new JobVertex("1");
            JobVertex v2 = new JobVertex("2");
            JobVertex v3 = new JobVertex("3");
            JobVertex v4 = new JobVertex("4");
            v1.connectNewDataSetAsInput(v4, POINTWISE, PIPELINED);
            v2.connectNewDataSetAsInput(v1, POINTWISE, PIPELINED);
            v3.connectNewDataSetAsInput(v2, POINTWISE, PIPELINED);
            v4.connectNewDataSetAsInput(v3, POINTWISE, PIPELINED);
            JobGraph jg = new JobGraph("Cyclic Graph", v1, v2, v3, v4);
            try {
                jg.getVerticesSortedTopologicallyFromSources();
                Assert.fail("Failed to raise error on topologically sorting cyclic graph.");
            } catch (InvalidProgramException e) {
                // that what we wanted
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTopoSortCyclicGraphIntermediateCycle() {
        try {
            JobVertex source = new JobVertex("source");
            JobVertex v1 = new JobVertex("1");
            JobVertex v2 = new JobVertex("2");
            JobVertex v3 = new JobVertex("3");
            JobVertex v4 = new JobVertex("4");
            JobVertex target = new JobVertex("target");
            v1.connectNewDataSetAsInput(source, POINTWISE, PIPELINED);
            v1.connectNewDataSetAsInput(v4, POINTWISE, PIPELINED);
            v2.connectNewDataSetAsInput(v1, POINTWISE, PIPELINED);
            v3.connectNewDataSetAsInput(v2, POINTWISE, PIPELINED);
            v4.connectNewDataSetAsInput(v3, POINTWISE, PIPELINED);
            target.connectNewDataSetAsInput(v3, POINTWISE, PIPELINED);
            JobGraph jg = new JobGraph("Cyclic Graph", v1, v2, v3, v4, source, target);
            try {
                jg.getVerticesSortedTopologicallyFromSources();
                Assert.fail("Failed to raise error on topologically sorting cyclic graph.");
            } catch (InvalidProgramException e) {
                // that what we wanted
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSetUserArtifactBlobKey() throws IOException, ClassNotFoundException {
        JobGraph jb = new JobGraph();
        final DistributedCache[] entries = new DistributedCacheEntry[]{ new DistributedCache.DistributedCacheEntry("p1", true, true), new DistributedCache.DistributedCacheEntry("p2", true, false), new DistributedCache.DistributedCacheEntry("p3", false, true), new DistributedCache.DistributedCacheEntry("p4", true, false) };
        for (DistributedCache.DistributedCacheEntry entry : entries) {
            jb.addUserArtifact(entry.filePath, entry);
        }
        for (DistributedCache.DistributedCacheEntry entry : entries) {
            PermanentBlobKey blobKey = new PermanentBlobKey();
            jb.setUserArtifactBlobKey(entry.filePath, blobKey);
            DistributedCache.DistributedCacheEntry jobGraphEntry = jb.getUserArtifacts().get(entry.filePath);
            Assert.assertNotNull(jobGraphEntry);
            Assert.assertEquals(blobKey, InstantiationUtil.deserializeObject(jobGraphEntry.blobKey, ClassLoader.getSystemClassLoader(), false));
            Assert.assertEquals(entry.isExecutable, jobGraphEntry.isExecutable);
            Assert.assertEquals(entry.isZipped, jobGraphEntry.isZipped);
            Assert.assertEquals(entry.filePath, jobGraphEntry.filePath);
        }
    }
}

