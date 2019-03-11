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


import OrientationHelper.HORIZONTAL;
import OrientationHelper.VERTICAL;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.EventHandler;
import com.facebook.litho.Size;
import com.facebook.litho.SizeSpec;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.shadows.ShadowLooper;


/**
 * Tests for {@link RecyclerBinder} with wrap content enabled.
 */
@RunWith(ComponentsTestRunner.class)
public class RecyclerBinderWrapContentTest {
    private static final float RANGE_RATIO = 2.0F;

    private ComponentContext mComponentContext;

    private ShadowLooper mLayoutThreadShadowLooper;

    private RecyclerView mRecyclerView;

    @Test
    public void testWrapContentWithInsertOnVertical() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderWrapContentTest.RANGE_RATIO).wrapContent(true).build(mComponentContext);
        recyclerBinder.mount(mRecyclerView);
        final Component component = TestDrawableComponent.create(mComponentContext).measuredHeight(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.insertItemAt(0, renderInfo);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final Size size = new Size();
        // Manually invoke the remeasure to get the measured size
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.height).isEqualTo(100);
        // Keep inserting items to exceed the maximum height
        for (int i = 0; i < 10; i++) {
            final Component newComponent = TestDrawableComponent.create(mComponentContext).measuredHeight(100).build();
            recyclerBinder.insertItemAt((i + 1), ComponentRenderInfo.create().component(newComponent).build());
            recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        }
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView, Mockito.times(10)).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.height).isEqualTo(1000);
    }

    @Test
    public void testWrapContentWithInsertRangeOnVertical() {
        final int NUM_TO_INSERT = 6;
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderWrapContentTest.RANGE_RATIO).wrapContent(true).build(mComponentContext);
        recyclerBinder.mount(mRecyclerView);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).measuredHeight(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final Size size = new Size();
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.height).isEqualTo(600);
        // Keep inserting items to exceed the maximum height.
        final ArrayList<RenderInfo> newRenderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).measuredHeight(100).build();
            newRenderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, newRenderInfos);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.height).isEqualTo(1000);
    }

    @Test
    public void testWrapContentWithRemoveOnVertical() {
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final RecyclerBinder recyclerBinder = prepareBinderWithMeasuredChildSize(widthSpec, heightSpec, 8, VERTICAL, 100);
        recyclerBinder.mount(mRecyclerView);
        recyclerBinder.removeItemAt(0);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        Size size = new Size();
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.height).isEqualTo(700);
    }

    @Test
    public void testWrapContentWithRemoveRangeOnVertical() {
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final RecyclerBinder recyclerBinder = prepareBinderWithMeasuredChildSize(widthSpec, heightSpec, 8, VERTICAL, 100);
        recyclerBinder.mount(mRecyclerView);
        recyclerBinder.removeRangeAt(0, 3);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        Size size = new Size();
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.height).isEqualTo(500);
    }

    @Test
    public void testWrapContentWithUpdateOnVertical() {
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final RecyclerBinder recyclerBinder = prepareBinderWithMeasuredChildSize(widthSpec, heightSpec, 8, VERTICAL, 100);
        recyclerBinder.mount(mRecyclerView);
        final Component newComponent = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return TestDrawableComponent.create(c).measuredHeight(200).build();
            }
        };
        recyclerBinder.updateItemAt(0, newComponent);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        Size size = new Size();
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.height).isEqualTo(900);
    }

    @Test
    public void testWrapContentWithUpdateRangeOnVertical() {
        final int NUM_TO_UPDATE = 5;
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final RecyclerBinder recyclerBinder = prepareBinderWithMeasuredChildSize(widthSpec, heightSpec, 8, VERTICAL, 100);
        recyclerBinder.mount(mRecyclerView);
        final ArrayList<RenderInfo> newRenderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_UPDATE; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).measuredHeight(50).build();
            newRenderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.updateRangeAt(0, newRenderInfos);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        Size size = new Size();
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.height).isEqualTo(550);
    }

    @Test
    public void testWrapContentWithInsertOnHorizontal() {
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false)).rangeRatio(RecyclerBinderWrapContentTest.RANGE_RATIO).wrapContent(true).build(mComponentContext);
        recyclerBinder.mount(mRecyclerView);
        final Component component = TestDrawableComponent.create(mComponentContext).measuredWidth(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.insertItemAt(0, renderInfo);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final Size size = new Size();
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.width).isEqualTo(100);
        // Keep inserting items to exceed the maximum height.
        for (int i = 0; i < 10; i++) {
            final Component newComponent = TestDrawableComponent.create(mComponentContext).measuredWidth(100).build();
            recyclerBinder.insertItemAt((i + 1), ComponentRenderInfo.create().component(newComponent).build());
            recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        }
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView, Mockito.times(10)).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.width).isEqualTo(1000);
    }

    @Test
    public void testWrapContentWithInsertRangeOnHorizontal() {
        final int NUM_TO_INSERT = 6;
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().layoutInfo(new LinearLayoutInfo(mComponentContext, OrientationHelper.HORIZONTAL, false)).rangeRatio(RecyclerBinderWrapContentTest.RANGE_RATIO).wrapContent(true).build(mComponentContext);
        recyclerBinder.mount(mRecyclerView);
        final ArrayList<RenderInfo> renderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).measuredWidth(100).build();
            renderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, renderInfos);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final Size size = new Size();
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.width).isEqualTo(600);
        // Keep inserting items to exceed the maximum height.
        final ArrayList<RenderInfo> newRenderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_INSERT; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).measuredWidth(100).build();
            newRenderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.insertRangeAt(0, newRenderInfos);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.width).isEqualTo(1000);
    }

    @Test
    public void testWrapContentWithRemoveOnHorizontal() {
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final RecyclerBinder recyclerBinder = prepareBinderWithMeasuredChildSize(widthSpec, heightSpec, 8, HORIZONTAL, 100);
        recyclerBinder.mount(mRecyclerView);
        recyclerBinder.removeItemAt(0);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        Size size = new Size();
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.width).isEqualTo(700);
    }

    @Test
    public void testWrapContentWithRemoveRangeOnHorizontal() {
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final RecyclerBinder recyclerBinder = prepareBinderWithMeasuredChildSize(widthSpec, heightSpec, 8, HORIZONTAL, 100);
        recyclerBinder.mount(mRecyclerView);
        recyclerBinder.removeRangeAt(0, 3);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        Size size = new Size();
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.width).isEqualTo(500);
    }

    @Test
    public void testWrapContentWithUpdateOnHorizontal() {
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final RecyclerBinder recyclerBinder = prepareBinderWithMeasuredChildSize(widthSpec, heightSpec, 8, HORIZONTAL, 100);
        recyclerBinder.mount(mRecyclerView);
        final Component newComponent = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return TestDrawableComponent.create(c).measuredWidth(200).build();
            }
        };
        recyclerBinder.updateItemAt(0, newComponent);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        Size size = new Size();
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.width).isEqualTo(900);
    }

    @Test
    public void testWrapContentWithUpdateRangeOnHorizontal() {
        final int NUM_TO_UPDATE = 5;
        final int widthSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(1000, SizeSpec.EXACTLY);
        final RecyclerBinder recyclerBinder = prepareBinderWithMeasuredChildSize(widthSpec, heightSpec, 8, HORIZONTAL, 100);
        recyclerBinder.mount(mRecyclerView);
        final ArrayList<RenderInfo> newRenderInfos = new ArrayList<>();
        for (int i = 0; i < NUM_TO_UPDATE; i++) {
            final Component component = TestDrawableComponent.create(mComponentContext).measuredWidth(50).build();
            newRenderInfos.add(ComponentRenderInfo.create().component(component).build());
        }
        recyclerBinder.updateRangeAt(0, newRenderInfos);
        recyclerBinder.notifyChangeSetComplete(true, RecyclerBinderTest.NO_OP_CHANGE_SET_COMPLETE_CALLBACK);
        // Verify remeasure is triggered through View#postOnAnimation(Runnable)
        Mockito.verify(mRecyclerView).postOnAnimation(recyclerBinder.mRemeasureRunnable);
        Size size = new Size();
        recyclerBinder.measure(size, widthSpec, heightSpec, Mockito.mock(EventHandler.class));
        assertThat(size.width).isEqualTo(550);
    }

    @Test
    public void testOnDataRenderedWithNoChanges() {
        final LithoRecylerView recyclerView = Mockito.mock(LithoRecylerView.class);
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        final RecyclerBinder recyclerBinder = new RecyclerBinder.Builder().rangeRatio(RecyclerBinderWrapContentTest.RANGE_RATIO).wrapContent(true).build(mComponentContext);
        recyclerBinder.mount(recyclerView);
        final Component component = TestDrawableComponent.create(mComponentContext).measuredHeight(100).build();
        final ComponentRenderInfo renderInfo = ComponentRenderInfo.create().component(component).build();
        recyclerBinder.insertItemAt(0, renderInfo);
        recyclerBinder.notifyChangeSetComplete(true, changeSetCompleteCallback);
        Mockito.verify(changeSetCompleteCallback).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
        Mockito.reset(changeSetCompleteCallback);
        // Call notifyChangeSetComplete with no actual data change.
        recyclerBinder.notifyChangeSetComplete(false, changeSetCompleteCallback);
        Mockito.verify(changeSetCompleteCallback).onDataRendered(ArgumentMatchers.eq(true), ArgumentMatchers.anyLong());
        Mockito.verify(mRecyclerView, Mockito.never()).postOnAnimation(recyclerBinder.mRemeasureRunnable);
    }
}

