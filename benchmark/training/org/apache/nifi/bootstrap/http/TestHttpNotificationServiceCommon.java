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
package org.apache.nifi.bootstrap.http;


import NotificationType.NIFI_DIED;
import NotificationType.NIFI_STARTED;
import NotificationType.NIFI_STOPPED;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.ParserConfigurationException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.apache.nifi.bootstrap.NotificationServiceManager;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;


public abstract class TestHttpNotificationServiceCommon {
    public static String tempConfigFilePath;

    public static MockWebServer mockWebServer;

    @Test
    public void testStartNotification() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        TestHttpNotificationServiceCommon.mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        NotificationServiceManager notificationServiceManager = new NotificationServiceManager();
        notificationServiceManager.setMaxNotificationAttempts(1);
        notificationServiceManager.loadNotificationServices(new File(TestHttpNotificationServiceCommon.tempConfigFilePath));
        notificationServiceManager.registerNotificationService(NIFI_STARTED, "http-notification");
        notificationServiceManager.notify(NIFI_STARTED, "Subject", "Message");
        RecordedRequest recordedRequest = TestHttpNotificationServiceCommon.mockWebServer.takeRequest(2, TimeUnit.SECONDS);
        Assert.assertNotNull(recordedRequest);
        Assert.assertEquals(NIFI_STARTED.name(), recordedRequest.getHeader(NOTIFICATION_TYPE_KEY));
        Assert.assertEquals("Subject", recordedRequest.getHeader(NOTIFICATION_SUBJECT_KEY));
        Assert.assertEquals("testing", recordedRequest.getHeader("testProp"));
        Buffer bodyBuffer = recordedRequest.getBody();
        String bodyString = new String(bodyBuffer.readByteArray(), StandardCharsets.UTF_8);
        Assert.assertEquals("Message", bodyString);
    }

    @Test
    public void testStopNotification() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        TestHttpNotificationServiceCommon.mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        NotificationServiceManager notificationServiceManager = new NotificationServiceManager();
        notificationServiceManager.setMaxNotificationAttempts(1);
        notificationServiceManager.loadNotificationServices(new File(TestHttpNotificationServiceCommon.tempConfigFilePath));
        notificationServiceManager.registerNotificationService(NIFI_STOPPED, "http-notification");
        notificationServiceManager.notify(NIFI_STOPPED, "Subject", "Message");
        RecordedRequest recordedRequest = TestHttpNotificationServiceCommon.mockWebServer.takeRequest(2, TimeUnit.SECONDS);
        Assert.assertNotNull(recordedRequest);
        Assert.assertEquals(NIFI_STOPPED.name(), recordedRequest.getHeader(NOTIFICATION_TYPE_KEY));
        Assert.assertEquals("Subject", recordedRequest.getHeader(NOTIFICATION_SUBJECT_KEY));
        Buffer bodyBuffer = recordedRequest.getBody();
        String bodyString = new String(bodyBuffer.readByteArray(), StandardCharsets.UTF_8);
        Assert.assertEquals("Message", bodyString);
    }

    @Test
    public void testDiedNotification() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        TestHttpNotificationServiceCommon.mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        NotificationServiceManager notificationServiceManager = new NotificationServiceManager();
        notificationServiceManager.setMaxNotificationAttempts(1);
        notificationServiceManager.loadNotificationServices(new File(TestHttpNotificationServiceCommon.tempConfigFilePath));
        notificationServiceManager.registerNotificationService(NIFI_DIED, "http-notification");
        notificationServiceManager.notify(NIFI_DIED, "Subject", "Message");
        RecordedRequest recordedRequest = TestHttpNotificationServiceCommon.mockWebServer.takeRequest(2, TimeUnit.SECONDS);
        Assert.assertNotNull(recordedRequest);
        Assert.assertEquals(NIFI_DIED.name(), recordedRequest.getHeader(NOTIFICATION_TYPE_KEY));
        Assert.assertEquals("Subject", recordedRequest.getHeader(NOTIFICATION_SUBJECT_KEY));
        Buffer bodyBuffer = recordedRequest.getBody();
        String bodyString = new String(bodyBuffer.readByteArray(), StandardCharsets.UTF_8);
        Assert.assertEquals("Message", bodyString);
    }

    @Test
    public void testStartNotificationFailure() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        // Web server will still get the request but will return an error. Observe that it is gracefully handled.
        TestHttpNotificationServiceCommon.mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        NotificationServiceManager notificationServiceManager = new NotificationServiceManager();
        notificationServiceManager.setMaxNotificationAttempts(1);
        notificationServiceManager.loadNotificationServices(new File(TestHttpNotificationServiceCommon.tempConfigFilePath));
        notificationServiceManager.registerNotificationService(NIFI_STARTED, "http-notification");
        notificationServiceManager.notify(NIFI_STARTED, "Subject", "Message");
        RecordedRequest recordedRequest = TestHttpNotificationServiceCommon.mockWebServer.takeRequest(2, TimeUnit.SECONDS);
        Assert.assertNotNull(recordedRequest);
        Assert.assertEquals(NIFI_STARTED.name(), recordedRequest.getHeader(NOTIFICATION_TYPE_KEY));
        Assert.assertEquals("Subject", recordedRequest.getHeader(NOTIFICATION_SUBJECT_KEY));
        Buffer bodyBuffer = recordedRequest.getBody();
        String bodyString = new String(bodyBuffer.readByteArray(), StandardCharsets.UTF_8);
        Assert.assertEquals("Message", bodyString);
    }
}

