package com.bumptech.glide.gifdecoder.test;


import java.nio.ByteBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link com.bumptech.glide.gifdecoder.test.GifBytesTestUtil}.
 */
@RunWith(JUnit4.class)
public class GifBytesTestUtilTest {
    @Test
    public void testWriteHeaderAndLsdWithoutGct() {
        ByteBuffer buffer = ByteBuffer.allocate(GifBytesTestUtil.HEADER_LENGTH);
        GifBytesTestUtil.writeHeaderAndLsd(buffer, 8, 16, false, 0);
        byte[] expected = new byte[]{ 71, 73, 70, 56, 57, 97, 0, 8, 0, 16, 32, 0, 0 };
        GifBytesTestUtilTest.assertEquals(expected, buffer);
    }

    @Test
    public void testWriteHeaderAndLsdWithGct() {
        ByteBuffer buffer = ByteBuffer.allocate(GifBytesTestUtil.HEADER_LENGTH);
        GifBytesTestUtil.writeHeaderAndLsd(buffer, 8, 16, true, 4);
        byte[] expected = new byte[]{ 71, 73, 70, 56, 57, 97, 0, 8, 0, 16, ((byte) (164)), 0, 0 };
        GifBytesTestUtilTest.assertEquals(expected, buffer);
    }

    @Test
    public void testWriteImageDescriptorWithoutColorTable() {
        ByteBuffer buffer = ByteBuffer.allocate(GifBytesTestUtil.IMAGE_DESCRIPTOR_LENGTH);
        GifBytesTestUtil.writeImageDescriptor(buffer, 10, 9, 8, 7, false, 0);
        byte[] expected = new byte[]{ // Image separator.
        44, // Image left.
        0, 10, // Image right.
        0, 9, // Image width.
        0, 8, // Image height.
        0, 7, // Packed field.
        0 };
        GifBytesTestUtilTest.assertEquals(expected, buffer);
    }

    @Test
    public void testWriteImageDescriptorWithColorTable() {
        ByteBuffer buffer = ByteBuffer.allocate(GifBytesTestUtil.IMAGE_DESCRIPTOR_LENGTH);
        GifBytesTestUtil.writeImageDescriptor(buffer, 10, 9, 8, 7, true, 4);
        // Set LCT flag
        byte packedField = ((byte) (128)) | // Size of color table (2^(N + 1) == 4)
        1;
        byte[] expected = new byte[]{ // Image separator.
        44, // Image left.
        0, 10, // Image right.
        0, 9, // Image width.
        0, 8, // Image height.
        0, 7, packedField };
        GifBytesTestUtilTest.assertEquals(expected, buffer);
    }

    @Test
    public void testWriteColorTable() {
        final int numColors = 4;
        ByteBuffer buffer = ByteBuffer.allocate(GifBytesTestUtil.getColorTableLength(numColors));
        GifBytesTestUtil.writeColorTable(buffer, numColors);
        byte[] expected = new byte[]{ // First color.
        0, 0, 0, // Second color.
        0, 0, 1, // Third color.
        0, 0, 2, // Fourth color.
        0, 0, 3 };
        GifBytesTestUtilTest.assertEquals(expected, buffer);
    }

    @Test
    public void testWriteFakeImageData() {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        GifBytesTestUtil.writeFakeImageData(buffer, 2);
        byte[] expected = new byte[]{ 2, 1, 1, 0 };
        GifBytesTestUtilTest.assertEquals(expected, buffer);
    }

    @Test
    public void testWritesGraphicsControlExtension() {
        short delay = 20;
        ByteBuffer buffer = ByteBuffer.allocate(GifBytesTestUtil.GRAPHICS_CONTROL_EXTENSION_LENGTH);
        byte[] expected = new byte[]{ // Extension inducer.
        33, // Graphic control label.
        ((byte) (249)), // Block size.
        4, // Packed byte.
        0, // Frame delay.
        0, 20, // Transparent color index.
        0, // block terminator.
        0 };
        GifBytesTestUtil.writeGraphicsControlExtension(buffer, delay);
        GifBytesTestUtilTest.assertEquals(expected, buffer);
    }
}

