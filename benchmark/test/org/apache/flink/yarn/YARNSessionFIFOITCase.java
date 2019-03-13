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
package org.apache.flink.yarn;


import SecurityOptions.KERBEROS_LOGIN_KEYTAB;
import SecurityOptions.KERBEROS_LOGIN_PRINCIPAL;
import WordCountData.TEXT;
import YarnApplicationState.FINISHED;
import YarnApplicationState.KILLED;
import YarnApplicationState.RUNNING;
import YarnConfigOptions.PROPERTIES_FILE_LOCATION;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.test.util.SecureTestEnvironment;
import org.apache.flink.yarn.cli.FlinkYarnSessionCli;
import org.apache.flink.yarn.util.YarnTestUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.flink.yarn.YarnTestBase.RunTypes.CLI_FRONTEND;
import static org.apache.flink.yarn.YarnTestBase.RunTypes.YARN_SESSION;


/**
 * This test starts a MiniYARNCluster with a FIFO scheduler.
 * There are no queues for that scheduler.
 */
public class YARNSessionFIFOITCase extends YarnTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(YARNSessionFIFOITCase.class);

    /**
     * Test regular operation, including command line parameter parsing.
     */
    // timeout after a minute.
    @Test(timeout = 60000)
    public void testDetachedMode() throws IOException, InterruptedException {
        YARNSessionFIFOITCase.LOG.info("Starting testDetachedMode()");
        UtilsTest.addTestAppender(FlinkYarnSessionCli.class, Level.INFO);
        File exampleJarLocation = YarnTestUtils.getTestJarPath("StreamingWordCount.jar");
        // get temporary file for reading input data for wordcount example
        File tmpInFile = YarnTestBase.tmp.newFile();
        FileUtils.writeStringToFile(tmpInFile, TEXT);
        ArrayList<String> args = new ArrayList<>();
        args.add("-j");
        args.add(YarnTestBase.flinkUberjar.getAbsolutePath());
        args.add("-t");
        args.add(YarnTestBase.flinkLibFolder.getAbsolutePath());
        args.add("-t");
        args.add(YarnTestBase.flinkShadedHadoopDir.getAbsolutePath());
        args.add("-n");
        args.add("1");
        args.add("-jm");
        args.add("768m");
        args.add("-tm");
        args.add("1024m");
        if ((SecureTestEnvironment.getTestKeytab()) != null) {
            args.add(((("-D" + (KERBEROS_LOGIN_KEYTAB.key())) + "=") + (SecureTestEnvironment.getTestKeytab())));
        }
        if ((SecureTestEnvironment.getHadoopServicePrincipal()) != null) {
            args.add(((("-D" + (KERBEROS_LOGIN_PRINCIPAL.key())) + "=") + (SecureTestEnvironment.getHadoopServicePrincipal())));
        }
        args.add("--name");
        args.add("MyCustomName");
        args.add("--detached");
        YarnTestBase.Runner clusterRunner = startWithArgs(args.toArray(new String[args.size()]), "Flink JobManager is now running on", YARN_SESSION);
        // before checking any strings outputted by the CLI, first give it time to return
        clusterRunner.join();
        // actually run a program, otherwise we wouldn't necessarily see any TaskManagers
        // be brought up
        YarnTestBase.Runner jobRunner = startWithArgs(new String[]{ "run", "--detached", exampleJarLocation.getAbsolutePath(), "--input", tmpInFile.getAbsoluteFile().toString() }, "Job has been submitted with JobID", CLI_FRONTEND);
        jobRunner.join();
        // in "new" mode we can only wait after the job is submitted, because TMs
        // are spun up lazily
        YARNSessionFIFOITCase.LOG.info("Waiting until two containers are running");
        // wait until two containers are running
        while ((YarnTestBase.getRunningContainers()) < 2) {
            YarnTestBase.sleep(500);
        } 
        // make sure we have two TMs running in either mode
        long startTime = System.nanoTime();
        while ((((System.nanoTime()) - startTime) < (TimeUnit.NANOSECONDS.convert(10, TimeUnit.SECONDS))) && (!(YarnTestBase.verifyStringsInNamedLogFiles(new String[]{ "switched from state RUNNING to FINISHED" }, "jobmanager.log")))) {
            YARNSessionFIFOITCase.LOG.info("Still waiting for cluster to finish job...");
            YarnTestBase.sleep(500);
        } 
        YARNSessionFIFOITCase.LOG.info("Two containers are running. Killing the application");
        // kill application "externally".
        try {
            YarnClient yc = YarnClient.createYarnClient();
            yc.init(YarnTestBase.YARN_CONFIGURATION);
            yc.start();
            List<ApplicationReport> apps = yc.getApplications(EnumSet.of(RUNNING));
            Assert.assertEquals(1, apps.size());// Only one running

            ApplicationReport app = apps.get(0);
            Assert.assertEquals("MyCustomName", app.getName());
            ApplicationId id = app.getApplicationId();
            yc.killApplication(id);
            while (((yc.getApplications(EnumSet.of(KILLED)).size()) == 0) && ((yc.getApplications(EnumSet.of(FINISHED)).size()) == 0)) {
                YarnTestBase.sleep(500);
            } 
        } catch (Throwable t) {
            YARNSessionFIFOITCase.LOG.warn("Killing failed", t);
            Assert.fail();
        } finally {
            // cleanup the yarn-properties file
            String confDirPath = System.getenv("FLINK_CONF_DIR");
            File configDirectory = new File(confDirPath);
            YARNSessionFIFOITCase.LOG.info(("testDetachedPerJobYarnClusterInternal: Using configuration directory " + (configDirectory.getAbsolutePath())));
            // load the configuration
            YARNSessionFIFOITCase.LOG.info("testDetachedPerJobYarnClusterInternal: Trying to load configuration file");
            Configuration configuration = GlobalConfiguration.loadConfiguration(configDirectory.getAbsolutePath());
            try {
                File yarnPropertiesFile = FlinkYarnSessionCli.getYarnPropertiesLocation(configuration.getString(PROPERTIES_FILE_LOCATION));
                if (yarnPropertiesFile.exists()) {
                    YARNSessionFIFOITCase.LOG.info("testDetachedPerJobYarnClusterInternal: Cleaning up temporary Yarn address reference: {}", yarnPropertiesFile.getAbsolutePath());
                    yarnPropertiesFile.delete();
                }
            } catch (Exception e) {
                YARNSessionFIFOITCase.LOG.warn("testDetachedPerJobYarnClusterInternal: Exception while deleting the JobManager address file", e);
            }
        }
        YARNSessionFIFOITCase.LOG.info("Finished testDetachedMode()");
    }

    /**
     * Test querying the YARN cluster.
     *
     * <p>This test validates through 666*2 cores in the "cluster".
     */
    @Test
    public void testQueryCluster() throws IOException {
        YARNSessionFIFOITCase.LOG.info("Starting testQueryCluster()");
        runWithArgs(new String[]{ "-q" }, "Summary: totalMemory 8192 totalCores 1332", null, YARN_SESSION, 0);// we have 666*2 cores.

        YARNSessionFIFOITCase.LOG.info("Finished testQueryCluster()");
    }
}

