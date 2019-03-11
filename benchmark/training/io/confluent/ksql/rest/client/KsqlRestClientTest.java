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
package io.confluent.ksql.rest.client;


import RestConfig.SSL_TRUSTSTORE_LOCATION_CONFIG;
import io.confluent.ksql.rest.client.exception.KsqlRestClientException;
import io.confluent.ksql.rest.ssl.SslClientConfigurer;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KsqlRestClientTest {
    private static final String SERVER_ADDRESS = "http://timbuktu";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ClientBuilder clientBuilder;

    @Mock
    private SslClientConfigurer sslClientConfigurer;

    @Mock
    private Client client;

    private Map<String, String> clientProps;

    private Map<String, String> localProps;

    @Test
    public void shouldConfigureSslOnTheClient() {
        // Given:
        clientProps.put(SSL_TRUSTSTORE_LOCATION_CONFIG, "/trust/store/path");
        // When:
        new KsqlRestClient(KsqlRestClientTest.SERVER_ADDRESS, localProps, clientProps, clientBuilder, sslClientConfigurer);
        // Then:
        Mockito.verify(sslClientConfigurer).configureSsl(clientBuilder, clientProps);
    }

    @Test
    public void shouldThrowIfFailedToConfigureClient() {
        // Given:
        Mockito.when(clientBuilder.register(ArgumentMatchers.any(Object.class))).thenThrow(new RuntimeException("boom"));
        // Then:
        expectedException.expect(KsqlRestClientException.class);
        expectedException.expectMessage("Failed to configure rest client");
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.is("boom")));
        // When:
        new KsqlRestClient(KsqlRestClientTest.SERVER_ADDRESS, localProps, clientProps, clientBuilder, sslClientConfigurer);
    }

    @Test
    public void shouldThrowOnInvalidServerAddress() {
        // Then:
        expectedException.expect(KsqlRestClientException.class);
        expectedException.expectMessage("The supplied serverAddress is invalid: timbuktu");
        // When:
        new KsqlRestClient("timbuktu", localProps, clientProps, clientBuilder, sslClientConfigurer);
    }
}

