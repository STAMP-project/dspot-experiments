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


import Color.BLACK;
import Color.WHITE;
import LayoutOutput.STATE_DIRTY;
import LayoutOutput.STATE_UNKNOWN;
import LayoutOutput.STATE_UPDATED;
import LayoutState.CalculateLayoutSource.TEST;
import SizeSpec.EXACTLY;
import android.graphics.Rect;
import androidx.collection.SparseArrayCompat;
import com.facebook.litho.drawable.ComparableDrawable;
import com.facebook.litho.testing.TestComponent;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.TestSizeDependentComponent;
import com.facebook.litho.testing.Whitebox;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static SizeSpec.EXACTLY;


@RunWith(ComponentsTestRunner.class)
public class TreeDiffingTest {
    private static ComparableDrawable sRedDrawable;

    private static ComparableDrawable sBlackDrawable;

    private static ComparableDrawable sTransparentDrawable;

    private int mUnspecifiedSpec;

    private ComponentContext mContext;

    @Test
    public void testDiffTreeDisabled() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c))).build();
            }
        };
        LayoutState layoutState = TreeDiffingTest.calculateLayoutState(mContext, component, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY));
        // Check diff tree is null.
        assertThat(layoutState.getDiffTree()).isNull();
    }

    @Test
    public void testDiffTreeEnabled() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c))).build();
            }
        };
        LayoutState layoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, component, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY), null);
        // Check diff tree is not null and consistent.
        DiffNode node = layoutState.getDiffTree();
        assertThat(node).isNotNull();
        assertThat(4).isEqualTo(TreeDiffingTest.countNodes(node));
    }

    @Test
    public void testCachedMeasureFunction() {
        final Component component = TestDrawableComponent.create(mContext).build();
        InternalNode node = createInternalNodeForMeasurableComponent(component);
        DiffNode diffNode = new DiffNode();
        diffNode.setLastHeightSpec(mUnspecifiedSpec);
        diffNode.setLastWidthSpec(mUnspecifiedSpec);
        diffNode.setLastMeasuredWidth(10);
        diffNode.setLastMeasuredHeight(5);
        diffNode.setComponent(component);
        node.setCachedMeasuresValid(true);
        node.setDiffNode(diffNode);
        long output = measureInternalNode(node, UNDEFINED, UNDEFINED);
        assertThat(((getHeight(output)) == ((int) (diffNode.getLastMeasuredHeight())))).isTrue();
        assertThat(((getWidth(output)) == ((int) (diffNode.getLastMeasuredWidth())))).isTrue();
    }

    @Test
    public void tesLastConstraints() {
        final Component component = TestDrawableComponent.create(mContext).build();
        InternalNode node = createInternalNodeForMeasurableComponent(component);
        DiffNode diffNode = new DiffNode();
        diffNode.setLastWidthSpec(SizeSpec.makeSizeSpec(10, EXACTLY));
        diffNode.setLastHeightSpec(SizeSpec.makeSizeSpec(5, EXACTLY));
        diffNode.setLastMeasuredWidth(10.0F);
        diffNode.setLastMeasuredHeight(5.0F);
        diffNode.setComponent(component);
        node.setCachedMeasuresValid(true);
        node.setDiffNode(diffNode);
        long output = measureInternalNode(node, 10.0F, 5.0F);
        assertThat(((getHeight(output)) == ((int) (diffNode.getLastMeasuredHeight())))).isTrue();
        assertThat(((getWidth(output)) == ((int) (diffNode.getLastMeasuredWidth())))).isTrue();
        int lastWidthSpec = node.getLastWidthSpec();
        int lastHeightSpec = node.getLastHeightSpec();
        assertThat(((SizeSpec.getMode(lastWidthSpec)) == (EXACTLY))).isTrue();
        assertThat(((SizeSpec.getMode(lastHeightSpec)) == (EXACTLY))).isTrue();
        assertThat(((SizeSpec.getSize(lastWidthSpec)) == 10)).isTrue();
        assertThat(((SizeSpec.getSize(lastHeightSpec)) == 5)).isTrue();
    }

    @Test
    public void measureAndCreateDiffNode() {
        final Component component = TestDrawableComponent.create(mContext).build();
        InternalNode node = createInternalNodeForMeasurableComponent(component);
        long output = measureInternalNode(node, UNDEFINED, UNDEFINED);
        node.setCachedMeasuresValid(false);
        DiffNode diffNode = LayoutState.createDiffNode(node, null);
        assertThat(((getHeight(output)) == ((int) (diffNode.getLastMeasuredHeight())))).isTrue();
        assertThat(((getWidth(output)) == ((int) (diffNode.getLastMeasuredWidth())))).isTrue();
    }

    @Test
    public void testCachedMeasures() {
        final Component component1 = new TreeDiffingTest.TestLayoutSpec(false);
        final Component component2 = new TreeDiffingTest.TestLayoutSpec(false);
        LayoutState prevLayoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, component1, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY), null);
        // Check diff tree is consistent.
        DiffNode node = prevLayoutState.getDiffTree();
        InternalNode layoutTreeRoot = LayoutState.createTree(component2, mContext);
        LayoutState.applyDiffNodeToUnchangedNodes(layoutTreeRoot, node);
        checkAllComponentsHaveMeasureCache(layoutTreeRoot);
    }

    @Test
    public void testPartiallyCachedMeasures() {
        final Component component1 = new TreeDiffingTest.TestLayoutSpec(false);
        final Component component2 = new TreeDiffingTest.TestLayoutSpec(true);
        LayoutState prevLayoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, component1, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY), null);
        // Check diff tree is consistent.
        DiffNode node = prevLayoutState.getDiffTree();
        InternalNode layoutTreeRoot = LayoutState.createTree(component2, mContext);
        LayoutState.applyDiffNodeToUnchangedNodes(layoutTreeRoot, node);
        InternalNode child_1 = ((InternalNode) (layoutTreeRoot.getChildAt(0)));
        TreeDiffingTest.assertCachedMeasurementsDefined(child_1);
        InternalNode child_2 = ((InternalNode) (layoutTreeRoot.getChildAt(1)));
        assertCachedMeasurementsNotDefined(child_2);
        InternalNode child_3 = ((InternalNode) (child_2.getChildAt(0)));
        TreeDiffingTest.assertCachedMeasurementsDefined(child_3);
        InternalNode child_4 = ((InternalNode) (layoutTreeRoot.getChildAt(2)));
        assertCachedMeasurementsNotDefined(child_4);
    }

    @Test
    public void testLayoutOutputReuse() {
        final Component component1 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c))).build();
            }
        };
        final Component component2 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c))).build();
            }
        };
        LayoutState prevLayoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, component1, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY), null);
        LayoutState layoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, component2, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY), prevLayoutState);
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(prevLayoutState.getMountableOutputCount());
        for (int i = 0, count = prevLayoutState.getMountableOutputCount(); i < count; i++) {
            assertThat(layoutState.getMountableOutputAt(i).getId()).isEqualTo(prevLayoutState.getMountableOutputAt(i).getId());
        }
    }

    @Test
    public void testLayoutOutputPartialReuse() {
        final Component component1 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c))).build();
            }
        };
        final Component component2 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c))).child(TestDrawableComponent.create(c)).build();
            }
        };
        LayoutState prevLayoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, component1, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY), null);
        LayoutState layoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, component2, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY), prevLayoutState);
        Assert.assertNotEquals(prevLayoutState.getMountableOutputCount(), layoutState.getMountableOutputCount());
        for (int i = 0, count = prevLayoutState.getMountableOutputCount(); i < count; i++) {
            assertThat(layoutState.getMountableOutputAt(i).getId()).isEqualTo(prevLayoutState.getMountableOutputAt(i).getId());
        }
    }

    @Test
    public void testComponentHostMoveItem() {
        ComponentHost hostHolder = new ComponentHost(mContext);
        MountItem mountItem = Mockito.mock(MountItem.class);
        MountItem mountItem1 = Mockito.mock(MountItem.class);
        MountItem mountItem2 = Mockito.mock(MountItem.class);
        hostHolder.mount(0, mountItem, new Rect());
        hostHolder.mount(1, mountItem1, new Rect());
        hostHolder.mount(2, mountItem2, new Rect());
        assertThat(mountItem).isEqualTo(hostHolder.getMountItemAt(0));
        assertThat(mountItem1).isEqualTo(hostHolder.getMountItemAt(1));
        assertThat(mountItem2).isEqualTo(hostHolder.getMountItemAt(2));
        hostHolder.moveItem(mountItem, 0, 2);
        hostHolder.moveItem(mountItem2, 2, 0);
        assertThat(mountItem2).isEqualTo(hostHolder.getMountItemAt(0));
        assertThat(mountItem1).isEqualTo(hostHolder.getMountItemAt(1));
        assertThat(mountItem).isEqualTo(hostHolder.getMountItemAt(2));
    }

    @Test
    public void testComponentHostMoveItemPartial() {
        ComponentHost hostHolder = new ComponentHost(mContext);
        MountItem mountItem = Mockito.mock(MountItem.class);
        MountItem mountItem1 = Mockito.mock(MountItem.class);
        MountItem mountItem2 = Mockito.mock(MountItem.class);
        hostHolder.mount(0, mountItem, new Rect());
        hostHolder.mount(1, mountItem1, new Rect());
        hostHolder.mount(2, mountItem2, new Rect());
        assertThat(mountItem).isEqualTo(hostHolder.getMountItemAt(0));
        assertThat(mountItem1).isEqualTo(hostHolder.getMountItemAt(1));
        assertThat(mountItem2).isEqualTo(hostHolder.getMountItemAt(2));
        hostHolder.moveItem(mountItem2, 2, 0);
        assertThat(mountItem2).isEqualTo(hostHolder.getMountItemAt(0));
        assertThat(mountItem1).isEqualTo(hostHolder.getMountItemAt(1));
        assertThat(1).isEqualTo(((SparseArrayCompat<MountItem>) (Whitebox.getInternalState(hostHolder, "mScrapMountItemsArray"))).size());
        hostHolder.unmount(0, mountItem);
        assertThat(2).isEqualTo(((SparseArrayCompat<MountItem>) (Whitebox.getInternalState(hostHolder, "mMountItems"))).size());
        assertThat(((Object) (Whitebox.getInternalState(hostHolder, "mScrapMountItemsArray")))).isNull();
    }

    @Test
    public void testLayoutOutputUpdateState() {
        final Component firstComponent = TestDrawableComponent.create(mContext).color(BLACK).build();
        final Component secondComponent = TestDrawableComponent.create(mContext).color(BLACK).build();
        final Component thirdComponent = TestDrawableComponent.create(mContext).color(WHITE).build();
        ComponentTree componentTree = ComponentTree.create(mContext, firstComponent).build();
        LayoutState state = componentTree.calculateLayoutState(mContext, firstComponent, SizeSpec.makeSizeSpec(10, EXACTLY), SizeSpec.makeSizeSpec(10, EXACTLY), true, null, null, TEST, null);
        TreeDiffingTest.assertOutputsState(state, STATE_UNKNOWN);
        LayoutState secondState = componentTree.calculateLayoutState(mContext, secondComponent, SizeSpec.makeSizeSpec(10, EXACTLY), SizeSpec.makeSizeSpec(10, EXACTLY), true, state, null, TEST, null);
        TreeDiffingTest.assertOutputsState(secondState, STATE_UPDATED);
        LayoutState thirdState = componentTree.calculateLayoutState(mContext, thirdComponent, SizeSpec.makeSizeSpec(10, EXACTLY), SizeSpec.makeSizeSpec(10, EXACTLY), true, secondState, null, TEST, null);
        TreeDiffingTest.assertOutputsState(thirdState, STATE_DIRTY);
    }

    @Test
    public void testLayoutOutputUpdateStateWithBackground() {
        final Component component1 = new TreeDiffingTest.TestLayoutSpecBgState(false);
        final Component component2 = new TreeDiffingTest.TestLayoutSpecBgState(false);
        final Component component3 = new TreeDiffingTest.TestLayoutSpecBgState(true);
        ComponentTree componentTree = ComponentTree.create(mContext, component1).build();
        LayoutState state = componentTree.calculateLayoutState(mContext, component1, SizeSpec.makeSizeSpec(10, EXACTLY), SizeSpec.makeSizeSpec(10, EXACTLY), true, null, null, TEST, null);
        TreeDiffingTest.assertOutputsState(state, LayoutOutput.STATE_UNKNOWN);
        LayoutState secondState = componentTree.calculateLayoutState(mContext, component2, SizeSpec.makeSizeSpec(10, EXACTLY), SizeSpec.makeSizeSpec(10, EXACTLY), true, state, null, TEST, null);
        assertThat(5).isEqualTo(secondState.getMountableOutputCount());
        TreeDiffingTest.assertOutputsState(secondState, LayoutOutput.STATE_UPDATED);
        LayoutState thirdState = componentTree.calculateLayoutState(mContext, component3, SizeSpec.makeSizeSpec(10, EXACTLY), SizeSpec.makeSizeSpec(10, EXACTLY), true, secondState, null, TEST, null);
        assertThat(5).isEqualTo(thirdState.getMountableOutputCount());
        assertThat(thirdState.getMountableOutputAt(1).getUpdateState()).isEqualTo(LayoutOutput.STATE_DIRTY);
        assertThat(thirdState.getMountableOutputAt(2).getUpdateState()).isEqualTo(LayoutOutput.STATE_UPDATED);
        assertThat(thirdState.getMountableOutputAt(3).getUpdateState()).isEqualTo(LayoutOutput.STATE_UPDATED);
        assertThat(thirdState.getMountableOutputAt(4).getUpdateState()).isEqualTo(LayoutOutput.STATE_UPDATED);
    }

    // This test covers the same case with the foreground since the code path is the same!
    @Test
    public void testLayoutOutputUpdateStateWithBackgroundInWithLayout() {
        final Component component1 = new TreeDiffingTest.TestLayoutSpecInnerState(false);
        final Component component2 = new TreeDiffingTest.TestLayoutSpecInnerState(false);
        final Component component3 = new TreeDiffingTest.TestLayoutSpecInnerState(true);
        ComponentTree componentTree = ComponentTree.create(mContext, component1).build();
        LayoutState state = componentTree.calculateLayoutState(mContext, component1, SizeSpec.makeSizeSpec(10, EXACTLY), SizeSpec.makeSizeSpec(10, EXACTLY), true, null, null, TEST, null);
        assertThat(state.getMountableOutputAt(2).getUpdateState()).isEqualTo(LayoutOutput.STATE_UNKNOWN);
        LayoutState secondState = componentTree.calculateLayoutState(mContext, component2, SizeSpec.makeSizeSpec(10, EXACTLY), SizeSpec.makeSizeSpec(10, EXACTLY), true, state, null, TEST, null);
        assertThat(secondState.getMountableOutputAt(2).getUpdateState()).isEqualTo(LayoutOutput.STATE_UPDATED);
        LayoutState thirdState = componentTree.calculateLayoutState(mContext, component3, SizeSpec.makeSizeSpec(10, EXACTLY), SizeSpec.makeSizeSpec(10, EXACTLY), true, secondState, null, TEST, null);
        assertThat(thirdState.getMountableOutputAt(2).getUpdateState()).isEqualTo(LayoutOutput.STATE_DIRTY);
    }

    @Test
    public void testLayoutOutputUpdateStateIdClash() {
        final Component component1 = new TreeDiffingTest.TestLayoutWithStateIdClash(false);
        final Component component2 = new TreeDiffingTest.TestLayoutWithStateIdClash(true);
        ComponentTree componentTree = ComponentTree.create(mContext, component1).build();
        LayoutState state = componentTree.calculateLayoutState(mContext, component1, SizeSpec.makeSizeSpec(10, EXACTLY), SizeSpec.makeSizeSpec(10, EXACTLY), true, null, null, TEST, null);
        TreeDiffingTest.assertOutputsState(state, LayoutOutput.STATE_UNKNOWN);
        LayoutState secondState = componentTree.calculateLayoutState(mContext, component2, SizeSpec.makeSizeSpec(10, EXACTLY), SizeSpec.makeSizeSpec(10, EXACTLY), true, state, null, TEST, null);
        assertThat(6).isEqualTo(secondState.getMountableOutputCount());
        assertThat(LayoutOutput.STATE_DIRTY).isEqualTo(secondState.getMountableOutputAt(0).getUpdateState());
        assertThat(LayoutOutput.STATE_UNKNOWN).isEqualTo(secondState.getMountableOutputAt(1).getUpdateState());
        assertThat(LayoutOutput.STATE_UPDATED).isEqualTo(secondState.getMountableOutputAt(2).getUpdateState());
        assertThat(LayoutOutput.STATE_UNKNOWN).isEqualTo(secondState.getMountableOutputAt(3).getUpdateState());
        assertThat(LayoutOutput.STATE_UNKNOWN).isEqualTo(secondState.getMountableOutputAt(4).getUpdateState());
        assertThat(LayoutOutput.STATE_UNKNOWN).isEqualTo(secondState.getMountableOutputAt(5).getUpdateState());
    }

    @Test
    public void testDiffTreeUsedIfRootMeasureSpecsAreDifferentButChildHasSame() {
        final TestComponent component = TestDrawableComponent.create(mContext).color(BLACK).build();
        final Component layoutComponent = new TreeDiffingTest.TestSimpleContainerLayout2(component);
        LayoutState firstLayoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, layoutComponent, SizeSpec.makeSizeSpec(100, EXACTLY), SizeSpec.makeSizeSpec(100, EXACTLY), null);
        assertThat(component.wasMeasureCalled()).isTrue();
        final TestComponent secondComponent = TestDrawableComponent.create(mContext).color(BLACK).build();
        final Component secondLayoutComponent = new TreeDiffingTest.TestSimpleContainerLayout2(secondComponent);
        TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, secondLayoutComponent, SizeSpec.makeSizeSpec(100, EXACTLY), SizeSpec.makeSizeSpec(90, EXACTLY), firstLayoutState);
        assertThat(secondComponent.wasMeasureCalled()).isFalse();
    }

    @Test
    public void testDiffTreeUsedIfMeasureSpecsAreSame() {
        final TestComponent component = TestDrawableComponent.create(mContext).color(BLACK).build();
        final Component layoutComponent = new TreeDiffingTest.TestSimpleContainerLayout(component, 0);
        LayoutState firstLayoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, layoutComponent, SizeSpec.makeSizeSpec(100, EXACTLY), SizeSpec.makeSizeSpec(100, EXACTLY), null);
        assertThat(component.wasMeasureCalled()).isTrue();
        final TestComponent secondComponent = TestDrawableComponent.create(mContext).color(BLACK).build();
        final Component secondLayoutComponent = new TreeDiffingTest.TestSimpleContainerLayout(secondComponent, 0);
        TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, secondLayoutComponent, SizeSpec.makeSizeSpec(100, EXACTLY), SizeSpec.makeSizeSpec(100, EXACTLY), firstLayoutState);
        assertThat(secondComponent.wasMeasureCalled()).isFalse();
    }

    @Test
    public void testCachedMeasuresForNestedTreeComponentDelegateWithUndefinedSize() {
        final Component component1 = new TreeDiffingTest.TestNestedTreeDelegateWithUndefinedSizeLayout();
        final Component component2 = new TreeDiffingTest.TestNestedTreeDelegateWithUndefinedSizeLayout();
        LayoutState prevLayoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, component1, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY), null);
        LayoutState layoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, component2, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY), prevLayoutState);
        // The nested root measure() was called in the first layout calculation.
        TestComponent prevNestedRoot = ((TestComponent) (prevLayoutState.getMountableOutputAt(2).getComponent()));
        assertThat(prevNestedRoot.wasMeasureCalled()).isTrue();
        TestComponent nestedRoot = ((TestComponent) (layoutState.getMountableOutputAt(2).getComponent()));
        assertThat(nestedRoot.wasMeasureCalled()).isFalse();
    }

    @Test
    public void testCachedMeasuresForNestedTreeComponentWithUndefinedSize() {
        final Component component1 = new TreeDiffingTest.TestUndefinedSizeLayout();
        final Component component2 = new TreeDiffingTest.TestUndefinedSizeLayout();
        LayoutState prevLayoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, component1, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY), null);
        LayoutState layoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, component2, SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(200, EXACTLY), prevLayoutState);
        // The nested root measure() was called in the first layout calculation.
        TestComponent prevMainTreeLeaf = ((TestComponent) (prevLayoutState.getMountableOutputAt(1).getComponent()));
        assertThat(prevMainTreeLeaf.wasMeasureCalled()).isTrue();
        TestComponent prevNestedLeaf1 = ((TestComponent) (prevLayoutState.getMountableOutputAt(3).getComponent()));
        assertThat(prevNestedLeaf1.wasMeasureCalled()).isTrue();
        TestComponent prevNestedLeaf2 = ((TestComponent) (prevLayoutState.getMountableOutputAt(4).getComponent()));
        assertThat(prevNestedLeaf2.wasMeasureCalled()).isTrue();
        TestComponent mainTreeLeaf = ((TestComponent) (layoutState.getMountableOutputAt(1).getComponent()));
        assertThat(mainTreeLeaf.wasMeasureCalled()).isFalse();
        TestComponent nestedLeaf1 = ((TestComponent) (layoutState.getMountableOutputAt(3).getComponent()));
        assertThat(nestedLeaf1.wasMeasureCalled()).isFalse();
        TestComponent nestedLeaf2 = ((TestComponent) (layoutState.getMountableOutputAt(4).getComponent()));
        assertThat(nestedLeaf2.wasMeasureCalled()).isFalse();
    }

    @Test
    public void testCachedMeasuresForCachedLayoutSpecWithMeasure() {
        final ComponentContext c = new ComponentContext(application);
        final int widthSpecContainer = SizeSpec.makeSizeSpec(300, EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(40, SizeSpec.AT_MOST);
        final int horizontalPadding = 20;
        final int widthMeasuredComponent = SizeSpec.makeSizeSpec((((SizeSpec.getSize(widthSpecContainer)) - horizontalPadding) - horizontalPadding), EXACTLY);
        final Component sizeDependentComponentSpy1 = Mockito.spy(TestSizeDependentComponent.create(c).setFixSizes(false).setDelegate(false).build());
        Size sizeOutput1 = new Size();
        sizeDependentComponentSpy1.measure(c, widthMeasuredComponent, heightSpec, sizeOutput1);
        // Now embed the measured component in another container and calculate a layout.
        final Component rootContainer1 = new TreeDiffingTest.TestSimpleContainerLayout(sizeDependentComponentSpy1, horizontalPadding);
        final Component sizeDependentComponentSpy2 = Mockito.spy(TestSizeDependentComponent.create(c).setFixSizes(false).setDelegate(false).build());
        Size sizeOutput2 = new Size();
        sizeDependentComponentSpy1.measure(c, widthMeasuredComponent, heightSpec, sizeOutput2);
        // Now embed the measured component in another container and calculate a layout.
        final Component rootContainer2 = new TreeDiffingTest.TestSimpleContainerLayout(sizeDependentComponentSpy2, horizontalPadding);
        // Reset the release/clear counts before issuing calculate().
        Mockito.reset(sizeDependentComponentSpy1);
        Mockito.doReturn(sizeDependentComponentSpy1).when(sizeDependentComponentSpy1).makeShallowCopy();
        LayoutState prevLayoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, rootContainer1, widthSpecContainer, heightSpec, null);
        // Make sure we reused the cached layout.
        Mockito.verify(sizeDependentComponentSpy1, Mockito.times(1)).clearCachedLayout();
        LayoutState layoutState = TreeDiffingTest.calculateLayoutStateWithDiffing(mContext, rootContainer2, widthSpecContainer, heightSpec, prevLayoutState);
        Mockito.verify(sizeDependentComponentSpy1).makeShallowCopy();
        // Make sure we reused the cached layout.
        Mockito.verify(sizeDependentComponentSpy2, Mockito.never()).clearCachedLayout();
        // The nested root measure() was called in the first layout calculation.
        TestComponent prevNestedLeaf1 = ((TestComponent) (prevLayoutState.getMountableOutputAt(2).getComponent()));
        assertThat(prevNestedLeaf1.wasMeasureCalled()).isTrue();
        TestComponent prevNestedLeaf2 = ((TestComponent) (prevLayoutState.getMountableOutputAt(3).getComponent()));
        assertThat(prevNestedLeaf2.wasMeasureCalled()).isTrue();
        TestComponent nestedLeaf1 = ((TestComponent) (layoutState.getMountableOutputAt(2).getComponent()));
        assertThat(nestedLeaf1.wasMeasureCalled()).isFalse();
        TestComponent nestedLeaf2 = ((TestComponent) (layoutState.getMountableOutputAt(3).getComponent()));
        assertThat(nestedLeaf2.wasMeasureCalled()).isFalse();
    }

    private static class TestLayoutSpec extends InlineLayoutSpec {
        private final boolean mAddThirdChild;

        TestLayoutSpec(boolean addThirdChild) {
            super();
            this.mAddThirdChild = addThirdChild;
        }

        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c))).child((mAddThirdChild ? TestDrawableComponent.create(c) : null)).build();
        }
    }

    private static class TestSimpleContainerLayout extends InlineLayoutSpec {
        private final Component mComponent;

        private final int mHorizontalPadding;

        TestSimpleContainerLayout(Component component, int horizontalPadding) {
            super();
            mComponent = component;
            mHorizontalPadding = horizontalPadding;
        }

        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return Column.create(c).paddingPx(HORIZONTAL, mHorizontalPadding).child(mComponent).build();
        }
    }

    private static class TestSimpleContainerLayout2 extends InlineLayoutSpec {
        private final Component mComponent;

        TestSimpleContainerLayout2(Component component) {
            super();
            mComponent = component;
        }

        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return Column.create(c).alignItems(FLEX_START).child(Wrapper.create(c).delegate(mComponent).heightPx(50)).build();
        }
    }

    private static class TestLayoutSpecInnerState extends InlineLayoutSpec {
        private final boolean mChangeChildDrawable;

        TestLayoutSpecInnerState(boolean changeChildDrawable) {
            super();
            mChangeChildDrawable = changeChildDrawable;
        }

        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return Column.create(c).background(TreeDiffingTest.sRedDrawable).foregroundRes(btn_default).child(TestDrawableComponent.create(c).background((mChangeChildDrawable ? TreeDiffingTest.sRedDrawable : TreeDiffingTest.sBlackDrawable))).child(Column.create(c).child(TestDrawableComponent.create(c))).build();
        }
    }

    private static class TestLayoutSpecBgState extends InlineLayoutSpec {
        private final boolean mChangeBg;

        TestLayoutSpecBgState(boolean changeBg) {
            super();
            mChangeBg = changeBg;
        }

        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return Column.create(c).background((mChangeBg ? TreeDiffingTest.sBlackDrawable : TreeDiffingTest.sRedDrawable)).foreground(TreeDiffingTest.sTransparentDrawable).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c))).build();
        }
    }

    private static class TestUndefinedSizeLayout extends InlineLayoutSpec {
        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return Column.create(c).paddingPx(ALL, 2).child(TestDrawableComponent.create(c, false, true, true, false, false)).child(TestSizeDependentComponent.create(c).setDelegate(false).flexShrink(0).marginPx(ALL, 11)).build();
        }
    }

    private static class TestNestedTreeDelegateWithUndefinedSizeLayout extends InlineLayoutSpec {
        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return Column.create(c).paddingPx(ALL, 2).child(TestSizeDependentComponent.create(c).setDelegate(true).marginPx(ALL, 11)).build();
        }
    }

    private static class TestLayoutWithStateIdClash extends InlineLayoutSpec {
        private final boolean mAddChild;

        TestLayoutWithStateIdClash(boolean addChild) {
            super();
            mAddChild = addChild;
        }

        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return Column.create(c).child(Column.create(c).wrapInView().child(TestDrawableComponent.create(c)).child((mAddChild ? TestDrawableComponent.create(c) : null))).child(Column.create(c).wrapInView().child(TestDrawableComponent.create(c))).build();
        }
    }
}

