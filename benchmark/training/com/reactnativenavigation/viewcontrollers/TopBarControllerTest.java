package com.reactnativenavigation.viewcontrollers;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.viewcontrollers.topbar.TopBarController;
import com.reactnativenavigation.views.StackLayout;
import com.reactnativenavigation.views.titlebar.TitleBar;
import com.reactnativenavigation.views.topbar.TopBar;
import org.junit.Test;
import org.mockito.Mockito;


public class TopBarControllerTest extends BaseTest {
    private TopBarController uut;

    @Test
    public void clear() {
        final TitleBar[] titleBar = new TitleBar[1];
        uut = new TopBarController() {
            @NonNull
            @Override
            protected TopBar createTopBar(Context context, StackLayout stackLayout) {
                return new TopBar(context, stackLayout) {
                    @Override
                    protected TitleBar createTitleBar(Context context) {
                        titleBar[0] = Mockito.spy(super.createTitleBar(context));
                        return titleBar[0];
                    }
                };
            }
        };
        Activity activity = newActivity();
        uut.createView(activity, Mockito.mock(StackLayout.class));
        uut.clear();
        Mockito.verify(titleBar[0], Mockito.times(1)).clear();
    }

    @Test
    public void destroy() {
        uut.createView(newActivity(), Mockito.mock(StackLayout.class));
        uut.clear();
    }

    @Test
    public void destroy_canBeCalledBeforeViewIsCreated() {
        uut.clear();
    }
}

