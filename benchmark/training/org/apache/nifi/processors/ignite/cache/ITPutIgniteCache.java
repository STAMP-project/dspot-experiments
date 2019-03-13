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
package org.apache.nifi.processors.ignite.cache;


import PutIgniteCache.BATCH_SIZE;
import PutIgniteCache.CACHE_NAME;
import PutIgniteCache.DATA_STREAMER_PER_NODE_BUFFER_SIZE;
import PutIgniteCache.IGNITE_BATCH_FLOW_FILE_ITEM_NUMBER;
import PutIgniteCache.IGNITE_BATCH_FLOW_FILE_SUCCESSFUL_COUNT;
import PutIgniteCache.IGNITE_BATCH_FLOW_FILE_SUCCESSFUL_ITEM_NUMBER;
import PutIgniteCache.IGNITE_BATCH_FLOW_FILE_TOTAL_COUNT;
import PutIgniteCache.IGNITE_CACHE_ENTRY_KEY;
import PutIgniteCache.IGNITE_CONFIGURATION_FILE;
import PutIgniteCache.REL_FAILURE;
import PutIgniteCache.REL_SUCCESS;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class ITPutIgniteCache {
    private static final String CACHE_NAME = "testCache";

    private static TestRunner runner;

    private static PutIgniteCache putIgniteCache;

    private static Map<String, String> properties1;

    private static HashMap<String, String> properties2;

    @Test
    public void testPutIgniteCacheOnTriggerFileConfigurationOneFlowFile() throws IOException, InterruptedException {
        ITPutIgniteCache.runner = TestRunners.newTestRunner(ITPutIgniteCache.putIgniteCache);
        ITPutIgniteCache.runner.setProperty(BATCH_SIZE, "5");
        ITPutIgniteCache.runner.setProperty(PutIgniteCache.CACHE_NAME, ITPutIgniteCache.CACHE_NAME);
        ITPutIgniteCache.runner.setProperty(IGNITE_CONFIGURATION_FILE, (("file:///" + (new File(".").getAbsolutePath())) + "/src/test/resources/test-ignite.xml"));
        ITPutIgniteCache.runner.setProperty(DATA_STREAMER_PER_NODE_BUFFER_SIZE, "1");
        ITPutIgniteCache.runner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        ITPutIgniteCache.runner.assertValid();
        ITPutIgniteCache.properties1.put("igniteKey", "key5");
        ITPutIgniteCache.runner.enqueue("test".getBytes(), ITPutIgniteCache.properties1);
        ITPutIgniteCache.runner.run(1, false, true);
        ITPutIgniteCache.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> sucessfulFlowFiles = ITPutIgniteCache.runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, sucessfulFlowFiles.size());
        List<MockFlowFile> failureFlowFiles = ITPutIgniteCache.runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, failureFlowFiles.size());
        final MockFlowFile out = ITPutIgniteCache.runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_SUCCESSFUL_ITEM_NUMBER, "0");
        out.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_TOTAL_COUNT, "1");
        out.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_SUCCESSFUL_COUNT, "1");
        out.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_ITEM_NUMBER, "0");
        out.assertContentEquals("test".getBytes());
        System.out.println(("Value was: " + (new String(((byte[]) (ITPutIgniteCache.putIgniteCache.getIgniteCache().get("key5")))))));
        Assert.assertArrayEquals("test".getBytes(), ((byte[]) (ITPutIgniteCache.putIgniteCache.getIgniteCache().get("key5"))));
        ITPutIgniteCache.putIgniteCache.getIgniteCache().remove("key5");
    }

    @Test
    public void testPutIgniteCacheOnTriggerNoConfigurationTwoFlowFile() throws IOException, InterruptedException {
        ITPutIgniteCache.runner = TestRunners.newTestRunner(ITPutIgniteCache.putIgniteCache);
        ITPutIgniteCache.runner.setProperty(BATCH_SIZE, "5");
        ITPutIgniteCache.runner.setProperty(PutIgniteCache.CACHE_NAME, ITPutIgniteCache.CACHE_NAME);
        ITPutIgniteCache.runner.setProperty(DATA_STREAMER_PER_NODE_BUFFER_SIZE, "1");
        ITPutIgniteCache.runner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        ITPutIgniteCache.runner.assertValid();
        ITPutIgniteCache.properties1.put("igniteKey", "key51");
        ITPutIgniteCache.runner.enqueue("test1".getBytes(), ITPutIgniteCache.properties1);
        ITPutIgniteCache.properties2.put("igniteKey", "key52");
        ITPutIgniteCache.runner.enqueue("test2".getBytes(), ITPutIgniteCache.properties2);
        ITPutIgniteCache.runner.run(1, false, true);
        ITPutIgniteCache.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        List<MockFlowFile> sucessfulFlowFiles = ITPutIgniteCache.runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, sucessfulFlowFiles.size());
        List<MockFlowFile> failureFlowFiles = ITPutIgniteCache.runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, failureFlowFiles.size());
        final MockFlowFile out = ITPutIgniteCache.runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_SUCCESSFUL_ITEM_NUMBER, "0");
        out.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_TOTAL_COUNT, "2");
        out.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_SUCCESSFUL_COUNT, "2");
        out.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_ITEM_NUMBER, "0");
        out.assertContentEquals("test1".getBytes());
        System.out.println(("value was " + (new String(ITPutIgniteCache.putIgniteCache.getIgniteCache().get("key51")))));
        Assert.assertArrayEquals("test1".getBytes(), ((byte[]) (ITPutIgniteCache.putIgniteCache.getIgniteCache().get("key51"))));
        final MockFlowFile out2 = ITPutIgniteCache.runner.getFlowFilesForRelationship(REL_SUCCESS).get(1);
        out2.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_SUCCESSFUL_ITEM_NUMBER, "1");
        out2.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_TOTAL_COUNT, "2");
        out2.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_SUCCESSFUL_COUNT, "2");
        out2.assertAttributeEquals(IGNITE_BATCH_FLOW_FILE_ITEM_NUMBER, "1");
        out2.assertContentEquals("test2".getBytes());
        Assert.assertArrayEquals("test2".getBytes(), ((byte[]) (ITPutIgniteCache.putIgniteCache.getIgniteCache().get("key52"))));
        ITPutIgniteCache.putIgniteCache.getIgniteCache().remove("key52");
        ITPutIgniteCache.putIgniteCache.getIgniteCache().remove("key51");
    }

    @Test
    public void testPutIgniteCacheOnTriggerNoConfigurationTwoFlowFileStopAndStart2Times() throws IOException, InterruptedException {
        ITPutIgniteCache.runner = TestRunners.newTestRunner(ITPutIgniteCache.putIgniteCache);
        ITPutIgniteCache.runner.setProperty(BATCH_SIZE, "5");
        ITPutIgniteCache.runner.setProperty(PutIgniteCache.CACHE_NAME, ITPutIgniteCache.CACHE_NAME);
        ITPutIgniteCache.runner.setProperty(DATA_STREAMER_PER_NODE_BUFFER_SIZE, "1");
        ITPutIgniteCache.runner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        ITPutIgniteCache.runner.assertValid();
        ITPutIgniteCache.properties1.put("igniteKey", "key51");
        ITPutIgniteCache.runner.enqueue("test1".getBytes(), ITPutIgniteCache.properties1);
        ITPutIgniteCache.properties2.put("igniteKey", "key52");
        ITPutIgniteCache.runner.enqueue("test2".getBytes(), ITPutIgniteCache.properties2);
        ITPutIgniteCache.runner.run(1, false, true);
        ITPutIgniteCache.putIgniteCache.getIgniteCache().remove("key51");
        ITPutIgniteCache.putIgniteCache.getIgniteCache().remove("key52");
        ITPutIgniteCache.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        ITPutIgniteCache.putIgniteCache.getIgniteCache().remove("key52");
        ITPutIgniteCache.putIgniteCache.getIgniteCache().remove("key52");
        // Close and restart first time
        ITPutIgniteCache.putIgniteCache.closeIgniteDataStreamer();
        ITPutIgniteCache.runner.clearTransferState();
        ITPutIgniteCache.putIgniteCache.initializeIgniteDataStreamer(ITPutIgniteCache.runner.getProcessContext());
        ITPutIgniteCache.runner.assertValid();
        ITPutIgniteCache.properties1.put("igniteKey", "key51");
        ITPutIgniteCache.runner.enqueue("test1".getBytes(), ITPutIgniteCache.properties1);
        ITPutIgniteCache.properties2.put("igniteKey", "key52");
        ITPutIgniteCache.runner.enqueue("test2".getBytes(), ITPutIgniteCache.properties2);
        ITPutIgniteCache.runner.run(1, false, true);
        ITPutIgniteCache.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        ITPutIgniteCache.putIgniteCache.getIgniteCache().remove("key51");
        ITPutIgniteCache.putIgniteCache.getIgniteCache().remove("key52");
        // Close and restart second time
        ITPutIgniteCache.putIgniteCache.closeIgniteDataStreamer();
        ITPutIgniteCache.runner.clearTransferState();
        ITPutIgniteCache.putIgniteCache.initializeIgniteDataStreamer(ITPutIgniteCache.runner.getProcessContext());
        ITPutIgniteCache.runner.assertValid();
        ITPutIgniteCache.properties1.put("igniteKey", "key51");
        ITPutIgniteCache.runner.enqueue("test1".getBytes(), ITPutIgniteCache.properties1);
        ITPutIgniteCache.properties2.put("igniteKey", "key52");
        ITPutIgniteCache.runner.enqueue("test2".getBytes(), ITPutIgniteCache.properties2);
        ITPutIgniteCache.runner.run(1, false, true);
        ITPutIgniteCache.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        ITPutIgniteCache.putIgniteCache.getIgniteCache().remove("key52");
        ITPutIgniteCache.putIgniteCache.getIgniteCache().remove("key51");
    }
}

