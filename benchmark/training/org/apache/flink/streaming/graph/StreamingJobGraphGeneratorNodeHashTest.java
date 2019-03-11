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
package org.apache.flink.streaming.graph;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the {@link StreamNode} hash assignment during translation from {@link StreamGraph} to
 * {@link JobGraph} instances.
 */
@SuppressWarnings("serial")
public class StreamingJobGraphGeneratorNodeHashTest extends TestLogger {
    // ------------------------------------------------------------------------
    // Deterministic hash assignment
    // ------------------------------------------------------------------------
    /**
     * Creates the same flow twice and checks that all IDs are the same.
     *
     * <pre>
     * [ (src) -> (map) -> (filter) -> (reduce) -> (map) -> (sink) ]
     *                                                       //
     * [ (src) -> (filter) ] -------------------------------//
     *                                                      /
     * [ (src) -> (filter) ] ------------------------------/
     * </pre>
     */
    @Test
    public void testNodeHashIsDeterministic() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        DataStream<String> src0 = env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction(), "src0").map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction()).keyBy(new StreamingJobGraphGeneratorNodeHashTest.NoOpKeySelector()).reduce(new StreamingJobGraphGeneratorNodeHashTest.NoOpReduceFunction()).name("reduce");
        DataStream<String> src1 = env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction(), "src1").filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction());
        DataStream<String> src2 = env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction(), "src2").filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction());
        src0.map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).union(src1, src2).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction()).name("sink");
        JobGraph jobGraph = env.getStreamGraph().getJobGraph();
        final Map<JobVertexID, String> ids = rememberIds(jobGraph);
        // Do it again and verify
        env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        src0 = env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction(), "src0").map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction()).keyBy(new StreamingJobGraphGeneratorNodeHashTest.NoOpKeySelector()).reduce(new StreamingJobGraphGeneratorNodeHashTest.NoOpReduceFunction()).name("reduce");
        src1 = env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction(), "src1").filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction());
        src2 = env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction(), "src2").filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction());
        src0.map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).union(src1, src2).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction()).name("sink");
        jobGraph = env.getStreamGraph().getJobGraph();
        verifyIdsEqual(jobGraph, ids);
    }

    /**
     * Tests that there are no collisions with two identical sources.
     *
     * <pre>
     * [ (src0) ] --\
     *               +--> [ (sink) ]
     * [ (src1) ] --/
     * </pre>
     */
    @Test
    public void testNodeHashIdenticalSources() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        env.disableOperatorChaining();
        DataStream<String> src0 = env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction());
        DataStream<String> src1 = env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction());
        src0.union(src1).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction());
        JobGraph jobGraph = env.getStreamGraph().getJobGraph();
        List<JobVertex> vertices = jobGraph.getVerticesSortedTopologicallyFromSources();
        Assert.assertTrue(vertices.get(0).isInputVertex());
        Assert.assertTrue(vertices.get(1).isInputVertex());
        Assert.assertNotNull(vertices.get(0).getID());
        Assert.assertNotNull(vertices.get(1).getID());
        Assert.assertNotEquals(vertices.get(0).getID(), vertices.get(1).getID());
    }

    /**
     * Tests that (un)chaining affects the node hash (for sources).
     *
     * <pre>
     * A (chained): [ (src0) -> (map) -> (filter) -> (sink) ]
     * B (unchained): [ (src0) ] -> [ (map) -> (filter) -> (sink) ]
     * </pre>
     *
     * <p>The hashes for the single vertex in A and the source vertex in B need to be different.
     */
    @Test
    public void testNodeHashAfterSourceUnchaining() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction()).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction());
        JobGraph jobGraph = env.getStreamGraph().getJobGraph();
        JobVertexID sourceId = jobGraph.getVerticesSortedTopologicallyFromSources().get(0).getID();
        env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).startNewChain().filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction()).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction());
        jobGraph = env.getStreamGraph().getJobGraph();
        JobVertexID unchainedSourceId = jobGraph.getVerticesSortedTopologicallyFromSources().get(0).getID();
        Assert.assertNotEquals(sourceId, unchainedSourceId);
    }

    /**
     * Tests that (un)chaining affects the node hash (for intermediate nodes).
     *
     * <pre>
     * A (chained): [ (src0) -> (map) -> (filter) -> (sink) ]
     * B (unchained): [ (src0) ] -> [ (map) -> (filter) -> (sink) ]
     * </pre>
     *
     * <p>The hashes for the single vertex in A and the source vertex in B need to be different.
     */
    @Test
    public void testNodeHashAfterIntermediateUnchaining() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).name("map").startNewChain().filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction()).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction());
        JobGraph jobGraph = env.getStreamGraph().getJobGraph();
        JobVertex chainedMap = jobGraph.getVerticesSortedTopologicallyFromSources().get(1);
        Assert.assertTrue(chainedMap.getName().startsWith("map"));
        JobVertexID chainedMapId = chainedMap.getID();
        env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).name("map").startNewChain().filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction()).startNewChain().addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction());
        jobGraph = env.getStreamGraph().getJobGraph();
        JobVertex unchainedMap = jobGraph.getVerticesSortedTopologicallyFromSources().get(1);
        Assert.assertEquals("map", unchainedMap.getName());
        JobVertexID unchainedMapId = unchainedMap.getID();
        Assert.assertNotEquals(chainedMapId, unchainedMapId);
    }

    /**
     * Tests that there are no collisions with two identical intermediate nodes connected to the
     * same predecessor.
     *
     * <pre>
     *             /-> [ (map) ] -> [ (sink) ]
     * [ (src) ] -+
     *             \-> [ (map) ] -> [ (sink) ]
     * </pre>
     */
    @Test
    public void testNodeHashIdenticalNodes() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        env.disableOperatorChaining();
        DataStream<String> src = env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction());
        src.map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction());
        src.map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction());
        JobGraph jobGraph = env.getStreamGraph().getJobGraph();
        Set<JobVertexID> vertexIds = new HashSet<>();
        for (JobVertex vertex : jobGraph.getVertices()) {
            Assert.assertTrue(vertexIds.add(vertex.getID()));
        }
    }

    /**
     * Tests that a changed operator name does not affect the hash.
     */
    @Test
    public void testChangedOperatorName() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction(), "A").map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction());
        JobGraph jobGraph = env.getStreamGraph().getJobGraph();
        JobVertexID expected = jobGraph.getVerticesAsArray()[0].getID();
        env = StreamExecutionEnvironment.createLocalEnvironment();
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction(), "B").map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction());
        jobGraph = env.getStreamGraph().getJobGraph();
        JobVertexID actual = jobGraph.getVerticesAsArray()[0].getID();
        Assert.assertEquals(expected, actual);
    }

    // ------------------------------------------------------------------------
    // Manual hash assignment
    // ------------------------------------------------------------------------
    /**
     * Tests that manual hash assignments are mapped to the same operator ID.
     *
     * <pre>
     *                     /-> [ (map) ] -> [ (sink)@sink0 ]
     * [ (src@source ) ] -+
     *                     \-> [ (map) ] -> [ (sink)@sink1 ]
     * </pre>
     *
     * <pre>
     *                    /-> [ (map) ] -> [ (reduce) ] -> [ (sink)@sink0 ]
     * [ (src)@source ] -+
     *                   \-> [ (map) ] -> [ (reduce) ] -> [ (sink)@sink1 ]
     * </pre>
     */
    @Test
    public void testManualHashAssignment() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        env.disableOperatorChaining();
        DataStream<String> src = env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).name("source").uid("source");
        src.map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction()).name("sink0").uid("sink0");
        src.map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction()).name("sink1").uid("sink1");
        JobGraph jobGraph = env.getStreamGraph().getJobGraph();
        Set<JobVertexID> ids = new HashSet<>();
        for (JobVertex vertex : jobGraph.getVertices()) {
            Assert.assertTrue(ids.add(vertex.getID()));
        }
        // Resubmit a slightly different program
        env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        env.disableOperatorChaining();
        src = // New map function, should be mapped to the source state
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).name("source").uid("source");
        src.map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).keyBy(new StreamingJobGraphGeneratorNodeHashTest.NoOpKeySelector()).reduce(new StreamingJobGraphGeneratorNodeHashTest.NoOpReduceFunction()).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction()).name("sink0").uid("sink0");
        src.map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).keyBy(new StreamingJobGraphGeneratorNodeHashTest.NoOpKeySelector()).reduce(new StreamingJobGraphGeneratorNodeHashTest.NoOpReduceFunction()).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction()).name("sink1").uid("sink1");
        JobGraph newJobGraph = env.getStreamGraph().getJobGraph();
        Assert.assertNotEquals(jobGraph.getJobID(), newJobGraph.getJobID());
        for (JobVertex vertex : newJobGraph.getVertices()) {
            // Verify that the expected IDs are the same
            if (((vertex.getName().endsWith("source")) || (vertex.getName().endsWith("sink0"))) || (vertex.getName().endsWith("sink1"))) {
                Assert.assertTrue(ids.contains(vertex.getID()));
            }
        }
    }

    /**
     * Tests that a collision on the manual hash throws an Exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testManualHashAssignmentCollisionThrowsException() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        env.disableOperatorChaining();
        // Collision
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).uid("source").map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).uid("source").addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction());
        // This call is necessary to generate the job graph
        env.getStreamGraph().getJobGraph();
    }

    /**
     * Tests that a manual hash for an intermediate chain node is accepted.
     */
    @Test
    public void testManualHashAssignmentForIntermediateNodeInChain() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        // Intermediate chained node
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).uid("map").addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction());
        env.getStreamGraph().getJobGraph();
    }

    /**
     * Tests that a manual hash at the beginning of a chain is accepted.
     */
    @Test
    public void testManualHashAssignmentForStartNodeInInChain() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setParallelism(4);
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).uid("source").map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).addSink(new StreamingJobGraphGeneratorNodeHashTest.NoOpSinkFunction());
        env.getStreamGraph().getJobGraph();
    }

    @Test
    public void testUserProvidedHashing() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        List<String> userHashes = Arrays.asList("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction(), "src").setUidHash(userHashes.get(0)).map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction()).keyBy(new StreamingJobGraphGeneratorNodeHashTest.NoOpKeySelector()).reduce(new StreamingJobGraphGeneratorNodeHashTest.NoOpReduceFunction()).name("reduce").setUidHash(userHashes.get(1));
        StreamGraph streamGraph = env.getStreamGraph();
        int idx = 1;
        for (JobVertex jobVertex : streamGraph.getJobGraph().getVertices()) {
            List<JobVertexID> idAlternatives = jobVertex.getIdAlternatives();
            Assert.assertEquals(idAlternatives.get(((idAlternatives.size()) - 1)).toString(), userHashes.get(idx));
            --idx;
        }
    }

    @Test
    public void testUserProvidedHashingOnChainSupported() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction(), "src").setUidHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").map(new StreamingJobGraphGeneratorNodeHashTest.NoOpMapFunction()).setUidHash("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb").filter(new StreamingJobGraphGeneratorNodeHashTest.NoOpFilterFunction()).setUidHash("cccccccccccccccccccccccccccccccc").keyBy(new StreamingJobGraphGeneratorNodeHashTest.NoOpKeySelector()).reduce(new StreamingJobGraphGeneratorNodeHashTest.NoOpReduceFunction()).name("reduce").setUidHash("dddddddddddddddddddddddddddddddd");
        env.getStreamGraph().getJobGraph();
    }

    @Test(expected = IllegalStateException.class)
    public void testDisablingAutoUidsFailsStreamGraphCreation() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.getConfig().disableAutoGeneratedUIDs();
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink());
        env.getStreamGraph();
    }

    @Test
    public void testDisablingAutoUidsAcceptsManuallySetId() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.getConfig().disableAutoGeneratedUIDs();
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).uid("uid1").addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink()).uid("uid2");
        env.getStreamGraph();
    }

    @Test
    public void testDisablingAutoUidsAcceptsManuallySetHash() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.getConfig().disableAutoGeneratedUIDs();
        env.addSource(new StreamingJobGraphGeneratorNodeHashTest.NoOpSourceFunction()).setUidHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink()).setUidHash("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        env.getStreamGraph();
    }

    // ------------------------------------------------------------------------
    private static class NoOpSourceFunction implements ParallelSourceFunction<String> {
        private static final long serialVersionUID = -5459224792698512636L;

        @Override
        public void run(SourceContext<String> ctx) throws Exception {
        }

        @Override
        public void cancel() {
        }
    }

    private static class NoOpSinkFunction implements SinkFunction<String> {
        private static final long serialVersionUID = -5654199886203297279L;

        @Override
        public void invoke(String value) throws Exception {
        }
    }

    private static class NoOpMapFunction implements MapFunction<String, String> {
        private static final long serialVersionUID = 6584823409744624276L;

        @Override
        public String map(String value) throws Exception {
            return value;
        }
    }

    private static class NoOpFilterFunction implements FilterFunction<String> {
        private static final long serialVersionUID = 500005424900187476L;

        @Override
        public boolean filter(String value) throws Exception {
            return true;
        }
    }

    private static class NoOpKeySelector implements KeySelector<String, String> {
        private static final long serialVersionUID = -96127515593422991L;

        @Override
        public String getKey(String value) throws Exception {
            return value;
        }
    }

    private static class NoOpReduceFunction implements ReduceFunction<String> {
        private static final long serialVersionUID = -8775747640749256372L;

        @Override
        public String reduce(String value1, String value2) throws Exception {
            return value1;
        }
    }
}

