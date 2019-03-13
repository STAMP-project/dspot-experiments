/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.schema.registry;


import KsqlSchemaRegistryClientFactory.SchemaRegistryClientFactory;
import SslConfigs.SSL_PROTOCOL_CONFIG;
import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.ksql.util.KsqlConfig;
import java.util.Map;
import java.util.function.Supplier;
import javax.net.ssl.SSLContext;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.ssl.SslFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author andy
created 3/26/18
 */
@RunWith(MockitoJUnitRunner.class)
public class KsqlSchemaRegistryClientFactoryTest {
    private static final SSLContext SSL_CONTEXT = KsqlSchemaRegistryClientFactoryTest.getTestSslContext();

    @Mock
    private Supplier<RestService> restServiceSupplier;

    @Mock
    private RestService restService;

    @Mock
    private SslFactory sslFactory;

    @Mock
    private SchemaRegistryClientFactory srClientFactory;

    @Test
    public void shouldSetSocketFactoryWhenNoSpecificSslConfig() {
        // Given:
        final KsqlConfig config = KsqlSchemaRegistryClientFactoryTest.config();
        final Map<String, Object> expectedConfigs = KsqlSchemaRegistryClientFactoryTest.defaultConfigs();
        // When:
        final SchemaRegistryClient client = get();
        // Then:
        MatcherAssert.assertThat(client, Matchers.is(Matchers.notNullValue()));
        Mockito.verify(sslFactory).configure(expectedConfigs);
        Mockito.verify(restService).setSslSocketFactory(ArgumentMatchers.isA(KsqlSchemaRegistryClientFactoryTest.SSL_CONTEXT.getSocketFactory().getClass()));
    }

    @Test
    public void shouldPickUpNonPrefixedSslConfig() {
        // Given:
        final KsqlConfig config = KsqlSchemaRegistryClientFactoryTest.config(SSL_PROTOCOL_CONFIG, "SSLv3");
        final Map<String, Object> expectedConfigs = KsqlSchemaRegistryClientFactoryTest.defaultConfigs();
        expectedConfigs.put(SSL_PROTOCOL_CONFIG, "SSLv3");
        // When:
        final SchemaRegistryClient client = get();
        // Then:
        MatcherAssert.assertThat(client, Matchers.is(Matchers.notNullValue()));
        Mockito.verify(sslFactory).configure(expectedConfigs);
        Mockito.verify(restService).setSslSocketFactory(ArgumentMatchers.isA(KsqlSchemaRegistryClientFactoryTest.SSL_CONTEXT.getSocketFactory().getClass()));
    }

    @Test
    public void shouldPickUpPrefixedSslConfig() {
        // Given:
        final KsqlConfig config = KsqlSchemaRegistryClientFactoryTest.config(("ksql.schema.registry." + (SslConfigs.SSL_PROTOCOL_CONFIG)), "SSLv3");
        final Map<String, Object> expectedConfigs = KsqlSchemaRegistryClientFactoryTest.defaultConfigs();
        expectedConfigs.put(SSL_PROTOCOL_CONFIG, "SSLv3");
        // When:
        final SchemaRegistryClient client = get();
        // Then:
        MatcherAssert.assertThat(client, Matchers.is(Matchers.notNullValue()));
        Mockito.verify(sslFactory).configure(expectedConfigs);
        Mockito.verify(restService).setSslSocketFactory(ArgumentMatchers.isA(KsqlSchemaRegistryClientFactoryTest.SSL_CONTEXT.getSocketFactory().getClass()));
    }

    @Test
    public void shouldPassBasicAuthCredentialsToSchemaRegistryClient() {
        // Given
        final Map<String, Object> schemaRegistryClientConfigs = ImmutableMap.of("ksql.schema.registry.basic.auth.credentials.source", "USER_INFO", "ksql.schema.registry.basic.auth.user.info", "username:password");
        final KsqlConfig config = new KsqlConfig(schemaRegistryClientConfigs);
        final Map<String, Object> expectedConfigs = KsqlSchemaRegistryClientFactoryTest.defaultConfigs();
        expectedConfigs.put("basic.auth.credentials.source", "USER_INFO");
        expectedConfigs.put("basic.auth.user.info", "username:password");
        // When:
        new KsqlSchemaRegistryClientFactory(config, restServiceSupplier, sslFactory, srClientFactory).get();
        // Then:
        srClientFactory.create(ArgumentMatchers.same(restService), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(expectedConfigs));
    }
}

