package com.bumptech.glide.load.resource.gif;


import Bitmap.Config.ARGB_8888;
import Bitmap.Config.RGB_565;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.gif.GifFrameLoader.DelayTarget;
import com.bumptech.glide.load.resource.gif.GifFrameLoader.FrameCallback;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.tests.TearDownGlide;
import com.bumptech.glide.util.Util;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class GifFrameLoaderTest {
    @Rule
    public TearDownGlide tearDownGlide = new TearDownGlide();

    @Mock
    private FrameCallback callback;

    @Mock
    private GifDecoder gifDecoder;

    @Mock
    private Handler handler;

    @Mock
    private Transformation<Bitmap> transformation;

    @Mock
    private RequestManager requestManager;

    private GifFrameLoader loader;

    private RequestBuilder<Bitmap> requestBuilder;

    private Bitmap firstFrame;

    @SuppressWarnings("unchecked")
    @Test
    public void testSetFrameTransformationSetsTransformationOnRequestBuilder() {
        Mockito.verify(requestBuilder, Mockito.times(2)).apply(ArgumentMatchers.isA(RequestOptions.class));
        Transformation<Bitmap> transformation = Mockito.mock(Transformation.class);
        loader.setFrameTransformation(transformation, firstFrame);
        Mockito.verify(requestBuilder, Mockito.times(3)).apply(ArgumentMatchers.isA(RequestOptions.class));
    }

    @Test(expected = NullPointerException.class)
    public void testSetFrameTransformationThrowsIfGivenNullTransformation() {
        loader.setFrameTransformation(null, null);
    }

    @Test
    public void testReturnsSizeFromGifDecoderAndCurrentFrame() {
        int decoderByteSize = 123456;
        Mockito.when(gifDecoder.getByteSize()).thenReturn(decoderByteSize);
        assertThat(loader.getSize()).isEqualTo((decoderByteSize + (Util.getBitmapByteSize(firstFrame))));
    }

    @Test
    public void testStartGetsNextFrameIfNotStartedAndWithNoLoadPending() {
        Mockito.verify(requestBuilder).into(GifFrameLoaderTest.aTarget());
    }

    @Test
    public void testGetNextFrameIncrementsSignatureAndAdvancesDecoderBeforeStartingLoad() {
        InOrder order = Mockito.inOrder(gifDecoder, requestBuilder);
        order.verify(gifDecoder).advance();
        order.verify(requestBuilder).apply(ArgumentMatchers.isA(RequestOptions.class));
        order.verify(requestBuilder).into(GifFrameLoaderTest.aTarget());
    }

    @Test
    public void testGetCurrentFrameReturnsFirstFrameWHenNoLoadHasCompleted() {
        assertThat(loader.getCurrentFrame()).isEqualTo(firstFrame);
    }

    @Test
    public void testGetCurrentFrameReturnsCurrentBitmapAfterLoadHasCompleted() {
        final Bitmap result = Bitmap.createBitmap(100, 200, ARGB_8888);
        DelayTarget target = Mockito.mock(DelayTarget.class);
        Mockito.when(target.getResource()).thenReturn(result);
        loader.onFrameReady(target);
        Assert.assertEquals(result, loader.getCurrentFrame());
    }

    @Test
    public void testStartDoesNotStartIfAlreadyRunning() {
        loader.subscribe(Mockito.mock(FrameCallback.class));
        Mockito.verify(requestBuilder, Mockito.times(1)).into(GifFrameLoaderTest.aTarget());
    }

    @Test
    public void testGetNextFrameDoesNotStartLoadIfLoaderIsNotRunning() {
        Mockito.verify(requestBuilder, Mockito.times(1)).into(GifFrameLoaderTest.aTarget());
        loader.unsubscribe(callback);
        loader.onFrameReady(Mockito.mock(DelayTarget.class));
        Mockito.verify(requestBuilder, Mockito.times(1)).into(GifFrameLoaderTest.aTarget());
    }

    @Test
    public void testGetNextFrameDoesNotStartLoadIfLoadIsInProgress() {
        loader.unsubscribe(callback);
        loader.subscribe(callback);
        Mockito.verify(requestBuilder, Mockito.times(1)).into(GifFrameLoaderTest.aTarget());
    }

    @Test
    public void testGetNextFrameDoesStartLoadIfRestartedAndNoLoadIsInProgress() {
        loader.unsubscribe(callback);
        loader.onFrameReady(Mockito.mock(DelayTarget.class));
        loader.subscribe(callback);
        Mockito.verify(requestBuilder, Mockito.times(2)).into(GifFrameLoaderTest.aTarget());
    }

    @Test
    public void testGetNextFrameDoesStartLoadAfterLoadCompletesIfStarted() {
        loader.onFrameReady(Mockito.mock(DelayTarget.class));
        Mockito.verify(requestBuilder, Mockito.times(2)).into(GifFrameLoaderTest.aTarget());
    }

    @Test
    public void testOnFrameReadyClearsPreviousFrame() {
        // Force the loader to create a real Handler.
        loader = createGifFrameLoader(null);
        DelayTarget previous = Mockito.mock(DelayTarget.class);
        Request previousRequest = Mockito.mock(Request.class);
        Mockito.when(previous.getRequest()).thenReturn(previousRequest);
        Mockito.when(previous.getResource()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        DelayTarget current = Mockito.mock(DelayTarget.class);
        Mockito.when(current.getResource()).thenReturn(Bitmap.createBitmap(100, 100, RGB_565));
        loader.onFrameReady(previous);
        loader.onFrameReady(current);
        Mockito.verify(requestManager).clear(ArgumentMatchers.eq(previous));
    }

    @Test
    public void testOnFrameReadyWithNullResourceDoesNotClearPreviousFrame() {
        // Force the loader to create a real Handler by passing null.
        loader = createGifFrameLoader(null);
        DelayTarget previous = Mockito.mock(DelayTarget.class);
        Request previousRequest = Mockito.mock(Request.class);
        Mockito.when(previous.getRequest()).thenReturn(previousRequest);
        Mockito.when(previous.getResource()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        DelayTarget current = Mockito.mock(DelayTarget.class);
        Mockito.when(current.getResource()).thenReturn(null);
        loader.onFrameReady(previous);
        loader.onFrameReady(current);
        Mockito.verify(previousRequest, Mockito.never()).clear();
    }

    @Test
    public void testDelayTargetSendsMessageWithHandlerDelayed() {
        long targetTime = 1234;
        DelayTarget delayTarget = new DelayTarget(handler, 1, targetTime);
        /* glideAnimation */
        delayTarget.onResourceReady(Bitmap.createBitmap(100, 100, ARGB_8888), null);
        Mockito.verify(handler).sendMessageAtTime(ArgumentMatchers.isA(Message.class), ArgumentMatchers.eq(targetTime));
    }

    @Test
    public void testDelayTargetSetsResourceOnResourceReady() {
        DelayTarget delayTarget = new DelayTarget(handler, 1, 1);
        Bitmap expected = Bitmap.createBitmap(100, 200, RGB_565);
        /* glideAnimation */
        delayTarget.onResourceReady(expected, null);
        Assert.assertEquals(expected, delayTarget.getResource());
    }

    @Test
    public void testClearsCompletedLoadOnFrameReadyIfCleared() {
        // Force the loader to create a real Handler by passing null;
        loader = createGifFrameLoader(null);
        loader.clear();
        DelayTarget delayTarget = Mockito.mock(DelayTarget.class);
        Request request = Mockito.mock(Request.class);
        Mockito.when(delayTarget.getRequest()).thenReturn(request);
        loader.onFrameReady(delayTarget);
        Mockito.verify(requestManager).clear(ArgumentMatchers.eq(delayTarget));
    }

    @Test
    public void testDoesNotReturnResourceForCompletedFrameInGetCurrentFrameIfLoadCompletesWhileCleared() {
        loader.clear();
        DelayTarget delayTarget = Mockito.mock(DelayTarget.class);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(delayTarget.getResource()).thenReturn(bitmap);
        loader.onFrameReady(delayTarget);
        Assert.assertNull(loader.getCurrentFrame());
    }

    @Test
    public void onFrameReady_whenNotRunning_doesNotClearPreviouslyLoadedImage() {
        loader = /* handler= */
        createGifFrameLoader(null);
        DelayTarget loaded = Mockito.mock(DelayTarget.class);
        Mockito.when(loaded.getResource()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        loader.onFrameReady(loaded);
        loader.unsubscribe(callback);
        DelayTarget nextFrame = Mockito.mock(DelayTarget.class);
        Mockito.when(nextFrame.getResource()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        loader.onFrameReady(nextFrame);
        Mockito.verify(requestManager, Mockito.never()).clear(loaded);
    }

    @Test
    public void onFrameReady_whenNotRunning_clearsPendingFrameOnClear() {
        loader = /* handler= */
        createGifFrameLoader(null);
        DelayTarget loaded = Mockito.mock(DelayTarget.class);
        Mockito.when(loaded.getResource()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        loader.onFrameReady(loaded);
        loader.unsubscribe(callback);
        DelayTarget nextFrame = Mockito.mock(DelayTarget.class);
        Mockito.when(nextFrame.getResource()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        loader.onFrameReady(nextFrame);
        loader.clear();
        Mockito.verify(requestManager).clear(loaded);
        Mockito.verify(requestManager).clear(nextFrame);
    }

    @Test
    public void onFrameReady_whenNotRunning_clearsOldFrameOnStart() {
        loader = /* handler= */
        createGifFrameLoader(null);
        DelayTarget loaded = Mockito.mock(DelayTarget.class);
        Mockito.when(loaded.getResource()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        loader.onFrameReady(loaded);
        loader.unsubscribe(callback);
        DelayTarget nextFrame = Mockito.mock(DelayTarget.class);
        Mockito.when(nextFrame.getResource()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        loader.onFrameReady(nextFrame);
        loader.subscribe(callback);
        Mockito.verify(requestManager).clear(loaded);
    }

    @Test
    public void onFrameReady_whenNotRunning_callsFrameReadyWithNewFrameOnStart() {
        loader = /* handler= */
        createGifFrameLoader(null);
        DelayTarget loaded = Mockito.mock(DelayTarget.class);
        Mockito.when(loaded.getResource()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        loader.onFrameReady(loaded);
        loader.unsubscribe(callback);
        DelayTarget nextFrame = Mockito.mock(DelayTarget.class);
        Bitmap expected = Bitmap.createBitmap(200, 200, ARGB_8888);
        Mockito.when(nextFrame.getResource()).thenReturn(expected);
        loader.onFrameReady(nextFrame);
        Mockito.verify(callback, Mockito.times(1)).onFrameReady();
        loader.subscribe(callback);
        Mockito.verify(callback, Mockito.times(2)).onFrameReady();
        assertThat(loader.getCurrentFrame()).isEqualTo(expected);
    }

    @Test
    public void startFromFirstFrame_withPendingFrame_clearsPendingFrame() {
        loader = /* handler= */
        createGifFrameLoader(null);
        DelayTarget loaded = Mockito.mock(DelayTarget.class);
        Mockito.when(loaded.getResource()).thenReturn(Bitmap.createBitmap(100, 100, ARGB_8888));
        loader.onFrameReady(loaded);
        loader.unsubscribe(callback);
        DelayTarget nextFrame = Mockito.mock(DelayTarget.class);
        Bitmap expected = Bitmap.createBitmap(200, 200, ARGB_8888);
        Mockito.when(nextFrame.getResource()).thenReturn(expected);
        loader.onFrameReady(nextFrame);
        loader.setNextStartFromFirstFrame();
        Mockito.verify(requestManager).clear(nextFrame);
        loader.subscribe(callback);
        Mockito.verify(callback, Mockito.times(1)).onFrameReady();
    }
}

