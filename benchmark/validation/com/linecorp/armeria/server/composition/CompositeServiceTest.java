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
package com.linecorp.armeria.server.composition;


import HttpStatus.OK;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.metric.PrometheusMeterRegistries;
import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.PathMapping;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.testing.server.ServerRule;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class CompositeServiceTest {
    private static final CompositeServiceTest.TestService serviceA = new CompositeServiceTest.TestService("A");

    private static final CompositeServiceTest.TestService serviceB = new CompositeServiceTest.TestService("B");

    private static final CompositeServiceTest.TestService serviceC = new CompositeServiceTest.TestService("C");

    private static final CompositeServiceTest.TestService otherService = new CompositeServiceTest.TestService("X");

    private static final CompositeServiceTest.TestCompositeService composite = new CompositeServiceTest.TestCompositeService();

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.meterRegistry(PrometheusMeterRegistries.newRegistry());
            sb.serviceUnder("/qux/", CompositeServiceTest.composite);
            // Should not hit the following services
            sb.serviceUnder("/foo/", CompositeServiceTest.otherService);
            sb.serviceUnder("/bar/", CompositeServiceTest.otherService);
            sb.service(PathMapping.ofGlob("/*"), CompositeServiceTest.otherService);
        }
    };

    @Test
    public void testMapping() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            try (CloseableHttpResponse res = hc.execute(new HttpGet(CompositeServiceTest.server.uri("/qux/foo/X")))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                assertThat(EntityUtils.toString(res.getEntity())).isEqualTo("A:/qux/foo/X:/X:/X");
            }
            try (CloseableHttpResponse res = hc.execute(new HttpGet(CompositeServiceTest.server.uri("/qux/bar/Y")))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                assertThat(EntityUtils.toString(res.getEntity())).isEqualTo("B:/qux/bar/Y:/Y:/Y");
            }
            try (CloseableHttpResponse res = hc.execute(new HttpGet(CompositeServiceTest.server.uri("/qux/Z")))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                assertThat(EntityUtils.toString(res.getEntity())).isEqualTo("C:/qux/Z:/Z:/Z");
            }
            // Make sure encoded path is handled correctly.
            try (CloseableHttpResponse res = hc.execute(new HttpGet(CompositeServiceTest.server.uri("/qux/%C2%A2")))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                assertThat(EntityUtils.toString(res.getEntity())).isEqualTo("C:/qux/%C2%A2:/%C2%A2:/?");
            }
        }
    }

    @Test
    public void testNonExistentMapping() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            try (CloseableHttpResponse res = hc.execute(new HttpGet(CompositeServiceTest.server.uri("/qux/Z/T")))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 404 Not Found");
            }
        }
    }

    @Test
    public void testServiceGetters() throws Exception {
        assertThat(((Object) (serviceAt(0)))).isSameAs(CompositeServiceTest.serviceA);
        assertThat(((Object) (serviceAt(1)))).isSameAs(CompositeServiceTest.serviceB);
        assertThat(((Object) (serviceAt(2)))).isSameAs(CompositeServiceTest.serviceC);
        try {
            serviceAt((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            serviceAt(3);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    private static final class TestCompositeService extends AbstractCompositeService<HttpRequest, HttpResponse> {
        TestCompositeService() {
            super(CompositeServiceEntry.ofPrefix("/foo/", CompositeServiceTest.serviceA), CompositeServiceEntry.ofPrefix("/bar/", CompositeServiceTest.serviceB), CompositeServiceEntry.ofGlob("/*", CompositeServiceTest.serviceC));// Matches /x but doesn't match /x/y

        }
    }

    private static final class TestService extends AbstractHttpService {
        private final String name;

        TestService(String name) {
            this.name = name;
        }

        @Override
        protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
            return HttpResponse.of(OK, PLAIN_TEXT_UTF_8, "%s:%s:%s:%s", name, ctx.path(), ctx.mappedPath(), ctx.decodedMappedPath());
        }
    }
}

