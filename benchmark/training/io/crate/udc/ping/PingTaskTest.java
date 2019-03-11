/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
package io.crate.udc.ping;


import Settings.EMPTY;
import SharedSettings.ENTERPRISE_LICENSE_SETTING;
import io.crate.http.HttpTestServer;
import io.crate.license.DecryptedLicenseData;
import io.crate.license.LicenseService;
import io.crate.monitor.ExtendedNodeInfo;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;


public class PingTaskTest extends CrateDummyClusterServiceUnitTest {
    private static DecryptedLicenseData LICENSE = new DecryptedLicenseData(((System.currentTimeMillis()) + (TimeUnit.DAYS.toMillis(30))), "crate");

    private ExtendedNodeInfo extendedNodeInfo;

    private LicenseService licenseService;

    private HttpTestServer testServer;

    @Test
    public void testGetHardwareAddressMacAddrNull() throws Exception {
        PingTask pingTask = createPingTask();
        assertThat(pingTask.getHardwareAddress(), Matchers.nullValue());
    }

    @Test
    public void testIsEnterpriseField() throws Exception {
        PingTask pingTask = createPingTask();
        assertThat(pingTask.isEnterprise(), Is.is(ENTERPRISE_LICENSE_SETTING.getDefault().toString()));
        pingTask = createPingTask("http://dummy", Settings.builder().put(ENTERPRISE_LICENSE_SETTING.getKey(), false).build());
        assertThat(pingTask.isEnterprise(), Is.is("false"));
    }

    @Test
    public void testSuccessfulPingTaskRunWhenLicenseIsNotNull() throws Exception {
        Mockito.when(licenseService.currentLicense()).thenReturn(PingTaskTest.LICENSE);
        testServer = new HttpTestServer(18080, false);
        testServer.run();
        PingTask task = createPingTask("http://localhost:18080/", EMPTY);
        task.run();
        assertThat(testServer.responses.size(), Is.is(1));
        task.run();
        assertThat(testServer.responses.size(), Is.is(2));
        for (long i = 0; i < (testServer.responses.size()); i++) {
            String json = testServer.responses.get(((int) (i)));
            Map<String, String> map = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            try {
                // convert JSON string to Map
                map = mapper.readValue(json, new org.codehaus.jackson.type.TypeReference<HashMap<String, String>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
            assertThat(map, Matchers.hasKey("kernel"));
            assertThat(map.get("kernel"), Is.is(Matchers.notNullValue()));
            assertThat(map, Matchers.hasKey("cluster_id"));
            assertThat(map.get("cluster_id"), Is.is(Matchers.notNullValue()));
            assertThat(map, Matchers.hasKey("master"));
            assertThat(map.get("master"), Is.is(Matchers.notNullValue()));
            assertThat(map, Matchers.hasKey("ping_count"));
            assertThat(map.get("ping_count"), Is.is(Matchers.notNullValue()));
            Map<String, Long> pingCountMap;
            pingCountMap = mapper.readValue(map.get("ping_count"), new org.codehaus.jackson.type.TypeReference<Map<String, Long>>() {});
            assertThat(pingCountMap.get("success"), Is.is(i));
            assertThat(pingCountMap.get("failure"), Is.is(0L));
            if ((task.getHardwareAddress()) != null) {
                assertThat(map, Matchers.hasKey("hardware_address"));
                assertThat(map.get("hardware_address"), Is.is(Matchers.notNullValue()));
            }
            assertThat(map, Matchers.hasKey("crate_version"));
            assertThat(map.get("crate_version"), Is.is(Matchers.notNullValue()));
            assertThat(map, Matchers.hasKey("java_version"));
            assertThat(map.get("java_version"), Is.is(Matchers.notNullValue()));
            assertThat(map, Matchers.hasKey("license_expiry_date"));
            assertThat(map.get("license_expiry_date"), Is.is(String.valueOf(PingTaskTest.LICENSE.expiryDateInMs())));
            assertThat(map, Matchers.hasKey("license_issued_to"));
            assertThat(map.get("license_issued_to"), Is.is(PingTaskTest.LICENSE.issuedTo()));
        }
    }

    @Test
    public void testSuccessfulPingTaskRunWhenLicenseIsNull() throws Exception {
        Mockito.when(licenseService.currentLicense()).thenReturn(null);
        testServer = new HttpTestServer(18080, false);
        testServer.run();
        PingTask task = createPingTask("http://localhost:18080/", EMPTY);
        task.run();
        assertThat(testServer.responses.size(), Is.is(1));
        String json = testServer.responses.get(0);
        Map<String, String> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            // convert JSON string to Map
            map = mapper.readValue(json, new org.codehaus.jackson.type.TypeReference<HashMap<String, String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertThat(map, Matchers.not(Matchers.hasKey("license_expiry_date")));
        assertThat(map, Matchers.not(Matchers.hasKey("license_issued_to")));
    }

    @Test
    public void testUnsuccessfulPingTaskRun() throws Exception {
        Mockito.when(licenseService.currentLicense()).thenReturn(PingTaskTest.LICENSE);
        testServer = new HttpTestServer(18081, true);
        testServer.run();
        PingTask task = createPingTask("http://localhost:18081/", EMPTY);
        task.run();
        assertThat(testServer.responses.size(), Is.is(1));
        task.run();
        assertThat(testServer.responses.size(), Is.is(2));
        for (long i = 0; i < (testServer.responses.size()); i++) {
            String json = testServer.responses.get(((int) (i)));
            Map<String, String> map = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            try {
                // convert JSON string to Map
                map = mapper.readValue(json, new org.codehaus.jackson.type.TypeReference<HashMap<String, String>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
            assertThat(map, Matchers.hasKey("kernel"));
            assertThat(map.get("kernel"), Is.is(Matchers.notNullValue()));
            assertThat(map, Matchers.hasKey("cluster_id"));
            assertThat(map.get("cluster_id"), Is.is(Matchers.notNullValue()));
            assertThat(map, Matchers.hasKey("master"));
            assertThat(map.get("master"), Is.is(Matchers.notNullValue()));
            assertThat(map, Matchers.hasKey("ping_count"));
            assertThat(map.get("ping_count"), Is.is(Matchers.notNullValue()));
            Map<String, Long> pingCountMap;
            pingCountMap = mapper.readValue(map.get("ping_count"), new org.codehaus.jackson.type.TypeReference<Map<String, Long>>() {});
            assertThat(pingCountMap.get("success"), Is.is(0L));
            assertThat(pingCountMap.get("failure"), Is.is(i));
            if ((task.getHardwareAddress()) != null) {
                assertThat(map, Matchers.hasKey("hardware_address"));
                assertThat(map.get("hardware_address"), Is.is(Matchers.notNullValue()));
            }
            assertThat(map, Matchers.hasKey("crate_version"));
            assertThat(map.get("crate_version"), Is.is(Matchers.notNullValue()));
            assertThat(map, Matchers.hasKey("java_version"));
            assertThat(map.get("java_version"), Is.is(Matchers.notNullValue()));
            assertThat(map, Matchers.hasKey("license_expiry_date"));
            assertThat(map.get("license_expiry_date"), Is.is(Matchers.notNullValue()));
            assertThat(map, Matchers.hasKey("license_issued_to"));
            assertThat(map.get("license_issued_to"), Is.is(Matchers.notNullValue()));
        }
    }
}

