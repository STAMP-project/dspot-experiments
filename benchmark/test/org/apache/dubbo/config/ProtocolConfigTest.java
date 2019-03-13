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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.dubbo.config.mock.MockProtocol2;
import org.apache.dubbo.rpc.Protocol;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class ProtocolConfigTest {
    @Test
    public void testDestroy() throws Exception {
        Protocol protocol = Mockito.mock(Protocol.class);
        MockProtocol2.delegate = protocol;
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("mockprotocol2");
        protocolConfig.destroy();
        Mockito.verify(protocol).destroy();
    }

    @Test
    public void testName() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("name");
        Map<String, String> parameters = new HashMap<String, String>();
        ProtocolConfig.appendParameters(parameters, protocol);
        MatcherAssert.assertThat(protocol.getName(), Matchers.equalTo("name"));
        MatcherAssert.assertThat(protocol.getId(), Matchers.equalTo("name"));
        MatcherAssert.assertThat(parameters.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testHost() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setHost("host");
        Map<String, String> parameters = new HashMap<String, String>();
        ProtocolConfig.appendParameters(parameters, protocol);
        MatcherAssert.assertThat(protocol.getHost(), Matchers.equalTo("host"));
        MatcherAssert.assertThat(parameters.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testPort() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setPort(8080);
        Map<String, String> parameters = new HashMap<String, String>();
        ProtocolConfig.appendParameters(parameters, protocol);
        MatcherAssert.assertThat(protocol.getPort(), Matchers.equalTo(8080));
        MatcherAssert.assertThat(parameters.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testPath() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setContextpath("context-path");
        Map<String, String> parameters = new HashMap<String, String>();
        ProtocolConfig.appendParameters(parameters, protocol);
        MatcherAssert.assertThat(protocol.getPath(), Matchers.equalTo("context-path"));
        MatcherAssert.assertThat(protocol.getContextpath(), Matchers.equalTo("context-path"));
        MatcherAssert.assertThat(parameters.isEmpty(), Matchers.is(true));
        protocol.setPath("path");
        MatcherAssert.assertThat(protocol.getPath(), Matchers.equalTo("path"));
        MatcherAssert.assertThat(protocol.getContextpath(), Matchers.equalTo("path"));
    }

    @Test
    public void testCorethreads() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setCorethreads(10);
        MatcherAssert.assertThat(protocol.getCorethreads(), Matchers.is(10));
    }

    @Test
    public void testThreads() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setThreads(10);
        MatcherAssert.assertThat(protocol.getThreads(), Matchers.is(10));
    }

    @Test
    public void testIothreads() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setIothreads(10);
        MatcherAssert.assertThat(protocol.getIothreads(), Matchers.is(10));
    }

    @Test
    public void testQueues() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setQueues(10);
        MatcherAssert.assertThat(protocol.getQueues(), Matchers.is(10));
    }

    @Test
    public void testAccepts() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setAccepts(10);
        MatcherAssert.assertThat(protocol.getAccepts(), Matchers.is(10));
    }

    @Test
    public void testCodec() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("dubbo");
        protocol.setCodec("mockcodec");
        MatcherAssert.assertThat(protocol.getCodec(), Matchers.equalTo("mockcodec"));
    }

    @Test
    public void testAccesslog() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setAccesslog("access.log");
        MatcherAssert.assertThat(protocol.getAccesslog(), Matchers.equalTo("access.log"));
    }

    @Test
    public void testTelnet() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setTelnet("mocktelnethandler");
        MatcherAssert.assertThat(protocol.getTelnet(), Matchers.equalTo("mocktelnethandler"));
    }

    @Test
    public void testRegister() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setRegister(true);
        MatcherAssert.assertThat(protocol.isRegister(), Matchers.is(true));
    }

    @Test
    public void testTransporter() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setTransporter("mocktransporter");
        MatcherAssert.assertThat(protocol.getTransporter(), Matchers.equalTo("mocktransporter"));
    }

    @Test
    public void testExchanger() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setExchanger("mockexchanger");
        MatcherAssert.assertThat(protocol.getExchanger(), Matchers.equalTo("mockexchanger"));
    }

    @Test
    public void testDispatcher() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setDispatcher("mockdispatcher");
        MatcherAssert.assertThat(protocol.getDispatcher(), Matchers.equalTo("mockdispatcher"));
    }

    @Test
    public void testNetworker() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setNetworker("networker");
        MatcherAssert.assertThat(protocol.getNetworker(), Matchers.equalTo("networker"));
    }

    @Test
    public void testParameters() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setParameters(Collections.singletonMap("k1", "v1"));
        MatcherAssert.assertThat(protocol.getParameters(), Matchers.hasEntry("k1", "v1"));
    }

    @Test
    public void testDefault() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setDefault(true);
        MatcherAssert.assertThat(protocol.isDefault(), Matchers.is(true));
    }

    @Test
    public void testKeepAlive() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setKeepAlive(true);
        MatcherAssert.assertThat(protocol.getKeepAlive(), Matchers.is(true));
    }

    @Test
    public void testOptimizer() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setOptimizer("optimizer");
        MatcherAssert.assertThat(protocol.getOptimizer(), Matchers.equalTo("optimizer"));
    }

    @Test
    public void testExtension() throws Exception {
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setExtension("extension");
        MatcherAssert.assertThat(protocol.getExtension(), Matchers.equalTo("extension"));
    }
}

