/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.stream.camel;


import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ServiceStatus;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.LifecycleStrategySupport;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.IgniteException;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test class for {@link CamelStreamer}.
 */
public class IgniteCamelStreamerTest extends GridCommonAbstractTest {
    /**
     * text/plain media type.
     */
    private static final MediaType TEXT_PLAIN = MediaType.parse("text/plain;charset=utf-8");

    /**
     * The test data.
     */
    private static final Map<Integer, String> TEST_DATA = new HashMap<>();

    /**
     * The Camel streamer currently under test.
     */
    private CamelStreamer<Integer, String> streamer;

    /**
     * The Ignite data streamer.
     */
    private IgniteDataStreamer<Integer, String> dataStreamer;

    /**
     * URL where the REST service will be exposed.
     */
    private String url;

    /**
     * The UUID of the currently active remote listener.
     */
    private UUID remoteLsnr;

    /**
     * The OkHttpClient.
     */
    private OkHttpClient httpClient = new OkHttpClient();

    // Initialize the test data.
    static {
        for (int i = 0; i < 100; i++)
            IgniteCamelStreamerTest.TEST_DATA.put(i, ("v" + i));

    }

    /**
     * Constructor.
     */
    public IgniteCamelStreamerTest() {
        super(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSendOneEntryPerMessage() throws Exception {
        streamer.setSingleTupleExtractor(IgniteCamelStreamerTest.singleTupleExtractor());
        // Subscribe to cache PUT events.
        CountDownLatch latch = subscribeToPutEvents(50);
        // Action time.
        streamer.start();
        // Send messages.
        sendMessages(0, 50, false);
        // Assertions.
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMultipleEntriesInOneMessage() throws Exception {
        streamer.setMultipleTupleExtractor(IgniteCamelStreamerTest.multipleTupleExtractor());
        // Subscribe to cache PUT events.
        CountDownLatch latch = subscribeToPutEvents(50);
        // Action time.
        streamer.start();
        // Send messages.
        sendMessages(0, 50, true);
        // Assertions.
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testResponseProcessorIsCalled() throws Exception {
        streamer.setSingleTupleExtractor(IgniteCamelStreamerTest.singleTupleExtractor());
        streamer.setResponseProcessor(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getOut().setBody("Foo bar");
            }
        });
        // Subscribe to cache PUT events.
        CountDownLatch latch = subscribeToPutEvents(50);
        // Action time.
        streamer.start();
        // Send messages.
        List<String> responses = sendMessages(0, 50, false);
        for (String r : responses)
            assertEquals("Foo bar", r);

        // Assertions.
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUserSpecifiedCamelContext() throws Exception {
        final AtomicInteger cnt = new AtomicInteger();
        // Create a CamelContext with a probe that'll help us know if it has been used.
        CamelContext context = new DefaultCamelContext();
        context.setTracing(true);
        context.addLifecycleStrategy(new LifecycleStrategySupport() {
            @Override
            public void onEndpointAdd(Endpoint endpoint) {
                cnt.incrementAndGet();
            }
        });
        streamer.setSingleTupleExtractor(IgniteCamelStreamerTest.singleTupleExtractor());
        streamer.setCamelContext(context);
        // Subscribe to cache PUT events.
        CountDownLatch latch = subscribeToPutEvents(50);
        // Action time.
        streamer.start();
        // Send messages.
        sendMessages(0, 50, false);
        // Assertions.
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
        assertTrue(((cnt.get()) > 0));
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUserSpecifiedCamelContextWithPropertyPlaceholders() throws Exception {
        // Create a CamelContext with a custom property placeholder.
        CamelContext context = new DefaultCamelContext();
        PropertiesComponent pc = new PropertiesComponent("camel.test.properties");
        context.addComponent("properties", pc);
        // Replace the context path in the test URL with the property placeholder.
        url = url.replaceAll("/ignite", "{{test.contextPath}}");
        // Recreate the Camel streamer with the new URL.
        streamer = createCamelStreamer(dataStreamer);
        streamer.setSingleTupleExtractor(IgniteCamelStreamerTest.singleTupleExtractor());
        streamer.setCamelContext(context);
        // Subscribe to cache PUT events.
        CountDownLatch latch = subscribeToPutEvents(50);
        // Action time.
        streamer.start();
        // Before sending the messages, get the actual URL after the property placeholder was resolved,
        // stripping the jetty: prefix from it.
        url = streamer.getCamelContext().getEndpoints().iterator().next().getEndpointUri().replaceAll("jetty:", "");
        // Send messages.
        sendMessages(0, 50, false);
        // Assertions.
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertCacheEntriesLoaded(50);
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInvalidEndpointUri() throws Exception {
        streamer.setSingleTupleExtractor(IgniteCamelStreamerTest.singleTupleExtractor());
        streamer.setEndpointUri("abc");
        // Action time.
        try {
            streamer.start();
            fail("Streamer started; should have failed.");
        } catch (IgniteException ignored) {
            assertTrue(((streamer.getCamelContext().getStatus()) == (ServiceStatus.Stopped)));
            assertTrue(((streamer.getCamelContext().getEndpointRegistry().size()) == 0));
        }
    }
}

