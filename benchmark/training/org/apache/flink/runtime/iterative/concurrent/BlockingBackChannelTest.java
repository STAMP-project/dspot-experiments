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
package org.apache.flink.runtime.iterative.concurrent;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.runtime.iterative.io.SerializedUpdateBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link BlockingBackChannel}.
 */
public class BlockingBackChannelTest {
    private static final int NUM_ITERATIONS = 3;

    private static final Integer INPUT_COMPLETELY_PROCESSED_MESSAGE = 1;

    @Test
    public void multiThreaded() throws InterruptedException {
        BlockingQueue<Integer> dataChannel = new ArrayBlockingQueue<Integer>(1);
        List<String> actionLog = new ArrayList<>();
        SerializedUpdateBuffer buffer = Mockito.mock(SerializedUpdateBuffer.class);
        BlockingBackChannel channel = new BlockingBackChannel(buffer);
        Thread head = new Thread(new BlockingBackChannelTest.IterationHead(channel, dataChannel, actionLog));
        Thread tail = new Thread(new BlockingBackChannelTest.IterationTail(channel, dataChannel, actionLog));
        tail.start();
        head.start();
        head.join();
        tail.join();
        // int action = 0;
        // for (String log : actionLog) {
        // System.out.println("ACTION " + (++action) + ": " + log);
        // }
        Assert.assertEquals(12, actionLog.size());
        Assert.assertEquals("head sends data", actionLog.get(0));
        Assert.assertEquals("tail receives data", actionLog.get(1));
        Assert.assertEquals("tail writes in iteration 0", actionLog.get(2));
        Assert.assertEquals("head reads in iteration 0", actionLog.get(3));
        Assert.assertEquals("head sends data", actionLog.get(4));
        Assert.assertEquals("tail receives data", actionLog.get(5));
        Assert.assertEquals("tail writes in iteration 1", actionLog.get(6));
        Assert.assertEquals("head reads in iteration 1", actionLog.get(7));
        Assert.assertEquals("head sends data", actionLog.get(8));
        Assert.assertEquals("tail receives data", actionLog.get(9));
        Assert.assertEquals("tail writes in iteration 2", actionLog.get(10));
        Assert.assertEquals("head reads in iteration 2", actionLog.get(11));
    }

    class IterationHead implements Runnable {
        private final BlockingBackChannel backChannel;

        private final BlockingQueue<Integer> dataChannel;

        private final Random random;

        private final List<String> actionLog;

        IterationHead(BlockingBackChannel backChannel, BlockingQueue<Integer> dataChannel, List<String> actionLog) {
            this.backChannel = backChannel;
            this.dataChannel = dataChannel;
            this.actionLog = actionLog;
            random = new Random();
        }

        @Override
        public void run() {
            processInputAndSendMessageThroughDataChannel();
            for (int n = 0; n < (BlockingBackChannelTest.NUM_ITERATIONS); n++) {
                try {
                    backChannel.getReadEndAfterSuperstepEnded();
                    actionLog.add(("head reads in iteration " + n));
                    Thread.sleep(random.nextInt(100));
                    // we don't send through the data channel in the last iteration, we would send to the output task
                    if (n != ((BlockingBackChannelTest.NUM_ITERATIONS) - 1)) {
                        processInputAndSendMessageThroughDataChannel();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        void processInputAndSendMessageThroughDataChannel() {
            actionLog.add("head sends data");
            dataChannel.offer(BlockingBackChannelTest.INPUT_COMPLETELY_PROCESSED_MESSAGE);
        }
    }

    class IterationTail implements Runnable {
        private final BlockingBackChannel backChannel;

        private final BlockingQueue<Integer> dataChannel;

        private final Random random;

        private final List<String> actionLog;

        IterationTail(BlockingBackChannel backChannel, BlockingQueue<Integer> dataChannel, List<String> actionLog) {
            this.backChannel = backChannel;
            this.dataChannel = dataChannel;
            this.actionLog = actionLog;
            random = new Random();
        }

        @Override
        public void run() {
            try {
                for (int n = 0; n < (BlockingBackChannelTest.NUM_ITERATIONS); n++) {
                    DataOutputView writeEnd = backChannel.getWriteEnd();
                    readInputFromDataChannel();
                    Thread.sleep(random.nextInt(10));
                    DataInputView inputView = Mockito.mock(DataInputView.class);
                    actionLog.add(("tail writes in iteration " + n));
                    writeEnd.write(inputView, 1);
                    backChannel.notifyOfEndOfSuperstep();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        void readInputFromDataChannel() throws InterruptedException {
            dataChannel.take();
            actionLog.add("tail receives data");
        }
    }
}

