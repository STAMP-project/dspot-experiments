/**
 * Copyright 2019 Confluent Inc.
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
package io.confluent.ksql.rest.ssl;


import RestConfig.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG;
import SslConfigs.SSL_KEY_PASSWORD_CONFIG;
import io.confluent.ksql.test.util.secure.ClientTrustStore;
import io.confluent.ksql.test.util.secure.ServerKeyStore;
import java.util.Map;
import javax.ws.rs.client.ClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultSslClientConfigurerTest {
    @Mock
    private ClientBuilder clientBuilder;

    private Map<String, String> clientProps;

    private DefaultSslClientConfigurer configurer;

    @Test
    public void shouldConfigureKeyStoreIfLocationSet() {
        // Given:
        clientProps.putAll(ServerKeyStore.keyStoreProps());
        final String keyPassword = clientProps.get(SSL_KEY_PASSWORD_CONFIG);
        // When:
        configurer.configureSsl(clientBuilder, clientProps);
        // Then:
        Mockito.verify(clientBuilder).keyStore(ArgumentMatchers.any(), ArgumentMatchers.eq(keyPassword));
    }

    @Test
    public void shouldConfigureTrustStoreIfLocationSet() {
        // Given:
        clientProps.putAll(ClientTrustStore.trustStoreProps());
        // When:
        configurer.configureSsl(clientBuilder, clientProps);
        // Then:
        Mockito.verify(clientBuilder).trustStore(ArgumentMatchers.any());
    }

    @Test
    public void shouldConfigureHostNameVerifierSet() {
        // Given:
        clientProps.put(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        // When:
        configurer.configureSsl(clientBuilder, clientProps);
        // Then:
        Mockito.verify(clientBuilder).hostnameVerifier(ArgumentMatchers.any());
    }
}

