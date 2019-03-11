/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix.contrib.metrics.controller;


import HystrixCommandGroupKey.Factory;
import com.netflix.hystrix.HystrixCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.Path;
import javax.ws.rs.ServiceUnavailableException;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author justinjose28
 */
@Path("/hystrix")
public class HystricsMetricsControllerTest extends JerseyTest {
    protected static final AtomicInteger requestCount = new AtomicInteger(0);

    @Test
    public void testInfiniteStream() throws Exception {
        executeHystrixCommand();// Execute a Hystrix command so that metrics are initialized.

        EventInput stream = getStream();// Invoke Stream API which returns a steady stream output.

        validateStream(stream, 1000);// Validate the stream.

        System.out.println("Validated Stream Output 1");
        executeHystrixCommand();// Execute Hystrix Command again so that request count is updated.

        validateStream(stream, 1000);// Stream should show updated request count

        System.out.println("Validated Stream Output 2");
        stream.close();
    }

    @Test
    public void testConcurrency() throws Exception {
        executeHystrixCommand();// Execute a Hystrix command so that metrics are initialized.

        List<EventInput> streamList = new ArrayList<EventInput>();
        // Fire 5 requests, validate their responses and hold these connections.
        for (int i = 0; i < 5; i++) {
            EventInput stream = getStream();
            System.out.println(("Received Response for Request#" + (i + 1)));
            streamList.add(stream);
            validateStream(stream, 1000);
            System.out.println(("Validated Response#" + (i + 1)));
        }
        // Sixth request should fail since max configured connection is 5.
        try {
            streamList.add(getStreamFailFast());
            Assert.fail("Expected 'ServiceUnavailableException' but, request went through.");
        } catch (ServiceUnavailableException e) {
            System.out.println("Got ServiceUnavailableException as expected.");
        }
        // Close one of the connections
        streamList.get(0).close();
        // Try again after closing one of the connections. This request should go through.
        EventInput eventInput = getStream();
        streamList.add(eventInput);
        validateStream(eventInput, 1000);
    }

    public static class TestHystrixCommand extends HystrixCommand<Void> {
        protected TestHystrixCommand() {
            super(Factory.asKey("test"));
        }

        @Override
        protected Void run() throws Exception {
            return null;
        }
    }
}

