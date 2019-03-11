package com.reactnativenavigation.viewcontrollers;


import android.app.Activity;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.mocks.TitleBarReactViewCreatorMock;
import com.reactnativenavigation.parse.Component;
import org.junit.Test;
import org.mockito.Mockito;


public class TitleBarReactViewControllerTest extends BaseTest {
    private TitleBarReactViewController uut;

    private TitleBarReactViewCreatorMock viewCreator;

    private Activity activity;

    private Component component;

    @Test
    public void createView() {
        uut.createView();
        Mockito.verify(viewCreator).create(activity, component.componentId.get(), component.name.get());
    }
}

