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
package io.undertow.server.handlers.accesslog;


import StatusCodes.OK;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.CompletionLatchHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests writing the access log to a file
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class AccessLogFileTestCase {
    private static final Path logDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "logs");

    private static final int NUM_THREADS = 10;

    private static final int NUM_REQUESTS = 12;

    private static final HttpHandler HELLO_HANDLER = new HttpHandler() {
        @Override
        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            exchange.getResponseSender().send("Hello");
        }
    };

    @Test
    public void testSingleLogMessageToFile() throws IOException, InterruptedException {
        Path directory = AccessLogFileTestCase.logDirectory;
        Path logFileName = directory.resolve("server1.log");
        DefaultAccessLogReceiver logReceiver = new DefaultAccessLogReceiver(DefaultServer.getWorker(), directory, "server1.");
        verifySingleLogMessageToFile(logFileName, logReceiver);
    }

    @Test
    public void testSingleLogMessageToFileWithSuffix() throws IOException, InterruptedException {
        Path directory = AccessLogFileTestCase.logDirectory;
        Path logFileName = directory.resolve("server1.logsuffix");
        DefaultAccessLogReceiver logReceiver = new DefaultAccessLogReceiver(DefaultServer.getWorker(), directory, "server1.", "logsuffix");
        verifySingleLogMessageToFile(logFileName, logReceiver);
    }

    @Test
    public void testLogLotsOfThreads() throws IOException, InterruptedException, ExecutionException {
        Path directory = AccessLogFileTestCase.logDirectory;
        Path logFileName = directory.resolve("server2.log");
        DefaultAccessLogReceiver logReceiver = new DefaultAccessLogReceiver(DefaultServer.getWorker(), directory, "server2.");
        CompletionLatchHandler latchHandler;
        DefaultServer.setRootHandler((latchHandler = new CompletionLatchHandler(((AccessLogFileTestCase.NUM_REQUESTS) * (AccessLogFileTestCase.NUM_THREADS)), new AccessLogHandler(AccessLogFileTestCase.HELLO_HANDLER, logReceiver, "REQ %{i,test-header}", AccessLogFileTestCase.class.getClassLoader()))));
        ExecutorService executor = Executors.newFixedThreadPool(AccessLogFileTestCase.NUM_THREADS);
        try {
            final List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < (AccessLogFileTestCase.NUM_THREADS); ++i) {
                final int threadNo = i;
                futures.add(executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        TestHttpClient client = new TestHttpClient();
                        try {
                            for (int i = 0; i < (AccessLogFileTestCase.NUM_REQUESTS); ++i) {
                                HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
                                get.addHeader("test-header", ((("thread-" + threadNo) + "-request-") + i));
                                HttpResponse result = client.execute(get);
                                Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
                                final String response = HttpClientUtils.readResponse(result);
                                Assert.assertEquals("Hello", response);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            client.getConnectionManager().shutdown();
                        }
                    }
                }));
            }
            for (Future<?> future : futures) {
                future.get();
            }
        } finally {
            executor.shutdown();
        }
        latchHandler.await();
        logReceiver.awaitWrittenForTest();
        String completeLog = new String(Files.readAllBytes(logFileName));
        for (int i = 0; i < (AccessLogFileTestCase.NUM_THREADS); ++i) {
            for (int j = 0; j < (AccessLogFileTestCase.NUM_REQUESTS); ++j) {
                Assert.assertTrue(completeLog.contains(((("REQ thread-" + i) + "-request-") + j)));
            }
        }
    }

    @Test
    public void testForcedLogRotation() throws IOException, InterruptedException {
        Path logFileName = AccessLogFileTestCase.logDirectory.resolve("server.log");
        DefaultAccessLogReceiver logReceiver = new DefaultAccessLogReceiver(DefaultServer.getWorker(), AccessLogFileTestCase.logDirectory, "server.");
        CompletionLatchHandler latchHandler;
        DefaultServer.setRootHandler((latchHandler = new CompletionLatchHandler(new AccessLogHandler(AccessLogFileTestCase.HELLO_HANDLER, logReceiver, "Remote address %a Code %s test-header %{i,test-header}", AccessLogFileTestCase.class.getClassLoader()))));
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
            get.addHeader("test-header", "v1");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals("Hello", HttpClientUtils.readResponse(result));
            latchHandler.await();
            latchHandler.reset();
            logReceiver.awaitWrittenForTest();
            Assert.assertEquals((("Remote address " + (DefaultServer.getDefaultServerAddress().getAddress().getHostAddress())) + " Code 200 test-header v1\n"), new String(Files.readAllBytes(logFileName)));
            logReceiver.rotate();
            logReceiver.awaitWrittenForTest();
            Assert.assertFalse(Files.exists(logFileName));
            Path firstLogRotate = AccessLogFileTestCase.logDirectory.resolve((("server." + (new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) + ".log"));
            Assert.assertEquals((("Remote address " + (DefaultServer.getDefaultServerAddress().getAddress().getHostAddress())) + " Code 200 test-header v1\n"), new String(Files.readAllBytes(firstLogRotate)));
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
            get.addHeader("test-header", "v2");
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals("Hello", HttpClientUtils.readResponse(result));
            latchHandler.await();
            latchHandler.reset();
            logReceiver.awaitWrittenForTest();
            Assert.assertEquals((("Remote address " + (DefaultServer.getDefaultServerAddress().getAddress().getHostAddress())) + " Code 200 test-header v2\n"), new String(Files.readAllBytes(logFileName)));
            logReceiver.rotate();
            logReceiver.awaitWrittenForTest();
            Assert.assertFalse(Files.exists(logFileName));
            Path secondLogRotate = AccessLogFileTestCase.logDirectory.resolve((("server." + (new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) + "-1.log"));
            Assert.assertEquals((("Remote address " + (DefaultServer.getDefaultServerAddress().getAddress().getHostAddress())) + " Code 200 test-header v2\n"), new String(Files.readAllBytes(secondLogRotate)));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

