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
import HttpResponseStatus.INTERNAL_SERVER_ERROR;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import org.apache.flink.runtime.rest.handler.HandlerRequest;
import org.apache.flink.runtime.rest.handler.RestHandlerException;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.runtime.webmonitor.RestfulGateway;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link JarDeleteHandler}.
 */
public class JarDeleteHandlerTest extends TestLogger {
    private static final String TEST_JAR_NAME = "test.jar";

    private JarDeleteHandler jarDeleteHandler;

    private RestfulGateway restfulGateway;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path jarDir;

    @Test
    public void testDeleteJarById() throws Exception {
        Assert.assertThat(Files.exists(jarDir.resolve(JarDeleteHandlerTest.TEST_JAR_NAME)), Matchers.equalTo(true));
        final HandlerRequest<EmptyRequestBody, JarDeleteMessageParameters> request = JarDeleteHandlerTest.createRequest(JarDeleteHandlerTest.TEST_JAR_NAME);
        jarDeleteHandler.handleRequest(request, restfulGateway).get();
        Assert.assertThat(Files.exists(jarDir.resolve(JarDeleteHandlerTest.TEST_JAR_NAME)), Matchers.equalTo(false));
    }

    @Test
    public void testDeleteUnknownJar() throws Exception {
        final HandlerRequest<EmptyRequestBody, JarDeleteMessageParameters> request = JarDeleteHandlerTest.createRequest("doesnotexist.jar");
        try {
            jarDeleteHandler.handleRequest(request, restfulGateway).get();
        } catch (final ExecutionException e) {
            final Throwable throwable = ExceptionUtils.stripCompletionException(e.getCause());
            Assert.assertThat(throwable, Matchers.instanceOf(RestHandlerException.class));
            final RestHandlerException restHandlerException = ((RestHandlerException) (throwable));
            Assert.assertThat(restHandlerException.getMessage(), Matchers.containsString("File doesnotexist.jar does not exist in"));
            Assert.assertThat(restHandlerException.getHttpResponseStatus(), Matchers.equalTo(BAD_REQUEST));
        }
    }

    @Test
    public void testFailedDelete() throws Exception {
        makeJarDirReadOnly();
        final HandlerRequest<EmptyRequestBody, JarDeleteMessageParameters> request = JarDeleteHandlerTest.createRequest(JarDeleteHandlerTest.TEST_JAR_NAME);
        try {
            jarDeleteHandler.handleRequest(request, restfulGateway).get();
        } catch (final ExecutionException e) {
            final Throwable throwable = ExceptionUtils.stripCompletionException(e.getCause());
            Assert.assertThat(throwable, Matchers.instanceOf(RestHandlerException.class));
            final RestHandlerException restHandlerException = ((RestHandlerException) (throwable));
            Assert.assertThat(restHandlerException.getMessage(), Matchers.containsString("Failed to delete jar"));
            Assert.assertThat(restHandlerException.getHttpResponseStatus(), Matchers.equalTo(INTERNAL_SERVER_ERROR));
        }
    }
}

