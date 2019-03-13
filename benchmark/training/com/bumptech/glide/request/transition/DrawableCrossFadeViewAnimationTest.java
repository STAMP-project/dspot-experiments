package com.bumptech.glide.request.transition;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class DrawableCrossFadeViewAnimationTest {
    private DrawableCrossFadeViewAnimationTest.CrossFadeHarness harness;

    @Test
    public void testIgnoresNullViews() {
        Mockito.when(harness.adapter.getView()).thenReturn(null);
        harness.animation.transition(harness.current, harness.adapter);
    }

    @Test
    public void transition_withNonNullPreviousDrawable_setsTransitionDrawable() {
        Drawable previous = new android.graphics.drawable.ColorDrawable(Color.WHITE);
        Mockito.when(harness.adapter.getCurrentDrawable()).thenReturn(previous);
        harness.animation.transition(harness.current, harness.adapter);
        Mockito.verify(harness.adapter).setDrawable(ArgumentMatchers.any(TransitionDrawable.class));
    }

    @Test
    public void transition_withNullPreviousDrawable_setsTransitionDrawable() {
        harness.animation.transition(harness.current, harness.adapter);
        Mockito.verify(harness.adapter).setDrawable(ArgumentMatchers.any(TransitionDrawable.class));
    }

    @Test
    public void transition_withNoCurrentDrawable_returnsTrue() {
        Assert.assertTrue(harness.animation.transition(harness.current, harness.adapter));
    }

    @Test
    public void transition_withCurrentDrawable_returnsTrue() {
        Drawable previous = new android.graphics.drawable.ColorDrawable(Color.RED);
        Mockito.when(harness.adapter.getCurrentDrawable()).thenReturn(previous);
        Assert.assertTrue(harness.animation.transition(harness.current, harness.adapter));
    }

    @SuppressWarnings("unchecked")
    private static class CrossFadeHarness {
        final Drawable current = new android.graphics.drawable.ColorDrawable(Color.GRAY);

        final Transition.ViewAdapter adapter = Mockito.mock(Transition.ViewAdapter.class);

        final int duration = 200;

        final DrawableCrossFadeTransition animation = /* isCrossFadeEnabled */
        new DrawableCrossFadeTransition(duration, true);
    }
}

