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
package org.apache.flink.table.client.cli;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.client.gateway.Executor;
import org.apache.flink.table.client.gateway.ProgramTargetDescriptor;
import org.apache.flink.table.client.gateway.ResultDescriptor;
import org.apache.flink.table.client.gateway.SessionContext;
import org.apache.flink.table.client.gateway.SqlExecutionException;
import org.apache.flink.table.client.gateway.TypedResult;
import org.apache.flink.types.Row;
import org.apache.flink.util.TestLogger;
import org.junit.Test;


/**
 * Tests for the {@link CliClient}.
 */
public class CliClientTest extends TestLogger {
    private static final String INSERT_INTO_STATEMENT = "INSERT INTO MyTable SELECT * FROM MyOtherTable";

    private static final String SELECT_STATEMENT = "SELECT * FROM MyOtherTable";

    @Test
    public void testUpdateSubmission() {
        verifyUpdateSubmission(CliClientTest.INSERT_INTO_STATEMENT, false, false);
    }

    @Test
    public void testFailedUpdateSubmission() {
        // fail at executor
        verifyUpdateSubmission(CliClientTest.INSERT_INTO_STATEMENT, true, true);
        // fail early in client
        verifyUpdateSubmission(CliClientTest.SELECT_STATEMENT, false, true);
    }

    @Test
    public void testSqlCompletion() throws IOException {
        verifySqlCompletion("", 0, Arrays.asList("SELECT", "QUIT;", "RESET;"), Collections.emptyList());
        verifySqlCompletion("SELEC", 5, Collections.singletonList("SELECT"), Collections.singletonList("QUIT;"));
        verifySqlCompletion("SELE", 0, Collections.singletonList("SELECT"), Collections.singletonList("QUIT;"));
        verifySqlCompletion("QU", 2, Collections.singletonList("QUIT;"), Collections.singletonList("SELECT"));
        verifySqlCompletion("qu", 2, Collections.singletonList("QUIT;"), Collections.singletonList("SELECT"));
        verifySqlCompletion("  qu", 2, Collections.singletonList("QUIT;"), Collections.singletonList("SELECT"));
        verifySqlCompletion("set ", 3, Collections.emptyList(), Collections.singletonList("SET"));
        verifySqlCompletion("show t ", 6, Collections.emptyList(), Collections.singletonList("SET"));
    }

    // --------------------------------------------------------------------------------------------
    private static class MockExecutor implements Executor {
        public boolean failExecution;

        public SessionContext receivedContext;

        public String receivedStatement;

        public int receivedPosition;

        @Override
        public void start() throws SqlExecutionException {
            // nothing to do
        }

        @Override
        public Map<String, String> getSessionProperties(SessionContext session) throws SqlExecutionException {
            return null;
        }

        @Override
        public List<String> listTables(SessionContext session) throws SqlExecutionException {
            return null;
        }

        @Override
        public List<String> listUserDefinedFunctions(SessionContext session) throws SqlExecutionException {
            return null;
        }

        @Override
        public TableSchema getTableSchema(SessionContext session, String name) throws SqlExecutionException {
            return null;
        }

        @Override
        public String explainStatement(SessionContext session, String statement) throws SqlExecutionException {
            return null;
        }

        @Override
        public List<String> completeStatement(SessionContext session, String statement, int position) {
            receivedContext = session;
            receivedStatement = statement;
            receivedPosition = position;
            return Arrays.asList("HintA", "Hint B");
        }

        @Override
        public ResultDescriptor executeQuery(SessionContext session, String query) throws SqlExecutionException {
            return null;
        }

        @Override
        public TypedResult<List<Tuple2<Boolean, Row>>> retrieveResultChanges(SessionContext session, String resultId) throws SqlExecutionException {
            return null;
        }

        @Override
        public TypedResult<Integer> snapshotResult(SessionContext session, String resultId, int pageSize) throws SqlExecutionException {
            return null;
        }

        @Override
        public List<Row> retrieveResultPage(String resultId, int page) throws SqlExecutionException {
            return null;
        }

        @Override
        public void cancelQuery(SessionContext session, String resultId) throws SqlExecutionException {
            // nothing to do
        }

        @Override
        public ProgramTargetDescriptor executeUpdate(SessionContext session, String statement) throws SqlExecutionException {
            receivedContext = session;
            receivedStatement = statement;
            if (failExecution) {
                throw new SqlExecutionException("Fail execution.");
            }
            return new ProgramTargetDescriptor("testClusterId", "testJobId", "http://testcluster:1234");
        }

        @Override
        public void validateSession(SessionContext session) throws SqlExecutionException {
            // nothing to do
        }

        @Override
        public void stop(SessionContext session) {
            // nothing to do
        }
    }
}

