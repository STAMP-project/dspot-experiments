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


import TemporaryJobReports.ReportWriter;
import com.google.common.io.Resources;
import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.descriptors.Job;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class JobWithConfigTest {
    @Mock
    private static HeliosClient client;

    @Mock
    private static Deployer deployer;

    @Test
    public void test() throws Exception {
        Assert.assertThat(PrintableResult.testResult(JobWithConfigTest.JobWithConfigTestImpl.class), ResultMatchers.isSuccessful());
    }

    public static class JobWithConfigTestImpl {
        // Local is the default profile, so don't specify it explicitly to test default loading
        @Rule
        public final TemporaryJobs temporaryJobs = TemporaryJobs.builder().client(JobWithConfigTest.client).deployer(JobWithConfigTest.deployer).build();

        @Test
        public void testJobWithConfig() throws Exception {
            final String configFile = Resources.getResource("helios_job_config.json").getPath();
            final TemporaryJobBuilder builder = temporaryJobs.jobWithConfig(configFile).port("https", 443);
            builder.deploy("test-host");
            final ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
            Mockito.verify(JobWithConfigTest.deployer).deploy(captor.capture(), ArgumentMatchers.anyListOf(String.class), ArgumentMatchers.anySetOf(String.class), ArgumentMatchers.any(Prober.class), ArgumentMatchers.any(ReportWriter.class));
            TestCase.assertEquals(80, captor.getValue().getPorts().get("http").getInternalPort());
            TestCase.assertEquals(443, captor.getValue().getPorts().get("https").getInternalPort());
        }

        @Test(expected = IllegalArgumentException.class)
        public void testJobWithBadConfig() throws Exception {
            temporaryJobs.jobWithConfig("/dev/null/doesnt_exist.json");
        }
    }
}

