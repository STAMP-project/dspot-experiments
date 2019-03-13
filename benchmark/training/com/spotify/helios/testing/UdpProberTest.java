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


import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;


/**
 * Tests that a UDP port in a TemporaryJob can be probed.
 */
public class UdpProberTest extends TemporaryJobsTestBase {
    @Test
    public void test() throws Exception {
        Assert.assertThat(PrintableResult.testResult(UdpProberTest.UdpProberTestImpl.class), ResultMatchers.isSuccessful());
    }

    public static class UdpProberTestImpl {
        private TemporaryJob job;

        @Rule
        public final TemporaryJobs temporaryJobs = TemporaryJobsTestBase.temporaryJobsBuilder().client(TemporaryJobsTestBase.client).prober(new TemporaryJobsTestBase.TestProber()).build();

        @Before
        public void setup() {
            job = temporaryJobs.job().image(ALPINE).command(Arrays.asList("nc", "-p", "4711", "-lu")).port("default", 4711, "udp").deploy(TemporaryJobsTestBase.testHost1);
        }

        @After
        public void tearDown() {
            // The TemporaryJobs Rule above doesn't undeploy the job for some reason...
            job.undeploy();
        }

        @Test
        public void test() throws Exception {
            final Map<JobId, Job> jobs = TemporaryJobsTestBase.client.jobs().get(15, TimeUnit.SECONDS);
            Assert.assertEquals("wrong number of jobs running", 1, jobs.size());
            for (final Job job : jobs.values()) {
                Assert.assertEquals("wrong job running", ALPINE, job.getImage());
            }
        }
    }
}

