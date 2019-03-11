/**
 * Copyright (c) 2014 Red Hat, Inc. and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.impl;


import ProxyType.SOCKS5;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ServerChannel;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.impl.transport.Transport;
import io.vertx.test.core.AsyncTestBase;
import org.junit.Test;


public class GlobalEventExecutorNotificationTest extends AsyncTestBase {
    private Vertx vertx;

    @Test
    public void testConnectError() {
        testConnectErrorNotifiesOnEventLoop(new NetClientOptions());
    }

    @Test
    public void testProxyConnectError() {
        testConnectErrorNotifiesOnEventLoop(new NetClientOptions().setProxyOptions(new ProxyOptions().setPort(1234).setType(SOCKS5).setHost("localhost")));
    }

    @Test
    public void testNetBindError() {
        RuntimeException cause = new RuntimeException();
        vertx = VertxImpl.vertx(new VertxOptions(), new Transport() {
            @Override
            public ChannelFactory<? extends ServerChannel> serverChannelFactory(boolean domainSocket) {
                return ((ChannelFactory<ServerChannel>) (() -> {
                    throw cause;
                }));
            }
        });
        vertx.createNetServer().connectHandler(( so) -> fail()).listen(1234, "localhost", onFailure(( err) -> {
            testComplete();
        }));
        await();
    }

    @Test
    public void testHttpBindError() {
        RuntimeException cause = new RuntimeException();
        vertx = VertxImpl.vertx(new VertxOptions(), new Transport() {
            @Override
            public ChannelFactory<? extends ServerChannel> serverChannelFactory(boolean domainSocket) {
                return ((ChannelFactory<ServerChannel>) (() -> {
                    throw cause;
                }));
            }
        });
        vertx.createHttpServer().requestHandler(( req) -> fail()).listen(8080, "localhost", onFailure(( err) -> {
            testComplete();
        }));
        await();
    }
}

