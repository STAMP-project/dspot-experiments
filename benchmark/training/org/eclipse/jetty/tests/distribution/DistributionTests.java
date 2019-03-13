/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.tests.distribution;


import DistributionTester.Run;
import HttpStatus.NOT_FOUND_404;
import HttpStatus.OK_200;
import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;


public class DistributionTests extends AbstractDistributionTest {
    @Test
    public void testStartStop() throws Exception {
        String jettyVersion = System.getProperty("jettyVersion");
        DistributionTester distribution = DistributionTester.Builder.newInstance().jettyVersion(jettyVersion).mavenLocalRepository(System.getProperty("mavenRepoPath")).build();
        try (DistributionTester.Run run1 = distribution.start("--add-to-start=http")) {
            Assertions.assertTrue(run1.awaitFor(5, TimeUnit.SECONDS));
            Assertions.assertEquals(0, run1.getExitValue());
            int port = distribution.freePort();
            try (DistributionTester.Run run2 = distribution.start(("jetty.http.port=" + port))) {
                Assertions.assertTrue(run2.awaitConsoleLogsFor("Started @", 10, TimeUnit.SECONDS));
                startHttpClient();
                ContentResponse response = client.GET(("http://localhost:" + port));
                Assertions.assertEquals(NOT_FOUND_404, response.getStatus());
                run2.stop();
                Assertions.assertTrue(run2.awaitFor(5, TimeUnit.SECONDS));
            }
        }
    }

    @Test
    public void testSimpleWebAppWithJSP() throws Exception {
        String jettyVersion = System.getProperty("jettyVersion");
        DistributionTester distribution = DistributionTester.Builder.newInstance().jettyVersion(jettyVersion).mavenLocalRepository(System.getProperty("mavenRepoPath")).build();
        String[] args1 = new String[]{ "--create-startd", "--approve-all-licenses", "--add-to-start=resources,server,http,webapp,deploy,jsp,jmx,servlet,servlets" };
        try (DistributionTester.Run run1 = distribution.start(args1)) {
            Assertions.assertTrue(run1.awaitFor(5, TimeUnit.SECONDS));
            Assertions.assertEquals(0, run1.getExitValue());
            File war = distribution.resolveArtifact(("org.eclipse.jetty.tests:test-simple-webapp:war:" + jettyVersion));
            distribution.installWarFile(war, "test");
            int port = distribution.freePort();
            try (DistributionTester.Run run2 = distribution.start(("jetty.http.port=" + port))) {
                Assertions.assertTrue(run2.awaitConsoleLogsFor("Started @", 10, TimeUnit.SECONDS));
                startHttpClient();
                ContentResponse response = client.GET((("http://localhost:" + port) + "/test/index.jsp"));
                Assertions.assertEquals(OK_200, response.getStatus());
                MatcherAssert.assertThat(response.getContentAsString(), Matchers.containsString("Hello"));
                MatcherAssert.assertThat(response.getContentAsString(), Matchers.not(Matchers.containsString("<%")));
            }
        }
    }

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testSimpleWebAppWithJSPOnModulePath() throws Exception {
        String jettyVersion = System.getProperty("jettyVersion");
        DistributionTester distribution = DistributionTester.Builder.newInstance().jettyVersion(jettyVersion).mavenLocalRepository(System.getProperty("mavenRepoPath")).build();
        String[] args1 = new String[]{ "--create-startd", "--approve-all-licenses", "--add-to-start=resources,server,http,webapp,deploy,jsp,jmx,servlet,servlets" };
        try (DistributionTester.Run run1 = distribution.start(args1)) {
            Assertions.assertTrue(run1.awaitFor(5, TimeUnit.SECONDS));
            Assertions.assertEquals(0, run1.getExitValue());
            File war = distribution.resolveArtifact(("org.eclipse.jetty.tests:test-simple-webapp:war:" + jettyVersion));
            distribution.installWarFile(war, "test");
            int port = distribution.freePort();
            String[] args2 = new String[]{ "--jpms", "jetty.http.port=" + port };
            try (DistributionTester.Run run2 = distribution.start(args2)) {
                Assertions.assertTrue(run2.awaitConsoleLogsFor("Started @", 10, TimeUnit.SECONDS));
                startHttpClient();
                ContentResponse response = client.GET((("http://localhost:" + port) + "/test/index.jsp"));
                Assertions.assertEquals(OK_200, response.getStatus());
                MatcherAssert.assertThat(response.getContentAsString(), Matchers.containsString("Hello"));
                MatcherAssert.assertThat(response.getContentAsString(), Matchers.not(Matchers.containsString("<%")));
            }
        }
    }

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testSimpleWebAppWithJSPOverH2C() throws Exception {
        String jettyVersion = System.getProperty("jettyVersion");
        DistributionTester distribution = DistributionTester.Builder.newInstance().jettyVersion(jettyVersion).mavenLocalRepository(System.getProperty("mavenRepoPath")).build();
        String[] args1 = new String[]{ "--create-startd", "--add-to-start=http2c,jsp,deploy" };
        try (DistributionTester.Run run1 = distribution.start(args1)) {
            Assertions.assertTrue(run1.awaitFor(5, TimeUnit.SECONDS));
            Assertions.assertEquals(0, run1.getExitValue());
            File war = distribution.resolveArtifact(("org.eclipse.jetty.tests:test-simple-webapp:war:" + jettyVersion));
            distribution.installWarFile(war, "test");
            int port = distribution.freePort();
            try (DistributionTester.Run run2 = distribution.start(("jetty.http.port=" + port))) {
                Assertions.assertTrue(run2.awaitConsoleLogsFor("Started @", 10, TimeUnit.SECONDS));
                HTTP2Client h2Client = new HTTP2Client();
                startHttpClient(() -> new HttpClient(new org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2(h2Client), null));
                ContentResponse response = client.GET((("http://localhost:" + port) + "/test/index.jsp"));
                Assertions.assertEquals(OK_200, response.getStatus());
                MatcherAssert.assertThat(response.getContentAsString(), Matchers.containsString("Hello"));
                MatcherAssert.assertThat(response.getContentAsString(), Matchers.not(Matchers.containsString("<%")));
            }
        }
    }

    @Test
    public void testDemoBase() throws Exception {
        String jettyVersion = System.getProperty("jettyVersion");
        DistributionTester distribution = DistributionTester.Builder.newInstance().jettyVersion(jettyVersion).jettyBase(Paths.get("demo-base")).mavenLocalRepository(System.getProperty("mavenRepoPath")).build();
        int port = distribution.freePort();
        try (DistributionTester.Run run1 = distribution.start(("jetty.http.port=" + port))) {
            Assertions.assertTrue(run1.awaitConsoleLogsFor("Started @", 20, TimeUnit.SECONDS));
            startHttpClient();
            ContentResponse response = client.GET((("http://localhost:" + port) + "/test/jsp/dump.jsp"));
            Assertions.assertEquals(OK_200, response.getStatus());
            MatcherAssert.assertThat(response.getContentAsString(), Matchers.containsString("PathInfo"));
            MatcherAssert.assertThat(response.getContentAsString(), Matchers.not(Matchers.containsString("<%")));
        }
    }
}

