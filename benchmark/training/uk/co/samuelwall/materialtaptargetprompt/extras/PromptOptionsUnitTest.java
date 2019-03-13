/**
 * Copyright (C) 2017 Samuel Wall
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


import Build.VERSION;
import Color.CYAN;
import Color.DKGRAY;
import Color.GREEN;
import Color.WHITE;
import Color.YELLOW;
import Gravity.END;
import Gravity.START;
import MaterialTapTargetPrompt.STATE_DISMISSED;
import MaterialTapTargetPrompt.STATE_FINISHED;
import MaterialTapTargetPrompt.STATE_FINISHING;
import MaterialTapTargetPrompt.STATE_FOCAL_PRESSED;
import MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED;
import MaterialTapTargetPrompt.STATE_REVEALED;
import MaterialTapTargetPrompt.STATE_REVEALING;
import PorterDuff.Mode.ADD;
import R.styleable.PromptView_mttp_target;
import Typeface.BOLD;
import Typeface.NORMAL;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.animation.Interpolator;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import uk.co.samuelwall.materialtaptargetprompt.BuildConfig;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.ResourceFinder;
import uk.co.samuelwall.materialtaptargetprompt.TestResourceFinder;
import uk.co.samuelwall.materialtaptargetprompt.UnitTestUtils;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class PromptOptionsUnitTest {
    @Test
    public void testPromptOptions_ResourceFinder() {
        final ResourceFinder resourceFinder = new uk.co.samuelwall.materialtaptargetprompt.ActivityResourceFinder(Robolectric.buildActivity(Activity.class).create().get());
        final PromptOptions options = new PromptOptions(resourceFinder);
        Assert.assertEquals(resourceFinder, options.getResourceFinder());
    }

    @Test
    public void testPromptOptions_TargetView() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final View view = Mockito.mock(View.class);
        Assert.assertEquals(options, options.setTarget(56, 24));
        Assert.assertEquals(options, options.setTarget(view));
        Assert.assertEquals(view, options.getTargetView());
        Assert.assertEquals(null, options.getTargetRenderView());
        Assert.assertNull(options.getTargetPosition());
        Assert.assertTrue(options.isTargetSet());
    }

    @Test
    public void testPromptOptions_TargetView_Resource() {
        @IdRes
        final int resourceId = 325436;
        final View view = Mockito.mock(View.class);
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        Mockito.when(options.getResourceFinder().findViewById(resourceId)).thenReturn(view);
        Assert.assertEquals(options, options.setTarget(56, 24));
        Assert.assertEquals(options, options.setTarget(resourceId));
        Assert.assertEquals(view, options.getTargetView());
        Assert.assertEquals(null, options.getTargetRenderView());
        Assert.assertNull(options.getTargetPosition());
        Assert.assertTrue(options.isTargetSet());
    }

    @Test
    public void testPromptOptions_TargetView_Resource_Null() {
        @IdRes
        final int resourceId = 325436;
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        Mockito.when(options.getResourceFinder().findViewById(resourceId)).thenReturn(null);
        Assert.assertEquals(options, options.setTarget(56, 24));
        Assert.assertEquals(options, options.setTarget(resourceId));
        Assert.assertEquals(null, options.getTargetView());
        Assert.assertEquals(null, options.getTargetRenderView());
        Assert.assertNull(options.getTargetPosition());
        Assert.assertFalse(options.isTargetSet());
    }

    @Test
    public void testPromptOptions_TargetView_Null() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setTarget(56, 24));
        Assert.assertEquals(options, options.setTarget(null));
        Assert.assertEquals(null, options.getTargetView());
        Assert.assertEquals(null, options.getTargetRenderView());
        Assert.assertNull(options.getTargetPosition());
        Assert.assertFalse(options.isTargetSet());
    }

    @Test
    public void testPromptOptions_TargetView_Load() {
        @IdRes
        final int resourceId = 325436;
        @StyleRes
        final int styleId = 436547;
        final View view = Mockito.mock(View.class);
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        TestResourceFinder testResourceFinder = ((TestResourceFinder) (options.getResourceFinder()));
        testResourceFinder.addFindByView(resourceId, view);
        final TypedArray typedArray = options.getResourceFinder().obtainStyledAttributes(styleId, new int[0]);
        Mockito.when(typedArray.getResourceId(PromptView_mttp_target, 0)).thenReturn(resourceId);
        options.load(styleId);
    }

    @Test
    public void testPromptOptions_TargetView_Load_Null() {
        @IdRes
        final int resourceId = 325436;
        @StyleRes
        final int styleId = 436547;
        final PromptOptions options = UnitTestUtils.createPromptOptionsWithTestResourceFinder();
        TestResourceFinder testResourceFinder = ((TestResourceFinder) (options.getResourceFinder()));
        testResourceFinder.addFindByView(resourceId, null);
        final TypedArray typedArray = options.getResourceFinder().obtainStyledAttributes(styleId, new int[0]);
        Mockito.when(typedArray.getResourceId(PromptView_mttp_target, 0)).thenReturn(resourceId);
        options.load(styleId);
    }

    @Test
    public void testPromptOptions_TargetPosition() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final View view = Mockito.mock(View.class);
        Assert.assertEquals(options, options.setTarget(view));
        Assert.assertEquals(options, options.setTarget(56, 24));
        Assert.assertEquals(56, options.getTargetPosition().x, 0);
        Assert.assertEquals(24, options.getTargetPosition().y, 0);
        Assert.assertNull(options.getTargetView());
        Assert.assertNull(options.getTargetRenderView());
        Assert.assertTrue(options.isTargetSet());
    }

    @Test
    public void testPromptOptions_PrimaryText_String() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final String text = "text";
        Assert.assertEquals(options, options.setPrimaryText(text));
        Assert.assertEquals(text, options.getPrimaryText());
    }

    @Test
    public void testPromptOptions_PrimaryText_CharSequence() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final CharSequence text = "text";
        Assert.assertEquals(options, options.setPrimaryText(text));
        Assert.assertEquals(text, options.getPrimaryText());
    }

    @Test
    public void testPromptOptions_PrimaryText_Resource() {
        final String text = "test";
        @StringRes
        final int resourceId = 235435;
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        Mockito.when(options.getResourceFinder().getString(resourceId)).thenReturn(text);
        Assert.assertEquals(options, options.setPrimaryText(resourceId));
        Assert.assertEquals(text, options.getPrimaryText());
    }

    @Test
    public void testPromptOptions_PrimaryTextSize() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setPrimaryTextSize(15.0F));
        Assert.assertEquals(15.0F, options.getPrimaryTextSize(), 1);
    }

    @Test
    public void testPromptOptions_PrimaryTextSize_Resource() {
        @DimenRes
        final int resourceId = 324356;
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        final Resources resources = Mockito.mock(Resources.class);
        Mockito.when(options.getResourceFinder().getResources()).thenReturn(resources);
        Mockito.when(resources.getDimension(resourceId)).thenReturn(14.0F);
        Assert.assertEquals(options, options.setPrimaryTextSize(resourceId));
        Assert.assertEquals(14.0F, options.getPrimaryTextSize(), 1);
    }

    @Test
    public void testPromptOptions_SecondaryText_String() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final String text = "text";
        Assert.assertEquals(options, options.setSecondaryText(text));
        Assert.assertEquals(text, options.getSecondaryText());
    }

    @Test
    public void testPromptOptions_SecondaryText_CharSequence() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final CharSequence text = "text";
        Assert.assertEquals(options, options.setSecondaryText(text));
        Assert.assertEquals(text, options.getSecondaryText());
    }

    @Test
    public void testPromptOptions_SecondaryText_Resource() {
        final String text = "test";
        @StringRes
        final int resourceId = 245686;
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        Mockito.when(options.getResourceFinder().getString(resourceId)).thenReturn(text);
        Assert.assertEquals(options, options.setSecondaryText(resourceId));
        Assert.assertEquals(text, options.getSecondaryText());
    }

    @Test
    public void testPromptOptions_SecondaryTextSize() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setSecondaryTextSize(15.0F));
        Assert.assertEquals(15.0F, options.getSecondaryTextSize(), 1);
    }

    @Test
    public void testPromptOptions_SecondaryTextSize_Resource() {
        @DimenRes
        final int resourceId = 325436;
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        final Resources resources = Mockito.mock(Resources.class);
        Mockito.when(options.getResourceFinder().getResources()).thenReturn(resources);
        Mockito.when(resources.getDimension(resourceId)).thenReturn(14.0F);
        Assert.assertEquals(options, options.setSecondaryTextSize(resourceId));
        Assert.assertEquals(14.0F, options.getSecondaryTextSize(), 1);
    }

    @Test
    public void testPromptOptions_BackButtonDismiss_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertTrue(options.getBackButtonDismissEnabled());
    }

    @Test
    public void testPromptOptions_BackButtonDismiss_Enabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setBackButtonDismissEnabled(true));
        Assert.assertTrue(options.getBackButtonDismissEnabled());
    }

    @Test
    public void testPromptOptions_BackButtonDismiss_Disabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setBackButtonDismissEnabled(false));
        Assert.assertFalse(options.getBackButtonDismissEnabled());
    }

    @Test
    public void testPromptOptions_MaxTextWidth() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setMaxTextWidth(452.0F));
        Assert.assertEquals(452.0F, options.getMaxTextWidth(), 0);
    }

    @Test
    public void testPromptOptions_MaxTextWidth_Resource() {
        @DimenRes
        final int resourceId = 325436;
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        final Resources resources = Mockito.mock(Resources.class);
        Mockito.when(options.getResourceFinder().getResources()).thenReturn(resources);
        Mockito.when(resources.getDimension(resourceId)).thenReturn(235.0F);
        Assert.assertEquals(options, options.setMaxTextWidth(resourceId));
        Assert.assertEquals(235.0F, options.getMaxTextWidth(), 0);
    }

    @Test
    public void testPromptOptions_AutoDismiss_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertTrue(options.getAutoDismiss());
    }

    @Test
    public void testPromptOptions_AutoDismiss_Disabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setAutoDismiss(false));
        Assert.assertFalse(options.getAutoDismiss());
    }

    @Test
    public void testPromptOptions_AutoDismiss_Enabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setAutoDismiss(true));
        Assert.assertTrue(options.getAutoDismiss());
    }

    @Test
    public void testPromptOptions_AutoFinish_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertTrue(options.getAutoFinish());
    }

    @Test
    public void testPromptOptions_AutoFinish_Disabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setAutoFinish(false));
        Assert.assertFalse(options.getAutoFinish());
    }

    @Test
    public void testPromptOptions_AutoFinish_Enabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setAutoFinish(true));
        Assert.assertTrue(options.getAutoFinish());
    }

    @Test
    public void testPromptOptions_CaptureTouchEventOnFocal_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertFalse(options.getCaptureTouchEventOnFocal());
    }

    @Test
    public void testPromptOptions_CaptureTouchEventOnFocal_Disabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setCaptureTouchEventOnFocal(false));
        Assert.assertFalse(options.getCaptureTouchEventOnFocal());
    }

    @Test
    public void testPromptOptions_CaptureTouchEventOnFocal_Enabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setCaptureTouchEventOnFocal(true));
        Assert.assertTrue(options.getCaptureTouchEventOnFocal());
    }

    @Test
    public void testPromptOptions_CaptureTouchEventOutsidePrompt_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertFalse(options.getCaptureTouchEventOutsidePrompt());
    }

    @Test
    public void testPromptOptions_CaptureTouchEventOutsidePrompt_Disabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setCaptureTouchEventOutsidePrompt(false));
        Assert.assertFalse(options.getCaptureTouchEventOutsidePrompt());
    }

    @Test
    public void testPromptOptions_CaptureTouchEventOutsidePrompt_Enabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setCaptureTouchEventOutsidePrompt(true));
        Assert.assertTrue(options.getCaptureTouchEventOutsidePrompt());
    }

    @Test
    public void testPromptOptions_PromptText_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        TestCase.assertNotNull(options.getPromptText());
    }

    @Test
    public void testPromptOptions_PromptText_Custom() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final PromptText text = new PromptText();
        Assert.assertEquals(options, options.setPromptText(text));
        Assert.assertEquals(text, options.getPromptText());
    }

    @Test
    public void testPromptOptions_PromptFocal_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        TestCase.assertNotNull(options.getPromptFocal());
    }

    @Test
    public void testPromptOptions_PromptFocal_Custom() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final PromptFocal promptFocal = new RectanglePromptFocal();
        Assert.assertEquals(options, options.setPromptFocal(promptFocal));
        Assert.assertEquals(promptFocal, options.getPromptFocal());
        options.setPrimaryText("Primary Text");
        options.setTarget(Mockito.mock(View.class));
        options.create();
    }

    @Test
    public void testPromptOptions_PromptBackground_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        TestCase.assertNotNull(options.getPromptBackground());
    }

    @Test
    public void testPromptOptions_PromptBackground_Custom() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final PromptBackground promptBackground = new RectanglePromptBackground();
        Assert.assertEquals(options, options.setPromptBackground(promptBackground));
        Assert.assertEquals(promptBackground, options.getPromptBackground());
    }

    @Test
    public void testPromptOptions_FocalRadius_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(44, options.getFocalRadius(), 0);
    }

    @Test
    public void testPromptOptions_FocalRadius() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setFocalRadius(436.0F));
        Assert.assertEquals(436.0F, options.getFocalRadius(), 0);
    }

    @Test
    public void testPromptOptions_FocalRadius_Resource() {
        @DimenRes
        final int resourceId = 254346;
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        final Resources resources = Mockito.mock(Resources.class);
        Mockito.when(options.getResourceFinder().getResources()).thenReturn(resources);
        Mockito.when(resources.getDimension(resourceId)).thenReturn(223.0F);
        Assert.assertEquals(options, options.setFocalRadius(resourceId));
        Assert.assertEquals(223.0F, options.getFocalRadius(), 0);
    }

    @Test
    public void testPromptOptions_ClipToView() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final View view = Mockito.mock(View.class);
        Assert.assertEquals(options, options.setClipToView(view));
        Assert.assertEquals(view, options.getClipToView());
    }

    @Test
    public void testPromptOptions_TargetRenderView() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final View view = Mockito.mock(View.class);
        Assert.assertEquals(options, options.setTargetRenderView(view));
        Assert.assertEquals(view, options.getTargetRenderView());
    }

    @Test
    public void testPromptOptions_PrimaryTextGravity_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(START, options.getPrimaryTextGravity());
    }

    @Test
    public void testPromptOptions_PrimaryTextGravity() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setPrimaryTextGravity(END));
        Assert.assertEquals(END, options.getPrimaryTextGravity());
    }

    @Test
    public void testPromptOptions_SecondaryTextGravity_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(START, options.getSecondaryTextGravity());
    }

    @Test
    public void testPromptOptions_SecondaryTextGravity() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setSecondaryTextGravity(END));
        Assert.assertEquals(END, options.getSecondaryTextGravity());
    }

    @Test
    public void testPromptOptions_TextGravity() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setTextGravity(END));
        Assert.assertEquals(END, options.getPrimaryTextGravity());
        Assert.assertEquals(END, options.getSecondaryTextGravity());
    }

    @Test
    public void testPromptOptions_PrimaryTextColour() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setPrimaryTextColour(DKGRAY));
        Assert.assertEquals(DKGRAY, options.getPrimaryTextColour());
    }

    @Test
    public void testPromptOptions_PrimaryTextColour_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(WHITE, options.getPrimaryTextColour());
    }

    @Test
    public void testPromptOptions_SecondaryTextColour() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setSecondaryTextColour(YELLOW));
        Assert.assertEquals(YELLOW, options.getSecondaryTextColour());
    }

    @Test
    public void testPromptOptions_SecondaryTextColour_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(Color.argb(179, 255, 255, 255), options.getSecondaryTextColour());
    }

    @Test
    public void testPromptOptions_PrimaryTextTypefaceStyle() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final Typeface typeface = Mockito.mock(Typeface.class);
        Assert.assertEquals(options, options.setPrimaryTextTypeface(typeface, BOLD));
        Assert.assertEquals(typeface, options.getPrimaryTextTypeface());
        Assert.assertEquals(BOLD, options.getPrimaryTextTypefaceStyle(), 0);
    }

    @Test
    public void testPromptOptions_PrimaryTextTypeface() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final Typeface typeface = Mockito.mock(Typeface.class);
        Assert.assertEquals(options, options.setPrimaryTextTypeface(typeface));
        Assert.assertEquals(typeface, options.getPrimaryTextTypeface());
        Assert.assertEquals(NORMAL, options.getPrimaryTextTypefaceStyle(), 0);
    }

    @Test
    public void testPromptOptions_SecondaryTextTypefaceStyle() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final Typeface typeface = Mockito.mock(Typeface.class);
        Assert.assertEquals(options, options.setSecondaryTextTypeface(typeface, BOLD));
        Assert.assertEquals(typeface, options.getSecondaryTextTypeface());
        Assert.assertEquals(BOLD, options.getSecondaryTextTypefaceStyle(), 0);
    }

    @Test
    public void testPromptOptions_SecondaryTextTypeface() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        final Typeface typeface = Mockito.mock(Typeface.class);
        Assert.assertEquals(options, options.setSecondaryTextTypeface(typeface));
        Assert.assertEquals(typeface, options.getSecondaryTextTypeface());
        Assert.assertEquals(NORMAL, options.getSecondaryTextTypefaceStyle(), 0);
    }

    @Test
    public void testPromptOptions_TextPadding_Resource() {
        @DimenRes
        final int resourceId = 436455;
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        final Resources resources = Mockito.mock(Resources.class);
        Mockito.when(options.getResourceFinder().getResources()).thenReturn(resources);
        Mockito.when(resources.getDimension(resourceId)).thenReturn(12.0F);
        Assert.assertEquals(options, options.setTextPadding(resourceId));
        Assert.assertEquals(12.0F, options.getTextPadding(), 0);
    }

    @Test
    public void testPromptOptions_TextPadding_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(40.0F, options.getTextPadding(), 0);
    }

    @Test
    public void testPromptOptions_TextPadding() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setTextPadding(23.0F));
        Assert.assertEquals(23.0F, options.getTextPadding(), 0);
    }

    @Test
    public void testPromptOptions_TextSeparation_Resource() {
        @DimenRes
        final int resourceId = 426547;
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        final Resources resources = Mockito.mock(Resources.class);
        Mockito.when(options.getResourceFinder().getResources()).thenReturn(resources);
        Mockito.when(resources.getDimension(resourceId)).thenReturn(15.0F);
        Assert.assertEquals(options, options.setTextSeparation(resourceId));
        Assert.assertEquals(15.0F, options.getTextSeparation(), 0);
    }

    @Test
    public void testPromptOptions_TextSeparation_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(16.0F, options.getTextSeparation(), 0);
    }

    @Test
    public void testPromptOptions_TextSeparation() {
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        Assert.assertEquals(options, options.setTextSeparation(23.0F));
        Assert.assertEquals(23.0F, options.getTextSeparation(), 0);
    }

    @Test
    public void testPromptOptions_FocalPadding_Resource() {
        @DimenRes
        final int resourceId = 243365;
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        final Resources resources = Mockito.mock(Resources.class);
        Mockito.when(options.getResourceFinder().getResources()).thenReturn(resources);
        Mockito.when(resources.getDimension(resourceId)).thenReturn(18.0F);
        Assert.assertEquals(options, options.setFocalPadding(resourceId));
        Assert.assertEquals(18.0F, options.getFocalPadding(), 0);
    }

    @Test
    public void testPromptOptions_FocalPadding_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(20.0F, options.getFocalPadding(), 0);
    }

    @Test
    public void testPromptOptions_FocalPadding() {
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        Assert.assertEquals(options, options.setFocalPadding(65.0F));
        Assert.assertEquals(65.0F, options.getFocalPadding(), 0);
    }

    @Test
    public void testPromptOptions_AnimationInterpolator() {
        final Interpolator animationInterpolator = Mockito.mock(Interpolator.class);
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setAnimationInterpolator(animationInterpolator));
        Assert.assertEquals(animationInterpolator, options.getAnimationInterpolator());
    }

    @Test
    public void testPromptOptions_AnimationInterpolator_Null() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertNull(options.getAnimationInterpolator());
        options.setPrimaryText("Primary Text");
        options.setTarget(Mockito.mock(View.class));
        options.create();
        TestCase.assertNotNull(options.getAnimationInterpolator());
    }

    @Test
    public void testPromptOptions_IdleAnimation_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        Assert.assertTrue(options.getIdleAnimationEnabled());
    }

    @Test
    public void testPromptOptions_IdleAnimation_Disabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setIdleAnimationEnabled(false));
        Assert.assertFalse(options.getIdleAnimationEnabled());
    }

    @Test
    public void testPromptOptions_IdleAnimation_Enabled() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setIdleAnimationEnabled(true));
        Assert.assertTrue(options.getIdleAnimationEnabled());
    }

    @Test
    public void testPromptOptions_FocalColour_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(WHITE, options.getFocalColour());
    }

    @Test
    public void testPromptOptions_FocalColour() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setFocalColour(CYAN));
        Assert.assertEquals(CYAN, options.getFocalColour());
    }

    @Test
    public void testPromptOptions_BackgroundColour_Default() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(Color.argb(244, 63, 81, 181), options.getBackgroundColour());
    }

    @Test
    public void testPromptOptions_BackgroundColour() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setBackgroundColour(GREEN));
        Assert.assertEquals(GREEN, options.getBackgroundColour());
    }

    @Test
    public void testPromptOptions_StateListener_Null() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setPromptStateChangeListener(null));
        options.onPromptStateChanged(null, STATE_REVEALING);
        options.onPromptStateChanged(null, STATE_REVEALED);
        options.onPromptStateChanged(null, STATE_FOCAL_PRESSED);
        options.onPromptStateChanged(null, STATE_NON_FOCAL_PRESSED);
        options.onPromptStateChanged(null, STATE_DISMISSED);
        options.onPromptStateChanged(null, STATE_DISMISSED);
        options.onPromptStateChanged(null, STATE_FINISHING);
        options.onPromptStateChanged(null, STATE_FINISHED);
    }

    @Test
    public void testPromptOptions_StateListener() {
        final MaterialTapTargetPrompt thePrompt = Mockito.mock(MaterialTapTargetPrompt.class);
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(@NonNull
            MaterialTapTargetPrompt prompt, int state) {
                Assert.assertEquals(thePrompt, prompt);
                Assert.assertEquals(STATE_FINISHED, state, 0);
            }
        }));
        options.onPromptStateChanged(thePrompt, STATE_FINISHED);
    }

    @Test
    public void testPromptOptions_Icon() {
        final Drawable drawable = Mockito.mock(Drawable.class);
        @DrawableRes
        final int resourceId = 235436;
        final PromptOptions options = UnitTestUtils.createPromptOptions(true);
        Mockito.when(options.getResourceFinder().getDrawable(resourceId)).thenReturn(drawable);
        Assert.assertEquals(options, options.setIcon(resourceId));
        Assert.assertEquals(drawable, options.getIconDrawable());
    }

    @Test
    public void testPromptOptions_IconDrawable() {
        final Drawable drawable = Mockito.mock(Drawable.class);
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        options.setPrimaryText("Primary Text");
        options.setTarget(Mockito.mock(View.class));
        options.create();
        Assert.assertEquals(options, options.setIconDrawable(drawable));
        Assert.assertEquals(drawable, options.getIconDrawable());
        options.create();
        Assert.assertEquals(options, options.setIconDrawableTintMode(ADD));
        Assert.assertEquals(options, options.setIconDrawableColourFilter(Color.argb(21, 235, 23, 43)));
        options.create();
        Assert.assertEquals(options, options.setIconDrawableTintMode(null));
    }

    @Test
    public void testPromptOptions_IconDrawable_TintList() {
        final Drawable drawable = Mockito.mock(Drawable.class);
        final ColorStateList colourStateList = Mockito.mock(ColorStateList.class);
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setIconDrawable(drawable));
        Assert.assertEquals(drawable, options.getIconDrawable());
        Assert.assertEquals(options, options.setIconDrawableTintList(colourStateList));
        options.setPrimaryText("Primary Text");
        options.setTarget(Mockito.mock(View.class));
        options.create();
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", 16);
        options.create();
        Assert.assertEquals(options, options.setIconDrawableTintList(null));
    }

    @Test
    public void testPromptOptions_CreateEmpty() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertNull(options.create());
    }

    @Test
    public void testPromptOptions_ShowEmpty() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertNull(options.show());
    }

    @Test
    public void testPromptOptions_Create_NullTarget() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setTarget(null));
        Assert.assertNull(options.create());
    }

    @Test
    public void testPromptOptions_Create_NullText() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setTarget(Mockito.mock(View.class)));
        Assert.assertNull(options.create());
    }

    @Test
    public void testPromptOptions_Create_NullPrimaryText() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setTarget(Mockito.mock(View.class)));
        Assert.assertEquals(options, options.setSecondaryText("text"));
        TestCase.assertNotNull(options.create());
    }

    @Test
    public void testPromptOptions_Create_NullSecondaryText() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setTarget(Mockito.mock(View.class)));
        Assert.assertEquals(options, options.setPrimaryText("text"));
        TestCase.assertNotNull(options.create());
    }

    @Test
    public void testPromptOptions_Create() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setTarget(Mockito.mock(View.class)));
        Assert.assertEquals(options, options.setPrimaryText("text"));
        Assert.assertEquals(options, options.setSecondaryText("text"));
        TestCase.assertNotNull(options.create());
    }

    @Test
    public void testPromptOptions_Show() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setTarget(Mockito.mock(View.class)));
        Assert.assertEquals(options, options.setPrimaryText("text"));
        Assert.assertEquals(options, options.setSecondaryText("text"));
        TestCase.assertNotNull(options.show());
    }

    @Test
    public void testPromptOptions_ShowFor() {
        final PromptOptions options = UnitTestUtils.createPromptOptions();
        Assert.assertEquals(options, options.setTarget(Mockito.mock(View.class)));
        Assert.assertEquals(options, options.setPrimaryText("text"));
        Assert.assertEquals(options, options.setSecondaryText("text"));
        final MaterialTapTargetPrompt prompt = options.showFor(5000);
        TestCase.assertNotNull(prompt);
    }

    @Test
    public void testPromptOptions_ShowFor_Empty() {
        Assert.assertNull(UnitTestUtils.createPromptOptions().showFor(6000));
    }
}

