/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.examples.sse.jersey;


import App.ROOT_PATH;
import Response.Status.CREATED;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * SSE example resources test.
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class ServerSentEventsTest extends JerseyTest {
    // TODO - due to JdkConnector migration this was radically reduced. It deadlocks with 25 clients, find out why!
    private static final int MAX_CLIENTS = 10;

    /**
     * Test consuming a single SSE event via event source.
     *
     * @throws Exception
     * 		in case of a failure during the test execution.
     */
    @Test
    public void testEventSource() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> message = new AtomicReference<String>();
        final EventSource eventSource = new EventSource(target().path(ROOT_PATH)) {
            @Override
            public void onEvent(InboundEvent inboundEvent) {
                try {
                    final String value = inboundEvent.readData();
                    message.set(value);
                    latch.countDown();
                } catch (ProcessingException e) {
                    e.printStackTrace();
                }
            }
        };
        target().path(ROOT_PATH).request().post(Entity.text("message"));
        try {
            Assert.assertTrue("Waiting for message to be delivered has timed out.", latch.await((5 * (getAsyncTimeoutMultiplier())), TimeUnit.SECONDS));
        } finally {
            eventSource.close();
        }
        Assert.assertThat("Unexpected SSE event data value.", message.get(), CoreMatchers.equalTo("message"));
    }

    /**
     * Test consuming multiple SSE events sequentially using event input.
     *
     * @throws Exception
     * 		in case of a failure during the test execution.
     */
    @Test
    public void testInboundEventReader() throws Exception {
        final int MAX_MESSAGES = 5;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            final Future<List<String>> futureMessages = executor.submit(new Callable<List<String>>() {
                @Override
                public List<String> call() throws Exception {
                    final EventInput eventInput = target(ROOT_PATH).register(SseFeature.class).request().get(EventInput.class);
                    startLatch.countDown();
                    final List<String> messages = new ArrayList<String>(MAX_MESSAGES);
                    try {
                        for (int i = 0; i < MAX_MESSAGES; i++) {
                            InboundEvent event = eventInput.read();
                            messages.add(event.readData());
                        }
                    } finally {
                        if (eventInput != null) {
                            eventInput.close();
                        }
                    }
                    return messages;
                }
            });
            Assert.assertTrue("Waiting for receiver thread to start has timed out.", startLatch.await(5, TimeUnit.SECONDS));
            for (int i = 0; i < MAX_MESSAGES; i++) {
                target(ROOT_PATH).request().post(Entity.text(("message " + i)));
            }
            int i = 0;
            for (String message : futureMessages.get(5000, TimeUnit.SECONDS)) {
                Assert.assertThat("Unexpected SSE event data value.", message, CoreMatchers.equalTo(("message " + (i++))));
            }
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Test receiving all streamed messages in parallel by multiple event sources.
     *
     * @throws Exception
     * 		in case of a failure during the test execution.
     */
    @Test
    public void testCreateDomain() throws Exception {
        final int MESSAGE_COUNT = 6;
        final Response response = target().path("domain/start").queryParam("testSources", ServerSentEventsTest.MAX_CLIENTS).request().post(Entity.text("data"), Response.class);
        Assert.assertThat("Unexpected start domain response status code.", response.getStatus(), CoreMatchers.equalTo(CREATED.getStatusCode()));
        final Map<Integer, Integer> messageCounts = new ConcurrentHashMap<Integer, Integer>(ServerSentEventsTest.MAX_CLIENTS);
        final CountDownLatch doneLatch = new CountDownLatch(ServerSentEventsTest.MAX_CLIENTS);
        final EventSource[] sources = new EventSource[ServerSentEventsTest.MAX_CLIENTS];
        final String processUriString = target().getUri().relativize(response.getLocation()).toString();
        final WebTarget sseTarget = target().path(processUriString).queryParam("testSource", "true");
        for (int i = 0; i < (ServerSentEventsTest.MAX_CLIENTS); i++) {
            final int id = i;
            sources[id] = EventSource.target(sseTarget).build();
            sources[id].register(new EventListener() {
                private final AtomicInteger messageCount = new AtomicInteger(0);

                @Override
                public void onEvent(InboundEvent inboundEvent) {
                    messageCount.incrementAndGet();
                    final String message = inboundEvent.readData(String.class);
                    if ("done".equals(message)) {
                        messageCounts.put(id, messageCount.get());
                        doneLatch.countDown();
                    }
                }
            });
            sources[i].open();
        }
        doneLatch.await((5 * (getAsyncTimeoutMultiplier())), TimeUnit.SECONDS);
        for (EventSource source : sources) {
            source.close();
        }
        for (int i = 0; i < (ServerSentEventsTest.MAX_CLIENTS); i++) {
            final Integer count = messageCounts.get(i);
            Assert.assertThat(("Final message not received by event source " + i), count, CoreMatchers.notNullValue());
            Assert.assertThat(("Unexpected number of messages received by event source " + i), count, CoreMatchers.equalTo(MESSAGE_COUNT));
        }
    }
}

