package org.embulk.spi;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.embulk.EmbulkTestRuntime;
import org.embulk.spi.util.InputStreamFileInput;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestInputStreamFileInput {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    @Test
    public void testSingleProvider() throws IOException {
        InputStreamFileInput subject = new InputStreamFileInput(runtime.getBufferAllocator(), provider(new ByteArrayInputStream("abcdef".getBytes("UTF-8"))));
        Assert.assertEquals(true, subject.nextFile());
        Assert.assertEquals("abcdef", bufferToString(subject.poll()));
        Assert.assertEquals(null, subject.poll());
        subject.close();
    }

    @Test
    public void testMultipleProvider() throws IOException {
        InputStreamFileInput subject = new InputStreamFileInput(runtime.getBufferAllocator(), provider(new ByteArrayInputStream("abcdef".getBytes("UTF-8")), new ByteArrayInputStream("ghijkl".getBytes("UTF-8")), new ByteArrayInputStream("mnopqr".getBytes("UTF-8"))));
        Assert.assertEquals(true, subject.nextFile());
        Assert.assertEquals("abcdef", bufferToString(subject.poll()));
        Assert.assertEquals(true, subject.nextFile());
        Assert.assertEquals("ghijkl", bufferToString(subject.poll()));
        Assert.assertEquals(true, subject.nextFile());
        Assert.assertEquals("mnopqr", bufferToString(subject.poll()));
        subject.close();
    }

    @Test
    public void testEmptyStream() throws IOException {
        InputStreamFileInput subject = new InputStreamFileInput(runtime.getBufferAllocator(), provider(new ByteArrayInputStream(new byte[0])));
        Assert.assertEquals(true, subject.nextFile());
        Assert.assertEquals(null, subject.poll());
        subject.close();
    }

    @Test
    public void testPollFirstException() throws IOException {
        InputStreamFileInput subject = new InputStreamFileInput(runtime.getBufferAllocator(), provider(new ByteArrayInputStream("abcdef".getBytes("UTF-8"))));
        try {
            subject.poll();
            Assert.fail();
        } catch (IllegalStateException ile) {
            // OK
        }
        subject.close();
    }

    @Test
    public void testEmptyProvider() throws IOException {
        InputStreamFileInput subject = new InputStreamFileInput(runtime.getBufferAllocator(), provider(new InputStream[0]));
        Assert.assertEquals(false, subject.nextFile());
        subject.close();
    }

    @Test
    public void testProviderOpenNextException() {
        InputStreamFileInput subject = new InputStreamFileInput(runtime.getBufferAllocator(), new InputStreamFileInput.Provider() {
            @Override
            public InputStream openNext() throws IOException {
                throw new IOException("emulated exception");
            }

            @Override
            public void close() throws IOException {
            }
        });
        try {
            subject.nextFile();
            Assert.fail();
        } catch (RuntimeException re) {
            // OK
        }
        subject.close();
    }

    @Test
    public void testProviderCloseException() {
        InputStreamFileInput subject = new InputStreamFileInput(runtime.getBufferAllocator(), new InputStreamFileInput.Provider() {
            @Override
            public InputStream openNext() throws IOException {
                return new ByteArrayInputStream(new byte[0]);
            }

            @Override
            public void close() throws IOException {
                throw new IOException("emulated exception");
            }
        });
        try {
            subject.close();
            Assert.fail();
        } catch (RuntimeException re) {
            // OK
        }
    }

    @Test
    public void testInputStreamReadException() {
        InputStreamFileInput subject = new InputStreamFileInput(runtime.getBufferAllocator(), new InputStreamFileInput.Provider() {
            @Override
            public InputStream openNext() throws IOException {
                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        throw new IOException("emulated exception");
                    }
                };
            }

            @Override
            public void close() throws IOException {
            }
        });
        Assert.assertEquals(true, subject.nextFile());
        try {
            subject.poll();
            Assert.fail();
        } catch (RuntimeException re) {
            // OK
        }
        subject.close();
    }
}

