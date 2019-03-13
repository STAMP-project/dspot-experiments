package com.bumptech.glide.request.transition;


import ViewPropertyTransition.Animator;
import android.view.View;
import android.widget.ImageView;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class ViewPropertyAnimationTest {
    private Animator animator;

    private ViewPropertyTransition<Object> animation;

    @Test
    public void testAlwaysReturnsFalse() {
        Assert.assertFalse(animation.transition(new Object(), Mockito.mock(Transition.ViewAdapter.class)));
    }

    @Test
    public void testCallsAnimatorWithGivenView() {
        ImageView view = new ImageView(RuntimeEnvironment.application);
        Transition.ViewAdapter adapter = Mockito.mock(Transition.ViewAdapter.class);
        Mockito.when(adapter.getView()).thenReturn(view);
        animation.transition(new Object(), adapter);
        Mockito.verify(animator).animate(ArgumentMatchers.eq(view));
    }

    @Test
    public void testDoesNotCallAnimatorIfGivenAdapterWithNullView() {
        Transition.ViewAdapter adapter = Mockito.mock(Transition.ViewAdapter.class);
        animation.transition(new Object(), adapter);
        Mockito.verify(animator, Mockito.never()).animate(ArgumentMatchers.any(View.class));
    }
}

