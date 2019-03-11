package com.reactnativenavigation.anim;


import Animation.AnimationListener;
import com.reactnativenavigation.BaseTest;
import org.junit.Test;
import org.mockito.Mockito;


public class ViewAnimationSetBuilderTest extends BaseTest {
    private Runnable mockListener;

    @Test
    public void implementsViewAnimationListener() {
        assertThat(new ViewAnimationSetBuilder()).isInstanceOf(AnimationListener.class);
    }

    @Test
    public void optionalCompletionListener() {
        new ViewAnimationSetBuilder().add(someView(), someAnimation()).start();
        Mockito.verify(mockListener, Mockito.times(0)).run();
    }

    @Test
    public void startsAllAnimations() {
        Animation anim1 = someAnimation();
        Animation anim2 = someAnimation();
        new ViewAnimationSetBuilder().withEndListener(mockListener).add(someView(), anim1).add(someView(), anim2).start();
        assertThat(anim1.hasStarted()).isTrue();
        assertThat(anim2.hasStarted()).isTrue();
    }

    @Test
    public void callsEndListenerOnlyAfterAllAnimationsFinish() {
        Animation anim1 = someAnimation();
        Animation anim2 = someAnimation();
        ViewAnimationSetBuilder uut = new ViewAnimationSetBuilder();
        uut.withEndListener(mockListener).add(someView(), anim1).add(someView(), anim2).start();
        Mockito.verify(mockListener, Mockito.times(1)).run();
    }

    @Test
    public void clearsAnimationFromViewsAfterFinished() {
        View v1 = someView();
        View v2 = someView();
        new ViewAnimationSetBuilder().withEndListener(mockListener).add(v1, someAnimation()).start();
        assertThat(v1.getAnimation()).isNull();
        assertThat(v2.getAnimation()).isNull();
    }
}

