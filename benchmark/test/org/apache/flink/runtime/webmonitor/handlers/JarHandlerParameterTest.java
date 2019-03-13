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


import HttpResponseStatus.BAD_REQUEST;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.TestCase;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.rest.handler.HandlerRequest;
import org.apache.flink.runtime.rest.handler.RestHandlerException;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.util.BlobServerResource;
import org.apache.flink.runtime.webmonitor.TestingDispatcherGateway;
import org.apache.flink.runtime.webmonitor.retriever.GatewayRetriever;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Base test class for jar request handlers.
 */
public abstract class JarHandlerParameterTest<REQB extends JarRequestBody, M extends JarMessageParameters> extends TestLogger {
    enum ProgramArgsParType {

        String,
        List,
        Both;}

    static final String[] PROG_ARGS = new String[]{ "--host", "localhost", "--port", "1234" };

    static final int PARALLELISM = 4;

    @ClassRule
    public static final TemporaryFolder TMP = new TemporaryFolder();

    @ClassRule
    public static final BlobServerResource BLOB_SERVER_RESOURCE = new BlobServerResource();

    static final AtomicReference<JobGraph> LAST_SUBMITTED_JOB_GRAPH_REFERENCE = new AtomicReference<>();

    static TestingDispatcherGateway restfulGateway;

    static Path jarDir;

    static GatewayRetriever<TestingDispatcherGateway> gatewayRetriever = () -> CompletableFuture.completedFuture(JarHandlerParameterTest.restfulGateway);

    static CompletableFuture<String> localAddressFuture = CompletableFuture.completedFuture("shazam://localhost:12345");

    static Time timeout = Time.seconds(10);

    static Map<String, String> responseHeaders = Collections.emptyMap();

    static Executor executor = TestingUtils.defaultExecutor();

    private static Path jarWithManifest;

    private static Path jarWithoutManifest;

    @Test
    public void testDefaultParameters() throws Exception {
        // baseline, ensure that reasonable defaults are chosen
        handleRequest(JarHandlerParameterTest.createRequest(getDefaultJarRequestBody(), getUnresolvedJarMessageParameters(), getUnresolvedJarMessageParameters(), JarHandlerParameterTest.jarWithManifest));
        validateDefaultGraph();
    }

    @Test
    public void testConfigurationViaQueryParametersWithProgArgsAsString() throws Exception {
        testConfigurationViaQueryParameters(JarHandlerParameterTest.ProgramArgsParType.String);
    }

    @Test
    public void testConfigurationViaQueryParametersWithProgArgsAsList() throws Exception {
        testConfigurationViaQueryParameters(JarHandlerParameterTest.ProgramArgsParType.List);
    }

    @Test
    public void testConfigurationViaQueryParametersFailWithProgArgsAsStringAndList() throws Exception {
        try {
            testConfigurationViaQueryParameters(JarHandlerParameterTest.ProgramArgsParType.Both);
            TestCase.fail("RestHandlerException is excepted");
        } catch (RestHandlerException e) {
            TestCase.assertEquals(BAD_REQUEST, e.getHttpResponseStatus());
        }
    }

    @Test
    public void testConfigurationViaJsonRequestWithProgArgsAsString() throws Exception {
        testConfigurationViaJsonRequest(JarHandlerParameterTest.ProgramArgsParType.String);
    }

    @Test
    public void testConfigurationViaJsonRequestWithProgArgsAsList() throws Exception {
        testConfigurationViaJsonRequest(JarHandlerParameterTest.ProgramArgsParType.List);
    }

    @Test
    public void testConfigurationViaJsonRequestFailWithProgArgsAsStringAndList() throws Exception {
        try {
            testConfigurationViaJsonRequest(JarHandlerParameterTest.ProgramArgsParType.Both);
            TestCase.fail("RestHandlerException is excepted");
        } catch (RestHandlerException e) {
            TestCase.assertEquals(BAD_REQUEST, e.getHttpResponseStatus());
        }
    }

    @Test
    public void testProvideJobId() throws Exception {
        JobID jobId = new JobID();
        HandlerRequest<REQB, M> request = JarHandlerParameterTest.createRequest(getJarRequestBodyWithJobId(jobId), getUnresolvedJarMessageParameters(), getUnresolvedJarMessageParameters(), JarHandlerParameterTest.jarWithManifest);
        handleRequest(request);
        Optional<JobGraph> jobGraph = JarHandlerParameterTest.getLastSubmittedJobGraphAndReset();
        MatcherAssert.assertThat(jobGraph.isPresent(), CoreMatchers.is(true));
        MatcherAssert.assertThat(jobGraph.get().getJobID(), CoreMatchers.is(CoreMatchers.equalTo(jobId)));
    }

    @Test
    public void testParameterPrioritizationWithProgArgsAsString() throws Exception {
        testParameterPrioritization(JarHandlerParameterTest.ProgramArgsParType.String);
    }

    @Test
    public void testParameterPrioritizationWithProgArgsAsList() throws Exception {
        testParameterPrioritization(JarHandlerParameterTest.ProgramArgsParType.List);
    }

    @Test
    public void testFailIfProgArgsAreAsStringAndAsList() throws Exception {
        try {
            testParameterPrioritization(JarHandlerParameterTest.ProgramArgsParType.Both);
            TestCase.fail("RestHandlerException is excepted");
        } catch (RestHandlerException e) {
            TestCase.assertEquals(BAD_REQUEST, e.getHttpResponseStatus());
        }
    }
}

