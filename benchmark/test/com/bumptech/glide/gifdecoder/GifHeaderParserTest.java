package com.bumptech.glide.gifdecoder;


import GifDecoder.STATUS_FORMAT_ERROR;
import GifDecoder.STATUS_OK;
import GifHeader.NETSCAPE_LOOP_COUNT_DOES_NOT_EXIST;
import GifHeader.NETSCAPE_LOOP_COUNT_FOREVER;
import com.bumptech.glide.gifdecoder.test.GifBytesTestUtil;
import com.bumptech.glide.testutil.TestUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static GifHeaderParser.DEFAULT_FRAME_DELAY;
import static GifHeaderParser.MIN_FRAME_DELAY;


/**
 * Tests for {@link com.bumptech.glide.gifdecoder.GifHeaderParser}.
 */
@RunWith(JUnit4.class)
public class GifHeaderParserTest {
    private GifHeaderParser parser;

    @Test
    public void testReturnsHeaderWithFormatErrorIfDoesNotStartWithGifHeader() {
        parser.setData("wrong_header".getBytes());
        GifHeader result = parser.parseHeader();
        Assert.assertEquals(STATUS_FORMAT_ERROR, result.status);
    }

    @Test
    public void testCanReadValidHeaderAndLSD() {
        final int width = 10;
        final int height = 20;
        ByteBuffer buffer = ByteBuffer.allocate(GifBytesTestUtil.HEADER_LENGTH).order(ByteOrder.LITTLE_ENDIAN);
        GifBytesTestUtil.writeHeaderAndLsd(buffer, width, height, false, 0);
        parser.setData(buffer.array());
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(width, header.width);
        Assert.assertEquals(height, header.height);
        Assert.assertFalse(header.gctFlag);
        // 2^(1+0) == 2^1 == 2.
        Assert.assertEquals(2, header.gctSize);
        Assert.assertEquals(0, header.bgIndex);
        Assert.assertEquals(0, header.pixelAspect);
    }

    @Test
    public void testCanParseHeaderOfTestImageWithoutGraphicalExtension() throws IOException {
        byte[] data = TestUtil.resourceToBytes(getClass(), "gif_without_graphical_control_extension.gif");
        parser.setData(data);
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(1, header.frameCount);
        Assert.assertNotNull(header.frames.get(0));
        Assert.assertEquals(STATUS_OK, header.status);
    }

    @Test
    public void testCanReadNetscapeIterationCountIfNetscapeIterationCountIsZero() throws IOException {
        byte[] data = TestUtil.resourceToBytes(getClass(), "gif_netscape_iteration_0.gif");
        parser.setData(data);
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(NETSCAPE_LOOP_COUNT_FOREVER, header.loopCount);
    }

    @Test
    public void testCanReadNetscapeIterationCountIfNetscapeIterationCountIs_1() throws IOException {
        byte[] data = TestUtil.resourceToBytes(getClass(), "gif_netscape_iteration_1.gif");
        parser.setData(data);
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(1, header.loopCount);
    }

    @Test
    public void testCanReadNetscapeIterationCountIfNetscapeIterationCountIs_0x0F() throws IOException {
        byte[] data = TestUtil.resourceToBytes(getClass(), "gif_netscape_iteration_255.gif");
        parser.setData(data);
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(255, header.loopCount);
    }

    @Test
    public void testCanReadNetscapeIterationCountIfNetscapeIterationCountIs_0x10() throws IOException {
        byte[] data = TestUtil.resourceToBytes(getClass(), "gif_netscape_iteration_256.gif");
        parser.setData(data);
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(256, header.loopCount);
    }

    @Test
    public void testCanReadNetscapeIterationCountIfNetscapeIterationCountIs_0xFF() throws IOException {
        byte[] data = TestUtil.resourceToBytes(getClass(), "gif_netscape_iteration_65535.gif");
        parser.setData(data);
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(65535, header.loopCount);
    }

    @Test
    public void testLoopCountReturnsMinusOneWithoutNetscapeIterationCount() throws IOException {
        byte[] data = TestUtil.resourceToBytes(getClass(), "gif_without_netscape_iteration.gif");
        parser.setData(data);
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(NETSCAPE_LOOP_COUNT_DOES_NOT_EXIST, header.loopCount);
    }

    @Test
    public void testCanReadImageDescriptorWithoutGraphicalExtension() {
        final int lzwMinCodeSize = 2;
        ByteBuffer buffer = ByteBuffer.allocate((((GifBytesTestUtil.HEADER_LENGTH) + (GifBytesTestUtil.IMAGE_DESCRIPTOR_LENGTH)) + (GifBytesTestUtil.getImageDataSize()))).order(ByteOrder.LITTLE_ENDIAN);
        GifBytesTestUtil.writeHeaderAndLsd(buffer, 1, 1, false, 0);
        /* hasLct */
        GifBytesTestUtil.writeImageDescriptor(buffer, 0, 0, 1, 1, false, 0);
        GifBytesTestUtil.writeFakeImageData(buffer, lzwMinCodeSize);
        parser.setData(buffer.array());
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(1, header.width);
        Assert.assertEquals(1, header.height);
        Assert.assertEquals(1, header.frameCount);
        Assert.assertNotNull(header.frames.get(0));
    }

    @Test
    public void testCanParseFrameDelay() {
        final short frameDelay = 50;
        ByteBuffer buffer = GifHeaderParserTest.writeHeaderWithGceAndFrameDelay(frameDelay);
        parser.setData(buffer.array());
        GifHeader header = parser.parseHeader();
        GifFrame frame = header.frames.get(0);
        // Convert delay in 100ths of a second to ms.
        Assert.assertEquals((frameDelay * 10), frame.delay);
    }

    @Test
    public void testSetsDefaultFrameDelayIfFrameDelayIsZero() {
        ByteBuffer buffer = GifHeaderParserTest.writeHeaderWithGceAndFrameDelay(((short) (0)));
        parser.setData(buffer.array());
        GifHeader header = parser.parseHeader();
        GifFrame frame = header.frames.get(0);
        // Convert delay in 100ths of a second to ms.
        Assert.assertEquals(((DEFAULT_FRAME_DELAY) * 10), frame.delay);
    }

    @Test
    public void testSetsDefaultFrameDelayIfFrameDelayIsLessThanMinimum() {
        final short frameDelay = (MIN_FRAME_DELAY) - 1;
        ByteBuffer buffer = GifHeaderParserTest.writeHeaderWithGceAndFrameDelay(frameDelay);
        parser.setData(buffer.array());
        GifHeader header = parser.parseHeader();
        GifFrame frame = header.frames.get(0);
        // Convert delay in 100ths of a second to ms.
        Assert.assertEquals(((DEFAULT_FRAME_DELAY) * 10), frame.delay);
    }

    @Test
    public void testObeysFrameDelayIfFrameDelayIsAtMinimum() {
        final short frameDelay = MIN_FRAME_DELAY;
        ByteBuffer buffer = GifHeaderParserTest.writeHeaderWithGceAndFrameDelay(frameDelay);
        parser.setData(buffer.array());
        GifHeader header = parser.parseHeader();
        GifFrame frame = header.frames.get(0);
        // Convert delay in 100ths of a second to ms.
        Assert.assertEquals((frameDelay * 10), frame.delay);
    }

    @Test
    public void testSetsFrameLocalColorTableToNullIfNoColorTable() {
        final int lzwMinCodeSize = 2;
        ByteBuffer buffer = ByteBuffer.allocate((((GifBytesTestUtil.HEADER_LENGTH) + (GifBytesTestUtil.IMAGE_DESCRIPTOR_LENGTH)) + (GifBytesTestUtil.getImageDataSize()))).order(ByteOrder.LITTLE_ENDIAN);
        GifBytesTestUtil.writeHeaderAndLsd(buffer, 1, 1, false, 0);
        /* hasLct */
        GifBytesTestUtil.writeImageDescriptor(buffer, 0, 0, 1, 1, false, 0);
        GifBytesTestUtil.writeFakeImageData(buffer, lzwMinCodeSize);
        parser.setData(buffer.array());
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(1, header.width);
        Assert.assertEquals(1, header.height);
        Assert.assertEquals(1, header.frameCount);
        Assert.assertNotNull(header.frames.get(0));
        Assert.assertNull(header.frames.get(0).lct);
    }

    @Test
    public void testSetsFrameLocalColorTableIfHasColorTable() {
        final int lzwMinCodeSize = 2;
        final int numColors = 4;
        ByteBuffer buffer = ByteBuffer.allocate(((((GifBytesTestUtil.HEADER_LENGTH) + (GifBytesTestUtil.IMAGE_DESCRIPTOR_LENGTH)) + (GifBytesTestUtil.getImageDataSize())) + (GifBytesTestUtil.getColorTableLength(numColors)))).order(ByteOrder.LITTLE_ENDIAN);
        GifBytesTestUtil.writeHeaderAndLsd(buffer, 1, 1, false, 0);
        /* hasLct */
        GifBytesTestUtil.writeImageDescriptor(buffer, 0, 0, 1, 1, true, numColors);
        GifBytesTestUtil.writeColorTable(buffer, numColors);
        GifBytesTestUtil.writeFakeImageData(buffer, 2);
        parser.setData(buffer.array());
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(1, header.width);
        Assert.assertEquals(1, header.height);
        Assert.assertEquals(1, header.frameCount);
        Assert.assertNotNull(header.frames.get(0));
        GifFrame frame = header.frames.get(0);
        Assert.assertNotNull(frame.lct);
    }

    @Test
    public void testCanParseMultipleFrames() {
        final int lzwMinCodeSize = 2;
        final int expectedFrames = 3;
        final int frameSize = (GifBytesTestUtil.IMAGE_DESCRIPTOR_LENGTH) + (GifBytesTestUtil.getImageDataSize());
        ByteBuffer buffer = ByteBuffer.allocate(((GifBytesTestUtil.HEADER_LENGTH) + (expectedFrames * frameSize))).order(ByteOrder.LITTLE_ENDIAN);
        GifBytesTestUtil.writeHeaderAndLsd(buffer, 1, 1, false, 0);
        for (int i = 0; i < expectedFrames; i++) {
            /* hasLct */
            /* numColors */
            GifBytesTestUtil.writeImageDescriptor(buffer, 0, 0, 1, 1, false, 0);
            GifBytesTestUtil.writeFakeImageData(buffer, 2);
        }
        parser.setData(buffer.array());
        GifHeader header = parser.parseHeader();
        Assert.assertEquals(expectedFrames, header.frameCount);
        Assert.assertEquals(expectedFrames, header.frames.size());
    }

    @Test
    public void testIsAnimatedMultipleFrames() {
        final int lzwMinCodeSize = 2;
        final int numFrames = 3;
        final int frameSize = (GifBytesTestUtil.IMAGE_DESCRIPTOR_LENGTH) + (GifBytesTestUtil.getImageDataSize());
        ByteBuffer buffer = ByteBuffer.allocate(((GifBytesTestUtil.HEADER_LENGTH) + (numFrames * frameSize))).order(ByteOrder.LITTLE_ENDIAN);
        GifBytesTestUtil.writeHeaderAndLsd(buffer, 1, 1, false, 0);
        for (int i = 0; i < numFrames; i++) {
            /* hasLct */
            /* numColors */
            GifBytesTestUtil.writeImageDescriptor(buffer, 0, 0, 1, 1, false, 0);
            GifBytesTestUtil.writeFakeImageData(buffer, 2);
        }
        parser.setData(buffer.array());
        Assert.assertTrue(parser.isAnimated());
    }

    @Test
    public void testIsNotAnimatedOneFrame() {
        final int lzwMinCodeSize = 2;
        final int frameSize = (GifBytesTestUtil.IMAGE_DESCRIPTOR_LENGTH) + (GifBytesTestUtil.getImageDataSize());
        ByteBuffer buffer = ByteBuffer.allocate(((GifBytesTestUtil.HEADER_LENGTH) + frameSize)).order(ByteOrder.LITTLE_ENDIAN);
        GifBytesTestUtil.writeHeaderAndLsd(buffer, 1, 1, false, 0);
        /* hasLct */
        /* numColors */
        GifBytesTestUtil.writeImageDescriptor(buffer, 0, 0, 1, 1, false, 0);
        GifBytesTestUtil.writeFakeImageData(buffer, 2);
        parser.setData(buffer.array());
        Assert.assertFalse(parser.isAnimated());
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfParseHeaderCalledBeforeSetData() {
        GifHeaderParser parser = new GifHeaderParser();
        parser.parseHeader();
    }
}

