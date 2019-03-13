/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.expression.reference.sys.check.node;


import GatewayService.EXPECTED_NODES_SETTING;
import GatewayService.RECOVER_AFTER_NODES_SETTING;
import GatewayService.RECOVER_AFTER_TIME_SETTING;
import Settings.EMPTY;
import SysCheck.Severity.HIGH;
import SysCheck.Severity.MEDIUM;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.NodeService;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;


public class SysNodeChecksTest extends CrateDummyClusterServiceUnitTest {
    @Test
    public void testRecoveryExpectedNodesCheckWithDefaultSetting() {
        RecoveryExpectedNodesSysCheck recoveryExpectedNodesCheck = new RecoveryExpectedNodesSysCheck(clusterService, Settings.EMPTY);
        assertThat(recoveryExpectedNodesCheck.id(), Is.is(1));
        assertThat(recoveryExpectedNodesCheck.severity(), Is.is(HIGH));
        assertThat(recoveryExpectedNodesCheck.validate(1, EXPECTED_NODES_SETTING.getDefault(EMPTY)), Is.is(true));
    }

    @Test
    public void testRecoveryExpectedNodesCheckWithLessThanQuorum() {
        RecoveryExpectedNodesSysCheck recoveryExpectedNodesCheck = new RecoveryExpectedNodesSysCheck(clusterService, Settings.EMPTY);
        assertThat(recoveryExpectedNodesCheck.id(), Is.is(1));
        assertThat(recoveryExpectedNodesCheck.severity(), Is.is(HIGH));
        assertThat(recoveryExpectedNodesCheck.validate(2, 1), Is.is(false));
    }

    @Test
    public void testRecoveryExpectedNodesCheckWithCorrectSetting() {
        RecoveryExpectedNodesSysCheck recoveryExpectedNodesCheck = new RecoveryExpectedNodesSysCheck(clusterService, Settings.EMPTY);
        assertThat(recoveryExpectedNodesCheck.id(), Is.is(1));
        assertThat(recoveryExpectedNodesCheck.severity(), Is.is(HIGH));
        assertThat(recoveryExpectedNodesCheck.validate(3, 3), Is.is(true));
    }

    @Test
    public void testRecoveryExpectedNodesCheckWithBiggerThanNumberOfNodes() {
        RecoveryExpectedNodesSysCheck recoveryExpectedNodesCheck = new RecoveryExpectedNodesSysCheck(clusterService, Settings.EMPTY);
        assertThat(recoveryExpectedNodesCheck.id(), Is.is(1));
        assertThat(recoveryExpectedNodesCheck.severity(), Is.is(HIGH));
        assertThat(recoveryExpectedNodesCheck.validate(3, 4), Is.is(false));
    }

    @Test
    public void testRecoveryAfterNodesCheckWithDefaultSetting() {
        RecoveryAfterNodesSysCheck recoveryAfterNodesCheck = new RecoveryAfterNodesSysCheck(clusterService, Settings.EMPTY);
        assertThat(recoveryAfterNodesCheck.id(), Is.is(2));
        assertThat(recoveryAfterNodesCheck.severity(), Is.is(HIGH));
        assertThat(recoveryAfterNodesCheck.validate(RECOVER_AFTER_NODES_SETTING.getDefault(EMPTY), EXPECTED_NODES_SETTING.getDefault(EMPTY)), Is.is(true));
    }

    @Test
    public void testRecoveryAfterNodesCheckWithLessThanQuorum() {
        ClusterService clusterService = Mockito.mock(ClusterService.class, Answers.RETURNS_DEEP_STUBS.get());
        RecoveryAfterNodesSysCheck recoveryAfterNodesCheck = new RecoveryAfterNodesSysCheck(clusterService, Settings.EMPTY);
        Mockito.when(clusterService.localNode().getId()).thenReturn("node");
        Mockito.when(clusterService.state().nodes().getSize()).thenReturn(8);
        assertThat(recoveryAfterNodesCheck.id(), Is.is(2));
        assertThat(recoveryAfterNodesCheck.severity(), Is.is(HIGH));
        assertThat(recoveryAfterNodesCheck.validate(4, 8), Is.is(false));
    }

    @Test
    public void testRecoveryAfterNodesCheckWithCorrectSetting() {
        ClusterService clusterService = Mockito.mock(ClusterService.class, Answers.RETURNS_DEEP_STUBS.get());
        RecoveryAfterNodesSysCheck recoveryAfterNodesCheck = new RecoveryAfterNodesSysCheck(clusterService, Settings.EMPTY);
        Mockito.when(clusterService.localNode().getId()).thenReturn("node");
        Mockito.when(clusterService.state().nodes().getSize()).thenReturn(8);
        assertThat(recoveryAfterNodesCheck.id(), Is.is(2));
        assertThat(recoveryAfterNodesCheck.severity(), Is.is(HIGH));
        assertThat(recoveryAfterNodesCheck.validate(8, 8), Is.is(true));
    }

    @Test
    public void testRecoveryAfterTimeCheckWithCorrectSetting() {
        Settings settings = Settings.builder().put(RECOVER_AFTER_TIME_SETTING.getKey(), TimeValue.timeValueMillis(4).toString()).put(RECOVER_AFTER_NODES_SETTING.getKey(), 3).put(EXPECTED_NODES_SETTING.getKey(), 3).build();
        RecoveryAfterTimeSysCheck recoveryAfterNodesCheck = new RecoveryAfterTimeSysCheck(settings);
        assertThat(recoveryAfterNodesCheck.validate(), Is.is(true));
    }

    @Test
    public void testRecoveryAfterTimeCheckWithDefaultSetting() {
        RecoveryAfterTimeSysCheck recoveryAfterNodesCheck = new RecoveryAfterTimeSysCheck(Settings.EMPTY);
        assertThat(recoveryAfterNodesCheck.id(), Is.is(3));
        assertThat(recoveryAfterNodesCheck.severity(), Is.is(MEDIUM));
        assertThat(recoveryAfterNodesCheck.validate(RECOVER_AFTER_TIME_SETTING.getDefault(EMPTY), RECOVER_AFTER_NODES_SETTING.getDefault(EMPTY), EXPECTED_NODES_SETTING.getDefault(EMPTY)), Is.is(true));
    }

    @Test
    public void testRecoveryAfterTimeCheckWithWrongSetting() {
        Settings settings = Settings.builder().put(RECOVER_AFTER_TIME_SETTING.getKey(), TimeValue.timeValueMillis(0).toString()).put(RECOVER_AFTER_NODES_SETTING.getKey(), 3).put(EXPECTED_NODES_SETTING.getKey(), 3).build();
        RecoveryAfterTimeSysCheck recoveryAfterNodesCheck = new RecoveryAfterTimeSysCheck(settings);
        assertThat(recoveryAfterNodesCheck.validate(), Is.is(false));
    }

    @Test
    public void testValidationLowDiskWatermarkCheck() {
        DiskWatermarkNodesSysCheck low = new LowDiskWatermarkNodesSysCheck(clusterService, Settings.EMPTY, Mockito.mock(NodeService.class, Answers.RETURNS_MOCKS.get()));
        assertThat(low.id(), Is.is(6));
        assertThat(low.severity(), Is.is(HIGH));
        // default threshold is: 85% used
        assertThat(low.validate(15, 100), Is.is(true));
        assertThat(low.validate(14, 100), Is.is(false));
    }

    @Test
    public void testValidationHighDiskWatermarkCheck() {
        DiskWatermarkNodesSysCheck high = new HighDiskWatermarkNodesSysCheck(clusterService, Settings.EMPTY, Mockito.mock(NodeService.class, Answers.RETURNS_MOCKS.get()));
        assertThat(high.id(), Is.is(5));
        assertThat(high.severity(), Is.is(HIGH));
        // default threshold is: 90% used
        assertThat(high.validate(10, 100), Is.is(true));
        assertThat(high.validate(9, 100), Is.is(false));
    }

    @Test
    public void testValidationFloodStageDiskWatermarkCheck() {
        DiskWatermarkNodesSysCheck floodStage = new FloodStageDiskWatermarkNodesSysCheck(clusterService, Settings.EMPTY, Mockito.mock(NodeService.class, Answers.RETURNS_MOCKS.get()));
        assertThat(floodStage.id(), Is.is(7));
        assertThat(floodStage.severity(), Is.is(HIGH));
        // default threshold is: 95% used
        assertThat(floodStage.validate(5, 100), Is.is(true));
        assertThat(floodStage.validate(4, 100), Is.is(false));
    }
}

