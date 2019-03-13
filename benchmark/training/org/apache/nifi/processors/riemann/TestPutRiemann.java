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
package org.apache.nifi.processors.riemann;


import Proto.Event;
import PutRiemann.BATCH_SIZE;
import PutRiemann.REL_FAILURE;
import PutRiemann.REL_SUCCESS;
import com.aphyr.riemann.Proto;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestPutRiemann {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    // Holds incoming events to Riemann
    private Queue<Proto.Event> eventStream = new LinkedList<Proto.Event>();

    @Test
    public void testBasicEvent() {
        TestRunner runner = getTestRunner();
        Map<String, String> attributes = new HashMap<>();
        attributes.put("riemann.metric", "42");
        attributes.put("riemann.host", "basic-host");
        MockFlowFile flowFile = new MockFlowFile(1);
        flowFile.putAttributes(attributes);
        runner.enqueue(flowFile);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Proto.Event event = eventStream.remove();
        Assert.assertEquals("nifi-test-service", event.getService());
        Assert.assertTrue((5.0 == (event.getTtl())));
        Assert.assertTrue((42.0 == (event.getMetricF())));
        Assert.assertEquals("basic-host", event.getHost());
        Assert.assertEquals("test", event.getDescription());
        Assert.assertEquals(3, event.getTagsCount());
        Assert.assertTrue(event.getTagsList().contains("tag1"));
        Assert.assertTrue(event.getTagsList().contains("tag2"));
        Assert.assertTrue(event.getTagsList().contains("tag3"));
        Assert.assertEquals(0, event.getAttributesCount());
    }

    @Test
    public void testBatchedEvents() {
        // (2 batches) + (1 remaining event)
        int iterations = ((Integer.parseInt(BATCH_SIZE.getDefaultValue())) * 2) + 1;
        TestRunner runner = getTestRunner();
        for (int i = 0; i < iterations; i++) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("riemann.metric", Float.toString(i));
            attributes.put("riemann.host", "batch-host");
            attributes.put("custom.attribute.1", "attr1");
            attributes.put("custom.attribute.2", "attr2");
            attributes.put("custom.attribute.3", "attr3");
            MockFlowFile flowFile = new MockFlowFile(i);
            flowFile.putAttributes(attributes);
            runner.enqueue(flowFile);
        }
        runner.run(3);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        for (int i = 0; i < iterations; i++) {
            Proto.Event event = eventStream.remove();
            Assert.assertEquals("nifi-test-service", event.getService());
            Assert.assertTrue((5.0 == (event.getTtl())));
            Assert.assertTrue((i == (event.getMetricF())));
            Assert.assertEquals("batch-host", event.getHost());
            Assert.assertEquals("test", event.getDescription());
            Assert.assertEquals(3, event.getTagsCount());
            Assert.assertEquals(3, event.getAttributesCount());
            Assert.assertTrue(event.getTagsList().contains("tag1"));
            Assert.assertTrue(event.getTagsList().contains("tag2"));
            Assert.assertTrue(event.getTagsList().contains("tag3"));
        }
    }

    @Test
    public void testInvalidEvents() {
        TestRunner runner = getTestRunner();
        MockFlowFile flowFile = new MockFlowFile(1);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("riemann.metric", "NOT A NUMBER");
        flowFile.putAttributes(attributes);
        runner.enqueue(flowFile);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
    }

    @Test(expected = AssertionError.class)
    public void testFailedDeref() {
        TestRunner runner = getTestRunner(true);
        MockFlowFile flowFile = new MockFlowFile(1);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("riemann.metric", "5");
        flowFile.putAttributes(attributes);
        runner.enqueue(flowFile);
        try {
            runner.run();
        } catch (ProcessException e) {
            runner.assertQueueNotEmpty();
            throw e;
        }
    }
}

