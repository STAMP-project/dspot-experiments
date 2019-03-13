/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.integrationtests;


import CrateCircuitBreakerService.JOBS_LOG_CIRCUIT_BREAKER_LIMIT_SETTING;
import CrateCircuitBreakerService.QUERY_CIRCUIT_BREAKER_LIMIT_SETTING;
import ESIntegTestCase.ClusterScope;
import GatewayService.EXPECTED_NODES_SETTING;
import GatewayService.RECOVER_AFTER_NODES_SETTING;
import GatewayService.RECOVER_AFTER_TIME_SETTING;
import JobsLogService.STATS_ENABLED_SETTING;
import JobsLogService.STATS_JOBS_LOG_SIZE_SETTING;
import JobsLogService.STATS_OPERATIONS_LOG_SIZE_SETTING;
import Settings.Builder;
import Settings.EMPTY;
import SharedSettings.ENTERPRISE_LICENSE_SETTING;
import UDCService.UDC_ENABLED_SETTING;
import UDCService.UDC_INITIAL_DELAY_SETTING;
import UDCService.UDC_INTERVAL_SETTING;
import UDCService.UDC_URL_SETTING;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.MemorySizeValue;
import org.elasticsearch.test.ESIntegTestCase;
import org.hamcrest.Matchers;
import org.junit.Test;


@ESIntegTestCase.ClusterScope
public class SysClusterSettingsTest extends SQLTransportIntegrationTest {
    @Test
    public void testSetResetGlobalSetting() throws Exception {
        execute("set global persistent stats.enabled = false");
        execute("select settings['stats']['enabled'] from sys.cluster");
        assertThat(response.rows()[0][0], Matchers.is(false));
        execute("reset global stats.enabled");
        execute("select settings['stats']['enabled'] from sys.cluster");
        assertThat(response.rows()[0][0], Matchers.is(STATS_ENABLED_SETTING.getDefault()));
        execute("set global transient stats = { enabled = true, jobs_log_size = 3, operations_log_size = 4 }");
        execute(("select settings['stats']['enabled'], settings['stats']['jobs_log_size']," + "settings['stats']['operations_log_size'] from sys.cluster"));
        assertThat(response.rows()[0][0], Matchers.is(true));
        assertThat(response.rows()[0][1], Matchers.is(3));
        assertThat(response.rows()[0][2], Matchers.is(4));
        execute("reset global stats");
        execute(("select settings['stats']['enabled'], settings['stats']['jobs_log_size']," + "settings['stats']['operations_log_size'] from sys.cluster"));
        assertThat(response.rowCount(), Matchers.is(1L));
        assertThat(response.rows()[0][0], Matchers.is(STATS_ENABLED_SETTING.getDefault()));
        assertThat(response.rows()[0][1], Matchers.is(STATS_JOBS_LOG_SIZE_SETTING.getDefault()));
        assertThat(response.rows()[0][2], Matchers.is(STATS_OPERATIONS_LOG_SIZE_SETTING.getDefault()));
        execute("set global transient \"indices.breaker.query.limit\" = \'2mb\'");
        execute("select settings from sys.cluster");
        assertSettingsValue(QUERY_CIRCUIT_BREAKER_LIMIT_SETTING.getKey(), "2mb");
    }

    @Test
    public void testResetPersistent() throws Exception {
        execute("select settings['bulk']['request_timeout'] from sys.cluster");
        assertThat(response.rows()[0][0], Matchers.is("42s"));// configured via nodeSettings

        execute("set global persistent bulk.request_timeout = '59s'");
        execute("select settings['bulk']['request_timeout'] from sys.cluster");
        assertThat(response.rows()[0][0], Matchers.is("59s"));
        execute("reset global bulk.request_timeout");
        execute("select settings['bulk']['request_timeout'] from sys.cluster");
        assertThat(response.rows()[0][0], Matchers.is("42s"));
    }

    @Test
    public void testDynamicTransientSettings() throws Exception {
        Settings.Builder builder = Settings.builder().put(STATS_JOBS_LOG_SIZE_SETTING.getKey(), 1).put(STATS_OPERATIONS_LOG_SIZE_SETTING.getKey(), 2).put(STATS_ENABLED_SETTING.getKey(), false);
        client().admin().cluster().prepareUpdateSettings().setTransientSettings(builder.build()).execute().actionGet();
        execute("select settings from sys.cluster");
        assertSettingsValue(STATS_JOBS_LOG_SIZE_SETTING.getKey(), 1);
        assertSettingsValue(STATS_OPERATIONS_LOG_SIZE_SETTING.getKey(), 2);
        assertSettingsValue(STATS_ENABLED_SETTING.getKey(), false);
        internalCluster().fullRestart();
        execute("select settings from sys.cluster");
        assertSettingsDefault(STATS_JOBS_LOG_SIZE_SETTING);
        assertSettingsDefault(STATS_OPERATIONS_LOG_SIZE_SETTING);
        assertSettingsDefault(STATS_ENABLED_SETTING);
    }

    @Test
    public void testDynamicPersistentSettings() throws Exception {
        Settings.Builder builder = Settings.builder().put(STATS_OPERATIONS_LOG_SIZE_SETTING.getKey(), 100);
        client().admin().cluster().prepareUpdateSettings().setPersistentSettings(builder.build()).execute().actionGet();
        execute("select settings from sys.cluster");
        assertSettingsValue(STATS_OPERATIONS_LOG_SIZE_SETTING.getKey(), 100);
        internalCluster().fullRestart();
        // the gateway recovery is async and
        // it might take a bit until it reads the persisted cluster state and updates the settings expression
        assertBusy(() -> {
            execute("select settings from sys.cluster");
            assertSettingsValue(JobsLogService.STATS_OPERATIONS_LOG_SIZE_SETTING.getKey(), 100);
        });
    }

    @Test
    public void testStaticGatewayDefaultSettings() {
        execute("select settings from sys.cluster");
        assertSettingsDefault(EXPECTED_NODES_SETTING);
        assertSettingsDefault(RECOVER_AFTER_NODES_SETTING);
        assertSettingsValue(RECOVER_AFTER_TIME_SETTING.getKey(), RECOVER_AFTER_TIME_SETTING.getDefaultRaw(EMPTY));
    }

    @Test
    public void testStaticUDCDefaultSettings() {
        execute("select settings from sys.cluster");
        assertSettingsDefault(UDC_ENABLED_SETTING);
        assertSettingsValue(UDC_INITIAL_DELAY_SETTING.getKey(), UDC_INITIAL_DELAY_SETTING.setting().getDefaultRaw(EMPTY));
        assertSettingsValue(UDC_INTERVAL_SETTING.getKey(), UDC_INTERVAL_SETTING.setting().getDefaultRaw(EMPTY));
        assertSettingsDefault(UDC_URL_SETTING);
    }

    @Test
    public void testStatsCircuitBreakerLogsDefaultSettings() {
        execute("select settings from sys.cluster");
        assertSettingsValue(JOBS_LOG_CIRCUIT_BREAKER_LIMIT_SETTING.getKey(), MemorySizeValue.parseBytesSizeValueOrHeapRatio(JOBS_LOG_CIRCUIT_BREAKER_LIMIT_SETTING.setting().getDefaultRaw(EMPTY), JOBS_LOG_CIRCUIT_BREAKER_LIMIT_SETTING.getKey()).toString());
    }

    @Test
    public void testDefaultEnterpriseSettings() {
        execute("select settings from sys.cluster");
        assertSettingsDefault(ENTERPRISE_LICENSE_SETTING);
    }

    @Test
    public void testReadChangedElasticsearchSetting() {
        execute("set global transient indices.recovery.max_bytes_per_sec = ?", new Object[]{ "100kb" });
        execute("select settings from sys.cluster");
        assertSettingsValue(INDICES_RECOVERY_MAX_BYTES_PER_SEC_SETTING.getKey(), "100kb");
    }
}

