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
package com.linecorp.armeria.server.tomcat;


import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.testing.internal.webapp.WebAppContainerTest;
import com.linecorp.armeria.testing.server.ServerRule;
import java.io.File;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.ClassRule;
import org.junit.Test;


public class UnmanagedTomcatServiceTest {
    private static Tomcat tomcatWithWebApp;

    private static Tomcat tomcatWithoutWebApp;

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            // Prepare Tomcat instances.
            UnmanagedTomcatServiceTest.tomcatWithWebApp = new Tomcat();
            UnmanagedTomcatServiceTest.tomcatWithWebApp.setPort(0);
            UnmanagedTomcatServiceTest.tomcatWithWebApp.setBaseDir((((("build" + (File.separatorChar)) + "tomcat-") + (UnmanagedTomcatServiceTest.class.getSimpleName())) + "-1"));
            UnmanagedTomcatServiceTest.tomcatWithWebApp.addWebapp("", WebAppContainerTest.webAppRoot().getAbsolutePath());
            TomcatUtil.engine(UnmanagedTomcatServiceTest.tomcatWithWebApp.getService(), "foo").setName("tomcatWithWebApp");
            UnmanagedTomcatServiceTest.tomcatWithoutWebApp = new Tomcat();
            UnmanagedTomcatServiceTest.tomcatWithoutWebApp.setPort(0);
            UnmanagedTomcatServiceTest.tomcatWithoutWebApp.setBaseDir((((("build" + (File.separatorChar)) + "tomcat-") + (UnmanagedTomcatServiceTest.class.getSimpleName())) + "-2"));
            assertThat(TomcatUtil.engine(UnmanagedTomcatServiceTest.tomcatWithoutWebApp.getService(), "bar")).isNotNull();
            // Start the Tomcats.
            UnmanagedTomcatServiceTest.tomcatWithWebApp.start();
            UnmanagedTomcatServiceTest.tomcatWithoutWebApp.start();
            // Bind them to the Server.
            sb.serviceUnder("/empty/", TomcatService.forConnector("someHost", new Connector())).serviceUnder("/some-webapp-nohostname/", TomcatService.forConnector(UnmanagedTomcatServiceTest.tomcatWithWebApp.getConnector())).serviceUnder("/no-webapp/", TomcatService.forTomcat(UnmanagedTomcatServiceTest.tomcatWithoutWebApp)).serviceUnder("/some-webapp/", TomcatService.forTomcat(UnmanagedTomcatServiceTest.tomcatWithWebApp));
        }
    };

    @Test
    public void serviceUnavailable() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            try (CloseableHttpResponse res = hc.execute(new HttpGet(UnmanagedTomcatServiceTest.server.uri("/empty/")))) {
                // as connector is not configured, TomcatServiceInvocationHandler will throw.
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 503 Service Unavailable");
            }
        }
    }

    @Test
    public void unconfiguredWebApp() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            try (CloseableHttpResponse res = hc.execute(new HttpGet(UnmanagedTomcatServiceTest.server.uri("/no-webapp/")))) {
                // When no webapp is configured, Tomcat sends:
                // - 400 Bad Request response for 9.0.10+
                // - 404 Not Found for other versions
                final String statusLine = res.getStatusLine().toString();
                assertThat(statusLine).matches("^HTTP/1\\.1 (400 Bad Request|404 Not Found)$");
            }
        }
    }

    @Test
    public void ok() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            try (CloseableHttpResponse res = hc.execute(new HttpGet(UnmanagedTomcatServiceTest.server.uri("/some-webapp/")))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
            }
        }
    }

    @Test
    public void okNoHostName() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            try (CloseableHttpResponse res = hc.execute(new HttpGet(UnmanagedTomcatServiceTest.server.uri("/some-webapp-nohostname/")))) {
                assertThat(res.getStatusLine().toString()).isEqualTo("HTTP/1.1 200 OK");
            }
        }
    }
}

