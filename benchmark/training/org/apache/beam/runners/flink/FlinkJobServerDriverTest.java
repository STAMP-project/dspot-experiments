/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.flink;


import Charsets.UTF_8;
import FlinkJobServerDriver.FlinkServerConfiguration;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for {@link FlinkJobServerDriver}.
 */
public class FlinkJobServerDriverTest {
    private static final Logger LOG = LoggerFactory.getLogger(FlinkJobServerDriverTest.class);

    @Test
    public void testConfigurationDefaults() {
        FlinkJobServerDriver.FlinkServerConfiguration config = new FlinkJobServerDriver.FlinkServerConfiguration();
        MatcherAssert.assertThat(config.getHost(), Is.is("localhost"));
        MatcherAssert.assertThat(config.getPort(), Is.is(8099));
        MatcherAssert.assertThat(config.getArtifactPort(), Is.is(8098));
        MatcherAssert.assertThat(config.getExpansionPort(), Is.is(8097));
        MatcherAssert.assertThat(config.getFlinkMasterUrl(), Is.is("[auto]"));
        MatcherAssert.assertThat(config.getSdkWorkerParallelism(), Is.is(1L));
        MatcherAssert.assertThat(config.isCleanArtifactsPerJob(), Is.is(false));
        FlinkJobServerDriver flinkJobServerDriver = FlinkJobServerDriver.fromConfig(config);
        MatcherAssert.assertThat(flinkJobServerDriver, Is.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }

    @Test
    public void testConfigurationFromArgs() {
        FlinkJobServerDriver driver = FlinkJobServerDriver.fromParams(new String[]{ "--job-host=test", "--job-port", "42", "--artifact-port", "43", "--expansion-port", "44", "--flink-master-url=jobmanager", "--sdk-worker-parallelism=4", "--clean-artifacts-per-job" });
        FlinkJobServerDriver.FlinkServerConfiguration config = ((FlinkJobServerDriver.FlinkServerConfiguration) (driver.configuration));
        MatcherAssert.assertThat(config.getHost(), Is.is("test"));
        MatcherAssert.assertThat(config.getPort(), Is.is(42));
        MatcherAssert.assertThat(config.getArtifactPort(), Is.is(43));
        MatcherAssert.assertThat(config.getExpansionPort(), Is.is(44));
        MatcherAssert.assertThat(config.getFlinkMasterUrl(), Is.is("jobmanager"));
        MatcherAssert.assertThat(config.getSdkWorkerParallelism(), Is.is(4L));
        MatcherAssert.assertThat(config.isCleanArtifactsPerJob(), Is.is(true));
    }

    @Test
    public void testConfigurationFromConfig() {
        FlinkJobServerDriver.FlinkServerConfiguration config = new FlinkJobServerDriver.FlinkServerConfiguration();
        FlinkJobServerDriver driver = FlinkJobServerDriver.fromConfig(config);
        MatcherAssert.assertThat(driver.configuration, Is.is(config));
    }

    @Test(timeout = 30000)
    public void testJobServerDriver() throws Exception {
        FlinkJobServerDriver driver = null;
        Thread driverThread = null;
        final PrintStream oldErr = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newErr = new PrintStream(baos);
        try {
            System.setErr(newErr);
            driver = FlinkJobServerDriver.fromParams(new String[]{ "--job-port=0", "--artifact-port=0", "--expansion-port=0" });
            driverThread = new Thread(driver);
            driverThread.start();
            boolean success = false;
            while (!success) {
                newErr.flush();
                String output = baos.toString(UTF_8.name());
                if (((output.contains("JobService started on localhost:")) && (output.contains("ArtifactStagingService started on localhost:"))) && (output.contains("ExpansionService started on localhost:"))) {
                    success = true;
                } else {
                    Thread.sleep(100);
                }
            } 
            MatcherAssert.assertThat(driverThread.isAlive(), Is.is(true));
        } catch (Throwable t) {
            // restore to print exception
            System.setErr(oldErr);
            throw t;
        } finally {
            System.setErr(oldErr);
            if (driver != null) {
                driver.stop();
            }
            if (driverThread != null) {
                driverThread.interrupt();
                driverThread.join();
            }
        }
    }
}

