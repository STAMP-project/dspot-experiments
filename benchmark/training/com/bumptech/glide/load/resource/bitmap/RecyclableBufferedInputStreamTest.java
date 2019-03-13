package com.bumptech.glide.load.resource.bitmap;


import RecyclableBufferedInputStream.InvalidMarkException;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// Not required in tests.
@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class RecyclableBufferedInputStreamTest {
    private static final int DATA_SIZE = 30;

    private static final int BUFFER_SIZE = 10;

    private RecyclableBufferedInputStream stream;

    private byte[] data;

    private ArrayPool byteArrayPool;

    @Test
    public void testReturnsTrueForMarkSupported() {
        Assert.assertTrue(stream.markSupported());
    }

    @Test
    public void testCanReadIndividualBytes() throws IOException {
        for (int i = 0; i < (data.length); i++) {
            Assert.assertEquals(i, stream.read());
        }
        Assert.assertEquals((-1), stream.read());
    }

    @Test
    public void testCanReadBytesInBulkLargerThanBufferSize() throws IOException {
        byte[] buffer = new byte[RecyclableBufferedInputStreamTest.DATA_SIZE];
        Assert.assertEquals(RecyclableBufferedInputStreamTest.DATA_SIZE, stream.read(buffer, 0, RecyclableBufferedInputStreamTest.DATA_SIZE));
        for (int i = 0; i < (RecyclableBufferedInputStreamTest.DATA_SIZE); i++) {
            Assert.assertEquals(i, buffer[i]);
        }
    }

    @Test
    public void testCanReadBytesInBulkSmallerThanBufferSize() throws IOException {
        int toRead = (RecyclableBufferedInputStreamTest.BUFFER_SIZE) / 2;
        byte[] buffer = new byte[toRead];
        Assert.assertEquals(toRead, stream.read(buffer, 0, toRead));
        for (int i = 0; i < toRead; i++) {
            Assert.assertEquals(i, buffer[i]);
        }
    }

    @Test
    public void testReadingZeroBytesIntoBufferReadsZeroBytes() throws IOException {
        // Make sure the next value is not 0.
        stream.read();
        byte[] buffer = new byte[RecyclableBufferedInputStreamTest.BUFFER_SIZE];
        Assert.assertEquals(0, stream.read(buffer, 0, 0));
        for (int i = 0; i < (RecyclableBufferedInputStreamTest.BUFFER_SIZE); i++) {
            Assert.assertEquals(0, buffer[i]);
        }
    }

    @Test
    public void testCanReadIntoBufferLargerThanDataSize() throws IOException {
        int toRead = (RecyclableBufferedInputStreamTest.DATA_SIZE) * 2;
        byte[] buffer = new byte[toRead];
        Assert.assertEquals(RecyclableBufferedInputStreamTest.DATA_SIZE, stream.read(buffer, 0, toRead));
        for (int i = 0; i < (RecyclableBufferedInputStreamTest.DATA_SIZE); i++) {
            Assert.assertEquals(i, buffer[i]);
        }
        for (int i = RecyclableBufferedInputStreamTest.DATA_SIZE; i < toRead; i++) {
            Assert.assertEquals(0, buffer[i]);
        }
    }

    @Test
    public void testCanReadBytesInBulkWithLimit() throws IOException {
        int toRead = (RecyclableBufferedInputStreamTest.BUFFER_SIZE) / 2;
        byte[] buffer = new byte[RecyclableBufferedInputStreamTest.BUFFER_SIZE];
        Assert.assertEquals(toRead, stream.read(buffer, 0, toRead));
        // 0, 1, 2, 3, 4, 0, 0, 0, 0, 0
        for (int i = 0; i < toRead; i++) {
            Assert.assertEquals(i, buffer[i]);
        }
        for (int i = toRead; i < (RecyclableBufferedInputStreamTest.BUFFER_SIZE); i++) {
            Assert.assertEquals(0, buffer[i]);
        }
    }

    @Test
    public void testCanReadBytesInBulkWithOffset() throws IOException {
        int toRead = (RecyclableBufferedInputStreamTest.BUFFER_SIZE) / 2;
        byte[] buffer = new byte[RecyclableBufferedInputStreamTest.BUFFER_SIZE];
        Assert.assertEquals(toRead, stream.read(buffer, ((RecyclableBufferedInputStreamTest.BUFFER_SIZE) - toRead), toRead));
        // 0, 0, 0, 0, 0, 0, 1, 2, 3, 4
        for (int i = 0; i < toRead; i++) {
            Assert.assertEquals(0, buffer[i]);
        }
        for (int i = toRead; i < (RecyclableBufferedInputStreamTest.BUFFER_SIZE); i++) {
            Assert.assertEquals((i - toRead), buffer[i]);
        }
    }

    @Test
    public void testCanReadBytesInBulkWhenSomeButNotAllBytesAreInBuffer() throws IOException {
        stream.read();
        byte[] buffer = new byte[RecyclableBufferedInputStreamTest.BUFFER_SIZE];
        Assert.assertEquals(RecyclableBufferedInputStreamTest.BUFFER_SIZE, stream.read(buffer, 0, RecyclableBufferedInputStreamTest.BUFFER_SIZE));
        for (int i = 1; i < ((RecyclableBufferedInputStreamTest.BUFFER_SIZE) + 1); i++) {
            Assert.assertEquals(i, buffer[(i - 1)]);
        }
    }

    @Test
    public void testCanSkipBytes() throws IOException {
        int toSkip = (data.length) / 2;
        Assert.assertEquals(toSkip, stream.skip(toSkip));
        for (int i = toSkip; i < (data.length); i++) {
            Assert.assertEquals(i, stream.read());
        }
        Assert.assertEquals((-1), stream.read());
    }

    @Test
    public void testSkipReturnsZeroIfSkipByteCountIsZero() throws IOException {
        Assert.assertEquals(0, stream.skip(0));
        Assert.assertEquals(0, stream.read());
    }

    @Test
    public void testSkipReturnsZeroIfSkipByteCountIsNegative() throws IOException {
        Assert.assertEquals(0, stream.skip((-13)));
        Assert.assertEquals(0, stream.read());
    }

    @Test
    public void testCloseClosesWrappedStream() throws IOException {
        InputStream wrapped = Mockito.mock(InputStream.class);
        stream = new RecyclableBufferedInputStream(wrapped, byteArrayPool);
        stream.close();
        Mockito.verify(wrapped).close();
    }

    @Test
    public void testCanSafelyBeClosedMultipleTimes() throws IOException {
        InputStream wrapped = Mockito.mock(InputStream.class);
        stream = new RecyclableBufferedInputStream(wrapped, byteArrayPool);
        stream.close();
        stream.close();
        stream.close();
        Mockito.verify(wrapped, Mockito.times(1)).close();
    }

    @Test
    public void testCanMarkAndReset() throws IOException {
        byte[] buffer = new byte[RecyclableBufferedInputStreamTest.BUFFER_SIZE];
        stream.mark(RecyclableBufferedInputStreamTest.BUFFER_SIZE);
        Assert.assertEquals(RecyclableBufferedInputStreamTest.BUFFER_SIZE, stream.read(buffer, 0, RecyclableBufferedInputStreamTest.BUFFER_SIZE));
        for (int i = 0; i < (RecyclableBufferedInputStreamTest.BUFFER_SIZE); i++) {
            Assert.assertEquals(i, buffer[i]);
        }
        Arrays.fill(buffer, ((byte) (0)));
        stream.reset();
        Assert.assertEquals(RecyclableBufferedInputStreamTest.BUFFER_SIZE, stream.read(buffer, 0, RecyclableBufferedInputStreamTest.BUFFER_SIZE));
        for (int i = 0; i < (RecyclableBufferedInputStreamTest.BUFFER_SIZE); i++) {
            Assert.assertEquals(i, buffer[i]);
        }
    }

    @Test
    public void testCanResetRepeatedlyAfterMarking() throws IOException {
        byte[] buffer = new byte[RecyclableBufferedInputStreamTest.BUFFER_SIZE];
        stream.mark(RecyclableBufferedInputStreamTest.BUFFER_SIZE);
        for (int repeat = 0; repeat < 10; repeat++) {
            Assert.assertEquals(RecyclableBufferedInputStreamTest.BUFFER_SIZE, stream.read(buffer, 0, RecyclableBufferedInputStreamTest.BUFFER_SIZE));
            for (int i = 0; i < (RecyclableBufferedInputStreamTest.BUFFER_SIZE); i++) {
                Assert.assertEquals(i, buffer[i]);
            }
            stream.reset();
        }
    }

    @Test
    public void testCanMarkInMiddleOfBufferAndStillReadUpToBufferLengthBeforeResetting() throws IOException {
        int markPos = (RecyclableBufferedInputStreamTest.BUFFER_SIZE) / 2;
        for (int i = 0; i < markPos; i++) {
            stream.read();
        }
        stream.mark(RecyclableBufferedInputStreamTest.BUFFER_SIZE);
        for (int i = 0; i < (RecyclableBufferedInputStreamTest.BUFFER_SIZE); i++) {
            stream.read();
        }
        stream.reset();
        Assert.assertEquals(markPos, stream.read());
    }

    @Test
    public void testAvailableReturnsWrappedAvailableIfNoBytesRead() throws IOException {
        Assert.assertEquals(RecyclableBufferedInputStreamTest.DATA_SIZE, stream.available());
    }

    @Test
    public void testAvailableIncludesDataInBufferAndWrappedAvailableIfBytesRead() throws IOException {
        stream.read();
        Assert.assertEquals(((RecyclableBufferedInputStreamTest.DATA_SIZE) - 1), stream.available());
    }

    @Test(expected = IOException.class)
    public void testCloseThrowsIfWrappedStreamThrowsOnClose() throws IOException {
        InputStream wrapped = Mockito.mock(InputStream.class);
        Mockito.doThrow(new IOException()).when(wrapped).close();
        stream = new RecyclableBufferedInputStream(wrapped, byteArrayPool);
        stream.close();
    }

    @Test(expected = IOException.class)
    public void testAvailableThrowsIfStreamIsClosed() throws IOException {
        stream.close();
        stream.available();
    }

    @Test(expected = IOException.class)
    public void testReadThrowsIfStreamIsClosed() throws IOException {
        stream.close();
        stream.read();
    }

    @Test(expected = IOException.class)
    public void testReadBulkThrowsIfStreamIsClosed() throws IOException {
        stream.close();
        stream.read(new byte[1], 0, 1);
    }

    @Test(expected = IOException.class)
    public void testResetThrowsIfStreamIsClosed() throws IOException {
        stream.close();
        stream.reset();
    }

    @Test(expected = IOException.class)
    public void testSkipThrowsIfStreamIsClosed() throws IOException {
        stream.close();
        stream.skip(10);
    }

    @Test(expected = InvalidMarkException.class)
    public void testResetThrowsIfMarkNotSet() throws IOException {
        stream.reset();
    }

    @Test(expected = InvalidMarkException.class)
    public void testResetThrowsIfMarkIsInvalid() throws IOException {
        stream.mark(1);
        stream.read(new byte[RecyclableBufferedInputStreamTest.BUFFER_SIZE], 0, RecyclableBufferedInputStreamTest.BUFFER_SIZE);
        stream.read();
        stream.reset();
    }
}

