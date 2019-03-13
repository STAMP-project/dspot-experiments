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
package org.apache.flink.runtime.webmonitor.handlers;


import JarListInfo.JarFileInfo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.util.BlobServerResource;
import org.apache.flink.runtime.webmonitor.TestingDispatcherGateway;
import org.apache.flink.runtime.webmonitor.retriever.GatewayRetriever;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests the entire lifecycle of a jar submission.
 */
public class JarSubmissionITCase extends TestLogger {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final BlobServerResource blobServerResource = new BlobServerResource();

    @Test
    public void testJarSubmission() throws Exception {
        final TestingDispatcherGateway restfulGateway = new TestingDispatcherGateway.Builder().setBlobServerPort(blobServerResource.getBlobServerPort()).setSubmitFunction(( jobGraph) -> CompletableFuture.completedFuture(Acknowledge.get())).build();
        final JarSubmissionITCase.JarHandlers handlers = new JarSubmissionITCase.JarHandlers(temporaryFolder.newFolder().toPath(), restfulGateway);
        final JarUploadHandler uploadHandler = handlers.uploadHandler;
        final JarListHandler listHandler = handlers.listHandler;
        final JarPlanHandler planHandler = handlers.planHandler;
        final JarRunHandler runHandler = handlers.runHandler;
        final JarDeleteHandler deleteHandler = handlers.deleteHandler;
        // targetDir property is set via surefire configuration
        final Path originalJar = Paths.get(System.getProperty("targetDir")).resolve("test-program.jar");
        final Path jar = Files.copy(originalJar, temporaryFolder.getRoot().toPath().resolve("test-program.jar"));
        final String storedJarPath = JarSubmissionITCase.uploadJar(uploadHandler, jar, restfulGateway);
        final String storedJarName = Paths.get(storedJarPath).getFileName().toString();
        final JarListInfo postUploadListResponse = JarSubmissionITCase.listJars(listHandler, restfulGateway);
        Assert.assertEquals(1, postUploadListResponse.jarFileList.size());
        final JarListInfo.JarFileInfo listEntry = postUploadListResponse.jarFileList.iterator().next();
        Assert.assertEquals(jar.getFileName().toString(), listEntry.name);
        Assert.assertEquals(storedJarName, listEntry.id);
        final JobPlanInfo planResponse = JarSubmissionITCase.showPlan(planHandler, storedJarName, restfulGateway);
        // we're only interested in the core functionality so checking for a small detail is sufficient
        Assert.assertThat(planResponse.getJsonPlan(), Matchers.containsString("TestProgram.java:29"));
        JarSubmissionITCase.runJar(runHandler, storedJarName, restfulGateway);
        JarSubmissionITCase.deleteJar(deleteHandler, storedJarName, restfulGateway);
        final JarListInfo postDeleteListResponse = JarSubmissionITCase.listJars(listHandler, restfulGateway);
        Assert.assertEquals(0, postDeleteListResponse.jarFileList.size());
    }

    private static class JarHandlers {
        final JarUploadHandler uploadHandler;

        final JarListHandler listHandler;

        final JarPlanHandler planHandler;

        final JarRunHandler runHandler;

        final JarDeleteHandler deleteHandler;

        JarHandlers(final Path jarDir, final TestingDispatcherGateway restfulGateway) {
            final GatewayRetriever<TestingDispatcherGateway> gatewayRetriever = () -> CompletableFuture.completedFuture(restfulGateway);
            final Time timeout = Time.seconds(10);
            final Map<String, String> responseHeaders = Collections.emptyMap();
            final Executor executor = TestingUtils.defaultExecutor();
            uploadHandler = new JarUploadHandler(gatewayRetriever, timeout, responseHeaders, JarUploadHeaders.getInstance(), jarDir, executor);
            listHandler = new JarListHandler(gatewayRetriever, timeout, responseHeaders, JarListHeaders.getInstance(), CompletableFuture.completedFuture("shazam://localhost:12345"), jarDir.toFile(), executor);
            planHandler = new JarPlanHandler(gatewayRetriever, timeout, responseHeaders, JarPlanHeaders.getInstance(), jarDir, new Configuration(), executor);
            runHandler = new JarRunHandler(gatewayRetriever, timeout, responseHeaders, JarRunHeaders.getInstance(), jarDir, new Configuration(), executor);
            deleteHandler = new JarDeleteHandler(gatewayRetriever, timeout, responseHeaders, JarDeleteHeaders.getInstance(), jarDir, executor);
        }
    }
}

