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


import azkaban.project.Project;
import azkaban.test.executions.ExecutionsTestUtil;
import java.io.File;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test the property resolution of jobs in a flow.
 *
 * The tests are contained in execpropstest, and should be resolved in the following fashion, where
 * the later props take precedence over the previous ones.
 *
 * 1. Global props (set in the FlowRunner) 2. Shared job props (depends on job directory) 3. Flow
 * Override properties 4. Previous job outputs to the embedded flow (Only if contained in embedded
 * flow) 5. Embedded flow properties (Only if contained in embedded flow) 6. Previous job outputs
 * (if exists) 7. Job Props
 *
 * The test contains the following structure: job2 -> innerFlow (job1 -> job4 ) -> job3
 *
 * job2 and 4 are in nested directories so should have different shared properties than other jobs.
 */
public class FlowRunnerPropertyResolutionTest extends FlowRunnerTestBase {
    private static final String EXEC_FLOW_DIR = "execpropstest";

    private static final String FLOW_YAML_DIR = "loadpropsflowyamltest";

    private static final String FLOW_NAME = "job3";

    private static final String FLOW_YAML_FILE = (FlowRunnerPropertyResolutionTest.FLOW_NAME) + ".flow";

    private FlowRunnerTestUtil testUtil;

    /**
     * Tests the basic flow resolution. Flow is defined in execpropstest
     */
    @Test
    public void testPropertyResolution() throws Exception {
        this.testUtil = new FlowRunnerTestUtil(FlowRunnerPropertyResolutionTest.EXEC_FLOW_DIR, this.temporaryFolder);
        assertProperties(false);
    }

    /**
     * Tests the YAML flow resolution. Flow is defined in loadpropsflowyamltest
     */
    @Test
    public void testYamlFilePropertyResolution() throws Exception {
        this.testUtil = new FlowRunnerTestUtil(FlowRunnerPropertyResolutionTest.FLOW_YAML_DIR, this.temporaryFolder);
        final Project project = this.testUtil.getProject();
        Mockito.when(this.testUtil.getProjectLoader().isFlowFileUploaded(project.getId(), project.getVersion())).thenReturn(true);
        Mockito.when(this.testUtil.getProjectLoader().getLatestFlowVersion(project.getId(), project.getVersion(), FlowRunnerPropertyResolutionTest.FLOW_YAML_FILE)).thenReturn(1);
        Mockito.when(this.testUtil.getProjectLoader().getUploadedFlowFile(ArgumentMatchers.eq(project.getId()), ArgumentMatchers.eq(project.getVersion()), ArgumentMatchers.eq(FlowRunnerPropertyResolutionTest.FLOW_YAML_FILE), ArgumentMatchers.eq(1), ArgumentMatchers.any(File.class))).thenReturn(ExecutionsTestUtil.getFlowFile(FlowRunnerPropertyResolutionTest.FLOW_YAML_DIR, FlowRunnerPropertyResolutionTest.FLOW_YAML_FILE));
        assertProperties(true);
    }
}

