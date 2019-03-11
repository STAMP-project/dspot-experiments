/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.controller;


import android.graphics.drawable.Drawable;
import com.facebook.common.internal.Supplier;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.components.DeferredReleaser;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.SettableDraweeHierarchy;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * * Tests for AbstractDraweeController
 */
@RunWith(RobolectricTestRunner.class)
public class AbstractDraweeControllerTest {
    public static class FakeImageInfo {}

    public static class FakeImage {
        private final Drawable mDrawable;

        private final AbstractDraweeControllerTest.FakeImageInfo mImageInfo;

        private boolean mIsOpened;

        private boolean mIsClosed;

        protected FakeImage(Drawable drawable, AbstractDraweeControllerTest.FakeImageInfo imageInfo) {
            mDrawable = drawable;
            mImageInfo = imageInfo;
            mIsOpened = false;
            mIsClosed = false;
        }

        public Drawable getDrawable() {
            return mDrawable;
        }

        @Nullable
        public AbstractDraweeControllerTest.FakeImageInfo getImageInfo() {
            return mImageInfo;
        }

        public void open() {
            mIsOpened = true;
        }

        public boolean isOpened() {
            return mIsOpened;
        }

        public void close() {
            mIsClosed = true;
        }

        public boolean isClosed() {
            return mIsClosed;
        }

        public static AbstractDraweeControllerTest.FakeImage create(Drawable drawable) {
            return new AbstractDraweeControllerTest.FakeImage(drawable, null);
        }

        public static AbstractDraweeControllerTest.FakeImage create(Drawable drawable, AbstractDraweeControllerTest.FakeImageInfo imageInfo) {
            return new AbstractDraweeControllerTest.FakeImage(drawable, imageInfo);
        }
    }

    public static class FakeDraweeController extends AbstractDraweeController<AbstractDraweeControllerTest.FakeImage, AbstractDraweeControllerTest.FakeImageInfo> {
        private Supplier<DataSource<AbstractDraweeControllerTest.FakeImage>> mDataSourceSupplier;

        public boolean mIsAttached = false;

        public FakeDraweeController(DeferredReleaser deferredReleaser, Executor uiThreadExecutor, Supplier<DataSource<AbstractDraweeControllerTest.FakeImage>> dataSourceSupplier, String id, Object callerContext) {
            super(deferredReleaser, uiThreadExecutor, id, callerContext);
            mDataSourceSupplier = dataSourceSupplier;
        }

        @Override
        public void onAttach() {
            mIsAttached = true;
            super.onAttach();
        }

        @Override
        public void onDetach() {
            mIsAttached = false;
            super.onDetach();
        }

        public boolean isAttached() {
            return mIsAttached;
        }

        @Override
        protected DataSource<AbstractDraweeControllerTest.FakeImage> getDataSource() {
            return mDataSourceSupplier.get();
        }

        @Override
        protected Drawable createDrawable(AbstractDraweeControllerTest.FakeImage image) {
            return image.getDrawable();
        }

        @Override
        @Nullable
        protected AbstractDraweeControllerTest.FakeImageInfo getImageInfo(AbstractDraweeControllerTest.FakeImage image) {
            return image.getImageInfo();
        }

        @Override
        protected void releaseImage(@Nullable
        AbstractDraweeControllerTest.FakeImage image) {
            if (image != null) {
                image.close();
            }
        }

        @Override
        protected void releaseDrawable(@Nullable
        Drawable drawable) {
        }

        @Override
        public boolean isSameImageRequest(DraweeController other) {
            return false;
        }
    }

    private DeferredReleaser mDeferredReleaser;

    private Object mCallerContext;

    private Supplier<DataSource<AbstractDraweeControllerTest.FakeImage>> mDataSourceSupplier;

    private SettableDraweeHierarchy mDraweeHierarchy;

    private Executor mUiThreadExecutor;

    private AbstractDraweeControllerTest.FakeDraweeController mController;

    @Test
    public void testOnAttach() {
        mController.setHierarchy(mDraweeHierarchy);
        mController.onAttach();
        Mockito.verify(mDeferredReleaser, Mockito.atLeastOnce()).cancelDeferredRelease(ArgumentMatchers.eq(mController));
        Mockito.verify(mDataSourceSupplier).get();
    }

    @Test
    public void testOnAttach_ThrowsWithoutHierarchy() {
        try {
            setHierarchy(null);
            mController.onAttach();
            Assert.fail("onAttach should fail if no drawee hierarchy is set!");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testOnDetach() {
        mController.setHierarchy(mDraweeHierarchy);
        mController.onAttach();
        mController.onDetach();
        Assert.assertSame(mDraweeHierarchy, getHierarchy());
        Mockito.verify(mDeferredReleaser).scheduleDeferredRelease(mController);
    }

    @Test
    public void testSettingControllerOverlay() {
        Drawable controllerOverlay1 = Mockito.mock(Drawable.class);
        Drawable controllerOverlay2 = Mockito.mock(Drawable.class);
        SettableDraweeHierarchy draweeHierarchy1 = Mockito.mock(SettableDraweeHierarchy.class);
        SettableDraweeHierarchy draweeHierarchy2 = Mockito.mock(SettableDraweeHierarchy.class);
        InOrder inOrder = Mockito.inOrder(draweeHierarchy1, draweeHierarchy2);
        // initial state
        Assert.assertNull(getHierarchy());
        // set controller overlay before hierarchy
        mController.setControllerOverlay(controllerOverlay1);
        // set drawee hierarchy
        mController.setHierarchy(draweeHierarchy1);
        Assert.assertSame(draweeHierarchy1, getHierarchy());
        inOrder.verify(draweeHierarchy1, Mockito.times(1)).setControllerOverlay(controllerOverlay1);
        inOrder.verify(draweeHierarchy1, Mockito.times(0)).reset();
        // change drawee hierarchy
        mController.setHierarchy(draweeHierarchy2);
        Assert.assertSame(draweeHierarchy2, getHierarchy());
        setControllerOverlay(null);
        inOrder.verify(draweeHierarchy1, Mockito.times(0)).reset();
        inOrder.verify(draweeHierarchy2, Mockito.times(1)).setControllerOverlay(controllerOverlay1);
        inOrder.verify(draweeHierarchy2, Mockito.times(0)).reset();
        // clear drawee hierarchy
        setHierarchy(null);
        Assert.assertSame(null, getHierarchy());
        inOrder.verify(draweeHierarchy1, Mockito.times(0)).setControllerOverlay(ArgumentMatchers.any(Drawable.class));
        inOrder.verify(draweeHierarchy1, Mockito.times(0)).reset();
        setControllerOverlay(null);
        inOrder.verify(draweeHierarchy2, Mockito.times(0)).reset();
        // set drawee hierarchy
        mController.setHierarchy(draweeHierarchy1);
        Assert.assertSame(draweeHierarchy1, getHierarchy());
        inOrder.verify(draweeHierarchy1, Mockito.times(1)).setControllerOverlay(controllerOverlay1);
        inOrder.verify(draweeHierarchy1, Mockito.times(0)).reset();
        inOrder.verify(draweeHierarchy2, Mockito.times(0)).setControllerOverlay(ArgumentMatchers.any(Drawable.class));
        inOrder.verify(draweeHierarchy2, Mockito.times(0)).reset();
        // change controller overlay
        mController.setControllerOverlay(controllerOverlay2);
        inOrder.verify(draweeHierarchy1, Mockito.times(1)).setControllerOverlay(controllerOverlay2);
        inOrder.verify(draweeHierarchy1, Mockito.times(0)).reset();
        inOrder.verify(draweeHierarchy2, Mockito.times(0)).setControllerOverlay(ArgumentMatchers.any(Drawable.class));
        inOrder.verify(draweeHierarchy2, Mockito.times(0)).reset();
        // clear controller overlay
        setControllerOverlay(null);
        setControllerOverlay(null);
        inOrder.verify(draweeHierarchy1, Mockito.times(0)).reset();
        inOrder.verify(draweeHierarchy2, Mockito.times(0)).setControllerOverlay(ArgumentMatchers.any(Drawable.class));
        inOrder.verify(draweeHierarchy2, Mockito.times(0)).reset();
    }

    @Test
    public void testListeners() {
        ControllerListener<AbstractDraweeControllerTest.FakeImageInfo> listener1 = Mockito.mock(ControllerListener.class);
        ControllerListener<Object> listener2 = Mockito.mock(ControllerListener.class);
        InOrder inOrder = Mockito.inOrder(listener1, listener2);
        getControllerListener().onRelease("id");
        inOrder.verify(listener1, Mockito.never()).onRelease(ArgumentMatchers.anyString());
        inOrder.verify(listener2, Mockito.never()).onRelease(ArgumentMatchers.anyString());
        mController.addControllerListener(listener1);
        getControllerListener().onRelease("id");
        inOrder.verify(listener1, Mockito.times(1)).onRelease("id");
        inOrder.verify(listener2, Mockito.never()).onRelease(ArgumentMatchers.anyString());
        mController.addControllerListener(listener2);
        getControllerListener().onRelease("id");
        inOrder.verify(listener1, Mockito.times(1)).onRelease("id");
        inOrder.verify(listener2, Mockito.times(1)).onRelease("id");
        mController.removeControllerListener(listener1);
        getControllerListener().onRelease("id");
        inOrder.verify(listener1, Mockito.never()).onRelease(ArgumentMatchers.anyString());
        inOrder.verify(listener2, Mockito.times(1)).onRelease("id");
        mController.removeControllerListener(listener2);
        getControllerListener().onRelease("id");
        inOrder.verify(listener1, Mockito.never()).onRelease(ArgumentMatchers.anyString());
        inOrder.verify(listener2, Mockito.never()).onRelease(ArgumentMatchers.anyString());
    }

    @Test
    public void testListenerReentrancy_AfterIntermediateSet() {
        testListenerReentrancy(AbstractDraweeControllerTest.INTERMEDIATE_FAILURE);
    }

    @Test
    public void testListenerReentrancy_AfterIntermediateFailed() {
        testListenerReentrancy(AbstractDraweeControllerTest.INTERMEDIATE_GOOD);
    }

    @Test
    public void testListenerReentrancy_AfterFinalSet() {
        testListenerReentrancy(AbstractDraweeControllerTest.SUCCESS);
    }

    @Test
    public void testListenerReentrancy_AfterFailure() {
        testListenerReentrancy(AbstractDraweeControllerTest.FAILURE);
    }

    @Test
    public void testLoading1_DelayedSuccess() {
        testLoading(false, AbstractDraweeControllerTest.SUCCESS, AbstractDraweeControllerTest.SET_IMAGE_P100);
    }

    @Test
    public void testLoading1_DelayedFailure() {
        testLoading(false, AbstractDraweeControllerTest.FAILURE, AbstractDraweeControllerTest.SET_FAILURE);
    }

    @Test
    public void testLoading1_ImmediateSuccess() {
        testLoading(true, AbstractDraweeControllerTest.SUCCESS, AbstractDraweeControllerTest.SET_IMAGE_P100);
    }

    @Test
    public void testLoading1_ImmediateFailure() {
        testLoading(true, AbstractDraweeControllerTest.FAILURE, AbstractDraweeControllerTest.SET_FAILURE);
    }

    @Test
    public void testLoadingS_S() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    @Test
    public void testLoadingS_F() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.FAILURE }, new int[]{ AbstractDraweeControllerTest.SET_FAILURE });
    }

    @Test
    public void testLoadingS_LS() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_LOW, AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.SET_IMAGE_P20, AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    @Test
    public void testLoadingS_GS() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_GOOD, AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.SET_IMAGE_P50, AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    @Test
    public void testLoadingS_FS() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_FAILURE, AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.IGNORE, AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    @Test
    public void testLoadingS_LF() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_LOW, AbstractDraweeControllerTest.FAILURE }, new int[]{ AbstractDraweeControllerTest.SET_IMAGE_P20, AbstractDraweeControllerTest.SET_FAILURE });
    }

    @Test
    public void testLoadingS_GF() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_GOOD, AbstractDraweeControllerTest.FAILURE }, new int[]{ AbstractDraweeControllerTest.SET_IMAGE_P50, AbstractDraweeControllerTest.SET_FAILURE });
    }

    @Test
    public void testLoadingS_FF() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_FAILURE, AbstractDraweeControllerTest.FAILURE }, new int[]{ AbstractDraweeControllerTest.IGNORE, AbstractDraweeControllerTest.SET_FAILURE });
    }

    @Test
    public void testLoadingS_LLS() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_LOW, AbstractDraweeControllerTest.INTERMEDIATE_LOW, AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.SET_IMAGE_P20, AbstractDraweeControllerTest.SET_IMAGE_P20, AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    @Test
    public void testLoadingS_FLS() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_FAILURE, AbstractDraweeControllerTest.INTERMEDIATE_LOW, AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.IGNORE, AbstractDraweeControllerTest.SET_IMAGE_P20, AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    @Test
    public void testLoadingS_LGS() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_LOW, AbstractDraweeControllerTest.INTERMEDIATE_GOOD, AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.SET_IMAGE_P20, AbstractDraweeControllerTest.SET_IMAGE_P50, AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    @Test
    public void testLoadingS_GGS() {
        testStreamedLoading(0, new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_GOOD, AbstractDraweeControllerTest.INTERMEDIATE_GOOD, AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.SET_IMAGE_P50, AbstractDraweeControllerTest.SET_IMAGE_P50, AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    @Test
    public void testLoadingS_FGS() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_FAILURE, AbstractDraweeControllerTest.INTERMEDIATE_GOOD, AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.IGNORE, AbstractDraweeControllerTest.SET_IMAGE_P50, AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    @Test
    public void testLoadingS_LFS() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_LOW, AbstractDraweeControllerTest.INTERMEDIATE_FAILURE, AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.SET_IMAGE_P20, AbstractDraweeControllerTest.IGNORE, AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    @Test
    public void testLoadingS_GFS() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_GOOD, AbstractDraweeControllerTest.INTERMEDIATE_FAILURE, AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.SET_IMAGE_P50, AbstractDraweeControllerTest.IGNORE, AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    @Test
    public void testLoadingS_FFS() {
        testStreamedLoading(new int[]{ AbstractDraweeControllerTest.INTERMEDIATE_FAILURE, AbstractDraweeControllerTest.INTERMEDIATE_FAILURE, AbstractDraweeControllerTest.SUCCESS }, new int[]{ AbstractDraweeControllerTest.IGNORE, AbstractDraweeControllerTest.IGNORE, AbstractDraweeControllerTest.SET_IMAGE_P100 });
    }

    private static final int FAILURE = 0;

    private static final int SUCCESS = 1;

    private static final int INTERMEDIATE_FAILURE = 2;

    private static final int INTERMEDIATE_LOW = 3;

    private static final int INTERMEDIATE_GOOD = 4;

    private static final int IGNORE = 1000;

    private static final int SET_FAILURE = 1001;

    private static final int SET_RETRY = 1002;

    private static final int SET_IMAGE_P20 = 1003;

    private static final int SET_IMAGE_P50 = 1004;

    private static final int SET_IMAGE_P100 = 1005;
}

