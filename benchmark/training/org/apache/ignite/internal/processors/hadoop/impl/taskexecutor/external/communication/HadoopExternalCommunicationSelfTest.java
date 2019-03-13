/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.hadoop.impl.taskexecutor.external.communication;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.ignite.internal.processors.hadoop.message.HadoopMessage;
import org.apache.ignite.internal.processors.hadoop.taskexecutor.external.HadoopProcessDescriptor;
import org.apache.ignite.internal.processors.hadoop.taskexecutor.external.communication.HadoopMessageListener;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Tests Hadoop external communication component.
 */
@Ignore("https://issues.apache.org/jira/browse/IGNITE-404")
public class HadoopExternalCommunicationSelfTest extends GridCommonAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleMessageSendingTcp() throws Exception {
        checkSimpleMessageSending(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleMessageSendingShmem() throws Exception {
        checkSimpleMessageSending(true);
    }

    /**
     *
     */
    private static class TestHadoopListener implements HadoopMessageListener {
        /**
         * Received messages (array list is safe because executor has one thread).
         */
        private Collection<HadoopExternalCommunicationSelfTest.TestMessage> msgs = new ArrayList<>();

        /**
         * Await latch.
         */
        private CountDownLatch receiveLatch;

        /**
         *
         *
         * @param msgs
         * 		Number of messages to await.
         */
        private TestHadoopListener(int msgs) {
            receiveLatch = new CountDownLatch(msgs);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void onMessageReceived(HadoopProcessDescriptor desc, HadoopMessage msg) {
            assert msg instanceof HadoopExternalCommunicationSelfTest.TestMessage;
            msgs.add(((HadoopExternalCommunicationSelfTest.TestMessage) (msg)));
            receiveLatch.countDown();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void onConnectionLost(HadoopProcessDescriptor desc) {
            // No-op.
        }

        /**
         *
         *
         * @return Received messages.
         */
        public Collection<HadoopExternalCommunicationSelfTest.TestMessage> messages() {
            return msgs;
        }

        /**
         *
         *
         * @param millis
         * 		Time to await.
         * @throws InterruptedException
         * 		If wait interrupted.
         */
        public void await(int millis) throws InterruptedException {
            receiveLatch.await(millis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     *
     */
    private static class TestMessage implements HadoopMessage {
        /**
         * From index.
         */
        private int from;

        /**
         * To index.
         */
        private int to;

        /**
         *
         *
         * @param from
         * 		From index.
         * @param to
         * 		To index.
         */
        private TestMessage(int from, int to) {
            this.from = from;
            this.to = to;
        }

        /**
         * Required by {@link Externalizable}.
         */
        public TestMessage() {
            // No-op.
        }

        /**
         *
         *
         * @return From index.
         */
        public int from() {
            return from;
        }

        /**
         *
         *
         * @return To index.
         */
        public int to() {
            return to;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(from);
            out.writeInt(to);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            from = in.readInt();
            to = in.readInt();
        }
    }
}

