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


import Build.VERSION_CODES;
import MountItem.LAYOUT_FLAG_DISABLE_TOUCHABLE;
import RuntimeEnvironment.application;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;


/**
 * Tests {@link ComponentHost}
 */
@RunWith(ComponentsTestRunner.class)
public class ComponentHostTest {
    private Component mViewGroupHost;

    private ComponentHostTest.TestableComponentHost mHost;

    private Component mDrawableComponent;

    private Component mViewComponent;

    private ComponentContext mContext;

    @Test
    public void testParentHostMarker() {
        assertThat(getParentHostMarker()).isEqualTo(0);
        setParentHostMarker(1);
        assertThat(getParentHostMarker()).isEqualTo(1);
    }

    @Test
    public void testInvalidations() {
        assertThat(mHost.getInvalidationCount()).isEqualTo(0);
        assertThat(mHost.getInvalidationRect()).isNull();
        Drawable d1 = new ColorDrawable();
        d1.setBounds(0, 0, 1, 1);
        MountItem mountItem1 = mount(0, d1);
        assertThat(mHost.getInvalidationCount()).isEqualTo(1);
        assertThat(mHost.getInvalidationRect()).isEqualTo(d1.getBounds());
        Drawable d2 = new ColorDrawable();
        d2.setBounds(0, 0, 2, 2);
        MountItem mountItem2 = mount(1, d2);
        assertThat(mHost.getInvalidationCount()).isEqualTo(2);
        assertThat(mHost.getInvalidationRect()).isEqualTo(d2.getBounds());
        View v1 = new View(mContext.getAndroidContext());
        Rect v1Bounds = new Rect(0, 0, 10, 10);
        v1.measure(makeMeasureSpec(v1Bounds.width(), EXACTLY), makeMeasureSpec(v1Bounds.height(), EXACTLY));
        v1.layout(v1Bounds.left, v1Bounds.top, v1Bounds.right, v1Bounds.bottom);
        MountItem mountItem3 = mount(2, v1);
        assertThat(mHost.getInvalidationCount()).isEqualTo(3);
        assertThat(mHost.getInvalidationRect()).isEqualTo(v1Bounds);
        unmount(0, mountItem1);
        assertThat(mHost.getInvalidationCount()).isEqualTo(4);
        assertThat(mHost.getInvalidationRect()).isEqualTo(d1.getBounds());
        unmount(1, mountItem2);
        assertThat(mHost.getInvalidationCount()).isEqualTo(5);
        assertThat(mHost.getInvalidationRect()).isEqualTo(d2.getBounds());
        unmount(2, mountItem3);
        assertThat(mHost.getInvalidationCount()).isEqualTo(6);
        assertThat(mHost.getInvalidationRect()).isEqualTo(v1Bounds);
    }

    @Test
    public void testCallbacks() {
        Drawable d = new ColorDrawable();
        assertThat(d.getCallback()).isNull();
        MountItem mountItem = mount(0, d);
        assertThat(d.getCallback()).isEqualTo(mHost);
        unmount(0, mountItem);
        assertThat(d.getCallback()).isNull();
    }

    @Test
    public void testGetMountItemCount() {
        assertThat(getMountItemCount()).isEqualTo(0);
        MountItem mountItem1 = mount(0, new ColorDrawable());
        assertThat(getMountItemCount()).isEqualTo(1);
        mount(1, new ColorDrawable());
        assertThat(getMountItemCount()).isEqualTo(2);
        MountItem mountItem3 = mount(2, new View(mContext.getAndroidContext()));
        assertThat(getMountItemCount()).isEqualTo(3);
        unmount(0, mountItem1);
        assertThat(getMountItemCount()).isEqualTo(2);
        MountItem mountItem4 = mount(1, new ColorDrawable());
        assertThat(getMountItemCount()).isEqualTo(2);
        unmount(2, mountItem3);
        assertThat(getMountItemCount()).isEqualTo(1);
        unmount(1, mountItem4);
        assertThat(getMountItemCount()).isEqualTo(0);
    }

    @Test
    public void testGetMountItemAt() {
        assertThat(getMountItemAt(0)).isNull();
        assertThat(getMountItemAt(1)).isNull();
        assertThat(getMountItemAt(2)).isNull();
        MountItem mountItem1 = mount(0, new ColorDrawable());
        MountItem mountItem2 = mount(1, new View(mContext.getAndroidContext()));
        MountItem mountItem3 = mount(5, new ColorDrawable());
        assertThat(getMountItemAt(0)).isEqualTo(mountItem1);
        assertThat(getMountItemAt(1)).isEqualTo(mountItem2);
        assertThat(getMountItemAt(2)).isEqualTo(mountItem3);
        unmount(1, mountItem2);
        assertThat(getMountItemAt(0)).isEqualTo(mountItem1);
        assertThat(getMountItemAt(1)).isEqualTo(mountItem3);
        unmount(0, mountItem1);
        assertThat(getMountItemAt(0)).isEqualTo(mountItem3);
    }

    @Test
    public void testOnTouchWithTouchables() {
        assertThat(getMountItemAt(0)).isNull();
        assertThat(getMountItemAt(1)).isNull();
        assertThat(getMountItemAt(2)).isNull();
        // Touchables are traversed backwards as drawing order.
        // The n.4 is the first parsed, and returning false means the n.2 will be parsed too.
        ComponentHostTest.TouchableDrawable touchableDrawableOnItem2 = Mockito.spy(new ComponentHostTest.TouchableDrawable());
        ComponentHostTest.TouchableDrawable touchableDrawableOnItem4 = Mockito.spy(new ComponentHostTest.TouchableDrawable());
        Mockito.when(touchableDrawableOnItem2.shouldHandleTouchEvent(ArgumentMatchers.any(MotionEvent.class))).thenReturn(true);
        Mockito.when(touchableDrawableOnItem4.shouldHandleTouchEvent(ArgumentMatchers.any(MotionEvent.class))).thenReturn(false);
        MountItem mountItem1 = mount(0, new ColorDrawable());
        MountItem mountItem2 = mount(1, touchableDrawableOnItem2);
        MountItem mountItem3 = mount(2, new View(mContext.getAndroidContext()));
        MountItem mountItem4 = mount(5, touchableDrawableOnItem4);
        assertThat(getMountItemAt(0)).isEqualTo(mountItem1);
        assertThat(getMountItemAt(1)).isEqualTo(mountItem2);
        assertThat(getMountItemAt(2)).isEqualTo(mountItem3);
        assertThat(getMountItemAt(3)).isEqualTo(mountItem4);
        onTouchEvent(Mockito.mock(MotionEvent.class));
        Mockito.verify(touchableDrawableOnItem4, Mockito.times(1)).shouldHandleTouchEvent(ArgumentMatchers.any(MotionEvent.class));
        Mockito.verify(touchableDrawableOnItem4, Mockito.never()).onTouchEvent(ArgumentMatchers.any(MotionEvent.class), ArgumentMatchers.any(View.class));
        Mockito.verify(touchableDrawableOnItem2, Mockito.times(1)).shouldHandleTouchEvent(ArgumentMatchers.any(MotionEvent.class));
        Mockito.verify(touchableDrawableOnItem2, Mockito.times(1)).onTouchEvent(ArgumentMatchers.any(MotionEvent.class), ArgumentMatchers.any(View.class));
    }

    @Test
    public void testOnTouchWithDisableTouchables() {
        assertThat(getMountItemAt(0)).isNull();
        assertThat(getMountItemAt(1)).isNull();
        assertThat(getMountItemAt(2)).isNull();
        MountItem mountItem1 = mount(0, new ColorDrawable());
        MountItem mountItem2 = mount(1, new ComponentHostTest.TouchableDrawable(), LAYOUT_FLAG_DISABLE_TOUCHABLE);
        MountItem mountItem3 = mount(2, new View(mContext.getAndroidContext()));
        MountItem mountItem4 = mount(4, Mockito.spy(new ComponentHostTest.TouchableDrawable()));
        MountItem mountItem5 = mount(5, new ComponentHostTest.TouchableDrawable(), LAYOUT_FLAG_DISABLE_TOUCHABLE);
        MountItem mountItem6 = mount(7, new View(mContext.getAndroidContext()));
        MountItem mountItem7 = mount(8, new ComponentHostTest.TouchableDrawable(), LAYOUT_FLAG_DISABLE_TOUCHABLE);
        assertThat(getMountItemAt(0)).isEqualTo(mountItem1);
        assertThat(getMountItemAt(1)).isEqualTo(mountItem2);
        assertThat(getMountItemAt(2)).isEqualTo(mountItem3);
        assertThat(getMountItemAt(3)).isEqualTo(mountItem4);
        assertThat(getMountItemAt(4)).isEqualTo(mountItem5);
        assertThat(getMountItemAt(5)).isEqualTo(mountItem6);
        assertThat(getMountItemAt(6)).isEqualTo(mountItem7);
        onTouchEvent(Mockito.mock(MotionEvent.class));
        ComponentHostTest.TouchableDrawable touchableDrawable = ((ComponentHostTest.TouchableDrawable) (mountItem4.getContent()));
        Mockito.verify(touchableDrawable, Mockito.times(1)).shouldHandleTouchEvent(ArgumentMatchers.any(MotionEvent.class));
        Mockito.verify(touchableDrawable, Mockito.times(1)).onTouchEvent(ArgumentMatchers.any(MotionEvent.class), ArgumentMatchers.any(View.class));
    }

    @Test
    public void testMoveItem() {
        MountItem mountItem1 = mount(1, new ColorDrawable());
        MountItem mountItem2 = mount(2, new View(mContext.getAndroidContext()));
        assertThat(getMountItemCount()).isEqualTo(2);
        assertThat(getMountItemAt(0)).isEqualTo(mountItem1);
        assertThat(getMountItemAt(1)).isEqualTo(mountItem2);
        mHost.moveItem(mountItem2, 2, 0);
        assertThat(getMountItemCount()).isEqualTo(2);
        assertThat(getMountItemAt(0)).isEqualTo(mountItem2);
        assertThat(getMountItemAt(1)).isEqualTo(mountItem1);
        mHost.moveItem(mountItem2, 0, 1);
        assertThat(getMountItemCount()).isEqualTo(1);
        assertThat(getMountItemAt(0)).isEqualTo(mountItem2);
        mHost.moveItem(mountItem2, 1, 0);
        assertThat(getMountItemCount()).isEqualTo(2);
        assertThat(getMountItemAt(0)).isEqualTo(mountItem1);
        assertThat(getMountItemAt(1)).isEqualTo(mountItem2);
    }

    @Test
    public void testMoveItemWithoutTouchables() throws Exception {
        Drawable d1 = new ColorDrawable(BLACK);
        MountItem mountItem1 = mount(1, d1);
        Drawable d2 = new ColorDrawable(BLACK);
        MountItem mountItem2 = mount(2, d2);
        assertThat(getDrawableItemsSize()).isEqualTo(2);
        assertThat(getDrawableMountItemAt(0)).isEqualTo(mountItem1);
        assertThat(getDrawableMountItemAt(1)).isEqualTo(mountItem2);
        mHost.moveItem(mountItem2, 2, 0);
        // There are no Touchable Drawables so this call should return false and not crash.
        assertThat(mHost.onTouchEvent(obtain(0, 0, 0, 0, 0, 0))).isFalse();
    }

    @Test
    public void testDrawableStateChangedOnDrawables() {
        Drawable d1 = Mockito.mock(ColorDrawable.class);
        Mockito.when(d1.getBounds()).thenReturn(new Rect());
        Mockito.when(d1.isStateful()).thenReturn(false);
        MountItem mountItem1 = mount(0, d1);
        Mockito.verify(d1, Mockito.never()).setState(ArgumentMatchers.any(int[].class));
        unmount(0, mountItem1);
        Drawable d2 = Mockito.mock(ColorDrawable.class);
        Mockito.when(d2.getBounds()).thenReturn(new Rect());
        Mockito.when(d2.isStateful()).thenReturn(true);
        mount(0, d2, MountItem.LAYOUT_FLAG_DUPLICATE_PARENT_STATE);
        Mockito.verify(d2, Mockito.times(1)).setState(ArgumentMatchers.eq(getDrawableState()));
        setSelected(true);
        Mockito.verify(d2, Mockito.times(1)).setState(ArgumentMatchers.eq(getDrawableState()));
    }

    @Test
    public void testMoveTouchExpansionItem() {
        View view = Mockito.mock(View.class);
        Mockito.when(view.getContext()).thenReturn(application);
        MountItem mountItem = mountTouchExpansionItem(0, view);
        mHost.moveItem(mountItem, 0, 1);
        unmount(1, mountItem);
    }

    @Test
    public void testTouchExpansionItemShouldAddTouchDelegate() {
        View view = Mockito.mock(View.class);
        Mockito.when(view.getContext()).thenReturn(application);
        MountItem mountItem = mountTouchExpansionItem(0, view);
        assertThat(getTouchExpansionDelegate()).isNotNull();
        unmount(0, mountItem);
    }

    @Test
    public void testRecursiveTouchExpansionItemShouldNotAddTouchDelegate() {
        MountItem mountItem = mountTouchExpansionItem(0, mHost);
        assertThat(getTouchExpansionDelegate()).isNull();
        unmount(0, mountItem);
    }

    @Test
    public void testRecursiveTouchExpansionItemSecondShouldNotCrash() {
        View view = Mockito.mock(View.class);
        Mockito.when(view.getContext()).thenReturn(application);
        MountItem mountItem1 = mountTouchExpansionItem(0, view);
        assertThat(getTouchExpansionDelegate()).isNotNull();
        MountItem mountItem2 = mountTouchExpansionItem(1, mHost);
        assertThat(getTouchExpansionDelegate()).isNotNull();
        unmount(1, mountItem2);
        assertThat(getTouchExpansionDelegate()).isNotNull();
        unmount(0, mountItem1);
    }

    @Test
    public void testDuplicateParentStateOnViews() {
        View v1 = Mockito.mock(View.class);
        mount(0, v1);
        View v2 = Mockito.mock(View.class);
        mount(1, v2, MountItem.LAYOUT_FLAG_DUPLICATE_PARENT_STATE);
        Mockito.verify(v1, Mockito.times(1)).setDuplicateParentStateEnabled(ArgumentMatchers.eq(false));
        Mockito.verify(v2, Mockito.times(1)).setDuplicateParentStateEnabled(ArgumentMatchers.eq(true));
    }

    @Test
    public void testJumpDrawablesToCurrentState() {
        jumpDrawablesToCurrentState();
        Drawable d1 = Mockito.mock(ColorDrawable.class);
        Mockito.when(d1.getBounds()).thenReturn(new Rect());
        mount(0, d1);
        Drawable d2 = Mockito.mock(ColorDrawable.class);
        Mockito.when(d2.getBounds()).thenReturn(new Rect());
        mount(1, d2);
        View v1 = Mockito.mock(View.class);
        mount(2, v1);
        jumpDrawablesToCurrentState();
        Mockito.verify(d1, Mockito.times(1)).jumpToCurrentState();
        Mockito.verify(d2, Mockito.times(1)).jumpToCurrentState();
    }

    @Test
    public void testSetVisibility() {
        Drawable d1 = Mockito.mock(ColorDrawable.class);
        Mockito.when(d1.getBounds()).thenReturn(new Rect());
        mount(0, d1);
        Drawable d2 = Mockito.mock(ColorDrawable.class);
        Mockito.when(d2.getBounds()).thenReturn(new Rect());
        mount(1, d2);
        View v1 = Mockito.mock(View.class);
        mount(2, v1);
        mHost.setVisibility(GONE);
        mHost.setVisibility(INVISIBLE);
        mHost.setVisibility(VISIBLE);
        Mockito.verify(d1, Mockito.times(2)).setVisible(ArgumentMatchers.eq(true), ArgumentMatchers.eq(false));
        Mockito.verify(d1, Mockito.times(2)).setVisible(ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
        Mockito.verify(d2, Mockito.times(2)).setVisible(ArgumentMatchers.eq(true), ArgumentMatchers.eq(false));
        Mockito.verify(d2, Mockito.times(2)).setVisible(ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
        Mockito.verify(v1, Mockito.never()).setVisibility(ArgumentMatchers.anyInt());
    }

    @Test
    public void testGetDrawables() {
        Drawable d1 = new ColorDrawable();
        MountItem mountItem1 = mount(0, d1);
        Drawable d2 = new ColorDrawable();
        mount(1, d2);
        MountItem mountItem3 = mount(2, new View(mContext.getAndroidContext()));
        List<Drawable> drawables = getDrawables();
        assertThat(drawables).hasSize(2);
        assertThat(drawables.get(0)).isEqualTo(d1);
        assertThat(drawables.get(1)).isEqualTo(d2);
        unmount(0, mountItem1);
        drawables = mHost.getDrawables();
        assertThat(drawables).hasSize(1);
        assertThat(drawables.get(0)).isEqualTo(d2);
        unmount(2, mountItem3);
        drawables = mHost.getDrawables();
        assertThat(drawables).hasSize(1);
        assertThat(drawables.get(0)).isEqualTo(d2);
    }

    @Test
    public void testViewTag() {
        assertThat(mHost.getTag()).isNull();
        Object tag = new Object();
        mHost.setViewTag(tag);
        assertThat(mHost.getTag()).isEqualTo(tag);
        setViewTag(null);
        assertThat(mHost.getTag()).isNull();
    }

    @Test
    public void testViewTags() {
        assertThat(getTag(1)).isNull();
        assertThat(getTag(2)).isNull();
        Object value1 = new Object();
        Object value2 = new Object();
        SparseArray<Object> viewTags = new SparseArray();
        viewTags.put(1, value1);
        viewTags.put(2, value2);
        mHost.setViewTags(viewTags);
        assertThat(getTag(1)).isEqualTo(value1);
        assertThat(getTag(2)).isEqualTo(value2);
        setViewTags(null);
        assertThat(getTag(1)).isNull();
        assertThat(getTag(2)).isNull();
    }

    @Test
    public void testComponentClickListener() {
        assertThat(getComponentClickListener()).isNull();
        ComponentClickListener listener = new ComponentClickListener();
        mHost.setComponentClickListener(listener);
        assertThat(getComponentClickListener()).isEqualTo(listener);
        setComponentClickListener(null);
        assertThat(getComponentClickListener()).isNull();
    }

    @Test
    public void testComponentLongClickListener() {
        assertThat(getComponentLongClickListener()).isNull();
        ComponentLongClickListener listener = new ComponentLongClickListener();
        mHost.setComponentLongClickListener(listener);
        assertThat(getComponentLongClickListener()).isEqualTo(listener);
        setComponentLongClickListener(null);
        assertThat(getComponentLongClickListener()).isNull();
    }

    @Test
    public void testComponentFocusChangeListener() {
        assertThat(getComponentFocusChangeListener()).isNull();
        ComponentFocusChangeListener listener = new ComponentFocusChangeListener();
        mHost.setComponentFocusChangeListener(listener);
        assertThat(getComponentFocusChangeListener()).isEqualTo(listener);
        setComponentFocusChangeListener(null);
        assertThat(getComponentFocusChangeListener()).isNull();
    }

    @Test
    public void testComponentTouchListener() {
        assertThat(getComponentTouchListener()).isNull();
        ComponentTouchListener listener = new ComponentTouchListener();
        mHost.setComponentTouchListener(listener);
        assertThat(getComponentTouchListener()).isEqualTo(listener);
        setComponentTouchListener(null);
        assertThat(getComponentTouchListener()).isNull();
    }

    @Test
    public void testSuppressInvalidations() {
        layout(0, 0, 100, 100);
        mHost.invalidate();
        assertThat(mHost.getInvalidationRect()).isEqualTo(new Rect(0, 0, 100, 100));
        suppressInvalidations(true);
        mHost.invalidate();
        mHost.invalidate(0, 0, 5, 5);
        suppressInvalidations(false);
        assertThat(mHost.getInvalidationRect()).isEqualTo(new Rect(0, 0, 100, 100));
    }

    @Test
    public void testSuppressInvalidationsWithCoordinates() {
        layout(0, 0, 100, 100);
        mHost.invalidate(0, 0, 20, 20);
        assertThat(mHost.getInvalidationRect()).isEqualTo(new Rect(0, 0, 20, 20));
        suppressInvalidations(true);
        mHost.invalidate(0, 0, 10, 10);
        mHost.invalidate(0, 0, 5, 5);
        suppressInvalidations(false);
        assertThat(mHost.getInvalidationRect()).isEqualTo(new Rect(0, 0, 100, 100));
    }

    @Test
    public void testSuppressInvalidationsWithRect() {
        layout(0, 0, 100, 100);
        mHost.invalidate(new Rect(0, 0, 20, 20));
        assertThat(mHost.getInvalidationRect()).isEqualTo(new Rect(0, 0, 20, 20));
        suppressInvalidations(true);
        mHost.invalidate(new Rect(0, 0, 10, 10));
        mHost.invalidate(new Rect(0, 0, 5, 5));
        suppressInvalidations(false);
        assertThat(mHost.getInvalidationRect()).isEqualTo(new Rect(0, 0, 100, 100));
    }

    @Test
    public void testSuppressRequestFocus() {
        requestFocus();
        assertThat(mHost.getFocusRequestCount()).isEqualTo(1);
        suppressInvalidations(true);
        requestFocus();
        assertThat(mHost.getFocusRequestCount()).isEqualTo(1);
        suppressInvalidations(false);
        assertThat(mHost.getFocusRequestCount()).isEqualTo(2);
    }

    @Test
    public void testGetContentDescriptions() {
        CharSequence hostContentDescription = "hostContentDescription";
        setContentDescription(hostContentDescription);
        CharSequence drawableContentDescription = "drawableContentDescription";
        MountItem mountItem0 = mount(0, new ColorDrawable(), 0, drawableContentDescription);
        CharSequence viewContentDescription = "viewContentDescription";
        mount(1, Mockito.mock(View.class), 0, viewContentDescription);
        assertThat(getContentDescriptions()).contains(hostContentDescription);
        assertThat(getContentDescriptions()).contains(drawableContentDescription);
        assertThat(getContentDescriptions()).doesNotContain(viewContentDescription);
        unmount(0, mountItem0);
        assertThat(getContentDescriptions()).contains(hostContentDescription);
        assertThat(getContentDescriptions()).doesNotContain(drawableContentDescription);
        assertThat(getContentDescriptions()).doesNotContain(viewContentDescription);
    }

    @Test
    public void testGetChildDrawingOrder() {
        View v1 = new View(mContext.getAndroidContext());
        mount(2, v1);
        View v2 = new View(mContext.getAndroidContext());
        MountItem mountItem2 = mount(0, v2);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 0)).isEqualTo(1);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 1)).isEqualTo(0);
        View v3 = new ComponentHost(mContext);
        MountItem mountItem3 = mount(1, v3);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 0)).isEqualTo(1);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 1)).isEqualTo(2);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 2)).isEqualTo(0);
        mHost.unmount(1, mountItem3);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 0)).isEqualTo(1);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 1)).isEqualTo(0);
        mount(1, v3);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 0)).isEqualTo(1);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 1)).isEqualTo(2);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 2)).isEqualTo(0);
        mHost.unmount(0, mountItem2);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 0)).isEqualTo(1);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 1)).isEqualTo(0);
        mHost.moveItem(mountItem3, 1, 3);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 0)).isEqualTo(0);
        assertThat(mHost.getChildDrawingOrder(getChildCount(), 1)).isEqualTo(1);
    }

    @Config(sdk = VERSION_CODES.JELLY_BEAN)
    @Test
    public void testTemporaryChildClippingDisablingJB() {
        testTemporaryChildClippingDisabling();
    }

    @Config(sdk = VERSION_CODES.LOLLIPOP)
    @Test
    public void testTemporaryChildClippingDisablingLollipop() {
        testTemporaryChildClippingDisabling();
    }

    @Test
    public void testDisappearingItems() {
        View v1 = new View(mContext.getAndroidContext());
        mount(0, v1);
        Drawable d1 = new ColorDrawable(BLACK);
        MountItem mountItem1 = mount(1, d1);
        View v2 = new View(mContext.getAndroidContext());
        MountItem mountItem2 = mount(2, v2);
        Drawable d2 = new ColorDrawable(BLACK);
        mount(3, d2);
        assertThat(getMountItemCount()).isEqualTo(4);
        assertThat(getChildCount()).isEqualTo(2);
        assertThat(hasDisappearingItems()).isFalse();
        mHost.startUnmountDisappearingItem(1, mountItem1);
        assertThat(getMountItemCount()).isEqualTo(3);
        assertThat(getChildCount()).isEqualTo(2);
        assertThat(hasDisappearingItems()).isTrue();
        mHost.unmountDisappearingItem(mountItem1);
        assertThat(getMountItemCount()).isEqualTo(3);
        assertThat(getChildCount()).isEqualTo(2);
        assertThat(hasDisappearingItems()).isFalse();
        mHost.startUnmountDisappearingItem(2, mountItem2);
        assertThat(getMountItemCount()).isEqualTo(2);
        assertThat(getChildCount()).isEqualTo(2);
        assertThat(hasDisappearingItems()).isTrue();
        mHost.unmountDisappearingItem(mountItem2);
        assertThat(getMountItemCount()).isEqualTo(2);
        assertThat(getChildCount()).isEqualTo(1);
        assertThat(hasDisappearingItems()).isFalse();
    }

    @Test
    public void testDrawableItemsSize() throws Exception {
        assertThat(getDrawableItemsSize()).isEqualTo(0);
        assertThat(getDrawableItemsSize()).isEqualTo(0);
        Drawable d1 = new ColorDrawable(BLACK);
        MountItem m1 = mount(0, d1);
        assertThat(getDrawableItemsSize()).isEqualTo(1);
        Drawable d2 = new ColorDrawable(BLACK);
        mount(1, d2);
        assertThat(getDrawableItemsSize()).isEqualTo(2);
        unmount(0, m1);
        assertThat(getDrawableItemsSize()).isEqualTo(1);
        Drawable d3 = new ColorDrawable(BLACK);
        MountItem m3 = mount(1, d3);
        assertThat(getDrawableItemsSize()).isEqualTo(1);
        unmount(1, m3);
        assertThat(getDrawableItemsSize()).isEqualTo(0);
    }

    @Test
    public void testGetDrawableMountItem() throws Exception {
        Drawable d1 = new ColorDrawable(BLACK);
        MountItem mountItem1 = mount(0, d1);
        Drawable d2 = new ColorDrawable(BLACK);
        MountItem mountItem2 = mount(1, d2);
        Drawable d3 = new ColorDrawable(BLACK);
        MountItem mountItem3 = mount(5, d3);
        assertThat(getDrawableMountItemAt(0)).isEqualTo(mountItem1);
        assertThat(getDrawableMountItemAt(1)).isEqualTo(mountItem2);
        assertThat(getDrawableMountItemAt(2)).isEqualTo(mountItem3);
    }

    @Test
    public void testGetLinkedDrawableForAnimation() {
        Drawable d1 = new ColorDrawable();
        MountItem mountItem1 = mount(0, d1, MountItem.LAYOUT_FLAG_MATCH_HOST_BOUNDS);
        Drawable d2 = new ColorDrawable();
        MountItem mountItem2 = mount(1, d2);
        Drawable d3 = new ColorDrawable();
        MountItem mountItem3 = mount(2, d3, MountItem.LAYOUT_FLAG_MATCH_HOST_BOUNDS);
        List<Drawable> drawables = getLinkedDrawablesForAnimation();
        assertThat(drawables).hasSize(2);
        assertThat(drawables).contains(d1, d3);
        unmount(0, mountItem1);
        drawables = mHost.getLinkedDrawablesForAnimation();
        assertThat(drawables).hasSize(1);
        assertThat(drawables).contains(d3);
        unmount(1, mountItem2);
        drawables = mHost.getDrawables();
        assertThat(drawables).hasSize(1);
        assertThat(drawables).contains(d3);
    }

    private static class TestableComponentHost extends ComponentHost {
        private int mInvalidationCount = 0;

        private Rect mInvalidationRect = null;

        private int mFocusRequestCount = 0;

        public TestableComponentHost(ComponentContext context) {
            super(context);
        }

        public TestableComponentHost(Context context) {
            super(context);
        }

        @Override
        public void invalidate(Rect dirty) {
            super.invalidate(dirty);
            trackInvalidation(dirty.left, dirty.top, dirty.right, dirty.bottom);
        }

        @Override
        public void invalidate(int l, int t, int r, int b) {
            super.invalidate(l, t, r, b);
            trackInvalidation(l, t, r, b);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            trackInvalidation(0, 0, getWidth(), getHeight());
        }

        @Override
        public void onViewAdded(View child) {
            super.onViewAdded(child);
            trackInvalidation(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        }

        @Override
        public void onViewRemoved(View child) {
            super.onViewRemoved(child);
            trackInvalidation(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        }

        @Override
        public int getDescendantFocusability() {
            (mFocusRequestCount)++;
            return super.getDescendantFocusability();
        }

        int getFocusRequestCount() {
            return mFocusRequestCount;
        }

        int getInvalidationCount() {
            return mInvalidationCount;
        }

        Rect getInvalidationRect() {
            return mInvalidationRect;
        }

        private void trackInvalidation(int l, int t, int r, int b) {
            (mInvalidationCount)++;
            mInvalidationRect = new Rect();
            mInvalidationRect.set(l, t, r, b);
        }
    }

    private static class TouchableDrawable extends ColorDrawable implements Touchable {
        @Override
        public boolean onTouchEvent(MotionEvent event, View host) {
            return true;
        }

        @Override
        public boolean shouldHandleTouchEvent(MotionEvent event) {
            return true;
        }
    }
}

