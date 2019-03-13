package com.reactnativenavigation.viewcontrollers.bottomtabs.attachmode;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AfterInitialTabTest extends AttachModeTest {
    @Test
    public void attach_initialTabIsAttached() {
        uut.attach();
        assertIsChild(parent, tab2);
    }

    @Test
    public void attach_otherTabsAreAttachedAfterInitialTab() {
        uut.attach();
        assertNotChildOf(parent, otherTabs());
        initialTab().onViewAppeared();
        assertIsChild(parent, otherTabs());
    }

    @Test
    public void destroy() {
        uut.destroy();
        Mockito.verify(initialTab()).removeOnAppearedListener(ArgumentMatchers.any());
    }
}

