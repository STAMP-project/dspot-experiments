/**
 * Copyright (C) 2017-2018 Samuel Wall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.samuelwall.materialtaptargetprompt.extras;


import Gravity.END;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.ViewGroup;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import uk.co.samuelwall.materialtaptargetprompt.BuildConfig;
import uk.co.samuelwall.materialtaptargetprompt.UnitTestUtils;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class PromptTextUnitTest {
    @Test
    public void testPromptText_Blank() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(500, 500, 550, 550);
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        options.load(0);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new PromptText();
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(0, bounds.width(), 0);
        Assert.assertEquals(0, bounds.height(), 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(0, bounds.width(), 0);
        Assert.assertEquals(0, bounds.height(), 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(0, bounds.width(), 0);
        Assert.assertEquals(0, bounds.height(), 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_Top_Left() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(1036, 1876, 1080, 1920);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_Rtl_Top_Left() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(1036, 1876, 1080, 1920);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(400.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300, true);
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        Assert.assertEquals(100, promptText.mPrimaryTextLeftChange, 0);
        Assert.assertEquals(100, promptText.mSecondaryTextLeftChange, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        Assert.assertEquals(100, promptText.mPrimaryTextLeftChange, 0);
        Assert.assertEquals(100, promptText.mSecondaryTextLeftChange, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        Assert.assertEquals(100, promptText.mPrimaryTextLeftChange, 0);
        Assert.assertEquals(100, promptText.mSecondaryTextLeftChange, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_NoPrimaryText_Top_Left() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(1036, 1876, 1080, 1920);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1656, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1656, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1656, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_Top_Right() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(0, 1876, 44, 1920);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_Bottom_Left() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(1036, 0, 1080, 44);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_Bottom_Right() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(0, 0, 44, 44);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_ClipBounds_Top_Left() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(1036, 1876, 1080, 1920);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, true, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_ClipBounds_Top_Right() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(0, 1876, 44, 1920);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        options.setTextGravity(END);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, true, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(1436, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(1856, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_ClipBounds_Bottom_Left() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(1036, 0, 1080, 44);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, true, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(760, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(1060, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_ClipBounds_Bottom_Right() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(0, 0, 44, 44);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, true, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(64, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(484, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_Centre_Top_Left() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(948, 1788, 992, 1832);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(690, bounds.left, 0);
        Assert.assertEquals(1348, bounds.top, 0);
        Assert.assertEquals(990, bounds.right, 0);
        Assert.assertEquals(1768, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(690, bounds.left, 0);
        Assert.assertEquals(1348, bounds.top, 0);
        Assert.assertEquals(990, bounds.right, 0);
        Assert.assertEquals(1768, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(690, bounds.left, 0);
        Assert.assertEquals(1348, bounds.top, 0);
        Assert.assertEquals(990, bounds.right, 0);
        Assert.assertEquals(1768, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_Centre_Top_Right() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(88, 1788, 132, 1832);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(1348, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(1768, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(1348, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(1768, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(1348, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(1768, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_Centre_Bottom_Left() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(948, 88, 992, 132);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(690, bounds.left, 0);
        Assert.assertEquals(152, bounds.top, 0);
        Assert.assertEquals(990, bounds.right, 0);
        Assert.assertEquals(572, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(690, bounds.left, 0);
        Assert.assertEquals(152, bounds.top, 0);
        Assert.assertEquals(990, bounds.right, 0);
        Assert.assertEquals(572, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(690, bounds.left, 0);
        Assert.assertEquals(152, bounds.top, 0);
        Assert.assertEquals(990, bounds.right, 0);
        Assert.assertEquals(572, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_Centre_Bottom_Right() {
        final Rect clipBounds = new Rect(0, 0, 1080, 1920);
        final RectF focalBounds = new RectF(88, 88, 132, 132);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(300.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(300);
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(152, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(572, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(152, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(572, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(152, bounds.top, 0);
        Assert.assertEquals(320, bounds.right, 0);
        Assert.assertEquals(572, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(525, 525));
        promptText.draw(Mockito.mock(Canvas.class));
    }

    @Test
    public void testPromptText_Overflow_Bottom_Right() {
        final Rect clipBounds = new Rect(20, 0, 1080, 1920);
        final RectF focalBounds = new RectF(88, 88, 132, 132);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        final ViewGroup parentView = options.getResourceFinder().getPromptParentView();
        Mockito.when(parentView.getWidth()).thenReturn(1080);
        Mockito.when(parentView.getRight()).thenReturn(1080);
        Mockito.when(parentView.getLeft()).thenReturn(0);
        options.load((-1));
        options.setPrimaryText("Primary Text");
        options.setSecondaryText("Secondary Text");
        options.setMaxTextWidth(1080.0F);
        options.setTextSeparation(20.0F);
        options.setPromptFocal(Mockito.mock(PromptFocal.class));
        options.setFocalPadding(20.0F);
        options.setTextPadding(20.0F);
        Mockito.when(options.getPromptFocal().getBounds()).thenReturn(focalBounds);
        final PromptText promptText = new TestPromptText(1050);
        promptText.prepare(options, false, clipBounds);
        RectF bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(152, bounds.top, 0);
        Assert.assertEquals(1070, bounds.right, 0);
        Assert.assertEquals(572, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(10, 10));
        promptText.update(options, 0.5F, 0.5F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(152, bounds.top, 0);
        Assert.assertEquals(1070, bounds.right, 0);
        Assert.assertEquals(572, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(10, 10));
        promptText.update(options, 1.0F, 1.0F);
        bounds = promptText.getBounds();
        Assert.assertEquals(20, bounds.left, 0);
        Assert.assertEquals(152, bounds.top, 0);
        Assert.assertEquals(1070, bounds.right, 0);
        Assert.assertEquals(572, bounds.bottom, 0);
        TestCase.assertFalse(promptText.contains(10, 10));
        promptText.draw(Mockito.mock(Canvas.class));
    }
}

