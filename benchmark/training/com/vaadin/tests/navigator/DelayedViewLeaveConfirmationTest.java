package com.vaadin.tests.navigator;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class DelayedViewLeaveConfirmationTest extends SingleBrowserTest {
    @Test
    public void navigateAwayWithoutChanges() {
        openMainView();
        navigateToOtherView();
        assertOnOtherView();
    }

    @Test
    public void cancelNavigateAwayWithChanges() {
        openMainView();
        updateValue();
        navigateToOtherView();
        assertOnMainView();
        chooseToStay();
        assertOnMainView();
    }

    @Test
    public void confirmNavigateAwayWithChanges() {
        openMainView();
        updateValue();
        navigateToOtherView();
        assertOnMainView();
        chooseToLeave();
        assertOnOtherView();
    }

    @Test
    public void confirmLogoutWithChanges() {
        openMainView();
        updateValue();
        logout();
        assertOnMainView();
        chooseToLeave();
        assertLoggedOut();
    }

    @Test
    public void cancelLogoutWithChanges() {
        openMainView();
        updateValue();
        logout();
        assertOnMainView();
        chooseToStay();
        assertOnMainView();
    }

    @Test
    public void logoutWithoutChanges() {
        openMainView();
        getLogout().click();
        assertLoggedOut();
    }
}

