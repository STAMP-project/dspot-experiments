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
package org.apache.nifi.processors.standard;


import MergeRecord.CORRELATION_ATTRIBUTE_NAME;
import MergeRecord.FRAGMENT_COUNT_ATTRIBUTE;
import MergeRecord.FRAGMENT_ID_ATTRIBUTE;
import MergeRecord.MAX_BIN_AGE;
import MergeRecord.MAX_BIN_COUNT;
import MergeRecord.MAX_RECORDS;
import MergeRecord.MAX_SIZE;
import MergeRecord.MERGE_STRATEGY;
import MergeRecord.MERGE_STRATEGY_DEFRAGMENT;
import MergeRecord.MIN_RECORDS;
import MergeRecord.MIN_SIZE;
import MergeRecord.REL_FAILURE;
import MergeRecord.REL_MERGED;
import MergeRecord.REL_ORIGINAL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.serialization.record.CommaSeparatedRecordReader;
import org.apache.nifi.serialization.record.MockRecordWriter;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestMergeRecord {
    private TestRunner runner;

    private CommaSeparatedRecordReader readerService;

    private MockRecordWriter writerService;

    @Test
    public void testSmallOutputIsFlushed() {
        runner.setProperty(MIN_RECORDS, "1");
        runner.setProperty(MAX_RECORDS, "1");
        runner.enqueue("Name, Age\nJohn, 35\nJane, 34");
        runner.run(1);
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        mff.assertAttributeEquals("record.count", "2");
        mff.assertContentEquals("header\nJohn,35\nJane,34\n");
        runner.getFlowFilesForRelationship(REL_ORIGINAL).forEach(( ff) -> assertEquals(mff.getAttribute(CoreAttributes.UUID.key()), ff.getAttribute(MergeRecord.MERGE_UUID_ATTRIBUTE)));
    }

    @Test
    public void testMergeSimple() {
        runner.setProperty(MIN_RECORDS, "2");
        runner.setProperty(MAX_RECORDS, "2");
        runner.enqueue("Name, Age\nJohn, 35");
        runner.enqueue("Name, Age\nJane, 34");
        runner.run(2);
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_ORIGINAL, 2);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        mff.assertAttributeEquals("record.count", "2");
        mff.assertContentEquals("header\nJohn,35\nJane,34\n");
        runner.getFlowFilesForRelationship(REL_ORIGINAL).forEach(( ff) -> assertEquals(mff.getAttribute(CoreAttributes.UUID.key()), ff.getAttribute(MergeRecord.MERGE_UUID_ATTRIBUTE)));
    }

    // Verify that FlowFiles are grouped with like schemas.
    @Test
    public void testDifferentSchema() {
        runner.setProperty(MIN_RECORDS, "2");
        runner.setProperty(MAX_RECORDS, "2");
        runner.enqueue("Name, Age\nJohn, 35");
        runner.enqueue("Name, Color\nJane, Red");
        runner.run(2, false, true);
        runner.assertTransferCount(REL_MERGED, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.enqueue("Name, Age\nJane, 34");
        runner.enqueue("Name, Color\nJohn, Blue");
        runner.run(2, true, false);
        runner.assertTransferCount(REL_MERGED, 2);
        runner.assertTransferCount(REL_ORIGINAL, 4);
        final List<MockFlowFile> mffs = runner.getFlowFilesForRelationship(REL_MERGED);
        Assert.assertEquals(1L, mffs.stream().filter(( ff) -> "2".equals(ff.getAttribute("record.count"))).filter(( ff) -> "header\nJohn,35\nJane,34\n".equals(new String(ff.toByteArray()))).count());
        Assert.assertEquals(1L, mffs.stream().filter(( ff) -> "2".equals(ff.getAttribute("record.count"))).filter(( ff) -> "header\nJane,Red\nJohn,Blue\n".equals(new String(ff.toByteArray()))).count());
    }

    @Test
    public void testFailureToParse() {
        runner.setProperty(MIN_RECORDS, "2");
        runner.setProperty(MAX_RECORDS, "3");
        readerService.failAfter(2);
        runner.enqueue("Name, Age\nJohn, 35");
        runner.enqueue("Name, Age\nJane, 34");
        runner.enqueue("Name, Age\nJake, 3");
        runner.run();
        // We have to route all of the FlowFiles in the same bin to the 'failure' relationship.
        // Otherwise, we may have read some of the records from the failing FlowFile and then
        // routed it to failure, which would result in some of its records moving on and others not.
        // This, in turn, would result in the same records being added to potentially many FlowFiles.
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 3);
    }

    @Test
    public void testDefragment() {
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        final Map<String, String> attr1 = new HashMap<>();
        attr1.put(FRAGMENT_COUNT_ATTRIBUTE, "2");
        attr1.put(FRAGMENT_ID_ATTRIBUTE, "1");
        final Map<String, String> attr2 = new HashMap<>();
        attr2.put(FRAGMENT_COUNT_ATTRIBUTE, "2");
        attr2.put(FRAGMENT_ID_ATTRIBUTE, "1");
        final Map<String, String> attr3 = new HashMap<>();
        attr3.put(FRAGMENT_COUNT_ATTRIBUTE, "2");
        attr3.put(FRAGMENT_ID_ATTRIBUTE, "2");
        final Map<String, String> attr4 = new HashMap<>();
        attr4.put(FRAGMENT_COUNT_ATTRIBUTE, "2");
        attr4.put(FRAGMENT_ID_ATTRIBUTE, "2");
        runner.enqueue("Name, Age\nJohn, 35", attr1);
        runner.enqueue("Name, Age\nJane, 34", attr2);
        runner.enqueue("Name, Age\nJake, 3", attr3);
        runner.enqueue("Name, Age\nJan, 2", attr4);
        runner.run(4);
        runner.assertTransferCount(REL_MERGED, 2);
        runner.assertTransferCount(REL_ORIGINAL, 4);
        final List<MockFlowFile> mffs = runner.getFlowFilesForRelationship(REL_MERGED);
        Assert.assertEquals(1L, mffs.stream().filter(( ff) -> "2".equals(ff.getAttribute("record.count"))).filter(( ff) -> "header\nJohn,35\nJane,34\n".equals(new String(ff.toByteArray()))).count());
        Assert.assertEquals(1L, mffs.stream().filter(( ff) -> "2".equals(ff.getAttribute("record.count"))).filter(( ff) -> "header\nJake,3\nJan,2\n".equals(new String(ff.toByteArray()))).count());
    }

    @Test
    public void testMinSize() {
        runner.setProperty(MIN_RECORDS, "2");
        runner.setProperty(MAX_RECORDS, "2");
        runner.setProperty(MIN_SIZE, "500 B");
        runner.enqueue("Name, Age\nJohn, 35");
        runner.enqueue("Name, Age\nJane, 34");
        runner.run();
        runner.assertTransferCount(REL_MERGED, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        final StringBuilder sb = new StringBuilder("Name, Age\n");
        for (int i = 0; i < 100; i++) {
            sb.append((((("Person " + i) + ", ") + i) + "\n"));
        }
        runner.enqueue(sb.toString());
        runner.run();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_ORIGINAL, 3);
    }

    @Test
    public void testValidation() {
        runner.setProperty(MIN_RECORDS, "103");
        runner.setProperty(MAX_RECORDS, "2");
        runner.setProperty(MIN_SIZE, "500 B");
        runner.assertNotValid();
        runner.setProperty(MIN_RECORDS, "2");
        runner.setProperty(MAX_RECORDS, "103");
        runner.assertValid();
    }

    @Test
    public void testMinRecords() {
        runner.setProperty(MIN_RECORDS, "103");
        runner.setProperty(MAX_RECORDS, "110");
        runner.setProperty(MIN_SIZE, "500 B");
        runner.enqueue("Name, Age\nJohn, 35");
        runner.enqueue("Name, Age\nJane, 34");
        final StringBuilder sb = new StringBuilder("Name, Age\n");
        for (int i = 0; i < 100; i++) {
            sb.append((((("Person " + i) + ", ") + i) + "\n"));
        }
        runner.enqueue(sb.toString());
        runner.run();
        runner.assertTransferCount(REL_MERGED, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.enqueue("Name, Age\nJohn, 35");
        runner.run(2);
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_ORIGINAL, 4);
    }

    @Test
    public void testMaxRecords() {
        runner.setProperty(MIN_RECORDS, "5");
        runner.setProperty(MAX_RECORDS, "10");
        for (int i = 0; i < 34; i++) {
            runner.enqueue("Name, Age\nJohn, 35");
        }
        runner.run();
        runner.assertTransferCount(REL_MERGED, 3);
        runner.assertTransferCount(REL_ORIGINAL, 30);
        Assert.assertEquals(4, runner.getQueueSize().getObjectCount());
        runner.getFlowFilesForRelationship(REL_MERGED).stream().forEach(( ff) -> ff.assertAttributeEquals("record.count", "10"));
    }

    @Test
    public void testMaxSize() {
        runner.setProperty(MIN_RECORDS, "5");
        runner.setProperty(MAX_SIZE, "100 B");
        for (int i = 0; i < 36; i++) {
            runner.enqueue("Name, Age\nJohnny, 5");
        }
        runner.run();
        runner.assertTransferCount(REL_MERGED, 3);
        runner.assertTransferCount(REL_ORIGINAL, 33);
        Assert.assertEquals(3, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void testBinCount() {
        runner.setProperty(MIN_RECORDS, "5");
        runner.setProperty(MAX_RECORDS, "10");
        runner.setProperty(MAX_BIN_COUNT, "5");
        runner.setProperty(CORRELATION_ATTRIBUTE_NAME, "correlationId");
        final Map<String, String> attrs = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            attrs.put("correlationId", String.valueOf(i));
            runner.enqueue(("Name, Age\nJohn, 3" + i), attrs);
        }
        runner.run(1, false);
        runner.assertTransferCount(REL_MERGED, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        attrs.put("correlationId", "5");
        runner.enqueue("Name, Age\nJohn, 35", attrs);
        Assert.assertEquals(5, getBinCount());
        runner.run(1, false, false);
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        Assert.assertEquals(5, getBinCount());
    }

    @Test
    public void testDefragmentOldestBinFailsWhenTooManyBins() {
        runner.setProperty(MIN_RECORDS, "5");
        runner.setProperty(MAX_RECORDS, "10");
        runner.setProperty(MAX_BIN_COUNT, "5");
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(FRAGMENT_COUNT_ATTRIBUTE, "5");
        for (int i = 0; i < 5; i++) {
            attrs.put(FRAGMENT_ID_ATTRIBUTE, String.valueOf(i));
            runner.enqueue(("Name, Age\nJohn, 3" + i), attrs);
        }
        runner.run(1, false);
        runner.assertTransferCount(REL_MERGED, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        attrs.put(FRAGMENT_ID_ATTRIBUTE, "5");
        runner.enqueue("Name, Age\nJohn, 35", attrs);
        Assert.assertEquals(5, getBinCount());
        runner.run(1, false, false);
        runner.assertTransferCount(REL_MERGED, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        Assert.assertEquals(5, getBinCount());
    }

    @Test
    public void testDefragmentExpiredBinFailsOnTimeout() throws InterruptedException {
        runner.setProperty(MAX_BIN_COUNT, "5");
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        runner.setProperty(MAX_BIN_AGE, "1 millis");
        final Map<String, String> attrs = new HashMap<>();
        attrs.put(FRAGMENT_COUNT_ATTRIBUTE, "5");
        attrs.put(FRAGMENT_ID_ATTRIBUTE, "0");
        runner.enqueue("Name, Age\nJohn, 30", attrs);
        runner.run(1, false);
        Thread.sleep(50L);
        runner.run(1, true, false);
        runner.assertTransferCount(REL_MERGED, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
    }
}

