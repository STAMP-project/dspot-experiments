package com.bumptech.glide.request.transition;


import DataSource.DATA_DISK_CACHE;
import DataSource.MEMORY_CACHE;
import RuntimeEnvironment.application;
import Transition.ViewAdapter;
import ViewTransition.ViewTransitionAnimationFactory;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ViewTransitionAnimationFactoryTest {
    private ViewTransitionAnimationFactory viewTransitionAnimationFactory;

    private ViewAnimationFactory<Object> factory;

    @Test
    public void testFactoryReturnsNoAnimationIfFromMemoryCache() {
        Transition<Object> animation = /* isFirstResource */
        factory.build(MEMORY_CACHE, true);
        Assert.assertEquals(NoTransition.get(), animation);
        Mockito.verify(viewTransitionAnimationFactory, Mockito.never()).build(application);
    }

    @Test
    public void testFactoryReturnsNoAnimationIfNotFirstResource() {
        Transition<Object> animation = /* isFirstResource */
        factory.build(DATA_DISK_CACHE, false);
        Assert.assertEquals(NoTransition.get(), animation);
        Mockito.verify(viewTransitionAnimationFactory, Mockito.never()).build(application);
    }

    @Test
    public void testFactoryReturnsActualAnimationIfNotIsFromMemoryCacheAndIsFirstResource() {
        Transition<Object> transition = /* isFirstResource */
        factory.build(DATA_DISK_CACHE, true);
        Animation animation = Mockito.mock(Animation.class);
        Mockito.when(viewTransitionAnimationFactory.build(ArgumentMatchers.any(Context.class))).thenReturn(animation);
        Transition.ViewAdapter adapter = Mockito.mock(ViewAdapter.class);
        View view = Mockito.mock(View.class);
        Mockito.when(adapter.getView()).thenReturn(view);
        transition.transition(new Object(), adapter);
        Mockito.verify(view).startAnimation(ArgumentMatchers.eq(animation));
    }
}

