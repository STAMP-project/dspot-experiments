/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.api;


import BasicArrayTypeInfo.INT_ARRAY_TYPE_INFO;
import PrimitiveArrayTypeInfo.INT_PRIMITIVE_ARRAY_TYPE_INFO;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.flink.api.common.InvalidProgramException;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.common.operators.ResourceSpec;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicArrayTypeInfo;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.PrimitiveArrayTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.streaming.api.graph.StreamEdge;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.streaming.api.operators.KeyedProcessOperator;
import org.apache.flink.streaming.api.operators.LegacyKeyedProcessOperator;
import org.apache.flink.streaming.api.operators.ProcessOperator;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.PurgingTrigger;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.runtime.partitioner.BroadcastPartitioner;
import org.apache.flink.streaming.runtime.partitioner.ForwardPartitioner;
import org.apache.flink.streaming.runtime.partitioner.GlobalPartitioner;
import org.apache.flink.streaming.runtime.partitioner.KeyGroupStreamPartitioner;
import org.apache.flink.streaming.runtime.partitioner.RebalancePartitioner;
import org.apache.flink.streaming.runtime.partitioner.ShufflePartitioner;
import org.apache.flink.streaming.runtime.partitioner.StreamPartitioner;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.flink.util.TestLogger;
import org.hamcrest.core.StringStartsWith;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link DataStream}.
 */
@SuppressWarnings("serial")
public class DataStreamTest extends TestLogger {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Tests union functionality. This ensures that self-unions and unions of streams
     * with differing parallelism work.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUnion() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        DataStream<Long> input1 = env.generateSequence(0, 0).map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        });
        DataStream<Long> selfUnion = input1.union(input1).map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        });
        DataStream<Long> input6 = env.generateSequence(0, 0).map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        });
        DataStream<Long> selfUnionDifferentPartition = input6.broadcast().union(input6).map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        });
        DataStream<Long> input2 = env.generateSequence(0, 0).map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        }).setParallelism(4);
        DataStream<Long> input3 = env.generateSequence(0, 0).map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        }).setParallelism(2);
        DataStream<Long> unionDifferingParallelism = input2.union(input3).map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        }).setParallelism(4);
        DataStream<Long> input4 = env.generateSequence(0, 0).map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        }).setParallelism(2);
        DataStream<Long> input5 = env.generateSequence(0, 0).map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        }).setParallelism(4);
        DataStream<Long> unionDifferingPartitioning = input4.broadcast().union(input5).map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        }).setParallelism(4);
        StreamGraph streamGraph = env.getStreamGraph();
        // verify self union
        Assert.assertTrue(((streamGraph.getStreamNode(selfUnion.getId()).getInEdges().size()) == 2));
        for (StreamEdge edge : streamGraph.getStreamNode(selfUnion.getId()).getInEdges()) {
            Assert.assertTrue(((edge.getPartitioner()) instanceof ForwardPartitioner));
        }
        // verify self union with different partitioners
        Assert.assertTrue(((streamGraph.getStreamNode(selfUnionDifferentPartition.getId()).getInEdges().size()) == 2));
        boolean hasForward = false;
        boolean hasBroadcast = false;
        for (StreamEdge edge : streamGraph.getStreamNode(selfUnionDifferentPartition.getId()).getInEdges()) {
            if ((edge.getPartitioner()) instanceof ForwardPartitioner) {
                hasForward = true;
            }
            if ((edge.getPartitioner()) instanceof BroadcastPartitioner) {
                hasBroadcast = true;
            }
        }
        Assert.assertTrue((hasForward && hasBroadcast));
        // verify union of streams with differing parallelism
        Assert.assertTrue(((streamGraph.getStreamNode(unionDifferingParallelism.getId()).getInEdges().size()) == 2));
        for (StreamEdge edge : streamGraph.getStreamNode(unionDifferingParallelism.getId()).getInEdges()) {
            if ((edge.getSourceId()) == (input2.getId())) {
                Assert.assertTrue(((edge.getPartitioner()) instanceof ForwardPartitioner));
            } else
                if ((edge.getSourceId()) == (input3.getId())) {
                    Assert.assertTrue(((edge.getPartitioner()) instanceof RebalancePartitioner));
                } else {
                    Assert.fail("Wrong input edge.");
                }

        }
        // verify union of streams with differing partitionings
        Assert.assertTrue(((streamGraph.getStreamNode(unionDifferingPartitioning.getId()).getInEdges().size()) == 2));
        for (StreamEdge edge : streamGraph.getStreamNode(unionDifferingPartitioning.getId()).getInEdges()) {
            if ((edge.getSourceId()) == (input4.getId())) {
                Assert.assertTrue(((edge.getPartitioner()) instanceof BroadcastPartitioner));
            } else
                if ((edge.getSourceId()) == (input5.getId())) {
                    Assert.assertTrue(((edge.getPartitioner()) instanceof ForwardPartitioner));
                } else {
                    Assert.fail("Wrong input edge.");
                }

        }
    }

    /**
     * Tests {@link SingleOutputStreamOperator#name(String)} functionality.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNaming() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Long> dataStream1 = env.generateSequence(0, 0).name("testSource1").map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        }).name("testMap");
        DataStream<Long> dataStream2 = env.generateSequence(0, 0).name("testSource2").map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        }).name("testMap");
        dataStream1.connect(dataStream2).flatMap(new org.apache.flink.streaming.api.functions.co.CoFlatMapFunction<Long, Long, Long>() {
            @Override
            public void flatMap1(Long value, Collector<Long> out) throws Exception {
            }

            @Override
            public void flatMap2(Long value, Collector<Long> out) throws Exception {
            }
        }).name("testCoFlatMap").windowAll(GlobalWindows.create()).trigger(PurgingTrigger.of(CountTrigger.of(10))).fold(0L, new org.apache.flink.api.common.functions.FoldFunction<Long, Long>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Long fold(Long accumulator, Long value) throws Exception {
                return null;
            }
        }).name("testWindowFold").print();
        // test functionality through the operator names in the execution plan
        String plan = env.getExecutionPlan();
        Assert.assertTrue(plan.contains("testSource1"));
        Assert.assertTrue(plan.contains("testSource2"));
        Assert.assertTrue(plan.contains("testMap"));
        Assert.assertTrue(plan.contains("testMap"));
        Assert.assertTrue(plan.contains("testCoFlatMap"));
        Assert.assertTrue(plan.contains("testWindowFold"));
    }

    /**
     * Tests that {@link DataStream#keyBy} and {@link DataStream#partitionCustom(Partitioner, int)} result in
     * different and correct topologies. Does the some for the {@link ConnectedStreams}.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testPartitioning() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Tuple2<Long, Long>> src1 = env.fromElements(new Tuple2(0L, 0L));
        DataStream<Tuple2<Long, Long>> src2 = env.fromElements(new Tuple2(0L, 0L));
        ConnectedStreams<Tuple2<Long, Long>, Tuple2<Long, Long>> connected = src1.connect(src2);
        // Testing DataStream grouping
        DataStream<Tuple2<Long, Long>> group1 = src1.keyBy(0);
        DataStream<Tuple2<Long, Long>> group2 = src1.keyBy(1, 0);
        DataStream<Tuple2<Long, Long>> group3 = src1.keyBy("f0");
        DataStream<Tuple2<Long, Long>> group4 = src1.keyBy(new DataStreamTest.FirstSelector());
        int id1 = DataStreamTest.createDownStreamId(group1);
        int id2 = DataStreamTest.createDownStreamId(group2);
        int id3 = DataStreamTest.createDownStreamId(group3);
        int id4 = DataStreamTest.createDownStreamId(group4);
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), id1)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), id2)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), id3)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), id4)));
        Assert.assertTrue(DataStreamTest.isKeyed(group1));
        Assert.assertTrue(DataStreamTest.isKeyed(group2));
        Assert.assertTrue(DataStreamTest.isKeyed(group3));
        Assert.assertTrue(DataStreamTest.isKeyed(group4));
        // Testing DataStream partitioning
        DataStream<Tuple2<Long, Long>> partition1 = src1.keyBy(0);
        DataStream<Tuple2<Long, Long>> partition2 = src1.keyBy(1, 0);
        DataStream<Tuple2<Long, Long>> partition3 = src1.keyBy("f0");
        DataStream<Tuple2<Long, Long>> partition4 = src1.keyBy(new DataStreamTest.FirstSelector());
        int pid1 = DataStreamTest.createDownStreamId(partition1);
        int pid2 = DataStreamTest.createDownStreamId(partition2);
        int pid3 = DataStreamTest.createDownStreamId(partition3);
        int pid4 = DataStreamTest.createDownStreamId(partition4);
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), pid1)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), pid2)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), pid3)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), pid4)));
        Assert.assertTrue(DataStreamTest.isKeyed(partition1));
        Assert.assertTrue(DataStreamTest.isKeyed(partition3));
        Assert.assertTrue(DataStreamTest.isKeyed(partition2));
        Assert.assertTrue(DataStreamTest.isKeyed(partition4));
        // Testing DataStream custom partitioning
        Partitioner<Long> longPartitioner = new Partitioner<Long>() {
            @Override
            public int partition(Long key, int numPartitions) {
                return 100;
            }
        };
        DataStream<Tuple2<Long, Long>> customPartition1 = src1.partitionCustom(longPartitioner, 0);
        DataStream<Tuple2<Long, Long>> customPartition3 = src1.partitionCustom(longPartitioner, "f0");
        DataStream<Tuple2<Long, Long>> customPartition4 = src1.partitionCustom(longPartitioner, new DataStreamTest.FirstSelector());
        int cid1 = DataStreamTest.createDownStreamId(customPartition1);
        int cid2 = DataStreamTest.createDownStreamId(customPartition3);
        int cid3 = DataStreamTest.createDownStreamId(customPartition4);
        Assert.assertTrue(DataStreamTest.isCustomPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), cid1)));
        Assert.assertTrue(DataStreamTest.isCustomPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), cid2)));
        Assert.assertTrue(DataStreamTest.isCustomPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), cid3)));
        Assert.assertFalse(DataStreamTest.isKeyed(customPartition1));
        Assert.assertFalse(DataStreamTest.isKeyed(customPartition3));
        Assert.assertFalse(DataStreamTest.isKeyed(customPartition4));
        // Testing ConnectedStreams grouping
        ConnectedStreams<Tuple2<Long, Long>, Tuple2<Long, Long>> connectedGroup1 = connected.keyBy(0, 0);
        Integer downStreamId1 = DataStreamTest.createDownStreamId(connectedGroup1);
        ConnectedStreams<Tuple2<Long, Long>, Tuple2<Long, Long>> connectedGroup2 = connected.keyBy(new int[]{ 0 }, new int[]{ 0 });
        Integer downStreamId2 = DataStreamTest.createDownStreamId(connectedGroup2);
        ConnectedStreams<Tuple2<Long, Long>, Tuple2<Long, Long>> connectedGroup3 = connected.keyBy("f0", "f0");
        Integer downStreamId3 = DataStreamTest.createDownStreamId(connectedGroup3);
        ConnectedStreams<Tuple2<Long, Long>, Tuple2<Long, Long>> connectedGroup4 = connected.keyBy(new String[]{ "f0" }, new String[]{ "f0" });
        Integer downStreamId4 = DataStreamTest.createDownStreamId(connectedGroup4);
        ConnectedStreams<Tuple2<Long, Long>, Tuple2<Long, Long>> connectedGroup5 = connected.keyBy(new DataStreamTest.FirstSelector(), new DataStreamTest.FirstSelector());
        Integer downStreamId5 = DataStreamTest.createDownStreamId(connectedGroup5);
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), downStreamId1)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src2.getId(), downStreamId1)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), downStreamId2)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src2.getId(), downStreamId2)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), downStreamId3)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src2.getId(), downStreamId3)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), downStreamId4)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src2.getId(), downStreamId4)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), downStreamId5)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src2.getId(), downStreamId5)));
        Assert.assertTrue(DataStreamTest.isKeyed(connectedGroup1));
        Assert.assertTrue(DataStreamTest.isKeyed(connectedGroup2));
        Assert.assertTrue(DataStreamTest.isKeyed(connectedGroup3));
        Assert.assertTrue(DataStreamTest.isKeyed(connectedGroup4));
        Assert.assertTrue(DataStreamTest.isKeyed(connectedGroup5));
        // Testing ConnectedStreams partitioning
        ConnectedStreams<Tuple2<Long, Long>, Tuple2<Long, Long>> connectedPartition1 = connected.keyBy(0, 0);
        Integer connectDownStreamId1 = DataStreamTest.createDownStreamId(connectedPartition1);
        ConnectedStreams<Tuple2<Long, Long>, Tuple2<Long, Long>> connectedPartition2 = connected.keyBy(new int[]{ 0 }, new int[]{ 0 });
        Integer connectDownStreamId2 = DataStreamTest.createDownStreamId(connectedPartition2);
        ConnectedStreams<Tuple2<Long, Long>, Tuple2<Long, Long>> connectedPartition3 = connected.keyBy("f0", "f0");
        Integer connectDownStreamId3 = DataStreamTest.createDownStreamId(connectedPartition3);
        ConnectedStreams<Tuple2<Long, Long>, Tuple2<Long, Long>> connectedPartition4 = connected.keyBy(new String[]{ "f0" }, new String[]{ "f0" });
        Integer connectDownStreamId4 = DataStreamTest.createDownStreamId(connectedPartition4);
        ConnectedStreams<Tuple2<Long, Long>, Tuple2<Long, Long>> connectedPartition5 = connected.keyBy(new DataStreamTest.FirstSelector(), new DataStreamTest.FirstSelector());
        Integer connectDownStreamId5 = DataStreamTest.createDownStreamId(connectedPartition5);
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), connectDownStreamId1)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src2.getId(), connectDownStreamId1)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), connectDownStreamId2)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src2.getId(), connectDownStreamId2)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), connectDownStreamId3)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src2.getId(), connectDownStreamId3)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), connectDownStreamId4)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src2.getId(), connectDownStreamId4)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src1.getId(), connectDownStreamId5)));
        Assert.assertTrue(DataStreamTest.isPartitioned(env.getStreamGraph().getStreamEdges(src2.getId(), connectDownStreamId5)));
        Assert.assertTrue(DataStreamTest.isKeyed(connectedPartition1));
        Assert.assertTrue(DataStreamTest.isKeyed(connectedPartition2));
        Assert.assertTrue(DataStreamTest.isKeyed(connectedPartition3));
        Assert.assertTrue(DataStreamTest.isKeyed(connectedPartition4));
        Assert.assertTrue(DataStreamTest.isKeyed(connectedPartition5));
    }

    /**
     * Tests whether parallelism gets set.
     */
    @Test
    public void testParallelism() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Tuple2<Long, Long>> src = env.fromElements(new Tuple2(0L, 0L));
        env.setParallelism(10);
        SingleOutputStreamOperator<Long> map = src.map(new org.apache.flink.api.common.functions.MapFunction<Tuple2<Long, Long>, Long>() {
            @Override
            public Long map(Tuple2<Long, Long> value) throws Exception {
                return null;
            }
        }).name("MyMap");
        DataStream<Long> windowed = map.windowAll(GlobalWindows.create()).trigger(PurgingTrigger.of(CountTrigger.of(10))).fold(0L, new org.apache.flink.api.common.functions.FoldFunction<Long, Long>() {
            @Override
            public Long fold(Long accumulator, Long value) throws Exception {
                return null;
            }
        });
        windowed.addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink<Long>());
        DataStreamSink<Long> sink = map.addSink(new org.apache.flink.streaming.api.functions.sink.SinkFunction<Long>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void invoke(Long value) throws Exception {
            }
        });
        Assert.assertEquals(1, env.getStreamGraph().getStreamNode(src.getId()).getParallelism());
        Assert.assertEquals(10, env.getStreamGraph().getStreamNode(map.getId()).getParallelism());
        Assert.assertEquals(1, env.getStreamGraph().getStreamNode(windowed.getId()).getParallelism());
        Assert.assertEquals(10, env.getStreamGraph().getStreamNode(sink.getTransformation().getId()).getParallelism());
        env.setParallelism(7);
        // Some parts, such as windowing rely on the fact that previous operators have a parallelism
        // set when instantiating the Discretizer. This would break if we dynamically changed
        // the parallelism of operations when changing the setting on the Execution Environment.
        Assert.assertEquals(1, env.getStreamGraph().getStreamNode(src.getId()).getParallelism());
        Assert.assertEquals(10, env.getStreamGraph().getStreamNode(map.getId()).getParallelism());
        Assert.assertEquals(1, env.getStreamGraph().getStreamNode(windowed.getId()).getParallelism());
        Assert.assertEquals(10, env.getStreamGraph().getStreamNode(sink.getTransformation().getId()).getParallelism());
        try {
            src.setParallelism(3);
            Assert.fail();
        } catch (IllegalArgumentException success) {
            // do nothing
        }
        DataStreamSource<Long> parallelSource = env.generateSequence(0, 0);
        parallelSource.addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink<Long>());
        Assert.assertEquals(7, env.getStreamGraph().getStreamNode(parallelSource.getId()).getParallelism());
        parallelSource.setParallelism(3);
        Assert.assertEquals(3, env.getStreamGraph().getStreamNode(parallelSource.getId()).getParallelism());
        map.setParallelism(2);
        Assert.assertEquals(2, env.getStreamGraph().getStreamNode(map.getId()).getParallelism());
        sink.setParallelism(4);
        Assert.assertEquals(4, env.getStreamGraph().getStreamNode(sink.getTransformation().getId()).getParallelism());
    }

    /**
     * Tests whether resources get set.
     */
    @Test
    public void testResources() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        ResourceSpec minResource1 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(100).build();
        ResourceSpec preferredResource1 = ResourceSpec.newBuilder().setCpuCores(2.0).setHeapMemoryInMB(200).build();
        ResourceSpec minResource2 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(200).build();
        ResourceSpec preferredResource2 = ResourceSpec.newBuilder().setCpuCores(2.0).setHeapMemoryInMB(300).build();
        ResourceSpec minResource3 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(300).build();
        ResourceSpec preferredResource3 = ResourceSpec.newBuilder().setCpuCores(2.0).setHeapMemoryInMB(400).build();
        ResourceSpec minResource4 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(400).build();
        ResourceSpec preferredResource4 = ResourceSpec.newBuilder().setCpuCores(2.0).setHeapMemoryInMB(500).build();
        ResourceSpec minResource5 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(500).build();
        ResourceSpec preferredResource5 = ResourceSpec.newBuilder().setCpuCores(2.0).setHeapMemoryInMB(600).build();
        ResourceSpec minResource6 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(600).build();
        ResourceSpec preferredResource6 = ResourceSpec.newBuilder().setCpuCores(2.0).setHeapMemoryInMB(700).build();
        ResourceSpec minResource7 = ResourceSpec.newBuilder().setCpuCores(1.0).setHeapMemoryInMB(700).build();
        ResourceSpec preferredResource7 = ResourceSpec.newBuilder().setCpuCores(2.0).setHeapMemoryInMB(800).build();
        Method opMethod = SingleOutputStreamOperator.class.getDeclaredMethod("setResources", ResourceSpec.class, ResourceSpec.class);
        opMethod.setAccessible(true);
        Method sinkMethod = DataStreamSink.class.getDeclaredMethod("setResources", ResourceSpec.class, ResourceSpec.class);
        sinkMethod.setAccessible(true);
        DataStream<Long> source1 = env.generateSequence(0, 0);
        opMethod.invoke(source1, minResource1, preferredResource1);
        DataStream<Long> map1 = source1.map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        });
        opMethod.invoke(map1, minResource2, preferredResource2);
        DataStream<Long> source2 = env.generateSequence(0, 0);
        opMethod.invoke(source2, minResource3, preferredResource3);
        DataStream<Long> map2 = source2.map(new org.apache.flink.api.common.functions.MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return null;
            }
        });
        opMethod.invoke(map2, minResource4, preferredResource4);
        DataStream<Long> connected = map1.connect(map2).flatMap(new org.apache.flink.streaming.api.functions.co.CoFlatMapFunction<Long, Long, Long>() {
            @Override
            public void flatMap1(Long value, Collector<Long> out) throws Exception {
            }

            @Override
            public void flatMap2(Long value, Collector<Long> out) throws Exception {
            }
        });
        opMethod.invoke(connected, minResource5, preferredResource5);
        DataStream<Long> windowed = connected.windowAll(GlobalWindows.create()).trigger(PurgingTrigger.of(CountTrigger.of(10))).fold(0L, new org.apache.flink.api.common.functions.FoldFunction<Long, Long>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Long fold(Long accumulator, Long value) throws Exception {
                return null;
            }
        });
        opMethod.invoke(windowed, minResource6, preferredResource6);
        DataStreamSink<Long> sink = windowed.print();
        sinkMethod.invoke(sink, minResource7, preferredResource7);
        Assert.assertEquals(minResource1, env.getStreamGraph().getStreamNode(source1.getId()).getMinResources());
        Assert.assertEquals(preferredResource1, env.getStreamGraph().getStreamNode(source1.getId()).getPreferredResources());
        Assert.assertEquals(minResource2, env.getStreamGraph().getStreamNode(map1.getId()).getMinResources());
        Assert.assertEquals(preferredResource2, env.getStreamGraph().getStreamNode(map1.getId()).getPreferredResources());
        Assert.assertEquals(minResource3, env.getStreamGraph().getStreamNode(source2.getId()).getMinResources());
        Assert.assertEquals(preferredResource3, env.getStreamGraph().getStreamNode(source2.getId()).getPreferredResources());
        Assert.assertEquals(minResource4, env.getStreamGraph().getStreamNode(map2.getId()).getMinResources());
        Assert.assertEquals(preferredResource4, env.getStreamGraph().getStreamNode(map2.getId()).getPreferredResources());
        Assert.assertEquals(minResource5, env.getStreamGraph().getStreamNode(connected.getId()).getMinResources());
        Assert.assertEquals(preferredResource5, env.getStreamGraph().getStreamNode(connected.getId()).getPreferredResources());
        Assert.assertEquals(minResource6, env.getStreamGraph().getStreamNode(windowed.getId()).getMinResources());
        Assert.assertEquals(preferredResource6, env.getStreamGraph().getStreamNode(windowed.getId()).getPreferredResources());
        Assert.assertEquals(minResource7, env.getStreamGraph().getStreamNode(sink.getTransformation().getId()).getMinResources());
        Assert.assertEquals(preferredResource7, env.getStreamGraph().getStreamNode(sink.getTransformation().getId()).getPreferredResources());
    }

    @Test
    public void testTypeInfo() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Long> src1 = env.generateSequence(0, 0);
        Assert.assertEquals(TypeExtractor.getForClass(Long.class), src1.getType());
        DataStream<Tuple2<Integer, String>> map = src1.map(new org.apache.flink.api.common.functions.MapFunction<Long, Tuple2<Integer, String>>() {
            @Override
            public Tuple2<Integer, String> map(Long value) throws Exception {
                return null;
            }
        });
        Assert.assertEquals(TypeExtractor.getForObject(new Tuple2(0, "")), map.getType());
        DataStream<String> window = map.windowAll(GlobalWindows.create()).trigger(PurgingTrigger.of(CountTrigger.of(5))).apply(new org.apache.flink.streaming.api.functions.windowing.AllWindowFunction<Tuple2<Integer, String>, String, GlobalWindow>() {
            @Override
            public void apply(GlobalWindow window, Iterable<Tuple2<Integer, String>> values, Collector<String> out) throws Exception {
            }
        });
        Assert.assertEquals(TypeExtractor.getForClass(String.class), window.getType());
        DataStream<DataStreamTest.CustomPOJO> flatten = window.windowAll(GlobalWindows.create()).trigger(PurgingTrigger.of(CountTrigger.of(5))).fold(new DataStreamTest.CustomPOJO(), new org.apache.flink.api.common.functions.FoldFunction<String, DataStreamTest.CustomPOJO>() {
            private static final long serialVersionUID = 1L;

            @Override
            public DataStreamTest.CustomPOJO fold(DataStreamTest.CustomPOJO accumulator, String value) throws Exception {
                return null;
            }
        });
        Assert.assertEquals(TypeExtractor.getForClass(DataStreamTest.CustomPOJO.class), flatten.getType());
    }

    /**
     * Verify that a {@link KeyedStream#process(ProcessFunction)} call is correctly translated to an operator.
     */
    @Test
    @Deprecated
    public void testKeyedStreamProcessTranslation() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Long> src = env.generateSequence(0, 0);
        ProcessFunction<Long, Integer> processFunction = new ProcessFunction<Long, Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void processElement(Long value, Context ctx, Collector<Integer> out) throws Exception {
                // Do nothing
            }

            @Override
            public void onTimer(long timestamp, OnTimerContext ctx, Collector<Integer> out) throws Exception {
                // Do nothing
            }
        };
        DataStream<Integer> processed = src.keyBy(new DataStreamTest.IdentityKeySelector<Long>()).process(processFunction);
        processed.addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink<Integer>());
        Assert.assertEquals(processFunction, DataStreamTest.getFunctionForDataStream(processed));
        Assert.assertTrue(((DataStreamTest.getOperatorForDataStream(processed)) instanceof LegacyKeyedProcessOperator));
    }

    /**
     * Verify that a {@link KeyedStream#process(KeyedProcessFunction)} call is correctly translated to an operator.
     */
    @Test
    public void testKeyedStreamKeyedProcessTranslation() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Long> src = env.generateSequence(0, 0);
        KeyedProcessFunction<Long, Long, Integer> keyedProcessFunction = new KeyedProcessFunction<Long, Long, Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void processElement(Long value, Context ctx, Collector<Integer> out) throws Exception {
                // Do nothing
            }

            @Override
            public void onTimer(long timestamp, OnTimerContext ctx, Collector<Integer> out) throws Exception {
                // Do nothing
            }
        };
        DataStream<Integer> processed = src.keyBy(new DataStreamTest.IdentityKeySelector<Long>()).process(keyedProcessFunction);
        processed.addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink<Integer>());
        Assert.assertEquals(keyedProcessFunction, DataStreamTest.getFunctionForDataStream(processed));
        Assert.assertTrue(((DataStreamTest.getOperatorForDataStream(processed)) instanceof KeyedProcessOperator));
    }

    /**
     * Verify that a {@link DataStream#process(ProcessFunction)} call is correctly translated to an operator.
     */
    @Test
    public void testProcessTranslation() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Long> src = env.generateSequence(0, 0);
        ProcessFunction<Long, Integer> processFunction = new ProcessFunction<Long, Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void processElement(Long value, Context ctx, Collector<Integer> out) throws Exception {
                // Do nothing
            }

            @Override
            public void onTimer(long timestamp, OnTimerContext ctx, Collector<Integer> out) throws Exception {
                // Do nothing
            }
        };
        DataStream<Integer> processed = src.process(processFunction);
        processed.addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink<Integer>());
        Assert.assertEquals(processFunction, DataStreamTest.getFunctionForDataStream(processed));
        Assert.assertTrue(((DataStreamTest.getOperatorForDataStream(processed)) instanceof ProcessOperator));
    }

    /**
     * Tests that with a {@link KeyedStream} we have to provide a {@link KeyedBroadcastProcessFunction}.
     */
    @Test
    public void testFailedTranslationOnKeyed() {
        final MapStateDescriptor<Long, String> descriptor = new MapStateDescriptor("broadcast", BasicTypeInfo.LONG_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final DataStream<Long> srcOne = env.generateSequence(0L, 5L).assignTimestampsAndWatermarks(new DataStreamTest.CustomWmEmitter<Long>() {
            @Override
            public long extractTimestamp(Long element, long previousElementTimestamp) {
                return element;
            }
        }).keyBy(((KeySelector<Long, Long>) (( value) -> value)));
        final DataStream<String> srcTwo = env.fromElements("Test:0", "Test:1", "Test:2", "Test:3", "Test:4", "Test:5").assignTimestampsAndWatermarks(new DataStreamTest.CustomWmEmitter<String>() {
            @Override
            public long extractTimestamp(String element, long previousElementTimestamp) {
                return Long.parseLong(element.split(":")[1]);
            }
        });
        BroadcastStream<String> broadcast = srcTwo.broadcast(descriptor);
        BroadcastConnectedStream<Long, String> bcStream = srcOne.connect(broadcast);
        expectedException.expect(IllegalArgumentException.class);
        bcStream.process(new org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction<Long, String, String>() {
            @Override
            public void processBroadcastElement(String value, Context ctx, Collector<String> out) throws Exception {
                // do nothing
            }

            @Override
            public void processElement(Long value, ReadOnlyContext ctx, Collector<String> out) throws Exception {
                // do nothing
            }
        });
    }

    /**
     * Tests that with a non-keyed stream we have to provide a {@link BroadcastProcessFunction}.
     */
    @Test
    public void testFailedTranslationOnNonKeyed() {
        final MapStateDescriptor<Long, String> descriptor = new MapStateDescriptor("broadcast", BasicTypeInfo.LONG_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final DataStream<Long> srcOne = env.generateSequence(0L, 5L).assignTimestampsAndWatermarks(new DataStreamTest.CustomWmEmitter<Long>() {
            @Override
            public long extractTimestamp(Long element, long previousElementTimestamp) {
                return element;
            }
        });
        final DataStream<String> srcTwo = env.fromElements("Test:0", "Test:1", "Test:2", "Test:3", "Test:4", "Test:5").assignTimestampsAndWatermarks(new DataStreamTest.CustomWmEmitter<String>() {
            @Override
            public long extractTimestamp(String element, long previousElementTimestamp) {
                return Long.parseLong(element.split(":")[1]);
            }
        });
        BroadcastStream<String> broadcast = srcTwo.broadcast(descriptor);
        BroadcastConnectedStream<Long, String> bcStream = srcOne.connect(broadcast);
        expectedException.expect(IllegalArgumentException.class);
        bcStream.process(new org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction<String, Long, String, String>() {
            @Override
            public void processBroadcastElement(String value, Context ctx, Collector<String> out) throws Exception {
                // do nothing
            }

            @Override
            public void processElement(Long value, ReadOnlyContext ctx, Collector<String> out) throws Exception {
                // do nothing
            }
        });
    }

    private abstract static class CustomWmEmitter<T> implements AssignerWithPunctuatedWatermarks<T> {
        @Nullable
        @Override
        public Watermark checkAndGetNextWatermark(T lastElement, long extractedTimestamp) {
            return new Watermark(extractedTimestamp);
        }
    }

    @Test
    public void operatorTest() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Long> src = env.generateSequence(0, 0);
        org.apache.flink.api.common.functions.MapFunction<Long, Integer> mapFunction = new org.apache.flink.api.common.functions.MapFunction<Long, Integer>() {
            @Override
            public Integer map(Long value) throws Exception {
                return null;
            }
        };
        DataStream<Integer> map = src.map(mapFunction);
        map.addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink<Integer>());
        Assert.assertEquals(mapFunction, DataStreamTest.getFunctionForDataStream(map));
        FlatMapFunction<Long, Integer> flatMapFunction = new FlatMapFunction<Long, Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void flatMap(Long value, Collector<Integer> out) throws Exception {
            }
        };
        DataStream<Integer> flatMap = src.flatMap(flatMapFunction);
        flatMap.addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink<Integer>());
        Assert.assertEquals(flatMapFunction, DataStreamTest.getFunctionForDataStream(flatMap));
        FilterFunction<Integer> filterFunction = new FilterFunction<Integer>() {
            @Override
            public boolean filter(Integer value) throws Exception {
                return false;
            }
        };
        DataStream<Integer> unionFilter = map.union(flatMap).filter(filterFunction);
        unionFilter.addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink<Integer>());
        Assert.assertEquals(filterFunction, DataStreamTest.getFunctionForDataStream(unionFilter));
        try {
            env.getStreamGraph().getStreamEdges(map.getId(), unionFilter.getId());
        } catch (RuntimeException e) {
            Assert.fail(e.getMessage());
        }
        try {
            env.getStreamGraph().getStreamEdges(flatMap.getId(), unionFilter.getId());
        } catch (RuntimeException e) {
            Assert.fail(e.getMessage());
        }
        OutputSelector<Integer> outputSelector = new DataStreamTest.DummyOutputSelector();
        SplitStream<Integer> split = unionFilter.split(outputSelector);
        split.select("dummy").addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink<Integer>());
        List<OutputSelector<?>> outputSelectors = env.getStreamGraph().getStreamNode(unionFilter.getId()).getOutputSelectors();
        Assert.assertEquals(1, outputSelectors.size());
        Assert.assertEquals(outputSelector, outputSelectors.get(0));
        DataStream<Integer> select = split.select("a");
        DataStreamSink<Integer> sink = select.print();
        StreamEdge splitEdge = env.getStreamGraph().getStreamEdges(unionFilter.getId(), sink.getTransformation().getId()).get(0);
        Assert.assertEquals("a", splitEdge.getSelectedNames().get(0));
        DataStreamSink<Integer> sinkWithIdentifier = select.print("identifier");
        StreamEdge newSplitEdge = env.getStreamGraph().getStreamEdges(unionFilter.getId(), sinkWithIdentifier.getTransformation().getId()).get(0);
        Assert.assertEquals("a", newSplitEdge.getSelectedNames().get(0));
        ConnectedStreams<Integer, Integer> connect = map.connect(flatMap);
        CoMapFunction<Integer, Integer, String> coMapper = new CoMapFunction<Integer, Integer, String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String map1(Integer value) {
                return null;
            }

            @Override
            public String map2(Integer value) {
                return null;
            }
        };
        DataStream<String> coMap = connect.map(coMapper);
        coMap.addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink<String>());
        Assert.assertEquals(coMapper, DataStreamTest.getFunctionForDataStream(coMap));
        try {
            env.getStreamGraph().getStreamEdges(map.getId(), coMap.getId());
        } catch (RuntimeException e) {
            Assert.fail(e.getMessage());
        }
        try {
            env.getStreamGraph().getStreamEdges(flatMap.getId(), coMap.getId());
        } catch (RuntimeException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void sinkKeyTest() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSink<Long> sink = env.generateSequence(1, 100).print();
        Assert.assertTrue(((env.getStreamGraph().getStreamNode(sink.getTransformation().getId()).getStatePartitioner1()) == null));
        Assert.assertTrue(((env.getStreamGraph().getStreamNode(sink.getTransformation().getId()).getInEdges().get(0).getPartitioner()) instanceof ForwardPartitioner));
        KeySelector<Long, Long> key1 = new KeySelector<Long, Long>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Long getKey(Long value) throws Exception {
                return ((long) (0));
            }
        };
        DataStreamSink<Long> sink2 = env.generateSequence(1, 100).keyBy(key1).print();
        Assert.assertNotNull(env.getStreamGraph().getStreamNode(sink2.getTransformation().getId()).getStatePartitioner1());
        Assert.assertNotNull(env.getStreamGraph().getStreamNode(sink2.getTransformation().getId()).getStateKeySerializer());
        Assert.assertNotNull(env.getStreamGraph().getStreamNode(sink2.getTransformation().getId()).getStateKeySerializer());
        Assert.assertEquals(key1, env.getStreamGraph().getStreamNode(sink2.getTransformation().getId()).getStatePartitioner1());
        Assert.assertTrue(((env.getStreamGraph().getStreamNode(sink2.getTransformation().getId()).getInEdges().get(0).getPartitioner()) instanceof KeyGroupStreamPartitioner));
        KeySelector<Long, Long> key2 = new KeySelector<Long, Long>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Long getKey(Long value) throws Exception {
                return ((long) (0));
            }
        };
        DataStreamSink<Long> sink3 = env.generateSequence(1, 100).keyBy(key2).print();
        Assert.assertTrue(((env.getStreamGraph().getStreamNode(sink3.getTransformation().getId()).getStatePartitioner1()) != null));
        Assert.assertEquals(key2, env.getStreamGraph().getStreamNode(sink3.getTransformation().getId()).getStatePartitioner1());
        Assert.assertTrue(((env.getStreamGraph().getStreamNode(sink3.getTransformation().getId()).getInEdges().get(0).getPartitioner()) instanceof KeyGroupStreamPartitioner));
    }

    @Test
    public void testChannelSelectors() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Long> src = env.generateSequence(0, 0);
        DataStream<Long> broadcast = src.broadcast();
        DataStreamSink<Long> broadcastSink = broadcast.print();
        StreamPartitioner<?> broadcastPartitioner = env.getStreamGraph().getStreamEdges(src.getId(), broadcastSink.getTransformation().getId()).get(0).getPartitioner();
        Assert.assertTrue((broadcastPartitioner instanceof BroadcastPartitioner));
        DataStream<Long> shuffle = src.shuffle();
        DataStreamSink<Long> shuffleSink = shuffle.print();
        StreamPartitioner<?> shufflePartitioner = env.getStreamGraph().getStreamEdges(src.getId(), shuffleSink.getTransformation().getId()).get(0).getPartitioner();
        Assert.assertTrue((shufflePartitioner instanceof ShufflePartitioner));
        DataStream<Long> forward = src.forward();
        DataStreamSink<Long> forwardSink = forward.print();
        StreamPartitioner<?> forwardPartitioner = env.getStreamGraph().getStreamEdges(src.getId(), forwardSink.getTransformation().getId()).get(0).getPartitioner();
        Assert.assertTrue((forwardPartitioner instanceof ForwardPartitioner));
        DataStream<Long> rebalance = src.rebalance();
        DataStreamSink<Long> rebalanceSink = rebalance.print();
        StreamPartitioner<?> rebalancePartitioner = env.getStreamGraph().getStreamEdges(src.getId(), rebalanceSink.getTransformation().getId()).get(0).getPartitioner();
        Assert.assertTrue((rebalancePartitioner instanceof RebalancePartitioner));
        DataStream<Long> global = src.global();
        DataStreamSink<Long> globalSink = global.print();
        StreamPartitioner<?> globalPartitioner = env.getStreamGraph().getStreamEdges(src.getId(), globalSink.getTransformation().getId()).get(0).getPartitioner();
        Assert.assertTrue((globalPartitioner instanceof GlobalPartitioner));
    }

    // ///////////////////////////////////////////////////////////
    // Split testing
    // ///////////////////////////////////////////////////////////
    @Test
    public void testConsecutiveSplitRejection() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Integer> src = env.fromElements(0, 0);
        OutputSelector<Integer> outputSelector = new DataStreamTest.DummyOutputSelector();
        src.split(outputSelector).split(outputSelector).addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Consecutive multiple splits are not supported. Splits are deprecated. Please use side-outputs.");
        env.getStreamGraph();
    }

    @Test
    public void testSplitAfterSideOutputRejection() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Integer> src = env.fromElements(0, 0);
        OutputTag<Integer> outputTag = new OutputTag<Integer>("dummy") {};
        OutputSelector<Integer> outputSelector = new DataStreamTest.DummyOutputSelector();
        src.getSideOutput(outputTag).split(outputSelector).addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Split after side-outputs are not supported. Splits are deprecated. Please use side-outputs.");
        env.getStreamGraph();
    }

    @Test
    public void testSelectBetweenConsecutiveSplitRejection() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Integer> src = env.fromElements(0, 0);
        OutputSelector<Integer> outputSelector = new DataStreamTest.DummyOutputSelector();
        src.split(outputSelector).select("dummy").split(outputSelector).addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Consecutive multiple splits are not supported. Splits are deprecated. Please use side-outputs.");
        env.getStreamGraph();
    }

    @Test
    public void testUnionBetweenConsecutiveSplitRejection() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Integer> src = env.fromElements(0, 0);
        OutputSelector<Integer> outputSelector = new DataStreamTest.DummyOutputSelector();
        src.split(outputSelector).select("dummy").union(src.map(( x) -> x)).split(outputSelector).addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Consecutive multiple splits are not supported. Splits are deprecated. Please use side-outputs.");
        env.getStreamGraph();
    }

    @Test
    public void testKeybyBetweenConsecutiveSplitRejection() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Integer> src = env.fromElements(0, 0);
        OutputSelector<Integer> outputSelector = new DataStreamTest.DummyOutputSelector();
        src.split(outputSelector).select("dummy").keyBy(( x) -> x).split(outputSelector).addSink(new org.apache.flink.streaming.api.functions.sink.DiscardingSink());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Consecutive multiple splits are not supported. Splits are deprecated. Please use side-outputs.");
        env.getStreamGraph();
    }

    // ///////////////////////////////////////////////////////////
    // KeyBy testing
    // ///////////////////////////////////////////////////////////
    @Test
    public void testPrimitiveArrayKeyRejection() {
        KeySelector<Tuple2<Integer[], String>, int[]> keySelector = new KeySelector<Tuple2<Integer[], String>, int[]>() {
            @Override
            public int[] getKey(Tuple2<Integer[], String> value) throws Exception {
                int[] ks = new int[value.f0.length];
                for (int i = 0; i < (ks.length); i++) {
                    ks[i] = value.f0[i];
                }
                return ks;
            }
        };
        testKeyRejection(keySelector, INT_PRIMITIVE_ARRAY_TYPE_INFO);
    }

    @Test
    public void testBasicArrayKeyRejection() {
        KeySelector<Tuple2<Integer[], String>, Integer[]> keySelector = new KeySelector<Tuple2<Integer[], String>, Integer[]>() {
            @Override
            public Integer[] getKey(Tuple2<Integer[], String> value) throws Exception {
                return value.f0;
            }
        };
        testKeyRejection(keySelector, INT_ARRAY_TYPE_INFO);
    }

    @Test
    public void testObjectArrayKeyRejection() {
        KeySelector<Tuple2<Integer[], String>, Object[]> keySelector = new KeySelector<Tuple2<Integer[], String>, Object[]>() {
            @Override
            public Object[] getKey(Tuple2<Integer[], String> value) throws Exception {
                Object[] ks = new Object[value.f0.length];
                for (int i = 0; i < (ks.length); i++) {
                    ks[i] = new Object();
                }
                return ks;
            }
        };
        ObjectArrayTypeInfo<Object[], Object> keyTypeInfo = ObjectArrayTypeInfo.getInfoFor(Object[].class, new org.apache.flink.api.java.typeutils.GenericTypeInfo(Object.class));
        testKeyRejection(keySelector, keyTypeInfo);
    }

    // //////////////			Composite Key Tests : POJOs			////////////////
    @Test
    public void testPOJOWithNestedArrayNoHashCodeKeyRejection() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<DataStreamTest.POJOWithHashCode> input = env.fromElements(new DataStreamTest.POJOWithHashCode(new int[]{ 1, 2 }));
        TypeInformation<?> expectedTypeInfo = new org.apache.flink.api.java.typeutils.TupleTypeInfo<org.apache.flink.api.java.tuple.Tuple1<int[]>>(PrimitiveArrayTypeInfo.INT_PRIMITIVE_ARRAY_TYPE_INFO);
        // adjust the rule
        expectedException.expect(InvalidProgramException.class);
        expectedException.expectMessage(new StringStartsWith((("Type " + expectedTypeInfo) + " cannot be used as key.")));
        input.keyBy("id");
    }

    @Test
    public void testPOJOWithNestedArrayAndHashCodeWorkAround() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<DataStreamTest.POJOWithHashCode> input = env.fromElements(new DataStreamTest.POJOWithHashCode(new int[]{ 1, 2 }));
        input.keyBy(new KeySelector<DataStreamTest.POJOWithHashCode, DataStreamTest.POJOWithHashCode>() {
            @Override
            public DataStreamTest.POJOWithHashCode getKey(DataStreamTest.POJOWithHashCode value) throws Exception {
                return value;
            }
        }).addSink(new org.apache.flink.streaming.api.functions.sink.SinkFunction<DataStreamTest.POJOWithHashCode>() {
            @Override
            public void invoke(DataStreamTest.POJOWithHashCode value) throws Exception {
                Assert.assertEquals(value.getId(), new int[]{ 1, 2 });
            }
        });
    }

    @Test
    public void testPOJOnoHashCodeKeyRejection() {
        KeySelector<DataStreamTest.POJOWithoutHashCode, DataStreamTest.POJOWithoutHashCode> keySelector = new KeySelector<DataStreamTest.POJOWithoutHashCode, DataStreamTest.POJOWithoutHashCode>() {
            @Override
            public DataStreamTest.POJOWithoutHashCode getKey(DataStreamTest.POJOWithoutHashCode value) throws Exception {
                return value;
            }
        };
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<DataStreamTest.POJOWithoutHashCode> input = env.fromElements(new DataStreamTest.POJOWithoutHashCode(new int[]{ 1, 2 }));
        // adjust the rule
        expectedException.expect(InvalidProgramException.class);
        input.keyBy(keySelector);
    }

    // //////////////			Composite Key Tests : Tuples			////////////////
    @Test
    public void testTupleNestedArrayKeyRejection() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Tuple2<Integer[], String>> input = env.fromElements(new Tuple2(new Integer[]{ 1, 2 }, "test-test"));
        TypeInformation<?> expectedTypeInfo = new org.apache.flink.api.java.typeutils.TupleTypeInfo<Tuple2<Integer[], String>>(BasicArrayTypeInfo.INT_ARRAY_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO);
        // adjust the rule
        expectedException.expect(InvalidProgramException.class);
        expectedException.expectMessage(new StringStartsWith((("Type " + expectedTypeInfo) + " cannot be used as key.")));
        input.keyBy(new KeySelector<Tuple2<Integer[], String>, Tuple2<Integer[], String>>() {
            @Override
            public Tuple2<Integer[], String> getKey(Tuple2<Integer[], String> value) throws Exception {
                return value;
            }
        });
    }

    @Test
    public void testPrimitiveKeyAcceptance() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setMaxParallelism(1);
        DataStream<Integer> input = env.fromElements(new Integer(10000));
        KeyedStream<Integer, Object> keyedStream = input.keyBy(new KeySelector<Integer, Object>() {
            @Override
            public Object getKey(Integer value) throws Exception {
                return value;
            }
        });
        keyedStream.addSink(new org.apache.flink.streaming.api.functions.sink.SinkFunction<Integer>() {
            @Override
            public void invoke(Integer value) throws Exception {
                Assert.assertEquals(10000L, ((long) (value)));
            }
        });
    }

    /**
     * POJO without hashCode.
     */
    public static class POJOWithoutHashCode {
        private int[] id;

        public POJOWithoutHashCode() {
        }

        public POJOWithoutHashCode(int[] id) {
            this.id = id;
        }

        public int[] getId() {
            return id;
        }

        public void setId(int[] id) {
            this.id = id;
        }
    }

    /**
     * POJO with hashCode.
     */
    public static class POJOWithHashCode extends DataStreamTest.POJOWithoutHashCode {
        public POJOWithHashCode() {
        }

        public POJOWithHashCode(int[] id) {
            super(id);
        }

        @Override
        public int hashCode() {
            int hash = 31;
            for (int i : getId()) {
                hash = (37 * hash) + i;
            }
            return hash;
        }
    }

    private static class FirstSelector implements KeySelector<Tuple2<Long, Long>, Long> {
        private static final long serialVersionUID = 1L;

        @Override
        public Long getKey(Tuple2<Long, Long> value) throws Exception {
            return value.f0;
        }
    }

    private static class IdentityKeySelector<T> implements KeySelector<T, T> {
        private static final long serialVersionUID = 1L;

        @Override
        public T getKey(T value) throws Exception {
            return value;
        }
    }

    private static class CustomPOJO {
        private String s;

        private int i;

        public CustomPOJO() {
        }

        public void setS(String s) {
            this.s = s;
        }

        public void setI(int i) {
            this.i = i;
        }

        public String getS() {
            return s;
        }

        public int getI() {
            return i;
        }
    }

    private class DummyOutputSelector<Integer> implements OutputSelector<Integer> {
        @Override
        public Iterable<String> select(Integer value) {
            return null;
        }
    }
}

