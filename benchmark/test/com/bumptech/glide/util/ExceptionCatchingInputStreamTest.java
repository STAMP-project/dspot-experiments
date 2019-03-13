package com.bumptech.glide.util;


import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ExceptionCatchingInputStreamTest {
    private RecyclableBufferedInputStream wrapped;

    private ExceptionCatchingInputStream is;

    @Test
    public void testReturnsWrappedAvailable() throws IOException {
        Mockito.when(wrapped.available()).thenReturn(25);
        Assert.assertEquals(25, is.available());
    }

    @Test
    public void testCallsCloseOnWrapped() throws IOException {
        is.close();
        Mockito.verify(wrapped).close();
    }

    @Test
    public void testCallsMarkOnWrapped() {
        int toMark = 50;
        is.mark(toMark);
        Mockito.verify(wrapped).mark(ArgumentMatchers.eq(toMark));
    }

    @Test
    public void testReturnsWrappedMarkSupported() {
        Mockito.when(wrapped.markSupported()).thenReturn(true);
        Assert.assertTrue(is.markSupported());
    }

    @Test
    public void testCallsReadByteArrayOnWrapped() throws IOException {
        byte[] buffer = new byte[100];
        Mockito.when(wrapped.read(ArgumentMatchers.eq(buffer))).thenReturn(buffer.length);
        Assert.assertEquals(buffer.length, is.read(buffer));
    }

    @Test
    public void testCallsReadArrayWithOffsetAndCountOnWrapped() throws IOException {
        int offset = 5;
        int count = 100;
        byte[] buffer = new byte[105];
        Mockito.when(wrapped.read(ArgumentMatchers.eq(buffer), ArgumentMatchers.eq(offset), ArgumentMatchers.eq(count))).thenReturn(count);
        Assert.assertEquals(count, is.read(buffer, offset, count));
    }

    @Test
    public void testCallsReadOnWrapped() throws IOException {
        Mockito.when(wrapped.read()).thenReturn(1);
        Assert.assertEquals(1, is.read());
    }

    @Test
    public void testCallsResetOnWrapped() throws IOException {
        is.reset();
        Mockito.verify(wrapped).reset();
    }

    @Test
    public void testCallsSkipOnWrapped() throws IOException {
        long toSkip = 67;
        long expected = 55;
        Mockito.when(wrapped.skip(ArgumentMatchers.eq(toSkip))).thenReturn(expected);
        Assert.assertEquals(expected, is.skip(toSkip));
    }

    @Test
    public void testCatchesExceptionOnRead() throws IOException {
        IOException expected = new SocketTimeoutException();
        Mockito.when(wrapped.read()).thenThrow(expected);
        int read = is.read();
        Assert.assertEquals((-1), read);
        Assert.assertEquals(expected, is.getException());
    }

    @Test
    public void testCatchesExceptionOnReadBuffer() throws IOException {
        IOException exception = new SocketTimeoutException();
        Mockito.when(wrapped.read(ArgumentMatchers.any(byte[].class))).thenThrow(exception);
        int read = is.read(new byte[0]);
        Assert.assertEquals((-1), read);
        Assert.assertEquals(exception, is.getException());
    }

    @Test
    public void testCatchesExceptionOnReadBufferWithOffsetAndCount() throws IOException {
        IOException exception = new SocketTimeoutException();
        Mockito.when(wrapped.read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenThrow(exception);
        int read = is.read(new byte[0], 10, 100);
        Assert.assertEquals((-1), read);
        Assert.assertEquals(exception, is.getException());
    }

    @Test
    public void testCatchesExceptionOnSkip() throws IOException {
        IOException exception = new SocketTimeoutException();
        Mockito.when(wrapped.skip(ArgumentMatchers.anyLong())).thenThrow(exception);
        long skipped = is.skip(100);
        Assert.assertEquals(0, skipped);
        Assert.assertEquals(exception, is.getException());
    }

    @Test
    public void testExceptionIsNotSetInitially() {
        Assert.assertNull(is.getException());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testResetsExceptionToNullOnRelease() throws IOException {
        IOException exception = new SocketTimeoutException();
        Mockito.when(wrapped.read()).thenThrow(exception);
        is.read();
        is.release();
        Assert.assertNull(is.getException());
    }

    @Test
    public void testCanReleaseAnObtainFromPool() {
        is.release();
        InputStream fromPool = ExceptionCatchingInputStream.obtain(wrapped);
        Assert.assertEquals(is, fromPool);
    }

    @Test
    public void testCanObtainNewStreamFromPool() throws IOException {
        InputStream fromPool = ExceptionCatchingInputStream.obtain(wrapped);
        Mockito.when(wrapped.read()).thenReturn(1);
        int read = fromPool.read();
        Assert.assertEquals(1, read);
    }
}

