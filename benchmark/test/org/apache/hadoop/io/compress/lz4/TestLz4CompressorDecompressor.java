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
package org.apache.hadoop.io.compress.lz4;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.compress.BlockCompressorStream;
import org.apache.hadoop.io.compress.BlockDecompressorStream;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.test.MultithreadedTestUtil;
import org.junit.Assert;
import org.junit.Test;


public class TestLz4CompressorDecompressor {
    private static final Random rnd = new Random(12345L);

    // test on NullPointerException in {@code compressor.setInput()}
    @Test
    public void testCompressorSetInputNullPointerException() {
        try {
            Lz4Compressor compressor = new Lz4Compressor();
            compressor.setInput(null, 0, 10);
            Assert.fail("testCompressorSetInputNullPointerException error !!!");
        } catch (NullPointerException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testCompressorSetInputNullPointerException ex error !!!");
        }
    }

    // test on NullPointerException in {@code decompressor.setInput()}
    @Test
    public void testDecompressorSetInputNullPointerException() {
        try {
            Lz4Decompressor decompressor = new Lz4Decompressor();
            decompressor.setInput(null, 0, 10);
            Assert.fail("testDecompressorSetInputNullPointerException error !!!");
        } catch (NullPointerException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testDecompressorSetInputNullPointerException ex error !!!");
        }
    }

    // test on ArrayIndexOutOfBoundsException in {@code compressor.setInput()}
    @Test
    public void testCompressorSetInputAIOBException() {
        try {
            Lz4Compressor compressor = new Lz4Compressor();
            compressor.setInput(new byte[]{  }, (-5), 10);
            Assert.fail("testCompressorSetInputAIOBException error !!!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        } catch (Exception ex) {
            Assert.fail("testCompressorSetInputAIOBException ex error !!!");
        }
    }

    // test on ArrayIndexOutOfBoundsException in {@code decompressor.setInput()}
    @Test
    public void testDecompressorSetInputAIOUBException() {
        try {
            Lz4Decompressor decompressor = new Lz4Decompressor();
            decompressor.setInput(new byte[]{  }, (-5), 10);
            Assert.fail("testDecompressorSetInputAIOBException error !!!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testDecompressorSetInputAIOBException ex error !!!");
        }
    }

    // test on NullPointerException in {@code compressor.compress()}
    @Test
    public void testCompressorCompressNullPointerException() {
        try {
            Lz4Compressor compressor = new Lz4Compressor();
            byte[] bytes = TestLz4CompressorDecompressor.generate((1024 * 6));
            compressor.setInput(bytes, 0, bytes.length);
            compressor.compress(null, 0, 0);
            Assert.fail("testCompressorCompressNullPointerException error !!!");
        } catch (NullPointerException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testCompressorCompressNullPointerException ex error !!!");
        }
    }

    // test on NullPointerException in {@code decompressor.decompress()}
    @Test
    public void testDecompressorCompressNullPointerException() {
        try {
            Lz4Decompressor decompressor = new Lz4Decompressor();
            byte[] bytes = TestLz4CompressorDecompressor.generate((1024 * 6));
            decompressor.setInput(bytes, 0, bytes.length);
            decompressor.decompress(null, 0, 0);
            Assert.fail("testDecompressorCompressNullPointerException error !!!");
        } catch (NullPointerException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testDecompressorCompressNullPointerException ex error !!!");
        }
    }

    // test on ArrayIndexOutOfBoundsException in {@code compressor.compress()}
    @Test
    public void testCompressorCompressAIOBException() {
        try {
            Lz4Compressor compressor = new Lz4Compressor();
            byte[] bytes = TestLz4CompressorDecompressor.generate((1024 * 6));
            compressor.setInput(bytes, 0, bytes.length);
            compressor.compress(new byte[]{  }, 0, (-1));
            Assert.fail("testCompressorCompressAIOBException error !!!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testCompressorCompressAIOBException ex error !!!");
        }
    }

    // test on ArrayIndexOutOfBoundsException in decompressor.decompress()
    @Test
    public void testDecompressorCompressAIOBException() {
        try {
            Lz4Decompressor decompressor = new Lz4Decompressor();
            byte[] bytes = TestLz4CompressorDecompressor.generate((1024 * 6));
            decompressor.setInput(bytes, 0, bytes.length);
            decompressor.decompress(new byte[]{  }, 0, (-1));
            Assert.fail("testDecompressorCompressAIOBException error !!!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        } catch (Exception e) {
            Assert.fail("testDecompressorCompressAIOBException ex error !!!");
        }
    }

    // test Lz4Compressor compressor.compress()
    @Test
    public void testSetInputWithBytesSizeMoreThenDefaultLz4CompressorByfferSize() {
        int BYTES_SIZE = (1024 * 64) + 1;
        try {
            Lz4Compressor compressor = new Lz4Compressor();
            byte[] bytes = TestLz4CompressorDecompressor.generate(BYTES_SIZE);
            Assert.assertTrue("needsInput error !!!", compressor.needsInput());
            compressor.setInput(bytes, 0, bytes.length);
            byte[] emptyBytes = new byte[BYTES_SIZE];
            int csize = compressor.compress(emptyBytes, 0, bytes.length);
            Assert.assertTrue("testSetInputWithBytesSizeMoreThenDefaultLz4CompressorByfferSize error !!!", (csize != 0));
        } catch (Exception ex) {
            Assert.fail("testSetInputWithBytesSizeMoreThenDefaultLz4CompressorByfferSize ex error !!!");
        }
    }

    // test compress/decompress process
    @Test
    public void testCompressDecompress() {
        int BYTE_SIZE = 1024 * 54;
        byte[] bytes = TestLz4CompressorDecompressor.generate(BYTE_SIZE);
        Lz4Compressor compressor = new Lz4Compressor();
        try {
            compressor.setInput(bytes, 0, bytes.length);
            Assert.assertTrue("Lz4CompressDecompress getBytesRead error !!!", ((compressor.getBytesRead()) > 0));
            Assert.assertTrue("Lz4CompressDecompress getBytesWritten before compress error !!!", ((compressor.getBytesWritten()) == 0));
            byte[] compressed = new byte[BYTE_SIZE];
            int cSize = compressor.compress(compressed, 0, compressed.length);
            Assert.assertTrue("Lz4CompressDecompress getBytesWritten after compress error !!!", ((compressor.getBytesWritten()) > 0));
            Lz4Decompressor decompressor = new Lz4Decompressor();
            // set as input for decompressor only compressed data indicated with cSize
            decompressor.setInput(compressed, 0, cSize);
            byte[] decompressed = new byte[BYTE_SIZE];
            decompressor.decompress(decompressed, 0, decompressed.length);
            Assert.assertTrue("testLz4CompressDecompress finished error !!!", decompressor.finished());
            Assert.assertArrayEquals(bytes, decompressed);
            compressor.reset();
            decompressor.reset();
            Assert.assertTrue("decompressor getRemaining error !!!", ((decompressor.getRemaining()) == 0));
        } catch (Exception e) {
            Assert.fail("testLz4CompressDecompress ex error!!!");
        }
    }

    // test compress/decompress with empty stream
    @Test
    public void testCompressorDecompressorEmptyStreamLogic() {
        ByteArrayInputStream bytesIn = null;
        ByteArrayOutputStream bytesOut = null;
        byte[] buf = null;
        BlockDecompressorStream blockDecompressorStream = null;
        try {
            // compress empty stream
            bytesOut = new ByteArrayOutputStream();
            BlockCompressorStream blockCompressorStream = new BlockCompressorStream(bytesOut, new Lz4Compressor(), 1024, 0);
            // close without write
            blockCompressorStream.close();
            // check compressed output
            buf = bytesOut.toByteArray();
            Assert.assertEquals("empty stream compressed output size != 4", 4, buf.length);
            // use compressed output as input for decompression
            bytesIn = new ByteArrayInputStream(buf);
            // create decompression stream
            blockDecompressorStream = new BlockDecompressorStream(bytesIn, new Lz4Decompressor(), 1024);
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

    // test compress/decompress process through CompressionOutputStream/CompressionInputStream api
    @Test
    public void testCompressorDecopressorLogicWithCompressionStreams() {
        DataOutputStream deflateOut = null;
        DataInputStream inflateIn = null;
        int BYTE_SIZE = 1024 * 100;
        byte[] bytes = TestLz4CompressorDecompressor.generate(BYTE_SIZE);
        int bufferSize = 262144;
        int compressionOverhead = (bufferSize / 6) + 32;
        try {
            DataOutputBuffer compressedDataBuffer = new DataOutputBuffer();
            CompressionOutputStream deflateFilter = new BlockCompressorStream(compressedDataBuffer, new Lz4Compressor(bufferSize), bufferSize, compressionOverhead);
            deflateOut = new DataOutputStream(new BufferedOutputStream(deflateFilter));
            deflateOut.write(bytes, 0, bytes.length);
            deflateOut.flush();
            deflateFilter.finish();
            DataInputBuffer deCompressedDataBuffer = new DataInputBuffer();
            deCompressedDataBuffer.reset(compressedDataBuffer.getData(), 0, compressedDataBuffer.getLength());
            CompressionInputStream inflateFilter = new BlockDecompressorStream(deCompressedDataBuffer, new Lz4Decompressor(bufferSize), bufferSize);
            inflateIn = new DataInputStream(new BufferedInputStream(inflateFilter));
            byte[] result = new byte[BYTE_SIZE];
            inflateIn.read(result);
            Assert.assertArrayEquals("original array not equals compress/decompressed array", result, bytes);
        } catch (IOException e) {
            Assert.fail("testLz4CompressorDecopressorLogicWithCompressionStreams ex error !!!");
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

    @Test
    public void testLz4CompressDecompressInMultiThreads() throws Exception {
        MultithreadedTestUtil.TestContext ctx = new MultithreadedTestUtil.TestContext();
        for (int i = 0; i < 10; i++) {
            ctx.addThread(new MultithreadedTestUtil.TestingThread(ctx) {
                @Override
                public void doWork() throws Exception {
                    testCompressDecompress();
                }
            });
        }
        ctx.startThreads();
        ctx.waitFor(60000);
    }
}

