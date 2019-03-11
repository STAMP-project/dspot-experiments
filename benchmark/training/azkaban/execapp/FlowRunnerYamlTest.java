/**
 * Copyright 2018 LinkedIn Corp.
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
package azkaban.execapp;


import ConfigurationKeys.AZKABAN_POLL_MODEL;
import Status.FAILED;
import Status.FAILED_FINISHING;
import Status.RUNNING;
import Status.SUCCEEDED;
import azkaban.alert.Alerter;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.ExecutionOptions;
import azkaban.executor.InteractiveTestJob;
import azkaban.utils.Props;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.Test;
import org.mockito.Mockito;


public class FlowRunnerYamlTest extends FlowRunnerTestBase {
    private static final String BASIC_FLOW_YAML_DIR = "basicflowwithoutendnode";

    private static final String FAIL_BASIC_FLOW_YAML_DIR = "failbasicflowwithoutendnode";

    private static final String EMBEDDED_FLOW_YAML_DIR = "embeddedflowwithoutendnode";

    private static final String ALERT_FLOW_YAML_DIR = "alertflow";

    private static final String BASIC_FLOW_NAME = "basic_flow";

    private static final String BASIC_FLOW_YAML_FILE = (FlowRunnerYamlTest.BASIC_FLOW_NAME) + ".flow";

    private static final String FAIL_BASIC_FLOW_NAME = "fail_basic_flow";

    private static final String FAIL_BASIC_FLOW_YAML_FILE = (FlowRunnerYamlTest.FAIL_BASIC_FLOW_NAME) + ".flow";

    private static final String EMBEDDED_FLOW_NAME = "embedded_flow";

    private static final String EMBEDDED_FLOW_YAML_FILE = (FlowRunnerYamlTest.EMBEDDED_FLOW_NAME) + ".flow";

    private static final String ALERT_FLOW_NAME = "alert_flow";

    private static final String ALERT_FLOW_YAML_FILE = (FlowRunnerYamlTest.ALERT_FLOW_NAME) + ".flow";

    private FlowRunnerTestUtil testUtil;

    @Test
    public void testBasicFlowWithoutEndNode() throws Exception {
        setUp(FlowRunnerYamlTest.BASIC_FLOW_YAML_DIR, FlowRunnerYamlTest.BASIC_FLOW_YAML_FILE);
        final HashMap<String, String> flowProps = new HashMap<>();
        this.runner = this.testUtil.createFromFlowMap(FlowRunnerYamlTest.BASIC_FLOW_NAME, flowProps);
        final ExecutableFlow flow = this.runner.getExecutableFlow();
        FlowRunnerTestUtil.startThread(this.runner);
        assertStatus("jobA", SUCCEEDED);
        assertStatus("jobB", SUCCEEDED);
        assertFlowStatus(flow, RUNNING);
        InteractiveTestJob.getTestJob("jobC").succeedJob();
        assertStatus("jobC", SUCCEEDED);
        assertFlowStatus(flow, SUCCEEDED);
    }

    @Test
    public void testEmbeddedFlowWithoutEndNode() throws Exception {
        setUp(FlowRunnerYamlTest.EMBEDDED_FLOW_YAML_DIR, FlowRunnerYamlTest.EMBEDDED_FLOW_YAML_FILE);
        final HashMap<String, String> flowProps = new HashMap<>();
        this.runner = this.testUtil.createFromFlowMap(FlowRunnerYamlTest.EMBEDDED_FLOW_NAME, flowProps);
        final ExecutableFlow flow = this.runner.getExecutableFlow();
        FlowRunnerTestUtil.startThread(this.runner);
        assertStatus("jobA", SUCCEEDED);
        assertStatus("embedded_flow1:jobB", SUCCEEDED);
        assertStatus("embedded_flow1:jobC", SUCCEEDED);
        assertStatus("embedded_flow1", SUCCEEDED);
        assertStatus("jobD", SUCCEEDED);
        assertFlowStatus(flow, SUCCEEDED);
    }

    @Test
    public void testAlertOnFlowFinished() throws Exception {
        setUp(FlowRunnerYamlTest.ALERT_FLOW_YAML_DIR, FlowRunnerYamlTest.ALERT_FLOW_YAML_FILE);
        final Alerter mailAlerter = Mockito.mock(Alerter.class);
        final ExecutionOptions executionOptions = new ExecutionOptions();
        executionOptions.setFailureEmails(Arrays.asList("test@example.com"));
        final Props azkabanProps = new Props();
        azkabanProps.put(AZKABAN_POLL_MODEL, "true");
        this.runner = this.testUtil.createFromFlowMap(FlowRunnerYamlTest.ALERT_FLOW_NAME, executionOptions, new HashMap(), azkabanProps);
        final ExecutableFlow flow = this.runner.getExecutableFlow();
        Mockito.when(this.runner.getAlerterHolder().get("email")).thenReturn(mailAlerter);
        FlowRunnerTestUtil.startThread(this.runner);
        InteractiveTestJob.getTestJob("jobA").failJob();
        InteractiveTestJob.getTestJob("jobB").failJob();
        InteractiveTestJob.getTestJob("jobC").succeedJob();
        assertFlowStatus(flow, FAILED);
        Mockito.verify(mailAlerter).alertOnError(flow, "Flow finished");
    }

    @Test
    public void testAlertOnFirstError() throws Exception {
        setUp(FlowRunnerYamlTest.ALERT_FLOW_YAML_DIR, FlowRunnerYamlTest.ALERT_FLOW_YAML_FILE);
        final Alerter mailAlerter = Mockito.mock(Alerter.class);
        final ExecutionOptions executionOptions = new ExecutionOptions();
        executionOptions.setNotifyOnFirstFailure(true);
        final Props azkabanProps = new Props();
        azkabanProps.put(AZKABAN_POLL_MODEL, "true");
        this.runner = this.testUtil.createFromFlowMap(FlowRunnerYamlTest.ALERT_FLOW_NAME, executionOptions, new HashMap(), azkabanProps);
        final ExecutableFlow flow = this.runner.getExecutableFlow();
        Mockito.when(this.runner.getAlerterHolder().get("email")).thenReturn(mailAlerter);
        FlowRunnerTestUtil.startThread(this.runner);
        InteractiveTestJob.getTestJob("jobA").failJob();
        assertFlowStatus(flow, FAILED_FINISHING);
        InteractiveTestJob.getTestJob("jobB").failJob();
        assertFlowStatus(flow, FAILED_FINISHING);
        InteractiveTestJob.getTestJob("jobC").succeedJob();
        assertFlowStatus(flow, FAILED);
        Mockito.verify(mailAlerter, Mockito.times(1)).alertOnFirstError(flow);
    }
}

