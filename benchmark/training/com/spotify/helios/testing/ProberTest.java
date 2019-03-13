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
import com.spotify.helios.common.descriptors.PortMapping;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;


public class ProberTest extends TemporaryJobsTestBase {
    @Test
    public void testOverrideDefaultProber() throws Exception {
        Assert.assertThat(PrintableResult.testResult(ProberTest.OverrideDefaultProberTest.class), ResultMatchers.isSuccessful());
    }

    private static class MockProber implements Prober {
        private boolean probed;

        @Override
        public boolean probe(String host, PortMapping port) {
            return probed = true;
        }

        public boolean probed() {
            return probed;
        }
    }

    public static class OverrideDefaultProberTest {
        private ProberTest.MockProber defaultProber = new ProberTest.MockProber();

        private ProberTest.MockProber overrideProber = new ProberTest.MockProber();

        @Rule
        public final TemporaryJobs temporaryJobs = TemporaryJobsTestBase.temporaryJobsBuilder().client(TemporaryJobsTestBase.client).prober(defaultProber).jobPrefix(Optional.of(TemporaryJobsTestBase.testTag).get()).build();

        @Before
        public void setup() {
            temporaryJobs.job().command(IDLE_COMMAND).port("default", 4711).deploy(TemporaryJobsTestBase.testHost1);
            temporaryJobs.job().command(IDLE_COMMAND).port("override", 4712).prober(overrideProber).deploy(TemporaryJobsTestBase.testHost1);
        }

        @Test
        public void test() {
            // Verify that the first job used the prober passed to the TemporaryJobs rule.
            Assert.assertThat(defaultProber.probed(), Matchers.is(true));
            // Verify that the second job used the prober that was passed to its builder.
            Assert.assertThat(overrideProber.probed(), Matchers.is(true));
        }
    }
}

