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


import Constants.SHUTDOWN_TIMEOUT_KEY;
import Constants.SHUTDOWN_WAIT_KEY;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class RegistryConfigTest {
    @Test
    public void testProtocol() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setProtocol("protocol");
        MatcherAssert.assertThat(registry.getProtocol(), Matchers.equalTo(registry.getProtocol()));
    }

    @Test
    public void testAddress() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("localhost");
        MatcherAssert.assertThat(registry.getAddress(), Matchers.equalTo("localhost"));
        Map<String, String> parameters = new HashMap<String, String>();
        RegistryConfig.appendParameters(parameters, registry);
        MatcherAssert.assertThat(parameters, Matchers.not(Matchers.hasKey("address")));
    }

    @Test
    public void testUsername() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setUsername("username");
        MatcherAssert.assertThat(registry.getUsername(), Matchers.equalTo("username"));
    }

    @Test
    public void testPassword() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setPassword("password");
        MatcherAssert.assertThat(registry.getPassword(), Matchers.equalTo("password"));
    }

    @Test
    public void testWait() throws Exception {
        try {
            RegistryConfig registry = new RegistryConfig();
            registry.setWait(10);
            MatcherAssert.assertThat(registry.getWait(), CoreMatchers.is(10));
            MatcherAssert.assertThat(System.getProperty(SHUTDOWN_WAIT_KEY), Matchers.equalTo("10"));
        } finally {
            System.clearProperty(SHUTDOWN_TIMEOUT_KEY);
        }
    }

    @Test
    public void testCheck() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setCheck(true);
        MatcherAssert.assertThat(registry.isCheck(), CoreMatchers.is(true));
    }

    @Test
    public void testFile() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setFile("file");
        MatcherAssert.assertThat(registry.getFile(), Matchers.equalTo("file"));
    }

    @Test
    public void testTransporter() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setTransporter("transporter");
        MatcherAssert.assertThat(registry.getTransporter(), Matchers.equalTo("transporter"));
    }

    @Test
    public void testClient() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setClient("client");
        MatcherAssert.assertThat(registry.getClient(), Matchers.equalTo("client"));
    }

    @Test
    public void testTimeout() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setTimeout(10);
        MatcherAssert.assertThat(registry.getTimeout(), CoreMatchers.is(10));
    }

    @Test
    public void testSession() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setSession(10);
        MatcherAssert.assertThat(registry.getSession(), CoreMatchers.is(10));
    }

    @Test
    public void testDynamic() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setDynamic(true);
        MatcherAssert.assertThat(registry.isDynamic(), CoreMatchers.is(true));
    }

    @Test
    public void testRegister() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setRegister(true);
        MatcherAssert.assertThat(registry.isRegister(), CoreMatchers.is(true));
    }

    @Test
    public void testSubscribe() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setSubscribe(true);
        MatcherAssert.assertThat(registry.isSubscribe(), CoreMatchers.is(true));
    }

    @Test
    public void testCluster() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setCluster("cluster");
        MatcherAssert.assertThat(registry.getCluster(), Matchers.equalTo("cluster"));
    }

    @Test
    public void testGroup() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setGroup("group");
        MatcherAssert.assertThat(registry.getGroup(), Matchers.equalTo("group"));
    }

    @Test
    public void testVersion() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setVersion("1.0.0");
        MatcherAssert.assertThat(registry.getVersion(), Matchers.equalTo("1.0.0"));
    }

    @Test
    public void testParameters() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setParameters(Collections.singletonMap("k1", "v1"));
        MatcherAssert.assertThat(registry.getParameters(), Matchers.hasEntry("k1", "v1"));
        Map<String, String> parameters = new HashMap<String, String>();
        RegistryConfig.appendParameters(parameters, registry);
        MatcherAssert.assertThat(parameters, Matchers.hasEntry("k1", "v1"));
    }

    @Test
    public void testDefault() throws Exception {
        RegistryConfig registry = new RegistryConfig();
        registry.setDefault(true);
        MatcherAssert.assertThat(registry.isDefault(), CoreMatchers.is(true));
    }
}

