package water.parser.parquet;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class VecDataInputStreamTest extends TestUtil {
    @Test
    public void testReadVecAsInputStream() throws Exception {
        Vec v0 = Vec.makeCon(0.0, 10000L, 10, true);
        Vec v = VecDataInputStreamTest.makeRandomByteVec(v0);
        try {
            InputStream t = new VecDataInputStreamTest.TestInputStream(v);
            Assert.assertTrue(((t.read()) >= 0));
            Assert.assertTrue(((t.skip(1)) >= 0));
            Assert.assertTrue(((t.read()) >= 0));
            Assert.assertTrue(((t.skip(5000)) >= 0));
            Assert.assertTrue(((t.read()) >= 0));
            Assert.assertTrue(((t.read(new byte[1333], 33, 67)) >= 0));
            Assert.assertTrue(((t.skip(100000L)) >= 0));
            Assert.assertEquals((-1), t.read());// reached the end of the stream

            Assert.assertEquals(0, t.available());
        } finally {
            if (v0 != null)
                v0.remove();

            if (v != null)
                v.remove();

        }
    }

    private static class TestInputStream extends InputStream {
        private InputStream ref;

        private InputStream tst;

        private TestInputStream(Vec v) {
            this.ref = new ByteArrayInputStream(VecDataInputStreamTest.chunkBytes(v));
            this.tst = new water.persist.VecDataInputStream(v);
        }

        @Override
        public int read(byte[] b) throws IOException {
            byte[] ref_b = Arrays.copyOf(b, b.length);
            int res = tst.read(b);
            Assert.assertEquals(ref.read(ref_b), res);
            Assert.assertArrayEquals(ref_b, b);
            return res;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            byte[] ref_b = Arrays.copyOf(b, b.length);
            int res = tst.read(b, off, len);
            Assert.assertEquals(ref.read(ref_b, off, len), res);
            Assert.assertArrayEquals(ref_b, b);
            return res;
        }

        @Override
        public long skip(long n) throws IOException {
            long res = tst.skip(n);
            Assert.assertEquals(ref.skip(n), res);
            return res;
        }

        @Override
        public int available() throws IOException {
            int res = tst.available();
            Assert.assertEquals(ref.available(), res);
            return res;
        }

        @Override
        public int read() throws IOException {
            int res = tst.read();
            Assert.assertEquals(ref.read(), res);
            return res;
        }

        @Override
        public synchronized void mark(int readlimit) {
            throw new UnsupportedOperationException("Intentionally not implemented");
        }

        @Override
        public synchronized void reset() throws IOException {
            throw new UnsupportedOperationException("Intentionally not implemented");
        }

        @Override
        public boolean markSupported() {
            throw new UnsupportedOperationException("Intentionally not implemented");
        }

        @Override
        public void close() throws IOException {
            throw new UnsupportedOperationException("Intentionally not implemented");
        }
    }
}

