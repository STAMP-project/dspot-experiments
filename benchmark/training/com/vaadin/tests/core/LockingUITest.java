package com.vaadin.tests.core;


import LockingUI.ALL_OK;
import LockingUI.LOCKING_ENDED;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class LockingUITest extends SingleBrowserTest {
    @Test
    public void testLockingTheUIFor4HeartBeats() {
        openTestURL();
        clickButtonAndCheckNotification("check", ALL_OK);
        clickButtonAndCheckNotification("lock", LOCKING_ENDED);
        clickButtonAndCheckNotification("check", ALL_OK);
    }
}

