/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.kafka;


import GetKafka.BATCH_SIZE;
import GetKafka.KAFKA_TIMEOUT;
import GetKafka.MESSAGE_DEMARCATOR;
import GetKafka.REL_SUCCESS;
import GetKafka.TOPIC;
import GetKafka.ZOOKEEPER_CONNECTION_STRING;
import GetKafka.ZOOKEEPER_TIMEOUT;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import kafka.consumer.ConsumerIterator;
import kafka.message.MessageAndMetadata;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestGetKafka {
    @Test
    public void testWithDelimiter() {
        final List<String> messages = new ArrayList<>();
        messages.add("Hello");
        messages.add("Good-bye");
        final TestGetKafka.TestableProcessor proc = new TestGetKafka.TestableProcessor(null, messages);
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(ZOOKEEPER_CONNECTION_STRING, "localhost:2181");
        runner.setProperty(TOPIC, "testX");
        runner.setProperty(KAFKA_TIMEOUT, "3 secs");
        runner.setProperty(ZOOKEEPER_TIMEOUT, "3 secs");
        runner.setProperty(MESSAGE_DEMARCATOR, "\\n");
        runner.setProperty(BATCH_SIZE, "2");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertContentEquals("Hello\nGood-bye");
    }

    @Test
    public void testWithDelimiterAndNotEnoughMessages() {
        final List<String> messages = new ArrayList<>();
        messages.add("Hello");
        messages.add("Good-bye");
        final TestGetKafka.TestableProcessor proc = new TestGetKafka.TestableProcessor(null, messages);
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(ZOOKEEPER_CONNECTION_STRING, "localhost:2181");
        runner.setProperty(TOPIC, "testX");
        runner.setProperty(KAFKA_TIMEOUT, "3 secs");
        runner.setProperty(ZOOKEEPER_TIMEOUT, "3 secs");
        runner.setProperty(MESSAGE_DEMARCATOR, "\\n");
        runner.setProperty(BATCH_SIZE, "3");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertContentEquals("Hello\nGood-bye");
    }

    private static class TestableProcessor extends GetKafka {
        private final byte[] key;

        private final Iterator<String> messageItr;

        public TestableProcessor(final byte[] key, final List<String> messages) {
            this.key = key;
            messageItr = messages.iterator();
        }

        @Override
        public void createConsumers(ProcessContext context) {
            try {
                Field f = GetKafka.class.getDeclaredField("consumerStreamsReady");
                f.setAccessible(true);
                ((AtomicBoolean) (f.get(this))).set(true);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        protected ConsumerIterator<byte[], byte[]> getStreamIterator() {
            final ConsumerIterator<byte[], byte[]> itr = Mockito.mock(ConsumerIterator.class);
            Mockito.doAnswer(new Answer<Boolean>() {
                @Override
                public Boolean answer(final InvocationOnMock invocation) throws Throwable {
                    return messageItr.hasNext();
                }
            }).when(itr).hasNext();
            Mockito.doAnswer(new Answer<MessageAndMetadata>() {
                @Override
                public MessageAndMetadata answer(InvocationOnMock invocation) throws Throwable {
                    final MessageAndMetadata mam = Mockito.mock(MessageAndMetadata.class);
                    Mockito.when(mam.key()).thenReturn(key);
                    Mockito.when(mam.offset()).thenReturn(0L);
                    Mockito.when(mam.partition()).thenReturn(0);
                    Mockito.doAnswer(new Answer<byte[]>() {
                        @Override
                        public byte[] answer(InvocationOnMock invocation) throws Throwable {
                            return messageItr.next().getBytes();
                        }
                    }).when(mam).message();
                    return mam;
                }
            }).when(itr).next();
            return itr;
        }
    }
}

