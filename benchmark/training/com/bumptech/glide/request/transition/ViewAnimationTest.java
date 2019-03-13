package com.bumptech.glide.request.transition;


import ViewTransition.ViewTransitionAnimationFactory;
import android.content.Context;
import android.view.animation.Animation;
import android.widget.ImageView;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class ViewAnimationTest {
    private ViewTransition<Object> viewAnimation;

    private Transition.ViewAdapter adapter;

    private ImageView view;

    private ViewTransitionAnimationFactory viewTransitionAnimationFactory;

    @Test
    public void testClearsAnimationOnAnimate() {
        viewAnimation.transition(null, adapter);
        Mockito.verify(view).clearAnimation();
    }

    @Test
    public void testAlwaysReturnsFalse() {
        Assert.assertFalse(viewAnimation.transition(null, adapter));
    }

    @Test
    public void testStartsAnimationOnAnimate() {
        Animation animation = Mockito.mock(Animation.class);
        Mockito.when(viewTransitionAnimationFactory.build(ArgumentMatchers.any(Context.class))).thenReturn(animation);
        viewAnimation.transition(null, adapter);
        Mockito.verify(view).clearAnimation();
        Mockito.verify(view).startAnimation(ArgumentMatchers.eq(animation));
    }
}

