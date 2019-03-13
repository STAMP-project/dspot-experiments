/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config;


import Constants.ACCEPT_FOREIGN_IP;
import Constants.APPLICATION_KEY;
import Constants.DUMP_DIRECTORY;
import Constants.QOS_ENABLE;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ApplicationConfigTest {
    @Test
    public void testName() throws Exception {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("app");
        MatcherAssert.assertThat(application.getName(), Matchers.equalTo("app"));
        application = new ApplicationConfig("app2");
        MatcherAssert.assertThat(application.getName(), Matchers.equalTo("app2"));
        Map<String, String> parameters = new HashMap<String, String>();
        ApplicationConfig.appendParameters(parameters, application);
        MatcherAssert.assertThat(parameters, Matchers.hasEntry(APPLICATION_KEY, "app2"));
    }

    @Test
    public void testVersion() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setVersion("1.0.0");
        MatcherAssert.assertThat(application.getVersion(), Matchers.equalTo("1.0.0"));
        Map<String, String> parameters = new HashMap<String, String>();
        ApplicationConfig.appendParameters(parameters, application);
        MatcherAssert.assertThat(parameters, Matchers.hasEntry("application.version", "1.0.0"));
    }

    @Test
    public void testOwner() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setOwner("owner");
        MatcherAssert.assertThat(application.getOwner(), Matchers.equalTo("owner"));
    }

    @Test
    public void testOrganization() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setOrganization("org");
        MatcherAssert.assertThat(application.getOrganization(), Matchers.equalTo("org"));
    }

    @Test
    public void testArchitecture() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setArchitecture("arch");
        MatcherAssert.assertThat(application.getArchitecture(), Matchers.equalTo("arch"));
    }

    @Test
    public void testEnvironment1() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setEnvironment("develop");
        MatcherAssert.assertThat(application.getEnvironment(), Matchers.equalTo("develop"));
        application.setEnvironment("test");
        MatcherAssert.assertThat(application.getEnvironment(), Matchers.equalTo("test"));
        application.setEnvironment("product");
        MatcherAssert.assertThat(application.getEnvironment(), Matchers.equalTo("product"));
    }

    @Test
    public void testEnvironment2() throws Exception {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            ApplicationConfig application = new ApplicationConfig("app");
            application.setEnvironment("illegal-env");
        });
    }

    @Test
    public void testRegistry() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        RegistryConfig registry = new RegistryConfig();
        application.setRegistry(registry);
        MatcherAssert.assertThat(application.getRegistry(), Matchers.sameInstance(registry));
        application.setRegistries(Collections.singletonList(registry));
        MatcherAssert.assertThat(application.getRegistries(), Matchers.contains(registry));
        MatcherAssert.assertThat(application.getRegistries(), hasSize(1));
    }

    @Test
    public void testMonitor() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setMonitor(new MonitorConfig("monitor-addr"));
        MatcherAssert.assertThat(application.getMonitor().getAddress(), Matchers.equalTo("monitor-addr"));
        application.setMonitor("monitor-addr");
        MatcherAssert.assertThat(application.getMonitor().getAddress(), Matchers.equalTo("monitor-addr"));
    }

    @Test
    public void testLogger() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setLogger("log4j");
        MatcherAssert.assertThat(application.getLogger(), Matchers.equalTo("log4j"));
    }

    @Test
    public void testDefault() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setDefault(true);
        MatcherAssert.assertThat(application.isDefault(), Matchers.is(true));
    }

    @Test
    public void testDumpDirectory() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setDumpDirectory("/dump");
        MatcherAssert.assertThat(application.getDumpDirectory(), Matchers.equalTo("/dump"));
        Map<String, String> parameters = new HashMap<String, String>();
        ApplicationConfig.appendParameters(parameters, application);
        MatcherAssert.assertThat(parameters, Matchers.hasEntry(DUMP_DIRECTORY, "/dump"));
    }

    @Test
    public void testQosEnable() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setQosEnable(true);
        MatcherAssert.assertThat(application.getQosEnable(), Matchers.is(true));
        Map<String, String> parameters = new HashMap<String, String>();
        ApplicationConfig.appendParameters(parameters, application);
        MatcherAssert.assertThat(parameters, Matchers.hasEntry(QOS_ENABLE, "true"));
    }

    @Test
    public void testQosPort() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setQosPort(8080);
        MatcherAssert.assertThat(application.getQosPort(), Matchers.equalTo(8080));
    }

    @Test
    public void testQosAcceptForeignIp() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setQosAcceptForeignIp(true);
        MatcherAssert.assertThat(application.getQosAcceptForeignIp(), Matchers.is(true));
        Map<String, String> parameters = new HashMap<String, String>();
        ApplicationConfig.appendParameters(parameters, application);
        MatcherAssert.assertThat(parameters, Matchers.hasEntry(ACCEPT_FOREIGN_IP, "true"));
    }

    @Test
    public void testParameters() throws Exception {
        ApplicationConfig application = new ApplicationConfig("app");
        application.setQosAcceptForeignIp(true);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("k1", "v1");
        ApplicationConfig.appendParameters(parameters, application);
        MatcherAssert.assertThat(parameters, Matchers.hasEntry("k1", "v1"));
        MatcherAssert.assertThat(parameters, Matchers.hasEntry(ACCEPT_FOREIGN_IP, "true"));
    }
}

