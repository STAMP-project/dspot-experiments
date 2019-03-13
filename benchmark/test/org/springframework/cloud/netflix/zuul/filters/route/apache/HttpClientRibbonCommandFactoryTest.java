/**
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.netflix.zuul.filters.route.apache;


import IClientConfigKey.Keys.ConnectTimeout;
import IClientConfigKey.Keys.ReadTimeout;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.config.ConfigurationManager;
import java.util.HashSet;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.ribbon.apache.RibbonLoadBalancingHttpClient;
import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;


/**
 *
 *
 * @author Ryan Baxter
 * @author Gang Li
 */
public class HttpClientRibbonCommandFactoryTest {
    SpringClientFactory springClientFactory;

    ZuulProperties zuulProperties;

    HttpClientRibbonCommandFactory ribbonCommandFactory;

    @Test
    public void testHystrixTimeoutValue() throws Exception {
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = this.ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(4000);
    }

    @Test
    public void testHystrixTimeoutValueSetting() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", 50);
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = this.ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(50);
    }

    @Test
    public void testHystrixTimeoutValueCommandSetting() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.service.execution.isolation.thread.timeoutInMilliseconds", 50);
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = this.ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(50);
    }

    @Test
    public void testHystrixTimeoutValueCommandAndDefaultSetting() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", 30);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.service.execution.isolation.thread.timeoutInMilliseconds", 50);
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = this.ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(50);
    }

    @Test
    public void testHystrixTimeoutValueRibbonTimeouts() throws Exception {
        SpringClientFactory springClientFactory = Mockito.mock(SpringClientFactory.class);
        ZuulProperties zuulProperties = new ZuulProperties();
        RibbonLoadBalancingHttpClient loadBalancingHttpClient = Mockito.mock(RibbonLoadBalancingHttpClient.class);
        IClientConfig clientConfig = new DefaultClientConfigImpl();
        clientConfig.set(ConnectTimeout, 100);
        clientConfig.set(ReadTimeout, 500);
        Mockito.doReturn(loadBalancingHttpClient).when(springClientFactory).getClient(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RibbonLoadBalancingHttpClient.class));
        Mockito.doReturn(clientConfig).when(springClientFactory).getClientConfig(ArgumentMatchers.anyString());
        HttpClientRibbonCommandFactory ribbonCommandFactory = new HttpClientRibbonCommandFactory(springClientFactory, zuulProperties, new HashSet<org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider>());
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(1200);
    }

    @Test
    public void testHystrixDefaultAndRibbonSetting() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", 30);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.ConnectTimeout", 1000);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.ReadTimeout", 1000);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.MaxAutoRetries", 1);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.MaxAutoRetriesNextServer", 2);
        SpringClientFactory springClientFactory = Mockito.mock(SpringClientFactory.class);
        ZuulProperties zuulProperties = new ZuulProperties();
        RibbonLoadBalancingHttpClient loadBalancingHttpClient = Mockito.mock(RibbonLoadBalancingHttpClient.class);
        IClientConfig clientConfig = new DefaultClientConfigImpl();
        clientConfig.set(ConnectTimeout, 100);
        clientConfig.set(ReadTimeout, 500);
        Mockito.doReturn(loadBalancingHttpClient).when(springClientFactory).getClient(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RibbonLoadBalancingHttpClient.class));
        Mockito.doReturn(clientConfig).when(springClientFactory).getClientConfig(ArgumentMatchers.anyString());
        HttpClientRibbonCommandFactory ribbonCommandFactory = new HttpClientRibbonCommandFactory(springClientFactory, zuulProperties, new HashSet<org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider>());
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(30);
    }

    @Test
    public void testHystrixCommandAndRibbonSetting() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", 30);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.service.execution.isolation.thread.timeoutInMilliseconds", 50);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.ConnectTimeout", 1000);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.ReadTimeout", 1000);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.MaxAutoRetries", 1);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.MaxAutoRetriesNextServer", 2);
        SpringClientFactory springClientFactory = Mockito.mock(SpringClientFactory.class);
        ZuulProperties zuulProperties = new ZuulProperties();
        RibbonLoadBalancingHttpClient loadBalancingHttpClient = Mockito.mock(RibbonLoadBalancingHttpClient.class);
        IClientConfig clientConfig = new DefaultClientConfigImpl();
        clientConfig.set(ConnectTimeout, 100);
        clientConfig.set(ReadTimeout, 500);
        Mockito.doReturn(loadBalancingHttpClient).when(springClientFactory).getClient(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RibbonLoadBalancingHttpClient.class));
        Mockito.doReturn(clientConfig).when(springClientFactory).getClientConfig(ArgumentMatchers.anyString());
        HttpClientRibbonCommandFactory ribbonCommandFactory = new HttpClientRibbonCommandFactory(springClientFactory, zuulProperties, new HashSet<org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider>());
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(50);
    }

    @Test
    public void testDefaultRibbonSetting() throws Exception {
        SpringClientFactory springClientFactory = Mockito.mock(SpringClientFactory.class);
        ZuulProperties zuulProperties = new ZuulProperties();
        RibbonLoadBalancingHttpClient loadBalancingHttpClient = Mockito.mock(RibbonLoadBalancingHttpClient.class);
        IClientConfig clientConfig = new DefaultClientConfigImpl();
        Mockito.doReturn(loadBalancingHttpClient).when(springClientFactory).getClient(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RibbonLoadBalancingHttpClient.class));
        Mockito.doReturn(clientConfig).when(springClientFactory).getClientConfig(ArgumentMatchers.anyString());
        HttpClientRibbonCommandFactory commandFactory = new HttpClientRibbonCommandFactory(springClientFactory, zuulProperties, new HashSet<org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider>());
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = commandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(4000);
    }

    @Test
    public void testRibbonTimeoutAndRibbonRetriesDefaultAndNameSpaceSetting() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("service.test.ConnectTimeout", 1000);
        ConfigurationManager.getConfigInstance().setProperty("service.test.ReadTimeout", 1000);
        SpringClientFactory springClientFactory = Mockito.mock(SpringClientFactory.class);
        ZuulProperties zuulProperties = new ZuulProperties();
        RibbonLoadBalancingHttpClient loadBalancingHttpClient = Mockito.mock(RibbonLoadBalancingHttpClient.class);
        IClientConfig clientConfig = new DefaultClientConfigImpl();
        clientConfig.set(ConnectTimeout, 100);
        clientConfig.set(ReadTimeout, 500);
        Mockito.doReturn(loadBalancingHttpClient).when(springClientFactory).getClient(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RibbonLoadBalancingHttpClient.class));
        Mockito.doReturn(clientConfig).when(springClientFactory).getClientConfig(ArgumentMatchers.anyString());
        HttpClientRibbonCommandFactory ribbonCommandFactory = new HttpClientRibbonCommandFactory(springClientFactory, zuulProperties, new HashSet<org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider>());
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(1200);
    }

    @Test
    public void testRibbonTimeoutAndRibbonRetriesDefaultAndDefaultSpaceSetting() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.ConnectTimeout", 1000);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.ReadTimeout", 1000);
        SpringClientFactory springClientFactory = Mockito.mock(SpringClientFactory.class);
        ZuulProperties zuulProperties = new ZuulProperties();
        RibbonLoadBalancingHttpClient loadBalancingHttpClient = Mockito.mock(RibbonLoadBalancingHttpClient.class);
        IClientConfig clientConfig = new DefaultClientConfigImpl();
        clientConfig.set(ConnectTimeout, 100);
        clientConfig.set(ReadTimeout, 500);
        Mockito.doReturn(loadBalancingHttpClient).when(springClientFactory).getClient(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RibbonLoadBalancingHttpClient.class));
        Mockito.doReturn(clientConfig).when(springClientFactory).getClientConfig(ArgumentMatchers.anyString());
        HttpClientRibbonCommandFactory ribbonCommandFactory = new HttpClientRibbonCommandFactory(springClientFactory, zuulProperties, new HashSet<org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider>());
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(4000);
    }

    @Test
    public void testRibbonTimeoutAndRibbonNameSpaceRetriesDefaultAndDefaultSpaceSetting() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.ConnectTimeout", 1000);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.ReadTimeout", 1000);
        ConfigurationManager.getConfigInstance().setProperty("service.test.MaxAutoRetriesNextServer", 2);
        SpringClientFactory springClientFactory = Mockito.mock(SpringClientFactory.class);
        ZuulProperties zuulProperties = new ZuulProperties();
        RibbonLoadBalancingHttpClient loadBalancingHttpClient = Mockito.mock(RibbonLoadBalancingHttpClient.class);
        IClientConfig clientConfig = new DefaultClientConfigImpl();
        clientConfig.set(ConnectTimeout, 100);
        clientConfig.set(ReadTimeout, 500);
        Mockito.doReturn(loadBalancingHttpClient).when(springClientFactory).getClient(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RibbonLoadBalancingHttpClient.class));
        Mockito.doReturn(clientConfig).when(springClientFactory).getClientConfig(ArgumentMatchers.anyString());
        HttpClientRibbonCommandFactory ribbonCommandFactory = new HttpClientRibbonCommandFactory(springClientFactory, zuulProperties, new HashSet<org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider>());
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(4000);
    }

    @Test
    public void testRibbonRetriesAndRibbonTimeoutSetting() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.MaxAutoRetries", 1);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.MaxAutoRetriesNextServer", 2);
        SpringClientFactory springClientFactory = Mockito.mock(SpringClientFactory.class);
        ZuulProperties zuulProperties = new ZuulProperties();
        RibbonLoadBalancingHttpClient loadBalancingHttpClient = Mockito.mock(RibbonLoadBalancingHttpClient.class);
        IClientConfig clientConfig = new DefaultClientConfigImpl();
        clientConfig.set(ConnectTimeout, 100);
        clientConfig.set(ReadTimeout, 500);
        Mockito.doReturn(loadBalancingHttpClient).when(springClientFactory).getClient(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RibbonLoadBalancingHttpClient.class));
        Mockito.doReturn(clientConfig).when(springClientFactory).getClientConfig(ArgumentMatchers.anyString());
        HttpClientRibbonCommandFactory ribbonCommandFactory = new HttpClientRibbonCommandFactory(springClientFactory, zuulProperties, new HashSet<org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider>());
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(3600);
    }

    @Test
    public void testRibbonCommandRetriesAndRibbonCommandTimeoutSetting() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.ConnectTimeout", 1000);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.ReadTimeout", 1000);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.MaxAutoRetries", 1);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.MaxAutoRetriesNextServer", 2);
        SpringClientFactory springClientFactory = Mockito.mock(SpringClientFactory.class);
        ZuulProperties zuulProperties = new ZuulProperties();
        RibbonLoadBalancingHttpClient loadBalancingHttpClient = Mockito.mock(RibbonLoadBalancingHttpClient.class);
        IClientConfig clientConfig = new DefaultClientConfigImpl();
        clientConfig.set(ConnectTimeout, 100);
        clientConfig.set(ReadTimeout, 500);
        Mockito.doReturn(loadBalancingHttpClient).when(springClientFactory).getClient(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RibbonLoadBalancingHttpClient.class));
        Mockito.doReturn(clientConfig).when(springClientFactory).getClientConfig(ArgumentMatchers.anyString());
        HttpClientRibbonCommandFactory ribbonCommandFactory = new HttpClientRibbonCommandFactory(springClientFactory, zuulProperties, new HashSet<org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider>());
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(12000);
    }

    @Test
    public void testRibbonCommandRetriesAndRibbonCommandTimeoutPartOfSetting() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.ConnectTimeout", 1000);
        ConfigurationManager.getConfigInstance().setProperty("service.ribbon.MaxAutoRetries", 1);
        SpringClientFactory springClientFactory = Mockito.mock(SpringClientFactory.class);
        ZuulProperties zuulProperties = new ZuulProperties();
        RibbonLoadBalancingHttpClient loadBalancingHttpClient = Mockito.mock(RibbonLoadBalancingHttpClient.class);
        IClientConfig clientConfig = new DefaultClientConfigImpl();
        clientConfig.set(ConnectTimeout, 100);
        clientConfig.set(ReadTimeout, 500);
        Mockito.doReturn(loadBalancingHttpClient).when(springClientFactory).getClient(ArgumentMatchers.anyString(), ArgumentMatchers.eq(RibbonLoadBalancingHttpClient.class));
        Mockito.doReturn(clientConfig).when(springClientFactory).getClientConfig(ArgumentMatchers.anyString());
        HttpClientRibbonCommandFactory ribbonCommandFactory = new HttpClientRibbonCommandFactory(springClientFactory, zuulProperties, new HashSet<org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider>());
        RibbonCommandContext context = Mockito.mock(RibbonCommandContext.class);
        Mockito.doReturn("service").when(context).getServiceId();
        HttpClientRibbonCommand ribbonCommand = ribbonCommandFactory.create(context);
        assertThat(ribbonCommand.getProperties().executionTimeoutInMilliseconds().get().intValue()).isEqualTo(6000);
    }
}

