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
package azkaban.project;


import Constants.DEFAULT_FLOW_TRIGGER_MAX_WAIT_TIME;
import Constants.FLOW_NODE_TYPE;
import Constants.NODE_TYPE;
import FlowTriggerProps.CRON_SCHEDULE_TYPE;
import FlowTriggerProps.SCHEDULE_TYPE;
import FlowTriggerProps.SCHEDULE_VALUE;
import azkaban.Constants;
import azkaban.test.executions.ExecutionsTestUtil;
import azkaban.utils.Props;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Test;


public class NodeBeanLoaderTest {
    private static final String BASIC_FLOW_YML_TEST_DIR = "basicflowyamltest";

    private static final String BASIC_FLOW_NAME = "basic_flow";

    private static final String BASIC_FLOW_YML_FILE = (NodeBeanLoaderTest.BASIC_FLOW_NAME) + ".flow";

    private static final String EMBEDDED_FLOW_YML_TEST_DIR = "embeddedflowyamltest";

    private static final String EMBEDDED_FLOW_NAME = "embedded_flow";

    private static final String EMBEDDED_FLOW_YML_FILE = (NodeBeanLoaderTest.EMBEDDED_FLOW_NAME) + ".flow";

    private static final String TRIGGER_FLOW_YML_TEST_DIR = "flowtriggeryamltest";

    private static final String TRIGGER_FLOW_NAME = "flow_trigger";

    private static final String TRIGGER_FLOW_YML_FILE = (NodeBeanLoaderTest.TRIGGER_FLOW_NAME) + ".flow";

    private static final String FLOW_CONFIG_KEY = "flow-level-parameter";

    private static final String FLOW_CONFIG_VALUE = "value";

    private static final String SHELL_END = "shell_end";

    private static final String SHELL_ECHO = "shell_echo";

    private static final String SHELL_BASH = "shell_bash";

    private static final String SHELL_PWD = "shell_pwd";

    private static final String ECHO_COMMAND = "echo \"This is an echoed text.\"";

    private static final String ECHO_COMMAND_1 = "echo \"This is an echoed text from embedded_flow1.\"";

    private static final String ECHO_OVERRIDE = "echo \"Override job properties.\"";

    private static final String PWD_COMMAND = "pwd";

    private static final String BASH_COMMAND = "bash ./sample_script.sh";

    private static final String EMBEDDED_FLOW1 = "embedded_flow1";

    private static final String EMBEDDED_FLOW2 = "embedded_flow2";

    private static final String TYPE_NOOP = "noop";

    private static final String TYPE_COMMAND = "command";

    private static final int MAX_WAIT_MINS = 5;

    private static final String CRON_EXPRESSION = "0 0 1 ? * *";

    private static final String TRIGGER_NAME_1 = "search-impression";

    private static final String TRIGGER_NAME_2 = "other-name";

    private static final String TRIGGER_TYPE = "dali-dataset";

    private static final ImmutableMap<String, String> PARAMS_1 = ImmutableMap.of("view", "search_mp_versioned.search_impression_event_0_0_47", "delay", "1", "window", "1", "unit", "daily", "filter", "is_guest=0");

    private static final ImmutableMap<String, String> PARAMS_2 = ImmutableMap.of("view", "another dataset", "delay", "1", "window", "7");

    @Test
    public void testLoadNodeBeanForBasicFlow() throws Exception {
        final NodeBeanLoader loader = new NodeBeanLoader();
        final NodeBean nodeBean = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.BASIC_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.BASIC_FLOW_YML_FILE));
        validateNodeBean(nodeBean, NodeBeanLoaderTest.BASIC_FLOW_NAME, FLOW_NODE_TYPE, NodeBeanLoaderTest.FLOW_CONFIG_KEY, NodeBeanLoaderTest.FLOW_CONFIG_VALUE, 4, null);
        validateNodeBean(nodeBean.getNodes().get(0), NodeBeanLoaderTest.SHELL_END, NodeBeanLoaderTest.TYPE_NOOP, null, null, 0, Arrays.asList(NodeBeanLoaderTest.SHELL_PWD, NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.SHELL_BASH));
        validateNodeBean(nodeBean.getNodes().get(1), NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.ECHO_COMMAND, 0, null);
    }

    @Test
    public void testLoadNodeBeanForEmbeddedFlow() throws Exception {
        final NodeBeanLoader loader = new NodeBeanLoader();
        final NodeBean nodeBean = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.EMBEDDED_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.EMBEDDED_FLOW_YML_FILE));
        validateNodeBean(nodeBean, NodeBeanLoaderTest.EMBEDDED_FLOW_NAME, FLOW_NODE_TYPE, NodeBeanLoaderTest.FLOW_CONFIG_KEY, NodeBeanLoaderTest.FLOW_CONFIG_VALUE, 4, null);
        validateNodeBean(nodeBean.getNodes().get(0), NodeBeanLoaderTest.SHELL_END, NodeBeanLoaderTest.TYPE_NOOP, null, null, 0, Arrays.asList(NodeBeanLoaderTest.SHELL_PWD, NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.EMBEDDED_FLOW1));
        validateNodeBean(nodeBean.getNodes().get(1), NodeBeanLoaderTest.SHELL_PWD, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.PWD_COMMAND, 0, null);
        validateNodeBean(nodeBean.getNodes().get(2), NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.ECHO_COMMAND, 0, null);
        validateNodeBean(nodeBean.getNodes().get(3), NodeBeanLoaderTest.EMBEDDED_FLOW1, FLOW_NODE_TYPE, NodeBeanLoaderTest.FLOW_CONFIG_KEY, NodeBeanLoaderTest.FLOW_CONFIG_VALUE, 4, null);
        // Verify nodes in embedded_flow1 are loaded correctly.
        final NodeBean embeddedNodeBean1 = nodeBean.getNodes().get(3);
        validateNodeBean(embeddedNodeBean1.getNodes().get(0), NodeBeanLoaderTest.SHELL_END, NodeBeanLoaderTest.TYPE_NOOP, null, null, 0, Arrays.asList(NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.EMBEDDED_FLOW2));
        validateNodeBean(embeddedNodeBean1.getNodes().get(1), NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.ECHO_COMMAND_1, 0, null);
        validateNodeBean(embeddedNodeBean1.getNodes().get(2), NodeBeanLoaderTest.EMBEDDED_FLOW2, FLOW_NODE_TYPE, NodeBeanLoaderTest.FLOW_CONFIG_KEY, NodeBeanLoaderTest.FLOW_CONFIG_VALUE, 2, Arrays.asList(NodeBeanLoaderTest.SHELL_BASH));
        validateNodeBean(embeddedNodeBean1.getNodes().get(3), NodeBeanLoaderTest.SHELL_BASH, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.BASH_COMMAND, 0, null);
        // Verify nodes in embedded_flow2 are loaded correctly.
        validateNodeBean(embeddedNodeBean1.getNodes().get(2).getNodes().get(0), NodeBeanLoaderTest.SHELL_END, NodeBeanLoaderTest.TYPE_NOOP, null, null, 0, Arrays.asList(NodeBeanLoaderTest.SHELL_PWD));
        validateNodeBean(embeddedNodeBean1.getNodes().get(2).getNodes().get(1), NodeBeanLoaderTest.SHELL_PWD, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.PWD_COMMAND, 0, null);
    }

    @Test
    public void testLoadNodeBeanForFlowTrigger() throws Exception {
        final NodeBeanLoader loader = new NodeBeanLoader();
        final NodeBean nodeBean = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.TRIGGER_FLOW_YML_FILE));
        final Map<String, String> schedule = ImmutableMap.of(SCHEDULE_TYPE, CRON_SCHEDULE_TYPE, SCHEDULE_VALUE, NodeBeanLoaderTest.CRON_EXPRESSION);
        validateFlowTriggerBean(nodeBean.getTrigger(), NodeBeanLoaderTest.MAX_WAIT_MINS, schedule, 2);
        final List<TriggerDependencyBean> triggerDependencyBeans = nodeBean.getTrigger().getTriggerDependencies();
        validateTriggerDependencyBean(triggerDependencyBeans.get(0), NodeBeanLoaderTest.TRIGGER_NAME_1, NodeBeanLoaderTest.TRIGGER_TYPE, NodeBeanLoaderTest.PARAMS_1);
        validateTriggerDependencyBean(triggerDependencyBeans.get(1), NodeBeanLoaderTest.TRIGGER_NAME_2, NodeBeanLoaderTest.TRIGGER_TYPE, NodeBeanLoaderTest.PARAMS_2);
    }

    @Test
    public void testToBasicAzkabanFlow() throws Exception {
        final NodeBeanLoader loader = new NodeBeanLoader();
        final NodeBean nodeBean = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.BASIC_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.BASIC_FLOW_YML_FILE));
        final AzkabanFlow flow = ((AzkabanFlow) (loader.toAzkabanNode(nodeBean)));
        final Props props = new Props();
        props.put(NODE_TYPE, FLOW_NODE_TYPE);
        props.put(NodeBeanLoaderTest.FLOW_CONFIG_KEY, NodeBeanLoaderTest.FLOW_CONFIG_VALUE);
        validateAzkabanNode(flow, NodeBeanLoaderTest.BASIC_FLOW_NAME, FLOW_NODE_TYPE, props, Arrays.asList(NodeBeanLoaderTest.SHELL_END, NodeBeanLoaderTest.SHELL_PWD, NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.SHELL_BASH), null);
        final Props props1 = new Props();
        props1.put(NODE_TYPE, NodeBeanLoaderTest.TYPE_NOOP);
        validateAzkabanNode(flow.getNode(NodeBeanLoaderTest.SHELL_END), NodeBeanLoaderTest.SHELL_END, NodeBeanLoaderTest.TYPE_NOOP, props1, null, Arrays.asList(NodeBeanLoaderTest.SHELL_PWD, NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.SHELL_BASH));
        final Props props2 = new Props();
        props2.put(NODE_TYPE, NodeBeanLoaderTest.TYPE_COMMAND);
        props2.put(NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.ECHO_COMMAND);
        validateAzkabanNode(flow.getNode(NodeBeanLoaderTest.SHELL_ECHO), NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.TYPE_COMMAND, props2, null, null);
    }

    @Test
    public void testToEmbeddedAzkabanFlow() throws Exception {
        final NodeBeanLoader loader = new NodeBeanLoader();
        final NodeBean nodeBean = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.EMBEDDED_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.EMBEDDED_FLOW_YML_FILE));
        final AzkabanFlow flow = ((AzkabanFlow) (loader.toAzkabanNode(nodeBean)));
        final Props props = new Props();
        props.put(NODE_TYPE, FLOW_NODE_TYPE);
        props.put(NodeBeanLoaderTest.FLOW_CONFIG_KEY, NodeBeanLoaderTest.FLOW_CONFIG_VALUE);
        validateAzkabanNode(flow, NodeBeanLoaderTest.EMBEDDED_FLOW_NAME, FLOW_NODE_TYPE, props, Arrays.asList(NodeBeanLoaderTest.SHELL_END, NodeBeanLoaderTest.SHELL_PWD, NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.EMBEDDED_FLOW1), null);
        final Props props1 = new Props();
        props1.put(NODE_TYPE, NodeBeanLoaderTest.TYPE_NOOP);
        validateAzkabanNode(flow.getNode(NodeBeanLoaderTest.SHELL_END), NodeBeanLoaderTest.SHELL_END, NodeBeanLoaderTest.TYPE_NOOP, props1, null, Arrays.asList(NodeBeanLoaderTest.SHELL_PWD, NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.EMBEDDED_FLOW1));
        final Props props2 = new Props();
        props2.put(NODE_TYPE, FLOW_NODE_TYPE);
        props2.put(NodeBeanLoaderTest.FLOW_CONFIG_KEY, NodeBeanLoaderTest.FLOW_CONFIG_VALUE);
        final AzkabanFlow embeddedFlow1 = ((AzkabanFlow) (flow.getNode(NodeBeanLoaderTest.EMBEDDED_FLOW1)));
        validateAzkabanNode(embeddedFlow1, NodeBeanLoaderTest.EMBEDDED_FLOW1, FLOW_NODE_TYPE, props2, Arrays.asList(NodeBeanLoaderTest.SHELL_END, NodeBeanLoaderTest.SHELL_BASH, NodeBeanLoaderTest.SHELL_ECHO, NodeBeanLoaderTest.EMBEDDED_FLOW2), null);
        final AzkabanFlow embeddedFlow2 = ((AzkabanFlow) (embeddedFlow1.getNode(NodeBeanLoaderTest.EMBEDDED_FLOW2)));
        validateAzkabanNode(embeddedFlow2, NodeBeanLoaderTest.EMBEDDED_FLOW2, FLOW_NODE_TYPE, props2, Arrays.asList(NodeBeanLoaderTest.SHELL_END, NodeBeanLoaderTest.SHELL_PWD), Arrays.asList(NodeBeanLoaderTest.SHELL_BASH));
    }

    @Test
    public void testToFlowTrigger() throws Exception {
        final NodeBeanLoader loader = new NodeBeanLoader();
        final NodeBean nodeBean = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.TRIGGER_FLOW_YML_FILE));
        final FlowTrigger flowTrigger = loader.toFlowTrigger(nodeBean.getTrigger());
        validateFlowTrigger(flowTrigger, NodeBeanLoaderTest.MAX_WAIT_MINS, NodeBeanLoaderTest.CRON_EXPRESSION, 2);
        validateTriggerDependency(flowTrigger, NodeBeanLoaderTest.TRIGGER_NAME_1, NodeBeanLoaderTest.TRIGGER_TYPE, NodeBeanLoaderTest.PARAMS_1);
        validateTriggerDependency(flowTrigger, NodeBeanLoaderTest.TRIGGER_NAME_2, NodeBeanLoaderTest.TRIGGER_TYPE, NodeBeanLoaderTest.PARAMS_2);
    }

    @Test
    public void testFlowTriggerMaxWaitMinValidation() throws Exception {
        final NodeBeanLoader loader = new NodeBeanLoader();
        final NodeBean nodeBean = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_no_max_wait_min.flow"));
        assertThatThrownBy(() -> loader.toFlowTrigger(nodeBean.getTrigger())).isInstanceOf(IllegalArgumentException.class).hasMessage("max wait min cannot be null unless no dependency is defined");
        final NodeBean nodeBean2 = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_zero_max_wait_min.flow"));
        assertThatThrownBy(() -> loader.toFlowTrigger(nodeBean2.getTrigger())).isInstanceOf(IllegalArgumentException.class).hasMessage(("max wait min must be at least 1" + " min(s)"));
        NodeBean nodeBean3 = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_large_max_wait_min.flow"));
        FlowTrigger flowTrigger = loader.toFlowTrigger(nodeBean3.getTrigger());
        assertThat(flowTrigger.getMaxWaitDuration().orElse(null)).isEqualTo(DEFAULT_FLOW_TRIGGER_MAX_WAIT_TIME);
        nodeBean3 = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_no_max_wait_min_zero_dep.flow"));
        flowTrigger = loader.toFlowTrigger(nodeBean3.getTrigger());
        assertThat(flowTrigger.getMaxWaitDuration().orElse(null)).isEqualTo(null);
        nodeBean3 = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_max_wait_min_zero_dep.flow"));
        flowTrigger = loader.toFlowTrigger(nodeBean3.getTrigger());
        assertThat(flowTrigger.getMaxWaitDuration().get().toMinutes()).isEqualTo(5);
    }

    @Test
    public void testFlowTriggerDepDuplicationValidation() throws Exception {
        final NodeBeanLoader loader = new NodeBeanLoader();
        final NodeBean nodeBean = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_duplicate_dep_props.flow"));
        assertThatThrownBy(() -> loader.toFlowTrigger(nodeBean.getTrigger())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testFlowTriggerRequireDepNameAndType() throws Exception {
        final NodeBeanLoader loader = new NodeBeanLoader();
        final NodeBean nodeBean = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_without_dep_name.flow"));
        assertThatThrownBy(() -> loader.toFlowTrigger(nodeBean.getTrigger())).isInstanceOf(NullPointerException.class);
        final NodeBean nodeBean2 = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_without_dep_type.flow"));
        assertThatThrownBy(() -> loader.toFlowTrigger(nodeBean2.getTrigger())).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testFlowTriggerScheduleValidation() throws Exception {
        final NodeBeanLoader loader = new NodeBeanLoader();
        final NodeBean nodeBean = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_invalid_cron_expression.flow"));
        assertThatThrownBy(() -> loader.toFlowTrigger(nodeBean.getTrigger())).isInstanceOf(IllegalArgumentException.class);
        final NodeBean nodeBean1 = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_second_level_cron_expression1.flow"));
        assertThatThrownBy(() -> loader.toFlowTrigger(nodeBean1.getTrigger())).isInstanceOf(IllegalArgumentException.class);
        final NodeBean nodeBean2 = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_second_level_cron_expression2.flow"));
        assertThatThrownBy(() -> loader.toFlowTrigger(nodeBean2.getTrigger())).isInstanceOf(IllegalArgumentException.class);
        final NodeBean nodeBean3 = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, "flow_trigger_no_schedule.flow"));
        assertThatThrownBy(() -> loader.toFlowTrigger(nodeBean3.getTrigger())).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testToAzkabanFlowWithFlowTrigger() throws Exception {
        final NodeBeanLoader loader = new NodeBeanLoader();
        final NodeBean nodeBean = loader.load(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.TRIGGER_FLOW_YML_FILE));
        final AzkabanFlow flow = ((AzkabanFlow) (loader.toAzkabanNode(nodeBean)));
        validateFlowTrigger(flow.getFlowTrigger(), NodeBeanLoaderTest.MAX_WAIT_MINS, NodeBeanLoaderTest.CRON_EXPRESSION, 2);
        validateTriggerDependency(flow.getFlowTrigger(), NodeBeanLoaderTest.TRIGGER_NAME_1, NodeBeanLoaderTest.TRIGGER_TYPE, NodeBeanLoaderTest.PARAMS_1);
        validateTriggerDependency(flow.getFlowTrigger(), NodeBeanLoaderTest.TRIGGER_NAME_2, NodeBeanLoaderTest.TRIGGER_TYPE, NodeBeanLoaderTest.PARAMS_2);
    }

    @Test
    public void testGetFlowName() {
        assertThat(new NodeBeanLoader().getFlowName(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.BASIC_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.BASIC_FLOW_YML_FILE))).isEqualTo(NodeBeanLoaderTest.BASIC_FLOW_NAME);
    }

    @Test
    public void testGetFlowProps() {
        final Props flowProps = FlowLoaderUtils.getPropsFromYamlFile(NodeBeanLoaderTest.BASIC_FLOW_NAME, ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.BASIC_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.BASIC_FLOW_YML_FILE));
        assertThat(flowProps.size()).isEqualTo(2);
        assertThat(flowProps.get(NODE_TYPE)).isEqualTo(FLOW_NODE_TYPE);
        assertThat(flowProps.get(NodeBeanLoaderTest.FLOW_CONFIG_KEY)).isEqualTo(NodeBeanLoaderTest.FLOW_CONFIG_VALUE);
    }

    @Test
    public void testGetJobPropsFromBasicFlow() {
        final Props jobProps = FlowLoaderUtils.getPropsFromYamlFile((((NodeBeanLoaderTest.BASIC_FLOW_NAME) + (Constants.PATH_DELIMITER)) + (NodeBeanLoaderTest.SHELL_ECHO)), ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.BASIC_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.BASIC_FLOW_YML_FILE));
        assertThat(jobProps.size()).isEqualTo(2);
        assertThat(jobProps.get(NODE_TYPE)).isEqualTo(NodeBeanLoaderTest.TYPE_COMMAND);
        assertThat(jobProps.get(NodeBeanLoaderTest.TYPE_COMMAND)).isEqualTo(NodeBeanLoaderTest.ECHO_COMMAND);
    }

    @Test
    public void testGetJobPropsWithInvalidPath() {
        final Props jobProps = FlowLoaderUtils.getPropsFromYamlFile((((NodeBeanLoaderTest.BASIC_FLOW_NAME) + (Constants.PATH_DELIMITER)) + (NodeBeanLoaderTest.EMBEDDED_FLOW_NAME)), ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.BASIC_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.BASIC_FLOW_YML_FILE));
        assertThat(jobProps).isNull();
    }

    @Test
    public void testGetJobPropsFromEmbeddedFlow() {
        // Get job props from parent flow
        String jobPrefix = (NodeBeanLoaderTest.EMBEDDED_FLOW_NAME) + (Constants.PATH_DELIMITER);
        Props jobProps = FlowLoaderUtils.getPropsFromYamlFile((jobPrefix + (NodeBeanLoaderTest.SHELL_ECHO)), ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.EMBEDDED_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.EMBEDDED_FLOW_YML_FILE));
        assertThat(jobProps.size()).isEqualTo(2);
        assertThat(jobProps.get(NODE_TYPE)).isEqualTo(NodeBeanLoaderTest.TYPE_COMMAND);
        assertThat(jobProps.get(NodeBeanLoaderTest.TYPE_COMMAND)).isEqualTo(NodeBeanLoaderTest.ECHO_COMMAND);
        // Get job props from first level embedded flow
        jobPrefix = (jobPrefix + (NodeBeanLoaderTest.EMBEDDED_FLOW1)) + (Constants.PATH_DELIMITER);
        jobProps = FlowLoaderUtils.getPropsFromYamlFile((jobPrefix + (NodeBeanLoaderTest.SHELL_ECHO)), ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.EMBEDDED_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.EMBEDDED_FLOW_YML_FILE));
        assertThat(jobProps.size()).isEqualTo(2);
        assertThat(jobProps.get(NODE_TYPE)).isEqualTo(NodeBeanLoaderTest.TYPE_COMMAND);
        assertThat(jobProps.get(NodeBeanLoaderTest.TYPE_COMMAND)).isEqualTo(NodeBeanLoaderTest.ECHO_COMMAND_1);
        // Get job props from second level embedded flow
        jobPrefix = (jobPrefix + (NodeBeanLoaderTest.EMBEDDED_FLOW2)) + (Constants.PATH_DELIMITER);
        jobProps = FlowLoaderUtils.getPropsFromYamlFile((jobPrefix + (NodeBeanLoaderTest.SHELL_PWD)), ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.EMBEDDED_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.EMBEDDED_FLOW_YML_FILE));
        assertThat(jobProps.size()).isEqualTo(2);
        assertThat(jobProps.get(NODE_TYPE)).isEqualTo(NodeBeanLoaderTest.TYPE_COMMAND);
        assertThat(jobProps.get(NodeBeanLoaderTest.TYPE_COMMAND)).isEqualTo(NodeBeanLoaderTest.PWD_COMMAND);
    }

    @Test
    public void testSetJobPropsInBasicFlow() throws Exception {
        final String path = ((NodeBeanLoaderTest.BASIC_FLOW_NAME) + (Constants.PATH_DELIMITER)) + (NodeBeanLoaderTest.SHELL_ECHO);
        final Props overrideProps = new Props();
        overrideProps.put(NODE_TYPE, NodeBeanLoaderTest.TYPE_COMMAND);
        overrideProps.put(NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.ECHO_OVERRIDE);
        final File newFile = new File(ExecutionsTestUtil.getDataRootDir(), NodeBeanLoaderTest.BASIC_FLOW_YML_FILE);
        newFile.deleteOnExit();
        FileUtils.copyFile(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.BASIC_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.BASIC_FLOW_YML_FILE), newFile);
        FlowLoaderUtils.setPropsInYamlFile(path, newFile, overrideProps);
        assertThat(FlowLoaderUtils.getPropsFromYamlFile(path, newFile)).isEqualTo(overrideProps);
    }

    @Test
    public void testSetJobPropsInEmbeddedFlow() throws Exception {
        final String path = ((((((NodeBeanLoaderTest.EMBEDDED_FLOW_NAME) + (Constants.PATH_DELIMITER)) + (NodeBeanLoaderTest.EMBEDDED_FLOW1)) + (Constants.PATH_DELIMITER)) + (NodeBeanLoaderTest.EMBEDDED_FLOW2)) + (Constants.PATH_DELIMITER)) + (NodeBeanLoaderTest.SHELL_END);
        final Props overrideProps = new Props();
        overrideProps.put(NODE_TYPE, NodeBeanLoaderTest.TYPE_COMMAND);
        overrideProps.put(NodeBeanLoaderTest.TYPE_COMMAND, NodeBeanLoaderTest.ECHO_OVERRIDE);
        final File newFile = new File(ExecutionsTestUtil.getDataRootDir(), NodeBeanLoaderTest.EMBEDDED_FLOW_YML_FILE);
        newFile.deleteOnExit();
        FileUtils.copyFile(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.EMBEDDED_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.EMBEDDED_FLOW_YML_FILE), newFile);
        FlowLoaderUtils.setPropsInYamlFile(path, newFile, overrideProps);
        assertThat(FlowLoaderUtils.getPropsFromYamlFile(path, newFile)).isEqualTo(overrideProps);
    }

    @Test
    public void testGetFlowTrigger() {
        final FlowTrigger flowTrigger = FlowLoaderUtils.getFlowTriggerFromYamlFile(ExecutionsTestUtil.getFlowFile(NodeBeanLoaderTest.TRIGGER_FLOW_YML_TEST_DIR, NodeBeanLoaderTest.TRIGGER_FLOW_YML_FILE));
        validateFlowTrigger(flowTrigger, NodeBeanLoaderTest.MAX_WAIT_MINS, NodeBeanLoaderTest.CRON_EXPRESSION, 2);
        validateTriggerDependency(flowTrigger, NodeBeanLoaderTest.TRIGGER_NAME_1, NodeBeanLoaderTest.TRIGGER_TYPE, NodeBeanLoaderTest.PARAMS_1);
        validateTriggerDependency(flowTrigger, NodeBeanLoaderTest.TRIGGER_NAME_2, NodeBeanLoaderTest.TRIGGER_TYPE, NodeBeanLoaderTest.PARAMS_2);
    }
}

