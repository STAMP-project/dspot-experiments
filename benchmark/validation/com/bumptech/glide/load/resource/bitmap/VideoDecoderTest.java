package com.bumptech.glide.load.resource.bitmap;


import Bitmap.Config.ARGB_8888;
import DownsampleStrategy.AT_LEAST;
import DownsampleStrategy.OPTION;
import MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT;
import MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION;
import MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH;
import MediaMetadataRetriever.OPTION_CLOSEST_SYNC;
import Target.SIZE_ORIGINAL;
import VideoDecoder.DEFAULT_FRAME;
import VideoDecoder.DEFAULT_FRAME_OPTION;
import VideoDecoder.MediaMetadataRetrieverFactory;
import VideoDecoder.TARGET_FRAME;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.tests.Util;
import com.bumptech.glide.util.Preconditions;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 27)
public class VideoDecoderTest {
    @Mock
    private ParcelFileDescriptor resource;

    @Mock
    private MediaMetadataRetrieverFactory factory;

    @Mock
    private VideoDecoder.MediaMetadataRetrieverInitializer<ParcelFileDescriptor> initializer;

    @Mock
    private MediaMetadataRetriever retriever;

    @Mock
    private BitmapPool bitmapPool;

    private VideoDecoder<ParcelFileDescriptor> decoder;

    private Options options;

    private int initialSdkVersion;

    @Test
    public void testReturnsRetrievedFrameForResource() throws IOException {
        Util.setSdkVersionInt(19);
        Bitmap expected = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(retriever.getFrameAtTime(DEFAULT_FRAME, DEFAULT_FRAME_OPTION)).thenReturn(expected);
        Resource<Bitmap> result = Preconditions.checkNotNull(decoder.decode(resource, 100, 100, options));
        Mockito.verify(initializer).initialize(retriever, resource);
        Assert.assertEquals(expected, result.get());
    }

    @Test
    public void testReleasesMediaMetadataRetriever() throws IOException {
        Util.setSdkVersionInt(19);
        decoder.decode(resource, 1, 2, options);
        Mockito.verify(retriever).release();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionIfCalledWithInvalidFrame() throws IOException {
        Util.setSdkVersionInt(19);
        options.set(TARGET_FRAME, (-5L));
        new VideoDecoder(bitmapPool, initializer, factory).decode(resource, 100, 100, options);
    }

    @Test
    public void testSpecifiesThumbnailFrameIfICalledWithFrameNumber() throws IOException {
        Util.setSdkVersionInt(19);
        long frame = 5;
        options.set(TARGET_FRAME, frame);
        decoder = new VideoDecoder(bitmapPool, initializer, factory);
        decoder.decode(resource, 100, 100, options);
        Mockito.verify(retriever).getFrameAtTime(frame, DEFAULT_FRAME_OPTION);
    }

    @Test
    public void testDoesNotSpecifyThumbnailFrameIfCalledWithoutFrameNumber() throws IOException {
        Util.setSdkVersionInt(19);
        decoder = new VideoDecoder(bitmapPool, initializer, factory);
        decoder.decode(resource, 100, 100, options);
        Mockito.verify(retriever).getFrameAtTime(DEFAULT_FRAME, DEFAULT_FRAME_OPTION);
    }

    @Test
    public void getScaledFrameAtTime() throws IOException {
        // Anything other than NONE.
        options.set(OPTION, AT_LEAST);
        Bitmap expected = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(retriever.extractMetadata(METADATA_KEY_VIDEO_WIDTH)).thenReturn("100");
        Mockito.when(retriever.extractMetadata(METADATA_KEY_VIDEO_HEIGHT)).thenReturn("100");
        Mockito.when(retriever.extractMetadata(METADATA_KEY_VIDEO_ROTATION)).thenReturn("0");
        Mockito.when(retriever.getScaledFrameAtTime((-1), OPTION_CLOSEST_SYNC, 100, 100)).thenReturn(expected);
        assertThat(decoder.decode(resource, 100, 100, options).get()).isSameAs(expected);
    }

    @Test
    public void decodeFrame_withTargetSizeOriginal_onApi27_doesNotThrow() throws IOException {
        Bitmap expected = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(retriever.getFrameAtTime((-1), OPTION_CLOSEST_SYNC)).thenReturn(expected);
        Mockito.verify(retriever, Mockito.never()).getScaledFrameAtTime(anyLong(), anyInt(), anyInt(), anyInt());
        assertThat(decoder.decode(resource, SIZE_ORIGINAL, SIZE_ORIGINAL, options).get()).isSameAs(expected);
    }

    @Test
    public void decodeFrame_withTargetSizeOriginalWidthOnly_onApi27_doesNotThrow() throws IOException {
        Bitmap expected = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(retriever.getFrameAtTime((-1), OPTION_CLOSEST_SYNC)).thenReturn(expected);
        Mockito.verify(retriever, Mockito.never()).getScaledFrameAtTime(anyLong(), anyInt(), anyInt(), anyInt());
        assertThat(decoder.decode(resource, SIZE_ORIGINAL, 100, options).get()).isSameAs(expected);
    }

    @Test
    public void decodeFrame_withTargetSizeOriginalHeightOnly_onApi27_doesNotThrow() throws IOException {
        Bitmap expected = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(retriever.getFrameAtTime((-1), OPTION_CLOSEST_SYNC)).thenReturn(expected);
        Mockito.verify(retriever, Mockito.never()).getScaledFrameAtTime(anyLong(), anyInt(), anyInt(), anyInt());
        assertThat(decoder.decode(resource, 100, SIZE_ORIGINAL, options).get()).isSameAs(expected);
    }
}

