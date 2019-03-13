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
package org.springframework.boot.webservices.client;


import java.time.Duration;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.testsupport.runner.classpath.ClassPathExclusions;
import org.springframework.boot.testsupport.runner.classpath.ModifiedClassPathRunner;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.transport.WebServiceMessageSender;


/**
 * Tests for {@link HttpWebServiceMessageSenderBuilder} when Http Components is not
 * available.
 *
 * @author Stephane Nicoll
 */
@RunWith(ModifiedClassPathRunner.class)
@ClassPathExclusions("httpclient-*.jar")
public class HttpWebServiceMessageSenderBuilderOkHttp3IntegrationTests {
    private final HttpWebServiceMessageSenderBuilder builder = new HttpWebServiceMessageSenderBuilder();

    @Test
    public void buildUseOkHttp3ByDefault() {
        WebServiceMessageSender messageSender = this.builder.build();
        assertOkHttp3RequestFactory(messageSender);
    }

    @Test
    public void buildWithCustomTimeouts() {
        WebServiceMessageSender messageSender = this.builder.setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(2)).build();
        OkHttp3ClientHttpRequestFactory factory = assertOkHttp3RequestFactory(messageSender);
        OkHttpClient client = ((OkHttpClient) (ReflectionTestUtils.getField(factory, "client")));
        assertThat(client).isNotNull();
        assertThat(client.connectTimeoutMillis()).isEqualTo(5000);
        assertThat(client.readTimeoutMillis()).isEqualTo(2000);
    }
}

