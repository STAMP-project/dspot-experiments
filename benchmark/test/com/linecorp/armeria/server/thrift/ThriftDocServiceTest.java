/**
 * Copyright 2016 LINE Corporation
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


import HelloService.AsyncIface;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.SerializationFormat;
import com.linecorp.armeria.common.thrift.ThriftSerializationFormats;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.docs.DocServiceBuilder;
import com.linecorp.armeria.server.docs.EndpointInfoBuilder;
import com.linecorp.armeria.server.thrift.ThriftDocServicePlugin.Entry;
import com.linecorp.armeria.server.thrift.ThriftDocServicePlugin.EntryBuilder;
import com.linecorp.armeria.service.test.thrift.cassandra.Cassandra;
import com.linecorp.armeria.service.test.thrift.hbase.Hbase;
import com.linecorp.armeria.service.test.thrift.main.FooService;
import com.linecorp.armeria.service.test.thrift.main.HelloService;
import com.linecorp.armeria.service.test.thrift.main.HelloService.hello_args;
import com.linecorp.armeria.service.test.thrift.main.OnewayHelloService;
import com.linecorp.armeria.service.test.thrift.main.SleepService;
import com.linecorp.armeria.testing.server.ServerRule;
import java.util.List;
import java.util.Set;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;


public class ThriftDocServiceTest {
    private static final AsyncIface HELLO_SERVICE_HANDLER = ( name, resultHandler) -> resultHandler.onComplete(("Hello " + name));

    private static final SleepService.AsyncIface SLEEP_SERVICE_HANDLER = ( duration, resultHandler) -> resultHandler.onComplete(duration);

    private static final hello_args EXAMPLE_HELLO = new hello_args("sample user");

    private static final HttpHeaders EXAMPLE_HEADERS_ALL = HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("a"), "b");

    private static final HttpHeaders EXAMPLE_HEADERS_HELLO = HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("c"), "d");

    private static final HttpHeaders EXAMPLE_HEADERS_FOO = HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("e"), "f");

    private static final HttpHeaders EXAMPLE_HEADERS_FOO_BAR1 = HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("g"), "h");

    private static final ObjectMapper mapper = new ObjectMapper();

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            final THttpService helloAndSleepService = THttpService.of(ImmutableMap.of("hello", ThriftDocServiceTest.HELLO_SERVICE_HANDLER, "sleep", ThriftDocServiceTest.SLEEP_SERVICE_HANDLER));
            final THttpService fooService = THttpService.ofFormats(Mockito.mock(FooService.AsyncIface.class), ThriftSerializationFormats.COMPACT);
            final THttpService cassandraService = THttpService.ofFormats(Mockito.mock(Cassandra.AsyncIface.class), ThriftSerializationFormats.BINARY);
            final THttpService cassandraServiceDebug = THttpService.ofFormats(Mockito.mock(Cassandra.AsyncIface.class), ThriftSerializationFormats.TEXT);
            final THttpService hbaseService = THttpService.of(Mockito.mock(Hbase.AsyncIface.class));
            final THttpService onewayHelloService = THttpService.of(Mockito.mock(OnewayHelloService.AsyncIface.class));
            sb.service("/", helloAndSleepService);
            sb.service("/foo", fooService);
            // Add a service with serviceUnder() to test whether prefix mapping is detected.
            sb.serviceUnder("/foo", fooService);
            sb.service("/cassandra", cassandraService);
            sb.service("/cassandra/debug", cassandraServiceDebug);
            sb.service("/hbase", hbaseService);
            sb.service("/oneway", onewayHelloService);
            sb.serviceUnder("/docs/", new DocServiceBuilder().exampleHttpHeaders(ThriftDocServiceTest.EXAMPLE_HEADERS_ALL).exampleHttpHeaders(HelloService.class, ThriftDocServiceTest.EXAMPLE_HEADERS_HELLO).exampleHttpHeaders(FooService.class, ThriftDocServiceTest.EXAMPLE_HEADERS_FOO).exampleHttpHeaders(FooService.class, "bar1", ThriftDocServiceTest.EXAMPLE_HEADERS_FOO_BAR1).exampleRequest(ThriftDocServiceTest.EXAMPLE_HELLO).build());
        }
    };

    @Test
    public void testOk() throws Exception {
        final Set<SerializationFormat> allThriftFormats = ThriftSerializationFormats.values();
        final List<Entry> entries = ImmutableList.of(new EntryBuilder(HelloService.class).endpoint(new EndpointInfoBuilder("*", "/").fragment("hello").defaultFormat(ThriftSerializationFormats.BINARY).availableFormats(allThriftFormats).build()).build(), new EntryBuilder(SleepService.class).endpoint(new EndpointInfoBuilder("*", "/").fragment("sleep").defaultFormat(ThriftSerializationFormats.BINARY).availableFormats(allThriftFormats).build()).build(), new EntryBuilder(FooService.class).endpoint(new EndpointInfoBuilder("*", "/foo").defaultFormat(ThriftSerializationFormats.COMPACT).build()).endpoint(new EndpointInfoBuilder("*", "/foo/").defaultFormat(ThriftSerializationFormats.COMPACT).build()).build(), new EntryBuilder(Cassandra.class).endpoint(new EndpointInfoBuilder("*", "/cassandra").defaultFormat(ThriftSerializationFormats.BINARY).build()).endpoint(new EndpointInfoBuilder("*", "/cassandra/debug").defaultFormat(ThriftSerializationFormats.TEXT).build()).build(), new EntryBuilder(Hbase.class).endpoint(new EndpointInfoBuilder("*", "/hbase").defaultFormat(ThriftSerializationFormats.BINARY).availableFormats(allThriftFormats).build()).build(), new EntryBuilder(OnewayHelloService.class).endpoint(new EndpointInfoBuilder("*", "/oneway").defaultFormat(ThriftSerializationFormats.BINARY).availableFormats(allThriftFormats).build()).build());
        final JsonNode expectedJson = ThriftDocServiceTest.mapper.valueToTree(ThriftDocServicePlugin.generate(entries));
        // The specification generated by ThriftDocServicePlugin does not include the examples specified
        // when building a DocService, so we add them manually here.
        ThriftDocServiceTest.addExamples(expectedJson);
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpGet req = new HttpGet(ThriftDocServiceTest.specificationUri());
            try (CloseableHttpResponse res = hc.execute(req)) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                final JsonNode actualJson = ThriftDocServiceTest.mapper.readTree(EntityUtils.toString(res.getEntity()));
                // The specification generated by ThriftDocServicePlugin does not include the docstrings
                // because it's injected by the DocService, so we remove them here for easier comparison.
                ThriftDocServiceTest.removeDocStrings(actualJson);
                // Convert to the prettified strings for human-readable comparison.
                final ObjectWriter writer = ThriftDocServiceTest.mapper.writerWithDefaultPrettyPrinter();
                final String actualJsonString = writer.writeValueAsString(actualJson);
                final String expectedJsonString = writer.writeValueAsString(expectedJson);
                assertThat(actualJsonString).isEqualTo(expectedJsonString);
            }
        }
    }

    @Test
    public void testMethodNotAllowed() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpPost req = new HttpPost(ThriftDocServiceTest.specificationUri());
            try (CloseableHttpResponse res = hc.execute(req)) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 405 Method Not Allowed");
            }
        }
    }
}

