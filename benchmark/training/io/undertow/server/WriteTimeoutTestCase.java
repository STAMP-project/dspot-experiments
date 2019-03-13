/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server;


import Options.WRITE_TIMEOUT;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpOneOnly;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.WriteTimeoutException;


/**
 * Tests read timeout with a client that is slow to read the response
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
@HttpOneOnly
@Ignore("This test fails intermittently")
public class WriteTimeoutTestCase {
    private volatile Exception exception;

    private static final CountDownLatch errorLatch = new CountDownLatch(1);

    @Test
    public void testWriteTimeout() throws IOException, InterruptedException {
        DefaultServer.setRootHandler(new HttpHandler() {
            @Override
            public void handleRequest(final HttpServerExchange exchange) throws Exception {
                final StreamSinkChannel response = exchange.getResponseChannel();
                try {
                    response.setOption(WRITE_TIMEOUT, 10);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                final int capacity = (1 * 1024) * 1024;// 1mb

                final ByteBuffer originalBuffer = ByteBuffer.allocateDirect(capacity);
                for (int i = 0; i < capacity; ++i) {
                    originalBuffer.put(((byte) ('*')));
                }
                originalBuffer.flip();
                response.getWriteSetter().set(new org.xnio.ChannelListener<Channel>() {
                    private ByteBuffer buffer = originalBuffer.duplicate();

                    int count = 0;

                    @Override
                    public void handleEvent(final Channel channel) {
                        do {
                            try {
                                int res = response.write(buffer);
                                if (res == 0) {
                                    return;
                                }
                            } catch (IOException e) {
                                exception = e;
                                WriteTimeoutTestCase.errorLatch.countDown();
                            }
                            if (!(buffer.hasRemaining())) {
                                (count)++;
                                buffer = originalBuffer.duplicate();
                            }
                        } while ((count) < 1000 );
                        exchange.endExchange();
                    }
                });
                response.wakeupWrites();
            }
        });
        final TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(DefaultServer.getDefaultServerURL());
            try {
                HttpResponse result = client.execute(get);
                InputStream content = result.getEntity().getContent();
                byte[] buffer = new byte[512];
                int r = 0;
                while ((r = content.read(buffer)) > 0) {
                    Thread.sleep(200);
                    if ((exception) != null) {
                        Assert.assertEquals(WriteTimeoutException.class, exception.getClass());
                        return;
                    }
                } 
                Assert.fail("Write did not time out");
            } catch (IOException e) {
                if (WriteTimeoutTestCase.errorLatch.await(5, TimeUnit.SECONDS)) {
                    Assert.assertEquals(WriteTimeoutException.class, exception.getClass());
                } else {
                    Assert.fail("Write did not time out");
                }
            }
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

