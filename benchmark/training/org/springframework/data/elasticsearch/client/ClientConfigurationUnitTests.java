/**
 * Copyright 2018-2019 the original author or authors.
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
package org.springframework.data.elasticsearch.client;


import java.net.InetSocketAddress;
import java.time.Duration;
import javax.net.ssl.SSLContext;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;


/**
 * Unit tests for {@link ClientConfiguration}.
 *
 * @author Mark Paluch
 */
public class ClientConfigurationUnitTests {
    // DATAES-488
    @Test
    public void shouldCreateSimpleConfiguration() {
        ClientConfiguration clientConfiguration = ClientConfiguration.create("localhost:9200");
        assertThat(clientConfiguration.getEndpoints()).containsOnly(InetSocketAddress.createUnresolved("localhost", 9200));
    }

    // DATAES-488, DATAES-504
    @Test
    public void shouldCreateCustomizedConfiguration() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("foo", "bar");
        ClientConfiguration clientConfiguration = // 
        // 
        // 
        // 
        ClientConfiguration.builder().connectedTo("foo", "bar").usingSsl().withDefaultHeaders(headers).withConnectTimeout(Duration.ofDays(1)).withSocketTimeout(Duration.ofDays(2)).build();
        assertThat(clientConfiguration.getEndpoints()).containsOnly(InetSocketAddress.createUnresolved("foo", 9200), InetSocketAddress.createUnresolved("bar", 9200));
        assertThat(clientConfiguration.useSsl()).isTrue();
        assertThat(clientConfiguration.getDefaultHeaders().get("foo")).containsOnly("bar");
        assertThat(clientConfiguration.getConnectTimeout()).isEqualTo(Duration.ofDays(1));
        assertThat(clientConfiguration.getSocketTimeout()).isEqualTo(Duration.ofDays(2));
    }

    // DATAES-488, DATAES-504
    @Test
    public void shouldCreateSslConfiguration() {
        SSLContext sslContext = Mockito.mock(SSLContext.class);
        ClientConfiguration clientConfiguration = // 
        // 
        // 
        ClientConfiguration.builder().connectedTo("foo", "bar").usingSsl(sslContext).build();
        assertThat(clientConfiguration.getEndpoints()).containsOnly(InetSocketAddress.createUnresolved("foo", 9200), InetSocketAddress.createUnresolved("bar", 9200));
        assertThat(clientConfiguration.useSsl()).isTrue();
        assertThat(clientConfiguration.getSslContext()).contains(sslContext);
        assertThat(clientConfiguration.getConnectTimeout()).isEqualTo(Duration.ofSeconds(10));
        assertThat(clientConfiguration.getSocketTimeout()).isEqualTo(Duration.ofSeconds(5));
    }
}

