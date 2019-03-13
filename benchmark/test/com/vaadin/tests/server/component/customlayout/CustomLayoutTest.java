package com.vaadin.tests.server.component.customlayout;


import com.vaadin.ui.CustomLayout;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link CustomLayout}
 *
 * @author Vaadin Ltd
 */
public class CustomLayoutTest {
    @Test
    public void ctor_inputStreamProvided_inputStreamIsRead() throws IOException, IllegalAccessException, IllegalArgumentException {
        Integer buffer = getBufferSize();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < buffer; i++) {
            builder.append('a');
        }
        byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        CustomLayoutTest.InputStreamImpl stream = new CustomLayoutTest.InputStreamImpl(inputStream, (buffer / 2));
        new CustomLayout(stream);
        Assert.assertTrue("Stream is not closed in CustomLayout CTOR ", stream.isClosed());
        Assert.assertEquals("Number of read bytes is incorrect", bytes.length, stream.getCount());
    }

    private static class InputStreamImpl extends FilterInputStream {
        InputStreamImpl(InputStream inputStream, int maxArrayLength) {
            super(inputStream);
            this.maxArrayLength = maxArrayLength;
        }

        @Override
        public int read() throws IOException {
            int read = super.read();
            if (read != (-1)) {
                (readCount)++;
            }
            return read;
        }

        @Override
        public int read(byte[] b) throws IOException {
            if ((b.length) > (maxArrayLength)) {
                return read(b, 0, maxArrayLength);
            }
            int count = super.read(b);
            if (count != (-1)) {
                readCount += count;
            }
            return count;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (len > (maxArrayLength)) {
                return read(b, off, maxArrayLength);
            }
            int count = super.read(b, off, len);
            if (count != (-1)) {
                readCount += count;
            }
            return count;
        }

        @Override
        public void close() throws IOException {
            isClosed = true;
            super.close();
        }

        int getCount() {
            return readCount;
        }

        boolean isClosed() {
            return isClosed;
        }

        private int readCount;

        private boolean isClosed;

        private final int maxArrayLength;
    }
}

