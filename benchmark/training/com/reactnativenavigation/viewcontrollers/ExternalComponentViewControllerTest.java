package com.reactnativenavigation.viewcontrollers;


import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;
import com.facebook.react.ReactInstanceManager;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.parse.ExternalComponent;
import com.reactnativenavigation.viewcontrollers.externalcomponent.ExternalComponentViewController;
import com.reactnativenavigation.viewcontrollers.externalcomponent.FragmentCreatorMock;
import com.reactnativenavigation.views.ExternalComponentLayout;
import org.junit.Test;
import org.mockito.Mockito;


public class ExternalComponentViewControllerTest extends BaseTest {
    private ExternalComponentViewController uut;

    private FragmentCreatorMock componentCreator;

    private Activity activity;

    private ExternalComponent ec;

    private ReactInstanceManager reactInstanceManager;

    @Test
    public void createView_returnsFrameLayout() {
        ExternalComponentLayout view = uut.getView();
        assertThat(FrameLayout.class.isAssignableFrom(view.getClass())).isTrue();
    }

    @Test
    public void createView_createsExternalComponent() {
        ExternalComponentLayout view = uut.getView();
        Mockito.verify(componentCreator, Mockito.times(1)).create(((FragmentActivity) (activity)), reactInstanceManager, ec.passProps);
        assertThat(view.getChildCount()).isGreaterThan(0);
    }
}

