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


import AccessibilityRole.BUTTON;
import Build.VERSION_CODES;
import Color.BLACK;
import Color.RED;
import ImportantForAccessibility.IMPORTANT_FOR_ACCESSIBILITY_AUTO;
import Transition.TransitionKeyType.GLOBAL;
import YogaAlign.AUTO;
import YogaDirection.INHERIT;
import YogaEdge.ALL;
import YogaEdge.BOTTOM;
import YogaEdge.LEFT;
import YogaEdge.RIGHT;
import YogaEdge.TOP;
import YogaEdge.VERTICAL;
import YogaPositionType.ABSOLUTE;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.util.SparseArray;
import androidx.annotation.AttrRes;
import androidx.annotation.StyleRes;
import com.facebook.litho.drawable.ComparableColorDrawable;
import com.facebook.litho.drawable.ComparableDrawable;
import com.facebook.litho.reference.DrawableReference;
import com.facebook.litho.reference.Reference;
import com.facebook.litho.testing.TestComponent;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.TestSizeDependentComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;


@RunWith(ComponentsTestRunner.class)
public class LayoutStateCreateTreeTest {
    private ComponentContext mComponentContext;

    @Test
    public void testSimpleLayoutCreatesExpectedInternalNodeTree() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c))).build();
            }
        };
        InternalNode node = LayoutState.createTree(component, mComponentContext);
        assertThat(node.getChildCount()).isEqualTo(1);
        assertThat(node.getRootComponent()).isEqualTo(component);
        node = node.getChildAt(0);
        assertThat(node.getChildCount()).isEqualTo(1);
        assertThat(node.getRootComponent()).isInstanceOf(Column.class);
        node = node.getChildAt(0);
        assertThat(node.getChildCount()).isEqualTo(0);
        assertThat(node.getRootComponent()).isInstanceOf(TestDrawableComponent.class);
    }

    @Test
    public void testHandlersAreAppliedToCorrectInternalNodes() {
        final EventHandler<ClickEvent> clickHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<ClickEvent> clickHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<ClickEvent> clickHandler3 = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler3 = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler3 = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler3 = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler3 = Mockito.mock(EventHandler.class);
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c).clickHandler(clickHandler1).longClickHandler(longClickHandler1).touchHandler(touchHandler1).interceptTouchHandler(interceptTouchHandler1).focusChangeHandler(focusChangedHandler1)).clickHandler(clickHandler2).longClickHandler(longClickHandler2).touchHandler(touchHandler2).interceptTouchHandler(interceptTouchHandler2).focusChangeHandler(focusChangedHandler2)).clickHandler(clickHandler3).longClickHandler(longClickHandler3).touchHandler(touchHandler3).interceptTouchHandler(interceptTouchHandler3).focusChangeHandler(focusChangedHandler3).build();
            }
        };
        InternalNode node = LayoutState.createTree(component, mComponentContext);
        assertThat(node.getNodeInfo().getClickHandler()).isEqualTo(clickHandler3);
        assertThat(node.getNodeInfo().getLongClickHandler()).isEqualTo(longClickHandler3);
        assertThat(node.getNodeInfo().getTouchHandler()).isEqualTo(touchHandler3);
        assertThat(node.getNodeInfo().getInterceptTouchHandler()).isEqualTo(interceptTouchHandler3);
        assertThat(node.getNodeInfo().getFocusChangeHandler()).isEqualTo(focusChangedHandler3);
        node = node.getChildAt(0);
        assertThat(node.getNodeInfo().getClickHandler()).isEqualTo(clickHandler2);
        assertThat(node.getNodeInfo().getLongClickHandler()).isEqualTo(longClickHandler2);
        assertThat(node.getNodeInfo().getTouchHandler()).isEqualTo(touchHandler2);
        assertThat(node.getNodeInfo().getInterceptTouchHandler()).isEqualTo(interceptTouchHandler2);
        assertThat(node.getNodeInfo().getFocusChangeHandler()).isEqualTo(focusChangedHandler2);
        node = node.getChildAt(0);
        assertThat(node.getNodeInfo().getClickHandler()).isEqualTo(clickHandler1);
        assertThat(node.getNodeInfo().getLongClickHandler()).isEqualTo(longClickHandler1);
        assertThat(node.getNodeInfo().getTouchHandler()).isEqualTo(touchHandler1);
        assertThat(node.getNodeInfo().getInterceptTouchHandler()).isEqualTo(interceptTouchHandler1);
        assertThat(node.getNodeInfo().getFocusChangeHandler()).isEqualTo(focusChangedHandler1);
    }

    @Test
    public void testHandlersAreAppliedToCorrectInternalNodesForSizeDependentComponent() {
        final EventHandler<ClickEvent> clickHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<ClickEvent> clickHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<ClickEvent> clickHandler3 = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler3 = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler3 = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler3 = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler3 = Mockito.mock(EventHandler.class);
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestSizeDependentComponent.create(c).clickHandler(clickHandler1).longClickHandler(longClickHandler1).touchHandler(touchHandler1).interceptTouchHandler(interceptTouchHandler1).focusChangeHandler(focusChangedHandler1)).clickHandler(clickHandler2).longClickHandler(longClickHandler2).touchHandler(touchHandler2).interceptTouchHandler(interceptTouchHandler2).focusChangeHandler(focusChangedHandler2)).clickHandler(clickHandler3).longClickHandler(longClickHandler3).touchHandler(touchHandler3).interceptTouchHandler(interceptTouchHandler3).focusChangeHandler(focusChangedHandler3).build();
            }
        };
        InternalNode node = LayoutState.createTree(component, mComponentContext);
        assertThat(node.getNodeInfo().getClickHandler()).isEqualTo(clickHandler3);
        assertThat(node.getNodeInfo().getLongClickHandler()).isEqualTo(longClickHandler3);
        assertThat(node.getNodeInfo().getTouchHandler()).isEqualTo(touchHandler3);
        assertThat(node.getNodeInfo().getInterceptTouchHandler()).isEqualTo(interceptTouchHandler3);
        assertThat(node.getNodeInfo().getFocusChangeHandler()).isEqualTo(focusChangedHandler3);
        node = node.getChildAt(0);
        assertThat(node.getNodeInfo().getClickHandler()).isEqualTo(clickHandler2);
        assertThat(node.getNodeInfo().getLongClickHandler()).isEqualTo(longClickHandler2);
        assertThat(node.getNodeInfo().getTouchHandler()).isEqualTo(touchHandler2);
        assertThat(node.getNodeInfo().getInterceptTouchHandler()).isEqualTo(interceptTouchHandler2);
        assertThat(node.getNodeInfo().getFocusChangeHandler()).isEqualTo(focusChangedHandler2);
        node = node.getChildAt(0);
        assertThat(node.getNodeInfo().getClickHandler()).isEqualTo(clickHandler1);
        assertThat(node.getNodeInfo().getLongClickHandler()).isEqualTo(longClickHandler1);
        assertThat(node.getNodeInfo().getTouchHandler()).isEqualTo(touchHandler1);
        assertThat(node.getNodeInfo().getInterceptTouchHandler()).isEqualTo(interceptTouchHandler1);
        assertThat(node.getNodeInfo().getFocusChangeHandler()).isEqualTo(focusChangedHandler1);
    }

    @Test
    public void testOverridingHandlers() {
        final EventHandler<ClickEvent> clickHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<ClickEvent> clickHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler2 = Mockito.mock(EventHandler.class);
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Wrapper.create(c).delegate(new InlineLayoutSpec() {
                    @Override
                    protected Component onCreateLayout(ComponentContext c) {
                        return TestDrawableComponent.create(c).clickHandler(clickHandler1).longClickHandler(longClickHandler1).touchHandler(touchHandler1).interceptTouchHandler(interceptTouchHandler1).focusChangeHandler(focusChangedHandler1).build();
                    }
                }).clickHandler(clickHandler2).longClickHandler(longClickHandler2).touchHandler(touchHandler2).interceptTouchHandler(interceptTouchHandler2).focusChangeHandler(focusChangedHandler2).build();
            }
        };
        InternalNode node = LayoutState.createTree(component, mComponentContext);
        assertThat(node.getChildCount()).isEqualTo(0);
        assertThat(node.getRootComponent()).isInstanceOf(TestDrawableComponent.class);
        assertThat(node.getNodeInfo().getClickHandler()).isEqualTo(clickHandler2);
        assertThat(node.getNodeInfo().getLongClickHandler()).isEqualTo(longClickHandler2);
        assertThat(node.getNodeInfo().getTouchHandler()).isEqualTo(touchHandler2);
        assertThat(node.getNodeInfo().getInterceptTouchHandler()).isEqualTo(interceptTouchHandler2);
        assertThat(node.getNodeInfo().getFocusChangeHandler()).isEqualTo(focusChangedHandler2);
    }

    @Test
    public void testOverridingHandlersForSizeDependentComponent() {
        final EventHandler<ClickEvent> clickHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<ClickEvent> clickHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler2 = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler1 = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler2 = Mockito.mock(EventHandler.class);
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Wrapper.create(c).delegate(new InlineLayoutSpec() {
                    @Override
                    protected Component onCreateLayout(ComponentContext c) {
                        return TestSizeDependentComponent.create(c).clickHandler(clickHandler1).longClickHandler(longClickHandler1).touchHandler(touchHandler1).interceptTouchHandler(interceptTouchHandler1).focusChangeHandler(focusChangedHandler1).build();
                    }
                }).clickHandler(clickHandler2).longClickHandler(longClickHandler2).touchHandler(touchHandler2).interceptTouchHandler(interceptTouchHandler2).focusChangeHandler(focusChangedHandler2).build();
            }
        };
        InternalNode node = LayoutState.createTree(component, mComponentContext);
        assertThat(node.getChildCount()).isEqualTo(0);
        assertThat(node.getRootComponent()).isInstanceOf(TestSizeDependentComponent.class);
        assertThat(node.getNodeInfo().getClickHandler()).isEqualTo(clickHandler2);
        assertThat(node.getNodeInfo().getLongClickHandler()).isEqualTo(longClickHandler2);
        assertThat(node.getNodeInfo().getTouchHandler()).isEqualTo(touchHandler2);
        assertThat(node.getNodeInfo().getInterceptTouchHandler()).isEqualTo(interceptTouchHandler2);
        assertThat(node.getNodeInfo().getFocusChangeHandler()).isEqualTo(focusChangedHandler2);
    }

    @Test
    @Config(sdk = VERSION_CODES.LOLLIPOP)
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public void testAddingAllAttributes() {
        final ComparableDrawable background = ComparableColorDrawable.create(RED);
        final Reference<ComparableDrawable> bgRef = DrawableReference.create(background);
        final ComparableDrawable foreground = ComparableColorDrawable.create(BLACK);
        final EventHandler<ClickEvent> clickHandler = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler = Mockito.mock(EventHandler.class);
        final EventHandler<VisibleEvent> visibleHandler = Mockito.mock(EventHandler.class);
        final EventHandler<FocusedVisibleEvent> focusedHandler = Mockito.mock(EventHandler.class);
        final EventHandler<UnfocusedVisibleEvent> unfocusedHandler = Mockito.mock(EventHandler.class);
        final EventHandler<FullImpressionVisibleEvent> fullImpressionHandler = Mockito.mock(EventHandler.class);
        final EventHandler<InvisibleEvent> invisibleHandler = Mockito.mock(EventHandler.class);
        final EventHandler<VisibilityChangedEvent> visibleRectChangedHandler = Mockito.mock(EventHandler.class);
        final Object viewTag = new Object();
        final SparseArray<Object> viewTags = new SparseArray();
        final EventHandler<DispatchPopulateAccessibilityEventEvent> dispatchPopulateAccessibilityEventHandler = Mockito.mock(EventHandler.class);
        final EventHandler<OnInitializeAccessibilityEventEvent> onInitializeAccessibilityEventHandler = Mockito.mock(EventHandler.class);
        final EventHandler<OnInitializeAccessibilityNodeInfoEvent> onInitializeAccessibilityNodeInfoHandler = Mockito.mock(EventHandler.class);
        final EventHandler<OnPopulateAccessibilityEventEvent> onPopulateAccessibilityEventHandler = Mockito.mock(EventHandler.class);
        final EventHandler<OnRequestSendAccessibilityEventEvent> onRequestSendAccessibilityEventHandler = Mockito.mock(EventHandler.class);
        final EventHandler<PerformAccessibilityActionEvent> performAccessibilityActionHandler = Mockito.mock(EventHandler.class);
        final EventHandler<SendAccessibilityEventEvent> sendAccessibilityEventHandler = Mockito.mock(EventHandler.class);
        final EventHandler<SendAccessibilityEventUncheckedEvent> sendAccessibilityEventUncheckedHandler = Mockito.mock(EventHandler.class);
        final StateListAnimator stateListAnimator = Mockito.mock(StateListAnimator.class);
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return LayoutStateCreateTreeTest.TestDrawableComponentWithMockInternalNode.create(c).layoutDirection(INHERIT).alignSelf(AUTO).positionType(ABSOLUTE).flex(2).flexGrow(3).flexShrink(4).flexBasisPx(5).flexBasisPercent(6).importantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_AUTO).duplicateParentState(false).marginPx(ALL, 5).marginPx(RIGHT, 6).marginPx(LEFT, 4).marginPercent(ALL, 10).marginPercent(VERTICAL, 12).marginPercent(RIGHT, 5).marginAuto(LEFT).marginAuto(TOP).marginAuto(RIGHT).marginAuto(BOTTOM).paddingPx(ALL, 1).paddingPx(RIGHT, 2).paddingPx(LEFT, 3).paddingPercent(VERTICAL, 7).paddingPercent(RIGHT, 6).paddingPercent(ALL, 5).positionPx(ALL, 11).positionPx(RIGHT, 12).positionPx(LEFT, 13).positionPercent(VERTICAL, 17).positionPercent(RIGHT, 16).positionPercent(ALL, 15).widthPx(5).widthPercent(50).minWidthPx(15).minWidthPercent(100).maxWidthPx(25).maxWidthPercent(26).heightPx(30).heightPercent(31).minHeightPx(32).minHeightPercent(33).maxHeightPx(34).maxHeightPercent(35).aspectRatio(20).touchExpansionPx(RIGHT, 22).touchExpansionPx(LEFT, 23).touchExpansionPx(ALL, 21).background(bgRef).foreground(foreground).wrapInView().clickHandler(clickHandler).focusChangeHandler(focusChangedHandler).longClickHandler(longClickHandler).touchHandler(touchHandler).interceptTouchHandler(interceptTouchHandler).focusable(true).selected(false).enabled(false).visibleHeightRatio(55).visibleWidthRatio(56).visibleHandler(visibleHandler).focusedHandler(focusedHandler).unfocusedHandler(unfocusedHandler).fullImpressionHandler(fullImpressionHandler).invisibleHandler(invisibleHandler).visibilityChangedHandler(visibleRectChangedHandler).contentDescription("test").viewTag(viewTag).viewTags(viewTags).shadowElevationPx(60).clipToOutline(false).transitionKey("transitionKey").transitionKeyType(GLOBAL).testKey("testKey").accessibilityRole(BUTTON).accessibilityRoleDescription("Test Role Description").dispatchPopulateAccessibilityEventHandler(dispatchPopulateAccessibilityEventHandler).onInitializeAccessibilityEventHandler(onInitializeAccessibilityEventHandler).onInitializeAccessibilityNodeInfoHandler(onInitializeAccessibilityNodeInfoHandler).onPopulateAccessibilityEventHandler(onPopulateAccessibilityEventHandler).onRequestSendAccessibilityEventHandler(onRequestSendAccessibilityEventHandler).performAccessibilityActionHandler(performAccessibilityActionHandler).sendAccessibilityEventHandler(sendAccessibilityEventHandler).sendAccessibilityEventUncheckedHandler(sendAccessibilityEventUncheckedHandler).stateListAnimator(stateListAnimator).build();
            }
        };
        InternalNode node = LayoutState.createTree(component, mComponentContext);
        NodeInfo nodeInfo = node.getOrCreateNodeInfo();
        Mockito.verify(node).layoutDirection(INHERIT);
        Mockito.verify(node).alignSelf(AUTO);
        Mockito.verify(node).positionType(ABSOLUTE);
        Mockito.verify(node).flex(2);
        Mockito.verify(node).flexGrow(3);
        Mockito.verify(node).flexShrink(4);
        Mockito.verify(node).flexBasisPx(5);
        Mockito.verify(node).flexBasisPercent(6);
        Mockito.verify(node).importantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        Mockito.verify(node).duplicateParentState(false);
        Mockito.verify(node).marginPx(ALL, 5);
        Mockito.verify(node).marginPx(RIGHT, 6);
        Mockito.verify(node).marginPx(LEFT, 4);
        Mockito.verify(node).marginPercent(ALL, 10);
        Mockito.verify(node).marginPercent(VERTICAL, 12);
        Mockito.verify(node).marginPercent(RIGHT, 5);
        Mockito.verify(node).marginAuto(LEFT);
        Mockito.verify(node).marginAuto(TOP);
        Mockito.verify(node).marginAuto(RIGHT);
        Mockito.verify(node).marginAuto(BOTTOM);
        Mockito.verify(node).paddingPx(ALL, 1);
        Mockito.verify(node).paddingPx(RIGHT, 2);
        Mockito.verify(node).paddingPx(LEFT, 3);
        Mockito.verify(node).paddingPercent(VERTICAL, 7);
        Mockito.verify(node).paddingPercent(RIGHT, 6);
        Mockito.verify(node).paddingPercent(ALL, 5);
        Mockito.verify(node).positionPx(ALL, 11);
        Mockito.verify(node).positionPx(RIGHT, 12);
        Mockito.verify(node).positionPx(LEFT, 13);
        Mockito.verify(node).positionPercent(VERTICAL, 17);
        Mockito.verify(node).positionPercent(RIGHT, 16);
        Mockito.verify(node).positionPercent(ALL, 15);
        Mockito.verify(node).widthPx(5);
        Mockito.verify(node).widthPercent(50);
        Mockito.verify(node).minWidthPx(15);
        Mockito.verify(node).minWidthPercent(100);
        Mockito.verify(node).maxWidthPx(25);
        Mockito.verify(node).maxWidthPercent(26);
        Mockito.verify(node).heightPx(30);
        Mockito.verify(node).heightPercent(31);
        Mockito.verify(node).minHeightPx(32);
        Mockito.verify(node).minHeightPercent(33);
        Mockito.verify(node).maxHeightPx(34);
        Mockito.verify(node).maxHeightPercent(35);
        Mockito.verify(node).aspectRatio(20);
        Mockito.verify(node).touchExpansionPx(RIGHT, 22);
        Mockito.verify(node).touchExpansionPx(LEFT, 23);
        Mockito.verify(node).touchExpansionPx(ALL, 21);
        Mockito.verify(node).background(bgRef);
        Mockito.verify(node).foreground(foreground);
        Mockito.verify(node).wrapInView();
        Mockito.verify(nodeInfo).setClickHandler(clickHandler);
        Mockito.verify(nodeInfo).setFocusChangeHandler(focusChangedHandler);
        Mockito.verify(nodeInfo).setLongClickHandler(longClickHandler);
        Mockito.verify(nodeInfo).setTouchHandler(touchHandler);
        Mockito.verify(nodeInfo).setInterceptTouchHandler(interceptTouchHandler);
        Mockito.verify(nodeInfo).setFocusable(true);
        Mockito.verify(nodeInfo).setSelected(false);
        Mockito.verify(nodeInfo).setEnabled(false);
        Mockito.verify(node).visibleHeightRatio(55);
        Mockito.verify(node).visibleWidthRatio(56);
        Mockito.verify(node).visibleHandler(visibleHandler);
        Mockito.verify(node).focusedHandler(focusedHandler);
        Mockito.verify(node).unfocusedHandler(unfocusedHandler);
        Mockito.verify(node).fullImpressionHandler(fullImpressionHandler);
        Mockito.verify(node).invisibleHandler(invisibleHandler);
        Mockito.verify(node).visibilityChangedHandler(visibleRectChangedHandler);
        Mockito.verify(nodeInfo).setContentDescription("test");
        Mockito.verify(nodeInfo).setViewTag(viewTag);
        Mockito.verify(nodeInfo).setViewTags(viewTags);
        Mockito.verify(nodeInfo).setShadowElevation(60);
        Mockito.verify(nodeInfo).setClipToOutline(false);
        Mockito.verify(node).transitionKey("transitionKey");
        Mockito.verify(node).transitionKeyType(GLOBAL);
        Mockito.verify(node).testKey("testKey");
        Mockito.verify(nodeInfo).setAccessibilityRole(BUTTON);
        Mockito.verify(nodeInfo).setAccessibilityRoleDescription("Test Role Description");
        Mockito.verify(nodeInfo).setDispatchPopulateAccessibilityEventHandler(dispatchPopulateAccessibilityEventHandler);
        Mockito.verify(nodeInfo).setOnInitializeAccessibilityEventHandler(onInitializeAccessibilityEventHandler);
        Mockito.verify(nodeInfo).setOnInitializeAccessibilityNodeInfoHandler(onInitializeAccessibilityNodeInfoHandler);
        Mockito.verify(nodeInfo).setOnPopulateAccessibilityEventHandler(onPopulateAccessibilityEventHandler);
        Mockito.verify(nodeInfo).setOnRequestSendAccessibilityEventHandler(onRequestSendAccessibilityEventHandler);
        Mockito.verify(nodeInfo).setPerformAccessibilityActionHandler(performAccessibilityActionHandler);
        Mockito.verify(nodeInfo).setSendAccessibilityEventHandler(sendAccessibilityEventHandler);
        Mockito.verify(nodeInfo).setSendAccessibilityEventUncheckedHandler(sendAccessibilityEventUncheckedHandler);
        Mockito.verify(node).stateListAnimator(stateListAnimator);
    }

    @Test
    public void testCopyPropsOnlyCalledOnce() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).flexGrow(1)).build();
            }
        };
        final InternalNode root = LayoutState.createTree(component, new LayoutStateCreateTreeTest.MockInternalNodeComponentContext(application));
        assertThat(((root.getChildAt(0)) instanceof LayoutStateCreateTreeTest.TestInternalNode)).isTrue();
        assertThat(((LayoutStateCreateTreeTest.TestInternalNode) (root.getChildAt(0))).mFlexGrowCounter).isEqualTo(1);
    }

    private static class TestDrawableComponentWithMockInternalNode extends TestComponent {
        protected ComponentLayout resolve(ComponentContext c) {
            InternalNode node = Mockito.mock(InternalNode.class);
            NodeInfo nodeInfo = Mockito.mock(NodeInfo.class);
            Mockito.when(node.getOrCreateNodeInfo()).thenReturn(nodeInfo);
            getCommonPropsCopyable().copyInto(c, node);
            return node;
        }

        public static LayoutStateCreateTreeTest.TestDrawableComponentWithMockInternalNode.Builder create(ComponentContext c) {
            LayoutStateCreateTreeTest.TestDrawableComponentWithMockInternalNode.Builder builder = new LayoutStateCreateTreeTest.TestDrawableComponentWithMockInternalNode.Builder();
            builder.mComponent = new LayoutStateCreateTreeTest.TestDrawableComponentWithMockInternalNode();
            builder.init(c, 0, 0, builder.mComponent);
            return builder;
        }

        public static class Builder extends Component.Builder<LayoutStateCreateTreeTest.TestDrawableComponentWithMockInternalNode.Builder> {
            private Component mComponent;

            @Override
            public LayoutStateCreateTreeTest.TestDrawableComponentWithMockInternalNode.Builder getThis() {
                return this;
            }

            @Override
            public Component build() {
                return mComponent;
            }
        }
    }

    private class MockInternalNodeComponentContext extends ComponentContext {
        private MockInternalNodeComponentContext(Context context) {
            super(context);
        }

        InternalNode newLayoutBuilder(@AttrRes
        int defStyleAttr, @StyleRes
        int defStyleRes) {
            return new LayoutStateCreateTreeTest.TestInternalNode(this);
        }

        @Override
        ComponentContext makeNewCopy() {
            return new LayoutStateCreateTreeTest.MockInternalNodeComponentContext(getAndroidContext());
        }
    }

    private class TestInternalNode extends InternalNode {
        private int mFlexGrowCounter;

        protected TestInternalNode(ComponentContext componentContext) {
            super(componentContext);
        }

        @Override
        LayoutStateCreateTreeTest.TestInternalNode flexGrow(float flex) {
            (mFlexGrowCounter)++;
            return ((LayoutStateCreateTreeTest.TestInternalNode) (super.flexGrow(flex)));
        }
    }
}

