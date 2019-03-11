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
import LogDivertAppender.QUERY_ROUTING_APPENDER;
import LogDivertAppenderForTest.TEST_QUERY_ROUTING_APPENDER;
import java.io.File;
import java.util.Map;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.apache.hive.service.cli.CLIServiceClient;
import org.apache.hive.service.cli.OperationHandle;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.SessionHandle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.AbstractLogEvent;
import org.apache.logging.log4j.core.Appender;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests to verify operation logging layout for different modes.
 */
public class TestOperationLoggingLayout {
    protected static HiveConf hiveConf;

    protected static String tableName;

    private File dataFile;

    protected CLIServiceClient client;

    protected static MiniHS2 miniHS2 = null;

    protected static Map<String, String> confOverlay;

    protected SessionHandle sessionHandle;

    protected final String sql = "select * from " + (TestOperationLoggingLayout.tableName);

    private final String sqlCntStar = "select count(*) from " + (TestOperationLoggingLayout.tableName);

    @Test
    public void testSwitchLogLayout() throws Exception {
        // verify whether the sql operation log is generated and fetch correctly.
        OperationHandle operationHandle = client.executeStatement(sessionHandle, sqlCntStar, null);
        RowSet rowSetLog = client.fetchResults(operationHandle, FETCH_FIRST, 1000, LOG);
        String queryId = getQueryId(rowSetLog);
        Assert.assertNotNull("Could not find query id, perhaps a logging message changed", queryId);
        checkAppenderState("before operation close ", QUERY_ROUTING_APPENDER, queryId, false);
        checkAppenderState("before operation close ", TEST_QUERY_ROUTING_APPENDER, queryId, false);
        client.closeOperation(operationHandle);
        checkAppenderState("after operation close ", QUERY_ROUTING_APPENDER, queryId, true);
        checkAppenderState("after operation close ", TEST_QUERY_ROUTING_APPENDER, queryId, true);
    }

    /**
     * Test to make sure that appending log event to HushableRandomAccessFileAppender even after
     * closing the corresponding operation would not throw an exception.
     */
    @Test
    public void testHushableRandomAccessFileAppender() throws Exception {
        // verify whether the sql operation log is generated and fetch correctly.
        OperationHandle operationHandle = client.executeStatement(sessionHandle, sqlCntStar, null);
        RowSet rowSetLog = client.fetchResults(operationHandle, FETCH_FIRST, 1000, LOG);
        Appender queryAppender;
        Appender testQueryAppender;
        String queryId = getQueryId(rowSetLog);
        Assert.assertNotNull("Could not find query id, perhaps a logging message changed", queryId);
        checkAppenderState("before operation close ", QUERY_ROUTING_APPENDER, queryId, false);
        queryAppender = getAppender(QUERY_ROUTING_APPENDER, queryId);
        checkAppenderState("before operation close ", TEST_QUERY_ROUTING_APPENDER, queryId, false);
        testQueryAppender = getAppender(TEST_QUERY_ROUTING_APPENDER, queryId);
        client.closeOperation(operationHandle);
        appendHushableRandomAccessFileAppender(queryAppender);
        appendHushableRandomAccessFileAppender(testQueryAppender);
    }

    /**
     * A minimal LogEvent implementation for testing
     */
    private static class LocalLogEvent extends AbstractLogEvent {
        LocalLogEvent() {
        }

        @Override
        public Level getLevel() {
            return Level.DEBUG;
        }
    }
}

