/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.io.compress.snappy;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Random;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.compress.BlockCompressorStream;
import org.apache.hadoop.io.compress.BlockDecompressorStream;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.test.MultithreadedTestUtil;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class TestSnappyCompressorDecompressor {
    @Test
    public void testSnappyCompressorSetInputNullPointerException() {
        try {
            SnappyCompressor compressor = new SnappyCompressor();
            compressor.setInput(null, 0, 10);
            Assert.fail("testSnappyCompressorSetInputNullPointerException error !!!");
        } catch (NullPointerException ex) {
            // excepted
        } catch (Exception ex) {
            Assert.fail("testSnappyCompressorSetInputNullPointerException ex error !!!");
        }
    }

    @Test
    public void testSnappyDecompressorSetInputNullPointerException() {
        try {
            SnappyDecompressor decompressor = new SnappyDecompressor();
            decompressor.setInput(null, 0, 10);
            Assert.fail("testSnappyDecompressorSetInputNullPointerException error !!!");
        } catch (NullPointerException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testSnappyDecompressorSetInputNullPointerException ex error !!!");
        }
    }

    @Test
    public void testSnappyCompressorSetInputAIOBException() {
        try {
            SnappyCompressor compressor = new SnappyCompressor();
            compressor.setInput(new byte[]{  }, (-5), 10);
            Assert.fail("testSnappyCompressorSetInputAIOBException error !!!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        } catch (Exception ex) {
            Assert.fail("testSnappyCompressorSetInputAIOBException ex error !!!");
        }
    }

    @Test
    public void testSnappyDecompressorSetInputAIOUBException() {
        try {
            SnappyDecompressor decompressor = new SnappyDecompressor();
            decompressor.setInput(new byte[]{  }, (-5), 10);
            Assert.fail("testSnappyDecompressorSetInputAIOUBException error !!!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testSnappyDecompressorSetInputAIOUBException ex error !!!");
        }
    }

    @Test
    public void testSnappyCompressorCompressNullPointerException() {
        try {
            SnappyCompressor compressor = new SnappyCompressor();
            byte[] bytes = TestSnappyCompressorDecompressor.BytesGenerator.get((1024 * 6));
            compressor.setInput(bytes, 0, bytes.length);
            compressor.compress(null, 0, 0);
            Assert.fail("testSnappyCompressorCompressNullPointerException error !!!");
        } catch (NullPointerException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testSnappyCompressorCompressNullPointerException ex error !!!");
        }
    }

    @Test
    public void testSnappyDecompressorCompressNullPointerException() {
        try {
            SnappyDecompressor decompressor = new SnappyDecompressor();
            byte[] bytes = TestSnappyCompressorDecompressor.BytesGenerator.get((1024 * 6));
            decompressor.setInput(bytes, 0, bytes.length);
            decompressor.decompress(null, 0, 0);
            Assert.fail("testSnappyDecompressorCompressNullPointerException error !!!");
        } catch (NullPointerException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testSnappyDecompressorCompressNullPointerException ex error !!!");
        }
    }

    @Test
    public void testSnappyCompressorCompressAIOBException() {
        try {
            SnappyCompressor compressor = new SnappyCompressor();
            byte[] bytes = TestSnappyCompressorDecompressor.BytesGenerator.get((1024 * 6));
            compressor.setInput(bytes, 0, bytes.length);
            compressor.compress(new byte[]{  }, 0, (-1));
            Assert.fail("testSnappyCompressorCompressAIOBException error !!!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testSnappyCompressorCompressAIOBException ex error !!!");
        }
    }

    @Test
    public void testSnappyDecompressorCompressAIOBException() {
        try {
            SnappyDecompressor decompressor = new SnappyDecompressor();
            byte[] bytes = TestSnappyCompressorDecompressor.BytesGenerator.get((1024 * 6));
            decompressor.setInput(bytes, 0, bytes.length);
            decompressor.decompress(new byte[]{  }, 0, (-1));
            Assert.fail("testSnappyDecompressorCompressAIOBException error !!!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testSnappyDecompressorCompressAIOBException ex error !!!");
        }
    }

    @Test
    public void testSnappyCompressDecompress() {
        int BYTE_SIZE = 1024 * 54;
        byte[] bytes = TestSnappyCompressorDecompressor.BytesGenerator.get(BYTE_SIZE);
        SnappyCompressor compressor = new SnappyCompressor();
        try {
            compressor.setInput(bytes, 0, bytes.length);
            Assert.assertTrue("SnappyCompressDecompress getBytesRead error !!!", ((compressor.getBytesRead()) > 0));
            Assert.assertTrue("SnappyCompressDecompress getBytesWritten before compress error !!!", ((compressor.getBytesWritten()) == 0));
            byte[] compressed = new byte[BYTE_SIZE];
            int cSize = compressor.compress(compressed, 0, compressed.length);
            Assert.assertTrue("SnappyCompressDecompress getBytesWritten after compress error !!!", ((compressor.getBytesWritten()) > 0));
            SnappyDecompressor decompressor = new SnappyDecompressor(BYTE_SIZE);
            // set as input for decompressor only compressed data indicated with cSize
            decompressor.setInput(compressed, 0, cSize);
            byte[] decompressed = new byte[BYTE_SIZE];
            decompressor.decompress(decompressed, 0, decompressed.length);
            Assert.assertTrue("testSnappyCompressDecompress finished error !!!", decompressor.finished());
            Assert.assertArrayEquals(bytes, decompressed);
            compressor.reset();
            decompressor.reset();
            Assert.assertTrue("decompressor getRemaining error !!!", ((decompressor.getRemaining()) == 0));
        } catch (Exception e) {
            Assert.fail("testSnappyCompressDecompress ex error!!!");
        }
    }

    @Test
    public void testCompressorDecompressorEmptyStreamLogic() {
        ByteArrayInputStream bytesIn = null;
        ByteArrayOutputStream bytesOut = null;
        byte[] buf = null;
        BlockDecompressorStream blockDecompressorStream = null;
        try {
            // compress empty stream
            bytesOut = new ByteArrayOutputStream();
            BlockCompressorStream blockCompressorStream = new BlockCompressorStream(bytesOut, new SnappyCompressor(), 1024, 0);
            // close without write
            blockCompressorStream.close();
            // check compressed output
            buf = bytesOut.toByteArray();
            Assert.assertEquals("empty stream compressed output size != 4", 4, buf.length);
            // use compressed output as input for decompression
            bytesIn = new ByteArrayInputStream(buf);
            // create decompression stream
            blockDecompressorStream = new BlockDecompressorStream(bytesIn, new SnappyDecompressor(), 1024);
            // no byte is available because stream was closed
            Assert.assertEquals("return value is not -1", (-1), blockDecompressorStream.read());
        } catch (Exception e) {
            Assert.fail(("testCompressorDecompressorEmptyStreamLogic ex error !!!" + (e.getMessage())));
        } finally {
            if (blockDecompressorStream != null)
                try {
                    bytesIn.close();
                    bytesOut.close();
                    blockDecompressorStream.close();
                } catch (IOException e) {
                }

        }
    }

    @Test
    public void testSnappyBlockCompression() {
        int BYTE_SIZE = 1024 * 50;
        int BLOCK_SIZE = 512;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] block = new byte[BLOCK_SIZE];
        byte[] bytes = TestSnappyCompressorDecompressor.BytesGenerator.get(BYTE_SIZE);
        try {
            // Use default of 512 as bufferSize and compressionOverhead of
            // (1% of bufferSize + 12 bytes) = 18 bytes (zlib algorithm).
            SnappyCompressor compressor = new SnappyCompressor();
            int off = 0;
            int len = BYTE_SIZE;
            int maxSize = BLOCK_SIZE - 18;
            if (BYTE_SIZE > maxSize) {
                do {
                    int bufLen = Math.min(len, maxSize);
                    compressor.setInput(bytes, off, bufLen);
                    compressor.finish();
                    while (!(compressor.finished())) {
                        compressor.compress(block, 0, block.length);
                        out.write(block);
                    } 
                    compressor.reset();
                    off += bufLen;
                    len -= bufLen;
                } while (len > 0 );
            }
            Assert.assertTrue("testSnappyBlockCompression error !!!", ((out.toByteArray().length) > 0));
        } catch (Exception ex) {
            Assert.fail("testSnappyBlockCompression ex error !!!");
        }
    }

    @Test
    public void testSnappyDirectBlockCompression() {
        int[] size = new int[]{ 4 * 1024, 64 * 1024, 128 * 1024, 1024 * 1024 };
        Assume.assumeTrue(SnappyCodec.isNativeCodeLoaded());
        try {
            for (int i = 0; i < (size.length); i++) {
                compressDecompressLoop(size[i]);
            }
        } catch (IOException ex) {
            Assert.fail(("testSnappyDirectBlockCompression ex !!!" + ex));
        }
    }

    @Test
    public void testSnappyCompressorDecopressorLogicWithCompressionStreams() {
        int BYTE_SIZE = 1024 * 100;
        byte[] bytes = TestSnappyCompressorDecompressor.BytesGenerator.get(BYTE_SIZE);
        int bufferSize = 262144;
        int compressionOverhead = (bufferSize / 6) + 32;
        DataOutputStream deflateOut = null;
        DataInputStream inflateIn = null;
        try {
            DataOutputBuffer compressedDataBuffer = new DataOutputBuffer();
            CompressionOutputStream deflateFilter = new BlockCompressorStream(compressedDataBuffer, new SnappyCompressor(bufferSize), bufferSize, compressionOverhead);
            deflateOut = new DataOutputStream(new BufferedOutputStream(deflateFilter));
            deflateOut.write(bytes, 0, bytes.length);
            deflateOut.flush();
            deflateFilter.finish();
            DataInputBuffer deCompressedDataBuffer = new DataInputBuffer();
            deCompressedDataBuffer.reset(compressedDataBuffer.getData(), 0, compressedDataBuffer.getLength());
            CompressionInputStream inflateFilter = new BlockDecompressorStream(deCompressedDataBuffer, new SnappyDecompressor(bufferSize), bufferSize);
            inflateIn = new DataInputStream(new BufferedInputStream(inflateFilter));
            byte[] result = new byte[BYTE_SIZE];
            inflateIn.read(result);
            Assert.assertArrayEquals("original array not equals compress/decompressed array", result, bytes);
        } catch (IOException e) {
            Assert.fail("testSnappyCompressorDecopressorLogicWithCompressionStreams ex error !!!");
        } finally {
            try {
                if (deflateOut != null)
                    deflateOut.close();

                if (inflateIn != null)
                    inflateIn.close();

            } catch (Exception e) {
            }
        }
    }

    static final class BytesGenerator {
        private BytesGenerator() {
        }

        private static final byte[] CACHE = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

        private static final Random rnd = new Random(12345L);

        public static byte[] get(int size) {
            byte[] array = ((byte[]) (Array.newInstance(byte.class, size)));
            for (int i = 0; i < size; i++)
                array[i] = TestSnappyCompressorDecompressor.BytesGenerator.CACHE[TestSnappyCompressorDecompressor.BytesGenerator.rnd.nextInt(((TestSnappyCompressorDecompressor.BytesGenerator.CACHE.length) - 1))];

            return array;
        }
    }

    @Test
    public void testSnappyCompressDecompressInMultiThreads() throws Exception {
        MultithreadedTestUtil.TestContext ctx = new MultithreadedTestUtil.TestContext();
        for (int i = 0; i < 10; i++) {
            ctx.addThread(new MultithreadedTestUtil.TestingThread(ctx) {
                @Override
                public void doWork() throws Exception {
                    testSnappyCompressDecompress();
                }
            });
        }
        ctx.startThreads();
        ctx.waitFor(60000);
    }
}

