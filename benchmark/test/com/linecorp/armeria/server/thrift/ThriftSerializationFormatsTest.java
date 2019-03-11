/**
 * Copyright 2015 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.thrift;


import ClientOption.HTTP_HEADERS;
import HelloService.Iface;
import HttpHeaderNames.ACCEPT;
import HttpHeaderNames.CONTENT_TYPE;
import com.linecorp.armeria.client.Clients;
import com.linecorp.armeria.client.InvalidResponseException;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.common.SerializationFormat;
import com.linecorp.armeria.common.thrift.ThriftSerializationFormats;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.service.test.thrift.main.HelloService;
import com.linecorp.armeria.testing.server.ServerRule;
import java.nio.charset.StandardCharsets;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Test of serialization format validation / detection based on HTTP headers.
 */
public class ThriftSerializationFormatsTest {
    private static final Iface HELLO_SERVICE = ( name) -> ("Hello, " + name) + '!';

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.service("/hello", THttpService.of(ThriftSerializationFormatsTest.HELLO_SERVICE)).service("/hellobinaryonly", THttpService.ofFormats(ThriftSerializationFormatsTest.HELLO_SERVICE, ThriftSerializationFormats.BINARY)).service("/hellotextonly", THttpService.ofFormats(ThriftSerializationFormatsTest.HELLO_SERVICE, ThriftSerializationFormats.TEXT));
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void findByMediaType() {
        // The 'protocol' parameter has to be case-insensitive.
        assertThat(SerializationFormat.find(MediaType.parse("application/x-thrift; protocol=tbinary"))).containsSame(ThriftSerializationFormats.BINARY);
        assertThat(SerializationFormat.find(MediaType.parse("application/x-thrift;protocol=TCompact"))).containsSame(ThriftSerializationFormats.COMPACT);
        assertThat(SerializationFormat.find(MediaType.parse("application/x-thrift ; protocol=\"TjSoN\""))).containsSame(ThriftSerializationFormats.JSON);
        // An unknown parameter ('version' in this case) should not be accepted.
        assertThat(SerializationFormat.find(MediaType.parse("application/x-thrift ; version=3;protocol=ttext"))).isEmpty();
        // 'charset=utf-8' parameter should be accepted for TJSON and TTEXT.
        assertThat(SerializationFormat.find(MediaType.parse("application/x-thrift; protocol=tjson; charset=utf-8"))).containsSame(ThriftSerializationFormats.JSON);
        assertThat(SerializationFormat.find(MediaType.parse("application/vnd.apache.thrift.json; charset=utf-8"))).containsSame(ThriftSerializationFormats.JSON);
        assertThat(SerializationFormat.find(MediaType.parse("application/x-thrift; protocol=ttext; charset=utf-8"))).containsSame(ThriftSerializationFormats.TEXT);
        assertThat(SerializationFormat.find(MediaType.parse("application/vnd.apache.thrift.text; charset=utf-8"))).containsSame(ThriftSerializationFormats.TEXT);
        // .. but neither non-UTF-8 charsets:
        assertThat(SerializationFormat.find(MediaType.parse("application/x-thrift; protocol=tjson; charset=us-ascii"))).isEmpty();
        assertThat(SerializationFormat.find(MediaType.parse("application/vnd.apache.thrift.json; charset=us-ascii"))).isEmpty();
        assertThat(SerializationFormat.find(MediaType.parse("application/x-thrift; protocol=ttext; charset=us-ascii"))).isEmpty();
        assertThat(SerializationFormat.find(MediaType.parse("application/vnd.apache.thrift.text; charset=us-ascii"))).isEmpty();
        // .. nor binary/compact formats:
        assertThat(SerializationFormat.find(MediaType.parse("application/x-thrift; protocol=tbinary; charset=utf-8"))).isEmpty();
        assertThat(SerializationFormat.find(MediaType.parse("application/vnd.apache.thrift.binary; charset=utf-8"))).isEmpty();
        assertThat(SerializationFormat.find(MediaType.parse("application/x-thrift; protocol=tcompact; charset=utf-8"))).isEmpty();
        assertThat(SerializationFormat.find(MediaType.parse("application/vnd.apache.thrift.compact; charset=utf-8"))).isEmpty();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void backwardCompatibility() {
        assertThat(SerializationFormat.ofThrift()).containsExactlyInAnyOrder(ThriftSerializationFormats.BINARY, ThriftSerializationFormats.COMPACT, ThriftSerializationFormats.JSON, ThriftSerializationFormats.TEXT);
        assertThat(SerializationFormat.THRIFT_BINARY).isNotNull();
        assertThat(SerializationFormat.THRIFT_COMPACT).isNotNull();
        assertThat(SerializationFormat.THRIFT_JSON).isNotNull();
        assertThat(SerializationFormat.THRIFT_TEXT).isNotNull();
    }

    @Test
    public void defaults() throws Exception {
        final HelloService.Iface client = Clients.newClient(ThriftSerializationFormatsTest.server.uri(ThriftSerializationFormats.BINARY, "/hello"), Iface.class);
        assertThat(client.hello("Trustin")).isEqualTo("Hello, Trustin!");
    }

    @Test
    public void notDefault() throws Exception {
        final HelloService.Iface client = Clients.newClient(ThriftSerializationFormatsTest.server.uri(ThriftSerializationFormats.TEXT, "/hello"), Iface.class);
        assertThat(client.hello("Trustin")).isEqualTo("Hello, Trustin!");
    }

    @Test
    public void notAllowed() throws Exception {
        final HelloService.Iface client = Clients.newClient(ThriftSerializationFormatsTest.server.uri(ThriftSerializationFormats.TEXT, "/hellobinaryonly"), Iface.class);
        thrown.expect(InvalidResponseException.class);
        thrown.expectMessage("415 Unsupported Media Type");
        client.hello("Trustin");
    }

    @Test
    public void contentTypeNotThrift() throws Exception {
        // Browser clients often send a non-thrift content type.
        final HttpHeaders headers = HttpHeaders.of(CONTENT_TYPE, "text/plain; charset=utf-8");
        final HelloService.Iface client = Clients.newClient(ThriftSerializationFormatsTest.server.uri(ThriftSerializationFormats.BINARY, "/hello"), Iface.class, HTTP_HEADERS.newValue(headers));
        assertThat(client.hello("Trustin")).isEqualTo("Hello, Trustin!");
    }

    @Test
    public void acceptNotSameAsContentType() throws Exception {
        final HttpHeaders headers = HttpHeaders.of(ACCEPT, "application/x-thrift; protocol=TBINARY");
        final HelloService.Iface client = Clients.newClient(ThriftSerializationFormatsTest.server.uri(ThriftSerializationFormats.TEXT, "/hello"), Iface.class, HTTP_HEADERS.newValue(headers));
        thrown.expect(InvalidResponseException.class);
        thrown.expectMessage("406 Not Acceptable");
        client.hello("Trustin");
    }

    @Test
    public void defaultSerializationFormat() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            // Send a TTEXT request with content type 'application/x-thrift' without 'protocol' parameter.
            final HttpPost req = new HttpPost(ThriftSerializationFormatsTest.server.uri("/hellotextonly"));
            req.setHeader("Content-type", "application/x-thrift");
            req.setEntity(new StringEntity((((('{' + "  \"method\": \"hello\",") + "  \"type\":\"CALL\",") + "  \"args\": { \"name\": \"trustin\"}") + '}'), StandardCharsets.UTF_8));
            try (CloseableHttpResponse res = hc.execute(req)) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
            }
        }
    }
}

