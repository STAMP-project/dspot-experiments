/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.limit;


import Headers.HOST;
import Http2Client.BUFFER_POOL;
import Http2Client.RESPONSE_BODY;
import Http2Client.SSL;
import Http2Client.WORKER;
import Methods.GET;
import UndertowOptions.ENABLE_HTTP2;
import com.networknt.client.Http2Client;
import com.networknt.exception.ClientException;
import io.undertow.Undertow;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.IoUtils;
import org.xnio.OptionMap;


/**
 * Created by steve on 23/09/16.
 */
/* @Test
// For some reason, travis become really slow or not allow multi-thread anymore and this test fails always.
public void testMoreRequests() throws Exception {
Callable<String> task = this::callApi;
List<Callable<String>> tasks = Collections.nCopies(10, task);
long start = System.currentTimeMillis();
ExecutorService executorService = Executors.newFixedThreadPool(10);
List<Future<String>> futures = executorService.invokeAll(tasks);
List<String> resultList = new ArrayList<>(futures.size());
// Check for exceptions
for (Future<String> future : futures) {
// Throws an exception if an exception was thrown by the task.
String s = future.get();
logger.info("future = " + s);
resultList.add(s);
}
long last = (System.currentTimeMillis() - start);
// make sure that there are at least one element in resultList is :513
Assert.assertTrue(resultList.contains(":513"));
}
 */
public class LimitHandlerTest {
    static final Logger logger = LoggerFactory.getLogger(LimitHandlerTest.class);

    static Undertow server = null;

    @Test
    public void testOneRequest() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, OptionMap.create(ENABLE_HTTP2, true)).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            LimitHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        String body = reference.get().getAttachment(RESPONSE_BODY);
        Assert.assertEquals(200, statusCode);
        if (statusCode == 200) {
            Assert.assertEquals("OK", body);
        }
    }
}

