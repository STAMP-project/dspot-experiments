/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.server.rest;


import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
import org.apache.drill.exec.memory.BufferAllocator;
import org.apache.drill.exec.rpc.TransportCheck;
import org.apache.drill.exec.rpc.user.UserSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Validates {@link WebSessionResources} close works as expected w.r.t {@link io.netty.channel.AbstractChannel.CloseFuture}
 * associated with it.
 */
public class WebSessionResourcesTest {
    // private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WebSessionResourcesTest.class);
    private WebSessionResources webSessionResources;

    private boolean listenerComplete;

    private CountDownLatch latch;

    private EventExecutor executor;

    // A close listener added in close future in one of the test to see if it's invoked correctly.
    private class TestClosedListener implements GenericFutureListener<Future<Void>> {
        @Override
        public void operationComplete(Future<Void> future) throws Exception {
            listenerComplete = true;
            latch.countDown();
        }
    }

    /**
     * Validates {@link WebSessionResources#close()} throws NPE when closefuture passed to WebSessionResources doesn't
     * have a valid channel and EventExecutor associated with it.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testChannelPromiseWithNullExecutor() throws Exception {
        try {
            ChannelPromise closeFuture = new DefaultChannelPromise(null);
            webSessionResources = new WebSessionResources(Mockito.mock(BufferAllocator.class), Mockito.mock(SocketAddress.class), Mockito.mock(UserSession.class), closeFuture);
            webSessionResources.close();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NullPointerException));
            Mockito.verify(webSessionResources.getAllocator()).close();
            Mockito.verify(webSessionResources.getSession()).close();
        }
    }

    /**
     * Validates successful {@link WebSessionResources#close()} with valid CloseFuture and other parameters.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testChannelPromiseWithValidExecutor() throws Exception {
        try {
            EventExecutor mockExecutor = Mockito.mock(EventExecutor.class);
            ChannelPromise closeFuture = new DefaultChannelPromise(null, mockExecutor);
            webSessionResources = new WebSessionResources(Mockito.mock(BufferAllocator.class), Mockito.mock(SocketAddress.class), Mockito.mock(UserSession.class), closeFuture);
            webSessionResources.close();
            Mockito.verify(webSessionResources.getAllocator()).close();
            Mockito.verify(webSessionResources.getSession()).close();
            Mockito.verify(mockExecutor).inEventLoop();
            Mockito.verify(mockExecutor).execute(ArgumentMatchers.any(Runnable.class));
            Assert.assertTrue(((webSessionResources.getCloseFuture()) == null));
            Assert.assertTrue((!(listenerComplete)));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Validates double call to {@link WebSessionResources#close()} doesn't throw any exception.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDoubleClose() throws Exception {
        try {
            ChannelPromise closeFuture = new DefaultChannelPromise(null, Mockito.mock(EventExecutor.class));
            webSessionResources = new WebSessionResources(Mockito.mock(BufferAllocator.class), Mockito.mock(SocketAddress.class), Mockito.mock(UserSession.class), closeFuture);
            webSessionResources.close();
            Mockito.verify(webSessionResources.getAllocator()).close();
            Mockito.verify(webSessionResources.getSession()).close();
            Assert.assertTrue(((webSessionResources.getCloseFuture()) == null));
            webSessionResources.close();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Validates successful {@link WebSessionResources#close()} with valid CloseFuture and {@link TestClosedListener}
     * getting invoked which is added to the close future.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCloseWithListener() throws Exception {
        try {
            // Assign latch, executor and closeListener for this test case
            GenericFutureListener<Future<Void>> closeListener = new WebSessionResourcesTest.TestClosedListener();
            latch = new CountDownLatch(1);
            executor = TransportCheck.createEventLoopGroup(1, "Test-Thread").next();
            ChannelPromise closeFuture = new DefaultChannelPromise(null, executor);
            // create WebSessionResources with above ChannelPromise to notify listener
            webSessionResources = new WebSessionResources(Mockito.mock(BufferAllocator.class), Mockito.mock(SocketAddress.class), Mockito.mock(UserSession.class), closeFuture);
            // Add the Test Listener to close future
            Assert.assertTrue((!(listenerComplete)));
            closeFuture.addListener(closeListener);
            // Close the WebSessionResources
            webSessionResources.close();
            // Verify the states
            Mockito.verify(webSessionResources.getAllocator()).close();
            Mockito.verify(webSessionResources.getSession()).close();
            Assert.assertTrue(((webSessionResources.getCloseFuture()) == null));
            // Since listener will be invoked so test should not wait forever
            latch.await();
            Assert.assertTrue(listenerComplete);
        } catch (Exception e) {
            Assert.fail();
        } finally {
            listenerComplete = false;
            executor.shutdownGracefully();
        }
    }
}

