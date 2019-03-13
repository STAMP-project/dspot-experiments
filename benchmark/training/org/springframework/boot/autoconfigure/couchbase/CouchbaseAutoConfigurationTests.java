/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.couchbase;


import DefaultCouchbaseEnvironment.Builder;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseBucket;
import com.couchbase.client.java.cluster.ClusterInfo;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import java.util.function.Consumer;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link CouchbaseAutoConfiguration}.
 *
 * @author Edd? Mel?ndez
 * @author Stephane Nicoll
 */
public class CouchbaseAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(PropertyPlaceholderAutoConfiguration.class, CouchbaseAutoConfiguration.class));

    @Test
    public void bootstrapHostsIsRequired() {
        this.contextRunner.run(this::assertNoCouchbaseBeans);
    }

    @Test
    public void bootstrapHostsNotRequiredIfCouchbaseConfigurerIsSet() {
        this.contextRunner.withUserConfiguration(CouchbaseTestConfigurer.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            // No beans are going to be created
            assertNoCouchbaseBeans(context);
        });
    }

    @Test
    public void bootstrapHostsIgnoredIfCouchbaseConfigurerIsSet() {
        this.contextRunner.withUserConfiguration(CouchbaseTestConfigurer.class).withPropertyValues("spring.couchbase.bootstrapHosts=localhost").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertNoCouchbaseBeans(context);
        });
    }

    @Test
    public void customizeEnvEndpoints() {
        testCouchbaseEnv(( env) -> {
            assertThat(env.kvServiceConfig().minEndpoints()).isEqualTo(2);
            assertThat(env.kvServiceConfig().maxEndpoints()).isEqualTo(2);
            assertThat(env.queryServiceConfig().minEndpoints()).isEqualTo(3);
            assertThat(env.queryServiceConfig().maxEndpoints()).isEqualTo(5);
            assertThat(env.viewServiceConfig().minEndpoints()).isEqualTo(4);
            assertThat(env.viewServiceConfig().maxEndpoints()).isEqualTo(6);
        }, "spring.couchbase.env.endpoints.key-value=2", "spring.couchbase.env.endpoints.queryservice.min-endpoints=3", "spring.couchbase.env.endpoints.queryservice.max-endpoints=5", "spring.couchbase.env.endpoints.viewservice.min-endpoints=4", "spring.couchbase.env.endpoints.viewservice.max-endpoints=6");
    }

    @Test
    public void customizeEnvEndpointsUsesNewInfrastructure() {
        testCouchbaseEnv(( env) -> {
            assertThat(env.queryServiceConfig().minEndpoints()).isEqualTo(3);
            assertThat(env.queryServiceConfig().maxEndpoints()).isEqualTo(5);
            assertThat(env.viewServiceConfig().minEndpoints()).isEqualTo(4);
            assertThat(env.viewServiceConfig().maxEndpoints()).isEqualTo(6);
        }, "spring.couchbase.env.endpoints.queryservice.min-endpoints=3", "spring.couchbase.env.endpoints.queryservice.max-endpoints=5", "spring.couchbase.env.endpoints.viewservice.min-endpoints=4", "spring.couchbase.env.endpoints.viewservice.max-endpoints=6");
    }

    @Test
    public void customizeEnvEndpointsUsesNewInfrastructureWithOnlyMax() {
        testCouchbaseEnv(( env) -> {
            assertThat(env.queryServiceConfig().minEndpoints()).isEqualTo(1);
            assertThat(env.queryServiceConfig().maxEndpoints()).isEqualTo(5);
            assertThat(env.viewServiceConfig().minEndpoints()).isEqualTo(1);
            assertThat(env.viewServiceConfig().maxEndpoints()).isEqualTo(6);
        }, "spring.couchbase.env.endpoints.queryservice.max-endpoints=5", "spring.couchbase.env.endpoints.viewservice.max-endpoints=6");
    }

    @Test
    public void customizeEnvTimeouts() {
        testCouchbaseEnv(( env) -> {
            assertThat(env.connectTimeout()).isEqualTo(100);
            assertThat(env.kvTimeout()).isEqualTo(200);
            assertThat(env.queryTimeout()).isEqualTo(300);
            assertThat(env.socketConnectTimeout()).isEqualTo(400);
            assertThat(env.viewTimeout()).isEqualTo(500);
        }, "spring.couchbase.env.timeouts.connect=100", "spring.couchbase.env.timeouts.keyValue=200", "spring.couchbase.env.timeouts.query=300", "spring.couchbase.env.timeouts.socket-connect=400", "spring.couchbase.env.timeouts.view=500");
    }

    @Test
    public void enableSslNoEnabledFlag() {
        testCouchbaseEnv(( env) -> {
            assertThat(env.sslEnabled()).isTrue();
            assertThat(env.sslKeystoreFile()).isEqualTo("foo");
            assertThat(env.sslKeystorePassword()).isEqualTo("secret");
        }, "spring.couchbase.env.ssl.keyStore=foo", "spring.couchbase.env.ssl.keyStorePassword=secret");
    }

    @Test
    public void disableSslEvenWithKeyStore() {
        testCouchbaseEnv(( env) -> {
            assertThat(env.sslEnabled()).isFalse();
            assertThat(env.sslKeystoreFile()).isNull();
            assertThat(env.sslKeystorePassword()).isNull();
        }, "spring.couchbase.env.ssl.enabled=false", "spring.couchbase.env.ssl.keyStore=foo", "spring.couchbase.env.ssl.keyStorePassword=secret");
    }

    @Test
    public void customizeEnvWithCustomCouchbaseConfiguration() {
        this.contextRunner.withUserConfiguration(CouchbaseAutoConfigurationTests.CustomCouchbaseConfiguration.class).withPropertyValues("spring.couchbase.bootstrap-hosts=localhost", "spring.couchbase.env.timeouts.connect=100").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            DefaultCouchbaseEnvironment env = context.getBean(.class);
            assertThat(env.socketConnectTimeout()).isEqualTo(5000);
            assertThat(env.connectTimeout()).isEqualTo(2000);
        });
    }

    @Configuration
    static class CustomCouchbaseConfiguration extends CouchbaseConfiguration {
        CustomCouchbaseConfiguration(CouchbaseProperties properties) {
            super(properties);
        }

        @Override
        protected Builder initializeEnvironmentBuilder(CouchbaseProperties properties) {
            return super.initializeEnvironmentBuilder(properties).socketConnectTimeout(5000).connectTimeout(2000);
        }

        @Override
        public Cluster couchbaseCluster() {
            return Mockito.mock(Cluster.class);
        }

        @Override
        public ClusterInfo couchbaseClusterInfo() {
            return Mockito.mock(ClusterInfo.class);
        }

        @Override
        public Bucket couchbaseClient() {
            return Mockito.mock(CouchbaseBucket.class);
        }
    }
}

