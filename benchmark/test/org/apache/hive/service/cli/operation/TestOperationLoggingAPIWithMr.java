/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.service.cli.operation;


import FetchOrientation.FETCH_FIRST;
import FetchOrientation.FETCH_NEXT;
import FetchType.LOG;
import OperationState.FINISHED;
import org.apache.hive.service.cli.OperationHandle;
import org.apache.hive.service.cli.OperationState;
import org.apache.hive.service.cli.OperationStatus;
import org.apache.hive.service.cli.RowSet;
import org.junit.Assert;
import org.junit.Test;


/**
 * TestOperationLoggingAPIWithMr
 * Test the FetchResults of TFetchType.LOG in thrift level in MR mode.
 */
public class TestOperationLoggingAPIWithMr extends OperationLoggingAPITestBase {
    @Test
    public void testFetchResultsOfLog() throws Exception {
        // verify whether the sql operation log is generated and fetch correctly.
        OperationHandle operationHandle = client.executeStatement(sessionHandle, sql, null);
        RowSet rowSetLog = client.fetchResults(operationHandle, FETCH_FIRST, 1000, LOG);
        verifyFetchedLog(rowSetLog, OperationLoggingAPITestBase.expectedLogsVerbose);
    }

    @Test
    public void testFetchResultsOfLogAsync() throws Exception {
        // verify whether the sql operation log is generated and fetch correctly in async mode.
        OperationHandle operationHandle = client.executeStatementAsync(sessionHandle, sql, null);
        // Poll on the operation status till the query is completed
        boolean isQueryRunning = true;
        long pollTimeout = (System.currentTimeMillis()) + 100000;
        OperationStatus opStatus;
        OperationState state = null;
        RowSet rowSetAccumulated = null;
        StringBuilder logs = new StringBuilder();
        while (isQueryRunning) {
            // Break if polling times out
            if ((System.currentTimeMillis()) > pollTimeout) {
                break;
            }
            opStatus = client.getOperationStatus(operationHandle, false);
            Assert.assertNotNull(opStatus);
            state = opStatus.getState();
            rowSetAccumulated = client.fetchResults(operationHandle, FETCH_NEXT, 2000, LOG);
            for (Object[] row : rowSetAccumulated) {
                logs.append(row[0]);
            }
            if ((((state == (OperationState.CANCELED)) || (state == (OperationState.CLOSED))) || (state == (OperationState.FINISHED))) || (state == (OperationState.ERROR))) {
                isQueryRunning = false;
            }
            Thread.sleep(10);
        } 
        // The sql should be completed now.
        Assert.assertEquals("Query should be finished", FINISHED, state);
        // Verify the accumulated logs
        verifyFetchedLogPost(logs.toString(), OperationLoggingAPITestBase.expectedLogsVerbose, true);
        // Verify the fetched logs from the beginning of the log file
        RowSet rowSet = client.fetchResults(operationHandle, FETCH_FIRST, 2000, LOG);
        verifyFetchedLog(rowSet, OperationLoggingAPITestBase.expectedLogsVerbose);
    }

    @Test
    public void testFetchResultsOfLogWithOrientation() throws Exception {
        // (FETCH_FIRST) execute a sql, and fetch its sql operation log as expected value
        OperationHandle operationHandle = client.executeStatement(sessionHandle, sql, null);
        RowSet rowSetLog = client.fetchResults(operationHandle, FETCH_FIRST, 1000, LOG);
        int expectedLogLength = rowSetLog.numRows();
        // (FETCH_NEXT) execute the same sql again,
        // and fetch the sql operation log with FETCH_NEXT orientation
        OperationHandle operationHandleWithOrientation = client.executeStatement(sessionHandle, sql, null);
        RowSet rowSetLogWithOrientation;
        int logLength = 0;
        int maxRows = calculateProperMaxRows(expectedLogLength);
        do {
            rowSetLogWithOrientation = client.fetchResults(operationHandleWithOrientation, FETCH_NEXT, maxRows, LOG);
            logLength += rowSetLogWithOrientation.numRows();
        } while ((rowSetLogWithOrientation.numRows()) == maxRows );
        Assert.assertEquals(expectedLogLength, logLength);
        // (FETCH_FIRST) fetch again from the same operation handle with FETCH_FIRST orientation
        rowSetLogWithOrientation = client.fetchResults(operationHandleWithOrientation, FETCH_FIRST, 1000, LOG);
        verifyFetchedLog(rowSetLogWithOrientation, OperationLoggingAPITestBase.expectedLogsVerbose);
    }
}

