package org.embulk.spi;


import java.io.ByteArrayOutputStream;
import java.util.Random;
import org.embulk.EmbulkTestRuntime;
import org.embulk.spi.util.FileInputInputStream;
import org.embulk.spi.util.FileOutputOutputStream;
import org.embulk.spi.util.ListFileInput;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestFileInputInputStream {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    private ListFileInput fileInput;

    private MockFileOutput fileOutput;

    private FileInputInputStream in;

    private FileOutputOutputStream out;

    @Test
    public void testRandomReadWrite() throws Exception {
        newOutputStream();
        out.nextFile();
        ByteArrayOutputStream expectedOut = new ByteArrayOutputStream();
        Random rand = runtime.getRandom();
        byte[] buffer = new byte[((rand.nextInt()) % 1024) + 1024];
        for (int i = 0; i < 256; i++) {
            rand.nextBytes(buffer);
            expectedOut.write(buffer);
            out.write(buffer);
        }
        out.finish();
        byte[] expected = expectedOut.toByteArray();
        byte[] actual = new byte[expected.length];
        newInputStream();
        in.nextFile();
        int pos = 0;
        while (pos < (actual.length)) {
            int n = in.read(actual, pos, ((actual.length) - pos));
            if (n < 0) {
                break;
            }
            pos += n;
        } 
        Assert.assertEquals(expected.length, pos);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSkipReturnsZeroForNoData() {
        FileInputInputStream in = new FileInputInputStream(new TestFileInputInputStream.MockFileInput());
        Assert.assertEquals("Verify skip() returns 0 when there is no data.", 0L, in.skip(1));
    }

    private static class MockFileInput implements FileInput {
        @Override
        public boolean nextFile() {
            return false;
        }

        @Override
        public Buffer poll() {
            return null;
        }

        @Override
        public void close() {
        }
    }
}

