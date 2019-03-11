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
import android.util.SparseArray;
import com.facebook.litho.drawable.ComparableColorDrawable;
import com.facebook.litho.drawable.ComparableDrawable;
import com.facebook.litho.drawable.ComparableResDrawable;
import com.facebook.litho.reference.DrawableReference;
import com.facebook.litho.reference.Reference;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


@RunWith(ComponentsTestRunner.class)
public class CommonPropsTest {
    private InternalNode mNode;

    private NodeInfo mNodeInfo;

    private CommonProps mCommonProps;

    private ComponentContext mComponentContext;

    @Test
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public void testSetPropsAndBuild() {
        mCommonProps.layoutDirection(INHERIT);
        mCommonProps.alignSelf(AUTO);
        mCommonProps.positionType(ABSOLUTE);
        mCommonProps.flex(2);
        mCommonProps.flexGrow(3);
        mCommonProps.flexShrink(4);
        mCommonProps.flexBasisPx(5);
        mCommonProps.flexBasisPercent(6);
        mCommonProps.importantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        mCommonProps.duplicateParentState(false);
        mCommonProps.marginPx(ALL, 5);
        mCommonProps.marginPx(RIGHT, 6);
        mCommonProps.marginPx(LEFT, 4);
        mCommonProps.marginPercent(ALL, 10);
        mCommonProps.marginPercent(VERTICAL, 12);
        mCommonProps.marginPercent(RIGHT, 5);
        mCommonProps.marginAuto(LEFT);
        mCommonProps.marginAuto(TOP);
        mCommonProps.marginAuto(RIGHT);
        mCommonProps.marginAuto(BOTTOM);
        mCommonProps.paddingPx(ALL, 1);
        mCommonProps.paddingPx(RIGHT, 2);
        mCommonProps.paddingPx(LEFT, 3);
        mCommonProps.paddingPercent(VERTICAL, 7);
        mCommonProps.paddingPercent(RIGHT, 6);
        mCommonProps.paddingPercent(ALL, 5);
        mCommonProps.border(Border.create(mComponentContext).build());
        mCommonProps.positionPx(ALL, 11);
        mCommonProps.positionPx(RIGHT, 12);
        mCommonProps.positionPx(LEFT, 13);
        mCommonProps.positionPercent(VERTICAL, 17);
        mCommonProps.positionPercent(RIGHT, 16);
        mCommonProps.positionPercent(ALL, 15);
        mCommonProps.widthPx(5);
        mCommonProps.widthPercent(50);
        mCommonProps.minWidthPx(15);
        mCommonProps.minWidthPercent(100);
        mCommonProps.maxWidthPx(25);
        mCommonProps.maxWidthPercent(26);
        mCommonProps.heightPx(30);
        mCommonProps.heightPercent(31);
        mCommonProps.minHeightPx(32);
        mCommonProps.minHeightPercent(33);
        mCommonProps.maxHeightPx(34);
        mCommonProps.maxHeightPercent(35);
        mCommonProps.aspectRatio(20);
        mCommonProps.touchExpansionPx(RIGHT, 22);
        mCommonProps.touchExpansionPx(LEFT, 23);
        mCommonProps.touchExpansionPx(ALL, 21);
        ComparableDrawable background = ComparableColorDrawable.create(RED);
        Reference<ComparableDrawable> bgRef = DrawableReference.create(background);
        mCommonProps.background(bgRef);
        ComparableDrawable foreground = ComparableColorDrawable.create(BLACK);
        mCommonProps.foreground(foreground);
        mCommonProps.wrapInView();
        final EventHandler<ClickEvent> clickHandler = Mockito.mock(EventHandler.class);
        final EventHandler<LongClickEvent> longClickHandler = Mockito.mock(EventHandler.class);
        final EventHandler<TouchEvent> touchHandler = Mockito.mock(EventHandler.class);
        final EventHandler<InterceptTouchEvent> interceptTouchHandler = Mockito.mock(EventHandler.class);
        final EventHandler<FocusChangedEvent> focusChangedHandler = Mockito.mock(EventHandler.class);
        mCommonProps.clickHandler(clickHandler);
        mCommonProps.focusChangeHandler(focusChangedHandler);
        mCommonProps.longClickHandler(longClickHandler);
        mCommonProps.touchHandler(touchHandler);
        mCommonProps.interceptTouchHandler(interceptTouchHandler);
        mCommonProps.focusable(true);
        mCommonProps.selected(false);
        mCommonProps.enabled(false);
        mCommonProps.visibleHeightRatio(55);
        mCommonProps.visibleWidthRatio(56);
        final EventHandler<VisibleEvent> visibleHandler = Mockito.mock(EventHandler.class);
        final EventHandler<FocusedVisibleEvent> focusedHandler = Mockito.mock(EventHandler.class);
        final EventHandler<UnfocusedVisibleEvent> unfocusedHandler = Mockito.mock(EventHandler.class);
        final EventHandler<FullImpressionVisibleEvent> fullImpressionHandler = Mockito.mock(EventHandler.class);
        final EventHandler<InvisibleEvent> invisibleHandler = Mockito.mock(EventHandler.class);
        final EventHandler<VisibilityChangedEvent> visibleRectChangedHandler = Mockito.mock(EventHandler.class);
        mCommonProps.visibleHandler(visibleHandler);
        mCommonProps.focusedHandler(focusedHandler);
        mCommonProps.unfocusedHandler(unfocusedHandler);
        mCommonProps.fullImpressionHandler(fullImpressionHandler);
        mCommonProps.invisibleHandler(invisibleHandler);
        mCommonProps.visibilityChangedHandler(visibleRectChangedHandler);
        mCommonProps.contentDescription("test");
        Object viewTag = new Object();
        SparseArray<Object> viewTags = new SparseArray();
        mCommonProps.viewTag(viewTag);
        mCommonProps.viewTags(viewTags);
        mCommonProps.shadowElevationPx(60);
        mCommonProps.clipToOutline(false);
        mCommonProps.transitionKey("transitionKey");
        mCommonProps.testKey("testKey");
        final EventHandler<DispatchPopulateAccessibilityEventEvent> dispatchPopulateAccessibilityEventHandler = Mockito.mock(EventHandler.class);
        final EventHandler<OnInitializeAccessibilityEventEvent> onInitializeAccessibilityEventHandler = Mockito.mock(EventHandler.class);
        final EventHandler<OnInitializeAccessibilityNodeInfoEvent> onInitializeAccessibilityNodeInfoHandler = Mockito.mock(EventHandler.class);
        final EventHandler<OnPopulateAccessibilityEventEvent> onPopulateAccessibilityEventHandler = Mockito.mock(EventHandler.class);
        final EventHandler<OnRequestSendAccessibilityEventEvent> onRequestSendAccessibilityEventHandler = Mockito.mock(EventHandler.class);
        final EventHandler<PerformAccessibilityActionEvent> performAccessibilityActionHandler = Mockito.mock(EventHandler.class);
        final EventHandler<SendAccessibilityEventEvent> sendAccessibilityEventHandler = Mockito.mock(EventHandler.class);
        final EventHandler<SendAccessibilityEventUncheckedEvent> sendAccessibilityEventUncheckedHandler = Mockito.mock(EventHandler.class);
        mCommonProps.accessibilityRole(BUTTON);
        mCommonProps.accessibilityRoleDescription("Test Role Description");
        mCommonProps.dispatchPopulateAccessibilityEventHandler(dispatchPopulateAccessibilityEventHandler);
        mCommonProps.onInitializeAccessibilityEventHandler(onInitializeAccessibilityEventHandler);
        mCommonProps.onInitializeAccessibilityNodeInfoHandler(onInitializeAccessibilityNodeInfoHandler);
        mCommonProps.onPopulateAccessibilityEventHandler(onPopulateAccessibilityEventHandler);
        mCommonProps.onRequestSendAccessibilityEventHandler(onRequestSendAccessibilityEventHandler);
        mCommonProps.performAccessibilityActionHandler(performAccessibilityActionHandler);
        mCommonProps.sendAccessibilityEventHandler(sendAccessibilityEventHandler);
        mCommonProps.sendAccessibilityEventUncheckedHandler(sendAccessibilityEventUncheckedHandler);
        final StateListAnimator stateListAnimator = Mockito.mock(StateListAnimator.class);
        mCommonProps.stateListAnimator(stateListAnimator);
        mCommonProps.copyInto(mComponentContext, mNode);
        Mockito.verify(mNode).layoutDirection(INHERIT);
        Mockito.verify(mNode).alignSelf(AUTO);
        Mockito.verify(mNode).positionType(ABSOLUTE);
        Mockito.verify(mNode).flex(2);
        Mockito.verify(mNode).flexGrow(3);
        Mockito.verify(mNode).flexShrink(4);
        Mockito.verify(mNode).flexBasisPx(5);
        Mockito.verify(mNode).flexBasisPercent(6);
        Mockito.verify(mNode).importantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        Mockito.verify(mNode).duplicateParentState(false);
        Mockito.verify(mNode).marginPx(ALL, 5);
        Mockito.verify(mNode).marginPx(RIGHT, 6);
        Mockito.verify(mNode).marginPx(LEFT, 4);
        Mockito.verify(mNode).marginPercent(ALL, 10);
        Mockito.verify(mNode).marginPercent(VERTICAL, 12);
        Mockito.verify(mNode).marginPercent(RIGHT, 5);
        Mockito.verify(mNode).marginAuto(LEFT);
        Mockito.verify(mNode).marginAuto(TOP);
        Mockito.verify(mNode).marginAuto(RIGHT);
        Mockito.verify(mNode).marginAuto(BOTTOM);
        Mockito.verify(mNode).paddingPx(ALL, 1);
        Mockito.verify(mNode).paddingPx(RIGHT, 2);
        Mockito.verify(mNode).paddingPx(LEFT, 3);
        Mockito.verify(mNode).paddingPercent(VERTICAL, 7);
        Mockito.verify(mNode).paddingPercent(RIGHT, 6);
        Mockito.verify(mNode).paddingPercent(ALL, 5);
        Mockito.verify(mNode).border(ArgumentMatchers.any(Border.class));
        Mockito.verify(mNode).positionPx(ALL, 11);
        Mockito.verify(mNode).positionPx(RIGHT, 12);
        Mockito.verify(mNode).positionPx(LEFT, 13);
        Mockito.verify(mNode).positionPercent(VERTICAL, 17);
        Mockito.verify(mNode).positionPercent(RIGHT, 16);
        Mockito.verify(mNode).positionPercent(ALL, 15);
        Mockito.verify(mNode).widthPx(5);
        Mockito.verify(mNode).widthPercent(50);
        Mockito.verify(mNode).minWidthPx(15);
        Mockito.verify(mNode).minWidthPercent(100);
        Mockito.verify(mNode).maxWidthPx(25);
        Mockito.verify(mNode).maxWidthPercent(26);
        Mockito.verify(mNode).heightPx(30);
        Mockito.verify(mNode).heightPercent(31);
        Mockito.verify(mNode).minHeightPx(32);
        Mockito.verify(mNode).minHeightPercent(33);
        Mockito.verify(mNode).maxHeightPx(34);
        Mockito.verify(mNode).maxHeightPercent(35);
        Mockito.verify(mNode).aspectRatio(20);
        Mockito.verify(mNode).touchExpansionPx(RIGHT, 22);
        Mockito.verify(mNode).touchExpansionPx(LEFT, 23);
        Mockito.verify(mNode).touchExpansionPx(ALL, 21);
        Mockito.verify(mNode).background(bgRef);
        Mockito.verify(mNode).foreground(foreground);
        Mockito.verify(mNode).wrapInView();
        Mockito.verify(mNodeInfo).setClickHandler(clickHandler);
        Mockito.verify(mNodeInfo).setFocusChangeHandler(focusChangedHandler);
        Mockito.verify(mNodeInfo).setLongClickHandler(longClickHandler);
        Mockito.verify(mNodeInfo).setTouchHandler(touchHandler);
        Mockito.verify(mNodeInfo).setInterceptTouchHandler(interceptTouchHandler);
        Mockito.verify(mNodeInfo).setFocusable(true);
        Mockito.verify(mNodeInfo).setSelected(false);
        Mockito.verify(mNodeInfo).setEnabled(false);
        Mockito.verify(mNode).visibleHeightRatio(55);
        Mockito.verify(mNode).visibleWidthRatio(56);
        Mockito.verify(mNode).visibleHandler(visibleHandler);
        Mockito.verify(mNode).focusedHandler(focusedHandler);
        Mockito.verify(mNode).unfocusedHandler(unfocusedHandler);
        Mockito.verify(mNode).fullImpressionHandler(fullImpressionHandler);
        Mockito.verify(mNode).invisibleHandler(invisibleHandler);
        Mockito.verify(mNode).visibilityChangedHandler(visibleRectChangedHandler);
        Mockito.verify(mNodeInfo).setContentDescription("test");
        Mockito.verify(mNodeInfo).setViewTag(viewTag);
        Mockito.verify(mNodeInfo).setViewTags(viewTags);
        Mockito.verify(mNodeInfo).setShadowElevation(60);
        Mockito.verify(mNodeInfo).setClipToOutline(false);
        Mockito.verify(mNode).transitionKey("transitionKey");
        Mockito.verify(mNode).testKey("testKey");
        Mockito.verify(mNodeInfo).setAccessibilityRole(BUTTON);
        Mockito.verify(mNodeInfo).setAccessibilityRoleDescription("Test Role Description");
        Mockito.verify(mNodeInfo).setDispatchPopulateAccessibilityEventHandler(dispatchPopulateAccessibilityEventHandler);
        Mockito.verify(mNodeInfo).setOnInitializeAccessibilityEventHandler(onInitializeAccessibilityEventHandler);
        Mockito.verify(mNodeInfo).setOnInitializeAccessibilityNodeInfoHandler(onInitializeAccessibilityNodeInfoHandler);
        Mockito.verify(mNodeInfo).setOnPopulateAccessibilityEventHandler(onPopulateAccessibilityEventHandler);
        Mockito.verify(mNodeInfo).setOnRequestSendAccessibilityEventHandler(onRequestSendAccessibilityEventHandler);
        Mockito.verify(mNodeInfo).setPerformAccessibilityActionHandler(performAccessibilityActionHandler);
        Mockito.verify(mNodeInfo).setSendAccessibilityEventHandler(sendAccessibilityEventHandler);
        Mockito.verify(mNodeInfo).setSendAccessibilityEventUncheckedHandler(sendAccessibilityEventUncheckedHandler);
        Mockito.verify(mNode).stateListAnimator(stateListAnimator);
    }

    @Test
    public void testSetScalePropsWrapsInView() {
        mCommonProps.scale(5);
        mCommonProps.copyInto(mComponentContext, mNode);
        Mockito.verify(mNodeInfo).setScale(5);
        Mockito.verify(mNode).wrapInView();
    }

    @Test
    public void testSetAlphaPropsWrapsInView() {
        mCommonProps.alpha(5);
        mCommonProps.copyInto(mComponentContext, mNode);
        Mockito.verify(mNodeInfo).setAlpha(5);
        Mockito.verify(mNode).wrapInView();
    }

    @Test
    public void testSetRotationPropsWrapsInView() {
        mCommonProps.rotation(5);
        mCommonProps.copyInto(mComponentContext, mNode);
        Mockito.verify(mNodeInfo).setRotation(5);
        Mockito.verify(mNode).wrapInView();
    }

    @Test
    public void testPaddingFromDrawable() {
        final InternalNode node = Mockito.spy(new InternalNode(mComponentContext));
        mCommonProps.background(DrawableReference.create(ComparableResDrawable.create(mComponentContext.getAndroidContext(), background_with_padding)));
        mCommonProps.copyInto(mComponentContext, node);
        Mockito.verify(node).paddingPx(LEFT, 48);
        Mockito.verify(node).paddingPx(TOP, 0);
        Mockito.verify(node).paddingPx(RIGHT, 0);
        Mockito.verify(node).paddingPx(BOTTOM, 0);
    }

    @Test
    public void testPaddingFromDrawableIsOverwritten() {
        final InternalNode node = Mockito.spy(new InternalNode(mComponentContext));
        mCommonProps.background(DrawableReference.create(ComparableResDrawable.create(mComponentContext.getAndroidContext(), background_with_padding)));
        mCommonProps.paddingPx(LEFT, 0);
        mCommonProps.paddingPx(TOP, 0);
        mCommonProps.paddingPx(RIGHT, 0);
        mCommonProps.paddingPx(BOTTOM, 0);
        mCommonProps.copyInto(mComponentContext, node);
        InOrder inOrder = Mockito.inOrder(node);
        inOrder.verify(node).paddingPx(LEFT, 48);
        inOrder.verify(node).paddingPx(LEFT, 0);
        Mockito.verify(node, Mockito.times(2)).paddingPx(TOP, 0);
        Mockito.verify(node, Mockito.times(2)).paddingPx(RIGHT, 0);
        Mockito.verify(node, Mockito.times(2)).paddingPx(BOTTOM, 0);
    }
}

