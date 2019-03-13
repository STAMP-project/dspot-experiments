package com.bumptech.glide.load.data;


import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Runs some tests based on a random seed that asserts the output of writing to our buffered stream
 * matches the output of writing to {@link java.io.ByteArrayOutputStream}.
 */
@RunWith(JUnit4.class)
public class BufferedOutputStreamFuzzTest {
    private static final int TESTS = 500;

    private static final int BUFFER_SIZE = 10;

    private static final int WRITES_PER_TEST = 50;

    private static final int MAX_BYTES_PER_WRITE = (BufferedOutputStreamFuzzTest.BUFFER_SIZE) * 6;

    private static final Random RANDOM = new Random((-3207167907493985134L));

    @Mock
    private ArrayPool arrayPool;

    @Test
    public void runFuzzTest() throws IOException {
        for (int i = 0; i < (BufferedOutputStreamFuzzTest.TESTS); i++) {
            runTest(BufferedOutputStreamFuzzTest.RANDOM);
        }
    }

    private static final class Write {
        private final byte[] data;

        private final int length;

        private final int offset;

        private final BufferedOutputStreamFuzzTest.WriteType writeType;

        @Override
        public String toString() {
            return (((((((("Write{" + "data=") + (Arrays.toString(data))) + ", length=") + (length)) + ", offset=") + (offset)) + ", writeType=") + (writeType)) + '}';
        }

        Write(byte[] data, int length, int offset, BufferedOutputStreamFuzzTest.WriteType writeType) {
            this.data = data;
            this.length = length;
            this.offset = offset;
            this.writeType = writeType;
        }
    }

    private enum WriteType {

        BYTE,
        BUFFER,
        OFFSET_BUFFER;}
}

