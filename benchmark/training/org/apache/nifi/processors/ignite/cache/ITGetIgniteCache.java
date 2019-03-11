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


import GetIgniteCache.CACHE_NAME;
import GetIgniteCache.IGNITE_CACHE_ENTRY_KEY;
import GetIgniteCache.IGNITE_CONFIGURATION_FILE;
import GetIgniteCache.REL_FAILURE;
import GetIgniteCache.REL_SUCCESS;
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


public class ITGetIgniteCache {
    private static final String CACHE_NAME = "testCache";

    private static TestRunner runner;

    private static GetIgniteCache getIgniteCache;

    private static Map<String, String> properties1;

    private static HashMap<String, String> properties2;

    @Test
    public void testgetIgniteCacheOnTriggerFileConfigurationOneFlowFile() throws IOException, InterruptedException {
        ITGetIgniteCache.runner = TestRunners.newTestRunner(ITGetIgniteCache.getIgniteCache);
        ITGetIgniteCache.runner.setProperty(GetIgniteCache.CACHE_NAME, ITGetIgniteCache.CACHE_NAME);
        ITGetIgniteCache.runner.setProperty(IGNITE_CONFIGURATION_FILE, (("file:///" + (new File(".").getAbsolutePath())) + "/src/test/resources/test-ignite.xml"));
        ITGetIgniteCache.runner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        ITGetIgniteCache.runner.assertValid();
        ITGetIgniteCache.properties1.put("igniteKey", "key5");
        ITGetIgniteCache.runner.enqueue("test5".getBytes(), ITGetIgniteCache.properties1);
        ITGetIgniteCache.getIgniteCache.initialize(ITGetIgniteCache.runner.getProcessContext());
        ITGetIgniteCache.getIgniteCache.getIgniteCache().put("key5", "test5".getBytes());
        ITGetIgniteCache.runner.run(1, false, true);
        ITGetIgniteCache.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> sucessfulFlowFiles = ITGetIgniteCache.runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, sucessfulFlowFiles.size());
        List<MockFlowFile> failureFlowFiles = ITGetIgniteCache.runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, failureFlowFiles.size());
        final MockFlowFile out = ITGetIgniteCache.runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("test5".getBytes());
        Assert.assertArrayEquals("test5".getBytes(), ((byte[]) (ITGetIgniteCache.getIgniteCache.getIgniteCache().get("key5"))));
        ITGetIgniteCache.runner.shutdown();
    }

    @Test
    public void testgetIgniteCacheOnTriggerNoConfigurationTwoFlowFile() throws IOException, InterruptedException {
        ITGetIgniteCache.runner = TestRunners.newTestRunner(ITGetIgniteCache.getIgniteCache);
        ITGetIgniteCache.runner.setProperty(GetIgniteCache.CACHE_NAME, ITGetIgniteCache.CACHE_NAME);
        ITGetIgniteCache.runner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        ITGetIgniteCache.runner.assertValid();
        ITGetIgniteCache.properties1.put("igniteKey", "key51");
        ITGetIgniteCache.runner.enqueue("test1".getBytes(), ITGetIgniteCache.properties1);
        ITGetIgniteCache.properties2.put("igniteKey", "key52");
        ITGetIgniteCache.runner.enqueue("test2".getBytes(), ITGetIgniteCache.properties2);
        ITGetIgniteCache.getIgniteCache.initialize(ITGetIgniteCache.runner.getProcessContext());
        ITGetIgniteCache.getIgniteCache.getIgniteCache().put("key51", "test51".getBytes());
        ITGetIgniteCache.getIgniteCache.getIgniteCache().put("key52", "test52".getBytes());
        ITGetIgniteCache.runner.run(2, false, true);
        ITGetIgniteCache.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        List<MockFlowFile> sucessfulFlowFiles = ITGetIgniteCache.runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, sucessfulFlowFiles.size());
        List<MockFlowFile> failureFlowFiles = ITGetIgniteCache.runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, failureFlowFiles.size());
        final MockFlowFile out = ITGetIgniteCache.runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("test51".getBytes());
        Assert.assertArrayEquals("test51".getBytes(), ((byte[]) (ITGetIgniteCache.getIgniteCache.getIgniteCache().get("key51"))));
        final MockFlowFile out2 = ITGetIgniteCache.runner.getFlowFilesForRelationship(REL_SUCCESS).get(1);
        out2.assertContentEquals("test52".getBytes());
        Assert.assertArrayEquals("test52".getBytes(), ((byte[]) (ITGetIgniteCache.getIgniteCache.getIgniteCache().get("key52"))));
        ITGetIgniteCache.runner.shutdown();
    }

    @Test
    public void testgetIgniteCacheOnTriggerNoConfigurationTwoFlowFileStopStart2Times() throws IOException, InterruptedException {
        ITGetIgniteCache.runner = TestRunners.newTestRunner(ITGetIgniteCache.getIgniteCache);
        ITGetIgniteCache.runner.setProperty(GetIgniteCache.CACHE_NAME, ITGetIgniteCache.CACHE_NAME);
        ITGetIgniteCache.runner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        ITGetIgniteCache.runner.assertValid();
        ITGetIgniteCache.properties1.put("igniteKey", "key51");
        ITGetIgniteCache.runner.enqueue("test1".getBytes(), ITGetIgniteCache.properties1);
        ITGetIgniteCache.properties2.put("igniteKey", "key52");
        ITGetIgniteCache.runner.enqueue("test2".getBytes(), ITGetIgniteCache.properties2);
        ITGetIgniteCache.getIgniteCache.initialize(ITGetIgniteCache.runner.getProcessContext());
        ITGetIgniteCache.getIgniteCache.getIgniteCache().put("key51", "test51".getBytes());
        ITGetIgniteCache.getIgniteCache.getIgniteCache().put("key52", "test52".getBytes());
        ITGetIgniteCache.runner.run(2, false, true);
        ITGetIgniteCache.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        ITGetIgniteCache.getIgniteCache.closeIgniteCache();
        ITGetIgniteCache.runner.clearTransferState();
        // reinit and check first time
        ITGetIgniteCache.runner.assertValid();
        ITGetIgniteCache.properties1.put("igniteKey", "key51");
        ITGetIgniteCache.runner.enqueue("test1".getBytes(), ITGetIgniteCache.properties1);
        ITGetIgniteCache.properties2.put("igniteKey", "key52");
        ITGetIgniteCache.runner.enqueue("test2".getBytes(), ITGetIgniteCache.properties2);
        ITGetIgniteCache.getIgniteCache.initialize(ITGetIgniteCache.runner.getProcessContext());
        ITGetIgniteCache.runner.run(2, false, true);
        ITGetIgniteCache.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        ITGetIgniteCache.getIgniteCache.closeIgniteCache();
        ITGetIgniteCache.runner.clearTransferState();
        // reinit and check second time
        ITGetIgniteCache.runner.assertValid();
        ITGetIgniteCache.properties1.put("igniteKey", "key51");
        ITGetIgniteCache.runner.enqueue("test1".getBytes(), ITGetIgniteCache.properties1);
        ITGetIgniteCache.properties2.put("igniteKey", "key52");
        ITGetIgniteCache.runner.enqueue("test2".getBytes(), ITGetIgniteCache.properties2);
        ITGetIgniteCache.getIgniteCache.initialize(ITGetIgniteCache.runner.getProcessContext());
        ITGetIgniteCache.runner.run(2, false, true);
        ITGetIgniteCache.runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        ITGetIgniteCache.getIgniteCache.closeIgniteCache();
        ITGetIgniteCache.runner.clearTransferState();
    }
}

