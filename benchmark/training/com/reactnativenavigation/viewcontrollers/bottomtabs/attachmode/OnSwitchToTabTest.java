package com.reactnativenavigation.viewcontrollers.bottomtabs.attachmode;


import org.junit.Test;


public class OnSwitchToTabTest extends AttachModeTest {
    @Test
    public void attach_onlyInitialTabIsAttached() {
        uut.attach();
        assertIsChild(parent, initialTab());
        assertNotChildOf(parent, otherTabs());
    }

    @Test
    public void onTabSelected_initialTabIsNotHandled() {
        uut.onTabSelected(initialTab());
        assertNotChildOf(parent, initialTab());
    }

    @Test
    public void onTabSelected_otherTabIsAttached() {
        uut.onTabSelected(tab1);
        assertIsChild(parent, tab1);
    }
}

