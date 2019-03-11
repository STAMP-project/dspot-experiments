package com.reactnativenavigation.viewcontrollers.bottomtabs;


import com.reactnativenavigation.BaseTest;
import org.junit.Test;
import org.mockito.Mockito;


public class BottomTabsAttacherTest extends BaseTest {
    private BottomTabsAttacher uut;

    private AttachMode mode;

    @Test
    public void attach_delegatesToStrategy() {
        uut.attach();
        Mockito.verify(mode).attach();
    }

    @Test
    public void onTabSelected() {
        ViewController tab = Mockito.mock(com.reactnativenavigation.viewcontrollers.ViewController.class);
        uut.onTabSelected(tab);
        Mockito.verify(mode).onTabSelected(tab);
    }

    @Test
    public void destroy_delegatesToStrategy() {
        uut.destroy();
        Mockito.verify(mode).destroy();
    }
}

