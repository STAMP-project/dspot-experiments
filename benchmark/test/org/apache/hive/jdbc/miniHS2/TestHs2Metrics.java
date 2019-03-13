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
package org.apache.hive.jdbc.miniHS2;


import MetricsTestUtils.COUNTER;
import MetricsTestUtils.TIMER;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hive.common.metrics.MetricsTestUtils;
import org.apache.hadoop.hive.common.metrics.common.MetricsFactory;
import org.apache.hadoop.hive.common.metrics.metrics2.CodahaleMetrics;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveSemanticAnalyzerHook;
import org.apache.hadoop.hive.ql.parse.HiveSemanticAnalyzerHookContext;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hive.service.cli.CLIServiceClient;
import org.apache.hive.service.cli.SessionHandle;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests HiveServer2 metrics.
 */
public class TestHs2Metrics {
    private static MiniHS2 miniHS2;

    private static Map<String, String> confOverlay;

    // Check metrics during semantic analysis.
    public static class MetricCheckingHook implements HiveSemanticAnalyzerHook {
        @Override
        public ASTNode preAnalyze(HiveSemanticAnalyzerHookContext context, ASTNode ast) throws SemanticException {
            try {
                CodahaleMetrics metrics = ((CodahaleMetrics) (MetricsFactory.getInstance()));
                String json = metrics.dumpJson();
                // Pre-analyze hook is fired in the middle of these calls
                MetricsTestUtils.verifyMetricsJson(json, COUNTER, "active_calls_api_semanticAnalyze", 1);
                MetricsTestUtils.verifyMetricsJson(json, COUNTER, "active_calls_api_compile", 1);
                MetricsTestUtils.verifyMetricsJson(json, COUNTER, "active_calls_api_hs2_operation_RUNNING", 1);
                MetricsTestUtils.verifyMetricsJson(json, COUNTER, "active_calls_api_hs2_sql_operation_RUNNING", 1);
            } catch (Exception e) {
                throw new SemanticException("metrics verification failed", e);
            }
            return ast;
        }

        @Override
        public void postAnalyze(HiveSemanticAnalyzerHookContext context, List<Task<? extends Serializable>> rootTasks) throws SemanticException {
        }
    }

    @Test
    public void testMetrics() throws Exception {
        String tableName = "testMetrics";
        CLIServiceClient serviceClient = TestHs2Metrics.miniHS2.getServiceClient();
        SessionHandle sessHandle = serviceClient.openSession("foo", "bar");
        // Block on semantic analysis to check 'active_calls'
        serviceClient.executeStatement(sessHandle, (("CREATE TABLE " + tableName) + " (id INT)"), TestHs2Metrics.confOverlay);
        // check that all calls were recorded.
        CodahaleMetrics metrics = ((CodahaleMetrics) (MetricsFactory.getInstance()));
        String json = metrics.dumpJson();
        MetricsTestUtils.verifyMetricsJson(json, TIMER, "api_hs2_operation_INITIALIZED", 1);
        MetricsTestUtils.verifyMetricsJson(json, TIMER, "api_hs2_operation_PENDING", 1);
        MetricsTestUtils.verifyMetricsJson(json, TIMER, "api_hs2_operation_RUNNING", 1);
        MetricsTestUtils.verifyMetricsJson(json, COUNTER, "hs2_completed_operation_FINISHED", 1);
        MetricsTestUtils.verifyMetricsJson(json, TIMER, "api_hs2_sql_operation_PENDING", 1);
        MetricsTestUtils.verifyMetricsJson(json, TIMER, "api_hs2_sql_operation_RUNNING", 1);
        MetricsTestUtils.verifyMetricsJson(json, COUNTER, "hs2_completed_sql_operation_FINISHED", 1);
        // but there should be no more active calls.
        MetricsTestUtils.verifyMetricsJson(json, COUNTER, "active_calls_api_semanticAnalyze", 0);
        MetricsTestUtils.verifyMetricsJson(json, COUNTER, "active_calls_api_compile", 0);
        MetricsTestUtils.verifyMetricsJson(json, COUNTER, "active_calls_api_hs2_operation_RUNNING", 0);
        MetricsTestUtils.verifyMetricsJson(json, COUNTER, "active_calls_api_hs2_sql_operation_RUNNING", 0);
        serviceClient.closeSession(sessHandle);
    }

    @Test
    public void testClosedScopes() throws Exception {
        CLIServiceClient serviceClient = TestHs2Metrics.miniHS2.getServiceClient();
        SessionHandle sessHandle = serviceClient.openSession("foo", "bar");
        // this should error at analyze scope
        Exception expectedException = null;
        try {
            serviceClient.executeStatement(sessHandle, "select aaa", TestHs2Metrics.confOverlay);
        } catch (Exception e) {
            expectedException = e;
        }
        Assert.assertNotNull("Expected semantic exception", expectedException);
        // verify all scopes were recorded
        CodahaleMetrics metrics = ((CodahaleMetrics) (MetricsFactory.getInstance()));
        String json = metrics.dumpJson();
        MetricsTestUtils.verifyMetricsJson(json, TIMER, "api_parse", 1);
        MetricsTestUtils.verifyMetricsJson(json, TIMER, "api_semanticAnalyze", 1);
        // verify all scopes are closed.
        MetricsTestUtils.verifyMetricsJson(json, COUNTER, "active_calls_api_parse", 0);
        MetricsTestUtils.verifyMetricsJson(json, COUNTER, "active_calls_api_semanticAnalyze", 0);
        serviceClient.closeSession(sessHandle);
    }
}

