/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.webserver;


import ServerConfiguration.DEFAULT_SOCKET_NAME;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * The NettyWebServerTest.
 */
public class NettyWebServerTest {
    private static final Logger LOGGER = Logger.getLogger(NettyWebServerTest.class.getName());

    @Test
    public void testShutdown() throws Exception {
        WebServer webServer = WebServer.create(NettyWebServerTest.routing(( bareRequest, bareResponse) -> {
        }));
        long startNanos = System.nanoTime();
        webServer.start().toCompletableFuture().get(10, TimeUnit.SECONDS);
        long shutdownStartNanos = System.nanoTime();
        webServer.shutdown().toCompletableFuture().get(10, TimeUnit.SECONDS);
        long endNanos = System.nanoTime();
        System.out.println((("Start took: " + (TimeUnit.MILLISECONDS.convert((shutdownStartNanos - startNanos), TimeUnit.NANOSECONDS))) + " ms."));
        System.out.println((("Shutdown took: " + (TimeUnit.MILLISECONDS.convert((endNanos - shutdownStartNanos), TimeUnit.NANOSECONDS))) + " ms."));
    }

    @Test
    public void testSinglePortsSuccessStart() throws Exception {
        WebServer webServer = WebServer.create(Routing.builder());
        webServer.start().toCompletableFuture().join();
        try {
            MatcherAssert.assertThat(webServer.port(), Matchers.greaterThan(0));
            MatcherAssert.assertThat(webServer.configuration().sockets().entrySet(), IsCollectionWithSize.hasSize(1));
            MatcherAssert.assertThat(webServer.configuration().sockets().get(DEFAULT_SOCKET_NAME).port(), Is.is(webServer.configuration().port()));
        } finally {
            webServer.shutdown().toCompletableFuture().join();
        }
    }

    @Test
    public void testMultiplePortsSuccessStart() throws Exception {
        WebServer webServer = WebServer.create(ServerConfiguration.builder().addSocket("1", ((SocketConfiguration) (null))).addSocket("2", ((SocketConfiguration) (null))).addSocket("3", ((SocketConfiguration) (null))).addSocket("4", ((SocketConfiguration) (null))), Routing.builder());
        webServer.start().toCompletableFuture().join();
        try {
            MatcherAssert.assertThat(webServer.port(), Matchers.greaterThan(0));
            MatcherAssert.assertThat(webServer.port("1"), AllOf.allOf(Matchers.greaterThan(0), IsNot.not(webServer.port())));
            MatcherAssert.assertThat(webServer.port("2"), AllOf.allOf(Matchers.greaterThan(0), IsNot.not(webServer.port()), IsNot.not(webServer.port("1"))));
            MatcherAssert.assertThat(webServer.port("3"), AllOf.allOf(Matchers.greaterThan(0), IsNot.not(webServer.port()), IsNot.not(webServer.port("1")), IsNot.not(webServer.port("2"))));
            MatcherAssert.assertThat(webServer.port("4"), AllOf.allOf(Matchers.greaterThan(0), IsNot.not(webServer.port()), IsNot.not(webServer.port("1")), IsNot.not(webServer.port("2")), IsNot.not(webServer.port("3"))));
        } finally {
            webServer.shutdown().toCompletableFuture().join();
        }
    }

    @Test
    public void testMultiplePortsAllTheSame() throws Exception {
        int samePort = 9999;
        WebServer webServer = WebServer.create(ServerConfiguration.builder().port(samePort).addSocket("third", SocketConfiguration.builder().port(samePort)), Routing.builder());
        assertStartFailure(webServer);
    }

    @Test
    public void testManyPortsButTwoTheSame() throws Exception {
        int samePort = 9999;
        WebServer webServer = WebServer.create(ServerConfiguration.builder().port(samePort).addSocket("1", ((SocketConfiguration) (null))).addSocket("2", SocketConfiguration.builder().port(samePort)).addSocket("3", ((SocketConfiguration) (null))).addSocket("4", ((SocketConfiguration) (null))).addSocket("5", ((SocketConfiguration) (null))).addSocket("6", ((SocketConfiguration) (null))), Routing.builder());
        assertStartFailure(webServer);
    }

    @Test
    public void unpairedRoutingCausesAFailure() throws Exception {
        try {
            WebServer webServer = WebServer.builder(Routing.builder()).config(ServerConfiguration.builder().addSocket("matched", SocketConfiguration.builder())).addNamedRouting("unmatched-first", Routing.builder()).addNamedRouting("matched", Routing.builder()).addNamedRouting("unmatched-second", Routing.builder()).build();
            Assertions.fail(("Should have thrown an exception: " + webServer));
        } catch (IllegalStateException e) {
            MatcherAssert.assertThat(e.getMessage(), AllOf.allOf(StringContains.containsString("unmatched-first"), StringContains.containsString("unmatched-second")));
        }
    }

    @Test
    public void additionalPairedRoutingsDoWork() throws Exception {
        WebServer webServer = WebServer.builder(Routing.builder()).config(ServerConfiguration.builder().addSocket("matched", SocketConfiguration.builder())).addNamedRouting("matched", Routing.builder()).build();
        MatcherAssert.assertThat(webServer.configuration().socket("matched"), CoreMatchers.notNullValue());
    }
}

