package com.reactnativenavigation.viewcontrollers;


import Options.EMPTY;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.presentation.ComponentPresenter;
import com.reactnativenavigation.views.ComponentLayout;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;


public class ComponentViewControllerTest extends BaseTest {
    private ComponentViewController uut;

    private ComponentLayout view;

    private ComponentPresenter presenter;

    private Options resolvedOptions = new Options();

    @Test
    public void setDefaultOptions() {
        Options defaultOptions = new Options();
        uut.setDefaultOptions(defaultOptions);
        verify(presenter).setDefaultOptions(defaultOptions);
    }

    @Test
    public void applyOptions() {
        Options options = new Options();
        uut.applyOptions(options);
        verify(view).applyOptions(options);
        verify(presenter).applyOptions(view, resolvedOptions);
    }

    @Test
    public void createsViewFromComponentViewCreator() {
        assertThat(uut.getView()).isSameAs(view);
    }

    @Test
    public void componentViewDestroyedOnDestroy() {
        uut.ensureViewIsCreated();
        Mockito.verify(view, Mockito.times(0)).destroy();
        uut.onViewAppeared();
        uut.destroy();
        Mockito.verify(view, Mockito.times(1)).destroy();
    }

    @Test
    public void lifecycleMethodsSentToComponentView() {
        uut.ensureViewIsCreated();
        Mockito.verify(view, Mockito.times(0)).sendComponentStart();
        Mockito.verify(view, Mockito.times(0)).sendComponentStop();
        uut.onViewAppeared();
        Mockito.verify(view, Mockito.times(1)).sendComponentStart();
        Mockito.verify(view, Mockito.times(0)).sendComponentStop();
        uut.onViewDisappear();
        Mockito.verify(view, Mockito.times(1)).sendComponentStart();
        Mockito.verify(view, Mockito.times(1)).sendComponentStop();
    }

    @Test
    public void isViewShownOnlyIfComponentViewIsReady() {
        assertThat(uut.isViewShown()).isFalse();
        uut.ensureViewIsCreated();
        Mockito.when(view.asView().isShown()).thenReturn(true);
        assertThat(uut.isViewShown()).isFalse();
        Mockito.when(view.isReady()).thenReturn(true);
        assertThat(uut.isViewShown()).isTrue();
    }

    @Test
    public void onNavigationButtonPressInvokedOnReactComponent() {
        uut.ensureViewIsCreated();
        uut.sendOnNavigationButtonPressed("btn1");
        Mockito.verify(view, Mockito.times(1)).sendOnNavigationButtonPressed("btn1");
    }

    @Test
    public void mergeOptions_emptyOptionsAreIgnored() {
        ComponentViewController spy = Mockito.spy(uut);
        spy.mergeOptions(EMPTY);
        Mockito.verify(spy, Mockito.times(0)).performOnParentController(ArgumentMatchers.any());
    }

    @Test
    public void mergeOptions_delegatesToPresenter() {
        Options options = new Options();
        uut.mergeOptions(options);
        verify(presenter).mergeOptions(uut.getView(), options);
    }
}

