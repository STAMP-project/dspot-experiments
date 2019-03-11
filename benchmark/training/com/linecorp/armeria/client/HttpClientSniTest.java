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
package com.linecorp.armeria.client;


import HttpHeaderNames.AUTHORITY;
import HttpMethod.GET;
import HttpStatus.OK;
import MediaType.PLAIN_TEXT_UTF_8;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.util.SafeCloseable;
import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.VirtualHostBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.security.cert.X509Certificate;
import org.junit.Assert;
import org.junit.Test;


public class HttpClientSniTest {
    private static final Server server;

    private static int httpsPort;

    private static ClientFactory clientFactory;

    private static final SelfSignedCertificate sscA;

    private static final SelfSignedCertificate sscB;

    static {
        final ServerBuilder sb = new ServerBuilder();
        try {
            sscA = new SelfSignedCertificate("a.com");
            sscB = new SelfSignedCertificate("b.com");
            final VirtualHostBuilder a = new VirtualHostBuilder("a.com");
            final VirtualHostBuilder b = new VirtualHostBuilder("b.com");
            a.service("/", new HttpClientSniTest.SniTestService("a.com"));
            b.service("/", new HttpClientSniTest.SniTestService("b.com"));
            a.tls(HttpClientSniTest.sscA.certificate(), HttpClientSniTest.sscA.privateKey());
            b.tls(HttpClientSniTest.sscB.certificate(), HttpClientSniTest.sscB.privateKey());
            sb.virtualHost(a.build());
            sb.defaultVirtualHost(b.build());
        } catch (Exception e) {
            throw new Error(e);
        }
        server = sb.build();
    }

    @Test
    public void testMatch() throws Exception {
        HttpClientSniTest.testMatch("a.com");
        HttpClientSniTest.testMatch("b.com");
    }

    @Test
    public void testMismatch() throws Exception {
        HttpClientSniTest.testMismatch("127.0.0.1");
        HttpClientSniTest.testMismatch("mismatch.com");
    }

    @Test
    public void testCustomAuthority() throws Exception {
        final HttpClient client = HttpClient.of(HttpClientSniTest.clientFactory, ("https://127.0.0.1:" + (HttpClientSniTest.httpsPort)));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(GET, "/").set(AUTHORITY, ("a.com:" + (HttpClientSniTest.httpsPort)))).aggregate().get();
        Assert.assertEquals(OK, response.status());
        Assert.assertEquals("a.com: CN=a.com", response.contentUtf8());
    }

    @Test
    public void testCustomAuthorityWithAdditionalHeaders() throws Exception {
        final HttpClient client = HttpClient.of(HttpClientSniTest.clientFactory, ("https://127.0.0.1:" + (HttpClientSniTest.httpsPort)));
        try (SafeCloseable unused = Clients.withHttpHeader(AUTHORITY, ("a.com:" + (HttpClientSniTest.httpsPort)))) {
            final AggregatedHttpMessage response = client.get("/").aggregate().get();
            Assert.assertEquals(OK, response.status());
            Assert.assertEquals("a.com: CN=a.com", response.contentUtf8());
        }
    }

    private static class SniTestService extends AbstractHttpService {
        private final String domainName;

        SniTestService(String domainName) {
            this.domainName = domainName;
        }

        @Override
        protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
            final X509Certificate c = ((X509Certificate) (ctx.sslSession().getLocalCertificates()[0]));
            final String name = c.getSubjectX500Principal().getName();
            return HttpResponse.of(OK, PLAIN_TEXT_UTF_8, (((domainName) + ": ") + name));
        }
    }
}

