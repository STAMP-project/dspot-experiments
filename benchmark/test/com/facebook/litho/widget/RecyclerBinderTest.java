/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.widget;


import ComponentTreeHolder.RENDER_DRAWN;
import ComponentTreeHolder.RENDER_UNINITIALIZED;
import ComponentsLogger.LogLevel.ERROR;
import OrientationHelper.VERTICAL;
import RecyclerBinder.Builder;
import RecyclerBinder.CommitPolicy.LAYOUT_BEFORE_INSERT;
import RecyclerBinder.ComponentTreeHolderFactory;
import RecyclerView.Adapter;
import RecyclerView.LayoutManager;
import SizeSpec.AT_MOST;
import SizeSpec.EXACTLY;
import View.GONE;
import View.VISIBLE;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentsLogger;
import com.facebook.litho.EventHandler;
import com.facebook.litho.LayoutHandler;
import com.facebook.litho.LayoutThreadPoolConfigurationImpl;
import com.facebook.litho.LithoView;
import com.facebook.litho.RenderCompleteEvent;
import com.facebook.litho.Size;
import com.facebook.litho.SizeSpec;
import com.facebook.litho.ThreadUtils;
import com.facebook.litho.config.ComponentsConfiguration;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import com.facebook.litho.viewcompat.SimpleViewBinder;
import com.facebook.litho.viewcompat.ViewBinder;
import com.facebook.litho.viewcompat.ViewCreator;
import com.facebook.litho.widget.RecyclerBinder.RenderCompleteRunnable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLooper;

import static RenderInfoViewCreatorController.DEFAULT_COMPONENT_VIEW_TYPE;


/**
 * Tests for {@link RecyclerBinder}
 */
@RunWith(ComponentsTestRunner.class)
public class RecyclerBinderTest {
    public static final RecyclerBinderTest.NoOpChangeSetCompleteCallback NO_OP_CHANGE_SET_COMPLETE_CALLBACK = new RecyclerBinderTest.NoOpChangeSetCompleteCallback();

    private static final float RANGE_RATIO = 2.0F;

    private static final int RANGE_SIZE = 3;

    private static final ViewCreator VIEW_CREATOR_1 = new ViewCreator() {
        @Override
        public View createView(Context c, ViewGroup parent) {
            return Mockito.mock(View.class);
        }
    };

    private static final ViewCreator VIEW_CREATOR_2 = new ViewCreator() {
        @Override
        public View createView(Context c, ViewGroup parent) {
            return Mockito.mock(View.class);
        }
    };

    private static final ViewCreator VIEW_CREATOR_3 = new ViewCreator() {
        @Override
        public View createView(Context c, ViewGroup parent) {
            return Mockito.mock(View.class);
        }
    };

    private static final int SCROLL_RESTORATION_VIEW_POSITION = 1;

    private static final int SCROLL_RESTORATION_RECYCLER_VIEW_SIZE = 30;

    private static final int SCROLL_RESTORATION_PADDING_EDGE = 20;

    private static final int SCROLL_RESTORATION_ITEM_EDGE = 10;

    private interface ViewCreatorProvider {
        ViewCreator get();
    }

    private final Map<Component, TestComponentTreeHolder> mHoldersForComponents = new HashMap<>();

    private RecyclerBinder mRecyclerBinder;

    private Builder mRecyclerBinderForAsyncInitRangeBuilder;

    private Builder mRecyclerBinderBuilder;

    private RecyclerBinder mCircularRecyclerBinder;

    private LayoutInfo mLayoutInfo;

    private LayoutInfo mCircularLayoutInfo;

    private ComponentContext mComponentContext;

    private ShadowLooper mLayoutThreadShadowLooper;

    private ComponentTreeHolderFactory mComponentTreeHolderFactory;

    @Test
    public void testComponentTreeHolderCreation() {
        final List<ComponentRenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            components.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
            mRecyclerBinder.insertItemAt(0, components.get(i));
        }
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        for (int i = 0; i < 100; i++) {
            Assert.assertNotNull(mHoldersForComponents.get(getComponent()));
        }
    }

    @Test
    public void testAppendItems() {
        final List<ComponentRenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            components.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
            mRecyclerBinder.appendItem(components.get(i));
        }
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        for (int i = 0; i < 100; i++) {
            assertThat(mHoldersForComponents.get(getComponent())).isNotNull();
            assertThat(components.get(i)).isEqualTo(mRecyclerBinder.getRenderInfoAt(i));
        }
    }

    @Test
    public void testOnMeasureAfterAddingItems() {
        final List<ComponentRenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            components.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
            mRecyclerBinder.insertItemAt(i, components.get(i));
        }
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        for (int i = 0; i < 100; i++) {
            assertThat(mHoldersForComponents.get(getComponent())).isNotNull();
        }
        final Size size = new Size();
        final int widthSpec = SizeSpec.makeSizeSpec(200, SizeSpec.AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        mRecyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        TestComponentTreeHolder componentTreeHolder = mHoldersForComponents.get(getComponent());
        assertThat(componentTreeHolder.isTreeValid()).isTrue();
        assertThat(componentTreeHolder.mLayoutSyncCalled).isTrue();
        int rangeTotal = (RecyclerBinderTest.RANGE_SIZE) + ((int) ((RecyclerBinderTest.RANGE_SIZE) * (RecyclerBinderTest.RANGE_RATIO)));
        for (int i = 1; i <= rangeTotal; i++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            assertThat(componentTreeHolder.isTreeValid()).isTrue();
            assertThat(componentTreeHolder.mLayoutAsyncCalled).isTrue();
            assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
        }
        for (int k = rangeTotal + 1; k < (components.size()); k++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            assertThat(componentTreeHolder.isTreeValid()).isFalse();
            assertThat(componentTreeHolder.mLayoutAsyncCalled).isFalse();
            assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
        }
        assertThat(100).isEqualTo(size.width);
    }

    @Test
    public void onBoundsDefined() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        for (int i = 0; i < (components.size()); i++) {
            final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
            holder.mLayoutAsyncCalled = false;
            holder.mLayoutSyncCalled = false;
        }
        mRecyclerBinder.setSize(200, 200);
        for (int i = 0; i < (components.size()); i++) {
            final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.mLayoutAsyncCalled).isFalse();
            assertThat(holder.mLayoutSyncCalled).isFalse();
        }
    }

    @Test
    public void onBoundsDefinedWithDifferentSize() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        for (int i = 0; i < (components.size()); i++) {
            final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
            holder.mLayoutAsyncCalled = false;
            holder.mLayoutSyncCalled = false;
        }
        mRecyclerBinder.setSize(300, 200);
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
        assertThat(holder.isTreeValid()).isTrue();
        assertThat(holder.mLayoutSyncCalled).isTrue();
        for (int i = 1; i <= rangeTotal; i++) {
            holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.isTreeValid()).isTrue();
            assertThat(holder.mLayoutAsyncCalled).isTrue();
        }
        for (int i = rangeTotal + 1; i < (components.size()); i++) {
            holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.isTreeValid()).isFalse();
            assertThat(holder.mLayoutAsyncCalled).isFalse();
            assertThat(holder.mLayoutSyncCalled).isFalse();
        }
    }

    @Test
    public void testMount() {
        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        mRecyclerBinder.mount(recyclerView);
        Mockito.verify(recyclerView).setLayoutManager(mLayoutInfo.getLayoutManager());
        Mockito.verify(recyclerView).setAdapter(ArgumentMatchers.any(Adapter.class));
        Mockito.verify(mLayoutInfo).setRenderInfoCollection(mRecyclerBinder);
        Mockito.verify(recyclerView).addOnScrollListener(ArgumentMatchers.any(OnScrollListener.class));
    }

    @Test
    public void testMountWithStaleView() {
        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        mRecyclerBinder.mount(recyclerView);
        Mockito.verify(recyclerView).setLayoutManager(mLayoutInfo.getLayoutManager());
        Mockito.verify(recyclerView).setAdapter(ArgumentMatchers.any(Adapter.class));
        Mockito.verify(recyclerView).addOnScrollListener(ArgumentMatchers.any(OnScrollListener.class));
        RecyclerView secondRecyclerView = Mockito.mock(RecyclerView.class);
        mRecyclerBinder.mount(secondRecyclerView);
        Mockito.verify(recyclerView).setLayoutManager(null);
        Mockito.verify(recyclerView).setAdapter(null);
        Mockito.verify(recyclerView).removeOnScrollListener(ArgumentMatchers.any(OnScrollListener.class));
        Mockito.verify(secondRecyclerView).setLayoutManager(mLayoutInfo.getLayoutManager());
        Mockito.verify(secondRecyclerView).setAdapter(ArgumentMatchers.any(Adapter.class));
        Mockito.verify(secondRecyclerView).addOnScrollListener(ArgumentMatchers.any(OnScrollListener.class));
    }

    @Test
    public void testUnmount() {
        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        mRecyclerBinder.mount(recyclerView);
        Mockito.verify(recyclerView).setLayoutManager(mLayoutInfo.getLayoutManager());
        Mockito.verify(recyclerView).setAdapter(ArgumentMatchers.any(Adapter.class));
        Mockito.verify(recyclerView).addOnScrollListener(ArgumentMatchers.any(OnScrollListener.class));
        mRecyclerBinder.unmount(recyclerView);
        Mockito.verify(recyclerView).setLayoutManager(null);
        Mockito.verify(recyclerView).setAdapter(null);
        Mockito.verify(mLayoutInfo).setRenderInfoCollection(null);
        Mockito.verify(recyclerView).removeOnScrollListener(ArgumentMatchers.any(OnScrollListener.class));
    }

    @Test
    public void testScrollRestorationVertical() {
        /* verticalScroll */
        /* reverseLayout */
        testScrollRestoration(true, false);
    }

    @Test
    public void testScrollRestorationVerticalReversed() {
        /* verticalScroll */
        /* reverseLayout */
        testScrollRestoration(true, true);
    }

    @Test
    public void testScrollRestorationHorizontal() {
        /* verticalScroll */
        /* reverseLayout */
        testScrollRestoration(false, false);
    }

    @Test
    public void testScrollRestorationHorizontalReversed() {
        /* verticalScroll */
        /* reverseLayout */
        testScrollRestoration(false, true);
    }

    @Test
    public void testAddStickyHeaderIfSectionsRecyclerViewExists() throws Exception {
        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        SectionsRecyclerView recycler = Mockito.mock(SectionsRecyclerView.class);
        Mockito.when(recyclerView.getParent()).thenReturn(recycler);
        Mockito.when(recyclerView.getLayoutManager()).thenReturn(Mockito.mock(LayoutManager.class));
        Mockito.when(recycler.getRecyclerView()).thenReturn(recyclerView);
        mRecyclerBinder.mount(recyclerView);
        Mockito.verify(recyclerView).setAdapter(ArgumentMatchers.any(Adapter.class));
        Mockito.verify(recyclerView, Mockito.times(2)).addOnScrollListener(ArgumentMatchers.any(OnScrollListener.class));
    }

    @Test
    public void onRemeasureWithDifferentSize() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        for (int i = 0; i < (components.size()); i++) {
            final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
            holder.mLayoutAsyncCalled = false;
            holder.mLayoutSyncCalled = false;
        }
        int widthSpec = SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY);
        int heightSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        mRecyclerBinder.measure(new Size(), widthSpec, heightSpec, null);
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
        assertThat(holder.isTreeValid()).isTrue();
        assertThat(holder.mLayoutSyncCalled).isTrue();
        for (int i = 1; i <= rangeTotal; i++) {
            holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.isTreeValid()).isTrue();
            assertThat(holder.mLayoutAsyncCalled).isTrue();
        }
        for (int i = rangeTotal + 1; i < (components.size()); i++) {
            holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.isTreeValid()).isFalse();
            assertThat(holder.mLayoutAsyncCalled).isFalse();
            assertThat(holder.mLayoutSyncCalled).isFalse();
        }
    }

    @Test
    public void testComponentWithDifferentSpanSize() {
        final List<ComponentRenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            components.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).spanSize(((i == 0) || ((i % 3) == 0) ? 2 : 1)).build());
            mRecyclerBinder.insertItemAt(i, components.get(i));
        }
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        Mockito.when(mLayoutInfo.getChildWidthSpec(ArgumentMatchers.anyInt(), ArgumentMatchers.any(RenderInfo.class))).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                final RenderInfo renderInfo = ((RenderInfo) (invocation.getArguments()[1]));
                final int spanSize = renderInfo.getSpanSize();
                return SizeSpec.makeSizeSpec((100 * spanSize), SizeSpec.EXACTLY);
            }
        });
        for (int i = 0; i < 100; i++) {
            assertThat(mHoldersForComponents.get(getComponent())).isNotNull();
        }
        Size size = new Size();
        int widthSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        int heightSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        mRecyclerBinder.measure(size, widthSpec, heightSpec, null);
        TestComponentTreeHolder componentTreeHolder = mHoldersForComponents.get(getComponent());
        assertThat(componentTreeHolder.isTreeValid()).isTrue();
        assertThat(componentTreeHolder.mLayoutSyncCalled).isTrue();
        assertThat(200).isEqualTo(size.width);
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        for (int i = 1; i <= rangeTotal; i++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            assertThat(componentTreeHolder.isTreeValid()).isTrue();
            assertThat(componentTreeHolder.mLayoutAsyncCalled).isTrue();
            final int expectedWidth = ((i % 3) == 0) ? 200 : 100;
            assertThat(expectedWidth).isEqualTo(componentTreeHolder.mChildWidth);
            assertThat(100).isEqualTo(componentTreeHolder.mChildHeight);
        }
    }

    @Test
    public void testAddItemsAfterMeasuring() {
        final Size size = new Size();
        final int widthSpec = SizeSpec.makeSizeSpec(200, EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(200, EXACTLY);
        mRecyclerBinder.measure(size, widthSpec, heightSpec, null);
        assertThat(200).isEqualTo(size.width);
        final List<ComponentRenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            components.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
            mRecyclerBinder.insertItemAt(i, components.get(i));
        }
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        for (int i = 0; i < 100; i++) {
            Assert.assertNotNull(mHoldersForComponents.get(getComponent()));
        }
        TestComponentTreeHolder componentTreeHolder;
        int rangeTotal = (RecyclerBinderTest.RANGE_SIZE) + ((int) ((RecyclerBinderTest.RANGE_SIZE) * (RecyclerBinderTest.RANGE_RATIO)));
        // The first component is used to calculate the range
        TestComponentTreeHolder firstHolder = mHoldersForComponents.get(getComponent());
        assertThat(firstHolder.isTreeValid()).isTrue();
        assertThat(firstHolder.mLayoutSyncCalled).isTrue();
        for (int i = 1; i < (RecyclerBinderTest.RANGE_SIZE); i++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            assertThat(componentTreeHolder.isTreeValid()).isTrue();
            assertThat(componentTreeHolder.mLayoutAsyncCalled).isTrue();
            assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
        }
        for (int i = RecyclerBinderTest.RANGE_SIZE; i <= rangeTotal; i++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            assertThat(componentTreeHolder.isTreeValid()).isTrue();
            assertThat(componentTreeHolder.mLayoutAsyncCalled).isTrue();
            assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
        }
        for (int i = rangeTotal + 1; i < (components.size()); i++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            assertThat(componentTreeHolder.isTreeValid()).isFalse();
            assertThat(componentTreeHolder.mLayoutAsyncCalled).isFalse();
            assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
        }
    }

    @Test
    public void testRequestRemeasure() {
        final Size size = new Size();
        final int widthSpec = SizeSpec.makeSizeSpec(200, AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(200, EXACTLY);
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        mRecyclerBinder.mount(recyclerView);
        mRecyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(0).isEqualTo(size.width);
        final List<ComponentRenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            components.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
            mRecyclerBinder.insertItemAt(i, components.get(i));
        }
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        Mockito.verify(recyclerView).removeCallbacks(mRecyclerBinder.mRemeasureRunnable);
        Mockito.verify(recyclerView).postOnAnimation(mRecyclerBinder.mRemeasureRunnable);
    }

    @Test
    public void testRequestRemeasureInsertRange() {
        final Size size = new Size();
        final int widthSpec = SizeSpec.makeSizeSpec(200, AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(200, EXACTLY);
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        mRecyclerBinder.mount(recyclerView);
        mRecyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(0).isEqualTo(size.width);
        final List<RenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            components.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        }
        mRecyclerBinder.insertRangeAt(0, components);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        Mockito.verify(recyclerView).removeCallbacks(mRecyclerBinder.mRemeasureRunnable);
        Mockito.verify(recyclerView).postOnAnimation(mRecyclerBinder.mRemeasureRunnable);
    }

    @Test
    public void testRequestRemeasureUpdateAt() {
        final Size size = new Size();
        final int widthSpec = SizeSpec.makeSizeSpec(200, AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(200, EXACTLY);
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        mRecyclerBinder.mount(recyclerView);
        mRecyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(0).isEqualTo(size.width);
        final List<RenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            components.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        }
        mRecyclerBinder.insertRangeAt(0, components);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        Mockito.reset(recyclerView);
        for (int i = 0; i < 50; i++) {
            mRecyclerBinder.updateItemAt(i, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        }
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        Mockito.verify(recyclerView).removeCallbacks(mRecyclerBinder.mRemeasureRunnable);
        Mockito.verify(recyclerView).postOnAnimation(mRecyclerBinder.mRemeasureRunnable);
    }

    @Test
    public void testRequestRemeasureUpdateRange() {
        final Size size = new Size();
        final int widthSpec = SizeSpec.makeSizeSpec(200, AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(200, EXACTLY);
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        mRecyclerBinder.mount(recyclerView);
        mRecyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(0).isEqualTo(size.width);
        final List<RenderInfo> components = new ArrayList<>();
        final List<RenderInfo> updatedComponents = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            components.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        }
        for (int i = 0; i < 50; i++) {
            updatedComponents.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        }
        mRecyclerBinder.insertRangeAt(0, components);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        Mockito.reset(recyclerView);
        mRecyclerBinder.updateRangeAt(0, updatedComponents);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        Mockito.verify(recyclerView).removeCallbacks(mRecyclerBinder.mRemeasureRunnable);
        Mockito.verify(recyclerView).postOnAnimation(mRecyclerBinder.mRemeasureRunnable);
    }

    @Test
    public void testDoesntRequestRemeasure() {
        final Size size = new Size();
        final int widthSpec = SizeSpec.makeSizeSpec(200, EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(200, EXACTLY);
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        mRecyclerBinder.mount(recyclerView);
        mRecyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(200).isEqualTo(size.width);
        final List<ComponentRenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            components.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
            mRecyclerBinder.insertItemAt(i, components.get(i));
        }
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        Mockito.verify(recyclerView, Mockito.never()).removeCallbacks(mRecyclerBinder.mRemeasureRunnable);
        Mockito.verify(recyclerView, Mockito.never()).postOnAnimation(mRecyclerBinder.mRemeasureRunnable);
    }

    @Test
    public void testRangeBiggerThanContent() {
        final List<ComponentRenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            components.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
            mRecyclerBinder.insertItemAt(i, components.get(i));
        }
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        for (int i = 0; i < 2; i++) {
            assertThat(mHoldersForComponents.get(getComponent())).isNotNull();
        }
        Size size = new Size();
        int widthSpec = SizeSpec.makeSizeSpec(200, SizeSpec.AT_MOST);
        int heightSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        mRecyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        TestComponentTreeHolder componentTreeHolder = mHoldersForComponents.get(getComponent());
        assertThat(componentTreeHolder.isTreeValid()).isTrue();
        assertThat(componentTreeHolder.mLayoutSyncCalled).isTrue();
        for (int i = 1; i < 2; i++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            assertThat(componentTreeHolder.isTreeValid()).isTrue();
            assertThat(componentTreeHolder.mLayoutAsyncCalled).isTrue();
            assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
        }
        assertThat(100).isEqualTo(size.width);
    }

    @Test
    public void testMoveRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final int newRangeStart = 40;
        final int newRangeEnd = 42;
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        mRecyclerBinder.onNewVisibleRange(newRangeStart, newRangeEnd);
        TestComponentTreeHolder componentTreeHolder;
        for (int i = 0; i < (components.size()); i++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            if ((i >= (newRangeStart - ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE)))) && (i <= (newRangeStart + rangeTotal))) {
                assertThat(componentTreeHolder.isTreeValid()).isTrue();
                assertThat(componentTreeHolder.mLayoutAsyncCalled).isTrue();
                assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
            } else {
                assertThat(componentTreeHolder.isTreeValid()).isFalse();
                assertThat(componentTreeHolder.mLayoutAsyncCalled).isFalse();
                assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
                if (i <= rangeTotal) {
                    assertThat(componentTreeHolder.mDidAcquireStateHandler).isTrue();
                } else {
                    assertThat(componentTreeHolder.mDidAcquireStateHandler).isFalse();
                }
            }
        }
    }

    @Test
    public void testRealRangeOverridesEstimatedRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final int newRangeStart = 40;
        final int newRangeEnd = 50;
        int rangeSize = newRangeEnd - newRangeStart;
        final int rangeTotal = ((int) (rangeSize + ((RecyclerBinderTest.RANGE_RATIO) * rangeSize)));
        mRecyclerBinder.onNewVisibleRange(newRangeStart, newRangeEnd);
        TestComponentTreeHolder componentTreeHolder;
        for (int i = 0; i < (components.size()); i++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            if ((i >= (newRangeStart - ((RecyclerBinderTest.RANGE_RATIO) * rangeSize))) && (i <= (newRangeStart + rangeTotal))) {
                assertThat(componentTreeHolder.isTreeValid()).isTrue();
                assertThat(componentTreeHolder.mLayoutAsyncCalled).isTrue();
                assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
            } else {
                assertThat(componentTreeHolder.isTreeValid()).isFalse();
                assertThat(componentTreeHolder.mLayoutAsyncCalled).isFalse();
                assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
            }
        }
    }

    @Test
    public void testStickyComponentsStayValidOutsideRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        makeIndexSticky(components, 5);
        makeIndexSticky(components, 40);
        makeIndexSticky(components, 80);
        Size size = new Size();
        int widthSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        int heightSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        mRecyclerBinder.measure(size, widthSpec, heightSpec, null);
        assertThat(mHoldersForComponents.get(getComponent()).isTreeValid()).isTrue();
        final int newRangeStart = 40;
        final int newRangeEnd = 50;
        int rangeSize = newRangeEnd - newRangeStart;
        final int rangeTotal = ((int) (rangeSize + ((RecyclerBinderTest.RANGE_RATIO) * rangeSize)));
        mRecyclerBinder.onNewVisibleRange(newRangeStart, newRangeEnd);
        TestComponentTreeHolder componentTreeHolder;
        for (int i = 0; i < (components.size()); i++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            boolean isIndexInRange = (i >= (newRangeStart - ((RecyclerBinderTest.RANGE_RATIO) * rangeSize))) && (i <= (newRangeStart + rangeTotal));
            boolean isPreviouslyComputedTreeAndSticky = (i <= (newRangeStart + rangeTotal)) && (componentTreeHolder.getRenderInfo().isSticky());
            if (isIndexInRange || isPreviouslyComputedTreeAndSticky) {
                assertThat(componentTreeHolder.isTreeValid()).isTrue();
                assertThat(componentTreeHolder.mLayoutAsyncCalled).isTrue();
                assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
            } else {
                assertThat(componentTreeHolder.isTreeValid()).isFalse();
                assertThat(componentTreeHolder.mLayoutAsyncCalled).isFalse();
                assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
            }
        }
    }

    @Test
    public void testMoveRangeToEnd() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final int newRangeStart = 99;
        final int newRangeEnd = 99;
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        mRecyclerBinder.onNewVisibleRange(newRangeStart, newRangeEnd);
        TestComponentTreeHolder componentTreeHolder;
        for (int i = 0; i < (components.size()); i++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            if ((i >= (newRangeStart - ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE)))) && (i <= (newRangeStart + rangeTotal))) {
                assertThat(componentTreeHolder.isTreeValid()).isTrue();
                assertThat(componentTreeHolder.mLayoutAsyncCalled).isTrue();
                assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
            } else {
                assertThat(componentTreeHolder.isTreeValid()).isFalse();
                assertThat(componentTreeHolder.mLayoutAsyncCalled).isFalse();
                assertThat(componentTreeHolder.mLayoutSyncCalled).isFalse();
                if (i <= rangeTotal) {
                    assertThat(componentTreeHolder.mDidAcquireStateHandler).isTrue();
                } else {
                    assertThat(componentTreeHolder.mDidAcquireStateHandler).isFalse();
                }
            }
        }
    }

    @Test
    public void testMoveItemOutsideFromRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        mRecyclerBinder.moveItem(0, 99);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final TestComponentTreeHolder movedHolder = mHoldersForComponents.get(getComponent());
        assertThat(movedHolder.isTreeValid()).isFalse();
        assertThat(movedHolder.mLayoutAsyncCalled).isFalse();
        assertThat(movedHolder.mLayoutSyncCalled).isFalse();
        assertThat(movedHolder.mDidAcquireStateHandler).isTrue();
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        final TestComponentTreeHolder holderMovedIntoRange = mHoldersForComponents.get(getComponent());
        assertThat(holderMovedIntoRange.isTreeValid()).isTrue();
        assertThat(holderMovedIntoRange.mLayoutAsyncCalled).isTrue();
        assertThat(holderMovedIntoRange.mLayoutSyncCalled).isFalse();
    }

    @Test
    public void testMoveItemInsideRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        mRecyclerBinder.moveItem(99, 4);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        TestComponentTreeHolder movedHolder = mHoldersForComponents.get(getComponent());
        assertThat(movedHolder.isTreeValid()).isTrue();
        assertThat(movedHolder.mLayoutAsyncCalled).isTrue();
        assertThat(movedHolder.mLayoutSyncCalled).isFalse();
        assertThat(movedHolder.mDidAcquireStateHandler).isFalse();
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        TestComponentTreeHolder holderMovedOutsideRange = mHoldersForComponents.get(getComponent());
        assertThat(holderMovedOutsideRange.isTreeValid()).isFalse();
        assertThat(holderMovedOutsideRange.mLayoutAsyncCalled).isFalse();
        assertThat(holderMovedOutsideRange.mLayoutSyncCalled).isFalse();
    }

    @Test
    public void testMoveItemInsideVisibleRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        mRecyclerBinder.moveItem(99, 2);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final TestComponentTreeHolder movedHolder = mHoldersForComponents.get(getComponent());
        assertThat(movedHolder.isTreeValid()).isTrue();
        assertThat(movedHolder.mLayoutAsyncCalled).isTrue();
        assertThat(movedHolder.mLayoutSyncCalled).isFalse();
        assertThat(movedHolder.mDidAcquireStateHandler).isFalse();
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        final TestComponentTreeHolder holderMovedOutsideRange = mHoldersForComponents.get(getComponent());
        assertThat(holderMovedOutsideRange.isTreeValid()).isFalse();
        assertThat(holderMovedOutsideRange.mLayoutAsyncCalled).isFalse();
        assertThat(holderMovedOutsideRange.mLayoutSyncCalled).isFalse();
        assertThat(holderMovedOutsideRange.mDidAcquireStateHandler).isTrue();
    }

    @Test
    public void testMoveItemOutsideRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        mRecyclerBinder.moveItem(2, 99);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final TestComponentTreeHolder movedHolder = mHoldersForComponents.get(getComponent());
        assertThat(movedHolder.isTreeValid()).isFalse();
        assertThat(movedHolder.mLayoutAsyncCalled).isFalse();
        assertThat(movedHolder.mLayoutSyncCalled).isFalse();
        assertThat(movedHolder.mDidAcquireStateHandler).isTrue();
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        final TestComponentTreeHolder holderMovedInsideRange = mHoldersForComponents.get(getComponent());
        assertThat(holderMovedInsideRange.isTreeValid()).isTrue();
        assertThat(holderMovedInsideRange.mLayoutAsyncCalled).isTrue();
        assertThat(holderMovedInsideRange.mLayoutSyncCalled).isFalse();
        assertThat(holderMovedInsideRange.mDidAcquireStateHandler).isFalse();
    }

    @Test
    public void testMoveWithinRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final TestComponentTreeHolder movedHolderOne = mHoldersForComponents.get(getComponent());
        final TestComponentTreeHolder movedHolderTwo = mHoldersForComponents.get(getComponent());
        movedHolderOne.mLayoutSyncCalled = false;
        movedHolderOne.mLayoutAsyncCalled = false;
        movedHolderOne.mDidAcquireStateHandler = false;
        movedHolderTwo.mLayoutSyncCalled = false;
        movedHolderTwo.mLayoutAsyncCalled = false;
        movedHolderTwo.mDidAcquireStateHandler = false;
        mRecyclerBinder.moveItem(0, 1);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(movedHolderOne.isTreeValid()).isTrue();
        assertThat(movedHolderOne.mLayoutAsyncCalled).isFalse();
        assertThat(movedHolderOne.mLayoutSyncCalled).isFalse();
        assertThat(movedHolderOne.mDidAcquireStateHandler).isFalse();
        assertThat(movedHolderTwo.isTreeValid()).isTrue();
        assertThat(movedHolderTwo.mLayoutAsyncCalled).isFalse();
        assertThat(movedHolderTwo.mLayoutSyncCalled).isFalse();
        assertThat(movedHolderTwo.mDidAcquireStateHandler).isFalse();
    }

    @Test
    public void testInsertInVisibleRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final ComponentRenderInfo newRenderInfo = ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build();
        mRecyclerBinder.insertItemAt(1, newRenderInfo);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final TestComponentTreeHolder holder = mHoldersForComponents.get(newRenderInfo.getComponent());
        assertThat(holder.isTreeValid()).isTrue();
        assertThat(holder.mLayoutSyncCalled).isFalse();
        assertThat(holder.mLayoutAsyncCalled).isTrue();
        assertThat(holder.mDidAcquireStateHandler).isFalse();
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        final TestComponentTreeHolder holderMovedOutsideRange = mHoldersForComponents.get(getComponent());
        assertThat(holderMovedOutsideRange.isTreeValid()).isFalse();
        assertThat(holderMovedOutsideRange.mLayoutSyncCalled).isFalse();
        assertThat(holderMovedOutsideRange.mLayoutAsyncCalled).isFalse();
        assertThat(holderMovedOutsideRange.mDidAcquireStateHandler).isTrue();
    }

    @Test
    public void testRemoveRangeAboveTheViewport() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        RecyclerView recyclerView = new RecyclerView(RuntimeEnvironment.application);
        mRecyclerBinder.mount(recyclerView);
        final int firstVisible = 40;
        final int lastVisible = 50;
        int rangeSize = lastVisible - firstVisible;
        changeViewportTo(firstVisible, lastVisible);
        assertThat(mRecyclerBinder.mViewportManager.shouldUpdate()).isFalse();
        int removeRangeSize = rangeSize;
        // Remove above the visible range
        mRecyclerBinder.removeRangeAt(0, removeRangeSize);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.mViewportManager.shouldUpdate()).isTrue();
        // compute range not yet updated, range will be updated in next frame
        assertThat(mRecyclerBinder.mCurrentFirstVisiblePosition).isEqualTo(firstVisible);
        assertThat(mRecyclerBinder.mCurrentLastVisiblePosition).isEqualTo(lastVisible);
    }

    @Test
    public void testRemoveRangeBelowTheViewport() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        RecyclerView recyclerView = new RecyclerView(RuntimeEnvironment.application);
        mRecyclerBinder.mount(recyclerView);
        final int firstVisible = 40;
        final int lastVisible = 50;
        int rangeSize = lastVisible - firstVisible;
        changeViewportTo(firstVisible, lastVisible);
        assertThat(mRecyclerBinder.mViewportManager.shouldUpdate()).isFalse();
        int removeRangeSize = rangeSize;
        // Remove below the visible range
        mRecyclerBinder.removeRangeAt((lastVisible + 1), removeRangeSize);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.mViewportManager.shouldUpdate()).isFalse();
        // compute range has been updated and range did not change
        assertThat(mRecyclerBinder.mCurrentFirstVisiblePosition).isEqualTo(firstVisible);
        assertThat(mRecyclerBinder.mCurrentLastVisiblePosition).isEqualTo(lastVisible);
    }

    @Test
    public void testInsertInRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final ComponentRenderInfo newRenderInfo = ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build();
        mRecyclerBinder.insertItemAt(((RecyclerBinderTest.RANGE_SIZE) + 1), newRenderInfo);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final TestComponentTreeHolder holder = mHoldersForComponents.get(newRenderInfo.getComponent());
        assertThat(holder.isTreeValid()).isTrue();
        assertThat(holder.mLayoutSyncCalled).isFalse();
        assertThat(holder.mLayoutAsyncCalled).isTrue();
        assertThat(holder.mDidAcquireStateHandler).isFalse();
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        final TestComponentTreeHolder holderMovedOutsideRange = mHoldersForComponents.get(getComponent());
        assertThat(holderMovedOutsideRange.isTreeValid()).isFalse();
        assertThat(holderMovedOutsideRange.mLayoutSyncCalled).isFalse();
        assertThat(holderMovedOutsideRange.mLayoutAsyncCalled).isFalse();
        assertThat(holderMovedOutsideRange.mDidAcquireStateHandler).isTrue();
    }

    @Test
    public void testInsertRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final List<RenderInfo> newComponents = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            newComponents.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        }
        mRecyclerBinder.insertRangeAt(0, newComponents);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // The new elements were scheduled for layout.
        for (int i = 0; i < 3; i++) {
            final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.isTreeValid()).isTrue();
            assertThat(holder.mLayoutSyncCalled).isFalse();
            assertThat(holder.mLayoutAsyncCalled).isTrue();
            assertThat(holder.mDidAcquireStateHandler).isFalse();
        }
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        // The elements that went outside the layout range have been released.
        for (int i = rangeTotal - 2; i <= rangeTotal; i++) {
            final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.isTreeValid()).isFalse();
            assertThat(holder.mLayoutSyncCalled).isFalse();
            assertThat(holder.mLayoutAsyncCalled).isFalse();
            assertThat(holder.mDidAcquireStateHandler).isTrue();
        }
    }

    @Test
    public void testInsertOusideRange() {
        prepareLoadedBinder();
        final ComponentRenderInfo newRenderInfo = ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build();
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        mRecyclerBinder.insertItemAt((rangeTotal + 1), newRenderInfo);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final TestComponentTreeHolder holder = mHoldersForComponents.get(newRenderInfo.getComponent());
        assertThat(holder.isTreeValid()).isFalse();
        assertThat(holder.mLayoutSyncCalled).isFalse();
        assertThat(holder.mLayoutAsyncCalled).isFalse();
        assertThat(holder.mDidAcquireStateHandler).isFalse();
    }

    @Test
    public void testRemoveItem() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        mRecyclerBinder.removeItemAt((rangeTotal + 1));
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
    }

    @Test
    public void testRemoveFromRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        mRecyclerBinder.removeItemAt(rangeTotal);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
        final TestComponentTreeHolder holderMovedInRange = mHoldersForComponents.get(getComponent());
        assertThat(holderMovedInRange.isTreeValid()).isTrue();
        assertThat(holderMovedInRange.mLayoutSyncCalled).isFalse();
        assertThat(holderMovedInRange.mLayoutAsyncCalled).isTrue();
        assertThat(holderMovedInRange.mDidAcquireStateHandler).isFalse();
    }

    @Test
    public void testRemoveRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final int rangeTotal = ((int) ((RecyclerBinderTest.RANGE_SIZE) + ((RecyclerBinderTest.RANGE_RATIO) * (RecyclerBinderTest.RANGE_SIZE))));
        mRecyclerBinder.removeRangeAt(0, RecyclerBinderTest.RANGE_SIZE);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // The elements that are now in the range get their layout computed
        for (int i = rangeTotal + 1; i <= (rangeTotal + (RecyclerBinderTest.RANGE_SIZE)); i++) {
            final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.isTreeValid()).isTrue();
            assertThat(holder.mLayoutSyncCalled).isFalse();
            assertThat(holder.mLayoutAsyncCalled).isTrue();
            assertThat(holder.mDidAcquireStateHandler).isFalse();
        }
    }

    @Test
    public void testUpdate() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
        assertThat(holder.isTreeValid()).isTrue();
        holder.mTreeValid = false;
        assertThat(holder.isTreeValid()).isFalse();
        final ComponentRenderInfo newRenderInfo = ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build();
        mRecyclerBinder.updateItemAt(0, newRenderInfo);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(newRenderInfo).isEqualTo(holder.getRenderInfo());
        assertThat(holder.isTreeValid()).isTrue();
    }

    @Test
    public void testUpdateRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        for (int i = 0; i < (RecyclerBinderTest.RANGE_SIZE); i++) {
            final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.isTreeValid()).isTrue();
            holder.mTreeValid = false;
            assertThat(holder.isTreeValid()).isFalse();
        }
        final List<RenderInfo> newInfos = new ArrayList<>();
        for (int i = 0; i < (RecyclerBinderTest.RANGE_SIZE); i++) {
            newInfos.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        }
        mRecyclerBinder.updateRangeAt(0, newInfos);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        for (int i = 0; i < (RecyclerBinderTest.RANGE_SIZE); i++) {
            final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
            assertThat(newInfos.get(i)).isEqualTo(holder.getRenderInfo());
            assertThat(holder.isTreeValid()).isTrue();
        }
    }

    @Test
    public void testInsertMixedContentWithSingleViewCreator() {
        List<Integer> viewItems = Arrays.asList(3, 6, 7, 11);
        prepareMixedLoadedBinder(30, new HashSet<>(viewItems), new RecyclerBinderTest.ViewCreatorProvider() {
            @Override
            public ViewCreator get() {
                return RecyclerBinderTest.VIEW_CREATOR_1;
            }
        });
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewTypeToViewCreator.size()).isEqualTo(1);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewCreatorToViewType.size()).isEqualTo(1);
        ViewCreator obtainedViewCreator = mRecyclerBinder.mRenderInfoViewCreatorController.mViewCreatorToViewType.keySet().iterator().next();
        assertThat(obtainedViewCreator).isEqualTo(RecyclerBinderTest.VIEW_CREATOR_1);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewTypeToViewCreator.indexOfValue(obtainedViewCreator)).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void testInsertMixedContentWithMultiViewCreator() {
        final List<Integer> viewItems = Arrays.asList(3, 6, 7, 11);
        prepareMixedLoadedBinder(30, new HashSet<>(viewItems), new RecyclerBinderTest.ViewCreatorProvider() {
            @Override
            public ViewCreator get() {
                // Different ViewCreator instances for each view item.
                return new ViewCreator() {
                    @Override
                    public View createView(Context c, ViewGroup parent) {
                        return Mockito.mock(View.class);
                    }
                };
            }
        });
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewTypeToViewCreator.size()).isEqualTo(4);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewCreatorToViewType.size()).isEqualTo(4);
        for (ViewCreator obtainedViewCreator : mRecyclerBinder.mRenderInfoViewCreatorController.mViewCreatorToViewType.keySet()) {
            assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewTypeToViewCreator.indexOfValue(obtainedViewCreator)).isGreaterThanOrEqualTo(0);
        }
    }

    @Test
    public void testInsertMixedContentFollowedByDelete() {
        mRecyclerBinder.insertItemAt(0, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        mRecyclerBinder.insertItemAt(1, ViewRenderInfo.create().viewBinder(new SimpleViewBinder()).viewCreator(RecyclerBinderTest.VIEW_CREATOR_1).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewCreatorToViewType.size()).isEqualTo(1);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewTypeToViewCreator.size()).isEqualTo(1);
        mRecyclerBinder.insertItemAt(2, ViewRenderInfo.create().viewBinder(new SimpleViewBinder()).viewCreator(RecyclerBinderTest.VIEW_CREATOR_2).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewCreatorToViewType.size()).isEqualTo(2);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewTypeToViewCreator.size()).isEqualTo(2);
        mRecyclerBinder.removeItemAt(1);
        mRecyclerBinder.removeItemAt(1);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewCreatorToViewType.size()).isEqualTo(2);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewTypeToViewCreator.size()).isEqualTo(2);
    }

    @Test
    public void testInsertMixedContentFollowedByUpdate() {
        mRecyclerBinder.insertItemAt(0, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        mRecyclerBinder.insertItemAt(1, ViewRenderInfo.create().viewBinder(new SimpleViewBinder()).viewCreator(RecyclerBinderTest.VIEW_CREATOR_1).build());
        mRecyclerBinder.insertItemAt(2, ViewRenderInfo.create().viewBinder(new SimpleViewBinder()).viewCreator(RecyclerBinderTest.VIEW_CREATOR_2).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewCreatorToViewType.size()).isEqualTo(2);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewTypeToViewCreator.size()).isEqualTo(2);
        mRecyclerBinder.updateItemAt(1, ViewRenderInfo.create().viewBinder(new SimpleViewBinder()).viewCreator(RecyclerBinderTest.VIEW_CREATOR_2).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewCreatorToViewType.size()).isEqualTo(2);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewTypeToViewCreator.size()).isEqualTo(2);
        mRecyclerBinder.updateItemAt(2, ViewRenderInfo.create().viewCreator(RecyclerBinderTest.VIEW_CREATOR_3).viewBinder(new SimpleViewBinder()).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewCreatorToViewType.size()).isEqualTo(3);
        assertThat(mRecyclerBinder.mRenderInfoViewCreatorController.mViewTypeToViewCreator.size()).isEqualTo(3);
    }

    @Test
    public void testCustomViewTypeEnabledViewTypeProvided() {
        final RecyclerBinder recyclerBinder = mRecyclerBinderBuilder.enableCustomViewType(5).build(mComponentContext);
        recyclerBinder.insertItemAt(0, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        recyclerBinder.insertItemAt(1, ViewRenderInfo.create().viewBinder(new SimpleViewBinder()).viewCreator(RecyclerBinderTest.VIEW_CREATOR_1).customViewType(10).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(recyclerBinder.mRenderInfoViewCreatorController.mViewCreatorToViewType.size()).isEqualTo(1);
    }

    @Test(expected = IllegalStateException.class)
    public void testCustomViewTypeEnabledViewTypeNotProvided() {
        final RecyclerBinder recyclerBinder = mRecyclerBinderBuilder.enableCustomViewType(4).build(mComponentContext);
        recyclerBinder.insertItemAt(0, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        recyclerBinder.insertItemAt(1, ViewRenderInfo.create().viewBinder(new SimpleViewBinder()).viewCreator(RecyclerBinderTest.VIEW_CREATOR_1).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
    }

    @Test(expected = IllegalStateException.class)
    public void testCustomViewTypeNotEnabledViewTypeProvided() {
        final RecyclerBinder recyclerBinder = mRecyclerBinderBuilder.build(mComponentContext);
        recyclerBinder.insertItemAt(0, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        recyclerBinder.insertItemAt(1, ViewRenderInfo.create().viewBinder(new SimpleViewBinder()).viewCreator(RecyclerBinderTest.VIEW_CREATOR_1).customViewType(10).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
    }

    @Test(expected = IllegalStateException.class)
    public void testCustomViewTypeEnabledComponentViewTypeSameAsCustomViewType() {
        final RecyclerBinder recyclerBinder = mRecyclerBinderBuilder.enableCustomViewType(2).build(mComponentContext);
        recyclerBinder.insertItemAt(0, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        recyclerBinder.insertItemAt(1, ViewRenderInfo.create().viewBinder(new SimpleViewBinder()).viewCreator(RecyclerBinderTest.VIEW_CREATOR_1).customViewType(2).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
    }

    @Test
    public void testShouldAlwaysUpdateLayoutHandler() {
        final LayoutHandler layoutHandlerBase = Mockito.mock(LayoutHandler.class);
        final LayoutHandler layoutHandler1 = Mockito.mock(LayoutHandler.class);
        final LayoutHandler layoutHandler2 = Mockito.mock(LayoutHandler.class);
        final LayoutHandler layoutHandlerN = Mockito.mock(LayoutHandler.class);
        final RecyclerBinder recyclerBinder = mRecyclerBinderBuilder.layoutHandlerFactory(new LayoutHandlerFactory() {
            @Nullable
            @Override
            public LayoutHandler createLayoutCalculationHandler(RenderInfo renderInfo) {
                final Object handlerType = renderInfo.getCustomAttribute("handlerType");
                if (handlerType == null) {
                    return layoutHandlerBase;
                } else
                    if (((Integer) (handlerType)) == 1) {
                        return layoutHandler1;
                    } else
                        if (((Integer) (handlerType)) == 2) {
                            return layoutHandler2;
                        } else {
                            return layoutHandlerN;
                        }


            }

            @Override
            public boolean shouldUpdateLayoutHandler(RenderInfo previousRenderInfo, RenderInfo newRenderInfo) {
                return true;
            }
        }).build(mComponentContext);
        final Component component0 = Mockito.mock(Component.class);
        final Component component1 = Mockito.mock(Component.class);
        final Component component2 = Mockito.mock(Component.class);
        recyclerBinder.insertItemAt(0, ComponentRenderInfo.create().component(component0).build());
        recyclerBinder.insertItemAt(1, ComponentRenderInfo.create().component(component1).customAttribute("handlerType", 1).build());
        recyclerBinder.insertItemAt(2, ComponentRenderInfo.create().component(component2).customAttribute("handlerType", 2).build());
        assertThat(mHoldersForComponents.get(component0).mLayoutHandler).isSameAs(layoutHandlerBase);
        assertThat(mHoldersForComponents.get(component1).mLayoutHandler).isSameAs(layoutHandler1);
        assertThat(mHoldersForComponents.get(component2).mLayoutHandler).isSameAs(layoutHandler2);
        recyclerBinder.updateItemAt(1, ComponentRenderInfo.create().component(component1).build());
        assertThat(mHoldersForComponents.get(component1).mLayoutHandler).isSameAs(layoutHandlerBase);
        recyclerBinder.updateItemAt(2, ComponentRenderInfo.create().component(component2).customAttribute("handlerType", 10).build());
        assertThat(mHoldersForComponents.get(component2).mLayoutHandler).isSameAs(layoutHandlerN);
    }

    @Test
    public void testShouldNeverUpdateLayoutHandler() {
        final LayoutHandler layoutHandler1 = Mockito.mock(LayoutHandler.class);
        final LayoutHandler layoutHandler2 = Mockito.mock(LayoutHandler.class);
        final LayoutHandler layoutHandlerN = Mockito.mock(LayoutHandler.class);
        final RecyclerBinder recyclerBinder = mRecyclerBinderBuilder.layoutHandlerFactory(new LayoutHandlerFactory() {
            @Nullable
            @Override
            public LayoutHandler createLayoutCalculationHandler(RenderInfo renderInfo) {
                final Object handlerType = renderInfo.getCustomAttribute("handlerType");
                if (handlerType == null) {
                    return null;
                } else
                    if (((Integer) (handlerType)) == 1) {
                        return layoutHandler1;
                    } else
                        if (((Integer) (handlerType)) == 2) {
                            return layoutHandler2;
                        } else {
                            return layoutHandlerN;
                        }


            }

            @Override
            public boolean shouldUpdateLayoutHandler(RenderInfo previousRenderInfo, RenderInfo newRenderInfo) {
                return false;
            }
        }).build(mComponentContext);
        final Component component0 = Mockito.mock(Component.class);
        final Component component1 = Mockito.mock(Component.class);
        final Component component2 = Mockito.mock(Component.class);
        recyclerBinder.insertItemAt(0, ComponentRenderInfo.create().component(component0).build());
        recyclerBinder.insertItemAt(1, ComponentRenderInfo.create().component(component1).customAttribute("handlerType", 1).build());
        recyclerBinder.insertItemAt(2, ComponentRenderInfo.create().component(component2).customAttribute("handlerType", 2).build());
        recyclerBinder.updateItemAt(1, ComponentRenderInfo.create().component(component1).build());
        assertThat(mHoldersForComponents.get(component1).mLayoutHandler).isSameAs(layoutHandler1);
        recyclerBinder.updateItemAt(2, ComponentRenderInfo.create().component(component2).customAttribute("handlerType", 10).build());
        assertThat(mHoldersForComponents.get(component2).mLayoutHandler).isSameAs(layoutHandler2);
    }

    @Test
    public void testShouldUpdateOnlyFromFirstToSecondLayoutHandler() {
        final LayoutHandler layoutHandler1 = Mockito.mock(LayoutHandler.class);
        final LayoutHandler layoutHandler2 = Mockito.mock(LayoutHandler.class);
        final LayoutHandler layoutHandlerN = Mockito.mock(LayoutHandler.class);
        final RecyclerBinder recyclerBinder = mRecyclerBinderBuilder.layoutHandlerFactory(new LayoutHandlerFactory() {
            @Nullable
            @Override
            public LayoutHandler createLayoutCalculationHandler(RenderInfo renderInfo) {
                final Object handlerType = renderInfo.getCustomAttribute("handlerType");
                if (handlerType == null) {
                    return null;
                } else
                    if (((Integer) (handlerType)) == 1) {
                        return layoutHandler1;
                    } else
                        if (((Integer) (handlerType)) == 2) {
                            return layoutHandler2;
                        } else {
                            return layoutHandlerN;
                        }


            }

            @Override
            public boolean shouldUpdateLayoutHandler(RenderInfo previousRenderInfo, RenderInfo newRenderInfo) {
                final Object previousHandlerType = previousRenderInfo.getCustomAttribute("handlerType");
                final Object newHandlerType = newRenderInfo.getCustomAttribute("handlerType");
                return (((previousHandlerType != null) && (newHandlerType != null)) && (previousHandlerType.equals(1))) && (newHandlerType.equals(2));
            }
        }).build(mComponentContext);
        final Component component0 = Mockito.mock(Component.class);
        final Component component1 = Mockito.mock(Component.class);
        recyclerBinder.insertItemAt(0, ComponentRenderInfo.create().component(component0).build());
        recyclerBinder.insertItemAt(1, ComponentRenderInfo.create().component(component1).customAttribute("handlerType", 1).build());
        assertThat(mHoldersForComponents.get(component1).mLayoutHandler).isSameAs(layoutHandler1);
        recyclerBinder.updateItemAt(1, ComponentRenderInfo.create().component(component1).customAttribute("handlerType", 2).build());
        assertThat(mHoldersForComponents.get(component1).mLayoutHandler).isSameAs(layoutHandler2);
        recyclerBinder.updateItemAt(1, ComponentRenderInfo.create().component(component1).customAttribute("handlerType", 1).build());
        assertThat(mHoldersForComponents.get(component1).mLayoutHandler).isSameAs(layoutHandler2);
    }

    @Test
    public void testViewBinderBindAndUnbind() {
        final View view = Mockito.mock(View.class);
        final RecyclerView recyclerView = new RecyclerView(mComponentContext.getAndroidContext());
        ViewBinder viewBinder = Mockito.mock(ViewBinder.class);
        final ViewCreator<View> viewCreator = new ViewCreator<View>() {
            @Override
            public View createView(Context c, ViewGroup parent) {
                return view;
            }
        };
        mRecyclerBinder.insertItemAt(0, ViewRenderInfo.create().viewBinder(viewBinder).viewCreator(viewCreator).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mRecyclerBinder.mount(recyclerView);
        final ViewHolder vh = recyclerView.getAdapter().onCreateViewHolder(new android.widget.FrameLayout(mComponentContext.getAndroidContext()), ((DEFAULT_COMPONENT_VIEW_TYPE) + 1));
        recyclerView.getAdapter().onBindViewHolder(vh, 0);
        Mockito.verify(viewBinder).bind(view);
        Mockito.verify(viewBinder, Mockito.never()).unbind(view);
        recyclerView.getAdapter().onViewRecycled(vh);
        Mockito.verify(viewBinder, Mockito.times(1)).bind(view);
        Mockito.verify(viewBinder).unbind(view);
    }

    @Test
    public void testGetItemCount() {
        for (int i = 0; i < 100; i++) {
            assertThat(mRecyclerBinder.getItemCount()).isEqualTo(i);
            mRecyclerBinder.insertItemAt(i, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        }
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
    }

    @Test
    public void testAllComponentsRangeInitialized() {
        prepareLoadedBinder();
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNotNull();
    }

    @Test
    public void testMixedContentFirstItemIsViewRangeInitialized() {
        prepareMixedLoadedBinder(3, new HashSet<>(Arrays.asList(0)), new RecyclerBinderTest.ViewCreatorProvider() {
            @Override
            public ViewCreator get() {
                return RecyclerBinderTest.VIEW_CREATOR_1;
            }
        });
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNotNull();
    }

    @Test
    public void testAllViewsInsertItemRangeInitialized() {
        prepareMixedLoadedBinder(3, new HashSet<>(Arrays.asList(0, 1, 2)), new RecyclerBinderTest.ViewCreatorProvider() {
            @Override
            public ViewCreator get() {
                return RecyclerBinderTest.VIEW_CREATOR_1;
            }
        });
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNull();
        mRecyclerBinder.insertItemAt(1, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNotNull();
    }

    @Test
    public void testAllViewsUpdateItemRangeInitialized() {
        prepareMixedLoadedBinder(3, new HashSet<>(Arrays.asList(0, 1, 2)), new RecyclerBinderTest.ViewCreatorProvider() {
            @Override
            public ViewCreator get() {
                return RecyclerBinderTest.VIEW_CREATOR_1;
            }
        });
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNull();
        mRecyclerBinder.updateItemAt(1, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNotNull();
    }

    @Test
    public void testAllViewsInsertMultiItemRangeNotInitialized() {
        prepareMixedLoadedBinder(3, new HashSet<>(Arrays.asList(0, 1, 2)), new RecyclerBinderTest.ViewCreatorProvider() {
            @Override
            public ViewCreator get() {
                return RecyclerBinderTest.VIEW_CREATOR_1;
            }
        });
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNull();
        ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        renderInfos.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        renderInfos.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        mRecyclerBinder.insertRangeAt(0, renderInfos);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNotNull();
    }

    @Test
    public void testAllViewsUpdateMultiItemRangeNotInitialized() {
        prepareMixedLoadedBinder(3, new HashSet<>(Arrays.asList(0, 1, 2)), new RecyclerBinderTest.ViewCreatorProvider() {
            @Override
            public ViewCreator get() {
                return RecyclerBinderTest.VIEW_CREATOR_1;
            }
        });
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNull();
        ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        renderInfos.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        renderInfos.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        mRecyclerBinder.updateRangeAt(0, renderInfos);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNotNull();
    }

    @Test
    public void testAllViewsInsertComponentMeasureAfterRangeInitialized() {
        for (int i = 0; i < 3; i++) {
            mRecyclerBinder.insertItemAt(i, ViewRenderInfo.create().viewBinder(new SimpleViewBinder()).viewCreator(RecyclerBinderTest.VIEW_CREATOR_1).build());
        }
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNull();
        mRecyclerBinder.insertItemAt(1, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNull();
        Size size = new Size();
        int widthSpec = SizeSpec.makeSizeSpec(200, EXACTLY);
        int heightSpec = SizeSpec.makeSizeSpec(200, EXACTLY);
        mRecyclerBinder.measure(size, widthSpec, heightSpec, null);
        assertThat(mRecyclerBinder.getRangeCalculationResult()).isNotNull();
    }

    @Test
    public void testCircularRecyclerItemCount() {
        List<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            renderInfos.add(ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        }
        mCircularRecyclerBinder.insertRangeAt(0, renderInfos);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        Assert.assertEquals(Integer.MAX_VALUE, mCircularRecyclerBinder.getItemCount());
    }

    @Test
    public void testCircularRecyclerItemCountWithOneItem() {
        mCircularRecyclerBinder.insertItemAt(0, ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build());
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        Assert.assertEquals(Integer.MAX_VALUE, mCircularRecyclerBinder.getItemCount());
    }

    @Test
    public void testCircularRecyclerItemCOuntWithZeroItems() {
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        Assert.assertEquals(0, mCircularRecyclerBinder.getItemCount());
    }

    @Test
    public void testCircularRecyclerItemFirstVisible() {
        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        Mockito.when(mCircularLayoutInfo.getLayoutManager()).thenReturn(Mockito.mock(LayoutManager.class));
        mCircularRecyclerBinder.mount(recyclerView);
        Mockito.verify(recyclerView).scrollToPosition(((Integer.MAX_VALUE) / 2));
    }

    @Test
    public void testCircularRecyclerItemFirstVisibleWithScrollToIndex() {
        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        Mockito.when(mCircularLayoutInfo.getLayoutManager()).thenReturn(Mockito.mock(LayoutManager.class));
        mCircularRecyclerBinder.scrollToPosition(1);
        mCircularRecyclerBinder.mount(recyclerView);
        Mockito.verify(recyclerView).scrollToPosition((((Integer.MAX_VALUE) / 2) + 1));
    }

    @Test
    public void testCircularRecyclerInitRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder(mCircularRecyclerBinder, 10);
        TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
        assertThat(holder.isTreeValid()).isTrue();
        assertThat(holder.mLayoutSyncCalled).isTrue();
    }

    @Test
    public void testCircularRecyclerMeasure() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder(mCircularRecyclerBinder, 10);
        int widthSpec = SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY);
        int heightSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        mCircularRecyclerBinder.measure(new Size(), widthSpec, heightSpec, null);
        TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
        assertThat(holder.isTreeValid()).isTrue();
        assertThat(holder.mLayoutSyncCalled).isTrue();
        for (int i = 1; i < 10; i++) {
            holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.isTreeValid()).isTrue();
            assertThat(holder.mLayoutAsyncCalled).isTrue();
        }
    }

    @Test
    public void testCircularRecyclerMeasureExact() {
        RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false)).build(mComponentContext);
        final List<RenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Component component = new InlineLayoutSpec() {
                @Override
                protected Component onCreateLayout(ComponentContext c) {
                    return TestDrawableComponent.create(c).widthPx(100).heightPx(100).build();
                }
            };
            components.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, components);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        RecyclerView rv = new RecyclerView(RuntimeEnvironment.application);
        recyclerBinder.mount(rv);
        rv.measure(SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        recyclerBinder.setSize(200, 200);
        rv.layout(0, 0, 200, 200);
        assertThat(rv.getChildCount()).isGreaterThan(0);
        for (int i = 0; i < (rv.getChildCount()); i++) {
            LithoView lv = ((LithoView) (rv.getChildAt(i)));
            assertThat(lv.getMeasuredWidth()).isEqualTo(100);
            assertThat(lv.getMeasuredHeight()).isEqualTo(200);
        }
    }

    @Test
    public void testCircularRecyclerMeasureAtMost() {
        RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false) {
            @Override
            public int getChildWidthSpec(int widthSpec, RenderInfo renderInfo) {
                return SizeSpec.makeSizeSpec(SizeSpec.getSize(widthSpec), SizeSpec.AT_MOST);
            }
        }).build(mComponentContext);
        final List<RenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Component component = new InlineLayoutSpec() {
                @Override
                protected Component onCreateLayout(ComponentContext c) {
                    return TestDrawableComponent.create(c).widthPercent(50).heightPercent(25).build();
                }
            };
            components.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, components);
        mRecyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        RecyclerView rv = new RecyclerView(RuntimeEnvironment.application);
        recyclerBinder.mount(rv);
        rv.measure(SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        recyclerBinder.setSize(100, 100);
        rv.layout(0, 0, 100, 100);
        assertThat(rv.getChildCount()).isGreaterThan(0);
        for (int i = 0; i < (rv.getChildCount()); i++) {
            LithoView lv = ((LithoView) (rv.getChildAt(i)));
            assertThat(lv.getMeasuredWidth()).isEqualTo(50);
            assertThat(lv.getMeasuredHeight()).isEqualTo(100);
        }
    }

    @Test
    public void testOnNewWorkingRange() {
        final List<ComponentRenderInfo> components = prepareLoadedBinder();
        final int firstVisibleIndex = 40;
        final int lastVisibleIndex = 82;
        final int rangeSize = Math.max(RecyclerBinderTest.RANGE_SIZE, (lastVisibleIndex - firstVisibleIndex));
        final int layoutRangeSize = ((int) (rangeSize * (RecyclerBinderTest.RANGE_RATIO)));
        final int rangeTotal = rangeSize + layoutRangeSize;
        mRecyclerBinder.onNewWorkingRange(firstVisibleIndex, lastVisibleIndex, (firstVisibleIndex + 1), (lastVisibleIndex - 1));
        TestComponentTreeHolder componentTreeHolder;
        for (int i = 0; i < (components.size()); i++) {
            componentTreeHolder = mHoldersForComponents.get(getComponent());
            assertThat(componentTreeHolder).isNotNull();
            if ((i >= (firstVisibleIndex - layoutRangeSize)) && (i <= (firstVisibleIndex + rangeTotal))) {
                assertThat(componentTreeHolder.mCheckWorkingRangeCalled).isTrue();
            } else {
                assertThat(componentTreeHolder.mCheckWorkingRangeCalled).isFalse();
            }
        }
    }

    @Test
    public void testInsertAsyncOnMainThread() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertItemAtAsync(0, renderInfo);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(0);
        assertThat(getComponent()).isEqualTo(component);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
        assertThat(holder.isTreeValid()).isTrue();
    }

    @Test
    public void testInsertAsyncOnBackgroundThread() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.insertItemAtAsync(0, renderInfo);
                recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
            }
        });
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        ShadowLooper.runUiThreadTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(0);
        assertThat(getComponent()).isEqualTo(component);
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
        assertThat(holder.isTreeValid()).isTrue();
    }

    @Test
    public void testMultipleInsertAsyncs() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.insertItemAtAsync(0, renderInfo);
                recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
            }
        });
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        ShadowLooper.runUiThreadTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        final Component component2 = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo2 = ComponentRenderInfo.create().component(component2).build();
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.insertItemAtAsync(0, renderInfo2);
                recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
            }
        });
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        ShadowLooper.runUiThreadTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(2);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(0);
        assertThat(getComponent()).isEqualTo(component);
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
        assertThat(holder.isTreeValid()).isTrue();
        final ComponentTreeHolder holder2 = recyclerBinder.getComponentTreeHolderAt(1);
        assertThat(getComponent()).isEqualTo(component2);
        assertThat(holder2.hasCompletedLatestLayout()).isTrue();
        assertThat(holder2.isTreeValid()).isTrue();
    }

    @Test
    public void testInsertAsyncBeforeInitialMeasure() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.insertItemAtAsync(0, renderInfo);
                recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
            }
        });
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        // TODO T36028263
        // assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        ShadowLooper.runUiThreadTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(0);
        assertThat(getComponent()).isEqualTo(component);
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
        assertThat(holder.isTreeValid()).isTrue();
    }

    @Test
    public void testInsertRangeAsync() {
        final int NUM_TO_INSERT = 5;
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.insertRangeAtAsync(0, renderInfos);
                recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
            }
        });
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        ShadowLooper.runUiThreadTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(NUM_TO_INSERT);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(i);
            assertThat(getComponent()).isEqualTo(components.get(i));
            assertThat(holder.hasCompletedLatestLayout()).isTrue();
            assertThat(holder.isTreeValid()).isTrue();
        }
    }

    @Test
    public void testInsertRangeAsyncBeforeInitialMeasure() {
        final int NUM_TO_INSERT = 5;
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.insertRangeAtAsync(0, renderInfos);
                recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
            }
        });
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        // TODO T36028263
        // assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        ShadowLooper.runUiThreadTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(NUM_TO_INSERT);
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(i);
            assertThat(getComponent()).isEqualTo(components.get(i));
            assertThat(holder.hasCompletedLatestLayout()).isTrue();
            assertThat(holder.isTreeValid()).isTrue();
        }
    }

    @Test
    public void testInsertAsync_AsyncMode() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        recyclerBinder.setCommitPolicy(LAYOUT_BEFORE_INSERT);
        final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertItemAtAsync(0, renderInfo);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(0);
        assertThat(getComponent()).isEqualTo(component);
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
        assertThat(holder.isTreeValid()).isTrue();
    }

    @Test
    public void testMultipleInsertAsyncs_AsyncMode() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        recyclerBinder.setCommitPolicy(LAYOUT_BEFORE_INSERT);
        final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertItemAtAsync(0, renderInfo);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        final Component component2 = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo2 = ComponentRenderInfo.create().component(component2).build();
        recyclerBinder.insertItemAtAsync(0, renderInfo2);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(2);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(0);
        assertThat(getComponent()).isEqualTo(component);
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
        assertThat(holder.isTreeValid()).isTrue();
        final ComponentTreeHolder holder2 = recyclerBinder.getComponentTreeHolderAt(1);
        assertThat(getComponent()).isEqualTo(component2);
        assertThat(holder2.hasCompletedLatestLayout()).isTrue();
        assertThat(holder2.isTreeValid()).isTrue();
    }

    @Test
    public void testInsertAsyncBeforeInitialMeasure_AsyncMode() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        recyclerBinder.setCommitPolicy(LAYOUT_BEFORE_INSERT);
        final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.insertItemAtAsync(0, renderInfo);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(0);
        assertThat(getComponent()).isEqualTo(component);
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
        assertThat(holder.isTreeValid()).isTrue();
    }

    @Test
    public void testInsertRangeAsync_AsyncMode() {
        final int NUM_TO_INSERT = 5;
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        recyclerBinder.setCommitPolicy(LAYOUT_BEFORE_INSERT);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(NUM_TO_INSERT);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(i);
            assertThat(getComponent()).isEqualTo(components.get(i));
            assertThat(holder.hasCompletedLatestLayout()).isTrue();
            assertThat(holder.isTreeValid()).isTrue();
        }
    }

    @Test
    public void testInsertRangeAsyncBeforeInitialMeasure_AsyncMode() {
        final int NUM_TO_INSERT = 5;
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        recyclerBinder.setCommitPolicy(LAYOUT_BEFORE_INSERT);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(NUM_TO_INSERT);
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(i);
            assertThat(getComponent()).isEqualTo(components.get(i));
            assertThat(holder.hasCompletedLatestLayout()).isTrue();
            assertThat(holder.isTreeValid()).isTrue();
        }
    }

    @Test
    public void testInsertRangeAsyncBeforeInitialMeasureRangeIsLargerThanMeasure_AsyncMode() {
        final int NUM_TO_INSERT = 5;
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        recyclerBinder.setCommitPolicy(LAYOUT_BEFORE_INSERT);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(600).heightPx(600).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        // compute one layout to ensure batching behavior remains
        mLayoutThreadShadowLooper.runOneTask();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        // finish computing all layouts - batch should now be applied
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(NUM_TO_INSERT);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(i);
            assertThat(getComponent()).isEqualTo(components.get(i));
            assertThat(holder.hasCompletedLatestLayout()).isTrue();
            assertThat(holder.isTreeValid()).isTrue();
        }
    }

    @Test
    public void testInsertsDispatchedInBatch_AsyncMode() {
        final int NUM_TO_INSERT = 5;
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        recyclerBinder.setCommitPolicy(LAYOUT_BEFORE_INSERT);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        // complete first layout
        mLayoutThreadShadowLooper.runOneTask();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        // complete second layout
        mLayoutThreadShadowLooper.runOneTask();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        // complete the rest of the layouts
        for (int i = 2; i < NUM_TO_INSERT; i++) {
            mLayoutThreadShadowLooper.runToEndOfTasks();
        }
        assertThat(recyclerBinder.getItemCount()).isEqualTo(NUM_TO_INSERT);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
    }

    @Test
    public void testInsertAsyncWithSizeChangeBeforeCompletion() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertItemAtAsync(0, renderInfo);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(500, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(0);
        assertThat(getComponent()).isEqualTo(component);
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
        assertThat(holder.isTreeValid()).isTrue();
        assertHasCompatibleLayout(recyclerBinder, 0, SizeSpec.makeSizeSpec(500, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED));
    }

    @Test
    public void testInsertAsyncWithSizeChangeBeforeBatchClosed() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertItemAtAsync(0, renderInfo);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(500, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(0);
        assertThat(getComponent()).isEqualTo(component);
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
        assertThat(holder.isTreeValid()).isTrue();
        assertHasCompatibleLayout(recyclerBinder, 0, SizeSpec.makeSizeSpec(500, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED));
    }

    @Test(expected = RuntimeException.class)
    public void testThrowsIfSyncInsertAfterAsyncInsert() {
        mRecyclerBinder.insertItemAtAsync(0, createTestComponentRenderInfo());
        mRecyclerBinder.insertItemAt(1, createTestComponentRenderInfo());
        mRecyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
    }

    @Test(expected = RuntimeException.class)
    public void testThrowsIfSyncInsertAfterAsyncInsertRange() {
        final ArrayList<RenderInfo> firstInsert = new ArrayList<>();
        final ArrayList<RenderInfo> secondInsert = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            firstInsert.add(createTestComponentRenderInfo());
            secondInsert.add(createTestComponentRenderInfo());
        }
        mRecyclerBinder.insertRangeAtAsync(0, firstInsert);
        mRecyclerBinder.insertRangeAt(firstInsert.size(), secondInsert);
        mRecyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
    }

    @Test
    public void testRemoveAsync() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertItemAtAsync(0, renderInfo);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(1);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
        recyclerBinder.removeItemAtAsync(0);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNotNull();
    }

    @Test
    public void testRemoveAsyncMixedWithInserts() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(0));
        recyclerBinder.insertItemAtAsync(1, renderInfos.get(1));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(2);
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(2));
        recyclerBinder.removeItemAtAsync(0);
        recyclerBinder.insertItemAtAsync(2, renderInfos.get(3));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(3);
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 0, components.get(0));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 1, components.get(1));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 2, components.get(3));
    }

    @Test
    public void testMoveAsync() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(4);
        recyclerBinder.moveItemAsync(0, 2);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(4);
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 0, components.get(1));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 1, components.get(2));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 2, components.get(0));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 3, components.get(3));
    }

    @Test
    public void testMoveAsyncMixedWithInserts() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(0));
        recyclerBinder.insertItemAtAsync(1, renderInfos.get(1));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(2);
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(2));
        recyclerBinder.moveItemAsync(0, 1);
        recyclerBinder.insertItemAtAsync(2, renderInfos.get(3));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(4);
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 0, components.get(0));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 1, components.get(2));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 2, components.get(3));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 3, components.get(1));
    }

    @Test
    public void testUpdateAsyncMixedWithInserts() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(0));
        recyclerBinder.insertItemAtAsync(1, renderInfos.get(1));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(2);
        final ComponentRenderInfo newRenderInfo0 = ComponentRenderInfo.create().component(TestDrawableComponent.create(mComponentContext).widthPx(50).heightPx(50).build()).build();
        final ComponentRenderInfo newRenderInfo1 = ComponentRenderInfo.create().component(TestDrawableComponent.create(mComponentContext).widthPx(50).heightPx(50).build()).build();
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(2));
        recyclerBinder.updateItemAtAsync(0, newRenderInfo0);
        recyclerBinder.insertItemAtAsync(2, renderInfos.get(3));
        recyclerBinder.updateItemAtAsync(1, newRenderInfo1);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(4);
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 0, newRenderInfo0.getComponent());
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 1, newRenderInfo1.getComponent());
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 2, components.get(3));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 3, components.get(1));
    }

    @Test
    public void testUpdateAsyncMixedWithOtherOperations() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(0));
        recyclerBinder.insertItemAtAsync(1, renderInfos.get(1));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(2);
        final ComponentRenderInfo newRenderInfo0 = ComponentRenderInfo.create().component(TestDrawableComponent.create(mComponentContext).widthPx(50).heightPx(50).build()).build();
        final ComponentRenderInfo newRenderInfo1 = ComponentRenderInfo.create().component(TestDrawableComponent.create(mComponentContext).widthPx(50).heightPx(50).build()).build();
        final ComponentRenderInfo newRenderInfo2 = ComponentRenderInfo.create().component(TestDrawableComponent.create(mComponentContext).widthPx(50).heightPx(50).build()).build();
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(2));
        recyclerBinder.updateItemAtAsync(0, newRenderInfo0);
        recyclerBinder.removeItemAtAsync(2);
        recyclerBinder.insertItemAtAsync(1, renderInfos.get(3));
        recyclerBinder.moveItemAsync(1, 2);
        recyclerBinder.updateItemAtAsync(1, newRenderInfo1);
        recyclerBinder.insertRangeAtAsync(0, renderInfos.subList(4, renderInfos.size()));
        recyclerBinder.updateItemAtAsync(2, newRenderInfo2);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(6);
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 0, components.get(4));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 1, components.get(5));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 2, newRenderInfo2.getComponent());
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 3, newRenderInfo0.getComponent());
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 4, newRenderInfo1.getComponent());
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 5, components.get(3));
    }

    @Test
    public void testUpdateRangeAtAsync() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertRangeAtAsync(0, renderInfos.subList(0, 5));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(5);
        final ArrayList<Component> newComponents = new ArrayList<>();
        final ArrayList<RenderInfo> newRenderInfos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            newComponents.add(component);
            newRenderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(5));
        recyclerBinder.updateRangeAtAsync(0, newRenderInfos);
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(6));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(7);
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 0, components.get(6));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 1, newComponents.get(0));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 2, newComponents.get(1));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 3, newComponents.get(2));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 4, newComponents.get(3));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 5, components.get(3));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 6, components.get(4));
    }

    @Test
    public void testRemoveRangeAtAsync() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertRangeAtAsync(0, renderInfos.subList(0, 5));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(5);
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(5));
        recyclerBinder.removeRangeAtAsync(0, 3);
        recyclerBinder.insertItemAtAsync(0, renderInfos.get(6));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(4);
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 0, components.get(6));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 1, components.get(2));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 2, components.get(3));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 3, components.get(4));
    }

    @Test
    public void testInsertAsyncOutOfOrderBeforeMeasure() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos.subList(0, 3));
        recyclerBinder.insertRangeAtAsync(0, renderInfos.subList(3, renderInfos.size()));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(7);
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 0, components.get(3));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 1, components.get(4));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 2, components.get(5));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 3, components.get(6));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 4, components.get(0));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 5, components.get(1));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 6, components.get(2));
    }

    @Test
    public void testInsertAsyncAndMutationsBeforeMeasure() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        final Component updatedIndex3 = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.removeRangeAtAsync(1, 3);
        recyclerBinder.updateItemAtAsync(3, ComponentRenderInfo.create().component(updatedIndex3).build());
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(4);
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 0, components.get(0));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 1, components.get(4));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 2, components.get(5));
        RecyclerBinderTest.assertComponentAtEquals(recyclerBinder, 3, updatedIndex3);
    }

    @Test
    public void testClearAsync() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.clearAsync();
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
    }

    @Test
    public void testClearAsyncBeforeMeasure() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.clearAsync();
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
    }

    @Test
    public void testRemoveItemsAsyncBeforeMeasure() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos.subList(0, 3));
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        recyclerBinder.insertRangeAtAsync(0, renderInfos.subList(3, 6));
        recyclerBinder.removeRangeAtAsync(0, 3);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), null);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getRenderInfoAt(0)).isEqualTo(renderInfos.get(0));
        assertThat(recyclerBinder.getRenderInfoAt(1)).isEqualTo(renderInfos.get(1));
        assertThat(recyclerBinder.getRenderInfoAt(2)).isEqualTo(renderInfos.get(2));
        final Component component0 = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final Component component1 = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        final Component component2 = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
        recyclerBinder.updateItemAtAsync(0, ComponentRenderInfo.create().component(component0).build());
        recyclerBinder.updateItemAtAsync(1, ComponentRenderInfo.create().component(component1).build());
        recyclerBinder.updateItemAtAsync(2, ComponentRenderInfo.create().component(component2).build());
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(3);
        assertThat(getComponent()).isEqualTo(component0);
        assertThat(getComponent()).isEqualTo(component1);
        assertThat(getComponent()).isEqualTo(component2);
    }

    @Test
    public void testRemoveAllItemsAsyncBeforeMeasure() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        recyclerBinder.removeRangeAtAsync(0, renderInfos.size());
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), null);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
    }

    @Test
    public void testRenderStateWithNotifyItemRenderCompleteAt() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        recyclerBinder.mount(recyclerView);
        final Component component = TestDrawableComponent.create(mComponentContext).build();
        final EventHandler<RenderCompleteEvent> renderCompleteEventHandler = ((EventHandler<RenderCompleteEvent>) (Mockito.mock(EventHandler.class)));
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).renderCompleteHandler(renderCompleteEventHandler).build();
        recyclerBinder.insertItemAt(0, renderInfo);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(0);
        assertThat(holder.getRenderState()).isEqualTo(RENDER_UNINITIALIZED);
        recyclerBinder.notifyItemRenderCompleteAt(0, 0);
        Mockito.verify(recyclerView).postOnAnimation(ArgumentMatchers.any(RenderCompleteRunnable.class));
        assertThat(holder.getRenderState()).isEqualTo(RENDER_DRAWN);
    }

    @Test
    public void testOnDataBound() {
        final ChangeSetCompleteCallback changeSetCompleteCallback1 = Mockito.mock(ChangeSetCompleteCallback.class);
        final ChangeSetCompleteCallback changeSetCompleteCallback2 = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos1 = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos1.add(ComponentRenderInfo.create().component(component).build());
            renderInfos2.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, renderInfos1);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback1);
        Mockito.verify(changeSetCompleteCallback1).onDataBound();
        Mockito.reset(changeSetCompleteCallback1);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
        recyclerBinder.insertRangeAt(renderInfos1.size(), renderInfos2);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback2);
        Mockito.verify(changeSetCompleteCallback1, Mockito.never()).onDataBound();
        Mockito.verify(changeSetCompleteCallback2).onDataBound();
    }

    @Test
    public void testOnDataBoundInsertAsync() {
        final ChangeSetCompleteCallback changeSetCompleteCallback1 = Mockito.mock(ChangeSetCompleteCallback.class);
        final ChangeSetCompleteCallback changeSetCompleteCallback2 = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos1 = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos1.add(ComponentRenderInfo.create().component(component).build());
            renderInfos2.add(ComponentRenderInfo.create().component(component).build());
        }
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.insertRangeAtAsync(0, renderInfos1);
                recyclerBinder.notifyChangeSetCompleteAsync(true, changeSetCompleteCallback1);
            }
        });
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        Mockito.verify(changeSetCompleteCallback1, Mockito.never()).onDataBound();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
        // TODO T36028263
        // verify(changeSetCompleteCallback1, never()).onDataBound();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        Mockito.verify(changeSetCompleteCallback1).onDataBound();
        Mockito.reset(changeSetCompleteCallback1);
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.insertRangeAtAsync(renderInfos1.size(), renderInfos2);
                recyclerBinder.notifyChangeSetCompleteAsync(true, changeSetCompleteCallback2);
            }
        });
        Mockito.verify(changeSetCompleteCallback2, Mockito.never()).onDataBound();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        Mockito.verify(changeSetCompleteCallback1, Mockito.never()).onDataBound();
        Mockito.verify(changeSetCompleteCallback2).onDataBound();
    }

    @Test
    public void testOnDataBoundInsertAsync_AsyncMode() {
        final ChangeSetCompleteCallback changeSetCompleteCallback1 = Mockito.mock(ChangeSetCompleteCallback.class);
        final ChangeSetCompleteCallback changeSetCompleteCallback2 = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        recyclerBinder.setCommitPolicy(LAYOUT_BEFORE_INSERT);
        final ArrayList<RenderInfo> renderInfos1 = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos1.add(ComponentRenderInfo.create().component(component).build());
            renderInfos2.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos1);
        recyclerBinder.notifyChangeSetCompleteAsync(true, changeSetCompleteCallback1);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        assertThat(recyclerBinder.getRangeCalculationResult()).isNull();
        Mockito.verify(changeSetCompleteCallback1, Mockito.never()).onDataBound();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
        Mockito.verify(changeSetCompleteCallback1, Mockito.never()).onDataBound();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        Mockito.verify(changeSetCompleteCallback1).onDataBound();
        Mockito.reset(changeSetCompleteCallback1);
        recyclerBinder.insertRangeAtAsync(renderInfos1.size(), renderInfos2);
        recyclerBinder.notifyChangeSetCompleteAsync(true, changeSetCompleteCallback2);
        Mockito.verify(changeSetCompleteCallback2, Mockito.never()).onDataBound();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        Mockito.verify(changeSetCompleteCallback1, Mockito.never()).onDataBound();
        Mockito.verify(changeSetCompleteCallback2).onDataBound();
    }

    @Test
    public void testOnDataBoundInsertAsyncLessThanViewport_AsyncMode() {
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        recyclerBinder.setCommitPolicy(LAYOUT_BEFORE_INSERT);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, changeSetCompleteCallback);
        Mockito.verify(changeSetCompleteCallback, Mockito.never()).onDataBound();
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        Mockito.verify(changeSetCompleteCallback).onDataBound();
    }

    @Test
    public void testOnDataRenderedWithMountAfterInsert() {
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback);
        // Mount view after insertions
        final LithoRecylerView recyclerView = new LithoRecylerView(RuntimeEnvironment.application);
        recyclerBinder.mount(recyclerView);
        // Simulate calling ViewGroup#dispatchDraw(Canvas).
        recyclerView.dispatchDraw(Mockito.mock(Canvas.class));
        Mockito.verify(changeSetCompleteCallback).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
    }

    @Test
    public void testOnDataRenderedWithMountAfterInsertAsync() {
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, changeSetCompleteCallback);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        // Mount view after insertions
        final LithoRecylerView recyclerView = new LithoRecylerView(RuntimeEnvironment.application);
        recyclerBinder.mount(recyclerView);
        // Simulate calling ViewGroup#dispatchDraw(Canvas).
        recyclerView.dispatchDraw(Mockito.mock(Canvas.class));
        Mockito.verify(changeSetCompleteCallback).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
    }

    @Test
    public void testOnDataRenderedWithMountUnMountBeforeInsert() {
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        // mount() and unmount() are called prior to data insertions.
        final RecyclerView recyclerView = Mockito.mock(LithoRecylerView.class);
        recyclerBinder.mount(recyclerView);
        recyclerBinder.unmount(recyclerView);
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback);
        Mockito.verify(changeSetCompleteCallback).onDataRendered(ArgumentMatchers.eq(false), ArgumentMatchers.anyLong());
    }

    @Test
    public void testOnDataRenderedWithNoPendingUpdate() {
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        final RecyclerView recyclerView = Mockito.mock(LithoRecylerView.class);
        Mockito.when(recyclerView.hasPendingAdapterUpdates()).thenReturn(false);
        Mockito.when(recyclerView.isAttachedToWindow()).thenReturn(true);
        Mockito.when(recyclerView.getWindowVisibility()).thenReturn(VISIBLE);
        Mockito.when(recyclerView.getAlpha()).thenReturn(1.0F);
        Mockito.when(recyclerView.getVisibility()).thenReturn(VISIBLE);
        Mockito.when(recyclerView.getGlobalVisibleRect(ArgumentMatchers.any(Rect.class))).thenReturn(true);
        recyclerBinder.mount(recyclerView);
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback);
        Mockito.verify(changeSetCompleteCallback).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
    }

    @Test
    public void testOnDataRenderedWithViewDetachedFromWindow() {
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        final RecyclerView recyclerView = Mockito.mock(LithoRecylerView.class);
        Mockito.when(recyclerView.hasPendingAdapterUpdates()).thenReturn(true);
        Mockito.when(recyclerView.isAttachedToWindow()).thenReturn(false);
        Mockito.when(recyclerView.getWindowVisibility()).thenReturn(VISIBLE);
        Mockito.when(recyclerView.getAlpha()).thenReturn(1.0F);
        Mockito.when(recyclerView.getVisibility()).thenReturn(VISIBLE);
        Mockito.when(recyclerView.getGlobalVisibleRect(ArgumentMatchers.any(Rect.class))).thenReturn(true);
        recyclerBinder.mount(recyclerView);
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback);
        Mockito.verify(changeSetCompleteCallback).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
    }

    @Test
    public void testOnDataRenderedWithViewVisibilityIsGone() {
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        final RecyclerView recyclerView = Mockito.mock(LithoRecylerView.class);
        Mockito.when(recyclerView.hasPendingAdapterUpdates()).thenReturn(true);
        Mockito.when(recyclerView.isAttachedToWindow()).thenReturn(true);
        Mockito.when(recyclerView.getWindowVisibility()).thenReturn(GONE);
        Mockito.when(recyclerView.getAlpha()).thenReturn(1.0F);
        Mockito.when(recyclerView.getVisibility()).thenReturn(GONE);
        Mockito.when(recyclerView.getGlobalVisibleRect(ArgumentMatchers.any(Rect.class))).thenReturn(false);
        recyclerBinder.mount(recyclerView);
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback);
        Mockito.verify(changeSetCompleteCallback).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
    }

    @Test
    public void testOnDataRenderedWithMultipleUpdates() {
        final ChangeSetCompleteCallback changeSetCompleteCallback1 = Mockito.mock(ChangeSetCompleteCallback.class);
        final ChangeSetCompleteCallback changeSetCompleteCallback2 = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos1 = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos1.add(ComponentRenderInfo.create().component(component).build());
            renderInfos2.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, renderInfos1);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback1);
        Mockito.verify(changeSetCompleteCallback1, Mockito.never()).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
        recyclerBinder.insertRangeAt(0, renderInfos2);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback2);
        Mockito.verify(changeSetCompleteCallback2, Mockito.never()).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
        // Mount view after insertions
        final LithoRecylerView recyclerView = new LithoRecylerView(RuntimeEnvironment.application);
        recyclerBinder.mount(recyclerView);
        // Simulate calling ViewGroup#dispatchDraw(Canvas).
        recyclerView.dispatchDraw(Mockito.mock(Canvas.class));
        Mockito.verify(changeSetCompleteCallback1).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
        Mockito.verify(changeSetCompleteCallback2).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
    }

    @Test
    public void testOnDataRenderedWithMultipleAsyncUpdates() {
        final ChangeSetCompleteCallback changeSetCompleteCallback1 = Mockito.mock(ChangeSetCompleteCallback.class);
        final ChangeSetCompleteCallback changeSetCompleteCallback2 = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos1 = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos1.add(ComponentRenderInfo.create().component(component).build());
            renderInfos2.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos1);
        recyclerBinder.notifyChangeSetCompleteAsync(true, changeSetCompleteCallback1);
        Mockito.verify(changeSetCompleteCallback1, Mockito.never()).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
        recyclerBinder.insertRangeAtAsync(0, renderInfos2);
        recyclerBinder.notifyChangeSetCompleteAsync(true, changeSetCompleteCallback2);
        Mockito.verify(changeSetCompleteCallback2, Mockito.never()).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
        // Mount view after insertions
        final LithoRecylerView recyclerView = new LithoRecylerView(RuntimeEnvironment.application);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo((2 * 5));
        recyclerBinder.mount(recyclerView);
        // Simulate calling ViewGroup#dispatchDraw(Canvas).
        recyclerView.dispatchDraw(Mockito.mock(Canvas.class));
        Mockito.verify(changeSetCompleteCallback1).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
        Mockito.verify(changeSetCompleteCallback2).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
    }

    @Test
    public void testOnDataRenderedWithNoChanges() {
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        final RecyclerView recyclerView = Mockito.mock(LithoRecylerView.class);
        Mockito.when(recyclerView.hasPendingAdapterUpdates()).thenReturn(true);
        recyclerBinder.mount(recyclerView);
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback);
        Mockito.verify(changeSetCompleteCallback).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
        Mockito.reset(changeSetCompleteCallback);
        // Call notifyChangeSetComplete with no actual data change.
        recyclerBinder.notifyChangeSetComplete(false, changeSetCompleteCallback);
        Mockito.verify(changeSetCompleteCallback).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
        Mockito.verify(recyclerView, Mockito.never()).postOnAnimation(recyclerBinder.mRemeasureRunnable);
    }

    @Test
    public void testChangeSetCompleteCallbacksIsNotEmptyWithInsertBeforeMount() {
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback);
        // Mount view after insertions
        recyclerBinder.mount(recyclerView);
        assertThat(recyclerBinder.mDataRenderedCallbacks).isNotEmpty();
    }

    @Test
    public void testChangeSetCompleteCallbacksIsNotEmptyWithAsyncInsertBeforeMount() {
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, changeSetCompleteCallback);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        // Mount view after insertions
        recyclerBinder.mount(recyclerView);
        assertThat(recyclerBinder.mDataRenderedCallbacks).isNotEmpty();
    }

    @Test
    public void testDataRenderedCallbacksIsEmptyWithInsertAfterMount() {
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        // Mount view before insertions
        recyclerBinder.mount(recyclerView);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback);
        assertThat(recyclerBinder.mDataRenderedCallbacks).isEmpty();
    }

    @Test
    public void testDataRenderedCallbacksIsEmptyWithAsyncInsertAfterMount() {
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
        // Mount view before insertions
        recyclerBinder.mount(recyclerView);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, changeSetCompleteCallback);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
        assertThat(recyclerBinder.mDataRenderedCallbacks).isEmpty();
    }

    // Async init range tests.
    @Test
    public void testInitRangeAsync() {
        RecyclerBinder recyclerBinder = mRecyclerBinderForAsyncInitRangeBuilder.build(mComponentContext);
        final List<RenderInfo> components = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            final Component component = new InlineLayoutSpec() {
                @Override
                protected Component onCreateLayout(ComponentContext c) {
                    return TestDrawableComponent.create(c).widthPx(0).heightPx(0).build();
                }
            };
            RenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
            components.add(renderInfo);
        }
        recyclerBinder.insertRangeAt(0, components);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        for (int i = 0; i < 30; i++) {
            assertThat(mHoldersForComponents.get(getComponent())).isNotNull();
        }
        final Size size = new Size();
        final int widthSpec = SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY);
        Mockito.when(mLayoutInfo.getChildHeightSpec(ArgumentMatchers.anyInt(), ArgumentMatchers.any(RenderInfo.class))).thenReturn(SizeSpec.makeSizeSpec(0, EXACTLY));
        Mockito.when(mLayoutInfo.getChildWidthSpec(ArgumentMatchers.anyInt(), ArgumentMatchers.any(RenderInfo.class))).thenReturn(SizeSpec.makeSizeSpec(0, EXACTLY));
        Mockito.when(mLayoutInfo.approximateRangeSize(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(0);
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(mHoldersForComponents.get(getComponent()).mLayoutSyncCalled).isTrue();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(mHoldersForComponents.get(getComponent()).mLayoutAsyncCalled).isTrue();
        assertThat(mHoldersForComponents.get(getComponent()).mLayoutAsyncCalled).isFalse();
    }

    @Test
    public void testInitRangeAsyncThreadPool() {
        final CountDownLatch lockRangeIsNotNull = new CountDownLatch(1);
        final CountDownLatch lockInitRangeFinishes1 = new CountDownLatch(1);
        final CountDownLatch lockInitRangeFinishes2 = new CountDownLatch(1);
        final CountDownLatch lockTest = new CountDownLatch(2);
        Mockito.when(mLayoutInfo.getChildHeightSpec(ArgumentMatchers.anyInt(), ArgumentMatchers.any(RenderInfo.class))).thenReturn(SizeSpec.makeSizeSpec(0, EXACTLY));
        Mockito.when(mLayoutInfo.getChildWidthSpec(ArgumentMatchers.anyInt(), ArgumentMatchers.any(RenderInfo.class))).thenReturn(SizeSpec.makeSizeSpec(0, EXACTLY));
        Mockito.when(mLayoutInfo.approximateRangeSize(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(0);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().asyncInitRange(true).rangeRatio(0).layoutInfo(mLayoutInfo).threadPoolConfig(new LayoutThreadPoolConfigurationImpl(2, 2, 5)).build(mComponentContext);
        final List<RenderInfo> components = new ArrayList<>();
        int NOT_SET = -1;
        final int SYNC = 1;
        final int ASYNC = 2;
        final List<Integer> syncLayouts = new ArrayList<>(30);
        for (int i = 0; i < 30; i++) {
            syncLayouts.add(NOT_SET);
        }
        final Component initRangeComponent = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                syncLayouts.set(0, (ThreadUtils.isMainThread() ? SYNC : ASYNC));
                lockRangeIsNotNull.countDown();
                return null;
            }
        };
        RenderInfo renderInfo = ComponentRenderInfo.create().component(initRangeComponent).build();
        components.add(renderInfo);
        for (int i = 1; i < 30; i++) {
            final int finalI = i;
            final Component component = new InlineLayoutSpec() {
                @Override
                protected Component onCreateLayout(ComponentContext c) {
                    syncLayouts.set(finalI, (ThreadUtils.isMainThread() ? SYNC : ASYNC));
                    if (finalI == 1) {
                        try {
                            lockInitRangeFinishes1.await(5, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lockTest.countDown();
                    }
                    if (finalI == 2) {
                        try {
                            lockInitRangeFinishes2.await(5, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lockTest.countDown();
                    }
                    return null;
                }
            };
            renderInfo = ComponentRenderInfo.create().component(component).build();
            components.add(renderInfo);
        }
        recyclerBinder.insertRangeAt(0, components);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        for (int i = 0; i < 30; i++) {
            final ComponentTreeHolder holder = recyclerBinder.getComponentTreeHolderAt(i);
            assertThat(holder).isNotNull();
        }
        final int widthSpec = SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY);
        recyclerBinder.initRange(SizeSpec.getSize(widthSpec), SizeSpec.getSize(heightSpec), new RecyclerBinder.ComponentTreeHolderRangeInfo(0, recyclerBinder.getComponentTreeHolders()), VERTICAL);
        try {
            lockRangeIsNotNull.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lockInitRangeFinishes1.countDown();
        lockInitRangeFinishes2.countDown();
        try {
            lockTest.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertThat(recyclerBinder.getComponentTreeHolderAt(0).isTreeValid()).isTrue();
        assertThat(syncLayouts.get(0)).isEqualTo(SYNC);
        assertThat(recyclerBinder.getComponentTreeHolderAt(1).isTreeValid()).isTrue();
        assertThat(syncLayouts.get(1)).isEqualTo(ASYNC);
        assertThat(recyclerBinder.getComponentTreeHolderAt(2).isTreeValid()).isTrue();
        assertThat(syncLayouts.get(2)).isEqualTo(ASYNC);
        assertThat(recyclerBinder.getComponentTreeHolderAt(3).isTreeValid()).isFalse();
        assertThat(syncLayouts.get(3)).isEqualTo(NOT_SET);
    }

    @Test
    public void testSyncLayoutForUnknownSizeSpec() {
        final boolean splitInitRange = ComponentsConfiguration.checkNeedsRemeasure;
        ComponentsConfiguration.splitLayoutForMeasureAndRangeEstimation = true;
        final int NUM_TO_INSERT = 20;
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false)).componentTreeHolderFactory(mComponentTreeHolderFactory).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).color(i).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(100, SizeSpec.UNSPECIFIED), Mockito.mock(EventHandler.class));
        assertThat(recyclerBinder.getItemCount()).isEqualTo(NUM_TO_INSERT);
        assertThat(recyclerBinder.requiresRemeasure()).isFalse();
        assertThat(recyclerBinder.mSizeForMeasure.height).isEqualTo(100);
        assertThat(recyclerBinder.mEstimatedViewportCount).isEqualTo(10);
        assertThat(mHoldersForComponents.get(getComponent()).mLayoutSyncCalled).isTrue();
        assertThat(mHoldersForComponents.get(getComponent()).mLayoutAsyncCalled).isTrue();
        ComponentsConfiguration.splitLayoutForMeasureAndRangeEstimation = splitInitRange;
    }

    @Test
    public void testNoSyncLayoutForKnownSizeSpec() {
        final boolean splitInitRange = ComponentsConfiguration.checkNeedsRemeasure;
        ComponentsConfiguration.splitLayoutForMeasureAndRangeEstimation = true;
        final int NUM_TO_INSERT = 20;
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false)).componentTreeHolderFactory(mComponentTreeHolderFactory).build(mComponentContext);
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).color(i).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), Mockito.mock(EventHandler.class));
        assertThat(recyclerBinder.getItemCount()).isEqualTo(NUM_TO_INSERT);
        assertThat(recyclerBinder.requiresRemeasure()).isFalse();
        assertThat(mHoldersForComponents.get(getComponent()).mLayoutSyncCalled).isFalse();
        assertThat(recyclerBinder.mSizeForMeasure).isNull();
        assertThat(recyclerBinder.mEstimatedViewportCount).isEqualTo(10);
        assertThat(mHoldersForComponents.get(getComponent()).mLayoutAsyncCalled).isTrue();
        assertThat(mHoldersForComponents.get(getComponent()).mLayoutAsyncCalled).isTrue();
        ComponentsConfiguration.splitLayoutForMeasureAndRangeEstimation = splitInitRange;
    }

    @Test
    public void testDataRenderedCallbacksAreNotTriggered() {
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final ComponentsLogger componentsLogger = Mockito.mock(ComponentsLogger.class);
        final ComponentContext componentContext = new ComponentContext(RuntimeEnvironment.application, "", componentsLogger);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(componentContext);
        for (int i = 0; i < 40; i++) {
            recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback);
        }
        final RecyclerView recyclerView = Mockito.mock(LithoRecylerView.class);
        Mockito.when(recyclerView.hasPendingAdapterUpdates()).thenReturn(true);
        Mockito.when(recyclerView.isAttachedToWindow()).thenReturn(true);
        Mockito.when(recyclerView.getWindowVisibility()).thenReturn(VISIBLE);
        Mockito.when(recyclerView.getAlpha()).thenReturn(1.0F);
        Mockito.when(recyclerView.getVisibility()).thenReturn(VISIBLE);
        Mockito.when(recyclerView.getGlobalVisibleRect(ArgumentMatchers.any(Rect.class))).thenReturn(true);
        recyclerBinder.mount(recyclerView);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback);
        Mockito.verify(componentsLogger).emitMessage(ArgumentMatchers.eq(ERROR), ArgumentMatchers.anyString());
        assertThat(recyclerBinder.mDataRenderedCallbacks).isEmpty();
    }

    @Test(expected = IllegalStateException.class)
    public void testAsyncOperationsFromMultipleThreadsCrashes() throws InterruptedException {
        final boolean isDebugMode = ComponentsConfiguration.isDebugModeEnabled;
        // Manually override this to cause change set thread checks
        ComponentsConfiguration.isDebugModeEnabled = true;
        try {
            final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderTest.RANGE_RATIO).build(mComponentContext);
            final Component component1 = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            final Component component2 = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            final ComponentRenderInfo renderInfo1 = ComponentRenderInfo.create().component(component1).build();
            final ComponentRenderInfo renderInfo2 = ComponentRenderInfo.create().component(component2).build();
            recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), null);
            final CountDownLatch latch = new CountDownLatch(1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        recyclerBinder.insertItemAtAsync(0, renderInfo2);
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
            assertThat(latch.await(5000, TimeUnit.MILLISECONDS)).isTrue();
            recyclerBinder.insertItemAtAsync(0, renderInfo1);
        } finally {
            ComponentsConfiguration.isDebugModeEnabled = isDebugMode;
        }
    }

    @Test
    public void testHScrollAsyncModeInsertFirstOnMainThread() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false)).rangeRatio(10).hscrollAsyncMode(true).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            final Component component2 = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
            renderInfos2.add(ComponentRenderInfo.create().component(component2).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        recyclerBinder.insertRangeAtAsync(5, renderInfos2);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // Measure on main means we don't pre-compute all the layouts
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
        assertThat(recyclerBinder.getItemCount()).isEqualTo(10);
        // First is completed for range calculation
        assertThat(recyclerBinder.getComponentTreeHolderAt(0).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(1).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(2).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(3).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(4).hasCompletedLatestLayout()).isFalse();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(10);
        assertThat(recyclerBinder.getComponentTreeHolderAt(5).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(6).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(7).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(8).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(9).hasCompletedLatestLayout()).isTrue();
    }

    @Test
    public void testHScrollAsyncModeInsertFirstOnBGThread() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false)).rangeRatio(10).hscrollAsyncMode(true).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            final Component component2 = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
            renderInfos2.add(ComponentRenderInfo.create().component(component2).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        recyclerBinder.insertRangeAtAsync(5, renderInfos2);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
            }
        });
        ShadowLooper.runUiThreadTasks();
        // Measure on background means we fill the viewport
        assertThat(recyclerBinder.getItemCount()).isEqualTo(10);
        assertThat(recyclerBinder.getComponentTreeHolderAt(0).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(1).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(2).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(3).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(4).hasCompletedLatestLayout()).isFalse();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(10);
        assertThat(recyclerBinder.getComponentTreeHolderAt(5).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(6).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(7).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(8).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(9).hasCompletedLatestLayout()).isTrue();
    }

    @Test
    public void testHScrollAsyncModeInsertBeforeBatchApplied() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false)).rangeRatio(10).hscrollAsyncMode(true).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.insertRangeAtAsync(0, renderInfos);
                recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
                recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
            }
        });
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        ShadowLooper.runUiThreadTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(5);
        assertThat(recyclerBinder.getComponentTreeHolderAt(0).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(1).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(2).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(3).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(4).hasCompletedLatestLayout()).isFalse();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(recyclerBinder.getComponentTreeHolderAt(2).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(3).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(4).hasCompletedLatestLayout()).isTrue();
    }

    @Test
    public void testHScrollAsyncModeDoesNotFillViewportTwice() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false)).rangeRatio(10).hscrollAsyncMode(true).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            final Component component2 = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
            renderInfos2.add(ComponentRenderInfo.create().component(component2).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
            }
        });
        ShadowLooper.runUiThreadTasks();
        // Measure on background means we fill the viewport
        assertThat(recyclerBinder.getItemCount()).isEqualTo(5);
        assertThat(recyclerBinder.getComponentTreeHolderAt(0).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(1).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(2).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(3).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(4).hasCompletedLatestLayout()).isFalse();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(300, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(300, SizeSpec.EXACTLY), null);
            }
        });
        ShadowLooper.runUiThreadTasks();
        assertThat(recyclerBinder.getComponentTreeHolderAt(1).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(1).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(2).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(3).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(4).hasCompletedLatestLayout()).isFalse();
    }

    @Test
    public void testNoEagerLayoutCalculationWithHScrollAsyncModeOff() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false)).rangeRatio(10).hscrollAsyncMode(false).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
            }
        });
        ShadowLooper.runUiThreadTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(5);
        assertThat(recyclerBinder.getComponentTreeHolderAt(0).hasCompletedLatestLayout()).isTrue();
        assertThat(recyclerBinder.getComponentTreeHolderAt(1).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(2).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(3).hasCompletedLatestLayout()).isFalse();
        assertThat(recyclerBinder.getComponentTreeHolderAt(4).hasCompletedLatestLayout()).isFalse();
    }

    @Test
    public void testAsyncBatchesAppliedAfterMeasureWithEarlyExit() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false)).rangeRatio(10).build(mComponentContext);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
                // Second measure will early exit
                recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
            }
        });
        ShadowLooper.runUiThreadTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(5);
    }

    @Test
    public void testBothLayoutHandlerFactoryAndThreadPoolConfigProvided() {
        final LayoutHandler layoutHandler = Mockito.mock(LayoutHandler.class);
        final Component component = Mockito.mock(Component.class);
        final RecyclerBinder binder = mRecyclerBinderBuilder.threadPoolConfig(new LayoutThreadPoolConfigurationImpl(3, 3, 0)).layoutHandlerFactory(new LayoutHandlerFactory() {
            @Override
            public LayoutHandler createLayoutCalculationHandler(RenderInfo renderInfo) {
                return layoutHandler;
            }

            @Override
            public boolean shouldUpdateLayoutHandler(RenderInfo previousRenderInfo, RenderInfo newRenderInfo) {
                return false;
            }
        }).build(mComponentContext);
        binder.insertItemAt(0, ComponentRenderInfo.create().component(component).build());
        assertThat(mHoldersForComponents.get(component).mLayoutHandler).isSameAs(layoutHandler);
    }

    @Test
    public void testDoNotApplyReadyBatchesWhileRecyclerViewIsInScrollOrLayout() {
        ShadowLooper.pauseMainLooper();
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.VERTICAL, false)).rangeRatio(10).build(mComponentContext);
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        Mockito.when(recyclerView.isComputingLayout()).thenReturn(true);
        recyclerBinder.mount(recyclerView);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        // Run for a bit -- runTasks causes the test to hang because of how ShadowLooper is implemented
        for (int i = 0; i < 10; i++) {
            ShadowLooper.runMainLooperOneTask();
        }
        assertThat(recyclerBinder.getItemCount()).isEqualTo(0);
        System.err.println("done");
        Mockito.when(recyclerView.isComputingLayout()).thenReturn(false);
        ShadowLooper.runUiThreadTasks();
        assertThat(recyclerBinder.getItemCount()).isEqualTo(5);
    }

    @Test(expected = RuntimeException.class)
    public void testApplyReadyBatchesInfiniteLoop() {
        ShadowLooper.pauseMainLooper();
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.VERTICAL, false)).rangeRatio(10).build(mComponentContext);
        final RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        Mockito.when(recyclerView.isComputingLayout()).thenReturn(true);
        recyclerBinder.mount(recyclerView);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY), null);
        recyclerBinder.insertRangeAtAsync(0, renderInfos);
        recyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        for (int i = 0; i < 10000; i++) {
            ShadowLooper.runMainLooperOneTask();
        }
        fail("Should have escaped infinite retries with exception.");
    }

    @Test
    public void tesLayoutAsyncInRegisterAsyncInsertWhenRemesureIsNotNeeded() {
        final boolean checkNeedsRemeasure = ComponentsConfiguration.checkNeedsRemeasure;
        ComponentsConfiguration.checkNeedsRemeasure = true;
        final int NUM_TO_INSERT = 5;
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        mRecyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), Mockito.mock(EventHandler.class));
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                mRecyclerBinder.insertRangeAtAsync(0, renderInfos);
                mRecyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
            }
        });
        assertThat(mRecyclerBinder.getItemCount()).isEqualTo(0);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        ShadowLooper.runUiThreadTasks();
        assertThat(mRecyclerBinder.getItemCount()).isEqualTo(NUM_TO_INSERT);
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.mLayoutAsyncCalled).isTrue();
        }
        ComponentsConfiguration.checkNeedsRemeasure = checkNeedsRemeasure;
    }

    @Test
    public void testNoLayoutAsyncInRegisterAsyncInsertWhenRemesureIsNeeded() {
        final boolean checkNeedsRemeasure = ComponentsConfiguration.checkNeedsRemeasure;
        ComponentsConfiguration.checkNeedsRemeasure = true;
        final int NUM_TO_INSERT = 5;
        final ArrayList<Component> components = new ArrayList<>();
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).widthPx(100).heightPx(100).build();
            components.add(component);
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        mRecyclerBinder.measure(new Size(), SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY), Mockito.mock(EventHandler.class));
        RecyclerBinderTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                mRecyclerBinder.insertRangeAtAsync(0, renderInfos);
                mRecyclerBinder.notifyChangeSetCompleteAsync(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
            }
        });
        assertThat(mRecyclerBinder.getItemCount()).isEqualTo(0);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        ShadowLooper.runUiThreadTasks();
        assertThat(mRecyclerBinder.getItemCount()).isEqualTo(NUM_TO_INSERT);
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final TestComponentTreeHolder holder = mHoldersForComponents.get(getComponent());
            assertThat(holder.mLayoutAsyncCalled).isFalse();
        }
        ComponentsConfiguration.checkNeedsRemeasure = checkNeedsRemeasure;
    }

    private static class NoOpChangeSetCompleteCallback implements ChangeSetCompleteCallback {
        @Override
        public void onDataBound() {
        }

        @Override
        public void onDataRendered(boolean isMounted, long uptimeMillis) {
        }
    }
}

