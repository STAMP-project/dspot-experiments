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
package org.apache.flink.streaming.runtime.tasks;


import java.io.EOFException;
import java.io.IOException;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.runtime.state.StreamStateHandle;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test checks that task restores that get stuck in the presence of interrupts
 * are handled properly.
 *
 * <p>In practice, reading from HDFS is interrupt sensitive: The HDFS code frequently deadlocks
 * or livelocks if it is interrupted.
 */
public class InterruptSensitiveRestoreTest {
    private static final OneShotLatch IN_RESTORE_LATCH = new OneShotLatch();

    private static final int OPERATOR_MANAGED = 0;

    private static final int OPERATOR_RAW = 1;

    private static final int KEYED_MANAGED = 2;

    private static final int KEYED_RAW = 3;

    @Test
    public void testRestoreWithInterruptOperatorManaged() throws Exception {
        testRestoreWithInterrupt(InterruptSensitiveRestoreTest.OPERATOR_MANAGED);
    }

    @Test
    public void testRestoreWithInterruptOperatorRaw() throws Exception {
        testRestoreWithInterrupt(InterruptSensitiveRestoreTest.OPERATOR_RAW);
    }

    @Test
    public void testRestoreWithInterruptKeyedManaged() throws Exception {
        testRestoreWithInterrupt(InterruptSensitiveRestoreTest.KEYED_MANAGED);
    }

    @Test
    public void testRestoreWithInterruptKeyedRaw() throws Exception {
        testRestoreWithInterrupt(InterruptSensitiveRestoreTest.KEYED_RAW);
    }

    // ------------------------------------------------------------------------
    @SuppressWarnings("serial")
    private static class InterruptLockingStateHandle implements StreamStateHandle {
        private static final long serialVersionUID = 1L;

        private volatile boolean closed;

        @Override
        public FSDataInputStream openInputStream() throws IOException {
            closed = false;
            FSDataInputStream is = new FSDataInputStream() {
                @Override
                public void seek(long desired) {
                }

                @Override
                public long getPos() {
                    return 0;
                }

                @Override
                public int read() throws IOException {
                    block();
                    throw new EOFException();
                }

                @Override
                public void close() throws IOException {
                    super.close();
                    closed = true;
                }
            };
            return is;
        }

        private void block() {
            InterruptSensitiveRestoreTest.IN_RESTORE_LATCH.trigger();
            // this mimics what happens in the HDFS client code.
            // an interrupt on a waiting object leads to an infinite loop
            try {
                synchronized(this) {
                    // noinspection WaitNotInLoop
                    wait();
                }
            } catch (InterruptedException e) {
                while (!(closed)) {
                    try {
                        synchronized(this) {
                            wait();
                        }
                    } catch (InterruptedException ignored) {
                    }
                } 
            }
        }

        @Override
        public void discardState() throws Exception {
        }

        @Override
        public long getStateSize() {
            return 0;
        }
    }

    // ------------------------------------------------------------------------
    private static class TestSource implements CheckpointedFunction , SourceFunction<Object> {
        private static final long serialVersionUID = 1L;

        private final int testType;

        public TestSource(int testType) {
            this.testType = testType;
        }

        @Override
        public void run(SourceContext<Object> ctx) throws Exception {
            Assert.fail("should never be called");
        }

        @Override
        public void cancel() {
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            Assert.fail("should never be called");
        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            // raw keyed state is already read by timer service, all others to initialize the context...we only need to
            // trigger this manually.
            getRawOperatorStateInputs().iterator().next().getStream().read();
        }
    }
}

