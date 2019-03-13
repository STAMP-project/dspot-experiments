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


import StatusCodes.OK;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.CompletionLatchHandler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests writing the database (in memory)
 *
 * @author Filipe Ferraz
 */
@RunWith(DefaultServer.class)
public class JDBCLogDatabaseTestCase {
    private static final int NUM_THREADS = 10;

    private static final int NUM_REQUESTS = 12;

    private static final HttpHandler HELLO_HANDLER = new HttpHandler() {
        @Override
        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            exchange.getResponseSender().send("Hello");
        }
    };

    private JdbcConnectionPool ds;

    @Test
    public void testSingleLogMessageToDatabase() throws IOException, InterruptedException, SQLException {
        JDBCLogHandler logHandler = new JDBCLogHandler(JDBCLogDatabaseTestCase.HELLO_HANDLER, DefaultServer.getWorker(), "common", ds);
        CompletionLatchHandler latchHandler;
        DefaultServer.setRootHandler((latchHandler = new CompletionLatchHandler(logHandler)));
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
            HttpResponse result = client.execute(get);
            latchHandler.await();
            logHandler.awaitWrittenForTest();
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals("Hello", HttpClientUtils.readResponse(result));
        } finally {
            Connection conn = null;
            Statement statement = null;
            try {
                conn = ds.getConnection();
                statement = conn.createStatement();
                ResultSet resultDatabase = statement.executeQuery("SELECT * FROM PUBLIC.ACCESS;");
                resultDatabase.next();
                Assert.assertEquals(DefaultServer.getDefaultServerAddress().getAddress().getHostAddress(), resultDatabase.getString(logHandler.getRemoteHostField()));
                Assert.assertEquals("5", resultDatabase.getString(logHandler.getBytesField()));
                Assert.assertEquals("200", resultDatabase.getString(logHandler.getStatusField()));
                client.getConnectionManager().shutdown();
            } finally {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }

    @Test
    public void testLogLotsOfThreadsToDatabase() throws IOException, InterruptedException, SQLException, ExecutionException {
        JDBCLogHandler logHandler = new JDBCLogHandler(JDBCLogDatabaseTestCase.HELLO_HANDLER, DefaultServer.getWorker(), "combined", ds);
        CompletionLatchHandler latchHandler;
        DefaultServer.setRootHandler((latchHandler = new CompletionLatchHandler(((JDBCLogDatabaseTestCase.NUM_REQUESTS) * (JDBCLogDatabaseTestCase.NUM_THREADS)), logHandler)));
        ExecutorService executor = Executors.newFixedThreadPool(JDBCLogDatabaseTestCase.NUM_THREADS);
        try {
            final List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < (JDBCLogDatabaseTestCase.NUM_THREADS); ++i) {
                final int threadNo = i;
                futures.add(executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        TestHttpClient client = new TestHttpClient();
                        try {
                            for (int i = 0; i < (JDBCLogDatabaseTestCase.NUM_REQUESTS); ++i) {
                                HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
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
        logHandler.awaitWrittenForTest();
        Connection conn = null;
        Statement statement = null;
        try {
            conn = ds.getConnection();
            statement = conn.createStatement();
            ResultSet resultDatabase = conn.createStatement().executeQuery("SELECT COUNT(*) FROM PUBLIC.ACCESS;");
            resultDatabase.next();
            Assert.assertEquals(resultDatabase.getInt(1), ((JDBCLogDatabaseTestCase.NUM_REQUESTS) * (JDBCLogDatabaseTestCase.NUM_THREADS)));
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}

