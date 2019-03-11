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


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.streaming.api.operators.AbstractStreamOperator;
import org.apache.flink.streaming.api.operators.OneInputStreamOperator;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class test the {@link OperatorChain}.
 *
 * <p>It takes a different (simpler) approach at testing the operator chain than
 * {@link StreamOperatorChainingTest}.
 */
public class OperatorChainTest {
    @Test
    public void testPrepareCheckpointPreBarrier() throws Exception {
        final AtomicInteger intRef = new AtomicInteger();
        final OneInputStreamOperator<String, String> one = new OperatorChainTest.ValidatingOperator(intRef, 0);
        final OneInputStreamOperator<String, String> two = new OperatorChainTest.ValidatingOperator(intRef, 1);
        final OneInputStreamOperator<String, String> three = new OperatorChainTest.ValidatingOperator(intRef, 2);
        final OperatorChain<?, ?> chain = OperatorChainTest.setupOperatorChain(one, two, three);
        chain.prepareSnapshotPreBarrier(OperatorChainTest.ValidatingOperator.CHECKPOINT_ID);
        Assert.assertEquals(3, intRef.get());
    }

    // ------------------------------------------------------------------------
    // Test Operator Implementations
    // ------------------------------------------------------------------------
    private static class ValidatingOperator extends AbstractStreamOperator<String> implements OneInputStreamOperator<String, String> {
        private static final long serialVersionUID = 1L;

        static final long CHECKPOINT_ID = 5765167L;

        final AtomicInteger toUpdate;

        final int expected;

        public ValidatingOperator(AtomicInteger toUpdate, int expected) {
            this.toUpdate = toUpdate;
            this.expected = expected;
        }

        @Override
        public void prepareSnapshotPreBarrier(long checkpointId) throws Exception {
            Assert.assertEquals("wrong checkpointId", OperatorChainTest.ValidatingOperator.CHECKPOINT_ID, checkpointId);
            Assert.assertEquals("wrong order", expected, toUpdate.getAndIncrement());
        }

        @Override
        public void processElement(StreamRecord<String> element) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public OperatorID getOperatorID() {
            return new OperatorID();
        }
    }
}

