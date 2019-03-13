/**
 * Copyright (C) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
package com.google.mockwebserver;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;


public class CustomDispatcherTest extends TestCase {
    private MockWebServer mockWebServer = new MockWebServer();

    public void testSimpleDispatch() throws Exception {
        mockWebServer.play();
        final List<RecordedRequest> requestsMade = new ArrayList<RecordedRequest>();
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                requestsMade.add(request);
                return new MockResponse();
            }
        };
        TestCase.assertEquals(0, requestsMade.size());
        mockWebServer.setDispatcher(dispatcher);
        final URL url = mockWebServer.getUrl("/");
        final HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.getResponseCode();// Force the connection to hit the "server".

        // Make sure our dispatcher got the request.
        TestCase.assertEquals(1, requestsMade.size());
    }

    public void testOutOfOrderResponses() throws Exception {
        AtomicInteger firstResponseCode = new AtomicInteger();
        AtomicInteger secondResponseCode = new AtomicInteger();
        mockWebServer.play();
        final String secondRequest = "/bar";
        final String firstRequest = "/foo";
        final CountDownLatch latch = new CountDownLatch(1);
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                if (request.getPath().equals(firstRequest)) {
                    latch.await();
                }
                return new MockResponse();
            }
        };
        mockWebServer.setDispatcher(dispatcher);
        final Thread startsFirst = buildRequestThread(firstRequest, firstResponseCode);
        startsFirst.start();
        final Thread endsFirst = buildRequestThread(secondRequest, secondResponseCode);
        endsFirst.start();
        endsFirst.join();
        TestCase.assertEquals(0, firstResponseCode.get());// First response is still waiting.

        TestCase.assertEquals(200, secondResponseCode.get());// Second response is done.

        latch.countDown();
        startsFirst.join();
        TestCase.assertEquals(200, firstResponseCode.get());// And now it's done!

        TestCase.assertEquals(200, secondResponseCode.get());// (Still done).

    }
}

