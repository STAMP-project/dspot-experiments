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


import PostSlack.ACCESS_TOKEN;
import PostSlack.CHANNEL;
import PostSlack.POST_MESSAGE_URL;
import PostSlack.TEXT;
import PutSlack.REL_FAILURE;
import PutSlack.REL_SUCCESS;
import javax.json.JsonObject;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.web.util.TestServer;
import org.junit.Assert;
import org.junit.Test;


public class PostSlackTextMessageTest {
    private TestRunner testRunner;

    private TestServer server;

    private PostSlackCaptureServlet servlet;

    @Test
    public void sendTextOnlyMessageSuccessfully() {
        testRunner.setProperty(POST_MESSAGE_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_TEXT_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        JsonObject requestBodyJson = getRequestBodyJson();
        assertBasicRequest(requestBodyJson);
        Assert.assertEquals("my-text", requestBodyJson.getString("text"));
    }

    @Test
    public void sendTextWithAttachmentMessageSuccessfully() {
        testRunner.setProperty(POST_MESSAGE_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_TEXT_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.setProperty("attachment_01", "{\"my-attachment-key\": \"my-attachment-value\"}");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        JsonObject requestBodyJson = getRequestBodyJson();
        assertBasicRequest(requestBodyJson);
        Assert.assertEquals("my-text", requestBodyJson.getString("text"));
        Assert.assertEquals("[{\"my-attachment-key\":\"my-attachment-value\"}]", requestBodyJson.getJsonArray("attachments").toString());
    }

    @Test
    public void sendAttachmentOnlyMessageSuccessfully() {
        testRunner.setProperty(POST_MESSAGE_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_TEXT_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty("attachment_01", "{\"my-attachment-key\": \"my-attachment-value\"}");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        JsonObject requestBodyJson = getRequestBodyJson();
        assertBasicRequest(requestBodyJson);
        Assert.assertEquals("[{\"my-attachment-key\":\"my-attachment-value\"}]", requestBodyJson.getJsonArray("attachments").toString());
    }

    @Test
    public void processShouldFailWhenChannelIsEmpty() {
        testRunner.setProperty(POST_MESSAGE_URL, server.getUrl());
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "${dummy}");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE);
        Assert.assertFalse(servlet.hasBeenInteracted());
    }

    @Test
    public void processShouldFailWhenTextIsEmptyAndNoAttachmentSpecified() {
        testRunner.setProperty(POST_MESSAGE_URL, server.getUrl());
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "${dummy}");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE);
        Assert.assertFalse(servlet.hasBeenInteracted());
    }

    @Test
    public void emptyAttachmentShouldBeSkipped() {
        testRunner.setProperty(POST_MESSAGE_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_TEXT_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty("attachment_01", "${dummy}");
        testRunner.setProperty("attachment_02", "{\"my-attachment-key\": \"my-attachment-value\"}");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        JsonObject requestBodyJson = getRequestBodyJson();
        assertBasicRequest(requestBodyJson);
        Assert.assertEquals(1, requestBodyJson.getJsonArray("attachments").size());
        Assert.assertEquals("[{\"my-attachment-key\":\"my-attachment-value\"}]", requestBodyJson.getJsonArray("attachments").toString());
    }

    @Test
    public void invalidAttachmentShouldBeSkipped() {
        testRunner.setProperty(POST_MESSAGE_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_TEXT_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty("attachment_01", "{invalid-json}");
        testRunner.setProperty("attachment_02", "{\"my-attachment-key\": \"my-attachment-value\"}");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        JsonObject requestBodyJson = getRequestBodyJson();
        assertBasicRequest(requestBodyJson);
        Assert.assertEquals(1, requestBodyJson.getJsonArray("attachments").size());
        Assert.assertEquals("[{\"my-attachment-key\":\"my-attachment-value\"}]", requestBodyJson.getJsonArray("attachments").toString());
    }

    @Test
    public void processShouldFailWhenHttpErrorCodeReturned() {
        testRunner.setProperty(POST_MESSAGE_URL, server.getUrl());
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE);
    }

    @Test
    public void processShouldFailWhenSlackReturnsError() {
        testRunner.setProperty(POST_MESSAGE_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_ERROR)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE);
    }

    @Test
    public void processShouldNotFailWhenSlackReturnsWarning() {
        testRunner.setProperty(POST_MESSAGE_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_WARNING)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        assertBasicRequest(getRequestBodyJson());
    }

    @Test
    public void processShouldFailWhenSlackReturnsEmptyJson() {
        testRunner.setProperty(POST_MESSAGE_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_EMPTY_JSON)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE);
    }

    @Test
    public void processShouldFailWhenSlackReturnsInvalidJson() {
        testRunner.setProperty(POST_MESSAGE_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_INVALID_JSON)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "my-text");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE);
    }

    @Test
    public void sendInternationalMessageSuccessfully() {
        testRunner.setProperty(POST_MESSAGE_URL, ((server.getUrl()) + (PostSlackCaptureServlet.REQUEST_PATH_SUCCESS_TEXT_MSG)));
        testRunner.setProperty(ACCESS_TOKEN, "my-access-token");
        testRunner.setProperty(CHANNEL, "my-channel");
        testRunner.setProperty(TEXT, "I?t?rn?ti?n?li??ti?n");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS);
        JsonObject requestBodyJson = getRequestBodyJson();
        assertBasicRequest(requestBodyJson);
        Assert.assertEquals("I?t?rn?ti?n?li??ti?n", requestBodyJson.getString("text"));
    }
}

