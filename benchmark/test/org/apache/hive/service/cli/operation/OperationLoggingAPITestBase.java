/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.service.cli.operation;


import FetchOrientation.FETCH_FIRST;
import FetchType.LOG;
import HiveConf.ConfVars.HIVE_SERVER2_LOGGING_OPERATION_LOG_LOCATION;
import java.io.File;
import java.util.Map;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.apache.hive.service.cli.CLIServiceClient;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.OperationHandle;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.SessionHandle;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * OperationLoggingAPITestBase
 * Test the FetchResults of TFetchType.LOG in thrift level.
 * This is the base class.
 */
@Ignore
public abstract class OperationLoggingAPITestBase {
    protected static HiveConf hiveConf;

    protected static String tableName;

    private File dataFile;

    protected CLIServiceClient client;

    protected static MiniHS2 miniHS2 = null;

    protected static Map<String, String> confOverlay;

    protected SessionHandle sessionHandle;

    protected final String sql = "select * from " + (OperationLoggingAPITestBase.tableName);

    private final String sqlCntStar = "select count(*) from " + (OperationLoggingAPITestBase.tableName);

    protected static String[] expectedLogsVerbose;

    protected static String[] expectedLogsExecution;

    protected static String[] expectedLogsPerformance;

    @Test
    public void testFetchResultsOfLogWithVerboseMode() throws Exception {
        String queryString = "set hive.server2.logging.operation.level=verbose";
        client.executeStatement(sessionHandle, queryString, null);
        // verify whether the sql operation log is generated and fetch correctly.
        OperationHandle operationHandle = client.executeStatement(sessionHandle, sqlCntStar, null);
        RowSet rowSetLog = client.fetchResults(operationHandle, FETCH_FIRST, 1000, LOG);
        // Verbose Logs should contain everything, including execution and performance
        verifyFetchedLog(rowSetLog, OperationLoggingAPITestBase.expectedLogsVerbose);
        verifyFetchedLog(rowSetLog, OperationLoggingAPITestBase.expectedLogsExecution);
        verifyFetchedLog(rowSetLog, OperationLoggingAPITestBase.expectedLogsPerformance);
    }

    @Test
    public void testFetchResultsOfLogWithPerformanceMode() throws Exception {
        try {
            String queryString = "set hive.server2.logging.operation.level=performance";
            client.executeStatement(sessionHandle, queryString, null);
            // verify whether the sql operation log is generated and fetch correctly.
            OperationHandle operationHandle = client.executeStatement(sessionHandle, sqlCntStar, null);
            RowSet rowSetLog = client.fetchResults(operationHandle, FETCH_FIRST, 1000, LOG);
            // rowSetLog should contain execution as well as performance logs
            verifyFetchedLog(rowSetLog, OperationLoggingAPITestBase.expectedLogsExecution);
            verifyFetchedLog(rowSetLog, OperationLoggingAPITestBase.expectedLogsPerformance);
            verifyMissingContentsInFetchedLog(rowSetLog, OperationLoggingAPITestBase.expectedLogsVerbose);
        } finally {
            // Restore everything to default setup to avoid discrepancy between junit test runs
            String queryString2 = "set hive.server2.logging.operation.level=verbose";
            client.executeStatement(sessionHandle, queryString2, null);
        }
    }

    @Test
    public void testFetchResultsOfLogWithExecutionMode() throws Exception {
        try {
            String queryString = "set hive.server2.logging.operation.level=execution";
            client.executeStatement(sessionHandle, queryString, null);
            // verify whether the sql operation log is generated and fetch correctly.
            OperationHandle operationHandle = client.executeStatement(sessionHandle, sqlCntStar, null);
            RowSet rowSetLog = client.fetchResults(operationHandle, FETCH_FIRST, 1000, LOG);
            verifyFetchedLog(rowSetLog, OperationLoggingAPITestBase.expectedLogsExecution);
            verifyMissingContentsInFetchedLog(rowSetLog, OperationLoggingAPITestBase.expectedLogsPerformance);
            verifyMissingContentsInFetchedLog(rowSetLog, OperationLoggingAPITestBase.expectedLogsVerbose);
        } finally {
            // Restore everything to default setup to avoid discrepancy between junit test runs
            String queryString2 = "set hive.server2.logging.operation.level=verbose";
            client.executeStatement(sessionHandle, queryString2, null);
        }
    }

    @Test
    public void testFetchResultsOfLogWithNoneMode() throws Exception {
        try {
            String queryString = "set hive.server2.logging.operation.level=none";
            client.executeStatement(sessionHandle, queryString, null);
            // verify whether the sql operation log is generated and fetch correctly.
            OperationHandle operationHandle = client.executeStatement(sessionHandle, sqlCntStar, null);
            RowSet rowSetLog = client.fetchResults(operationHandle, FETCH_FIRST, 1000, LOG);
            // We should not get any rows.
            assert (rowSetLog.numRows()) == 0;
        } finally {
            // Restore everything to default setup to avoid discrepancy between junit test runs
            String queryString2 = "set hive.server2.logging.operation.level=verbose";
            client.executeStatement(sessionHandle, queryString2, null);
        }
    }

    @Test
    public void testFetchResultsOfLogCleanup() throws Exception {
        // Verify cleanup functionality.
        // Open a new session, since this case needs to close the session in the end.
        SessionHandle sessionHandleCleanup = setupSession();
        // prepare
        OperationHandle operationHandle = client.executeStatement(sessionHandleCleanup, sql, null);
        RowSet rowSetLog = client.fetchResults(operationHandle, FETCH_FIRST, 1000, LOG);
        verifyFetchedLog(rowSetLog, OperationLoggingAPITestBase.expectedLogsVerbose);
        File sessionLogDir = new File((((OperationLoggingAPITestBase.hiveConf.getVar(HIVE_SERVER2_LOGGING_OPERATION_LOG_LOCATION)) + (File.separator)) + (sessionHandleCleanup.getHandleIdentifier())));
        File operationLogFile = new File(sessionLogDir, operationHandle.getHandleIdentifier().toString());
        // check whether exception is thrown when fetching log from a closed operation.
        client.closeOperation(operationHandle);
        try {
            client.fetchResults(operationHandle, FETCH_FIRST, 1000, LOG);
            Assert.fail("Fetch should fail");
        } catch (HiveSQLException e) {
            Assert.assertTrue(e.getMessage().contains("Invalid OperationHandle:"));
        }
        // check whether operation log file is deleted.
        if (operationLogFile.exists()) {
            Assert.fail("Operation log file should be deleted.");
        }
        // check whether session log dir is deleted after session is closed.
        client.closeSession(sessionHandleCleanup);
        if (sessionLogDir.exists()) {
            Assert.fail("Session log dir should be deleted.");
        }
    }
}

