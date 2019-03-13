package com.reactnativenavigation.react;


import com.facebook.react.uimanager.ThemedReactContext;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.mocks.SimpleViewController;
import com.reactnativenavigation.views.element.Element;
import org.junit.Test;
import org.mockito.Mockito;


public class ElementViewManagerTest extends BaseTest {
    private ElementViewManager uut;

    private Element view;

    private ThemedReactContext reactContext = Mockito.mock(ThemedReactContext.class);

    private SimpleViewController.SimpleView parentView;

    @Test
    public void createViewInstance() {
        Element element = uut.createViewInstance(reactContext);
        assertThat(element).isNotNull();
    }

    @Test
    public void createViewInstance_registersInParentReactView() {
        ElementViewManager spy = Mockito.spy(uut);
        parentView.addView(view);
        spy.createViewInstance(reactContext);
        dispatchPreDraw(view);
        dispatchOnGlobalLayout(view);
        Mockito.verify(parentView).registerElement(view);
    }

    @Test
    public void onDropViewInstance() {
        uut.onDropViewInstance(view);
        Mockito.verify(parentView).unregisterElement(view);
    }
}

