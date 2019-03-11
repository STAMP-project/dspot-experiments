/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.slack;


import CoreAttributes.FILENAME;
import CoreAttributes.MIME_TYPE;
import PostSlack.ACCESS_TOKEN;
import PostSlack.CHANNEL;
import PostSlack.FILE_MIME_TYPE;
import PostSlack.FILE_NAME;
import PostSlack.FILE_TITLE;
import PostSlack.FILE_UPLOAD_URL;
import PostSlack.TEXT;
import PostSlack.UPLOAD_FLOWFILE;
import PostSlack.UPLOAD_FLOWFILE_YES;
import PutSlack.REL_FAILURE;
import PutSlack.REL_SUCCESS;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.web.util.TestServer;
import org.junit.Assert;
import org.junit.Test;


public class PostSlackFileMessageTest {
    private TestRunner testRunner;

    private TestServer server;

    private PostSlackCaptureServlet servlet;

    @Test
    public void sendMessageWithBasicPropertiesSuccessfully() {
        testRunner.setProperty(FILE_UPLOAD_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_FILE_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(UPLOAD_FLOWFILE, UPLOAD_FLOWFILE_YES);
        Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), "my-file-name");
        flowFileAttributes.put(MIME_TYPE.key(), "image/png");
        // in order not to make the assertion logic (even more) complicated, the file content is tested with character data instead of binary data
        testRunner.enqueue("my-data", flowFileAttributes);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        assertRequest("my-file-name", "image/png", null, null);
        FlowFile flowFileOut = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("slack-file-url", flowFileOut.getAttribute("slack.file.url"));
    }

    @Test
    public void sendMessageWithAllPropertiesSuccessfully() {
        testRunner.setProperty(FILE_UPLOAD_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_FILE_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.setProperty(UPLOAD_FLOWFILE, UPLOAD_FLOWFILE_YES);
        testRunner.setProperty(FILE_TITLE, "my-file-title");
        testRunner.setProperty(FILE_NAME, "my-file-name");
        testRunner.setProperty(FILE_MIME_TYPE, "image/png");
        testRunner.enqueue("my-data");
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        assertRequest("my-file-name", "image/png", "my-text", "my-file-title");
        FlowFile flowFileOut = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("slack-file-url", flowFileOut.getAttribute("slack.file.url"));
    }

    @Test
    public void processShouldFailWhenChannelIsEmpty() {
        testRunner.setProperty(FILE_UPLOAD_URL, server.getUrl());
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "${dummy}");
        testRunner.setProperty(UPLOAD_FLOWFILE, UPLOAD_FLOWFILE_YES);
        testRunner.enqueue("my-data");
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE);
        Assert.assertFalse(servlet.hasBeenInteracted());
    }

    @Test
    public void fileNameShouldHaveFallbackValueWhenEmpty() {
        testRunner.setProperty(FILE_UPLOAD_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_FILE_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(UPLOAD_FLOWFILE, UPLOAD_FLOWFILE_YES);
        testRunner.setProperty(FILE_NAME, "${dummy}");
        testRunner.setProperty(FILE_MIME_TYPE, "image/png");
        testRunner.enqueue("my-data");
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        // fallback value for file name is 'file'
        assertRequest("file", "image/png", null, null);
        FlowFile flowFileOut = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("slack-file-url", flowFileOut.getAttribute("slack.file.url"));
    }

    @Test
    public void mimeTypeShouldHaveFallbackValueWhenEmpty() {
        testRunner.setProperty(FILE_UPLOAD_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_FILE_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(UPLOAD_FLOWFILE, UPLOAD_FLOWFILE_YES);
        testRunner.setProperty(FILE_NAME, "my-file-name");
        testRunner.setProperty(FILE_MIME_TYPE, "${dummy}");
        testRunner.enqueue("my-data");
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        // fallback value for mime type is 'application/octet-stream'
        assertRequest("my-file-name", "application/octet-stream", null, null);
        FlowFile flowFileOut = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("slack-file-url", flowFileOut.getAttribute("slack.file.url"));
    }

    @Test
    public void mimeTypeShouldHaveFallbackValueWhenInvalid() {
        testRunner.setProperty(FILE_UPLOAD_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_FILE_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(UPLOAD_FLOWFILE, UPLOAD_FLOWFILE_YES);
        testRunner.setProperty(FILE_NAME, "my-file-name");
        testRunner.setProperty(FILE_MIME_TYPE, "invalid");
        testRunner.enqueue("my-data");
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        // fallback value for mime type is 'application/octet-stream'
        assertRequest("my-file-name", "application/octet-stream", null, null);
        FlowFile flowFileOut = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("slack-file-url", flowFileOut.getAttribute("slack.file.url"));
    }

    @Test
    public void sendInternationalMessageSuccessfully() {
        testRunner.setProperty(FILE_UPLOAD_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_FILE_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "I?t?rn?ti?n?li??ti?n");
        testRunner.setProperty(UPLOAD_FLOWFILE, UPLOAD_FLOWFILE_YES);
        testRunner.setProperty(FILE_TITLE, "I?t?rn?ti?n?li??ti?n");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        Map<String, String> parts = parsePostBodyParts(parseMultipartBoundary(servlet.getLastPostHeaders().get("Content-Type")));
        Assert.assertEquals("I?t?rn?ti?n?li??ti?n", parts.get("initial_comment"));
        Assert.assertEquals("I?t?rn?ti?n?li??ti?n", parts.get("title"));
        FlowFile flowFileOut = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("slack-file-url", flowFileOut.getAttribute("slack.file.url"));
    }
}

