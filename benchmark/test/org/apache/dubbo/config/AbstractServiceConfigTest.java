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


import Constants.EXPORTER_LISTENER_KEY;
import Constants.SERVICE_FILTER_KEY;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class AbstractServiceConfigTest {
    @Test
    public void testVersion() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setVersion("version");
        MatcherAssert.assertThat(getVersion(), Matchers.equalTo("version"));
    }

    @Test
    public void testGroup() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setGroup("group");
        MatcherAssert.assertThat(getGroup(), Matchers.equalTo("group"));
    }

    @Test
    public void testDelay() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setDelay(1000);
        MatcherAssert.assertThat(getDelay(), Matchers.equalTo(1000));
    }

    @Test
    public void testExport() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setExport(true);
        MatcherAssert.assertThat(getExport(), Matchers.is(true));
    }

    @Test
    public void testWeight() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setWeight(500);
        MatcherAssert.assertThat(getWeight(), Matchers.equalTo(500));
    }

    @Test
    public void testDocument() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setDocument("http://dubbo.io");
        MatcherAssert.assertThat(getDocument(), Matchers.equalTo("http://dubbo.io"));
        Map<String, String> parameters = new HashMap<String, String>();
        AbstractServiceConfig.appendParameters(parameters, serviceConfig);
        MatcherAssert.assertThat(parameters, Matchers.hasEntry("document", "http%3A%2F%2Fdubbo.io"));
    }

    @Test
    public void testToken() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        serviceConfig.setToken("token");
        MatcherAssert.assertThat(getToken(), Matchers.equalTo("token"));
        serviceConfig.setToken(((Boolean) (null)));
        MatcherAssert.assertThat(getToken(), Matchers.nullValue());
        setToken(true);
        MatcherAssert.assertThat(getToken(), Matchers.is("true"));
    }

    @Test
    public void testDeprecated() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setDeprecated(true);
        MatcherAssert.assertThat(isDeprecated(), Matchers.is(true));
    }

    @Test
    public void testDynamic() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setDynamic(true);
        MatcherAssert.assertThat(isDynamic(), Matchers.is(true));
    }

    @Test
    public void testProtocol() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        MatcherAssert.assertThat(getProtocol(), Matchers.nullValue());
        serviceConfig.setProtocol(new ProtocolConfig());
        MatcherAssert.assertThat(getProtocol(), Matchers.notNullValue());
        serviceConfig.setProtocols(Collections.singletonList(new ProtocolConfig()));
        MatcherAssert.assertThat(getProtocols(), Matchers.hasSize(1));
    }

    @Test
    public void testAccesslog() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        serviceConfig.setAccesslog("access.log");
        MatcherAssert.assertThat(getAccesslog(), Matchers.equalTo("access.log"));
        serviceConfig.setAccesslog(((Boolean) (null)));
        MatcherAssert.assertThat(getAccesslog(), Matchers.nullValue());
        setAccesslog(true);
        MatcherAssert.assertThat(getAccesslog(), Matchers.equalTo("true"));
    }

    @Test
    public void testExecutes() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setExecutes(10);
        MatcherAssert.assertThat(getExecutes(), Matchers.equalTo(10));
    }

    @Test
    public void testFilter() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setFilter("mockfilter");
        MatcherAssert.assertThat(getFilter(), Matchers.equalTo("mockfilter"));
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(SERVICE_FILTER_KEY, "prefilter");
        AbstractServiceConfig.appendParameters(parameters, serviceConfig);
        MatcherAssert.assertThat(parameters, Matchers.hasEntry(SERVICE_FILTER_KEY, "prefilter,mockfilter"));
    }

    @Test
    public void testListener() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setListener("mockexporterlistener");
        MatcherAssert.assertThat(getListener(), Matchers.equalTo("mockexporterlistener"));
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(EXPORTER_LISTENER_KEY, "prelistener");
        AbstractServiceConfig.appendParameters(parameters, serviceConfig);
        MatcherAssert.assertThat(parameters, Matchers.hasEntry(EXPORTER_LISTENER_KEY, "prelistener,mockexporterlistener"));
    }

    @Test
    public void testRegister() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setRegister(true);
        MatcherAssert.assertThat(isRegister(), Matchers.is(true));
    }

    @Test
    public void testWarmup() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setWarmup(100);
        MatcherAssert.assertThat(getWarmup(), Matchers.equalTo(100));
    }

    @Test
    public void testSerialization() throws Exception {
        AbstractServiceConfigTest.ServiceConfig serviceConfig = new AbstractServiceConfigTest.ServiceConfig();
        setSerialization("serialization");
        MatcherAssert.assertThat(getSerialization(), Matchers.equalTo("serialization"));
    }

    private static class ServiceConfig extends AbstractServiceConfig {}
}

