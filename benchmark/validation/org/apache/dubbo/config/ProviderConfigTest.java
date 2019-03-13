/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config;


import com.alibaba.dubbo.config.ProviderConfig;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ProviderConfigTest {
    @Test
    public void testProtocol() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setProtocol("protocol");
        MatcherAssert.assertThat(provider.getProtocol().getName(), Matchers.equalTo("protocol"));
    }

    @Test
    public void testDefault() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setDefault(true);
        Map<String, String> parameters = new HashMap<String, String>();
        ProviderConfig.appendParameters(parameters, provider);
        MatcherAssert.assertThat(provider.isDefault(), Matchers.is(true));
        MatcherAssert.assertThat(parameters, Matchers.not(Matchers.hasKey("default")));
    }

    @Test
    public void testHost() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setHost("demo-host");
        Map<String, String> parameters = new HashMap<String, String>();
        ProviderConfig.appendParameters(parameters, provider);
        MatcherAssert.assertThat(provider.getHost(), Matchers.equalTo("demo-host"));
        MatcherAssert.assertThat(parameters, Matchers.not(Matchers.hasKey("host")));
    }

    @Test
    public void testPort() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setPort(8080);
        Map<String, String> parameters = new HashMap<String, String>();
        ProviderConfig.appendParameters(parameters, provider);
        MatcherAssert.assertThat(provider.getPort(), Matchers.is(8080));
        MatcherAssert.assertThat(parameters, Matchers.not(Matchers.hasKey("port")));
    }

    @Test
    public void testPath() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setPath("/path");
        Map<String, String> parameters = new HashMap<String, String>();
        ProviderConfig.appendParameters(parameters, provider);
        MatcherAssert.assertThat(provider.getPath(), Matchers.equalTo("/path"));
        MatcherAssert.assertThat(provider.getContextpath(), Matchers.equalTo("/path"));
        MatcherAssert.assertThat(parameters, Matchers.not(Matchers.hasKey("path")));
    }

    @Test
    public void testContextPath() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setContextpath("/context-path");
        Map<String, String> parameters = new HashMap<String, String>();
        ProviderConfig.appendParameters(parameters, provider);
        MatcherAssert.assertThat(provider.getContextpath(), Matchers.equalTo("/context-path"));
        MatcherAssert.assertThat(parameters, Matchers.not(Matchers.hasKey("/context-path")));
    }

    @Test
    public void testThreads() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setThreads(10);
        MatcherAssert.assertThat(provider.getThreads(), Matchers.is(10));
    }

    @Test
    public void testIothreads() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setIothreads(10);
        MatcherAssert.assertThat(provider.getIothreads(), Matchers.is(10));
    }

    @Test
    public void testQueues() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setQueues(10);
        MatcherAssert.assertThat(provider.getQueues(), Matchers.is(10));
    }

    @Test
    public void testAccepts() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setAccepts(10);
        MatcherAssert.assertThat(provider.getAccepts(), Matchers.is(10));
    }

    @Test
    public void testCharset() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setCharset("utf-8");
        MatcherAssert.assertThat(provider.getCharset(), Matchers.equalTo("utf-8"));
    }

    @Test
    public void testPayload() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setPayload(10);
        MatcherAssert.assertThat(provider.getPayload(), Matchers.is(10));
    }

    @Test
    public void testBuffer() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setBuffer(10);
        MatcherAssert.assertThat(provider.getBuffer(), Matchers.is(10));
    }

    @Test
    public void testServer() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setServer("demo-server");
        MatcherAssert.assertThat(provider.getServer(), Matchers.equalTo("demo-server"));
    }

    @Test
    public void testClient() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setClient("client");
        MatcherAssert.assertThat(provider.getClient(), Matchers.equalTo("client"));
    }

    @Test
    public void testPrompt() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setPrompt("#");
        Map<String, String> parameters = new HashMap<String, String>();
        ProviderConfig.appendParameters(parameters, provider);
        MatcherAssert.assertThat(provider.getPrompt(), Matchers.equalTo("#"));
        MatcherAssert.assertThat(parameters, Matchers.hasEntry("prompt", "%23"));
    }

    @Test
    public void testDispatcher() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setDispatcher("mockdispatcher");
        MatcherAssert.assertThat(provider.getDispatcher(), Matchers.equalTo("mockdispatcher"));
    }

    @Test
    public void testNetworker() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setNetworker("networker");
        MatcherAssert.assertThat(provider.getNetworker(), Matchers.equalTo("networker"));
    }

    @Test
    public void testWait() throws Exception {
        ProviderConfig provider = new ProviderConfig();
        provider.setWait(10);
        MatcherAssert.assertThat(provider.getWait(), Matchers.equalTo(10));
    }
}

