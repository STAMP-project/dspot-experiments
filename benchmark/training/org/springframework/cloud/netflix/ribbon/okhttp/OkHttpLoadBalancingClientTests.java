/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.ribbon.okhttp;


import CommonClientConfigKey.ConnectTimeout;
import CommonClientConfigKey.IsSecure;
import CommonClientConfigKey.ReadTimeout;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.DefaultServerIntrospector;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Spencer Gibb
 */
public class OkHttpLoadBalancingClientTests {
    @Test
    public void testOkHttpClientUseDefaultsNoOverride() throws Exception {
        OkHttpClient result = getHttpClient(OkHttpLoadBalancingClientTests.UseDefaults.class, null);
        assertThat(result.followRedirects()).isFalse();
    }

    @Test
    public void testOkHttpClientDoNotFollowRedirectsNoOverride() throws Exception {
        OkHttpClient result = getHttpClient(OkHttpLoadBalancingClientTests.DoNotFollowRedirects.class, null);
        assertThat(result.followRedirects()).isFalse();
    }

    @Test
    public void testOkHttpClientFollowRedirectsNoOverride() throws Exception {
        OkHttpClient result = getHttpClient(OkHttpLoadBalancingClientTests.FollowRedirects.class, null);
        assertThat(result.followRedirects()).isTrue();
    }

    @Test
    public void testOkHttpClientDoNotFollowRedirectsOverrideWithFollowRedirects() throws Exception {
        DefaultClientConfigImpl override = new DefaultClientConfigImpl();
        override.set(CommonClientConfigKey.FollowRedirects, true);
        override.set(IsSecure, false);
        OkHttpClient result = getHttpClient(OkHttpLoadBalancingClientTests.DoNotFollowRedirects.class, override);
        assertThat(result.followRedirects()).isTrue();
    }

    @Test
    public void testOkHttpClientFollowRedirectsOverrideWithDoNotFollowRedirects() throws Exception {
        DefaultClientConfigImpl override = new DefaultClientConfigImpl();
        override.set(CommonClientConfigKey.FollowRedirects, false);
        override.set(IsSecure, false);
        OkHttpClient result = getHttpClient(OkHttpLoadBalancingClientTests.FollowRedirects.class, override);
        assertThat(result.followRedirects()).isFalse();
    }

    @Test
    public void testTimeouts() throws Exception {
        OkHttpClient result = getHttpClient(OkHttpLoadBalancingClientTests.Timeouts.class, null);
        assertThat(result.readTimeoutMillis()).isEqualTo(50000);
        assertThat(result.connectTimeoutMillis()).isEqualTo(60000);
    }

    @Test
    public void testDefaultTimeouts() throws Exception {
        OkHttpClient result = getHttpClient(OkHttpLoadBalancingClientTests.UseDefaults.class, null);
        assertThat(result.readTimeoutMillis()).isEqualTo(1000);
        assertThat(result.connectTimeoutMillis()).isEqualTo(1000);
    }

    @Test
    public void testTimeoutsOverride() throws Exception {
        DefaultClientConfigImpl override = new DefaultClientConfigImpl();
        override.set(ConnectTimeout, 60);
        override.set(ReadTimeout, 50);
        OkHttpClient result = getHttpClient(OkHttpLoadBalancingClientTests.Timeouts.class, override);
        assertThat(result.readTimeoutMillis()).isEqualTo(50);
        assertThat(result.connectTimeoutMillis()).isEqualTo(60);
    }

    @Test
    public void testUpdatedTimeouts() throws Exception {
        SpringClientFactory factory = new SpringClientFactory();
        OkHttpClient result = getHttpClient(OkHttpLoadBalancingClientTests.Timeouts.class, null, factory);
        assertThat(result.readTimeoutMillis()).isEqualTo(50000);
        assertThat(result.connectTimeoutMillis()).isEqualTo(60000);
        IClientConfig config = factory.getClientConfig("service");
        config.set(ConnectTimeout, 60);
        config.set(ReadTimeout, 50);
        result = getHttpClient(OkHttpLoadBalancingClientTests.Timeouts.class, null, factory);
        assertThat(result.readTimeoutMillis()).isEqualTo(50);
        assertThat(result.connectTimeoutMillis()).isEqualTo(60);
    }

    @Configuration
    protected static class OkHttpClientConfiguration {
        @Autowired(required = false)
        IClientConfig clientConfig;

        @Bean
        public OkHttpLoadBalancingClient okHttpLoadBalancingClient() {
            if ((clientConfig) == null) {
                clientConfig = new DefaultClientConfigImpl();
            }
            return new OkHttpLoadBalancingClient(new OkHttpClient(), clientConfig, new DefaultServerIntrospector());
        }
    }

    @Configuration
    protected static class UseDefaults {}

    @Configuration
    protected static class FollowRedirects {
        @Bean
        public IClientConfig clientConfig() {
            DefaultClientConfigImpl config = new DefaultClientConfigImpl();
            config.set(CommonClientConfigKey.FollowRedirects, true);
            return config;
        }
    }

    @Configuration
    protected static class DoNotFollowRedirects {
        @Bean
        public IClientConfig clientConfig() {
            DefaultClientConfigImpl config = new DefaultClientConfigImpl();
            config.set(CommonClientConfigKey.FollowRedirects, false);
            return config;
        }
    }

    @Configuration
    protected static class Timeouts {
        @Bean
        public IClientConfig clientConfig() {
            DefaultClientConfigImpl config = new DefaultClientConfigImpl();
            config.set(ConnectTimeout, 60000);
            config.set(ReadTimeout, 50000);
            return config;
        }
    }
}

