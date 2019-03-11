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


import DeleteHBaseRow.BATCH_SIZE;
import DeleteHBaseRow.FLOWFILE_FETCH_COUNT;
import DeleteHBaseRow.REL_FAILURE;
import DeleteHBaseRow.REL_SUCCESS;
import DeleteHBaseRow.ROW_ID;
import DeleteHBaseRow.ROW_ID_ATTR;
import DeleteHBaseRow.ROW_ID_CONTENT;
import DeleteHBaseRow.ROW_ID_LOCATION;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.junit.Assert;
import org.junit.Test;


public class TestDeleteHBaseRow extends DeleteTestBase {
    @Test
    public void testSimpleDelete() {
        List<String> ids = populateTable(100);
        runner.setProperty(BATCH_SIZE, "100");
        runner.setProperty(FLOWFILE_FETCH_COUNT, "100");
        for (String id : ids) {
            runner.enqueue(id);
        }
        runner.run(1, true);
        Assert.assertTrue("The mock client was not empty.", hBaseClient.isEmpty());
    }

    @Test
    public void testDeletesSeparatedByNewLines() {
        testSeparatedDeletes("\n");
    }

    @Test
    public void testDeletesSeparatedByCommas() {
        testSeparatedDeletes(",");
    }

    @Test
    public void testDeleteWithELSeparator() {
        Map<String, String> attrs = new HashMap<>();
        attrs.put("test.separator", "____");
        testSeparatedDeletes("${test.separator}", "____", attrs);
    }

    @Test
    public void testDeleteWithExpressionLanguage() {
        List<String> ids = populateTable(1000);
        for (String id : ids) {
            String[] parts = id.split("-");
            Map<String, String> attrs = new HashMap<>();
            for (int index = 0; index < (parts.length); index++) {
                attrs.put(String.format("part_%d", index), parts[index]);
            }
            runner.enqueue(id, attrs);
        }
        runner.setProperty(ROW_ID, "${part_0}-${part_1}-${part_2}-${part_3}-${part_4}");
        runner.setProperty(ROW_ID_LOCATION, ROW_ID_ATTR);
        runner.setProperty(BATCH_SIZE, "200");
        runner.run(1, true);
    }

    @Test
    public void testConnectivityErrorHandling() {
        List<String> ids = populateTable(100);
        for (String id : ids) {
            runner.enqueue(id);
        }
        boolean exception = false;
        try {
            hBaseClient.setThrowException(true);
            runner.run(1, true);
        } catch (Exception ex) {
            exception = true;
        } finally {
            hBaseClient.setThrowException(false);
        }
        Assert.assertFalse("An unhandled exception was caught.", exception);
    }

    @Test
    public void testRestartIndexAttribute() {
        List<String> ids = populateTable(500);
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < (ids.size()); index++) {
            sb.append(ids.get(index)).append((index < ((ids.size()) - 1) ? "," : ""));
        }
        runner.enqueue(sb.toString());
        runner.setProperty(ROW_ID_LOCATION, ROW_ID_CONTENT);
        Assert.assertTrue("There should have been 500 rows.", ((hBaseClient.size()) == 500));
        hBaseClient.setDeletePoint(20);
        hBaseClient.setThrowExceptionDuringBatchDelete(true);
        runner.run(1, true, true);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 0);
        Assert.assertTrue("Partially deleted", ((hBaseClient.size()) < 500));
        List<MockFlowFile> flowFile = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertNotNull("Missing restart.index attribute", flowFile.get(0).getAttribute("restart.index"));
        byte[] oldData = runner.getContentAsByteArray(flowFile.get(0));
        Map<String, String> attrs = new HashMap<>();
        attrs.put("restart.index", flowFile.get(0).getAttribute("restart.index"));
        runner.enqueue(oldData, attrs);
        hBaseClient.setDeletePoint((-1));
        hBaseClient.setThrowExceptionDuringBatchDelete(false);
        runner.clearTransferState();
        runner.run(1, true, true);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertTrue("The client should have been empty", hBaseClient.isEmpty());
        Assert.assertNull("The restart.index attribute should be null", flowFile.get(0).getAttribute("restart.index"));
    }
}

