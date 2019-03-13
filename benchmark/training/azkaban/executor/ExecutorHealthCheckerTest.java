/**
 * Copyright 2019 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the ?License?); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an ?AS IS? BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.executor;


import ConnectorParams.PING_ACTION;
import ConnectorParams.RESPONSE_ALIVE;
import ConnectorParams.STATUS_PARAM;
import Status.FAILED;
import Status.RUNNING;
import azkaban.alert.Alerter;
import azkaban.utils.Pair;
import azkaban.utils.Props;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test case for executor health checker.
 */
public class ExecutorHealthCheckerTest {
    private static final int EXECUTION_ID_11 = 11;

    private static final String AZ_ADMIN_ALERT_EMAIL = "az_admin1@foo.com,az_admin2@foo.com";

    private final Map<Integer, Pair<ExecutionReference, ExecutableFlow>> activeFlows = new HashMap<>();

    private ExecutorHealthChecker executorHealthChecker;

    private Props props;

    private ExecutorLoader loader;

    private ExecutorApiGateway apiGateway;

    private Alerter mailAlerter;

    private AlerterHolder alerterHolder;

    private ExecutableFlow flow1;

    private Executor executor1;

    /**
     * Test running flow is not finalized and alert email is not sent when executor is alive.
     */
    @Test
    public void checkExecutorHealthAlive() throws Exception {
        this.activeFlows.put(ExecutorHealthCheckerTest.EXECUTION_ID_11, new Pair(new ExecutionReference(ExecutorHealthCheckerTest.EXECUTION_ID_11, this.executor1), this.flow1));
        Mockito.when(this.apiGateway.callWithExecutionId(this.executor1.getHost(), this.executor1.getPort(), PING_ACTION, null, null)).thenReturn(ImmutableMap.of(STATUS_PARAM, RESPONSE_ALIVE));
        this.executorHealthChecker.checkExecutorHealth();
        assertThat(this.flow1.getStatus()).isEqualTo(RUNNING);
        Mockito.verifyZeroInteractions(this.alerterHolder);
    }

    /**
     * Test running flow is finalized when its executor is removed from DB.
     */
    @Test
    public void checkExecutorHealthExecutorIdRemoved() throws Exception {
        this.activeFlows.put(ExecutorHealthCheckerTest.EXECUTION_ID_11, new Pair(new ExecutionReference(ExecutorHealthCheckerTest.EXECUTION_ID_11, null), this.flow1));
        Mockito.when(this.loader.fetchExecutableFlow(ExecutorHealthCheckerTest.EXECUTION_ID_11)).thenReturn(this.flow1);
        this.executorHealthChecker.checkExecutorHealth();
        Mockito.verify(this.loader).updateExecutableFlow(this.flow1);
        assertThat(this.flow1.getStatus()).isEqualTo(FAILED);
    }

    /**
     * Test alert emails are sent when there are consecutive failures to contact the executor.
     */
    @Test
    public void checkExecutorHealthConsecutiveFailures() throws Exception {
        this.activeFlows.put(ExecutorHealthCheckerTest.EXECUTION_ID_11, new Pair(new ExecutionReference(ExecutorHealthCheckerTest.EXECUTION_ID_11, this.executor1), this.flow1));
        // Failed to ping executor. Failure count (=1) < MAX_FAILURE_COUNT (=2). Do not alert.
        this.executorHealthChecker.checkExecutorHealth();
        Mockito.verify(this.apiGateway).callWithExecutionId(this.executor1.getHost(), this.executor1.getPort(), PING_ACTION, null, null);
        Mockito.verifyZeroInteractions(this.alerterHolder);
        // Pinged executor successfully. Failure count (=0) < MAX_FAILURE_COUNT (=2). Do not alert.
        Mockito.when(this.apiGateway.callWithExecutionId(this.executor1.getHost(), this.executor1.getPort(), PING_ACTION, null, null)).thenReturn(ImmutableMap.of(STATUS_PARAM, RESPONSE_ALIVE));
        this.executorHealthChecker.checkExecutorHealth();
        Mockito.verifyZeroInteractions(this.alerterHolder);
        // Failed to ping executor. Failure count (=1) < MAX_FAILURE_COUNT (=2). Do not alert.
        Mockito.when(this.apiGateway.callWithExecutionId(this.executor1.getHost(), this.executor1.getPort(), PING_ACTION, null, null)).thenReturn(null);
        this.executorHealthChecker.checkExecutorHealth();
        Mockito.verifyZeroInteractions(this.alerterHolder);
        // Failed to ping executor again. Failure count (=2) = MAX_FAILURE_COUNT (=2). Alert AZ admin.
        this.executorHealthChecker.checkExecutorHealth();
        Mockito.verify(this.alerterHolder.get("email")).alertOnFailedUpdate(ArgumentMatchers.eq(this.executor1), ArgumentMatchers.eq(Arrays.asList(this.flow1)), ArgumentMatchers.any(ExecutorManagerException.class));
        assertThat(this.flow1.getExecutionOptions().getFailureEmails()).isEqualTo(Arrays.asList(ExecutorHealthCheckerTest.AZ_ADMIN_ALERT_EMAIL.split(",")));
    }
}

