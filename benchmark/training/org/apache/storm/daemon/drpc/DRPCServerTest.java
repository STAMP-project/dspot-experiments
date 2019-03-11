/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.daemon.drpc;


import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.storm.drpc.DRPCInvocationsClient;
import org.apache.storm.generated.DRPCExecutionException;
import org.apache.storm.generated.DRPCRequest;
import org.apache.storm.metric.StormMetricsRegistry;
import org.apache.storm.utils.DRPCClient;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DRPCServerTest {
    private static final Logger LOG = LoggerFactory.getLogger(DRPCServerTest.class);

    private static final ExecutorService exec = Executors.newCachedThreadPool();

    @Test
    public void testGoodThrift() throws Exception {
        Map<String, Object> conf = getConf(0, 0, null);
        try (DRPCServer server = new DRPCServer(conf, new StormMetricsRegistry())) {
            server.start();
            try (DRPCClient client = new DRPCClient(conf, "localhost", server.getDrpcPort());DRPCInvocationsClient invoke = new DRPCInvocationsClient(conf, "localhost", server.getDrpcInvokePort())) {
                final Future<String> found = DRPCServerTest.exec.submit(() -> client.getClient().execute("testing", "test"));
                DRPCRequest request = DRPCServerTest.getNextAvailableRequest(invoke, "testing");
                Assert.assertNotNull(request);
                Assert.assertEquals("test", request.get_func_args());
                Assert.assertNotNull(request.get_request_id());
                invoke.result(request.get_request_id(), "tested");
                String result = found.get(1000, TimeUnit.MILLISECONDS);
                Assert.assertEquals("tested", result);
            }
        }
    }

    @Test
    public void testFailedThrift() throws Exception {
        Map<String, Object> conf = getConf(0, 0, null);
        try (DRPCServer server = new DRPCServer(conf, new StormMetricsRegistry())) {
            server.start();
            try (DRPCClient client = new DRPCClient(conf, "localhost", server.getDrpcPort());DRPCInvocationsClient invoke = new DRPCInvocationsClient(conf, "localhost", server.getDrpcInvokePort())) {
                Future<String> found = DRPCServerTest.exec.submit(() -> client.getClient().execute("testing", "test"));
                DRPCRequest request = DRPCServerTest.getNextAvailableRequest(invoke, "testing");
                Assert.assertNotNull(request);
                Assert.assertEquals("test", request.get_func_args());
                Assert.assertNotNull(request.get_request_id());
                invoke.failRequest(request.get_request_id());
                try {
                    found.get(1000, TimeUnit.MILLISECONDS);
                    Assert.fail("exec did not throw an exception");
                } catch (ExecutionException e) {
                    Throwable t = e.getCause();
                    Assert.assertEquals(t.getClass(), DRPCExecutionException.class);
                    // Don't know a better way to validate that it failed.
                    Assert.assertEquals("Request failed", get_msg());
                }
            }
        }
    }

    @Test
    public void testGoodHttpGet() throws Exception {
        DRPCServerTest.LOG.info("STARTING HTTP GET TEST...");
        Map<String, Object> conf = getConf(0, 0, 0);
        try (DRPCServer server = new DRPCServer(conf, new StormMetricsRegistry())) {
            server.start();
            // TODO need a better way to do this
            Thread.sleep(2000);
            try (DRPCInvocationsClient invoke = new DRPCInvocationsClient(conf, "localhost", server.getDrpcInvokePort())) {
                final Future<String> found = DRPCServerTest.exec.submit(() -> DRPCServerTest.doGet(server.getHttpServerPort(), "testing", "test"));
                DRPCRequest request = DRPCServerTest.getNextAvailableRequest(invoke, "testing");
                Assert.assertNotNull(request);
                Assert.assertEquals("test", request.get_func_args());
                Assert.assertNotNull(request.get_request_id());
                invoke.result(request.get_request_id(), "tested");
                String result = found.get(1000, TimeUnit.MILLISECONDS);
                Assert.assertEquals("tested", result);
            }
        }
    }

    @Test
    public void testFailedHttpGet() throws Exception {
        DRPCServerTest.LOG.info("STARTING HTTP GET (FAIL) TEST...");
        Map<String, Object> conf = getConf(0, 0, 0);
        try (DRPCServer server = new DRPCServer(conf, new StormMetricsRegistry())) {
            server.start();
            // TODO need a better way to do this
            Thread.sleep(2000);
            try (DRPCInvocationsClient invoke = new DRPCInvocationsClient(conf, "localhost", server.getDrpcInvokePort())) {
                Future<String> found = DRPCServerTest.exec.submit(() -> DRPCServerTest.doGet(server.getHttpServerPort(), "testing", "test"));
                DRPCRequest request = DRPCServerTest.getNextAvailableRequest(invoke, "testing");
                Assert.assertNotNull(request);
                Assert.assertEquals("test", request.get_func_args());
                Assert.assertNotNull(request.get_request_id());
                invoke.getClient().failRequest(request.get_request_id());
                try {
                    found.get(1000, TimeUnit.MILLISECONDS);
                    Assert.fail("exec did not throw an exception");
                } catch (ExecutionException e) {
                    DRPCServerTest.LOG.warn("Got Expected Exception", e);
                    // Getting the exact response code is a bit more complex.
                    // TODO should use a better client
                }
            }
        }
    }
}

