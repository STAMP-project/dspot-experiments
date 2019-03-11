package com.annimon.stream.function;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code ThrowableConsumer}.
 *
 * @see com.annimon.stream.function.ThrowableConsumer
 */
public class ThrowableConsumerTest {
    @Test
    public void testAccept() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(5);
        ThrowableConsumerTest.writer.accept(baos);
        ThrowableConsumerTest.writer.accept(baos);
        ThrowableConsumerTest.writer.accept(baos);
        Assert.assertEquals("XXX", baos.toString());
    }

    private static final ThrowableConsumer<OutputStream, IOException> writer = new ThrowableConsumer<OutputStream, IOException>() {
        @Override
        public void accept(OutputStream os) throws IOException {
            os.write('X');
        }
    };
}

