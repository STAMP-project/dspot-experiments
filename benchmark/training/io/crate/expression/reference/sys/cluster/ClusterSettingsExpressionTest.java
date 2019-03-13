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
package io.crate.expression.reference.sys.cluster;


import DecommissioningService.GRACEFUL_STOP_MIN_AVAILABILITY_SETTING;
import JobsLogService.STATS_ENABLED_SETTING;
import JobsLogService.STATS_JOBS_LOG_SIZE_SETTING;
import SharedSettings.ENTERPRISE_LICENSE_SETTING;
import SharedSettings.LICENSE_IDENT_SETTING;
import io.crate.common.collections.Maps;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.ClusterStateUpdateTask;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;


public class ClusterSettingsExpressionTest extends CrateDummyClusterServiceUnitTest {
    @Test
    public void testSettingsAreAppliedImmediately() throws Exception {
        Settings settings = Settings.builder().put("bulk.request_timeout", "20s").build();
        // build cluster service mock to pass initial settings
        ClusterService clusterService = Mockito.mock(ClusterService.class);
        Mockito.when(clusterService.getSettings()).thenReturn(settings);
        ClusterSettingsExpression clusterSettingsExpression = new ClusterSettingsExpression(clusterService, new io.crate.metadata.settings.CrateSettings(clusterService, clusterService.getSettings()));
        assertThat(clusterSettingsExpression.getChild("bulk").getChild("request_timeout").value(), Is.is("20s"));
    }

    @Test
    public void testSettingsAreUpdated() throws Exception {
        ClusterSettingsExpression expression = new ClusterSettingsExpression(clusterService, new io.crate.metadata.settings.CrateSettings(clusterService, clusterService.getSettings()));
        Settings settings = Settings.builder().put(STATS_JOBS_LOG_SIZE_SETTING.getKey(), 1).put(STATS_ENABLED_SETTING.getKey(), false).put(GRACEFUL_STOP_MIN_AVAILABILITY_SETTING.getKey(), "full").build();
        CountDownLatch latch = new CountDownLatch(1);
        clusterService.addListener(( event) -> latch.countDown());
        clusterService.submitStateUpdateTask("update settings", new ClusterStateUpdateTask() {
            @Override
            public ClusterState execute(ClusterState currentState) throws Exception {
                return ClusterState.builder(currentState).metaData(MetaData.builder().transientSettings(settings)).build();
            }

            @Override
            public void onFailure(String source, Exception e) {
                fail(e.getMessage());
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        Map<String, Object> values = expression.value();
        assertThat(Maps.getByPath(values, STATS_JOBS_LOG_SIZE_SETTING.getKey()), Is.is(1));
        assertThat(Maps.getByPath(values, STATS_ENABLED_SETTING.getKey()), Is.is(false));
        assertThat(Maps.getByPath(values, GRACEFUL_STOP_MIN_AVAILABILITY_SETTING.getKey()), Is.is("FULL"));
        assertSettingDeprecationsAndWarnings(new Setting<?>[]{ LICENSE_IDENT_SETTING.setting(), ENTERPRISE_LICENSE_SETTING.setting() });
    }
}

