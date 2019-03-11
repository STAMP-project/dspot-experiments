/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.server.rest;


import ExecConstants.HTTP_ENABLE;
import ExecConstants.HTTP_PORT_HUNT;
import ExecConstants.SLICE_TARGET;
import ExecConstants.SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE;
import StatusResources.OptionWrapper;
import org.apache.drill.exec.ExecConstants;
import org.apache.drill.exec.server.options.OptionDefinition;
import org.apache.drill.exec.server.options.TestConfigLinkage;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.ClientFixture;
import org.apache.drill.test.ClusterFixture;
import org.apache.drill.test.ClusterFixtureBuilder;
import org.apache.drill.test.RestClientFixture;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class StatusResourcesTest {
    @Rule
    public final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    @Test
    public void testRetrieveInternalOption() throws Exception {
        OptionDefinition optionDefinition = TestConfigLinkage.createMockPropOptionDefinition();
        ClusterFixtureBuilder builder = ClusterFixture.builder(dirTestWatcher).configProperty(HTTP_ENABLE, true).configProperty(ExecConstants.bootDefaultFor(TestConfigLinkage.MOCK_PROPERTY), "a").configProperty(HTTP_PORT_HUNT, true).configProperty(SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE, false).putDefinition(optionDefinition);
        try (ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture();RestClientFixture restClientFixture = cluster.restClientFixture()) {
            Assert.assertNull(restClientFixture.getStatusOption(TestConfigLinkage.MOCK_PROPERTY));
            StatusResources.OptionWrapper option = restClientFixture.getStatusInternalOption(TestConfigLinkage.MOCK_PROPERTY);
            Assert.assertEquals("a", option.getValueAsString());
            client.alterSystem(TestConfigLinkage.MOCK_PROPERTY, "c");
            Assert.assertNull(restClientFixture.getStatusOption(TestConfigLinkage.MOCK_PROPERTY));
            option = restClientFixture.getStatusInternalOption(TestConfigLinkage.MOCK_PROPERTY);
            Assert.assertEquals("c", option.getValueAsString());
        }
    }

    @Test
    public void testRetrievePublicOption() throws Exception {
        ClusterFixtureBuilder builder = ClusterFixture.builder(dirTestWatcher).configProperty(HTTP_ENABLE, true).configProperty(HTTP_PORT_HUNT, true).configProperty(SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE, false).systemOption(SLICE_TARGET, 20);
        try (ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture();RestClientFixture restClientFixture = cluster.restClientFixture()) {
            Assert.assertNull(restClientFixture.getStatusInternalOption(SLICE_TARGET));
            StatusResources.OptionWrapper option = restClientFixture.getStatusOption(SLICE_TARGET);
            Assert.assertEquals(20, option.getValue());
            client.alterSystem(SLICE_TARGET, 30);
            Assert.assertNull(restClientFixture.getStatusInternalOption(SLICE_TARGET));
            option = restClientFixture.getStatusOption(SLICE_TARGET);
            Assert.assertEquals(30, option.getValue());
        }
    }
}

