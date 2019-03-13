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


import YogaEdge.TOP;
import android.content.Context;
import android.graphics.Rect;
import android.view.ViewGroup;
import com.facebook.litho.testing.TestComponent;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.TestViewComponent;
import com.facebook.litho.testing.ViewGroupWithLithoViewChildren;
import com.facebook.litho.testing.helper.ComponentTestHelper;
import com.facebook.litho.testing.logging.TestComponentsLogger;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@RunWith(ComponentsTestRunner.class)
public class MountStateIncrementalMountTest {
    private ComponentContext mContext;

    private TestComponentsLogger mComponentsLogger;

    /**
     * Tests incremental mount behaviour of a vertical stack of components with a View mount type.
     */
    @Test
    public void testIncrementalMountVerticalViewStackScrollUp() {
        final TestComponent child1 = TestViewComponent.create(mContext).build();
        final TestComponent child2 = TestViewComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10)).build();
            }
        });
        verifyLoggingAndResetLogger(2, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, (-10), 10, (-5)), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 2);
        lithoView.getComponentTree().mountComponent(new Rect(0, 0, 10, 5), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(1, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, 5, 10, 15), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isTrue();
        verifyLoggingAndResetLogger(1, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, 15, 10, 25), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isTrue();
        verifyLoggingAndResetLogger(0, 1);
        lithoView.getComponentTree().mountComponent(new Rect(0, 20, 10, 30), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 1);
    }

    @Test
    public void testIncrementalMountVerticalViewStackScrollDown() {
        final TestComponent child1 = TestViewComponent.create(mContext).build();
        final TestComponent child2 = TestViewComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10)).build();
            }
        });
        verifyLoggingAndResetLogger(2, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, 20, 10, 30), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 2);
        lithoView.getComponentTree().mountComponent(new Rect(0, 15, 10, 25), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isTrue();
        verifyLoggingAndResetLogger(1, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, 5, 10, 15), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isTrue();
        verifyLoggingAndResetLogger(1, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, 0, 10, 10), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 1);
        lithoView.getComponentTree().mountComponent(new Rect(0, (-10), 10, (-5)), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 1);
    }

    /**
     * Tests incremental mount behaviour of a horizontal stack of components with a View mount type.
     */
    @Test
    public void testIncrementalMountHorizontalViewStack() {
        final TestComponent child1 = TestViewComponent.create(mContext).build();
        final TestComponent child2 = TestViewComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Row.create(c).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10)).build();
            }
        });
        verifyLoggingAndResetLogger(2, 0);
        lithoView.getComponentTree().mountComponent(new Rect((-10), 0, (-5), 10), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 2);
        lithoView.getComponentTree().mountComponent(new Rect(0, 0, 5, 10), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(1, 0);
        lithoView.getComponentTree().mountComponent(new Rect(5, 0, 15, 10), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isTrue();
        verifyLoggingAndResetLogger(1, 0);
        lithoView.getComponentTree().mountComponent(new Rect(15, 0, 25, 10), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isTrue();
        verifyLoggingAndResetLogger(0, 1);
        lithoView.getComponentTree().mountComponent(new Rect(20, 0, 30, 10), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 1);
    }

    /**
     * Tests incremental mount behaviour of a vertical stack of components with a Drawable mount type.
     */
    @Test
    public void testIncrementalMountVerticalDrawableStack() {
        final TestComponent child1 = TestDrawableComponent.create(mContext).build();
        final TestComponent child2 = TestDrawableComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10)).build();
            }
        });
        verifyLoggingAndResetLogger(2, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, (-10), 10, (-5)), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 2);
        lithoView.getComponentTree().mountComponent(new Rect(0, 0, 10, 5), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(1, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, 5, 10, 15), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isTrue();
        verifyLoggingAndResetLogger(1, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, 15, 10, 25), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isTrue();
        verifyLoggingAndResetLogger(0, 1);
        lithoView.getComponentTree().mountComponent(new Rect(0, 20, 10, 30), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 1);
    }

    /**
     * Tests incremental mount behaviour of a view mount item in a nested hierarchy.
     */
    @Test
    public void testIncrementalMountNestedView() {
        final TestComponent child = TestViewComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).wrapInView().paddingPx(ALL, 20).child(Wrapper.create(c).delegate(child).widthPx(10).heightPx(10)).child(TestDrawableComponent.create(c)).build();
            }
        });
        verifyLoggingAndResetLogger(2, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, 0, 50, 20), true);
        assertThat(child.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 2);
        lithoView.getComponentTree().mountComponent(new Rect(0, 0, 50, 40), true);
        assertThat(child.isMounted()).isTrue();
        verifyLoggingAndResetLogger(2, 0);
        lithoView.getComponentTree().mountComponent(new Rect(30, 0, 50, 40), true);
        assertThat(child.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 1);
    }

    /**
     * Verify that we can cope with a negative padding on a component that is wrapped in a view
     * (since the bounds of the component will be larger than the bounds of the view).
     */
    @Test
    public void testIncrementalMountVerticalDrawableStackNegativeMargin() {
        final TestComponent child1 = TestDrawableComponent.create(mContext).build();
        final LithoView lithoView = ComponentTestHelper.mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10).clickHandler(c.newEventHandler(1)).marginDip(TOP, (-10))).build();
            }
        });
        verifyLoggingAndResetLogger(2, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, (-10), 10, (-5)), true);
        verifyLoggingAndResetLogger(0, 0);
    }

    /**
     * Tests incremental mount behaviour of overlapping view mount items.
     */
    @Test
    public void testIncrementalMountOverlappingView() {
        final TestComponent child1 = TestViewComponent.create(mContext).build();
        final TestComponent child2 = TestViewComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Wrapper.create(c).delegate(child1).positionType(ABSOLUTE).positionPx(TOP, 0).positionPx(LEFT, 0).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).positionType(ABSOLUTE).positionPx(TOP, 5).positionPx(LEFT, 5).widthPx(10).heightPx(10)).child(TestDrawableComponent.create(c)).build();
            }
        });
        verifyLoggingAndResetLogger(3, 0);
        lithoView.getComponentTree().mountComponent(new Rect(0, 0, 5, 5), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 1);
        lithoView.getComponentTree().mountComponent(new Rect(5, 5, 10, 10), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isTrue();
        verifyLoggingAndResetLogger(1, 0);
        lithoView.getComponentTree().mountComponent(new Rect(10, 10, 15, 15), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isTrue();
        verifyLoggingAndResetLogger(0, 1);
        lithoView.getComponentTree().mountComponent(new Rect(15, 15, 20, 20), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isFalse();
        verifyLoggingAndResetLogger(0, 1);
    }

    @Test
    public void testChildViewGroupIncrementallyMounted() {
        final ViewGroup mountedView = Mockito.mock(ViewGroup.class);
        Mockito.when(mountedView.getChildCount()).thenReturn(3);
        final LithoView childView1 = MountStateIncrementalMountTest.getMockLithoViewWithBounds(new Rect(5, 10, 20, 30));
        Mockito.when(mountedView.getChildAt(0)).thenReturn(childView1);
        final LithoView childView2 = MountStateIncrementalMountTest.getMockLithoViewWithBounds(new Rect(10, 10, 50, 60));
        Mockito.when(mountedView.getChildAt(1)).thenReturn(childView2);
        final LithoView childView3 = MountStateIncrementalMountTest.getMockLithoViewWithBounds(new Rect(30, 35, 50, 60));
        Mockito.when(mountedView.getChildAt(2)).thenReturn(childView3);
        final LithoView lithoView = ComponentTestHelper.mountComponent(TestViewComponent.create(mContext).testView(mountedView));
        lithoView.getComponentTree().mountComponent(new Rect(15, 15, 40, 40), true);
        Mockito.verify(childView1).performIncrementalMount();
        Mockito.verify(childView2).performIncrementalMount();
        Mockito.verify(childView3).performIncrementalMount();
    }

    @Test
    public void testChildViewGroupAllIncrementallyMountedNotProcessVisibilityOutputs() {
        final ViewGroup mountedView = Mockito.mock(ViewGroup.class);
        Mockito.when(mountedView.getLeft()).thenReturn(0);
        Mockito.when(mountedView.getTop()).thenReturn(0);
        Mockito.when(mountedView.getRight()).thenReturn(100);
        Mockito.when(mountedView.getBottom()).thenReturn(100);
        Mockito.when(mountedView.getChildCount()).thenReturn(3);
        final LithoView childView1 = MountStateIncrementalMountTest.getMockLithoViewWithBounds(new Rect(5, 10, 20, 30));
        Mockito.when(childView1.getTranslationX()).thenReturn(5.0F);
        Mockito.when(childView1.getTranslationY()).thenReturn((-10.0F));
        Mockito.when(mountedView.getChildAt(0)).thenReturn(childView1);
        final LithoView childView2 = MountStateIncrementalMountTest.getMockLithoViewWithBounds(new Rect(10, 10, 50, 60));
        Mockito.when(mountedView.getChildAt(1)).thenReturn(childView2);
        final LithoView childView3 = MountStateIncrementalMountTest.getMockLithoViewWithBounds(new Rect(30, 35, 50, 60));
        Mockito.when(mountedView.getChildAt(2)).thenReturn(childView3);
        final LithoView lithoView = ComponentTestHelper.mountComponent(TestViewComponent.create(mContext).testView(mountedView));
        // Can't verify directly as the object will have changed by the time we get the chance to
        // verify it.
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Rect rect = ((Rect) (invocation.getArguments()[0]));
                if (!(rect.equals(new Rect(0, 0, 15, 20)))) {
                    Assert.fail();
                }
                return null;
            }
        }).when(childView1).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(true));
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Rect rect = ((Rect) (invocation.getArguments()[0]));
                if (!(rect.equals(new Rect(0, 0, 40, 50)))) {
                    Assert.fail();
                }
                return null;
            }
        }).when(childView2).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(true));
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Rect rect = ((Rect) (invocation.getArguments()[0]));
                if (!(rect.equals(new Rect(0, 0, 20, 25)))) {
                    Assert.fail();
                }
                return null;
            }
        }).when(childView3).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(true));
        lithoView.getComponentTree().mountComponent(new Rect(0, 0, 100, 100), false);
        Mockito.verify(childView1).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(false));
        Mockito.verify(childView2).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(false));
        Mockito.verify(childView3).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(false));
    }

    /**
     * Tests incremental mount behaviour of a vertical stack of components with a View mount type.
     */
    @Test
    public void testIncrementalMountDoesNotCauseMultipleUpdates() {
        final TestComponent child1 = TestViewComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).build();
            }
        });
        lithoView.getComponentTree().mountComponent(new Rect(0, (-10), 10, (-5)), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child1.wasOnUnbindCalled()).isTrue();
        assertThat(child1.wasOnUnmountCalled()).isTrue();
        lithoView.getComponentTree().mountComponent(new Rect(0, 0, 10, 5), true);
        assertThat(child1.isMounted()).isTrue();
        child1.resetInteractions();
        lithoView.getComponentTree().mountComponent(new Rect(0, 5, 10, 15), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child1.wasOnBindCalled()).isFalse();
        assertThat(child1.wasOnMountCalled()).isFalse();
        assertThat(child1.wasOnUnbindCalled()).isFalse();
        assertThat(child1.wasOnUnmountCalled()).isFalse();
    }

    /**
     * Tests incremental mount behaviour of a vertical stack of components with a Drawable mount type
     * after unmountAllItems was called.
     */
    @Test
    public void testIncrementalMountAfterUnmountAllItemsCall() {
        final TestComponent child1 = TestDrawableComponent.create(mContext).build();
        final TestComponent child2 = TestDrawableComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10)).build();
            }
        });
        lithoView.getComponentTree().mountComponent(new Rect(0, (-10), 10, (-5)), true);
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isFalse();
        lithoView.getComponentTree().mountComponent(new Rect(0, 0, 10, 5), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isFalse();
        lithoView.getComponentTree().mountComponent(new Rect(0, 5, 10, 15), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isTrue();
        lithoView.unmountAllItems();
        assertThat(child1.isMounted()).isFalse();
        assertThat(child2.isMounted()).isFalse();
        lithoView.getComponentTree().mountComponent(new Rect(0, 5, 10, 15), true);
        assertThat(child1.isMounted()).isTrue();
        assertThat(child2.isMounted()).isTrue();
    }

    /**
     * Tests incremental mount behaviour of a nested Litho View. We want to ensure that when a child
     * view is first mounted due to a layout pass it does not also have performIncrementalMount called
     * on it.
     */
    @Test
    public void testIncrementalMountAfterLithoViewIsMounted() {
        final LithoView lithoView = Mockito.mock(LithoView.class);
        Mockito.when(lithoView.isIncrementalMountEnabled()).thenReturn(true);
        final ViewGroupWithLithoViewChildren viewGroup = new ViewGroupWithLithoViewChildren(mContext.getAndroidContext());
        viewGroup.addView(lithoView);
        final LithoView lithoViewParent = ComponentTestHelper.mountComponent(TestViewComponent.create(mContext, true, true, true, true).testView(viewGroup), true);
        // Mount views with visible rect
        lithoViewParent.getComponentTree().mountComponent(new Rect(0, 0, 100, 1000), false);
        Mockito.verify(lithoView).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(false));
        Mockito.reset(lithoView);
        Mockito.when(lithoView.isIncrementalMountEnabled()).thenReturn(true);
        // Unmount views with visible rect outside
        lithoViewParent.getComponentTree().mountComponent(new Rect(0, (-10), 100, (-5)), false);
        Mockito.verify(lithoView, Mockito.never()).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(false));
        Mockito.reset(lithoView);
        Mockito.when(lithoView.isIncrementalMountEnabled()).thenReturn(true);
        // Mount again with visible rect
        lithoViewParent.getComponentTree().mountComponent(new Rect(0, 0, 100, 1000), false);
        // Now LithoView performIncrementalMount should not be called as the LithoView is mounted when
        // it is laid out and therefore doesn't need mounting again in the same frame
        Mockito.verify(lithoView, Mockito.never()).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(false));
    }

    private static class TestLithoView extends LithoView {
        private final Rect mPreviousIncrementalMountBounds = new Rect();

        public TestLithoView(Context context) {
            super(context);
        }

        @Override
        public void performIncrementalMount(Rect visibleRect, boolean processVisibilityOutputs) {
            System.out.println("performIncMount on TestLithoView");
            mPreviousIncrementalMountBounds.set(visibleRect);
        }

        private Rect getPreviousIncrementalMountBounds() {
            return mPreviousIncrementalMountBounds;
        }

        @Override
        public boolean isIncrementalMountEnabled() {
            return true;
        }
    }
}

