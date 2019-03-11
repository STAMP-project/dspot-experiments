/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.examples.sseitemstore.jaxrs;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import org.glassfish.jersey.internal.guava.ThreadFactoryBuilder;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Item store test.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class JaxrsItemStoreResourceTest extends JerseyTest {
    private static final int RECONNECT_DEFAULT = 500;

    private static final Logger LOGGER = Logger.getLogger(JaxrsItemStoreResourceTest.class.getName());

    private static final int MAX_LISTENERS = 5;

    private static final int MAX_ITEMS = 10;

    private final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("JaxrsItemStoreResourceTest-%d").build());

    private final AtomicReference<Client> client = new AtomicReference<>(null);

    /**
     * Test the item addition, addition event broadcasting and item retrieval from {@link JaxrsItemStoreResource}.
     *
     * @throws Exception
     * 		in case of a test failure.
     */
    @Test
    public void testItemsStore() throws Exception {
        final List<String> items = Collections.unmodifiableList(Arrays.asList("foo", "bar", "baz"));
        final WebTarget itemsTarget = target("items");
        final CountDownLatch latch = new CountDownLatch((((items.size()) * (JaxrsItemStoreResourceTest.MAX_LISTENERS)) * 2));// countdown on all events

        final List<Queue<Integer>> indexQueues = new ArrayList<>(JaxrsItemStoreResourceTest.MAX_LISTENERS);
        final SseEventSource[] sources = new SseEventSource[JaxrsItemStoreResourceTest.MAX_LISTENERS];
        final AtomicInteger sizeEventsCount = new AtomicInteger(0);
        for (int i = 0; i < (JaxrsItemStoreResourceTest.MAX_LISTENERS); i++) {
            final int id = i;
            final SseEventSource es = SseEventSource.target(itemsTarget.path("events")).build();
            sources[id] = es;
            final Queue<Integer> indexes = new ConcurrentLinkedQueue<>();
            indexQueues.add(indexes);
            es.register(( inboundEvent) -> {
                try {
                    if (null == (inboundEvent.getName())) {
                        final String data = inboundEvent.readData();
                        LOGGER.info(((((("[-i-] SOURCE " + id) + ": Received event id=") + (inboundEvent.getId())) + " data=") + data));
                        indexes.add(items.indexOf(data));
                    } else
                        if ("size".equals(inboundEvent.getName())) {
                            sizeEventsCount.incrementAndGet();
                        }

                } catch ( ex) {
                    LOGGER.log(Level.SEVERE, (("[-x-] SOURCE " + id) + ": Error getting event data."), ex);
                    indexes.add((-999));
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            JaxrsItemStoreResourceTest.open(sources);
            items.forEach(( item) -> JaxrsItemStoreResourceTest.postItem(itemsTarget, item));
            Assert.assertTrue("Waiting to receive all events has timed out.", latch.await(((1000 + ((JaxrsItemStoreResourceTest.MAX_LISTENERS) * (JaxrsItemStoreResourceTest.RECONNECT_DEFAULT))) * (getAsyncTimeoutMultiplier())), TimeUnit.MILLISECONDS));
            // need to force disconnect on server in order for EventSource.close(...) to succeed with HttpUrlConnection
            JaxrsItemStoreResourceTest.sendCommand(itemsTarget, "disconnect");
        } finally {
            JaxrsItemStoreResourceTest.close(sources);
        }
        String postedItems = itemsTarget.request().get(String.class);
        items.forEach(( item) -> Assert.assertTrue((("Item '" + item) + "' not stored on server."), postedItems.contains(item)));
        final AtomicInteger queueId = new AtomicInteger(0);
        indexQueues.forEach(( indexes) -> {
            for (int i = 0; i < (items.size()); i++) {
                Assert.assertTrue(((("Event for '" + (items.get(i))) + "' not received in queue ") + (queueId.get())), indexes.contains(i));
            }
            Assert.assertEquals(("Not received the expected number of events in queue " + (queueId.get())), items.size(), indexes.size());
            queueId.incrementAndGet();
        });
        Assert.assertEquals("Number of received 'size' events does not match.", ((items.size()) * (JaxrsItemStoreResourceTest.MAX_LISTENERS)), sizeEventsCount.get());
    }

    /**
     * Test the {@link SseEventSource} reconnect feature.
     *
     * @throws Exception
     * 		in case of a test failure.
     */
    @Test
    public void testEventSourceReconnect() throws Exception {
        final WebTarget itemsTarget = target("items");
        final CountDownLatch latch = new CountDownLatch((((JaxrsItemStoreResourceTest.MAX_ITEMS) * (JaxrsItemStoreResourceTest.MAX_LISTENERS)) * 2));// countdown only on new item events

        final List<Queue<String>> receivedQueues = new ArrayList<>(JaxrsItemStoreResourceTest.MAX_LISTENERS);
        final SseEventSource[] sources = new SseEventSource[JaxrsItemStoreResourceTest.MAX_LISTENERS];
        for (int i = 0; i < (JaxrsItemStoreResourceTest.MAX_LISTENERS); i++) {
            final int id = i;
            final SseEventSource es = SseEventSource.target(itemsTarget.path("events")).reconnectingEvery(1, TimeUnit.MILLISECONDS).build();
            sources[id] = es;
            final Queue<String> received = new ConcurrentLinkedQueue<>();
            receivedQueues.add(received);
            es.register(( inboundEvent) -> {
                try {
                    if (null == (inboundEvent.getName())) {
                        final String data = inboundEvent.readData();
                        LOGGER.info(((((("[-i-] SOURCE " + id) + ": Received event id=") + (inboundEvent.getId())) + " data=") + data));
                        received.add(data);
                        latch.countDown();
                    }
                } catch ( ex) {
                    LOGGER.log(Level.SEVERE, (("[-x-] SOURCE " + id) + ": Error getting event data."), ex);
                    received.add("[data processing error]");
                }
            });
        }
        final String[] postedItems = new String[(JaxrsItemStoreResourceTest.MAX_ITEMS) * 2];
        try {
            JaxrsItemStoreResourceTest.open(sources);
            for (int i = 0; i < (JaxrsItemStoreResourceTest.MAX_ITEMS); i++) {
                final String item = String.format("round-1-%02d", i);
                JaxrsItemStoreResourceTest.postItem(itemsTarget, item);
                postedItems[i] = item;
                JaxrsItemStoreResourceTest.sendCommand(itemsTarget, "disconnect");
                Thread.sleep(200);
            }
            final int reconnectDelay = 1;
            JaxrsItemStoreResourceTest.sendCommand(itemsTarget, ("reconnect " + reconnectDelay));
            JaxrsItemStoreResourceTest.sendCommand(itemsTarget, "disconnect");
            Thread.sleep((reconnectDelay * 1000));
            for (int i = 0; i < (JaxrsItemStoreResourceTest.MAX_ITEMS); i++) {
                final String item = String.format("round-2-%02d", i);
                postedItems[(i + (JaxrsItemStoreResourceTest.MAX_ITEMS))] = item;
                JaxrsItemStoreResourceTest.postItem(itemsTarget, item);
            }
            JaxrsItemStoreResourceTest.sendCommand(itemsTarget, "reconnect now");
            Assert.assertTrue("Waiting to receive all events has timed out.", latch.await(((1 + (((JaxrsItemStoreResourceTest.MAX_LISTENERS) * ((JaxrsItemStoreResourceTest.MAX_ITEMS) + 1)) * reconnectDelay)) * (getAsyncTimeoutMultiplier())), TimeUnit.SECONDS));
            // need to force disconnect on server in order for EventSource.close(...) to succeed with HttpUrlConnection
            JaxrsItemStoreResourceTest.sendCommand(itemsTarget, "disconnect");
        } finally {
            JaxrsItemStoreResourceTest.close(sources);
        }
        final String storedItems = itemsTarget.request().get(String.class);
        for (String item : postedItems) {
            Assert.assertThat((("Posted item '" + item) + "' stored on server"), storedItems, CoreMatchers.containsString(item));
        }
        int sourceId = 0;
        for (Queue<String> queue : receivedQueues) {
            Assert.assertThat(("Received events in source " + sourceId), queue, CoreMatchers.describedAs("Collection containing %0", CoreMatchers.hasItems(postedItems), Arrays.asList(postedItems).toString()));
            Assert.assertThat(("Size of received queue for source " + sourceId), queue.size(), CoreMatchers.equalTo(postedItems.length));
            sourceId++;
        }
    }
}

