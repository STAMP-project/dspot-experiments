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
import GetIgniteCache.IGNITE_GET_FAILED_MISSING_KEY_MESSAGE;
import GetIgniteCache.IGNITE_GET_FAILED_REASON_ATTRIBUTE_KEY;
import GetIgniteCache.REL_FAILURE;
import GetIgniteCache.REL_SUCCESS;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;

import static GetIgniteCache.IGNITE_GET_FAILED_MESSAGE_PREFIX;


public class TestGetIgniteCache {
    private static final String CACHE_NAME = "testCache";

    private TestRunner getRunner;

    private GetIgniteCache getIgniteCache;

    private Map<String, String> properties1;

    private Map<String, String> properties2;

    private static Ignite ignite;

    @Test
    public void testGetIgniteCacheDefaultConfOneFlowFileWithPlainKey() throws IOException, InterruptedException {
        getRunner = TestRunners.newTestRunner(getIgniteCache);
        getRunner.setProperty(IGNITE_CACHE_ENTRY_KEY, "mykey");
        getRunner.assertValid();
        getRunner.enqueue(new byte[]{  });
        getIgniteCache.initialize(getRunner.getProcessContext());
        getIgniteCache.getIgniteCache().put("mykey", "test".getBytes());
        getRunner.run(1, false, true);
        getRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> getSucessfulFlowFiles = getRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, getSucessfulFlowFiles.size());
        List<MockFlowFile> getFailureFlowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, getFailureFlowFiles.size());
        final MockFlowFile getOut = getRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        getOut.assertContentEquals("test".getBytes());
        getRunner.shutdown();
    }

    @Test
    public void testGetIgniteCacheNullGetCacheThrowsException() throws IOException, InterruptedException {
        getIgniteCache = new GetIgniteCache() {
            @Override
            protected Ignite getIgnite() {
                return TestGetIgniteCache.ignite;
            }

            @Override
            protected IgniteCache<String, byte[]> getIgniteCache() {
                return null;
            }
        };
        getRunner = TestRunners.newTestRunner(getIgniteCache);
        getRunner.setProperty(IGNITE_CACHE_ENTRY_KEY, "mykey");
        getRunner.assertValid();
        getRunner.enqueue(new byte[]{  });
        getIgniteCache.initialize(getRunner.getProcessContext());
        getRunner.run(1, false, true);
        getRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> getSucessfulFlowFiles = getRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, getSucessfulFlowFiles.size());
        List<MockFlowFile> getFailureFlowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, getFailureFlowFiles.size());
        final MockFlowFile getOut = getRunner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        getOut.assertAttributeEquals(IGNITE_GET_FAILED_REASON_ATTRIBUTE_KEY, ((IGNITE_GET_FAILED_MESSAGE_PREFIX) + "java.lang.NullPointerException"));
        getRunner.shutdown();
    }

    @Test
    public void testGetIgniteCacheDefaultConfOneFlowFileWithKeyExpression() throws IOException, InterruptedException {
        getRunner = TestRunners.newTestRunner(getIgniteCache);
        getRunner.setProperty(GetIgniteCache.CACHE_NAME, TestGetIgniteCache.CACHE_NAME);
        getRunner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        getRunner.assertValid();
        getRunner.enqueue("".getBytes(), properties1);
        getIgniteCache.initialize(getRunner.getProcessContext());
        getIgniteCache.getIgniteCache().put("key1", "test".getBytes());
        getRunner.run(1, false, true);
        getRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> sucessfulFlowFiles = getRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, sucessfulFlowFiles.size());
        List<MockFlowFile> failureFlowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, failureFlowFiles.size());
        final MockFlowFile out = getRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("test".getBytes());
        getRunner.shutdown();
    }

    @Test
    public void testGetIgniteCacheDefaultConfTwoFlowFilesWithExpressionKeys() throws IOException, InterruptedException {
        getRunner = TestRunners.newTestRunner(getIgniteCache);
        getRunner.setProperty(GetIgniteCache.CACHE_NAME, TestGetIgniteCache.CACHE_NAME);
        getRunner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        getRunner.assertValid();
        getRunner.enqueue("".getBytes(), properties1);
        getRunner.enqueue("".getBytes(), properties2);
        getIgniteCache.initialize(getRunner.getProcessContext());
        getIgniteCache.getIgniteCache().put("key1", "test1".getBytes());
        getIgniteCache.getIgniteCache().put("key2", "test2".getBytes());
        getRunner.run(2, false, true);
        getRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        List<MockFlowFile> sucessfulFlowFiles = getRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, sucessfulFlowFiles.size());
        List<MockFlowFile> failureFlowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, failureFlowFiles.size());
        final MockFlowFile out1 = getRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out1.assertContentEquals("test1".getBytes());
        Assert.assertEquals("test1", new String(getIgniteCache.getIgniteCache().get("key1")));
        final MockFlowFile out2 = getRunner.getFlowFilesForRelationship(REL_SUCCESS).get(1);
        out2.assertContentEquals("test2".getBytes());
        Assert.assertArrayEquals("test2".getBytes(), ((byte[]) (getIgniteCache.getIgniteCache().get("key2"))));
        getRunner.shutdown();
    }

    @Test
    public void testGetIgniteCacheDefaultConfOneFlowFileNoKey() throws IOException, InterruptedException {
        getRunner = TestRunners.newTestRunner(getIgniteCache);
        getRunner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        getRunner.assertValid();
        properties1.clear();
        getRunner.enqueue("".getBytes(), properties1);
        getIgniteCache.initialize(getRunner.getProcessContext());
        getRunner.run(1, false, true);
        getRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> sucessfulFlowFiles = getRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, sucessfulFlowFiles.size());
        List<MockFlowFile> failureFlowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, failureFlowFiles.size());
        final MockFlowFile out = getRunner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        out.assertAttributeEquals(IGNITE_GET_FAILED_REASON_ATTRIBUTE_KEY, IGNITE_GET_FAILED_MISSING_KEY_MESSAGE);
        getRunner.shutdown();
    }

    @Test
    public void testGetIgniteCacheDefaultConfTwoFlowFilesNoKey() throws IOException, InterruptedException {
        getRunner = TestRunners.newTestRunner(getIgniteCache);
        getRunner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        getRunner.assertValid();
        properties1.clear();
        getRunner.enqueue("".getBytes(), properties1);
        getRunner.enqueue("".getBytes(), properties1);
        getIgniteCache.initialize(getRunner.getProcessContext());
        getRunner.run(2, false, true);
        getRunner.assertAllFlowFilesTransferred(REL_FAILURE, 2);
        List<MockFlowFile> sucessfulFlowFiles = getRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, sucessfulFlowFiles.size());
        List<MockFlowFile> failureFlowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(2, failureFlowFiles.size());
        final MockFlowFile out1 = getRunner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        out1.assertAttributeEquals(IGNITE_GET_FAILED_REASON_ATTRIBUTE_KEY, IGNITE_GET_FAILED_MISSING_KEY_MESSAGE);
        final MockFlowFile out2 = getRunner.getFlowFilesForRelationship(REL_FAILURE).get(1);
        out2.assertAttributeEquals(IGNITE_GET_FAILED_REASON_ATTRIBUTE_KEY, IGNITE_GET_FAILED_MISSING_KEY_MESSAGE);
        getRunner.shutdown();
    }

    @Test
    public void testGetIgniteCacheDefaultConfTwoFlowFileFirstNoKey() throws IOException, InterruptedException {
        getRunner = TestRunners.newTestRunner(getIgniteCache);
        getRunner.setProperty(GetIgniteCache.CACHE_NAME, TestGetIgniteCache.CACHE_NAME);
        getRunner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        getRunner.assertValid();
        getRunner.enqueue("".getBytes());
        getRunner.enqueue("".getBytes(), properties2);
        getIgniteCache.initialize(getRunner.getProcessContext());
        getIgniteCache.getIgniteCache().put("key2", "test2".getBytes());
        getRunner.run(2, false, true);
        List<MockFlowFile> sucessfulFlowFiles = getRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, sucessfulFlowFiles.size());
        List<MockFlowFile> failureFlowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, failureFlowFiles.size());
        final MockFlowFile out1 = getRunner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        out1.assertContentEquals("".getBytes());
        out1.assertAttributeEquals(IGNITE_GET_FAILED_REASON_ATTRIBUTE_KEY, IGNITE_GET_FAILED_MISSING_KEY_MESSAGE);
        final MockFlowFile out2 = getRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out2.assertContentEquals("test2".getBytes());
        Assert.assertArrayEquals("test2".getBytes(), ((byte[]) (getIgniteCache.getIgniteCache().get("key2"))));
        getRunner.shutdown();
    }

    @Test
    public void testGetIgniteCacheDefaultConfTwoFlowFileSecondNoKey() throws IOException, InterruptedException {
        getRunner = TestRunners.newTestRunner(getIgniteCache);
        getRunner.setProperty(GetIgniteCache.CACHE_NAME, TestGetIgniteCache.CACHE_NAME);
        getRunner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        getRunner.assertValid();
        getRunner.enqueue("".getBytes(), properties1);
        getRunner.enqueue("".getBytes());
        getIgniteCache.initialize(getRunner.getProcessContext());
        getIgniteCache.getIgniteCache().put("key1", "test1".getBytes());
        getRunner.run(2, false, true);
        List<MockFlowFile> sucessfulFlowFiles = getRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, sucessfulFlowFiles.size());
        List<MockFlowFile> failureFlowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, failureFlowFiles.size());
        final MockFlowFile out1 = getRunner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        out1.assertContentEquals("".getBytes());
        out1.assertAttributeEquals(IGNITE_GET_FAILED_REASON_ATTRIBUTE_KEY, IGNITE_GET_FAILED_MISSING_KEY_MESSAGE);
        final MockFlowFile out2 = getRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out2.assertContentEquals("test1".getBytes());
        Assert.assertArrayEquals("test1".getBytes(), ((byte[]) (getIgniteCache.getIgniteCache().get("key1"))));
        getRunner.shutdown();
    }

    @Test
    public void testGetIgniteCacheDefaultConfThreeFlowFilesOneOkSecondOkThirdNoExpressionKey() throws IOException, InterruptedException {
        getRunner = TestRunners.newTestRunner(getIgniteCache);
        getRunner.setProperty(GetIgniteCache.CACHE_NAME, TestGetIgniteCache.CACHE_NAME);
        getRunner.setProperty(IGNITE_CACHE_ENTRY_KEY, "${igniteKey}");
        getRunner.assertValid();
        getRunner.enqueue("".getBytes(), properties1);
        getRunner.enqueue("".getBytes(), properties2);
        getRunner.enqueue("".getBytes());
        getIgniteCache.initialize(getRunner.getProcessContext());
        getIgniteCache.getIgniteCache().put("key1", "test1".getBytes());
        getIgniteCache.getIgniteCache().put("key2", "test2".getBytes());
        getRunner.run(3, false, true);
        List<MockFlowFile> sucessfulFlowFiles = getRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, sucessfulFlowFiles.size());
        List<MockFlowFile> failureFlowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, failureFlowFiles.size());
        final MockFlowFile out1 = getRunner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        out1.assertContentEquals("".getBytes());
        out1.assertAttributeEquals(IGNITE_GET_FAILED_REASON_ATTRIBUTE_KEY, IGNITE_GET_FAILED_MISSING_KEY_MESSAGE);
        final MockFlowFile out2 = getRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out2.assertContentEquals("test1".getBytes());
        Assert.assertArrayEquals("test1".getBytes(), ((byte[]) (getIgniteCache.getIgniteCache().get("key1"))));
        final MockFlowFile out3 = getRunner.getFlowFilesForRelationship(REL_SUCCESS).get(1);
        out3.assertContentEquals("test2".getBytes());
        Assert.assertArrayEquals("test2".getBytes(), ((byte[]) (getIgniteCache.getIgniteCache().get("key2"))));
        getRunner.shutdown();
    }
}

