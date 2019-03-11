package com.github.dockerjava.netty.handler;


import com.github.dockerjava.core.async.ResultCallbackTemplate;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Alexander Koshevoy
 */
public class HttpResponseStreamHandlerTest {
    @Test
    public void testNoBytesSkipped() throws Exception {
        HttpResponseStreamHandlerTest.ResultCallbackTest callback = new HttpResponseStreamHandlerTest.ResultCallbackTest();
        HttpResponseStreamHandler streamHandler = new HttpResponseStreamHandler(callback);
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
        ByteBuf buffer = generateByteBuf();
        ByteBuf readBuffer = buffer.copy();
        Assert.assertEquals(buffer.refCnt(), 1);
        streamHandler.channelRead(ctx, buffer);
        streamHandler.channelInactive(ctx);
        Assert.assertEquals(buffer.refCnt(), 0);
        try (InputStream inputStream = callback.getInputStream()) {
            Assert.assertTrue(IOUtils.contentEquals(inputStream, new io.netty.buffer.ByteBufInputStream(readBuffer)));
        }
        ReferenceCountUtil.release(readBuffer);
    }

    @Test
    public void testReadByteByByte() throws Exception {
        HttpResponseStreamHandlerTest.ResultCallbackTest callback = new HttpResponseStreamHandlerTest.ResultCallbackTest();
        HttpResponseStreamHandler streamHandler = new HttpResponseStreamHandler(callback);
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
        ByteBuf buffer = generateByteBuf();
        ByteBuf readBuffer = buffer.copy();
        Assert.assertEquals(buffer.refCnt(), 1);
        streamHandler.channelRead(ctx, buffer);
        streamHandler.channelInactive(ctx);
        Assert.assertEquals(buffer.refCnt(), 0);
        try (InputStream inputStream = callback.getInputStream()) {
            for (int i = 0; i < (readBuffer.readableBytes()); i++) {
                int b = inputStream.read();
                Assert.assertEquals(b, readBuffer.getByte(i));
            }
            Assert.assertTrue(((inputStream.read()) == (-1)));
        }
        ReferenceCountUtil.release(readBuffer);
    }

    @Test
    public void testCloseResponseStreamBeforeWrite() throws Exception {
        HttpResponseStreamHandler.HttpResponseInputStream inputStream = new HttpResponseStreamHandler.HttpResponseInputStream();
        ByteBuf buffer = generateByteBuf();
        inputStream.write(buffer);
        inputStream.close();
        inputStream.write(buffer);
    }

    @Test
    public void testCloseResponseStreamOnWrite() throws Exception {
        final HttpResponseStreamHandler.HttpResponseInputStream inputStream = new HttpResponseStreamHandler.HttpResponseInputStream();
        final ByteBuf buffer = generateByteBuf();
        final CountDownLatch firstWrite = new CountDownLatch(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> submit = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    inputStream.write(buffer);
                    firstWrite.countDown();
                    inputStream.write(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        firstWrite.await();
        Assert.assertTrue(((inputStream.available()) > 0));
        // second write should have started
        Thread.sleep(500L);
        inputStream.close();
        submit.get();
    }

    @Test(expected = IOException.class)
    public void testReadClosedResponseStream() throws Exception {
        HttpResponseStreamHandler.HttpResponseInputStream inputStream = new HttpResponseStreamHandler.HttpResponseInputStream();
        ByteBuf buffer = generateByteBuf();
        inputStream.write(buffer);
        inputStream.close();
        inputStream.read();
    }

    private static class ResultCallbackTest extends ResultCallbackTemplate<HttpResponseStreamHandlerTest.ResultCallbackTest, InputStream> {
        private InputStream stream;

        @Override
        public void onNext(InputStream stream) {
            this.stream = stream;
        }

        private InputStream getInputStream() {
            return stream;
        }
    }
}

