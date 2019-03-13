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
package io.crate.metadata.settings;


import CrateSettings.NestedSettingExpression;
import CrateSettings.SettingExpression;
import GatewayService.EXPECTED_NODES_SETTING;
import JobsLogService.STATS_ENABLED_SETTING;
import JobsLogService.STATS_JOBS_LOG_EXPIRATION_SETTING;
import JobsLogService.STATS_JOBS_LOG_FILTER;
import JobsLogService.STATS_JOBS_LOG_PERSIST_FILTER;
import JobsLogService.STATS_JOBS_LOG_SIZE_SETTING;
import Setting.Property.Dynamic;
import Setting.Property.NodeScope;
import Settings.Builder;
import Settings.EMPTY;
import io.crate.expression.NestableInput;
import io.crate.expression.reference.NestedObjectExpression;
import io.crate.settings.CrateSetting;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.types.ObjectType;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.Matchers;
import org.junit.Test;


public class CrateSettingsTest extends CrateDummyClusterServiceUnitTest {
    @Test
    public void testValidSetting() {
        assertThat(CrateSettings.isValidSetting(STATS_ENABLED_SETTING.getKey()), Matchers.is(true));
    }

    @Test
    public void testValidLoggingSetting() {
        assertThat(CrateSettings.isValidSetting("logger.info"), Matchers.is(true));
    }

    @Test
    public void testValidPrefixSetting() {
        assertThat(CrateSettings.isValidSetting("stats"), Matchers.is(true));
    }

    @Test
    public void testSettingsByNamePrefix() {
        assertThat(CrateSettings.settingNamesByPrefix("stats.jobs_log"), Matchers.containsInAnyOrder(STATS_JOBS_LOG_SIZE_SETTING.getKey(), STATS_JOBS_LOG_FILTER.getKey(), STATS_JOBS_LOG_PERSIST_FILTER.getKey(), STATS_JOBS_LOG_EXPIRATION_SETTING.getKey()));
    }

    @Test
    public void testLoggingSettingsByNamePrefix() throws Exception {
        assertThat(CrateSettings.settingNamesByPrefix("logger."), Matchers.contains("logger."));
    }

    @Test
    public void testIsRuntimeSetting() {
        // valid, no exception thrown here
        CrateSettings.checkIfRuntimeSetting(STATS_ENABLED_SETTING.getKey());
    }

    @Test
    public void testIsNotRuntimeSetting() {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Setting 'gateway.expected_nodes' cannot be set/reset at runtime");
        CrateSettings.checkIfRuntimeSetting(EXPECTED_NODES_SETTING.getKey());
    }

    @Test
    public void testFlattenObjectSettings() {
        Map<String, Object> value = MapBuilder.<String, Object>newMapBuilder().put("enabled", true).put("breaker", MapBuilder.newMapBuilder().put("log", MapBuilder.newMapBuilder().put("jobs", MapBuilder.newMapBuilder().put("overhead", 1.05).map()).map()).map()).map();
        Settings.Builder builder = Settings.builder();
        Settings expected = Settings.builder().put("stats.enabled", true).put("stats.breaker.log.jobs.overhead", 1.05).build();
        CrateSettings.flattenSettings(builder, "stats", value);
        assertThat(builder.build(), Matchers.is(expected));
    }

    @Test
    public void testDefaultValuesAreSet() {
        CrateSettings crateSettings = new CrateSettings(clusterService, clusterService.getSettings());
        assertThat(crateSettings.settings().get(STATS_ENABLED_SETTING.getKey()), Matchers.is(STATS_ENABLED_SETTING.setting().getDefaultRaw(EMPTY)));
    }

    @Test
    public void testReferenceMapIsBuild() {
        CrateSettings crateSettings = new CrateSettings(clusterService, clusterService.getSettings());
        NestedObjectExpression stats = ((NestedObjectExpression) (crateSettings.referenceImplementationTree().get("stats")));
        CrateSettings.SettingExpression statsEnabled = ((CrateSettings.SettingExpression) (stats.getChild("enabled")));
        assertThat(statsEnabled.name(), Matchers.is("enabled"));
        assertThat(statsEnabled.value(), Matchers.is(true));
    }

    @Test
    public void testSettingsChanged() {
        CrateSettings crateSettings = new CrateSettings(clusterService, clusterService.getSettings());
        ClusterState newState = ClusterState.builder(clusterService.state()).metaData(MetaData.builder().transientSettings(Settings.builder().put(STATS_ENABLED_SETTING.getKey(), true).build())).build();
        crateSettings.clusterChanged(new org.elasticsearch.cluster.ClusterChangedEvent("settings updated", newState, clusterService.state()));
        assertThat(crateSettings.settings().getAsBoolean(STATS_ENABLED_SETTING.getKey(), false), Matchers.is(true));
    }

    @Test
    public void testBuildGroupSettingReferenceTree() {
        // create groupSetting to be used in tests
        CrateSetting<Settings> TEST_SETTING = CrateSetting.of(Setting.groupSetting("test.", Dynamic, NodeScope), ObjectType.untyped());
        Settings initialSettings = Settings.builder().put("test.setting.a.user", "42").put("test.setting.a.method", "something").put("test.setting.b", "value-b").put("test.setting.c", "value-c").build();
        CrateSettings crateSettings = new CrateSettings(clusterService, initialSettings);
        Map<String, Settings> settingsMap = initialSettings.getGroups(TEST_SETTING.getKey(), true);
        // build reference Map for TEST_SETTING
        Map<String, NestableInput> referenceMap = new HashMap<>(4);
        for (Map.Entry<String, Settings> entry : settingsMap.entrySet()) {
            crateSettings.buildGroupSettingReferenceTree(TEST_SETTING.getKey(), entry.getKey(), entry.getValue(), referenceMap);
        }
        NestedObjectExpression test = ((NestedObjectExpression) (referenceMap.get("test")));
        NestedObjectExpression testSetting = ((NestedObjectExpression) (test.getChild("setting")));
        assertThat(testSetting.value().containsKey("a"), Matchers.is(true));
        assertThat(testSetting.value().containsKey("b"), Matchers.is(true));
        assertThat(testSetting.value().containsKey("c"), Matchers.is(true));
        CrateSettings.NestedSettingExpression a = ((CrateSettings.NestedSettingExpression) (testSetting.getChild("a")));
        assertThat(a.value().containsKey("method"), Matchers.is(true));
        assertThat(a.value().containsKey("user"), Matchers.is(true));
    }

    @Test
    public void testRecoverAfterTimeDefaultValueWithExpectedNodesSet() throws Exception {
        Settings settings = Settings.builder().put(EXPECTED_NODES_SETTING.getKey(), 1).build();
        CrateSettings crateSettings = new CrateSettings(clusterService, settings);
        NestableInput impl = crateSettings.referenceImplementationTree().get("gateway").getChild("recover_after_time");
        assertThat(impl.value(), Matchers.is("5m"));
    }

    @Test
    public void testRecoverAfterTimeDefaultValueWithoutExpectedNodesSet() throws Exception {
        CrateSettings crateSettings = new CrateSettings(clusterService, Settings.EMPTY);
        NestableInput impl = crateSettings.referenceImplementationTree().get("gateway").getChild("recover_after_time");
        assertThat(impl.value(), Matchers.is("0ms"));
    }
}

