package com.reactnativenavigation.viewcontrollers.bottomtabs.attachmode;


import org.junit.Test;


public class TogetherTest extends AttachModeTest {
    @Test
    public void attach_allTabsAreAttached() {
        uut.attach();
        assertIsChild(parent, tabs.toArray(new ViewController[0]));
    }
}

