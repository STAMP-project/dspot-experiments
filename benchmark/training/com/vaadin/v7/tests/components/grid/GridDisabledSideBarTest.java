package com.vaadin.v7.tests.components.grid;


import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import org.junit.Test;


public class GridDisabledSideBarTest extends GridBasicClientFeaturesTest {
    @Test
    public void sidebarButtonIsDisabledOnCreation() {
        selectMenuPath("Component", "State", "Enabled");
        makeColumnHidable();
        clickSideBarButton();
        assertSideBarContainsClass("closed");
    }

    @Test
    public void sidebarButtonCanBeEnabled() {
        makeColumnHidable();
        clickSideBarButton();
        assertSideBarContainsClass("open");
    }

    @Test
    public void sidebarButtonCanBeDisabled() {
        makeColumnHidable();
        toggleEnabled();
        clickSideBarButton();
        assertSideBarContainsClass("closed");
    }

    @Test
    public void sidebarIsClosedOnDisable() {
        makeColumnHidable();
        toggleSideBarMenuAndDisable();
        assertSideBarContainsClass("closed");
    }
}

