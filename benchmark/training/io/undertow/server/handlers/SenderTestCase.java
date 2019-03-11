/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server.handlers;


import Headers.CONTENT_LENGTH_STRING;
import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class SenderTestCase {
    public static final int SENDS = 10000;

    public static final int TXS = 1000;

    public static final String HELLO_WORLD = "Hello World";

    @Test
    public void testAsyncSender() throws IOException {
        StringBuilder sb = new StringBuilder(SenderTestCase.SENDS);
        for (int i = 0; i < (SenderTestCase.SENDS); ++i) {
            sb.append("a");
        }
        HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/lots?blocking=false"));
        TestHttpClient client = new TestHttpClient();
        try {
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(sb.toString(), HttpClientUtils.readResponse(result));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testAsyncTransfer() throws Exception {
        StringBuilder sb = new StringBuilder(SenderTestCase.TXS);
        for (int i = 0; i < (SenderTestCase.TXS); ++i) {
            sb.append("a");
        }
        HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/transfer?blocking=false"));
        TestHttpClient client = new TestHttpClient();
        try {
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Path file = Paths.get(SenderTestCase.class.getResource(((SenderTestCase.class.getSimpleName()) + ".class")).toURI());
            long length = Files.size(file);
            byte[] data = new byte[((int) (length)) * (SenderTestCase.TXS)];
            for (int i = 0; i < (SenderTestCase.TXS); i++) {
                try (DataInputStream is = new DataInputStream(Files.newInputStream(file))) {
                    is.readFully(data, ((int) (i * length)), ((int) (length)));
                }
            }
            Assert.assertArrayEquals(data, HttpClientUtils.readRawResponse(result));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testSyncTransfer() throws Exception {
        StringBuilder sb = new StringBuilder(SenderTestCase.TXS);
        for (int i = 0; i < (SenderTestCase.TXS); ++i) {
            sb.append("a");
        }
        HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/transfer?blocking=true"));
        TestHttpClient client = new TestHttpClient();
        try {
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Path file = Paths.get(SenderTestCase.class.getResource(((SenderTestCase.class.getSimpleName()) + ".class")).toURI());
            long length = Files.size(file);
            byte[] data = new byte[((int) (length)) * (SenderTestCase.TXS)];
            for (int i = 0; i < (SenderTestCase.TXS); i++) {
                try (DataInputStream is = new DataInputStream(Files.newInputStream(file))) {
                    is.readFully(data, ((int) (i * length)), ((int) (length)));
                }
            }
            Assert.assertArrayEquals(data, HttpClientUtils.readRawResponse(result));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testBlockingSender() throws IOException {
        StringBuilder sb = new StringBuilder(SenderTestCase.SENDS);
        for (int i = 0; i < (SenderTestCase.SENDS); ++i) {
            sb.append("a");
        }
        HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/lots?blocking=true"));
        TestHttpClient client = new TestHttpClient();
        try {
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(sb.toString(), HttpClientUtils.readResponse(result));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testSenderSetsContentLength() throws IOException {
        HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/fixed"));
        TestHttpClient client = new TestHttpClient();
        try {
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(SenderTestCase.HELLO_WORLD, HttpClientUtils.readResponse(result));
            Header[] header = result.getHeaders(CONTENT_LENGTH_STRING);
            Assert.assertEquals(1, header.length);
            Assert.assertEquals(("" + (SenderTestCase.HELLO_WORLD.length())), header[0].getValue());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

