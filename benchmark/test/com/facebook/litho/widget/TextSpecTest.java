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


import Color.BLACK;
import Color.GREEN;
import Color.RED;
import EventHandlerTestHelper.MockEventHandler;
import MotionEvent.ACTION_DOWN;
import MotionEvent.ACTION_MOVE;
import MotionEvent.ACTION_UP;
import Spannable.Factory;
import SynchronizedTypefaceHelper.SynchronizedLongSparseArray;
import SynchronizedTypefaceHelper.SynchronizedSparseArray;
import SynchronizedTypefaceHelper.SynchronizedTypefaceSparseArray;
import Typeface.DEFAULT;
import Typeface.DEFAULT_BOLD;
import View.MeasureSpec;
import View.MeasureSpec.UNSPECIFIED;
import android.R.attr;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.EventHandler;
import com.facebook.litho.LithoView;
import com.facebook.litho.testing.eventhandler.EventHandlerTestHelper;
import com.facebook.litho.testing.helper.ComponentTestHelper;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


/**
 * Tests {@link Text} component.
 */
@RunWith(ComponentsTestRunner.class)
public class TextSpecTest {
    private ComponentContext mContext;

    private static final int FULL_TEXT_WIDTH = 100;

    private static final int MINIMAL_TEXT_WIDTH = 95;

    @Test
    public void testTextWithoutClickableSpans() {
        TextDrawable drawable = getMountedDrawableForText("Some text.");
        assertThat(drawable.getClickableSpans()).isNull();
    }

    @Test
    public void testSpannableWithoutClickableSpans() {
        Spannable nonClickableText = Factory.getInstance().newSpannable("Some text.");
        TextDrawable drawable = getMountedDrawableForText(nonClickableText);
        assertThat(drawable.getClickableSpans()).isNotNull().hasSize(0);
    }

    @Test
    public void testSpannableWithClickableSpans() {
        Spannable clickableText = Factory.getInstance().newSpannable("Some text.");
        clickableText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }
        }, 0, 1, 0);
        TextDrawable drawable = getMountedDrawableForText(clickableText);
        assertThat(drawable.getClickableSpans()).isNotNull().hasSize(1);
    }

    @Test(expected = IllegalStateException.class)
    public void testTextIsRequired() throws Exception {
        Text.create(mContext).build();
    }

    @Test
    public void testMountableCharSequenceText() {
        MountableCharSequence mountableCharSequence = Mockito.mock(MountableCharSequence.class);
        TextDrawable drawable = getMountedDrawableForText(mountableCharSequence);
        Mockito.verify(mountableCharSequence).onMount(drawable);
    }

    @Test
    public void testTouchOffsetChangeHandlerFired() {
        final boolean[] eventFired = new boolean[]{ false };
        EventHandler<TextOffsetOnTouchEvent> eventHandler = EventHandlerTestHelper.createMockEventHandler(TextOffsetOnTouchEvent.class, new MockEventHandler<TextOffsetOnTouchEvent, Void>() {
            @Override
            public Void handleEvent(TextOffsetOnTouchEvent event) {
                eventFired[0] = true;
                return null;
            }
        });
        LithoView lithoView = ComponentTestHelper.mountComponent(mContext, Text.create(mContext).text("Some text").textOffsetOnTouchHandler(eventHandler).build());
        TextDrawable textDrawable = ((TextDrawable) (lithoView.getDrawables().get(0)));
        MotionEvent motionEvent = MotionEvent.obtain(0, 0, ACTION_DOWN, 0, 0, 0);
        boolean handled = textDrawable.onTouchEvent(motionEvent, lithoView);
        // We don't consume touch events from TextTouchOffsetChange event
        assertThat(handled).isFalse();
        assertThat(eventFired[0]).isTrue();
    }

    @Test
    public void testTouchOffsetChangeHandlerNotFired() {
        final boolean[] eventFired = new boolean[]{ false };
        EventHandler<TextOffsetOnTouchEvent> eventHandler = EventHandlerTestHelper.createMockEventHandler(TextOffsetOnTouchEvent.class, new MockEventHandler<TextOffsetOnTouchEvent, Void>() {
            @Override
            public Void handleEvent(TextOffsetOnTouchEvent event) {
                eventFired[0] = true;
                return null;
            }
        });
        LithoView lithoView = ComponentTestHelper.mountComponent(mContext, Text.create(mContext).text("Text2").textOffsetOnTouchHandler(eventHandler).build());
        TextDrawable textDrawable = ((TextDrawable) (lithoView.getDrawables().get(0)));
        MotionEvent actionUp = MotionEvent.obtain(0, 0, ACTION_UP, 0, 0, 0);
        boolean handledActionUp = textDrawable.onTouchEvent(actionUp, lithoView);
        assertThat(handledActionUp).isFalse();
        assertThat(eventFired[0]).isFalse();
        MotionEvent actionDown = MotionEvent.obtain(0, 0, ACTION_MOVE, 0, 0, 0);
        boolean handledActionMove = textDrawable.onTouchEvent(actionDown, lithoView);
        assertThat(handledActionMove).isFalse();
        assertThat(eventFired[0]).isFalse();
    }

    @Test
    public void testColorDefault() {
        TextDrawable drawable = getMountedDrawableForText("Some text");
        assertThat(drawable.getColor()).isEqualTo(BLACK);
    }

    @Test
    public void testColorOverride() {
        int[][] states = new int[][]{ new int[]{ 0 } };
        int[] colors = new int[]{ Color.GREEN };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        TextDrawable drawable = getMountedDrawableForTextWithColors("Some text", RED, colorStateList);
        assertThat(drawable.getColor()).isEqualTo(RED);
    }

    @Test
    public void testColor() {
        TextDrawable drawable = getMountedDrawableForTextWithColors("Some text", RED, null);
        assertThat(drawable.getColor()).isEqualTo(RED);
    }

    @Test
    public void testColorStateList() {
        int[][] states = new int[][]{ new int[]{ 0 } };
        int[] colors = new int[]{ Color.GREEN };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        TextDrawable drawable = getMountedDrawableForTextWithColors("Some text", 0, colorStateList);
        assertThat(drawable.getColor()).isEqualTo(GREEN);
    }

    @Test
    public void testColorStateListMultipleStates() {
        ColorStateList colorStateList = new ColorStateList(new int[][]{ new int[]{ -(attr.state_enabled) }// disabled state
        // disabled state
        // disabled state
        , new int[]{  } }, new int[]{ Color.RED, Color.GREEN });
        TextDrawable drawable = getMountedDrawableForTextWithColors("Some text", 0, colorStateList);
        // color should fallback to default state
        assertThat(drawable.getColor()).isEqualTo(GREEN);
    }

    @Test
    public void testSynchronizedTypefaceSparseArray() {
        SparseArray<Typeface> sparseArray = new SparseArray();
        sparseArray.put(1, DEFAULT);
        SynchronizedTypefaceHelper.SynchronizedTypefaceSparseArray synchronizedSparseArray = new SynchronizedTypefaceHelper.SynchronizedTypefaceSparseArray(sparseArray);
        synchronizedSparseArray.put(2, DEFAULT_BOLD);
        assertThat(synchronizedSparseArray.get(1)).isSameAs(DEFAULT);
        assertThat(synchronizedSparseArray.get(2)).isSameAs(DEFAULT_BOLD);
    }

    @Test
    public void testSynchronizedLongSparseArray() {
        SynchronizedTypefaceHelper.SynchronizedLongSparseArray synchronizedLongSparseArray = new SynchronizedTypefaceHelper.SynchronizedLongSparseArray(new Object(), 2);
        SparseArray<Typeface> sparseArray = new SparseArray();
        sparseArray.put(1, DEFAULT);
        synchronizedLongSparseArray.put(2, sparseArray);
        SparseArray<Typeface> gotSparseArray = synchronizedLongSparseArray.get(2);
        assertThat(gotSparseArray).isInstanceOf(SynchronizedTypefaceSparseArray.class);
        assertThat(gotSparseArray.get(1)).isSameAs(DEFAULT);
    }

    @Test
    public void testSynchronizedSparseArray() {
        SynchronizedTypefaceHelper.SynchronizedSparseArray synchronizedSparseArray = new SynchronizedTypefaceHelper.SynchronizedSparseArray(new Object(), 2);
        SparseArray<Typeface> sparseArray = new SparseArray();
        sparseArray.put(1, DEFAULT);
        synchronizedSparseArray.put(2, sparseArray);
        SparseArray<Typeface> gotSparseArray = synchronizedSparseArray.get(2);
        assertThat(gotSparseArray).isInstanceOf(SynchronizedTypefaceSparseArray.class);
        assertThat(gotSparseArray.get(1)).isSameAs(DEFAULT);
    }

    @Test
    public void testFullWidthText() {
        final Layout layout = TextSpecTest.setupWidthTestTextLayout();
        final int resolvedWidth = /* minimallyWide */
        /* minimallyWideThreshold */
        TextSpec.resolveWidth(MeasureSpec.makeMeasureSpec(0, UNSPECIFIED), layout, false, 0);
        Assert.assertEquals(resolvedWidth, TextSpecTest.FULL_TEXT_WIDTH);
    }

    @Test
    public void testMinimallyWideText() {
        final Layout layout = TextSpecTest.setupWidthTestTextLayout();
        final int resolvedWidth = /* minimallyWide */
        /* minimallyWideThreshold */
        TextSpec.resolveWidth(MeasureSpec.makeMeasureSpec(0, UNSPECIFIED), layout, true, (((TextSpecTest.FULL_TEXT_WIDTH) - (TextSpecTest.MINIMAL_TEXT_WIDTH)) - 1));
        Assert.assertEquals(resolvedWidth, TextSpecTest.MINIMAL_TEXT_WIDTH);
    }

    @Test
    public void testMinimallyWideThresholdText() {
        final Layout layout = TextSpecTest.setupWidthTestTextLayout();
        final int resolvedWidth = /* minimallyWide */
        /* minimallyWideThreshold */
        TextSpec.resolveWidth(MeasureSpec.makeMeasureSpec(0, UNSPECIFIED), layout, true, ((TextSpecTest.FULL_TEXT_WIDTH) - (TextSpecTest.MINIMAL_TEXT_WIDTH)));
        Assert.assertEquals(resolvedWidth, TextSpecTest.FULL_TEXT_WIDTH);
    }
}

