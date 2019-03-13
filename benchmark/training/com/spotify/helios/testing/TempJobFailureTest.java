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


import Jobs.TIMEOUT_MILLIS;
import com.google.common.base.Optional;
import com.spotify.helios.common.Json;
import com.spotify.helios.testing.descriptors.TemporaryJobEvent;
import java.io.File;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.rules.TemporaryFolder;

import static Jobs.TIMEOUT_MILLIS;


public class TempJobFailureTest extends TemporaryJobsTestBase {
    @ClassRule
    public static final TemporaryFolder REPORT_DIR = new TemporaryFolder();

    @Test
    public void testDeploymentFailure() throws Exception {
        final long start = System.currentTimeMillis();
        Assert.assertThat(PrintableResult.testResult(TempJobFailureTest.TempJobFailureTestImpl.class), ResultMatchers.hasSingleFailureContaining("AssertionError: Unexpected job state"));
        final long end = System.currentTimeMillis();
        Assert.assertTrue("Test should not time out", ((end - start) < (TIMEOUT_MILLIS)));
        final byte[] testReport = Files.readAllBytes(TempJobFailureTest.REPORT_DIR.getRoot().listFiles()[0].toPath());
        final TemporaryJobEvent[] events = Json.read(testReport, TemporaryJobEvent[].class);
        for (final TemporaryJobEvent event : events) {
            if (event.getStep().equals("test")) {
                Assert.assertFalse("test should be reported as failed", event.isSuccess());
            }
        }
    }

    public static class TempJobFailureTestImpl {
        @Rule
        public final TemporaryJobs temporaryJobs = TemporaryJobsTestBase.temporaryJobsBuilder().hostFilter(".*").client(TemporaryJobsTestBase.client).prober(new TemporaryJobsTestBase.TestProber()).testReportDirectory(TempJobFailureTest.REPORT_DIR.getRoot().getAbsolutePath()).prefixDirectory(TemporaryJobsTestBase.prefixDirectory.toString()).jobPrefix(Optional.of(TemporaryJobsTestBase.testTag).get()).build();

        @Test
        public void testThatThisFailsQuickly() throws InterruptedException {
            temporaryJobs.job().image(BUSYBOX).command("false").deploy(TemporaryJobsTestBase.testHost1);
            Thread.sleep(TIMEOUT_MILLIS);
        }
    }
}

