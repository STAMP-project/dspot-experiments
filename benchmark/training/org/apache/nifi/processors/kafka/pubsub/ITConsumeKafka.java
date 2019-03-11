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
package org.apache.nifi.processors.kafka.pubsub;


import ConsumeKafka_0_10.AUTO_OFFSET_RESET;
import ConsumeKafka_0_10.GROUP_ID;
import ConsumeKafka_0_10.OFFSET_EARLIEST;
import ConsumeKafka_0_10.TOPICS;
import ConsumeKafka_0_10.TOPIC_TYPE;
import KafkaProcessorUtils.BOOTSTRAP_SERVERS;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ITConsumeKafka {
    ConsumerLease mockLease = null;

    ConsumerPool mockConsumerPool = null;

    @Test
    public void validateGetAllMessages() throws Exception {
        String groupName = "validateGetAllMessages";
        Mockito.when(mockConsumerPool.obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(mockLease);
        Mockito.when(mockLease.continuePolling()).thenReturn(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
        Mockito.when(mockLease.commit()).thenReturn(Boolean.TRUE);
        ConsumeKafka_0_10 proc = new ConsumeKafka_0_10() {
            @Override
            protected ConsumerPool createConsumerPool(final ProcessContext context, final ComponentLog log) {
                return mockConsumerPool;
            }
        };
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(BOOTSTRAP_SERVERS, "0.0.0.0:1234");
        runner.setProperty(TOPICS, "foo,bar");
        runner.setProperty(GROUP_ID, groupName);
        runner.setProperty(AUTO_OFFSET_RESET, OFFSET_EARLIEST);
        runner.run(1, false);
        Mockito.verify(mockConsumerPool, Mockito.times(1)).obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject());
        Mockito.verify(mockLease, Mockito.times(3)).continuePolling();
        Mockito.verify(mockLease, Mockito.times(2)).poll();
        Mockito.verify(mockLease, Mockito.times(1)).commit();
        Mockito.verify(mockLease, Mockito.times(1)).close();
        Mockito.verifyNoMoreInteractions(mockConsumerPool);
        Mockito.verifyNoMoreInteractions(mockLease);
    }

    @Test
    public void validateGetAllMessagesPattern() throws Exception {
        String groupName = "validateGetAllMessagesPattern";
        Mockito.when(mockConsumerPool.obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(mockLease);
        Mockito.when(mockLease.continuePolling()).thenReturn(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
        Mockito.when(mockLease.commit()).thenReturn(Boolean.TRUE);
        ConsumeKafka_0_10 proc = new ConsumeKafka_0_10() {
            @Override
            protected ConsumerPool createConsumerPool(final ProcessContext context, final ComponentLog log) {
                return mockConsumerPool;
            }
        };
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(BOOTSTRAP_SERVERS, "0.0.0.0:1234");
        runner.setProperty(TOPICS, "(fo.*)|(ba)");
        runner.setProperty(TOPIC_TYPE, "pattern");
        runner.setProperty(GROUP_ID, groupName);
        runner.setProperty(AUTO_OFFSET_RESET, OFFSET_EARLIEST);
        runner.run(1, false);
        Mockito.verify(mockConsumerPool, Mockito.times(1)).obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject());
        Mockito.verify(mockLease, Mockito.times(3)).continuePolling();
        Mockito.verify(mockLease, Mockito.times(2)).poll();
        Mockito.verify(mockLease, Mockito.times(1)).commit();
        Mockito.verify(mockLease, Mockito.times(1)).close();
        Mockito.verifyNoMoreInteractions(mockConsumerPool);
        Mockito.verifyNoMoreInteractions(mockLease);
    }

    @Test
    public void validateGetErrorMessages() throws Exception {
        String groupName = "validateGetErrorMessages";
        Mockito.when(mockConsumerPool.obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject())).thenReturn(mockLease);
        Mockito.when(mockLease.continuePolling()).thenReturn(true, false);
        Mockito.when(mockLease.commit()).thenReturn(Boolean.FALSE);
        ConsumeKafka_0_10 proc = new ConsumeKafka_0_10() {
            @Override
            protected ConsumerPool createConsumerPool(final ProcessContext context, final ComponentLog log) {
                return mockConsumerPool;
            }
        };
        final TestRunner runner = TestRunners.newTestRunner(proc);
        runner.setProperty(BOOTSTRAP_SERVERS, "0.0.0.0:1234");
        runner.setProperty(TOPICS, "foo,bar");
        runner.setProperty(GROUP_ID, groupName);
        runner.setProperty(AUTO_OFFSET_RESET, OFFSET_EARLIEST);
        runner.run(1, false);
        Mockito.verify(mockConsumerPool, Mockito.times(1)).obtainConsumer(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject());
        Mockito.verify(mockLease, Mockito.times(2)).continuePolling();
        Mockito.verify(mockLease, Mockito.times(1)).poll();
        Mockito.verify(mockLease, Mockito.times(1)).commit();
        Mockito.verify(mockLease, Mockito.times(1)).close();
        Mockito.verifyNoMoreInteractions(mockConsumerPool);
        Mockito.verifyNoMoreInteractions(mockLease);
    }
}

