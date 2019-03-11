/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.websockets.extensions;


import io.undertow.testutils.category.UnitTest;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * An auxiliar test class for compression/decompression operations implemented on extensions context.
 *
 * @author Lucas Ponce
 */
@Category(UnitTest.class)
public class CompressionUtilsTest {
    private static Inflater decompress;

    private static Deflater compress;

    private static byte[] buf = new byte[1024];

    @Test
    public void testBasicCompressDecompress() throws Exception {
        String raw = "Hello";
        CompressionUtilsTest.compress.setInput(raw.getBytes());
        CompressionUtilsTest.compress.finish();
        int read = CompressionUtilsTest.compress.deflate(CompressionUtilsTest.buf, 0, CompressionUtilsTest.buf.length, Deflater.SYNC_FLUSH);
        CompressionUtilsTest.decompress.setInput(CompressionUtilsTest.buf, 0, read);
        read = CompressionUtilsTest.decompress.inflate(CompressionUtilsTest.buf);
        Assert.assertEquals("Hello", new String(CompressionUtilsTest.buf, 0, read, "UTF-8"));
        CompressionUtilsTest.compress.reset();
        CompressionUtilsTest.decompress.reset();
        raw = "Hello, World!";
        CompressionUtilsTest.compress.setInput(raw.getBytes());
        CompressionUtilsTest.compress.finish();
        read = CompressionUtilsTest.compress.deflate(CompressionUtilsTest.buf, 0, CompressionUtilsTest.buf.length, Deflater.SYNC_FLUSH);
        CompressionUtilsTest.decompress.setInput(CompressionUtilsTest.buf, 0, read);
        read = CompressionUtilsTest.decompress.inflate(CompressionUtilsTest.buf);
        Assert.assertEquals("Hello, World!", new String(CompressionUtilsTest.buf, 0, read, "UTF-8"));
    }

    @Test
    public void testCompressDecompressByFrames() throws Exception {
        String raw = "Hello, World! This is a long input example data with a lot of content for testing";
        /* This test shares same buffer, 0-511 for compress, 512-1023 for decompress */
        int position1 = 0;
        int position2 = 512;
        int chunkLength = 10;
        // Frame 1
        CompressionUtilsTest.compress.setInput(raw.getBytes(), position1, chunkLength);
        int compressed = CompressionUtilsTest.compress.deflate(CompressionUtilsTest.buf, 0, 512, Deflater.SYNC_FLUSH);
        CompressionUtilsTest.decompress.setInput(CompressionUtilsTest.buf, 0, compressed);
        int decompressed = CompressionUtilsTest.decompress.inflate(CompressionUtilsTest.buf, position2, ((CompressionUtilsTest.buf.length) - position2));
        // Frame 2
        position1 += chunkLength;
        position2 += decompressed;
        CompressionUtilsTest.compress.setInput(raw.getBytes(), position1, chunkLength);
        compressed = CompressionUtilsTest.compress.deflate(CompressionUtilsTest.buf, 0, 512, Deflater.NO_FLUSH);
        CompressionUtilsTest.decompress.setInput(CompressionUtilsTest.buf, 0, compressed);
        CompressionUtilsTest.decompress.finished();
        decompressed = CompressionUtilsTest.decompress.inflate(CompressionUtilsTest.buf, position2, ((CompressionUtilsTest.buf.length) - position2));
        // Frame 3
        position1 += chunkLength;
        position2 += decompressed;
        CompressionUtilsTest.compress.setInput(raw.getBytes(), position1, ((raw.getBytes().length) - position1));
        CompressionUtilsTest.compress.finish();
        compressed = CompressionUtilsTest.compress.deflate(CompressionUtilsTest.buf, 0, 512, Deflater.NO_FLUSH);
        CompressionUtilsTest.decompress.setInput(CompressionUtilsTest.buf, 0, compressed);
        decompressed = CompressionUtilsTest.decompress.inflate(CompressionUtilsTest.buf, position2, ((CompressionUtilsTest.buf.length) - position2));
        Assert.assertEquals(raw, new String(CompressionUtilsTest.buf, 512, ((position2 + decompressed) - 512), "UTF-8"));
    }

    @Test
    public void testCompressByFramesDecompressWhole() throws Exception {
        String raw = "Hello, World! This is a long input example data with a lot of content for testing";
        byte[] compressed = new byte[(raw.length()) + 64];
        byte[] decompressed = new byte[raw.length()];
        int n = 0;
        int total = 0;
        // Compress Frame1
        CompressionUtilsTest.compress.setInput(raw.getBytes(), 0, 10);
        n = CompressionUtilsTest.compress.deflate(compressed, 0, compressed.length, Deflater.SYNC_FLUSH);
        total += n;
        // Compress Frame2
        CompressionUtilsTest.compress.setInput(raw.getBytes(), 10, 10);
        n = CompressionUtilsTest.compress.deflate(compressed, total, ((compressed.length) - total), Deflater.SYNC_FLUSH);
        total += n;
        // Compress Frame3
        CompressionUtilsTest.compress.setInput(raw.getBytes(), 20, ((raw.getBytes().length) - 20));
        n = CompressionUtilsTest.compress.deflate(compressed, total, ((compressed.length) - total), Deflater.SYNC_FLUSH);
        total += n;
        // Uncompress
        CompressionUtilsTest.decompress.setInput(compressed, 0, total);
        n = CompressionUtilsTest.decompress.inflate(decompressed, 0, decompressed.length);
        Assert.assertEquals(raw, new String(decompressed, 0, n, "UTF-8"));
    }

    @Test
    public void testLongMessage() throws Exception {
        int LONG_MSG = 16384;
        StringBuilder longMsg = new StringBuilder(LONG_MSG);
        byte[] longbuf = new byte[LONG_MSG + 64];
        byte[] output = new byte[LONG_MSG];
        for (int i = 0; i < LONG_MSG; i++) {
            longMsg.append(new Integer(i).toString().charAt(0));
        }
        String msg = longMsg.toString();
        byte[] input = msg.getBytes();
        byte[] compressBuf = new byte[LONG_MSG + 64];
        byte[] decompressBuf = new byte[LONG_MSG];
        CompressionUtilsTest.compress.setInput(input);
        CompressionUtilsTest.compress.finish();
        int read = CompressionUtilsTest.compress.deflate(compressBuf, 0, compressBuf.length, Deflater.SYNC_FLUSH);
        CompressionUtilsTest.decompress.setInput(compressBuf, 0, read);
        read = CompressionUtilsTest.decompress.inflate(decompressBuf);
        Assert.assertEquals(msg, new String(decompressBuf, 0, read, "UTF-8"));
    }

    @Test
    public void testCompressByFramesDecompressWholeLongMessage() throws Exception {
        int LONG_MSG = 75 * 1024;
        StringBuilder longMsg = new StringBuilder(LONG_MSG);
        byte[] longbuf = new byte[LONG_MSG + 64];
        byte[] output = new byte[LONG_MSG];
        for (int i = 0; i < LONG_MSG; i++) {
            longMsg.append(new Integer(i).toString().charAt(0));
        }
        String msg = longMsg.toString();
        byte[] input = msg.getBytes();
        /* Compress in chunks of 1024 bytes */
        boolean finished = false;
        int start = 0;
        int end;
        int compressed;
        int total = 0;
        while (!finished) {
            end = ((start + 1024) < (input.length)) ? 1024 : (input.length) - start;
            CompressionUtilsTest.compress.setInput(input, start, end);
            start += 1024;
            finished = start >= (input.length);
            if (finished) {
                CompressionUtilsTest.compress.finish();
            }
            compressed = CompressionUtilsTest.compress.deflate(longbuf, total, ((longbuf.length) - total), Deflater.SYNC_FLUSH);
            total += compressed;
        } 
        /* Decompress whole message */
        int decompressed = 0;
        CompressionUtilsTest.decompress.setInput(longbuf, 0, total);
        CompressionUtilsTest.decompress.finished();
        decompressed = CompressionUtilsTest.decompress.inflate(output, 0, output.length);
        Assert.assertEquals(longMsg.toString(), new String(output, 0, decompressed, "UTF-8"));
    }

    @Test
    public void testEmptyFrames() throws Exception {
        CompressionUtilsTest.decompress.reset();
        byte[] compressedFrame1 = new byte[]{ ((byte) (242)), ((byte) (72)), ((byte) (205)) };
        byte[] compressedFrame2 = new byte[]{ ((byte) (201)), ((byte) (201)), ((byte) (7)), ((byte) (0)) };
        byte[] compressedFrame3 = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (255)), ((byte) (255)) };
        byte[] output = new byte[1024];
        int decompressed = 0;
        CompressionUtilsTest.decompress.setInput(compressedFrame1);
        decompressed = CompressionUtilsTest.decompress.inflate(output, 0, output.length);
        Assert.assertEquals(2, decompressed);
        Assert.assertEquals("He", new String(output, 0, decompressed, "UTF-8"));
        CompressionUtilsTest.decompress.setInput(compressedFrame2);
        decompressed = CompressionUtilsTest.decompress.inflate(output, 0, output.length);
        Assert.assertEquals(3, decompressed);
        Assert.assertEquals("llo", new String(output, 0, decompressed, "UTF-8"));
        CompressionUtilsTest.decompress.setInput(compressedFrame3);
        decompressed = CompressionUtilsTest.decompress.inflate(output, 0, output.length);
        Assert.assertEquals(0, decompressed);
        CompressionUtilsTest.decompress.setInput(compressedFrame1);
        decompressed = CompressionUtilsTest.decompress.inflate(output, 0, output.length);
        Assert.assertEquals(2, decompressed);
        Assert.assertEquals("He", new String(output, 0, decompressed, "UTF-8"));
        CompressionUtilsTest.decompress.setInput(compressedFrame2);
        decompressed = CompressionUtilsTest.decompress.inflate(output, 0, output.length);
        Assert.assertEquals(3, decompressed);
        Assert.assertEquals("llo", new String(output, 0, decompressed, "UTF-8"));
    }

    @Test
    public void testPadding() throws Exception {
        String original = "This is a long message - This is a long message - This is a long message";
        byte[] compressed = new byte[1024];
        int nCompressed;
        CompressionUtilsTest.compress.setInput(original.getBytes());
        nCompressed = CompressionUtilsTest.compress.deflate(compressed, 0, compressed.length, Deflater.SYNC_FLUSH);
        /* Padding */
        byte[] padding = new byte[]{ 0, 0, 0, ((byte) (255)), ((byte) (255)), 0, 0, 0, ((byte) (255)), ((byte) (255)), 0, 0, 0, ((byte) (255)), ((byte) (255)) };
        int nPadding = padding.length;
        for (int i = 0; i < (padding.length); i++) {
            compressed[(nCompressed + i)] = padding[i];
        }
        byte[] uncompressed = new byte[1024];
        int nUncompressed;
        CompressionUtilsTest.decompress.setInput(compressed, 0, (nCompressed + nPadding));
        nUncompressed = CompressionUtilsTest.decompress.inflate(uncompressed);
        Assert.assertEquals(original, new String(uncompressed, 0, nUncompressed, "UTF-8"));
    }
}

