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
package org.apache.nifi.hbase;


import PutHBaseRecord.BYTES_ENCODING_VALUE;
import PutHBaseRecord.FIELD_ENCODING_STRATEGY;
import PutHBaseRecord.REL_FAILURE;
import PutHBaseRecord.REL_SUCCESS;
import PutHBaseRecord.ROW_FIELD_NAME;
import PutHBaseRecord.STRING_ENCODING_VALUE;
import java.util.Arrays;
import java.util.List;
import org.apache.nifi.hbase.put.PutColumn;
import org.apache.nifi.hbase.put.PutFlowFile;
import org.apache.nifi.hbase.util.Bytes;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestPutHBaseRecord {
    public static final String DEFAULT_TABLE_NAME = "nifi";

    public static final String DEFAULT_COLUMN_FAMILY = "family1";

    private static final List<Integer> KEYS = Arrays.asList(1, 2, 3, 4);

    private static final List<String> NAMES = Arrays.asList("rec1", "rec2", "rec3", "rec4");

    private static final List<Long> CODES = Arrays.asList(101L, 102L, 103L, 104L);

    @Test
    public void testByteEncodedPut() throws Exception {
        basicPutSetup(BYTES_ENCODING_VALUE, (PutColumn[] columns) -> {
            PutColumn name = columns[0];
            PutColumn code = columns[1];
            String nameVal = Bytes.toString(name.getBuffer());
            Long codeVal = Bytes.toLong(code.getBuffer());
            Assert.assertTrue("Name was not found", NAMES.contains(nameVal));
            Assert.assertTrue("Code was not found ", CODES.contains(codeVal));
        });
    }

    @Test
    public void testStringEncodedPut() throws Exception {
        basicPutSetup(STRING_ENCODING_VALUE, (PutColumn[] columns) -> {
            innertTest(columns);
        });
    }

    @Test
    public void testBatchOfOne() throws Exception {
        basicPutSetup(STRING_ENCODING_VALUE, (PutColumn[] columns) -> {
            innertTest(columns);
        }, "1", 1);
    }

    @Test
    public void testBatchOfTwo() throws Exception {
        basicPutSetup(STRING_ENCODING_VALUE, (PutColumn[] columns) -> {
            innertTest(columns);
        }, "2", 2);
    }

    @Test
    public void testFailure() throws Exception {
        Assert.assertEquals(1L, 1L);
        TestRunner runner = getTestRunner(TestPutHBaseRecord.DEFAULT_TABLE_NAME, TestPutHBaseRecord.DEFAULT_COLUMN_FAMILY, "2");
        runner.setProperty(ROW_FIELD_NAME, "id");
        runner.setProperty(FIELD_ENCODING_STRATEGY, STRING_ENCODING_VALUE);
        MockHBaseClientService client = HBaseTestUtil.getHBaseClientService(runner);
        client.setTestFailure(true);
        client.setFailureThreshold(2);
        generateTestData(runner);
        runner.enqueue("Test".getBytes("UTF-8"));// This is to coax the processor into reading the data in the reader.

        runner.run();
        List<MockFlowFile> result = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals("Size was wrong", result.size(), 1);
        Assert.assertEquals("Wrong # of PutFlowFiles", client.getFlowFilePuts().get("nifi").size(), 2);
        Assert.assertTrue(((runner.getFlowFilesForRelationship(REL_SUCCESS).size()) == 0));
        MockFlowFile mff = result.get(0);
        Assert.assertNotNull("Missing restart index attribute", mff.getAttribute("restart.index"));
        List<PutFlowFile> old = client.getFlowFilePuts().get("nifi");
        client.setTestFailure(false);
        runner.enqueue("test");
        runner.run();
        Assert.assertEquals("Size was wrong", result.size(), 1);
        Assert.assertEquals("Wrong # of PutFlowFiles", client.getFlowFilePuts().get("nifi").size(), 2);
        List<PutFlowFile> newPFF = client.getFlowFilePuts().get("nifi");
        for (PutFlowFile putFlowFile : old) {
            Assert.assertFalse("Duplication", newPFF.contains(putFlowFile));
        }
    }

    interface PutValidator {
        void handle(PutColumn... columns);
    }
}

