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
package org.apache.nifi.processors.druid;


import PutDruidRecord.RECORD_COUNT;
import PutDruidRecord.REL_DROPPED;
import PutDruidRecord.REL_FAILURE;
import PutDruidRecord.REL_SUCCESS;
import RecordFieldType.INT;
import RecordFieldType.STRING;
import org.apache.nifi.controller.api.druid.DruidTranquilityService;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.serialization.record.MockRecordWriter;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class PutDruidRecordTest {
    private TestRunner runner;

    private DruidTranquilityService druidTranquilityController;

    private MockRecordParser recordReader;

    private MockRecordWriter recordWriter;

    @Test
    public void testEmptyRecord() throws Exception {
        recordReader.addSchemaField("name", STRING);
        recordReader.addSchemaField("age", INT);
        recordReader.addSchemaField("sport", STRING);
        runner.enqueue("");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_DROPPED, 0);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals(RECORD_COUNT, "0");
    }

    @Test
    public void testPutRecords() throws Exception {
        recordReader.addSchemaField("name", STRING);
        recordReader.addSchemaField("age", INT);
        recordReader.addSchemaField("sport", STRING);
        recordReader.addRecord("John Doe", 48, "Soccer");
        recordReader.addRecord("Jane Doe", 47, "Tennis");
        recordReader.addRecord("Sally Doe", 47, "Curling");// Will be dropped due to the "drop after 2" parameter on the MockDruidTranquilityController

        recordReader.addRecord("Jimmy Doe", 14, null);// Will fail due to the "fail after 3" parameter on the MockDruidTranquilityController

        recordReader.addRecord("Pizza Doe", 14, null);// Will fail due to the "fail after 3" parameter on the MockDruidTranquilityController

        recordReader.addRecord("Bad Record", "X", 13);// RecordWriter fail due to the "fail after 2" parameter on the MockRecordWriter, not written to output

        runner.enqueue("");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_DROPPED, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals(RECORD_COUNT, "2");
        flowFile = runner.getFlowFilesForRelationship(REL_DROPPED).get(0);
        flowFile.assertAttributeEquals(RECORD_COUNT, "1");
        flowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        flowFile.assertAttributeEquals(RECORD_COUNT, "2");
        // Assert a single SEND event present for the successful flow file
        Assert.assertEquals(1, runner.getProvenanceEvents().stream().filter(( e) -> ProvenanceEventType.SEND.equals(e.getEventType())).count());
    }
}

