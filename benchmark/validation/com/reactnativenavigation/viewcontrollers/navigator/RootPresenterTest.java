package com.reactnativenavigation.viewcontrollers.navigator;


import android.widget.FrameLayout;
import com.facebook.react.ReactInstanceManager;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.anim.NavigationAnimator;
import com.reactnativenavigation.parse.AnimationOptions;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.viewcontrollers.ViewController;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RootPresenterTest extends BaseTest {
    private RootPresenter uut;

    private FrameLayout rootContainer;

    private ViewController root;

    private NavigationAnimator animator;

    private Options defaultOptions;

    private ReactInstanceManager reactInstanceManager;

    @Test
    public void setRoot_viewIsAddedToContainer() {
        uut.setRoot(root, defaultOptions, new CommandListenerAdapter(), reactInstanceManager);
        assertThat(root.getView().getParent()).isEqualTo(rootContainer);
    }

    @Test
    public void setRoot_reportsOnSuccess() {
        CommandListenerAdapter listener = Mockito.spy(new CommandListenerAdapter());
        uut.setRoot(root, defaultOptions, listener, reactInstanceManager);
        Mockito.verify(listener).onSuccess(root.getId());
    }

    @Test
    public void setRoot_doesNotAnimateByDefault() {
        CommandListenerAdapter listener = Mockito.spy(new CommandListenerAdapter());
        uut.setRoot(root, defaultOptions, listener, reactInstanceManager);
        Mockito.verifyZeroInteractions(animator);
        Mockito.verify(listener).onSuccess(root.getId());
    }

    @Test
    public void setRoot_animates() {
        Options animatedSetRoot = new Options();
        animatedSetRoot.animations.setRoot = new AnimationOptions() {
            @Override
            public boolean hasAnimation() {
                return true;
            }
        };
        ViewController spy = Mockito.spy(root);
        Mockito.when(spy.resolveCurrentOptions(defaultOptions)).thenReturn(animatedSetRoot);
        CommandListenerAdapter listener = Mockito.spy(new CommandListenerAdapter());
        uut.setRoot(spy, defaultOptions, listener, reactInstanceManager);
        Mockito.verify(animator).setRoot(ArgumentMatchers.eq(spy.getView()), ArgumentMatchers.eq(animatedSetRoot.animations.setRoot), ArgumentMatchers.any());
        Mockito.verify(listener).onSuccess(spy.getId());
    }

    @Test
    public void setRoot_waitForRenderIsSet() {
        root.options.animations.setRoot.waitForRender = new Bool(true);
        ViewController spy = Mockito.spy(root);
        uut.setRoot(spy, defaultOptions, new CommandListenerAdapter(), reactInstanceManager);
        ArgumentCaptor<Bool> captor = ArgumentCaptor.forClass(Bool.class);
        Mockito.verify(spy).setWaitForRender(captor.capture());
        assertThat(captor.getValue().get()).isTrue();
    }

    @Test
    public void setRoot_waitForRender() {
        root.options.animations.setRoot.waitForRender = new Bool(true);
        ViewController spy = Mockito.spy(root);
        CommandListenerAdapter listener = Mockito.spy(new CommandListenerAdapter());
        uut.setRoot(spy, defaultOptions, listener, reactInstanceManager);
        Mockito.verify(spy).addOnAppearedListener(ArgumentMatchers.any());
        assertThat(spy.getView().getAlpha()).isZero();
        Mockito.verifyZeroInteractions(listener);
        spy.onViewAppeared();
        assertThat(spy.getView().getAlpha()).isOne();
        Mockito.verify(listener).onSuccess(spy.getId());
    }
}

