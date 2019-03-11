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
package org.apache.flink.runtime.rest;


import HttpResponseStatus.BAD_REQUEST;
import HttpResponseStatus.INTERNAL_SERVER_ERROR;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.flink.runtime.io.network.netty.NettyLeakDetectionResource;
import org.apache.flink.runtime.rest.util.RestMapperUtils;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.FileUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Tests for the {@link FileUploadHandler}. Ensures that multipart http messages containing files and/or json are properly
 * handled.
 */
public class FileUploadHandlerTest extends TestLogger {
    @ClassRule
    public static final MultipartUploadResource MULTIPART_UPLOAD_RESOURCE = new MultipartUploadResource();

    private static final ObjectMapper OBJECT_MAPPER = RestMapperUtils.getStrictObjectMapper();

    @ClassRule
    public static final NettyLeakDetectionResource LEAK_DETECTION = new NettyLeakDetectionResource();

    @Test
    public void testUploadDirectoryRegeneration() throws Exception {
        OkHttpClient client = new OkHttpClient();
        MultipartUploadResource.MultipartFileHandler fileHandler = FileUploadHandlerTest.MULTIPART_UPLOAD_RESOURCE.getFileHandler();
        FileUtils.deleteDirectory(FileUploadHandlerTest.MULTIPART_UPLOAD_RESOURCE.getUploadDirectory().toFile());
        Request fileRequest = FileUploadHandlerTest.buildFileRequest(getMessageHeaders().getTargetRestEndpointURL());
        try (Response response = client.newCall(fileRequest).execute()) {
            Assert.assertEquals(getMessageHeaders().getResponseStatusCode().code(), response.code());
        }
    }

    @Test
    public void testMixedMultipart() throws Exception {
        OkHttpClient client = new OkHttpClient();
        MultipartUploadResource.MultipartMixedHandler mixedHandler = FileUploadHandlerTest.MULTIPART_UPLOAD_RESOURCE.getMixedHandler();
        Request jsonRequest = FileUploadHandlerTest.buildJsonRequest(getMessageHeaders().getTargetRestEndpointURL(), new MultipartUploadResource.TestRequestBody());
        try (Response response = client.newCall(jsonRequest).execute()) {
            // explicitly rejected by the test handler implementation
            Assert.assertEquals(INTERNAL_SERVER_ERROR.code(), response.code());
        }
        Request fileRequest = FileUploadHandlerTest.buildFileRequest(getMessageHeaders().getTargetRestEndpointURL());
        try (Response response = client.newCall(fileRequest).execute()) {
            // expected JSON payload is missing
            Assert.assertEquals(BAD_REQUEST.code(), response.code());
        }
        MultipartUploadResource.TestRequestBody json = new MultipartUploadResource.TestRequestBody();
        Request mixedRequest = FileUploadHandlerTest.buildMixedRequest(getMessageHeaders().getTargetRestEndpointURL(), json);
        try (Response response = client.newCall(mixedRequest).execute()) {
            Assert.assertEquals(getMessageHeaders().getResponseStatusCode().code(), response.code());
            Assert.assertEquals(json, mixedHandler.lastReceivedRequest);
        }
    }

    @Test
    public void testJsonMultipart() throws Exception {
        OkHttpClient client = new OkHttpClient();
        MultipartUploadResource.MultipartJsonHandler jsonHandler = FileUploadHandlerTest.MULTIPART_UPLOAD_RESOURCE.getJsonHandler();
        MultipartUploadResource.TestRequestBody json = new MultipartUploadResource.TestRequestBody();
        Request jsonRequest = FileUploadHandlerTest.buildJsonRequest(getMessageHeaders().getTargetRestEndpointURL(), json);
        try (Response response = client.newCall(jsonRequest).execute()) {
            Assert.assertEquals(getMessageHeaders().getResponseStatusCode().code(), response.code());
            Assert.assertEquals(json, jsonHandler.lastReceivedRequest);
        }
        Request fileRequest = FileUploadHandlerTest.buildFileRequest(getMessageHeaders().getTargetRestEndpointURL());
        try (Response response = client.newCall(fileRequest).execute()) {
            // either because JSON payload is missing or FileUploads are outright forbidden
            Assert.assertEquals(BAD_REQUEST.code(), response.code());
        }
        Request mixedRequest = FileUploadHandlerTest.buildMixedRequest(getMessageHeaders().getTargetRestEndpointURL(), new MultipartUploadResource.TestRequestBody());
        try (Response response = client.newCall(mixedRequest).execute()) {
            // FileUploads are outright forbidden
            Assert.assertEquals(BAD_REQUEST.code(), response.code());
        }
    }

    @Test
    public void testFileMultipart() throws Exception {
        OkHttpClient client = new OkHttpClient();
        MultipartUploadResource.MultipartFileHandler fileHandler = FileUploadHandlerTest.MULTIPART_UPLOAD_RESOURCE.getFileHandler();
        Request jsonRequest = FileUploadHandlerTest.buildJsonRequest(getMessageHeaders().getTargetRestEndpointURL(), new MultipartUploadResource.TestRequestBody());
        try (Response response = client.newCall(jsonRequest).execute()) {
            // JSON payload did not match expected format
            Assert.assertEquals(BAD_REQUEST.code(), response.code());
        }
        Request fileRequest = FileUploadHandlerTest.buildFileRequest(getMessageHeaders().getTargetRestEndpointURL());
        try (Response response = client.newCall(fileRequest).execute()) {
            Assert.assertEquals(getMessageHeaders().getResponseStatusCode().code(), response.code());
        }
        Request mixedRequest = FileUploadHandlerTest.buildMixedRequest(getMessageHeaders().getTargetRestEndpointURL(), new MultipartUploadResource.TestRequestBody());
        try (Response response = client.newCall(mixedRequest).execute()) {
            // JSON payload did not match expected format
            Assert.assertEquals(BAD_REQUEST.code(), response.code());
        }
    }

    @Test
    public void testUploadCleanupOnUnknownAttribute() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = FileUploadHandlerTest.buildMixedRequestWithUnknownAttribute(getMessageHeaders().getTargetRestEndpointURL());
        try (Response response = client.newCall(request).execute()) {
            Assert.assertEquals(BAD_REQUEST.code(), response.code());
        }
        FileUploadHandlerTest.MULTIPART_UPLOAD_RESOURCE.assertUploadDirectoryIsEmpty();
    }

    /**
     * Crashes the handler be submitting a malformed multipart request and tests that the upload directory is cleaned up.
     */
    @Test
    public void testUploadCleanupOnFailure() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = FileUploadHandlerTest.buildMalformedRequest(getMessageHeaders().getTargetRestEndpointURL());
        try (Response response = client.newCall(request).execute()) {
            // decoding errors aren't handled separately by the FileUploadHandler
            Assert.assertEquals(INTERNAL_SERVER_ERROR.code(), response.code());
        }
        FileUploadHandlerTest.MULTIPART_UPLOAD_RESOURCE.assertUploadDirectoryIsEmpty();
    }
}

