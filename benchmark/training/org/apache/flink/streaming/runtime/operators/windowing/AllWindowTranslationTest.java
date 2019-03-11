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
package org.apache.flink.streaming.runtime.operators.windowing;


import BasicTypeInfo.STRING_TYPE_INFO;
import TimeCharacteristic.IngestionTime;
import TimeCharacteristic.ProcessingTime;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichAggregateFunction;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.common.state.FoldingStateDescriptor;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.operators.OneInputStreamOperator;
import org.apache.flink.streaming.api.transformations.OneInputTransformation;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.evictors.CountEvictor;
import org.apache.flink.streaming.api.windowing.evictors.TimeEvictor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.ProcessingTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction.<init>;


/**
 * These tests verify that the api calls on {@link AllWindowedStream} instantiate the correct
 * window operator.
 *
 * <p>We also create a test harness and push one element into the operator to verify
 * that we get some output.
 */
@SuppressWarnings("serial")
public class AllWindowTranslationTest {
    // ------------------------------------------------------------------------
    // rich function tests
    // ------------------------------------------------------------------------
    /**
     * .reduce() does not support RichReduceFunction, since the reduce function is used internally
     * in a {@code ReducingState}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testReduceWithRichReducerFails() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        env.setStreamTimeCharacteristic(ProcessingTime);
        source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).reduce(new org.apache.flink.api.common.functions.RichReduceFunction<Tuple2<String, Integer>>() {
            private static final long serialVersionUID = -6448847205314995812L;

            @Override
            public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                return null;
            }
        });
        Assert.fail("exception was not thrown");
    }

    /**
     * .aggregate() does not support RichAggregateFunction, since the AggregateFunction is used internally
     * in an {@code AggregatingState}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAggregateWithRichFunctionFails() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        env.setStreamTimeCharacteristic(ProcessingTime);
        source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).aggregate(new AllWindowTranslationTest.DummyRichAggregationFunction<Tuple2<String, Integer>>());
        Assert.fail("exception was not thrown");
    }

    /**
     * .fold() does not support RichFoldFunction, since the fold function is used internally
     * in a {@code FoldingState}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testFoldWithRichFolderFails() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        env.setStreamTimeCharacteristic(ProcessingTime);
        source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).fold(new Tuple2("", 0), new org.apache.flink.api.common.functions.RichFoldFunction<Tuple2<String, Integer>, Tuple2<String, Integer>>() {
            private static final long serialVersionUID = -6448847205314995812L;

            @Override
            public Tuple2<String, Integer> fold(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                return null;
            }
        });
        Assert.fail("exception was not thrown");
    }

    // ------------------------------------------------------------------------
    // Merging Windows Support
    // ------------------------------------------------------------------------
    @Test
    public void testSessionWithFoldFails() throws Exception {
        // verify that fold does not work with merging windows
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        AllWindowedStream<String, TimeWindow> windowedStream = env.fromElements("Hello", "Ciao").windowAll(EventTimeSessionWindows.withGap(Time.seconds(5)));
        try {
            windowedStream.fold("", new org.apache.flink.api.common.functions.FoldFunction<String, String>() {
                private static final long serialVersionUID = -4567902917104921706L;

                @Override
                public String fold(String accumulator, String value) throws Exception {
                    return accumulator;
                }
            });
        } catch (UnsupportedOperationException e) {
            // expected
            // use a catch to ensure that the exception is thrown by the fold
            return;
        }
        Assert.fail("The fold call should fail.");
    }

    @Test
    public void testMergingAssignerWithNonMergingTriggerFails() throws Exception {
        // verify that we check for trigger compatibility
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        AllWindowedStream<String, TimeWindow> windowedStream = env.fromElements("Hello", "Ciao").windowAll(EventTimeSessionWindows.withGap(Time.seconds(5)));
        try {
            windowedStream.trigger(new org.apache.flink.streaming.api.windowing.triggers.Trigger<String, TimeWindow>() {
                private static final long serialVersionUID = 6558046711583024443L;

                @Override
                public TriggerResult onElement(String element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
                    return null;
                }

                @Override
                public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
                    return null;
                }

                @Override
                public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
                    return null;
                }

                @Override
                public boolean canMerge() {
                    return false;
                }

                @Override
                public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
                }
            });
        } catch (UnsupportedOperationException e) {
            // expected
            // use a catch to ensure that the exception is thrown by the fold
            return;
        }
        Assert.fail("The trigger call should fail.");
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testMergingWindowsWithEvictor() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple3<String, String, Integer>> window1 = source.windowAll(EventTimeSessionWindows.withGap(Time.seconds(5))).evictor(CountEvictor.of(5)).process(new AllWindowTranslationTest.TestProcessAllWindowFunction());
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof EventTimeSessionWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    // ------------------------------------------------------------------------
    // reduce() translation tests
    // ------------------------------------------------------------------------
    @Test
    @SuppressWarnings("rawtypes")
    public void testReduceEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).reduce(new AllWindowTranslationTest.DummyReducer());
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ReducingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testReduceProcessingTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(SlidingProcessingTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).reduce(new AllWindowTranslationTest.DummyReducer());
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof ProcessingTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingProcessingTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ReducingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testReduceWithWindowFunctionEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        AllWindowTranslationTest.DummyReducer reducer = new AllWindowTranslationTest.DummyReducer();
        DataStream<Tuple3<String, String, Integer>> window = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).reduce(reducer, new AllWindowFunction<Tuple2<String, Integer>, Tuple3<String, String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void apply(TimeWindow window, Iterable<Tuple2<String, Integer>> values, Collector<Tuple3<String, String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(new Tuple3(in.f0, in.f0, in.f1));
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ReducingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(operator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testReduceWithWindowFunctionProcessingTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple3<String, String, Integer>> window = source.windowAll(TumblingProcessingTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).reduce(new AllWindowTranslationTest.DummyReducer(), new AllWindowFunction<Tuple2<String, Integer>, Tuple3<String, String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void apply(TimeWindow window, Iterable<Tuple2<String, Integer>> values, Collector<Tuple3<String, String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(new Tuple3(in.f0, in.f0, in.f1));
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof ProcessingTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingProcessingTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ReducingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(operator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testReduceWithProcessWindowFunctionEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        AllWindowTranslationTest.DummyReducer reducer = new AllWindowTranslationTest.DummyReducer();
        DataStream<Tuple3<String, String, Integer>> window = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).reduce(reducer, new org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple3<String, String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void process(Context ctx, Iterable<Tuple2<String, Integer>> values, Collector<Tuple3<String, String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(new Tuple3(in.f0, in.f0, in.f1));
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ReducingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(operator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testReduceWithProcessWindowFunctionProcessingTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple3<String, String, Integer>> window = source.windowAll(TumblingProcessingTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).reduce(new AllWindowTranslationTest.DummyReducer(), new org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple3<String, String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void process(Context ctx, Iterable<Tuple2<String, Integer>> values, Collector<Tuple3<String, String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(new Tuple3(in.f0, in.f0, in.f1));
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof ProcessingTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingProcessingTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ReducingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(operator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testReduceWithEvictorAndProcessFunction() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        AllWindowTranslationTest.DummyReducer reducer = new AllWindowTranslationTest.DummyReducer();
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).evictor(CountEvictor.of(100)).reduce(reducer, new org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            @Override
            public void process(Context context, Iterable<Tuple2<String, Integer>> elements, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : elements) {
                    out.collect(in);
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof EvictingWindowOperator));
        EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?> winOperator = ((EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getEvictor()) instanceof CountEvictor));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    /**
     * Test for the deprecated .apply(Reducer, WindowFunction).
     */
    @Test
    @SuppressWarnings("rawtypes")
    public void testApplyWithPreReducerEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        AllWindowTranslationTest.DummyReducer reducer = new AllWindowTranslationTest.DummyReducer();
        DataStream<Tuple3<String, String, Integer>> window = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).apply(reducer, new AllWindowFunction<Tuple2<String, Integer>, Tuple3<String, String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void apply(TimeWindow window, Iterable<Tuple2<String, Integer>> values, Collector<Tuple3<String, String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(new Tuple3(in.f0, in.f0, in.f1));
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ReducingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(operator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    // ------------------------------------------------------------------------
    // aggregate() translation tests
    // ------------------------------------------------------------------------
    @Test
    public void testAggregateEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).aggregate(new AllWindowTranslationTest.DummyAggregationFunction());
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof AggregatingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    public void testAggregateProcessingTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(SlidingProcessingTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).aggregate(new AllWindowTranslationTest.DummyAggregationFunction());
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof ProcessingTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingProcessingTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof AggregatingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    public void testAggregateWithWindowFunctionEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple3<String, String, Integer>> window = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).aggregate(new AllWindowTranslationTest.DummyAggregationFunction(), new AllWindowTranslationTest.TestAllWindowFunction());
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof AggregatingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(operator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    public void testAggregateWithWindowFunctionProcessingTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple3<String, String, Integer>> window = source.windowAll(TumblingProcessingTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).aggregate(new AllWindowTranslationTest.DummyAggregationFunction(), new AllWindowTranslationTest.TestAllWindowFunction());
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof ProcessingTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingProcessingTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof AggregatingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(operator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    public void testAggregateWithEvictor() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).evictor(CountEvictor.of(100)).aggregate(new AllWindowTranslationTest.DummyAggregationFunction());
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    public void testAggregateWithEvictorAndProcessFunction() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).evictor(CountEvictor.of(100)).aggregate(new AllWindowTranslationTest.DummyAggregationFunction(), new org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            @Override
            public void process(Context context, Iterable<Tuple2<String, Integer>> elements, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : elements) {
                    out.collect(in);
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    // ------------------------------------------------------------------------
    // process() translation tests
    // ------------------------------------------------------------------------
    @Test
    @SuppressWarnings("rawtypes")
    public void testProcessEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).process(new org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void process(Context ctx, Iterable<Tuple2<String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(in);
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testProcessProcessingTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(TumblingProcessingTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).process(new org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void process(Context ctx, Iterable<Tuple2<String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(in);
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof ProcessingTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingProcessingTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testProcessWithEvictor() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).trigger(CountTrigger.of(1)).evictor(TimeEvictor.of(Time.of(100, TimeUnit.MILLISECONDS))).process(new org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void process(Context ctx, Iterable<Tuple2<String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(in);
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof EvictingWindowOperator));
        EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?> winOperator = ((EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof CountTrigger));
        Assert.assertTrue(((winOperator.getEvictor()) instanceof TimeEvictor));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testProcessWithCustomTrigger() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).trigger(CountTrigger.of(1)).process(new org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void process(Context ctx, Iterable<Tuple2<String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(in);
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof CountTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    // ------------------------------------------------------------------------
    // fold() translation tests
    // ------------------------------------------------------------------------
    @Test
    @SuppressWarnings("rawtypes")
    public void testFoldEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple3<String, String, Integer>> window1 = source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).fold(new Tuple3("", "", 1), new AllWindowTranslationTest.DummyFolder());
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof FoldingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testFoldProcessingTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple3<String, String, Integer>> window = source.windowAll(SlidingProcessingTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).fold(new Tuple3("", "", 0), new AllWindowTranslationTest.DummyFolder());
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof ProcessingTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingProcessingTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof FoldingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testFoldWithWindowFunctionEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).fold(new Tuple3("", "", 0), new AllWindowTranslationTest.DummyFolder(), new AllWindowFunction<Tuple3<String, String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void apply(TimeWindow window, Iterable<Tuple3<String, String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple3<String, String, Integer> in : values) {
                    out.collect(new Tuple2(in.f0, in.f2));
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof FoldingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testFoldWithWindowFunctionProcessingTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window = source.windowAll(TumblingProcessingTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).fold(new Tuple3("", "empty", 0), new AllWindowTranslationTest.DummyFolder(), new AllWindowFunction<Tuple3<String, String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void apply(TimeWindow window, Iterable<Tuple3<String, String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple3<String, String, Integer> in : values) {
                    out.collect(new Tuple2(in.f0, in.f2));
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof ProcessingTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingProcessingTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof FoldingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testFoldWithProcessAllWindowFunctionEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).fold(new Tuple3("", "", 0), new AllWindowTranslationTest.DummyFolder(), new org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple3<String, String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void process(Context ctx, Iterable<Tuple3<String, String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple3<String, String, Integer> in : values) {
                    out.collect(new Tuple2(in.f0, in.f2));
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof FoldingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testFoldWithProcessAllWindowFunctionProcessingTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window = source.windowAll(TumblingProcessingTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).fold(new Tuple3("", "empty", 0), new AllWindowTranslationTest.DummyFolder(), new org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple3<String, String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void process(Context ctx, Iterable<Tuple3<String, String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple3<String, String, Integer> in : values) {
                    out.collect(new Tuple2(in.f0, in.f2));
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof ProcessingTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingProcessingTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof FoldingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testFoldWithEvictorAndProcessFunction() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple3<String, String, Integer>> window1 = source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).evictor(CountEvictor.of(100)).fold(new Tuple3("", "", 1), new AllWindowTranslationTest.DummyFolder(), new org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple3<String, String, Integer>, Tuple3<String, String, Integer>, TimeWindow>() {
            @Override
            public void process(Context context, Iterable<Tuple3<String, String, Integer>> elements, Collector<Tuple3<String, String, Integer>> out) throws Exception {
                for (Tuple3<String, String, Integer> in : elements) {
                    out.collect(in);
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof EvictingWindowOperator));
        EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?> winOperator = ((EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getEvictor()) instanceof CountEvictor));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        winOperator.setOutputType(((TypeInformation) (window1.getType())), new ExecutionConfig());
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testApplyWithPreFolderEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple3<String, String, Integer>> window = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).apply(new Tuple3("", "", 0), new AllWindowTranslationTest.DummyFolder(), new AllWindowFunction<Tuple3<String, String, Integer>, Tuple3<String, String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void apply(TimeWindow window, Iterable<Tuple3<String, String, Integer>> values, Collector<Tuple3<String, String, Integer>> out) throws Exception {
                for (Tuple3<String, String, Integer> in : values) {
                    out.collect(new Tuple3(in.f0, in.f1, in.f2));
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof FoldingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    // ------------------------------------------------------------------------
    // apply() translation tests
    // ------------------------------------------------------------------------
    @Test
    @SuppressWarnings("rawtypes")
    public void testApplyEventTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).apply(new AllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void apply(TimeWindow window, Iterable<Tuple2<String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(in);
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testApplyProcessingTimeTime() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(TumblingProcessingTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).apply(new AllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void apply(TimeWindow window, Iterable<Tuple2<String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(in);
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof ProcessingTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingProcessingTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testReduceWithCustomTrigger() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        AllWindowTranslationTest.DummyReducer reducer = new AllWindowTranslationTest.DummyReducer();
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).trigger(CountTrigger.of(1)).reduce(reducer);
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof CountTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ReducingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testFoldWithCustomTrigger() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple3<String, String, Integer>> window1 = source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).trigger(CountTrigger.of(1)).fold(new Tuple3("", "", 1), new AllWindowTranslationTest.DummyFolder());
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof CountTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof FoldingStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testApplyWithCustomTrigger() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(ProcessingTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).trigger(CountTrigger.of(1)).apply(new AllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void apply(TimeWindow window, Iterable<Tuple2<String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(in);
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof WindowOperator));
        WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?> winOperator = ((WindowOperator<String, Tuple2<String, Integer>, ?, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof CountTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testReduceWithEvictor() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        AllWindowTranslationTest.DummyReducer reducer = new AllWindowTranslationTest.DummyReducer();
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).evictor(CountEvictor.of(100)).reduce(reducer);
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof EvictingWindowOperator));
        EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?> winOperator = ((EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingEventTimeWindows));
        Assert.assertTrue(((winOperator.getEvictor()) instanceof CountEvictor));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testFoldWithEvictor() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple3<String, String, Integer>> window1 = source.windowAll(SlidingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS), Time.of(100, TimeUnit.MILLISECONDS))).evictor(CountEvictor.of(100)).fold(new Tuple3("", "", 1), new AllWindowTranslationTest.DummyFolder());
        OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple3<String, String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple3<String, String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof EvictingWindowOperator));
        EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?> winOperator = ((EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof EventTimeTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof SlidingEventTimeWindows));
        Assert.assertTrue(((winOperator.getEvictor()) instanceof CountEvictor));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        winOperator.setOutputType(((TypeInformation) (window1.getType())), new ExecutionConfig());
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testApplyWithEvictor() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(IngestionTime);
        DataStream<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("hello", 1), Tuple2.of("hello", 2));
        DataStream<Tuple2<String, Integer>> window1 = source.windowAll(TumblingEventTimeWindows.of(Time.of(1, TimeUnit.SECONDS))).trigger(CountTrigger.of(1)).evictor(TimeEvictor.of(Time.of(100, TimeUnit.MILLISECONDS))).apply(new AllWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, TimeWindow>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void apply(TimeWindow window, Iterable<Tuple2<String, Integer>> values, Collector<Tuple2<String, Integer>> out) throws Exception {
                for (Tuple2<String, Integer> in : values) {
                    out.collect(in);
                }
            }
        });
        OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>> transform = ((OneInputTransformation<Tuple2<String, Integer>, Tuple2<String, Integer>>) (window1.getTransformation()));
        OneInputStreamOperator<Tuple2<String, Integer>, Tuple2<String, Integer>> operator = transform.getOperator();
        Assert.assertTrue((operator instanceof EvictingWindowOperator));
        EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?> winOperator = ((EvictingWindowOperator<String, Tuple2<String, Integer>, ?, ?>) (operator));
        Assert.assertTrue(((winOperator.getTrigger()) instanceof CountTrigger));
        Assert.assertTrue(((winOperator.getWindowAssigner()) instanceof TumblingEventTimeWindows));
        Assert.assertTrue(((winOperator.getEvictor()) instanceof TimeEvictor));
        Assert.assertTrue(((winOperator.getStateDescriptor()) instanceof ListStateDescriptor));
        AllWindowTranslationTest.processElementAndEnsureOutput(winOperator, winOperator.getKeySelector(), STRING_TYPE_INFO, new Tuple2("hello", 1));
    }

    // ------------------------------------------------------------------------
    // UDFs
    // ------------------------------------------------------------------------
    private static class DummyReducer implements ReduceFunction<Tuple2<String, Integer>> {
        private static final long serialVersionUID = 1L;

        @Override
        public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
            return value1;
        }
    }

    private static class DummyFolder implements org.apache.flink.api.common.functions.FoldFunction<Tuple2<String, Integer>, Tuple3<String, String, Integer>> {
        @Override
        public Tuple3<String, String, Integer> fold(Tuple3<String, String, Integer> accumulator, Tuple2<String, Integer> value) throws Exception {
            return accumulator;
        }
    }

    private static class DummyAggregationFunction implements AggregateFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, Tuple2<String, Integer>> {
        @Override
        public Tuple2<String, Integer> createAccumulator() {
            return new Tuple2("", 0);
        }

        @Override
        public Tuple2<String, Integer> add(Tuple2<String, Integer> value, Tuple2<String, Integer> accumulator) {
            accumulator.f0 = value.f0;
            accumulator.f1 = value.f1;
            return accumulator;
        }

        @Override
        public Tuple2<String, Integer> getResult(Tuple2<String, Integer> accumulator) {
            return accumulator;
        }

        @Override
        public Tuple2<String, Integer> merge(Tuple2<String, Integer> a, Tuple2<String, Integer> b) {
            return a;
        }
    }

    private static class DummyRichAggregationFunction<T> extends RichAggregateFunction<T, T, T> {
        @Override
        public T createAccumulator() {
            return null;
        }

        @Override
        public T add(T value, T accumulator) {
            return accumulator;
        }

        @Override
        public T getResult(T accumulator) {
            return accumulator;
        }

        @Override
        public T merge(T a, T b) {
            return a;
        }
    }

    private static class TestAllWindowFunction implements AllWindowFunction<Tuple2<String, Integer>, Tuple3<String, String, Integer>, TimeWindow> {
        @Override
        public void apply(TimeWindow window, Iterable<Tuple2<String, Integer>> values, Collector<Tuple3<String, String, Integer>> out) throws Exception {
            for (Tuple2<String, Integer> in : values) {
                out.collect(new Tuple3(in.f0, in.f0, in.f1));
            }
        }
    }

    private static class TestProcessAllWindowFunction extends org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction<Tuple2<String, Integer>, Tuple3<String, String, Integer>, TimeWindow> {
        @Override
        public void process(Context ctx, Iterable<Tuple2<String, Integer>> values, Collector<Tuple3<String, String, Integer>> out) throws Exception {
            for (Tuple2<String, Integer> in : values) {
                out.collect(new Tuple3(in.f0, in.f0, in.f1));
            }
        }
    }
}

