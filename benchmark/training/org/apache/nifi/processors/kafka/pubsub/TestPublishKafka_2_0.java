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


import PublishKafka_2_0.REL_FAILURE;
import PublishKafka_2_0.REL_SUCCESS;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestPublishKafka_2_0 {
    private static final String TOPIC_NAME = "unit-test";

    private PublisherPool mockPool;

    private PublisherLease mockLease;

    private TestRunner runner;

    @Test
    public void testSingleSuccess() throws IOException {
        final MockFlowFile flowFile = runner.enqueue("hello world");
        Mockito.when(mockLease.complete()).thenReturn(createAllSuccessPublishResult(flowFile, 1));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Mockito.verify(mockLease, Mockito.times(1)).publish(ArgumentMatchers.any(FlowFile.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.eq(null), ArgumentMatchers.eq(null), ArgumentMatchers.eq(TestPublishKafka_2_0.TOPIC_NAME));
        Mockito.verify(mockLease, Mockito.times(1)).complete();
        Mockito.verify(mockLease, Mockito.times(0)).poison();
        Mockito.verify(mockLease, Mockito.times(1)).close();
    }

    @Test
    public void testMultipleSuccess() throws IOException {
        final Set<FlowFile> flowFiles = new HashSet<>();
        flowFiles.add(runner.enqueue("hello world"));
        flowFiles.add(runner.enqueue("hello world"));
        flowFiles.add(runner.enqueue("hello world"));
        Mockito.when(mockLease.complete()).thenReturn(createAllSuccessPublishResult(flowFiles, 1));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        Mockito.verify(mockLease, Mockito.times(3)).publish(ArgumentMatchers.any(FlowFile.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.eq(null), ArgumentMatchers.eq(null), ArgumentMatchers.eq(TestPublishKafka_2_0.TOPIC_NAME));
        Mockito.verify(mockLease, Mockito.times(1)).complete();
        Mockito.verify(mockLease, Mockito.times(0)).poison();
        Mockito.verify(mockLease, Mockito.times(1)).close();
    }

    @Test
    public void testSingleFailure() throws IOException {
        final MockFlowFile flowFile = runner.enqueue("hello world");
        Mockito.when(mockLease.complete()).thenReturn(createFailurePublishResult(flowFile));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        Mockito.verify(mockLease, Mockito.times(1)).publish(ArgumentMatchers.any(FlowFile.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.eq(null), ArgumentMatchers.eq(null), ArgumentMatchers.eq(TestPublishKafka_2_0.TOPIC_NAME));
        Mockito.verify(mockLease, Mockito.times(1)).complete();
        Mockito.verify(mockLease, Mockito.times(1)).close();
    }

    @Test
    public void testMultipleFailures() throws IOException {
        final Set<FlowFile> flowFiles = new HashSet<>();
        flowFiles.add(runner.enqueue("hello world"));
        flowFiles.add(runner.enqueue("hello world"));
        flowFiles.add(runner.enqueue("hello world"));
        Mockito.when(mockLease.complete()).thenReturn(createFailurePublishResult(flowFiles));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 3);
        Mockito.verify(mockLease, Mockito.times(3)).publish(ArgumentMatchers.any(FlowFile.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.eq(null), ArgumentMatchers.eq(null), ArgumentMatchers.eq(TestPublishKafka_2_0.TOPIC_NAME));
        Mockito.verify(mockLease, Mockito.times(1)).complete();
        Mockito.verify(mockLease, Mockito.times(1)).close();
    }

    @Test
    public void testMultipleMessagesPerFlowFile() throws IOException {
        final List<FlowFile> flowFiles = new ArrayList<>();
        flowFiles.add(runner.enqueue("hello world"));
        flowFiles.add(runner.enqueue("hello world"));
        final Map<FlowFile, Integer> msgCounts = new HashMap<>();
        msgCounts.put(flowFiles.get(0), 10);
        msgCounts.put(flowFiles.get(1), 20);
        final PublishResult result = createPublishResult(msgCounts, new HashSet(flowFiles), Collections.emptyMap());
        Mockito.when(mockLease.complete()).thenReturn(result);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        Mockito.verify(mockLease, Mockito.times(2)).publish(ArgumentMatchers.any(FlowFile.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.eq(null), ArgumentMatchers.eq(null), ArgumentMatchers.eq(TestPublishKafka_2_0.TOPIC_NAME));
        Mockito.verify(mockLease, Mockito.times(1)).complete();
        Mockito.verify(mockLease, Mockito.times(0)).poison();
        Mockito.verify(mockLease, Mockito.times(1)).close();
        runner.assertAllFlowFilesContainAttribute("msg.count");
        Assert.assertEquals(1, runner.getFlowFilesForRelationship(REL_SUCCESS).stream().filter(( ff) -> ff.getAttribute("msg.count").equals("10")).count());
        Assert.assertEquals(1, runner.getFlowFilesForRelationship(REL_SUCCESS).stream().filter(( ff) -> ff.getAttribute("msg.count").equals("20")).count());
    }

    @Test
    public void testSomeSuccessSomeFailure() throws IOException {
        final List<FlowFile> flowFiles = new ArrayList<>();
        flowFiles.add(runner.enqueue("hello world"));
        flowFiles.add(runner.enqueue("hello world"));
        flowFiles.add(runner.enqueue("hello world"));
        flowFiles.add(runner.enqueue("hello world"));
        final Map<FlowFile, Integer> msgCounts = new HashMap<>();
        msgCounts.put(flowFiles.get(0), 10);
        msgCounts.put(flowFiles.get(1), 20);
        final Map<FlowFile, Exception> failureMap = new HashMap<>();
        failureMap.put(flowFiles.get(2), new RuntimeException("Intentional Unit Test Exception"));
        failureMap.put(flowFiles.get(3), new RuntimeException("Intentional Unit Test Exception"));
        final PublishResult result = createPublishResult(msgCounts, new HashSet(flowFiles.subList(0, 2)), failureMap);
        Mockito.when(mockLease.complete()).thenReturn(result);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 4);
        Mockito.verify(mockLease, Mockito.times(4)).publish(ArgumentMatchers.any(FlowFile.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.eq(null), ArgumentMatchers.eq(null), ArgumentMatchers.eq(TestPublishKafka_2_0.TOPIC_NAME));
        Mockito.verify(mockLease, Mockito.times(1)).complete();
        Mockito.verify(mockLease, Mockito.times(1)).close();
        Assert.assertTrue(runner.getFlowFilesForRelationship(REL_FAILURE).stream().noneMatch(( ff) -> (ff.getAttribute("msg.count")) != null));
    }
}

