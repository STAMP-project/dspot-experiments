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
package com.linecorp.armeria.server;


import HttpStatus.OK;
import MediaType.PLAIN_TEXT_UTF_8;
import NetUtil.LOCALHOST4;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.testing.server.SelfSignedCertificateRule;
import com.linecorp.armeria.testing.server.ServerRule;
import java.security.cert.X509Certificate;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.InMemoryDnsResolver;
import org.apache.http.util.EntityUtils;
import org.junit.ClassRule;
import org.junit.Test;


public class SniServerTest {
    @ClassRule
    public static final SelfSignedCertificateRule sscA = new SelfSignedCertificateRule("a.com");

    @ClassRule
    public static final SelfSignedCertificateRule sscB = new SelfSignedCertificateRule("b.com");

    @ClassRule
    public static final SelfSignedCertificateRule sscC = new SelfSignedCertificateRule("c.com");

    private static InMemoryDnsResolver dnsResolver;

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            SniServerTest.dnsResolver = new InMemoryDnsResolver();
            SniServerTest.dnsResolver.add("a.com", LOCALHOST4);
            SniServerTest.dnsResolver.add("b.com", LOCALHOST4);
            SniServerTest.dnsResolver.add("c.com", LOCALHOST4);
            SniServerTest.dnsResolver.add("mismatch.com", LOCALHOST4);
            SniServerTest.dnsResolver.add("127.0.0.1", LOCALHOST4);
            final VirtualHostBuilder a = new VirtualHostBuilder("a.com");
            final VirtualHostBuilder b = new VirtualHostBuilder("b.com");
            final VirtualHostBuilder c = new VirtualHostBuilder("c.com");
            a.service("/", new SniServerTest.SniTestService("a.com"));
            b.service("/", new SniServerTest.SniTestService("b.com"));
            c.service("/", new SniServerTest.SniTestService("c.com"));
            a.tls(SniServerTest.sscA.certificateFile(), SniServerTest.sscA.privateKeyFile());
            b.tls(SniServerTest.sscB.certificateFile(), SniServerTest.sscB.privateKeyFile());
            c.tls(SniServerTest.sscC.certificateFile(), SniServerTest.sscC.privateKeyFile());
            sb.virtualHost(a.build());
            sb.virtualHost(b.build());
            sb.defaultVirtualHost(c.build());
        }
    };

    @Test
    public void testSniMatch() throws Exception {
        try (CloseableHttpClient hc = newHttpClient()) {
            try (CloseableHttpResponse res = hc.execute(new HttpGet(("https://a.com:" + (SniServerTest.server.httpsPort()))))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                assertThat(EntityUtils.toString(res.getEntity())).isEqualTo("a.com: CN=a.com");
            }
            try (CloseableHttpResponse res = hc.execute(new HttpGet(("https://b.com:" + (SniServerTest.server.httpsPort()))))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                assertThat(EntityUtils.toString(res.getEntity())).isEqualTo("b.com: CN=b.com");
            }
            try (CloseableHttpResponse res = hc.execute(new HttpGet(("https://c.com:" + (SniServerTest.server.httpsPort()))))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                assertThat(EntityUtils.toString(res.getEntity())).isEqualTo("c.com: CN=c.com");
            }
        }
    }

    @Test
    public void testSniMismatch() throws Exception {
        try (CloseableHttpClient hc = newHttpClient()) {
            try (CloseableHttpResponse res = hc.execute(new HttpGet(("https://mismatch.com:" + (SniServerTest.server.httpsPort()))))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                assertThat(EntityUtils.toString(res.getEntity())).isEqualTo("c.com: CN=c.com");
            }
            try (CloseableHttpResponse res = hc.execute(new HttpGet(SniServerTest.server.httpsUri("/")))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
                assertThat(EntityUtils.toString(res.getEntity())).isEqualTo("c.com: CN=c.com");
            }
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

