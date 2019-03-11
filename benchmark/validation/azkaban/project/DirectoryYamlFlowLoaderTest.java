/**
 * Copyright 2017 LinkedIn Corp.
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
package azkaban.project;


import Constants.JobProperties;
import azkaban.Constants;
import azkaban.test.executions.ExecutionsTestUtil;
import azkaban.utils.Props;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DirectoryYamlFlowLoaderTest {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryYamlFlowLoaderTest.class);

    private static final String BASIC_FLOW_YAML_DIR = "basicflowyamltest";

    private static final String MULTIPLE_FLOW_YAML_DIR = "multipleflowyamltest";

    private static final String RECURSIVE_DIRECTORY_FLOW_YAML_DIR = "recursivedirectoryyamltest";

    private static final String EMBEDDED_FLOW_YAML_DIR = "embeddedflowyamltest";

    private static final String MULTIPLE_EMBEDDED_FLOW_YAML_DIR = "multipleembeddedflowyamltest";

    private static final String CYCLE_FOUND_YAML_DIR = "cyclefoundyamltest";

    private static final String DUPLICATE_NODENAME_YAML_DIR = "duplicatenodenamesyamltest";

    private static final String DEPENDENCY_UNDEFINED_YAML_DIR = "dependencyundefinedyamltest";

    private static final String INVALID_JOBPROPS_YAML_DIR = "invalidjobpropsyamltest";

    private static final String NO_FLOW_YAML_DIR = "noflowyamltest";

    private static final String CONDITION_YAML_DIR = "conditionalflowyamltest";

    private static final String INVALID_CONDITION_YAML_DIR = "invalidconditionalflowyamltest";

    private static final String BASIC_FLOW_1 = "basic_flow";

    private static final String BASIC_FLOW_2 = "basic_flow2";

    private static final String EMBEDDED_FLOW = "embedded_flow";

    private static final String EMBEDDED_FLOW_1 = ("embedded_flow" + (Constants.PATH_DELIMITER)) + "embedded_flow1";

    private static final String EMBEDDED_FLOW_2 = ((("embedded_flow" + (Constants.PATH_DELIMITER)) + "embedded_flow1") + (Constants.PATH_DELIMITER)) + "embedded_flow2";

    private static final String EMBEDDED_FLOW_B = "embedded_flow_b";

    private static final String EMBEDDED_FLOW_B1 = ("embedded_flow_b" + (Constants.PATH_DELIMITER)) + "embedded_flow1";

    private static final String EMBEDDED_FLOW_B2 = ((("embedded_flow_b" + (Constants.PATH_DELIMITER)) + "embedded_flow1") + (Constants.PATH_DELIMITER)) + "embedded_flow2";

    private static final String CONDITIONAL_FLOW = "conditional_flow6";

    private static final String DUPLICATE_NODENAME_FLOW_FILE = "duplicate_nodename.flow";

    private static final String DEPENDENCY_UNDEFINED_FLOW_FILE = "dependency_undefined.flow";

    private static final String CYCLE_FOUND_FLOW = "cycle_found";

    private static final String CYCLE_FOUND_ERROR = "Cycles found.";

    private static final String SHELL_PWD = "invalid_jobprops:shell_pwd";

    private Project project;

    @Test
    public void testLoadBasicYamlFile() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.BASIC_FLOW_YAML_DIR));
        checkFlowLoaderProperties(loader, 0, 1, 1);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.BASIC_FLOW_1, 0, 4, 1, 3, null);
    }

    @Test
    public void testLoadMultipleYamlFiles() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.MULTIPLE_FLOW_YAML_DIR));
        checkFlowLoaderProperties(loader, 0, 2, 2);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.BASIC_FLOW_1, 0, 4, 1, 3, null);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.BASIC_FLOW_2, 0, 3, 1, 2, null);
    }

    @Test
    public void testLoadYamlFileRecursively() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.RECURSIVE_DIRECTORY_FLOW_YAML_DIR));
        checkFlowLoaderProperties(loader, 0, 2, 2);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.BASIC_FLOW_1, 0, 3, 1, 2, null);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.BASIC_FLOW_2, 0, 4, 1, 3, null);
    }

    @Test
    public void testLoadEmbeddedFlowYamlFile() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.EMBEDDED_FLOW_YAML_DIR));
        checkFlowLoaderProperties(loader, 0, 3, 3);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.EMBEDDED_FLOW, 0, 4, 1, 3, null);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.EMBEDDED_FLOW_1, 0, 4, 1, 3, null);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.EMBEDDED_FLOW_2, 0, 2, 1, 1, null);
    }

    @Test
    public void testLoadMultipleEmbeddedFlowYamlFiles() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.MULTIPLE_EMBEDDED_FLOW_YAML_DIR));
        checkFlowLoaderProperties(loader, 0, 6, 6);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.EMBEDDED_FLOW, 0, 4, 1, 3, null);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.EMBEDDED_FLOW_1, 0, 4, 1, 3, null);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.EMBEDDED_FLOW_2, 0, 2, 1, 1, null);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.EMBEDDED_FLOW_B, 0, 4, 1, 3, null);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.EMBEDDED_FLOW_B1, 0, 4, 1, 3, null);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.EMBEDDED_FLOW_B2, 0, 2, 1, 1, null);
    }

    @Test
    public void testLoadInvalidFlowYamlFileWithDuplicateNodeNames() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.DUPLICATE_NODENAME_YAML_DIR));
        checkFlowLoaderProperties(loader, 1, 0, 0);
        assertThat(loader.getErrors()).containsExactly((("Failed to validate nodeBean for " + (DirectoryYamlFlowLoaderTest.DUPLICATE_NODENAME_FLOW_FILE)) + ". Duplicate nodes found or dependency undefined."));
    }

    @Test
    public void testLoadInvalidFlowYamlFileWithUndefinedDependency() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.DEPENDENCY_UNDEFINED_YAML_DIR));
        checkFlowLoaderProperties(loader, 1, 0, 0);
        assertThat(loader.getErrors()).containsExactly((("Failed to validate nodeBean for " + (DirectoryYamlFlowLoaderTest.DEPENDENCY_UNDEFINED_FLOW_FILE)) + ". Duplicate nodes found or dependency undefined."));
    }

    @Test
    public void testLoadInvalidFlowYamlFileWithCycle() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.CYCLE_FOUND_YAML_DIR));
        checkFlowLoaderProperties(loader, 1, 1, 1);
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.CYCLE_FOUND_FLOW, 1, 4, 1, 4, DirectoryYamlFlowLoaderTest.CYCLE_FOUND_ERROR);
    }

    @Test
    public void testLoadFlowYamlFileWithInvalidJobProps() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.INVALID_JOBPROPS_YAML_DIR));
        checkFlowLoaderProperties(loader, 1, 1, 1);
        assertThat(loader.getErrors()).containsExactly(((((DirectoryYamlFlowLoaderTest.SHELL_PWD) + ": Xms value has exceeded the allowed limit (max Xms = ") + (JobProperties.MAX_XMS_DEFAULT)) + ")"));
    }

    @Test
    public void testLoadNoFlowYamlFile() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.NO_FLOW_YAML_DIR));
        checkFlowLoaderProperties(loader, 0, 0, 0);
    }

    @Test
    public void testFlowYamlFileWithValidCondition() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.CONDITION_YAML_DIR));
        assertThat(loader.getFlowMap().containsKey(DirectoryYamlFlowLoaderTest.CONDITIONAL_FLOW)).isTrue();
        checkFlowProperties(loader, DirectoryYamlFlowLoaderTest.CONDITIONAL_FLOW, 0, 4, 1, 4, null);
        assertThat(loader.getFlowMap().get(DirectoryYamlFlowLoaderTest.CONDITIONAL_FLOW).getNode("jobA").getCondition()).isNull();
        assertThat(loader.getFlowMap().get(DirectoryYamlFlowLoaderTest.CONDITIONAL_FLOW).getNode("jobB").getCondition()).isEqualTo("${jobA:props} == 'foo'");
        assertThat(loader.getFlowMap().get(DirectoryYamlFlowLoaderTest.CONDITIONAL_FLOW).getNode("jobC").getCondition()).isEqualTo("${jobA:props} == 'bar'");
        assertThat(loader.getFlowMap().get(DirectoryYamlFlowLoaderTest.CONDITIONAL_FLOW).getNode("jobD").getCondition()).isEqualTo("one_success && ${jobA:props} == 'foo'");
    }

    @Test
    public void testFlowYamlFileWithInvalidConditions() {
        final DirectoryYamlFlowLoader loader = new DirectoryYamlFlowLoader(new Props());
        loader.loadProjectFlow(this.project, ExecutionsTestUtil.getFlowDir(DirectoryYamlFlowLoaderTest.INVALID_CONDITION_YAML_DIR));
        checkFlowLoaderProperties(loader, 5, 5, 5);
        Assert.assertTrue(loader.getErrors().contains("Invalid condition for jobB: jobC doesn't exist in the flow."));
        Assert.assertTrue(loader.getErrors().contains("Invalid condition for jobA: should not define condition on its descendant node jobD."));
        Assert.assertTrue(loader.getErrors().contains("Invalid condition for jobB: operand is an empty string."));
        Assert.assertTrue(loader.getErrors().contains("Invalid condition for jobB: cannot resolve the condition. Please check the syntax for supported conditions."));
        Assert.assertTrue(loader.getErrors().contains("Invalid condition for jobB: cannot combine more than one conditionOnJobStatus macros."));
    }
}

