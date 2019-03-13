package com.bumptech.glide.load.data;


import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class BufferedOutputStreamTest {
    @Mock
    private ArrayPool arrayPool;

    @Mock
    private OutputStream mockOutputStream;

    private final int bufferSize = 10;

    private final ByteArrayOutputStream inner = new ByteArrayOutputStream();

    private int currentValue = 0;

    private BufferedOutputStream os;

    @Test
    public void constructor_obtainsBufferFromArrayPool() {
        Mockito.verify(arrayPool).get(bufferSize, byte[].class);
    }

    @Test
    public void close_returnsBufferObtainedFromConstructor() throws IOException {
        byte[] data = new byte[bufferSize];
        Mockito.when(arrayPool.get(bufferSize, byte[].class)).thenReturn(data);
        os = new BufferedOutputStream(inner, arrayPool, bufferSize);
        os.close();
        Mockito.verify(arrayPool).put(data);
    }

    @Test
    public void write_withEmptyBuffer_andSingleByte_doesNotWriteToStream() throws IOException {
        os.write(next());
        assertThat(inner.toByteArray()).isEmpty();
    }

    @Test
    public void write_withEmptyBuffer_andDataSmallerThanBuffer_doesNotWriteToStream() throws IOException {
        os.write(next(((bufferSize) - 1)));
        assertThat(inner.toByteArray()).isEmpty();
    }

    @Test
    public void write_withEmptyBuffer_andDataWithOffsetSizeSmallerThanBuffer_doesNotWriteToStream() throws IOException {
        int offset = 1;
        int length = (bufferSize) - offset;
        byte[] data = nextWithOffset(offset, length);
        os.write(data, offset, length);
        assertThat(inner.toByteArray()).isEmpty();
    }

    @Test
    public void write_withEmptyBuffer_andDataWithPaddingSizeSmallerThanBuffer_doesNotWriteToStream() throws IOException {
        int padding = 1;
        int length = (bufferSize) - padding;
        byte[] data = nextWithPadding(length, padding);
        os.write(data, 0, length);
        assertThat(inner.toByteArray()).isEmpty();
    }

    @Test
    public void write_withEmptyBuffer_andDataEqualToBufferSize_writesDataToStream() throws IOException {
        os.write(next(bufferSize));
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withEmptyBuffer_andDataGreaterThanBufferSize_writesDataToStream() throws IOException {
        os.write(next(((bufferSize) + 1)));
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withEmptyBuffer_andDataWithOffsetAndLengthEqualToBufferSize_writesDataToStream() throws IOException {
        int offset = 5;
        int length = bufferSize;
        byte[] data = nextWithOffset(offset, length);
        os.write(data, offset, length);
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withEmptyBuffer_andDataWithPaddingAndLengthEqualToBufferSize_writesData() throws IOException {
        int padding = 5;
        int length = bufferSize;
        byte[] data = nextWithPadding(length, padding);
        os.write(data, 0, length);
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withEmptyBuffer_andDataWithOffsetAndLengthGreaterThanBuffer_writesDataToStream() throws IOException {
        int offset = 5;
        int length = (bufferSize) + 1;
        byte[] data = nextWithOffset(offset, length);
        os.write(data, offset, length);
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withEmptyBuffer_andDataWithPaddingAndLengthGreaterThanBuffer_writesData() throws IOException {
        int padding = 5;
        int length = (bufferSize) + 1;
        byte[] data = nextWithPadding(length, padding);
        os.write(data, 0, length);
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void writeSingleByte_whenBufferAlmostFull_writesBufferToStream() throws IOException {
        for (int i = 0; i < (bufferSize); i++) {
            os.write(next());
        }
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void flush_withSingleByteInBuffer_writesBufferToStream() throws IOException {
        os.write(next());
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void flush_afterWritingByteAfterBufferFull_writesByteToStream() throws IOException {
        for (int i = 0; i < (bufferSize); i++) {
            os.write(next());
        }
        os.write(next());
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void flushAfterPreviousFlush_withSingleByte_writesOnlySingleByte() throws IOException {
        os.write(next());
        os.flush();
        os.write(next());
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_withSingleByteInBuffer_writesBufferToStream() throws IOException {
        os.write(next());
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWritingByteAfterBufferFull_writesByteToStream() throws IOException {
        for (int i = 0; i < (bufferSize); i++) {
            os.write(next());
        }
        os.write(next());
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void closeAfterPreviousFlush_withSingleByte_writesOnlySingleByte() throws IOException {
        os.write(next());
        os.flush();
        os.write(next());
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withDataInBuffer_bufferLessThanRemaining_doesNotWriteToStream() throws IOException {
        os.write(next());
        os.write(next(((remaining()) - 1)));
        assertThat(inner.toByteArray()).isEmpty();
    }

    @Test
    public void flush_afterWriteWithDataInBuffer_bufferLessThanRemaining_writesToStream() throws IOException {
        os.write(next());
        byte[] data = next(((remaining()) - 1));
        os.write(data);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWriteWithDataInBuffer_bufferLessThanRemaining_writesToStream() throws IOException {
        os.write(next());
        byte[] data = next(remaining());
        os.write(data);
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withBufferEqualToRemaining_lessThanLength_writesToStream() throws IOException {
        os.write(next());
        os.write(next(remaining()));
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void flush_afterWriteBufferEqualToRemaining_doesNothing() throws IOException {
        os.write(next());
        os.write(next(remaining()));
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWriteBufferEqualToRemaining_doesNothing() throws IOException {
        os.write(next());
        os.write(next(remaining()));
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withOffsetBufferEqualToRemaining_lessThanLength_writesToStream() throws IOException {
        os.write(next());
        int offset = 5;
        int length = remaining();
        os.write(nextWithOffset(offset, length), offset, length);
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void flush_afterWriteOffsetBufferEqualToRemaining_lessThanLength_writesToStream() throws IOException {
        os.write(next());
        int offset = 5;
        int length = remaining();
        os.write(nextWithOffset(offset, length), offset, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWriteOffsetBufferEqualToRemaining_lessThanLength_writesToStream() throws IOException {
        os.write(next());
        int offset = 5;
        int length = remaining();
        os.write(nextWithOffset(offset, length), offset, length);
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withPaddedBufferEqualToRemaining_lessThanLength_writesToStream() throws IOException {
        os.write(next());
        int padding = 5;
        int length = remaining();
        os.write(nextWithPadding(length, padding), 0, length);
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void flush_afterWritePaddedBufferEqualToRemaining_lessThanLength_writesToStream() throws IOException {
        os.write(next());
        int padding = 5;
        int length = remaining();
        os.write(nextWithPadding(length, padding), 0, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWritePaddedBufferEqualToRemaining_lessThanLength_writesToStream() throws IOException {
        os.write(next());
        int padding = 5;
        int length = remaining();
        os.write(nextWithPadding(length, padding), 0, length);
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withBufferGreaterThanRemaining_lessThanLength_writesUpToBufferToStream() throws IOException {
        os.write(next(2));
        os.write(next(((bufferSize) - 1)));
        assertThat(inner.toByteArray()).isEqualTo(upTo(bufferSize));
    }

    @Test
    public void flush_afterWriteBufferGreaterThanRemaining_lessThanLength_writesAll() throws IOException {
        os.write(next(2));
        os.write(next(((bufferSize) - 1)));
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWriteBufferGreaterThanRemaining_lessThanLength_writesAll() throws IOException {
        os.write(next(2));
        os.write(next(((bufferSize) - 1)));
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withOffsetBufferGreaterThanRemaining_lessThanLength_writesUpToBuffer() throws IOException {
        os.write(next(2));
        int offset = 5;
        int length = (bufferSize) - 1;
        os.write(nextWithOffset(offset, length), offset, length);
        assertThat(inner.toByteArray()).isEqualTo(upTo(bufferSize));
    }

    @Test
    public void flush_afterWriteOffsetBufferGreaterThanRemaining_lessThanLength_writesAll() throws IOException {
        os.write(next(2));
        int offset = 5;
        int length = (bufferSize) - 1;
        os.write(nextWithOffset(offset, length), offset, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWriteOffsetBufferGreaterThanRemaining_lessThanLength_writesAll() throws IOException {
        os.write(next(2));
        int offset = 5;
        int length = (bufferSize) - 1;
        os.write(nextWithOffset(offset, length), offset, length);
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withPaddedBufferGreaterThanRemaining_lessThanLength_writesUpToBuffer() throws IOException {
        os.write(next(2));
        int padding = 5;
        int length = (bufferSize) - 1;
        os.write(nextWithPadding(length, padding), 0, length);
        assertThat(inner.toByteArray()).isEqualTo(upTo(bufferSize));
    }

    @Test
    public void flush_afterWritePaddedBufferGreaterThanRemaining_lessThanLength_writesAll() throws IOException {
        os.write(next(2));
        int padding = 5;
        int length = (bufferSize) - 1;
        os.write(nextWithPadding(length, padding), 0, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWritePaddedBufferGreaterThanRemaining_lessThanLength_writesAll() throws IOException {
        os.write(next(2));
        int padding = 5;
        int length = (bufferSize) - 1;
        os.write(nextWithPadding(length, padding), 0, length);
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withBufferGreaterThanRemaining_equalToLength_writesUpToBufferToStream() throws IOException {
        os.write(next(2));
        os.write(next(bufferSize));
        assertThat(inner.toByteArray()).isEqualTo(upTo(bufferSize));
    }

    @Test
    public void flush_afterWriteBufferGreaterThanRemaining_equalToLength_writesAll() throws IOException {
        os.write(next(2));
        os.write(next(bufferSize));
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWriteBufferGreaterThanRemaining_equalToLength_writesAll() throws IOException {
        os.write(next(2));
        os.write(next(bufferSize));
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withOffsetBufferGreaterThanRemaining_equalToLength_writesUpToBufferToStream() throws IOException {
        os.write(next(2));
        int offset = 6;
        int length = bufferSize;
        os.write(nextWithOffset(offset, length), offset, length);
        assertThat(inner.toByteArray()).isEqualTo(upTo(bufferSize));
    }

    @Test
    public void flush_afterWriteOffsetBufferGreaterThanRemaining_equalToLength_writesAll() throws IOException {
        os.write(next(2));
        int offset = 6;
        int length = bufferSize;
        os.write(nextWithOffset(offset, length), offset, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWriteOffsetBufferGreaterThanRemaining_equalToLength_writesAll() throws IOException {
        os.write(next(2));
        int offset = 6;
        int length = bufferSize;
        os.write(nextWithOffset(offset, length), offset, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withPaddedBufferGreaterThanRemaining_equalToLength_writesUpToBufferToStream() throws IOException {
        os.write(next(2));
        int padding = 6;
        int length = bufferSize;
        os.write(nextWithPadding(length, padding), 0, length);
        assertThat(inner.toByteArray()).isEqualTo(upTo(bufferSize));
    }

    @Test
    public void flush_afterWritePaddedBufferGreaterThanRemaining_equalToLength_writesAll() throws IOException {
        os.write(next(2));
        int padding = 6;
        int length = bufferSize;
        os.write(nextWithPadding(length, padding), 0, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWritePaddedBufferGreaterThanRemaining_equalToLength_writesAll() throws IOException {
        os.write(next(2));
        int padding = 6;
        int length = bufferSize;
        os.write(nextWithPadding(length, padding), 0, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withBufferGreaterThanRemaining_greaterThanLength_writesUpToBufferToStream() throws IOException {
        os.write(next(2));
        os.write(next(((bufferSize) + 1)));
        assertThat(inner.toByteArray()).isEqualTo(upTo(bufferSize));
    }

    @Test
    public void flush_afterWriteBufferGreaterThanRemaining_greaterThanLength_writesAll() throws IOException {
        os.write(next(2));
        os.write(next(((bufferSize) + 1)));
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWriteBufferGreaterThanRemaining_greaterThanLength_writesAll() throws IOException {
        os.write(next(2));
        os.write(next(((bufferSize) + 1)));
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withOffsetBufferGreaterThanRemaining_greaterThanLength_writesUpToBuffer() throws IOException {
        os.write(next(2));
        int offset = 2;
        int length = (bufferSize) + 1;
        os.write(nextWithOffset(offset, length), offset, length);
        assertThat(inner.toByteArray()).isEqualTo(upTo(bufferSize));
    }

    @Test
    public void flush_afterWriteOffsetBufferGreaterThanRemaining_greaterThanLength_writesAllToStream() throws IOException {
        os.write(next(2));
        int offset = 2;
        int length = (bufferSize) + 1;
        os.write(nextWithOffset(offset, length), offset, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWriteOffsetBufferGreaterThanRemaining_greaterThanLength_writesAllToStream() throws IOException {
        os.write(next(2));
        int offset = 2;
        int length = (bufferSize) + 1;
        os.write(nextWithOffset(offset, length), offset, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withPaddedBufferGreaterThanRemaining_greaterThanLength_writesUpToBuffer() throws IOException {
        os.write(next(2));
        int padding = 2;
        int length = (bufferSize) + 1;
        os.write(nextWithPadding(length, padding), 0, length);
        assertThat(inner.toByteArray()).isEqualTo(upTo(bufferSize));
    }

    @Test
    public void flush_afterWritePaddedBufferGreaterThanRemaining_greaterThanLength_writesAllToStream() throws IOException {
        os.write(next(2));
        int padding = 2;
        int length = (bufferSize) + 1;
        os.write(nextWithPadding(length, padding), 0, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWritePaddedBufferGreaterThanRemaining_greaterThanLength_writesAllToStream() throws IOException {
        os.write(next(2));
        int padding = 2;
        int length = (bufferSize) + 1;
        os.write(nextWithPadding(length, padding), 0, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withBufferMoreThanRemains_greaterThanTwiceLength_writesAll() throws IOException {
        os.write(next(2));
        os.write(next((((bufferSize) * 2) + 1)));
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void flush_afterWriteBufferMoreThanRemains_greaterThanTwiceLength_writesAll() throws IOException {
        os.write(next(2));
        os.write(next((((bufferSize) * 2) + 1)));
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWriteBufferMoreThanRemains_greaterThanTwiceLength_writesAll() throws IOException {
        os.write(next(2));
        os.write(next((((bufferSize) * 2) + 1)));
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withOffsetBufferMoreThanRemains_greaterThanTwiceLength_writesAll() throws IOException {
        os.write(next(2));
        int offset = (bufferSize) + 1;
        int length = ((bufferSize) * 2) + 2;
        os.write(nextWithOffset(offset, length), offset, length);
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void flush_afterWriteOffsetBufferMoreThanRemains_greaterThanTwiceLength_writesAll() throws IOException {
        os.write(next(2));
        int offset = (bufferSize) + 1;
        int length = ((bufferSize) * 2) + 2;
        os.write(nextWithOffset(offset, length), offset, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWriteOffsetBufferMoreThanRemains_greaterThanTwiceLength_writesAll() throws IOException {
        os.write(next(2));
        int offset = (bufferSize) + 1;
        int length = ((bufferSize) * 2) + 2;
        os.write(nextWithOffset(offset, length), offset, length);
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_withPaddedBufferMoreThanRemains_greaterThanTwiceLength_writesAll() throws IOException {
        os.write(next(2));
        int padding = (bufferSize) + 1;
        int length = ((bufferSize) * 2) + 2;
        os.write(nextWithPadding(length, padding), 0, length);
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void flush_afterWritePaddedBufferMoreThanRemains_greaterThanTwiceLength_writesAll() throws IOException {
        os.write(next(2));
        int padding = (bufferSize) + 1;
        int length = ((bufferSize) * 2) + 2;
        os.write(nextWithPadding(length, padding), 0, length);
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void close_afterWritePaddedBufferMoreThanRemains_greaterThanTwiceLength_writesAll() throws IOException {
        os.write(next(2));
        int padding = (bufferSize) + 1;
        int length = ((bufferSize) * 2) + 2;
        os.write(nextWithPadding(length, padding), 0, length);
        os.close();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void flush_flushesUnderlyingStream() throws IOException {
        os = new BufferedOutputStream(mockOutputStream, arrayPool, bufferSize);
        os.flush();
        Mockito.verify(mockOutputStream).flush();
    }

    @Test
    public void overflowBuffer_doesNotFlushUnderlyingStream() throws IOException {
        os = new BufferedOutputStream(mockOutputStream, arrayPool, bufferSize);
        os.write(1);
        os.write(next(((remaining()) + 1)));
        Mockito.verify(mockOutputStream, Mockito.never()).flush();
    }

    @Test
    public void close_closesUnderlyingStream() throws IOException {
        os = new BufferedOutputStream(mockOutputStream, arrayPool, bufferSize);
        os.close();
        Mockito.verify(mockOutputStream).close();
    }

    @Test
    public void close_whenUnderlyingStreamThrows_closesStream() throws IOException {
        os = new BufferedOutputStream(mockOutputStream, arrayPool, bufferSize);
        Mockito.doThrow(new IOException()).when(mockOutputStream).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        os.write(1);
        try {
            os.close();
            Assert.fail("Failed to receive expected exception");
        } catch (IOException e) {
            // Expected.
        }
        Mockito.verify(mockOutputStream).close();
    }

    @Test
    public void flush_withZeroBytesWritten_doesNotWriteToStream() throws IOException {
        os = new BufferedOutputStream(mockOutputStream, arrayPool, bufferSize);
        os.flush();
        Mockito.verify(mockOutputStream, Mockito.never()).write(ArgumentMatchers.anyInt());
        Mockito.verify(mockOutputStream, Mockito.never()).write(ArgumentMatchers.any(byte[].class));
        Mockito.verify(mockOutputStream, Mockito.never()).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void write_throwsIfOffsetIsLessThanZero() {
        Assert.assertThrows(IndexOutOfBoundsException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                /* initialOffset= */
                /* length= */
                os.write(new byte[0], (-1), 0);
            }
        });
    }

    @Test
    public void write_throwsIfLengthIsLessThanZero() {
        Assert.assertThrows(IndexOutOfBoundsException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                /* initialOffset= */
                /* length= */
                os.write(new byte[0], 0, (-1));
            }
        });
    }

    @Test
    public void write_throwsIfOffsetIsGreaterThanLength() {
        Assert.assertThrows(IndexOutOfBoundsException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                /* initialOffset= */
                /* length= */
                os.write(new byte[0], 1, 0);
            }
        });
    }

    @Test
    public void write_throwsIfLengthsIsGreaterThanLength() {
        Assert.assertThrows(IndexOutOfBoundsException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                /* initialOffset= */
                /* length= */
                os.write(new byte[0], 0, 1);
            }
        });
    }

    @Test
    public void write_throwsIfLengthAndOffsetsIsGreaterThanLength() {
        Assert.assertThrows(IndexOutOfBoundsException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                /* initialOffset= */
                /* length= */
                os.write(new byte[1], 1, 1);
            }
        });
    }

    @Test
    public void write_withZeroLengthBuffer_doesNothing() throws IOException {
        os.write(new byte[0]);
        assertThat(inner.toByteArray()).hasLength(0);
    }

    @Test
    public void write_withZeroLengthBufferAndZeroOffsetAndLength_doesNothing() throws IOException {
        os.write(new byte[0], 0, 0);
        assertThat(inner.toByteArray()).hasLength(0);
    }

    @Test
    public void write_afterWriteWithZeroLengthBuffer_writesExpected() throws IOException {
        os.write(new byte[0]);
        os.write(next());
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }

    @Test
    public void write_afterWriteZeroLengthBufferAndZeroOffsetAndLength_writesExpected() throws IOException {
        os.write(new byte[0], 0, 0);
        os.write(next());
        os.flush();
        assertThat(inner.toByteArray()).isEqualTo(all());
    }
}

