package com.bumptech.glide.integration.gifencoder;


import Bitmap.Config.ARGB_8888;
import Bitmap.Config.RGB_565;
import EncodeStrategy.SOURCE;
import EncodeStrategy.TRANSFORMED;
import ReEncodingGifResourceEncoder.ENCODE_TRANSFORMATION;
import android.graphics.Bitmap;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.GifHeader;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.bumptech.glide.gifencoder.AnimatedGifEncoder;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.UnitTransformation;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests for {@link com.bumptech.glide.integration.gifencoder.ReEncodingGifResourceEncoder}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class ReEncodingGifResourceEncoderTest {
    @Mock
    private Resource<GifDrawable> resource;

    @Mock
    private GifDecoder decoder;

    @Mock
    private GifHeaderParser parser;

    @Mock
    private AnimatedGifEncoder gifEncoder;

    @Mock
    private Resource<Bitmap> frameResource;

    @Mock
    private GifDrawable gifDrawable;

    @Mock
    private Transformation<Bitmap> frameTransformation;

    @Mock
    private Resource<Bitmap> transformedResource;

    private ReEncodingGifResourceEncoder encoder;

    private Options options;

    private File file;

    @Test
    public void testEncodeStrategy_withEncodeTransformationTrue_returnsTransformed() {
        assertThat(encoder.getEncodeStrategy(options)).isEqualTo(TRANSFORMED);
    }

    @Test
    public void testEncodeStrategy_withEncodeTransformationUnSet_returnsSource() {
        options.set(ENCODE_TRANSFORMATION, null);
        assertThat(encoder.getEncodeStrategy(options)).isEqualTo(SOURCE);
    }

    @Test
    public void testEncodeStrategy_withEncodeTransformationFalse_returnsSource() {
        options.set(ENCODE_TRANSFORMATION, false);
        assertThat(encoder.getEncodeStrategy(options)).isEqualTo(SOURCE);
    }

    @Test
    public void testEncode_withEncodeTransformationFalse_writesSourceDataToStream() throws IOException {
        options.set(ENCODE_TRANSFORMATION, false);
        String expected = "testString";
        byte[] data = expected.getBytes("UTF-8");
        Mockito.when(gifDrawable.getBuffer()).thenReturn(ByteBuffer.wrap(data));
        Assert.assertTrue(encoder.encode(resource, file, options));
        assertThat(getEncodedData()).isEqualTo(expected);
    }

    @Test
    public void testEncode_WithEncodeTransformationFalse_whenOsThrows_returnsFalse() throws IOException {
        options.set(ENCODE_TRANSFORMATION, false);
        byte[] data = "testString".getBytes("UTF-8");
        Mockito.when(gifDrawable.getBuffer()).thenReturn(ByteBuffer.wrap(data));
        assertThat(file.mkdirs()).isTrue();
        Assert.assertFalse(encoder.encode(resource, file, options));
    }

    @Test
    public void testReturnsFalseIfEncoderFailsToStart() {
        Mockito.when(gifEncoder.start(ArgumentMatchers.any(OutputStream.class))).thenReturn(false);
        Assert.assertFalse(encoder.encode(resource, file, options));
    }

    @Test
    public void testSetsDataOnParserBeforeParsingHeader() {
        ByteBuffer data = ByteBuffer.allocate(1);
        Mockito.when(gifDrawable.getBuffer()).thenReturn(data);
        GifHeader header = Mockito.mock(GifHeader.class);
        Mockito.when(parser.parseHeader()).thenReturn(header);
        encoder.encode(resource, file, options);
        InOrder order = Mockito.inOrder(parser, decoder);
        order.verify(parser).setData(ArgumentMatchers.eq(data));
        order.verify(parser).parseHeader();
        order.verify(decoder).setData(header, data);
    }

    @Test
    public void testAdvancesDecoderBeforeAttemptingToGetFirstFrame() {
        Mockito.when(gifEncoder.start(ArgumentMatchers.any(OutputStream.class))).thenReturn(true);
        Mockito.when(decoder.getFrameCount()).thenReturn(1);
        Mockito.when(decoder.getNextFrame()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        encoder.encode(resource, file, options);
        InOrder order = Mockito.inOrder(decoder);
        order.verify(decoder).advance();
        order.verify(decoder).getNextFrame();
    }

    @Test
    public void testSetsDelayOnEncoderAfterAddingFrame() {
        Mockito.when(gifEncoder.start(ArgumentMatchers.any(OutputStream.class))).thenReturn(true);
        Mockito.when(gifEncoder.addFrame(ArgumentMatchers.any(Bitmap.class))).thenReturn(true);
        Mockito.when(decoder.getFrameCount()).thenReturn(1);
        Mockito.when(decoder.getNextFrame()).thenReturn(Bitmap.createBitmap(100, 100, RGB_565));
        int expectedIndex = 34;
        Mockito.when(decoder.getCurrentFrameIndex()).thenReturn(expectedIndex);
        int expectedDelay = 5000;
        Mockito.when(decoder.getDelay(ArgumentMatchers.eq(expectedIndex))).thenReturn(expectedDelay);
        encoder.encode(resource, file, options);
        InOrder order = Mockito.inOrder(gifEncoder, decoder);
        order.verify(decoder).advance();
        order.verify(gifEncoder).addFrame(ArgumentMatchers.any(Bitmap.class));
        order.verify(gifEncoder).setDelay(ArgumentMatchers.eq(expectedDelay));
        order.verify(decoder).advance();
    }

    @Test
    public void testWritesSingleFrameToEncoderAndReturnsTrueIfEncoderFinishes() {
        Bitmap frame = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(frameResource.get()).thenReturn(frame);
        Mockito.when(decoder.getFrameCount()).thenReturn(1);
        Mockito.when(decoder.getNextFrame()).thenReturn(frame);
        Mockito.when(gifEncoder.start(ArgumentMatchers.any(OutputStream.class))).thenReturn(true);
        Mockito.when(gifEncoder.addFrame(ArgumentMatchers.eq(frame))).thenReturn(true);
        Mockito.when(gifEncoder.finish()).thenReturn(true);
        Assert.assertTrue(encoder.encode(resource, file, options));
        Mockito.verify(gifEncoder).addFrame(ArgumentMatchers.eq(frame));
    }

    @Test
    public void testReturnsFalseIfAddingFrameFails() {
        Mockito.when(decoder.getFrameCount()).thenReturn(1);
        Mockito.when(decoder.getNextFrame()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        Mockito.when(gifEncoder.start(ArgumentMatchers.any(OutputStream.class))).thenReturn(true);
        Mockito.when(gifEncoder.addFrame(ArgumentMatchers.any(Bitmap.class))).thenReturn(false);
        Assert.assertFalse(encoder.encode(resource, file, options));
    }

    @Test
    public void testReturnsFalseIfFinishingFails() {
        Mockito.when(gifEncoder.start(ArgumentMatchers.any(OutputStream.class))).thenReturn(true);
        Mockito.when(gifEncoder.finish()).thenReturn(false);
        Assert.assertFalse(encoder.encode(resource, file, options));
    }

    @Test
    public void testWritesTransformedBitmaps() {
        final Bitmap frame = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(decoder.getFrameCount()).thenReturn(1);
        Mockito.when(decoder.getNextFrame()).thenReturn(frame);
        Mockito.when(gifEncoder.start(ArgumentMatchers.any(OutputStream.class))).thenReturn(true);
        int expectedWidth = 123;
        int expectedHeight = 456;
        Mockito.when(gifDrawable.getIntrinsicWidth()).thenReturn(expectedWidth);
        Mockito.when(gifDrawable.getIntrinsicHeight()).thenReturn(expectedHeight);
        Bitmap transformedFrame = Bitmap.createBitmap(200, 200, RGB_565);
        Mockito.when(transformedResource.get()).thenReturn(transformedFrame);
        Mockito.when(frameTransformation.transform(ReEncodingGifResourceEncoderTest.anyContext(), ArgumentMatchers.eq(frameResource), ArgumentMatchers.eq(expectedWidth), ArgumentMatchers.eq(expectedHeight))).thenReturn(transformedResource);
        Mockito.when(gifDrawable.getFrameTransformation()).thenReturn(frameTransformation);
        encoder.encode(resource, file, options);
        Mockito.verify(gifEncoder).addFrame(ArgumentMatchers.eq(transformedFrame));
    }

    @Test
    public void testRecyclesFrameResourceBeforeWritingIfTransformedResourceIsDifferent() {
        Mockito.when(decoder.getFrameCount()).thenReturn(1);
        Mockito.when(frameTransformation.transform(ReEncodingGifResourceEncoderTest.anyContext(), ArgumentMatchers.eq(frameResource), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(transformedResource);
        Bitmap expected = Bitmap.createBitmap(200, 200, ARGB_8888);
        Mockito.when(transformedResource.get()).thenReturn(expected);
        Mockito.when(gifEncoder.start(ArgumentMatchers.any(OutputStream.class))).thenReturn(true);
        encoder.encode(resource, file, options);
        InOrder order = Mockito.inOrder(frameResource, gifEncoder);
        order.verify(frameResource).recycle();
        order.verify(gifEncoder).addFrame(ArgumentMatchers.eq(expected));
    }

    @Test
    public void testRecyclesTransformedResourceAfterWritingIfTransformedResourceIsDifferent() {
        Mockito.when(decoder.getFrameCount()).thenReturn(1);
        Bitmap expected = Bitmap.createBitmap(100, 200, RGB_565);
        Mockito.when(transformedResource.get()).thenReturn(expected);
        Mockito.when(frameTransformation.transform(ReEncodingGifResourceEncoderTest.anyContext(), ArgumentMatchers.eq(frameResource), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(transformedResource);
        Mockito.when(gifEncoder.start(ArgumentMatchers.any(OutputStream.class))).thenReturn(true);
        encoder.encode(resource, file, options);
        InOrder order = Mockito.inOrder(transformedResource, gifEncoder);
        order.verify(gifEncoder).addFrame(ArgumentMatchers.eq(expected));
        order.verify(transformedResource).recycle();
    }

    @Test
    public void testRecyclesFrameResourceAfterWritingIfFrameResourceIsNotTransformed() {
        Mockito.when(decoder.getFrameCount()).thenReturn(1);
        Mockito.when(frameTransformation.transform(ReEncodingGifResourceEncoderTest.anyContext(), ArgumentMatchers.eq(frameResource), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(frameResource);
        Bitmap expected = Bitmap.createBitmap(200, 100, ARGB_8888);
        Mockito.when(frameResource.get()).thenReturn(expected);
        Mockito.when(gifEncoder.start(ArgumentMatchers.any(OutputStream.class))).thenReturn(true);
        encoder.encode(resource, file, options);
        InOrder order = Mockito.inOrder(frameResource, gifEncoder);
        order.verify(gifEncoder).addFrame(ArgumentMatchers.eq(expected));
        order.verify(frameResource).recycle();
    }

    @Test
    public void testWritesBytesDirectlyToDiskIfTransformationIsUnitTransformation() {
        Mockito.when(gifDrawable.getFrameTransformation()).thenReturn(UnitTransformation.<Bitmap>get());
        String expected = "expected";
        Mockito.when(gifDrawable.getBuffer()).thenReturn(ByteBuffer.wrap(expected.getBytes()));
        encoder.encode(resource, file, options);
        assertThat(getEncodedData()).isEqualTo(expected);
        Mockito.verify(gifEncoder, Mockito.never()).start(ArgumentMatchers.any(OutputStream.class));
        Mockito.verify(parser, Mockito.never()).setData(ArgumentMatchers.any(byte[].class));
        Mockito.verify(parser, Mockito.never()).parseHeader();
    }
}

