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


import HeliosClient.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.descriptors.Job;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {
    private static ConfigTest.TestParameters parameters;

    @Mock
    private static HeliosClient client;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private static Builder clientBuilder;

    public static class ProfileTest implements Deployer {
        // Local is the default profile, so don't specify it explicitly to test default loading
        @Rule
        public final TemporaryJobs temporaryJobs = ConfigTest.parameters.builder.client(ConfigTest.client).deployer(this).build();

        @Before
        public void setup() {
            // This job will get deployed test-host
            temporaryJobs.job().deploy("test-host");
            // this job will get deployed using the host filter in the conf file
            temporaryJobs.job().deploy();
        }

        @Test
        public void test() throws Exception {
            // Dummy test so junit doesn't complain.
        }

        @Override
        public TemporaryJob deploy(Job job, List<String> hosts, Set<String> waitPorts, Prober prober, TemporaryJobReports.ReportWriter reportWriter) {
            // This is called when the first job is deployed
            Assert.assertThat(hosts, Matchers.equalTo(((List<String>) (Lists.newArrayList("test-host")))));
            ConfigTest.parameters.validate(job, temporaryJobs.prefix());
            return null;
        }

        @Override
        public TemporaryJob deploy(Job job, String hostFilter, Set<String> waitPorts, Prober prober, TemporaryJobReports.ReportWriter reportWriter) {
            // This is called when the second job is deployed
            Assert.assertThat(hostFilter, Matchers.equalTo(ConfigTest.parameters.hostFilter));
            ConfigTest.parameters.validate(job, temporaryJobs.prefix());
            return null;
        }

        @Override
        public void readyToDeploy() {
        }
    }

    @Test
    public void testLocalProfile() throws Exception {
        final ConfigTest.TestParameters.JobValidator validator = new ConfigTest.TestParameters.JobValidator() {
            @Override
            public void validate(Job job, String prefix) {
                final String local = prefix + ".local.";
                final Map<String, String> map = ImmutableMap.of("SPOTIFY_TEST_THING", String.format("See, we used the prefix here -->%s<--", prefix), "SPOTIFY_DOMAIN", local, "SPOTIFY_POD", local);
                Assert.assertThat(job.getEnv(), Matchers.equalTo(map));
                Assert.assertThat(job.getImage(), Matchers.equalTo("spotify/busybox:latest"));
            }
        };
        // The local profile is the default, so we don't specify it explicitly so we can test
        // the default loading mechanism.
        ConfigTest.parameters = new ConfigTest.TestParameters(TemporaryJobsTestBase.temporaryJobsBuilder(), ".*", validator);
        Assert.assertThat(PrintableResult.testResult(ConfigTest.ProfileTest.class), ResultMatchers.isSuccessful());
    }

    @Test
    public void testHeliosCiProfile() throws Exception {
        final ConfigTest.TestParameters.JobValidator validator = new ConfigTest.TestParameters.JobValidator() {
            @Override
            public void validate(Job job, String prefix) {
                final String domain = prefix + ".services.helios-ci.cloud.spotify.net";
                final Map<String, String> map = ImmutableMap.of("SPOTIFY_DOMAIN", domain, "SPOTIFY_POD", domain, "SPOTIFY_SYSLOG_HOST", "10.99.0.1");
                Assert.assertThat(job.getEnv(), Matchers.equalTo(map));
                Assert.assertThat(job.getImage(), Matchers.equalTo("spotify/busybox:latest"));
            }
        };
        // Specify the helios-ci profile explicitly, but make sure that the construction of the
        // HeliosClient used by TemporaryJobs is mocked out to avoid attempting to connect to
        // possibly-unresolvable hosts.
        Mockito.doReturn(ConfigTest.client).when(ConfigTest.clientBuilder).build();
        final TemporaryJobs.Builder builder = TemporaryJobs.builder("helios-ci", Collections.<String, String>emptyMap(), ConfigTest.clientBuilder);
        ConfigTest.parameters = new ConfigTest.TestParameters(builder, ".+\\.helios-ci\\.cloud", validator);
        Assert.assertThat(PrintableResult.testResult(ConfigTest.ProfileTest.class), ResultMatchers.isSuccessful());
    }

    /**
     * Helper class allows us to inject different test parameters into ProfileTest so we can
     * reuse it to test multiple profiles.
     */
    private static class TestParameters {
        private final TemporaryJobs.Builder builder;

        private final String hostFilter;

        private final ConfigTest.TestParameters.JobValidator jobValidator;

        private TestParameters(TemporaryJobs.Builder builder, String hostFilter, ConfigTest.TestParameters.JobValidator jobValidator) {
            this.builder = builder;
            this.hostFilter = hostFilter;
            this.jobValidator = jobValidator;
        }

        private void validate(final Job job, final String prefix) {
            jobValidator.validate(job, prefix);
        }

        private interface JobValidator {
            void validate(Job job, String prefix);
        }
    }
}

