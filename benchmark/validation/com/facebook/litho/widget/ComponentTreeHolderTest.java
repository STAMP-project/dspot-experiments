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


import ComponentTree.NewLayoutStateReadyListener;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentTree;
import com.facebook.litho.EventHandler;
import com.facebook.litho.RenderCompleteEvent;
import com.facebook.litho.Size;
import com.facebook.litho.SizeSpec;
import com.facebook.litho.testing.Whitebox;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.shadows.ShadowLooper;


/**
 * Tests for {@link ComponentTreeHolder}
 */
@RunWith(ComponentsTestRunner.class)
public class ComponentTreeHolderTest {
    private ComponentContext mContext;

    private Component mComponent;

    private EventHandler<RenderCompleteEvent> mRenderCompleteEventHandler;

    private ComponentRenderInfo mComponentRenderInfo;

    private ViewRenderInfo mViewRenderInfo;

    private ShadowLooper mLayoutThreadShadowLooper;

    private int mWidthSpec = SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY);

    private int mHeightSpec = SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY);

    private int mWidthSpec2 = SizeSpec.makeSizeSpec(101, SizeSpec.EXACTLY);

    private int mHeightSpec2 = SizeSpec.makeSizeSpec(101, SizeSpec.EXACTLY);

    @Test
    public void testHasCompletedLatestLayoutWhenNoLayoutComputed() {
        ComponentTreeHolder holder = createComponentTreeHolder(mComponentRenderInfo);
        assertThat(holder.hasCompletedLatestLayout()).isFalse();
    }

    @Test
    public void testRetainAnimationStateAfterExitingRange() {
        ComponentTreeHolder holder = createComponentTreeHolder(mComponentRenderInfo);
        holder.computeLayoutSync(mContext, mWidthSpec, mHeightSpec, new Size());
        assertThat(holder.getComponentTree().hasMounted()).isFalse();
        Whitebox.setInternalState(holder.getComponentTree(), "mHasMounted", true);
        // component goes out of range
        holder.acquireStateAndReleaseTree();
        assertThat(holder.getComponentTree()).isNull();
        // component comes back within range
        holder.computeLayoutSync(mContext, mWidthSpec, mHeightSpec, new Size());
        assertThat(holder.getComponentTree().hasMounted()).isTrue();
    }

    @Test
    public void testHasCompletedLatestLayoutForSyncRender() {
        ComponentTreeHolder holder = createComponentTreeHolder(mComponentRenderInfo);
        holder.computeLayoutSync(mContext, mWidthSpec, mHeightSpec, new Size());
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
    }

    @Test
    public void testHasCompletedLatestLayoutForAsyncRender() {
        ComponentTreeHolder holder = createComponentTreeHolder(mComponentRenderInfo);
        holder.computeLayoutAsync(mContext, mWidthSpec, mHeightSpec);
        assertThat(holder.hasCompletedLatestLayout()).isFalse();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
        // Re-computing with the same async layout specs shouldn't invalidate the layout
        holder.computeLayoutAsync(mContext, mWidthSpec, mHeightSpec);
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
    }

    @Test
    public void testHasCompletedLatestLayoutForAsyncRenderAfterSyncRender() {
        ComponentTreeHolder holder = createComponentTreeHolder(mComponentRenderInfo);
        holder.computeLayoutSync(mContext, mWidthSpec, mHeightSpec, new Size());
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
        holder.computeLayoutAsync(mContext, mWidthSpec2, mHeightSpec2);
        assertThat(holder.hasCompletedLatestLayout()).isFalse();
    }

    @Test
    public void testHasCompletedLatestLayoutForViewBasedTreeHolder() {
        ComponentTreeHolder holder = createComponentTreeHolder(mViewRenderInfo);
        assertThat(holder.hasCompletedLatestLayout()).isTrue();
    }

    @Test
    public void testSetListenerBeforeTreeCreation() {
        ComponentTreeHolder holder = createComponentTreeHolder(mComponentRenderInfo);
        ComponentTree.NewLayoutStateReadyListener listener = Mockito.mock(NewLayoutStateReadyListener.class);
        holder.setNewLayoutReadyListener(listener);
        assertThat(holder.getComponentTree()).isNull();
        holder.computeLayoutSync(mContext, SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), new Size());
        assertThat(holder.getComponentTree().getNewLayoutStateReadyListener()).isEqualTo(listener);
        holder.setNewLayoutReadyListener(null);
        assertThat(holder.getComponentTree().getNewLayoutStateReadyListener()).isNull();
    }

    @Test
    public void testSetListenerAfterTreeCreation() {
        ComponentTreeHolder holder = createComponentTreeHolder(mComponentRenderInfo);
        ComponentTree.NewLayoutStateReadyListener listener = Mockito.mock(NewLayoutStateReadyListener.class);
        holder.computeLayoutSync(mContext, SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), new Size());
        holder.setNewLayoutReadyListener(listener);
        assertThat(holder.getComponentTree().getNewLayoutStateReadyListener()).isEqualTo(listener);
    }

    @Test
    public void testSetListenerOnViewRenderInfoDoesNotCrash() {
        ComponentTreeHolder holder = createComponentTreeHolder(mViewRenderInfo);
        ComponentTree.NewLayoutStateReadyListener listener = Mockito.mock(NewLayoutStateReadyListener.class);
        holder.setNewLayoutReadyListener(listener);
    }

    @Test
    public void testGetRenderCompleteHandlerDoesNotCrash() {
        ComponentTreeHolder holder = createComponentTreeHolder(mComponentRenderInfo);
        holder.getRenderInfo().getRenderCompleteEventHandler();
    }
}

