package com.bumptech.glide.load.resource.gif;


import Bitmap.Config.ARGB_4444;
import Bitmap.Config.ARGB_8888;
import Bitmap.Config.RGB_565;
import Drawable.Callback;
import GifDecoder.TOTAL_ITERATION_COUNT_FOREVER;
import GifDrawable.LOOP_FOREVER;
import GifDrawable.LOOP_INTRINSIC;
import PixelFormat.TRANSPARENT;
import RuntimeEnvironment.application;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.tests.GlideShadowLooper;
import com.bumptech.glide.tests.TearDownGlide;
import com.bumptech.glide.util.Preconditions;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowCanvas;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, shadows = { GlideShadowLooper.class, GifDrawableTest.BitmapTrackingShadowCanvas.class })
public class GifDrawableTest {
    @Rule
    public final TearDownGlide tearDownGlide = new TearDownGlide();

    private GifDrawable drawable;

    private int frameHeight;

    private int frameWidth;

    private Bitmap firstFrame;

    private int initialSdkVersion;

    @Mock
    private Callback cb;

    @Mock
    private GifFrameLoader frameLoader;

    @Mock
    private Paint paint;

    @Mock
    private Transformation<Bitmap> transformation;

    private Application context;

    // containsExactly doesn't need its return value checked.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testShouldDrawFirstFrameBeforeAnyFrameRead() {
        Canvas canvas = new Canvas();
        drawable.draw(canvas);
        GifDrawableTest.BitmapTrackingShadowCanvas shadowCanvas = Shadow.extract(canvas);
        assertThat(shadowCanvas.getDrawnBitmaps()).containsExactly(firstFrame);
    }

    @Test
    public void testDoesDrawCurrentFrameIfOneIsAvailable() {
        Canvas canvas = Mockito.mock(Canvas.class);
        Bitmap currentFrame = Bitmap.createBitmap(100, 100, ARGB_4444);
        Mockito.when(frameLoader.getCurrentFrame()).thenReturn(currentFrame);
        drawable.draw(canvas);
        Mockito.verify(canvas).drawBitmap(ArgumentMatchers.eq(currentFrame), ((Rect) (ArgumentMatchers.isNull())), GifDrawableTest.isARect(), GifDrawableTest.isAPaint());
        Mockito.verify(canvas, Mockito.never()).drawBitmap(ArgumentMatchers.eq(firstFrame), ((Rect) (ArgumentMatchers.isNull())), GifDrawableTest.isARect(), GifDrawableTest.isAPaint());
    }

    @Test
    public void testRequestsNextFrameOnStart() {
        drawable.setVisible(true, true);
        drawable.start();
        Mockito.verify(frameLoader).subscribe(ArgumentMatchers.eq(drawable));
    }

    @Test
    public void testRequestsNextFrameOnStartWithoutCallToSetVisible() {
        drawable.start();
        Mockito.verify(frameLoader).subscribe(ArgumentMatchers.eq(drawable));
    }

    @Test
    public void testDoesNotRequestNextFrameOnStartIfGotCallToSetVisibleWithVisibleFalse() {
        drawable.setVisible(false, false);
        drawable.start();
        Mockito.verify(frameLoader, Mockito.never()).subscribe(ArgumentMatchers.eq(drawable));
    }

    @Test
    public void testDoesNotRequestNextFrameOnStartIfHasSingleFrame() {
        Mockito.when(frameLoader.getFrameCount()).thenReturn(1);
        drawable.setVisible(true, false);
        drawable.start();
        Mockito.verify(frameLoader, Mockito.never()).subscribe(ArgumentMatchers.eq(drawable));
    }

    @Test
    public void testInvalidatesSelfOnStartIfHasSingleFrame() {
        Mockito.when(frameLoader.getFrameCount()).thenReturn(1);
        drawable.setVisible(true, false);
        drawable.start();
        Mockito.verify(cb).invalidateDrawable(ArgumentMatchers.eq(drawable));
    }

    @Test
    public void testShouldInvalidateSelfOnRun() {
        drawable.setVisible(true, true);
        drawable.start();
        Mockito.verify(cb).invalidateDrawable(ArgumentMatchers.eq(drawable));
    }

    @Test
    public void testShouldNotScheduleItselfIfAlreadyRunning() {
        drawable.setVisible(true, true);
        drawable.start();
        drawable.start();
        Mockito.verify(frameLoader, Mockito.times(1)).subscribe(ArgumentMatchers.eq(drawable));
    }

    @Test
    public void testReturnsFalseFromIsRunningWhenNotRunning() {
        Assert.assertFalse(drawable.isRunning());
    }

    @Test
    public void testReturnsTrueFromIsRunningWhenRunning() {
        drawable.setVisible(true, true);
        drawable.start();
        Assert.assertTrue(drawable.isRunning());
    }

    @Test
    public void testInvalidatesSelfWhenFrameReady() {
        drawable.setIsRunning(true);
        drawable.onFrameReady();
        Mockito.verify(cb).invalidateDrawable(ArgumentMatchers.eq(drawable));
    }

    @Test
    public void testDoesNotStartLoadingNextFrameWhenCurrentFinishesIfHasNoCallback() {
        drawable.setIsRunning(true);
        drawable.setCallback(null);
        drawable.onFrameReady();
        Mockito.verify(frameLoader).unsubscribe(ArgumentMatchers.eq(drawable));
    }

    @Test
    public void testStopsWhenCurrentFrameFinishesIfHasNoCallback() {
        drawable.setIsRunning(true);
        drawable.setCallback(null);
        drawable.onFrameReady();
        Assert.assertFalse(drawable.isRunning());
    }

    @Test
    public void testUnsubscribesWhenCurrentFinishesIfHasNoCallback() {
        drawable.setIsRunning(true);
        drawable.setCallback(null);
        drawable.onFrameReady();
        Mockito.verify(frameLoader).unsubscribe(ArgumentMatchers.eq(drawable));
    }

    @Test
    public void testSetsIsRunningFalseOnStop() {
        drawable.start();
        drawable.stop();
        Assert.assertFalse(drawable.isRunning());
    }

    @Test
    public void testStopsOnSetVisibleFalse() {
        drawable.start();
        drawable.setVisible(false, true);
        Assert.assertFalse(drawable.isRunning());
    }

    @Test
    public void testStartsOnSetVisibleTrueIfRunning() {
        drawable.start();
        drawable.setVisible(false, false);
        drawable.setVisible(true, true);
        Assert.assertTrue(drawable.isRunning());
    }

    @Test
    public void testDoesNotStartOnVisibleTrueIfNotRunning() {
        drawable.setVisible(true, true);
        Assert.assertFalse(drawable.isRunning());
    }

    @Test
    public void testDoesNotStartOnSetVisibleIfStartedAndStopped() {
        drawable.start();
        drawable.stop();
        drawable.setVisible(true, true);
        Assert.assertFalse(drawable.isRunning());
    }

    @Test
    public void testDoesNotImmediatelyRunIfStartedWhileNotVisible() {
        drawable.setVisible(false, false);
        drawable.start();
        Assert.assertFalse(drawable.isRunning());
    }

    @Test
    public void testGetOpacityReturnsTransparent() {
        Assert.assertEquals(TRANSPARENT, drawable.getOpacity());
    }

    @Test
    public void testReturnsFrameCountFromDecoder() {
        int expected = 4;
        Mockito.when(frameLoader.getFrameCount()).thenReturn(expected);
        Assert.assertEquals(expected, drawable.getFrameCount());
    }

    @Test
    public void testReturnsDefaultFrameIndex() {
        final int expected = -1;
        Mockito.when(frameLoader.getCurrentIndex()).thenReturn(expected);
        Assert.assertEquals(expected, drawable.getFrameIndex());
    }

    @Test
    public void testReturnsNonDefaultFrameIndex() {
        final int expected = 100;
        Mockito.when(frameLoader.getCurrentIndex()).thenReturn(expected);
        Assert.assertEquals(expected, drawable.getFrameIndex());
    }

    @Test
    public void testRecycleCallsClearOnFrameManager() {
        drawable.recycle();
        Mockito.verify(frameLoader).clear();
    }

    @Test
    public void testIsNotRecycledIfNotRecycled() {
        Assert.assertFalse(drawable.isRecycled());
    }

    @Test
    public void testIsRecycledAfterRecycled() {
        drawable.recycle();
        Assert.assertTrue(drawable.isRecycled());
    }

    @Test
    public void testReturnsNonNullConstantState() {
        Assert.assertNotNull(drawable.getConstantState());
    }

    @Test
    public void testReturnsSizeFromFrameLoader() {
        int size = 1243;
        Mockito.when(frameLoader.getSize()).thenReturn(size);
        assertThat(drawable.getSize()).isEqualTo(size);
    }

    @Test
    public void testReturnsNewDrawableFromConstantState() {
        Bitmap firstFrame = Bitmap.createBitmap(100, 100, ARGB_8888);
        drawable = new GifDrawable(RuntimeEnvironment.application, Mockito.mock(GifDecoder.class), transformation, 100, 100, firstFrame);
        Assert.assertNotNull(Preconditions.checkNotNull(drawable.getConstantState()).newDrawable());
        Assert.assertNotNull(drawable.getConstantState().newDrawable(application.getResources()));
    }

    @Test
    public void testReturnsFrameWidthAndHeightForIntrinsicDimensions() {
        Assert.assertEquals(frameWidth, drawable.getIntrinsicWidth());
        Assert.assertEquals(frameHeight, drawable.getIntrinsicHeight());
    }

    @Test
    public void testLoopsASingleTimeIfLoopCountIsSetToOne() {
        final int loopCount = 1;
        final int frameCount = 2;
        Mockito.when(frameLoader.getFrameCount()).thenReturn(frameCount);
        drawable.setLoopCount(loopCount);
        drawable.setVisible(true, true);
        drawable.start();
        runLoops(loopCount, frameCount);
        verifyRanLoops(loopCount, frameCount);
        Assert.assertFalse("drawable should be stopped after loop is completed", drawable.isRunning());
    }

    @Test
    public void testLoopsForeverIfLoopCountIsSetToLoopForever() {
        final int loopCount = 40;
        final int frameCount = 2;
        Mockito.when(frameLoader.getFrameCount()).thenReturn(frameCount);
        drawable.setLoopCount(LOOP_FOREVER);
        drawable.setVisible(true, true);
        drawable.start();
        runLoops(loopCount, frameCount);
        verifyRanLoops(loopCount, frameCount);
        Assert.assertTrue("drawable should be still running", drawable.isRunning());
    }

    @Test
    public void testLoopsOnceIfLoopCountIsSetToOneWithThreeFrames() {
        final int loopCount = 1;
        final int frameCount = 3;
        Mockito.when(frameLoader.getFrameCount()).thenReturn(frameCount);
        drawable.setLoopCount(loopCount);
        drawable.setVisible(true, true);
        drawable.start();
        runLoops(loopCount, frameCount);
        verifyRanLoops(loopCount, frameCount);
        Assert.assertFalse("drawable should be stopped after loop is completed", drawable.isRunning());
    }

    @Test
    public void testLoopsThreeTimesIfLoopCountIsSetToThree() {
        final int loopCount = 3;
        final int frameCount = 2;
        Mockito.when(frameLoader.getFrameCount()).thenReturn(frameCount);
        drawable.setLoopCount(loopCount);
        drawable.setVisible(true, true);
        drawable.start();
        runLoops(loopCount, frameCount);
        verifyRanLoops(loopCount, frameCount);
        Assert.assertFalse("drawable should be stopped after loop is completed", drawable.isRunning());
    }

    @Test
    public void testCallingStartResetsLoopCounter() {
        Mockito.when(frameLoader.getFrameCount()).thenReturn(2);
        drawable.setLoopCount(1);
        drawable.setVisible(true, true);
        drawable.start();
        drawable.onFrameReady();
        Mockito.when(frameLoader.getCurrentIndex()).thenReturn(1);
        drawable.onFrameReady();
        Assert.assertFalse("drawable should be stopped after loop is completed", drawable.isRunning());
        drawable.start();
        Mockito.when(frameLoader.getCurrentIndex()).thenReturn(0);
        drawable.onFrameReady();
        Mockito.when(frameLoader.getCurrentIndex()).thenReturn(1);
        drawable.onFrameReady();
        // 4 onFrameReady(), 2 start()
        Mockito.verify(cb, Mockito.times((4 + 2))).invalidateDrawable(ArgumentMatchers.eq(drawable));
        Assert.assertFalse("drawable should be stopped after loop is completed", drawable.isRunning());
    }

    @Test
    public void testChangingTheLoopCountAfterHittingTheMaxLoopCount() {
        final int initialLoopCount = 1;
        final int frameCount = 2;
        Mockito.when(frameLoader.getFrameCount()).thenReturn(frameCount);
        drawable.setLoopCount(initialLoopCount);
        drawable.setVisible(true, true);
        drawable.start();
        runLoops(initialLoopCount, frameCount);
        Assert.assertFalse("drawable should be stopped after loop is completed", drawable.isRunning());
        final int newLoopCount = 2;
        drawable.setLoopCount(newLoopCount);
        drawable.start();
        runLoops(newLoopCount, frameCount);
        int numStarts = 2;
        int expectedFrames = ((initialLoopCount + newLoopCount) * frameCount) + numStarts;
        Mockito.verify(cb, Mockito.times(expectedFrames)).invalidateDrawable(ArgumentMatchers.eq(drawable));
        Assert.assertFalse("drawable should be stopped after loop is completed", drawable.isRunning());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfGivenLoopCountLessThanZeroAndNotInfinite() {
        drawable.setLoopCount((-2));
    }

    @Test
    public void testUsesDecoderTotalLoopCountIfLoopCountIsLoopIntrinsic() {
        final int frameCount = 3;
        final int loopCount = 2;
        Mockito.when(frameLoader.getLoopCount()).thenReturn(loopCount);
        Mockito.when(frameLoader.getFrameCount()).thenReturn(frameCount);
        drawable.setLoopCount(LOOP_INTRINSIC);
        drawable.setVisible(true, true);
        drawable.start();
        runLoops(loopCount, frameCount);
        verifyRanLoops(loopCount, frameCount);
        Assert.assertFalse("drawable should be stopped after loop is completed", drawable.isRunning());
    }

    @Test
    public void testLoopsForeverIfLoopCountIsLoopIntrinsicAndTotalIterationCountIsForever() {
        final int frameCount = 3;
        final int loopCount = 40;
        Mockito.when(frameLoader.getLoopCount()).thenReturn(TOTAL_ITERATION_COUNT_FOREVER);
        Mockito.when(frameLoader.getFrameCount()).thenReturn(frameCount);
        drawable.setLoopCount(LOOP_INTRINSIC);
        drawable.setVisible(true, true);
        drawable.start();
        runLoops(loopCount, frameCount);
        verifyRanLoops(loopCount, frameCount);
        Assert.assertTrue("drawable should be still running", drawable.isRunning());
    }

    @Test
    public void testDoesNotDrawFrameAfterRecycle() {
        Bitmap bitmap = Bitmap.createBitmap(100, 112341, RGB_565);
        drawable.setVisible(true, true);
        drawable.start();
        Mockito.when(frameLoader.getCurrentFrame()).thenReturn(bitmap);
        drawable.onFrameReady();
        drawable.recycle();
        Canvas canvas = Mockito.mock(Canvas.class);
        drawable.draw(canvas);
        Mockito.verify(canvas, Mockito.never()).drawBitmap(ArgumentMatchers.eq(bitmap), GifDrawableTest.isARect(), GifDrawableTest.isARect(), GifDrawableTest.isAPaint());
    }

    @Test
    public void testSetsFrameTransformationOnFrameManager() {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        drawable.setFrameTransformation(transformation, bitmap);
        Mockito.verify(frameLoader).setFrameTransformation(ArgumentMatchers.eq(transformation), ArgumentMatchers.eq(bitmap));
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfConstructedWithNullFirstFrame() {
        new GifDrawable(RuntimeEnvironment.application, Mockito.mock(GifDecoder.class), transformation, 100, 100, null);
    }

    @Test
    public void testAppliesGravityOnDrawAfterBoundsChange() {
        Rect bounds = new Rect(0, 0, ((frameWidth) * 2), ((frameHeight) * 2));
        drawable.setBounds(bounds);
        Canvas canvas = Mockito.mock(Canvas.class);
        drawable.draw(canvas);
        Mockito.verify(canvas).drawBitmap(ArgumentMatchers.isA(Bitmap.class), ((Rect) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(bounds), ArgumentMatchers.eq(paint));
    }

    @Test
    public void testSetAlphaSetsAlphaOnPaint() {
        int alpha = 100;
        drawable.setAlpha(alpha);
        Mockito.verify(paint).setAlpha(ArgumentMatchers.eq(alpha));
    }

    @Test
    public void testSetColorFilterSetsColorFilterOnPaint() {
        ColorFilter colorFilter = new android.graphics.PorterDuffColorFilter(Color.RED, Mode.ADD);
        drawable.setColorFilter(colorFilter);
        // Use ArgumentCaptor instead of eq() due to b/73121412 where ShadowPorterDuffColorFilter.equals
        // uses a method that can't be found (PorterDuffColorFilter.getColor).
        ArgumentCaptor<ColorFilter> captor = ArgumentCaptor.forClass(ColorFilter.class);
        Mockito.verify(paint).setColorFilter(captor.capture());
        assertThat(captor.getValue()).isSameAs(colorFilter);
    }

    @Test
    public void testReturnsCurrentTransformationInGetFrameTransformation() {
        @SuppressWarnings("unchecked")
        Transformation<Bitmap> newTransformation = Mockito.mock(Transformation.class);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, ARGB_8888);
        drawable.setFrameTransformation(newTransformation, bitmap);
        Mockito.verify(frameLoader).setFrameTransformation(ArgumentMatchers.eq(newTransformation), ArgumentMatchers.eq(bitmap));
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfCreatedWithNullState() {
        new GifDrawable(null);
    }

    @Test
    public void onFrameReady_whenAttachedToDrawableCallbackButNotViewCallback_stops() {
        TransitionDrawable topLevel = new TransitionDrawable(new Drawable[]{ drawable });
        drawable.setCallback(topLevel);
        topLevel.setCallback(null);
        drawable.start();
        drawable.onFrameReady();
        assertThat(drawable.isRunning()).isFalse();
    }

    @Test
    public void onFrameReady_whenAttachedtoDrawableCallbackWithViewCallbackParent_doesNotStop() {
        TransitionDrawable topLevel = new TransitionDrawable(new Drawable[]{ drawable });
        drawable.setCallback(topLevel);
        topLevel.setCallback(new android.view.View(context));
        drawable.start();
        drawable.onFrameReady();
        assertThat(drawable.isRunning()).isTrue();
    }

    /**
     * Keeps track of the set of Bitmaps drawn to the canvas.
     */
    @Implements(Canvas.class)
    public static final class BitmapTrackingShadowCanvas extends ShadowCanvas {
        private final Set<Bitmap> drawnBitmaps = new HashSet<>();

        @Implementation
        @Override
        public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
            drawnBitmaps.add(bitmap);
        }

        private Iterable<Bitmap> getDrawnBitmaps() {
            return drawnBitmaps;
        }
    }
}

