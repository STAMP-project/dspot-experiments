/**
 * -
 * -\-\-
 * Helios Testing Library
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.helios.testing;


import com.google.common.base.Optional;
import com.spotify.helios.common.descriptors.HealthCheck;
import com.spotify.helios.common.descriptors.HttpHealthCheck;
import com.spotify.helios.common.descriptors.TcpHealthCheck;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;


public class HealthCheckTest extends TemporaryJobsTestBase {
    private static final String HEALTH_CHECK_PORT = "healthCheck";

    private static final String QUERY_PORT = "query";

    @Test
    public void test() throws Exception {
        Assert.assertThat(PrintableResult.testResult(HealthCheckTest.TestImpl.class), ResultMatchers.isSuccessful());
    }

    public static class TestImpl {
        @Rule
        public final TemporaryJobs temporaryJobs = TemporaryJobsTestBase.temporaryJobsBuilder().client(TemporaryJobsTestBase.client).jobPrefix(Optional.of(TemporaryJobsTestBase.testTag).get()).deployTimeoutMillis(TimeUnit.MINUTES.toMillis(3)).build();

        @Test
        public void testTcpCheck() throws Exception {
            // running netcat twice on different ports lets us verify the health check actually executed
            // because otherwise we wouldn't be able to connect to the second port.
            final TemporaryJob job = temporaryJobs.job().image(ALPINE).command("sh", "-c", "nc -l -p 4711 && nc -kl -p 4712 -e true").port(HealthCheckTest.HEALTH_CHECK_PORT, 4711).port(HealthCheckTest.QUERY_PORT, 4712).tcpHealthCheck(HealthCheckTest.HEALTH_CHECK_PORT).deploy(TemporaryJobsTestBase.testHost1);
            // verify health check was set correctly in job
            Assert.assertThat(job.job().getHealthCheck(), Matchers.equalTo(((HealthCheck) (TcpHealthCheck.of(HealthCheckTest.HEALTH_CHECK_PORT)))));
            // verify we can actually connect to the port
            // noinspection EmptyTryBlock
            try (final Socket ignored = new Socket(DOCKER_HOST.address(), job.address(HealthCheckTest.QUERY_PORT).getPort())) {
                // ignored
            }
        }

        @Test
        public void testHttpCheck() throws Exception {
            // Start an HTTP server that listens on ports 4711 and 4712.
            final TemporaryJob job = temporaryJobs.job().image(UHTTPD).command("-p", "4711", "-p", "4712").port(HealthCheckTest.HEALTH_CHECK_PORT, 4711).port(HealthCheckTest.QUERY_PORT, 4712).httpHealthCheck(HealthCheckTest.HEALTH_CHECK_PORT, "/").deploy(TemporaryJobsTestBase.testHost1);
            // verify health check was set correctly in job
            Assert.assertThat(job.job().getHealthCheck(), Matchers.equalTo(((HealthCheck) (HttpHealthCheck.of(HealthCheckTest.HEALTH_CHECK_PORT, "/")))));
            // verify we can actually make http requests
            final URL url = new URL("http", DOCKER_HOST.address(), job.address(HealthCheckTest.QUERY_PORT).getPort(), "/");
            final HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            Assert.assertThat(connection.getResponseCode(), Matchers.equalTo(200));
        }

        @Test
        public void testHealthCheck() throws Exception {
            // same as the tcp test above, but uses a HealthCheck
            // object instead of the tcpHealthCheck convenience method
            final HealthCheck healthCheck = TcpHealthCheck.of(HealthCheckTest.HEALTH_CHECK_PORT);
            final TemporaryJob job = temporaryJobs.job().image(ALPINE).command("sh", "-c", "nc -l -p 4711 && nc -kl -p 4712 -e true").port(HealthCheckTest.HEALTH_CHECK_PORT, 4711).port(HealthCheckTest.QUERY_PORT, 4712).healthCheck(healthCheck).deploy(TemporaryJobsTestBase.testHost1);
            // verify health check was set correctly in job
            Assert.assertThat(job.job().getHealthCheck(), Matchers.equalTo(healthCheck));
            // verify we can actually connect to the port
            // noinspection EmptyTryBlock
            try (final Socket ignored = new Socket(DOCKER_HOST.address(), job.address(HealthCheckTest.QUERY_PORT).getPort())) {
                // ignored
            }
        }
    }
}

