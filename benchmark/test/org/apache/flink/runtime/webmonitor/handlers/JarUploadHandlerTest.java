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
import JarUploadResponseBody.UploadStatus.success;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import org.apache.flink.runtime.dispatcher.DispatcherGateway;
import org.apache.flink.runtime.rest.handler.HandlerRequest;
import org.apache.flink.runtime.rest.handler.RestHandlerException;
import org.apache.flink.runtime.rest.messages.EmptyMessageParameters;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;


/**
 * Tests for {@link JarUploadHandler}.
 */
public class JarUploadHandlerTest extends TestLogger {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private JarUploadHandler jarUploadHandler;

    @Mock
    private DispatcherGateway mockDispatcherGateway;

    private Path jarDir;

    @Test
    public void testRejectNonJarFiles() throws Exception {
        final Path uploadedFile = Files.createFile(jarDir.resolve("katrin.png"));
        final HandlerRequest<EmptyRequestBody, EmptyMessageParameters> request = JarUploadHandlerTest.createRequest(uploadedFile);
        try {
            jarUploadHandler.handleRequest(request, mockDispatcherGateway).get();
            Assert.fail("Expected exception not thrown.");
        } catch (final ExecutionException e) {
            final Throwable throwable = ExceptionUtils.stripCompletionException(e.getCause());
            Assert.assertThat(throwable, Matchers.instanceOf(RestHandlerException.class));
            final RestHandlerException restHandlerException = ((RestHandlerException) (throwable));
            Assert.assertThat(restHandlerException.getHttpResponseStatus(), Matchers.equalTo(BAD_REQUEST));
        }
    }

    @Test
    public void testUploadJar() throws Exception {
        final Path uploadedFile = Files.createFile(jarDir.resolve("Kafka010Example.jar"));
        final HandlerRequest<EmptyRequestBody, EmptyMessageParameters> request = JarUploadHandlerTest.createRequest(uploadedFile);
        final JarUploadResponseBody jarUploadResponseBody = jarUploadHandler.handleRequest(request, mockDispatcherGateway).get();
        Assert.assertThat(jarUploadResponseBody.getStatus(), Matchers.equalTo(success));
        final String returnedFileNameWithUUID = jarUploadResponseBody.getFilename();
        Assert.assertThat(returnedFileNameWithUUID, Matchers.containsString("_"));
        final String returnedFileName = returnedFileNameWithUUID.substring(((returnedFileNameWithUUID.lastIndexOf("_")) + 1));
        Assert.assertThat(returnedFileName, Matchers.equalTo(uploadedFile.getFileName().toString()));
    }

    @Test
    public void testFailedUpload() throws Exception {
        final Path uploadedFile = jarDir.resolve("Kafka010Example.jar");
        final HandlerRequest<EmptyRequestBody, EmptyMessageParameters> request = JarUploadHandlerTest.createRequest(uploadedFile);
        try {
            jarUploadHandler.handleRequest(request, mockDispatcherGateway).get();
            Assert.fail("Expected exception not thrown.");
        } catch (final ExecutionException e) {
            final Throwable throwable = ExceptionUtils.stripCompletionException(e.getCause());
            Assert.assertThat(throwable, Matchers.instanceOf(RestHandlerException.class));
            final RestHandlerException restHandlerException = ((RestHandlerException) (throwable));
            Assert.assertThat(restHandlerException.getMessage(), Matchers.containsString("Could not move uploaded jar file"));
            Assert.assertThat(restHandlerException.getHttpResponseStatus(), Matchers.equalTo(INTERNAL_SERVER_ERROR));
        }
    }
}

