/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.examples.server.async;


import App.ASYNC_LONG_RUNNING_OP_PATH;
import App.ASYNC_MESSAGING_BLOCKING_PATH;
import App.ASYNC_MESSAGING_FIRE_N_FORGET_PATH;
import FireAndForgetChatResource.POST_NOTIFICATION_RESPONSE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.internal.guava.ThreadFactoryBuilder;
import org.glassfish.jersey.process.JerseyProcessingUncaughtExceptionHandler;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import static SimpleLongRunningResource.NOTIFICATION_RESPONSE;


public class AsyncResourceTest extends JerseyTest {
    private static final Logger LOGGER = Logger.getLogger(AsyncResourceTest.class.getName());

    @Test
    public void testFireAndForgetChatResource() throws InterruptedException {
        executeChatTest(target().path(ASYNC_MESSAGING_FIRE_N_FORGET_PATH), POST_NOTIFICATION_RESPONSE);
    }

    @Test
    public void testBlockingPostChatResource() throws InterruptedException {
        executeChatTest(target().path(ASYNC_MESSAGING_BLOCKING_PATH), BlockingPostChatResource.POST_NOTIFICATION_RESPONSE);
    }

    @Test
    public void testLongRunningResource() throws InterruptedException {
        final WebTarget resourceTarget = target().path(ASYNC_LONG_RUNNING_OP_PATH);
        final String expectedResponse = NOTIFICATION_RESPONSE;
        final int MAX_MESSAGES = 100;
        final int LATCH_WAIT_TIMEOUT = 25 * (getAsyncTimeoutMultiplier());
        final boolean debugMode = false;
        final boolean sequentialGet = false;
        final Object sequentialGetLock = new Object();
        final ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("async-resource-test-%02d").setUncaughtExceptionHandler(new JerseyProcessingUncaughtExceptionHandler()).build());
        final Map<Integer, String> getResponses = new ConcurrentHashMap<Integer, String>();
        final CountDownLatch getRequestLatch = new CountDownLatch(MAX_MESSAGES);
        try {
            for (int i = 0; i < MAX_MESSAGES; i++) {
                final int requestId = i;
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        if (debugMode || sequentialGet) {
                            synchronized(sequentialGetLock) {
                                get();
                            }
                        } else {
                            get();
                        }
                    }

                    private void get() {
                        try {
                            int attemptCounter = 0;
                            while (true) {
                                attemptCounter++;
                                try {
                                    final String response = resourceTarget.request().get(String.class);
                                    getResponses.put(requestId, response);
                                    break;
                                } catch (Throwable t) {
                                    AsyncResourceTest.LOGGER.log(Level.SEVERE, String.format("Error sending GET request <%s> for %d. time.", requestId, attemptCounter), t);
                                }
                                if (attemptCounter > 3) {
                                    break;
                                }
                                Thread.sleep(10);
                            } 
                        } catch (InterruptedException ignored) {
                            AsyncResourceTest.LOGGER.log(Level.WARNING, String.format("Error sending GET message <%s>: Interrupted", requestId), ignored);
                        } finally {
                            getRequestLatch.countDown();
                        }
                    }
                });
            }
            if (debugMode) {
                getRequestLatch.await();
            } else {
                if (!(getRequestLatch.await(LATCH_WAIT_TIMEOUT, TimeUnit.SECONDS))) {
                    AsyncResourceTest.LOGGER.log(Level.SEVERE, "Waiting for all GET requests to complete has timed out.");
                }
            }
        } finally {
            executor.shutdownNow();
        }
        final ArrayList<Map.Entry<Integer, String>> responseEntryList = new ArrayList<Map.Entry<Integer, String>>(getResponses.entrySet());
        Collections.sort(responseEntryList, new Comparator<Map.Entry<Integer, String>>() {
            @Override
            public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        StringBuilder messageBuilder = new StringBuilder("GET responses received: ").append(responseEntryList.size()).append("\n");
        for (Map.Entry<Integer, String> getResponseEntry : responseEntryList) {
            messageBuilder.append(String.format("GET response for message %02d: ", getResponseEntry.getKey())).append(getResponseEntry.getValue()).append('\n');
        }
        AsyncResourceTest.LOGGER.info(messageBuilder.toString());
        for (Map.Entry<Integer, String> entry : responseEntryList) {
            Assert.assertEquals(String.format("Unexpected GET notification response for message %02d", entry.getKey()), expectedResponse, entry.getValue());
        }
        Assert.assertEquals(MAX_MESSAGES, getResponses.size());
    }
}

