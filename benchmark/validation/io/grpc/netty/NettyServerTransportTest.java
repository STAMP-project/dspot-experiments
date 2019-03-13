/**
 * Copyright 2017 The gRPC Authors
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
package io.grpc.netty;


import java.io.IOException;
import java.util.logging.Level;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class NettyServerTransportTest {
    @Test
    public void unknownException() {
        Assert.assertEquals(Level.INFO, NettyServerTransport.getLogLevel(new Exception()));
    }

    @Test
    public void quiet() {
        Assert.assertEquals(Level.FINE, NettyServerTransport.getLogLevel(new IOException("Connection reset by peer")));
        Assert.assertEquals(Level.FINE, NettyServerTransport.getLogLevel(new IOException("An existing connection was forcibly closed by the remote host")));
    }

    @Test
    public void nonquiet() {
        Assert.assertEquals(Level.INFO, NettyServerTransport.getLogLevel(new IOException("foo")));
    }

    @Test
    public void nullMessage() {
        IOException e = new IOException();
        Assert.assertNull(e.getMessage());
        Assert.assertEquals(Level.INFO, NettyServerTransport.getLogLevel(e));
    }
}

