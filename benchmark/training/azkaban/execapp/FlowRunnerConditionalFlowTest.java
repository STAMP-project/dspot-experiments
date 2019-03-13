/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.execapp;


import Status.CANCELLED;
import Status.FAILED;
import Status.KILLED;
import Status.READY;
import Status.RUNNING;
import Status.SUCCEEDED;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.InteractiveTestJob;
import azkaban.project.Project;
import azkaban.utils.Props;
import java.util.HashMap;
import org.junit.Test;


public class FlowRunnerConditionalFlowTest extends FlowRunnerTestBase {
    private static final String FLOW_YAML_DIR = "conditionalflowyamltest";

    private static final String CONDITIONAL_FLOW_1 = "conditional_flow1";

    private static final String CONDITIONAL_FLOW_2 = "conditional_flow2";

    private static final String CONDITIONAL_FLOW_3 = "conditional_flow3";

    private static final String CONDITIONAL_FLOW_4 = "conditional_flow4";

    private static final String CONDITIONAL_FLOW_5 = "conditional_flow5";

    private static final String CONDITIONAL_FLOW_6 = "conditional_flow6";

    private static final String CONDITIONAL_FLOW_7 = "conditional_flow7";

    private FlowRunnerTestUtil testUtil;

    private Project project;

    @Test
    public void runFlowOnJobOutputCondition() throws Exception {
        final HashMap<String, String> flowProps = new HashMap<>();
        setUp(FlowRunnerConditionalFlowTest.CONDITIONAL_FLOW_2, flowProps);
        final ExecutableFlow flow = this.runner.getExecutableFlow();
        assertStatus(flow, "jobA", RUNNING);
        final Props generatedProperties = new Props();
        generatedProperties.put("key1", "value1");
        generatedProperties.put("key2", "value2");
        InteractiveTestJob.getTestJob("jobA").succeedJob(generatedProperties);
        assertStatus(flow, "jobA", SUCCEEDED);
        assertStatus(flow, "jobB", SUCCEEDED);
        assertStatus(flow, "jobC", CANCELLED);
        assertStatus(flow, "jobD", CANCELLED);
        assertFlowStatus(flow, KILLED);
    }

    @Test
    public void runFlowOnJobStatusAllFailed() throws Exception {
        final HashMap<String, String> flowProps = new HashMap<>();
        setUp(FlowRunnerConditionalFlowTest.CONDITIONAL_FLOW_4, flowProps);
        final ExecutableFlow flow = this.runner.getExecutableFlow();
        InteractiveTestJob.getTestJob("jobA").failJob();
        assertStatus(flow, "jobA", FAILED);
        assertStatus(flow, "jobB", RUNNING);
        assertStatus(flow, "jobC", READY);
        InteractiveTestJob.getTestJob("jobB").failJob();
        assertStatus(flow, "jobB", FAILED);
        assertStatus(flow, "jobC", SUCCEEDED);
        assertFlowStatus(flow, SUCCEEDED);
    }

    @Test
    public void runFlowOnJobStatusOneSuccess() throws Exception {
        final HashMap<String, String> flowProps = new HashMap<>();
        setUp(FlowRunnerConditionalFlowTest.CONDITIONAL_FLOW_5, flowProps);
        final ExecutableFlow flow = this.runner.getExecutableFlow();
        InteractiveTestJob.getTestJob("jobA").succeedJob();
        assertStatus(flow, "jobA", SUCCEEDED);
        assertStatus(flow, "jobB", RUNNING);
        assertStatus(flow, "jobC", READY);
        InteractiveTestJob.getTestJob("jobB").failJob();
        assertStatus(flow, "jobB", FAILED);
        assertStatus(flow, "jobC", SUCCEEDED);
        assertFlowStatus(flow, SUCCEEDED);
    }

    @Test
    public void runFlowOnBothJobStatusAndPropsCondition() throws Exception {
        final HashMap<String, String> flowProps = new HashMap<>();
        setUp(FlowRunnerConditionalFlowTest.CONDITIONAL_FLOW_6, flowProps);
        final ExecutableFlow flow = this.runner.getExecutableFlow();
        final Props generatedProperties = new Props();
        generatedProperties.put("props", "foo");
        InteractiveTestJob.getTestJob("jobA").succeedJob(generatedProperties);
        assertStatus(flow, "jobA", SUCCEEDED);
        assertStatus(flow, "jobB", SUCCEEDED);
        assertStatus(flow, "jobC", CANCELLED);
        assertStatus(flow, "jobD", SUCCEEDED);
        assertFlowStatus(flow, SUCCEEDED);
    }

    @Test
    public void runFlowOnJobStatusConditionNull() throws Exception {
        final HashMap<String, String> flowProps = new HashMap<>();
        setUp(FlowRunnerConditionalFlowTest.CONDITIONAL_FLOW_3, flowProps);
        final ExecutableFlow flow = this.runner.getExecutableFlow();
        flow.getExecutableNode("jobC").setConditionOnJobStatus(null);
        InteractiveTestJob.getTestJob("jobA").succeedJob();
        assertStatus(flow, "jobA", SUCCEEDED);
        InteractiveTestJob.getTestJob("jobB").succeedJob();
        assertStatus(flow, "jobB", SUCCEEDED);
        assertStatus(flow, "jobC", SUCCEEDED);
        assertFlowStatus(flow, SUCCEEDED);
    }
}

