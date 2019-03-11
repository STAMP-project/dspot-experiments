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
package com.facebook.litho;


import LayoutState.CalculateLayoutSource.TEST;
import SizeSpec.AT_MOST;
import SizeSpec.EXACTLY;
import SizeSpec.UNSPECIFIED;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.TestLayoutComponent;
import com.facebook.litho.testing.Whitebox;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowLooper;

import static ErrorBoundariesConfiguration.rootWrapperComponentFactory;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


@RunWith(ComponentsTestRunner.class)
public class ComponentTreeTest {
    private int mWidthSpec;

    private int mWidthSpec2;

    private int mHeightSpec;

    private int mHeightSpec2;

    private Component mComponent;

    private ShadowLooper mLayoutThreadShadowLooper;

    private ComponentContext mContext;

    private RootWrapperComponentFactory mOldWrapperConfig;

    @Test
    public void testCreate() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        creationCommonChecks(componentTree);
        // Both the main thread and the background layout state shouldn't be calculated yet.
        Assert.assertNull(Whitebox.getInternalState(componentTree, "mMainThreadLayoutState"));
        Assert.assertNull(Whitebox.getInternalState(componentTree, "mBackgroundLayoutState"));
        Assert.assertFalse(ComponentTreeTest.componentTreeHasSizeSpec(componentTree));
    }

    @Test
    public void testSetSizeSpec() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setSizeSpec(mWidthSpec, mHeightSpec);
        // Since this happens post creation, it's not in general safe to update the main thread layout
        // state synchronously, so the result should be in the background layout state
        postSizeSpecChecks(componentTree, "mBackgroundLayoutState");
    }

    @Test
    public void testSetSizeSpecAsync() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setSizeSpecAsync(mWidthSpec, mHeightSpec);
        // Only fields changed but no layout is done yet.
        assertThat(ComponentTreeTest.componentTreeHasSizeSpec(componentTree)).isTrue();
        assertThat(((int) (Whitebox.getInternalState(componentTree, "mWidthSpec")))).isEqualTo(mWidthSpec);
        assertThat(((int) (Whitebox.getInternalState(componentTree, "mHeightSpec")))).isEqualTo(mHeightSpec);
        Assert.assertNull(Whitebox.getInternalState(componentTree, "mMainThreadLayoutState"));
        Assert.assertNull(Whitebox.getInternalState(componentTree, "mBackgroundLayoutState"));
        // Now the background thread run the queued task.
        mLayoutThreadShadowLooper.runOneTask();
        // Since this happens post creation, it's not in general safe to update the main thread layout
        // state synchronously, so the result should be in the background layout state
        postSizeSpecChecks(componentTree, "mBackgroundLayoutState");
    }

    @Test
    public void testSetSizeSpecAsyncThenSyncBeforeRunningTask() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setSizeSpecAsync(mWidthSpec, mHeightSpec);
        componentTree.setSizeSpec(mWidthSpec2, mHeightSpec2);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        // Since this happens post creation, it's not in general safe to update the main thread layout
        // state synchronously, so the result should be in the background layout state
        postSizeSpecChecks(componentTree, "mBackgroundLayoutState", mWidthSpec2, mHeightSpec2);
    }

    @Test
    public void testSetSizeSpecAsyncThenSyncAfterRunningTask() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setSizeSpecAsync(mWidthSpec, mHeightSpec);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        componentTree.setSizeSpec(mWidthSpec2, mHeightSpec2);
        // Since this happens post creation, it's not in general safe to update the main thread layout
        // state synchronously, so the result should be in the background layout state
        postSizeSpecChecks(componentTree, "mBackgroundLayoutState", mWidthSpec2, mHeightSpec2);
    }

    @Test
    public void testSetSizeSpecWithOutput() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        Size size = new Size();
        componentTree.setSizeSpec(mWidthSpec, mHeightSpec, size);
        junit.framework.Assert.assertEquals(SizeSpec.getSize(mWidthSpec), size.width, 0.0);
        junit.framework.Assert.assertEquals(SizeSpec.getSize(mHeightSpec), size.height, 0.0);
        // Since this happens post creation, it's not in general safe to update the main thread layout
        // state synchronously, so the result should be in the background layout state
        postSizeSpecChecks(componentTree, "mBackgroundLayoutState");
    }

    @Test
    public void testSetSizeSpecWithOutputWhenAttachedToViewWithSameSpec() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setLithoView(new LithoView(mContext));
        Size size = new Size();
        componentTree.measure(mWidthSpec, mHeightSpec, new int[2], false);
        componentTree.attach();
        componentTree.setSizeSpec(mWidthSpec, mHeightSpec, size);
        junit.framework.Assert.assertEquals(SizeSpec.getSize(mWidthSpec), size.width, 0.0);
        junit.framework.Assert.assertEquals(SizeSpec.getSize(mHeightSpec), size.height, 0.0);
        assertThat(componentTree.hasCompatibleLayout(mWidthSpec, mHeightSpec)).isTrue();
        assertThat(componentTree.getRoot()).isEqualTo(mComponent);
    }

    @Test
    public void testSetSizeSpecWithOutputWhenAttachedToViewWithNewSpec() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setLithoView(new LithoView(mContext));
        Size size = new Size();
        componentTree.measure(mWidthSpec2, mHeightSpec2, new int[2], false);
        componentTree.attach();
        componentTree.setSizeSpec(mWidthSpec, mHeightSpec, size);
        junit.framework.Assert.assertEquals(SizeSpec.getSize(mWidthSpec), size.width, 0.0);
        junit.framework.Assert.assertEquals(SizeSpec.getSize(mHeightSpec), size.height, 0.0);
        assertThat(componentTree.hasCompatibleLayout(mWidthSpec, mHeightSpec)).isTrue();
        assertThat(componentTree.getRoot()).isEqualTo(mComponent);
    }

    @Test
    public void testSetCompatibleSizeSpec() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        Size size = new Size();
        componentTree.setSizeSpec(SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST), size);
        junit.framework.Assert.assertEquals(100, size.width, 0.0);
        junit.framework.Assert.assertEquals(100, size.height, 0.0);
        LayoutState firstLayoutState = componentTree.getBackgroundLayoutState();
        assertThat(firstLayoutState).isNotNull();
        componentTree.setSizeSpec(SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), size);
        junit.framework.Assert.assertEquals(100, size.width, 0.0);
        junit.framework.Assert.assertEquals(100, size.height, 0.0);
        assertThat(componentTree.getBackgroundLayoutState()).isEqualTo(firstLayoutState);
    }

    @Test
    public void testSetCompatibleSizeSpecWithDifferentRoot() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        Size size = new Size();
        componentTree.setSizeSpec(SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST), size);
        junit.framework.Assert.assertEquals(100, size.width, 0.0);
        junit.framework.Assert.assertEquals(100, size.height, 0.0);
        LayoutState firstLayoutState = componentTree.getBackgroundLayoutState();
        assertThat(firstLayoutState).isNotNull();
        componentTree.setRootAndSizeSpec(TestDrawableComponent.create(mContext).build(), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), size);
        Assert.assertNotEquals(firstLayoutState, componentTree.getBackgroundLayoutState());
    }

    @Test
    public void testSetRootAndSizeSpecWithTreeProps() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        final Size size = new Size();
        final TreeProps treeProps = new TreeProps();
        treeProps.put(Object.class, "hello world");
        componentTree.setRootAndSizeSpec(TestDrawableComponent.create(mContext).build(), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), size, treeProps);
        final ComponentContext c = Whitebox.getInternalState(componentTree.getBackgroundLayoutState(), "mContext");
        assertThat(c.getTreeProps()).isSameAs(treeProps);
    }

    @Test
    public void testSetRootWithTreePropsThenMeasure() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setLithoView(new LithoView(mContext));
        componentTree.attach();
        final TreeProps treeProps = new TreeProps();
        treeProps.put(Object.class, "hello world");
        componentTree.setRootAndSizeSpecAsync(TestDrawableComponent.create(mContext).build(), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), treeProps);
        assertThat(componentTree.getBackgroundLayoutState()).isNull();
        componentTree.measure(SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), new int[2], true);
        final ComponentContext c = Whitebox.getInternalState(componentTree.getMainThreadLayoutState(), "mContext");
        assertThat(c.getTreeProps()).isNotNull();
        assertThat(c.getTreeProps().get(Object.class)).isEqualTo(treeProps.get(Object.class));
    }

    @Test
    public void testSetInput() {
        Component component = TestLayoutComponent.create(mContext).build();
        ComponentTree componentTree = ComponentTree.create(mContext, component).build();
        componentTree.setRoot(mComponent);
        creationCommonChecks(componentTree);
        Assert.assertNull(Whitebox.getInternalState(componentTree, "mMainThreadLayoutState"));
        Assert.assertNull(Whitebox.getInternalState(componentTree, "mBackgroundLayoutState"));
        componentTree.setSizeSpec(mWidthSpec, mHeightSpec);
        // Since this happens post creation, it's not in general safe to update the main thread layout
        // state synchronously, so the result should be in the background layout state
        postSizeSpecChecks(componentTree, "mBackgroundLayoutState");
    }

    @Test
    public void testRootWrapperComponent() {
        final Component component = TestLayoutComponent.create(mContext).build();
        final Component wrapperComponent = TestLayoutComponent.create(mContext).build();
        rootWrapperComponentFactory = new RootWrapperComponentFactory() {
            @Override
            public Component createWrapper(ComponentContext c, Component root) {
                return wrapperComponent;
            }
        };
        ComponentTree componentTree = ComponentTree.create(mContext, component).build();
        componentTree.setRootAndSizeSpec(mComponent, mWidthSpec, mHeightSpec);
        assertThat(componentTree.getRoot()).isEqualTo(wrapperComponent);
    }

    @Test
    public void testSetComponentFromView() {
        Component component1 = TestDrawableComponent.create(mContext).build();
        ComponentTree componentTree1 = ComponentTree.create(mContext, component1).build();
        Component component2 = TestDrawableComponent.create(mContext).build();
        ComponentTree componentTree2 = ComponentTree.create(mContext, component2).build();
        Assert.assertNull(ComponentTreeTest.getLithoView(componentTree1));
        Assert.assertNull(ComponentTreeTest.getLithoView(componentTree2));
        LithoView lithoView = new LithoView(mContext);
        lithoView.setComponentTree(componentTree1);
        Assert.assertNotNull(ComponentTreeTest.getLithoView(componentTree1));
        Assert.assertNull(ComponentTreeTest.getLithoView(componentTree2));
        lithoView.setComponentTree(componentTree2);
        Assert.assertNull(ComponentTreeTest.getLithoView(componentTree1));
        Assert.assertNotNull(ComponentTreeTest.getLithoView(componentTree2));
    }

    @Test
    public void testComponentTreeReleaseClearsView() {
        Component component = TestDrawableComponent.create(mContext).build();
        ComponentTree componentTree = ComponentTree.create(mContext, component).build();
        LithoView lithoView = new LithoView(mContext);
        lithoView.setComponentTree(componentTree);
        assertThat(componentTree).isEqualTo(lithoView.getComponentTree());
        componentTree.release();
        assertThat(lithoView.getComponentTree()).isNull();
    }

    @Test
    public void testSetTreeToTwoViewsBothAttached() {
        Component component = TestDrawableComponent.create(mContext).build();
        ComponentTree componentTree = ComponentTree.create(mContext, component).build();
        // Attach first view.
        LithoView lithoView1 = new LithoView(mContext);
        lithoView1.setComponentTree(componentTree);
        lithoView1.onAttachedToWindow();
        // Attach second view.
        LithoView lithoView2 = new LithoView(mContext);
        lithoView2.onAttachedToWindow();
        // Set the component that is already mounted on the first view, on the second attached view.
        // This should be ok.
        lithoView2.setComponentTree(componentTree);
    }

    @Test
    public void testSettingNewViewToTree() {
        Component component = TestDrawableComponent.create(mContext).build();
        ComponentTree componentTree = ComponentTree.create(mContext, component).build();
        // Attach first view.
        LithoView lithoView1 = new LithoView(mContext);
        lithoView1.setComponentTree(componentTree);
        assertThat(ComponentTreeTest.getLithoView(componentTree)).isEqualTo(lithoView1);
        assertThat(ComponentTreeTest.getComponentTree(lithoView1)).isEqualTo(componentTree);
        // Attach second view.
        LithoView lithoView2 = new LithoView(mContext);
        Assert.assertNull(ComponentTreeTest.getComponentTree(lithoView2));
        lithoView2.setComponentTree(componentTree);
        assertThat(ComponentTreeTest.getLithoView(componentTree)).isEqualTo(lithoView2);
        assertThat(ComponentTreeTest.getComponentTree(lithoView2)).isEqualTo(componentTree);
        Assert.assertNull(ComponentTreeTest.getComponentTree(lithoView1));
    }

    @Test
    public void testSetRootAsyncFollowedByMeasureDoesntComputeSyncLayout() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setLithoView(new LithoView(mContext));
        componentTree.measure(mWidthSpec, mHeightSpec, new int[2], false);
        componentTree.attach();
        Component newComponent = TestDrawableComponent.create(mContext).color(1234).build();
        componentTree.setRootAsync(newComponent);
        assertThat(componentTree.getRoot()).isEqualTo(newComponent);
        componentTree.measure(mWidthSpec, mHeightSpec, new int[2], false);
        assertThat(componentTree.getMainThreadLayoutState().isForComponentId(mComponent.getId())).isTrue();
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(componentTree.getMainThreadLayoutState().isForComponentId(newComponent.getId())).isTrue();
    }

    @Test
    public void testSetRootAsyncFollowedByNonCompatibleMeasureComputesSyncLayout() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setLithoView(new LithoView(mContext));
        componentTree.measure(mWidthSpec, mHeightSpec, new int[2], false);
        componentTree.attach();
        Component newComponent = TestDrawableComponent.create(mContext).color(1234).build();
        componentTree.setRootAsync(newComponent);
        componentTree.measure(mWidthSpec2, mHeightSpec2, new int[2], false);
        assertThat(componentTree.getRoot()).isEqualTo(newComponent);
        assertThat(componentTree.hasCompatibleLayout(mWidthSpec2, mHeightSpec2)).isTrue();
        assertThat(componentTree.getMainThreadLayoutState().isForComponentId(newComponent.getId())).isTrue();
        // Clear tasks
        mLayoutThreadShadowLooper.runToEndOfTasks();
    }

    @Test
    public void testMeasureWithIncompatibleSetRootAsyncBeforeStart() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setLithoView(new LithoView(mContext));
        int widthSpec1 = SizeSpec.makeSizeSpec(1000, EXACTLY);
        int heightSpec1 = SizeSpec.makeSizeSpec(1000, AT_MOST);
        int widthSpec2 = SizeSpec.makeSizeSpec(1000, EXACTLY);
        int heightSpec2 = SizeSpec.makeSizeSpec(0, UNSPECIFIED);
        componentTree.measure(widthSpec2, heightSpec2, new int[2], false);
        componentTree.attach();
        componentTree.measure(widthSpec1, heightSpec1, new int[2], false);
        Component newComponent = TestDrawableComponent.create(mContext).color(1234).build();
        componentTree.setRootAsync(newComponent);
        componentTree.measure(widthSpec2, heightSpec2, new int[2], false);
        // Since the layout thread hasn't started the async layout, we know it will capture the updated
        // size specs when it does run
        assertThat(componentTree.getRoot()).isEqualTo(newComponent);
        assertThat(componentTree.hasCompatibleLayout(widthSpec2, heightSpec2)).isTrue();
        assertThat(componentTree.getMainThreadLayoutState().isForComponentId(mComponent.getId())).isTrue();
        ComponentTreeTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                mLayoutThreadShadowLooper.runToEndOfTasks();
            }
        });
        ShadowLooper.runUiThreadTasks();
        // Once the async layout finishes, the main thread should have the updated layout.
        assertThat(componentTree.hasCompatibleLayout(widthSpec2, heightSpec2)).isTrue();
        assertThat(componentTree.getMainThreadLayoutState().isForComponentId(newComponent.getId())).isTrue();
    }

    @Test
    public void testMeasureWithIncompatibleSetRootAsyncThatFinishes() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setLithoView(new LithoView(mContext));
        int widthSpec1 = SizeSpec.makeSizeSpec(1000, EXACTLY);
        int heightSpec1 = SizeSpec.makeSizeSpec(1000, AT_MOST);
        int widthSpec2 = SizeSpec.makeSizeSpec(1000, EXACTLY);
        int heightSpec2 = SizeSpec.makeSizeSpec(0, UNSPECIFIED);
        componentTree.measure(widthSpec2, heightSpec2, new int[2], false);
        componentTree.attach();
        componentTree.measure(widthSpec1, heightSpec1, new int[2], false);
        Component newComponent = TestDrawableComponent.create(mContext).color(1234).build();
        componentTree.setRootAsync(newComponent);
        ComponentTreeTest.runOnBackgroundThreadSync(new Runnable() {
            @Override
            public void run() {
                // "Commit" layout (it will fail since it doesn't have compatible size specs)
                mLayoutThreadShadowLooper.runToEndOfTasks();
            }
        });
        componentTree.measure(widthSpec2, heightSpec2, new int[2], false);
        assertThat(componentTree.getRoot()).isEqualTo(newComponent);
        assertThat(componentTree.hasCompatibleLayout(widthSpec2, heightSpec2)).isTrue();
        assertThat(componentTree.getMainThreadLayoutState().isForComponentId(newComponent.getId())).isTrue().withFailMessage("The main thread should calculate a new layout synchronously because the async layout will not be used once it completes");
    }

    @Test
    public void testMeasureWithIncompatibleSetRootAsync() throws InterruptedException {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.setLithoView(new LithoView(mContext));
        int widthSpec1 = SizeSpec.makeSizeSpec(1000, EXACTLY);
        int heightSpec1 = SizeSpec.makeSizeSpec(1000, AT_MOST);
        int widthSpec2 = SizeSpec.makeSizeSpec(1000, EXACTLY);
        int heightSpec2 = SizeSpec.makeSizeSpec(0, UNSPECIFIED);
        componentTree.measure(widthSpec2, heightSpec2, new int[2], false);
        componentTree.attach();
        componentTree.measure(widthSpec1, heightSpec1, new int[2], false);
        final CountDownLatch unblockAsyncPrepare = new CountDownLatch(1);
        final CountDownLatch onAsyncPrepareStart = new CountDownLatch(1);
        TestDrawableComponent newComponent = TestDrawableComponent.create(mContext).color(1234).build();
        newComponent.setTestComponentListener(new TestDrawableComponent.TestComponentListener() {
            @Override
            public void onPrepare() {
                // We only want to block/wait for the component instance that is created async
                if (ThreadUtils.isMainThread()) {
                    return;
                }
                onAsyncPrepareStart.countDown();
                try {
                    if (!(unblockAsyncPrepare.await(5, TimeUnit.SECONDS))) {
                        throw new RuntimeException("Timed out waiting for prepare to unblock!");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        componentTree.setRootAsync(newComponent);
        final CountDownLatch asyncLayoutFinish = ComponentTreeTest.runOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
                mLayoutThreadShadowLooper.runToEndOfTasks();
            }
        });
        if (!(onAsyncPrepareStart.await(5, TimeUnit.SECONDS))) {
            throw new RuntimeException("Timeout!");
        }
        // At this point, the Layout thread is blocked in prepare (waiting for unblockAsyncPrepare) and
        // will have already captured the "bad" specs, but not completed its layout. We expect the main
        // thread to determine that this async layout will not be correct and that it needs to compute
        // one in measure
        componentTree.measure(widthSpec2, heightSpec2, new int[2], false);
        assertThat(componentTree.getRoot()).isEqualTo(newComponent);
        assertThat(componentTree.hasCompatibleLayout(widthSpec2, heightSpec2)).isTrue();
        assertThat(componentTree.getMainThreadLayoutState().isForComponentId(newComponent.getId())).isTrue().withFailMessage("The main thread should calculate a new layout synchronously because the async layout will not have compatible size specs");
        // Finally, let the async layout finish and make sure it doesn't replace the layout from measure
        unblockAsyncPrepare.countDown();
        if (!(asyncLayoutFinish.await(5, TimeUnit.SECONDS))) {
            throw new RuntimeException("Timeout!");
        }
        assertThat(componentTree.getRoot()).isEqualTo(newComponent);
        assertThat(componentTree.hasCompatibleLayout(widthSpec2, heightSpec2)).isTrue();
        assertThat(componentTree.getMainThreadLayoutState().isForComponentId(newComponent.getId())).isTrue();
    }

    @Test
    public void testSetRootAfterRelease() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        componentTree.release();
        // Verify we don't crash
        componentTree.setRoot(TestDrawableComponent.create(mContext).build());
    }

    @Test
    public void testCachedValues() {
        ComponentTree componentTree = ComponentTree.create(mContext, mComponent).build();
        assertThat(componentTree.getCachedValue("key1")).isNull();
        componentTree.putCachedValue("key1", "value1");
        assertThat(componentTree.getCachedValue("key1")).isEqualTo("value1");
        assertThat(componentTree.getCachedValue("key2")).isNull();
    }

    @Test
    public void testLayoutStateFutureMainWaitingOnBg() {
        ComponentTreeTest.MyTestComponent root1 = new ComponentTreeTest.MyTestComponent("MyTestComponent");
        root1.testId = 1;
        ThreadPoolLayoutHandler handler = new ThreadPoolLayoutHandler(new LayoutThreadPoolConfigurationImpl(1, 1, 5));
        ComponentTree componentTree = ComponentTree.create(mContext, root1).layoutThreadHandler(handler).useSharedLayoutStateFuture(true).build();
        componentTree.setLithoView(new LithoView(mContext));
        componentTree.measure(mWidthSpec, mHeightSpec, new int[2], false);
        componentTree.attach();
        final CountDownLatch unlockWaitingOnCreateLayout = new CountDownLatch(1);
        final CountDownLatch lockOnCreateLayoutFinish = new CountDownLatch(1);
        ComponentTreeTest.MyTestComponent root2 = new ComponentTreeTest.MyTestComponent("MyTestComponent");
        root2.testId = 2;
        root2.unlockWaitingOnCreateLayout = unlockWaitingOnCreateLayout;
        root2.lockOnCreateLayoutFinish = lockOnCreateLayoutFinish;
        ComponentTreeTest.MyTestComponent root3 = new ComponentTreeTest.MyTestComponent("MyTestComponent");
        root3.testId = 2;
        componentTree.setRootAsync(root2);
        // Wait for first thread to get into onCreateLayout
        try {
            unlockWaitingOnCreateLayout.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, componentTree.getLayoutStateFutures().size());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                componentTree.calculateLayoutState(mContext, root3, mWidthSpec, mHeightSpec, true, null, null, TEST, null);
                // At this point, the current thread is unblocked after waiting for the first to
                // finish layout.
                assertFalse(root3.hasRunLayout);
                assertTrue(root2.hasRunLayout);
            }
        });
        // Schedule second thread to start
        thread.start();
        // Unblock the first thread to continue through onCreateLayout. The second thread will only
        // unblock once the first thread's onCreateLayout finishes
        lockOnCreateLayoutFinish.countDown();
    }

    @Test
    public void testRecalculateDifferentRoots() {
        ComponentTreeTest.MyTestComponent root1 = new ComponentTreeTest.MyTestComponent("MyTestComponent");
        root1.testId = 1;
        ThreadPoolLayoutHandler handler = new ThreadPoolLayoutHandler(new LayoutThreadPoolConfigurationImpl(1, 1, 5));
        ComponentTree componentTree = ComponentTree.create(mContext, root1).layoutThreadHandler(handler).useSharedLayoutStateFuture(true).build();
        componentTree.setLithoView(new LithoView(mContext));
        componentTree.measure(mWidthSpec, mHeightSpec, new int[2], false);
        componentTree.attach();
        final CountDownLatch unlockWaitingOnCreateLayout = new CountDownLatch(1);
        final CountDownLatch lockOnCreateLayoutFinish = new CountDownLatch(1);
        ComponentTreeTest.MyTestComponent root2 = new ComponentTreeTest.MyTestComponent("MyTestComponent");
        root2.testId = 2;
        root2.unlockWaitingOnCreateLayout = unlockWaitingOnCreateLayout;
        root2.lockOnCreateLayoutFinish = lockOnCreateLayoutFinish;
        ComponentTreeTest.MyTestComponent root3 = new ComponentTreeTest.MyTestComponent("MyTestComponent");
        root3.testId = 2;
        componentTree.setRootAsync(root2);
        // Wait for first thread to get into onCreateLayout
        try {
            unlockWaitingOnCreateLayout.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, componentTree.getLayoutStateFutures().size());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                componentTree.calculateLayoutState(mContext, root3, mWidthSpec, mHeightSpec, true, null, null, TEST, null);
                // At this point, the current thread is unblocked after waiting for the first to
                // finish layout.
                assertTrue(root3.hasRunLayout);
                assertTrue(root2.hasRunLayout);
            }
        });
        // Schedule second thread to start
        thread.start();
        // Unblock the first thread to continue through onCreateLayout. The second thread will only
        // unblock once the first thread's onCreateLayout finishes
        lockOnCreateLayoutFinish.countDown();
    }

    @Test
    public void testAttachFromListenerDoesntCrash() {
        final Component component = TestLayoutComponent.create(mContext).build();
        final LithoView lithoView = new LithoView(mContext);
        final ComponentTree componentTree = ComponentTree.create(mContext, component).build();
        lithoView.setComponentTree(componentTree);
        componentTree.setNewLayoutStateReadyListener(new ComponentTree.NewLayoutStateReadyListener() {
            @Override
            public void onNewLayoutStateReady(ComponentTree componentTree) {
                lithoView.onAttachedToWindow();
            }
        });
        componentTree.setRootAndSizeSpec(mComponent, mWidthSpec, mHeightSpec);
    }

    @Test
    public void testDetachFromListenerDoesntCrash() {
        final Component component = TestLayoutComponent.create(mContext).build();
        final LithoView lithoView = new LithoView(mContext);
        final ComponentTree componentTree = ComponentTree.create(mContext, component).build();
        lithoView.setComponentTree(componentTree);
        lithoView.onAttachedToWindow();
        componentTree.setNewLayoutStateReadyListener(new ComponentTree.NewLayoutStateReadyListener() {
            @Override
            public void onNewLayoutStateReady(ComponentTree componentTree) {
                lithoView.onDetachedFromWindow();
                componentTree.clearLithoView();
            }
        });
        componentTree.setRootAndSizeSpec(mComponent, mWidthSpec, mHeightSpec);
    }

    class MyTestComponent extends Component {
        CountDownLatch unlockWaitingOnCreateLayout;

        CountDownLatch lockOnCreateLayoutFinish;

        int testId;

        boolean hasRunLayout;

        protected MyTestComponent(String simpleName) {
            super(simpleName);
        }

        @Override
        protected Component onCreateLayout(ComponentContext c) {
            if ((unlockWaitingOnCreateLayout) != null) {
                unlockWaitingOnCreateLayout.countDown();
            }
            if ((lockOnCreateLayoutFinish) != null) {
                try {
                    lockOnCreateLayoutFinish.await(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            hasRunLayout = true;
            return Column.create(c).build();
        }

        @Override
        protected int getId() {
            return testId;
        }
    }
}

