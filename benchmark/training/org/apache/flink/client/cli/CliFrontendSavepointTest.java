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
package org.apache.flink.client.cli;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.zip.ZipOutputStream;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.cli.util.MockedCliFrontend;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.Preconditions;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for the SAVEPOINT command.
 */
public class CliFrontendSavepointTest extends CliFrontendTestBase {
    private static PrintStream stdOut;

    private static PrintStream stdErr;

    private static ByteArrayOutputStream buffer;

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    // ------------------------------------------------------------------------
    // Trigger savepoint
    // ------------------------------------------------------------------------
    @Test
    public void testTriggerSavepointSuccess() throws Exception {
        CliFrontendSavepointTest.replaceStdOutAndStdErr();
        JobID jobId = new JobID();
        String savepointPath = "expectedSavepointPath";
        final ClusterClient<String> clusterClient = CliFrontendSavepointTest.createClusterClient(savepointPath);
        try {
            MockedCliFrontend frontend = new MockedCliFrontend(clusterClient);
            String[] parameters = new String[]{ jobId.toString() };
            savepoint(parameters);
            Mockito.verify(clusterClient, Mockito.times(1)).triggerSavepoint(ArgumentMatchers.eq(jobId), ArgumentMatchers.isNull(String.class));
            Assert.assertTrue(CliFrontendSavepointTest.buffer.toString().contains(savepointPath));
        } finally {
            clusterClient.shutdown();
            CliFrontendSavepointTest.restoreStdOutAndStdErr();
        }
    }

    @Test
    public void testTriggerSavepointFailure() throws Exception {
        CliFrontendSavepointTest.replaceStdOutAndStdErr();
        JobID jobId = new JobID();
        String expectedTestException = "expectedTestException";
        Exception testException = new Exception(expectedTestException);
        final ClusterClient<String> clusterClient = CliFrontendSavepointTest.createFailingClusterClient(testException);
        try {
            MockedCliFrontend frontend = new MockedCliFrontend(clusterClient);
            String[] parameters = new String[]{ jobId.toString() };
            try {
                savepoint(parameters);
                Assert.fail("Savepoint should have failed.");
            } catch (FlinkException e) {
                Assert.assertTrue(ExceptionUtils.findThrowableWithMessage(e, expectedTestException).isPresent());
            }
        } finally {
            clusterClient.shutdown();
            CliFrontendSavepointTest.restoreStdOutAndStdErr();
        }
    }

    @Test
    public void testTriggerSavepointFailureIllegalJobID() throws Exception {
        CliFrontendSavepointTest.replaceStdOutAndStdErr();
        try {
            CliFrontend frontend = new MockedCliFrontend(new RestClusterClient(getConfiguration(), StandaloneClusterId.getInstance()));
            String[] parameters = new String[]{ "invalid job id" };
            try {
                frontend.savepoint(parameters);
                Assert.fail("Should have failed.");
            } catch (CliArgsException e) {
                Assert.assertThat(e.getMessage(), Matchers.containsString("Cannot parse JobID"));
            }
        } finally {
            CliFrontendSavepointTest.restoreStdOutAndStdErr();
        }
    }

    /**
     * Tests that a CLI call with a custom savepoint directory target is
     * forwarded correctly to the cluster client.
     */
    @Test
    public void testTriggerSavepointCustomTarget() throws Exception {
        CliFrontendSavepointTest.replaceStdOutAndStdErr();
        JobID jobId = new JobID();
        String savepointDirectory = "customTargetDirectory";
        final ClusterClient<String> clusterClient = CliFrontendSavepointTest.createClusterClient(savepointDirectory);
        try {
            MockedCliFrontend frontend = new MockedCliFrontend(clusterClient);
            String[] parameters = new String[]{ jobId.toString(), savepointDirectory };
            savepoint(parameters);
            Mockito.verify(clusterClient, Mockito.times(1)).triggerSavepoint(ArgumentMatchers.eq(jobId), ArgumentMatchers.eq(savepointDirectory));
            Assert.assertTrue(CliFrontendSavepointTest.buffer.toString().contains(savepointDirectory));
        } finally {
            clusterClient.shutdown();
            CliFrontendSavepointTest.restoreStdOutAndStdErr();
        }
    }

    // ------------------------------------------------------------------------
    // Dispose savepoint
    // ------------------------------------------------------------------------
    @Test
    public void testDisposeSavepointSuccess() throws Exception {
        CliFrontendSavepointTest.replaceStdOutAndStdErr();
        String savepointPath = "expectedSavepointPath";
        ClusterClient clusterClient = new CliFrontendSavepointTest.DisposeSavepointClusterClient((String path) -> CompletableFuture.completedFuture(Acknowledge.get()), getConfiguration());
        try {
            CliFrontend frontend = new MockedCliFrontend(clusterClient);
            String[] parameters = new String[]{ "-d", savepointPath };
            frontend.savepoint(parameters);
            String outMsg = CliFrontendSavepointTest.buffer.toString();
            Assert.assertTrue(outMsg.contains(savepointPath));
            Assert.assertTrue(outMsg.contains("disposed"));
        } finally {
            clusterClient.shutdown();
            CliFrontendSavepointTest.restoreStdOutAndStdErr();
        }
    }

    /**
     * Tests disposal with a JAR file.
     */
    @Test
    public void testDisposeWithJar() throws Exception {
        CliFrontendSavepointTest.replaceStdOutAndStdErr();
        final CompletableFuture<String> disposeSavepointFuture = new CompletableFuture<>();
        final CliFrontendSavepointTest.DisposeSavepointClusterClient clusterClient = new CliFrontendSavepointTest.DisposeSavepointClusterClient((String savepointPath) -> {
            disposeSavepointFuture.complete(savepointPath);
            return CompletableFuture.completedFuture(Acknowledge.get());
        }, getConfiguration());
        try {
            CliFrontend frontend = new MockedCliFrontend(clusterClient);
            // Fake JAR file
            File f = tmp.newFile();
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
            out.close();
            final String disposePath = "any-path";
            String[] parameters = new String[]{ "-d", disposePath, "-j", f.getAbsolutePath() };
            frontend.savepoint(parameters);
            final String actualSavepointPath = disposeSavepointFuture.get();
            Assert.assertEquals(disposePath, actualSavepointPath);
        } finally {
            shutdown();
            CliFrontendSavepointTest.restoreStdOutAndStdErr();
        }
    }

    @Test
    public void testDisposeSavepointFailure() throws Exception {
        CliFrontendSavepointTest.replaceStdOutAndStdErr();
        String savepointPath = "expectedSavepointPath";
        Exception testException = new Exception("expectedTestException");
        CliFrontendSavepointTest.DisposeSavepointClusterClient clusterClient = new CliFrontendSavepointTest.DisposeSavepointClusterClient((String path) -> FutureUtils.completedExceptionally(testException), getConfiguration());
        try {
            CliFrontend frontend = new MockedCliFrontend(clusterClient);
            String[] parameters = new String[]{ "-d", savepointPath };
            try {
                frontend.savepoint(parameters);
                Assert.fail("Savepoint should have failed.");
            } catch (Exception e) {
                Assert.assertTrue(ExceptionUtils.findThrowableWithMessage(e, testException.getMessage()).isPresent());
            }
        } finally {
            shutdown();
            CliFrontendSavepointTest.restoreStdOutAndStdErr();
        }
    }

    // ------------------------------------------------------------------------
    private static final class DisposeSavepointClusterClient extends RestClusterClient<StandaloneClusterId> {
        private final Function<String, CompletableFuture<Acknowledge>> disposeSavepointFunction;

        DisposeSavepointClusterClient(Function<String, CompletableFuture<Acknowledge>> disposeSavepointFunction, Configuration configuration) throws Exception {
            super(configuration, StandaloneClusterId.getInstance());
            this.disposeSavepointFunction = Preconditions.checkNotNull(disposeSavepointFunction);
        }

        @Override
        public CompletableFuture<Acknowledge> disposeSavepoint(String savepointPath) {
            return disposeSavepointFunction.apply(savepointPath);
        }
    }
}

