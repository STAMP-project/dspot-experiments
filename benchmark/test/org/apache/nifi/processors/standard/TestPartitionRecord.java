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


import PartitionRecord.REL_FAILURE;
import PartitionRecord.REL_ORIGINAL;
import PartitionRecord.REL_SUCCESS;
import java.io.IOException;
import java.util.List;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.serialization.record.MockRecordWriter;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestPartitionRecord {
    private TestRunner runner;

    private MockRecordParser readerService;

    private MockRecordWriter writerService;

    @Test
    public void groupByStringMixedNumberOfRecords() {
        runner.setProperty("person-name", "/name");
        readerService.addRecord("John", 28, null);
        readerService.addRecord("Jake", 49, null);
        readerService.addRecord("Mark", 19, null);
        readerService.addRecord("Jane", 20, null);
        readerService.addRecord("Jake", 14, null);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 4);
        final List<MockFlowFile> out = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(3L, out.stream().filter(( ff) -> ff.getAttribute("record.count").equals("1")).count());
        Assert.assertEquals(1L, out.stream().filter(( ff) -> ff.getAttribute("record.count").equals("2")).count());
        out.stream().filter(( ff) -> ff.getAttribute("record.count").equals("2")).forEach(( ff) -> ff.assertContentEquals("Jake,49,\nJake,14,\n"));
        for (final String name : new String[]{ "John", "Jake", "Mark", "Jane" }) {
            Assert.assertEquals(1L, out.stream().filter(( ff) -> ff.getAttribute("person-name").equals(name)).count());
        }
    }

    @Test
    public void testGroupByIntAllRecordsTogether() {
        runner.setProperty("age", "/age");
        readerService.addRecord("John", 30, null);
        readerService.addRecord("Jake", 30, null);
        readerService.addRecord("Mark", 30, null);
        readerService.addRecord("Jane", 30, null);
        readerService.addRecord("Jake", 30, null);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertAttributeEquals("record.count", "5");
        out.assertContentEquals("John,30,\nJake,30,\nMark,30,\nJane,30,\nJake,30,\n");
        out.assertAttributeEquals("age", "30");
    }

    @Test
    public void testGroupByMultipleFields() {
        runner.setProperty("age", "/age");
        runner.setProperty("name", "/name");
        readerService.addRecord("John", 30, null);
        readerService.addRecord("Jane", 30, null);
        readerService.addRecord("John", 30, null);
        readerService.addRecord("John", 31, null);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 3);
        final List<MockFlowFile> out = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1L, out.stream().filter(( mff) -> ((mff.isContentEqual("John,30,\nJohn,30,\n")) && (mff.isAttributeEqual("age", "30"))) && (mff.isAttributeEqual("name", "John"))).count());
        Assert.assertEquals(1L, out.stream().filter(( mff) -> ((mff.isContentEqual("Jane,30,\n")) && (mff.isAttributeEqual("age", "30"))) && (mff.isAttributeEqual("name", "Jane"))).count());
        Assert.assertEquals(1L, out.stream().filter(( mff) -> ((mff.isContentEqual("John,31,\n")) && (mff.isAttributeEqual("age", "31"))) && (mff.isAttributeEqual("name", "John"))).count());
    }

    @Test
    public void testGroupByArrayField() {
        runner.setProperty("sports", "/sports");
        readerService.addRecord("John", 30, new String[]{ "baseball" });
        readerService.addRecord("Jane", 30, new String[]{ "baseball" });
        readerService.addRecord("John", 30, new String[]{ "basketball" });
        readerService.addRecord("John", 31, new String[]{ "football" });
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 3);
        final List<MockFlowFile> out = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1L, out.stream().filter(( mff) -> mff.isContentEqual("John,30,[baseball]\nJane,30,[baseball]\n")).count());
        Assert.assertEquals(1L, out.stream().filter(( mff) -> mff.isContentEqual("John,30,[basketball]\n")).count());
        Assert.assertEquals(1L, out.stream().filter(( mff) -> mff.isContentEqual("John,31,[football]\n")).count());
        // There should be no sports attribute because it's not a scalar value
        Assert.assertTrue(out.stream().noneMatch(( mff) -> mff.getAttributes().containsKey("sports")));
    }

    @Test
    public void testReadFailure() throws IOException {
        runner.setProperty("sports", "/sports");
        readerService.failAfter(2);
        readerService.addRecord("John", 30, new String[]{ "baseball" });
        readerService.addRecord("Jane", 30, new String[]{ "baseball" });
        readerService.addRecord("John", 30, new String[]{ "basketball" });
        readerService.addRecord("John", 31, new String[]{ "football" });
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        runner.getFlowFilesForRelationship(REL_FAILURE).get(0).assertContentEquals(new byte[0]);
    }

    @Test
    public void testValueWrapperEqualityWithArrays() {
        final Object a = new String[]{ "baseball" };
        final Object b = new String[]{ "baseball" };
        Assert.assertEquals(new PartitionRecord.ValueWrapper(a), new PartitionRecord.ValueWrapper(b));
    }
}

