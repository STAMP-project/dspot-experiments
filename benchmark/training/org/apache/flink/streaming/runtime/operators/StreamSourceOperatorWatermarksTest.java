/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.runtime.operators;


import TimeCharacteristic.EventTime;
import TimeCharacteristic.IngestionTime;
import Watermark.MAX_WATERMARK;
import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.functions.StoppableFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.operators.StoppableStreamSource;
import org.apache.flink.streaming.api.operators.StreamSource;
import org.apache.flink.streaming.api.operators.StreamSourceContexts;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.runtime.streamrecord.StreamElement;
import org.apache.flink.streaming.runtime.streamstatus.StreamStatusMaintainer;
import org.apache.flink.streaming.runtime.tasks.TestProcessingTimeService;
import org.apache.flink.streaming.util.CollectorOutput;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link StreamSource} operators.
 */
@SuppressWarnings("serial")
public class StreamSourceOperatorWatermarksTest {
    @Test
    public void testEmitMaxWatermarkForFiniteSource() throws Exception {
        // regular stream source operator
        StreamSource<String, StreamSourceOperatorWatermarksTest.FiniteSource<String>> operator = new StreamSource(new StreamSourceOperatorWatermarksTest.FiniteSource<String>());
        final List<StreamElement> output = new ArrayList<>();
        StreamSourceOperatorWatermarksTest.setupSourceOperator(operator, EventTime, 0);
        operator.run(new Object(), Mockito.mock(StreamStatusMaintainer.class), new CollectorOutput<String>(output));
        Assert.assertEquals(1, output.size());
        Assert.assertEquals(MAX_WATERMARK, output.get(0));
    }

    @Test
    public void testNoMaxWatermarkOnImmediateCancel() throws Exception {
        final List<StreamElement> output = new ArrayList<>();
        // regular stream source operator
        final StreamSource<String, StreamSourceOperatorWatermarksTest.InfiniteSource<String>> operator = new StreamSource(new StreamSourceOperatorWatermarksTest.InfiniteSource<String>());
        StreamSourceOperatorWatermarksTest.setupSourceOperator(operator, EventTime, 0);
        operator.cancel();
        // run and exit
        operator.run(new Object(), Mockito.mock(StreamStatusMaintainer.class), new CollectorOutput<String>(output));
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testNoMaxWatermarkOnAsyncCancel() throws Exception {
        final List<StreamElement> output = new ArrayList<>();
        // regular stream source operator
        final StreamSource<String, StreamSourceOperatorWatermarksTest.InfiniteSource<String>> operator = new StreamSource(new StreamSourceOperatorWatermarksTest.InfiniteSource<String>());
        StreamSourceOperatorWatermarksTest.setupSourceOperator(operator, EventTime, 0);
        // trigger an async cancel in a bit
        new Thread("canceler") {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
                operator.cancel();
            }
        }.start();
        // run and wait to be canceled
        try {
            operator.run(new Object(), Mockito.mock(StreamStatusMaintainer.class), new CollectorOutput<String>(output));
        } catch (InterruptedException ignored) {
        }
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testNoMaxWatermarkOnImmediateStop() throws Exception {
        final List<StreamElement> output = new ArrayList<>();
        // regular stream source operator
        final StoppableStreamSource<String, StreamSourceOperatorWatermarksTest.InfiniteSource<String>> operator = new StoppableStreamSource(new StreamSourceOperatorWatermarksTest.InfiniteSource<String>());
        StreamSourceOperatorWatermarksTest.setupSourceOperator(operator, EventTime, 0);
        operator.stop();
        // run and stop
        operator.run(new Object(), Mockito.mock(StreamStatusMaintainer.class), new CollectorOutput<String>(output));
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testNoMaxWatermarkOnAsyncStop() throws Exception {
        final List<StreamElement> output = new ArrayList<>();
        // regular stream source operator
        final StoppableStreamSource<String, StreamSourceOperatorWatermarksTest.InfiniteSource<String>> operator = new StoppableStreamSource(new StreamSourceOperatorWatermarksTest.InfiniteSource<String>());
        StreamSourceOperatorWatermarksTest.setupSourceOperator(operator, EventTime, 0);
        // trigger an async cancel in a bit
        new Thread("canceler") {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
                operator.stop();
            }
        }.start();
        // run and wait to be stopped
        operator.run(new Object(), Mockito.mock(StreamStatusMaintainer.class), new CollectorOutput<String>(output));
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testAutomaticWatermarkContext() throws Exception {
        // regular stream source operator
        final StoppableStreamSource<String, StreamSourceOperatorWatermarksTest.InfiniteSource<String>> operator = new StoppableStreamSource(new StreamSourceOperatorWatermarksTest.InfiniteSource<String>());
        long watermarkInterval = 10;
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        processingTimeService.setCurrentTime(0);
        StreamSourceOperatorWatermarksTest.setupSourceOperator(operator, IngestionTime, watermarkInterval, processingTimeService);
        final List<StreamElement> output = new ArrayList<>();
        StreamSourceContexts.getSourceContext(IngestionTime, operator.getContainingTask().getProcessingTimeService(), operator.getContainingTask().getCheckpointLock(), operator.getContainingTask().getStreamStatusMaintainer(), new CollectorOutput<String>(output), operator.getExecutionConfig().getAutoWatermarkInterval(), (-1));
        // periodically emit the watermarks
        // even though we start from 1 the watermark are still
        // going to be aligned with the watermark interval.
        for (long i = 1; i < 100; i += watermarkInterval) {
            processingTimeService.setCurrentTime(i);
        }
        Assert.assertTrue(((output.size()) == 9));
        long nextWatermark = 0;
        for (StreamElement el : output) {
            nextWatermark += watermarkInterval;
            Watermark wm = ((Watermark) (el));
            Assert.assertTrue(((wm.getTimestamp()) == nextWatermark));
        }
    }

    // ------------------------------------------------------------------------
    private static final class FiniteSource<T> implements StoppableFunction , SourceFunction<T> {
        @Override
        public void run(SourceContext<T> ctx) {
        }

        @Override
        public void cancel() {
        }

        @Override
        public void stop() {
        }
    }

    private static final class InfiniteSource<T> implements StoppableFunction , SourceFunction<T> {
        private volatile boolean running = true;

        @Override
        public void run(SourceContext<T> ctx) throws Exception {
            while (running) {
                Thread.sleep(20);
            } 
        }

        @Override
        public void cancel() {
            running = false;
        }

        @Override
        public void stop() {
            running = false;
        }
    }
}

