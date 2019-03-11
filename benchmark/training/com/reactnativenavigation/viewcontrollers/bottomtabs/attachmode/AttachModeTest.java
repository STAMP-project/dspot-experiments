package com.reactnativenavigation.viewcontrollers.bottomtabs.attachmode;


import View.VISIBLE;
import com.reactnativenavigation.Activity;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.ViewController;
import com.reactnativenavigation.ViewGroup;
import com.reactnativenavigation.mocks.ChildControllersRegistry;
import com.reactnativenavigation.mocks.Options;
import com.reactnativenavigation.viewcontrollers.BottomTabsPresenter;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


public abstract class AttachModeTest extends BaseTest {
    private static final int INITIAL_TAB = 1;

    private Activity activity;

    private ChildControllersRegistry childRegistry;

    protected ViewGroup parent;

    ViewController tab1;

    ViewController tab2;

    List<ViewController> tabs;

    protected Options options;

    protected BottomTabsPresenter presenter;

    protected AttachMode uut;

    @Test
    public void attach_layoutOptionsAreApplied() {
        uut.attach(tab1);
        Mockito.verify(presenter).applyLayoutParamsOptions(options, tabs.indexOf(tab1));
    }

    @Test
    public void attach_initialTabIsVisible() {
        uut.attach(initialTab());
        assertThat(initialTab().getView().getVisibility()).isEqualTo(VISIBLE);
    }

    @Test
    public void attach_otherTabsAreInvisibleWhenAttached() {
        forEach(otherTabs(), ( t) -> uut.attach(t));
        forEach(otherTabs(), ( t) -> assertThat(t.getView().getVisibility()).isEqualTo(View.INVISIBLE));
    }
}

