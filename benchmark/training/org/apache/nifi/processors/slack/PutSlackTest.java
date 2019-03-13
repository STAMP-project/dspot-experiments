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


import PutSlack.CHANNEL;
import PutSlack.ICON_EMOJI;
import PutSlack.ICON_URL;
import PutSlack.REL_FAILURE;
import PutSlack.REL_SUCCESS;
import PutSlack.USERNAME;
import PutSlack.WEBHOOK_TEXT;
import PutSlack.WEBHOOK_URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.web.util.TestServer;
import org.junit.Assert;
import org.junit.Test;


public class PutSlackTest {
    private TestRunner testRunner;

    private TestServer server;

    private CaptureServlet servlet;

    public static final String WEBHOOK_TEST_TEXT = "Hello From Apache NiFi";

    @Test(expected = AssertionError.class)
    public void testBlankText() {
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, "");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
    }

    @Test
    public void testBlankTextViaExpression() {
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, "${invalid-attr}");// Create a blank webhook text

        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE);
    }

    @Test
    public void testInvalidChannel() {
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, PutSlackTest.WEBHOOK_TEST_TEXT);
        testRunner.setProperty(CHANNEL, "invalid");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE);
    }

    @Test(expected = AssertionError.class)
    public void testInvalidIconUrl() {
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, PutSlackTest.WEBHOOK_TEST_TEXT);
        testRunner.setProperty(ICON_URL, "invalid");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
    }

    @Test(expected = AssertionError.class)
    public void testInvalidIconEmoji() {
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, PutSlackTest.WEBHOOK_TEST_TEXT);
        testRunner.setProperty(ICON_EMOJI, "invalid");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
    }

    @Test
    public void testInvalidDynamicProperties() {
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, PutSlackTest.WEBHOOK_TEST_TEXT);
        PropertyDescriptor dynamicProp = new PropertyDescriptor.Builder().dynamic(true).name("foo").build();
        testRunner.setProperty(dynamicProp, "{\"a\": a}");
        testRunner.enqueue("{}".getBytes());
        testRunner.run(1);
        testRunner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void testValidDynamicProperties() {
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, PutSlackTest.WEBHOOK_TEST_TEXT);
        PropertyDescriptor dynamicProp = new PropertyDescriptor.Builder().dynamic(true).name("foo").build();
        testRunner.setProperty(dynamicProp, "{\"a\": \"a\"}");
        testRunner.enqueue("{}".getBytes());
        testRunner.run(1);
        testRunner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test
    public void testValidDynamicPropertiesWithExpressionLanguage() {
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        Map<String, String> props = new HashMap<>();
        props.put("foo", "\"bar\"");
        props.put("ping", "pong");
        ff = session.putAllAttributes(ff, props);
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, PutSlackTest.WEBHOOK_TEST_TEXT);
        PropertyDescriptor dynamicProp = new PropertyDescriptor.Builder().dynamic(true).name("foo").build();
        testRunner.setProperty(dynamicProp, "{\"foo\": ${foo}, \"ping\":\"${ping}\"}");
        testRunner.enqueue(ff);
        testRunner.run(1);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
    }

    @Test
    public void testInvalidDynamicPropertiesWithExpressionLanguage() {
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        Map<String, String> props = new HashMap<>();
        props.put("foo", "\"\"bar\"");
        props.put("ping", "\"pong");
        ff = session.putAllAttributes(ff, props);
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, PutSlackTest.WEBHOOK_TEST_TEXT);
        PropertyDescriptor dynamicProp = new PropertyDescriptor.Builder().dynamic(true).name("foo").build();
        testRunner.setProperty(dynamicProp, "{\"foo\": ${foo}, \"ping\":\"${ping}\"}");
        testRunner.enqueue(ff);
        testRunner.run(1);
        testRunner.assertTransferCount(REL_SUCCESS, 0);
        testRunner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void testGetPropertyDescriptors() throws Exception {
        PutSlack processor = new PutSlack();
        List<PropertyDescriptor> pd = processor.getSupportedPropertyDescriptors();
        Assert.assertEquals("size should be eq", 6, pd.size());
        Assert.assertTrue(pd.contains(WEBHOOK_TEXT));
        Assert.assertTrue(pd.contains(WEBHOOK_URL));
        Assert.assertTrue(pd.contains(CHANNEL));
        Assert.assertTrue(pd.contains(USERNAME));
        Assert.assertTrue(pd.contains(ICON_URL));
        Assert.assertTrue(pd.contains(ICON_EMOJI));
    }

    @Test
    public void testSimplePut() {
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, PutSlackTest.WEBHOOK_TEST_TEXT);
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        byte[] expected = "payload=%7B%22text%22%3A%22Hello+From+Apache+NiFi%22%7D".getBytes();
        Assert.assertTrue(Arrays.equals(expected, servlet.getLastPost()));
    }

    @Test
    public void testSimplePutWithAttributes() {
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, PutSlackTest.WEBHOOK_TEST_TEXT);
        testRunner.setProperty(CHANNEL, "#test-attributes");
        testRunner.setProperty(USERNAME, "integration-test-webhook");
        testRunner.setProperty(ICON_EMOJI, ":smile:");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expected = "payload=%7B%22text%22%3A%22Hello+From+Apache+NiFi%22%2C%22channel%22%3A%22%23test-attributes%22%2C%22username%22%3A%22" + "integration-test-webhook%22%2C%22icon_emoji%22%3A%22%3Asmile%3A%22%7D";
        Assert.assertTrue(Arrays.equals(expected.getBytes(), servlet.getLastPost()));
    }

    @Test
    public void testSimplePutWithAttributesIconURL() {
        testRunner.setProperty(WEBHOOK_URL, server.getUrl());
        testRunner.setProperty(WEBHOOK_TEXT, PutSlackTest.WEBHOOK_TEST_TEXT);
        testRunner.setProperty(CHANNEL, "#test-attributes-url");
        testRunner.setProperty(USERNAME, "integration-test-webhook");
        testRunner.setProperty(ICON_URL, "http://lorempixel.com/48/48/");
        testRunner.enqueue(new byte[0]);
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expected = "payload=%7B%22text%22%3A%22Hello+From+Apache+NiFi%22%2C%22channel%22%3A%22%23test-attributes-url%22%2C%22username%22%3A%22" + "integration-test-webhook%22%2C%22icon_url%22%3A%22http%3A%2F%2Florempixel.com%2F48%2F48%2F%22%7D";
        Assert.assertTrue(Arrays.equals(expected.getBytes(), servlet.getLastPost()));
    }

    @Test
    public void testSimplePutWithEL() {
        testRunner.setProperty(WEBHOOK_URL, "${slack.url}");
        testRunner.setProperty(WEBHOOK_TEXT, PutSlackTest.WEBHOOK_TEST_TEXT);
        testRunner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("slack.url", server.getUrl());
            }
        });
        testRunner.run(1);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        byte[] expected = "payload=%7B%22text%22%3A%22Hello+From+Apache+NiFi%22%7D".getBytes();
        Assert.assertTrue(Arrays.equals(expected, servlet.getLastPost()));
    }
}

